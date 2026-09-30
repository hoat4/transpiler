package com.flyordie.code.util;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;

public class MethodNameAndType {
    final String name;
    final Class<?> returnType;
    final Class<?>[] params;

    public MethodNameAndType(String name, Class<?> returnType, Class<?>[] params) {
        this.name = name;
        this.returnType = returnType;
        this.params = params;
    }

    public static MethodNameAndType of(Method m) {
        return new MethodNameAndType(m.getName(), m.getReturnType(), m.getParameterTypes());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MethodNameAndType that = (MethodNameAndType) o;
        return name.equals(that.name) && returnType.equals(that.returnType) && Arrays.equals(params, that.params);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(name, returnType);
        result = 31 * result + Arrays.hashCode(params);
        return result;
    }
}
