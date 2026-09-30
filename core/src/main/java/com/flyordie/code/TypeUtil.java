package com.flyordie.code;

import org.objectweb.asm.Type;

import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Method;

public class TypeUtil {

    // t = t.getElementType(); nem jó, mert a legbelső típust adja vissza, nem az eggyel beljebbit
    public static Type elementType(Type arrayType) {
        assert arrayType.getSort() == Type.ARRAY;
        return Type.getType(arrayType.getDescriptor().substring(1));
    }

    public static Class<?> primitiveType(Type t) {
        return switch (t.getSort()) {
            case Type.BOOLEAN -> boolean.class;
            case Type.BYTE -> byte.class;
            case Type.SHORT -> short.class;
            case Type.CHAR -> char.class;
            case Type.INT -> int.class;
            case Type.FLOAT -> float.class;
            case Type.LONG -> long.class;
            case Type.DOUBLE -> double.class;
            default -> throw new IllegalArgumentException("not a primitive type: " + t);
        };
    }

    public static String getMethodDescriptor(Executable executable) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append('(');
        Class<?>[] parameters = executable.getParameterTypes();
        for (Class<?> parameter : parameters)
            stringBuilder.append(parameter.descriptorString());
        stringBuilder.append(')');
        if (executable instanceof Constructor<?>)
            stringBuilder.append('V');
        else
            stringBuilder.append(((Method) executable).getReturnType().descriptorString());

        return stringBuilder.toString();
    }

    public static String getMethodName(Executable m) {
        return m instanceof Constructor<?> ? "<init>" : ((Method)m).getName();
    }

    public static Type methodTypeOf(Method m) {
        return Type.getMethodType(Type.getMethodDescriptor(m));
    }
}
