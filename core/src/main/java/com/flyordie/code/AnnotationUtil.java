package com.flyordie.code;

import org.objectweb.asm.tree.AnnotationNode;

import javax.annotation.Nullable;

public class AnnotationUtil {

    private AnnotationUtil() {
    }

    @Nullable
    public static Object findValue(@Nullable AnnotationNode ann) {
        return findValue(ann, "value");
    }

    @Nullable
    public static Object findValue(@Nullable AnnotationNode ann, String name) {
        return findValueOrDefault(ann, name, null);
    }

    public static Object findValueOrDefault(@Nullable AnnotationNode ann, String name, Object defaultValue) {
        if (ann == null || ann.values == null)
            return defaultValue;
        for (int i = 0; i < ann.values.size(); i += 2) {
            if (ann.values.get(i).equals(name))
                return ann.values.get(i + 1);
        }
        return defaultValue;
    }
}
