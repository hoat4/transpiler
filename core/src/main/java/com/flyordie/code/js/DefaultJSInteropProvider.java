package com.flyordie.code.js;

import com.flyordie.code.Clazz;
import com.flyordie.code.Clazz.Method;
import com.flyordie.code.Node;
import org.objectweb.asm.tree.AnnotationNode;

import javax.annotation.Nullable;

import java.util.List;

import static com.flyordie.code.js.JSEmitter.findAnnotation;

public class DefaultJSInteropProvider implements JSInteropProvider {

    static final String NAME_ANN_DESC = com.flyordie.code.jsinterop.Name.class.descriptorString();
    static final String NOINTERFACEOBJECT_ANN_DESC = com.flyordie.code.jsinterop.NoInterfaceObject.class.descriptorString();
    private static final String STATICS_ANN_DESC = com.flyordie.code.jsinterop.Statics.class.descriptorString();

    private static final String CONSTRUCTOR_ANN_DESC = com.flyordie.code.jsinterop.Constructor.class.descriptorString();
    private static final String GETTER_ANN_DESC = com.flyordie.code.jsinterop.Getter.class.descriptorString();
    private static final String SETTER_ANN_DESC = com.flyordie.code.jsinterop.Setter.class.descriptorString();

    private static final String DYNAMIC_GETTER_ANN_DESC = com.flyordie.code.jsinterop.DynamicGetter.class.descriptorString();
    private static final String DYNAMIC_SETTER_ANN_DESC = com.flyordie.code.jsinterop.DynamicSetter.class.descriptorString();

    private static final String FUNCTIONAL_INTERFACE_ANN_DESC = FunctionalInterface.class.descriptorString();

    @Nullable
    @Override
    public NativeTypeKind nativeTypeKind(Clazz clazz) {
        if (findAnnotation(clazz, NOINTERFACEOBJECT_ANN_DESC) != null)
            if (findAnnotation(clazz, FUNCTIONAL_INTERFACE_ANN_DESC) != null)
                return NativeTypeKind.FUNCTOR;
            else
                return NativeTypeKind.NO_INTERFACE_OBJECT;
        else if (findAnnotation(clazz, NAME_ANN_DESC) != null)
            if (findAnnotation(clazz, FUNCTIONAL_INTERFACE_ANN_DESC) != null)
                return NativeTypeKind.FUNCTOR;
            else
                return NativeTypeKind.HAS_INTERFACE_OBJECT;

        return null;
    }

    @Nullable
    @Override
    public String nativeTypeName(Clazz clazz) {
        AnnotationNode a = findAnnotation(clazz, NAME_ANN_DESC);
        if (a == null)
            return null;

        if (findAnnotation(clazz, FUNCTIONAL_INTERFACE_ANN_DESC) != null)
            // webidl-es generátor hiányossága miatti hack
            return null;
        else
            return (String) a.values.get(1);
    }

    private static boolean isNativeInterface0(Clazz clazz) {
        // ez egyezzen meg JSReplacementProvider.ServiceLoaderHelperben lévővel
        if (clazz.visibleAnnotations == null)
            return false;
        return clazz.visibleAnnotations.stream().
                anyMatch(ann -> ann.desc.equals(NAME_ANN_DESC) || ann.desc.equals(STATICS_ANN_DESC) ||
                        ann.desc.equals(NOINTERFACEOBJECT_ANN_DESC));
    }

    @Override
    public NativeMethodKind nativeMethodKind(Method m) {
        if (!isNativeInterface0(m.clazz))
            return null;
        // TODO ha statikus függvényt hív interfaceben, akkor nem kéne
        //      natívnak tekinteni

        boolean constr = m.findAnnotation(CONSTRUCTOR_ANN_DESC) != null;
        boolean getter = m.findAnnotation(GETTER_ANN_DESC) != null;
        boolean setter = m.findAnnotation(SETTER_ANN_DESC) != null;
        boolean dynSetter = m.findAnnotation(DYNAMIC_SETTER_ANN_DESC) != null;
        boolean dynGetter = m.findAnnotation(DYNAMIC_GETTER_ANN_DESC) != null;
        int i = (constr ? 1 : 0) + (getter ? 1 : 0) + (setter ? 1 : 0) + (dynSetter ? 1 : 0) + (dynGetter ? 1 : 0);
        return switch (i) {
            case 0 -> NativeMethodKind.REGULAR_METHOD;
            case 1 -> constr ? NativeMethodKind.CONSTRUCTOR :
                    getter ? NativeMethodKind.GETTER :
                            setter ? NativeMethodKind.SETTER :
                                    dynSetter ? NativeMethodKind.DYNAMIC_SETTER :
                                            NativeMethodKind.DYNAMIC_GETTER;
            default -> throw new RuntimeException("invalid definition of method " + m +
                    ", multiple native method kinds applicable");
        };
    }

    @Override
    public String nativeMethodName(Method m) {
        if (!isNativeInterface0(m.clazz))
            return null;

        AnnotationNode ann = m.findAnnotation(NAME_ANN_DESC);
        return ann == null ? m.name : (String) ann.values.get(1);
    }

    @Override
    public Node nativeMethodBody(Method m, List<Node> args, JSEmitter emitter) {
        return null;
    }

    @Nullable
    @Override
    public Boolean isFunctor(Clazz clazz) {
        if (clazz.findAnnotation(FUNCTIONAL_INTERFACE_ANN_DESC) != null)
            return true;
        else
            return null;
    }

    static String staticsName(Clazz clazz) {
        AnnotationNode staticsAnn = findAnnotation(clazz, STATICS_ANN_DESC);
        return staticsAnn != null ? (String) staticsAnn.values.get(1) : null;
    }
}
