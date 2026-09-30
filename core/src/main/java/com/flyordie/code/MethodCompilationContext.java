package com.flyordie.code;

import java.util.ArrayList;
import java.util.List;

public class MethodCompilationContext {

    public final CompilationContext compilationContext;
    // úgy tűnik (2023-11-25) hogy ez nem feltétlen a replacement. jó lenne ezen változtatni.
    public final Clazz.Method method;
    public final CompilationContext.TransformationChain transformationChain;

    public final List<String> methodNotEmittable = new ArrayList<>();

    public MethodCompilationContext(CompilationContext compilationContext,
                                    Clazz.Method method,
                                    CompilationContext.TransformationChain transformationChain) {
        this.compilationContext = compilationContext;
        this.method = method;
        this.transformationChain = transformationChain;
    }

    public void markAsNonEmittable(Node node, String reason) {
        node.markAsNonEmittable(reason);
        methodNotEmittable.add(reason);
    }
}
