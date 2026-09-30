package com.flyordie.code.js;

import com.flyordie.code.Clazz;
import com.flyordie.code.Clazz.Method;
import com.flyordie.code.Node;

import javax.annotation.Nullable;
import java.util.List;

public interface JSInteropProvider {

    @Nullable
    NativeTypeKind nativeTypeKind(Clazz clazz);

    // ez nem lesz meghívva, ha nativeTypeKind = NO_INTERFACE_OBJECT
    @Nullable
    String nativeTypeName(Clazz clazz);

    @Nullable
    NativeMethodKind nativeMethodKind(Method method);

    @Nullable
    String nativeMethodName(Method m);

    Node nativeMethodBody(Method m, List<Node> args, JSEmitter emitter);

    // azért nem lehet erre NativeTypeKindot használni, mert lehet hogy egy
    // típus használható functorként, de nem kezelendő natív típusként
    // pl. v.ö. HTMLDivElement és java.util.function.Consumer
    @Nullable
    Boolean isFunctor(Clazz clazz);

    enum NativeTypeKind{
        HAS_INTERFACE_OBJECT(false),
        NO_INTERFACE_OBJECT(true),

        // ez most valójában nincs is használva, mert helyette isFunctort használjuk
        FUNCTOR(true); // ez implikálja a NO_INTERFACE_OBJECTET is

        public final  boolean noInterfaceObject;

        NativeTypeKind(boolean noInterfaceObject) {
            this.noInterfaceObject = noInterfaceObject;
        }
    }

    enum NativeMethodKind {
        REGULAR_METHOD,
        CONSTRUCTOR,
        GETTER, SETTER,
        DYNAMIC_GETTER, DYNAMIC_SETTER
    }
}
