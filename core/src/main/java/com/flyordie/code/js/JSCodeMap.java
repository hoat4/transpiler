package com.flyordie.code.js;

import com.flyordie.code.js.JSSerialization.Serializer;

import java.lang.constant.ClassDesc;
import java.lang.constant.MethodTypeDesc;
import java.util.Map;

public interface JSCodeMap {

    JSClassMap clazz(String javaClassName);

    class JSClassMap {

        public final ClassDesc javaClass;
        public final String name;
        public final Map<FieldNameAndType, String> fieldNames;
        public final JSClassMap superclass;
        public final Map<MethodNameAndType, String> methodNames;
        Serializer<?> serializer;

        public JSClassMap(ClassDesc javaClass, String name, Map<FieldNameAndType, String> fieldNames, JSClassMap superclass, Map<MethodNameAndType, String> methodNames) {
            this.javaClass = javaClass;
            this.name = name;
            this.fieldNames = fieldNames;
            this.superclass = superclass;
            this.methodNames = methodNames;
        }

        public record FieldNameAndType(String name, ClassDesc type) {}
        public record MethodNameAndType(String name, MethodTypeDesc type) {}
    }
}
