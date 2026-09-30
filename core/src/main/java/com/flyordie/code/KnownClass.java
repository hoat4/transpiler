package com.flyordie.code;

import com.flyordie.code.runtime.ArrayHelper;
import com.flyordie.code.runtime.Unsafe2;
import ui11.reflectutil.ReflectionUtil;

import java.util.Map;
import java.util.stream.Stream;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

public enum KnownClass {

    OBJECT("java/lang/Object"),
    CLASS("java/lang/Class"),
    BOOLEAN("java/lang/Boolean"),
    BYTE("java/lang/Byte"),
    SHORT("java/lang/Short"),
    CHARACTER("java/lang/Character"),
    INTEGER("java/lang/Integer"),
    FLOAT("java/lang/Float"),
    LONG("java/lang/Long"),
    DOUBLE("java/lang/Double"),
    STRING("java/lang/String"),
    METHOD_TYPE("java/lang/invoke/MethodType"),
    METHOD_HANDLE("java/lang/invoke/MethodHandle"),
    THROWABLE("java/lang/Throwable"),
    NULL_POINTER_EXCEPTION("java/lang/NullPointerException"),
    MEMBER_NAME("java/lang/invoke/MemberName"),
    Unsafe("jdk/internal/misc/Unsafe"),
    UNSAFE2(Unsafe2.class.getName().replace('.', '/')),
    UnsatisfiedLinkError("java/lang/UnsatisfiedLinkError"),
    IllegalArgumentException("java/lang/IllegalArgumentException"),
    ClassCastException("java/lang/ClassCastException"),
    VarHandle("java/lang/invoke/VarHandle"),
    ClassValue("java/lang/ClassValue"),
    Enum("java/lang/Enum"),
    Objects("java/util/Objects"),
    List("java/util/List"),
    ArrayList("java/util/ArrayList"),
    NativeEntryPoint("jdk/internal/foreign/abi/NativeEntryPoint"),
    StaticsHolder(Interpreter.Statics.StaticsHolder.class.getName().replace('.', '/')),
    ClassNotFoundException("java/lang/ClassNotFoundException"),
    Field("java/lang/reflect/Field"),
    Executable("java/lang/reflect/Executable"),
    Proxy("java/lang/reflect/Proxy"),
    ClassLoader("java/lang/ClassLoader"),
    Constructor("java/lang/reflect/Constructor"),
    VirtualThread("java/lang/VirtualThread"),
    NativeMemorySegmentImpl("jdk/internal/foreign/NativeMemorySegmentImpl"),
    ThreadLocalMap("java/lang/ThreadLocal$ThreadLocalMap"),
    MemorySegment("java/lang/foreign/MemorySegment"),
    System("java/lang/System"),
    Method("java/lang/reflect/Method"),
    RecordComponent("java/lang/reflect/RecordComponent"),
    RuntimeException("java/lang/RuntimeException"),
    StackWalker("java/lang/StackWalker"),
    ArrayHelper(ReflectionUtil.internalName(com.flyordie.code.runtime.ArrayHelper.class)),
    Locale(ReflectionUtil.internalName(java.util.Locale.class)),
    HashMap(ReflectionUtil.internalName(java.util.HashMap.class)),
    LinkedHashMap(ReflectionUtil.internalName(java.util.LinkedHashMap.class)),
    ConcurrentHashMap(ReflectionUtil.internalName(java.util.concurrent.ConcurrentHashMap.class)),
    Map(ReflectionUtil.internalName(java.util.Map.class)),
    Preconditions("jdk/internal/util/Preconditions");


    public final String className;

    KnownClass(String className) {
        this.className = className;
    }

    static final Map<String, KnownClass> COMMON_TYPES = Stream.of(values()).collect(toMap(c -> c.className, identity()));
}
