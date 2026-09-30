package com.flyordie.code.runtime;

import com.flyordie.code.annotation.DontInline;
import com.flyordie.code.annotation.Inline;
import com.flyordie.code.annotation.MustBeInlined;
import com.flyordie.code.js.JSValue;
import com.flyordie.code.js.JSValue.JSObject;
import com.flyordie.code.js.JSValue.Undefined;

import javax.annotation.Nonnull;
import java.util.List;

public class Unsafe2 {

    //    Compile-time eliminált függvények

    // lehetne egyébként csinálni egy Any (vagy JSObject) típust, és akkor azt lehetne használni j.l.Object helyett

    // ez t=Object.class-szal nem működik, mert nem lép életbe az optimizer rule, mert nem rak javac checkcastot a
    // hívás elé. primitívekre ne ezt használjuk, hanem a lentieket.
    @DontInline
    public static native <T> T reinterpretCast(Object obj, Class<T> t);

    // lehetne megcsinálni Optimizer2-be hogy primitív típusokkal is működjön reinterpretCast,
    // akkor nem kellenének ezek a specializációk, de lusta voltam rá.
    // annyi kéne hogy checkcast helyett az Integer.intValue illetve társait szedi ki.
    @DontInline
    public static native boolean reinterpretAsBoolean(Object obj);

    @DontInline
    public static native byte reinterpretAsByte(Object obj);

    @DontInline
    public static native short reinterpretAsShort(Object obj);

    @DontInline
    public static native char reinterpretAsChar(Object obj);

    @DontInline
    public static native int reinterpretAsInt(Object obj);

    @DontInline
    public static native float reinterpretAsFloat(Object obj);

    @DontInline
    public static native long reinterpretAsLong(Object obj);

    @DontInline
    public static native double reinterpretAsDouble(Object obj);

    @DontInline
    public static native Object reinterpretAsObject(byte b);

    @DontInline
    public static native Object reinterpretAsObject(boolean b);

    @DontInline
    public static native Object reinterpretAsObject(short b);

    @DontInline
    public static native Object reinterpretAsObject(char b);

    @DontInline
    public static native Object reinterpretAsObject(int b);

    @DontInline
    public static native Object reinterpretAsObject(float b);

    @DontInline
    public static native Object reinterpretAsObject(long b);

    @DontInline
    public static native Object reinterpretAsObject(double b);

    @DontInline
    public static native int fieldID(Class<?> clazz, String fieldName, Class<?> type);


    @Inline
    @MustBeInlined // reinterpretCast miatt
    public static <T> T setType(Object o, Class<T> aClass) {
        setTypeImpl(o, aClass);
        return reinterpretCast(o, aClass);
    }

    @SuppressWarnings("ConstantValue")
    public static void downcallResultSetType(Object o, Class<?> aClass) {
        // natív objektumoknak több típusuk is lehet, pl. ha
        // használjuk a sima native interface-eket és a TeaVM JSO-ot is egyszerre.
        // alapból a sima native interface-es típusuk van a natív objektumoknak, mert
        // ezt beírjuk fordításkor, és ilyenkor a JSO-esre akarunk átírni.
        // mivel JSO-s típusokra a cast nop, ezért a simát preferáljuk, nem a JSO-t.

        // meg a property-k másolása miatt a setTypeImpl amúgy is lassú, nem baj ha nem hívogatjuk folyton

        Object undefined = Undefined.UNDEFINED;
        if (o != null && o.getClass() == undefined)
            setTypeImpl(o, aClass);
    }

    // TODO setType-ban reinterpretCast valamiért nem működik. ha azt megcsináljuk, akkor ez lehet újra privát.
    public static native void setTypeImpl(Object o, Class<?> type);

    // TODO ezek eliminálására most van kód OptPhase2-ben, de nem tűnik működőnek,
    //      mert inline-olódnak ezek előtte.
    public static native Object getInstanceField(Object obj, int fieldID);

    public static native void putInstanceField(Object obj, int fieldID, Object value);

    public static native Object getStaticField(int fieldID);

    public static native void putStaticField(int fieldID, Object value);

    // ezek nyers értéket ad vissza, tehát pl. intet boxolni vagy reinterpretAsInt-elni kell
    public static native Object invokeStatic(int methodID, Object[] args);

    public static native Object invokeSpecial(int methodID, Object receiver, Object[] args);

    public static native Object invokeVirtual(int methodID, Object receiver, Object[] args);

    public static Object box(Object obj, Class<?> type) {
        if (type == boolean.class) return Unsafe2.reinterpretAsBoolean(obj);
        if (type == byte.class) return Unsafe2.reinterpretAsByte(obj);
        if (type == short.class) return Unsafe2.reinterpretAsShort(obj);
        if (type == char.class) return Unsafe2.reinterpretAsChar(obj);
        if (type == int.class) return Unsafe2.reinterpretAsInt(obj);
        if (type == float.class) return Unsafe2.reinterpretAsFloat(obj);
        if (type == long.class) return Unsafe2.reinterpretAsLong(obj);
        if (type == double.class) return Unsafe2.reinterpretAsDouble(obj);
        return obj;
    }

    public static Object unbox(Object o, Class<?> type) {
        if (type == boolean.class) return Unsafe2.reinterpretAsObject((boolean) o);
        if (type == byte.class) return Unsafe2.reinterpretAsObject((byte) o);
        if (type == short.class) return Unsafe2.reinterpretAsObject((short) o);
        if (type == char.class) return Unsafe2.reinterpretAsObject((char) o);
        if (type == int.class) return Unsafe2.reinterpretAsObject((int) o);
        if (type == float.class) return Unsafe2.reinterpretAsObject((float) o);
        if (type == long.class) return Unsafe2.reinterpretAsObject((long) o);
        if (type == double.class) return Unsafe2.reinterpretAsObject((double) o);
        return o;
    }


    public static native boolean canBeWidenedTo(Class<?> type, Class<?> to);

    public static native int refSize(Class<?> type);

    // itt sem a regularArgs paraméter illetve a visszatérési érték is lehet hogy nem csak Objecteket tartalmaz,
    // hanem primitív típusú értékeket is
    @SuppressWarnings("ManualArrayCopy")
    private static Object[] nativeInterfaceVarargs(Object[] regularArgs, Object[] varargs) { // Emitter használja
        Object[] args = new Object[regularArgs.length + varargs.length];
        // System.arraycopy nem használható, mert a regularArgs itt csak natív tömb, nincs Java típusa
        // mondjuk undefined elemtype-ú tömböt létrehoz, szóval akár működhetne is, viszont a végén van
        // egy checkcast ami már nem fog sikerülni
        for (int i = 0; i < regularArgs.length; i++)
            args[i] = regularArgs[i];
        for (int i = 0; i < varargs.length; i++)
            args[regularArgs.length + i] = convertToNative(varargs[i]);
        return args;
    }


    private static native Object nativeTrue();

    private static native Object nativeFalse();

    // abban különbözne egy sima unboxtól, hogy longot JS számmá alakítja
    // a convertToNative-okat hívatja meg JSTransformer

    public static Object convertToNative(long l) {
        return (double) l;
    }

    public static Object convertToNative(Object o) {
        if (o instanceof Boolean b) return b ? nativeTrue() : nativeFalse();
        if (o instanceof Byte) return reinterpretAsByte(o);
        if (o instanceof Character)
            return reinterpretAsChar(o); // ilyen nem fordul elő native interface-ekben, szóval tökmindegy hogy mit csinálunk vele
        if (o instanceof Short) return reinterpretAsShort(o);
        if (o instanceof Integer) return reinterpretAsInt(o);
        if (o instanceof Float) return reinterpretAsFloat(o);
        if (o instanceof Long) return (double) reinterpretAsLong(o);
        if (o instanceof Double) return reinterpretAsDouble(o);

        if (o instanceof List<?> l) return l.toArray();

        // array, string, nativeinterface jó lesz úgy ahogy van.
        // sima objektumokat lehetne esetleg CB szabályok szerint konvertálni,
        // pl. window.postMessage esetén, de valahol meg nem (pl. console.log),
        // ezért amikor tényleg arra lesz szükség, akkor majd kézzel konvertáljuk

        return o;
    }

    // ennek semmi köze a fentiekhez, kéne neki egy normális hely.
    // az lenne a legjobb ha be lehetne rakni jdk/internal/vm/VM replacementjébe.
    public static double timeOffsetMS = -1;

    @SuppressWarnings("unchecked")
    public static <K> K asObject(JSValue jsValue) {
        return (K) jsValue;
    }

    public static native <T> T allocateInstance(Class<T> clazz);

    public static class IncomingUntypedValue {
    }

    public static Object checkCast(Object obj, Class<?> clazz) {
        if (checkCast_fastpath(obj, clazz))
            checkCast_slowpath(obj, clazz);
        return obj;
    }

    private static boolean checkCast_fastpath(Object obj, Class<?> clazz) {
        return true;
    }

    @DontInline
    private static void checkCast_slowpath(Object obj, Class<?> clazz) {
        if (checkCast_platformSpecific(obj, clazz))
            return;
        if (obj != null && !clazz.isAssignableFrom(obj.getClass())
                && !tryAddIncomingUntypedValueType(clazz, obj.getClass())) {

            // meg kéne nézni hogy mi OpenJDK-ban a CCE szöveg
            throw cce(obj, clazz);
        }
    }

    private static boolean checkCast_platformSpecific(Object obj, Class<?> clazz) {
        return false;
    }

    @Nonnull
    public static ClassCastException cce(Object obj, Class<?> clazz) {
        return new ClassCastException("can't cast " + obj.getClass().getName() + " to " + clazz.getName());
    }

    private static native boolean jsValueIsObject(Object obj);

    private static boolean tryAddIncomingUntypedValueType(Class<?> supertype,
                                                          Class<?> t) {
        return false;
    }

    // ezt csak a konstans classokra használjuk, másra ott a lenti
    // (az a különbség köztük, hogy ezt a változatot JSReplacementProvider
    // felüldefiniálja, míg a lentit nem)
    public static boolean instanceOf(Object obj, Class<?> clazz) {
        // JS változatban van JSArray kezelés is
        return obj != null && clazz.isAssignableFrom(obj.getClass());
    }

    public static boolean instanceOf_nonConst(Object obj, Class<?> clazz) {
        assert asJSValue(obj) != Undefined.UNDEFINED;
        return obj != null && clazz.isAssignableFrom(obj.getClass());
    }

    // JSTransformer lecseréli a névre. a paraméternek konstansnak kell lennie
    @DontInline
    public static native String typeName(Class<?> clazz);

    @DontInline
    public static <T> T globalObjectAs(Class<T> a) { // ha a @Statics, akkor lecserélődik ez a függvényhívás loadNativeStatics-ra
        return globalObjectAsImpl(a);
    }

    private static native <T> T globalObjectAsImpl(Class<T> a);

    @SuppressWarnings("unchecked")
    static <T> T loadNativeStatics(String name, Class<T> t) { // JSTransformers hívja
        Object obj = loadNativeObject(name);
        setTypeImpl(obj, t);
        return (T) obj;
    }

    private static native Object loadNativeObject(String n);

    // TODO ezt el kéne tüntetni, helyette "implementálják" a JS objektumok is a toStringet
    public static String nativeToString(Object obj) {
        return String.valueOf(obj);
    }

    // FinalJSTransformerből hívva
    private static native boolean isUndefined(Object obj);

    public static native String typeof(Object obj);

    public static native boolean isArray(Object obj);

    public static native String[] propertiesIn(Object obj);

    public static JSValue asJSValue(Object object) {
        return reinterpretCast(object, JSValue.class);
    }

    @MustBeInlined
    public static <T> T compilationFailure() {
        return compilationFailure();
    }

    public static void throwException(Throwable e) {
        throwHelper(e);
    }

    @SuppressWarnings("unchecked")
    private static <E extends RuntimeException> void throwHelper(Throwable e) {
        throw (E) e;
    }

    // TODO ezeket helyettesítsük FMA API-val

    public static native int getInt(Void dummy, Object o, long offset);

    public static native void putInt(Void dummy, Object o, long offset, int x);

    public static native Object getReference(Void dummy, Object o, long offset);

    public static native void putReference(Void dummy, Object o, long offset, Object x);

    public static native boolean getBoolean(Void dummy, Object o, long offset);

    public static native void putBoolean(Void dummy, Object o, long offset, boolean x);

    public static native byte getByte(Void dummy, Object o, long offset);

    public static native void putByte(Void dummy, Object o, long offset, byte x);

    public static native short getShort(Void dummy, Object o, long offset);

    public static native void putShort(Void dummy, Object o, long offset, short x);

    public static native char getChar(Void dummy, Object o, long offset);

    public static native void putChar(Void dummy, Object o, long offset, char x);

    public static native long getLong(Void dummy, Object o, long offset);

    public static native void putLong(Void dummy, Object o, long offset, long x);

    public static native float getFloat(Void dummy, Object o, long offset);

    public static native void putFloat(Void dummy, Object o, long offset, float x);

    public static native double getDouble(Void dummy, Object o, long offset);

    public static native void putDouble(Void dummy, Object o, long offset, double x);

    public static native Object getReferenceVolatile(Void dummy, Object o, long offset);

    public static native void putReferenceVolatile(Void dummy, Object o, long offset, Object x);

    public static native int getIntVolatile(Void dummy, Object o, long offset);

    public static native void putIntVolatile(Void dummy, Object o, long offset, int x);

    public static native boolean getBooleanVolatile(Void dummy, Object o, long offset);

    public static native void putBooleanVolatile(Void dummy, Object o, long offset, boolean x);

    public static native byte getByteVolatile(Void dummy, Object o, long offset);

    public static native void putByteVolatile(Void dummy, Object o, long offset, byte x);

    public static native short getShortVolatile(Void dummy, Object o, long offset);

    public static native void putShortVolatile(Void dummy, Object o, long offset, short x);

    public static native char getCharVolatile(Void dummy, Object o, long offset);

    public static native void putCharVolatile(Void dummy, Object o, long offset, char x);

    public static native long getLongVolatile(Void dummy, Object o, long offset);

    public static native void putLongVolatile(Void dummy, Object o, long offset, long x);

    public static native float getFloatVolatile(Void dummy, Object o, long offset);

    public static native void putFloatVolatile(Void dummy, Object o, long offset, float x);

    public static native double getDoubleVolatile(Void dummy, Object o, long offset);

    public static native void putDoubleVolatile(Void dummy, Object o, long offset, double x);

    public static native void throwExceptionImpl(Throwable e);
}
