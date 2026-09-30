package com.flyordie.code;

import com.flyordie.code.Clazz.Field;
import com.flyordie.code.Clazz.Method;
import com.flyordie.code.Node.ObjectNode.ObjectIdentity;
import com.flyordie.code.Type.ArrayType;
import com.flyordie.code.Type.PrimitiveType;
import com.flyordie.code.Type.ReferenceType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;

public interface Variable {

    boolean isExact();

    default boolean equals2(Variable variable) {
        return equals(variable);
    }

    @Nonnull Type type();

    record LocalVar(Method method, int number, Kind kind, MethodIdentity methodIdentity) implements Variable {

        @Override
        public String toString() {
            return method.clazz.name.substring(method.clazz.name.lastIndexOf('/') + 1) + "." + method.name + ":var" + number;
        }

        @Override
        public boolean isExact() {
            // akkor lenne false, ha rekurzív függvényeket is inline-olnánk.
            // de ez egyelőre meg van tiltva OptPhase1-ben.
            return true;
        }

        @Nonnull
        @Override
        public Type type() {
            if (number == 0 && !method.isStatic())
                return method.clazz;
            return kind.primitiveType == null ? method.clazz.lookup.findClass("java/lang/Object") : kind.primitiveType;
        }

        public enum Kind {

            // sorrend ugyanaz mint opcodeokban
            I(PrimitiveType.I), J(PrimitiveType.J), F(PrimitiveType.F), D(PrimitiveType.D), L(null);

            final PrimitiveType primitiveType;

            Kind(PrimitiveType primitiveType) {
                this.primitiveType = primitiveType;
            }

            public static final List<Kind> KINDS = List.of(values());

            public static Kind ofType(Type type) {
                if (type instanceof ReferenceType)
                    return L;
                return switch ((PrimitiveType) type) {
                    case Z, B, C, S, I -> I;
                    case J -> J;
                    case F -> F;
                    case D -> D;
                    default -> throw new IllegalArgumentException(type.toString());
                };
            }
        }

        static class MethodIdentity {}
    }

    record InstanceField(Field field, ObjectIdentity object) implements Variable {
        @Override
        public boolean equals2(Variable variable) {
            return variable instanceof InstanceField other &&
                    other.field.equals(field) &&
                    (this.object == null || other.object == null || Objects.equals(this.object, other.object));
        }

        @Override
        public boolean isExact() {
            return object != null;
        }

        @Override
        public Type type() {
            return field.type();
        }
    }

    record StaticField(Field field) implements Variable {

        @Override
        public boolean isExact() {
            return true;
        }

        @Override
        public String toString() {
            return field.clazz.name.substring(field.clazz.name.lastIndexOf('/') + 1) + "." + field.name;
        }

        @Override
        public Type type() {
            return field.type();
        }
    }

    record ArrayElement(@Nullable ArrayType arrayType) implements Variable {
        @Override
        public boolean isExact() {
            return false;
        }

        @Override
        public Type type() {
            return arrayType == null ? null : arrayType.elementType();
        }
    }
}
