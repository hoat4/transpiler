package com.flyordie.code.js;

import com.flyordie.code.Clazz;
import com.flyordie.code.Clazz.Method;
import com.flyordie.code.Node;
import com.flyordie.code.Node.NativeSnippetNode.NativeSnippet;

import javax.annotation.Nullable;
import java.util.List;

public class CompositeJSInteropProvider implements JSInteropProvider {

    private final List<JSInteropProvider> providers;

    public CompositeJSInteropProvider(List<JSInteropProvider> providers) {
        this.providers = providers;
    }

    @Nullable
    @Override
    public NativeTypeKind nativeTypeKind(Clazz clazz) {
        for (JSInteropProvider p : providers) {
            NativeTypeKind k = p.nativeTypeKind(clazz);
            if (k != null)
                return k;
        }
        return null;
    }

    @Nullable
    @Override
    public String nativeTypeName(Clazz clazz) {
        for (JSInteropProvider p : providers) {
            NativeTypeKind k = p.nativeTypeKind(clazz);
            if (k != null)
                return p.nativeTypeName(clazz);
        }
        return null;
    }

    @Nullable
    @Override
    public NativeMethodKind nativeMethodKind(Method method) {
        for (JSInteropProvider p : providers) {
            NativeMethodKind k = p.nativeMethodKind(method);
            if (k != null)
                return k;
        }
        return null;
    }

    @Nullable
    @Override
    public String nativeMethodName(Method m) {
        for (JSInteropProvider p : providers) {
            NativeMethodKind k = p.nativeMethodKind(m);
            if (k != null)
                return p.nativeMethodName(m);
        }
        return null;
    }

    @Override
    public Node nativeMethodBody(Method m, List<Node> args, JSEmitter emitter) {
        for (JSInteropProvider p : providers) {
            Node n = p.nativeMethodBody(m, args, emitter);
            if (n != null)
                return n;
        }
        return null;
    }

    @Override
    @Nullable
    public Boolean isFunctor(Clazz clazz) {
        for (JSInteropProvider p : providers) {
            Boolean b = p.isFunctor(clazz);
            if (b != null)
                return b;
        }
        return null;
    }
}
