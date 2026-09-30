package com.flyordie.code;


import com.flyordie.code.Clazz.Field;
import com.flyordie.code.Clazz.Method;

import javax.annotation.Nullable;
import java.util.List;

public interface ReplacementProvider {

    ReplacementProvider EMPTY = new ReplacementProvider() {
    };

    default Method replacementFor(Method m) {
        return m;
    }

    default Method replacedMethod(Method m) {
        return m;
    }

    default Node nativeMethodImplementation(Method m) {
        throw new UnsupportedOperationException("native method: " + m);
    }

    @Nullable
    default Method constructorReplacement(Method method) {
        return null;
    }

    @Nullable
    default Clazz replacementClass(Clazz c) {
        return null;
    }

    @Nullable
    default List<Clazz> replacedClass(Clazz c) {
        return null;
    }

    // ez a nem fully módon replace-elő osztályokat is figyelembe veszi, míg replacedClass nem
    default Clazz classOrReplaced(Clazz c) {
        return c;
    }

    default boolean isReplacedInConstants(Clazz clazz) { // pl. JS-nél immutable listek ArrayListImpl-el vannak helyettesítve
        return false;
    }

    @Nullable
    default Field replacedStaticField(Field field) {
        return null;
    }

    default Object constantReplacement(Object constant) {
        return constant;
    }

    default boolean allowInlining(Method method) {
        return true;
    }

    default void registerClassValue(Interpreter.ClassObj jlClassCO, Interpreter.ClassObj cvObj, Object value) {
    }

    default boolean treatStaticFinalAsConstant(Field field) {
        return true;
    }

    default void prepareForSerialization(Interpreter.ClassObj co) {
    }

    /**
     * 
     * @see #classOrReplaced(Clazz) 
     */
    @Nullable
    default MethodType replacedMethodType(MethodType type) {
        boolean noReplacedTypes = !(type.returnType() instanceof Clazz c) || classOrReplaced(c) == c;
        for (int i = 0;noReplacedTypes&& i < type.parameterTypes().size(); i++) {
            Type t = type.parameterTypes().get(i);
            if (t instanceof Clazz c && replacedClass(c) != c) {
                noReplacedTypes = false;
            }
        }
        if (noReplacedTypes)
            return type;
        else
            return new MethodType(
                    type.parameterTypes().stream().
                            map(t2 -> t2 instanceof Clazz c2 ? classOrReplaced(c2) : t2).
                            toList(),
                    type.returnType() instanceof Clazz c2 ? classOrReplaced(c2) : type.returnType()
            );
    }

    default boolean shouldNotSerializeInstanceField(Field f){
        return false;
    }
}
