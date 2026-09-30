package com.flyordie.code;

import javax.annotation.Nonnull;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

import static com.flyordie.code.CompilationContext.context;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;
import static org.objectweb.asm.Opcodes.*;
import static org.objectweb.asm.Opcodes.ACC_PROTECTED;

public interface Type {

    String descriptor();

    default org.objectweb.asm.Type type() {
        return org.objectweb.asm.Type.getType(descriptor());
    }

    boolean isAssignableFrom(Type otherType);

    int slotSize();

    /**
     * @see Class#getModifiers()
     */
    int modifiers();

    String displayName();

    /**
     * Ez eltér java.lang.Class::getSuperclasstól abban, hogy tömbök esetén nem Objectet ad vissza, hanem az elemtípus
     * superclassából képzett tömbtípust, illetve interface-ek esetén nem nullt ad vissza, hanem Objectet.
     */
    Type supertype();

    int bytesSize();

    Collection<? extends Type> allAncestorsAndThis();

    static Type of(Class<?> type) {
        if (type.isPrimitive())
            return PrimitiveType.FROM_CLASS.get(type);
        else if (type.isArray())
            return new ArrayType(of(type.getComponentType()));
        else
            throw new IllegalArgumentException();
    }

    static Type of(Class<?> type, Lookup lookup) {
        if (type.isPrimitive())
            return PrimitiveType.FROM_CLASS.get(type);
        else if (type.isArray())
            return new ArrayType(of(type.getComponentType(), lookup));
        else
            return lookup.findClass(type.getName().replace('.', '/'));
    }

    static Type parse(String descriptor, Lookup lookup) {
        int dims = 0;
        char ch;
        while ((ch = descriptor.charAt(dims)) == '[')
            dims++;

        Type t;
        if (ch == 'L') {
            int end = descriptor.length() - 1;
            if (descriptor.charAt(end) != ';')
                throw invalidDescriptor(descriptor);
            t = lookup.findClass(descriptor.substring(dims + 1, end));
        } else {
            t = PrimitiveType.ofDescriptorCharacter(ch);
            if (descriptor.length() != dims + 1)
                throw invalidDescriptor(descriptor);
        }

        while (dims != 0) {
            t = new ArrayType(t);
            dims--;
        }

        return t;
    }

    @Nonnull
    private static IllegalArgumentException invalidDescriptor(String descriptor) {
        return new IllegalArgumentException("invalid type descriptor string: " + descriptor);
    }

    enum PrimitiveType implements Type {

        // sorrend számít, mert CompilationContext.lub használja ordinalt

        V(0, "void", void.class, Void.class, -1),
        Z(1, "boolean", boolean.class, Boolean.class, T_BOOLEAN),
        B(1, "byte", byte.class, Byte.class, T_BYTE),
        S(2, "short", short.class, Short.class, T_SHORT),
        C(2, "char", char.class, Character.class, T_CHAR),
        I(4, "int", int.class, Integer.class, T_INT),
        F(4, "float", float.class, Float.class, T_FLOAT),
        D(8, "double", double.class, Double.class, T_DOUBLE),
        J(8, "long", long.class, Long.class, T_LONG);

        private final int byteSize;
        private final String displayName;
        private final Class<?> asClass;
        private final String wrapperName;
        private final String unboxMethodName;
        private final String boxMethodDesc;
        private final int arrayElementType;

        static final Map<Class<?>, PrimitiveType> FROM_CLASS =
                Stream.of(values()).collect(toMap(p -> p.asClass, identity()));

        PrimitiveType(int byteSize, String displayName, Class<?> asClass, Class<?> wrapperClass, int arrayElementType) {
            this.byteSize = byteSize;
            this.displayName = displayName;
            this.asClass = asClass;
            this.wrapperName = wrapperClass.getName().replace('.', '/');
            this.unboxMethodName = displayName + "Value";
            this.arrayElementType = arrayElementType;
            this.boxMethodDesc = "(" + name() + ")L" + wrapperName + ";";
        }

        public static Type ofDescriptorCharacter(char ch) {
            return valueOf(String.valueOf(ch));
        }

        public static PrimitiveType of(Class<?> c) {
            return FROM_CLASS.get(c);
        }

        @Override
        public org.objectweb.asm.Type type() {
            return org.objectweb.asm.Type.getType(name());
        }

        @Override
        public boolean isAssignableFrom(Type otherType) {
            return otherType == this;
        }

        @Override
        public String descriptor() {
            return name();
        }

        @Override
        public String displayName() {
            return displayName;
        }

        @Override
        public int slotSize() {
            return switch (this) {
                case V -> 0;
                case J, D -> 2;
                default -> 1;
            };
        }

        @Override
        public int bytesSize() {
            return byteSize;
        }

        @Override
        public int modifiers() {
            return ACC_PUBLIC | ACC_FINAL;
        }

        @Override
        public Type supertype() {
            return null;
        }

        public Class<?> asClass() {
            return asClass;
        }

        @Override
        public Collection<Type> allAncestorsAndThis() {
            return List.of(this);
        }

        public String wrapperName() {
            return wrapperName;
        }

        public String unboxMethodName() {
            return unboxMethodName;
        }

        public String boxMethodDesc() {
            return boxMethodDesc;
        }

        public int newarrayCode() {
            if (arrayElementType < 0)
                throw new UnsupportedOperationException();
            return arrayElementType;
        }
    }

    interface ReferenceType extends Type {

        @Override
        default int bytesSize() {
            return context().addressingMode.pointerSize;
        }

        String referenceTypeName();

        static ReferenceType ofReferenceTypeName(String referenceTypeName, Lookup lookup) {
            return referenceTypeName.startsWith("[")
                    ? (ReferenceType) Type.parse(referenceTypeName, lookup)
                    : lookup.findClass(referenceTypeName);
        }
    }

    // kéne ASM-ba bugreport, hogy nincs array type factory
    record ArrayType(Type elementType) implements ReferenceType {

        public ArrayType {
            Objects.requireNonNull(elementType);
        }

        public static ArrayType dims(int dimensions, Type elementType) {
            assert dimensions > 0;
            for (; dimensions > 0; dimensions--)
                elementType = new ArrayType(elementType);
            return (ArrayType) elementType;
        }

        @Override
        public String descriptor() {
            return "[" + elementType.descriptor();
        }

        @Override
        public boolean isAssignableFrom(Type otherType) {
            return otherType instanceof ArrayType arrayType && elementType.isAssignableFrom(arrayType.elementType);
        }

        @Override
        public int slotSize() {
            return 1;
        }

        @Override
        public int modifiers() {
            return elementType.modifiers() & (ACC_PUBLIC | ACC_PRIVATE | ACC_PROTECTED) | ACC_FINAL;
        }

        @Override
        public String toString() {
            return descriptor();
        }

        @Override
        public String displayName() {
            return elementType.displayName() + "[]";
        }

        @Override
        public Type supertype() {
            Type elementSupertype = elementType.supertype();
            if (elementSupertype == null)
                return context().findClass(KnownClass.OBJECT);
            else
                return new ArrayType(elementSupertype);
        }

        public Type endingElementType() {
            return elementType instanceof ArrayType arrayType ? arrayType.endingElementType() : elementType;
        }

        public int dimensions() {
            return elementType instanceof ArrayType arrayType ? 1 + arrayType.dimensions() : 1;
        }

        @Override
        public String referenceTypeName() {
            return descriptor();
        }

        @Override
        public Collection<Type> allAncestorsAndThis() {
            return Stream.<Type>concat(
                    Stream.of(context().findClass(KnownClass.OBJECT)),
                    elementType.allAncestorsAndThis().stream().map(ArrayType::new)
            ).toList();
        }

        static final ArrayType BYTES = new ArrayType(PrimitiveType.B);
    }

    interface Lookup {
        Clazz findClass(String name);
    }
}
