package com.flyordie.code;

import com.flyordie.code.Clazz.Field;
import com.flyordie.code.Clazz.Member;
import com.flyordie.code.Clazz.Method;
import com.flyordie.code.CompilationContext.AddressingMode;
import com.flyordie.code.CompilationContext.NoSuchClassException;
import com.flyordie.code.Node.*;
import com.flyordie.code.Type.ArrayType;
import com.flyordie.code.Type.PrimitiveType;
import com.flyordie.code.Type.ReferenceType;
import com.flyordie.code.isolate.HostEnvironmentImpl;
import com.flyordie.code.isolate.HostEnvironment;
import com.flyordie.code.runtime.*;
import com.flyordie.code.util.CRC32Impl;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Handle;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.ParameterNode;
import org.objectweb.asm.tree.RecordComponentNode;

import javax.annotation.Nonnull;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.*;
import java.lang.foreign.MemorySegment;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.ref.Reference;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.zip.CRC32;

import static com.flyordie.code.Type.PrimitiveType.*;
import static java.lang.invoke.MethodHandles.identity;
import static java.lang.invoke.MethodHandles.lookup;
import static java.lang.invoke.MethodType.methodType;
import static java.util.stream.Collectors.toSet;
import static org.objectweb.asm.Opcodes.*;

public abstract class Interpreter {

    public static final ArrayType BYTE_ARRAY = new ArrayType(PrimitiveType.B);

    public static final int NEXT_THREAD_ID_OFFSET = 2; // ld. komment Thread.ThreadIdentifiers-ben

    protected final CompilationContext compContext;
    public final Symbols symbols;

    private final Map<Object, ClassObj> reflectionObjects = new HashMap<>();
    private final List<Object> reflectionObjectsInProgress = new ArrayList<>();

    public final Map<Method, Intrinsic> intrinsics = new HashMap<>();
    public static int counter;

    private ClassObj bootLoaderUnnamedModule;
    private final Map<String, ClassObj> modulesByPackages = new HashMap<>();

    public final Set<Member> reflectivelyUsedMembers = new HashSet<>();

    final HostEnvironment hostEnvironment = new HostEnvironmentImpl();
    boolean initialized;

    public CallFrame prependedCallFrame;

    Statics statics;

    {
        statics = new Statics();
        statics.interpreter = this;
    }

    public Interpreter(CompilationContext compContext) {
        this.compContext = compContext;
        this.symbols = new Symbols();

        intrinsic1(symbols.Class_desiredAssertionStatus0, a -> {
            if (!(fromClass((ClassObj) a) instanceof Clazz c))
                return 0;
            if (c.name.equals("java/lang/invoke/BoundMethodHandle"))
                // foreign upcall support. BMH konstruktorban assert meghívja speciesData-t, amit nem tudunk runtime végrehajtani.
                return 0;
            return 1;
        });
        intrinsic0(symbols.Class_registerNatives, () -> null);
        intrinsic0(symbols.ClassLoader_registerNatives, () -> null);
        intrinsic0(symbols.MethodHandleNatives_registerNatives, () -> null);
        intrinsic1(symbols.Class_isInterface, c -> {
            Type t = (Type) ((ClassObj) c).representedData();
            return t instanceof Clazz clazz && clazz.isInterface() ? 1 : 0;
        });
        intrinsic1(symbols.Class_isPrimitive, c -> {
            Type t = (Type) ((ClassObj) c).representedData();
            return t instanceof PrimitiveType ? 1 : 0;
        });
        intrinsic1(symbols.Class_getSuperclass, c -> {
            Type t = (Type) ((ClassObj) c).representedData();
            if (t instanceof Clazz c2 && c2.isInterface()) return null;
            Type supertype = t instanceof ArrayType ? symbols.Object : t.supertype();
            return fromType(supertype);
        });
        intrinsic1(symbols.Class_isHidden, c -> {
            Type t = fromClass((ClassObj) c);
            return t instanceof Clazz clazz && clazz.hidden ? 1 : 0;
        });
        intrinsic2(symbols.Class_isAssignableFrom, (c1, c2) -> {
            Type t1 = (Type) ((ClassObj) c1).representedData();
            Type t2 = (Type) ((ClassObj) c2).representedData();
            return t1.isAssignableFrom(t2) ? 1 : 0;
        });
        intrinsic1(symbols.method(symbols.Class, "isArray", Z), c -> {
            Type t = (Type) ((ClassObj) c).representedData();
            return t instanceof ArrayType ? 1 : 0;
        });
        intrinsics.put(symbols.Class_forName0, args1 -> {
            String name1 = fromStringObj((ClassObj) args1[0]);
            boolean initialize = intToBool((int) args1[1]);
            ClassObj classLoader = (ClassObj) args1[2];
            Clazz caller1 = args1[3] == null ? null : (Clazz) fromClass((ClassObj) args1[3]);
            // nézzünk majd utána, mitől lehet a caller null

            // TODO if (classLoader != null) throw new UnsupportedOperationException("TODO");

            Clazz clazz1;
            try {
                clazz1 = compContext.findClass(name1.replace('.', '/'));
            } catch (NoSuchClassException e) {
                throwException(compContext.findClass(KnownClass.ClassNotFoundException));
                throw new RuntimeException("should not reach here");
            }

            // TODO access check

            if (initialize) {
                ExecutionResult r = ensureInitialized(clazz1);
                if (r != null && r.returnFrom != null)
                    // TODO wrappeljük NoClassDefFoundErrorba
                    return r;
            }

            return new ExecutionResult(fromType(clazz1), null);
        });
        intrinsics.put(symbols.ClassLoader_findBootstrapClass, args1 -> {
            String name1 = fromStringObj((ClassObj) args1[0]);
            if (!name1.startsWith("java.") && !name1.startsWith("jdk.") && !name1.startsWith("sun.")) // TODO
                return new ExecutionResult(null, null);
            Clazz clazz1 = compContext.findClassOrNull(name1.replace('.', '/'), true);
            return new ExecutionResult(fromType(clazz1), null);
        });
        intrinsic1(symbols.Class_getEnclosingMethod0, c -> {
            Type t = fromClass((ClassObj) c);
            if (!(t instanceof Clazz clazz)) return null;
            if (clazz.outerMethod == null) return null;
            if (clazz.outerClass == null)
                throw new RuntimeException("has outerMethod but not outerClass");
            Array array = createArray(symbols.objectArray, 3);
            array.writeElement(0, fromType(compContext.findClass(clazz.outerClass)));
            array.writeElement(1, fromString(clazz.outerMethod));
            array.writeElement(2, fromString(clazz.outerMethodDesc));
            return array;
        });
        intrinsic1(symbols.Class_getDeclaringClass0, c -> {
            Type t = fromClass((ClassObj) c);
            if (!(t instanceof Clazz clazz)) return null;
            if (clazz.outerClass == null) return null;
            return fromType(clazz.lookup.findClass(clazz.outerClass));
        });
        intrinsic2(symbols.Class_isInstance, (c, obj) -> {
            if (obj == null)
                return 0;
            Type t = (Type) ((ClassObj) c).representedData();
            return t.isAssignableFrom(((Obj) obj).type()) ? 1 : 0;
        });
        Function<Object, Object> Class_getModifiers = c -> {
            // TODO ha inner classról van szó, valamit piszkálni kéne a modifierekkel,
            //      lásd Reflection.getClassAccessFlags javadocja
            Type type = fromClass((ClassObj) c);
            return type.modifiers();
        };
        intrinsic1(symbols.Class_getModifiers, Class_getModifiers);
        intrinsic1(symbols.Reflection_getClassAccessFlags, Class_getModifiers);
        intrinsic1(symbols.Class_initClassName, c -> {
            Type t = (Type) ((ClassObj) c).representedData();
            String name = t instanceof ArrayType arrayType ? arrayType.descriptor() : t.displayName();
            ClassObj nameStr = fromString(name);
            ((ClassObj) c).writeField(symbols.Class_name, nameStr);
            return nameStr;
        });
        intrinsic1(symbols.Class_getPrimitiveClass, n -> {
            String s = (String) ((ClassObj) n).representedData();
            return switch (s) {
                case "boolean" -> fromType(Z);
                case "byte" -> fromType(PrimitiveType.B);
                case "char" -> fromType(PrimitiveType.C);
                case "double" -> fromType(PrimitiveType.D);
                case "float" -> fromType(PrimitiveType.F);
                case "int" -> fromType(I);
                case "long" -> fromType(J);
                case "short" -> fromType(PrimitiveType.S);
                case "void" -> fromType(V);
                default -> throw new RuntimeException("unknown primitive type: " + s);
            };
        });

        intrinsic1(symbols.String_intern, s -> fromString(fromStringObj((ClassObj) s)));

        // Kéne hozzá getNamedCon-t implementálni, ezért inkább letiltjuk az egész verifyConstants-t.
        // Meg amúgy sem szeretem azt a függvényt, mindenféle stringösszefűzés meg System.err.println
        // van benne túl korán.
        intrinsic0(symbols.MethodHandleNatives_verifyConstants, () -> 1);

        intrinsics.put(symbols.MethodHandleNatives_resolve, args -> {
            ClassObj memberName = (ClassObj) args[0];
            Clazz caller = args[1] == null ? null : (Clazz) ((ClassObj) args[1]).representedData();
            int lookupMode = (int) args[2];
            boolean speculativeResolve = intToBool((int) args[3]);

            // TODO access checkek

            int mnFlags = (int) memberName.readField(symbols.MemberName_flags);
            int refKind = (mnFlags & 0x0F000000) >>> 24;

            Clazz declaringClass = (Clazz) representedClass((ClassObj) memberName.readField(symbols.MemberName_clazz));
            String name = fromStringObj((ClassObj) memberName.readField(symbols.MemberName_name));
            ClassObj typeObj = (ClassObj) memberName.readField(symbols.MemberName_type);

            switch (refKind) {
                case H_INVOKEVIRTUAL, H_INVOKESTATIC, H_INVOKESPECIAL, H_INVOKEINTERFACE, H_NEWINVOKESPECIAL -> {
                    MethodType type = fromMethodType(typeObj);
                    Method m = compContext.findMethodOrNull(declaringClass, name, type);
                    if (m == null) if (speculativeResolve) return new ExecutionResult(null, null);
                    else
                        return throwException(symbols.NoSuchMethodError, "No such method: " + declaringClass.name + "." + name + type);
                    fillMemberName(m, refKind, memberName);
                    return new ExecutionResult(memberName, null);
                }
                case H_GETFIELD, H_PUTFIELD, H_GETSTATIC, H_PUTSTATIC -> {
                    Type type = (Type) typeObj.representedData();
                    Field f = compContext.findFieldOrNull(declaringClass, name, type);
                    if (f == null) if (speculativeResolve) return new ExecutionResult(null, null);
                    else
                        return throwException(symbols.NoSuchFieldError, "No such field: " + declaringClass.name + "." + name + " " + type);
                    fillMemberName(f, refKind, memberName);
                    return new ExecutionResult(memberName, null);
                }
                default -> throw new UnsupportedOperationException("unknown refkind: " + refKind);
            }
        });
        intrinsic1(symbols.MethodHandleNatives_getMemberVMInfo, mnObj -> {
            ClassObj mn = (ClassObj) mnObj;
            int refKind = ((int) mn.readField(symbols.MemberName_flags)) >>> 24 & 0xF;

            long vmindex;
            Obj vmtarget;

            // hogy megnyugodjon MemberName.vminfoIsConsistent arról hogy resolveolva van a MemberName
            if (mn.representedData() instanceof Field f) {
                vmindex = f.index;
                vmtarget = fromType(f.clazz);
            } else {
                assert mn.representedData() instanceof Method;
                if (refKind == H_INVOKEVIRTUAL || refKind == H_INVOKEINTERFACE) vmindex = 0L;
                else vmindex = -1L;
                vmtarget = mn;
            }

            Array array = createArray(new ArrayType(symbols.Object), 2);
            array.writeElement(0, execute(symbols.Long_valueOf, vmindex).orElseThrow());
            array.writeElement(1, vmtarget);
            return array;
        });
        intrinsic1(symbols.MethodHandleNatives_objectFieldOffset, mn -> {
            Field f = (Field) ((ClassObj) mn).representedData();
            reflectivelyUsedMembers.add(f);
            return (long) compContext.fieldOffset(f);
        });
        intrinsic1(symbols.MethodHandleNatives_staticFieldOffset, mn -> {
            Field f = (Field) ((ClassObj) mn).representedData();
            reflectivelyUsedField(f);
            return (long) compContext.fieldOffset(f);
        });
        intrinsic1(symbols.MethodHandleNatives_staticFieldBase, mn -> statics);
        intrinsic2(symbols.MethodHandleNatives_init, (mnObj, reflectionObj) -> {
            ClassObj reflectionObject = (ClassObj) reflectionObj;
            ClassObj memberName = (ClassObj) mnObj;
            assert memberName.representedData() == null;

            if (reflectionObject.type() == symbols.Field) {
                Field f = (Field) compContext.memberFromGlobalID((int) reflectionObject.readField(symbols.Field_slot));
                int guessedRefKind = (f.access & ACC_STATIC) != 0 ? H_GETSTATIC : H_GETFIELD;
                fillMemberName(f, guessedRefKind, memberName);
            } else if (reflectionObject.type() == symbols.Constructor) {
                Clazz c = (Clazz) fromClass((ClassObj) reflectionObject.readField(symbols.Constructor_clazz));
                Method m = (Method) compContext.memberFromGlobalID(
                        (int) reflectionObject.readField(symbols.Constructor_slot));
                fillMemberName(m, H_NEWINVOKESPECIAL, memberName);
            } else if (reflectionObject.type() == symbols.Method) {
                Method m = methodFromReflectionObject(reflectionObject);
                int guessedRefKind = (m.access & ACC_STATIC) != 0 ? H_INVOKESTATIC : m.clazz.isInterface() ? H_INVOKEINTERFACE : H_INVOKEVIRTUAL;
                fillMemberName(m, guessedRefKind, memberName);
            } else throw new UnsupportedOperationException(reflectionObj.toString());

            return null;
        });

        intrinsic0(symbols.StringUTF16_isBigEndian, () -> 1);

        intrinsic1(symbols.method(symbols.Float, "floatToRawIntBits", I, PrimitiveType.F), f -> Float.floatToRawIntBits((Float) f));
        intrinsic1(symbols.method(symbols.Float, "intBitsToFloat", PrimitiveType.F, I), f -> Float.intBitsToFloat((Integer) f));
        intrinsic1(symbols.method(symbols.Double, "doubleToRawLongBits", J, PrimitiveType.D), f -> Double.doubleToRawLongBits((Double) f));
        intrinsic1(symbols.method(symbols.Double, "longBitsToDouble", PrimitiveType.D, J), f -> Double.longBitsToDouble((Long) f));
        intrinsic0(symbols.method(symbols.System, "registerNatives", V), () -> null);
        intrinsic0(symbols.method(symbols.Thread, "registerNatives", V), () -> null);
        intrinsics.put(symbols.method(symbols.System, "arraycopy", V, symbols.Object, I, symbols.Object, I, I), args -> {
            Array arr1 = (Array) args[0], arr2 = (Array) args[2];
            int srcPos = (int) args[1], dstPos = (int) args[3], len = (int) args[4];
            //System.arraycopy(arr1.elements, srcPos, arr2.elements, dstPos, len);
            arr1.copy(srcPos, arr2, dstPos, len);
            return new ExecutionResult(null, null);
        });
        intrinsic0(symbols.method(symbols.Unsafe, "registerNatives", V), () -> null);
        intrinsic2(symbols.method(symbols.Unsafe, "arrayBaseOffset0", I, symbols.Class),
                (u, c) -> compContext.addressingMode.arrayElementOffset);
        intrinsic2(symbols.method(symbols.Unsafe, "arrayIndexScale0", I, symbols.Class), (u, c) -> {
            Type t = (Type) ((ClassObj) c).representedData();
            return arrayIndexScale((ArrayType) t);
        });
        intrinsic2(symbols.method(symbols.Unsafe, "objectFieldOffset0", J, symbols.Field), (u, fieldObj) -> {
            int field = fieldIndexFromReflectionObj((ClassObj) fieldObj);
            Field f = (Field) compContext.memberFromGlobalID(field);
            reflectivelyUsedField(f);
            return (long) compContext.fieldOffset(f);
        });
        intrinsic2(symbols.method(symbols.Unsafe, "staticFieldOffset0", J, symbols.Field), (u, fieldObj) -> {
            int field = fieldIndexFromReflectionObj((ClassObj) fieldObj);
            Field f = (Field) compContext.memberFromGlobalID(field);
            reflectivelyUsedField(f);
            return (long) compContext.fieldOffset(f);
        });
        intrinsic2(symbols.method(symbols.Unsafe, "staticFieldBase0", symbols.Object, symbols.Field), (u, fieldObj) -> {
            return statics;
        });
        intrinsic2(symbols.method(symbols.Unsafe, "ensureClassInitialized0", V, symbols.Class), (u, c) -> {
            ensureInitialized((Clazz) ((ClassObj) c).representedData());
            return null;
        });
        intrinsics.put(symbols.method(symbols.Unsafe, "objectFieldOffset1", J, symbols.Class, symbols.String), args -> {
            Clazz clazz = (Clazz) ((ClassObj) args[1]).representedData();
            String fieldName = (String) ((ClassObj) args[2]).representedData();
            for (FieldNode f : clazz.fields)
                if (f.name.equals(fieldName)) {
                    reflectivelyUsedMembers.add((Field) f);
                    return new ExecutionResult((long) compContext.fieldOffset((Field) f), null);
                }
            throw new RuntimeException("no such field '" + fieldName + "' in " + clazz.name);
        });

        Object[] types = {"Byte", PrimitiveType.B, "Boolean", Z, "Short", PrimitiveType.S, "Char", PrimitiveType.C, "Int", I, "Long", J, "Float", PrimitiveType.F, "Double", PrimitiveType.D, "Reference", symbols.Object};
        for (int i = 0; i < types.length; i += 2) {
            String n = (String) types[i];
            Type t = (Type) types[i + 1];

            registerCAS("compareAndSet" + n, t);
            registerUnsafeGet("get" + n, t);
            registerUnsafeGet("get" + n + "Volatile", t);
            registerUnsafePut("put" + n, t);
            registerUnsafePut("put" + n + "Volatile", t);
        }
        intrinsic1(symbols.method(symbols.Throwable, "fillInStackTrace", symbols.Throwable), t -> {
            List<ClassObj> stackTraceElements = new ArrayList<>();
            StringBuilder stackTrace2 = makeStackTrace(stackTraceElements);

            Array array = createArray(new ArrayType(symbols.StackTraceElement), stackTraceElements.toArray());
            ClassObj exceptionObject = (ClassObj) t;
            exceptionObject.representedData(stackTrace2.toString());
            exceptionObject.writeField(symbols.Throwable_backtrace, array);
            return t;
        });
        intrinsic0(symbols.method(symbols.CDS, "isDumpingClassList0", Z), () -> 0);
        intrinsic0(symbols.method(symbols.CDS, "isDumpingArchive0", Z), () -> 0);
        intrinsic0(symbols.method(symbols.CDS, "isSharingEnabled0", Z), () -> 0);
        intrinsic0(symbols.method(symbols.CDS, "getRandomSeedForDumping", J), () -> 1234);
        intrinsic0(symbols.method(symbols.CDS, "initializeFromArchive", V, symbols.Class), () -> null);
        intrinsic1(symbols.method(symbols.Object, "hashCode", I), o -> identityHashCode((Obj) o));
        intrinsic1(symbols.method(symbols.Object, "getClass", symbols.Class), o -> fromType(((Obj) o).type()));
        intrinsic1(symbols.method(symbols.Runtime, "availableProcessors", I), o -> 1);
        intrinsic0(symbols.method(symbols.Reflection, "getCallerClass", symbols.Class), () -> {
            Iterator<CallFrame> iterator = stackFrames(false).iterator();
            if (!iterator.hasNext()) throw new RuntimeException("no available direct caller");
            iterator.next();
            if (!iterator.hasNext()) throw new RuntimeException("no available caller");
            CallFrame c = iterator.next();
            return fromType(c.method().clazz);
        });
        intrinsics.put(symbols.method(symbols.Object, "clone", symbols.Object), args -> {
            Object o = args[0];
            if (o instanceof Array a1) {
                Array a2 = createArray(a1.type(), a1.length());
                for (int i = 0; i < a1.length(); i++)
                    a2.writeElement(i, a1.readElement(i));
                return new ExecutionResult(a2, null);
            }

            ClassObj co = (ClassObj) o;

            if (!((Type) symbols.Cloneable).isAssignableFrom(co.type()))
                return throwException(symbols.CloneNotSupportedException, co.type().displayName() + " doesn't implement Cloneable");

            return new ExecutionResult(co.cloneObject(), null);
        });

        // szálat indítana, ezért kihagyjuk
        intrinsic0(symbols.CleanerImpl_start, () -> null);

        intrinsic2(symbols.Reference_refersTo0, (ref, obj) -> {
            return ((ClassObj) ref).readField(symbols.Reference_referent) == obj ? 1 : 0;
        });

        intrinsic0(symbols.method(symbols.AtomicLong, "VMSupportsCS8", Z), () -> 1);
        intrinsics.put(symbols.method(symbols.Class, "getDeclaredFields0", new ArrayType(symbols.Field), Z), args -> {
            ClassObj c = (ClassObj) args[0];
            boolean publicOnly = intToBool((int) args[1]);

            ExecutionResult initializationResult = ensureInitialized(symbols.Field);

            if (initializationResult != null) return initializationResult;

            Type t = fromClass(c);
            Object[] a;
            if (t instanceof Clazz clazz)
                a = clazz.fields.stream().filter(f -> (f.access & ACC_PUBLIC) != 0 || !publicOnly).map(f -> adapters.fromNative((Field) f)).toArray();
            else a = new Object[0];
            return new ExecutionResult(createArray(new ArrayType(symbols.Field), a), null);
        });
        intrinsics.put(symbols.Class_getDeclaredConstructors0, args -> {
            ClassObj c = (ClassObj) args[0];
            boolean publicOnly = intToBool((int) args[1]);

            ExecutionResult initializationResult = ensureInitialized(symbols.Constructor);

            if (initializationResult != null) return initializationResult;

            Type t = fromClass(c);
            if (!(t instanceof Clazz clazz))
                return new ExecutionResult(createArray(new ArrayType(symbols.Constructor), 0), null);

            Object[] a = clazz.methods.stream().filter(m -> (m.access & ACC_PUBLIC) != 0 || !publicOnly).filter(m -> m.name.equals("<init>")).map(method -> {
                Method m = (Method) method;

                List<Type> fullArgTypes = m.fullArgTypes();
                Array paramTypeArray = createArray(new ArrayType(symbols.Class), fullArgTypes.size() - 1);
                for (int i = 1; i < fullArgTypes.size(); i++)
                    paramTypeArray.writeElement(i - 1, fromType(fullArgTypes.get(i)));

                int slot = m.clazz.methods.indexOf(m);
                assert slot != -1;

                ClassObj obj = createObject(symbols.Constructor, m);
                obj.writeField(symbols.Constructor_clazz, fromType(m.clazz));
                obj.writeField(symbols.Constructor_parameterTypes, paramTypeArray);
                // enélkül Constructor.toString exceptiont dobna
                obj.writeField(symbols.Constructor_exceptionTypes, createArray(symbols.classArray, 0));
                obj.writeField(symbols.Constructor_modifiers, m.access);
                obj.writeField(symbols.Constructor_slot, m.globalNumber);
                obj.writeField(symbols.Constructor_signature, fromString(m.signature));
                obj.writeField(symbols.Constructor_annotations, byteArray(ClassfileUtil.readMethodAttribute(m,
                        "RuntimeVisibleAnnotations")));
                obj.writeField(symbols.Constructor_parameterAnnotations, byteArray(ClassfileUtil.readMethodAttribute(m,
                        "RuntimeVisibleParameterAnnotations")));
                return obj;
            }).toArray();
            return new ExecutionResult(createArray(new ArrayType(symbols.Constructor), a), null);
        });
        intrinsics.put(symbols.Class_getDeclaredMethods0, args -> {
            ClassObj c = (ClassObj) args[0];
            boolean publicOnly = intToBool((int) args[1]);

            ExecutionResult initializationResult = ensureInitialized(symbols.Method);

            if (initializationResult != null) return initializationResult;

            Type t = fromClass(c);
            if (!(t instanceof Clazz clazz))
                return new ExecutionResult(createArray(new ArrayType(symbols.Method), 0), null);

            if (clazz.name.equals("sun/nio/fs/DefaultFileSystemProvider"))
                System.out.println("GDM " + t);

            Object[] a = clazz.methods.stream().map(Method.class::cast).filter(m -> (m.access & ACC_PUBLIC) != 0 || !publicOnly).filter(m -> !m.name.equals("<init>") && !m.name.equals("<clinit>")).filter(m -> m.clazz != symbols.Object || !clazz.isInterface()).map(adapters::fromNative).toArray();
            return new ExecutionResult(createArray(new ArrayType(symbols.Method), a), null);
        });
        intrinsics.put(symbols.NativeConstructorAccessorImpl_newInstance0, args -> {
            Method m = (Method) ((ClassObj) args[0]).representedData();
            Array argArray = (Array) args[1];

            ClassObj newInstance = createObject(m.clazz);

            Object[] newArgs;
            if (argArray == null) newArgs = new Object[1];
            else {
                newArgs = new Object[argArray.length() + 1];
                for (int i = 0; i < argArray.length(); i++) {
                    Object e = argArray.readElement(i);
                    // TODO unbox
                    if (e instanceof Obj || e == null) newArgs[i + 1] = e;
                    else throw new UnsupportedOperationException(e.getClass().getName());
                }
            }
            newArgs[0] = newInstance;

            ExecutionResult r = execute(m, newArgs);
            if (r.returnFrom != null) return r;

            return new ExecutionResult(newInstance, null);
        });

        intrinsic0(symbols.method(symbols.AccessController, "getStackAccessControlContext", symbols.AccessControlContext), () -> null);
        intrinsic0(symbols.method(symbols.SystemProps_Raw, "platformProperties", symbols.stringArray), () -> createArray(symbols.stringArray, 39));
        intrinsic0(symbols.method(symbols.SystemProps_Raw, "vmProperties", symbols.stringArray), () -> {
            Map<String, String> m = new LinkedHashMap<>();
            m.put("user.home", "/user-home");
            m.put("user.dir", "/user-dir");
            m.put("user.name", "usernamevalue");
            m.put("java.io.tmpdir", "/tmp-dir");
            m.put("native.encoding", "UTF-8");
            m.put("sun.stdout.encoding", "UTF-8");
            m.put("sun.stderr.encoding", "UTF-8");
            m.put("file.separator", "/");
            m.put("path.separator", ";");
            // eddig voltak amik JDK-nak mindenképp kellenek elinduláshoz
            m.put("line.separator", "\n");
            m.put("file.encoding", "UTF-8");
            m.put("sun.reflect.inflationThreshold", "2147483647");
            m.put("java.lang.invoke.MethodHandle.DONT_INLINE_THRESHOLD", "-1"); // különben CountingWrappert fog belerakni
            m.put("java.util.Arrays.useLegacyMergeSort", "true"); // sokkal rövidebb kód mint TimSort
            m.put("jdk.module.enable.native.access.0", "ALL-UNNAMED");
            m.put("sun.jnu.encoding", "UTF-8"); // különben pampognak System.err-re

            // CLinkerImplnek
            m.put("jdk.module.addopens.0", "java.base/jdk.internal.foreign.abi=ALL-UNNAMED");
            m.put("jdk.module.addopens.1", "java.base/java.lang.invoke=ALL-UNNAMED");
            m.put("jdk.module.addopens.2", "java.base/java.lang=ALL-UNNAMED");

            hostEnvironment.fillSystemProperties(m);

            List<String> l = new ArrayList<>(m.size() * 2);
            m.forEach((k, v) -> {
                l.add(k);
                l.add(v);
            });
            return toStringArray(l);
        });

        intrinsic0(symbols.VM_initialize, () -> {
            // ugyanaz mint máshol a registerNatives(), nem értem hogy itt miért hívják máshogy
            return null;
        });

        intrinsic0(symbols.Runtime_maxMemory, () -> 10_000_000L);
        intrinsic0(symbols.Unsafe_storeFence, () -> null);
        intrinsic0(symbols.Unsafe_loadFence, () -> null);
        intrinsic0(symbols.Unsafe_fullFence, () -> null);
        intrinsic2(symbols.Unsafe_shouldBeInitialized0, (u, c) -> {
            return !isInitialized((Clazz) fromClass((ClassObj) c)) ? 1 : 0;
        });
        intrinsic0(symbols.method(symbols.System, "nanoTime", J), () -> 43211234L);
        intrinsic0(symbols.method(symbols.System, "currentTimeMillis", J), () -> 12344321L);

        // Nem stimmel valami ezzel a függvénnyel. Lekérdezné a "java.util.secureRandomSeed" system property-t.
        // De a system property-ket egy Hashtable-be rakja bele a System.initPhase1, ami használja ConcurrentHashMapet,
        // ami használja ThreadLocalRandomot és így RandomSupportot. Tehát ennek nem kéne működnie normál JDK-n sem.
        // Meg kérdezni tőlük, hogy náluk miért működik mégis.
        intrinsic0(symbols.RandomSupport_secureRandomSeedRequested, () -> 0);

        intrinsic0(symbols.Thread_currentThread, () -> readStaticField(symbols.Fiber_currentThread));
        intrinsic0(symbols.Thread_currentCarrierThread, () -> readStaticField(symbols.Fiber_currentThread));
        intrinsic2(symbols.Thread_setPriority0, (thread, priority) -> null);
        linkAllMethods(symbols.FileInputStream, symbols.OldFileIO);
        linkAllMethods(symbols.FileOutputStream, symbols.OldFileIO);
        linkAllMethods(symbols.FileDescriptor, symbols.OldFileIO);
        intrinsic1(symbols.System_setIn0, newIn -> {
            writeStaticField(symbols.System_in, newIn);
            return null;
        });
        intrinsic1(symbols.System_setOut0, newOut -> {
            writeStaticField(symbols.System_out, newOut);
            return null;
        });
        intrinsic1(symbols.System_setErr0, newErr -> {
            writeStaticField(symbols.System_err, newErr);
            return null;
        });

        intrinsics.put(symbols.DefaultFileSystem_getFileSystem, args -> execute(symbols.NioDelegatingFileSystem_create));

        intrinsic0(symbols.ScopedMemoryAccess_registerNatives, () -> null);
        intrinsic1(symbols.Signal_findSignal0, signalNameObj -> {
            return switch (fromStringObj((ClassObj) signalNameObj)) {
                case "HUP" -> 1;
                case "INT" -> 2;
                case "QUIT" -> 3;
                case "ILL" -> 4;
                case "TRAP" -> 5;
                case "ABRT" -> 6;
                case "BUS" -> 7;
                case "FPE" -> 8;
                case "KILL" -> 9;
                case "ALRM" -> 14;
                case "TERM" -> 15;
                default -> -1;
            };
        });
        intrinsic2(symbols.Signal_handle0, (sig, nativeH) -> {
            // TODO
            return 0L;
        });
        if (symbols.Win32ErrorMode_setErrorMode != null)
            intrinsic0(symbols.Win32ErrorMode_setErrorMode, () -> 0L);
        intrinsic1(symbols.Object_notifyAll, obj -> {
            // TODO
            return null;
        });

        intrinsic0(symbols.DefaultFileSystemProvider_clinit, () -> null);
        intrinsics.put(symbols.DefaultFileSystemProvider_instance, args -> {
            ExecutionResult r = ensureInitialized(symbols.EmptyFileSystemProvider);
            if (r != null) return r;
            return new ExecutionResult(readStaticField(symbols.EmptyFileSystemProvider_INSTANCE), null);
        });
        intrinsics.put(symbols.DefaultFileSystemProvider_theFileSystem, args -> {
            ExecutionResult r = ensureInitialized(symbols.EmptyFileSystemProvider);
            if (r != null) return r;
            return new ExecutionResult(readStaticField(symbols.EmptyFileSystemProvider_FS_INSTANCE), null);
        });
        intrinsics.put(symbols.method(symbols.OldFileIO, "setFD", V, symbols.FileDescriptor, I, J), args -> {
            ((ClassObj) args[0]).writeField(symbols.FileDescriptor_fd, args[1]);
            ((ClassObj) args[0]).writeField(symbols.FileDescriptor_handle, args[2]);
            return new ExecutionResult(null, null);
        });
        intrinsic1(symbols.method(compContext.findClass("jdk/internal/loader/BootLoader"), "loadLibrary", V, symbols.String), libName -> null);
        intrinsic2(symbols.method(symbols.SystemModuleFinders_SystemModuleReader, "containsImageLocation", Z, symbols.String), (reader, name) -> {
            return 0;
        });
        intrinsic2(symbols.method(symbols.SystemModuleFinders_SystemModuleReader, "read", symbols.Optional, symbols.String), (reader, name) -> {
            final String s = fromStringObj((ClassObj) name);
            System.out.println("SMR: " + s);
            try (InputStream in = ClassLoader.getSystemResourceAsStream(s)) {
                if (in == null) return execute(symbols.Optional_empty).orElseThrow();
                else {
                    byte[] bytes = in.readAllBytes();
                    Array a = createArray(BYTE_ARRAY, bytes.length);
                    for (int i = 0; i < bytes.length; i++)
                        a.writeElement(i, bytes[i]);
                    ClassObj bb = (ClassObj) execute(symbols.ByteBuffer_wrap, a).orElseThrow();
                    return execute(symbols.Optional_of, bb).orElseThrow();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        //intrinsic1(symbols.PlatformLayouts_pick, a -> a);

        // TODO ennek a kettőnek meg kéne hívnia Reflection.ensureNativeAccesst is
        intrinsic0(symbols.CLinker_getInstance, () -> readStaticField(symbols.CLinkerImpl_INSTANCE));
        intrinsic0(symbols.CLinker_systemLookup, () -> readStaticField(symbols.CLinkerImpl_SYSTEM_SYMBOL_LOOKUP));
        intrinsic0(symbols.method(symbols.NativeEntryPoint, "registerNatives", V), () -> null);

        intrinsic0(symbols.method(compContext.findClass("java/lang/ref/Reference$1"), "startThreads", V), () -> null);

        Method FUMImpl_getPath = symbols.method(compContext.findClass(FileURLMapperImpl.class), "getPath", symbols.String, symbols.URL);
        Method FUMImpl_exists = symbols.method(compContext.findClass(FileURLMapperImpl.class), "exists", Z, symbols.URL);
        Clazz FUM = compContext.findClass("jdk/internal/loader/FileURLMapper");
        Field FUM_url = symbols.field(FUM, "url", symbols.URL);
        intrinsic1(symbols.method(FUM, "getPath", symbols.String), obj -> execute(FUMImpl_getPath, ((ClassObj) obj).readField(FUM_url)).orElseThrow());
        intrinsic1(symbols.method(FUM, "exists", Z), obj -> execute(FUMImpl_exists, ((ClassObj) obj).readField(FUM_url)).orElseThrow());
    }

    private void reflectivelyUsedField(Field f) {
        reflectivelyUsedMembers.add(f);
        // ez csak addig volt igaz, míg a statikusakat is beleszámoltuk:
        // assert f.index - f.clazz.ancestorFieldCount == f.clazz.fields.indexOf(f);
    }

    @Nonnull
    private StringBuilder makeStackTrace(List<ClassObj> dst) {
        StringBuilder stackTrace2 = new StringBuilder();
        for (CallFrame call : stackFrames(true)) {
            ClassObj stackTraceElement = createObject(symbols.StackTraceElement);
            String className = call.method().clazz.name.replace('/', '.');
            stackTraceElement.writeField(symbols.StackTraceElement_declaringClass, fromString(className));
            stackTraceElement.writeField(symbols.StackTraceElement_methodName, fromString(call.method().name));
            //stackTraceElement.writeField(symbols.StackTraceElement_fileName, call.method.name);
            stackTraceElement.writeField(symbols.StackTraceElement_lineNumber, -1);
            dst.add(stackTraceElement);
            stackTrace2.append("\n   ").append(className).append(".").append(call.method().name);
        }
        return stackTrace2;
    }

    protected String stackTrace() {
        return makeStackTrace(new ArrayList<>()).toString();
    }

    // ez konstruktorokra nem működik
    public Method methodFromReflectionObject(ClassObj reflectionObject) {
        return (Method) compContext.memberFromGlobalID((int) reflectionObject.readField(symbols.Method_slot));
    }

    public Method methodFromConstructorReflectionObject(ClassObj reflectionObject) {
        return (Method) compContext.memberFromGlobalID((int) reflectionObject.readField(symbols.Constructor_slot));
    }

    public int fieldIndexFromReflectionObj(ClassObj fieldObj) {
        return (int) fieldObj.readField(symbols.Field_slot);
    }

    private void linkAllMethods(Clazz from, Clazz to) {
        for (MethodNode method : from.methods) {
            Method m = (Method) method;
            if ((m.access & ACC_NATIVE) != 0) {
                Method replacement = compContext.findMethodOrFail(to, m.name, m.type().withArgTypes(m.fullArgTypes()));
                intrinsics.put(m, args -> execute(replacement, args));
            }
        }
    }

    public void addReplacements(Class<?> replacement, Clazz replacedClass) {
        Clazz replacementClazz = compContext.findClass(replacement);
        for (java.lang.reflect.Method m : replacement.getDeclaredMethods()) {
            MethodType methodType = MethodType.parse(java.lang.invoke.MethodType.methodType(m.getReturnType(), m.getParameterTypes()).toMethodDescriptorString(), replacedClass.lookup);
            Method replacedMethod = compContext.findMethodOrFail(replacedClass, m.getName(), methodType);
            Method replacementMethod = compContext.findMethodOrFail(replacementClazz, m.getName(), methodType);
            intrinsics.put(replacedMethod, args -> {
                return execute(replacementMethod, args);
            });
        }
    }

    private Clazz getNestHost(Clazz t) {
        if (t.nestHostClass == null) return t;
        else return t.lookup.findClass(t.nestHostClass);
    }

    private Array toStringArray(List<String> s) {
        return createArray(symbols.stringArray, s.stream().map(this::fromString).toArray());
    }

    private Object rawGetFromArray(Type expectedType, Array array, int byteOffset) {
        if (expectedType instanceof ReferenceType || expectedType == array.type().elementType())
            return array.readElement(Math.divideExact(byteOffset, array.type().elementType().bytesSize()));

        return switch ((PrimitiveType) expectedType) {
            case B -> getByteFromArray(array, byteOffset);
            case S -> (short) (getByteFromArray(array, byteOffset) | getByteFromArray(array, byteOffset + 1) << 8);
            case I ->
                    (int) (getByteFromArray(array, byteOffset) | getByteFromArray(array, byteOffset + 1) << 8 | getByteFromArray(array, byteOffset + 2) << 16 | getByteFromArray(array, byteOffset + 3) << 24);
            case J ->
                    (long) (getByteFromArray(array, byteOffset) | (long) getByteFromArray(array, byteOffset + 1) << 8 | (long) getByteFromArray(array, byteOffset + 2) << 16 | (long) getByteFromArray(array, byteOffset + 3) << 24 | (long) getByteFromArray(array, byteOffset + 4) << 32 | (long) getByteFromArray(array, byteOffset + 5) << 40 | (long) getByteFromArray(array, byteOffset + 6) << 48 | (long) getByteFromArray(array, byteOffset + 7) << 56);
            default -> throw new UnsupportedOperationException("get " + expectedType + " from " + array.type());
        };
    }

    private void rawPutToArray(Type expectedType, Array array, int byteOffset, Object obj) {
        if (expectedType instanceof ReferenceType || expectedType == array.type().elementType()) {
            array.writeElement(Math.divideExact(byteOffset, array.type().elementType().bytesSize()), obj);
            return;
        }

        switch ((PrimitiveType) expectedType) {
            case B -> putByteToArray(array, byteOffset, (byte) obj);
            case I -> {
                int i = (int) obj;
                putByteToArray(array, byteOffset, i);
                putByteToArray(array, byteOffset + 1, i >> 8);
                putByteToArray(array, byteOffset + 2, i >> 16);
                putByteToArray(array, byteOffset + 3, i >> 24);
            }
            default -> throw new UnsupportedOperationException("get " + expectedType + " from " + array.type());
        }
    }

    private int getByteFromArray(Array array, int offset) {
        return switch ((PrimitiveType) array.type().elementType()) {
            case B -> (byte) array.readElement(offset) & 0xFF;
            default -> throw new UnsupportedOperationException("get byte from " + array.type().displayName());
        };
    }

    private void putByteToArray(Array array, int offset, int b) {
        switch ((PrimitiveType) array.type().elementType()) {
            case B -> array.writeElement(offset, (byte) b);
            default -> throw new UnsupportedOperationException("put byte to " + array.type().displayName());
        }
    }

    private void registerUnsafeGet(String name, Type type) {
        intrinsics.put(symbols.method(symbols.Unsafe, name, type, symbols.Object, J), args -> {
            Obj obj = (Obj) args[1];
            Object val;
            int offset = Math.toIntExact((long) args[2]);
            if (obj instanceof Array array) {
                val = rawGetFromArray(type, array, offset - compContext.addressingMode.arrayElementOffset);
            } else {
                Field f1;
                if (isStaticFieldRef((ClassObj) obj, offset)) {
                    f1 = staticFieldFromUnsafeOffset(offset, (ClassObj) obj);
                    val = readStaticField(f1);
                } else {
                    f1 = compContext.fieldFromAddress(((ClassObj) obj).type(), offset);
                    val = ((ClassObj) obj).readField(f1);
                    assert !(val instanceof Obj co) || f1.type().isAssignableFrom(co.type()) : "field read returned " +
                            "object with wrong type: " + f1 + ", " + ((ClassObj) val).type() + ", " + val + ", " + obj;
                }
            }
            assert val == null || (val instanceof Obj) == (type instanceof Clazz || type instanceof ArrayType) : val + ", " + type + ", " + offset + ", " + obj;
            return new ExecutionResult(convertShortNumericsToInt(getOrDefault(val, type)), null);
        });
    }

    private void registerUnsafePut(String name, Type type) {
        intrinsics.put(symbols.method(symbols.Unsafe, name, V, symbols.Object, J, type), args -> {
            int fieldOffset = Math.toIntExact((long) args[2]);
            Obj obj = (Obj) args[1];
            Object value = args[3];
            assert (value == null || value instanceof Obj) == (type instanceof Clazz || type instanceof ArrayType);
            if (obj instanceof Array array)
                rawPutToArray(type, array, fieldOffset - compContext.addressingMode.arrayElementOffset, value);
            else {
                int fieldIndex = Math.toIntExact(fieldOffset);
                Field f1;
                if (isStaticFieldRef((ClassObj) obj, fieldIndex)) {
                    f1 = staticFieldFromUnsafeOffset(fieldIndex, (ClassObj) obj);
                    writeStaticField(f1, value);
                } else {
                    f1 = compContext.fieldFromAddress(((ClassObj) obj).type(), fieldIndex);
                    Field f = f1;
                    ((ClassObj) obj).writeField(f, value);
                }
            }
            return new ExecutionResult(null, null);
        });
    }

    // paraméterek: Unsafe-stílusú address
    private boolean isStaticFieldRef(ClassObj obj, int fieldIndex) {
        if (compContext.addressingMode == AddressingMode.JS)
            return compContext.memberFromGlobalID(fieldIndex).isStatic();
        else
            return obj.type().knownClass == KnownClass.CLASS && fieldIndex >= CompilationContext.MEMBER_NUMBER_MIN;
    }

    private Field staticFieldFromUnsafeOffset(int offset, ClassObj obj) {
        Field f1;
        f1 = (Field) compContext.memberFromGlobalID(offset);
        if (compContext.addressingMode != AddressingMode.JS)
            // JS esetén valami StaticsImpl vacak van, nem a rendes class van obj-ban
            assert f1.clazz == obj.representedData();
        return f1;
    }

    private void registerCAS(String name, Type type) {
        intrinsics.put(symbols.method(symbols.Unsafe, name, Z, symbols.Object, J, type, type), args -> {
            int fieldOffset = Math.toIntExact((long) args[2]);
            Object expectedVal = args[3], updateVal = args[4];
            Obj obj = (Obj) args[1];
            if (obj instanceof Array array) {
                fieldOffset -= compContext.addressingMode.arrayElementOffset;
                fieldOffset /= arrayIndexScale(array.type());
                Object prevVal = getOrDefault(array.readElement(fieldOffset), type);
                if (Objects.equals(convertShortNumericsToInt(prevVal), expectedVal)) {
                    array.writeElement(fieldOffset, updateVal);
                    return new ExecutionResult(1, null);
                } else return new ExecutionResult(0, null);
            } else {
                ClassObj co = (ClassObj) obj;
                int fieldIndex = Math.toIntExact(fieldOffset);
                if (isStaticFieldRef(co, fieldIndex)) {
                    Field f = staticFieldFromUnsafeOffset(fieldIndex, co);
                    Object prevVal = getOrDefault(readStaticField(f), type);
                    if (Objects.equals(convertShortNumericsToInt(prevVal), expectedVal)) {
                        writeStaticField(f, updateVal);
                        return new ExecutionResult(1, null);
                    } else return new ExecutionResult(0, null);
                } else {
                    Field f = compContext.fieldFromAddress(co.type(), fieldIndex);
                    Object prevVal = getOrDefault(co.readField(f), type);
                    if (Objects.equals(convertShortNumericsToInt(prevVal), expectedVal)) {
                        co.writeField(f, updateVal);
                        return new ExecutionResult(1, null);
                    } else return new ExecutionResult(0, null);
                }
            }
        });
    }

    private static int arrayIndexScale(ArrayType t) {
        return t.elementType().bytesSize();
    }

    public void intrinsic0(Method m, Supplier<Object> f) {
        intrinsics.put(m, a -> new ExecutionResult(f.get(), null));
    }

    public void intrinsic1(Method m, Function<Object, Object> f) {
        intrinsics.put(m, a -> new ExecutionResult(f.apply(a[0]), null));
    }

    public void intrinsic2(Method m, BiFunction<Object, Object, Object> f) {
        intrinsics.put(m, a -> new ExecutionResult(f.apply(a[0], a[1]), null));
    }

    public void intrinsic3(Method m, Intrinsic3 f) {
        intrinsics.put(m, a -> new ExecutionResult(f.execute(a[0], a[1], a[2]), null));
    }

    public interface Intrinsic3 {
        Object execute(Object arg1, Object arg2, Object arg3);
    }

    protected ExecutionResult throwException(Clazz exceptionClass) {
        return throwException(exceptionClass, "");
    }

    protected ExecutionResult throwException(Clazz exceptionClass, String message) {
        ClassObj obj = createObject(exceptionClass);
        Method constructor = symbols.method(exceptionClass, "<init>", V, symbols.String);
        execute(constructor, obj, fromString(message)).orElseThrow();
        return throwException(obj);
    }

    protected ExecutionResult throwException(ClassObj exceptionObj) {
        // ClassObj msgObj = (ClassObj) exceptionObj.readField(symbols.Throwable_detailMessage);
        // String msg = msgObj == null ? null : fromStringObj(msgObj);
        // Array backtrace = (Array) exceptionObj.readField(symbols.Throwable_backtrace);
        // throw new RuntimeException(exceptionObj.type().name + ": " + msg + exceptionObj.representedData());
        if (exceptionObj instanceof WrappedException)
            throw (WrappedException) exceptionObj;
        else
            throw new WrappedException(exceptionObj, this);
    }

    public abstract ExecutionResult execute(Method method, Object... args);

    public ExecutionResult executeVirtual(Method method, Object... args) {
        return execute(compContext.findMethodOrFail(((ClassObj) args[0]).type(), method.name, method.type()), args);
    }

    public ExecutionResult executeWithVarargs(Method method, Object... args) {
        if ((method.access & ACC_VARARGS) == 0) return execute(method, args);

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


    protected List<CallFrame> stackFrames(boolean forException) {
        List<CallFrame> cf = stackFramesImpl(forException);
        if (prependedCallFrame != null) {
            cf = new ArrayList<>(cf);
            cf.add(prependedCallFrame);
        }
        return cf;
    }

    protected abstract List<CallFrame> stackFramesImpl(boolean forException);

    public boolean shouldLoadViaAppClassLoader(String className) {
        if (className.startsWith("com/flyordie/code/runtime/"))
            // mert pl. OldFileIO szükséges ahhoz hogy AppClassLoader működjön, ezért
            // ezeket nem lehet az appclassloaderrel betölteni
            return false;

        final int slash = className.lastIndexOf('/');
        if (slash == -1) return true;
        String packageName = className.substring(0, slash).replace('/', '.');
        return !modulesByPackages.containsKey(packageName);
    }

    public MethodType methodHandleType(ClassObj mh) {
        return fromMethodType((ClassObj) executeVirtual(symbols.MethodHandle_type, mh).orElseThrow());
    }

    public int identityHashCode(Obj co) {
        // a | 2 azért kell, mert a CReplacementProvider-ben lévő identityhashcode
        // hashcode==0-t úgy tekinti, hogy nincs még kiszámítva a hashcode

        // viszont LSB-t le kell szednünk, mert azt meg GC marknak használja

        return (co.hashCode() | 2) & ~1;
    }

    protected interface CallFrame {

        Method method();
    }

    protected IndyLinkResult linkIndy(Clazz caller, Handle bsm, String name, MethodType methodType, Object[] bsmArgs) {
        ClassObj callerObj = fromType(caller);
        ClassObj bootstrapMethodObj = toMethodHandle(bsm, caller);
        ClassObj nameObj = fromString(name);
        ClassObj typeObj = toMethodType(methodType);
        Array staticArguments = createArray(symbols.objectArray, bsmArgs.length);
        for (int i = 0; i < bsmArgs.length; i++)
            staticArguments.writeElement(i, boxIfNeeded(constant(bsmArgs[i], caller)));
        Array appendixResult = createArray(symbols.objectArray, 1);

        // System.out.println("LINK INDY " + caller + ", " + bsm + ", " + name + ", " + methodType + ", " + Arrays.asList(bsmArgs));
        ClassObj memberName = (ClassObj) execute(symbols.MethodHandleNatives_linkCallSite, callerObj, bootstrapMethodObj, nameObj, typeObj, staticArguments, appendixResult).orElseThrow();
        Obj appendix = (Obj) appendixResult.readElement(0);

        if (memberNameRefKind(memberName) != H_INVOKESTATIC)
            throw new UnsupportedOperationException();

        return new IndyLinkResult((Method) memberName.representedData(), appendix);
    }

    protected record IndyLinkResult(Method method, Obj appendix) {
    }

    private ClassObj toMemberName(Method m, int refKind) {
        assert ((refKind & 1) == 0) == ((m.access & ACC_STATIC) != 0);
        ClassObj co = interned(m, () -> {
            ClassObj obj = createObject(symbols.MemberName);
            fillMemberName(m, refKind, obj);
            return obj;
        });
        assert (((int) co.readField(symbols.MemberName_flags)) >>> 24 & 0xF) == refKind;
        return co;
    }

    private void fillMemberName(Method m, int refKind, ClassObj obj) {
        obj.representedData(m);
        obj.writeField(symbols.MemberName_clazz, fromType(m.clazz));
        obj.writeField(symbols.MemberName_name, fromString(m.name));
        obj.writeField(symbols.MemberName_type, toMethodType(m.type()));
        obj.writeField(symbols.MemberName_flags, m.access | refKind << 24 | (m.name.equals("<init>") ? 0x00020000 /* MN_IS_CONSTRUCTOR */ : 0x00010000 /* MN_IS_METHOD */));
        prepareMemberName(obj, m, refKind);
    }

    protected void prepareMemberName(ClassObj co, Method m, int refKind) {
    }

    private void fillMemberName(Field f, int refKind, ClassObj obj) {
        if (refKind == H_GETFIELD && f.isStatic())
            // DMH.createFunctionben UNSAFE static mezőt akar getfield-ként resolveolni.
            // lehet hogy meg kéne kérdezni OpenJDK listán hogy mi a fene ez.
            // egyelőre kijavítjuk getstaticra.
            refKind = H_GETSTATIC;

        obj.representedData(f);
        obj.writeField(symbols.MemberName_clazz, fromType(f.clazz));
        obj.writeField(symbols.MemberName_name, fromString(f.name));
        obj.writeField(symbols.MemberName_type, fromType(f.type()));
        obj.writeField(symbols.MemberName_flags, f.access | refKind << 24 | 0x00040000 /* MN_IS_FIELD */);
    }

    public ClassObj toMethodType(MethodType t) {
        return interned(t, () -> {
            Array paramTypeArray = createArray(new ArrayType(symbols.Class), t.parameterTypes().size());
            for (int i = 0; i < t.parameterTypes().size(); i++)
                paramTypeArray.writeElement(i, fromType(t.parameterTypes().get(i)));
            ClassObj retType = fromType(t.returnType());

            return (ClassObj) execute(symbols.MethodType_makeImpl, retType, paramTypeArray, 1).orElseThrow();
        });
    }

    private MethodType fromMethodType(ClassObj typeObj) {
        if (typeObj.representedData() != null) return (MethodType) typeObj.representedData();

        Type returnType = (Type) ((ClassObj) typeObj.readField(symbols.MethodType_rtype)).representedData();
        Array paramTypesArray = (Array) typeObj.readField(symbols.MethodType_ptypes);
        Type[] paramTypes = new Type[paramTypesArray.length()];
        for (int i = 0; i < paramTypes.length; i++)
            paramTypes[i] = fromClass((ClassObj) paramTypesArray.readElement(i));
        return new MethodType(Arrays.asList(paramTypes), returnType);
    }

    public Type fromClass(ClassObj javaLangClass) {
        // assert javaLangClass.type().name.equals("java/lang/Class") : javaLangClass.type().name; lehet j.l.Class replacementje is
        Type representedData = (Type) javaLangClass.representedData();
        assert representedData != null;
        return representedData;
    }

    public ClassObj fromType(Type t) {
        if (t == null) return null;

        return adapters.fromNative(t);
    }

    public ClassObj fromString(String s) {
        // lehet hogy adaptersben kéne csinálni univerzális nulltoleranciát
        if (s == null) return null;

        return interned(s, () -> {
            ClassObj obj = createObject(symbols.String, s);

            Array byteArray = createArray(ArrayType.BYTES, s.length());
            obj.writeField(symbols.String_coder, (byte) 0); // LATIN1
            obj.writeField(symbols.String_value, byteArray);
            for (int i = 0; i < s.length(); i++) {
                char ch = s.charAt(i);
                if (ch > 255) {
                    obj.writeField(symbols.String_coder, (byte) 1); // UTF16
                    Array charArray = createArray(ArrayType.BYTES, s.length() * 2);
                    for (int j = 0; j < s.length(); j++) {
                        charArray.writeElement(j * 2, (byte) (s.charAt(j) >> 8));
                        charArray.writeElement(j * 2 + 1, (byte) s.charAt(j));
                    }
                    obj.writeField(symbols.String_value, charArray);
                    break;
                }
                byteArray.writeElement(i, (byte) ch);
            }

            return obj;
        });
    }

    private Type representedClass(ClassObj javaLangClassObject) {
        return (Type) javaLangClassObject.representedData();
    }

    public String fromStringObj(ClassObj o) {
        if (o == null) // TODO ehelyett általános nullellenőrzés kéne
            return null;

        assert o.type() == symbols.String;

        if (o.representedData() != null) return (String) o.representedData();

        Array arr = (Array) o.readField(symbols.String_value);
        byte coder = (byte) o.readField(symbols.String_coder);

        return switch (coder) {
            case 0 -> { // LATIN1
                char[] c = new char[arr.length()];
                for (int i = 0; i < c.length; i++)
                    c[i] = (char) ((byte) arr.readElement(i) & 0xFF);
                yield new String(c);
            }
            case 1 -> { // UTF16
                char[] c = new char[arr.length() / 2];
                for (int i = 0; i < c.length; i++) {
                    int ch1 = (byte) arr.readElement(i * 2 + 1) & 0xFF;
                    int ch2 = (byte) arr.readElement(i * 2) & 0xFF;
                    c[i] = (char) (ch1 << 8 | ch2);
                }
                yield new String(c);
            }
            default -> throw new RuntimeException("unknown string coder: " + coder);
        };
    }

    public ClassObj toMethodHandle(Handle h, Clazz callerClass) {
        ClassObj lookupObj = createObject(symbols.Lookup);
        execute(symbols.Lookup_constructor, lookupObj, fromType(callerClass)).orElseThrow();

        ClassObj refc = fromType(compContext.findClass(h.getOwner()));
        ClassObj name = fromString(h.getName());
        ClassObj type = h.getTag() <= H_PUTSTATIC ? fromType(Type.parse(h.getDesc(), callerClass.lookup)) : toMethodType(MethodType.parse(h.getDesc(), callerClass.lookup));

        return (ClassObj) switch (h.getTag()) {
            case H_GETFIELD -> execute(symbols.Lookup_findGetter, lookupObj, refc, name, type).orElseThrow();
            case H_GETSTATIC -> execute(symbols.Lookup_findStaticGetter, lookupObj, refc, name, type).orElseThrow();
            case H_PUTFIELD -> execute(symbols.Lookup_findSetter, lookupObj, refc, name, type).orElseThrow();
            case H_PUTSTATIC -> execute(symbols.Lookup_findStaticSetter, lookupObj, refc, name, type).orElseThrow();
            case H_INVOKESTATIC -> execute(symbols.Lookup_findStatic, lookupObj, refc, name, type).orElseThrow();
            case H_INVOKEVIRTUAL, H_INVOKEINTERFACE ->
                    execute(symbols.Lookup_findVirtual, lookupObj, refc, name, type).orElseThrow();
            case H_INVOKESPECIAL ->
                // ch.qus.logback.core.joran.sanity.SanityChecker .deepFindNestedSubModelsOfType-nál jött elő
                // a lambdánál LogBack 1.4.11-re upgrade után. meg kéne nézni, hogy eddig miért nem jött elő.
                // azzal függhet össze, hogy interface default methodban volt a lambda, ez atipikus.
                    execute(symbols.Lookup_findSpecial, lookupObj, refc, name, type,
                            fromType(callerClass)).orElseThrow();
            case H_NEWINVOKESPECIAL -> {
                assert h.getName().equals("<init>");
                yield execute(symbols.Lookup_findConstructor, lookupObj, refc, type).orElseThrow();
            }
            //    case H_GETFIELD, H_GETSTATIC, H_PUTFIELD, H_PUTSTATIC ->
            //            toMemberName(compContext.findFieldOrFail(h.getOwner(), h.getName(), h.getDesc()), h.getTag());
            //    case H_INVOKEVIRTUAL, H_INVOKESTATIC, H_INVOKESPECIAL, H_NEWINVOKESPECIAL, H_INVOKEINTERFACE ->
            //            toMemberName(compContext.findMethodOrFail(h.getOwner(), h.getName(), h.getDesc()), h.getTag());
            default -> throw new UnsupportedOperationException("unknown MH kind: " + h.getTag());
        };
    }

    private ClassObj interned(Object obj, Supplier<ClassObj> s) {
        if (reflectionObjectsInProgress.contains(obj))
            throw new IllegalStateException(obj.toString());
        reflectionObjectsInProgress.add(obj);
        try {
            // computeIfAbsentet nem használhatjuk, mert lehet hogy egy kszámításához kell egy másik is
            ClassObj co = (ClassObj) reflectionObjects.get(obj);
            if (co == null) {
                co = s.get();
                reflectionObjects.put(obj, co);
            }
            return co;
        } finally {
            reflectionObjectsInProgress.remove(obj);
        }
    }

    public Object constant(Object arg, Clazz callerClass) {
        if (arg == null) return null;
        else if (arg instanceof String s) return fromString(s);
        else if (arg instanceof MethodType methodType) return toMethodType(methodType);
        else if (arg instanceof Type type) return fromType(type);
        else if (arg instanceof Byte || arg instanceof Boolean || arg instanceof Short || arg instanceof Character || arg instanceof Integer || arg instanceof Float || arg instanceof Long || arg instanceof Double || arg instanceof Obj)
            return arg;
        else if (arg instanceof Handle h) return toMethodHandle(h, callerClass);
        else throw new UnsupportedOperationException("unknown constant type: " + arg.getClass());
    }

    public Object toConstant(Obj obj) {
        if (obj instanceof Array) return obj;

        ClassObj co = (ClassObj) obj;

        if (co.type().knownClass == null) return co;

        return switch (co.type().knownClass) {
            case STRING -> fromStringObj(co);
            case CLASS -> fromClass(co);
            case METHOD_TYPE -> fromMethodType(co);
            default -> co;
        };
    }

    public Object convertForStore(Object obj, Type type) {
        if (obj instanceof Integer i) return switch ((PrimitiveType) type) {
            case Z -> intToBool(i);
            case B -> intToByte(i);
            case C -> intToChar(i);
            case S -> intToShort(i);
            case I -> i;
            default -> throw new UnsupportedOperationException("store conversion from Integer to " + type);
        };
        else return obj;
    }

    protected static boolean intToBool(int i) {
        return switch (i) {
            case 0 -> false;
            case 1 -> true;
            default -> throw new RuntimeException("logical value must be 0 or 1, but it is: " + i);
        };
    }

    protected static byte intToByte(int i) {
        assert i == (byte) i;
        return (byte) i;
    }

    protected static short intToShort(int i) {
        assert i == (short) i;
        return (short) i;
    }

    protected static char intToChar(int i) {
        assert i == (char) i;
        return (char) i;
    }

    public abstract ExecutionResult ensureInitialized(Clazz clazz);

    public abstract boolean isInitialized(Clazz clazz);

    public abstract boolean isFullyInitialized(Clazz clazz);

    public abstract void writeStaticField(Field f, Object value);

    public abstract Object readStaticField(Field f);

    public static Object convertShortNumericsToInt(Object obj) {
        if (obj instanceof Byte b) return (int) b;
        if (obj instanceof Character c) return (int) c;
        if (obj instanceof Short c) return (int) c;
        if (obj instanceof Boolean b) return b ? 1 : 0;
        return obj;
    }

    public void initialize() {
        long begin = System.nanoTime();

        registerNativeMethodImplementations();

        writeStaticField(symbols.UnsafeConstants_ADDRESS_SIZE0, 4);

        ClassObj threadGroupObj = createObject(symbols.ThreadGroup);
        execute(symbols.ThreadGroup_constructor, threadGroupObj).orElseThrow();

        ClassObj threadObj_h = createObject(symbols.Thread_FieldHolder);
        threadObj_h.writeField(symbols.Thread_FieldHolder_group, threadGroupObj);
        threadObj_h.writeField(symbols.Thread_FieldHolder_priority, Thread.NORM_PRIORITY);

        ClassObj threadObj = createObject(symbols.Thread);
        // 1-es ID-t kéne assignolni hozzá, mert a getNextThreadIdOffset 2-t ad vissza ID-tól van az ID offset
        threadObj.writeField(symbols.Thread_name, fromString("main"));
        threadObj.writeField(symbols.Thread_holder, threadObj_h);

        // System.initPhase1 majd hozzáadja a szálat a grouphoz
        writeStaticField(symbols.Fiber_currentThread, threadObj);

        ensureInitialized(symbols.Reference); // JavaLangRefAccess miatt

        execute(symbols.method(symbols.System, "initPhase1", V)).orElseThrow();

        // ez állítja be SS.JavaLangReflectAccesst. elvileg initPhase1 során inicializálódnia kéne, de
        // valamiért mégsem. ki kéne deríteni, hogy miért nem (meg hogy mitől kéne inicializálódnia).
        ensureInitialized(compContext.findClass("java/lang/reflect/AccessibleObject"));

        if ((int) execute(symbols.method(symbols.System, "initPhase2", I, Z, Z), 1, 1).orElseThrow() != 0)
            throw new RuntimeException("Initialization failed");
        execute(symbols.method(symbols.System, "initPhase3", V)).orElseThrow();

        // enélkül rekurzív staticinitializerekbe keveredik
        ensureInitialized(compContext.findClass("jdk/internal/foreign/Utils"));

        initialized = true;
        long end = System.nanoTime();
        System.out.println("Isolated environment initialized in " + (end - begin) / 1000000 + " ms");
    }

    public int memberNameRefKind(ClassObj memberName) {
        return ((int) memberName.readField(symbols.MemberName_flags)) >>> 24 & 0xF;
    }

    public Field memberNameReferredField(ClassObj memberName) {
        Clazz clazz = (Clazz) representedClass((ClassObj) memberName.readField(symbols.MemberName_clazz));
        String fieldName = fromStringObj((ClassObj) memberName.readField(symbols.MemberName_name));
        Type type = fromClass((ClassObj) memberName.readField(symbols.MemberName_type));
        return compContext.field(clazz, fieldName, type);
    }

    public Method memberNameReferredMethod(ClassObj memberName) {
        Clazz clazz = (Clazz) representedClass((ClassObj) memberName.readField(symbols.MemberName_clazz));
        String fieldName = fromStringObj((ClassObj) memberName.readField(symbols.MemberName_name));
        MethodType type = fromMethodType((ClassObj) memberName.readField(symbols.MemberName_type));
        return compContext.findMethodOrFail(clazz, fieldName, type);
    }

    public abstract Array createArray(ArrayType arrayType, int length);

    /**
     * Ez utólag is módosíthatja a paraméterként megadott tömböt!
     */
    public Array createArray(ArrayType arrayType, Object[] content) {
        Array a = createArray(arrayType, content.length);
        for (int i = 0; i < content.length; i++)
            a.writeElement(i, content[i]);
        return a;
    }

    public abstract ClassObj createObject(Clazz clazz);

    public ClassObj createObject(Clazz clazz, Object representedData) {
        ClassObj o = createObject(clazz);
        o.representedData(representedData);
        return o;
    }

    public static Object getOrDefault(Object obj, Type type) {
        if (obj != null) return obj;

        if (type instanceof ReferenceType) return null;
        else return switch ((PrimitiveType) type) {
            case I -> 0;
            case J -> 0L;
            case B -> (byte) 0;
            case S -> (short) 0;
            case C -> '\0';
            case F -> 0F;
            case D -> 0D;
            case Z -> false;
            default -> throw new IllegalArgumentException();
        };
    }

    public record ExecutionResult(Object value, Returnable returnFrom) {

        public ExecutionResult {
            assert !(value instanceof ExecutionResult) && !(value instanceof Boolean) && !(value instanceof Short) && !(value instanceof Byte) && !(value instanceof Character) : value.getClass();
        }

        ExecutionResult withoutReturn(Returnable self) {
            return returnFrom.equals(self) ? new ExecutionResult(value, null) : this;
        }

        public Object orElseThrow() {
            if (returnFrom != null) throw new RuntimeException(toString());
            return value;
        }
    }

    public static class WrappedException extends RuntimeException {

        public final ClassObj obj;
        private final String msg;

        public WrappedException(ClassObj co, Interpreter interpreter) {
            this.obj = co == null ? (ClassObj) this : co;
            this.msg = "Exception in isolated code: " + interpreter.fromStringObj((ClassObj) interpreter.executeVirtual(
                    interpreter.symbols.Object_toString, obj).orElseThrow());
        }

        @Override
        public String getMessage() {
            return msg;
        }
    }

    protected enum SpecialExecutionResult {

        CONTINUE_LOOP
    }

    public interface Obj {

        // a hashCode most hülyén van, mert Obj.hashCode nem használható, helyette
        // Interpreter.hashCode-dal lehet kiolvasni a hashCode-ot

        Type type();

        Object representedData();

        void representedData(Object obj);
    }

    public interface Array extends Obj {

        ArrayType type();

        int length();

        Object readElement(int index);

        void writeElement(int index, Object value);

        default void copy(int srcPos, Array dst, int dstPos, int length) {
            if (equals(dst) && srcPos < dstPos) for (int i = length - 1; i >= 0; i--)
                dst.writeElement(dstPos + i, readElement(srcPos + i));
            else for (int i = 0; i < length; i++)
                dst.writeElement(dstPos + i, readElement(srcPos + i));
        }
    }

    public interface ClassObj extends Obj {

        Clazz type();

        Object readField(int index);

        default Object readField(Field f) {
            return readField(f.index);
        }

        void writeField(int index, Object value);

        default void writeField(Field f, Object value) {
            // TODO ezt nem csak ide kéne, hanem másikba is
            // MatchGUI-ban PlayerScoring.active nem működik (compile time exception), ha nincs ez
            if (f.type() == PrimitiveType.Z && value instanceof Integer i)
                value = i != 0;
            writeField(f.index, value);
        }

        // DMH.asSpecial hagyatkozik arra hogy representedData-t is másoljuk clone-kor
        ClassObj cloneObject();
    }

    public boolean ensureInitialized2(Clazz clazz) {
        ExecutionResult r = ensureInitialized(clazz);
        if (r != null) {
            r.orElseThrow();
            throw new RuntimeException("should not reach here");
        }
        return isFullyInitialized(clazz);
    }

    public Obj boxIfNeeded(Object obj) {
        if (obj == null || obj instanceof Obj) return (Obj) obj;
        if (obj instanceof Boolean b) {
            ExecutionResult r = ensureInitialized(symbols.Boolean);
            return (Obj) readStaticField(b ? symbols.Boolean_TRUE : symbols.Boolean_FALSE);
        }
        if (obj instanceof Byte b) {
            ensureInitialized2(symbols.Byte);
            return (Obj) execute(symbols.Byte_valueOf, b.intValue()).orElseThrow();
        }
        if (obj instanceof Short s) {
            ensureInitialized2(symbols.Short);
            return (Obj) execute(symbols.Short_valueOf, ((Short) obj).intValue()).orElseThrow();
        }
        if (obj instanceof Character c) {
            ensureInitialized2(symbols.Character);
            return (Obj) execute(symbols.Character_valueOf, (int) c).orElseThrow();
        }
        if (obj instanceof Integer i) {
            ensureInitialized2(symbols.Integer);
            return (Obj) execute(symbols.Integer_valueOf, i).orElseThrow();
        }
        if (obj instanceof Float f) {
            ensureInitialized2(symbols.Float);
            return (Obj) execute(symbols.Float_valueOf, f).orElseThrow();
        }
        if (obj instanceof Long l) {
            ensureInitialized2(symbols.Long);
            return (Obj) execute(symbols.Long_valueOf, l).orElseThrow();
        }
        if (obj instanceof Double d) {
            ensureInitialized2(symbols.Double);
            return (Obj) execute(symbols.Double_valueOf, d).orElseThrow();
        }
        throw new IllegalArgumentException(obj.toString());
    }

    private Object unboxIfNeeded(Obj obj, Type type) {
        if (type instanceof ReferenceType) return obj;
        else return switch ((PrimitiveType) type) {
            case Z -> (boolean) ((ClassObj) obj).readField(symbols.Boolean_value) ? 1 : 0;
            case B -> (byte) ((ClassObj) obj).readField(symbols.Byte_value);
            case S -> (short) ((ClassObj) obj).readField(symbols.Short_value);
            case C -> (char) ((ClassObj) obj).readField(symbols.Character_value);
            case I -> (int) ((ClassObj) obj).readField(symbols.Integer_value);
            case F -> (float) ((ClassObj) obj).readField(symbols.Float_value);
            case J -> (long) ((ClassObj) obj).readField(symbols.Long_value);
            case D -> (double) ((ClassObj) obj).readField(symbols.Double_value);
            default -> throw new IllegalArgumentException();
        };
    }

    private String packageName(Type t) {
        if (t instanceof Clazz clazz)
            return clazz.name.substring(0, clazz.name.lastIndexOf('/')).replace('/', '.');
        else if (t instanceof PrimitiveType)
            return "java.lang"; // az a lényeg hogy java.base modulba tartozzon, mert a modul megkereséséhez használjuk ezt a metódust
        else return packageName(((ArrayType) t).endingElementType());
    }

    private final Adapters adapters = new Adapters();

    private static final MethodHandle Interpreter_createArray;
    private static final MethodHandle Array_length;
    private static final MethodHandle Array_readElement;
    private static final MethodHandle Array_writeElement;
    private static final MethodHandle Number_intValue;
    private static final MethodHandle Objects_nonNull;

    static {
        try {
            Interpreter_createArray = lookup().findVirtual(Interpreter.class, "createArray", methodType(Array.class, ArrayType.class, int.class));
            Array_readElement = lookup().findVirtual(Array.class, "readElement", methodType(Object.class, int.class));
            Array_writeElement = lookup().findVirtual(Array.class, "writeElement", methodType(void.class, int.class, Object.class));
            Array_length = lookup().findVirtual(Array.class, "length", methodType(int.class));
            Number_intValue = lookup().findVirtual(Number.class, "intValue", methodType(int.class));
            Objects_nonNull = lookup().findStatic(Objects.class, "nonNull", methodType(boolean.class, Object.class));
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    protected MethodHandle adapterN2J(Class<?> src, Type dst) {
        if (src.isArray() && dst instanceof ArrayType dstArrayType) {
            final MethodHandle elementAdapter = adapterN2J(src.getComponentType(), dstArrayType.elementType());
            return MethodHandles.guardWithTest(Objects_nonNull.asType(methodType(boolean.class, src)), MethodHandles.countedLoop(MethodHandles.arrayLength(src), MethodHandles.collectArguments(Interpreter_createArray.bindTo(this).bindTo(dst), 0, MethodHandles.arrayLength(src)), MethodHandles.permuteArguments(MethodHandles.collectArguments(identity(Array.class), 1, MethodHandles.collectArguments(Array_writeElement.asType(methodType(void.class, Array.class, int.class, elementAdapter.type().returnType())), 2, MethodHandles.collectArguments(elementAdapter, 0, MethodHandles.arrayElementGetter(src)) // (SRC[], int)DST
                            ) // (Array, int, SRC[], int)void
                    ), // (Array, Array, int, SRC[], int)Array
                    methodType(Array.class, Array.class, int.class, src), 0, 0, 1, 2, 1)), MethodHandles.dropArguments(MethodHandles.constant(Array.class, null), 0, src));
        }

        Class<?> dst2 = dst instanceof Clazz ? ClassObj.class : dst instanceof ArrayType ? Array.class : ((PrimitiveType) dst).asClass();
        if (src == dst2 || src == Obj.class && dst instanceof ReferenceType)
            return MethodHandles.identity(src);
        try {
            return lookup().findVirtual(Adapters.class, "fromNative", methodType(dst2, src)).bindTo(adapters);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("no N2J adapter: " + src.getName() + " -> " + dst);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("internal error", e);
        }
    }

    protected MethodHandle adapterJ2N(Type src, Class<?> dst) {
        if (src instanceof ArrayType srcArrayType && dst.isArray()) {
            final MethodHandle elementAdapter = adapterJ2N(srcArrayType.elementType(), dst.getComponentType());
            return MethodHandles.guardWithTest(Objects_nonNull.asType(methodType(boolean.class, Array.class)), MethodHandles.countedLoop(Array_length, MethodHandles.collectArguments(MethodHandles.arrayConstructor(dst), 0, Array_length), MethodHandles.permuteArguments(MethodHandles.collectArguments(identity(dst), 1, MethodHandles.collectArguments(MethodHandles.arrayElementSetter(dst), 2, MethodHandles.collectArguments(elementAdapter, 0, Array_readElement.asType(methodType(elementAdapter.type().parameterType(0), Array.class, int.class))) // (Array, int)DST
                            ) // (DST[], int, Array, int)void
                    ), // (DST[], Array, Array, int, SRC[], int)DST[]
                    methodType(dst, dst, int.class, Array.class), 0, 0, 1, 2, 1)), MethodHandles.dropArguments(MethodHandles.constant(dst, null), 0, Array.class));
        }

        Class<?> src2 = src instanceof Clazz clazz ? clazz == symbols.Object ? Obj.class : ClassObj.class : src instanceof ArrayType ? Array.class : ((PrimitiveType) src).asClass();
        if (src2 == boolean.class || src2 == byte.class || src2 == short.class || src2 == char.class)
            src2 = int.class;
        if (dst == Object.class || src instanceof ReferenceType && dst == Obj.class)
            return MethodHandles.identity(src2).asType(methodType(dst, src2));
        if (src2 == dst) return MethodHandles.identity(src2);
        try {
            String dstSimpleName = dst.getSimpleName();
            if (Character.isLowerCase(dstSimpleName.charAt(0))) dstSimpleName = "_" + dstSimpleName;
            return lookup().findVirtual(Adapters.class, "toNative" + dstSimpleName, methodType(dst, src2)).bindTo(adapters);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("no J2N adapter: " + src + " -> " + dst.getName());
        } catch (IllegalAccessException e) {
            throw new RuntimeException("internal error", e);
        }
    }

    private class Adapters {

        ClassObj fromNative(Type t) {
            if (t == null) return null;
            return interned(t, () -> {
                ClassObj obj = createObject(symbols.Class, t);
                if (t instanceof ArrayType arrayType)
                    obj.writeField(symbols.Class_componentType, fromType(arrayType.elementType()));

                String packageName = packageName(t);
                ClassObj module = modulesByPackages.get(packageName);
                if (module == null)
                    // ld. komment CompContext.defineClass-ban
                    module = bootLoaderUnnamedModule;
                if (obj.readField(symbols.Class_module) == null)
                    obj.writeField(symbols.Class_module, module);

                Clazz clazz = switch (t) {
                    case Clazz c -> c;
                    case ArrayType at -> at.endingElementType() instanceof Clazz c ? c : null;
                    default -> null;
                };

                if (clazz != null && false) {
                    System.out.println("make Class object for " + clazz + ": " + module);
                }

                return obj;
            });
        }

        Type toNativeType(ClassObj co) {
            if (co == null) return null;
            return fromClass(co);
        }

        boolean toNative_boolean(int i) {
            return switch (i) {
                case 0 -> false;
                case 1 -> true;
                default -> throw new RuntimeException("invalid boolean value: " + i);
            };
        }

        byte toNative_byte(int i) {
            byte b = (byte) i;
            if (i != b) throw new RuntimeException("invalid byte value: " + i);
            return b;
        }

        short toNative_short(int i) {
            short s = (short) i;
            if (i != s) throw new RuntimeException("invalid char value: " + i);
            return s;
        }

        char toNative_char(int i) {
            char s = (char) i;
            if (i != s) throw new RuntimeException("invalid char value: " + i);
            return s;
        }

        ClassObj fromNative(Clazz t) { // ezt nem lehet kitörölni?
            return fromNative((Type) t);
        }

        ClassObj fromNative(String s) {
            return fromString(s);
        }

        Clazz toNativeClazz(ClassObj obj) {
            final Type t = fromClass(obj);
            if (t instanceof Clazz c) return c;
            else throw new RuntimeException("not a class: " + t);
        }

        Method toNativeMethod(ClassObj obj) {
            assert obj.type() == symbols.Method;
            return (Method) compContext.memberFromGlobalID((int) obj.readField(symbols.Method_slot));
        }

        String toNativeString(ClassObj c) {
            return Interpreter.this.fromStringObj(c);
        }

        ClassObj fromNative(Field f) {
            ClassObj obj = createObject(symbols.Field, f);
            obj.writeField(symbols.Field_clazz, fromType(f.clazz));
            assert compContext.memberFromGlobalID(f.globalNumber) == f;
            obj.writeField(symbols.Field_slot, f.globalNumber); // ez kell Unsafe.objectFieldOffset0-nak is
            obj.writeField(symbols.Field_name, fromString(f.name));
            obj.writeField(symbols.Field_type, fromType(f.type()));
            obj.writeField(symbols.Field_modifiers, f.access);
            obj.writeField(symbols.Field_signature, fromString(f.signature));
            obj.writeField(symbols.Field_annotations, byteArray(ClassfileUtil.readFieldAttribute(f, "RuntimeVisibleAnnotations")));
            return obj;
        }

        ClassObj fromNative(Method m) {
            return interned(m, () -> { // RecordComponent.accessornak is kell, azért interned
                List<Type> paramTypes = m.type().parameterTypes();
                Array paramTypeArray = createArray(symbols.classArray, paramTypes.size());
                for (int i = 0; i < paramTypes.size(); i++)
                    paramTypeArray.writeElement(i, fromType(paramTypes.get(i)));

                ClassObj obj = createObject(symbols.Method, m);
                obj.writeField(symbols.Method_clazz, fromType(m.clazz));
                obj.writeField(symbols.Method_name, fromString(m.name));
                obj.writeField(symbols.Method_slot, m.globalNumber);
                obj.writeField(symbols.Method_parameterTypes, paramTypeArray);
                // enélkül Method.toString exceptiont dobna
                obj.writeField(symbols.Method_exceptionTypes, createArray(symbols.classArray, 0));
                obj.writeField(symbols.Method_returnType, fromType(m.type().returnType()));
                obj.writeField(symbols.Method_signature, fromString(m.signature));
                obj.writeField(symbols.Method_modifiers, m.access);
                obj.writeField(symbols.Method_annotations, byteArray(ClassfileUtil.readMethodAttribute(m, "RuntimeVisibleAnnotations")));
                obj.writeField(symbols.Method_annotationDefault, byteArray(ClassfileUtil.readMethodAttribute(m, "AnnotationDefault")));
                obj.writeField(symbols.Method_parameterAnnotations, byteArray(ClassfileUtil.readMethodAttribute(m, "RuntimeVisibleParameterAnnotations")));

                return obj;
            });
        }

        ByteBuffer toNativeByteBuffer(ClassObj obj) {
            Array byteArray = (Array) obj.readField(symbols.ByteBuffer_hb);
            int offset = (int) obj.readField(symbols.ByteBuffer_offset);
            int position = (int) obj.readField(symbols.ByteBuffer_position);
            int limit = (int) obj.readField(symbols.ByteBuffer_limit);

            // TODO ez olvasásra nem működik, csak írásra

            ByteBuffer b = ByteBuffer.allocate(limit - position);
            while (position < limit) {
                byte b2 = (Byte) byteArray.readElement(offset + position++);
                b.put(b2);
            }
            b.flip();
            obj.writeField(symbols.ByteBuffer_position, limit);
            return b;
        }
    }

    private Array byteArray(byte[] b) {
        if (b == null) return null;
        Array a = createArray(BYTE_ARRAY, b.length);
        for (int i = 0; i < b.length; i++)
            a.writeElement(i, b[i]);
        return a;
    }

    private void registerNativeMethodImplementations() {
        for (Class<?> nmiClass : NativeMethodImplementations.class.getDeclaredClasses()) {
            registerNativeMethodImplementations(nmiClass);
        }
    }

    public void registerNativeMethodImplementations(Class<?> nmiClass) {
        try {
            if (nmiClass.isAnnotation()) return;

            Clazz c;
            NativeMethodImplementations.InClass inClassAnn = nmiClass.getAnnotation(NativeMethodImplementations.InClass.class);
            if (inClassAnn != null) {
                c = compContext.findClassOrNull(inClassAnn.value());
                if (c == null)
                    if (inClassAnn.optional())
                        return;
                    else
                        compContext.findClass(inClassAnn.value()); // trigger NoSuchClassException
            } else {
                NativeMethodImplementations.In inAnn = nmiClass.getAnnotation(NativeMethodImplementations.In.class);
                if (inAnn == null)
                    throw new RuntimeException("no @" + NativeMethodImplementations.In.class.getSimpleName() + " or " + "@" + NativeMethodImplementations.InClass.class.getSimpleName() + " annotation " + "on " + nmiClass);
                c = compContext.findClass(inAnn.value().getName().replace('.', '/'));
            }

            Constructor<?>[] constructors = nmiClass.getDeclaredConstructors();
            assert constructors.length == 1;
            Constructor<?> constructor = constructors[0];
            MethodHandle instanceFactory;
            if (constructor.getParameterCount() == 1) {
                instanceFactory = lookup().findConstructor(nmiClass, methodType(void.class, NativeMethodImplementations.class));
                instanceFactory = MethodHandles.dropArguments(instanceFactory, 1, ClassObj.class);
                instanceFactory = instanceFactory.bindTo(new NativeMethodImplementations());
            } else {
                assert constructor.getParameterCount() == 2;
                MethodHandle adapter = adapterJ2N(c, constructor.getParameterTypes()[1]);
                instanceFactory = lookup().unreflectConstructor(constructor);
                instanceFactory = instanceFactory.bindTo(new NativeMethodImplementations());
                instanceFactory = MethodHandles.filterArguments(instanceFactory, 0, adapter);
            }

            for (java.lang.reflect.Method m : nmiClass.getDeclaredMethods()) {
                if (m.isAnnotationPresent(NativeMethodImplementations.Ignore.class) || m.isSynthetic()) // lambdákat kiszűrjük
                    continue;

                Method nativeMethod = null;
                for (MethodNode m2 : c.methods) {
                    if (m2.name.equals(m.getName())) {
                        if (!m.isAnnotationPresent(NativeMethodImplementations.MethodReplacement.class)
                                && (m2.access & ACC_NATIVE) == 0)
                            throw new RuntimeException("method not native: " + m2);
                        nativeMethod = (Method) m2;
                        break;
                    }
                }
                if (nativeMethod == null)
                    throw new RuntimeException("native method " + m.getName() + " (returning " + m.getReturnType() + ") not found in " + c);

                Type returnType = nativeMethod.type().returnType();

                MethodHandle mh;
                mh = lookup().unreflect(m);
                if (mh.type().returnType() != void.class)
                    mh = MethodHandles.filterReturnValue(mh, adapterN2J(mh.type().returnType(), returnType));

                MethodHandle[] argAdapters = new MethodHandle[mh.type().parameterCount()];
                argAdapters[0] = instanceFactory;
                if ((nativeMethod.access & ACC_STATIC) == 0) {
                    for (int i = 1; i < argAdapters.length; i++)
                        argAdapters[i] = adapterJ2N(nativeMethod.fullArgTypes().get(i), mh.type().parameterType(i));
                } else {
                    for (int i = 1; i < argAdapters.length; i++)
                        argAdapters[i] = adapterJ2N(nativeMethod.fullArgTypes().get(i - 1), mh.type().parameterType(i));
                }
                mh = MethodHandles.filterArguments(mh, 0, argAdapters);
                if ((nativeMethod.access & ACC_STATIC) != 0)
                    mh = MethodHandles.insertArguments(mh, 0, (Object) null);

                assert mh.type().parameterCount() == nativeMethod.fullArgTypes().size();
                mh = mh.asSpreader(Object[].class, mh.type().parameterCount());

                Class<?> retType = mh.type().returnType();
                if (retType == boolean.class)
                    mh = MethodHandles.filterReturnValue(mh, MethodHandles.guardWithTest(identity(boolean.class), MethodHandles.dropArguments(MethodHandles.constant(int.class, 1), 0, boolean.class), MethodHandles.dropArguments(MethodHandles.constant(int.class, 0), 0, boolean.class)));
                else if (retType == byte.class || retType == short.class || retType == char.class)
                    mh = mh.asType(mh.type().changeReturnType(int.class));

                mh = mh.asType(methodType(Object.class, Object[].class));

                MethodHandle finalMH = mh;
                intrinsics.put(nativeMethod, args -> {
                    try {
                        return new ExecutionResult((Object) finalMH.invokeExact(args), null);
                    } catch (Error | RuntimeException e) {
                        throw e; // TODO itt is kéne stack trace
                    } catch (Throwable e) {
                        throw new RuntimeException(e + "\nIsolated Environment Stack trace: " + stackTrace(), e);
                    }
                });
            }
        } catch (IllegalAccessException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    private class NativeMethodImplementations {

        private static final String ConstantPool = "jdk/internal/reflect/ConstantPool";
        private static final String NativeMethodAccessorImpl = "jdk/internal/reflect/NativeMethodAccessorImpl";
        private static final String Reflection = "jdk/internal/reflect/Reflection";
        private static final String Unsafe = "jdk/internal/misc/Unsafe";

        @In(Class.class)
        class ClassImpl {

            private final Type type;
            private final ClassObj obj;

            ClassImpl(ClassObj obj) {
                this.obj = obj;
                this.type = fromClass(obj);
            }

            ClassObj getConstantPool() {
                return type instanceof Clazz c ? createObject(symbols.ConstantPool, c) : null;
            }

            String getGenericSignature0() {
                return type instanceof Clazz c ? c.signature : null;
            }

            byte[] getRawAnnotations() {
                return type instanceof Clazz c ? ClassfileUtil.readClassAttribute(c, "RuntimeVisibleAnnotations") : null;
            }

            byte[] getRawTypeAnnotations() {
                return type instanceof Clazz c ? ClassfileUtil.readClassAttribute(c, "RuntimeVisibleTypeAnnotations") : null;
            }

            Clazz[] getPermittedSubclasses0() {
                // primitívek és tömbök esetén nem hívódik meg

                Clazz c = (Clazz) type;
                if (c.permittedSubclasses == null)
                    return null;

                Clazz[] sc = new Clazz[c.permittedSubclasses.size()];
                for (int i = 0; i < sc.length; i++)
                    sc[i] = c.lookup.findClass(c.permittedSubclasses.get(i));
                return sc;
            }

            Clazz[] getInterfaces0() {
                return type instanceof Clazz c ? c.superinterfaces.toArray(Clazz[]::new) : new Clazz[0];
            }

            Clazz getNestHost0() {
                // array vagy primitíven nem lehet meghívva
                return getNestHost((Clazz) type);
            }

            boolean isRecord0() {
                return type instanceof Clazz c && (c.access & ACC_RECORD) != 0;
            }

            ClassObj[] getRecordComponents0() {
                if (!(type instanceof Clazz c) || c.recordComponents == null)
                    return new ClassObj[0];
                ClassObj[] a = new ClassObj[c.recordComponents.size()];
                for (int i = 0; i < a.length; i++) {
                    RecordComponentNode rc = c.recordComponents.get(i);
                    Type rcType = Type.parse(rc.descriptor, c.lookup);
                    Method accessor = c.context.findMethodOrFail(c, rc.name, new MethodType(List.of(), rcType));
                    if (accessor.clazz != c) throw new RuntimeException();

                    ClassObj o = createObject(symbols.RecordComponent);
                    o.writeField(symbols.RecordComponent_clazz, this.obj);
                    o.writeField(symbols.RecordComponent_name, fromString(rc.name));
                    o.writeField(symbols.RecordComponent_type, fromType(rcType));
                    o.writeField(symbols.RecordComponent_accessor, adapters.fromNative(accessor));
                    o.writeField(symbols.RecordComponent_signature, fromString(rc.signature));
                    o.writeField(symbols.RecordComponent_annotations,
                            byteArray(ClassfileUtil.readRecordComponentAttribute(c,
                                    rc, "RuntimeVisibleAnnotations")));
                    o.writeField(symbols.RecordComponent_typeAnnotations,
                            byteArray(ClassfileUtil.readRecordComponentAttribute(c,
                                    rc, "RuntimeVisibleTypeAnnotations")));
                    a[i] = o;
                }
                return a;
            }

            String getSimpleBinaryName0() {
                // TODO meg kéne nézni, hogy ez JDK-ban hogy van implementálva
                String className = ((Clazz) type).name;
                String simpleName = className.substring(
                        className.lastIndexOf('$') + 1);
                return Character.isJavaIdentifierStart(simpleName.codePointAt(0)) ?
                        simpleName : null;
            }

            ClassObj getProtectionDomain0() {
                return null; // TODO
            }
        }

        @In(System.class)
        class SystemImpl {
            int identityHashCode(Obj obj) {
                // TODO NPE ha null
                return Interpreter.this.identityHashCode(obj);
            }
        }

        @In(ClassLoader.class)
        class ClassLoaderImpl {

            Clazz findLoadedClass0(String name) {
                return compContext.classes.get(name.replace('.', '/'));
            }

            ClassObj defineClass0(ClassObj loader, Clazz lookupClass, String name, byte[] b, int off, int len, ClassObj protectionDomain, boolean initialize, int flags, Obj classData) {
                // ez Lookup.defineClass által hívódik meg.
                // normális esetben (ClassLoader.defineClass) a defineClass1
                // lesz meghívva.

                // MethodHandleNatives.Constants
                boolean nestmateClass = (flags & 1) != 0;
                boolean hiddenClass = (flags & 2) != 0;
                boolean strongLoaderLink = (flags & 4) != 0;
                boolean accessVMAnnotations = (flags & 8) != 0;

                // System.out.println("defineClass0: " + name + (hiddenClass ? " (HIDDEN CLASS)" : ""));

                byte[] bytes = Arrays.copyOfRange(b, off, off + len);
                Clazz clazz = compContext.defineClass(bytes, hiddenClass);
                if (name != null && !clazz.name.equals(name.replace('.', '/')))
                    throw new RuntimeException("name mismatch: " + name + ", " + clazz.name);

                if (nestmateClass) clazz.nestHostClass = getNestHost(lookupClass).name;

                ClassObj classObj = fromType(clazz);
                // TODO ez így nem thread-safe, hogy berakjuk az intern táblába és utána módosítgatjuk
                classObj.writeField(symbols.Class_classData, classData);
                setClassLoader(classObj, loader, clazz);

                if (initialize) {
                    ExecutionResult r = ensureInitialized(clazz);
                    if (r != null) r.orElseThrow();
                }
                return classObj;
            }

            Clazz defineClass1(ClassObj loader, String name, byte[] b, int off, int len, ClassObj protectionDomain, String source) {
                Clazz clazz = compContext.defineClass(Arrays.copyOfRange(b, off, off + len), false);
                // System.out.println("defineClass1: " + clazz + ", " + loader);

                ClassObj classObj = fromType(clazz);
                setClassLoader(classObj, loader, clazz);

                return clazz;
            }

            @Ignore
            private void setClassLoader(ClassObj classObj, ClassObj loader, Clazz clazz) {
                classObj.writeField(symbols.Class_classLoader, loader);
                ClassObj module = (ClassObj) classObj.readField(symbols.Class_module);
                // eredetileg fromType állítja be, de ha nem találja
                // package név alapján a modult, akkor hülyeséget (bootLoaderUnnamedModule) állítja eb
                if (module == bootLoaderUnnamedModule && loader != null) {
                    classObj.writeField(symbols.Class_module,
                            Objects.requireNonNull(loader.readField(symbols.ClassLoader_unnamedModule), clazz.name));
                }
            }
        }

        @InClass(ConstantPool)
        class ConstantPool {

            private final Clazz clazz;

            ConstantPool(ClassObj obj) {
                clazz = (Clazz) obj.representedData();
            }

            int getSize0(Object constantPoolOop) {
                return cr().getItemCount();
            }

            Clazz getClassAt0(Object constantPoolOop, int index) {
                return compContext.findClass(cr().readClass(item(index), clazz.crCharBuffer));
            }

            Clazz getClassAtIfLoaded0(Object constantPoolOop, int index) {
                throwException(symbols.UnsupportedOperationException);
                return null;
            }

            int getClassRefIndexAt0(Object constantPoolOop, int index) {
                throwException(symbols.UnsupportedOperationException);
                return -1;
            }

            Method getMethodAt0(Object constantPoolOop, int index) {
                throwException(symbols.UnsupportedOperationException);
                return null;
            }

            Method getMethodAtIfLoaded0(Object constantPoolOop, int index) {
                throwException(symbols.UnsupportedOperationException);
                return null;
            }

            Field getFieldAt0(Object constantPoolOop, int index) {
                throwException(symbols.UnsupportedOperationException);
                return null;
            }

            Field getFieldAtIfLoaded0(Object constantPoolOop, int index) {
                throwException(symbols.UnsupportedOperationException);
                return null;
            }

            String[] getMemberRefInfoAt0(Object constantPoolOop, int index) {
                throwException(symbols.UnsupportedOperationException);
                return null;
            }

            int getNameAndTypeRefIndexAt0(Object constantPoolOop, int index) {
                throwException(symbols.UnsupportedOperationException);
                return -1;
            }

            String[] getNameAndTypeRefInfoAt0(Object constantPoolOop, int index) {
                throwException(symbols.UnsupportedOperationException);
                return null;
            }

            int getIntAt0(Object constantPoolOop, int index) {
                return cr().readInt(item(index));
            }

            long getLongAt0(Object constantPoolOop, int index) {
                return cr().readLong(item(index));
            }

            float getFloatAt0(Object constantPoolOop, int index) {
                return Float.intBitsToFloat(cr().readInt(item(index)));
            }

            double getDoubleAt0(Object constantPoolOop, int index) {
                return Double.longBitsToDouble(cr().readLong(item(index)));
            }

            String getStringAt0(Object constantPoolOop, int index) {
                // readString nincs
                return cr().readClass(item(index), clazz.crCharBuffer);
            }

            String getUTF8At0(Object constantPoolOop, int index) {
                clazz.classReader.b[1] = (byte) (index >>> 8);
                clazz.classReader.b[2] = (byte) index;
                return cr().readUTF8(1, clazz.crCharBuffer);
            }

            byte getTagAt0(Object constantPoolOop, int index) {
                return (byte) cr().readByte(cr().getItem(index) - 1);
            }

            @Ignore
            int item(int index) {
                return cr().getItem(index);
            }

            @Ignore
            private ClassReader cr() {
                return clazz.classReader;
            }
        }

        @InClass(NativeMethodAccessorImpl)
        class NativeMethodAccessorImpl {

            Obj invoke0(Method m, Obj receiver, Obj[] args) {
                if (args == null) args = new Obj[0];

                Object[] newArgs;

                List<Type> paramTypes = m.type().parameterTypes();


                if ((m.access & ACC_STATIC) == 0) {
                    if (receiver == null) {
                        throwException(symbols.NullPointerException);
                        return null;
                    }


                    newArgs = new Object[args.length + 1];
                    newArgs[0] = receiver;
                    for (int i = 0; i < args.length; i++)
                        newArgs[1 + i] = unboxIfNeeded(args[i], paramTypes.get(i));
                } else {
                    newArgs = new Object[args.length];
                    for (int i = 0; i < args.length; i++)
                        newArgs[i] = unboxIfNeeded(args[i], paramTypes.get(i));
                }

                Object r = execute(m).orElseThrow();
                return boxIfNeeded(r);
            }
        }

        @InClass(Reflection)
        class Reflection {

            boolean areNestMates(Clazz currentClass, Clazz memberClass) {
                return true; // TODO
            }
        }

        @InClass(Unsafe)
        class Unsafe {

            ClassObj allocateInstance(Clazz clazz) {
                return createObject(clazz);
            }

            void copyMemory0(Object srcBase, long srcOffset, Object destBase, long destOffset, long bytes) {
                Array a = (Array) srcBase, b = (Array) destBase;
                srcOffset -= compContext.addressingMode.arrayElementOffset;
                destOffset -= compContext.addressingMode.arrayElementOffset;
                srcOffset /= a.type().elementType().bytesSize();
                destOffset /= b.type().elementType().bytesSize();
                bytes /= b.type().elementType().bytesSize();
                a.copy(Math.toIntExact(srcOffset), b, Math.toIntExact(destOffset), Math.toIntExact(bytes));
            }
        }

        @In(TimeZone.class)
        class TimeZoneImpl {
            String getSystemTimeZoneID(String javaHome) {
                return "Europe/Budapest";
            }
        }

        @In(DefaultFileSystemProvider.class)
        class DefaultFileSystemProviderNatives {

            @SuppressWarnings("resource")
            void openFile(String name, ClassObj fc) throws IOException {
                fc.representedData(hostEnvironment.openFile(name));
            }

            @SuppressWarnings("resource")
            long exists0(String name) {
                return hostEnvironment.fileSize(name);
            }
        }

        @In(Reference.class)
        class ReferenceNatives {

            private final ClassObj reference;

            public ReferenceNatives(ClassObj reference) {
                this.reference = reference;
            }

            void clear0() {
                reference.writeField(symbols.Reference_referent, null);
            }
        }

        @In(CRC32.class)
        class CRC32Natives {

            int updateBytes0(int crc, byte[] b, int off, int len) {
                return CRC32Impl.update(crc, ByteBuffer.wrap(b, off, len));
            }
        }

        @InClass("jdk/internal/loader/BootLoader")
        class BootLoaderNatives {
            void setBootLoaderUnnamedModule0(ClassObj module) {
                assert module != null;
                Interpreter.this.bootLoaderUnnamedModule = module;
                for (Map.Entry<Object, ClassObj> e : reflectionObjects.entrySet()) {
                    if (!(e.getKey() instanceof Type t)) continue;
                    System.out.println("sblum " + t.displayName());
                    ClassObj co = e.getValue();
                    if (co.readField(symbols.Class_module) != null)
                        throw new RuntimeException("Already has module: " + t);
                    co.writeField(symbols.Class_module, module);
                }
                reflectionObjects.forEach((k, v) -> {
                    assert !(k instanceof Type) || v.readField(symbols.Class_module) == module;
                });
            }

            String getSystemPackageLocation(String packageName) {
                ClassObj co = modulesByPackages.get(packageName);
                return co == null ? null : ((ModuleInfo) co.representedData()).location;
            }
        }

        @In(Module.class)
        class ModuleImpl {

            void defineModule0(ClassObj module, boolean isOpen, String version, String location, Obj[] pns) {
                module.representedData(new ModuleInfo(location));
                System.out.println("Define module: " + fromStringObj((ClassObj) module.readField(symbols.Module_name)));

                Set<String> packageNames = Stream.of(pns).map(o -> fromStringObj((ClassObj) o)).collect(toSet());
                for (String pkg : packageNames)
                    modulesByPackages.put(pkg, module);

                reflectionObjects.forEach((k, v) -> {
                    if (!(k instanceof Type t)) return;

                    if (packageNames.contains(packageName(t))) {
                        if (v.readField(symbols.Class_module) != bootLoaderUnnamedModule)
                            throw new RuntimeException("Already has module: " + t + ", " + bootLoaderUnnamedModule + ", " + v.readField(symbols.Class_module));
                        v.writeField(symbols.Class_module, module);
                    }
                });
            }

            void addReads0(ClassObj from, ClassObj to) {
            }

            void addExports0(ClassObj from, String pn, ClassObj to) {
            }

            void addExportsToAll0(ClassObj from, String pn) {
            }

            void addExportsToAllUnnamed0(ClassObj from, String pn) {
            }
        }

        @In(java.lang.reflect.Field.class)
        class FieldImpl {

            private final Field f;

            public FieldImpl(ClassObj obj) {
                f = (Field) compContext.memberFromGlobalID((int) obj.readField(symbols.Field_slot));
            }

            byte[] getTypeAnnotationBytes0() {
                return ClassfileUtil.readFieldAttribute(f, "RuntimeVisibleTypeAnnotations");
            }
        }

        @In(java.lang.reflect.Executable.class)
        class ExecutableImpl {

            private final Method m;
            private final ClassObj reflectionObject;

            public ExecutableImpl(ClassObj reflectionObject) {
                this.reflectionObject = reflectionObject;
                Clazz c = (Clazz) fromClass((ClassObj) reflectionObject.readField(symbols.Method_clazz));
                if (reflectionObject.type() == symbols.Method)
                    m = (Method) compContext.memberFromGlobalID((int) reflectionObject.readField(symbols.Method_slot));
                else {
                    assert reflectionObject.type() == symbols.Constructor;
                    m = (Method) compContext.memberFromGlobalID((int) reflectionObject.readField(symbols.Constructor_slot));
                }
            }

            byte[] getTypeAnnotationBytes0() {
                return ClassfileUtil.readMethodAttribute(m, "RuntimeVisibleTypeAnnotations");
            }

            Array getParameters0() {
                int paramCount;
                if (m.parameters == null) if (m.type().parameterTypes().isEmpty()) paramCount = 0;
                else throw new RuntimeException("no parameter info for " + m);
                else paramCount = m.parameters.size();
                Array array = createArray(new ArrayType(symbols.Parameter), paramCount);
                for (int i = 0; i < paramCount; i++) {
                    ParameterNode p2 = m.parameters.get(i);
                    ClassObj p = createObject(symbols.Parameter, null);
                    p.writeField(symbols.Parameter_name, fromString(p2.name));
                    p.writeField(symbols.Parameter_modifiers, p2.access);
                    p.writeField(symbols.Parameter_executable, reflectionObject);
                    p.writeField(symbols.Parameter_index, i);
                    array.writeElement(i, p);
                }
                return array;
            }
        }

        /*
        @InClass("java/net/InetAddressImplFactory")
        class InetAddressImplFactoryImpl {
            boolean isIPv6Supported() {
                return true;
            }
        }*/

        @In(java.lang.reflect.Array.class)
        class ArrayImpl {

            int getLength(Obj array) {
                return ((Array) array).length();
            }

            Obj newArray(Type componentType, int length) {
                if (length < 0) {
                    throwException(symbols.NegativeArraySizeException, "negative length: " + length);
                    throw new RuntimeException("should not reach here");
                }
                return createArray(new ArrayType(componentType), length);
            }

            Obj multiNewArray(Type componentType, int[] dimensions) {
                if (dimensions.length == 0) {
                    throwException(compContext.findClass(KnownClass.IllegalArgumentException));
                    throw new RuntimeException("should not reach here");
                }
                if (dimensions.length == 1) return newArray(componentType, dimensions[0]);

                Type t = componentType;
                for (int dimension : dimensions) {
                    if (dimension < 0) {
                        throwException(symbols.NegativeArraySizeException, "negative length: " + Arrays.toString(dimensions));
                        throw new RuntimeException("should not reach here");
                    }
                    t = new ArrayType(t);
                }
                ArrayType rootType = (ArrayType) t;

                int[] indices = new int[dimensions.length - 1];
                Array[] arrays = new Array[indices.length];
                arrays[0] = createArray(rootType, dimensions[0]);
                int currentDepth = 0;
                while (true) {
                    while (indices[currentDepth] == dimensions[currentDepth]) {
                        currentDepth--;
                        if (currentDepth == -1) return arrays[0];
                    }
                    Array a = arrays[currentDepth];
                    Array subarray = createArray((ArrayType) a.type().elementType(), dimensions[currentDepth + 1]);
                    a.writeElement(indices[currentDepth], subarray);
                    if (currentDepth < indices.length - 1) {
                        currentDepth++;
                        indices[currentDepth] = 0;
                    } else indices[currentDepth]++;
                }
            }
        }

        @In(OldFileIO.class)
        class OldFileIOImpl {

            void stdio(ClassObj obj, int i) {
                obj.representedData(switch (i) {
                    case 0 -> hostEnvironment.stdin();
                    case 1 -> hostEnvironment.stdout();
                    case 2 -> hostEnvironment.stderr();
                    default -> throw new RuntimeException("should not reach here");
                });
            }
        }

        @In(HostProvidedFileChannel.class)
        class HostProvidedFileChannelImpl {

            private final FileChannel fc;

            public HostProvidedFileChannelImpl(ClassObj co) {
                fc = (FileChannel) co.representedData();
                assert fc != null : co;
            }

            // TODO interruptok?

            int read0(ClassObj dst) throws IOException {
                Array byteArray = (Array) dst.readField(symbols.ByteBuffer_hb);
                int offset = (int) dst.readField(symbols.ByteBuffer_offset);
                int position = (int) dst.readField(symbols.ByteBuffer_position);
                int limit = (int) dst.readField(symbols.ByteBuffer_limit);

                ByteBuffer b = ByteBuffer.allocate(limit - position);
                int readenBytes = fc.read(b);
                b.flip();
                if (readenBytes != -1 && b.remaining() != readenBytes)
                    throw new RuntimeException("readen " + readenBytes + ", but buffer state: " + b);
                while (b.hasRemaining()) {
                    byteArray.writeElement(offset + position++, b.get());
                }
                dst.writeField(symbols.ByteBuffer_position, position);

                return readenBytes;
            }

            int write0(ByteBuffer src) throws IOException {
                return fc.write(src);
            }

            long position() throws IOException {
                return fc.position();
            }

            long size() throws IOException {
                return fc.size();
            }

            void implCloseChannel() throws IOException {
                fc.close();
            }
        }

        @InClass(value = "com/flyordie/ui/renderer/dom/StaticResourceImageLoader", optional = true)
        class StaticResourceImageLoaderNatives {

            String imageSizeAndMimeType(String url, byte[] image) throws IOException {
                try (ImageInputStream in = ImageIO.createImageInputStream(new ByteArrayInputStream(image))) {
                    ImageReader imageReader = ImageIO.getImageReaders(in).next();
                    try {
                        imageReader.setInput(in);
                        int width = imageReader.getWidth(0);
                        int height = imageReader.getHeight(0);
                        String mimeType = imageReader.getOriginatingProvider().getMIMETypes()[0];
                        return width + "," + height + "," + mimeType;
                    } catch (IOException e) {
                        throw new RuntimeException("couldn't determine image size of " + url + ": " + e, e);
                    } finally {
                        imageReader.dispose();
                    }
                }
            }
        }

        @InClass("jdk/internal/foreign/NativeMemorySegmentImpl")
        class NativeMemorySegmentImplNatives {

            @MethodReplacement
            ClassObj makeNativeSegment(long bytesSize, long alignmentBytes, ClassObj session) {
                // TODO
                Array b = createArray(new ArrayType(B), Math.toIntExact(bytesSize));
                return (ClassObj) execute(symbols.MemorySegment_ofArray, b).orElseThrow();
            }
        }

        @In(Thread.class)
        class ThreadNatives {

            long getNextThreadIdOffset() {
                return NEXT_THREAD_ID_OFFSET;
            }
        }

        @InClass("java/lang/VirtualThread")
        class VirtualThreadNatives {
            void registerNatives() {
            }
        }

        @InClass("jdk/internal/vm/ContinuationSupport")
        class ContinuationSupportNatives {
            boolean isSupported0() {
                return false;
            }
        }

        @InClass("jdk/internal/foreign/abi/UpcallLinker")
        class UpcallLinkerNatives {
            void registerNatives() {
            }
        }

        @In(InetAddress.class)
        class InetAddressImpl {
            void init() {
            }

            boolean isIPv4Available() {
                return hostEnvironment.isIPv4Supported();
            }

            boolean isIPv6Supported() {
                return hostEnvironment.isIPv6Supported();
            }
        }

        @In(Inet4Address.class)
        class Inet4AddressImpl {
            void init() {
            }
        }

        @In(Inet6Address.class)
        class Inet6AddressImpl {
            void init() {
            }
        }

        @InClass("java/lang/StackStreamFactory")
        class StackStreamFactoryNatives {
            boolean checkStackWalkModes() {
                return true;
            }
        }

        @InClass("jdk/internal/misc/PreviewFeatures")
        class PreviewFeatures {
            boolean isPreviewEnabled() {
                return true;
            }
        }

        @Target(ElementType.METHOD)
        @Retention(RetentionPolicy.RUNTIME)
        @interface Ignore {
        }

        @Target(ElementType.TYPE)
        @Retention(RetentionPolicy.RUNTIME)
        @interface In {
            Class<?> value();
        }

        @Target(ElementType.TYPE)
        @Retention(RetentionPolicy.RUNTIME)
        @interface InClass {

            String value();

            boolean optional() default false;
        }

        @Target(ElementType.METHOD)
        @Retention(RetentionPolicy.RUNTIME)
        protected @interface MethodReplacement {
        }
    }

    record ModuleInfo(String location) {
    }

    @SuppressWarnings("Since15") // IntelliJ bug preview feature-ös classoknál
    public class Symbols {

        public final Clazz Object = compContext.findClass("java/lang/Object");
        public final ArrayType objectArray = new ArrayType(Object);
        public final Clazz String = compContext.findClass("java/lang/String");
        public final ArrayType stringArray = new ArrayType(String);
        public final Field String_value = field(String, "value", BYTE_ARRAY);
        public final Field String_coder = field(String, "coder", PrimitiveType.B);
        public final Clazz StringUTF16 = compContext.findClass("java/lang/StringUTF16");
        public final Method StringUTF16_isBigEndian = method(StringUTF16, "isBigEndian", Z);
        public final Clazz.Method Object_toString = method(Object, "toString", String);
        public final Clazz.Method Object_hashCode = method(Object, "hashCode", I);
        public final Method Object_notifyAll = method(Object, "notifyAll", V);
        public final Method String_hashCode = method(String, "hashCode", I);
        public final Method String_intern = method(String, "intern", String);
        public final Clazz Error = clazz(Error.class);
        public final Clazz Optional = compContext.findClass("java/util/Optional");
        public final Method Optional_empty = method(Optional, "empty", Optional);
        public final Method Optional_of = method(Optional, "of", Optional, Object);
        public final Clazz Thread = clazz(java.lang.Thread.class);
        public final Clazz ThreadGroup = clazz(java.lang.ThreadGroup.class);
        public final Method ThreadGroup_constructor = method(ThreadGroup, "<init>", V);
        public final Field Thread_name = field(Thread, "name", String);
        public final Clazz Thread_FieldHolder = compContext.findClass("java/lang/Thread$FieldHolder");
        public final Field Thread_FieldHolder_group = field(Thread_FieldHolder, "group", ThreadGroup);
        public final Field Thread_FieldHolder_priority = field(Thread_FieldHolder, "priority", I);
        public final Field Thread_holder = field(Thread, "holder", Thread_FieldHolder);
        public final Method Thread_currentThread = method(Thread, "currentThread", Thread);
        public final Method Thread_currentCarrierThread = method(Thread, "currentCarrierThread", Thread);
        public final Method Thread_setPriority0 = method(Thread, "setPriority0", V, I);
        public final Clazz System = compContext.findClass("java/lang/System");
        public final Clazz InputStream = clazz(java.io.InputStream.class);
        public final Clazz PrintStream = clazz(java.io.PrintStream.class);
        public final Field System_in = field(System, "in", InputStream);
        public final Field System_out = field(System, "out", PrintStream);
        public final Field System_err = field(System, "err", PrintStream);
        public final Method System_setIn0 = method(System, "setIn0", V, InputStream);
        public final Method System_setOut0 = method(System, "setOut0", V, PrintStream);
        public final Method System_setErr0 = method(System, "setErr0", V, PrintStream);
        public final Clazz Boolean = compContext.findClass("java/lang/Boolean");
        public final Clazz Byte = compContext.findClass("java/lang/Byte");
        public final Clazz Short = compContext.findClass("java/lang/Short");
        public final Clazz Character = compContext.findClass("java/lang/Character");
        public final Clazz Integer = compContext.findClass("java/lang/Integer");
        public final Clazz Float = compContext.findClass("java/lang/Float");
        public final Clazz Long = compContext.findClass("java/lang/Long");
        public final Clazz Double = compContext.findClass("java/lang/Double");
        public final Clazz Class = compContext.findClass("java/lang/Class");
        public final Clazz Unsafe = compContext.findClass("jdk/internal/misc/Unsafe");
        public final Field Boolean_FALSE = field(Boolean, "FALSE", Boolean);
        public final Field Boolean_TRUE = field(Boolean, "TRUE", Boolean);
        public final Method Byte_valueOf = method(Byte, "valueOf", Byte, PrimitiveType.B);
        public final Method Short_valueOf = method(Short, "valueOf", Short, PrimitiveType.S);
        public final Method Character_valueOf = method(Character, "valueOf", Character, PrimitiveType.C);
        public final Method Integer_valueOf = method(Integer, "valueOf", Integer, I);
        public final Method Float_valueOf = method(Float, "valueOf", Float, PrimitiveType.F);
        public final Method Long_valueOf = method(Long, "valueOf", Long, J);
        public final Method Double_valueOf = method(Double, "valueOf", Double, PrimitiveType.D);
        public final Field Boolean_value = field(Boolean, "value", Z);
        public final Field Byte_value = field(Byte, "value", PrimitiveType.B);
        public final Field Short_value = field(Short, "value", PrimitiveType.S);
        public final Field Character_value = field(Character, "value", PrimitiveType.C);
        public final Field Integer_value = field(Integer, "value", I);
        public final Field Float_value = field(Float, "value", PrimitiveType.F);
        public final Field Long_value = field(Long, "value", J);
        public final Field Double_value = field(Double, "value", PrimitiveType.D);
        public final Method Unsafe_storeFence = method(Unsafe, "storeFence", V);
        public final Method Unsafe_loadFence = method(Unsafe, "loadFence", V);
        public final Method Unsafe_fullFence = method(Unsafe, "fullFence", V);
        public final Method Unsafe_shouldBeInitialized0 = method(Unsafe, "shouldBeInitialized0", Z, Class);
        public final Clazz Throwable = compContext.findClass("java/lang/Throwable");
        public final Field Throwable_backtrace = field(Throwable, "backtrace", Object);
        public final Field Throwable_detailMessage = field(Throwable, "detailMessage", String);
        public final Clazz CDS = compContext.findClass("jdk/internal/misc/CDS");
        public final Clazz Runtime = compContext.findClass("java/lang/Runtime");
        public final Method Runtime_maxMemory = method(Runtime, "maxMemory", J);
        public final Clazz Reference = compContext.findClass("java/lang/ref/Reference");
        public final Method Reference_refersTo0 = method(Reference, "refersTo0", Z, Object);
        public final Field Reference_referent = field(Reference, "referent", Object);
        public final Clazz AtomicLong = compContext.findClass("java/util/concurrent/atomic/AtomicLong");
        public final Clazz AccessController = compContext.findClass("java/security/AccessController");
        public final Clazz AccessControlContext = compContext.findClass("java/security/AccessControlContext");
        public final Clazz SystemProps_Raw = compContext.findClass("jdk/internal/util/SystemProps$Raw");
        public final Clazz StackTraceElement = compContext.findClass("java/lang/StackTraceElement");
        public final Field StackTraceElement_declaringClass = field(StackTraceElement, "declaringClass", String);
        public final Field StackTraceElement_methodName = field(StackTraceElement, "declaringClass", String);
        public final Field StackTraceElement_fileName = field(StackTraceElement, "fileName", String);
        public final Field StackTraceElement_lineNumber = field(StackTraceElement, "lineNumber", I);
        public final Clazz UnsatisfiedLinkError = compContext.findClass("java/lang/UnsatisfiedLinkError");
        public final Clazz NullPointerException = compContext.findClass("java/lang/NullPointerException");
        public final Clazz VM = compContext.findClass("jdk/internal/misc/VM");
        public final Method VM_initialize = method(VM, "initialize", V);
        public final Clazz RandomSupport = compContext.findClass("jdk/internal/util/random/RandomSupport");
        public final Method RandomSupport_secureRandomSeedRequested = method(RandomSupport, "secureRandomSeedRequested", Z);
        public final Clazz FileInputStream = clazz(java.io.FileInputStream.class);
        public final Clazz OldFileIO = clazz(OldFileIO.class);
        public final Clazz FileOutputStream = clazz(java.io.FileOutputStream.class);
        public final Clazz FileDescriptor = clazz(java.io.FileDescriptor.class);
        public final Field FileDescriptor_handle = field(FileDescriptor, "handle", J);
        public final Field FileDescriptor_fd = field(FileDescriptor, "fd", I);
        public final Clazz ScopedMemoryAccess = compContext.findClass("jdk/internal/misc/ScopedMemoryAccess");
        public final Method ScopedMemoryAccess_registerNatives = method(ScopedMemoryAccess, "registerNatives", V);
        public final Clazz Signal = compContext.findClass("jdk/internal/misc/Signal");
        public final Method Signal_findSignal0 = method(Signal, "findSignal0", I, String);
        public final Method Signal_handle0 = method(Signal, "handle0", J, I, J);
        public final Clazz Win32ErrorMode = compContext.findClassOrNull("sun/io/Win32ErrorMode");
        public final Method Win32ErrorMode_setErrorMode = Win32ErrorMode == null ? null : method(Win32ErrorMode, "setErrorMode", J, J);
        public final Clazz Cloneable = clazz(java.lang.Cloneable.class);
        public final Clazz CloneNotSupportedException = clazz(java.lang.CloneNotSupportedException.class);
        public final Clazz ClassLoader = clazz(java.lang.ClassLoader.class);
        public final Method ClassLoader_registerNatives = method(ClassLoader, "registerNatives", V);
        public final Method ClassLoader_findBootstrapClass = method(ClassLoader, "findBootstrapClass", Class, String);
        public final Clazz ProtectionDomain = clazz(java.security.ProtectionDomain.class);
        public final Clazz JavaIoFileSystem = compContext.findClass("java/io/FileSystem");
        public final Clazz DefaultFileSystem = compContext.findClass("java/io/DefaultFileSystem");
        public final Method DefaultFileSystem_getFileSystem = method(DefaultFileSystem, "getFileSystem", JavaIoFileSystem);
        public final Clazz NioDelegatingFileSystem = clazz(com.flyordie.code.runtime.NioDelegatingFileSystem.class);
        public final Method NioDelegatingFileSystem_create = method(NioDelegatingFileSystem, "create", NioDelegatingFileSystem);
        public final Clazz FileSystem = clazz(java.nio.file.FileSystem.class);
        public final Clazz FileSystemProvider = compContext.findClass("java/nio/file/spi/FileSystemProvider");
        public final Clazz DefaultFileSystemProvider = compContext.findClass("sun/nio/fs/DefaultFileSystemProvider");
        public final Method DefaultFileSystemProvider_clinit = method(DefaultFileSystemProvider, "<clinit>", V);
        public final Method DefaultFileSystemProvider_instance = method(DefaultFileSystemProvider, "instance", FileSystemProvider); // nem ez a valós return type-ja
        public final Method DefaultFileSystemProvider_theFileSystem = method(DefaultFileSystemProvider, "theFileSystem", FileSystem);
        public final Clazz EmptyFileSystemProvider = clazz(com.flyordie.code.runtime.DefaultFileSystemProvider.class);
        public final Field EmptyFileSystemProvider_INSTANCE = field(EmptyFileSystemProvider, "INSTANCE", EmptyFileSystemProvider);
        public final Field EmptyFileSystemProvider_FS_INSTANCE = field(EmptyFileSystemProvider, "FS_INSTANCE", FileSystem);
        public final Clazz Cleaner = clazz(java.lang.ref.Cleaner.class);
        public final Clazz CleanerImpl = compContext.findClass("jdk/internal/ref/CleanerImpl");
        public final Clazz ThreadFactory = clazz(java.util.concurrent.ThreadFactory.class);
        public final Method CleanerImpl_start = method(CleanerImpl, "start", V, Cleaner, ThreadFactory);
        public final Clazz UnsupportedOperationException = clazz(java.lang.UnsupportedOperationException.class);
        public final Clazz Unsafe2 = clazz(com.flyordie.code.runtime.Unsafe2.class);
        public final Clazz Fiber = clazz(com.flyordie.code.runtime.Fiber.class);
        public final Field Fiber_currentThread = field(Fiber, "currentThread", Thread);
        public final Clazz ByteBuffer = clazz(java.nio.ByteBuffer.class);
        public final Method ByteBuffer_wrap = method(ByteBuffer, "wrap", ByteBuffer, BYTE_ARRAY);
        public final Clazz HeapByteBuffer = compContext.findClass("java/nio/HeapByteBuffer");
        public final Field ByteBuffer_hb = field(ByteBuffer, "hb", BYTE_ARRAY);
        public final Field ByteBuffer_offset = field(ByteBuffer, "offset", I);
        public final Field ByteBuffer_position = field(ByteBuffer, "position", I);
        public final Field ByteBuffer_limit = field(ByteBuffer, "limit", I);
        public final Clazz InetAddress = clazz(java.net.InetAddress.class);

        // Reflection
        public final Clazz Method = clazz(java.lang.reflect.Method.class);
        public final Clazz Constructor = clazz(java.lang.reflect.Constructor.class);
        public final Clazz ConstantPool = compContext.findClass("jdk/internal/reflect/ConstantPool");
        public final Field Constructor_clazz = field(Constructor, "clazz", Class);
        public final Field Constructor_slot = field(Constructor, "slot", I);
        public final Field Constructor_parameterTypes = field(Constructor, "parameterTypes", new ArrayType(Class));
        public final Field Constructor_exceptionTypes = field(Constructor, "exceptionTypes", new ArrayType(Class));
        public final Field Constructor_modifiers = field(Constructor, "modifiers", I);
        public final Field Constructor_annotations = field(Constructor, "annotations", BYTE_ARRAY);
        public final Field Constructor_parameterAnnotations = field(Constructor, "parameterAnnotations", BYTE_ARRAY);
        public final Field Constructor_signature = field(Constructor, "signature", String);
        public final Field Method_clazz = field(Method, "clazz", Class);
        public final Field Method_name = field(Method, "name", String);
        public final Field Method_returnType = field(Method, "returnType", Class);
        public final Field Method_slot = field(Method, "slot", I);
        public final Field Method_parameterTypes = field(Method, "parameterTypes", new ArrayType(Class));
        public final Field Method_exceptionTypes = field(Method, "exceptionTypes", new ArrayType(Class));
        public final Field Method_modifiers = field(Method, "modifiers", I);
        public final Field Method_annotations = field(Method, "annotations", BYTE_ARRAY);
        public final Field Method_parameterAnnotations = field(Method, "parameterAnnotations", BYTE_ARRAY);
        public final Field Method_annotationDefault = field(Method, "annotationDefault", BYTE_ARRAY);
        public final Field Method_signature = field(Method, "signature", String);
        public final Clazz Reflection = compContext.findClass("jdk/internal/reflect/Reflection");
        public final Clazz Array = clazz(java.lang.reflect.Array.class);
        public final Field Class_componentType = field(Class, "componentType", Class);
        public final Method Class_desiredAssertionStatus0 = method(Class, "desiredAssertionStatus0", Z, Class);
        public final Method Class_registerNatives = method(Class, "registerNatives", V);
        public final Method Class_isInterface = method(Class, "isInterface", Z);
        public final Method Class_isPrimitive = method(Class, "isPrimitive", Z);
        public final Method Class_isInstance = method(Class, "isInstance", Z, Object);
        public final Method Class_getModifiers = method(Class, "getModifiers", I);
        public final Method Class_isHidden = method(Class, "isHidden", Z);
        public final Method Class_getDeclaringClass0 = method(Class, "getDeclaringClass0", Class);
        public final Method Class_getEnclosingMethod0 = method(Class, "getEnclosingMethod0", objectArray);
        public final Method Class_isAssignableFrom = method(Class, "isAssignableFrom", Z, Class);
        public final Method Class_getSuperclass = method(Class, "getSuperclass", Class);
        public final Method Class_initClassName = method(Class, "initClassName", String);
        public final Method Class_getDeclaredConstructors0 = method(Class, "getDeclaredConstructors0", new ArrayType(Constructor), Z);
        public final Method Class_getDeclaredMethods0 = method(Class, "getDeclaredMethods0", new ArrayType(Method), Z);
        public final Method Class_getConstantPool = method(Class, "getConstantPool", ConstantPool);
        public final Field Class_classLoader = field(Class, "classLoader", ClassLoader);
        public final Field Class_classData = field(Class, "classData", Object);
        public final Field Class_name = field(Class, "name", String);
        public final Method Class_forName0 = method(Class, "forName0", Class, String, Z, ClassLoader, Class);
        public final Method Class_getPrimitiveClass = method(Class, "getPrimitiveClass", Class, String);
        public final Clazz Field = compContext.findClass("java/lang/reflect/Field");
        public final Field Field_clazz = field(Field, "clazz", Class);
        public final Field Field_slot = field(Field, "slot", I);
        public final Field Field_name = field(Field, "name", String);
        public final Field Field_type = field(Field, "type", Class);
        public final Field Field_modifiers = field(Field, "modifiers", I);
        public final Field Field_signature = field(Field, "signature", String);
        public final Field Field_annotations = field(Field, "annotations", BYTE_ARRAY);
        public final Clazz NegativeArraySizeException = clazz(java.lang.NegativeArraySizeException.class);
        public final Method Reflection_getClassAccessFlags = method(Reflection, "getClassAccessFlags", I, Class);
        public final Clazz NativeConstructorAccessorImpl = compContext.findClass("jdk/internal/reflect/NativeConstructorAccessorImpl");
        public final Method NativeConstructorAccessorImpl_newInstance0 = method(NativeConstructorAccessorImpl, "newInstance0", Object, Constructor, objectArray);
        public final Clazz RecordComponent = clazz(java.lang.reflect.RecordComponent.class);
        public final Field RecordComponent_clazz = field(RecordComponent, "clazz", Class);
        public final Field RecordComponent_name = field(RecordComponent, "name", String);
        public final Field RecordComponent_type = field(RecordComponent, "type", Class);
        public final Field RecordComponent_accessor = field(RecordComponent, "accessor", Method);
        public final Field RecordComponent_signature = field(RecordComponent, "signature", String);
        public final Field RecordComponent_annotations = field(RecordComponent, "annotations", BYTE_ARRAY);
        public final Field RecordComponent_typeAnnotations = field(RecordComponent, "typeAnnotations", BYTE_ARRAY);
        public final Clazz Executable = clazz(java.lang.reflect.Executable.class);
        public final Clazz Parameter = clazz(java.lang.reflect.Parameter.class);
        public final Field Parameter_name = field(Parameter, "name", String);
        public final Field Parameter_modifiers = field(Parameter, "modifiers", I);
        public final Field Parameter_executable = field(Parameter, "executable", Executable);
        public final Field Parameter_index = field(Parameter, "index", I);

        // java.lang.invoke
        public final Clazz MethodHandle = clazz(java.lang.invoke.MethodHandle.class);
        public final Clazz MemberName = compContext.findClass("java/lang/invoke/MemberName");
        public final Field MemberName_clazz = field(MemberName, "clazz", Class);
        public final Field MemberName_name = field(MemberName, "name", String);
        public final Field MemberName_type = field(MemberName, "type", Object);
        public final Field MemberName_flags = field(MemberName, "flags", I);
        public final Clazz LambdaForm = compContext.findClass("java/lang/invoke/LambdaForm");
        public final Field MethodHandle_form = field(MethodHandle, "form", LambdaForm);
        public final Field LambdaForm_vmentry = field(LambdaForm, "vmentry", MemberName);
        public final Clazz MethodType = compContext.findClass("java/lang/invoke/MethodType");
        public final Clazz.Method MethodHandle_type = method(MethodHandle, "type", MethodType);
        public final Field MethodType_rtype = field(MethodType, "rtype", Class);
        public final Field MethodType_ptypes = field(MethodType, "ptypes", new ArrayType(Class));
        public final Method MethodType_makeImpl = method(MethodType, "makeImpl", MethodType, Class, new ArrayType(Class), Z);
        public final Clazz UnsafeConstants = compContext.findClass("jdk/internal/misc/UnsafeConstants");
        public final Clazz.Field UnsafeConstants_ADDRESS_SIZE0 = field(UnsafeConstants, "ADDRESS_SIZE0", I);
        public final Clazz.Method LambdaForm_prepare = method(LambdaForm, "prepare", V); // foreign upcall support

        public final Clazz DirectMethodHandle = compContext.findClass("java/lang/invoke/DirectMethodHandle");
        public final Method DirectMethodHandle_make = method(DirectMethodHandle, "make", DirectMethodHandle, PrimitiveType.B, Class, MemberName, Class);
        public final Clazz Lookup = compContext.findClass("java/lang/invoke/MethodHandles$Lookup");
        public final Method Lookup_constructor = method(Lookup, "<init>", V, Class);
        public final Field Lookup_lookupClass = field(Lookup, "lookupClass", Class);
        public final Field Lookup_allowedModes = field(Lookup, "allowedModes", I);
        public final Method Lookup_findGetter = method(Lookup, "findGetter", MethodHandle, Class, String, Class);
        public final Method Lookup_findSetter = method(Lookup, "findSetter", MethodHandle, Class, String, Class);
        public final Method Lookup_findStaticGetter = method(Lookup, "findStaticGetter", MethodHandle, Class, String, Class);
        public final Method Lookup_findStaticSetter = method(Lookup, "findStaticSetter", MethodHandle, Class, String, Class);
        public final Method Lookup_findStatic = method(Lookup, "findStatic", MethodHandle, Class, String, MethodType);
        public final Method Lookup_findSpecial = method(Lookup,
                "findSpecial", MethodHandle, Class, String, MethodType, Class);
        public final Method Lookup_findVirtual = method(Lookup, "findVirtual", MethodHandle, Class, String, MethodType);
        public final Method Lookup_findConstructor = method(Lookup, "findConstructor", MethodHandle, Class, MethodType);
        public static final int Lookup_FULL_POWER_MODES = 95;
        public final Clazz MethodHandleNatives = compContext.findClass("java/lang/invoke/MethodHandleNatives");
        public final Method MethodHandleNatives_registerNatives = method(MethodHandleNatives, "registerNatives", V);
        public final Method MethodHandleNatives_verifyConstants = method(MethodHandleNatives, "verifyConstants", Z);
        public final Method MethodHandleNatives_resolve = method(MethodHandleNatives, "resolve", MemberName, MemberName, Class, I, Z);
        public final Method MethodHandleNatives_getMemberVMInfo = method(MethodHandleNatives, "getMemberVMInfo", Object, MemberName);
        public final Method MethodHandleNatives_objectFieldOffset = method(MethodHandleNatives, "objectFieldOffset", J, MemberName);
        public final Method MethodHandleNatives_staticFieldOffset = method(MethodHandleNatives, "staticFieldOffset", J, MemberName);
        public final Method MethodHandleNatives_staticFieldBase = method(MethodHandleNatives, "staticFieldBase", Object, MemberName);
        public final Method MethodHandleNatives_linkMethod = method(MethodHandleNatives, "linkMethod", MemberName, Class, I, Class, String, Object, objectArray);
        public final Method MethodHandleNatives_linkCallSite = method(MethodHandleNatives, "linkCallSite", MemberName, Object, Object, Object, Object, Object, objectArray);
        public final Method MethodHandleNatives_init = method(MethodHandleNatives, "init", V, MemberName, Object);
        public final Clazz NoSuchMethodError = clazz(java.lang.NoSuchMethodError.class);
        public final Clazz NoSuchFieldError = clazz(java.lang.NoSuchFieldError.class);

        // Modules
        public final Clazz BootLoader = compContext.findClass("jdk/internal/loader/BootLoader");
        public final Clazz Module = clazz(java.lang.Module.class);
        public final Field Module_name = field(Module, "name", String);
        public final Field ClassLoader_unnamedModule = field(ClassLoader, "unnamedModule", Module);
        public final Field Class_module = field(Class, "module", Module);
        public final Clazz SystemModuleFinders_SystemModuleReader = compContext.findClass("jdk/internal/module/SystemModuleFinders$SystemModuleReader");

        // Foreign Memory / Foreign Function Interface
        public final Clazz MemoryLayout = clazz(java.lang.foreign.MemoryLayout.class);
        //public final Clazz PlatformLayouts = compContext.findClass("jdk/internal/foreign/PlatformLayouts");
        //public final Method PlatformLayouts_pick = method(PlatformLayouts, "pick", MemoryLayout,
        //        MemoryLayout, MemoryLayout, MemoryLayout);
        public final Clazz CLinker = clazz(java.lang.foreign.Linker.class);
        public final Clazz SymbolLookup = clazz(java.lang.foreign.SymbolLookup.class);
        public final Clazz NativeEntryPoint = compContext.findClass("jdk/internal/foreign/abi/NativeEntryPoint");
        public final Method CLinker_getInstance = method(CLinker, "nativeLinker", CLinker);
        public final Method CLinker_systemLookup = method(SymbolLookup, "loaderLookup", SymbolLookup);
        public final Clazz CLinkerImpl = clazz(ForeignLinkerImpl.class);
        public final Clazz.Field CLinkerImpl_INSTANCE = field(CLinkerImpl, "INSTANCE", CLinkerImpl);
        public final Clazz.Field CLinkerImpl_SYSTEM_SYMBOL_LOOKUP = field(CLinkerImpl, "SYSTEM_SYMBOL_LOOKUP", SymbolLookup);
        public final Clazz SystemSymbolLookup = clazz(ForeignLinkerImpl.SystemSymbolLookup.class);
        public final Clazz.Method SystemSymbolLookup_functionName = method(SystemSymbolLookup, "functionName", String, J);

        // Other
        public final ArrayType classArray = new ArrayType(Class);
        public final Clazz ClassValue = clazz(ClassValue.class);
        public final Method ClassValue_get = method(ClassValue, "get", Object, Class);
        public final Field Class_classValueMap = field(Class, "classValueMap", compContext.findClass("java/lang/ClassValue$ClassValueMap"));
        public final Clazz.Method Class_getEnumConstantsShared = method(Class, "getEnumConstantsShared", objectArray);
        public final Clazz RuntimeHelper = clazz(com.flyordie.code.runtime.RuntimeHelper.class);
        public final Clazz.Method RuntimeHelper_loadAppClass = method(RuntimeHelper, "loadAppClass", Class, String);
        public final Clazz URL = clazz(java.net.URL.class);
        public final Clazz MemorySegment = clazz(MemorySegment.class);
        public final Method MemorySegment_ofArray = method(MemorySegment, "ofArray", MemorySegment, new ArrayType(B));
        public final Clazz.Method AnnotatedElement_getAnnotations = method(clazz(AnnotatedElement.class),
                "getAnnotations", new ArrayType(clazz(Annotation.class)));

        Clazz clazz(Class<?> runtimeClass) {
            return compContext.findClass(runtimeClass.getName().replace('.', '/'));
        }

        Field field(Clazz clazz, String name, Type type) {
            return compContext.field(clazz, name, type);
        }

        Method method(Clazz clazz, String name, Type returnType, Type... paramTypes) {
            return compContext.findMethodOrFail(clazz, name, new MethodType(List.of(paramTypes), returnType));
        }
    }

    protected interface Intrinsic {
        ExecutionResult evaluate(Object[] args);
    }

    public static class Statics implements ClassObj {

        Interpreter interpreter;

        @Override
        public Clazz type() {
            return interpreter.compContext.findClass(StaticsHolder.class);
        }

        @Override
        public Object readField(int index) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Object readField(Field f) {
            return interpreter.readStaticField(f);
        }

        @Override
        public void writeField(int index, Object value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void writeField(Field f, Object value) {
            interpreter.writeStaticField(f, value);
        }

        @Override
        public Object representedData() {
            return null;
        }

        @Override
        public void representedData(Object obj) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ClassObj cloneObject() {
            throw new UnsupportedOperationException();
        }

        public static class StaticsHolder {
        }
    }
}
