package com.flyordie.code.runtime;

import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Stream;

// Linker permits ForeignLinkerImpl
public final class ForeignLinkerImpl /* implements Linker */ {

    private static final Map<Long, String[]> FUNCTION_CALL_ARRANGEMENTS =
            Collections.synchronizedMap(new WeakHashMap<>());

    private static final Class<?> NativeEntryPoint;
    private static final MethodHandle NativeEntryPoint_constructor;
    private static final MethodHandle NativeMethodHandle_make;
    private static final MethodHandle InternalError_constructor;
    private static final MethodHandle MemorySegment_address;
    private static final MethodHandle MemorySegment_ofAddress;
    private static final MethodHandle Z2I, I2Z, I2C, I2B, I2S;

    public static final ForeignLinkerImpl INSTANCE;
    public static final SymbolLookup SYSTEM_SYMBOL_LOOKUP = new SystemSymbolLookup();

    private ForeignLinkerImpl() {
    }

    static {
        try {
            final Lookup lookup1 = MethodHandles.lookup();
            Lookup lookup = MethodHandles.privateLookupIn(Object.class, lookup1);
            NativeEntryPoint = lookup.
                    findClass("jdk/internal/foreign/abi/NativeEntryPoint");
            NativeEntryPoint_constructor = lookup.findConstructor(NativeEntryPoint,
                            MethodType.methodType(void.class, MethodType.class, long.class)).
                    asType(MethodType.methodType(Object.class, MethodType.class, long.class));
            Class<?> NativeMethodHandle = Class.forName("java/lang/invoke/NativeMethodHandle");
            Method make = Stream.of(NativeMethodHandle.getDeclaredMethods()).
                    filter(m -> m.getName().equals("make")).findAny().get();
            make.setAccessible(true);
            NativeMethodHandle_make = lookup.unreflect(make).
                    asType(MethodType.methodType(MethodHandle.class, Object.class));
            InternalError_constructor = lookup.findConstructor(InternalError.class, MethodType.methodType(void.class));
            MemorySegment_address = lookup.findVirtual(MemorySegment.class, "address", MethodType.methodType(long.class));
            MemorySegment_ofAddress = lookup.findStatic(MemorySegment.class, "ofAddress",
                    MethodType.methodType(MemorySegment.class, long.class));

            Z2I = lookup1.findStatic(ForeignLinkerImpl.class, "z2i", MethodType.methodType(int.class, boolean.class));
            I2Z = lookup1.findStatic(ForeignLinkerImpl.class, "i2z", MethodType.methodType(boolean.class, int.class));
            I2B = lookup1.findStatic(ForeignLinkerImpl.class, "i2b", MethodType.methodType(byte.class, int.class));
            I2S = lookup1.findStatic(ForeignLinkerImpl.class, "i2s", MethodType.methodType(short.class, int.class));
            I2C = lookup1.findStatic(ForeignLinkerImpl.class, "i2c", MethodType.methodType(char.class, int.class));
        } catch (ClassNotFoundException | IllegalAccessException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        INSTANCE = new ForeignLinkerImpl();
    }

    public MethodHandle downcallHandle(MemorySegment symbol, FunctionDescriptor function, Linker.Option... options) {
        if (options.length != 0)
            throw new UnsupportedOperationException("unknown Linker options");
        try {
            MethodType methodType = function.toMethodType();

            MethodType methodType2 = methodType;
            if (methodType.returnType() == MemorySegment.class)
                methodType2 = methodType2.changeReturnType(long.class);
            else if (methodType.returnType() == byte.class || methodType.returnType() == boolean.class ||
                    methodType.returnType() == short.class || methodType.returnType() == char.class)
                methodType2 = methodType2.changeReturnType(int.class);
            for (int i = 0; i < methodType.parameterCount(); i++) {
                final Class<?> ptype = methodType.parameterType(i);
                if (ptype == MemorySegment.class)
                    methodType2 = methodType2.changeParameterType(i, long.class);
                else if (ptype == byte.class || ptype == boolean.class ||
                        ptype == short.class || ptype == char.class)
                    methodType2 = methodType2.changeParameterType(i, int.class);
            }

            long id;
            String symbolName = SystemSymbolLookup.functionName(symbol.address());
            String[] shuffle = new String[function.argumentLayouts().size() + 1];
            synchronized (this) {
                id = FUNCTION_CALL_ARRANGEMENTS.size();
                FUNCTION_CALL_ARRANGEMENTS.put(id, shuffle);
            }

            shuffle[0] = symbolName;
            for (int i = 0; i < function.argumentLayouts().size(); i++) {
                if (function.argumentLayouts().get(i) instanceof GroupLayout groupLayout)
                    if (groupLayout.name().isPresent())
                        shuffle[i + 1] = groupLayout.name().get();
                    else {
                        // pl. busy_wait_until absolute_time_t-t fogad, ami egy névtelen struct
                        final String msg = "no struct name for arg #" + i + " of " + symbolName + ": " + groupLayout;
                        return exceptionThrower(methodType2, msg);
                    }
            }

            Object nativeEntryPoint = NativeEntryPoint_constructor.invokeExact(methodType2, id);
            MethodHandle mh = (MethodHandle) NativeMethodHandle_make.invokeExact(nativeEntryPoint);
            if (methodType.returnType() == MemorySegment.class)
                mh = MethodHandles.filterReturnValue(mh, MemorySegment_ofAddress);
            else if (methodType.returnType() == byte.class)
                mh = MethodHandles.filterReturnValue(mh, I2B);
            else if (methodType.returnType() == short.class)
                mh = MethodHandles.filterReturnValue(mh, I2S);
            else if (methodType.returnType() == char.class)
                mh = MethodHandles.filterReturnValue(mh, I2C);
            else if (methodType.returnType() == boolean.class)
                mh = MethodHandles.filterReturnValue(mh, I2Z);
            for (int i = 0; i < methodType.parameterCount(); i++) {
                final Class<?> ptype = methodType.parameterType(i);
                if (ptype == MemorySegment.class)
                    mh = MethodHandles.filterArguments(mh, i, MemorySegment_address);
                else if (methodType.returnType() == byte.class ||
                        methodType.returnType() == short.class || methodType.returnType() == char.class)
                    mh = mh.asType(mh.type().changeParameterType(i, methodType.returnType()));
                else if (ptype == boolean.class)
                    mh = MethodHandles.filterArguments(mh, i, Z2I);
            }
            return mh;
        } catch (RuntimeException | Error e) {
            throw e;
        } catch (Throwable e) {
            // nem lehetséges, mert make() és a konstruktor nem dob checked exceptiont
            throw new RuntimeException("should not reach here", e);
        }
    }

    private static MethodHandle exceptionThrower(MethodType methodType, String msg) {
        MethodHandle mh = MethodHandles.throwException(void.class, RuntimeException.class);
        try {
            mh = MethodHandles.collectArguments(mh, 0, MethodHandles.lookup().
                    findConstructor(RuntimeException.class, MethodType.methodType(void.class, String.class)));
        } catch (NoSuchMethodException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        mh = mh.bindTo(msg);
        mh = MethodHandles.dropArguments(mh, 0, methodType.parameterArray());
        return mh.asType(methodType);
    }

    public MethodHandle downcallHandle(FunctionDescriptor function, Linker.Option... options) {
        if (options.length != 0)
            throw new UnsupportedOperationException("unknown Linker options");
        return null; // TODO
    }

    public native MemorySegment upcallStub(MethodHandle target, FunctionDescriptor function,
                                           Arena arena, Linker.Option... options);

    // called by CTransformer
    static /*String[]*/ Object functionCallArrangement(long id) {
        return Objects.requireNonNull(FUNCTION_CALL_ARRANGEMENTS.get(id));
    }

    static boolean i2z(int i) {
        return i != 0;
    }

    static byte i2b(int i) {
        //if (i != (byte) i)
        //    throw new RuntimeException("overflow, not a signed byte: " + i);
        return (byte) i;
    }

    static short i2s(int i) {
        //if (i != (short) i)
        //    throw new RuntimeException("overflow, not a signed short: " + i);
        return (short) i;
    }

    static char i2c(int i) {
        //if (i != (char) i)
        //    throw new RuntimeException("overflow, not an unsigned short: " + i);
        return (char) i;
    }

    static int z2i(boolean b) {
        return b ? 1 : 0;
    }

    public static class SystemSymbolLookup implements SymbolLookup {

        private static final Map<Long, String> FUNCTION_NAMES =
                Collections.synchronizedMap(new WeakHashMap<>());

        private SystemSymbolLookup() {
        }

        @Override
        public Optional<MemorySegment> find(String name) {
            Objects.requireNonNull(name);
            if (name.isEmpty() || name.contains("\0"))
                throw new IllegalArgumentException();
            MemorySegment addr = MemorySegment.ofAddress(FUNCTION_NAMES.size() + 100);
            FUNCTION_NAMES.put(addr.address(), name);
            return Optional.of(addr);
        }

        // át kéne nevezni nem csak függvény lehet, hanem globális változó is
        // meg ha áttérünk újabb Javára, meg lehetne szüntetni az egészet, és helyett csinálni egy MemorySegment
        // subclasst, ami egy unresolved symbolt reprezentál
        public static String functionName(long address) {
            String f = FUNCTION_NAMES.get(address);
            if (f == null)
                throw new InternalError("no function name for address: " + address);
            return f;
        }
    }
}
