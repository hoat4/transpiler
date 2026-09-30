package com.flyordie.code;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;

import static com.flyordie.code.CompilationContext.context;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static java.lang.invoke.MethodHandles.filterReturnValue;
import static java.lang.invoke.MethodHandles.lookup;
import static org.objectweb.asm.Opcodes.*;

public class NumericOps {

    public static final OpInfo[] ops = new OpInfo[256];

    static {
        MethodHandles.Lookup lookup = lookup();
        MethodHandle z2i;
        try {
            z2i = lookup.findStatic(NumericOps.class, "z2i", MethodType.methodType(int.class, boolean.class));
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("can't happen: " + e, e);
        }
        for (Method method : NumericOps.class.getDeclaredMethods()) {
            Op ann = method.getAnnotation(Op.class);
            if (ann != null) {
                MethodHandle mh;
                try {
                    mh = lookup.unreflect(method);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("can't happen: " + e, e);
                }
                if (mh.type().returnType() == boolean.class)
                    mh = filterReturnValue(mh, z2i);
                mh = mh.asType(mh.type().generic());

                ops[ann.code()] = new OpInfo(mh, method.getParameterCount(),
                        Type.of(method.getParameterTypes()[0], context()), Type.of(method.getReturnType(), context()),
                        ann.symbol(), ann.code());
            }
        }
    }

    private static int z2i(boolean b) {
        return b ? 1 : 0;
    }


    public static class OpInfo {

        final MethodHandle method;
        final int inputArgCount;
        final Type outputType;
        final Type inputType1;
        public final String symbol;
        public final int code;

        public OpInfo(MethodHandle method, int inputArgCount, Type inputType1, Type outputType, String symbol, int code) {
            this.method = method;
            this.inputArgCount = inputArgCount;
            this.inputType1 = inputType1;
            this.outputType = outputType;
            this.symbol = symbol;
            this.code = code;
        }
    }

    private static Object invoke1(MethodHandle mh, Object a) {
        try {
            return mh.invokeExact(a);
        } catch (RuntimeException | Error e) {
            throw e;
        } catch (Throwable e) {
            throw new RuntimeException("should not happen", e);
        }
    }

    private static Object invoke2(MethodHandle mh, Object a, Object b) {
        try {
            return mh.invokeExact(a, b);
        } catch (RuntimeException | Error e) {
            throw e;
        } catch (Throwable e) {
            throw new RuntimeException("should not happen", e);
        }
    }

    // INT

    @Op(code = IADD, symbol = "+")
    public static int iadd(int a, int b) {
        return a + b;
    }

    @Op(code = ISUB, symbol = "-")
    public static int isub(int a, int b) {
        return a - b;
    }

    @Op(code = IMUL, symbol = "*")
    public static int imul(int a, int b) {
        return a * b;
    }

    @Op(code = IDIV, symbol = "/")
    public static int idiv(int a, int b) {
        return a / b;
    }

    @Op(code = IREM, symbol = "%")
    public static int irem(int a, int b) {
        return a % b;
    }

    @Op(code = INEG, symbol = "-")
    public static int ineg(int i) {
        return -i;
    }

    @Op(code = ISHL, symbol = "<<")
    public static int ishl(int a, int b) {
        return a << b;
    }

    @Op(code = ISHR, symbol = ">>")
    public static int ishr(int a, int b) {
        return a >> b;
    }

    @Op(code = IUSHR, symbol = ">>>")
    public static int iushr(int a, int b) {
        return a >>> b;
    }

    @Op(code = IOR, symbol = "|")
    public static int ior(int a, int b) {
        return a | b;
    }

    @Op(code = IAND, symbol = "&")
    public static int iand(int a, int b) {
        return a & b;
    }

    @Op(code = IXOR, symbol = "^")
    public static int ixor(int a, int b) {
        return a ^ b;
    }

    @Op(code = IFEQ, symbol = "0 == ")
    public static boolean eq(int i) {
        return i == 0;
    }

    @Op(code = IFNE, symbol = "0 != ")
    public static boolean ne(int i) {
        return i != 0;
    }

    @Op(code = IFLT, symbol = "0 > ")
    public static boolean lt(int i) {
        return i < 0;
    }

    @Op(code = IFLE, symbol = "0 >= ")
    public static boolean le(int i) {
        return i <= 0;
    }

    @Op(code = IFGT, symbol = "0 < ")
    public static boolean gt(int i) {
        return i > 0;
    }

    @Op(code = IFGE, symbol = "0 <= ")
    public static boolean ge(int i) {
        return i >= 0;
    }

    @Op(code = IF_ICMPEQ, symbol = " == ")
    public static boolean icmpeq(int a, int b) {
        return a == b;
    }

    @Op(code = IF_ICMPNE, symbol = " != ")
    public static boolean icmpne(int a, int b) {
        return a != b;
    }

    @Op(code = IF_ICMPLT, symbol = " < ")
    public static boolean icmplt(int a, int b) {
        return a < b;
    }

    @Op(code = IF_ICMPLE, symbol = " <= ")
    public static boolean icmple(int a, int b) {
        return a <= b;
    }

    @Op(code = IF_ICMPGT, symbol = " > ")
    public static boolean icmpgt(int a, int b) {
        return a > b;
    }

    @Op(code = IF_ICMPGE, symbol = " >= ")
    public static boolean icmpge(int a, int b) {
        return a >= b;
    }

    // LONG

    @Op(code = LADD, symbol = "+")
    public static long ladd(long a, long b) {
        return a + b;
    }

    @Op(code = LSUB, symbol = "-")
    public static long lsub(long a, long b) {
        return a - b;
    }

    @Op(code = LMUL, symbol = "*")
    public static long lmul(long a, long b) {
        return a * b;
    }

    @Op(code = LDIV, symbol = "/")
    public static long ldiv(long a, long b) {
        return a / b;
    }

    @Op(code = LREM, symbol = "%")
    public static long lrem(long a, long b) {
        return a % b;
    }

    @Op(code = LSHL, symbol = "<<")
    public static long lshl(long a, int b) {
        return a << b;
    }

    @Op(code = LSHR, symbol = ">>")
    public static long lshr(long a, int b) {
        return a >> b;
    }

    @Op(code = LUSHR, symbol = ">>>")
    public static long lushr(long a, int b) {
        return a >>> b;
    }

    @Op(code = LOR, symbol = "|")
    public static long lor(long a, long b) {
        return a | b;
    }

    @Op(code = LAND, symbol = "&")
    public static long land(long a, long b) {
        return a & b;
    }

    @Op(code = LXOR, symbol = "^")
    public static long lxor(long a, long b) {
        return a ^ b;
    }

    @Op(code = LCMP, symbol = "<>")
    public static int lcmp(long a, long b) {
        return Long.compare(a, b);
    }

    @Op(code = LNEG, symbol = "-")
    public static long lneg(long l) {
        return -l;
    }

    // FLOAT

    @Op(code = FCMPG, symbol = " fcmpg ")
    @SuppressWarnings("UseCompareMethod")
    public static int fcmpg(float a, float b) {
        return a < b ? -1 : a == b ? 0 : 1;
    }

    @Op(code = FCMPL, symbol = " fcmpl ")
    @SuppressWarnings("UseCompareMethod")
    public static int fcmpl(float a, float b) {
        return a > b ? 1 : a == b ? 0 : -1;
    }

    @Op(code = FADD, symbol = "+")
    public static float fadd(float a, float b) {
        return a + b;
    }

    @Op(code = FSUB, symbol = "-")
    public static float fsub(float a, float b) {
        return a - b;
    }

    @Op(code = FMUL, symbol = "*")
    public static float fmul(float a, float b) {
        return a * b;
    }

    @Op(code = FDIV, symbol = "/")
    public static float fdiv(float a, float b) {
        return a / b;
    }

    @Op(code = FREM, symbol = "%")
    public static float frem(float a, float b) {
        return a % b;
    }

    @Op(code = FNEG, symbol = "-")
    public static float fneg(float i) {
        return -i;
    }

    // DOUBLE

    @Op(code = DCMPG, symbol = " dcmpg ")
    @SuppressWarnings("UseCompareMethod")
    public static int dcmpg(double a, double b) {
        return a < b ? -1 : a == b ? 0 : 1;
    }

    @Op(code = DCMPL, symbol = " dcmpl ")
    @SuppressWarnings("UseCompareMethod")
    public static int dcmpl(double a, double b) {
        return a > b ? 1 : a == b ? 0 : -1;
    }

    @Op(code = DADD, symbol = "+")
    public static double dadd(double a, double b) {
        return a + b;
    }

    @Op(code = DSUB, symbol = "-")
    public static double dsub(double a, double b) {
        return a - b;
    }

    @Op(code = DMUL, symbol = "*")
    public static double dmul(double a, double b) {
        return a * b;
    }

    @Op(code = DDIV, symbol = "/")
    public static double ddiv(double a, double b) {
        return a / b;
    }

    @Op(code = DREM, symbol = "%")
    public static double drem(double a, double b) {
        return a % b;
    }

    @Op(code = DNEG, symbol = "-")
    public static double dneg(double i) {
        return -i;
    }

    // CONVERSIONS

    @Op(code = I2L, symbol = "(long) ")
    public static long i2l(int i) {
        return i;
    }

    @Op(code = I2B, symbol = "(byte) ")
    public static int i2b(int i) {
        return (byte) i;
    }

    @Op(code = I2C, symbol = "(char) ")
    public static int i2c(int i) {
        return (char) i;
    }

    @Op(code = I2S, symbol = "(short) ")
    public static int i2s(int i) {
        return (short) i;
    }

    @Op(code = L2I, symbol = "(int) ")
    public static int l2i(long i) {
        return (int) i;
    }

    @Op(code = F2D, symbol = "(double) ")
    public static double f2d(float f) {
        return f;
    }

    @Op(code = I2F, symbol = "(float) ")
    public static float i2f(int i) {
        return i;
    }

    @Op(code = F2I, symbol = "(int) ")
    public static int f2i(float f) {
        return (int) f;
    }

    @Op(code = D2I, symbol = "(int) ")
    public static int d2i(double d) {
        return (int) d;
    }

    @Op(code = D2F, symbol = "(float) ")
    public static float d2f(double d) {
        return (float) d;
    }

    @Op(code = I2D, symbol = "(double) ")
    public static double i2d(int i) {
        return i;
    }

    @Op(code = L2D, symbol = "(double) ")
    public static double l2d(long i) {
        return i;
    }

    @Op(code = D2L, symbol = "(long) ")
    public static long d2l(double f) {
        return (long) f;
    }

    @Op(code = F2L, symbol = "(long) ")
    public static long f2l(float i) {
        return (long) i;
    }

    @Op(code = L2F, symbol = "(float) ")
    public static float l2f(long l) {
        return (float) l;
    }

    // OBJECTS

    @Op(code = IFNULL, symbol = "null == ")
    public static boolean isNull(Object obj) {
        return obj == null;
    }

    @Op(code = IFNONNULL, symbol = "null != ")
    public static boolean isNonNull(Object obj) {
        return obj != null;
    }

    @Op(code = IF_ACMPNE, symbol = " !== ")
    public static boolean notEquals(Object a, Object b) {
        return a != b;
    }

    @Op(code = IF_ACMPEQ, symbol = " === ")
    public static boolean equals(Object a, Object b) {
        return a == b;
    }

    @Retention(RUNTIME)
    @Target(METHOD)
    public @interface Op {

        int code();

        String symbol();
    }
}
