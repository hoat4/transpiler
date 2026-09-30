package com.flyordie.code;

import com.flyordie.code.Clazz.Field;
import com.flyordie.code.Clazz.Method;
import com.flyordie.code.Node.*;
import com.flyordie.code.Node.Switch.SwitchType;
import com.flyordie.code.Type.ArrayType;
import com.flyordie.code.Variable.LocalVar;
import com.flyordie.code.Variable.LocalVar.Kind;

import javax.annotation.Nullable;
import java.lang.invoke.MethodHandle;
import java.util.*;

import static com.flyordie.code.Type.PrimitiveType.V;
import static org.objectweb.asm.Opcodes.ACC_NATIVE;
import static org.objectweb.asm.Opcodes.ACC_VARARGS;

public class OldInterpreter extends Interpreter {

    protected static final CompilationContext.TransformationChain INTERPRETER_TRANSFORMATION_CHAIN = new CompilationContext.TransformationChain(ReplacementProvider.EMPTY,
            List.of(
                    context -> new OptPhase1(context, context.transformationChain, false),
                    OptPhase2::new,
                    OptPhase3::new
            ), List.of());
    private final Deque<MethodCall> callStack = new LinkedList<>();
    private final Set<Clazz> initializedClasses = new HashSet<>();
    private final Set<Clazz> fullyInitializedClasses = new HashSet<>();
    private final Map<Field, Object> staticFieldValues = new HashMap<>();

    public OldInterpreter(CompilationContext compContext) {
        super(compContext);
    }


    public ExecutionResult execute(Method method, Object... args) {
        //System.out.println(counter++);
        assert args.length == method.fullArgTypes().size() : "arg number mismatch: " + Arrays.toString(args) + ", " + method;

        if (method.clazz.knownClass != KnownClass.STRING&&false)
            System.out.println("execute: " + method + " " + Arrays.toString(args));
        if (method.clazz.name.endsWith("/URLClassPath")) {
            System.out.println("execute: " + method + " " + Arrays.toString(args));
            if (method.name.equals("<init>"))
                System.out.println("ucp arg: " + fromStringObj((ClassObj) args[1]));
        }

        Intrinsic intrinsic = intrinsics.get(method);
        if (intrinsic != null)
            return intrinsic.evaluate(args);

        if (method.clazz == symbols.MethodHandle && method.name.startsWith("linkTo"))
            return executeMHLinkerMethod(method, args);
        if ((method.access & ACC_NATIVE) != 0) {
            return throwException(symbols.UnsatisfiedLinkError, method.clazz.name + "." + method.name);
        }

        Node node = compContext.compile(method, INTERPRETER_TRANSFORMATION_CHAIN).root();
        if (node == null)
            throw new RuntimeException("method " + method + " compiled to null");

        MethodCall mc = new MethodCall(method, node);
        callStack.push(mc);


        int varNumber = 0, argIndex = 0;
        for (Object arg : args) {
            Type t = method.fullArgTypes().get(argIndex++);

            mc.localVars.put(new LocalVar(method, varNumber, LocalVar.Kind.ofType(t), method.rootMethodIdentity), arg);

            varNumber += t.slotSize();
        }


        //System.out.println(method.type().name + "." + method.name);
        try {
            final ExecutionResult ret = evaluateChild(node);
            return ret;
        } catch (StackOverflowError e) {
            throw new RuntimeException(e.toString() + "\n" + stackTrace(), e);
        } finally {
            MethodCall n = callStack.pop();
            assert n == mc;
        }
    }

    public ExecutionResult executeWithVarargs(Method method, Object... args) {
        if ((method.access & ACC_VARARGS) == 0)
            return execute(method, args);

        List<Type> argTypes = method.fullArgTypes();
        if (args.length < argTypes.size() - 1)
            throw new IllegalArgumentException("too few arguments for " + method + ": " + Arrays.toString(args));

        Type arrayType = argTypes.get(argTypes.size() - 1);
        Array array = createArray((ArrayType) arrayType, args.length - argTypes.size() + 1);
        for (int i = 0; i < array.length(); i++)
            array.writeElement(i, args[i + argTypes.size() - 1]);
        Object[] newArgs = Arrays.copyOf(args, argTypes.size());
        newArgs[newArgs.length - 1] = array;
        return execute(method, newArgs);
    }

    public ExecutionResult execute(Node node) {
        assert !node.invalid : node;
        callStack.push(new MethodCall(null, node));
        try {
            return evaluateChild(node);
        } finally {
            callStack.pop();
        }
    }

    private MethodCall currentCall() {
        MethodCall c = callStack.peek();
        assert c != null;
        return c;
    }

    private ExecutionResult evaluateChild(Node node) {
        if (node == null)
            return new ExecutionResult(null, null);

        assert node.isAttached() : "not attached: " + node;

        //System.out.println(callStack.peek().method.type()+callStack.peek().method.name+": "+node);
        Map<Node, ExecutionResult> nodeResultCache = currentCall().nodeResultCacheStack.peek();
        if (nodeResultCache.containsKey(node))
            throw new IllegalStateException("already evaluated: " + node);

        ExecutionResult result = evaluateImpl(node);
        nodeResultCache.put(node, result);
        return result;
    }

    @SuppressWarnings("ConstantConditions")
    private ExecutionResult evaluateInput(Node node) {
        if (node == null)
            return new ExecutionResult(null, null);

        assert node.isAttached() : "not attached: " + node;

        Map<Node, ExecutionResult> nodeResultCache = currentCall().nodeResultCacheStack.peek();

        ExecutionResult r = nodeResultCache.get(node);
        if (r == null)
            if (node.sideEffects().isEmpty())
                return evaluateImpl(node);
            else
                throw new RuntimeException("not evaluated" + (node.isAttached() ? "" : " (not attached)")
                        + " in " + callStack.peek().method + ": " + node);
        return r;
    }

    private ExecutionResult evaluateImpl(Node node) {
        if (node instanceof ConstantNode cn)
            return new ExecutionResult(constant(cn.value, null), null);
        else if (node instanceof SequenceNode seq) {
            if (currentCall().root != seq && seq.parent() instanceof Switch switchNode) {
                assert seq.resultSlot.get() == null;
                Object valueObj = currentCall().nodeResultCacheStack.peek().get(switchNode.input.get()).value();
                assert valueObj != null : switchNode.input.get();
                int value = (int) valueObj;

                boolean exec = false;
                for (Node n : seq.nodes) {
                    if (exec) {
                        if (n instanceof SwitchCase)
                            continue;
                        ExecutionResult r = evaluateChild(n);
                        if (r.returnFrom() != null)
                            return r.withoutReturn(seq.methodKey);
                    } else {
                        if (n instanceof SwitchCase switchCase) {
                            if (!switchCase.isDefault() && switchCase.values().contains(value)) {
                                exec = true;
                            }
                        }
                    }
                }

                if (exec)
                    if (switchNode.switchType == SwitchType.SWITCH_EXPRESSION)
                        throw new RuntimeException("should not reach here");
                    else
                        return new ExecutionResult(null, null);

                for (Node n : seq.nodes) {
                    if (exec) {
                        ExecutionResult r = evaluateChild(n);
                        if (r.returnFrom() != null)
                            return r.withoutReturn(seq.methodKey);
                    } else {
                        if (n instanceof SwitchCase switchCase && switchCase.isDefault())
                            exec = true;
                    }
                }

                if (!exec || switchNode.switchType == SwitchType.SWITCH_EXPRESSION)
                    throw new RuntimeException("should not reach here");

                // ha ide eljutunk, akkor nem volt a kiválasztott case helyén break
                // TODO ez a komment még értelmes?
                return new ExecutionResult(null, null);
            } else {
                for (Node n : seq.nodes) {
                    ExecutionResult r = evaluateChild(n);
                    if (r.returnFrom() != null)
                        return r.withoutReturn(seq.methodKey);
                }
                return evaluateInput(seq.resultSlot.get());
            }
        } else if (node instanceof IfElseNode ifElseNode) {
            ExecutionResult r = evaluateInput(ifElseNode.conditionSlot.get());
            if (r.returnFrom() != null)
                return r;
            int i = (int) r.value();
            Node selectedBranch = switch (i) {
                case 0 -> ifElseNode.failureBranchSlot.get();
                case 1 -> ifElseNode.successBranchSlot.get();
                default -> throw new RuntimeException("condition value must be 0 or 1, but it is " + i);
            };
            if (selectedBranch == null)
                return new ExecutionResult(null, null);
            else
                return evaluateChild(selectedBranch);
        } else if (node instanceof PhiNode phiNode) {
            for (int i = 0; i < phiNode.conditionList.size(); i++) {
                ExecutionResult r = evaluateInput(phiNode.conditionList.get(i));
                if (r.returnFrom() != null)
                    return r;
                int j = (int) r.value();
                switch (j) {
                    case 0 -> {
                        return evaluateInput(phiNode.valueList.get(i));
                    }
                    case 1 -> {
                        // continue
                    }
                    default -> throw new RuntimeException("condition value must be 0 or 1, but it is " + j);
                }
            }

            throw new RuntimeException("no value for phi node: " + phiNode);
        } else if (node instanceof LoopNode loop) {
            while (true) {
                // ez a komment szerintem már elavult:
                // át kéne gondolni a feltételes elágazások kezelését, lehet hogy az utolsó ciklusiteráció
                // ExecutionResultjait meg kéne őrizni.

                Map<Node, ExecutionResult> cs = new HashMap<>(currentCall().nodeResultCacheStack.peek());
                currentCall().nodeResultCacheStack.push(cs);
                ExecutionResult r = evaluateChild(loop.bodySlot.get());
                Map<Node, ExecutionResult> cs2 = currentCall().nodeResultCacheStack.pop();
                assert cs == cs2;
                if (r.value() == SpecialExecutionResult.CONTINUE_LOOP && r.returnFrom().equals(loop.loopKey))
                    continue;
                if (r.returnFrom() != null)
                    return r.withoutReturn(loop.loopKey);
            }
        } else if (node instanceof ReturnNode returnNode) {
            ExecutionResult r = evaluateInput(returnNode.returnValueSlot.get());
            if (r.returnFrom() != null)
                return r;
            return new ExecutionResult(r.value(), returnNode.returnFrom);
        } else if (node instanceof ContinueLoopNode continueLoop) {
            return new ExecutionResult(SpecialExecutionResult.CONTINUE_LOOP, continueLoop.loop);
        } else if (node instanceof BreakLoopNode breakLoopNode) {
            return new ExecutionResult(null, breakLoopNode.loop);
        } else if (node instanceof AllocateArray allocateArray) {
            Array root = null;
            Type t = allocateArray.arrayType;
            List<Array> arrays1 = new ArrayList<>();
            List<Array> arrays2 = new ArrayList<>();
            for (Node dimNode : allocateArray.lengthList) {
                assert t instanceof ArrayType;
                ExecutionResult r = evaluateInput(dimNode);
                if (r.returnFrom() != null)
                    return r;
                int dim = (int) r.value();

                if (arrays1.isEmpty())
                    arrays1.add(root = createArray((ArrayType) t, dim));
                else {
                    for (Array parent : arrays1) {
                        for (int i = 0; i < parent.length(); i++) {
                            Array a = createArray((ArrayType) t, dim);
                            parent.writeElement(i, a);
                            arrays2.add(a);
                        }
                    }
                    arrays1.clear();
                    List<Array> tmp = arrays2;
                    arrays2 = arrays1;
                    arrays1 = tmp;
                }

                t = ((ArrayType) t).elementType();
            }
            assert root != null;
            return new ExecutionResult(root, null);
        } else if (node instanceof ArrayStore arrayStore) {
            ExecutionResult r = evaluateInput(arrayStore.arraySlot.get());
            if (r.returnFrom() != null)
                return r;
            ExecutionResult r2 = evaluateInput(arrayStore.indexSlot.get());
            if (r2.returnFrom() != null)
                return r;
            ExecutionResult r3 = evaluateInput(arrayStore.valueSlot.get());
            if (r3.returnFrom() != null)
                return r;
            Array arr = (Array) r.value();
            arr.writeElement((int) r2.value(), convertForStore(r3.value(), arr.type().elementType()));
            return new ExecutionResult(null, null);
        } else if (node instanceof ArrayLoad arrayLoad) {
            ExecutionResult r = evaluateInput(arrayLoad.arraySlot.get());
            if (r.returnFrom() != null)
                return r;
            ExecutionResult r2 = evaluateInput(arrayLoad.indexSlot.get());
            if (r2.returnFrom() != null)
                return r;
            Array arr = (Array) r.value();
            if (arr == null)
                return throwException(symbols.NullPointerException);
            Object loadedValue = arr.readElement((int) r2.value());
            return new ExecutionResult(getOrDefault(convertShortNumericsToInt(loadedValue), arr.type().elementType()),
                    null);
        } else if (node instanceof ArrayLength arrayLength) {
            ExecutionResult r = evaluateInput(arrayLength.arraySlot.get());
            if (r.returnFrom() != null)
                return r;
            if (r.value() == null)
                return throwException(symbols.NullPointerException, "Can't get array length from null array" +
                        " (got null object from: " + arrayLength.arraySlot.get() + ")");

            return new ExecutionResult(((Array) r.value()).length(), null);
        } else if (node instanceof ThrowNode throwNode) {
            ExecutionResult r = evaluateInput(throwNode.exceptionSlot.get());
            if (r.returnFrom() != null)
                return r;
            return throwException((ClassObj) r.value());
        } else if (node instanceof ObjectNode objectNode) {
            Clazz clazz = (Clazz) objectNode.objectIdentity.type;
            ExecutionResult initializationResult = ensureInitialized(clazz);

            if (initializationResult != null)
                return initializationResult;

            return new ExecutionResult(createObject(clazz), null);
        } else if (node instanceof CheckCast checkCast) {
            ExecutionResult r = evaluateInput(checkCast.valueSlot.get());
            if (r.returnFrom() != null)
                return r;
            Obj obj = (Obj) r.value();
            if (obj == null || checkCast.type.isAssignableFrom(obj.type()))
                return new ExecutionResult(obj, null);
            else
                throw new RuntimeException("CCE " + obj.type() + ", " + checkCast.type());
        } else if (node instanceof InstanceOf instanceOf) {
            ExecutionResult r = evaluateInput(instanceOf.valueSlot.get());
            if (r.returnFrom() != null)
                return r;
            Obj obj = (Obj) r.value();
            boolean isInstanceOf = obj != null && instanceOf.type.isAssignableFrom(obj.type());
            return new ExecutionResult(isInstanceOf ? 1 : 0, null);
        } else if (node instanceof GetStaticNode getStaticNode) {
            ExecutionResult initializationResult = ensureInitialized(getStaticNode.field.clazz);
            if (initializationResult != null)
                return initializationResult;

            Object val = readStaticField(getStaticNode.field);
            return new ExecutionResult(convertShortNumericsToInt(val), null);
        } else if (node instanceof PutStaticNode putStaticNode) {
            ExecutionResult initializationResult = ensureInitialized(putStaticNode.field.clazz);
            if (initializationResult != null)
                return initializationResult;

            ExecutionResult r = evaluateInput(putStaticNode.valueSlot.get());
            if (r.returnFrom() != null)
                return r;
            staticFieldValues.put(putStaticNode.field, r.value());
            return new ExecutionResult(null, null);
        } else if (node instanceof GetFieldNode getFieldNode) {
            ExecutionResult r = evaluateInput(getFieldNode.objectSlot.get());
            if (r.returnFrom() != null)
                return r;

            if (r.value() == null)
                return throwException(symbols.NullPointerException, "Can't get field '" + getFieldNode.field.name + "'" +
                        " value from null object (" + getFieldNode.objectSlot.get() + ")");

            Object fieldValue = getOrDefault(((ClassObj) r.value()).readField(getFieldNode.field), getFieldNode.type());
            return new ExecutionResult(convertShortNumericsToInt(fieldValue), null);
        } else if (node instanceof PutFieldNode putFieldNode) {
            ExecutionResult r = evaluateInput(putFieldNode.objectSlot.get());
            if (r.returnFrom() != null)
                return r;
            ExecutionResult r2 = evaluateInput(putFieldNode.valueSlot.get());
            if (r2.returnFrom() != null)
                return r;
            ClassObj obj = (ClassObj) r.value();
            assert putFieldNode.field.clazz.isAssignableFrom(obj.type());
            assert putFieldNode.field.clazz.allFields <= obj.type().allFields : putFieldNode.field + ", " + obj.type();
            //assert obj.fields.length == obj.type().allFields;
            assert putFieldNode.field.index < obj.type().allInstanceFields;
            obj.writeField(putFieldNode.field, convertForStore(r2.value(), putFieldNode.field.type()));
            return new ExecutionResult(null, null);
        } else if (node instanceof UnaryNumericOpNode unaryNumericOpNode) {
            ExecutionResult r = evaluateInput(unaryNumericOpNode.inputSlot.get());
            if (r.returnFrom() != null)
                return r;
            MethodHandle opMH = unaryNumericOpNode.op.method;
            Object opResult;
            try {
                opResult = opMH.invoke(r.value());
            } catch (RuntimeException | Error e) {
                throw e;
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
            return new ExecutionResult(opResult, null);
        } else if (node instanceof BinaryNumericOpNode binaryNumericOpNode) {
            ExecutionResult r = evaluateInput(binaryNumericOpNode.input1Slot.get());
            if (r.returnFrom() != null)
                return r;
            ExecutionResult r2 = evaluateInput(binaryNumericOpNode.input2Slot.get());
            if (r2.returnFrom() != null)
                return r;

            MethodHandle opMH = binaryNumericOpNode.op.method;
            Object opResult;
            try {
                opResult = opMH.invoke(r.value(), r2.value());
            } catch (RuntimeException | Error e) {
                throw e;
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
            return new ExecutionResult(opResult, null);
        } else if (node instanceof LogicalAnd and) {
            ExecutionResult r = evaluateInput(and.input1.get());
            if (r.returnFrom() != null)
                return r;

            if (!intToBool((int) r.value()))
                return new ExecutionResult(0, null);

            ExecutionResult r2 = evaluateInput(and.input2.get());
            if (r2.returnFrom() != null)
                return r;

            return new ExecutionResult(intToBool((Integer) r2.value()) ? 1 : 0, null);
        } else if (node instanceof LogicalOr or) {
            ExecutionResult r = evaluateInput(or.input1.get());
            if (r.returnFrom() != null)
                return r;

            if (intToBool((int) r.value()))
                return new ExecutionResult(1, null);

            ExecutionResult r2 = evaluateInput(or.input2.get());
            if (r2.returnFrom() != null)
                return r;

            return new ExecutionResult(intToBool((Integer) r2.value()) ? 1 : 0, null);
        } else if (node instanceof WriteLocalVar writeLocalVar) {
            ExecutionResult r = evaluateInput(writeLocalVar.valueSlot.get());
            if (r.returnFrom() != null)
                return r;
            currentCall().localVars.put(writeLocalVar.localVar, r.value());
            return new ExecutionResult(null, null);
        } else if (node instanceof ReadLocalVar readLocalVar) {
            if (!currentCall().localVars.containsKey(readLocalVar.localVar))
                throw new IllegalStateException("local var not set: " + readLocalVar.localVar);
            return new ExecutionResult(currentCall().localVars.get(readLocalVar.localVar), null);
        } else if (node instanceof NopNode nopNode) {
            return new ExecutionResult(null, null);
        } else if (node instanceof InvokeSpecialOrStatic invokeSpecialOrStatic) {
            ExecutionResult initializationResult = ensureInitialized(invokeSpecialOrStatic.method.clazz);
            if (initializationResult != null)
                return initializationResult;

            Object[] args = new Object[invokeSpecialOrStatic.args.size()];
            for (int i = 0; i < args.length; i++) {
                ExecutionResult r = evaluateInput(invokeSpecialOrStatic.args.get(i));
                if (r.returnFrom() != null)
                    return r;
                args[i] = r.value();
            }
            return execute(invokeSpecialOrStatic.method, args);
        } else if (node instanceof InvokeVirtualOrInterface invokeVirtualOrInterface) {
            Object[] args = new Object[invokeVirtualOrInterface.args.size()];
            for (int i = 0; i < args.length; i++) {
                ExecutionResult r = evaluateInput(invokeVirtualOrInterface.args.get(i));
                if (r.returnFrom() != null)
                    return r;
                args[i] = r.value();
            }
            return invokeVirtual(invokeVirtualOrInterface.method, args, invokeVirtualOrInterface.args.get(0));
        } else if (node instanceof Switch switchNode) {
            ExecutionResult r = evaluateInput(switchNode.input.get());
            if (r.returnFrom() != null)
                return r;

            // SequenceNode végrehajtásánál kezeljük a case-eket
            ExecutionResult r2 = evaluateChild(switchNode.child.get());
            return r2.returnFrom() == null ? r2 : r2.withoutReturn(switchNode.switchBreakKey);
        } else if (node instanceof SwitchCase switchCase) {
            throw new RuntimeException("should not reach here");
        } else if (node instanceof BreakSwitch breakSwitch) {
            Object resultValue;
            if (breakSwitch.resultSlot.get() != null) {
                ExecutionResult r = evaluateInput(breakSwitch.resultSlot.get());
                if (r.returnFrom() != null)
                    return r;
                resultValue = r.value();
            } else
                resultValue = null;
            return new ExecutionResult(resultValue, breakSwitch.switchBreakKey);
        } else if (node instanceof TypeOfNode typeOf) {
            ExecutionResult r = evaluateInput(typeOf.inputSlot.get());
            if (r.returnFrom() != null)
                return r;
            return new ExecutionResult(fromType(((Obj) r.orElseThrow()).type()), null);
        } else
            throw new UnsupportedOperationException("TODO: " + node.getClass());
    }

    private ExecutionResult invokeVirtual(Method method, Object[] args, @Nullable Node receiverNode) {
        Obj receiver = (Obj) args[0];
        if (receiver == null)
            return throwException(symbols.NullPointerException, "Can't invoke method " +
                    method.name + " on null object" + (receiverNode == null ? "" : " (got null object from: " + receiverNode + ")"));
        Clazz receiverClass = receiver instanceof Array ? symbols.Object : ((ClassObj) receiver).type();

        assert ((Type) method.clazz).isAssignableFrom(receiverClass)
                : "tried to invoke " + method + " on " + receiverClass + (receiverNode == null ? "" :
                " (got it from " + receiverNode + " in " + callStack.peek().method.clazz + ")");

        Method m = compContext.resolveVirtualMethodOrFail(method, receiverClass);
        return execute(m, args);
    }

    private ExecutionResult executeMHLinkerMethod(Method m, Object[] args) {
        Object[] newArgs = Arrays.copyOf(args, args.length - 1);
        Method target = (Method) ((ClassObj) args[args.length - 1]).representedData();
        if (target == null)
            return throwException(symbols.Error, "MemberName not resolved");
        return switch (m.name) {
            case "linkToStatic", "linkToSpecial" -> execute(target, newArgs);
            case "linkToVirtual" -> invokeVirtual(target, newArgs, null);
            case "linkToInterface" -> throw new UnsupportedOperationException("TODO");
            default -> throw new RuntimeException("unknown MH linker method: " + m);
        };
    }

    @Override
    public ExecutionResult ensureInitialized(Clazz clazz) {
        if (initializedClasses.add(clazz)) {
            for (Clazz superclass : clazz.directSupertypes)
                ensureInitialized(superclass);
            Method clinit = compContext.findMethodOrNull(clazz, "<clinit>", new MethodType(List.of(), V));
            if (clinit != null && clinit.clazz == clazz) {
                ExecutionResult r = execute(clinit);
                assert r.value() == null;
                if (r.returnFrom() != null)
                    return r;
                fullyInitializedClasses.add(clazz);
            }
        }
        return null;
    }

    @Override
    protected List<CallFrame> stackFramesImpl(boolean forException) {
        List<CallFrame> cf = new ArrayList<>();
        boolean skip = forException;
        boolean first = true;
        ClassObj exceptionObj = null;
        for (MethodCall call : callStack) {
            if (skip) {
                if (first) {
                    exceptionObj = (ClassObj) call.localVars.get(new LocalVar(call.method, 0, Kind.L,
                            call.method.rootMethodIdentity));
                    first = false;
                    continue;
                }
                skip = false;

                if (call.localVars.get(new LocalVar(call.method, 0, Kind.L,
                        call.method.rootMethodIdentity)) == exceptionObj) {
                    switch (call.method.name) {
                        case "<init>":
                            skip = true;
                            break;
                        case "fillInStackTrace":
                            if (call.method.clazz == symbols.Throwable)
                                skip = true;
                            break;
                    }
                }
            }
            if (skip)
                continue;
            cf.add(call);
        }
        return cf;
    }

    @Override
    public Object readStaticField(Field f) {
        return getOrDefault(staticFieldValues.get(f), f.type());
    }

    @Override
    public void writeStaticField(Field f, Object value) {
        staticFieldValues.put(f, value);
    }

    @Override
    public boolean isInitialized(Clazz clazz) {
        return initializedClasses.contains(clazz);
    }

    @Override
    public boolean isFullyInitialized(Clazz clazz) {
        return fullyInitializedClasses.contains(clazz);
    }

    @Override
    public Array createArray(ArrayType arrayType, int length) {
        return new InterpreterArray(arrayType, length);
    }

    @Override
    public Array createArray(ArrayType arrayType, Object[] content) {
        return new InterpreterArray(arrayType, content);
    }

    @Override
    public ClassObj createObject(Clazz clazz) {
        return new InterpreterClassObj(clazz);
    }

    public static abstract class InterpreterObj implements Obj {

        final Type type;

        public Object representedData;

        public InterpreterObj(Type type) {
            this.type = type;
        }

        public Type type() {
            return type;
        }

        @Override
        public Object representedData() {
            return representedData;
        }

        @Override
        public void representedData(Object representedData) {
            this.representedData = representedData;
        }
    }


    public static class InterpreterArray extends InterpreterObj implements Array {

        public final Object[] elements;
        private Object info;

        public InterpreterArray(Type type, int length) {
            this(type, new Object[length]);
        }

        public InterpreterArray(Type type, Object[] elements) {
            super(type);
            assert type instanceof ArrayType;
            this.elements = elements;
        }

        public byte[] asByteArray(int off, int len) {
            byte[] b = new byte[len];
            for (int i = 0; i < len; i++)
                b[i] = (byte) elements[off + i];
            return b;
        }

        @Override
        public int length() {
            return elements.length;
        }

        @Override
        public ArrayType type() {
            return (ArrayType) type;
        }

        @Override
        public Object readElement(int index) {
            return elements[index];
        }

        @Override
        public void writeElement(int index, Object value) {
            elements[index] = value;
        }
    }

    public static class InterpreterClassObj extends InterpreterObj implements ClassObj {

        public final Clazz clazz;

        private final Object[] fields;

        public InterpreterClassObj(Clazz clazz) {
            super(clazz);
            this.clazz = clazz;
            this.representedData = null;
            this.fields = new Object[clazz.allInstanceFields];
        }

        public InterpreterClassObj(Clazz clazz, Object representedData) {
            super(clazz);
            this.clazz = clazz;
            this.representedData = representedData;
            this.fields = new Object[clazz.allInstanceFields];
        }

        @Override
        public Clazz type() {
            return clazz;
        }

        @Nullable
        public Object readField(Field f) {
            Object v = readField(f.index);
            return Interpreter.getOrDefault(v, f.type());
        }

        @Nullable
        public Object readField(int index) {
            Object v = fields[index];
            return Interpreter.getOrDefault(v, clazz.allInstanceFieldList.get(index).type());
        }

        public void writeField(Field field, Object value) {
            fields[field.index] = value;
        }

        public void writeField(int fieldIndex, Object value) {
            fields[fieldIndex] = value;
        }

        @Override
        public ClassObj cloneObject() {
            InterpreterClassObj cloned = new InterpreterClassObj(clazz, representedData);
            System.arraycopy(fields, 0, cloned.fields, 0, fields.length);
            return cloned;
        }

        @Override
        public String toString() {
            return "Obj" + Integer.toHexString(hashCode()) + " ("
                    + clazz.name.substring(clazz.name.lastIndexOf('/') + 1) + ")";
        }
    }

    private static class MethodCall implements CallFrame {

        final Method method;
        final Map<Variable, Object> localVars = new HashMap<>();
        final Deque<Map<Node, ExecutionResult>> nodeResultCacheStack = new LinkedList<>();
        final Node root;

        {
            nodeResultCacheStack.push(new HashMap<>());
        }

        public MethodCall(Method method, Node root) {
            this.method = method;
            this.root = root;
        }

        @Override
        public Method method() {
            return method;
        }
    }
}
