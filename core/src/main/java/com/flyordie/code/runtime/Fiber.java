package com.flyordie.code.runtime;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public final class Fiber {

    public static final boolean ENABLE_MULTITHREADING = false;

    public static final Thread eventLoopThread = Thread.currentThread();
    public static Thread currentThread;

    private static List<Fiber> scheduled = new ArrayList<>();

    private final Thread thread;
    private Deque<Object> suspendStack;

    public Fiber(Thread thread) {
        assert ENABLE_MULTITHREADING;
        this.thread = thread;
    }

    public static void suspend() {
        Fiber fiber = of(currentThread);
        assert fiber.suspendStack == null;
        fiber.suspendStack = new ArrayDeque<>();
        suspendImpl(fiber);
    }

    public static void resume(Thread thread) {
        Fiber fiber = of(thread);
        assert fiber.suspendStack != null;
        if (!scheduled.contains(fiber)) {
            scheduled.add(fiber);
            scheduleContinuation();
        }
    }

    /**
     * @param obj a paraméter nem feltétlen Object, hanem lehet (natív) primitív érték is
     */
    public void push(Object obj) {
        suspendStack.push(obj);
    }

    /**
     * @return ez nem feltétlen Object, hanem lehet (natív) primitív érték is
     */
    public Object pop() {
        return suspendStack.pop();
    }

    public static String generateResumer(Fiber fiber, String function) {
        System.out.println("gen resumer for: ");
        System.out.println(function);
        System.out.println("end");
        return null;
    }

    public static void runNext() {
        assert currentThread == eventLoopThread;
        try {
            Fiber f = scheduled.remove(0);
            currentThread = f.thread;
            continueFiber(f);
        } finally {
            currentThread = eventLoopThread;
        }
    }

    public static Fiber of(Thread thread) {
        Fiber f = getFiber(thread);
        assert f != null;
        return f;
    }

    private static native Fiber getFiber(Thread thread);

    private static native void setFiber(Thread thread);

    private static native void suspendImpl(Fiber fiber);

    private static native void continueFiber(Fiber fiber);

    private static native void scheduleContinuation();
}
