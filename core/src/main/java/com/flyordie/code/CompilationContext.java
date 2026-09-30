package com.flyordie.code;

import com.flyordie.code.Clazz.Field;
import com.flyordie.code.Clazz.Member;
import com.flyordie.code.Clazz.Method;
import com.flyordie.code.Interpreter.ClassObj;
import com.flyordie.code.Interpreter.Obj;
import com.flyordie.code.Interpreter.WrappedException;
import com.flyordie.code.Location.MethodLocationElement;
import com.flyordie.code.Node.*;
import com.flyordie.code.Node.ErrorNode.ErrorType;
import com.flyordie.code.Node.SequenceNode.MethodKey;
import com.flyordie.code.Type.ArrayType;
import com.flyordie.code.Type.PrimitiveType;
import com.flyordie.code.Variable.LocalVar;
import com.flyordie.code.Variable.LocalVar.Kind;
import com.flyordie.code.js.DefaultJSInteropProvider;
import com.flyordie.code.js.JSEmitter;
import com.flyordie.code.util.ScopedValue;
import org.objectweb.asm.*;
import org.objectweb.asm.commons.JSRInlinerAdapter;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.UnaryOperator;

import static org.objectweb.asm.Opcodes.*;

public class CompilationContext implements Type.Lookup {

    // statikus mezők miatt, amik j.l.Classban vannak látszólag tárolva
    public static final int MEMBER_NUMBER_MIN = 1000;

    static final boolean INLINE = false;

    final Map<String, Clazz> classes = new HashMap<>();
    public final Map<CompilationCacheKey, CompilationResult> compilationCache = new HashMap<>();

    // ezt memberFromGlobalID-n keresztül olvassuk ki, mert a Member.globalNumber-ben többszöröse van a számnak
    public final List<Member> allMembers = new ArrayList<>(Collections.nCopies(MEMBER_NUMBER_MIN * 8, null));

    public static final ScopedValue<CompilationContext> threadLocal = new ScopedValue<>();

    private Interpreter interpreter;
    private final Set<CompilationCacheKey> compiling = new HashSet<>();

    int inliningDepth;

    public AddressingMode addressingMode = AddressingMode.JS;

    public CompilationContext() {
    }

    public void initialize() {
        ScopedValue.where(threadLocal, this).run(() -> {
            // OldInterpreter most nem működik, mert SystemModules$defaultban átírják 0-s lokálvart más értékre,
            // ami nem tetszik OptPhase2-nek
            interpreter = new NewInterpreter(this);
            configureInterpreter(interpreter);
            interpreter.initialize();
        });
    }

    protected void configureInterpreter(Interpreter interpreter) {
    }

    public Clazz findClass(KnownClass knownClass) {
        return findClass(knownClass.className);
    }

    public Clazz findClass(Class<?> type) {
        assert type != null;
        return findClass(type.getName().replace('.', '/'));
    }

    private LinkedList<Clazz> classLoadQueue;

    @Override
    public Clazz findClass(String name) {
        Clazz c = findClassOrNull(name);
        if (c == null)
            throw new NoSuchClassException("class not found: " + name);
        return c;
    }

    @Nonnull
    public Member memberFromGlobalID(int id) {
        assert id % 8 == 0 : id;
        Member member = allMembers.get(id / 8);
        assert member != null : id + ", " + allMembers.size();
        return member;
    }

    public Field fieldFromAddress(Clazz clazz, int address) {
        // ez össze van kavarodva, mert máshol meg a statikusmező j.l.Class-át használjuk a statikusok tárolására
        if (clazz.knownClass == KnownClass.StaticsHolder)
            return (Field) memberFromGlobalID(address);
        return switch (addressingMode) {
            case JS -> (Field) memberFromGlobalID(address);
            case NATIVE_32BIT_UNALIGNED, NATIVE_32BIT_ALIGNED -> {
                int i = addressingMode.objectFieldOffset;
                for (Field f : clazz.allInstanceFieldList) {
                    if (i == address)
                        yield f;
                    i += addressingMode.alignFieldSize(f.type().bytesSize());
                }
                throw new IllegalArgumentException("No field at offset " + address + " in " + clazz);
            }
        };
    }

    public int fieldOffset(Field f) {
        return switch (addressingMode) {
            case JS -> f.globalNumber;
            case NATIVE_32BIT_ALIGNED, NATIVE_32BIT_UNALIGNED -> {
                if (f.isStatic())
                    yield f.globalNumber;
                int i = addressingMode.objectFieldOffset;
                for (Field f2 : f.clazz.allInstanceFieldList) {
                    if (f2 == f)
                        yield i;
                    i += addressingMode.alignFieldSize(f2.type().bytesSize());
                }
                throw new RuntimeException("internal error, field not found in its declaring class: " + f);
            }
        };
    }

    public java.lang.reflect.Method reflect(Method m) {
        try {
            return reflect(m.clazz).getDeclaredMethod(m.name,
                    m.type().parameterTypes().stream().map(this::reflect).toArray(Class[]::new));
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public Class<?> reflect(Type type) {
        return switch (type) {
            case Clazz c -> {
                try {
                    yield Class.forName(c.name.replace('/', '.'));
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
            case PrimitiveType pt -> pt.asClass();
            case ArrayType(Type elementType) -> java.lang.reflect.Array.newInstance(reflect(elementType), 0).getClass();
            default -> throw new IllegalArgumentException(type.toString());
        };
    }

    public static class NoSuchClassException extends RuntimeException {
        public NoSuchClassException(String message) {
            super(message);
        }
    }

    public Clazz findClassOrNull(String name) {
        return findClassOrNull(name, false);
    }

    public Clazz findClassOrNull(String name, boolean onlyBootstrapClasses) {
        if (name.startsWith("["))
            throw new IllegalArgumentException();
        assert name != null;
        Clazz c = classes.get(name);
        if (c == null) {
            // System.out.println("BEGIN LOAD CLASS "+name);
            if (!onlyBootstrapClasses && interpreter != null && interpreter.initialized &&
                    interpreter.shouldLoadViaAppClassLoader(name)) {
                ClassObj co;
                try {
                    co = (ClassObj) interpreter.execute(interpreter.symbols.RuntimeHelper_loadAppClass,
                            interpreter.fromString(name.replace('/', '.'))).orElseThrow();
                } catch (WrappedException e) {
                    if (findClass(KnownClass.ClassNotFoundException).isAssignableFrom(e.obj.type()))
                        return null;
                    else
                        throw e;
                }
                Clazz c2 = (Clazz) interpreter.fromClass(co);
                if (classes.get(name) != c2) {
                    // ez akkor szokott előjönni, ha a név '/'  helyett '.'-okkal van megadva
                    throw new RuntimeException(name+", "+c2+", "+classes.get(name));
                }
                return c2;
            } else {
                c = loadViaBootClassLoader(name);
            }
        }

        return c;
    }

    @Nullable
    private Clazz loadViaBootClassLoader(String name) {
        Clazz c;
        byte[] bytes;
        try (InputStream in = getClass().getResourceAsStream("/" + name + ".class")) {
            if (in == null)
                return null;
            bytes = in.readAllBytes();
        } catch (IOException e) {
            System.out.println("Class " + name + " not found: " + e);
            return null;
        }
        c = defineClass2(name, bytes);
        return c;
    }

    @Nonnull
    private Clazz defineClass2(String name, byte[] bytes) {
        ClassReader classReader;
        Clazz c;
        classReader = new ClassReader(bytes);
        classReader = generateMissingStackMapTable(classReader);
        c = new Clazz(this, classReader);
        c.name = name;
        classes.put(name, c);

        parseOrQueueClass(c);
        return c;
    }

    private void parseOrQueueClass(Clazz c) {
        if (classLoadQueue == null) {
            classLoadQueue = new LinkedList<>();
            classLoadQueue.add(c);
            while (!classLoadQueue.isEmpty()) {
                Clazz c2 = classLoadQueue.remove();
                try {
                    c2.classReader.accept(c2, 0);
                } catch (NoSuchClassException e) {
                    if (c2 == c)
                        throw e;
                    //assert c2.name.equals(name) : name+", "+c2.name;
                    classes.remove(c2.name);
                }
            }
            classLoadQueue = null;
        } else {
            // ha a
            if (c.name.startsWith("java/")) // kéne inkább egy flag arra hogy bootstrap classloaderrel lett-e betöltve
                classLoadQueue.addFirst(c);
            else
                classLoadQueue.addLast(c);
        }
    }

    public void ensureClassLoaded(Clazz clazz) {
        if (!clazz.headerFilled) {
            boolean b = classLoadQueue.remove(clazz);
            assert b;
            clazz.classReader.accept(clazz, 0);
        }
    }

    @Nonnull
    private static ClassReader generateMissingStackMapTable(ClassReader classReader) {
        if (classReader.readShort(6) <= V1_7) {
            ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
            classReader.accept(new ClassVisitor(Opcodes.ASM9, cw) {
                @Override
                public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
                    final MethodVisitor methodWriter = super.visitMethod(access, name, descriptor, signature, exceptions);
                    return new JSRInlinerAdapter(methodWriter, access, name, descriptor, signature, exceptions);
                }
            }, 0);
            classReader = new ClassReader(cw.toByteArray());
            /*
            try {
                Files.write(Path.of("build", "generate_stackmaptable_"+classReader.getClassName().replace('/', '_')+".class"), classReader.b);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
             */
        }
        return classReader;
    }

    public Clazz findClassAndForceParse(String name) {
        Clazz c = findClass(name);
        if (classLoadQueue != null && classLoadQueue.contains(c)) {
            classLoadQueue.remove(c);
            c.classReader.accept(c, 0);
        }
        assert c.allFields != -1 : name + ", " + classLoadQueue;
        return c;
    }

    public Clazz defineClass(byte[] bytes, boolean hidden) {
        /*
        System.out.println(HexFormat.of().formatHex(bytes));
        char[] ch = new String(bytes, StandardCharsets.ISO_8859_1).toCharArray();
        for (int i = 0; i < ch.length; i++)
            if (ch[i] < 32 || ch[i] >= 127)
                ch[i]= '?';
        System.out.println(new String(ch));
         */

        ClassReader classReader = new ClassReader(bytes);
        classReader = generateMissingStackMapTable(classReader);
        Clazz clazz = new Clazz(this, classReader);
        clazz.hidden = hidden;
        String className = classReader.getClassName();
        if (!hidden && classes.putIfAbsent(className, clazz) != null)
            throw new RuntimeException("class already exists: " + className);

        classReader.accept(clazz, 0);
        return clazz;
    }

    public Interpreter interpreter() {
        return interpreter;
    }

    public Node parse(Method method) {
        return new MethodParser(this, method).parse();
    }

    public CompilationResult compile(Method method, TransformationChain transformationChain) {
        if ((method.access & ACC_ABSTRACT) != 0)
            throw new IllegalArgumentException("method is abstract: " + method);

        CompilationCacheKey key = new CompilationCacheKey(method, transformationChain);

        CompilationResult n = compilationCache.get(key);
        if (n == null)
            n = ScopedValue.where(threadLocal, this).execute(() -> {
                if (!compiling.add(key))
                    throw new RuntimeException("already compiling: " + method);
                try {
                    Node node;

                    if (OptPhase1.mustEvaluateCompileTime(method, null)) {
                        String msg = "Compilation disabled for: " + method;
                        node = exceptionThrowerCode(msg, method);
                    } else {
                        Method m = transformationChain.replacementProvider.replacementFor(method);
                        node = (m.access & ACC_NATIVE) != 0
                                ? transformationChain.replacementProvider.nativeMethodImplementation(m)
                                : m.isPolySigMethodSpecialization ? mhInvoker(m) : parse(m);
                    }

                    Node prev = node;

                    MethodCompilationContext mcc = new MethodCompilationContext(this, method, transformationChain);
                    CompilationResult r = optimize(mcc, node);
                    assert r.root != null : prev;
                    compilationCache.put(key, r);
                    return r;
                } finally {
                    compiling.remove(key);
                }
            });
        return n;
    }

    public CompilationResult optimize(MethodCompilationContext mcc, Node node) {
        return optimize(mcc, node, false);
    }

    private CompilationResult optimize(MethodCompilationContext mcc, Node node, boolean failIfNotEmittable) {
        //System.out.println(method +" FROM: "+node);
        List<Transformation> transformations = new ArrayList<>();
        for (TransformationFactory tf : mcc.transformationChain.factories)
            transformations.add(tf.makeTransformation(mcc));
        for (int i = 0; i < 15; i++) {
            // 7 volt, de az kevés volt MH.bindTo PE-jéhez
            // aztán 10 lett, az JS-ben elég volt, de C-ben kevés volt Record::equalshez MH kavarás miatt

            for (Transformation transformation : transformations)
                node = node.transform(transformation);
        }
        for (TransformationFactory tf : mcc.transformationChain.oneTimeTransformations)
            node = node.transform(tf.makeTransformation(mcc));
        for (Transformation transformation : transformations)
            node = node.transform(transformation);

        node.walk(n -> {
            if (n instanceof InvokeNode invoke && OptPhase1.mustEvaluateCompileTime(invoke.method, invoke.location))
                mcc.markAsNonEmittable(invoke,
                        invoke.method.toShortString() +
                                " args are not constant: " + invoke.args + ". Current method: "
                                + mcc.method.toShortString() + ". Invoked at " + invoke.location);
        });

        //System.out.println(method+" TO: "+node);
        List<String> mne = List.copyOf(mcc.methodNotEmittable);
        if (failIfNotEmittable && !mne.isEmpty())
            throw new RuntimeException("internal error: failed to eliminate non-emittable nodes in " + mcc.method +
                    ": " + mne);
        mcc.methodNotEmittable.clear(); // inkább új MCC-t kéne létrehozni

        if (mne.isEmpty()) {
            return new CompilationResult(node, Collections.emptyList(), node);
        } else {
            Cloner cloner = new Cloner(UnaryOperator.identity());
            // főleg a methodidentity miatt lényeges, nem tudná beolvasni a paramétereket ha
            // klónozódna a MethodIdentity-k
            cloner.preserveKeys = true;
            Node n = cloner.clone(node);
            Node throwerCode = replaceNonEmittables(mcc, node);
            return new CompilationResult(n, mne,
                    optimize(mcc, throwerCode, true).emittableRoot());
        }
    }

    @Nonnull
    private Node replaceNonEmittables(MethodCompilationContext mcc, Node node) {
        node = node.transform(new Transformation() {
            @Override
            public Node enter(Node node) {
                if (node != null && node.nonEmittable != null) {
                    return exceptionThrowerCode("Not emittable because " + node.nonEmittable, mcc.method);
                }
                return node;
            }

            @Override
            public Node exit(Node node) {
                return node;
            }
        });
        return node;
    }

    // utility függvény, nem ide való
    public Node exceptionThrowerCode(String msg, Method method) {
        Node node;
        try (var ignored = Location.with(new Location(List.of(new MethodLocationElement(method))))) {
            // TODO AbstractReplacementProviderből másolva

            Clazz exceptionClass = findClass(UnsatisfiedLinkError.class);
            ObjectNode obj = new ObjectNode(new ObjectNode.ObjectIdentity(exceptionClass));
            SequenceNode seq = new SequenceNode(
                    new MethodKey(method),
                    obj,
                    new InvokeSpecialOrStatic(findMethodOrFail(exceptionClass, "<init>",
                            MethodType.parse("(Ljava/lang/String;)V", this)),
                            new Node[]{obj, new ConstantNode(msg)}),
                    new ThrowNode(obj)
            );
            seq.root = true;
            node = seq;
        }
        return node;
    }

    public record CompilationResult(Node root, List<String> notEmittableReasons, Node emittableRoot) {

        public boolean emittable() {
            return notEmittableReasons.isEmpty();
        }
    }

    public static class TransformationChain {

        public final ReplacementProvider replacementProvider;
        public final List<TransformationFactory> factories;
        public final List<TransformationFactory> oneTimeTransformations;

        public TransformationChain(ReplacementProvider replacementProvider,
                                   List<TransformationFactory> factories, List<TransformationFactory> oneTimeTransformations) {
            this.replacementProvider = replacementProvider;
            this.factories = factories;
            this.oneTimeTransformations = oneTimeTransformations;
        }
    }

    public interface TransformationFactory {

        Transformation makeTransformation(MethodCompilationContext context);
    }

    private record CompilationCacheKey(Method method, TransformationChain transformationChain) {
    }

    public CompilationResult alreadyCompiled(Method m) {
        return compilationCache.get(m);
    }

    public boolean isCompiling(Method m, TransformationChain transformationChain) {
        return compiling.contains(new CompilationCacheKey(m, transformationChain));
    }

    private Node mhInvoker(Method m) {
        Interpreter interpreter = interpreter();

        List<Type> fullArgTypes = m.fullArgTypes();
        try (var ignored = Location.with(new Location(List.of(new MethodLocationElement(m))))) {
            if (m.name.equals("invokeBasic")) {
                Node[] args = new Node[fullArgTypes.size() + 1];
                Type[] argTypes = new Type[args.length];
                for (int i = 0; i < fullArgTypes.size(); i++) {
                    Type type = fullArgTypes.get(i);
                    args[i] = new ReadLocalVar(m.parameter(i, m.rootMethodIdentity));
                    argTypes[i] = type;
                }
                LocalVar mhVar = new LocalVar(m, 0, Kind.L, m.rootMethodIdentity);
                ReadLocalVar readlocal_mh = new ReadLocalVar(mhVar);
                GetFieldNode getfield_form = new GetFieldNode(readlocal_mh, interpreter.symbols.MethodHandle_form);
                GetFieldNode getfield_vmentry = new GetFieldNode(getfield_form, interpreter.symbols.LambdaForm_vmentry);
                args[args.length - 1] = getfield_vmentry;
                argTypes[args.length - 1] = findClass(KnownClass.OBJECT);
                Method linkerMethod = findMethodOrFail(findClass(KnownClass.METHOD_HANDLE),
                        "linkToStatic", m.type().withArgTypes(Arrays.asList(argTypes)));
                SequenceNode seq = new SequenceNode(
                        new MethodKey(m), true,
                        readlocal_mh,
                        getfield_form,
                        new InvokeSpecialOrStatic(linkerMethod, args)
                );
                seq.nodes.addAll(2, Arrays.asList(args));
                seq.root = true;
                return seq;
            } else if (m.name.equals("invoke") || m.name.equals("invokeExact") || m.clazz.name.equals("java/lang/invoke/VarHandle")) {
                ClassObj callerClass = null; // csak akkor nézné, ha az átadott type nem MethodType hanem String, de mi MethodType-ot adunk át
                int refKind = H_INVOKEVIRTUAL;
                ClassObj defc = interpreter.fromType(m.clazz);
                ClassObj name = interpreter.fromString(m.name);
                ClassObj type = interpreter.toMethodType(m.type());
                Interpreter.Array appendixResult = interpreter().createArray(new ArrayType(findClass(KnownClass.OBJECT)), 1);

                ClassObj memberName = (ClassObj) interpreter.execute(interpreter.symbols.MethodHandleNatives_linkMethod,
                        callerClass, refKind, defc, name, type, appendixResult).orElseThrow();
                Obj appendix = (Obj) appendixResult.readElement(0);

                if (interpreter.memberNameRefKind(memberName) != H_INVOKESTATIC)
                    throw new UnsupportedOperationException();

                Node[] args = new Node[fullArgTypes.size() + (appendix == null ? 0 : 1)];
                Type[] argTypes = new Type[args.length];
                int localVarIndex = 0;
                for (int i = 0; i < fullArgTypes.size(); i++) {
                    Type argType = fullArgTypes.get(i);
                    args[i] = new ReadLocalVar(new LocalVar(m, localVarIndex, Kind.ofType(argType), m.rootMethodIdentity));
                    localVarIndex += argType.slotSize();
                    argTypes[i] = argType;
                }
                if (appendix != null)
                    args[args.length - 1] = new ConstantNode(interpreter.toConstant(appendix));

                SequenceNode seq = new SequenceNode(new MethodKey(m), true,
                        new InvokeSpecialOrStatic((Method) memberName.representedData(), args)
                );
                seq.nodes.addAll(0, Arrays.asList(args));
                seq.root = true;
                return seq;
            } else if (m.name.startsWith("linkTo")) {
                SequenceNode seq = new SequenceNode(new MethodKey(m));
                ReadLocalVar[] args = new ReadLocalVar[m.fullArgTypes().size()];
                for (int i = 0; i < args.length; i++) {
                    args[i] = new ReadLocalVar(m.parameter(i, m.rootMethodIdentity));
                    seq.nodes.add(args[i]);
                }
                InvokeSpecialOrStatic invokeNode = new InvokeSpecialOrStatic(m, args);
                seq.nodes.add(invokeNode);
                if (m.type().returnType() != PrimitiveType.V)
                    seq.resultSlot.set(invokeNode);
                return seq;
            } else {
                throw new RuntimeException("unknown MH invoker or linker method: " + m);
            }
        }
    }

    public void overriddenMethods(Method overrider, Clazz clazz, Collection<Method> consumer) {
        if (overrider.name.equals("<init>") || (overrider.access & ACC_STATIC) != 0)
            return;

        assert overrider.clazz.allDescendants.contains(clazz) || overrider.clazz == clazz : clazz + ", " + overrider;

        clazz.allMethods().forEach(m -> {
            if (m.clazz != clazz && m != overrider && canOverride(m, overrider))
                consumer.add(m);
        });
    }

    public Type lub(Type a, Type b) {
        if (a == ErrorType.ErrorType)
            a = null;
        if (b == ErrorType.ErrorType)
            b = null;
        if (Objects.equals(a, b))
            return a;
        if (a == null)
            return b;
        if (b == null)
            return a;
        if (a == PrimitiveType.V || b == PrimitiveType.V)
            throw new IllegalArgumentException(a + ", " + b);

        if (a instanceof PrimitiveType p && p.ordinal() < PrimitiveType.I.ordinal())
            a = PrimitiveType.I;
        if (b instanceof PrimitiveType p && p.ordinal() < PrimitiveType.I.ordinal())
            b = PrimitiveType.I;
        if (Objects.equals(a, b)) // int normalizálás után újra
            return a;

        if (a instanceof PrimitiveType || b instanceof PrimitiveType)
            throw new IllegalArgumentException(a + ", " + b);

        while (a != null) {
            Type c = b;
            while (c != null) {
                if (c.equals(a))
                    return c;
                c = c.supertype();
            }
            a = a.supertype();
        }
        // TODO interface-ek
        return null;
    }

    public Method method(KnownClass clazz, String name, Type returnType, Type... paramTypes) {
        return findMethodOrFail(findClass(clazz), name, new MethodType(List.of(paramTypes), returnType));
    }

    public Method findMethodOrFail(Class<?> clazz, String name, MethodType type) {
        return findMethodOrFail(findClass(clazz), name, type);
    }

    public Method findMethodOrFail(Clazz clazz, String name, MethodType type) {
        Method m = findMethodOrNull(clazz, name, type);
        if (m == null)
            throw new IllegalArgumentException("method not exists: " + clazz + "." + name + type);
        return m;
    }

    public Method resolveVirtualMethodOrFail(Method method, Clazz clazz) {
        Method m = resolveVirtualMethodOrNull(method, clazz);
        if (m == null)
            throw new IllegalArgumentException("virtual method resolution failed: " + clazz + "." + method.name + method.type() + " " +
                    "(searching for " + method + ")");
        return m;
    }

    // TODO most ennek megtévesztő a neve, mert ez a superclassokban is keresgél
    public Method findMethodOrNull(Clazz clazz, String name, MethodType type) {
        for (Clazz ancestor : clazz.allAncestorTypesAndThis) {
            Method m = findMethodExcludingOverrides(ancestor, name, type);
            if (m != null)
                return m;
        }
        return null;
    }

    /**
     * ha abscsak abstract metódus van ilyen néven, akkor ez nullt fog visszaadni
     */
    public Method resolveVirtualMethodOrNull(Method method, Clazz clazz) {
        // TODO a sorrend nem felel meg a specnek
        for (Clazz ancestor : clazz.allAncestorTypesAndThis) {
            Method m = findMethodExcludingOverrides(ancestor, method.name, method.type());
            if (m != null && (m.access & ACC_ABSTRACT) == 0 && (m == method || canOverride(method, m)))
                return m;
        }
        return null;
    }

    @Nullable
    public Method findMethodExcludingOverrides(Clazz clazz, String name, MethodType type) {
        Method m2 = resolvePolySigMethod(clazz, name, type);
        if (m2 != null)
            return m2;

        for (MethodNode method : clazz.methods)
            if (method.name.equals(name) && method.desc.equals(type.descriptor()))
                return (Method) method;
        return null;
    }

    public boolean canOverride(Method a, Method c) {
        // 5.4.5. Method Overriding

        //if (!c.clazz.allAncestorTypesAndThis.contains(a.clazz) &&
        //        !(a.clazz.isInterface() && c.clazz.knownClass == KnownClass.OBJECT
        //                /* j.l.Object overrideolhat interfaceekben lévő függvényeket,
        //                 pedig nem szerepelnek az ancestorai közt*/))
        //    throw new IllegalArgumentException(a + ", " + c);

        if ((a.access & ACC_STATIC) != 0 || (c.access & ACC_STATIC) != 0 || a.name.equals("<init>") || a == c)
            return false;

        if (!a.name.equals(c.name) || !a.desc.equals(c.desc))
            return false;

        if ((c.access & ACC_PRIVATE) == ACC_PRIVATE)
            return false;

        if ((a.access & ACC_PUBLIC) == ACC_PUBLIC ||
                (a.access & ACC_PROTECTED) == ACC_PROTECTED)
            return true;

        if ((a.access & (ACC_PUBLIC | ACC_PROTECTED | ACC_PRIVATE)) != 0)
            return false;

        if (isInSamePackage(a.clazz, c.clazz))
            return true;


        // "if mA is declared in a class A and mC is declared in a class C,
        // then there exists a method mB declared in a class B such that C
        // is a subclass of B and B is a subclass of A and mC can override
        // mB and mB can override mA. "

        for (Clazz bClass : c.clazz.allAncestorTypesAndThis) {
            if (bClass == c.clazz || a.clazz.allAncestorTypesAndThis.contains(bClass))
                continue;

            Method b = findMethodExcludingOverrides(bClass, c.name, c.type());
            if (b != null && canOverride(a, b) && canOverride(b, c))
                return true;
        }

        return false;
    }

    private static boolean isInSamePackage(Clazz a, Clazz b) {
        return a.packageName.equals(b.packageName);
    }

    private static final Set<String> POLY_SIG_MH_METHODS = Set.of("invokeExact", "invoke", "invokeBasic",
            "linkToVirtual", "linkToStatic", "linkToSpecial", "linkToInterface", "linkToNative");
    // Stream.of(java.lang.invoke.VarHandle.class.getDeclaredMethods()).filter(m->Arrays.equals(m.getParameterTypes(), new Class[]{Object[].class})).map(m->'"'+m.getName()+"\"").toList()
    private static final Set<String> POLY_SIG_VH_METHODS = Set.of(
            "get", "set", "compareAndSet", "getVolatile", "setVolatile", "getOpaque", "setOpaque",
            "getAcquire", "setRelease",
            "compareAndExchange", "compareAndExchangeAcquire", "compareAndExchangeRelease",
            "weakCompareAndSetPlain", "weakCompareAndSet", "weakCompareAndSetAcquire",
            "weakCompareAndSetRelease",
            "getAndSet", "getAndSetAcquire", "getAndSetRelease", "getAndAdd", "getAndAddAcquire",
            "getAndAddRelease", "getAndBitwiseOr", "getAndBitwiseOrAcquire",
            "getAndBitwiseOrRelease", "getAndBitwiseAnd", "getAndBitwiseAndAcquire",
            "getAndBitwiseAndRelease", "getAndBitwiseXor", "getAndBitwiseXorAcquire",
            "getAndBitwiseXorRelease"
    );

    private final Map<PolySigCacheKey, Method> polySigCache = new HashMap<>();

    private static record PolySigCacheKey(Clazz clazz, String name, MethodType type) {
    }

    public static boolean isPolySigMethod(String className, String methodName) {
        return className.equals("java/lang/invoke/MethodHandle") && POLY_SIG_MH_METHODS.contains(methodName) ||
                className.equals("java/lang/invoke/VarHandle") && POLY_SIG_VH_METHODS.contains(methodName);
    }

    private Method resolvePolySigMethod(Clazz clazz, String name, MethodType type) {
        if (clazz.name.equals("java/lang/invoke/MethodHandle") && POLY_SIG_MH_METHODS.contains(name)) {
            return polySigCache.computeIfAbsent(new PolySigCacheKey(clazz, name, type), __ -> {
                // LambdaForm.Name.isInvokeBasic pl. ellenőrzi néhányról, hog public-e.
                // Tehát ha invokeBasic publickus lenne, akkor InvokerBytecodeGenerator hibát dobna isSelectAlternative assertnél.
                boolean isPublic = name.equals("invoke") || name.equals("invokeExact");
                Method m = new Method(clazz, (isPublic ? ACC_PUBLIC : 0) |
                        (name.startsWith("linkTo") ? ACC_STATIC : ACC_FINAL), name, type.descriptor(), null,
                        new String[]{"java/lang/Throwable"});
                m.isPolySigMethodSpecialization = true;
                return m;
            });
        } else if (clazz.name.equals("java/lang/invoke/VarHandle") && POLY_SIG_VH_METHODS.contains(name)) {
            return polySigCache.computeIfAbsent(new PolySigCacheKey(clazz, name, type), __ -> {
                Method m = new Method(clazz, ACC_PUBLIC | ACC_FINAL, name, type.descriptor(), null,
                        new String[]{});
                m.isPolySigMethodSpecialization = true;
                return m;
            });
        } else
            return null;
    }

    public Field field(Clazz clazz, String name, Type type) {
        Field f = findFieldOrNull(clazz, name, type);
        if (f == null)
            throw new RuntimeException("no such field " + name + " with type " + type.descriptor() + " in " + clazz.name);
        else
            return f;
    }

    public Field findFieldOrNull(Clazz clazz, String name, Type type) {
        for (FieldNode field : clazz.fields)
            if (field.name.equals(name) && field.desc.equals(type.descriptor()))
                return (Field) field;
        for (Clazz supertype : clazz.directSupertypes) {
            // superinterface-ekben is kell keresni, mert statikus fieldek ott is lehetnek
            Field f = findFieldOrNull(supertype, name, type);
            if (f != null)
                return f;
        }
        return null;
    }

    public static CompilationContext context() {
        CompilationContext env = threadLocal.get();
        if (env == null)
            throw new IllegalStateException();
        return env;
    }

    public enum AddressingMode {

        JS(0, 0, 1, 1),
        NATIVE_32BIT_UNALIGNED(8, 12, 4, 1),
        NATIVE_32BIT_ALIGNED(8, 12, 4, 4);

        public final int objectFieldOffset;
        public final int arrayElementOffset;
        public final int pointerSize;
        public final int alignment;

        AddressingMode(int objectFieldOffset, int arrayElementOffset, int pointerSize, int alignment) {
            this.objectFieldOffset = objectFieldOffset;
            this.arrayElementOffset = arrayElementOffset;
            this.pointerSize = pointerSize;
            this.alignment = alignment;
        }

        @SuppressWarnings("PointlessBitwiseExpression")
        public int alignFieldSize(int i) {
            if (i <= 0)
                throw new IllegalArgumentException();
            return (i + alignment - 1) & ~(alignment - 1);
        }
    }

    public static void main(String[] args) throws IOException {
        CompilationContext env = new CompilationContext();
        ScopedValue.where(threadLocal, env).run(() -> {
            env.initialize();
            try (Writer out = Files.newBufferedWriter(Path.of("build/transpiled.js"))) {
                EmissionContext emissionContext = new EmissionContext(env, new JSEmitter(out,
                        new DefaultJSInteropProvider(), false));
                //emissionContext.enqueue(env.findMethodOrNull(env.findClass(C.class), "m4", Type.getMethodType("()I")), false, List.of());
                Method mainMethod = env.findMethodOrNull(env.findClass(CompTest2.class), "proxyTest",
                        new MethodType(List.of(), PrimitiveType.V));
                //Method mainMethod = env.findMethodOrNull(env.findClass(CompTest.class), "main",
                //        new MethodType(List.of(env.findClass(Window.class)), PrimitiveType.V));
                emissionContext.enqueue(mainMethod, false, List.of());
                emissionContext.run();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

//        // switch tesztek
//
//        Interpreter interpreter = new Interpreter(env);
//        emissionContext.enqueue(env.findMethodOrNull(cn, "switchTest2", Type.getMethodType("(I)I")));
//        emissionContext.enqueue(env.findMethodOrNull(cn, "continueInSwitch",
//                Type.getMethodType("()V")));
//        emissionContext.enqueue(env.findMethodOrNull(cn, "nestedSwitch",
//                Type.getMethodType("()V")));
//        emissionContext.enqueue(env.findMethodOrNull(cn, "nestedSwitch2",
//                Type.getMethodType("()V")));
//        emissionContext.enqueue(env.findMethodOrNull(cn, "nestedSwitch3",
//                Type.getMethodType("()V")));
//        emissionContext.enqueue(env.findMethodOrNull(cn, "switchInWhileTrue",
//                Type.getMethodType("()V")));
//        System.out.println(interpreter.execute(env.findMethodOrNull(cn, "nestedSwitch",
//                Type.getMethodType("()V"))));
//        System.out.println(interpreter.execute(env.findMethodOrNull(cn, "stringSwitchTest", Type.getMethodType("(Ljava/lang/String;)Z")),
//                interpreter.fromString("asdf")));
//    }

    public static class C {

        private int i;

        public C() {
        }

        public C(int i) {
            this.i = i;
        }

        public static int m3() {
            C o = new C(54);
            o.i = 23;
            return o.i;
        }

        public static int m() {
            int l = 1;
            for (int i = 0; i < 5; i++) {
                l *= 2;
                if (m2(i))
                    break;
            }
            return l;
        }

        public static boolean m2(int i) {
            return i > 500;
        }

        public static boolean boolAndTest(int a, int b) {
            return a != 0 && b == 3;
        }

        public static boolean boolOrTest(int a, int b) {
            return a != 0 || b == 3;
        }

        static void conditionalWhileLoop() {
            int i = 34;
            while (i < 100) {
                i *= 2;
            }
        }

        static void intSwitchTest() {
            int i;
            switch (23) {
                case 1:
                    i = 234;
                    break;
                case 2:
                    i = 47683;
                    break;
            }
        }


        static void stringSwitchTest() {
            String s = "test";
            switch (s) {
                case "a":
                    s = "1";
                    break;
                case "b":
                    s = "2";
                    break;
            }
        }

        static boolean testSimpleTernary() {
            boolean a = false, b = false;

            return a ? true : false;
        }

        static boolean nestedTernaryOpTest() {
            boolean a = false, b = true;

            return a ? (b ? true : false) : false;
        }

        static boolean nestedBoolOps1(boolean a, boolean b, boolean c) {
            return a && (b || c);
        }

        static boolean nestedBoolOps2(boolean a, boolean b, boolean c) {
            return a || (b && c);
        }

        static boolean eq() {
            return true;
        }

        private static native void printInt(int i);

        public static int m4() {
            List<C> list = new ArrayList<>();
            list.add(new C());
            list.add(new C());
            list.add(new C());
            return list.size();
        }

        private static int switchExprTest(int i) {
            return switch (i) {
                case 3 -> 30;
                case 4 -> 40;
                default -> 50;
            };
        }

        public static void conditionallyContinueOuterLoopWithIncrement() {
            outer:
            for (int i = 0; i < 10; i++) {
                for (int j = 0; j < 20; j++) {
                    if (j == 10)
                        continue outer;
                }
                empty();
            }
        }

        private static void empty() {
        }


        @SuppressWarnings("SwitchStatementWithTooFewBranches")
        public static String ternaryOpInSwitchExpression(int language, boolean OLD_ISO_CODES) {
            return switch (language) {
                case 1 -> OLD_ISO_CODES ? "iw" : "he";
                default -> "asdf";
            };
        }

        public static void whileWithIf() {
            // String.startsWith
            int i = 1, j = 5, k = 10;
            while (i < j)
                if (i++ == k)
                    return;
        }

        private static V chmGet() {
            N[] table = new N[23];
            N[] tab;
            N e, p;
            int n, eh;
            K ek;
            K key = new K();
            int h = 343443;
            if ((tab = table) != null && (n = tab.length) > 0 &&
                    (e = tabAt(tab, (n - 1) & h)) != null) {
                while ((e = e.next) != null) {
                    if (e.hash == h &&
                            ((ek = e.key) == key || (ek != null && key.equals(ek))))
                        return e.val;
                }
            }
            return null;
        }

        private static N tabAt(N[] tab, int b) {
            return tab[12];
        }

        private static class N {
            K key;
            V val;
            N next;
            int hash;
        }

        private static class K {
        }

        private static class V {
        }

        public static boolean stringSwitchTest(String s) {
            return switch (s) {
                case "asdf" -> true;
                default -> throw new RuntimeException();
            };
        }

        public static void continueInSwitch() {
            for (int i = 0; i < 10; i++) {
                switch (23) {
                    case 23:
                        e1();
                        continue;

                    case 43:
                        e2();
                        break;

                    default:
                        e3();
                }
                e4();
            }
        }

        public static int switchTest2(int i) {
            switch (i) {
                case 10:
                    break;
                case 20:
                    return 573;
            }
            return 111;
        }

        public static void nestedSwitch() {
            switch (10) {
                case 10:
                    switch (21) {
                        case 21:
                            e1();
                            break;
                        default:
                            e2();
                    }
                    break;
                default:
                    e3();
                    break;
            }
        }

        public static void nestedSwitch2() {
            switch (10) {
                case 10:
                    switch (21) {
                        case 21:
                            e1();
                            return;
                    }
            }
            e2();
        }

        public static void nestedSwitch3() {
            switch (1000) {
                case 'i':  // non CDATA normalization
                    switch (123) {
                        case 10:
                            e1();
                            return;
                    }
                    break;

                case 'c':  // CDATA normalization
                    e2();
                    break;
            }
        }

        public static void switchInWhileTrue() {
            //jdk/internal/util/xml/impl/Parser.bntok

            while (true) {
                switch (234) {
                    case 100:
                        break;

                    case 200:
                        e2();

                    default:
                        e3();
                        return;
                }
            }
        }

        private static boolean b1() {
            return false;
        }

        private static boolean b2() {
            return false;
        }

        private static void e1() {
        }

        private static void e2() {
        }

        private static void e3() {
        }

        private static void e4() {
        }
    }
}
