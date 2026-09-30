package com.flyordie.code.js;

import com.flyordie.code.Interpreter;
import com.flyordie.code.Type.ArrayType;
import com.flyordie.code.Type.ReferenceType;
import com.flyordie.code.js.JSConstantPool.Entry.State;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

class JSConstantPool {

    private final JSEmitter emitter;

    private final List<Entry> entries = new ArrayList<>();
    private final Map<Object, Entry> entriesByKey = new HashMap<>();

    private boolean freeze;
    private int counter;

    public JSConstantPool(JSEmitter emitter) {
        this.emitter = emitter;
    }

    public Entry constant0(Object key) {
        if (freeze)
            throw new IllegalStateException();

        key = emitter.replaceConstant(key);

        Entry e = entriesByKey.get(key);
        if (e == null) {
            e = emitter.constValue(key);
            Objects.requireNonNull(e);
            entries.add(e);
            entriesByKey.put(key, e);
        }
        return e;
    }

    public Entry constant1(Object val) {
        Entry v = constant0(val);
        v.referrers.add(Referrer.CODE);
        return v;
    }

    public String constant(Object val) {
        return constant1(val).ref();
    }

    public void constant(Object val, Consumer<String> consumer) {
        Entry v = constant1(val);
        String js = v.ref();
        if (v.isReady())
            consumer.accept(js);
        else
            v.afterWrite.add(() -> consumer.accept(js));
    }

    @SuppressWarnings("ForLoopReplaceableByForEach")
    List<Entry> finish() {
        // bővül menet közben a collection
        for (int i = 0; i < entries.size(); i++)
            entries.get(i).init();

        freeze = true;

        sort();

        entries.removeIf(entry -> {
            if (entry.referrers.size() == 1 && !entry.referrers.contains(Referrer.CODE) &&
                    !((Entry) entry.referrers.get(0)).visited && entry.allDependenciesHaveBeenVisited())
                entry.state = State.INLINE;

            entry.visited = true;

            return entry.state == State.INLINE;
        });

        return entries;
    }

    private void sort() {
        Set<Entry> whites = new HashSet<>(entries);

        Deque<Entry> vertices = new ArrayDeque<>();
        Deque<Integer> indices = new ArrayDeque<>();
        int t = 1;

        while (!whites.isEmpty()) {
            // lehet hogy ilyenkor olyat kéne preferálni, amibe nem megy egy él se.
            // bár enélkül is viszonylag jól működik.

            Entry first = whites.iterator().next();
            whites.remove(first);
            first.dfsColor = true;

            vertices.push(first);
            indices.push(0);
            while (!vertices.isEmpty()) {
                Entry v = vertices.getFirst();
                assert v.dfsExit == 0;

                boolean foundWhite = false;
                for (int adjacentIndex = indices.pop(); adjacentIndex < v.dependencies.size(); adjacentIndex++) {
                    final Entry adjacent = v.dependencies.get(adjacentIndex);
                    if (!adjacent.dfsColor) {
                        assert adjacent.dfsExit == 0;
                        foundWhite = true;
                        adjacent.dfsColor = true;
                        whites.remove(adjacent);
                        vertices.push(adjacent);
                        indices.push(adjacentIndex + 1);
                        indices.push(0);
                        //System.out.println("ENTER " + v + "[" + adjacentIndex + "]: " + adjacent);
                        break;
                    }
                }
                if (!foundWhite) {
                    assert v.dfsColor;
                    assert v.dfsExit == 0;
                    v.dfsExit = t++;
                    Entry e2 = vertices.pop();
                    assert e2 == v;
                    //System.out.println("EXIT (" + (t - 1) + "): " + v);
                }
            }
            assert indices.isEmpty();
        }
        assert entries.stream().allMatch(e -> e.dfsExit != 0);
        entries.sort(Comparator.comparingInt(e -> e.dfsExit));
    }

    interface Referrer {

        Referrer CODE = new Referrer() {
            @Override
            public String toString() {
                return "CODE";
            }
        };
    }

    sealed abstract class Entry implements Referrer {

        private String name;
        State state = State.NOT_WRITTEN;
        private final List<Referrer> referrers = new ArrayList<>();

        List<Entry> dependencies;
        boolean dfsColor;
        int dfsExit;

        boolean visited;
        List<Runnable> afterWrite = new ArrayList<>();

        private boolean isReady() {
            return state != State.NOT_WRITTEN;
        }

        String ref() {
            if (state == State.INLINE)
                return js();
            return getOrCreateName();
        }

        String getOrCreateName() {
            if (state == State.INLINE)
                throw new IllegalStateException();
            if (name == null)
                name = "cp" + (++counter);
            return name;
        }

        String name() {
            if (name == null)
                throw new IllegalStateException();
            else
                return name;
        }

        abstract String js();

        abstract void init();

        abstract boolean allDependenciesHaveBeenVisited();

        private boolean visited() {
            return visited || state == State.INLINE;
        }

        enum State {
            INLINE, NOT_WRITTEN, WRITTEN
        }

        @Override
        public String toString() {
            return state != State.INLINE ? getOrCreateName() : "<inline " + hashCode() + ">";
        }
    }

    final class Primitive extends Entry {
        final String code;

        public Primitive(boolean preferInline, String code) {
            this.code = code;

            if (preferInline)
                state = State.INLINE;
        }

        @Override
        void init() {
            dependencies = List.of();
        }

        @Override
        String js() {
            return code;
        }

        @Override
        boolean allDependenciesHaveBeenVisited() {
            return true;
        }
    }

    final class Array extends Entry {

        private final ArrayType type;
        private final int hash;
        private final Consumer<Consumer<Entry>> populator;

        private List<Entry> elements;

        public Array(ArrayType type, int hash, Consumer<Consumer<Entry>> populator) {
            this.type = type;
            this.hash = hash;
            this.populator = populator;
        }

        @Override
        void init() {
            elements = new ArrayList<>();
            populator.accept(elem -> {
                elem.referrers.add(this);
                elements.add(elem);
            });
            dependencies = elements;
        }

        @Override
        boolean allDependenciesHaveBeenVisited() {
            for (Entry e : elements)
                if (!e.visited())
                    return false;
            return true;
        }

        @Override
        String js() {
            // TODO identityhashcode
            StringJoiner joiner = new StringJoiner(", ",
                    "constArray(" + emitter.typeName(type) + ", [", "])");
            for (int i = 0; i < elements.size(); i++) {
                Entry elem = elements.get(i);
                if (elem.isReady()) {
                    joiner.add(elem.ref());
                } else {
                    joiner.add("'CONSTANT TO-BE-REPLACED LATER'");

                    int i2 = i;
                    elem.afterWrite.add(() -> {
                        if (state == State.INLINE)
                            throw new IllegalStateException();
                        emitter.printLine(name() + "[" + i2 + "] = " + elem.name + ";\r\n");
                    });
                }
            }
            return joiner.toString();
        }
    }

    final class Obj extends Entry {

        private final ReferenceType type;
        private final int hash;
        private Map<String, Entry> values;
        private final Consumer<BiConsumer<String, Entry>> populator;

        public Obj(ReferenceType type, int hash, Consumer<BiConsumer<String, Entry>> populator) {
            this.type = type;
            this.hash = hash;
            this.populator = populator;
        }

        @Override
        void init() {
            values = new HashMap<>();
            populator.accept((name, entry) -> {
                entry.referrers.add(this);
                values.put(name, entry);
            });
            dependencies = List.copyOf(values.values());
        }

        @Override
        boolean allDependenciesHaveBeenVisited() {
            for (Entry e : values.values())
                if (!e.visited())
                    return false;
            return true;
        }

        @Override
        String js() {
            StringJoiner joiner = new StringJoiner(", ",
                    "{__proto__:" + emitter.typeName(type) + ".prototype, hc:" + hash + ", ", "}");
            values.forEach((fieldName, c) -> {
                if (c.isReady())
                    joiner.add(fieldName + ": " + c.ref());
                else {
                    if (state == State.INLINE)
                        throw new RuntimeException();
                    c.afterWrite.add(() -> {
                        emitter.printLine(name() + "." + fieldName + " = " + c.name + ";");
                    });
                }
            });
            return joiner.toString();
        }
    }

    final class JSMap extends Entry {

        private List<Entry> keys, values;
        private final Consumer<BiConsumer<Entry, Entry>> populator;

        public JSMap(Consumer<BiConsumer<Entry, Entry>> populator) {
            this.populator = populator;
        }

        @Override
        void init() {
            keys = new ArrayList<>();
            values = new ArrayList<>();
            populator.accept((key, value) -> {
                key.referrers.add(this);
                value.referrers.add(this);
                keys.add(key);
                values.add(value);
            });

            dependencies = new ArrayList<>(keys);
            dependencies.addAll(values);
            dependencies = List.copyOf(dependencies);
        }

        @Override
        boolean allDependenciesHaveBeenVisited() {
            for (Entry e : keys)
                if (!e.visited())
                    return false;
            for (Entry e : values)
                if (!e.visited())
                    return false;
            return true;
        }

        @Override
        String js() {
            StringJoiner joiner = new StringJoiner(", ", "new Map([", "])");
            for (int i = 0; i < keys.size(); i++) {
                Entry k = keys.get(i), v = values.get(i);
                if (k.isReady() && v.isReady())
                    joiner.add("[" + k.ref() + ", " + v.ref() + "]");
                else {
                    if (state == State.INLINE)
                        throw new RuntimeException();

                    boolean[] ready = {k.isReady(), v.isReady(), false};
                    k.afterWrite.add(() -> {
                        if (ready[1] && !ready[2]) {
                            emitter.printLine(name() + "[" + k.name + "] = " + v.ref() + ";");
                            ready[2] = true;
                        } else
                            ready[0] = true;
                    });
                    v.afterWrite.add(() -> {
                        if (ready[0] && !ready[2]) {
                            emitter.printLine(name() + "[" + k.name + "] = " + v.ref() + ";");
                            ready[2] = true;
                        } else
                            ready[1] = true;
                    });
                }
            }
            return joiner.toString();
        }
    }
}
