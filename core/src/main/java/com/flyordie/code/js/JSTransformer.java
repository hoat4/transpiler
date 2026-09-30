package com.flyordie.code.js;

import com.flyordie.code.*;
import com.flyordie.code.Clazz.Field;
import com.flyordie.code.Clazz.Method;
import com.flyordie.code.CompilationContext.TransformationChain;
import com.flyordie.code.Interpreter.ClassObj;
import com.flyordie.code.Node.*;
import com.flyordie.code.Node.SequenceNode.MethodKey;
import com.flyordie.code.Type.PrimitiveType;
import com.flyordie.code.Type.ReferenceType;
import com.flyordie.code.js.JSInteropProvider.NativeMethodKind;
import com.flyordie.code.js.JSInteropProvider.NativeTypeKind;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.flyordie.code.Type.PrimitiveType.Z;

public class JSTransformer implements Transformation {

    private final Method compilingMethod;
    private final TransformationChain transformationChain;
    private final JSEmitter emitter;
    private final CompilationContext ctx;
    private final String usages;

    public JSTransformer(JSEmitter emitter, MethodCompilationContext mcc, EmissionContext emissionContext) {
        this.emitter = emitter;
        this.ctx = mcc.compilationContext;
        this.compilingMethod = mcc.method;
        this.transformationChain = mcc.transformationChain;
        this.usages = emissionContext.usagesString(compilingMethod);
    }

    @Override
    public Node enter(Node node) {
        if (node instanceof SequenceNode seq) {
            for (int i = 0; i < seq.nodes.size(); i++) {
                if (seq.nodes.get(i) instanceof InvokeSpecialOrStatic invokeNode) {
                    Method factoryMethod = transformationChain.replacementProvider.constructorReplacement(invokeNode.method);
                    if (factoryMethod != null) {
                        ObjectNode objectNode = (ObjectNode) invokeNode.args.get(0);
                        assert seq.nodes.contains(objectNode);
                        assert objectNode.usages().contains(invokeNode);

                        List<Node> factoryArgs = invokeNode.args.subList(1, invokeNode.args.size());
                        Node replacement = new InvokeSpecialOrStatic(factoryMethod, factoryArgs.toArray(Node[]::new));

                        seq.nodes.set(i, replacement);
                        Node.replaceInInputsOfUsages(objectNode, replacement);
                        seq.nodes.remove(objectNode);
                    }
                }
            }
        }
        if (node instanceof CheckCast checkCast && checkCast.type instanceof Clazz c) {
            if (c.knownClass == KnownClass.MEMBER_NAME) {
                Node.replaceInInputsOfUsages(checkCast, checkCast.valueSlot.get());
                return new NopNode("removed MemberName checkcast");
            }
            if (c.name.startsWith("org/teavm/jso/")) {
                // TODO ez nem ide kéne, hanem a TeaVM-es modulba
                Node.replaceInInputsOfUsages(checkCast, checkCast.valueSlot.get());
                return new NopNode("removed TeaVM JSO checkcast");
            }

            List<Clazz> replaced = transformationChain.replacementProvider.replacedClass(c);
            if (replaced != null) {
                if (replaced.size() == 1)
                    c = replaced.get(0);
                else
                    throw new RuntimeException("ambiguous class: " + c);
                return new CheckCast(checkCast.valueSlot.get(), c);
            }
        }
        if (node instanceof InstanceOf instanceOf && instanceOf.type instanceof Clazz c) {
            Clazz replacement = transformationChain.replacementProvider.replacementClass(c);
            if (replacement != null)
                return new InstanceOf(instanceOf.valueSlot.get(), replacement);
        }

        return node;
    }

    @Override
    public Node exit(Node node) {
        Node n2 = null;

        if (node instanceof InvokeSpecialOrStatic invokeSpecialOrStatic) {
            Method replaced = transformationChain.replacementProvider.replacedMethod(invokeSpecialOrStatic.method);
            if (replaced != null && transformationChain.replacementProvider.constructorReplacement(replaced) == null)
                node = (invokeSpecialOrStatic = new InvokeSpecialOrStatic(replaced,
                        invokeSpecialOrStatic.args.toArray(Node[]::new))).copyInfoFrom(node);
            if (invokeSpecialOrStatic.method.clazz.knownClass == KnownClass.UNSAFE2 &&
                    invokeSpecialOrStatic.method.name.equals("globalObjectAs")) {
                Node arg0 = invokeSpecialOrStatic.args.get(0);
                // System.out.println("GLBOALOBJECTAS "+arg0);
                if (arg0 instanceof ConstantNode cn) {
                    Type t = cn.value instanceof ClassObj co
                            ? ctx.interpreter().fromClass(co) : (Type) cn.value;
                    // System.out.println("GLBOALOBJECTAS "+t);
                    if (!(t instanceof Clazz c))
                        n2 = new ConstantNode(null);
                    else {
                        String staticsName = DefaultJSInteropProvider.staticsName(c);
                        // System.out.println("GLBOALOBJECTAS "+staticsName);
                        if (staticsName != null)
                            n2 = new InvokeSpecialOrStatic(
                                    ctx.method(KnownClass.UNSAFE2, "loadNativeStatics",
                                            ctx.findClass(KnownClass.OBJECT),
                                            ctx.findClass(KnownClass.STRING),
                                            ctx.findClass(KnownClass.CLASS)
                                    ),
                                    new ConstantNode(staticsName),
                                    cn
                            );
                    }
                }
            }
            if (invokeSpecialOrStatic.method.clazz.knownClass == KnownClass.UNSAFE2 &&
                    invokeSpecialOrStatic.method.name.equals("typeName")) {
                Object type = ((ConstantNode) invokeSpecialOrStatic.args.get(0)).value;
                if (!(type instanceof Type))
                    type = ctx.interpreter().fromClass((ClassObj) type);
                return new ConstantNode(emitter.typeName2((Type) type));
            }
        } else if (node instanceof InvokeVirtualOrInterface invokeNode) {
            Method replaced = transformationChain.replacementProvider.replacedMethod(invokeNode.method);
            if (replaced != null)
                node = (invokeNode = new InvokeVirtualOrInterface(replaced, invokeNode.args.toArray(Node[]::new))).
                        copyInfoFrom(node);

            NativeMethodKind nativeMethodKind = emitter.interopProvider.nativeMethodKind(invokeNode.method);
            if (nativeMethodKind != null) {
                // TODO azt is meg kéne csinálni hogy MemberName-be rakott natív függvény hívás működjön

                Clazz objClass = ctx.findClass(KnownClass.OBJECT);

                NativeCallNode nativeCallNode = new NativeCallNode(invokeNode.method,
                        new ArrayList<>(invokeNode.args), nativeMethodKind);

                SequenceNode seq = new SequenceNode(new MethodKey("native downcall " + invokeNode.method));
                for (int i = 1; i < invokeNode.args.size(); i++) {
                    Type t = invokeNode.method.fullArgTypes().get(i);
                    Node arg = invokeNode.args.get(i);
                    arg = emitter.downcallArgConvert(seq, t, arg);
                    nativeCallNode.args.set(i, arg);
                }
                seq.nodes.add(nativeCallNode);
                Node result = nativeCallNode;

                NativeTypeKind k;
                if (invokeNode.method.type().returnType() instanceof Clazz c &&
                        (k = emitter.interopProvider.nativeTypeKind(c)) != null && k.noInterfaceObject)
                    // TODO ugyanezt meg kéne csinálni upcalloknál paraméterekben
                    seq.nodes.add(new InvokeSpecialOrStatic(ctx.method(KnownClass.UNSAFE2, "downcallResultSetType",
                            PrimitiveType.V, objClass, ctx.findClass(KnownClass.CLASS)), result,
                            new ConstantNode(invokeNode.method.type().returnType())));

                if (invokeNode.method.type().returnType().descriptor().equals("Ljava/util/Optional;")) {
                    InvokeSpecialOrStatic optionalFactory = new InvokeSpecialOrStatic(
                            ctx.findMethodOrNull(ctx.findClass(Optional.class), "ofNullable",
                                    MethodType.parse("(Ljava/lang/Object;)Ljava/util/Optional;", ctx)),
                            new Node[]{result});
                    seq.nodes.add(optionalFactory);
                    seq.resultSlot.set(optionalFactory);
                } else {
                    seq.resultSlot.set(result);
                }
                n2 = seq;
            }
/*
            if (invokeNode.method.clazz.knownClass == KnownClass.MEMBER_NAME&&invokeNode.method.name.equals("getReturnType")) {
                assert invokeNode.args.get(0) instanceof ConstantNode : invokeNode.toString();
                ConstantNode cn = (ConstantNode) invokeNode.args.get(0);
                return new ConstantNode(ctx.interpreter().fromType(
                        ctx.interpreter().memberNameReferredMethod((ClassObj)cn.value).type.returnType()));
            }
 */
        } else if (node instanceof ObjectNode objectNode) {
            Clazz replacementClass = transformationChain.replacementProvider.replacementClass((Clazz) objectNode.objectIdentity.type);
            if (replacementClass != null)
                objectNode.objectIdentity.type = replacementClass;
        } else if (node instanceof GetStaticNode getStaticNode) {
            Field f = transformationChain.replacementProvider.replacedStaticField(getStaticNode.field);
            if (f != null)
                n2 = new GetStaticNode(f);
        }
        return n2 == null ? node : n2;
    }

    private Method unboxer(PrimitiveType t) {
        return switch (t) {
            case Z -> ctx.method(KnownClass.BOOLEAN, "booleanValue", Z);
            case B -> ctx.method(KnownClass.BYTE, "byteValue", PrimitiveType.B);
            case S -> ctx.method(KnownClass.SHORT, "shortValue", PrimitiveType.S);
            case I -> ctx.method(KnownClass.INTEGER, "intValue", PrimitiveType.I);
            case F -> ctx.method(KnownClass.FLOAT, "floatValue", PrimitiveType.F);
            case J -> ctx.method(KnownClass.LONG, "longValue", PrimitiveType.J);
            case D -> ctx.method(KnownClass.DOUBLE, "doubleValue", PrimitiveType.D);
            default -> throw new IllegalArgumentException("unknown primitive type: " + t);
        };
    }
/*
    private Node handleMethodCall(Method m, List<Node> args) {
        if (!Emitter.isNativeInterface(m.clazz))
            return null;

        Object r;
        if (Emitter.findAnnotation(m, GETTER_ANN_DESC) != null)
            r = new JSSnippetNode(a -> a.get(0) + "." + Emitter.nativeMethodName(m), m.type.returnType());
        else if (Emitter.findAnnotation(m, SETTER_ANN_DESC) != null)
            r = new JSSnippetNode(a -> a.get(0) + "." + Emitter.nativeMethodName(m) + "=" + a.get(1), PrimitiveType.V);
        else
            r = replacement;

        Node n2;
        if (r instanceof JSSnippet snippet)
            n2 = new JSSnippetNode(snippet, m.type.returnType());
        else if (r instanceof JSSnippetNode jsn) {
            n2 = jsn;
        } else if (r instanceof Node n)
            n2 = n;
        else if (r instanceof Method m2)
            n2 = new InvokeSpecialOrStatic(m2, args.toArray(Node[]::new));
        else if (r != null)
            throw new RuntimeException();
        else
            n2 = null;

        if (n2 instanceof JSSnippetNode jsn)
            jsn.args.addAll(args);

        return n2;
    }*/

    private RuntimeException unknownMethod(Method m) {
        throw new UnsupportedOperationException("method " + m + " (used by " + usages + ")");
    }
}
