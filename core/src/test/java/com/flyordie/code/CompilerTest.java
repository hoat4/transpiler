package com.flyordie.code;

import com.flyordie.code.Clazz.Method;
import com.flyordie.code.Type.PrimitiveType;
import ui11.Element;
import ui11.Node;
import ui11.Widget;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class CompilerTest {
    @Test
    public void testLubVoid() {
        assertEquals(PrimitiveType.V, new CompilationContext().lub(PrimitiveType.V, null));
    }

    @Test
    public void testMethodResolution_Element_build() {
        // "if mA is declared in a class A and mC is declared in a class C,
        // then there exists a method mB declared in a class B such that C
        // is a subclass of B and B is a subclass of A and mC can override
        // mB and mB can override mA. "

        // TODO nem ui Elementet kéne használni itt a teszthez, hanem csinálni egy rendes osztályhierarchiát

        class Vacak extends Element {
            @Override
            protected Widget build() {
                throw new RuntimeException();
            }
        }
        CompilationContext ctx = new CompilationContext();
        Clazz Vacak = ctx.findClass(Vacak.class);
        Clazz Element = ctx.findClass(Node.class);
        Method Vacak_build = ctx.findMethodExcludingOverrides(Vacak, "build",
                new MethodType(List.of(), Element));
        Method Element_build = ctx.findMethodExcludingOverrides(Element, "build",
                new MethodType(List.of(), Element));
        assertEquals(
                Vacak_build,
                ctx.resolveVirtualMethodOrFail(Element_build, Vacak)
        );
    }

    @Test
    public void testMethodResolution_ObjectMethodOverridesInterfaceMethod() {
        interface I {
            String toString();
        }
        CompilationContext ctx = new CompilationContext();
        Clazz I = ctx.findClass(I.class);
        Clazz Object = ctx.findClass(Object.class);
        Clazz String = ctx.findClass(String.class);
        Method I_toString = ctx.findMethodExcludingOverrides(I, "toString",
                new MethodType(List.of(), String));
        Method Object_toString = ctx.findMethodExcludingOverrides(Object, "toString",
                new MethodType(List.of(), String));
        assertEquals(
                Object_toString,
                ctx.resolveVirtualMethodOrFail(I_toString, Object)
        );
    }
}
