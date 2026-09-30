package com.flyordie.code.js;

import com.flyordie.code.*;
import com.flyordie.code.Clazz.Field;
import com.flyordie.code.Clazz.Method;
import com.flyordie.code.CompilationContext.TransformationFactory;
import com.flyordie.code.Interpreter.Array;
import com.flyordie.code.Interpreter.ClassObj;
import com.flyordie.code.Interpreter.Obj;
import com.flyordie.code.Interpreter.Statics.StaticsHolder;
import com.flyordie.code.Node.*;
import com.flyordie.code.Node.SequenceNode.MethodKey;
import com.flyordie.code.Node.Switch.SwitchType;
import com.flyordie.code.Type.ArrayType;
import com.flyordie.code.Type.PrimitiveType;
import com.flyordie.code.Variable.LocalVar;
import com.flyordie.code.Variable.LocalVar.Kind;
import com.flyordie.code.js.JSConstantPool.Entry;
import com.flyordie.code.js.JSConstantPool.Entry.State;
import com.flyordie.code.js.JSInteropProvider.NativeMethodKind;
import com.flyordie.code.js.JSInteropProvider.NativeTypeKind;
import com.flyordie.code.js.JSReplacementProvider.ArrayListImpl;
import com.flyordie.code.js.JSReplacementProvider.NativeMapData;
import com.flyordie.code.runtime.Fiber;
import com.flyordie.code.util.StringEscape;
import ui11.reflectutil.ReflectionUtil;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

import javax.annotation.Nonnull;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.*;
import static org.objectweb.asm.Opcodes.*;

public class JSEmitter extends AbstractEmitter {

    public static final String FOR_ANN_DESC = JSReplacementProvider.For.class.descriptorString();
    private static final boolean PRINT_METHOD_USAGES = false;
    private static final boolean PRINT_METHOD_CC_TYPING = false;
    private static final boolean PRINT_CLASS_CC_TYPING = false;
    public static final String NativeMapData_CLASS_NAME = ReflectionUtil.internalName(NativeMapData.class);
    public static final String StaticsHolder_CLASS_NAME = ReflectionUtil.internalName(StaticsHolder.class);

    public final JSInteropProvider interopProvider;
    private final Map<MethodKey, String> labels = new HashMap<>();
    private final Map<ClassObj, String> classValueIDs = new HashMap<>();
    private final JSConstantPool constantPool = new JSConstantPool(this);
    public final List<String> externs = new ArrayList<>();

    // ld. komment beginTypeban
    private Writer origOut;
    private StringWriter typeDefOut;

    private Field emittingStaticFinal;
    private Clazz Undefined;

    public JSEmitter(Writer out, JSInteropProvider interopProvider, boolean compactTypeNames) {
        super(out, compactTypeNames);
        this.interopProvider = interopProvider;
    }

    @Override
    public void setEmissionContext(EmissionContext emissionContext) {
        super.setEmissionContext(emissionContext);
        Undefined = emissionContext.compContext.findClass(com.flyordie.code.js.JSValue.Undefined.class);
    }

    @Override
    public ReplacementProvider makeReplacementProvider() {
        return new JSReplacementProvider(ctx, emissionContext, this);
    }

    @Override
    public List<TransformationFactory> intermediateTransformations() {
        return List.of(ctx -> new JSTransformer(this, ctx, emissionContext));
    }

    @Override
    public List<TransformationFactory> finalTransformations() {
        return List.of(mcc -> new FinalJSTransformer(mcc, this));
    }

    protected void printMethodImpl(Method method, Node body) {
        printLine("// " + method.toString() + (compilingMethod == method ? "" : "; replaced by " + compilingMethod));
        if (PRINT_METHOD_USAGES)
            printLine("// " + emissionContext.usagesString(method, 5).replace("\n", "\n// "));

        String functionName = methodToFunctionName(method);
        if (PRINT_METHOD_CC_TYPING)
            printLine("/**");

        StringBuilder sb = new StringBuilder();
        sb.append("function ").append(functionName).append("(");
        int argNum = 0;
        int firstPrintedArg = method.isStatic() ? 0 : 1;
        for (Type argType : method.fullArgTypes()) {
            if (argNum >= firstPrintedArg) {
                if (argNum != firstPrintedArg)
                    sb.append(", ");
                String argName = id(new LocalVar(compilingMethod, argNum, Kind.ofType(argType), method.rootMethodIdentity));
                usedLocalVars.add(argName);
                sb.append(argName);
                if (PRINT_METHOD_CC_TYPING)
                    printLine(" * @param {" + ccType(argType) + "} " + argName);
            }
            argNum += argType.slotSize();
        }
        if (PRINT_METHOD_CC_TYPING)
            printLine(" * @return {" + ccType(method.type().returnType()) + "}");
        if (PRINT_METHOD_CC_TYPING)
            printLine(" */");

        sb.append(") {");

        boolean canSuspend;
        if (Fiber.ENABLE_MULTITHREADING) {
            boolean[] canSuspendA = {false};
            body.walk(n -> {
                if (n instanceof InvokeVirtualOrInterface || n instanceof InvokeSpecialOrStatic || n instanceof Node.NativeSnippetNode)
                    canSuspendA[0] = true;
            });
            canSuspend = canSuspendA[0];
        } else
            canSuspend = false;

        printLine(sb.toString());
        indent++;
        if (canSuspend) {
            printLine("try {");
            indent++;
        }

        if (body instanceof SequenceNode seq) {
            printSequenceNodeBody(seq);
        } else if (body instanceof NopNode nop) {
            // ha sequencenodeban lenne, Optimizer2 kiszedné
            doPrint(nop);
        } else {
            printLine("return " + n(body) + ";");
        }
        if (canSuspend) {
            indent--;
            printLine("} catch(ce) {");
            indent++;
            if (!usedLocalVars.isEmpty()) {
                printLine("if (ce == VT_SUSPEND)");
                indent++;
                printLine("vtPush(" + String.join(", ", usedLocalVars) + ");");
                indent--;
            }
            printLine("throw ce;");
            indent--;
            printLine("}");
        }
        indent--;
        printLine("}");
        printLine("");
    }

    private String ccType(Type type) {
        if (type instanceof Clazz c) {
            if (c.knownClass == KnownClass.STRING)
                return "string";
            return typeName2(type);
        }
        if (type instanceof ArrayType arrayType)
            return "Array<" + ccType(arrayType.elementType()) + ">";
        if (type instanceof PrimitiveType primitiveType) {
            return switch (primitiveType) {
                case Z, B, S, C, I, F, D -> "number";
                case J -> "Object";
                case V -> "void";
            };
        }
        throw new RuntimeException("unknown type: " + type);
    }

    public void printType(Clazz replacementClazz, Clazz clazz) {
        String typeNativeName = typeName(replacementClazz);

        boolean isNativeType = beginType(replacementClazz, clazz, typeNativeName);

        // instance fields
        printLine(typeNativeName + ".t=" + typeName(ctx.findClass(KnownClass.CLASS)) + ";");
        printLine(typeNativeName + ".jtn='" + clazz.name.replace('/', '.') + "';");
        printLine(typeNativeName + ".typeName='" + typeNativeName + "';");
        if (isNativeType && typeNativeName.equals("EventTarget"))
            printLine("if (!" + typeNativeName + ".prototype) " + typeNativeName + ".prototype={};");
        printLine(typeNativeName + ".hc=" + ctx.interpreter().identityHashCode(ctx.interpreter().fromType(clazz)) + ";");
        if (clazz.outerClass == null)
            printLine(typeNativeName + ".declaringClass=null;");
        else
            printLine(typeNativeName + ".declaringClass=self." + typeName2(ctx.findClass(clazz.outerClass)) + "||null;");

        printLine(typeNativeName + ".prototype.t=" + typeNativeName + ";");
        replacementClazz.allFields().forEach(f -> {
            if ((f.access & ACC_STATIC) == 0) {
                printLine(typeNativeName + ".prototype." + fieldName(f) + " = " + defaultValue(f.desc) + ";");
            }
        });

        for (FieldNode field : replacementClazz.fields) {
            Field f = (Field) field;
            if (emissionContext.compContext.interpreter().reflectivelyUsedMembers.contains(f)) {
                String name = fieldName(f);
                printLine("fieldNames[" + f.globalNumber + "] = {" +
                        "fieldGetter: o => o." + name + ", " +
                        "fieldSetter: (o, v) => o." + name + " = v};");
            }
        }

        for (MethodNode method : replacementClazz.methods) {
            Method m = (Method) method;
            if (emissionContext.compContext.interpreter().reflectivelyUsedMembers.contains(m)) {
                String name = methodToFunctionName(m);
                printLine("fieldNames[" + m.globalNumber + "] = {" +
                        (m.isAbstract() ? "" : "asStaticMethod: " + name + ", ") +
                        "asVirtualMethod: o => o." + name + "};");
            }
        }

        printInheritanceAndMethods(clazz, typeNativeName, isNativeType);
        if (clazz.knownClass == KnownClass.OBJECT)
            printInheritanceAndMethods(clazz, "Array", isNativeType);


        for (Map.Entry<ClassObj, Object> cvEntry : emissionContext.classValueMap(clazz).entrySet()) {
            constantPool.constant(cvEntry.getValue(), cvVal -> {
                printLine(typeNativeName + "." + classValueID(cvEntry.getKey()) + " = " + cvVal + ";");
            });
        }
        printLine("initJavaLangClassPrototype(" + typeNativeName + ", " + typeName2(ctx.findClass(KnownClass.CLASS)) + ");");
        printLine("");
    }

    private boolean beginType(Clazz replacementClazz, Clazz clazz, String typeNativeName) {
        boolean isNativeType = isNativeType(replacementClazz);
        final NativeTypeKind nativeTypeKind = interopProvider.nativeTypeKind(replacementClazz);
        boolean noInterfaceObject = nativeTypeKind != null && nativeTypeKind.noInterfaceObject;
        if (!isNativeType || noInterfaceObject) {
            if (PRINT_CLASS_CC_TYPING) {
                printLine("/**");
                if (clazz.isInterface())
                    printLine(" * @interface");
                else
                    printLine(" * @constructor");
                if (clazz.superclass != null)
                    printLine(" * @extends {" + typeName2(clazz.superclass) + "}");
                for (Clazz implementedInterface : clazz.superinterfaces)
                    printLine(" * @implements {" + typeName2(implementedInterface) + "}");
                printLine(" */");
            }
            printLine("function " + typeNativeName + "(){}");
        }
        return isNativeType;
    }

    private void printInheritanceAndMethods(Clazz clazz, String typeNativeName, boolean isNativeType) {
        if (clazz.superclass != null)
            if (isNativeType || clazz.knownClass == KnownClass.CLASS)
                // beépített objektumoknak nem lehet __proto__-t állítani, mert exception lesz
                // j.l.Classnak meg azért nem, mert akkor initJavaLangClassPrototype elromlik
                printInheritanceAndMethods(clazz.superclass, typeNativeName, isNativeType);
            else
                printLine(typeNativeName + ".prototype.__proto__ = " + typeName(clazz.superclass) + ".prototype;");

        for (Clazz t : clazz.allAncestorTypesAndThis)
            // if (clazz.superclass == null || !clazz.superclass.allAncestorTypesAndThis.contains(t))
            // szándékosan [''] van . helyett, hogy CC ne kavarja össze, mert kell a checkcasthoz
            printLine(typeNativeName + "['" + typeName2(t) + "'] = 1;");

        if (clazz.supertype() != null) // csak reflectionnek kell
            printLine(typeNativeName + ".st=" + typeName(clazz.supertype()) + ";");

        printMethods(clazz, typeNativeName);
    }

    @SuppressWarnings("unchecked")
    private void printMethods(Clazz clazz, String typeNativeName) {
        for (MethodNode method : clazz.methods) {
            if ((method.access & (ACC_STATIC | ACC_ABSTRACT)) != 0
                    || method.name.equals("<init>"))
                continue;
            Method m = (Method) method;

            for (Clazz c : clazz.allAncestorTypesAndThis) {
                for (Method m2 : (List<Method>) (List<? extends MethodNode>) c.methods) {
                    if ((m == m2 || ctx.canOverride(m2, m)) && emissionContext.usedMethodsV.containsKey(m2)) {
                        // itt lehetne egy assert hogy a resolveVirtualMethodOrFail által visszaadott metódus volt-e
                        // enqueueolva
                        printLine(typeNativeName + ".prototype." +
                                methodToFunctionName(m2) + " = " +
                                methodToFunctionName(m) + ";");
                    }
                }
            }
        }

        // default method-ok kezelése, mert azok nem szerepelhetnek a prototype chainben.
        // és azoknak a metódusoknak, amik implementálnak egy interface metódust, de a deklaráló
        // osztályuk nem leszármazottja annak az interface-nek (pl. HashMap$HashIterator.hasNext)
        for (Clazz ancestor : clazz.allAncestorTypesAndThis) {
            if (ancestor == clazz || (ancestor.access & ACC_INTERFACE) == 0)
                continue;
            for (Method interfaceMethod : (List<Method>) (List<? extends MethodNode>) ancestor.methods) {
                if ((interfaceMethod.access & ACC_STATIC) != 0
                        || !emissionContext.usedMethodsV.containsKey(interfaceMethod))
                    continue;

                Method resolvedMethod = ctx.resolveVirtualMethodOrNull(interfaceMethod, clazz);
                if (resolvedMethod != null && (
                        (resolvedMethod.access & ACC_ABSTRACT) == 0 && resolvedMethod.clazz.isInterface() ||
                                !resolvedMethod.clazz.allAncestorTypesAndThis.contains(ancestor)))
                    printLine(typeNativeName + ".prototype." +
                            methodToFunctionName(interfaceMethod) + " = " +
                            methodToFunctionName(resolvedMethod) + ";");
            }
        }
    }

    @Override
    public void begin() {
        // különben belekavarodunk abba, hogy olyan függvényneveket
        // generál a CC, amelyik ütközik objektum property nevekkel
        printLine("(function() {");
        printLine("");

        try (Reader r = new InputStreamReader(
                JSEmitter.class.getResourceAsStream("runtime.js"),
                StandardCharsets.UTF_8)) {
            r.transferTo(out);
        } catch (IOException e) {
            // valójában nem tudjuk hogy a beolvasásból vagy a kiírásból van-e az IO hiba
            handleIOError(e);
        }
    }

    @Override
    public void end() {
        this.out = origOut;
        try {
            out.write(typeDefOut.toString());
        } catch (IOException e) {
            handleIOError(e);
        }

        printCP();
    }

    // ezt a printExportMethod hívások után kell meghívni
    public void finish() {
        printLine("})();");
    }

    private void printCP() {
        for (Entry entry : constantPool.finish()) {
            assert entry.state == State.NOT_WRITTEN;

            printLineBegin("var " + entry.getOrCreateName() /*+ (" /* " + entry.dfsExit + " * /")*/ + " = ");
            print(entry.js());
            printLine(";");

            assert entry.state == State.NOT_WRITTEN;
            entry.state = State.WRITTEN;
            for (Runnable task : entry.afterWrite)
                task.run();
        }
    }

    @Override
    public void printStaticField(Field f, Object value) {
        emittingStaticFinal = f;
        constantPool.constant(value, constantID -> {
            printLine("var " + fieldName(f) + " = " + constantID + ";");
        });
        emittingStaticFinal = null;
    }

    @Override
    public boolean isNativeType(Clazz clazz) {
        if (interopProvider.nativeTypeKind(clazz) != null)
            return true;

        AnnotationNode forAnn = findAnnotation(clazz, FOR_ANN_DESC);
        if (forAnn != null)
            for (int i = 0; i < forAnn.values.size(); i += 2)
                if (forAnn.values.get(i).equals("nativeType"))
                    return true;

        Clazz replacement = emissionContext.transformationChain.replacementProvider.replacementClass(clazz);
        if (replacement != null && isNativeType(replacement))
            return true;

        return false;
    }

    @Override
    public void printStubType(Clazz replacementClass, Clazz clazz) {
        // régebben úgy volt, hogy stubtype esetén nem állított be semmilyen értéket egy adott típusnak,
        // de az nem jó, mert lehet hogy supertypeként hivatkozik rá valaki
        String name = typeName(replacementClass);
        beginType(replacementClass, clazz, name);
        printLine(name + ".typeName = '" + name + "';");
        printLine(name + ".jtn = '" + clazz.name.replace('/', '.') + "';");
        printLine(name + ".prototype.t = " + name + ";");
        printLine(name + "." + name + " = 1;");
    }

    public void printArrayType(ArrayType t) {
        String n = typeName(t);

        printLine("var " + n + " = {");
        indent++;
        printLine("typeName:'" + n + "', ");
        printLine("ais: " + t.elementType().bytesSize() + ", "); // arrayIndexScale
        printLine("jtn: '" + t.descriptor() + "', ");
        printLine("e: " + typeName(t.elementType()) + ", ");

        indent--;
        printLine("};");


        // azért lett [''] json property helyett, hogy CC ne kavarja össze a nevet,
        // mert kell a checkcasthoz, ld. printType
        printLine(n + "['" + n + "'] = 1;");
        Set<Type> visitedSupertypes = new HashSet<>();
        visitedSupertypes.add(t);
        for (Type t2 : t.allAncestorsAndThis())
            if (visitedSupertypes.add(t2))
                //printLine(typeName2(t2) + ": 1, ");
                printLine(n + "['" + typeName2(t2) + "'] = 1;");

        // hogy arrayTypeFromCompType el tudja érni.
        // eddig window-ban próbálta keresni, de az most nem működik,
        // hogy az egész kód bekerült egy functionbe.
        printLine("arrayTypes['" + n + "'] = " + n + ";");
    }

    protected String nativeTypeName2(Clazz clazz) {
        final NativeTypeKind nativeTypeKind = interopProvider.nativeTypeKind(clazz);
        if (nativeTypeKind == null || !nativeTypeKind.noInterfaceObject) {
            // ha ==null, akkor valszeg felesleges nativeTypeName-et hívni
            String n = interopProvider.nativeTypeName(clazz);
            if (n != null)
                return n;
        }

        AnnotationNode forAnn = findAnnotation(clazz, FOR_ANN_DESC);
        if (forAnn != null)
            for (int i = 0; i < forAnn.values.size(); i += 2)
                if (forAnn.values.get(i).equals("nativeType"))
                    return (String) forAnn.values.get(i + 1);
        return null;
    }


    private void doPrint(Node node) {
        if (!compact && !Objects.equals(prevLoc, node.location)) {
            if (prevLoc != null || node.location.elements().size() > 1)
                printLine("// " + node.location.toString());
            prevLoc = node.location;
        }

        if (node instanceof SequenceNode) {
            SequenceNode seq = (SequenceNode) node;
            String seqID = id(seq);
            if (seqID == null) {
                boolean hasReturn = hasReturnToIt(seq);
                if (hasReturn) {
                    assignId(seq);
                    seqID = id(seq);
                }
            }
            if (seqID != null) {
                labels.put(seq.methodKey, seqID);
                printLine(seqID + ": do {");
            }
            indent++;
            printSequenceNodeBody(seq);
            indent--;
            if (seqID != null)
                printLine("} while (false);");
        } else if (node instanceof IfElseNode ifNode/* && node.type() == Type.VOID_TYPE*/) {
            // ha nem void a típusa, akkor lehet ternary operátor is. csak
            // ellenőrizni kéne hogy nincs-e benne SequenceNode vagy LoopNode, mert akkor meg nem lehet.
            // ezért kommenteztem egyelőre ki.

            boolean v = ifNode.type() == PrimitiveType.V;
            if (!v) {
                String ifNodeID = id(ifNode);
                printLine("var " + ifNodeID + ";");
                usedLocalVars.add(ifNodeID);
            }
            statementWithBody("if (" + n(ifNode.conditionSlot.get()) + ")", ifNode.successBranch(),
                    ifNode.failureBranch() == null, v ? null : n(ifNode));
            if (ifNode.failureBranch() != null)
                statementWithBody("else", ifNode.failureBranch(),
                        true, v ? null : n(ifNode));
        } else if (node instanceof LoopNode) {
            LoopNode loopNode = (LoopNode) node;
            String id = "loop" + loopIDs.size();
            String prev;
            if ((prev = loopIDs.putIfAbsent(loopNode.loopKey, id)) != null)
                throw new RuntimeException("loop key duplicate: " + loopNode.loopKey + ", " + prev + ", " + id);
            // TODO targetVar?
            statementWithBody(id + ": while (true)", loopNode.bodySlot.get(), true, null);
        } else if (node instanceof ReturnNode) {
            ReturnNode ret = (ReturnNode) node;
            MethodKey returnFrom = ret.returnFrom;
            if (returnFrom == rootBlock.methodKey)
                if (ret.returnValueSlot.get() == null)
                    printLine("return;");
                else
                    printLine("return " + n(ret.returnValueSlot.get()) + ";");
            else {
                String label = Objects.requireNonNull(labels.get(ret.returnFrom), () -> ret.returnFrom + " " + rootBlock.methodKey);
                if (ret.returnValueSlot.get() != null)
                    printLine("var " + label + " = " + n(ret.returnValueSlot.get()) + ";");
                printLine("break " + label);
            }
        } else if (node instanceof BreakLoopNode breakLoopNode) {
            printLine("break " + loopIDs.get(breakLoopNode.loop) + ";");
        } else if (node instanceof ContinueLoopNode continueLoopNode) {
            printLine("continue " + loopIDs.get(continueLoopNode.loop) + ";");
        } else if (node instanceof WriteLocalVar wlv) {
            String varID = id(wlv.localVar);
            printLine((usedLocalVars.add(varID) ? "var " : "") + varID + " = " + n(wlv.valueSlot.get()) + ";");
        } else if (node instanceof PutFieldNode putfield) {
            printLine(n(putfield.object()) + "." + fieldName(putfield.field) + " = " + n(putfield.value()) + ";");
        } else if (node instanceof PutStaticNode putstatic) {
            printLine(fieldName(putstatic.field) + " = " + n(putstatic.value()) + ";");
        } else if (node instanceof ArrayStore arrayStore) {
            printLine("arraystore(" + n(arrayStore.arraySlot.get()) + ", " + n(arrayStore.indexSlot.get()) + ", " + n(arrayStore.valueSlot.get()) + ");");
        } else if (node instanceof ThrowNode throwNode)
            printLine("throw " + n(throwNode.exceptionSlot.get()) + ";");
        /*
        else if (node instanceof CheckCast checkCast) {
            Node v = checkCast.valueSlot.get();
            printLine("if (" + n(v) + " !== null && " +
                    "!" + n(v) + ".t." + classMetadataVarName2(checkCast.type)
                    + ") throw 'cce';");
            printLine(id(checkCast) + " = " + n(v) + ";");
        } */
        else if (node instanceof Switch switchNode) {
            if (switches.putIfAbsent(switchNode.switchBreakKey, switchNode) != null)
                throw new IllegalStateException();
            if (switchNode.switchType == SwitchType.SWITCH_EXPRESSION) {
                String switchNodeID = id(switchNode);
                if (switchNodeID == null)
                    // printSequenceNodeBody azt állapította meg hogy nem használjuk a switch expression értékét sehol,
                    // ezért nem rendelt hozzá ID-t
                    switchNodeID = "switchExpression_valueNotUsed";
                printLine("var " + switchNodeID + ";");
                usedLocalVars.add(switchNodeID);
            }
            printLine("switch (" + n(switchNode.input.get()) + ") {");
            indent += 2;
            printSequenceNodeBody((SequenceNode) switchNode.child.get());
            indent -= 2;
            printLine("};");
        } else if (node instanceof SwitchCase switchCase) {
            indent--;
            if (switchCase.isDefault())
                printLine("default:");
            else
                for (Integer val : switchCase.values())
                    printLine("case " + val + ":");
            indent++;
        } else if (node instanceof BreakSwitch breakSwitch) {
            // TODO azt is nézni kéne, hogy melyik switchet breakeli
            if (breakSwitch.resultSlot.get() != null) {
                String switchID = id(switches.get(breakSwitch.switchBreakKey));
                if (switchID != null)
                    // ha nem használjuk később egy switch expression eredményét semmire, akkor a switchID null lesz
                    printLine(switchID +
                            " = " + n(breakSwitch.resultSlot.get()) + ";");
            }
            printLine("break;");
        } else if (node instanceof NopNode nop) {
            printLine("; // " + nop.toString());
        } else {
            String id = id(node);
            // most ez itt zavaros, hogy a != V itt van, a többi meg printSequenceNodeBody-ban
            if (id != null && node.type() != PrimitiveType.V) {
                String s = printExpression(node);
                printLine("var " + id + " = " + s + ";");
                usedLocalVars.add(id);
            } else
                printLine(printExpression(node) + ";");
        }
    }

    @Nonnull
    private static boolean hasReturnToIt(SequenceNode seq) {
        boolean[] hasReturn = new boolean[1];
        seq.walk(n -> {
            if (n instanceof ReturnNode ret && ret.returnFrom == seq.methodKey)
                hasReturn[0] = true;
        });
        return hasReturn[0];
    }

    private void statementWithBody(String statement, Node body, boolean close, String targetVar) {
        if (body instanceof SequenceNode seq) {
            assert id(seq) == null;
            boolean hasReturn = hasReturnToIt(seq);
            if (hasReturn) {
                assignId(seq);
                printLine(statement);
                doPrint(seq);
            } else {
                printLine(statement + " {");
                indent++;
                printSequenceNodeBody((SequenceNode) body);
                if (targetVar != null)
                    printLine(targetVar + " = " + n(body) + ";");
                indent--;
                if (close)
                    printLine("}");
                else
                    printLineBegin("}");
            }
        } else {
            printLine(targetVar == null ? statement : statement + " {");
            indent++;
            if (body == null)
                printLine(";");
            else {
                doPrint(body);
                if (targetVar != null)
                    printLine(targetVar + " = " + n(body) + ";");
            }
            indent--;
            if (targetVar != null)
                if (close)
                    printLine("}");
                else
                    printLineBegin("}");
            // itt kéne ellenőrizni, hogy nem lett-e több 1 sornál
        }
    }

    private void printSequenceNodeBody(SequenceNode seq) {
        Set<Node> hasAdditionalSideEffects = new HashSet<>();
        List<Node> nodes = new ArrayList<>(seq.nodes);
        for (ListIterator<Node> li = nodes.listIterator(); li.hasNext(); ) {
            Node n = li.next();
            if (n.sideEffects().isEmpty() && !hasAdditionalSideEffects.contains(n)) {
                li.remove();
                continue;
            }
            if (n.deterministicInputEvaluationOrder()) {
                List<Node> inputs = n.inputs();
                for (int i = inputs.size() - 1; i >= 0; i--) {
                    Node input = inputs.get(i);
                    if (input == null)
                        continue;
                    if (input.usages2().size() != 1)
                        break;

                    Node n2 = li.previous();
                    assert n2 == n;

                    if (li.hasPrevious()) {
                        Node prev = li.previous();
                        if (input == prev) {
                            li.remove();
                            if (!prev.sideEffects().isEmpty() && !hasAdditionalSideEffects.contains(prev))
                                hasAdditionalSideEffects.add(n);
                        } else {
                            n2 = li.next();
                            assert prev == n2;
                        }
                    }

                    n2 = li.next();
                    assert n2 == n;
                }
            }
        }

        int i = 0;
        for (Node n : nodes) {
            if (n instanceof ReturnNode ret && ret.returnFrom == seq.methodKey
                    && n == nodes.get(nodes.size() - 1) && seq != rootBlock) {
                if (ret.returnValueSlot.get() != null) {
                    String seqNodeID = nodeIDs.get(seq);
                    assert seqNodeID != null;
                    printLine("var " + seqNodeID + " = " + n(ret.returnValueSlot.get()));
                    usedLocalVars.add(seqNodeID);
                }
            } else if (n.usages().isEmpty()) {
                doPrint(n);
            } else {
                assignId(n);
                doPrint(n);
                // printLine("// " + usedOnlyInNextStatement + ", " + nextHasAdditionalSideEffects + ", " + n.sideEffects());
            }
            i++;
        }
        if (seq.resultSlot.get() != null)
            if (seq.root)
                printLine("return " + n(seq.resultSlot.get()) + ";");
            else {
                String seqNodeID = nodeIDs.get(seq);
                if (seqNodeID != null) {
                    printLine("var " + seqNodeID + " = " + n(seq.resultSlot.get()) + ";");
                    usedLocalVars.add(seqNodeID);
                }
            }
    }

    protected String printExpression(Node node) {
        assert node.isAttached2() : node;

        String s;
        if (node instanceof ConstantNode) {
            Object val = ((ConstantNode) node).value;
            s = constantPool.constant(val);
        } else if (node instanceof UnaryNumericOpNode n) {
            String a = n(n.inputSlot.get());
            s = switch (n.op.code) {
                case INEG -> "(- " + a + "|0)"; // mínuszjel után space, hogy negatív számot is kivonhasson
                case IFEQ -> "!" + a;
                case IFNE -> "!!" + a;
                case IFLT -> "(" + a + "<0)";
                case IFLE -> "(" + a + "<=0)";
                case IFGT -> "(" + a + ">0)";
                case IFGE -> "(" + a + ">=0)";
                case LNEG -> a + ".negate()";
                case FNEG, DNEG -> "-(" + a + ")";
                case I2L -> "ll.fromNumber(" + a + ")";
                case I2B -> "(" + a + "<<24>>24)";
                case I2C -> "(" + a + "&0xFFFF)";
                case I2S -> "(" + a + "<<16>>16)";
                case L2I -> a + ".toInt()";
                case F2D, D2F -> a;
                case F2I, D2I -> "(" + a + "|0)";
                case I2F, I2D -> a;
                case L2D, L2F -> a + ".toNumber()";
                case D2L, F2L ->
                        "ll.fromNumber(" + a + ")"; // TODO meg kéne nézni, hogy ez kerekít-e lefele, csak exceptiont dob
                // '!' nem jó IFNULL-ra, mert üres stringet is nullnak tekintene
                // esetleg azt lehetne, hogy csak akkor használjuk, ha tudjuk, hogy lehet String a vizsgálandó érték
                case IFNULL -> "(" + a + " == null)";
                case IFNONNULL -> "(" + a + " != null)";
                default -> throw new RuntimeException("unsupported operator in: " + n);
            };
        } else if (node instanceof BinaryNumericOpNode binaryOpNode) {
            String a = n(binaryOpNode.input1Slot.get());
            String b = n(binaryOpNode.input2Slot.get());
            s = switch (binaryOpNode.op.code) {
                case IADD -> "(" + a + "+" + b + "|0)";
                case ISUB -> "(" + a + "- " + b + "|0)"; // mínuszjel után space, hogy negatív számot is kivonhasson
                case IMUL -> "imul(" + a + "," + b + ")";
                case IDIV -> "(" + a + "/" + b + "|0)";
                case IREM -> "(" + a + "%" + b + ")";
                case ISHL -> "(" + a + "<<" + b + ")";
                case ISHR -> "(" + a + ">>" + b + ")";
                case IUSHR -> "(" + a + ">>>" + b + ")";
                case IOR -> "(" + a + "|" + b + ")";
                case IAND -> "(" + a + "&" + b + ")";
                case IXOR -> "(" + a + "^" + b + ")";
                case IF_ICMPEQ -> "(" + a + "===" + b + ")";
                case IF_ICMPNE -> "(" + a + "!==" + b + ")";
                case IF_ICMPLT -> "(" + a + "<" + b + ")";
                case IF_ICMPLE -> "(" + a + "<=" + b + ")";
                case IF_ICMPGT -> "(" + a + ">" + b + ")";
                case IF_ICMPGE -> "(" + a + ">=" + b + ")";
                case LADD -> a + ".add(" + b + ")";
                case LSUB -> a + ".subtract(" + b + ")";
                case LMUL -> a + ".multiply(" + b + ")";
                case LDIV -> a + ".div(" + b + ")";
                case LREM -> a + ".modulo(" + b + ")";
                case LSHL -> a + ".shiftLeft(" + b + ")";
                case LSHR -> a + ".shiftRight(" + b + ")";
                case LUSHR -> a + ".shiftRightUnsigned(" + b + ")";
                case LOR -> a + ".or(" + b + ")";
                case LAND -> a + ".and(" + b + ")";
                case LXOR -> a + ".xor(" + b + ")";
                case LCMP -> a + ".compare(" + b + ")";
                case FCMPG, DCMPG -> "(" + a + "<" + b + "?-1:" + a + "!==" + b + ")";
                case FCMPL, DCMPL -> "(" + a + ">" + b + "?1:" + a + "===" + b + "?0:-1)";
                case FADD, DADD -> "(" + a + "+" + b + ")";
                case FSUB, DSUB -> "(" + a + "-" + b + ")";
                case FMUL, DMUL -> "(" + a + "*" + b + ")";
                case FDIV, DDIV -> "(" + a + "/" + b + ")";
                case FREM, DREM -> "(" + a + "%" + b + ")";
                case IF_ACMPEQ -> binaryOpNode.input1Slot.get().type() == Undefined ||
                        binaryOpNode.input2Slot.get().type() == Undefined ?
                        "(" + a + "===" + b + ")" :
                        "(" + a + "==" + b + ")";
                case IF_ACMPNE -> binaryOpNode.input1Slot.get().type() == Undefined ||
                        binaryOpNode.input2Slot.get().type() == Undefined ?
                        "(" + a + "!==" + b + ")" :
                        "(" + a + "!=" + b + ")";
                default -> throw new RuntimeException("unsupported operator in: " + binaryOpNode);
            };
        } else if (node instanceof LogicalAnd and) {
            final Node n1 = and.input1.get();
            final Node n2 = and.input2.get();
            s = "(" + n(n1) + " && " + n(n2) + ")";
        } else if (node instanceof LogicalOr or) {
            final Node n1 = or.input1.get();
            final Node n2 = or.input2.get();
            s = "(" + n(n1) + " || " + n(n2) + ")";
        } else if (node instanceof IfElseNode ifNode) {
            s = n(ifNode.conditionSlot.get())
                    + " ? " + n(ifNode.successBranch())
                    + " : " + n(ifNode.failureBranch());
        } else if (node instanceof PhiNode phiNode) {
            boolean failOnNoMatch = false;

            if (failOnNoMatch) {
                StringBuilder sb = new StringBuilder("(");
                for (int i = 0; i < phiNode.conditionList.size(); i++)
                    sb.append(n(phiNode.conditionList.get(i))).append(" ? ");
                sb.append("'vacak'");
                for (int i = phiNode.conditionList.size() - 1; i >= 0; i--)
                    sb.append(" : ").append(n(phiNode.valueList.get(i)));
                sb.append(")");
                return sb.toString();
            } else {
                if (phiNode.conditionList.size() == 1)
                    return printExpression(phiNode.valueList.get(0));

                StringBuilder sb = new StringBuilder("(");
                for (int i = 0; i < phiNode.conditionList.size() - 1; i++) {
                    if (i != 0)
                        sb.append(" ? ");
                    sb.append(n(phiNode.conditionList.get(i)));
                }
                for (int i = phiNode.conditionList.size() - 1; i >= 0; i--)
                    sb.append(i == phiNode.conditionList.size() - 1 ? " ? " : " : ").append(n(phiNode.valueList.get(i)));
                sb.append(")");
                return sb.toString();
            }
        } else if (node instanceof SequenceNode && ((SequenceNode) node).nodes.isEmpty())
            s = "null";
        else if (node instanceof ReadLocalVar)
            s = id(((ReadLocalVar) node).localVar);
        else if (node == null)
            s = "<null node>";
        else if (node instanceof SequenceNode) {
            assert id(node) == null;
            assignId(node);
            doPrint(node);
            s = id(node);
        } else if (node instanceof GetFieldNode getfield) {
            s = n(getfield.objectSlot.get()) + "." + fieldName(getfield.field);
        } else if (node instanceof ObjectNode objectNode) {
            Type objectType = objectNode.objectIdentity.type;
            s = instanceCreation(objectType);
        } else if (node instanceof InvokeSpecialOrStatic invokeNode) {
            StringBuilder sb = new StringBuilder();
            sb.append(methodToFunctionName(invokeNode.method));
            if (invokeNode.method.isStatic()) // invokestatic
                printArgList(sb, invokeNode.args, 0);
            else { // invokespecial
                sb.append(".call");
                printArgList(sb, invokeNode.args, 0);
            }
            s = sb.toString();
        } else if (node instanceof InvokeVirtualOrInterface invokeNode) {
            StringBuilder sb = new StringBuilder();
            if (invokeNode.method.isPolySigMethodSpecialization) {
                // ezeket nem tudjuk virtuális metódusként hívni, mert nincsenek bejegyezve a
                // típus virtuális függvényei közé.
                // meg ezek inline-olva szoktak lenni, szóval csak szélsőséges esetben lyukadunk ki ide.
                sb.append(methodToFunctionName(invokeNode.method)).append(".call");
                printArgList(sb, invokeNode.args, 0);
            } else {
                sb.append(n(invokeNode.args.get(0))).append(".");
                sb.append(methodToFunctionName(invokeNode.method));
                printArgList(sb, invokeNode.args, 1);
            }
            s = sb.toString();
        } else if (node instanceof NativeCallNode nativeCallNode) {
            s = handleNativeInterfaceCall(nativeCallNode);
        } else if (node instanceof GetStaticNode getStaticNode)
            s = fieldName(getStaticNode.field);
        else if (node instanceof ArrayLoad arrayLoad)
            // TODO bounds check
            s = n(arrayLoad.arraySlot.get()) + "[" + n(arrayLoad.indexSlot.get()) + "]";
        else if (node instanceof ArrayLength arrayLength)
            s = n(arrayLength.arraySlot.get()) + ".length";
        else if (node instanceof AllocateArray allocateArray)
            throw new RuntimeException("should not reach here"); // JSFinalTransformernek cserélie kellett volna már
        else if (node instanceof Node.NativeSnippetNode nativeSnippetNode) {
            s = nativeSnippetNode.snippet.makeScript(nativeSnippetNode.args.stream().map(this::n).toList());
        } else if (node instanceof Switch switchNode) {
            assignId(node);
            doPrint(node);
            return id(node);
        } else if (node instanceof NativeUpcallStubNode nativeUpcallStubNode) {
            String upcallStubName = "makeUpcallHandler_" + typeName(nativeUpcallStubNode.type());
            String functionalInterfaceVar = n(nativeUpcallStubNode.functionalInterfaceSlot.get());
            return upcallStubName + "(" + functionalInterfaceVar + ")";

            // régi böngészőkben nincs bind
            // return upcallStubName + ".bind(" + functionalInterfaceVar + ")";
        } else if (node instanceof TypeOfNode typeOf) {
            return n(typeOf.inputSlot.get()) + ".t";
        } else if (node instanceof ClassValueNode classValueNode)
            return "cvNonnull(" + n(classValueNode.typeObjSlot.get()) + ", " +
                    n(classValueNode.typeObjSlot.get()) + "." + classValueID(classValueNode.classValueInstance) + ")";
        else if (node instanceof ErrorNode en)
            return "transpilerBug(" + stringLiteral(en.toString()) + ")";
        else if (node instanceof ReinterpretCastNode reinterpretCast) {
            return n(reinterpretCast.input.get());
        } else
            throw new RuntimeException("unknown node type: " + node + " (isAttached=" + node.isAttached() + ")");
        return s;
    }

    @Nonnull
    private String classValueID(ClassObj classValueObj) {
        return classValueIDs.computeIfAbsent(classValueObj, __ -> "cv" + classValueIDs.size());
    }

    private String handleNativeInterfaceCall(NativeCallNode invokeNode) {
        StringBuilder sb = new StringBuilder();

        Method m = invokeNode.method;
        NativeMethodKind k = invokeNode.nativeMethodKind;
        boolean constructor = k == NativeMethodKind.CONSTRUCTOR;
        if (constructor)
            sb.append("new ").append(n(invokeNode.args.get(0)));
        else
            sb.append(n(invokeNode.args.get(0)));
        if (k == NativeMethodKind.DYNAMIC_SETTER) {
            if (m.type().parameterTypes().size() != 2 || (m.access & ACC_VARARGS) != 0)
                throw new RuntimeException(m.toString());
            sb.append('[');
            sb.append(n(invokeNode.args.get(1)));
            sb.append("] = ").append(n(invokeNode.args.get(2)));
            // zárójelezésről ld. lenti komment
        } else if (k == NativeMethodKind.DYNAMIC_GETTER) {
            if (m.type().parameterTypes().size() != 1 || (m.access & ACC_VARARGS) != 0)
                throw new RuntimeException(m.toString());
            sb.append('[');
            sb.append(n(invokeNode.args.get(1)));
            sb.append(']');
        } else {
            if (!constructor)
                sb.append(".").append(methodToFunctionName(m));
            if ((m.access & ACC_VARARGS) != 0) {
                if (constructor)
                    throw new RuntimeException("constructors with varargs not yet supported");

                // bele lehetne rakni az apply hívást a nativeInterfaceVarargsba,
                // de úgy belekerülne a stack tracebe egy nem releváns hívás

                sb.append(".apply(").append(n(invokeNode.args.get(0))).append(", ");
                ArrayType objArray = new ArrayType(ctx.findClass(KnownClass.OBJECT));
                sb.append(methodToFunctionName(ctx.method(KnownClass.UNSAFE2,
                        "nativeInterfaceVarargs", objArray, objArray, objArray)));
                sb.append("([");
                for (int i = 1; i < invokeNode.args.size() - 1; i++) {
                    if (i != 1)
                        sb.append(',').append(' ');
                    sb.append(n(invokeNode.args.get(i)));
                }
                sb.append("], ").append(n(invokeNode.args.get(invokeNode.args.size() - 1))).append("))");
            } else if (k == NativeMethodKind.GETTER) {
                if (invokeNode.args.size() != 1)
                    throw new RuntimeException(m.toString());
            } else if (k == NativeMethodKind.SETTER) {
                if (invokeNode.args.size() != 2)
                    throw new RuntimeException(m.toString());
                sb.append(" = ").append(n(invokeNode.args.get(1)));
                // reméljük hogy nem fog bekerülni sose expressionként (mert akkor zárójelezni kéne)
            } else
                printArgList(sb, invokeNode.args, 1);
        }

        return sb.toString();
    }

    public void printNativeUpcallStub(Clazz nativeUpcallFunctionalInterface) {
        Method m = EmissionContext.methodInFunctionalInterface(nativeUpcallFunctionalInterface);

        String argNames = IntStream.range(0, m.type().parameterTypes().size()).
                mapToObj(i -> "arg" + i).
                collect(joining(", ", "(", ")"));
        printLine("function makeUpcallHandler_" + typeName(nativeUpcallFunctionalInterface) + "(functionalInterfaceObj) {");
        indent++;
        printLine("return function" + argNames + " {");
        indent++;
        for (int i = 0; i < m.type().parameterTypes().size(); i++) {
            upcallConvert(m.type().parameterTypes().get(i), "arg" + i);
        }
        printLine("return functionalInterfaceObj." + methodToFunctionName(m) + argNames + ";");
        indent--;
        printLine("}");
        indent--;
        printLine("}");
    }

    private void upcallConvert(Type type, String var) {
        if (type instanceof PrimitiveType primitiveType) {
            printLine("if (" + var + " == null) throw 'null " + var + "';"); // itt jó lenne a tényleges paraméternevet kiírni
            switch (primitiveType) {
                case I, D -> {
                }
                default -> throw new RuntimeException("unsupported primitive type in native upcall: " +
                        primitiveType.displayName());
            }
            return;
        }

        if (type instanceof Clazz c) {
            if (interopProvider.nativeTypeKind(c) != null || c.knownClass == KnownClass.STRING)
                return;
            if (c.knownClass == KnownClass.OBJECT) {
                throw new RuntimeException("TODO markIncomingUntypedValue");
                //printLine("markIncomingUntypedValue(" + var + ");");
                //return;
            }
        }

        throw new RuntimeException("unsupported type in native upcall: " + type);
    }

    @Override
    public void printPrimitiveTypes(Set<PrimitiveType> usedPrimitiveTypes) {
        if (usedPrimitiveTypes.contains(PrimitiveType.J))
            addRuntimeLib("longlib.js");

        // ennek a semmi köze primitív típusokhoz, csak valahova kellett rakni
        if (Fiber.ENABLE_MULTITHREADING)
            addRuntimeLib("threads.js");

        this.origOut = this.out;
        this.out = typeDefOut = new StringWriter();
    }

    private void addRuntimeLib(String name) {
        try (Reader r = new InputStreamReader(
                Objects.requireNonNull(JSEmitter.class.getResourceAsStream(name)),
                StandardCharsets.UTF_8)) {
            r.transferTo(out);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected String computeFunctionName(Method method) {
        if (method.clazz.knownClass == KnownClass.METHOD_HANDLE && method.name.startsWith("linkTo"))
            return "mhLinkerMethod";

        return super.computeFunctionName(method);
    }

    protected Entry constValue(Object val) {
        return switch (val) {
            case null -> constantPool.new Primitive(true, "null");
            case String s -> constantPool.new Primitive(true, stringLiteral(s));
            case Long l -> constantPool.new Primitive(false,
                    "ll.fromBits(" + ((int) (long) l) + ", " + (l >>> 32) + ")");
            case Number number -> {
                String n1 = val.toString();
                // mínuszjel bekavarhat, pl. -2.toString() nem értelmes, míg (-2).toString() igen
                yield constantPool.new Primitive(true, n1.startsWith("-") ? "(" + n1 + ")" : n1);
            }
            case Boolean b -> constantPool.new Primitive(true, b ? "1" : "0");
            case Character ch -> constantPool.new Primitive(true, Integer.toString(ch));
            case Type t -> constantPool.new Primitive(true, typeName(t));
            case MethodType methodType -> {
                // ennek nem inkább JSReplacementProviderben kéne lennie?
                yield constValue(emissionContext.compContext.interpreter().toMethodType(methodType));
            }
            case Obj obj -> {
                if (obj instanceof ClassObj co) {
                    Clazz clazz = co.type();
                    if (clazz.name.equals("java/lang/invoke/MemberName"))
                        yield constantPool.new Primitive(true, memberNameValue(co));
                    if (EmissionContext.FILTERED_OUT_CLASSNAMES_FOR_HEAP_SERIALIZATION.contains(clazz.name))
                        yield constantPool.new Primitive(true, "'" + clazz.name + "-et nem szerializálunk'");
                    if (clazz.name.equals("java/util/ArrayList"))
                        co = convertArrayList(co, ArrayListImpl.MUTABLE_VAR_SIZE);
                    else if (clazz.name.equals("java/util/Arrays$ArrayList"))
                        co = convertArrayList(co, ArrayListImpl.MUTABLE_FIXED_SIZE);
                    else if (clazz.name.equals("java/util/Collections$EmptyList"))
                        co = convertArrayList(co, ArrayListImpl.EMPTY); // itt valójában hivatkozni kéne a konstansra, nem mindig újat létrehozni
                    else if (ctx.findClass("java/util/ImmutableCollections$AbstractImmutableList").isAssignableFrom(clazz))
                        co = convertArrayList(co, ArrayListImpl.IMMUTABLE);
                    else if (clazz.name.equals(NativeMapData_CLASS_NAME)) {
                        ClassObj co2 = co;
                        yield constantPool.new JSMap((entries) -> {
                            Interpreter.Array keys = (Interpreter.Array) co2.readField(0);
                            Interpreter.Array values = (Interpreter.Array) co2.readField(1);
                            assert keys.length() == values.length();
                            for (int i = 0; i < keys.length(); i++) {
                                entries.accept(
                                        constantPool.constant0(keys.readElement(i)),
                                        constantPool.constant0(values.readElement(i))
                                );
                            }
                        });
                    } else if (clazz.name.equals(StaticsHolder_CLASS_NAME))
                        // Unsafe.get* ezt a StaticsHolder.classt kapja meg baseként ha statikusmemberről van szó
                        yield constantPool.new Primitive(true, "window");
                    ClassObj co2 = co;
                    Clazz clazz2 = co.type();

                    yield constantPool.new Obj(clazz2, ctx.interpreter().identityHashCode(co2), values -> {
                        for (Field f : clazz2.allFieldList) {
                            // Stream volt eredetileg, de túl sok stack frameet foglalt, StackOverflowError lett belőle.
                            // TODO át kéne állnunk egy queuera a constantpool entry-k kiírásakor, nem rekurzívan kiírogatni
                            if ((f.access & ACC_STATIC) == 0
                                    && emissionContext.usedFields.contains(f)
                                    && emissionContext.shouldSerializeField(f)) {
                                Entry c = constantPool.constant0(co2.readField(f));
                                values.accept(fieldName(f), c);
                            }
                        }
                    });
                } else {
                    Array array = (Array) obj;
                    yield constantPool.new Array(array.type(),
                            ctx.interpreter().identityHashCode(array), values -> {
                        for (int i = 0; i < array.length(); i++)
                            values.accept(constantPool.constant0(array.readElement(i)));
                    });
                }
            }
            default -> {
                throw new RuntimeException("unknown constant value type " + val.getClass().getName() + ": " + val.toString());
            }
        };
    }

    @Nonnull
    private static String stringLiteral(String val) {
        return "\"" + StringEscape.escapeJSString(val) + "\"";
    }

    private ClassObj convertArrayList(ClassObj co, int type) {
        Method toArrayMethod = ctx.findMethodOrFail(co.type(), "toArray", new MethodType(List.of(), new ArrayType(ctx.findClass(KnownClass.OBJECT))));
        Object arr = ctx.interpreter().execute(toArrayMethod, co).orElseThrow();
        co = ctx.interpreter().createObject(ctx.findClass(JSReplacementProvider.ArrayListImpl.class));
        co.writeField(0, arr);
        co.writeField(1, type);
        return co;
    }

    @Nonnull
    private String instanceCreation(Type objectType) {
        return "new " + typeName(objectType) + "()";
    }

    private String memberNameValue(ClassObj obj) {
        Interpreter interpreter = ctx.interpreter();
        if (interpreter.memberNameRefKind(obj) > 4) {
            Type returnType = interpreter.memberNameReferredMethod(obj).type().returnType();
            return "memberName(" + memberNameValueImpl(obj) + ", " + constantPool.constant(returnType) + ")";
        } else
            return memberNameValueImpl(obj);
    }

    private String memberNameValueImpl(ClassObj obj) {
        Interpreter interpreter = ctx.interpreter();
        int refKind = interpreter.memberNameRefKind(obj);
        switch (refKind) {
            case H_INVOKESTATIC -> {
                Method m = interpreter.memberNameReferredMethod(obj);

                if (interopProvider.nativeMethodKind(m) != null)
                    throw new UnsupportedOperationException("TODO native interface calls through MemberName");

                return methodToFunctionName(m);
            }
            case H_INVOKEVIRTUAL, H_INVOKEINTERFACE, H_INVOKESPECIAL -> {
                Method m = interpreter.memberNameReferredMethod(obj);

                if (interopProvider.nativeMethodKind(m) != null)
                    throw new UnsupportedOperationException("TODO native interface calls through MemberName");

                StringBuilder sb = new StringBuilder("(function(receiver");
                if (!m.type().parameterTypes().isEmpty())
                    sb.append(", ");
                printArgList(m, sb);
                // Class függvények miatt muszáj devirtualizálnunk
                // TODO vonjuk össze az Optimizer1-ben lévő devirtualizálási döntési logikával
                if (refKind == H_INVOKESPECIAL || (m.access & (ACC_FINAL | ACC_PRIVATE)) != 0 || (m.clazz.access & ACC_FINAL) != 0) {
                    sb.append(") { return ").append(methodToFunctionName(m)).append(".call(receiver");
                    if (!m.type().parameterTypes().isEmpty())
                        sb.append(", ");
                } else
                    sb.append(") { return receiver.").append(methodToFunctionName(m)).append('(');
                printArgList(m, sb);
                sb.append(")})");
                return sb.toString();
            }
            case H_NEWINVOKESPECIAL -> {
                Method m = interpreter.memberNameReferredMethod(obj);
                StringBuilder sb = new StringBuilder("(function(");
                printArgList(m, sb);
                sb.append(") { var obj = new ").append(typeName(m.clazz)).append("(); ");
                sb.append(methodToFunctionName(m)).append('(');
                printArgList(m, sb);
                sb.append("); return obj; })");
                return sb.toString();
            }
            case H_GETFIELD -> {
                Field f = interpreter.memberNameReferredField(obj);
                return "(function(obj) { return obj." + fieldName(f) + "; })";
            }
            case H_GETSTATIC -> {
                Field f = interpreter.memberNameReferredField(obj);
                return "(function() { return " + fieldName(f) + "; })";
            }
            case H_PUTFIELD -> {
                Field f = interpreter.memberNameReferredField(obj);
                return "(function(obj, val) { obj." + fieldName(f) + " = val; })";
            }
            case H_PUTSTATIC -> {
                Field f = interpreter.memberNameReferredField(obj);
                return "(function(val) { " + fieldName(f) + " = val; })";
            }
            default -> throw new UnsupportedOperationException("MN kind: " + refKind);
        }
    }

    private static void printArgList(Method m, StringBuilder sb) {
        for (int i = 0; i < m.type().parameterTypes().size(); i++) {
            if (i != 0)
                sb.append(", ");
            sb.append("arg").append(i);
        }
    }

    @Override
    protected String nativeMethodName(Method m) {
        return interopProvider.nativeMethodName(m);
    }

    public static AnnotationNode findAnnotation(Clazz m, String annotationType) {
        return m.findAnnotation(annotationType);
    }

    static Object findValueInAnnotation(AnnotationNode ann, String name) {
        if (ann.values == null)
            return null;
        for (int i = 0; i < ann.values.size(); i += 2)
            if (ann.values.get(i).equals(name))
                return ann.values.get(i + 1);
        return null;
    }

    private String defaultValue(String type) {
        return switch (type.charAt(0)) {
            case 'L', '[' -> "null";
            case 'J' -> "ll.getZero()";
            default -> "0";
        };
    }

    // ez JSTransformer dolga, csak azért emeltük ki ide, mert JSONativeInteropProvider is használja
    public Node downcallArgConvert(SequenceNode seq, Type t, Node arg) {
        Clazz objClass = ctx.findClass(KnownClass.OBJECT);

        // boolean-t nem kell konvertálni, jó a 0/1 is helyette: https://tc39.es/ecma262/#sec-toboolean
        // kivéve ha Objectté kell átalakítani, pl. console.log
        if (t == PrimitiveType.J || t == objClass) {
            InvokeSpecialOrStatic convertToNative = new InvokeSpecialOrStatic(
                    ctx.method(KnownClass.UNSAFE2, "convertToNative", objClass, t),
                    arg);
            seq.nodes.add(convertToNative);
            arg = convertToNative;
        } else if (t instanceof Clazz clazz && Boolean.TRUE.equals(interopProvider.isFunctor(clazz))) {
            if (!(arg instanceof NativeUpcallStubNode)) {
                // TODO kéne értelmesebb megoldás arra hogy idempotens legyen ez az átalakítás
                //      meg a felső is

                NativeUpcallStubNode upcallStubNode = new NativeUpcallStubNode(clazz, arg);
                seq.nodes.add(upcallStubNode);
                arg = upcallStubNode;
            }
        }
        return arg;
    }

    public void printExportMethod(Method method) {
        String n = methodToFunctionName(method);
        printLine("window[\"" + n + "\"] = " + n + ";");
    }
}
