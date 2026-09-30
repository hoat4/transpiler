package com.flyordie.code.js;

import java.util.*;
import java.util.stream.Collectors;

public sealed interface JSValue {

    // most a null/undefined kezelés inkonzisztens, mert JS-re fordítva az identity equals sima == lesz, ami szerint
    // null == undefined.

    record JSNumber(double d) implements JSValue {

        @Override
        public String toString() {
            // ez nem jó, nagy számoknál pontatlan lesz a longgá kasztolás
            return d == (long) d ? Long.toString((long) d) : Double.toString(d);
        }
    }

    record JSBoolean(boolean b) implements JSValue {
        @Override
        public String toString() {
            return Boolean.toString(b);
        }
    }

    record JSString(String s) implements JSValue {
        @Override
        public String toString() {
            return s;
        }
    }

    enum Undefined implements JSValue {
        UNDEFINED;

        @Override
        public String toString() {
            return "undefined";
        }
    }

    final class JSObject implements JSValue {

        private final Map<Long, JSValue> fields1 = new TreeMap<>();
        private final Map<String, JSValue> fields2 = new LinkedHashMap<>();

        public JSValue get(String fieldName) {
            return get(new JSString(fieldName));
        }

        public JSValue get(JSValue fieldName) {
            Long i = asIntegerKey(fieldName);
            if (i != null)
                return fields1.getOrDefault(i, Undefined.UNDEFINED);
            else
                return fields2.getOrDefault(String.valueOf(fieldName), Undefined.UNDEFINED);
        }

        public void put(String fieldName, JSValue value) {
            put(new JSString(fieldName), value);
        }

        public void put(JSValue fieldName, JSValue value) {
            Long i = asIntegerKey(fieldName);
            if (i != null)
                fields1.put(i, value);
            else
                fields2.put(String.valueOf(fieldName), value);
        }

        public void delete(String fieldName) {
            delete(new JSString(fieldName));
        }

        public void delete(JSValue fieldName) {
            Long i = asIntegerKey(fieldName);
            if (i != null)
                fields1.remove(i);
            else
                fields2.remove(String.valueOf(fieldName));
        }

        public Iterable<String> fieldNames() {
            return () -> new Iterator<>() {

                private final Iterator<Long> it1 = fields1.keySet().iterator();
                private final Iterator<String> it2 = fields2.keySet().iterator();

                @Override
                public boolean hasNext() {
                    return it1.hasNext() || it2.hasNext();
                }

                @Override
                public String next() {
                    return it1.hasNext() ? Long.toString(it1.next()) : it2.next();
                }
            };
        }

            /*
            return new Iterator<>() {

                private int offset = 4, count = 4;
                private Object[] indices = getSparseElementKeys(keys, 0, count);
                private int i;

                @Override
                public boolean hasNext() {
                    return i < indices.length;
                }

                @Override
                public Object next() {
                    if (i == indices.length)
                        throw new NoSuchElementException();

                    String index = (String) indices[i++];
                    Object next = onlyKeys ? readSparseArray(keys, index) : new EntryImpl(index);
                    if (i == indices.length) {
                        indices = getSparseElementKeys(keys, offset, count = count * 4);
                        offset += count;
                        i = 0;
                    }
                    return next;
                }
            };*/

        private Long asIntegerKey(JSValue v) {
            if (v instanceof JSNumber n)
                // nem tudom miért pont 4294967294L a max érték és miért nem 4294967295L,
                // de Chrome-ban ennyivel működött csak
                return n.d == (long) n.d && (long) n.d <= 4294967294L ? (long) n.d : null;
            if (v instanceof JSString s) {
                String str = s.s;
                if (str.equals("0"))
                    return 0L;
                long n = 0;
                for (int i = str.length() - 1; i >= 0; i--) {
                    char c = str.charAt(i);
                    if (c < '0' || c > '9')
                        return null;
                    n = n * 10 + c - '0';
                    if (n < 0)
                        return null;
                }
                return n <= 4294967294L ? n : null;
            }
            return null;
        }

        @Override
        public String toString() {
            return "[object Object]";
        }
    }

    final class JSArray implements JSValue {

        private final List<JSValue> contents = new ArrayList<>();

        public int length() {
            return contents.size();
        }

        public JSValue get(int i) {
            return contents.get(i);
        }

        // TODO set

        @Override
        public String toString() {
            return contents.stream().map(String::valueOf).collect(Collectors.joining(","));
        }
    }
}
