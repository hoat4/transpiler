package com.flyordie.code.runtime;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

@SuppressWarnings("unchecked")
public abstract class StreamImpl<T, S extends BaseStream<T, S>> implements BaseStream<T, S>, Iterator<T> {

    private static final int NEW = 0, LINKED = 1, CONSUMED = 2, CLOSED = 3;

    public static final int SOURCE = 0;
    private static final int OP_FILTER = 1;
    private static final int OP_MAP = 2;
    private static final int OP_FLATMAP = 3;
    private static final int OP_DISTINCT = 4;
    private static final int OP_SORTED = 5;
    private static final int OP_PEEK = 6;
    private static final int OP_LIMIT = 7;
    private static final int OP_SKIP = 8;

    private final ArrayList<Runnable> onClose = new ArrayList<>();
    protected int state; // ld. fenti NEW, LINKED, CONSUMED, CLOSED
    final int op;
    final Object objArg;
    final long numArg;

    protected final StreamImpl<?, ?> parent;


    // intermediate op impl
    Iterator parentIt;

    Object next;
    int iteratorState; // 0: alap, 1: van következő, 2: vége

    Iterator flatMapIterator;
    Set set;
    long counter;


    private StreamImpl(StreamImpl<?, ?> parent, int op, Object objArg, long numArg) {
        this.op = op;
        this.objArg = objArg;
        this.numArg = numArg;

        this.parent = parent;
        if (parent != null) {
            parent.ensureNew();
            parent.state = LINKED;
        }
    }

    @Override
    public final boolean isParallel() {
        return false;
    }

    @Override
    public final S sequential() {
        return (S) this;
    }

    @Override
    public final S parallel() {
        throw new UnsupportedOperationException();
    }

    @Override
    public final S unordered() {
        throw new UnsupportedOperationException();
    }

    @Override
    public final S onClose(Runnable closeHandler) {
        onClose.add(closeHandler);
        return (S) this;
    }

    @Override
    public final void close() {
        onClose.forEach(Runnable::run);
        state = CLOSED;
        parent.close();
    }

    final void begin() {
        ensureNew();
        state = CONSUMED;
        if (op == OP_DISTINCT)
            set = new HashSet<>();
    }

    final void ensureNew() {
        if (state != NEW)
            throw new IllegalStateException(Integer.toString(state));
    }

    final void openParentIterator() {
        // TODO ezt csináljuk meg normálisan. valamint a sorted-nál is.
        parent.state = NEW;
        parentIt = parent.iterator();
        parent.state = LINKED;
    }

    @SuppressWarnings("rawtypes")
    public static final class ReferenceStream<T> extends StreamImpl<T, Stream<T>> implements Stream<T> {

        public ReferenceStream(StreamImpl<?, ?> parent, int op, Object objArg, long numArg) {
            super(parent, op, objArg, numArg);
        }

        @Override
        public Iterator<T> iterator() {
            begin();
            return switch (op) {
                case SOURCE -> objArg instanceof Iterable<?> iterable
                        ? (Iterator<T>) iterable.iterator() : (Iterator<T>) Spliterators.iterator((Spliterator<?>) objArg);
                case OP_SORTED -> {
                    parent.state = NEW;
                    Object[] a = ((ReferenceStream) parent).toArray();
                    Arrays.sort(a, (Comparator<? super Object>) objArg);
                    yield (Iterator) Arrays.asList(a).iterator();
                }
                case OP_SKIP -> {
                    openParentIterator();
                    for (long i = 0; i < numArg; i++) {
                        if (!parentIt.hasNext())
                            yield Collections.<T>emptyIterator();
                        parentIt.next();
                    }
                    yield parentIt;
                }
                default -> {
                    openParentIterator();
                    yield this;
                }
            };
        }

        @Override
        public Spliterator<T> spliterator() {
            // TODO karakterisztikák
            return Spliterators.spliteratorUnknownSize(iterator(), 0);
        }

        @Override
        public Stream<T> filter(Predicate<? super T> predicate) {
            Objects.requireNonNull(predicate);
            return new ReferenceStream<>(this, OP_FILTER, predicate, -1);
        }

        @Override
        public <R> Stream<R> map(Function<? super T, ? extends R> mapper) {
            Objects.requireNonNull(mapper);
            return new ReferenceStream<>(this, OP_MAP, mapper, -1);
        }

        @Override
        public IntStream mapToInt(ToIntFunction<? super T> mapper) {
            Objects.requireNonNull(mapper);
            return new IntStreamImpl(this, OP_MAP, mapper, -1);
        }

        @Override
        public LongStream mapToLong(ToLongFunction<? super T> mapper) {
            Objects.requireNonNull(mapper);
            return new LongStreamImpl(this, OP_MAP, mapper, -1);
        }

        @Override
        public DoubleStream mapToDouble(ToDoubleFunction<? super T> mapper) {
            Objects.requireNonNull(mapper);
            return new DoubleStreamImpl(this, OP_MAP, mapper, -1);
        }

        @Override
        public <R> Stream<R> flatMap(Function<? super T, ? extends Stream<? extends R>> mapper) {
            Objects.requireNonNull(mapper);
            return new ReferenceStream<>(this, OP_FLATMAP, mapper, -1);
        }

        @Override
        public IntStream flatMapToInt(Function<? super T, ? extends IntStream> mapper) {
            Objects.requireNonNull(mapper);
            return new IntStreamImpl(this, OP_FLATMAP, mapper, -1);
        }

        @Override
        public LongStream flatMapToLong(Function<? super T, ? extends LongStream> mapper) {
            Objects.requireNonNull(mapper);
            return new LongStreamImpl(this, OP_FLATMAP, mapper, -1);
        }

        @Override
        public DoubleStream flatMapToDouble(Function<? super T, ? extends DoubleStream> mapper) {
            Objects.requireNonNull(mapper);
            return new DoubleStreamImpl(this, OP_FLATMAP, mapper, -1);
        }

        @Override
        public Stream<T> distinct() {
            return new ReferenceStream<>(this, OP_DISTINCT, null, -1);
        }

        @Override
        public Stream<T> sorted() {
            return new ReferenceStream<>(this, OP_SORTED, null, -1);
        }

        @Override
        public Stream<T> sorted(Comparator<? super T> comparator) {
            // kéne írni bugreportot hogy lehagyták a javadocból hogy nonnull a comparator
            Objects.requireNonNull(comparator);
            return new ReferenceStream<>(this, OP_SORTED, comparator, -1);
        }

        @Override
        public Stream<T> peek(Consumer<? super T> action) {
            return new ReferenceStream<>(this, OP_PEEK, action, -1);
        }

        @Override
        public Stream<T> limit(long maxSize) {
            if (maxSize < 0)
                throw new IllegalArgumentException();
            return new ReferenceStream<>(this, OP_LIMIT, null, maxSize);
        }

        @Override
        public Stream<T> skip(long n) {
            if (n < 0)
                throw new IllegalArgumentException();
            return new ReferenceStream<>(this, OP_SKIP, null, n);
        }

        @Override
        public void forEach(Consumer<? super T> action) {
            Objects.requireNonNull(action);
            forEachOrdered(action);
        }

        @Override
        public void forEachOrdered(Consumer<? super T> action) {
            Objects.requireNonNull(action);
            for (Iterator<T> it = iterator(); it.hasNext(); )
                action.accept(it.next());
        }

        @Override
        public Object[] toArray() {
            List<Object> l = new ArrayList<>();
            forEach(l::add);
            return l.toArray();
        }

        @Override
        public <A> A[] toArray(IntFunction<A[]> generator) {
            Objects.requireNonNull(generator);
            List<Object> l = new ArrayList<>();
            forEach(l::add);
            return l.toArray(generator);
        }

        @Override
        public T reduce(T identity, BinaryOperator<T> accumulator) {
            Objects.requireNonNull(accumulator);
            T t = identity;

            for (Iterator<T> it = iterator(); it.hasNext(); )
                t = accumulator.apply(t, it.next());

            return t;
        }

        @Override
        public Optional<T> reduce(BinaryOperator<T> accumulator) {
            Objects.requireNonNull(accumulator);

            Iterator<T> it = iterator();
            if (!it.hasNext())
                return Optional.empty();

            T t = it.next();
            while (it.hasNext())
                t = accumulator.apply(t, it.next());

            return Optional.of(t);
        }

        @Override
        public <U> U reduce(U identity, BiFunction<U, ? super T, U> accumulator, BinaryOperator<U> combiner) {
            Objects.requireNonNull(accumulator);
            Objects.requireNonNull(combiner);

            U u = identity;

            for (Iterator<T> it = iterator(); it.hasNext(); )
                u = accumulator.apply(u, it.next());

            return u;
        }

        @Override
        public <R> R collect(Supplier<R> supplier, BiConsumer<R, ? super T> accumulator, BiConsumer<R, R> combiner) {
            Objects.requireNonNull(accumulator);
            Objects.requireNonNull(combiner);

            R r = supplier.get();
            for (Iterator<T> it = iterator(); it.hasNext(); )
                accumulator.accept(r, it.next());
            return r;
        }

        @Override
        public <R, A> R collect(Collector<? super T, A, R> collector) {
            A a = collector.supplier().get();
            for (Iterator<T> it = iterator(); it.hasNext(); )
                collector.accumulator().accept(a, it.next());
            return collector.finisher().apply(a);
        }

        @Override
        public Optional<T> min(Comparator<? super T> comparator) {
            return minOrMax(comparator, 1);
        }

        @Override
        public Optional<T> max(Comparator<? super T> comparator) {
            return minOrMax(comparator, -1);
        }

        private Optional<T> minOrMax(Comparator<? super T> comparator, int minOrMax) {
            // itt is kifelejtették javadocból hogy nem lehet null

            Objects.requireNonNull(comparator);

            Iterator<T> it = iterator();
            if (!it.hasNext())
                return Optional.empty();

            T v = it.next();

            while (it.hasNext()) {
                T v2 = it.next();
                int cmp = comparator.compare(v, v2);
                if (cmp * minOrMax > 0)
                    v = v2;
            }

            return Optional.of(v);
        }

        @Override
        public long count() {
            long count = 0;
            for (Iterator<T> it = iterator(); it.hasNext(); ) {
                count++;
                it.next();
            }
            return count;
        }

        @Override
        public boolean anyMatch(Predicate<? super T> predicate) {
            for (Iterator<T> it = iterator(); it.hasNext(); ) {
                if (predicate.test(it.next()))
                    return true;
            }
            return false;
        }

        @Override
        public boolean allMatch(Predicate<? super T> predicate) {
            for (Iterator<T> it = iterator(); it.hasNext(); ) {
                if (!predicate.test(it.next()))
                    return false;
            }
            return true;
        }

        @Override
        public boolean noneMatch(Predicate<? super T> predicate) {
            for (Iterator<T> it = iterator(); it.hasNext(); ) {
                if (predicate.test(it.next()))
                    return false;
            }
            return true;
        }

        @Override
        public Optional<T> findFirst() {
            Iterator<T> it = iterator();
            return it.hasNext() ? Optional.of(it.next()) : Optional.empty();
        }

        @Override
        public Optional<T> findAny() {
            return findFirst();
        }

        // intermediate op impl

        private boolean advance() {
            switch (op) {
                case OP_FILTER -> {
                    while (true) {
                        if (!parentIt.hasNext())
                            return false;
                        Object o = parentIt.next();
                        if (((Predicate) objArg).test(o)) {
                            next = o;
                            return true;
                        }
                    }
                }
                case OP_MAP -> {
                    if (!parentIt.hasNext())
                        return false;
                    Object o = parentIt.next();
                    next = ((Function) objArg).apply(o);
                    return true;
                }
                case OP_FLATMAP -> {
                    while (true) {
                        if (flatMapIterator != null) {
                            if (flatMapIterator.hasNext()) {
                                next = flatMapIterator.next();
                                return true;
                            } else
                                flatMapIterator = null;
                        }
                        if (!parentIt.hasNext())
                            return false;
                        Object o = parentIt.next();
                        flatMapIterator = ((Stream) ((Function) objArg).apply(o)).iterator();
                    }
                }
                case OP_DISTINCT -> {
                    while (true) {
                        if (!parentIt.hasNext())
                            return false;
                        Object o = parentIt.next();
                        if (set.add(o)) {
                            next = o;
                            return true;
                        }
                    }
                }
                // OP_SORTED és OP_SKIP iteratorImpl-ben van implementálva
                case OP_PEEK -> {
                    if (!parentIt.hasNext())
                        return false;
                    Object o = parentIt.next();
                    ((Consumer) objArg).accept(o);
                    next = o;
                    return true;
                }
                case OP_LIMIT -> {
                    if (counter++ > numArg)
                        return false;

                    if (!parentIt.hasNext())
                        return false;
                    next = parentIt.next();
                    return true;
                }
                default -> throw new RuntimeException();
            }
        }

        @Override
        public boolean hasNext() {
            if (iteratorState == 0)
                iteratorState = advance() ? 1 : 2;
            return iteratorState == 1;
        }

        @Override
        public T next() {
            if (iteratorState == 0)
                iteratorState = advance() ? 1 : 2;
            if (iteratorState == 2)
                throw new NoSuchElementException();
            iteratorState = 0;
            return (T) next;
        }
    }

    @SuppressWarnings("rawtypes")
    public static final class IntStreamImpl extends StreamImpl<Integer, IntStream>
            implements IntStream, PrimitiveIterator.OfInt {

        /**
         * @param objArg ha SOURCE, akkor csak int[] vagy Spliterator.OfInt lehet
         */
        public IntStreamImpl(StreamImpl<?, ?> parent, int op, Object objArg, long numArg) {
            super(parent, op, objArg, numArg);
        }

        @Override
        public PrimitiveIterator.OfInt iterator() {
            begin();
            return switch (op) {
                case SOURCE -> objArg.getClass() == int[].class ?
                        new IntArrayIterator((int[]) objArg) :
                        Spliterators.iterator((Spliterator.OfInt) objArg);
                case OP_SORTED -> {
                    parent.state = NEW;
                    int[] a = ((IntStreamImpl) parent).toArray();
                    Arrays.sort(a);
                    yield new IntArrayIterator(a);
                }
                case OP_SKIP -> {
                    openParentIterator();
                    PrimitiveIterator.OfInt parentIt = (OfInt) this.parentIt;
                    for (long i = 0; i < numArg && parentIt.hasNext(); i++)
                        parentIt.next();
                    yield parentIt;
                }
                default -> {
                    openParentIterator();
                    yield this;
                }
            };
        }

        @Override
        public Spliterator.OfInt spliterator() {
            return Spliterators.spliteratorUnknownSize(iterator(), 0);
        }

        @Override
        public IntStream filter(IntPredicate predicate) {
            Objects.requireNonNull(predicate);
            return new IntStreamImpl(this, OP_FILTER, predicate, -1);
        }

        @Override
        public IntStream map(IntUnaryOperator mapper) {
            Objects.requireNonNull(mapper);
            return new IntStreamImpl(this, OP_MAP, (Function<Integer, Integer>) mapper::applyAsInt, -1);
        }

        @Override
        public <U> Stream<U> mapToObj(IntFunction<? extends U> mapper) {
            Objects.requireNonNull(mapper);
            return new ReferenceStream<>(this, OP_MAP, (Function<Integer, U>) mapper::apply, -1);
        }

        @Override
        public LongStream mapToLong(IntToLongFunction mapper) {
            Objects.requireNonNull(mapper);
            return new LongStreamImpl(this, OP_MAP, (Function<Integer, Long>) mapper::applyAsLong, -1);
        }

        @Override
        public DoubleStream mapToDouble(IntToDoubleFunction mapper) {
            Objects.requireNonNull(mapper);
            return new DoubleStreamImpl(this, OP_MAP, (Function<Integer, Double>) mapper::applyAsDouble, -1);
        }

        @Override
        public IntStream flatMap(IntFunction<? extends IntStream> mapper) {
            Objects.requireNonNull(mapper);
            return new IntStreamImpl(this, OP_FLATMAP, (Function<Integer, IntStream>) mapper::apply, -1);
        }

        @Override
        public IntStream distinct() {
            return new IntStreamImpl(this, OP_DISTINCT, null, -1);
        }

        @Override
        public IntStream sorted() {
            return new IntStreamImpl(this, OP_SORTED, null, -1);
        }

        @Override
        public IntStream peek(IntConsumer action) {
            return new IntStreamImpl(this, OP_PEEK, action, -1);
        }

        @Override
        public IntStream limit(long maxSize) {
            if (maxSize < 0)
                throw new IllegalArgumentException();
            return new IntStreamImpl(this, OP_LIMIT, null, maxSize);
        }

        @Override
        public IntStream skip(long n) {
            if (n < 0)
                throw new IllegalArgumentException();
            return new IntStreamImpl(this, OP_SKIP, null, n);
        }

        @Override
        public void forEach(IntConsumer action) {
            Objects.requireNonNull(action);
            forEachOrdered(action);
        }

        @Override
        public void forEachOrdered(IntConsumer action) {
            Objects.requireNonNull(action);
            for (Iterator<Integer> it = iterator(); it.hasNext(); )
                action.accept(it.next());
        }

        @Override
        public int[] toArray() {
            List<Integer> l = new ArrayList<>();
            forEach(l::add);
            int[] a = new int[l.size()];
            for (int i = 0; i < a.length; i++)
                a[i] = l.get(i);
            return a;
        }

        @Override
        public int reduce(int identity, IntBinaryOperator op) {
            Objects.requireNonNull(op);
            Integer t = identity;

            for (Iterator<Integer> it = iterator(); it.hasNext(); )
                t = op.applyAsInt(t, it.next());

            return t;
        }

        @Override
        public OptionalInt reduce(IntBinaryOperator accumulator) {
            Objects.requireNonNull(accumulator);

            Iterator<Integer> it = iterator();
            if (!it.hasNext())
                return OptionalInt.empty();

            int t = it.next();
            while (it.hasNext())
                t = accumulator.applyAsInt(t, it.next());

            return OptionalInt.of(t);
        }


        @Override
        public <R> R collect(Supplier<R> supplier, ObjIntConsumer<R> accumulator, BiConsumer<R, R> combiner) {
            Objects.requireNonNull(accumulator);
            Objects.requireNonNull(combiner);

            R r = supplier.get();
            for (Iterator<Integer> it = iterator(); it.hasNext(); )
                accumulator.accept(r, it.next());
            return r;
        }

        @Override
        public OptionalInt min() {
            return minOrMax(false);
        }

        @Override
        public OptionalInt max() {
            return minOrMax(true);
        }

        private OptionalInt minOrMax(boolean max) {
            Iterator<Integer> it = iterator();
            if (!it.hasNext())
                return OptionalInt.empty();

            int v = it.next();

            while (it.hasNext()) {
                int v2 = it.next();
                int cmp = v - v2;
                if (max) {
                    if (v2 > v)
                        v = v2;
                } else {
                    if (v2 < v)
                        v = v2;
                }
            }

            return OptionalInt.of(v);
        }

        @Override
        public long count() {
            long count = 0;
            for (Iterator<Integer> it = iterator(); it.hasNext(); ) {
                count++;
                it.next();
            }
            return count;
        }

        @Override
        public boolean anyMatch(IntPredicate predicate) {
            for (Iterator<Integer> it = iterator(); it.hasNext(); ) {
                if (predicate.test(it.next()))
                    return true;
            }
            return false;
        }

        @Override
        public boolean allMatch(IntPredicate predicate) {
            for (Iterator<Integer> it = iterator(); it.hasNext(); ) {
                if (!predicate.test(it.next()))
                    return false;
            }
            return true;
        }

        @Override
        public boolean noneMatch(IntPredicate predicate) {
            for (Iterator<Integer> it = iterator(); it.hasNext(); ) {
                if (predicate.test(it.next()))
                    return false;
            }
            return true;
        }


        @Override
        public OptionalInt findFirst() {
            Iterator<Integer> it = iterator();
            return it.hasNext() ? OptionalInt.of(it.next()) : OptionalInt.empty();
        }

        @Override
        public OptionalInt findAny() {
            return findFirst();
        }

        @Override
        public int sum() {
            int s = 0;
            for (Iterator<Integer> it = iterator(); it.hasNext(); ) {
                s += it.next();
            }
            return s;
        }

        @Override
        public OptionalDouble average() {
            int s = 0;
            long count = 0;
            for (Iterator<Integer> it = iterator(); it.hasNext(); ) {
                s += it.next();
                count++;
            }
            return count == 0 ? OptionalDouble.empty() : OptionalDouble.of((double) s / count);
        }

        @Override
        public IntSummaryStatistics summaryStatistics() {
            int sum = 0, min = Integer.MAX_VALUE, max = Integer.MIN_VALUE;
            long count = 0;
            for (Iterator<Integer> it = iterator(); it.hasNext(); ) {
                int v = it.next();
                sum += v;
                min = Math.min(min, v);
                max = Math.max(max, v);
                count++;
            }
            return new IntSummaryStatistics(count, min, max, sum);
        }

        @Override
        public LongStream asLongStream() {
            return mapToLong(i -> i);
        }

        @Override
        public DoubleStream asDoubleStream() {
            return mapToDouble(i -> i);
        }

        @Override
        public Stream<Integer> boxed() {
            return mapToObj(i -> i);
        }

        // intermediate op impl

        private boolean advance() {
            switch (op) {
                case OP_FILTER -> {
                    while (true) {
                        if (!parentIt.hasNext())
                            return false;
                        Object o = parentIt.next();
                        if (((IntPredicate) objArg).test((Integer) o)) {
                            next = o;
                            return true;
                        }
                    }
                }
                case OP_MAP -> {
                    if (!parentIt.hasNext())
                        return false;
                    Object o = parentIt.next();
                    if (objArg instanceof ToIntFunction f)
                        next = f.applyAsInt(o);
                    else
                        next = ((Function) objArg).apply(o);
                    return true;
                }
                case OP_FLATMAP -> {
                    while (true) {
                        if (flatMapIterator != null) {
                            if (flatMapIterator.hasNext()) {
                                next = flatMapIterator.next();
                                return true;
                            } else
                                flatMapIterator = null;
                        }
                        if (!parentIt.hasNext())
                            return false;
                        Object o = parentIt.next();
                        flatMapIterator = ((Stream) ((Function) objArg).apply(o)).iterator();
                    }
                }
                case OP_DISTINCT -> {
                    while (true) {
                        if (!parentIt.hasNext())
                            return false;
                        Object o = parentIt.next();
                        if (set.add(o)) {
                            next = o;
                            return true;
                        }
                    }
                }
                // OP_SORTED és OP_SKIP iteratorImpl-ben van implementálva
                case OP_PEEK -> {
                    if (!parentIt.hasNext())
                        return false;
                    Object o = parentIt.next();
                    ((Consumer) objArg).accept(o);
                    next = o;
                    return true;
                }
                case OP_LIMIT -> {
                    if (counter++ > numArg)
                        return false;

                    if (!parentIt.hasNext())
                        return false;
                    next = parentIt.next();
                    return true;
                }
                default -> throw new RuntimeException();
            }
        }

        @Override
        public boolean hasNext() {
            if (iteratorState == 0)
                iteratorState = advance() ? 1 : 2;
            return iteratorState == 1;
        }

        @Override
        public int nextInt() {
            if (iteratorState == 0)
                iteratorState = advance() ? 1 : 2;
            if (iteratorState == 2)
                throw new NoSuchElementException();
            iteratorState = 0;
            return (Integer) next;
        }
    }


    @SuppressWarnings("rawtypes")
    public static final class LongStreamImpl extends StreamImpl<Long, LongStream>
            implements LongStream, PrimitiveIterator.OfLong {

        public LongStreamImpl(StreamImpl<?, ?> parent, int op, Object objArg, long numArg) {
            super(parent, op, objArg, numArg);
        }

        @Override
        public PrimitiveIterator.OfLong iterator() {
            begin();
            return switch (op) {
                case SOURCE -> objArg.getClass() == long[].class ?
                        new LongArrayIterator((long[]) objArg) :
                        Spliterators.iterator((Spliterator.OfLong) objArg);
                case OP_SORTED -> {
                    parent.state = NEW;
                    long[] a = ((LongStreamImpl) parent).toArray();
                    Arrays.sort(a);
                    yield new LongArrayIterator(a);
                }
                case OP_SKIP -> {
                    openParentIterator();
                    PrimitiveIterator.OfLong parentIt = (OfLong) this.parentIt;
                    for (long i = 0; i < numArg && parentIt.hasNext(); i++)
                        parentIt.next();
                    yield parentIt;
                }
                default -> {
                    openParentIterator();
                    yield this;
                }
            };
        }

        @Override
        public Spliterator.OfLong spliterator() {
            return Spliterators.spliteratorUnknownSize(iterator(), 0);
        }

        @Override
        public LongStream filter(LongPredicate predicate) {
            Objects.requireNonNull(predicate);
            return new LongStreamImpl(this, OP_FILTER, predicate, -1);
        }

        @Override
        public LongStream map(LongUnaryOperator mapper) {
            Objects.requireNonNull(mapper);
            return new LongStreamImpl(this, OP_MAP, (Function<Long, Long>) mapper::applyAsLong, -1);
        }

        @Override
        public <U> Stream<U> mapToObj(LongFunction<? extends U> mapper) {
            Objects.requireNonNull(mapper);
            return new ReferenceStream<>(this, OP_MAP, (Function<Long, U>) mapper::apply, -1);
        }

        @Override
        public IntStream mapToInt(LongToIntFunction mapper) {
            Objects.requireNonNull(mapper);
            return new IntStreamImpl(this, OP_MAP, (Function<Long, Integer>) mapper::applyAsInt, -1);
        }

        @Override
        public DoubleStream mapToDouble(LongToDoubleFunction mapper) {
            Objects.requireNonNull(mapper);
            return new DoubleStreamImpl(this, OP_MAP, (Function<Long, Double>) mapper::applyAsDouble, -1);
        }

        @Override
        public LongStream flatMap(LongFunction<? extends LongStream> mapper) {
            Objects.requireNonNull(mapper);
            return new LongStreamImpl(this, OP_FLATMAP, (Function<Long, LongStream>) mapper::apply, -1);
        }

        @Override
        public LongStream distinct() {
            return new LongStreamImpl(this, OP_DISTINCT, null, -1);
        }

        @Override
        public LongStream sorted() {
            return new LongStreamImpl(this, OP_SORTED, null, -1);
        }

        @Override
        public LongStream peek(LongConsumer action) {
            return new LongStreamImpl(this, OP_PEEK, action, -1);
        }

        @Override
        public LongStream limit(long maxSize) {
            if (maxSize < 0)
                throw new IllegalArgumentException();
            return new LongStreamImpl(this, OP_LIMIT, null, maxSize);
        }

        @Override
        public LongStream skip(long n) {
            if (n < 0)
                throw new IllegalArgumentException();
            return new LongStreamImpl(this, OP_SKIP, null, n);
        }

        @Override
        public void forEach(LongConsumer action) {
            Objects.requireNonNull(action);
            forEachOrdered(action);
        }

        @Override
        public void forEachOrdered(LongConsumer action) {
            Objects.requireNonNull(action);
            for (Iterator<Long> it = iterator(); it.hasNext(); )
                action.accept(it.next());
        }

        @Override
        public long[] toArray() {
            List<Long> l = new ArrayList<>();
            forEach(l::add);
            long[] a = new long[l.size()];
            for (int i = 0; i < a.length; i++)
                a[i] = l.get(i);
            return a;
        }

        @Override
        public long reduce(long identity, LongBinaryOperator op) {
            Objects.requireNonNull(op);
            long t = identity;

            for (Iterator<Long> it = iterator(); it.hasNext(); )
                t = op.applyAsLong(t, it.next());

            return t;
        }

        @Override
        public OptionalLong reduce(LongBinaryOperator accumulator) {
            Objects.requireNonNull(accumulator);

            Iterator<Long> it = iterator();
            if (!it.hasNext())
                return OptionalLong.empty();

            long t = it.next();
            while (it.hasNext())
                t = accumulator.applyAsLong(t, it.next());

            return OptionalLong.of(t);
        }


        @Override
        public <R> R collect(Supplier<R> supplier, ObjLongConsumer<R> accumulator, BiConsumer<R, R> combiner) {
            Objects.requireNonNull(accumulator);
            Objects.requireNonNull(combiner);

            R r = supplier.get();
            for (Iterator<Long> it = iterator(); it.hasNext(); )
                accumulator.accept(r, it.next());
            return r;
        }

        @Override
        public OptionalLong min() {
            return minOrMax(false);
        }

        @Override
        public OptionalLong max() {
            return minOrMax(true);
        }

        private OptionalLong minOrMax(boolean max) {
            Iterator<Long> it = iterator();
            if (!it.hasNext())
                return OptionalLong.empty();

            long v = it.next();

            while (it.hasNext()) {
                long v2 = it.next();
                if (max) {
                    if (v2 > v)
                        v = v2;
                } else {
                    if (v2 < v)
                        v = v2;
                }
            }

            return OptionalLong.of(v);
        }

        @Override
        public long count() {
            long count = 0;
            for (Iterator<Long> it = iterator(); it.hasNext(); ) {
                count++;
                it.next();
            }
            return count;
        }

        @Override
        public boolean anyMatch(LongPredicate predicate) {
            for (Iterator<Long> it = iterator(); it.hasNext(); ) {
                if (predicate.test(it.next()))
                    return true;
            }
            return false;
        }

        @Override
        public boolean allMatch(LongPredicate predicate) {
            for (Iterator<Long> it = iterator(); it.hasNext(); ) {
                if (!predicate.test(it.next()))
                    return false;
            }
            return true;
        }

        @Override
        public boolean noneMatch(LongPredicate predicate) {
            for (Iterator<Long> it = iterator(); it.hasNext(); ) {
                if (predicate.test(it.next()))
                    return false;
            }
            return true;
        }


        @Override
        public OptionalLong findFirst() {
            Iterator<Long> it = iterator();
            return it.hasNext() ? OptionalLong.of(it.next()) : OptionalLong.empty();
        }

        @Override
        public OptionalLong findAny() {
            return findFirst();
        }

        @Override
        public long sum() {
            int s = 0;
            for (Iterator<Long> it = iterator(); it.hasNext(); ) {
                s += it.next();
            }
            return s;
        }

        @Override
        public OptionalDouble average() {
            int s = 0;
            long count = 0;
            for (Iterator<Long> it = iterator(); it.hasNext(); ) {
                s += it.next();
                count++;
            }
            return count == 0 ? OptionalDouble.empty() : OptionalDouble.of((double) s / count);
        }

        @Override
        public LongSummaryStatistics summaryStatistics() {
            long sum = 0, min = Long.MAX_VALUE, max = Long.MIN_VALUE;
            long count = 0;
            for (Iterator<Long> it = iterator(); it.hasNext(); ) {
                long v = it.next();
                sum += v;
                min = Math.min(min, v);
                max = Math.max(max, v);
                count++;
            }
            return new LongSummaryStatistics(count, min, max, sum);
        }

        @Override
        public DoubleStream asDoubleStream() {
            return mapToDouble(i -> i);
        }

        @Override
        public Stream<Long> boxed() {
            return mapToObj(i -> i);
        }

        // intermediate op impl

        private boolean advance() {
            switch (op) {
                case OP_FILTER -> {
                    while (true) {
                        if (!parentIt.hasNext())
                            return false;
                        Object o = parentIt.next();
                        if (((LongPredicate) objArg).test((Long) o)) {
                            next = o;
                            return true;
                        }
                    }
                }
                case OP_MAP -> {
                    if (!parentIt.hasNext())
                        return false;
                    Object o = parentIt.next();
                    next = ((Function) objArg).apply(o);
                    return true;
                }
                case OP_FLATMAP -> {
                    while (true) {
                        if (flatMapIterator != null) {
                            if (flatMapIterator.hasNext()) {
                                next = flatMapIterator.next();
                                return true;
                            } else
                                flatMapIterator = null;
                        }
                        if (!parentIt.hasNext())
                            return false;
                        Object o = parentIt.next();
                        flatMapIterator = ((Stream) ((Function) objArg).apply(o)).iterator();
                    }
                }
                case OP_DISTINCT -> {
                    while (true) {
                        if (!parentIt.hasNext())
                            return false;
                        Object o = parentIt.next();
                        if (set.add(o)) {
                            next = o;
                            return true;
                        }
                    }
                }
                // OP_SORTED és OP_SKIP iteratorImpl-ben van implementálva
                case OP_PEEK -> {
                    if (!parentIt.hasNext())
                        return false;
                    Object o = parentIt.next();
                    ((Consumer) objArg).accept(o);
                    next = o;
                    return true;
                }
                case OP_LIMIT -> {
                    if (counter++ > numArg)
                        return false;

                    if (!parentIt.hasNext())
                        return false;
                    next = parentIt.next();
                    return true;
                }
                default -> throw new RuntimeException();
            }
        }

        @Override
        public boolean hasNext() {
            if (iteratorState == 0)
                iteratorState = advance() ? 1 : 2;
            return iteratorState == 1;
        }

        @Override
        public long nextLong() {
            if (iteratorState == 0)
                iteratorState = advance() ? 1 : 2;
            if (iteratorState == 2)
                throw new NoSuchElementException();
            iteratorState = 0;
            return (Long) next;
        }
    }


    @SuppressWarnings("rawtypes")
    public static final class DoubleStreamImpl extends StreamImpl<Double, DoubleStream>
            implements DoubleStream, PrimitiveIterator.OfDouble {

        public DoubleStreamImpl(StreamImpl<?, ?> parent, int op, Object objArg, long numArg) {
            super(parent, op, objArg, numArg);
        }

        @Override
        public PrimitiveIterator.OfDouble iterator() {
            begin();
            return switch (op) {
                case SOURCE -> objArg.getClass() == double[].class ?
                        new DoubleArrayIterator((double[]) objArg) :
                        Spliterators.iterator((Spliterator.OfDouble) objArg);
                case OP_SORTED -> {
                    parent.state = NEW;
                    double[] a = ((DoubleStreamImpl) parent).toArray();
                    Arrays.sort(a);
                    yield new DoubleArrayIterator(a);
                }
                case OP_SKIP -> {
                    openParentIterator();
                    PrimitiveIterator.OfDouble parentIt = (OfDouble) this.parentIt;
                    for (long i = 0; i < numArg && parentIt.hasNext(); i++)
                        parentIt.next();
                    yield parentIt;
                }
                default -> {
                    openParentIterator();
                    yield this;
                }
            };
        }

        @Override
        public Spliterator.OfDouble spliterator() {
            return Spliterators.spliteratorUnknownSize(iterator(), 0);
        }

        @Override
        public DoubleStream filter(DoublePredicate predicate) {
            Objects.requireNonNull(predicate);
            return new DoubleStreamImpl(this, OP_FILTER, predicate, -1);
        }

        @Override
        public DoubleStream map(DoubleUnaryOperator mapper) {
            Objects.requireNonNull(mapper);
            return new DoubleStreamImpl(this, OP_MAP, (Function<Double, Double>) mapper::applyAsDouble, -1);
        }

        @Override
        public <U> Stream<U> mapToObj(DoubleFunction<? extends U> mapper) {
            Objects.requireNonNull(mapper);
            return new ReferenceStream<>(this, OP_MAP, (Function<Double, U>) mapper::apply, -1);
        }

        @Override
        public LongStream mapToLong(DoubleToLongFunction mapper) {
            Objects.requireNonNull(mapper);
            return new LongStreamImpl(this, OP_MAP, (Function<Double, Long>) mapper::applyAsLong, -1);
        }

        @Override
        public IntStream mapToInt(DoubleToIntFunction mapper) {
            Objects.requireNonNull(mapper);
            return new IntStreamImpl(this, OP_MAP, (Function<Double, Integer>) mapper::applyAsInt, -1);
        }

        @Override
        public DoubleStream flatMap(DoubleFunction<? extends DoubleStream> mapper) {
            Objects.requireNonNull(mapper);
            return new DoubleStreamImpl(this, OP_FLATMAP, (Function<Double, DoubleStream>) mapper::apply, -1);
        }

        @Override
        public DoubleStream distinct() {
            return new DoubleStreamImpl(this, OP_DISTINCT, null, -1);
        }

        @Override
        public DoubleStream sorted() {
            return new DoubleStreamImpl(this, OP_SORTED, null, -1);
        }

        @Override
        public DoubleStream peek(DoubleConsumer action) {
            return new DoubleStreamImpl(this, OP_PEEK, action, -1);
        }

        @Override
        public DoubleStream limit(long maxSize) {
            if (maxSize < 0)
                throw new IllegalArgumentException();
            return new DoubleStreamImpl(this, OP_LIMIT, null, maxSize);
        }

        @Override
        public DoubleStream skip(long n) {
            if (n < 0)
                throw new IllegalArgumentException();
            return new DoubleStreamImpl(this, OP_SKIP, null, n);
        }

        @Override
        public void forEach(DoubleConsumer action) {
            Objects.requireNonNull(action);
            forEachOrdered(action);
        }

        @Override
        public void forEachOrdered(DoubleConsumer action) {
            Objects.requireNonNull(action);
            for (Iterator<Double> it = iterator(); it.hasNext(); )
                action.accept(it.next());
        }

        @Override
        public double[] toArray() {
            List<Double> l = new ArrayList<>();
            forEach(l::add);
            double[] a = new double[l.size()];
            for (int i = 0; i < a.length; i++)
                a[i] = l.get(i);
            return a;
        }

        @Override
        public double reduce(double identity, DoubleBinaryOperator op) {
            Objects.requireNonNull(op);
            Double t = identity;

            for (Iterator<Double> it = iterator(); it.hasNext(); )
                t = op.applyAsDouble(t, it.next());

            return t;
        }

        @Override
        public OptionalDouble reduce(DoubleBinaryOperator accumulator) {
            Objects.requireNonNull(accumulator);

            Iterator<Double> it = iterator();
            if (!it.hasNext())
                return OptionalDouble.empty();

            double t = it.next();
            while (it.hasNext())
                t = accumulator.applyAsDouble(t, it.next());

            return OptionalDouble.of(t);
        }


        @Override
        public <R> R collect(Supplier<R> supplier, ObjDoubleConsumer<R> accumulator, BiConsumer<R, R> combiner) {
            Objects.requireNonNull(accumulator);
            Objects.requireNonNull(combiner);

            R r = supplier.get();
            for (Iterator<Double> it = iterator(); it.hasNext(); )
                accumulator.accept(r, it.next());
            return r;
        }

        @Override
        public OptionalDouble min() {
            return minOrMax(false);
        }

        @Override
        public OptionalDouble max() {
            return minOrMax(true);
        }

        private OptionalDouble minOrMax(boolean max) {
            Iterator<Double> it = iterator();
            if (!it.hasNext())
                return OptionalDouble.empty();

            double v = it.next();

            while (it.hasNext()) {
                double v2 = it.next();
                if (max) {
                    if (v2 > v)
                        v = v2;
                } else {
                    if (v2 < v)
                        v = v2;
                }
            }

            return OptionalDouble.of(v);
        }

        @Override
        public long count() {
            long count = 0;
            for (Iterator<Double> it = iterator(); it.hasNext(); ) {
                count++;
                it.next();
            }
            return count;
        }

        @Override
        public boolean anyMatch(DoublePredicate predicate) {
            for (Iterator<Double> it = iterator(); it.hasNext(); ) {
                if (predicate.test(it.next()))
                    return true;
            }
            return false;
        }

        @Override
        public boolean allMatch(DoublePredicate predicate) {
            for (Iterator<Double> it = iterator(); it.hasNext(); ) {
                if (!predicate.test(it.next()))
                    return false;
            }
            return true;
        }

        @Override
        public boolean noneMatch(DoublePredicate predicate) {
            for (Iterator<Double> it = iterator(); it.hasNext(); ) {
                if (predicate.test(it.next()))
                    return false;
            }
            return true;
        }


        @Override
        public OptionalDouble findFirst() {
            Iterator<Double> it = iterator();
            return it.hasNext() ? OptionalDouble.of(it.next()) : OptionalDouble.empty();
        }

        @Override
        public OptionalDouble findAny() {
            return findFirst();
        }

        @Override
        public double sum() {
            double s = 0;
            for (Iterator<Double> it = iterator(); it.hasNext(); ) {
                s += it.next();
            }
            return s;
        }

        @Override
        public OptionalDouble average() {
            double s = 0;
            long count = 0;
            for (Iterator<Double> it = iterator(); it.hasNext(); ) {
                s += it.next();
                count++;
            }
            return count == 0 ? OptionalDouble.empty() : OptionalDouble.of((double) s / count);
        }

        @Override
        public DoubleSummaryStatistics summaryStatistics() {
            double sum = 0, min = Double.MAX_VALUE, max = Double.MIN_VALUE;
            long count = 0;
            for (Iterator<Double> it = iterator(); it.hasNext(); ) {
                double v = it.next();
                sum += v;
                min = Math.min(min, v);
                max = Math.max(max, v);
                count++;
            }
            return new DoubleSummaryStatistics(count, min, max, sum);
        }

        @Override
        public Stream<Double> boxed() {
            return mapToObj(i -> i);
        }

        // intermediate op impl

        private boolean advance() {
            switch (op) {
                case OP_FILTER -> {
                    while (true) {
                        if (!parentIt.hasNext())
                            return false;
                        Object o = parentIt.next();
                        if (((DoublePredicate) objArg).test((Double) o)) {
                            next = o;
                            return true;
                        }
                    }
                }
                case OP_MAP -> {
                    if (!parentIt.hasNext())
                        return false;
                    Object o = parentIt.next();
                    next = ((Function) objArg).apply(o);
                    return true;
                }
                case OP_FLATMAP -> {
                    while (true) {
                        if (flatMapIterator != null) {
                            if (flatMapIterator.hasNext()) {
                                next = flatMapIterator.next();
                                return true;
                            } else
                                flatMapIterator = null;
                        }
                        if (!parentIt.hasNext())
                            return false;
                        Object o = parentIt.next();
                        flatMapIterator = ((Stream) ((Function) objArg).apply(o)).iterator();
                    }
                }
                case OP_DISTINCT -> {
                    while (true) {
                        if (!parentIt.hasNext())
                            return false;
                        Object o = parentIt.next();
                        if (set.add(o)) {
                            next = o;
                            return true;
                        }
                    }
                }
                // OP_SORTED és OP_SKIP iteratorImpl-ben van implementálva
                case OP_PEEK -> {
                    if (!parentIt.hasNext())
                        return false;
                    Object o = parentIt.next();
                    ((Consumer) objArg).accept(o);
                    next = o;
                    return true;
                }
                case OP_LIMIT -> {
                    if (counter++ > numArg)
                        return false;

                    if (!parentIt.hasNext())
                        return false;
                    next = parentIt.next();
                    return true;
                }
                default -> throw new RuntimeException();
            }
        }

        @Override
        public boolean hasNext() {
            if (iteratorState == 0)
                iteratorState = advance() ? 1 : 2;
            return iteratorState == 1;
        }

        @Override
        public double nextDouble() {
            if (iteratorState == 0)
                iteratorState = advance() ? 1 : 2;
            if (iteratorState == 2)
                throw new NoSuchElementException();
            iteratorState = 0;
            return (Double) next;
        }
    }

    static class IntArrayIterator implements PrimitiveIterator.OfInt {

        private final int[] a;
        private int i;

        public IntArrayIterator(int[] a) {
            this.a = a;
        }

        @Override
        public int nextInt() {
            if (i == a.length)
                throw new NoSuchElementException();
            return a[i++];
        }

        @Override
        public boolean hasNext() {
            // mi van ha a.length == Integer.MAX_VALUE;;
            return i < a.length;
        }
    }

    static class LongArrayIterator implements PrimitiveIterator.OfLong {

        private final long[] a;
        private int i;

        public LongArrayIterator(long[] a) {
            this.a = a;
        }

        @Override
        public long nextLong() {
            if (i == a.length)
                throw new NoSuchElementException();
            return a[i++];
        }

        @Override
        public boolean hasNext() {
            return i < a.length;
        }
    }

    static class DoubleArrayIterator implements PrimitiveIterator.OfDouble {

        private final double[] a;
        private int i;

        public DoubleArrayIterator(double[] a) {
            this.a = a;
        }

        @Override
        public double nextDouble() {
            if (i == a.length)
                throw new NoSuchElementException();
            return a[i++];
        }

        @Override
        public boolean hasNext() {
            // mi van ha a.length == Integer.MAX_VALUE;;
            return i < a.length;
        }
    }
}
