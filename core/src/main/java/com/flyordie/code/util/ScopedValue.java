package com.flyordie.code.util;


import java.util.NoSuchElementException;
import java.util.function.Supplier;

// https://openjdk.org/jeps/8263012
public class ScopedValue<T> {

    private final ThreadLocal<T> threadLocal = new ThreadLocal<>();

    public static <T> Carrier<T> where(ScopedValue<T> key, T value) {
        return new Carrier<>(key, value);
    }

    public T get() {
        T value = threadLocal.get();
        if (value == null)
            throw new NoSuchElementException();
        return value;
    }

    public T orElse(T other) {
        T value = threadLocal.get();
        if (value == null)
            return other;
        else
            return value;
    }

    public boolean isBound() {
        return threadLocal.get() != null; // ez nem pontos, de egyelőre jó lesz
    }

    public static final class Carrier<T> {

        private final ScopedValue<T> key;
        private final T value;

        private Carrier(ScopedValue<T> key, T value) {
            this.key = key;
            this.value = value;
        }

        public void run(Runnable runnable) {
            T prev = key.threadLocal.get();
            key.threadLocal.set(value);
            try {
                runnable.run();
            } finally {
                if (prev == null)
                    key.threadLocal.remove();
                else
                    key.threadLocal.set(prev);
            }
        }

        // TODO m @Inline
        public <R> R execute(Supplier<R> supplier) {
            T prev = key.threadLocal.get();
            key.threadLocal.set(value);
            try {
                return supplier.get();
            } finally {
                if (prev == null)
                    key.threadLocal.remove();
                else
                    key.threadLocal.set(prev);
            }
        }
    }
}
