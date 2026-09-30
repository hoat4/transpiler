package com.flyordie.code;

import com.flyordie.code.Clazz.Field;
import com.flyordie.code.Clazz.Method;
import com.flyordie.code.CompilationContext.TransformationFactory;
import com.flyordie.code.Type.ArrayType;
import com.flyordie.code.Type.PrimitiveType;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public interface Emitter {

    void setEmissionContext(EmissionContext emissionContext);

    void begin();

    ReplacementProvider makeReplacementProvider();

    void print(Method method, Node body);

    void printType(Clazz replacementClazz, Clazz clazz);

    void printNativeUpcallStub(Clazz nativeUpcallFunctionalInterface);

    void printPrimitiveTypes(Set<PrimitiveType> usedPrimitiveTypes);

    void printStaticField(Field f, Object value);

    void printArrayType(ArrayType t);

    void printStubType(Clazz replacementClass, Clazz clazz);

    void end();

    boolean isNativeType(Clazz clazz);

    default List<TransformationFactory> intermediateTransformations() {
        return Collections.emptyList();
    }

    default List<TransformationFactory> finalTransformations() {
        return Collections.emptyList();
    }

}
