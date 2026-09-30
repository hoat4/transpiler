package com.flyordie.code.js;

import com.flyordie.code.*;
import com.flyordie.code.Clazz.Method;
import com.flyordie.code.CompilationContext.TransformationChain;
import com.flyordie.code.Node.*;
import com.flyordie.code.Node.NativeSnippetNode.NativeSnippet;
import com.flyordie.code.Node.SequenceNode.MethodKey;
import com.flyordie.code.Type.ArrayType;
import com.flyordie.code.Type.PrimitiveType;

import java.util.List;

import static com.flyordie.code.Type.PrimitiveType.Z;

public class FinalJSTransformer implements Transformation {

    private final CompilationContext ctx;
    private final TransformationChain transformationChain;
    private final JSEmitter emitter;

    public FinalJSTransformer(MethodCompilationContext mcc, JSEmitter emitter) {
        this.ctx = mcc.compilationContext;
        this.transformationChain = mcc.transformationChain;
        this.emitter = emitter;
    }

    @Override
    public Node enter(Node node) {
        if (node instanceof CheckCast checkCast) {
            if (checkCast.type.descriptor().equals("L" + JSReplacementProvider.JSObjectImpl + ";")) {
                Node.replaceInInputsOfUsages(checkCast, checkCast.valueSlot.get());
                return new NopNode("checkcast to JSObject eliminated");
            } else
                return new InvokeSpecialOrStatic(ctx.method(KnownClass.UNSAFE2, "checkCast",
                        ctx.findClass(KnownClass.OBJECT), ctx.findClass(KnownClass.OBJECT), ctx.findClass(KnownClass.CLASS)),
                        checkCast.valueSlot.get(), new ConstantNode(checkCast.type));
        }
        if (node instanceof InstanceOf instanceOf) {
            return new InvokeSpecialOrStatic(ctx.method(KnownClass.UNSAFE2, "instanceOf",
                    Z, ctx.findClass(KnownClass.OBJECT), ctx.findClass(KnownClass.CLASS)),
                    instanceOf.valueSlot.get(), new ConstantNode(instanceOf.type));
        }
        if (node instanceof InvokeSpecialOrStatic invokeSpecialOrStatic) {
            Method replaced = transformationChain.replacementProvider.replacedMethod(invokeSpecialOrStatic.method);
            if (replaced != null && transformationChain.replacementProvider.constructorReplacement(replaced) == null)
                // ez itt szerintem redundáns, mert JSTransformerben van ilyesmi
                node = (invokeSpecialOrStatic = new InvokeSpecialOrStatic(replaced,
                        invokeSpecialOrStatic.args.toArray(Node[]::new))).copyInfoFrom(node);
            if (invokeSpecialOrStatic.method.isPolySigMethodSpecialization) {
                Method m = invokeSpecialOrStatic.method;
                if (m.clazz.name.equals("java/lang/invoke/MethodHandle") && m.name.startsWith("linkTo")) {
                    Node.NativeSnippetNode snippetNode = new Node.NativeSnippetNode(new NativeSnippet() {
                        @Override
                        public boolean isOrderedInputEvaluation() {
                            // Valójában nem, mert a MN az utolsó argumentum és nem az első,
                            // de mivel az mindig pure op lesz, ezért tökmindegy.
                            // Persze ehelyett inkább ki kéne egészíteni a JSSnippetNode feldolgozását Emitterben
                            // azzal, hogy ne vegye figyelembe a pure kifejezéseket a kifejezésinlineolási
                            // döntéseiben.
                            // Mező olvasást amúgy nem tekintenénk pure-nak, de itt final mezőt olvasunk
                            // (DMH.internalMemberName).
                            return true;
                        }

                        @Override
                        public String makeScript(List<String> args) {
                            StringBuilder sb = new StringBuilder();
                            if (!m.name.equals("linkToStatic"))
                                sb.append("/* ez itt hibás lesz, ").append(m.name).append(" kéne */");

                            sb.append('(').append(args.get(args.size() - 1)).append(')');
                            sb.append('(');
                            for (int i = 0; i < args.size() - 1; i++) {
                                if (i != 0)
                                    sb.append(", ");
                                sb.append(args.get(i));
                            }
                            sb.append(')');
                            return sb.toString();
                        }
                    }, m.type().returnType());
                    snippetNode.args.addAll(invokeSpecialOrStatic.args);
                    return snippetNode;
                }
            }
        }
        if (node instanceof GetStaticNode getStaticNode)
            if (getStaticNode.field.clazz.name.equals(JSReplacementProvider.Undefined) && getStaticNode.field.name.equals("UNDEFINED"))
                return new NativeSnippetNode(args -> "undefined", getStaticNode.field.type());
        if (node instanceof AllocateArray allocateArray) {
            if (allocateArray.lengthList.size() == 1) {
                return new InvokeSpecialOrStatic(singleLevelAllocator(),
                        new ConstantNode(allocateArray.arrayType),
                        allocateArray.lengthList.get(0));
            } else {
                AllocateArray lengths = new AllocateArray(
                        new ArrayType(PrimitiveType.I),
                        new ConstantNode(allocateArray.lengthList.size())
                );

                InvokeSpecialOrStatic alloc = new InvokeSpecialOrStatic(multiLevelAllocator(),
                        new ConstantNode(allocateArray.arrayType),
                        lengths
                );

                SequenceNode seq = new SequenceNode(new MethodKey("alloc multi dim array " + allocateArray.arrayType),
                        lengths,
                        alloc
                );

                for (int i = 0; i <allocateArray.lengthList.size(); i++)
                    seq.nodes.add(seq.nodes.size()-1,
                            new ArrayStore(lengths, new ConstantNode(i),
                                    allocateArray.lengthList.get(i),
                                    PrimitiveType.I));

                return seq;
            }
        }
        return node;
    }

    private Method singleLevelAllocator() {
        return ctx.findMethodOrNull(
                ctx.findClass(KnownClass.ArrayHelper),
                "allocate",
                new MethodType(List.of(
                        ctx.findClass(KnownClass.CLASS),
                        PrimitiveType.I
                ), ctx.findClass(KnownClass.OBJECT)));
    }

    private Method multiLevelAllocator() {
        return ctx.findMethodOrNull(
                ctx.findClass(KnownClass.ArrayHelper),
                "allocate",
                new MethodType(List.of(
                        ctx.findClass(KnownClass.CLASS),
                        new ArrayType(PrimitiveType.I)
                ), ctx.findClass(KnownClass.OBJECT)));
    }

    @Override
    public Node exit(Node node) {
        return node;
    }
}
