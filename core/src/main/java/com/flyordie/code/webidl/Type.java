package com.flyordie.code.webidl;

import java.util.Collection;

interface Type {

    enum PrimitiveType implements Type {
        SHORT, INT, LONG, FLOAT, DOUBLE, BOOLEAN, BYTE, BIG_INTEGER, VOID, ANY, OBJECT, SYMBOL
    }

    record NullableType(Type content) implements Type {
    }

    record ListType(Type content) implements Type {
    }

    enum StringType implements Type {

        ByteString, DOMString, USVString,
        UTF8String, JSString // ez a kettő vmi FF-only hülyeség
    }

    record InterfaceType(String interfaceName) implements Type {
        public InterfaceType {
            if (interfaceName.equals("UTF8String") || interfaceName.equals("void"))
                throw new RuntimeException();
        }
    }

    record EnumType(String enumName) implements Type {
    }

    record FrozenArrayType(Type elementType) implements Type {
    }

    record ObservableArrayType(Type elementType) implements Type {
    }

    enum BufferType implements Type {
        ArrayBuffer, DataView,
        Int8Array, Int16Array, Int32Array,
        Uint8Array, Uint16Array, Uint32Array, Uint8ClampedArray,
        Float32Array, Float64Array
    }

    record RecordType(StringType keyType, Type value) implements Type {
    }

    record PromiseType(Type type) implements Type {
    }

    record UnionType(Collection<Type> types) implements Type {
    }
}
