package com.flyordie.code.uitest;

import com.flyordie.code.Clazz.Method;
import com.flyordie.code.CompilationContext;
import com.flyordie.code.EmissionContext;
import com.flyordie.code.MethodType;
import com.flyordie.code.Type.PrimitiveType;
import com.flyordie.code.js.DefaultJSInteropProvider;
import com.flyordie.code.js.JSEmitter;
import com.flyordie.code.util.ScopedValue;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class UITestComp {
    public static void main(String[] args) {
        CompilationContext env = new CompilationContext();
        env.initialize();
        ScopedValue.where(CompilationContext.threadLocal, env).run(() -> {
            try (Writer out = Files.newBufferedWriter(Path.of("build/transpiled.js"))) {
                EmissionContext emissionContext = new EmissionContext(env, new JSEmitter(out,
                        new DefaultJSInteropProvider(), false));
                Method mainMethod = env.findMethodOrNull(env.findClass(UITest.class), "main",
                        new MethodType(List.of(), PrimitiveType.V));
                emissionContext.enqueue(mainMethod, false, List.of());
                emissionContext.run();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
