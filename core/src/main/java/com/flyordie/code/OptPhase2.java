package com.flyordie.code;

import com.flyordie.code.Clazz.Field;
import com.flyordie.code.Clazz.Method;
import com.flyordie.code.CompilationContext.TransformationChain;
import com.flyordie.code.Interpreter.Array;
import com.flyordie.code.Interpreter.ClassObj;
import com.flyordie.code.Interpreter.Obj;
import com.flyordie.code.Location.LocationElement;
import com.flyordie.code.Location.MethodLocationElement;
import com.flyordie.code.Node.*;
import com.flyordie.code.Node.ObjectNode.ObjectIdentity;
import com.flyordie.code.Node.SequenceNode.MethodKey;
import com.flyordie.code.Type.ArrayType;
import com.flyordie.code.Type.PrimitiveType;
import com.flyordie.code.Variable.InstanceField;
import com.flyordie.code.Variable.LocalVar;
import com.flyordie.code.Variable.LocalVar.Kind;
import com.flyordie.code.Variable.StaticField;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.util.*;
import java.util.stream.Collectors;

import static org.objectweb.asm.Opcodes.*;

// TODO String.equalsnek ki kéne tudni értékelődnie konstansokra, pl. System.getProperty miatt.
//      viszont az a probléma, hogy JSReplacementProviderben van egy inlineolható String.equals implementáció,
//      és inlineolódik, mielőtt elér ide. már sokszor felmerült ez a probléma, át kéne
//      gondolni egy általánosabb megoldást. pl. @Snippetnél bejelölni, hogy csak a többi optimalizáció után lépjen
//      életbe.

public class OptPhase2 implements Transformation {

    private static final Object STABLE_ARRAY_MARKER = new Object();

    private final CompilationContext compilationContext;
    private final Method compilingMethod;
    private final TransformationChain transformationChain;
    @Nonnull
    private final MethodCompilationContext mcc;
    private final Deque<Node> stack = new LinkedList<>();
    private final Map<Variable, LocalVarInfo> localVarValues = new HashMap<>();

    // eredetileg ClassValue miatt került be (paraméterben megkapott objektum típusát nem tudta),
    // de gondolom hasznos lesz máshol is
    private final Map<LocalVar, Type> localVarTypes = new HashMap<>();

    private final Variable receiverParam;
    private boolean receiverOverwritten;

    public OptPhase2(MethodCompilationContext mcc) {
        this.compilationContext = mcc.compilationContext;
        this.compilingMethod = mcc.method;
        this.transformationChain = mcc.transformationChain;
        this.mcc = mcc;
        if ((compilingMethod.access & ACC_STATIC) == 0)
            receiverParam = compilingMethod.parameter(0, mcc.method.rootMethodIdentity);
        else
            receiverParam = null;
    }

    @Override
    public void reset() {
        stack.clear();
        localVarValues.clear();
        localVarTypes.clear();
        receiverOverwritten = false;

        int i = 0;
        for (Type t : mcc.method.fullArgTypes()) {
            localVarTypes.put(new LocalVar(mcc.method, i, Kind.ofType(t), mcc.method.rootMethodIdentity), t);
            i += t.slotSize();
        }
    }

    @Override
    public Node enter(Node node) {
        stack.push(node);

        if (node == null)
            return node;

        WrittenVariableAndValue write = node.writtenVariable();
        if (write != null && Objects.equals(receiverParam, write.variable())) {
            // felülírjuk 0-s lokálvart. ilyen Java kódban nem fordulhat elő, csak generált bytecode-ban,
            // pl. jlink generál ilyet SystemModules$defaultba

            // ebben az esetben a this!=null optimalizációt ki kell kapcsolnunk.
            // de ha egy ciklusban vagyunk, akkor nem működne, mert lehet hogy már volt a loopban egy korábbi
            // utasításban ilyen this!=null kifejezés, amit már kiszedtünk.

            if (stack.stream().anyMatch(n -> n instanceof LoopNode))
                throw new RuntimeException("unsupported case: overwriting receiver parameter in a loop: " + write);
            else
                receiverOverwritten = true;
        }

        if (write != null && write.variable().isExact() && !node.root) {
            localVarValues.put(write.variable(), new LocalVarInfo(write.value(), node.parent()));
            if (write.variable() instanceof LocalVar localVar)
                localVarTypes.put(localVar, write.value().type());
        }
        if (node instanceof SwitchCase || node instanceof LoopNode) {
            Node container = switch (node) {
                case SwitchCase switchCase -> (Switch) ((SequenceNode) node.parent()).parent();
                case LoopNode loop -> loop;
                default -> throw new RuntimeException("should not reach here");
            };

            container.walk(n -> { // elég lenne a case előttig menni
                WrittenVariableAndValue writtenVariableAndValue = n.writtenVariable();
                if (writtenVariableAndValue != null) {
                    localVarValues.remove(writtenVariableAndValue.variable());
                }

                // ha olvas egy utasítás vart és nem tudjuk az értékét, a values mapbe berakjuk az olvasó utasítást
                // valuenak, ezért ezeket is ki kell törölni, mert lehet hogy másik case-ben volt az olvasás volt,
                // ezért ebben a case-ben nem elérhető az értéke.
                // ciklusok esetén erre valszeg nincs szükség, de ott meg mindegy.
                Variable readenVariable = n.readenVariable();
                if (readenVariable != null) {
                    LocalVarInfo varVal = localVarValues.get(readenVariable);
                    if (varVal != null && varVal.value instanceof ReadLocalVar readInstr && readInstr.hasAncestor(container)) {
                        localVarValues.remove(readenVariable);
                    }
                }
            });
            // lehet hogy ezt is elég lenne csak az írottakba, nem tudom
            localVarTypes.clear();
        }
        if (node instanceof ReadLocalVar readLocalVar && readLocalVar.localVar instanceof LocalVar localVar &&
                localVarTypes.containsKey(localVar))
            readLocalVar.type = localVarTypes.get(localVar);

        Node n2 = node;
        Set<ObjectIdentity> inputObjects = node.inputs().stream().
                filter(n -> n instanceof ObjectNode).map(b -> ((ObjectNode) b).objectIdentity).
                filter(oi -> !(n2.readenVariable() instanceof InstanceField f && f.object() == oi)
                        && !(n2.writtenVariable() != null && n2.writtenVariable().variable() instanceof InstanceField f2 && f2.object() == oi)).
                collect(Collectors.toSet());

        localVarValues.keySet().removeIf(var -> {
            if (var instanceof InstanceField f && inputObjects.contains(f.object()))
                return true;
            if (var instanceof StaticField && (n2 instanceof InvokeSpecialOrStatic || n2 instanceof InvokeVirtualOrInterface))
                return true;
            return false;
        });

        Variable readenVar = node.readenVariable();
        if (readenVar != null && readenVar.isExact() && !node.root) {
            LocalVarInfo v = localVarValues.get(readenVar);
            // mivel exception kezelés egyelőre nincs, így exception cause változó nincs beállítva, de kerülhet rá hivatkozás finallyban
            // break/continue miatt is eltűnhetnek
            // assert v != null || compilingMethod.isParameter(var)
            //         : var + " has no value (method: " + compilingMethod + ")";

            //if (compilingMethod.name.equals("main"))
            //    System.out.println("ASDF");
            if (v != null) {
                for (Node ancestor : stack) {
                    if (ancestor instanceof LoopNode loop && loop.bodySlot.get().anyMatchesInSubtree(n -> {
                        WrittenVariableAndValue write2 = n.writtenVariable();
                        return write2 != null && write2.variable().equals(readenVar);
                    }))
                        break;
                    if (ancestor == v.availableIn) {
                        //  System.out.println(compilingMethod + ": VAR READ " + readLocalVar + " REPLACED BY " + v.value + "; " + readLocalVar.usages());
                        Node.replaceInInputsOfUsages(node, v.value);
                        return new NopNode("eliminated read variable " + readenVar);
                    }
                }
            }
            localVarValues.put(readenVar, new LocalVarInfo(node, node.parent()));
        }
        if (node instanceof IfElseNode ifElseNode) {
            if (ifElseNode.conditionSlot.get() instanceof ConstantNode constantCondition) {
                return switch ((int) constantCondition.value) {
                    case 0 -> {
                        Node selectedBranch = ifElseNode.failureBranchSlot.get();
                        ifElseNode.failureBranchSlot.set(null);
                        yield Objects.requireNonNullElseGet(selectedBranch,
                                () -> new NopNode("empty IfElseNode false case"));
                    }
                    case 1 -> {
                        Node selectedBranch = ifElseNode.successBranchSlot.get();
                        ifElseNode.successBranchSlot.set(null);
                        yield Objects.requireNonNullElseGet(selectedBranch,
                                () -> new NopNode("empty IfElseNode true case"));
                    }
                    default -> throw new RuntimeException("invalid condition value in: " + constantCondition);
                };
            }
            if (ifElseNode.successBranchSlot.get() == null) {
                Node n = ifElseNode.failureBranchSlot.get();
                ifElseNode.failureBranchSlot.set(null); // különben hibát kapnánk lent
                Node negNode = UnaryNumericOpNode.negate(ifElseNode.conditionSlot.get());
                node = new SequenceNode(
                        new MethodKey("if-else swap cases"),
                        negNode,
                        new IfElseNode(negNode, n, null)
                );
            }
        }
        if (node instanceof PhiNode phi) {
            for (int i = 0; i < phi.conditionList.size(); i++) {
                Node condition = phi.conditionList.get(i);
                if (condition instanceof ConstantNode constantNode) {
                    switch ((int) constantNode.value) {
                        case 0 -> {
                            Node val = phi.valueList.get(i);
                            Node.replaceInInputsOfUsages(phi, val);
                            return new NopNode("eliminated PhiNode");
                        }
                        case 1 -> {
                            phi.valueList.remove(i);
                            phi.conditionList.remove(i);
                            i--;
                        }
                        default -> throw new RuntimeException("invalid condition value in: " + phi);
                    }
                }
            }
        }
        if (node instanceof Switch s && s.input.get() instanceof ConstantNode input) {
            int inputVal = (Integer) input.value;
            SequenceNode seq = (SequenceNode) s.child.get();
            boolean isDefault = seq.nodes.stream().noneMatch(n -> n instanceof SwitchCase c &&
                    !c.isDefault() && c.values().contains(inputVal));
            boolean active = false;
            for (Iterator<Node> it = seq.nodes.iterator(); it.hasNext(); ) {
                Node n = it.next();
                if (n instanceof SwitchCase cn) {
                    active = isDefault ? cn.isDefault() : !cn.isDefault() && cn.values().contains(inputVal);
                    it.remove();
                } else {
                    if (n instanceof BreakSwitch bs && bs.switchBreakKey == s.switchBreakKey) {
                        if (active)
                            seq.resultSlot.set(bs.resultSlot.get());
                        active = false;
                    }
                    if (!active)
                        it.remove();
                }
            }

            if (seq.nodes.isEmpty())
                return new NopNode("eliminated switch because constant value (" + inputVal + ")");

            boolean[] hasBreak = {false};
            seq.walk(n3 -> hasBreak[0] |= n3 instanceof BreakSwitch bs && bs.switchBreakKey == s.switchBreakKey);
            if (hasBreak[0]) {
                // TODO
                seq.nodes.add(0, isDefault ? SwitchCase.ofDefault() : SwitchCase.ofValue(inputVal));
            } else {
                s.child.set(new ErrorNode("SwitchNode that is ought to be eliminated"));
                return seq;
            }
        }
        return node;
    }

    @Override
    public Node exit(Node node) {
        stack.pop();

        // TODO usagesben benne kéne lennie hogy ha if-else-ben van és számít az értéke, de egyelőre nincs benne
        if (node instanceof ConstantNode && node.usages().isEmpty() && !(node.parent() instanceof IfElseNode))
            return new NopNode("eliminated constant node");
        if (node instanceof BinaryNumericOpNode binOp
                && binOp.input1Slot.get() instanceof ConstantNode a && binOp.input2Slot.get() instanceof ConstantNode b) {
            Object ao = a.value;
            Object bo = b.value;
            if (ao instanceof MethodType || bo instanceof MethodType) {
                MethodType m1 = ao instanceof Obj o
                        ? (MethodType) compilationContext.interpreter().toConstant(o)
                        : (MethodType) ao;
                MethodType m2 = ao instanceof Obj o
                        ? (MethodType) compilationContext.interpreter().toConstant(o)
                        : (MethodType) bo;
                return switch (binOp.op.code) {
                    case IF_ACMPEQ -> new ConstantNode(Objects.equals(m1, m2) ? 1 : 0);
                    case IF_ACMPNE -> new ConstantNode(Objects.equals(m1, m2) ? 0 : 1);
                    default -> throw new UnsupportedOperationException();
                };
            }

            Object result;
            try {
                result = binOp.op.method.invokeExact(ao, bo);
            } catch (RuntimeException | Error e) {
                throw e;
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
            //System.out.println("OPT BINOP "+ao+", "+bo+", "+result);
            return new ConstantNode(result);
        }
        if (node instanceof UnaryNumericOpNode uOp && uOp.inputSlot.get() instanceof ConstantNode a) {
            Object result;
            try {
                result = uOp.op.method.invokeExact(Interpreter.convertShortNumericsToInt(a.value));
            } catch (RuntimeException | Error e) {
                throw e;
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
            return new ConstantNode(result);
        }
        Node l = optimizeLogicOps(node);
        if (l != null) {
            Node.replaceInInputsOfUsages(node, l);
            return new NopNode("eliminated " + node.getClass().getSimpleName());
        }
        if (!receiverOverwritten && node instanceof UnaryNumericOpNode op && op.op.code == IFNONNULL
                && op.inputSlot.get() instanceof ReadLocalVar readLocalVar
                && Objects.equals(readLocalVar.localVar, receiverParam))
            return new ConstantNode(true);
        if (node instanceof InvokeVirtualOrInterface invoke) {
            if (invoke.args.get(0) instanceof ConstantNode constantNode) {
                if (constantNode.value == null) {
                    //    throw new RuntimeException("constant null receiver in "+invoke.location+": " + invoke);
                    // lehet hogy ifben vagyunk, ezért engedjük, csak lecseréljük NPE dobásra
                    ObjectNode npe;
                    Clazz npeClass = compilationContext.findClass(KnownClass.NULL_POINTER_EXCEPTION);
                    Clazz stringClass = compilationContext.findClass(KnownClass.STRING);
                    return new SequenceNode(
                            new MethodKey("virtual call on constant null"),
                            npe = new ObjectNode(new ObjectIdentity(npeClass)),
                            new InvokeSpecialOrStatic(
                                    compilationContext.findMethodOrFail(npeClass, "<init>", new MethodType(List.of(stringClass), PrimitiveType.V)),
                                    new Node[]{npe, new ConstantNode("Can't invoke " + invoke.method + " on constant null receiver")}
                            ),
                            new ThrowNode(npe)
                    );
                }
            }
            // a replacedMethod kikeresés lehet hogy nem is kell ide
            Method m = transformationChain.replacementProvider.replacedMethod(invoke.method);
            if (m.clazz.name.equals("jdk/internal/misc/Unsafe")) {
                Node n = handleUnsafeCall(node, invoke);

                if (n != null)
                    if (invoke.args.get(0) instanceof ConstantNode c && c.value != null)
                        node = n;
                    else
                        node = new SequenceNode(
                                new MethodKey("Unsafe call null-check"),
                                new InvokeSpecialOrStatic(
                                        compilationContext.findMethodOrFail(
                                                compilationContext.findClass("java/util/Objects"),
                                                "requireNonNull",
                                                MethodType.parse("(Ljava/lang/Object;)Ljava/lang/Object;", compilationContext)),
                                        invoke.args.get(0)),
                                n
                        );
            }
        }
        if (node instanceof InvokeSpecialOrStatic invokeNode) {
            //if (Optimizer1.shouldEvaluateCompileTime(invokeNode.method))
            //    return evaluateCompileTime(invokeNode);

            if (invokeNode.method.clazz.name.equals("jdk/internal/misc/Unsafe")) {
                // Optimizer1 már megcsinálni a nullchecket
                Node n = handleUnsafeCall(node, invokeNode);
                if (n != null)
                    return n;
            }

            if (invokeNode.method.clazz.name.equals("jdk/internal/reflect/Reflection") && invokeNode.method.name.equals("getCallerClass"))
                if (!compilingMethod.hasVisibleAnnotation(OptPhase1.CALLER_SENSISITVE_ANNOTATION_DESC))
                    return new ConstantNode(compilationContext.interpreter().fromType(compilingMethod.clazz));

            // nem statikus, de OptPhase1 átírja azzá, mivel StackWalker final osztály
            if (invokeNode.method.clazz.knownClass == KnownClass.StackWalker && invokeNode.method.name.equals("getCallerClass")) {
                List<LocationElement> elements = invokeNode.location.elements();
                if (elements.size() == 1) {
                    mcc.markAsNonEmittable(invokeNode, "caller class not available");
                    return invokeNode;
                }
                Method m = ((MethodLocationElement) invokeNode.location.elements().get(1)).method();
                return new ConstantNode(m.clazz);
            }

            if (invokeNode.method.clazz.name.equals("com/flyordie/code/runtime/Unsafe2")) {
                if (invokeNode.method.name.startsWith("reinterpretAs")) {
                    return new ReinterpretCastNode(invokeNode.args.get(0), invokeNode.method.type().returnType());
                }
                switch (invokeNode.method.name) {
                    case "fieldID" -> {
                        Clazz c1 = (Clazz) ((ConstantNode) invokeNode.args.get(0)).value;
                        String f2 = (String) ((ConstantNode) invokeNode.args.get(1)).value;
                        Object to = ((ConstantNode) invokeNode.args.get(2)).value;
                        Type t = to instanceof Type t2 ? t2 : compilationContext.interpreter().fromClass((ClassObj) to);
                        c1 = transformationChain.replacementProvider.classOrReplaced(c1);
                        Field f = compilationContext.field(c1, f2, t);
                        compilationContext.interpreter().reflectivelyUsedMembers.add(f);
                        return new ConstantNode(f.globalNumber);
                    }
                    case "getInstanceField" -> {
                        // ezt azért muszáj, mert ha megmaradna eredeti formában, akkor nem
                        // kerülne be a kérdéses mező a usedFieldsbe.
                        if (invokeNode.args.get(1) instanceof ConstantNode cn)
                            return new GetFieldNode(invokeNode.args.get(0),
                                    (Field) compilationContext.memberFromGlobalID((int) cn.value));
                    }
                    case "putInstanceField" -> {
                        if (invokeNode.args.get(1) instanceof ConstantNode cn)
                            return new PutFieldNode(invokeNode.args.get(0),
                                    (Field) compilationContext.memberFromGlobalID((int) cn.value),
                                    invokeNode.args.get(2));
                    }
                }
            }
            if (invokeNode.method.isPolySigMethodSpecialization
                    && invokeNode.args.get(invokeNode.args.size() - 1) instanceof ConstantNode constMN) {
                if (invokeNode.method.name.equals("linkToStatic") || invokeNode.method.name.equals("linkToSpecial")) {
                    Method method = compilationContext.interpreter().memberNameReferredMethod((ClassObj) constMN.value);
                    Node[] args = Arrays.copyOf(invokeNode.args.toArray(Node[]::new), invokeNode.args.size() - 1);
                    return new InvokeSpecialOrStatic(method, args);
                }
                if (invokeNode.method.name.equals("linkToVirtual") || invokeNode.method.name.equals("linkToInterface")) {
                    Method method = compilationContext.interpreter().memberNameReferredMethod((ClassObj) constMN.value);
                    Node[] args = Arrays.copyOf(invokeNode.args.toArray(Node[]::new), invokeNode.args.size() - 1);
                    return new InvokeVirtualOrInterface(method, args);
                }
            }

            // Optimizer1 cseréli le InvokeVirtualOrInterface-t InvokeSpecialOrStaticra, ha final az osztály/metódus
            if (invokeNode.method.clazz.knownClass == KnownClass.CLASS && invokeNode.method.name.equals("isArray")) {
                Node classObj = invokeNode.args.get(0);
                if (classObj instanceof ConstantNode cn) {
                    Type t = cn.value instanceof Type t2 ? t2 : compilationContext.interpreter().fromClass((ClassObj) cn.value);
                    return new ConstantNode(t instanceof ArrayType);
                } else if (classObj instanceof TypeOfNode typeOfNode) {
                    Type t = typeOfNode.inputSlot.get().type();
                    if (t instanceof Clazz c && transformationChain.replacementProvider.classOrReplaced(c).knownClass != KnownClass.OBJECT)
                        return new ConstantNode(false);
                    if (t instanceof ArrayType)
                        return new ConstantNode(true);
                }
            }

            if (invokeNode.method.clazz.knownClass == KnownClass.Proxy && invokeNode.method.name.equals(
                    "newProxyInstance") && (invokeNode.method.access & ACC_PUBLIC) == ACC_PUBLIC) {
                // van privát függvény is ott newProxyInstance néven
                AllocateArray aa = (AllocateArray) invokeNode.args.get(1);
                if (!((ConstantNode) aa.lengthList.get(0)).value.equals(1))
                    throw new UnsupportedOperationException();
                Node interfaceAStore = ((SequenceNode) aa.parent()).nodes.stream().
                        filter(n -> n instanceof ArrayStore as && as.arraySlot.get() == aa).
                        reduce((a, b) -> b).get();
                Node interfaceTypeNode = ((ArrayStore) interfaceAStore).valueSlot.get();
                if (!(interfaceTypeNode instanceof ConstantNode interfaceTypeConst)) {
                    mcc.markAsNonEmittable(invokeNode, "Proxy.newProxyInstance interface type not known");
                    return invokeNode;
                }

                Clazz interfaceC = (Clazz) interfaceTypeConst.value;
                Clazz Constructor = compilationContext.findClass(Constructor.class);
                final ArrayType ObjectArray = new ArrayType(compilationContext.findClass(KnownClass.OBJECT));
                final ArrayType ClassArray = new ArrayType(compilationContext.findClass(KnownClass.CLASS));
                Method getProxyConstructor =
                        compilationContext.findMethodOrFail(compilationContext.findClass(KnownClass.Proxy),
                                "getProxyConstructor", new MethodType(List.of(compilationContext.findClass(KnownClass.CLASS),
                                        compilationContext.findClass(ClassLoader.class),
                                        ClassArray),
                                        Constructor));
                ClassObj callerClass =
                        compilationContext.interpreter().fromType(((MethodLocationElement) invokeNode.location.elements().get(0)).method().clazz);
                ClassObj callerClassLoader = (ClassObj) compilationContext.interpreter().execute(
                        compilationContext.findMethodOrFail(compilationContext.findClass(KnownClass.CLASS),
                                "getClassLoader",
                                new MethodType(List.of(), compilationContext.findClass(KnownClass.ClassLoader))),
                        callerClass).orElseThrow();
                Array array = compilationContext.interpreter().createArray(ClassArray,
                        new Object[]{compilationContext.interpreter().fromType(interfaceC)});

                ClassObj constructor = (ClassObj) compilationContext.interpreter().execute(getProxyConstructor,
                        callerClass, callerClassLoader, array).orElseThrow();

                compilationContext.interpreter().execute(compilationContext.method(KnownClass.Constructor,
                        "acquireConstructorAccessor", compilationContext.findClass("jdk/internal/reflect" +
                                "/ConstructorAccessor")), constructor);

                AllocateArray allocateArgsArray = new AllocateArray(ObjectArray, new ConstantNode(1));

                return new SequenceNode(
                        new MethodKey("reflection proxy instantiation"),
                        allocateArgsArray,
                        new ArrayStore(allocateArgsArray, new ConstantNode(0), invokeNode.args.get(2),
                                compilationContext.findClass(InvocationHandler.class)),
                        new InvokeVirtualOrInterface(
                                compilationContext.method(KnownClass.Constructor,
                                        "newInstance", compilationContext.findClass(KnownClass.OBJECT), ObjectArray),
                                new ConstantNode(constructor), allocateArgsArray
                        )
                );
            }
        }
        if (node instanceof GetFieldNode getfield) {
            if (getfield.field.name.equals("cases") && !mcc.method.name.equals("tableSwitch"))
                System.out.println();
            if (getfield.objectSlot.get() instanceof ConstantNode constantNode &&
                    constantNode.value instanceof ClassObj co /* mi más lehet rajta kívül? */) {
                if (getfield.field.clazz.isAssignableFrom(co.type())) {
                    // feltesszük hogy nem változik a mező értéke

                    // trükközés unresolved addressek miatt (ld. komment ForeignLinkerImpl.SystemSymbolLookupban),
                    // azaz ne constantfoldozza a hülyeséget ("fake address"-t).
                    // ha majd megcsináljuk Java 21-en MemorySegment subclassal, akkor szedjük ki ezt az ifet.
                    if (!(getfield.field.clazz.knownClass == KnownClass.NativeMemorySegmentImpl && getfield.field.name.equals("min"))) {
                        Object value = co.readField(getfield.field);
                        if (value instanceof Array array && getfield.field.annotatedWithStable) {
                            // arrayeknél még csak a @Stable jelzésére használjuk representedData-t
                            // amit át kéne nevezni info()-ra vagy akármire
                            if (array.representedData() != STABLE_ARRAY_MARKER) {
                                assert array.representedData() == null;
                                array.representedData(STABLE_ARRAY_MARKER);
                            }
                        }
                        return new ConstantNode(value);
                    }
                } else {
                    // ez az ág már elavult, TL már nem így működik

                    // pl. java/lang/ThreadLocal$SuppliedThreadLocal,
                    // com/flyordie/code/js/JSReplacementProvider$ThreadLocalImpl.id, const Obj5bb173e8 (ThreadLocal$SuppliedThreadLocal)
                    Clazz c = co.type();
                    boolean hasReplacement = false;
                    while (c != null && !hasReplacement) {
                        if (transformationChain.replacementProvider.replacementClass(c) != null)
                            hasReplacement = true;
                        else
                            c = c.superclass;
                    }
                    assert hasReplacement : co.type() + ", " + getfield.field + ", " + getfield.objectSlot.get();
                }
            }
        }
        if (node instanceof ArrayLoad arrayLoad) {
            if (arrayLoad.arraySlot.get() instanceof ConstantNode constantNode
                    && constantNode.value instanceof Array array
                    && arrayLoad.indexSlot.get() instanceof ConstantNode indexCN) {
                if (array.representedData() == STABLE_ARRAY_MARKER && false ||
                        arrayLoad.elementType.type().getDescriptor().equals("Ljava/lang/invoke/MethodType;") /* TODO */) {
                    return new ConstantNode(array.readElement((int) indexCN.value));
                }
            }
        }
        if (node instanceof CheckCast checkcast) {
            //Node.replaceInInputsOfUsages(checkcast, checkcast.valueSlot.get());
            if (checkcast.valueSlot.get() instanceof InvokeSpecialOrStatic invokeNode &&
                    invokeNode.method.name.equals("reinterpretCast") &&
                    invokeNode.method.clazz.name.equals("com/flyordie/code/runtime/Unsafe2")) {
                Node.replaceInInputsOfUsages(checkcast, invokeNode.args.get(0));
                invokeNode.parent().children().set(invokeNode.parent().children().indexOf(invokeNode),
                        new NopNode("reinterpret cast to " + checkcast.type.displayName() + " (1)"));
                return new NopNode("reinterpret cast to " + checkcast.type.displayName() + " (2)");
            }
            if (checkcast.valueSlot.get() instanceof ConstantNode constantNode) {
                // látszólag értelmetlen új ConstantNode-okat létrehozni, de valójában already attached hiba lesz, ha nem teszem
                if (constantNode.value == null)
                    return new ConstantNode(constantNode.value);
                if (constantNode.value instanceof String && checkcast.type instanceof Clazz c && c.knownClass == KnownClass.STRING)
                    return new ConstantNode(constantNode.value);
                if (constantNode.value instanceof MethodType && checkcast.type instanceof Clazz c && c.knownClass == KnownClass.METHOD_TYPE)
                    return new ConstantNode(constantNode.value);
                if (constantNode.value instanceof Obj && checkcast.type.isAssignableFrom(((Obj) constantNode.value).type()))
                    return new ConstantNode(constantNode.value);
            }
            if (checkcast.valueSlot.get() instanceof ObjectNode objectNode) {
                if (checkcast.type.isAssignableFrom(objectNode.objectIdentity.type)) {
                    checkcast.valueSlot.set(null);
                    Node.replaceInInputsOfUsages(checkcast, objectNode);
                    return null;
                } else {
                    Clazz cceClass = compilationContext.findClass(KnownClass.ClassCastException);
                    Clazz stringClass = compilationContext.findClass(KnownClass.STRING);
                    ObjectNode cce;
                    return new SequenceNode(
                            new MethodKey("always failing cast"),
                            cce = new ObjectNode(new ObjectIdentity(cceClass)),
                            new InvokeSpecialOrStatic(
                                    compilationContext.findMethodOrFail(cceClass, "<init>", new MethodType(List.of(stringClass), PrimitiveType.V)),
                                    new Node[]{cce, new ConstantNode(objectNode.objectIdentity.type + " cannot be cast to " + checkcast.type)}
                            ),
                            new ThrowNode(cce)
                    );
                }
            }

            // methodhandlek folyton kasztolgatják a thist
            if (!receiverOverwritten && !compilingMethod.isStatic() &&
                    checkcast.valueSlot.get() instanceof ReadLocalVar readLocalVar &&
                    readLocalVar.localVar.equals(compilingMethod.parameter(0)) &&
                    checkcast.type.equals(compilingMethod.clazz)) {
                checkcast.valueSlot.set(null);
                Node.replaceInInputsOfUsages(checkcast, readLocalVar);
                return null;
            }
        }
        if (node instanceof SequenceNode seq) {
            if (seq.nodes.isEmpty())
                return Objects.requireNonNullElseGet(seq.resultSlot.get(),
                        () -> new NopNode("eliminated empty block"));

            int i = 0;
            for (Node n : seq.nodes) {
                i++;
                if (n.alwaysBreaks() && !(seq.hasParent() && seq.parent() instanceof Switch))
                    // ha van középen egy return és utána még szemét, akkor utóbbit levágjuk
                    // pl. constant condition esetén lehet ilyen hogy mindenképpen returnöl.
                    // Ezt ki kéne egészíteni azzal, hogy ha egy ciklus törzs nem tud visszatérni rendesen (continue),
                    // akkor a ciklus utáni részt töröljük ki. Most így fordítási hiba keletkezik (csak C-ben, JS-ben nem),
                    // ha a ciklus után használnánk egy olyan lokális változót, amit a ciklus beljesében lévő
                    // throw (pl. UnsatisfiedLinkError) után állítanánk be.
                    break;

                if (n instanceof SequenceNode subseq) {
                    if (i < seq.nodes.size() && seq.nodes.get(i) instanceof ReturnNode outerRet
                            && outerRet.returnValueSlot.get() == subseq) {
                        subseq.walk(n2 -> {
                            if (n2 instanceof ReturnNode innerRet && innerRet.returnFrom == subseq.methodKey)
                                innerRet.returnFrom = outerRet.returnFrom;
                        });
                    } else if (i == seq.nodes.size() && seq.resultSlot.get() == subseq) {
                        subseq.walk(n2 -> {
                            if (n2 instanceof ReturnNode innerRet && innerRet.returnFrom == subseq.methodKey)
                                innerRet.returnFrom = seq.methodKey;
                        });
                    }

                    if (sequenceNodeInlineable(subseq)) {
                        // if (subseq.nodes.isEmpty())
                        //     continue;

                        Node lastNode = subseq.nodes.get(subseq.nodes.size() - 1);
                        Node retVal = lastNode instanceof ReturnNode ret && ret.returnFrom == subseq.methodKey ?
                                ret.returnValueSlot.get() : subseq.resultSlot.get();
                        /*
                        if (retVal == null && subseq.hasAttachedUsage()) {
                            // Van legalább egy olyan ág, ahol nem tér vissza semmivel.
                            // Mivel olyan nem lehetséges, hogy egyik ágon visszatér, másikon meg nem,
                            // ezért feltételezzük, hogy egyiksen se tér vissza.
                            // Pl. ha egy inline-olt függvény egy exception dobásból áll, akkor ez fordul elő.
                            // Ezt azért jó lenne assertelni.


                            if (!(lastNode instanceof ThrowNode))
                                throw new RuntimeException(subseq.toString());

                        //    break; // levágjuk, ami utána jön

                            // az a baj, hogy rengeteg fals olyan eset van, amikor olyan seq-ra hivatkozunk
                            // inputként, ami valójában nem ad vissza eredményt.
                            // ezért a usages().isEmpty() semmitmondó.
                        } else

                         */

                        if (retVal == null) {
                            if (subseq.hasAttachedUsage()) // hogy könnyebb legyen breakpontot rakni ErrorNode konstruktorra
                                Node.replaceInInputsOfUsages(subseq, new ErrorNode("inlined sequence " +
                                        "has at least one branch which doesn't return a value but a return value is expected"));
                        } else
                            Node.replaceInInputsOfUsages(subseq, retVal);

                        subseq.walk(n2 -> {
                            if (n2 instanceof ReturnNode ret && ret.returnFrom == subseq.methodKey) {
                                ret.returnFrom = seq.methodKey;
                            }
                        });
                    }
                }
            }
            if (i != seq.nodes.size())
                seq.resultSlot.set(null);

            // seq.walk2(n->{
            //     assert !(n instanceof CheckCast cc) || cc.isAttached();
            // });

            List<Node> nodes = new ArrayList<>(seq.nodes.subList(0, i));

            seq.nodes.clear();
            for (Node n : nodes) {
                if (n instanceof SequenceNode subseq && sequenceNodeInlineable(subseq)) {
                    Node lastNode = subseq.nodes.get(subseq.nodes.size() - 1);
                    List<Node> subseqNodes = new ArrayList<>(subseq.nodes);
                    subseq.nodes.clear();
                    if (lastNode instanceof ReturnNode ret && ret.returnFrom == subseq.methodKey)
                        // ha nem szednénk ki, returnölni akarna olyan MethodKeyre, amihez tartozó SequenceNode már nem is létezik a fában
                        subseqNodes.remove(subseqNodes.size() - 1);
                    seq.nodes.addAll(subseqNodes);
                } else if (n instanceof ReturnNode ret && n == nodes.get(nodes.size() - 1) && ret.returnFrom == seq.methodKey)
                    seq.resultSlot.set(ret.returnValueSlot.get());
                else if (!(n instanceof NopNode)) // Emitterben lévő változóinlineolós logikának bekavarhatnak
                    seq.nodes.add(n);
            }

            // egyelemű SequenceNode-ok eliminálását egyelőre kikapcsoltam (ld. false az else ág feltételében),
            // mert problémát okoz, hogy elveszik a methodKey (nem tud hova visszatérni a return utasítás,
            // felpropagálódik a stack aljára).

            if (seq.nodes.isEmpty()) {
                if (seq.resultSlot.get() == null)
                    node = new NopNode("eliminated empty SequenceNode without result");
                else {
                    Node.replaceInInputsOfUsages(seq, seq.resultSlot.get());
                    node = new NopNode("eliminated empty SequenceNode with result");
                }
            } else if (false && seq.nodes.size() == 1 && (
                    seq.resultSlot.get() == null || seq.resultSlot.get() == seq.nodes.get(0)
                            || seq.resultSlot.get() instanceof NopNode)) {
                node = seq.nodes.get(0);
                seq.nodes.clear();
            }

            if (!seq.nodes.isEmpty() && seq.nodes.get(seq.nodes.size() - 1).alwaysBreaks())
                seq.resultSlot.set(null);

//            seq.walk2(n->{
//                assert !(n instanceof CheckCast cc) || cc.isAttached();
//            });
        }
        if (node instanceof TypeOfNode typeOf && typeOf.inputSlot.get() instanceof ConstantNode cn) {
            Obj value = (Obj) compilationContext.interpreter().constant(cn.value,
                    ((MethodLocationElement) cn.location.elements().get(0)).method().clazz);
            if (value == null) {
                // ennek nem kéne előfordulni, de Class::getEnumConstantsShared-ből
                // valahogy mégis ide lyukadunk ki, ezért lecseréltem a
                //
                Clazz npeClass = compilationContext.findClass(KnownClass.NULL_POINTER_EXCEPTION);
                Clazz stringClass = compilationContext.findClass(KnownClass.STRING);
                ObjectNode npe;
                node = new SequenceNode(
                        new MethodKey("getClass() no constant null"),
                        npe = new ObjectNode(new ObjectIdentity(npeClass)),
                        new InvokeSpecialOrStatic(
                                compilationContext.findMethodOrFail(npeClass, "<init>", new MethodType(List.of(stringClass), PrimitiveType.V)),
                                new Node[]{npe, new ConstantNode("getClass() called on constant null in " + cn.location)}
                        ),
                        new ThrowNode(npe)
                );
            } else
                node = new ConstantNode(value.type());
        }
        if (node instanceof TypeOfNode typeOf && typeOf.inputSlot.get().type() instanceof Clazz c && (c.access & ACC_FINAL) != 0) {
            return new InlineableSequenceNode(
                    new MethodKey("type of object with constant type, and receiver null check"),
                    new InvokeSpecialOrStatic(compilationContext.method(KnownClass.Objects, "requireNonNull",
                            compilationContext.findClass(KnownClass.OBJECT), compilationContext.findClass(KnownClass.OBJECT)),
                            typeOf.inputSlot.get()),
                    new ConstantNode(c)
            );
        }
        return node;
    }

    private Node optimizeLogicOps(Node node) {
        if (node instanceof LogicalAnd and) {
            // ezeket lehetne csinálni bitwise andra is

            if (and.input1.get() instanceof ConstantNode in1) {
                if ((int) in1.value == 0)
                    return in1;
                else
                    return and.input2.get();
            }
            if (and.input2.get() instanceof ConstantNode in2) {
                if ((int) in2.value == 0)
                    return in2;
                else
                    return and.input1.get();
            }
            if (and.input1.get() instanceof UnaryNumericOpNode uop1
                    && uop1.op.code == IFEQ && uop1.inputSlot.get().equals(and.input2.get())) {
                return new ConstantNode(0);
            }
            if (and.input2.get() instanceof UnaryNumericOpNode uop2
                    && uop2.op.code == IFEQ && uop2.inputSlot.get().equals(and.input1.get())) {
                return new ConstantNode(0);
            }
            // TODO ld. LogicalAnd::and
        } else if (node instanceof LogicalOr or) {
            // TODO itt csináljuk meg a fent lévőeket
            if (or.input1.get() instanceof ConstantNode in1 && in1.value.equals(1) ||
                    or.input2.get() instanceof ConstantNode in2 && in2.value.equals(1))
                return new ConstantNode(1);
            if (or.input1.get() instanceof ConstantNode in1 && in1.value.equals(0))
                return or.input2.get();
            if (or.input2.get() instanceof ConstantNode in2 && in2.value.equals(0))
                return or.input1.get();
        }
        return null;
    }

    public static class InlineableSequenceNode extends SequenceNode {

        public InlineableSequenceNode(MethodKey mk, Node... nodes) {
            super(mk, nodes);
        }
    }

    /*
    private Node evaluateCompileTime(InvokeSpecialOrStatic invokeNode) {
        List<Object> args = new ArrayList<>();
        for (Node n : invokeNode.args)
            args.add(compilationContext.interpreter().execute(n).orElseThrow());
            /*
            if (n instanceof ConstantNode constantNode)
                args.add(constantNode.value);
            else
                throw new RuntimeException("argument #" + (args.size() + 1) + " of " + invokeNode.method + " is not constant: " + n);
             *//*
        Object result = compilationContext.interpreter().execute(invokeNode.method, args.toArray(Object[]::new)).orElseThrow();
        if (result instanceof Obj obj)
            result = compilationContext.interpreter().toConstant(obj);
        return new ConstantNode(result);
    }
    */

    private static boolean sequenceNodeInlineable(SequenceNode subseq) {
        return !subseq.anyMatchesInSubtree(n2 -> n2 instanceof ReturnNode ret
                && ret.returnFrom == subseq.methodKey
                && n2 != subseq.nodes.get(subseq.nodes.size() - 1));
    }

    @Nullable
    private Node handleUnsafeCall(Node node, InvokeNode invoke) {
        switch (invoke.method.name) {
            case "putInt", "putIntVolatile", "putLong", "putLongVolatile", "putReference", "putReferenceVolatile" -> {
                long offset;
                if (invoke.args.get(2) instanceof ConstantNode addressNode)
                    offset = (long) addressNode.value;
                else
                    return null;
                Node objNode = invoke.args.get(1);
                Node valueNode = invoke.args.get(3);

                Field f;
                try {
                    f = compilationContext.fieldFromAddress((Clazz) objNode.type(), Math.toIntExact(offset));
                } catch (IllegalArgumentException e) {
                    // valszeg csak nem ismert a subclass és a superclasson (pl. Object) próbál kiolvasni egy mezőt
                    // TODO
                    return null;
                }
                return putfieldOrPutstatic(objNode, f, valueNode);
            }
            case "getInt", "getIntVolatile", "getLong", "getLongVolatile", "getReference", "getReferenceVolatile" -> {
                long offset;
                if (invoke.args.get(2) instanceof ConstantNode addressNode)
                    offset = (long) addressNode.value;
                else {
                    // System.out.println("UNSAFE NOT: " + compilingMethod.name + ", " + invoke.args);
                    return null;
                }
                Node objNode = invoke.args.get(1);

                Field f;
                try {
                    f = compilationContext.fieldFromAddress((Clazz) objNode.type(), Math.toIntExact(offset));
                } catch (IllegalArgumentException e) {
                    // TODO lásd fenti komment
                    return null;
                }
                return getfieldOrGetstatic(objNode, f);
            }
            case "compareAndExchangeByte" -> {
                long offset;
                if (invoke.args.get(2) instanceof ConstantNode addressNode)
                    offset = (long) addressNode.value;
                else
                    return null;
                Node objNode = invoke.args.get(1);
                Node expectedValueNode = invoke.args.get(3);
                Node newValueNode = invoke.args.get(4);

                Field f = compilationContext.fieldFromAddress((Clazz) objNode.type(), Math.toIntExact(offset));
                Node getfield = getfieldOrGetstatic(objNode, f);
                BinaryNumericOpNode icmpeq = new BinaryNumericOpNode(getfield, expectedValueNode, NumericOps.ops[IF_ICMPEQ]);
                SequenceNode seq = new SequenceNode(
                        new MethodKey(invoke.method),
                        getfield,
                        icmpeq,
                        new IfElseNode(
                                icmpeq,
                                putfieldOrPutstatic(objNode, f, newValueNode),
                                null
                        )
                );
                seq.resultSlot.set(getfield);
                return seq;
            }
            case "compareAndSetInt" -> {
                long offset;
                if (invoke.args.get(2) instanceof ConstantNode addressNode)
                    offset = (long) addressNode.value;
                else
                    return null;
                Node objNode = invoke.args.get(1);
                Node expectedValueNode = invoke.args.get(3);
                Node newValueNode = invoke.args.get(4);

                Field f = compilationContext.fieldFromAddress((Clazz) objNode.type(), Math.toIntExact(offset));
                Node getfield = getfieldOrGetstatic(objNode, f);
                BinaryNumericOpNode icmpeq = new BinaryNumericOpNode(getfield, expectedValueNode, NumericOps.ops[IF_ICMPEQ]);
                return new SequenceNode(
                        new MethodKey(invoke.method),
                        getfield,
                        icmpeq,
                        new IfElseNode(
                                icmpeq,
                                new SequenceNode(
                                        new MethodKey("compareAndSet success case"),
                                        putfieldOrPutstatic(objNode, f, newValueNode),
                                        new ConstantNode(1)
                                ),
                                new ConstantNode(0)
                        )
                );
            }
            case "compareAndSetLong" -> {
                long offset;
                if (invoke.args.get(2) instanceof ConstantNode addressNode)
                    offset = (long) addressNode.value;
                else
                    return null;
                Node objNode = invoke.args.get(1);
                Node expectedValueNode = invoke.args.get(3);
                Node newValueNode = invoke.args.get(4);

                Field f;
                try {
                    f = compilationContext.fieldFromAddress((Clazz) objNode.type(), Math.toIntExact(offset));
                } catch (IllegalArgumentException e) {
                    // TODO
                    return null;
                }
                Node getfield = getfieldOrGetstatic(objNode, f);
                BinaryNumericOpNode lcmp = new BinaryNumericOpNode(getfield, expectedValueNode, NumericOps.ops[LCMP]);
                UnaryNumericOpNode ifeq = new UnaryNumericOpNode(lcmp, NumericOps.ops[IFEQ]);
                return new SequenceNode(
                        new MethodKey(invoke.method),
                        getfield,
                        lcmp,
                        ifeq,
                        new IfElseNode(
                                ifeq,
                                new SequenceNode(
                                        new MethodKey("compareAndSet success case"),
                                        putfieldOrPutstatic(objNode, f, newValueNode),
                                        new ConstantNode(1)
                                ),
                                new ConstantNode(0)
                        )
                );
            }
            case "compareAndSetReference" -> {
                long offset;
                if (invoke.args.get(2) instanceof ConstantNode addressNode)
                    offset = (long) addressNode.value;
                else
                    return null;
                Node objNode = invoke.args.get(1);
                Node expectedValueNode = invoke.args.get(3);
                Node newValueNode = invoke.args.get(4);

                Field f = compilationContext.fieldFromAddress((Clazz) objNode.type(), Math.toIntExact(offset));
                Node getfield = getfieldOrGetstatic(objNode, f);
                BinaryNumericOpNode acmpeq = new BinaryNumericOpNode(getfield, expectedValueNode, NumericOps.ops[IF_ACMPEQ]);
                return new SequenceNode(
                        new MethodKey(invoke.method),
                        getfield,
                        acmpeq,
                        new IfElseNode(
                                acmpeq,
                                new SequenceNode(
                                        new MethodKey("compareAndSet success case"),
                                        putfieldOrPutstatic(objNode, f, newValueNode),
                                        new ConstantNode(1)
                                ),
                                new ConstantNode(0)
                        )
                );
            }
            case "allocateInstance" -> {
                if (invoke.args.get(1) instanceof ConstantNode constantNode) {
                    Clazz clazz = (Clazz) compilationContext.interpreter().fromClass((ClassObj) constantNode.value);
                    return new ObjectNode(new ObjectIdentity(clazz));
                } else
                    return null;
            }
        }
        return null;
    }

    private static Node putfieldOrPutstatic(Node objNode, Field f, Node newValueNode) {
        return f.isStatic() ? new PutStaticNode(f, newValueNode) :
                new PutFieldNode(objNode, f, newValueNode);
    }

    private static Node getfieldOrGetstatic(Node objNode, Field f) {
        return f.isStatic() ? new GetStaticNode(f) : new GetFieldNode(objNode, f);
    }

    private record LocalVarInfo(Node value, Node availableIn) {
    }
}
