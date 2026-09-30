package com.flyordie.code;

import com.flyordie.code.Type.PrimitiveType;
import com.flyordie.code.util.ScopedValue;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.Assert.assertEquals;

public class InterpreterTest {

    CompilationContext ctx;
    Interpreter interpreter;
    Clazz testClass;

    @Before
    public void init() {
        ctx = new CompilationContext();
        ScopedValue.where(CompilationContext.threadLocal, ctx).run(() -> {
            ctx.initialize();
            interpreter = ctx.interpreter();
            testClass = ctx.findClass(TestClass.class);
        });
    }

    @Test
    public void testConcurrentHashMap() {
        ScopedValue.where(CompilationContext.threadLocal, ctx).run(() -> {
            assertEquals(34, interpreter.execute(ctx.findMethodOrNull(testClass,
                    "chmTest", new MethodType(List.of(), PrimitiveType.I))).orElseThrow());
        });
    }

    private static class TestClass {

        static int chmTest() {
            Map<String, Integer> m = new ConcurrentHashMap<>();
            m.put("qwertz", 34);
            m.put("fdsa", 34);
            m.put("345", 847);
            return m.get("fdsa");
        }
    }
}
