package com.flyordie.code;

import com.flyordie.code.Clazz.Field;
import com.flyordie.code.Clazz.Method;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @param elements olyan sorrendben mint a stack trace-ekben
 */
public record Location(List<LocationElement> elements) {

    static final ThreadLocal<Location> TL = new ThreadLocal<>();

    public Location {
        assert !elements.isEmpty() && elements.get(elements.size() - 1)
                instanceof MethodLocationElement;
    }

    @Override
    public String toString() {
        return elements.stream().map(LocationElement::toString).collect(Collectors.joining(" <- "));
    }

    public Location prepend(LocationElement e) {
        List<LocationElement> l = new ArrayList<>();
        l.add(e);
        l.addAll(elements);
        return new Location(l);
    }

    public static Location location() {
        if (TL.get() == null)
            throw new IllegalStateException("no current location");
        return TL.get();
    }

    public static LocationContext with(Location loc) {
        Objects.requireNonNull(loc);
        Location prev = TL.get();
        TL.set(loc);
        return new LocationContext(loc, prev);
    }

    public sealed interface LocationElement permits MethodLocationElement, FieldLocationElement {
        default boolean isTooBroad() {
            return false;
        }
    }

    public record MethodLocationElement(Method method) implements LocationElement {

        @Override
        public String toString() {
            return method.toShortString();
        }

        @Override
        public boolean isTooBroad() {
            return method.name.equals("equals") && method.desc.equals("(Ljava/lang/Object;)Z") ||
                    method.name.equals("hashCode") && method.desc.equals("()I") ||
                    method.name.equals("toString") && method.desc.equals("()Ljava/lang/String;");
        }
    }

    public record FieldLocationElement(Field field) implements LocationElement {
        @Override
        public String toString() {
            return field.toString();
        }
    }

    public static class LocationContext implements AutoCloseable {

        private final Location loc;
        private final Location prev;

        private LocationContext(Location loc, Location prev) {
            this.loc = loc;
            this.prev = prev;
        }

        @Override
        public void close() {
            assert TL.get() == loc;
            TL.set(prev);
        }
    }
}
