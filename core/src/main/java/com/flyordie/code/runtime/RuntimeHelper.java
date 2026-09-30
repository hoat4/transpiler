package com.flyordie.code.runtime;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.util.Objects;

public class RuntimeHelper {

    public static Class<?> loadAppClass(String name) throws ClassNotFoundException {
        Class<?> c = ClassLoader.getSystemClassLoader().loadClass(name);
        Objects.requireNonNull(c);
        //Objects.requireNonNull(c.getClassLoader(), name);
        return c;
    }

    // nem ebbe az osztályba való, majd át kéne rakni máshova
    public static MethodHandles.Lookup privilegedLookup() {
        MethodHandles.Lookup lkp;
        try {
            Field f = MethodHandles.Lookup.class.getDeclaredField("IMPL_LOOKUP");
            f.setAccessible(true);
            lkp = (MethodHandles.Lookup) f.get(null);
        } catch (Exception ex) {
            throw new RuntimeException("couldn't get IMPL_LOOKUP", ex);
        }
        return lkp;
    }

}
