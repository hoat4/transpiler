package com.flyordie.code;

import com.flyordie.code.js.JSReplacementProvider;
import com.flyordie.code.js.JSReplacementProvider.StreamSupportImpl;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class CompTest2 {

    public static void main() {
        System.out.println(Stream.of("a", "b").toList().size());
    }

    public static void main(String[] args) {
        System.out.println(
                StreamSupportImpl.stream(List.of("a", "b").spliterator(), false).toList().size());
    }

    public static void resourceTest() {
        class C {
            static final byte[] b;

            static {
                System.out.println("vacak" + CompTest2.class.getClassLoader());
                try (InputStream s = CompTest2.class.getResourceAsStream("CompTest2.class")) {
                    b = s.readAllBytes();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        System.out.println(C.b.length);
    }

    @SuppressWarnings("unchecked")
    public static void proxyTest() {
        Supplier<Integer> s = (Supplier<Integer>) Proxy.newProxyInstance(CompTest2.class.getClassLoader(),
                new Class[]{Supplier.class},
                (proxy, method, args) -> {
                    System.out.println(method + ", " + Arrays.toString(args));
                    return 34;
                });
        System.out.println(s.get());
    }
}
