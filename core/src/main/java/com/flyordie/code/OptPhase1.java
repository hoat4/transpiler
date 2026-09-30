package com.flyordie.code;

import com.flyordie.code.Clazz.Method;
import com.flyordie.code.CompilationContext.TransformationChain;
import com.flyordie.code.Interpreter.Array;
import com.flyordie.code.Interpreter.ClassObj;
import com.flyordie.code.Interpreter.ExecutionResult;
import com.flyordie.code.Interpreter.Obj;
import com.flyordie.code.Location.LocationElement;
import com.flyordie.code.Location.MethodLocationElement;
import com.flyordie.code.Node.*;
import com.flyordie.code.Node.SequenceNode.MethodKey;
import com.flyordie.code.Type.ArrayType;
import com.flyordie.code.Type.PrimitiveType;
import com.flyordie.code.Variable.LocalVar;
import com.flyordie.code.Variable.LocalVar.MethodIdentity;
import com.flyordie.code.annotation.DontInline;
import com.flyordie.code.annotation.EvaluateCompileType;
import com.flyordie.code.annotation.Inline;
import com.flyordie.code.annotation.MustBeInlined;
import org.objectweb.asm.tree.AnnotationNode;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.flyordie.code.CompilationContext.context;
import static org.objectweb.asm.Opcodes.*;

public class OptPhase1 implements Transformation {

    private static final String INLINE_ANNOTATION_DESC = Inline.class.descriptorString();
    private static final String MUST_BE_INLINED_ANNOTATION_DESC = MustBeInlined.class.descriptorString();
    private static final String DONT_INLINE_ANNOTATION_DESC = DontInline.class.descriptorString();
    static final String CALLER_SENSISITVE_ANNOTATION_DESC = "Ljdk/internal/reflect/CallerSensitive;";
    private static final String EVALUATE_COMPILE_TYPE_ANN_DESC = EvaluateCompileType.class.descriptorString();
    private static final int INLINING_DEPTH_LIMIT = 5;

    private final CompilationContext compilationContext;
    private final Method compilingMethod;
    private final TransformationChain transformationChain;
    private final TransformationChain inliningTransformationChain;
    private boolean aggressiveInlining;
    private final MethodCompilationContext mcc;

    // TODO devirtualizálásnál elveszhet NPE, mert JS-ben .call úgy kezeli a null receivert mintha
    //      a Window lenne és nem dob semmit

    public OptPhase1(MethodCompilationContext mcc, TransformationChain inliningTransformationChain, boolean aggressiveInlining) {
        this.compilationContext = mcc.compilationContext;
        this.compilingMethod = mcc.method;
        this.transformationChain = mcc.transformationChain;
        this.mcc = mcc;
        this.inliningTransformationChain = inliningTransformationChain;
        this.aggressiveInlining = aggressiveInlining;
    }

    @Override
    public Node enter(Node node) {
        return node;
    }

    @Override
    public Node exit(Node node) {
        switch (node) {
            case GetStaticNode getStaticNode -> {
                // TODO ensureInitialized-et InvokeStaticOrSpecialnál és PutStaticNodenál is kéne. meg Lookup.ensureClassInitializednél is.
                if ((getStaticNode.field.access & ACC_STATIC) != 0) {

                    if ((getStaticNode.field.access & ACC_FINAL) == ACC_FINAL || getStaticNode.field.visibleAnnotations != null &&
                            getStaticNode.field.visibleAnnotations.stream().anyMatch(ann -> ann.desc.equals("Ljdk/internal/vm/annotation/Stable;"))) {
                        if (transformationChain.replacementProvider.treatStaticFinalAsConstant(getStaticNode.field)) {
                            boolean init;
                            try {
                                init = compilationContext.interpreter().ensureInitialized2(getStaticNode.field.clazz);
                            } catch (Exception
                                     | UnsatisfiedLinkError // ideiglenesen, amíg NewInterpreter.nativeCallBSM nincs
                                    // kijavítva
                                    e) {
                                throw new RuntimeException("Cannot execute static initializer of " + getStaticNode.field.clazz
                                        + ", needed to compile " + mcc.method.toString() + ": " + e, e);
                            }
                            if (init) {
                                Object val = compilationContext.interpreter().readStaticField(getStaticNode.field);
                                assert val != null
                                        || !getStaticNode.field.clazz.name.equals("jdk/internal/access/SharedSecrets")
                                        && !(getStaticNode.field.type() instanceof Clazz c && c.name.startsWith("jdk/internal/access/")) : getStaticNode.field;
                                return new ConstantNode(val);
                            }
                        }
                    }
                }
            }
            case InvokeVirtualOrInterface invoke -> {
                Method m = invoke.method;
                Node receiver = invoke.args.get(0);

                if (mustEvaluateCompileTime(invoke.method, invoke.location)) {
                    Object[] args = new Object[invoke.args.size()];
                    boolean allConstant = true;
                    for (int i = 0; i < args.length; i++) {
                        Node n = invoke.args.get(i);
                        if (!(n instanceof ConstantNode cn)) {
                            allConstant = false;
                            break;
                        }
                        //throw new RuntimeException("not constant arg of " + invoke.method + ": " + n);
                        args[i] = compilationContext.interpreter().constant(cn.value,
                                ((MethodLocationElement) invoke.location.elements().get(0)).method().clazz);
                    }
                    if (allConstant) {
                        Interpreter interpreter = compilationContext.interpreter();
                        Interpreter.CallFrame prevPrependedCallFrame = interpreter.prependedCallFrame;
                        ExecutionResult result;
                        try {
                            interpreter.prependedCallFrame = new Interpreter.CallFrame() {
                                @Override
                                public Method method() {
                                    return ((MethodLocationElement) invoke.location.elements().get(0)).method();
                                }
                            };
                            result = interpreter.executeVirtual(m, args);
                        } catch (RuntimeException e) {
                            // indirect VH-k miatt van ez egyelőre.
                            // VarHandleGuards.guard_LI_V-ben VH.checkAccessModeThenIsDirect
                            // konstans, viszont az if csak OptPhase2 által lenne eliminálva,
                            // és mi korábban jutunk el addig hogy meghívjuk
                            // VarForm.getMemberName-et (ami meg NPE-t dob indirect VH esetén).
                            // valszeg majd össze kell vonni OptPhase1-et OptPhase2-vel.
                            return new ErrorNode(e.toString());
                        } finally {
                            interpreter.prependedCallFrame = prevPrependedCallFrame;
                        }
                        return new ConstantNode(result.orElseThrow());
                    } else {
                        return node;
                    }
                }

                if (m.name.equals("getClass") && m.desc.equals("()Ljava/lang/Class;"))
                    return new TypeOfNode(receiver);
                if (receiver instanceof ConstantNode cn) {
                    if (cn.value != null) {
                        // TODO callerClass itt nem jó, mert lehet hogy inlineolva volt már ez a node
                        Obj obj = (Obj) compilationContext.interpreter().constant(cn.value, compilingMethod.clazz);
                        Clazz clazz = obj instanceof Array ? compilationContext.findClass(KnownClass.OBJECT) : ((ClassObj) obj).type();
                        Method methodToInline = inliningTransformationChain.replacementProvider.replacementClass(clazz) == m.clazz
                                ? m : compilationContext.findMethodOrFail(clazz, m.name,
                                // YourKittel nézve jött elő a replacedMethodTypera igény,
                                // JSReplacementProvider.ObjectImpl::clone()LObjectImpl;-et akarta hívni,
                                // de nem találta j.l.Object::clone()LObjectImpl;-et, csak
                                // j.l.Object::clone()LObject;-et
                                inliningTransformationChain.replacementProvider.replacedMethodType(m.type()));
                        if (shouldInline(methodToInline, compilingMethod, inliningTransformationChain, aggressiveInlining) != InliningSpec.DONT_INLINE)
                            return inlineMethodCall(methodToInline, invoke.args, invoke.location);
                        else {
                            InvokeSpecialOrStatic invokeSpecialOrStatic = new InvokeSpecialOrStatic(methodToInline, invoke.args.toArray(Node[]::new));
                            invokeSpecialOrStatic.copyInfoFrom(invoke);
                            return invokeSpecialOrStatic;
                        }
                    }
                } else {
                    Clazz receiverType = m.clazz;
                    if (!(receiver instanceof ErrorNode) /* mert akkor nincs type-ja */ &&
                            receiver.type() != null && !receiver.type().isAssignableFrom(m.clazz)
                            && m.clazz.isAssignableFrom(receiver.type())
                            && !(receiver.type() instanceof ArrayType && m.clazz.knownClass == KnownClass.OBJECT)) {
                        receiverType = (Clazz) receiver.type();
                        m = compilationContext.findMethodOrFail(receiverType, m.name, m.type());
                    }

                    Method m2 = isFinal(m, receiverType);
                    if (m2 != null) {
                        if (shouldInline(m2, compilingMethod, inliningTransformationChain, aggressiveInlining) != InliningSpec.DONT_INLINE)
                            return inlineMethodCall(m2, invoke.args, invoke.location);
                        else {
                            InvokeSpecialOrStatic invokeSpecialOrStatic = new InvokeSpecialOrStatic(m2, invoke.args.toArray(Node[]::new));
                            invokeSpecialOrStatic.copyInfoFrom(invoke);
                            return invokeSpecialOrStatic;
                        }
                    } else {
                        if (shouldInline(m, compilingMethod, inliningTransformationChain, aggressiveInlining) == InliningSpec.INLINE_OR_FAIL)
                            throw new RuntimeException(m + " is not inlineable to " + compilingMethod + " because receiver is not constant: " +
                                    receiver + " (expression has type: " + receiver.type() + ")");
                    }
                }
            }
            case InvokeSpecialOrStatic invoke -> {
                if (invoke.method.clazz.knownClass == KnownClass.System &&
                        invoke.method.name.equals("getProperty") &&
                        invoke.args.get(0) instanceof ConstantNode cn &&
                        "staticEnv".equals(cn.value)) {
                    return new ConstantNode("true");
                }
                if (invoke.method.clazz.knownClass == KnownClass.BOOLEAN &&
                        invoke.method.name.equals("getBoolean") &&
                        invoke.args.get(0) instanceof ConstantNode cn &&
                        "staticEnv".equals(cn.value)) {

                    // lehet hogy olyan függvényre lyukadunk ki, ami exceptiont okoz fordításnál,
                    // ezért kerüljük el a shouldInline hívásokat
                    // (ld. DefaultURLImageViewImpl.loadImageAsync)

                    aggressiveInlining = false;

                    return new ConstantNode(1);
                }

                if (mustEvaluateCompileTime(invoke.method, invoke.location)) {
                    if (invoke.args.stream().allMatch(ConstantNode.class::isInstance)) {
                        Object[] args = new Object[invoke.args.size()];
                        for (int i = 0; i < args.length; i++)
                            args[i] = compilationContext.interpreter().constant(
                                    ((ConstantNode) invoke.args.get(i)).value,
                                    ((MethodLocationElement) invoke.location.elements().get(0)).method().clazz);
                        ExecutionResult result = compilationContext.interpreter().execute(invoke.method, args);
                        return new ConstantNode(result.orElseThrow());
                    } else {
                        return invoke;
                    }
                }

                if (invoke.method.name.equals("getClass") && invoke.method.desc.equals("()Ljava/lang/Class;"))
                    return new TypeOfNode(invoke.args.get(0));

                if (invoke.method.clazz.knownClass == KnownClass.Preconditions && invoke.method.name.equals("checkIndex")) {
                    Node.replaceInInputsOfUsages(invoke, invoke.args.get(0));
                    return node;
                }

                if (shouldInline(invoke.method, compilingMethod, inliningTransformationChain, aggressiveInlining) != InliningSpec.DONT_INLINE)
                    return inlineMethodCall(invoke.method, invoke.args, invoke.location);
            }
            case InstanceOf instanceOf -> {
                if (instanceOf.type instanceof Clazz c && c.knownClass == KnownClass.VirtualThread)
                    // különben belekavarodna Continuationökbe (mert annak a clinitje exceptiont dob, ha
                    // ContinuationSupport.isSupported() false-t ad vissza)
                    return new ConstantNode(0);
            }
            case null, default -> {
            }
        }

        return node;
    }

    public static boolean mustEvaluateCompileTime(Method method, @Nullable Location caller) {
        Method callerMethod = caller != null &&
                caller.elements().get(0) instanceof MethodLocationElement mle ? mle.method() : null;
        return method.clazz.name.equals("sun/util/locale/provider/LocaleProviderAdapter")
                && method.name.equals("forType") ||
                method.clazz.knownClass == KnownClass.CLASS && method.name.equals("forName") ||
                method.clazz.knownClass == KnownClass.System && method.name.equals("getProperty") ||

                method.clazz.name.equals("java/lang/foreign/ValueLayout") && method.name.equals("accessHandle") ||
                method.clazz.name.equals("java/lang/invoke/VarForm") && method.name.equals("getMemberName") ||
                method.clazz.name.equals("java/lang/invoke/MethodHandle") && method.name.equals("asType") ||
                method.clazz.name.equals("java/lang/invoke/MethodType") && method.name.equals("methodType") ||
                method.clazz.name.equals("java/lang/invoke/VarHandle$AccessType") && method.name.equals("accessModeType") ||
                method.clazz.name.startsWith("java/lang/invoke/VarHandle$AccessTypeSegmentAs") &&
                        method.name.equals("accessModeTypeUncached") ||
                method.clazz.name.equals("jdk/internal/foreign/layout/ValueLayouts$AbstractValueLayout") &&
                        method.name.equals("accessHandle") ||

                // ConcurrentSkipListSetbeli clone hülyeség
                method.clazz.name.equals("java/lang/Class") && method.name.equals("getDeclaredField") ||
                method.clazz.name.equals("java/lang/reflect/Field") && method.name.equals("setAccessible") ||

                // upcall
                method.clazz.name.equals("java/lang/invoke/BoundMethodHandle") &&
                        (method.name.equals("makeReinvoker") || method.name.equals("editor")) ||
                method.clazz.name.startsWith("java/lang/invoke/") && method.name.equals("speciesData") &&
                        method.type().parameterTypes().isEmpty() ||
                method.clazz.name.equals("java/lang/invoke/LambdaFormEditor") &&
                        (method.name.equals("oldSpeciesData") || method.name.equals("bindArgumentType")
                                || method.name.equals("bindArgumentForm")) ||
                method.clazz.name.equals("java/lang/invoke/ClassSpecializer") && method.name.equals("transformHelper") ||
                method.clazz.name.equals("java/lang/invoke/BoundMethodHandle$SpeciesData") && method.name.equals("extendWith") ||
                method.clazz.name.equals("java/lang/invoke/ClassSpecializer$SpeciesData") && method.name.equals("factory") ||
                method.clazz.name.equals("java/lang/foreign/Linker") && method.name.equals("upcallType") ||
                method.clazz.name.equals("java/lang/invoke/MethodHandles$Lookup") && method.name.equals("findVirtual") ||

                // VH
                method.clazz.knownClass == KnownClass.VarHandle &&
                        (method.name.equals("accessModeType") || method.name.equals("accessModeTypeUncached")) ||
                // indirekt VH-hoz ez a kettő:
                method.clazz.knownClass == KnownClass.VarHandle && method.name.equals("checkAccessModeThenIsDirect") ||
                method.clazz.knownClass == KnownClass.VarHandle && method.name.equals("getMethodHandle") ||

                // explicit
                method.hasInvisibleAnnotation(EVALUATE_COMPILE_TYPE_ANN_DESC);
    }

    private static Method isFinal(Method m, Clazz receiverType) {
        if (m.clazz.name.equals("jdk/internal/foreign/Scoped") && m.name.equals("sessionImpl"))
            return m;
        if (m.clazz.name.equals("jdk/internal/foreign/MemorySessionImpl") && m.name.equals("checkValidStateRaw"))
            return m;
        if (m.clazz.name.equals("jdk/internal/reflect/DirectConstructorHandleAccessor") && m.name.equals("invokeImpl"))
            return m;
        if (m.clazz.name.equals("java/lang/foreign/ValueLayout") && (
                m.name.equals("accessHandle") || m.name.equals("byteSize")))
            return m;
        if (m.clazz.name.equals("jdk/internal/misc/ScopedMemoryAccess") && m.forceInline) // nincs leszármazottja
            return m;
        if (m.clazz.name.equals("java/lang/foreign/MemorySegment") && m.forceInline)
            return m;
        if (m.clazz.name.equals("org/teavm/jso/JSObject") && m.name.equals("cast"))
            // TODO ez nem ide kéne, hanem a TeaVM-es modulba
            return m;
        if (m.clazz.name.equals("jdk/internal/foreign/NativeMemorySegmentImpl"))
            // egyelőre feltesszük hogy MappedMemorySegmentImplet nem használunk
            // ez se ide kéne, hanem CEE-be
            return m;

        if ((m.access & (ACC_FINAL | ACC_PRIVATE)) != 0 || (receiverType.access & ACC_FINAL) != 0)
            return m;

        List<Clazz> possibleReceivers = collectPermittedSubclasses(m.clazz);
        if (possibleReceivers != null) {
            Set<Method> possibleImplementations = possibleReceivers.stream().
                    map(subclass -> context().findMethodOrFail(subclass, m.name, m.type())).
                    collect(Collectors.toSet());
            if (possibleImplementations.size() == 1) {
                Method m2 = possibleImplementations.iterator().next();
                assert (m2.access & ACC_ABSTRACT) == 0;
                return m2;
            }
        }
        return null;
    }

    // ennek inkább CompilationContextben kéne lennie
    private static List<Clazz> collectPermittedSubclasses(Clazz c) {
        List<Clazz> l = new ArrayList<>();
        l.add(c);
        for (int i = 0; i < l.size(); i++) {
            c = l.get(i);
            if ((c.access & ACC_FINAL) != 0)
                continue;
            if (c.permittedSubclasses == null)
                return null;
            for (String permittedSubclassName : c.permittedSubclasses)
                l.add(context().findClass(permittedSubclassName));
        }
        l.removeIf(cl -> (cl.access & ACC_ABSTRACT) == ACC_ABSTRACT);
        return l;
    }

    public static long main(long l, int i) {
        return l + i;
    }

    @Nonnull
    private SequenceNode inlineMethodCall(Method m, InputList args, Location location) {
        SequenceNode seq = new SequenceNode(new MethodKey("inline " + m));
        assert m.fullArgTypes().size() == args.size() : m + ", " + args;

        Method m2 = inliningTransformationChain.replacementProvider.replacementFor(m);
        // a típusok nem feltétlen egyeznek meg, mert a replacementben az eredeti paramétertípus helyett lehet hogy
        // replacement típusok vannak
        // ez akkor tipikus, ha replacement metódust elfelejtettük statikussá tenni
        assert m2.fullArgTypes().size() == m.fullArgTypes().size() : m + ", " + m2 + "; " + m.fullArgTypes() + ", " + m2.fullArgTypes();

        Node inlinedMethodRoot = compilationContext.compile(m, inliningTransformationChain).root();
        Cloner cloner = new Cloner(loc -> {
            List<LocationElement> l = new ArrayList<>(loc.elements());
            l.addAll(location.elements());
            return new Location(l);
        });

        Node body = cloner.clone(inlinedMethodRoot);
        body.root = false;

        MethodIdentity newMethodIdentity = cloner.cloneKey(m2.rootMethodIdentity, MethodIdentity::new);
        for (int i = 0; i < m.fullArgTypes().size(); i++) {
            Node arg = args.get(i);
            seq.nodes.add(new WriteLocalVar(m2.parameter(i, newMethodIdentity), arg));
        }
        seq.nodes.add(body);
        if (m.type().returnType() != PrimitiveType.V)
            seq.resultSlot.set(body);
        return seq;
    }

    InliningSpec shouldInline(Method from, Method to, TransformationChain transformationChain, boolean aggressiveInlining) {
        if (from.name.equals("containsRealNode")) // ez valszeg elavult, datamapper nem fordult le régebben enélkül
            return InliningSpec.DONT_INLINE;
        if (from.clazz.name.equals("com/flyordie/configbinder/type/structure" +
                "/PropertyDefinition$MethodPropertyLocation") && from.name.equals("<init>"))
            // TODO ezt nézzük meg alaposabban. úgy tűnik hogy ez sokszor inlineolódik magába, aztán így
            //      túl nagy kód generálódik. a tünet az hogy OOM lesz Node::walk2-aen, jó hosszú stack traceszel.
            return InliningSpec.DONT_INLINE;

        context().inliningDepth++;
        try {
            InliningSpec inliningSpec;
            try {
                inliningSpec = shouldInline2(from, transformationChain, aggressiveInlining, to);
            } catch (WrappedCompilationFailure e) {
                e.elements.add(to);
                throw e;
            } catch (InliningDecisionFailureException e) {
                InliningDecisionFailureException e2 = new InliningDecisionFailureException(e.getCause());
                e2.elements.addAll(e.elements);
                e2.elements.add(from + " to " + to);
                throw e2;
            } catch (Exception e) {
                InliningDecisionFailureException e2 = new InliningDecisionFailureException(e);
                e2.elements.add(from + " to " + to);
                throw e2;
            }
            if (transformationChain.replacementProvider.constructorReplacement(from) != null) {
                if (inliningSpec == InliningSpec.INLINE_OR_FAIL)
                    throw new RuntimeException("not inlineable (because has constructor replacement): " + from +
                            " ( -> " + to + ")");
            } else
                from = transformationChain.replacementProvider.replacementFor(from);
            return switch (inliningSpec) {
                case INLINE -> canInline(from, to, transformationChain, true) == null
                        ? InliningSpec.INLINE : InliningSpec.DONT_INLINE;
                case DONT_INLINE -> InliningSpec.DONT_INLINE;
                case INLINE_OR_FAIL -> {
                    InliningFailureCause c = canInline(from, to, transformationChain, false);
                    if (c != null)
                        throw new RuntimeException("not inlineable (" + c + "): " + from + " ( -> " + to + ")");
                    yield InliningSpec.INLINE_OR_FAIL;
                }
            };
        } finally {
            context().inliningDepth--;
        }
    }

    private static class InliningDecisionFailureException extends RuntimeException {

        public final List<String> elements = new ArrayList<>();

        public InliningDecisionFailureException(Throwable cause) {
            super(cause);
            assert !(cause instanceof InliningDecisionFailureException);
        }

        @Override
        public String getMessage() {
            return hashCode() + "couldn't make decision about inlining " + elements + ": " + getCause();
        }
    }

    private static boolean isRecursion(Method from, Method to) {
        return from.equals(to);
    }

    private static InliningFailureCause canInline(Method m, Method caller, TransformationChain transformationChain,
                                                  boolean checkDepthLimit) {
        if (checkDepthLimit && context().inliningDepth >= INLINING_DEPTH_LIMIT)
            return InliningFailureCause.INILING_TOO_DEEP;

        if (m.equals(caller)) // direct recursion
            return InliningFailureCause.DIRECT_RECURSION;

        // detect indirect recursion
        Node root = context().compile(m, transformationChain).root();
        boolean[] b = {true};
        root.walk(s -> {
            WrittenVariableAndValue writtenVar = s.writtenVariable();
            if (writtenVar != null && writtenVar.variable() instanceof LocalVar localVar &&
                    localVar.method().equals(caller))
                b[0] = false;
            Variable readenVar = s.readenVariable();
            if (readenVar instanceof LocalVar localVar && localVar.method().equals(caller))
                b[0] = false;
        });
        return b[0] ? null : InliningFailureCause.INDIRECT_RECURSION;
    }

    private enum InliningFailureCause {
        INILING_TOO_DEEP, DIRECT_RECURSION, INDIRECT_RECURSION
    }

    // Ezen van bőven javítandó. Pl. most ArraysSupport.vectorizedMismatchot 1000 sorra fordítja le, mert beleinline-ol
    // bonyolult (ciklust is tartalmazó) Unsafe-es függvényeket hússzor.
    private static InliningSpec shouldInline2(Method m, TransformationChain transformationChain,
                                              boolean aggressiveInlining, Method callerMethod) {

        /*
        if (m.name.equals("checkCast") && m.clazz.name.startsWith("java/lang/invoke/DirectMethodHandle")
                || m.name.equals("invoke") && m.clazz.name.equals("java/lang/invoke/LambdaForm$MH"))
            // hogy működjön JSTransformerben a konstanssá alakítás
            return InliningSpec.INLINE_OR_FAIL;
         */

        if (!transformationChain.replacementProvider.allowInlining(m))
            return InliningSpec.DONT_INLINE;

        if (transformationChain.replacementProvider.constructorReplacement(m) == null) {
            Method m2;
            try {
                m2 = transformationChain.replacementProvider.replacementFor(m);
            } catch (RuntimeException e) {
                // TODO itt hiányos lesz a usages
                throw new WrappedCompilationFailure(m, e);
            }

            if (m2.invisibleAnnotations != null)
                for (AnnotationNode ann : m2.invisibleAnnotations) {
                    if (ann.desc.equals(MUST_BE_INLINED_ANNOTATION_DESC))
                        return InliningSpec.INLINE_OR_FAIL;
                }
        }

        if (context().isCompiling(m, transformationChain))
            return InliningSpec.DONT_INLINE;

        if (transformationChain.replacementProvider.constructorReplacement(m) != null)
            // hogy később tudjunk constructorreplacementet csinálni
            return InliningSpec.DONT_INLINE;

        m = transformationChain.replacementProvider.replacementFor(m);

        if (m.invisibleAnnotations != null)
            for (AnnotationNode ann : m.invisibleAnnotations) {
                if (ann.desc.equals(DONT_INLINE_ANNOTATION_DESC))
                    return InliningSpec.DONT_INLINE;
            }

        if (m.forceInline) {
            // NativeMSI.asSlice hív checkBoundsot, amit nem tudunk inline-olni, mert
            // nem látjuk a receiver type-ot (pedig nyilvánvaló)
            if (m.clazz.name.equals("jdk/internal/foreign/AbstractMemorySegmentImpl"))
                return InliningSpec.INLINE;

            // checkAccessModeThenIsDirect @ForceInline, pedig felül van írva
            if (m.clazz.knownClass == KnownClass.VarHandle)
                return InliningSpec.INLINE;

            return InliningSpec.INLINE_OR_FAIL;
        }

        // jextract által generált segédfüggvények inlineolása, hogy NativeEntryPoint konstans legyen
        if (m.name.endsWith("$MH"))
            return InliningSpec.INLINE_OR_FAIL;
        if ((m.clazz.name.endsWith("/RuntimeHelper") || m.clazz.name.equals("RuntimeHelper")) && m.name.equals("requireNonNull"))
            return InliningSpec.INLINE_OR_FAIL;

        if (m.clazz.name.equals("jdk/internal/misc/Unsafe") && (m.access & ACC_NATIVE) == 0 && !m.isStatic())
            return InliningSpec.INLINE;
        if (m.isPolySigMethodSpecialization && !m.name.startsWith("linkTo")) // linkereket az emitter kezeli
            return InliningSpec.INLINE;
        if (m.clazz.name.equals("java/lang/invoke/DirectMethodHandle") &&
                (m.name.equals("constructorMethod") || m.name.equals("allocateInstance")))
            return InliningSpec.INLINE;
        if (m.name.equals("<init>") && (m.clazz.name.equals("java/lang/Object") || m.clazz.name.contains("$$Lambda$") ||
                (context().findClass(KnownClass.THROWABLE).isAssignableFrom(m.clazz) && m.clazz != context().findClass(KnownClass.THROWABLE))))
            return InliningSpec.INLINE;
        if (m.name.startsWith("lambda$"))
            return InliningSpec.INLINE;
        if (m.clazz.name.equals("java/io/PrintStream") && m.name.equals("println")) // hogy a console.log hívásoknál a valós hívót mutassa
            return InliningSpec.INLINE;
        if (m.clazz.name.equals("java/lang/reflect/Proxy") && m.name.equals("newProxyInstance"))
            return InliningSpec.DONT_INLINE;
        if (m.clazz.knownClass == KnownClass.StackWalker && m.name.equals("getCallerClass"))
            return InliningSpec.DONT_INLINE; // mert majd OptPhase2 elintézi
        if (m.clazz.allAncestorTypesAndThis.stream().
                anyMatch(cv -> cv.knownClass == KnownClass.ClassValue) &&
                m.name.equals("get") && m.type().parameterTypes().size() == 1
                && m.type().parameterTypes().get(0) instanceof Clazz c && c.knownClass == KnownClass.CLASS)
            return InliningSpec.DONT_INLINE; // különben OptPhase4 nem láthatná
        if (m.clazz.knownClass == KnownClass.THROWABLE && m.name.equals("<init>"))
            return InliningSpec.DONT_INLINE;
        if (m.clazz.name.equals("java/lang/invoke/Invokers") && m.name.startsWith("new") && m.name.endsWith("Exception"))
            return InliningSpec.DONT_INLINE;
        if (m.clazz.knownClass == KnownClass.Constructor && m.name.equals("newInstanceWithCaller") ||
                m.clazz.name.equals("jdk/internal/reflect/DirectConstructorHandleAccessor") &&
                        m.name.equals("newInstance"))
            // hogy a constant helyessen a ConstructorAccessor
            return InliningSpec.INLINE;

        // foreign upcall support (MH.bindTo)
        if (m.clazz.knownClass == KnownClass.METHOD_HANDLE && m.name.equals("invokeBasic") &&
                callerMethod != null && callerMethod.name.startsWith("copyWithExtend") &&
                callerMethod.clazz.name.startsWith("java/lang/invoke/BoundMethodHandle$Species_"))
            return InliningSpec.INLINE_OR_FAIL;
        if (m.clazz.name.equals("java/lang/invoke/DirectMethodHandle$Interface") && m.name.equals("checkReceiver"))
            // ez nem szükséges feltétlen, csak csökken kicsit a kód mennyisége tőle
            return InliningSpec.INLINE;

        // ezeket magától is kéne inlineolnia
        if (m.clazz.name.equals("java/util/stream/Stream") && m.name.equals("of")
                || isInClass(m, "java/util/stream/StreamSupport", transformationChain))
            return InliningSpec.INLINE_OR_FAIL;
        if (m.clazz.name.equals("java/util/Arrays") && (m.name.equals("stream") || m.name.equals("spliterator")))
            return InliningSpec.INLINE_OR_FAIL;

        if (!aggressiveInlining && (m.access & (ACC_NATIVE)) != 0) // TODO
            return InliningSpec.DONT_INLINE;

        if ((m.access & (ACC_ABSTRACT)) != 0)
            return InliningSpec.DONT_INLINE;

        if (m.invisibleAnnotations != null)
            for (AnnotationNode ann : m.invisibleAnnotations) {
                if (ann.desc.equals(INLINE_ANNOTATION_DESC))
                    return InliningSpec.INLINE;
            }
        if (m.hasVisibleAnnotation(CALLER_SENSISITVE_ANNOTATION_DESC))
            return InliningSpec.INLINE;

        if (m.clazz.knownClass == KnownClass.METHOD_HANDLE && m.name.startsWith("linkTo"))
            // CompContext.mhInvoker generált neki törzset, ami önmaga meghívása lesz, tehát fölösleges inlineolni
            return InliningSpec.DONT_INLINE;

        if (!aggressiveInlining)
            return InliningSpec.DONT_INLINE;

        if (context().inliningDepth < INLINING_DEPTH_LIMIT) {
            CompilationContext.CompilationResult compilationResult;
            Node n;
            try {
                compilationResult = context().compile(m, transformationChain);
            } catch (WrappedCompilationFailure e) {
                e.elements.add(m);
                throw e;
            } catch (RuntimeException e) {
                throw new WrappedCompilationFailure(m, e);
            }
            if (!compilationResult.emittable())
                return InliningSpec.INLINE;
            n = compilationResult.root();
            if (n != null) {
                int[] a = new int[1];
                n.walk(n2 -> {
                    a[0]++;

                    // 10 soknak tűnik, de pl. Throwable létrehozásnál rendszeresen van legalább 5 elemű
                    if (n2.location.elements().size() > 5)
                        a[0] = Integer.MIN_VALUE;
                });
                // eredetileg 10 volt, de az nagyon kevés, még Optional.of is meghaladja
                if (a[0] < 15 && a[0] >= 0)
                    return InliningSpec.INLINE;
            }
        }
        return InliningSpec.DONT_INLINE;
    }

    private static boolean isInClass(Method m, String className, TransformationChain transformationChain) {
        if (m.clazz.name.equals(className))
            return true;
        List<Clazz> replaced = transformationChain.replacementProvider.replacedClass(m.clazz);
        if (replaced != null)
            for (Clazz c : replaced)
                if (c != null && c.name.equals(className))
                    return true;
        return false;
    }

    enum InliningSpec {
        DONT_INLINE, INLINE, INLINE_OR_FAIL
    }

    private static class WrappedCompilationFailure extends RuntimeException {

        final List<Method> elements = new ArrayList<>();
        final Method m;

        public WrappedCompilationFailure(Method m, Throwable cause) {
            super(cause);
            this.m = m;
        }

        @Override
        public String getMessage() {
            return "Could not compile " + m + ", needed for possible inlining to " + elements + ": " +
                    (getCause().getClass() == RuntimeException.class && getCause().getMessage() != null
                            ? getCause().getMessage() : getCause());
        }
    }
}
