package com.flyordie.code;

import com.flyordie.code.Clazz.Field;
import com.flyordie.code.Clazz.Method;
import com.flyordie.code.Type.ArrayType;
import com.flyordie.code.Type.PrimitiveType;
import com.flyordie.code.Type.ReferenceType;
import ui11.reflectutil.ReflectionUtil;
import org.objectweb.asm.*;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TryCatchBlockNode;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.lang.StackWalker.Option;
import java.lang.constant.ClassDesc;
import java.lang.constant.MethodTypeDesc;
import java.lang.invoke.*;
import java.lang.invoke.MethodHandles.Lookup;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static java.lang.invoke.MethodType.methodType;
import static org.objectweb.asm.Opcodes.*;
import static org.objectweb.asm.Type.METHOD;

// TODO NoSuchMethodErrorok kezelése (pl. MDC::bwCompatibleGetMDCAdapterFromBinder)

// úgy tűnik hogy ClassCircularityErrort a javadoccal ellentétben nem csak superclass hierarchia cirkularitáskor
// dob OpenJDK, hanem akkor is ha egy osztály betöltése közben másik osztályt kezdünk betölteni.
// TODO ezt be kéne jelenteni nekik, mert hibás így a spec

public class NewInterpreter extends Interpreter {

    private static final boolean SAVE_MIRROR_CLASSES = false; // 11655 fájlt írt ki 2023-11-17-ben
    private static final boolean FRAME_DEBUG = false;

    private static final String CLASS_NAME_PREFIX = "com/flyordie/code/isolated_env/";
    private static final String METHOD_NAME_PREFIX = "m_";
    private static final String FIELD_NAME_PREFIX = "f_";
    private static final String BRIDGE_CLASS_NAME = "com/flyordie/code/IsolatedEnvironmentBridge";
    private static final String BRIDGE_FIELD_NAME = "holder";
    private static final String BRIDGE_FIELD_DESC = NewInterpreter.class.descriptorString();
    private static final String NEWINTERPRETER_NAME = ReflectionUtil.internalName(NewInterpreter.class);
    private static final String TYPEN2J_DESC = methodType(Type.class, Class.class).descriptorString();
    private static final String RuntimeObjectBase = ReflectionUtil.internalName(RuntimeObjectBase.class);
    private static final String ThrowableBase = ReflectionUtil.internalName(ThrowableBase.class);
    private static final String ClassObj = ReflectionUtil.internalName(ClassObj.class);
    private static final String Array = ReflectionUtil.internalName(Array.class);
    private static final String Obj = ReflectionUtil.internalName(Obj.class);
    private static final String ArrayType_name = ReflectionUtil.internalName(Type.ArrayType.class);
    private static final String Clazz_name = ReflectionUtil.internalName(Clazz.class);
    private static final String PRIM_MULTIDIM_ARR_PREFIX = CLASS_NAME_PREFIX + "$PrimMultiDim";
    private static final String ARRAY_CLASS_NAME_SUFFIX = "$$Array";
    private static final String ARRAY_IMPL_NAME_SUFFIX = "Impl";
    private static final String ROOT_INTERFACE_NAME = CLASS_NAME_PREFIX + "java/lang/Object";
    private static final String ROOT_INTERFACE_IMPL_NAME = ROOT_INTERFACE_NAME + "$$Impl";
    private static final String ROOT_TYPE_ARRAY_VARIANT = CLASS_NAME_PREFIX + "java/lang/Object$$ArrayVariant";

    private static final java.lang.invoke.MethodType SETTER_METHOD_TYPE = methodType(void.class, Object.class);
    private static final java.lang.invoke.MethodType GETTER_METHOD_TYPE = methodType(Object.class);
    public static final ArrayType BYTE_ARRAY = new ArrayType(PrimitiveType.B);

    private final MethodHandles.Lookup lookup;
    private final ClassLoaderImpl classLoader;

    private final Map<Type, Class<?>> classMirrors = new HashMap<>();
    private final Map<Class<?>, Type> mirrorClassesReverse = new HashMap<>();
    private final Map<MethodID, Method> mirrorMethodsReverse = new HashMap<>();
    private final Map<String, Type> mirrorClassesReverse2 = new HashMap<>();
    private final Map<Clazz, MethodHandle> allocatorCache = new HashMap<>();
    private final Map<ArrayType, MethodHandle> arrayAllocatorCache = new HashMap<>();
    private final Map<Method, MethodHandle> methodCache = new HashMap<>();
    private final Map<Field, MethodHandle> staticGetterCache = new HashMap<>();
    private final Map<Field, MethodHandle> staticSetterCache = new HashMap<>();
    private final Map<Clazz, String> hiddenClassNames = new HashMap<>();

    private MethodHandle mnResolutionFieldSetter, mnResolutionFieldGetter;
    private MethodHandle byteArrayNativeArrayGetter;

    public NewInterpreter(CompilationContext compContext) {
        super(compContext);
        classLoader = new ClassLoaderImpl();
        lookup = classLoader.createLookup();

        ClassWriter cw = new ClassWriter(0);
        String superclass = Statics.class.getName().replace('.', '/');
        cw.visit(Opcodes.V19, ACC_PUBLIC, "com/flyordie/code/NewInterpreter$StaticsImpl", null,
                superclass, new String[]{ROOT_INTERFACE_NAME});
        MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitMethodInsn(INVOKESPECIAL, superclass, "<init>", "()V", false);
        mv.visitMaxs(1, 1);
        mv.visitInsn(RETURN);
        try {
            statics = (Statics)
                    lookup.defineClass(cw.toByteArray()).getDeclaredConstructor().newInstance();
            statics.interpreter = this;
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    private ExecutionResult toInnerException(Throwable e) {
        throw new RuntimeException("Exception inside evaluate initialization code: " + e, e); // TODO
    }

    @Override
    public ExecutionResult execute(Method method, Object... args) {
        if (method.clazz.name.equals("sun/awt/windows/WToolkit"))
            throw new RuntimeException();
        MethodHandle mh = methodCache.computeIfAbsent(method, m -> {
            MethodHandle h = findGeneratedMethod(m);
            h = paramsBoolToInt(h.type(), h, MH_intToBool_erasedParam);
            h = h.asSpreader(Object[].class, h.type().parameterCount());
            h = h.asType(h.type().changeReturnType(Object.class));
            return h;
        });
        Object value;
        try {
            value = mh.invokeExact(args);
        } catch (WrappedException | Error e) {
            throw e;
        } catch (Throwable e) {
            throw new RuntimeException("Exception thrown while interpreting " + method + ": " + e, e);
        }
        if (value instanceof Boolean b)
            value = b ? 1 : 0;
        return new ExecutionResult(value, null);
    }

    private MethodHandle findGeneratedMethod(Method method) {
        try {
            java.lang.invoke.MethodType methodType = g2nMethodType(method);
            Class<?> c = (Class<?>) typeJ2N(method.clazz, true).resolveConstantDesc(lookup);
            if (method.isStatic()) {
                return lookup.findStatic(c, methodNameJ2N(method.name, method.clazz), methodType);
            } else {
                Lookup lookup = MethodHandles.privateLookupIn(c, this.lookup);
                return lookup.findSpecial(c, methodNameJ2N(method.name, method.clazz), methodType, c);
            }
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    private java.lang.invoke.MethodType g2nMethodType(Method method) throws ReflectiveOperationException {
        return (java.lang.invoke.MethodType) g2n(method.type()).resolveConstantDesc(lookup);
    }

    private MethodHandle findGeneratedMethod_virtual(Method method) {
        try {
            java.lang.invoke.MethodType methodType = g2nMethodType(method);
            Class<?> c = (Class<?>) typeJ2N(method.clazz, true).resolveConstantDesc(lookup);
            return lookup.findVirtual(c, methodNameJ2N(method.name, method.clazz), methodType);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected List<CallFrame> stackFramesImpl(boolean forException) {
        return StackWalker.getInstance(Set.of(Option.RETAIN_CLASS_REFERENCE)).walk(nativeFrames -> {
            record CallFrameImpl(Method method) implements CallFrame {
            }
            return nativeFrames.<CallFrame>map(nativeFrame -> {
                Type type = mirrorClassesReverse.get(nativeFrame.getDeclaringClass());
                // TODO csak egy bizonyos frame-ig kéne lemenni
                if (type != null) {
                    MethodID mid = new MethodID((Clazz) type,
                            nativeFrame.getMethodName(), nativeFrame.getMethodType().descriptorString());
                    Method method = mirrorMethodsReverse.get(mid);
                    if (method == null)
                        throw new RuntimeException("no guest method found for " + mid);
                    return new CallFrameImpl(method);
                } else
                    return null;
            }).filter(Objects::nonNull).skip(1).toList();

            // skip(1) azért kell, hogy konzisztensek legyünk OldInterpreterrel, ahol most nincsenek benne a
            // call stackben a natív függvények. ha majd ott benne lesznek, akkor itt is ki kell szedni
            // (és pl. Reflection.getCallerClass implementációját megváltoztatni. )
        });
    }

    @Override
    public ExecutionResult ensureInitialized(Clazz clazz) {
        try {
            lookup.ensureInitialized(typeJ2NClass(clazz));
            return null;
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        } catch (ExceptionInInitializerError e) {
            return toInnerException(e);
        }
    }

    @Override
    public boolean isInitialized(Clazz clazz) {
        try {
            return ((Class<?>) typeJ2N(clazz, true).resolveConstantDesc(lookup)).getDeclaredField("clinitBegan").getBoolean(null);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean isFullyInitialized(Clazz clazz) {
        try {
            return ((Class<?>) typeJ2N(clazz, true).resolveConstantDesc(lookup)).getDeclaredField("clinitFinished").getBoolean(null);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void writeStaticField(Field f, Object value) {
        MethodHandle mh = staticSetterCache.computeIfAbsent(f, m -> {
            MethodHandle h;
            try {
                h = lookup.findStaticSetter((Class<?>) typeJ2N(f.clazz, true).resolveConstantDesc(lookup), FIELD_NAME_PREFIX + m.name,
                        (Class<?>) typeJ2N(f.type(), false).resolveConstantDesc(lookup));
            } catch (ReflectiveOperationException e) {
                throw new RuntimeException(e);
            }
            h = h.asType(SETTER_METHOD_TYPE);
            return h;
        });
        try {
            mh.invokeExact(value);
        } catch (RuntimeException | Error e) {
            throw e;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Object readStaticField(Field f) {
        if ((f.access & ACC_STATIC) == 0)
            throw new RuntimeException("not a static field: " + f);
        MethodHandle mh = staticGetterCache.computeIfAbsent(f, m -> {
            MethodHandle h;
            try {
                h = lookup.findStaticGetter((Class<?>) typeJ2N(f.clazz, true).resolveConstantDesc(lookup),
                        FIELD_NAME_PREFIX + m.name,
                        (Class<?>) typeJ2N(f.type(), false).resolveConstantDesc(lookup));
            } catch (ReflectiveOperationException e) {
                throw new RuntimeException(e);
            }
            h = h.asType(GETTER_METHOD_TYPE);
            return h;
        });
        try {
            return mh.invokeExact();
        } catch (RuntimeException | Error e) {
            throw e;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("ConfusingArgumentToVarargsMethod")
    // IntelliJ bug, nem tudja hogy polysig methodnál egyértelmű a null
    @Override
    public ClassObj createObject(Clazz clazz) {
        Objects.requireNonNull(clazz);
        MethodHandle mh = allocatorCache.computeIfAbsent(clazz, c -> {
            MethodHandle h;
            try {
                h = lookup.findConstructor((Class<?>) typeJ2N(c, true).resolveConstantDesc(lookup),
                        methodType(void.class, classLoader.bridgeClass));
            } catch (ReflectiveOperationException e) {
                throw new RuntimeException(e);
            }
            h = h.asType(methodType(ClassObj.class, Void.class));
            return h;
        });
        try {
            return (ClassObj) mh.invokeExact(null);
        } catch (RuntimeException | Error e) {
            throw e;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Array createArray(ArrayType arrayType, int length) {
        Objects.requireNonNull(arrayType);
        MethodHandle mh = arrayAllocatorCache.computeIfAbsent(arrayType, c -> {
            MethodHandle h;
            try {
                h = lookup.findConstructor((Class<?>) typeJ2N(c, true).resolveConstantDesc(lookup), methodType(void.class, int.class));
            } catch (ReflectiveOperationException e) {
                throw new RuntimeException(e);
            }
            h = h.asType(h.type().changeReturnType(Array.class));
            return h;
        });
        try {
            return (Array) mh.invokeExact(length);
        } catch (RuntimeException | Error e) {
            throw e;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    private byte[] generateMirrorClassfile(Clazz clazz, String className) {
        ClassWriter cw = new ClassWriter(ASM9);
        if (className == null)
            className = classNameJ2N(clazz, true);
        assert className.equals(ROOT_TYPE_ARRAY_VARIANT) || className.equals(classNameJ2N(clazz, true));

        boolean isRoot = clazz.superclass == null;
        boolean implementsRootInterface = clazz.isInterface() || isRoot;
        String implementedObjSubinterface = null;
        if (implementsRootInterface)
            implementedObjSubinterface = className.equals(ROOT_TYPE_ARRAY_VARIANT) ? Array : ClassObj;

        String[] interfaces;
        if (clazz.interfaces == null)
            interfaces = implementsRootInterface ?
                    new String[]{ROOT_INTERFACE_NAME, implementedObjSubinterface,
                            "java/lang/Cloneable"} : new String[]{"java/lang/Cloneable"};
        else {
            interfaces = new String[clazz.interfaces.size() + (implementsRootInterface ? 3 : 1)];
            int i = 0;
            for (; i < clazz.interfaces.size(); i++)
                interfaces[i] = classNameJ2N(clazz.superinterfaces.get(i), false);
            if (implementsRootInterface) {
                interfaces[i++] = ROOT_INTERFACE_NAME;
                interfaces[i++] = implementedObjSubinterface;
            }
            interfaces[i++] = "java/lang/Cloneable";
        }
        boolean specialSuperclass = false;
        String superclass;
        if (clazz.isInterface())
            superclass = "java/lang/Object";
        else {
            if (clazz.superName == null) {
                specialSuperclass = true;
                superclass = RuntimeObjectBase;
            } else if (clazz.name.equals("java/lang/Throwable")) {
                specialSuperclass = true;
                superclass = ThrowableBase;
            } else
                superclass = classNameJ2N(clazz.superclass, true);
        }

        cw.visit(Opcodes.V17, ACC_PUBLIC | (clazz.access & (ACC_INTERFACE | ACC_ABSTRACT)),
                className, null,
                superclass, interfaces);
        cw.visitSource(clazz.sourceFile, clazz.sourceDebug);

        org.objectweb.asm.tree.MethodNode clinit = new MethodNode(ACC_PUBLIC | ACC_STATIC, "<clinit>", "()V", null, null);
        clinit.visitInsn(ICONST_1);
        clinit.visitFieldInsn(PUTSTATIC, className, "clinitBegan", "Z");

        for (FieldNode f : clazz.fields) {
            String fieldName = FIELD_NAME_PREFIX + f.name;
            ClassDesc fieldType = typeJ2N(((Field) f).type(), false);
            int mod = f.access & (ACC_STATIC | ACC_VOLATILE);
            if ((f.access & ACC_FINAL) != 0)
                if (clazz.isInterface())
                    mod |= ACC_FINAL;
                else
                    mod |= ACC_VOLATILE;
            cw.visitField(ACC_PUBLIC | mod,
                    fieldName, fieldType.descriptorString(), null, null);
            if ((f.access & ACC_STATIC) != 0) {
                Object c = convertConstant(f.value, clazz.lookup);
                if (c != null) {
                    clinit.visitLdcInsn(c);
                    clinit.visitFieldInsn(PUTSTATIC, className, fieldName, fieldType.descriptorString());
                }
            } else {
                // 4.7.2. Otherwise, the Java Virtual Machine must silently ignore the attribute.
            }
        }
        cw.visitField(ACC_PUBLIC | ACC_STATIC | ACC_FINAL, "clinitBegan", "Z", null, null);
        cw.visitField(ACC_PUBLIC | ACC_STATIC | ACC_FINAL, "clinitFinished", "Z", null, null);
        if (clazz.knownClass == KnownClass.MEMBER_NAME)
            // ld. mnResolutionGetter és mnResolutionSetter
            cw.visitField(ACC_PUBLIC, "nativeMH", "Ljava/lang/invoke/MethodHandle;", null, null);

        if (clazz.name.equals("java/lang/Throwable")) {
            Clazz objClass = compContext.findClass(KnownClass.OBJECT);
            for (MethodNode m : objClass.methods)
                if ((m.access & ACC_STATIC) == 0 && (m.name.equals("<init>") ||
                        clazz.methods.stream().noneMatch(throwableMethod -> throwableMethod.name.equals(m.name)
                                && throwableMethod.desc.equals(m.desc)))) {
                    String nname = methodNameJ2N(m.name, objClass);
                    String ntype = g2n(((Method) m).type()).descriptorString();
                    MethodVisitor mv = cw.visitMethod(ACC_PUBLIC | m.access & ACC_ABSTRACT,
                            nname, ntype, null, null);
                    handleMethod(className, (Method) m, mv);
                    mirrorMethodsReverse.put(new MethodID(clazz, nname, ntype), (Method) m);
                }
        }

        boolean hasUserDefinedClinit = false;
        for (MethodNode methodNode : clazz.methods) {
            Method m = (Method) methodNode;

            String methodNativeName;
            MethodVisitor target;

            if (m.visibleAnnotations != null) {
                boolean isPolySig = false;
                for (int i = 0; i < m.visibleAnnotations.size(); i++)
                    if (m.visibleAnnotations.get(i).desc.equals("Ljava/lang/invoke/MethodHandle$PolymorphicSignature;")) {
                        isPolySig = true;
                        break;
                    }
                if (isPolySig)
                    continue;
            }

            if (m.name.equals("<clinit>")) {
                hasUserDefinedClinit = true;
                target = clinit;
                methodNativeName = null;
                mirrorMethodsReverse.put(new MethodID(clazz, "<clinit>", "()V"), m);
            } else {
                methodNativeName = methodNameJ2N(m.name, clazz);
                String methodNativeType = g2n(((Method) m).type()).descriptorString();
                target = cw.visitMethod(ACC_PUBLIC | m.access & (ACC_STATIC | ACC_ABSTRACT),
                        methodNativeName, methodNativeType, null, null);
                mirrorMethodsReverse.put(new MethodID(clazz, methodNativeName, methodNativeType), m);
            }

            handleMethod(className, (Method) m, target);

            if (m.name.equals("<init>")) {
                MethodType desc2 = m.type().withReturnType(clazz);
                MethodVisitor mw = cw.visitMethod(ACC_PUBLIC | ACC_STATIC,
                        "allocAndInit", g2n(desc2).descriptorString(), null, null);

                mw.visitTypeInsn(NEW, className);
                mw.visitInsn(DUP);
                mw.visitInsn(ACONST_NULL);
                mw.visitMethodInsn(INVOKESPECIAL, className, "<init>",
                        "(L" + BRIDGE_CLASS_NAME + ";)V", false);
                mw.visitInsn(DUP);

                int argNum = 0;
                for (Type t : ((Method) m).type().parameterTypes()) {
                    if (t instanceof PrimitiveType prim) {
                        switch (prim) {
                            case F -> mw.visitVarInsn(FLOAD, argNum);
                            case D -> mw.visitVarInsn(DLOAD, argNum);
                            case J -> mw.visitVarInsn(LLOAD, argNum);
                            default -> mw.visitVarInsn(ILOAD, argNum);
                        }
                    } else
                        mw.visitVarInsn(ALOAD, argNum);
                    argNum += t.slotSize();
                }
                mw.visitMethodInsn(INVOKEVIRTUAL, className, methodNativeName, g2n(m.type()).descriptorString(), false);
                mw.visitInsn(ARETURN);
                mw.visitMaxs(3 + argNum, argNum);
            }
        }

        if (!hasUserDefinedClinit) {
            clinit.visitFrame(F_SAME, 0, null, 0, null);
            clinitEnd(clinit, className);
            clinit.visitInsn(RETURN);
            clinit.visitMaxs(2, 0); // 2 kell, mert lehet hogy long/double egy field
        }

        clinit.accept(cw);

        if (!clazz.isInterface()) {
            MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "<init>", "(L" + BRIDGE_CLASS_NAME + ";)V", null, null);
            mv.visitVarInsn(ALOAD, 0);
            if (!specialSuperclass)
                mv.visitVarInsn(ALOAD, 1);
            mv.visitMethodInsn(INVOKESPECIAL, superclass, "<init>",
                    specialSuperclass ? "()V" : "(L" + BRIDGE_CLASS_NAME + ";)V", false);
            mv.visitInsn(RETURN);
            mv.visitMaxs(specialSuperclass ? 1 : 2, 2);

            String typeClass = className.equals(ROOT_TYPE_ARRAY_VARIANT) ? ArrayType_name : Clazz_name;

            mv = cw.visitMethod(ACC_PUBLIC, "type", "()L" + typeClass + ";", null, null);
            mv.visitFieldInsn(GETSTATIC, BRIDGE_CLASS_NAME, BRIDGE_FIELD_NAME, BRIDGE_FIELD_DESC);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Object", "getClass", "()Ljava/lang/Class;", false);
            mv.visitMethodInsn(INVOKEVIRTUAL, NEWINTERPRETER_NAME, "typeN2J", TYPEN2J_DESC, false);
            mv.visitTypeInsn(CHECKCAST, typeClass);
            mv.visitInsn(ARETURN);
            mv.visitMaxs(2, 1);

            mv = cw.visitMethod(ACC_PUBLIC, "readField", "(I)Ljava/lang/Object;", null, null);
            Label[] labels = new Label[clazz.allInstanceFields];
            for (int i = 0; i < labels.length; i++)
                labels[i] = new Label();
            Label defaultCase = new Label();
            if (labels.length != 0) {
                mv.visitVarInsn(ALOAD, 0);
                mv.visitVarInsn(ILOAD, 1);
                mv.visitTableSwitchInsn(0, labels.length - 1, defaultCase, labels);
                for (int i = 0; i < labels.length; i++) {
                    mv.visitLabel(labels[i]);
                    mv.visitFrame(F_FULL, 2, new Object[]{className, INTEGER},
                            1, new Object[]{className});
                    Field field = clazz.allInstanceFieldList.get(i);
                    mv.visitFieldInsn(GETFIELD, className, FIELD_NAME_PREFIX + field.name, typeJ2N(field.type(), false).descriptorString());
                    if (field.type() instanceof PrimitiveType primitiveType)
                        mv.visitMethodInsn(INVOKESTATIC, primitiveType.wrapperName(),
                                "valueOf", primitiveType.boxMethodDesc(), false);
                    mv.visitInsn(ARETURN);
                }
                mv.visitLabel(defaultCase);
                mv.visitFrame(F_FULL, 2, new Object[]{className, INTEGER},
                        1, new Object[]{className});
            }
            mv.visitTypeInsn(NEW, "java/lang/IllegalArgumentException");
            mv.visitInsn(DUP);
            mv.visitMethodInsn(INVOKESPECIAL, "java/lang/IllegalArgumentException", "<init>", "()V", false);
            mv.visitInsn(ATHROW);
            mv.visitMaxs(3, 2);


            mv = cw.visitMethod(ACC_PUBLIC, "writeField", "(ILjava/lang/Object;)V", null, null);
            for (int i = 0; i < labels.length; i++)
                labels[i] = new Label();
            defaultCase = new Label(); // visitFrame összezavarodott hogyha a fent használt labeleket is használtam

            if (labels.length != 0) {
                mv.visitVarInsn(ALOAD, 0);
                mv.visitVarInsn(ALOAD, 2);
                mv.visitVarInsn(ILOAD, 1);
                mv.visitTableSwitchInsn(0, labels.length - 1, defaultCase, labels);
                for (int i = 0; i < labels.length; i++) {
                    mv.visitLabel(labels[i]);
                    mv.visitFrame(F_FULL, 3, new Object[]{className, INTEGER, "java/lang/Object"},
                            2, new Object[]{className, "java/lang/Object"});
                    Field field = clazz.allInstanceFieldList.get(i);
                    assert !field.isStatic();
                    if (field.type() instanceof PrimitiveType primitiveType) {
                        mv.visitTypeInsn(CHECKCAST, primitiveType.wrapperName());
                        mv.visitMethodInsn(INVOKEVIRTUAL, primitiveType.wrapperName(),
                                primitiveType.unboxMethodName(), "()" + primitiveType, false);
                    } else {
                        mv.visitTypeInsn(CHECKCAST, referenceTypeName(typeJ2N(field.type(), false)));
                    }
                    mv.visitFieldInsn(PUTFIELD, className, FIELD_NAME_PREFIX + field.name, typeJ2N(field.type(), false).descriptorString());
                    mv.visitInsn(RETURN);
                }
                mv.visitLabel(defaultCase);
                mv.visitFrame(F_FULL, 3, new Object[]{className, INTEGER, "java/lang/Object"},
                        2, new Object[]{className, "java/lang/Object"});
            }
            mv.visitTypeInsn(NEW, "java/lang/IllegalArgumentException");
            mv.visitInsn(DUP);
            mv.visitMethodInsn(INVOKESPECIAL, "java/lang/IllegalArgumentException", "<init>", "()V", false);
            mv.visitInsn(ATHROW);
            mv.visitMaxs(4, 3);

            mv = cw.visitMethod(ACC_PUBLIC, "cloneObject", "()L" + ClassObj + ";", null, null);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "clone", "()Ljava/lang/Object;", false);
            mv.visitTypeInsn(CHECKCAST, ClassObj);
            mv.visitInsn(ARETURN);
            mv.visitMaxs(1, 1);
        }

        return cw.toByteArray();
    }

    private void handleMethod(String className, Method m, MethodVisitor target) {
        if ((m.access & ACC_NATIVE) != 0 || intrinsics.get(m) != null) {
            int var = 0;
            for (Type paramType : m.fullArgTypes()) {
                assert paramType != PrimitiveType.V;
                target.visitVarInsn(ILOAD + basicTypeOffset(paramType), var);
                var += paramType.slotSize();
            }
            MethodType type = m.type();
            if (!m.isStatic())
                type = type.prependArg(m.clazz);
            target.visitInvokeDynamicInsn(m.name.startsWith("<") ? m.name.substring(1, m.name.length() - 1) : "$" + m.name,
                    g2n(type).descriptorString(), new Handle(H_INVOKESTATIC, NEWINTERPRETER_NAME,
                            "nativeCallBSM", "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/Class;I)Ljava/lang/invoke/CallSite;",
                            false), org.objectweb.asm.Type.getObjectType(className), (m.access & ACC_STATIC) == 0 ? 0 : 1);
            target.visitInsn(IRETURN + basicTypeOffset(m.type().returnType()));
            int maxStack = Math.max(m.type().returnType().slotSize(), var);
            if (m.name.equals("<clinit>"))
                maxStack = Math.max(2, maxStack); // ld. MethodConverter konstruktora
            target.visitMaxs(maxStack, var);
        } else if ((m.access & ACC_ABSTRACT) == 0) {
            m.instructions.get(0);
            try {
                m.accept(new MethodConverter(target, m, className));
            } catch (RuntimeException e) {
                throw new RuntimeException("cannot convert array operations in " + m + ": " + e, e);
            }
        }
    }

    private static int basicTypeOffset(Type type) {
        if (type instanceof PrimitiveType prim)
            return switch (prim) {
                case J -> 1;
                case F -> 2;
                case D -> 3;
                case V -> 5;
                default -> 0;
            };
        else
            return 4;
    }


    private static int arrayOpOffset(Type type) {
        if (type instanceof PrimitiveType prim)
            return switch (prim) {
                case I -> 0;
                case J -> 1;
                case F -> 2;
                case D -> 3;
                case B, Z -> 5;
                case C -> 6;
                case S -> 7;
                default -> throw new IllegalArgumentException(type.toString());
            };
        else
            return 4;
    }

    private byte[] generateRootInterfaceForClass(Clazz clazz) {
        ClassWriter cw = new ClassWriter(0);
        cw.visit(V17, ACC_PUBLIC | ACC_INTERFACE | ACC_ABSTRACT, ROOT_INTERFACE_NAME, null, "java/lang/Object",
                new String[]{Obj});

        for (MethodNode m : clazz.methods) {
            if ((m.access & ACC_STATIC) != 0 || m.name.equals("<clinit>"))
                continue;

            cw.visitMethod(ACC_PUBLIC | ACC_ABSTRACT,
                    methodNameJ2N(m.name, clazz), g2n(((Method) m).type()).descriptorString(), null, null);
        }

        return cw.toByteArray();
    }

    private void clinitEnd(MethodVisitor out, String thisClassName) {
        out.visitInsn(ICONST_1);
        out.visitFieldInsn(PUTSTATIC, thisClassName, "clinitFinished", "Z");
    }

    private Object convertConstant(Object obj, Type.Lookup lookup) {
        if (obj instanceof org.objectweb.asm.Type t)
            return typeCondy(t, lookup);
        else if (obj instanceof Handle h)
            return mhCondy(h);
        else if (obj instanceof ConstantDynamic condy) {
            Object[] bsmArgs = new Object[condy.getBootstrapMethodArgumentCount()];
            for (int i = 0; i < bsmArgs.length; i++)
                bsmArgs[i] = convertConstant(condy.getBootstrapMethodArgument(i), lookup);
            return new ConstantDynamic(condy.getName(), g2n(MethodType.parse(condy.getDescriptor(), lookup)).descriptorString(),
                    convertMH(condy.getBootstrapMethod(), lookup), bsmArgs);
        } else if (obj instanceof String s) {
            return new ConstantDynamic("str", "L" + CLASS_NAME_PREFIX + "java/lang/String;",
                    new Handle(H_INVOKESTATIC, NEWINTERPRETER_NAME, "stringBSM",
                            "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/Class;Ljava/lang/String;)Ljava/lang/Object;",
                            false), s);
        } else if (obj instanceof MethodType t) {
            // BSM argokban lehet hogy vannak már feldoldozottak, bár nem értem
            // hogy miért.
            return typeCondy(org.objectweb.asm.Type.getType(t.descriptor()), lookup);
        } else if (obj instanceof Type t) {
            // ld. komment MethodType-nál
            return typeCondy(org.objectweb.asm.Type.getType(t.descriptor()), lookup);
        } else {
            assert obj == null || obj instanceof Number;
            return obj;
        }
    }

    /**
     * reference, array és method típusokra működik, primitívekre nem
     */
    @Nonnull
    private ConstantDynamic typeCondy(org.objectweb.asm.Type t, Type.Lookup lookup) {
        String objType = t.getSort() == METHOD
                ? "L" + CLASS_NAME_PREFIX + "java/lang/invoke/MethodType;"
                : "L" + CLASS_NAME_PREFIX + "java/lang/Class;";

        String typeDescriptor = switch (t.getSort()) {
            case METHOD -> g2n(MethodType.parse(t.getDescriptor(), lookup)).descriptorString();
            default -> typeJ2N(Type.parse(t.getDescriptor(), lookup), false).descriptorString();
        };

        return new ConstantDynamic("clazz", objType,
                new Handle(H_INVOKESTATIC, NEWINTERPRETER_NAME, "typeConstantBSM",
                        "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/Class;Ljava/lang/Object;)Ljava/lang/Object;",
                        false), org.objectweb.asm.Type.getType(typeDescriptor));
    }

    private ConstantDynamic mhCondy(Handle h) {
        return new ConstantDynamic(h.getName(), "L" + CLASS_NAME_PREFIX + "java/lang/invoke/MethodHandle;",
                new Handle(H_INVOKESTATIC, NEWINTERPRETER_NAME, "mhBSM",
                        "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/Class;ILjava/lang/String;Ljava/lang/String;)Ljava/lang/Object;",
                        false), h.getTag(), h.getOwner(), h.getDesc());
    }

    private Handle convertMH(Handle h, Type.Lookup lookup) {
        Clazz owner = lookup.findClass(h.getOwner());
        if (h.getTag() == H_NEWINVOKESPECIAL) {
            if (!h.getName().equals("<init>"))
                throw new RuntimeException();
            MethodTypeDesc type = g2n(MethodType.parse(h.getDesc(), lookup));
            type = type.changeReturnType(typeJ2N(owner, false));
            return new Handle(H_INVOKESTATIC, classNameJ2N(owner, true),
                    "allocAndInit", type.descriptorString(), false);
        } else
            return new Handle(h.getTag(), classNameJ2N(owner, h.getTag() == H_INVOKESTATIC || h.getTag() <= 4),
                    methodNameJ2N(h.getName(), ReferenceType.ofReferenceTypeName(h.getOwner(), lookup)),
                    g2n(MethodType.parse(h.getDesc(), lookup)).descriptorString(), h.isInterface());
    }


    private byte[] generateArrayInterface(ArrayType arrayType, String className) {
        List<String> superinterfaces = new ArrayList<>();
        superinterfaces.add(Array.class.getName().replace('.', '/'));
        superinterfaces.add(referenceTypeName(typeJ2N(arrayType.supertype(), false)));
        if (arrayType.endingElementType() instanceof Clazz clazz) {
            for (Clazz superinterface : clazz.superinterfaces) {
                superinterfaces.add(referenceTypeName(typeJ2N(ArrayType.dims(arrayType.dimensions(), superinterface), false)));
            }
        }

        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
        cw.visit(V17, ACC_PUBLIC | ACC_INTERFACE | ACC_ABSTRACT, className, null,
                "java/lang/Object", superinterfaces.toArray(new String[0]));

        if (arrayType.elementType() instanceof PrimitiveType p) {
            if (p == PrimitiveType.Z)
                p = PrimitiveType.B;
            cw.visitMethod(ACC_PUBLIC | ACC_ABSTRACT, "typedReadElement",
                    "(I)" + p, null, null);
            cw.visitMethod(ACC_PUBLIC | ACC_ABSTRACT, "typedWriteElement",
                    "(I" + p + ")V", null, null);
        }

        return cw.toByteArray();
    }

    private byte[] generateArrayClass(ArrayType arrayType, String className) {
        Type.Lookup gLookup = arrayType.endingElementType() instanceof Clazz c ? c.lookup : compContext;

        ClassDesc elementTypeN = typeJ2N(arrayType.elementType(), false);
        String superName = arrayType.supertype() instanceof Clazz superclass && superclass.knownClass == KnownClass.OBJECT
                ? ROOT_TYPE_ARRAY_VARIANT
                : referenceTypeName(typeJ2N(arrayType.supertype(), true));

        boolean root = !superName.endsWith(ARRAY_CLASS_NAME_SUFFIX + ARRAY_IMPL_NAME_SUFFIX);

        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
        cw.visit(V17, ACC_PUBLIC, className, null, superName, new String[]{
                className.substring(0, className.length() - ARRAY_IMPL_NAME_SUFFIX.length())});
        String fname = "array", ftype = "[" + elementTypeN.descriptorString();
        cw.visitField(ACC_PUBLIC | ACC_FINAL, fname, ftype, null, null);

        MethodVisitor constructor = cw.visitMethod(ACC_PUBLIC, "<init>", "(I)V", null, null);
        constructor.visitVarInsn(ALOAD, 0);
        if (root) {
            constructor.visitInsn(ACONST_NULL);
            constructor.visitMethodInsn(INVOKESPECIAL, superName, "<init>", "(L" + BRIDGE_CLASS_NAME + ";)V", false);
            constructor.visitVarInsn(ALOAD, 0);
            assert superName.equals(ROOT_TYPE_ARRAY_VARIANT) : superName + ", " + arrayType;
            constructor.visitMethodInsn(INVOKEVIRTUAL, superName,
                    methodNameJ2N("<init>", compContext.findClass(KnownClass.OBJECT)), "()V", false);
        } else {
            constructor.visitInsn(ICONST_0);
            constructor.visitMethodInsn(INVOKESPECIAL, superName, "<init>", "(I)V", false);
        }
        constructor.visitVarInsn(ALOAD, 0);
        constructor.visitVarInsn(ILOAD, 1);
        if (arrayType.elementType() instanceof PrimitiveType p)
            constructor.visitIntInsn(NEWARRAY, p.newarrayCode());
        else
            constructor.visitTypeInsn(ANEWARRAY, referenceTypeName(elementTypeN));
        constructor.visitFieldInsn(PUTFIELD, className, fname, ftype);
        constructor.visitInsn(RETURN);
        constructor.visitMaxs(-1, -1);

        if (arrayType.elementType() == PrimitiveType.B) {
            assert root;

            constructor = cw.visitMethod(ACC_PUBLIC, "<init>", "([B)V", null, null);
            constructor.visitVarInsn(ALOAD, 0);
            constructor.visitInsn(ACONST_NULL);
            constructor.visitMethodInsn(INVOKESPECIAL, superName, "<init>", "(L" + BRIDGE_CLASS_NAME + ";)V", false);
            constructor.visitVarInsn(ALOAD, 0);
            constructor.visitMethodInsn(INVOKEVIRTUAL, superName,
                    methodNameJ2N("<init>", compContext.findClass(KnownClass.OBJECT)), "()V", false);
            constructor.visitVarInsn(ALOAD, 0);
            constructor.visitVarInsn(ALOAD, 1);
            constructor.visitFieldInsn(PUTFIELD, className, fname, ftype);
            constructor.visitInsn(RETURN);
            constructor.visitMaxs(-1, -1);
        }

        MethodVisitor typeMethod = cw.visitMethod(ACC_PUBLIC, "type",
                "()L" + ArrayType.class.getName().replace('.', '/') + ";", null, null);
        typeMethod.visitFieldInsn(GETSTATIC, BRIDGE_CLASS_NAME, BRIDGE_FIELD_NAME, BRIDGE_FIELD_DESC);
        typeMethod.visitVarInsn(ALOAD, 0);
        typeMethod.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Object", "getClass", "()Ljava/lang/Class;", false);
        typeMethod.visitMethodInsn(INVOKEVIRTUAL, NEWINTERPRETER_NAME, "typeN2J", TYPEN2J_DESC, false);
        typeMethod.visitTypeInsn(CHECKCAST, ArrayType.class.getName().replace('.', '/'));
        typeMethod.visitInsn(ARETURN);
        typeMethod.visitMaxs(-1, -1);

        typeMethod = cw.visitMethod(ACC_PUBLIC, "type",
                "()L" + Type.class.getName().replace('.', '/') + ";", null, null);
        typeMethod.visitFieldInsn(GETSTATIC, BRIDGE_CLASS_NAME, BRIDGE_FIELD_NAME, BRIDGE_FIELD_DESC);
        typeMethod.visitVarInsn(ALOAD, 0);
        typeMethod.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Object", "getClass", "()Ljava/lang/Class;", false);
        typeMethod.visitMethodInsn(INVOKEVIRTUAL, NEWINTERPRETER_NAME, "typeN2J", TYPEN2J_DESC, false);
        typeMethod.visitInsn(ARETURN);
        typeMethod.visitMaxs(-1, -1);

        MethodVisitor length = cw.visitMethod(ACC_PUBLIC, "length", "()I", null, null);
        length.visitVarInsn(ALOAD, 0);
        length.visitFieldInsn(GETFIELD, className, fname, ftype);
        length.visitInsn(ARRAYLENGTH);
        length.visitInsn(IRETURN);
        length.visitMaxs(-1, -1);

        if (elementTypeN.descriptorString().equals("Z"))
            elementTypeN = byte.class.describeConstable().orElseThrow();

        MethodVisitor readElement;
        if (elementTypeN.isPrimitive())
            readElement = cw.visitMethod(ACC_PUBLIC, "typedReadElement",
                    "(I)" + elementTypeN.descriptorString(), null, null);
        else
            readElement = cw.visitMethod(ACC_PUBLIC, "readElement",
                    "(I)Ljava/lang/Object;", null, null);
        readElement.visitVarInsn(ALOAD, 0);
        readElement.visitFieldInsn(GETFIELD, className, fname, ftype);
        readElement.visitVarInsn(ILOAD, 1);
        readElement.visitInsn(IALOAD + arrayOpOffset(arrayType.elementType()));
        readElement.visitInsn(IRETURN + basicTypeOffset(arrayType.elementType()));
        readElement.visitMaxs(-1, -1);

        MethodVisitor writeElement;
        if (elementTypeN.isPrimitive())
            writeElement = cw.visitMethod(ACC_PUBLIC, "typedWriteElement",
                    "(I" + elementTypeN.descriptorString() + ")V", null, null);
        else
            writeElement = cw.visitMethod(ACC_PUBLIC, "writeElement",
                    "(ILjava/lang/Object;)V", null, null);
        writeElement.visitVarInsn(ALOAD, 0);
        writeElement.visitFieldInsn(GETFIELD, className, fname, ftype);
        writeElement.visitVarInsn(ILOAD, 1);
        writeElement.visitVarInsn(ILOAD + basicTypeOffset(arrayType.elementType()), 2);
        if (arrayType.elementType() instanceof ReferenceType)
            writeElement.visitTypeInsn(CHECKCAST, referenceTypeName(elementTypeN));
        writeElement.visitInsn(IASTORE + arrayOpOffset(arrayType.elementType()));
        writeElement.visitInsn(RETURN);
        writeElement.visitMaxs(-1, -1);

        if (arrayType.elementType() instanceof PrimitiveType p) {
            readElement = cw.visitMethod(ACC_PUBLIC, "readElement",
                    "(I)Ljava/lang/Object;", null, null);
            readElement.visitVarInsn(ALOAD, 0);
            readElement.visitVarInsn(ILOAD, 1);
            readElement.visitMethodInsn(INVOKEVIRTUAL, className, "typedReadElement",
                    "(I)" + elementTypeN.descriptorString(), false);
            readElement.visitMethodInsn(INVOKESTATIC, p.wrapperName(), "valueOf", p.boxMethodDesc(), false);
            readElement.visitInsn(ARETURN);
            readElement.visitMaxs(-1, -1);

            writeElement = cw.visitMethod(ACC_PUBLIC, "writeElement",
                    "(ILjava/lang/Object;)V", null, null);
            writeElement.visitVarInsn(ALOAD, 0);
            writeElement.visitVarInsn(ILOAD, 1);
            writeElement.visitVarInsn(ALOAD, 2);
            writeElement.visitTypeInsn(CHECKCAST, p.wrapperName());
            writeElement.visitMethodInsn(INVOKEVIRTUAL, p.wrapperName(), p.unboxMethodName(), "()" + p, false);
            writeElement.visitMethodInsn(INVOKEVIRTUAL, className, "typedWriteElement",
                    "(I" + elementTypeN.descriptorString() + ")V", false);
            writeElement.visitInsn(RETURN);
            writeElement.visitMaxs(-1, -1);
        }

        return cw.toByteArray();
    }

    private Class<?> typeJ2NClass(Clazz clazz) {
        if (clazz.hidden) {
            Class<?> mirror = classMirrors.get(clazz);
            if (mirror == null) {
                mirror = classLoader.defineClass(clazz, referenceTypeName(typeJ2N(clazz, true)),
                        generateMirrorClassfile(clazz, null));
                assert mirror != null && classMirrors.get(clazz) == mirror : classMirrors.get(clazz) + ", " + mirror;
            }
            return mirror;
        } else {
            try {
                return classLoader.loadClass(referenceTypeName(typeJ2N(clazz, true)).replace('/', '.'));
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private ClassDesc typeJ2N(Type type, boolean forStatic) {
        if (type instanceof Clazz clazz) {
            assert clazz.name != null;
            return ClassDesc.ofDescriptor("L" + classNameJ2N(clazz, forStatic) + ";");
        }
        if (type instanceof PrimitiveType prim)
            return prim.asClass().describeConstable().orElseThrow();

        ArrayType arrayType = (ArrayType) type;
        StringBuilder sb = new StringBuilder();
        Type endingElementType = arrayType.endingElementType();
        if (endingElementType instanceof PrimitiveType p)
            sb.append("L" + PRIM_MULTIDIM_ARR_PREFIX).append(p.descriptor());
        else
            sb.append("L" + CLASS_NAME_PREFIX).append(((Clazz) endingElementType).name);
        for (int i = 0; i < arrayType.dimensions(); i++)
            sb.append(ARRAY_CLASS_NAME_SUFFIX);
        if (forStatic)
            sb.append(ARRAY_IMPL_NAME_SUFFIX);
        sb.append(';');
        return ClassDesc.ofDescriptor(sb.toString());
    }

    private String classNameJ2N(Clazz c, boolean forStatic) {
        String s;
        if (forStatic && c.knownClass == KnownClass.OBJECT)
            s = ROOT_INTERFACE_IMPL_NAME;
        else
            s = CLASS_NAME_PREFIX + c.name;
        if (c.hidden)
            return hiddenClassNames.computeIfAbsent(c, __ -> s + hiddenClassNames.size());
        else
            return s;
    }

    private MethodTypeDesc g2n(MethodType methodType) {
        return MethodTypeDesc.of(typeJ2N(methodType.returnType(), false),
                methodType.parameterTypes().stream().map(t -> typeJ2N(t, false)).toArray(ClassDesc[]::new));
    }

    private static String referenceTypeName(ClassDesc t) {
        assert !t.isPrimitive();
        String desc = t.descriptorString();
        return desc.substring(1, desc.length() - 1);
    }

    public Type typeN2J(Class<?> c) { // ezt használják generált osztályok type() függvényei is, ezért publikus
        if (c.isPrimitive())
            return PrimitiveType.of(c);
        assert !c.isArray();
        return classTypeN2J(c);
    }

    @Nonnull
    public Type classTypeN2J(Class<?> c) {
        if (c.isPrimitive())
            return PrimitiveType.of(c);

        Type type = mirrorClassesReverse.get(c);
        assert type != null : c;
        return type;
    }

    public Type classNameN2J(String nativeClassName) {
        if (nativeClassName.endsWith(ARRAY_CLASS_NAME_SUFFIX)) {
            Type elementType = classNameN2J(nativeClassName.substring(0, nativeClassName.length() - ARRAY_CLASS_NAME_SUFFIX.length()));
            return new ArrayType(elementType);
        } else if (nativeClassName.endsWith(ARRAY_CLASS_NAME_SUFFIX + ARRAY_IMPL_NAME_SUFFIX)) {
            Type elementType = classNameN2J(nativeClassName.substring(0, nativeClassName.length() -
                    (ARRAY_CLASS_NAME_SUFFIX.length() + ARRAY_IMPL_NAME_SUFFIX.length())));
            return new ArrayType(elementType);
        } else {
            if (!nativeClassName.startsWith(CLASS_NAME_PREFIX))
                return null;

            if (nativeClassName.startsWith(PRIM_MULTIDIM_ARR_PREFIX))
                return PrimitiveType.ofDescriptorCharacter(nativeClassName.charAt(PRIM_MULTIDIM_ARR_PREFIX.length()));

            if (nativeClassName.equals(ROOT_INTERFACE_IMPL_NAME) || nativeClassName.equals(ROOT_TYPE_ARRAY_VARIANT))
                nativeClassName = ROOT_INTERFACE_NAME;

            nativeClassName = mirrorClassNameToInside(nativeClassName);
            return compContext.findClassOrNull(nativeClassName);
        }
    }

    private static String methodNameJ2N(String methodName, ReferenceType clazz) {
        if (methodName.charAt(0) == '<')
            if (methodName.equals("<init>"))
                return "constr_" + ((Clazz) clazz).name.replace('/', '_');
            else if (methodName.equals("<clinit>"))
                return "clinit2";
            else
                throw new IllegalArgumentException();
        else
            return METHOD_NAME_PREFIX + methodName;
    }

    @Nonnull
    private MethodType methodTypeN2J(java.lang.invoke.MethodType nativeMethodType, boolean skipFirst) {
        return new MethodType(
                nativeMethodType.parameterList().stream().skip(skipFirst ? 1 : 0).map(this::classTypeN2J).toList(),
                classTypeN2J(nativeMethodType.returnType())
        );
    }

    private static String typeDescriptorToClassName(String typeDescriptor) {
        assert typeDescriptor.startsWith("L");
        return typeDescriptor.substring(1, typeDescriptor.length() - 1);
    }

    private MethodHandle cachedByteArrayAdapterN2J, cachedByteArrayAdapterJ2N;

    @Override
    protected MethodHandle adapterN2J(Class<?> src, Type dst) {
        if (src == byte[].class && dst.equals(BYTE_ARRAY)) {
            if (cachedByteArrayAdapterN2J != null)
                return cachedByteArrayAdapterN2J;

            MethodHandle constructor;
            try {
                Class<?> byteArrayMirror = (Class<?>) typeJ2N(BYTE_ARRAY, true).resolveConstantDesc(lookup);
                constructor = lookup.findConstructor(byteArrayMirror,
                        methodType(void.class, byte[].class));
            } catch (ReflectiveOperationException e) {
                throw new RuntimeException(e);
            }

            return cachedByteArrayAdapterN2J = nullSafe(constructor);
        }
        return super.adapterN2J(src, dst);
    }

    @Override
    protected MethodHandle adapterJ2N(Type src, Class<?> dst) {
        if (src.equals(BYTE_ARRAY) && dst == byte[].class) {
            if (cachedByteArrayAdapterJ2N != null)
                return cachedByteArrayAdapterJ2N;

            try {
                // ensure byte array mirror generated
                typeJ2N(BYTE_ARRAY, true).resolveConstantDesc(lookup);
            } catch (ReflectiveOperationException e) {
                throw new RuntimeException(e);
            }

            return cachedByteArrayAdapterJ2N = nullSafe(byteArrayNativeArrayGetter);
        }
        return super.adapterJ2N(src, dst);
    }

    private MethodHandle nullSafe(MethodHandle mh) {
        assert mh.type().parameterCount() == 1;
        Class<?> argType = mh.type().parameterType(0);
        Class<?> retType = mh.type().returnType();
        // TODO fel kéne küldeni hogy megtévesztő az exception,
        //      "target and test types must match"-ot ír target és fallback type-ok helyett
        return MethodHandles.guardWithTest(
                MH_Objects_nonNull.asType(methodType(boolean.class, argType)),
                mh,
                MethodHandles.dropArguments(MethodHandles.constant(retType, null), 0, argType)
        );
    }

    private class MethodConverter extends MethodVisitor {

        private final String thisClassName;
        private final Method m;
        private final MethodVisitor out;

        private final Deque<Type> stack = new LinkedList<>();
        private final List<Type> vars = new ArrayList<>();
        private final Type.Lookup gLookup;
        private List<Type> prevFrameLocals, prevFrameStack;
        private final Map<Label, ClassDesc> news = new HashMap<>();
        private Label lastLabel;

        private int maxStack;
        private boolean hasArrayAlloc;
        private boolean hasMultiANewArray;

        protected MethodConverter(MethodVisitor out, Method m, String thisClassName) {
            super(ASM9);
            if (FRAME_DEBUG)
                System.out.println("BEGIN " + m);
            this.m = m;
            this.out = out;
            this.thisClassName = thisClassName;

            vars.addAll(Collections.nCopies(m.maxLocals, null));
            int i = 0;
            for (Type t : m.fullArgTypes()) {
                vars.set(i, t);
                i += t.slotSize();
            }
            prevFrameStack = new ArrayList<>();
            prevFrameLocals = new ArrayList<>(vars.subList(0, i));

            maxStack = m.maxStack;
            if (m.clazz.superName == null && m.name.equals("<init>"))
                if (maxStack == 0)
                    maxStack = 1;
            if (m.name.equals("<clinit>"))
                // ha ezt változtatjuk, változtasuk handleMethodot is
                if (maxStack <= 1)
                    maxStack = 2; // azért kell 2, mert lehet hogy long/double változó is van a statikusan inicializálandók között

            maxStack += 1; // mert NEW-kor eggyel több hely kell nekünk
            gLookup = m.clazz.lookup;
        }

        @Override
        public void visitInsn(int opcode) {
            if (opcode >= IRETURN && opcode <= RETURN && m.name.equals("<clinit>")) {
                clinitEnd(out, thisClassName);
            }

            switch (opcode) {
                case ACONST_NULL -> stack.push(SpecialType.NULL);
                case ICONST_M1, ICONST_0, ICONST_1, ICONST_2, ICONST_3, ICONST_4, ICONST_5 ->
                        stack.push(PrimitiveType.I);
                case LCONST_0, LCONST_1 -> stack.push(PrimitiveType.J);
                case FCONST_0, FCONST_1, FCONST_2 -> stack.push(PrimitiveType.F);
                case DCONST_0, DCONST_1 -> stack.push(PrimitiveType.D);
                case BALOAD -> {
                    primArrayLoad("(I)B", PrimitiveType.I);
                    return;
                }
                case SALOAD -> {
                    primArrayLoad("(I)S", PrimitiveType.I);
                    return;
                }
                case CALOAD -> {
                    primArrayLoad("(I)C", PrimitiveType.I);
                    return;
                }
                case IALOAD -> {
                    primArrayLoad("(I)I", PrimitiveType.I);
                    return;
                }
                case FALOAD -> {
                    primArrayLoad("(I)F", PrimitiveType.F);
                    return;
                }
                case LALOAD -> {
                    primArrayLoad("(I)J", PrimitiveType.J);
                    return;
                }
                case DALOAD -> {
                    primArrayLoad("(I)D", PrimitiveType.D);
                    return;
                }
                case AALOAD -> {
                    stack.pop(); // element index
                    ArrayType t = (ArrayType) stack.pop();
                    String className = referenceTypeName(typeJ2N(t, false));
                    out.visitMethodInsn(INVOKEINTERFACE,
                            className, "readElement", "(I)Ljava/lang/Object;",
                            true);
                    out.visitTypeInsn(CHECKCAST, referenceTypeName(typeJ2N(t.elementType(), false)));
                    stack.push(t.elementType());
                    return;
                }
                case BASTORE -> {
                    primArrayStore("(IB)V");
                    return;
                }
                case SASTORE -> {
                    primArrayStore("(IS)V");
                    return;
                }
                case CASTORE -> {
                    primArrayStore("(IC)V");
                    return;
                }
                case IASTORE -> {
                    primArrayStore("(II)V");
                    return;
                }
                case FASTORE -> {
                    primArrayStore("(IF)V");
                    return;
                }
                case LASTORE -> {
                    primArrayStore("(IJ)V");
                    return;
                }
                case DASTORE -> {
                    primArrayStore("(ID)V");
                    return;
                }
                case AASTORE -> {
                    Type valueType = stack.pop(); // value
                    Type indexType = stack.pop(); // index
                    Type arrType = stack.pop();
                    if (!(arrType instanceof ArrayType t))
                        throw new RuntimeException("not array type for array store: " + arrType
                                + " (index type: " + indexType + ", value type: " + valueType + ")");
                    String className = referenceTypeName(typeJ2N(t, false));
                    out.visitMethodInsn(INVOKEINTERFACE,
                            className, "writeElement", "(ILjava/lang/Object;)V",
                            true);
                    return;
                }
                case POP -> stack.pop();
                case POP2 -> {
                    if (stack.pop().slotSize() == 1)
                        stack.pop();
                }
                // DUP implementációkat MethodParserből másoltam.
                // majd át kéne gondolni, hogy lehet/érdemes-e közösíteni őket.
                case DUP -> stack.push(stack.peek());
                case DUP_X1 -> {
                    Type v1 = stack.pop();
                    Type v2 = stack.pop();
                    stack.push(v1);
                    stack.push(v2);
                    stack.push(v1);
                }
                case DUP_X2 -> {
                    Type v1 = stack.pop();
                    Type v2 = stack.pop();
                    if (v2.slotSize() == 2) {
                        stack.push(v1);
                        stack.push(v2);
                        stack.push(v1);
                    } else {
                        Type v3 = stack.pop();
                        stack.push(v1);
                        stack.push(v3);
                        stack.push(v2);
                        stack.push(v1);
                    }
                }
                case DUP2 -> {
                    Type v1 = stack.pop();
                    if (v1.slotSize() == 2) {
                        stack.push(v1);
                        stack.push(v1);
                    } else {
                        Type v2 = stack.pop();
                        stack.push(v2);
                        stack.push(v1);
                        stack.push(v2);
                        stack.push(v1);
                    }
                }
                case DUP2_X1 -> {
                    Type v1 = stack.pop();
                    Type v2 = stack.pop();
                    if (v1.slotSize() == 2) {
                        stack.push(v1);
                        stack.push(v2);
                        stack.push(v1);
                    } else {
                        Type v3 = stack.pop();
                        stack.push(v2);
                        stack.push(v1);
                        stack.push(v3);
                        stack.push(v2);
                        stack.push(v1);
                    }
                }
                case SWAP -> {
                    // TODO ez nincs MethodParserben implementálva
                    //      mire használják ezt egyáltalán?
                    Type a = stack.pop();
                    Type b = stack.pop();
                    stack.push(a);
                    stack.push(b);
                }
                case IADD, ISUB, IMUL, IDIV, IREM, ISHL, ISHR, IUSHR, IAND, IOR, IXOR,
                        LADD, LSUB, LMUL, LDIV, LREM, LSHL, LSHR, LUSHR, LAND, LOR, LXOR,
                        FADD, FSUB, FMUL, FDIV, FREM,
                        DADD, DSUB, DMUL, DDIV, DREM -> stack.pop();
                case INEG, LNEG, FNEG, DNEG, I2B, I2S, I2C -> {
                    // nem változtatnak a típuson
                }
                case L2I, F2I, D2I -> primConvTo(PrimitiveType.I);
                case I2L, F2L, D2L -> primConvTo(PrimitiveType.J);
                case I2F, L2F, D2F -> primConvTo(PrimitiveType.F);
                case I2D, L2D, F2D -> primConvTo(PrimitiveType.D);
                case LCMP, FCMPL, FCMPG, DCMPL, DCMPG -> {
                    stack.pop();
                    stack.push(PrimitiveType.I);
                }
                case MONITORENTER, MONITOREXIT -> {
                    stack.pop();
                    out.visitInsn(POP);
                    return;
                }
                case IRETURN, LRETURN, FRETURN, DRETURN, ARETURN, RETURN, ATHROW -> {
                    if (opcode != RETURN)
                        stack.pop();
                    // jön egy stack frame a következő utasításban
                }
                case ARRAYLENGTH -> {
                    Type popped = stack.pop();
                    ArrayType t = (ArrayType) popped;
                    out.visitMethodInsn(INVOKEINTERFACE,
                            Array.class.getName().replace('.', '/'),
                            "length", "()I", true);
                    stack.push(PrimitiveType.I);
                    return;
                }
                default -> throw unknownOpcode(opcode);
            }
            out.visitInsn(opcode);
        }

        @Nonnull
        private RuntimeException unknownOpcode(int opcode) {
            return new RuntimeException("unknown opcode: " + opcode);
        }

        private void primConvTo(PrimitiveType to) {
            stack.pop();
            stack.push(to);
        }

        private static boolean isIntLike(Type t) {
            return t == PrimitiveType.I || t == PrimitiveType.C || t == PrimitiveType.S || t == PrimitiveType.B
                    || t == PrimitiveType.Z;
        }

        private void primArrayLoad(String methodDescriptor, PrimitiveType basicType) {
            Type index = stack.pop();
            if (!isIntLike(index))
                throw new RuntimeException("not integer index for array load: " + index);

            Type t = stack.pop();
            assert t instanceof ArrayType : t + ", " + methodDescriptor;
            // ez boolean és byte tömb esetén különböző, de ugyanaz a bytecode kezeli, ezért kell kiolvasnunk a stackről
            String className = referenceTypeName(typeJ2N(t, false));
            assert className.startsWith(PRIM_MULTIDIM_ARR_PREFIX) && className.endsWith(ARRAY_CLASS_NAME_SUFFIX) : className;
            out.visitMethodInsn(INVOKEINTERFACE, className, "typedReadElement", methodDescriptor, true);
            stack.push(basicType);
            if (FRAME_DEBUG)
                System.out.println("PRIM ARRAY LOAD " + stack);
        }

        private void primArrayStore(String methodDescriptor) {
            if (FRAME_DEBUG)
                System.out.println("BEFORE PRIM ARRAY STORE " + stack);
            Type valueType = stack.pop(); // value

            Type index = stack.pop();
            if (!isIntLike(index))
                throw new RuntimeException("not integer index for array store: " + index);

            Type t = stack.pop();
            assert t instanceof ArrayType : t + ", " + index + ", " + valueType;
            String className = referenceTypeName(typeJ2N(t, false));
            assert className.startsWith(PRIM_MULTIDIM_ARR_PREFIX) && className.endsWith(ARRAY_CLASS_NAME_SUFFIX) : className;
            out.visitMethodInsn(INVOKEINTERFACE, className, "typedWriteElement", methodDescriptor, true);
        }

        @Override
        public void visitIntInsn(int opcode, int operand) {
            switch (opcode) {
                case BIPUSH, SIPUSH -> stack.push(PrimitiveType.I);
                case NEWARRAY -> {
                    Type t = switch (operand) {
                        case T_BOOLEAN -> PrimitiveType.Z;
                        case T_BYTE -> PrimitiveType.B;
                        case T_SHORT -> PrimitiveType.S;
                        case T_CHAR -> PrimitiveType.C;
                        case T_INT -> PrimitiveType.I;
                        case T_FLOAT -> PrimitiveType.F;
                        case T_LONG -> PrimitiveType.J;
                        case T_DOUBLE -> PrimitiveType.D;
                        default -> throw new RuntimeException("unknown array type code: " + operand);
                    };

                    arrayAllocation(new ArrayType(t));
                    return;
                }
                default -> throw unknownOpcode(opcode);
            }
            out.visitIntInsn(opcode, operand);
        }

        @Override
        public void visitVarInsn(int opcode, int varIndex) {
            switch (opcode) {
                case ILOAD, LLOAD, FLOAD, DLOAD, ALOAD -> {
                    Type v = vars.get(varIndex);
                    if (v == null)
                        throw new RuntimeException("var contains no value: " + varIndex);
                    stack.push(v);
                }
                case ISTORE, LSTORE, FSTORE, DSTORE, ASTORE -> {
                    Type t = stack.pop();
                    if (t == null)
                        throw new RuntimeException("stored var type not known: " + t);
                    if (FRAME_DEBUG)
                        System.out.println("STORE TO " + varIndex);
                    while (varIndex >= vars.size())
                        vars.add(null);
                    vars.set(varIndex, t);
                }
                default -> throw unknownOpcode(opcode);
            }
            out.visitVarInsn(opcode, varIndex);
        }

        @Override
        public void visitTypeInsn(int opcode, String typeStringasdf) {
            Type gtype = ReferenceType.ofReferenceTypeName(typeStringasdf, compContext);
            ClassDesc ntype = typeJ2N(gtype, opcode == NEW);

            switch (opcode) {
                case NEW -> {
                    if (lastLabel != null) {
                        assert !news.containsKey(lastLabel) : news.get(lastLabel) + ", " + lastLabel + ", " + m;
                        news.put(lastLabel, ntype);

                        // ha nem nullozzuk, különben olyan labelhöz jegyezzük be valamelyik NEW-t, amelyikhez semmi közé
                        // viszont ha fog rá később Frame hivatkozni, akkor biztos kapunk most Labelt.
                        lastLabel = null;
                    }

                    stack.push(gtype);
                    out.visitTypeInsn(opcode, referenceTypeName(ntype));
                    out.visitInsn(DUP);
                    out.visitInsn(ACONST_NULL);
                    out.visitMethodInsn(INVOKESPECIAL, referenceTypeName(ntype),
                            "<init>", "(L" + BRIDGE_CLASS_NAME + ";)V", false);
                    return;
                }
                case ANEWARRAY -> {
                    ArrayType arrayType = new ArrayType(gtype);
                    arrayAllocation(arrayType);
                    return;
                }
                case CHECKCAST -> {
                    stack.pop();
                    stack.push(gtype);
                }
                case INSTANCEOF -> {
                    stack.pop();
                    stack.push(PrimitiveType.I);
                }
                default -> throw unknownOpcode(opcode);
            }

            out.visitTypeInsn(opcode, referenceTypeName(ntype));
        }

        private void arrayAllocation(ArrayType arrayType) {
            Type len = stack.pop();
            assert isIntLike(len);
            stack.push(arrayType);

            hasArrayAlloc = true;
            String arrayClass = referenceTypeName(typeJ2N(arrayType, true));
            out.visitTypeInsn(NEW, arrayClass);
            out.visitInsn(DUP_X1);
            out.visitInsn(SWAP);
            out.visitMethodInsn(INVOKESPECIAL, arrayClass, "<init>", "(I)V", false);
        }

        @Override
        public void visitFieldInsn(int opcode, String owner, String name, String descriptor) {
            switch (opcode) {
                case GETSTATIC -> stack.push(Type.parse(descriptor, compContext));
                case PUTSTATIC -> stack.pop();
                case GETFIELD -> {
                    stack.pop();
                    stack.push(Type.parse(descriptor, compContext));
                }
                case PUTFIELD -> {
                    stack.pop();
                    stack.pop();
                }
                default -> throw unknownOpcode(opcode);
            }

            out.visitFieldInsn(opcode, classNameJ2N(gLookup.findClass(owner), true),
                    FIELD_NAME_PREFIX + name,
                    typeJ2N(Type.parse(descriptor, gLookup), false).descriptorString());
        }

        @Override
        public void visitMethodInsn(int opcode, String ownerName, String name, String descriptor, boolean isInterface) {
            ReferenceType owner = ReferenceType.ofReferenceTypeName(ownerName, gLookup);

            if (FRAME_DEBUG)
                System.out.println("INVOKE" + opcode + " " + owner + "." + name + descriptor);
            if (opcode != INVOKESTATIC)
                stack.pop();
            methodCallStackMoves(MethodType.parse(descriptor, gLookup));

            MethodType guestMethodType = MethodType.parse(descriptor, gLookup);

            if (owner instanceof Clazz ownerClass && CompilationContext.isPolySigMethod(ownerClass.name, name)) {
                if (name.startsWith("linkTo")) {
                    assert opcode == INVOKESTATIC;
                    out.visitInvokeDynamicInsn(name, g2n(guestMethodType).descriptorString(),
                            new Handle(H_INVOKESTATIC, NEWINTERPRETER_NAME,
                                    "mhLinkerBSM", "(Ljava/lang/invoke/MethodHandles$Lookup;" +
                                    "Ljava/lang/String;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite;", false));
                } else {
                    assert opcode == INVOKEVIRTUAL;
                    MethodTypeDesc type2 = g2n(guestMethodType.prependArg(owner));
                    out.visitInvokeDynamicInsn(name, type2.descriptorString(),
                            new Handle(H_INVOKESTATIC, NEWINTERPRETER_NAME,
                                    name.equals("invokeBasic") ? "invokeBasicBSM" : "polySigMethodBSM",
                                    "(Ljava/lang/invoke/MethodHandles$Lookup;" +
                                            "Ljava/lang/String;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite;", false));
                }
                return;
            }

            if (owner instanceof Clazz ownerClass && ownerClass.knownClass == KnownClass.OBJECT
                    && (opcode == INVOKEVIRTUAL || opcode == INVOKESPECIAL && name.equals("<init>"))) {
                out.visitMethodInsn(INVOKEINTERFACE, ROOT_INTERFACE_NAME,
                        methodNameJ2N(name, owner), g2n(guestMethodType).descriptorString(), true);
            } else {
                if (name.equals("<init>")) {
                    assert opcode == INVOKESPECIAL;
                    opcode = INVOKEVIRTUAL;
                } else if (owner instanceof ArrayType) {
                    assert opcode == INVOKEVIRTUAL;
                    opcode = INVOKEINTERFACE;
                    isInterface = true;
                }
                out.visitMethodInsn(opcode, referenceTypeName(typeJ2N(owner, opcode == INVOKESPECIAL || opcode == INVOKESTATIC)),
                        methodNameJ2N(name, owner), g2n(guestMethodType).descriptorString(), isInterface);
            }
        }

        private void methodCallStackMoves(MethodType methodType) {
            for (int i = 0; i < methodType.parameterTypes().size(); i++)
                stack.pop();
            if (methodType.returnType() != PrimitiveType.V)
                stack.push(methodType.returnType());
        }

        @Override
        public void visitInvokeDynamicInsn(String name, String descriptor, Handle bsm, Object... bootstrapMethodArguments) {
            MethodType gMType = MethodType.parse(descriptor, gLookup);

            methodCallStackMoves(gMType);

            List<Object> args = new ArrayList<>();
            args.add(bsm.getTag());
            args.add(bsm.getOwner());
            args.add(bsm.getName());
            args.add(bsm.getDesc());
            args.add(bsm.isInterface() ? 1 : 0);
            for (Object bootstrapMethodArgument : bootstrapMethodArguments)
                args.add(convertConstant(bootstrapMethodArgument, gLookup));

            Handle bsm2 = new Handle(H_INVOKESTATIC, NEWINTERPRETER_NAME, "guestIndyBSM",
                    "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;" +
                            "ILjava/lang/String;Ljava/lang/String;Ljava/lang/String;I[Ljava/lang/Object;)" +
                            "Ljava/lang/invoke/CallSite;", false);

            out.visitInvokeDynamicInsn(name, g2n(gMType).descriptorString(), bsm2, args.toArray());
        }

        @Override
        public void visitJumpInsn(int opcode, Label label) {
            out.visitJumpInsn(opcode, label);
        }

        @Override
        public void visitLabel(Label label) {
            out.visitLabel(label);
            lastLabel = label;
            for (TryCatchBlockNode tryCatchBlock : m.tryCatchBlocks) {
                if (tryCatchBlock.handler.getLabel().equals(label)) {
                    if (tryCatchBlock.type == null)
                        stack.push(compContext.findClass(KnownClass.THROWABLE));
                    else
                        stack.push(ReferenceType.ofReferenceTypeName(tryCatchBlock.type, compContext));
                    break;
                }
            }
        }

        @Override
        public void visitLdcInsn(Object value) {
            if (value instanceof Integer) {
                stack.push(PrimitiveType.I);
            } else if (value instanceof Float) {
                stack.push(PrimitiveType.F);
            } else if (value instanceof Long) {
                stack.push(PrimitiveType.J);
            } else if (value instanceof Double) {
                stack.push(PrimitiveType.D);
            } else if (value instanceof String) {
                stack.push(compContext.findClass(KnownClass.STRING));
            } else if (value instanceof org.objectweb.asm.Type t) {
                int sort = t.getSort();
                if (sort == org.objectweb.asm.Type.OBJECT || sort == org.objectweb.asm.Type.ARRAY) {
                    stack.push(compContext.findClass(KnownClass.CLASS));
                } else if (sort == METHOD) {
                    stack.push(compContext.findClass(KnownClass.METHOD_TYPE));
                } else {
                    throw new RuntimeException("unnkown constant type: " + value);
                }
            } else if (value instanceof Handle) {
                stack.push(compContext.findClass(KnownClass.METHOD_HANDLE));
            } else if (value instanceof ConstantDynamic condy) {
                stack.push(Type.parse(condy.getDescriptor(), compContext));
            } else {
                throw new RuntimeException("unknown constant type: " + value);
            }
            out.visitLdcInsn(convertConstant(value, gLookup));
        }

        @Override
        public void visitIincInsn(int varIndex, int increment) {
            out.visitIincInsn(varIndex, increment);
        }

        @Override
        public void visitTableSwitchInsn(int min, int max, Label dflt, Label... labels) {
            stack.pop();
            out.visitTableSwitchInsn(min, max, dflt, labels);
        }

        @Override
        public void visitLookupSwitchInsn(Label dflt, int[] keys, Label[] labels) {
            stack.pop();
            out.visitLookupSwitchInsn(dflt, keys, labels);
        }

        @Override
        public void visitMultiANewArrayInsn(String descriptor, int numDimensions) {
            ArrayType arrayType = (ArrayType) Type.parse(descriptor, compContext);
            if (numDimensions != arrayType.dimensions())
                throw new RuntimeException("unsupported");

            hasMultiANewArray = true;
            pushInt(numDimensions);
            visitIntInsn(NEWARRAY, T_INT);
            for (int i = numDimensions - 1; i >= 0; i--) {
                visitInsn(DUP_X1);
                visitInsn(SWAP);
                pushInt(i);
                visitInsn(SWAP);
                visitInsn(IASTORE);
            }

            Type endingElementType = arrayType.endingElementType();
            if (endingElementType instanceof PrimitiveType p) {
                visitFieldInsn(GETSTATIC, p.wrapperName(), "TYPE", "Ljava/lang/Class;");
            } else {
                out.visitLdcInsn(typeCondy(org.objectweb.asm.Type.getType(endingElementType.descriptor()), gLookup));
                stack.push(compContext.findClass(KnownClass.CLASS));
            }
            visitInsn(SWAP);
            visitMethodInsn(INVOKESTATIC, "java/lang/reflect/Array",
                    "newInstance", "(Ljava/lang/Class;[I)Ljava/lang/Object;", false);
            visitTypeInsn(CHECKCAST, descriptor);
        }

        private void pushInt(int n) {
            assert n >= 0;
            if (n <= 5)
                out.visitInsn(ICONST_0 + n);
            else if (n <= Byte.MAX_VALUE)
                out.visitIntInsn(BIPUSH, n);
            else if (n <= Short.MAX_VALUE)
                out.visitIntInsn(SIPUSH, n);
            else
                out.visitLdcInsn(n);
            stack.push(PrimitiveType.I);
        }

        @Override
        public void visitMaxs(int maxStack, int maxLocals) {
            out.visitMaxs(this.maxStack + (hasMultiANewArray ? 3 : hasArrayAlloc ? 2 : 0), maxLocals);
        }

        @Override
        public void visitTryCatchBlock(Label start, Label end, Label handler, String type) {
            type = type == null
                    ? classNameJ2N(compContext.findClass(KnownClass.THROWABLE), false)
                    : classNameJ2N((Clazz) ReferenceType.ofReferenceTypeName(type, gLookup), false);
            out.visitTryCatchBlock(start, end, handler, type);
        }

        @Override
        public void visitLineNumber(int line, Label start) {
            out.visitLineNumber(line, start);
        }

        @Override
        public void visitFrame(int type, int numLocal, Object[] localsArr, int numStack, Object[] stackArr) {
            int numLocal2 = numLocal;

            if (localsArr != null) {
                int inc = 0;
                for (int i = 0; i < numLocal; i++)
                    if (localsArr[i] instanceof Integer j && (Opcodes.LONG.equals(j) || Opcodes.DOUBLE.equals(j)))
                        inc++;
                numLocal += inc;
            }
            List<Type> locals = convertFTypeArray(localsArr);
            List<Type> stack = convertFTypeArray(stackArr);
            if (FRAME_DEBUG)
                System.out.println("FRAME " + type + " " + numLocal + ", " + locals + ", " + stack + "; prev: " + prevFrameLocals + ", " + prevFrameStack + "; new locals: " + Arrays.toString(localsArr));

            this.vars.clear();
            this.vars.addAll(prevFrameLocals);
            this.stack.clear();

            switch (type) {
                case F_SAME -> {
                }
                case F_SAME1 -> {
                    this.stack.push(stack.get(0));
                }
                case F_CHOP -> {
                    for (int i = 0; i < numLocal; i++) {
                        if (vars.remove(vars.size() - 1) == null)
                            // ha long/double üres slot volt, azt is szedjük ki
                            vars.remove(vars.size() - 1);
                    }
                }
                case F_APPEND -> {
                    this.vars.addAll(locals.subList(0, numLocal));
                }
                case F_FULL, F_NEW -> {
                    this.vars.clear();
                    this.vars.addAll(locals.subList(0, numLocal));
                    for (int i = 0; i < numStack; i++)
                        this.stack.push(stack.get(i));
                }
                default -> throw new RuntimeException("unknown frame type: " + type);
            }

            prevFrameLocals = new ArrayList<>(this.vars);
            prevFrameStack = new ArrayList<>(this.stack);

            if (localsArr != null)
                for (int i = 0; i < localsArr.length; i++)
                    localsArr[i] = convertFType2(localsArr[i]);
            if (stackArr != null)
                for (int i = 0; i < stackArr.length; i++)
                    stackArr[i] = convertFType2(stackArr[i]);
            out.visitFrame(type, numLocal2, localsArr, numStack, stackArr);
        }

        private Object convertFType2(Object obj) {
            if (obj instanceof String s)
                return referenceTypeName(typeJ2N(ReferenceType.ofReferenceTypeName(s, gLookup), false));
            else if (obj instanceof Label label) {
                ClassDesc s = news.get(label);
                assert s != null;
                return referenceTypeName(s);
            } else if ((Integer) obj == 6)
                return referenceTypeName(typeJ2N(ReferenceType.ofReferenceTypeName(m.clazz.name, gLookup), true));
            else
                return obj;
        }

        private List<Type> convertFTypeArray(Object[] ftypeArray) {
            if (ftypeArray == null)
                return new ArrayList<>();

            List<Type> l = new ArrayList<>();
            for (Object o : ftypeArray) {
                Type t = ftypeToType(o);
                l.add(t);
                if (t != null && t.slotSize() == 2)
                    l.add(null);
            }
            return l;
        }

        private Type ftypeToType(Object ftype) {
            if (ftype instanceof String s)
                return ReferenceType.ofReferenceTypeName(s, compContext);
            else if (ftype instanceof Label)
                return SpecialType.UNINITIALIZED_TYPE;
                //return compContext.findClass(KnownClass.OBJECT);
            else {
                return switch ((int) ftype) {
                    case 0 -> SpecialType.TOP;
                    case 1 -> PrimitiveType.I;
                    case 2 -> PrimitiveType.F;
                    case 3 -> PrimitiveType.D;
                    case 4 -> PrimitiveType.J;
                    case 5 -> SpecialType.NULL;
                    case 6 -> m.clazz; // ITEM_UNINITIALIZED_THIS
                    default -> throw new RuntimeException("unknown frame var type: " + ftype);
                };
            }
        }

        private enum SpecialType implements Type {
            NULL, TOP, UNINITIALIZED_TYPE;

            @Override
            public String descriptor() {
                throw new UnsupportedOperationException();
            }

            @Override
            public boolean isAssignableFrom(Type otherType) {
                throw new UnsupportedOperationException();
            }

            @Override
            public int slotSize() {
                return 1;
            }

            @Override
            public int modifiers() {
                throw new UnsupportedOperationException();
            }

            @Override
            public String displayName() {
                throw new UnsupportedOperationException();
            }

            @Override
            public Type supertype() {
                throw new UnsupportedOperationException();
            }

            @Override
            public int bytesSize() {
                throw new UnsupportedOperationException();
            }

            @Override
            public Collection<? extends Type> allAncestorsAndThis() {
                throw new UnsupportedOperationException();
            }
        }
    }

    private String mirrorClassNameToInside(String className) {
        assert className.startsWith(CLASS_NAME_PREFIX) : className;
        return className.substring(CLASS_NAME_PREFIX.length());
    }

    private class ClassLoaderImpl extends ClassLoader {

        public Class<?> bridgeClass;

        Lookup createLookup() {
            ClassWriter cw = new ClassWriter(0);
            String className = BRIDGE_CLASS_NAME;
            cw.visit(V17, ACC_PUBLIC, className, null, "java/lang/Object", null);
            cw.visitField(ACC_PUBLIC | ACC_STATIC, BRIDGE_FIELD_NAME,
                    NewInterpreter.class.descriptorString(), null, null);

            String lookupTypeDesc = Lookup.class.descriptorString();
            cw.visitField(ACC_PUBLIC | ACC_STATIC, "LOOKUP",
                    lookupTypeDesc, null, null);

            MethodVisitor mv = cw.visitMethod(ACC_PUBLIC | ACC_STATIC, "<clinit>", "()V", null, null);
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/invoke/MethodHandles", "lookup", "()" + lookupTypeDesc, false);
            mv.visitFieldInsn(PUTSTATIC, className, "LOOKUP", lookupTypeDesc);
            mv.visitInsn(RETURN);
            mv.visitMaxs(1, 0);

            byte[] bytes = cw.toByteArray();
            bridgeClass = defineClass(className.replace('/', '.'), bytes, 0, bytes.length);
            Lookup lookup;
            try {
                lookup = (Lookup) MethodHandles.lookup().findStaticGetter(bridgeClass, "LOOKUP", Lookup.class).invokeExact();
                lookup.findStaticSetter(bridgeClass, BRIDGE_FIELD_NAME, NewInterpreter.class).
                        invokeExact(NewInterpreter.this);
            } catch (Error | RuntimeException e) {
                throw e;
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
            return lookup;
        }

        @Override
        protected Class<?> findClass(String name) throws ClassNotFoundException {
            if (name.isEmpty())
                throw new IllegalArgumentException();

            String nameWithDots = name;
            name = name.replace('.', '/');

            byte[] classfile;
            Type type = classNameN2J(name);
            if (type instanceof ArrayType arrayType) {
                // primitív tömböknél van bármi értelme interfaceeket generálni?
                if (name.endsWith(ARRAY_IMPL_NAME_SUFFIX))
                    classfile = generateArrayClass(arrayType, name);
                else
                    classfile = generateArrayInterface(arrayType, name);
            } else {
                if (type == null)
                    throw new ClassNotFoundException(name);

                Clazz c = (Clazz) type;
                compContext.ensureClassLoaded(c);
                if (c.superclass == null && !name.equals(ROOT_INTERFACE_IMPL_NAME) && !name.equals(ROOT_TYPE_ARRAY_VARIANT)) {
                    // System.out.println("gri "+c+", "+c.superclass);
                    classfile = generateRootInterfaceForClass(c);
                } else {
                    // System.out.println("gmc "+c+", "+name);
                    classfile = generateMirrorClassfile(c, name);
                }
            }
            Class<?> nativeClass = defineClass(type, name, classfile);
            if (type.equals(new ArrayType(PrimitiveType.B)) && name.endsWith(ARRAY_IMPL_NAME_SUFFIX)) {
                try {
                    byteArrayNativeArrayGetter = lookup.findGetter(nativeClass, "array", byte[].class);
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
            if (type instanceof Clazz c && c.knownClass == KnownClass.MEMBER_NAME) {
                try {
                    mnResolutionFieldGetter = lookup.findGetter(nativeClass, "nativeMH", MethodHandle.class);
                    mnResolutionFieldSetter = lookup.findSetter(nativeClass, "nativeMH", MethodHandle.class);
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
            return nativeClass;
        }

        public Class<?> defineClass(Type type, String outsideName, byte[] classfile) {
            if (SAVE_MIRROR_CLASSES)
                try {
                    Files.createDirectories(Path.of("build", "mirrorclasses"));
                    Files.write(Path.of("build", "mirrorclasses",
                            outsideName.replace('/', '.') + ".class"), classfile);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            Class<?> c;
           /* if (type instanceof Clazz ct && ct.hidden) {
                try {
                    c = lookup.defineClass(classfile);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            } else*/
            // System.out.println("outsidename "+outsideName+", "+type);
            c = defineClass(outsideName.replace('/', '.'), classfile, 0, classfile.length);
            if (type instanceof Clazz clazz && clazz.hidden) {
                Class<?> prev;
                if ((prev = classMirrors.put(type, c)) != null)
                    throw new RuntimeException(type + ": " + prev + ", " + c);
            }
            mirrorClassesReverse.put(c, type);
            mirrorClassesReverse2.put(outsideName, type);
            return c;
        }
    }

    public static abstract class RuntimeObjectBase implements Obj {

        private Object nativeData;

        public Object representedData() {
            return nativeData;
        }

        public void representedData(Object obj) {
            this.nativeData = obj;
        }
    }

    public static abstract class ThrowableBase extends WrappedException implements ClassObj {

        private final CompilationContext c = CompilationContext.context();
        private final Field messageField;

        private Object nativeData;

        public ThrowableBase() {
            super(null, CompilationContext.context().interpreter());
            messageField = c.field(c.findClass(KnownClass.THROWABLE), "detailMessage", c.findClass(KnownClass.STRING));

            // nem írunk ki minden exceptiont, pl. logback is csinál amikor megpróbálja betölteni logback-test.xml-t
            // (FileNotFoundException), mert File.exists()-et használja közvetetten
            //if (!type().name.equals("jdk/internal/misc/ScopedMemoryAccess$ScopedAccessError"))
            //    new Exception(type().name).printStackTrace();
        }

        @Override
        public Object representedData() {
            return nativeData;
        }

        @Override
        public void representedData(Object obj) {
            this.nativeData = obj;
        }

        @Override
        public String getMessage() {
            // itt vigyázni kell hogy ne dobjunk további exceptiont, mert ez lehet hogy UncaughtExceptionHandlerből
            // hívódik meg.
            // meg itt már ctx sincs.
            try {
                // lehetne valamit elé írni vagy akár sanitize-olni
                return c.interpreter().fromStringObj((Interpreter.ClassObj) readField(messageField));
            } catch (Exception e) {
                e.printStackTrace();
                return "<can't determine exception message because: " + e + ">";
            }
        }
    }

    public static Object stringBSM(MethodHandles.Lookup lookup, String name, Class<?> type,
                                   String value) throws Throwable {
        NewInterpreter ni = findInterpreter(lookup);
        return ni.fromString(value);
    }

    /**
     * @param value Class vagy MethodType
     */
    public static Object typeConstantBSM(MethodHandles.Lookup lookup, String name, Class<?> type,
                                         Object value) throws Throwable {
        NewInterpreter ni = findInterpreter(lookup);
        if (value instanceof Class<?> c)
            return ni.fromType(ni.classTypeN2J(c));
        else {
            java.lang.invoke.MethodType methodType = (java.lang.invoke.MethodType) value;
            return ni.toMethodType(ni.methodTypeN2J(methodType, false));
        }
    }

    public static Object mhBSM(MethodHandles.Lookup lookup, String name, Class<?> type,
                               int refKind, String owner, String desc) throws Throwable {
        NewInterpreter ni = findInterpreter(lookup);
        // isInterface mindegy, toMethodHandle úgysem nézi
        return ni.toMethodHandle(new Handle(refKind, owner, name, desc, false),
                (Clazz) ni.typeN2J(lookup.lookupClass()));
    }


    private static final MethodHandle MH_Intrinsic_evaluate;
    private static final MethodHandle MH_unboxExecutionResult;
    private static final MethodHandle MH_System_arraycopy_0_0;
    private static final MethodHandle MH_Objects_nonNull;

    private static final MethodHandle MH_intToBool_erasedParam, MH_intToByte_erasedParam, MH_intToShort_erasedParam, MH_intToChar_erasedParam;
    private static final MethodHandle MH_intToByte, MH_intToBool, MH_boolToInt;
    private static final MethodHandle MH_getMHTarget;

    static {
        try {
            MH_Intrinsic_evaluate = MethodHandles.lookup().findVirtual(Intrinsic.class,
                    "evaluate", methodType(ExecutionResult.class, Object[].class));
            MH_unboxExecutionResult = MethodHandles.lookup().findStatic(NewInterpreter.class,
                    "unboxExecutionResult", methodType(Object.class, ExecutionResult.class));
            MH_intToBool = MethodHandles.lookup().findStatic(NewInterpreter.class,
                    "intToBool", methodType(boolean.class, int.class));
            MH_intToBool_erasedParam = MH_intToBool.asType(methodType(boolean.class, Object.class));
            MH_intToByte = MethodHandles.lookup().findStatic(NewInterpreter.class,
                    "intToByte", methodType(byte.class, int.class));
            MH_intToByte_erasedParam = MH_intToByte.asType(methodType(byte.class, Object.class));
            MH_intToShort_erasedParam = MethodHandles.lookup().findStatic(NewInterpreter.class,
                    "intToShort", methodType(short.class, int.class)).asType(methodType(short.class, Object.class));
            MH_intToChar_erasedParam = MethodHandles.lookup().findStatic(NewInterpreter.class,
                    "intToChar", methodType(char.class, int.class)).asType(methodType(char.class, Object.class));
            MH_boolToInt = MethodHandles.lookup().findStatic(NewInterpreter.class,
                    "boolToInt", methodType(int.class, boolean.class));
            MH_getMHTarget = MethodHandles.lookup().findVirtual(NewInterpreter.class,
                    "getMHTarget", methodType(MethodHandle.class, Object.class));
            MH_System_arraycopy_0_0 =
                    MethodHandles.insertArguments(
                            MethodHandles.insertArguments(
                                    MethodHandles.lookup().findStatic(System.class,
                                            "arraycopy", methodType(void.class,
                                                    Object.class, int.class, Object.class, int.class, int.class)),
                                    1, 0),
                            2, 0);
            MH_Objects_nonNull = MethodHandles.lookup().findStatic(Objects.class, "nonNull",
                    methodType(boolean.class, Object.class));
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    private static int boolToInt(boolean bool) {
        return bool ? 1 : 0;
    }

    @SuppressWarnings("unchecked")
    public static CallSite nativeCallBSM(MethodHandles.Lookup lookup, String name, java.lang.invoke.MethodType nativeMethodType,
                                         Class<?> clazz, int isStatic) throws Throwable {
        // ha nem escape-elnénk a metódusnevet, ilyen hülyeséget írna ki:
        // Exception in thread "main" java.lang.VerifyError: Illegal call to internal method
        // Exception Details:
        //  Location:
        //    com/flyordie/code/isolated_env/sun/nio/fs/DefaultFileSystemProvider.<clinit>()V @4: invokedynamic
        //  Reason:
        //    Error exists in the bytecode
        //  Bytecode:
        //    0000000: 04b3 002d ba00 2f00 00b1
        if (name.startsWith("$"))
            name = name.substring(1);
        else
            name = "<" + name + ">";

        NewInterpreter ni = findInterpreter(lookup);
        Clazz receiverClass = (Clazz) ni.classTypeN2J(clazz);
        MethodType mt = ni.methodTypeN2J(nativeMethodType, isStatic == 0);
        Method m = ni.compContext.findMethodOrFail(receiverClass, name, mt);

        Intrinsic intrinsic = ni.intrinsics.get(m);
        if (intrinsic == null) {
            //Class<? extends Throwable> exceptionClass = (Class<? extends Throwable>)
            //        ni.typeJ2N(ni.compContext.findClass(KnownClass.UnsatisfiedLinkError), false);

            return new ConstantCallSite(exceptionThrower(
                    KnownClass.UnsatisfiedLinkError,
                    "No implementation available for native method: " + m.toString(),
                    nativeMethodType));
        } else {
            MethodHandle unbox = MH_unboxExecutionResult;
            if (nativeMethodType.returnType() == boolean.class)
                unbox = MethodHandles.filterReturnValue(unbox, MH_intToBool_erasedParam);
            else if (nativeMethodType.returnType() == short.class)
                unbox = MethodHandles.filterReturnValue(unbox, MH_intToShort_erasedParam);
            else if (nativeMethodType.returnType() == char.class)
                unbox = MethodHandles.filterReturnValue(unbox, MH_intToChar_erasedParam);
            else if (nativeMethodType.returnType() == byte.class)
                unbox = MethodHandles.filterReturnValue(unbox, MH_intToByte_erasedParam);
            MethodHandle c = MH_Intrinsic_evaluate.bindTo(intrinsic).asCollector(Object[].class, nativeMethodType.parameterCount());
            c = paramsBoolToInt(nativeMethodType, c, MH_boolToInt.asType(methodType(Object.class, boolean.class)));
            return new ConstantCallSite(MethodHandles.filterReturnValue(
                    c,
                    unbox
            ).asType(nativeMethodType));
        }
    }

    private static MethodHandle exceptionThrower(KnownClass exceptionType, String msg,
                                                 java.lang.invoke.MethodType methodType) throws NoSuchMethodException,
            IllegalAccessException {
        return MethodHandles.dropArguments(
                MethodHandles.filterReturnValue(
                        //      lookup.findStatic(exceptionClass, "allocAndInit", methodType(exceptionClass)),
                        // TODO ennek nem valós ULE-t kéne dobnia, hanem az izolált környezetben lévőt
                        MethodHandles.lookup().findConstructor(UnsatisfiedLinkError.class, methodType(void.class, String.class))
                                .bindTo(msg),
                        MethodHandles.throwException(methodType.returnType(), UnsatisfiedLinkError.class)
                ),
                0,
                methodType.parameterArray()
        );
    }

    // TODO végig kéne nézni a hívókat hogy hol kéne a return valuet is konvertálni
    private static MethodHandle paramsBoolToInt(java.lang.invoke.MethodType nativeMethodType, MethodHandle c, MethodHandle MH_boolToInt) {
        for (int i = 0; i < nativeMethodType.parameterCount(); i++)
            if (nativeMethodType.parameterType(i) == boolean.class)
                c = MethodHandles.filterArguments(c, i, MH_boolToInt);
        return c;
    }

    private static Object unboxExecutionResult(ExecutionResult executionResult) {
        return executionResult.orElseThrow();
    }

    public static CallSite guestIndyBSM(MethodHandles.Lookup lookup, String name, java.lang.invoke.MethodType methodType,
                                        int bsmRefKind, String bsmOwner, String bsmName, String bsmDesc, int bsmIsInterface,
                                        Object... bsmArgs) throws Throwable {
        NewInterpreter interpreter = findInterpreter(lookup);

        //for (int i = 0; i < bsmArgs.length; i++)
        //    bsmArgs[i] = interpreter.convertConstant(bsmArgs[i]);

        IndyLinkResult result = interpreter.linkIndy(
                (Clazz) interpreter.classTypeN2J(lookup.lookupClass()),
                new Handle(bsmRefKind, bsmOwner, bsmName, bsmDesc, bsmIsInterface != 0),
                name, interpreter.methodTypeN2J(methodType, false),
                bsmArgs
        );

        MethodHandle mh = interpreter.findGeneratedMethod(result.method());
        if (result.appendix() != null)
            mh = MethodHandles.insertArguments(mh, mh.type().parameterCount() - 1, result.appendix());
        if (methodType.returnType() == boolean.class)
            mh = MethodHandles.filterReturnValue(mh, MH_intToBool);
        return new ConstantCallSite(paramsBoolToInt(methodType, mh, MH_boolToInt).asType(methodType));
    }

    public static CallSite polySigMethodBSM(MethodHandles.Lookup lookup, String name, java.lang.invoke.MethodType nativeMethodType) throws Throwable {
        NewInterpreter interpreter = findInterpreter(lookup);

        Clazz objectClass = interpreter.compContext.findClass(KnownClass.OBJECT);
        MethodType methodType = interpreter.methodTypeN2J(nativeMethodType, false);

        ClassObj callerClass = interpreter.fromType(interpreter.typeN2J(lookup.lookupClass()));
        int refKind = H_INVOKEVIRTUAL;
        ClassObj defc = interpreter.fromType(methodType.parameterTypes().get(0));
        ClassObj nameObj = interpreter.fromString(name);
        ClassObj typeObj = interpreter.toMethodType(methodType.dropFirstArgument());
        Array appendixArray = interpreter.createArray(new ArrayType(objectClass), 1);
        ClassObj mn = (Interpreter.ClassObj) interpreter.execute(interpreter.symbols.MethodHandleNatives_linkMethod,
                callerClass, refKind, defc, nameObj, typeObj, appendixArray).orElseThrow();
        assert interpreter.memberNameRefKind(mn) == H_INVOKESTATIC;
        Method method = interpreter.memberNameReferredMethod(mn);
        Obj appendix = (Obj) appendixArray.readElement(0);

        MethodHandle mh = interpreter.findGeneratedMethod(method);
        if (appendix != null)
            mh = MethodHandles.insertArguments(mh, mh.type().parameterCount() - 1, appendix);
        mh = paramsBoolToInt(nativeMethodType, mh, MH_boolToInt.asType(methodType(int.class, boolean.class)));
        if (nativeMethodType.returnType() == byte.class && mh.type().returnType() == int.class)
            mh = MethodHandles.filterReturnValue(mh, MH_intToByte); // TODO ez itt ad-hoc, máshol is kéne
        return new ConstantCallSite(mh.asType(nativeMethodType));
    }

    public static CallSite invokeBasicBSM(MethodHandles.Lookup lookup, String name, java.lang.invoke.MethodType nativeMethodType) throws Throwable {
        NewInterpreter interpreter = findInterpreter(lookup);

        Class<?> mhClass = (Class<?>) interpreter.typeJ2N(
                interpreter.compContext.findClass(KnownClass.METHOD_HANDLE), false).resolveConstantDesc(lookup);

        MethodHandle h = MethodHandles.invoker(nativeMethodType);
        h = MethodHandles.collectArguments(h, 0, MH_getMHTarget.bindTo(interpreter).
                asType(methodType(MethodHandle.class, mhClass)));
        int[] reorder = new int[h.type().parameterCount()];
        for (int i = 1; i < reorder.length; i++)
            reorder[i] = i - 1;
        h = MethodHandles.permuteArguments(h, h.type().dropParameterTypes(0, 1), reorder);
        assert h.type().equals(nativeMethodType) : h.type() + ", " + nativeMethodType;
        return new ConstantCallSite(h);
    }

    private MethodHandle getMHTarget(Object co) {
        ClassObj lambdaForm = (Interpreter.ClassObj) ((Interpreter.ClassObj) co).readField(symbols.MethodHandle_form);
        ClassObj vmentry = (Interpreter.ClassObj) lambdaForm.readField(symbols.LambdaForm_vmentry);
        Method m = memberNameReferredMethod(vmentry);
        MethodHandle h = findGeneratedMethod(m);
        // System.out.println(h+", "+m.fullArgTypes());
        return h;
    }

    public static CallSite mhLinkerBSM(MethodHandles.Lookup lookup, String name, java.lang.invoke.MethodType nativeMethodType) throws Throwable {
        NewInterpreter interpreter = findInterpreter(lookup);

        // MethodType mt = MethodType.methodType(eraseToBasicType(nativeMethodType.returnType()),
        //        nativeMethodType.parameterList().stream().<Class<?>>map(NewInterpreter::eraseToBasicType).
        //                limit(nativeMethodType.parameterCount()-1).toList());
        // TODO meg kéne nézni hogyitt miért kell a mapnek megadni hogy <Class<?>>

        MethodHandle h = MethodHandles.invoker(nativeMethodType.dropParameterTypes(nativeMethodType.parameterCount() - 1,
                nativeMethodType.parameterCount()));
        h = MethodHandles.collectArguments(h, 0, interpreter.mnResolutionFieldGetter);

        // a MN paramétert az elejéről a végére visszük
        int[] reorder = new int[h.type().parameterCount()];
        reorder[0] = reorder.length - 1;
        for (int i = 1; i < reorder.length; i++)
            reorder[i] = i - 1;
        h = MethodHandles.permuteArguments(h, nativeMethodType, reorder);

        assert h.type().equals(nativeMethodType) : h + ", " + nativeMethodType;
        System.out.println(name + ", " + h + ", " + nativeMethodType);
        return new ConstantCallSite(h);
    }


    @Override
    protected void prepareMemberName(Interpreter.ClassObj co, Method m, int refKind) {
        boolean isVirtual = refKind == H_INVOKEVIRTUAL || refKind == H_INVOKEINTERFACE;
        MethodHandle mh;
        if (m.isPolySigMethodSpecialization) {
            try {
                mh = exceptionThrower(KnownClass.RuntimeException,
                        "TODO NI.pMN MN resolve polysigmethod specialization",
                        g2nMethodType(m));
            } catch (ReflectiveOperationException e) {
                throw new RuntimeException(e);
            }
        } else {
            //System.out.println("prepare MN "+m);
            mh = isVirtual ? findGeneratedMethod_virtual(m) : findGeneratedMethod(m);
            //System.out.println(mh);
            mh = intToBool(mh);
        }
        try {
            mnResolutionFieldSetter.invoke(co, mh);
        } catch (Error | RuntimeException e) {
            throw e;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    private static MethodHandle intToBool(MethodHandle mh) {
        for (int i = 0; i < mh.type().parameterCount(); i++) {
            if (mh.type().parameterType(i) == boolean.class)
                mh = MethodHandles.filterArguments(mh, i, MH_intToBool_erasedParam.asType(methodType(boolean.class, int.class)));
            if (mh.type().parameterType(i) == byte.class)
                mh = MethodHandles.filterArguments(mh, i, MH_intToByte_erasedParam.asType(methodType(byte.class, int.class)));
            if (mh.type().parameterType(i) == short.class)
                mh = MethodHandles.filterArguments(mh, i, MH_intToShort_erasedParam.asType(methodType(short.class, int.class)));
            if (mh.type().parameterType(i) == char.class)
                mh = MethodHandles.filterArguments(mh, i, MH_intToChar_erasedParam.
                        asType(methodType(char.class, int.class)));
        }
        if (mh.type().returnType() == boolean.class)
            mh = MethodHandles.filterReturnValue(mh, MH_boolToInt);
        return mh;
    }

    private static Class<?> eraseToBasicType(Class<?> c) {
        if (c.isPrimitive()) {
            if (c == long.class)
                return long.class;
            if (c == float.class)
                return float.class;
            if (c == double.class)
                return double.class;
            if (c == void.class)
                return void.class;
            return int.class;
        } else
            return Object.class;
    }

    private static NewInterpreter findInterpreter(Lookup lookup) throws Throwable {
        return (NewInterpreter) lookup.findStaticGetter(lookup.findClass(BRIDGE_CLASS_NAME.replace('/', '.')),
                BRIDGE_FIELD_NAME, NewInterpreter.class).invokeExact();
    }

    private record MethodID(Clazz clazz, String name, String type) {
    }
}
