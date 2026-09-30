package com.flyordie.code.runtime;

// FinalJSTransformer hívja ezeket
public class ArrayHelper {

    public static native Object allocate(Class<?> arrayType, int size);

    // ha nem reflectionnel (java.util.reflect.Array::newInstance) hívjuk meg,
    // hanem multianewarray-jel, akkor max 256 szint lehet,
    // az pedig elfér JS stackben:
    // https://stackoverflow.com/questions/7826992/browser-javascript-stack-size-limit
    // ha reflectionnel is akarjuk használni, kell csinálni rekurzió nélküli változatot is.
    public static Object allocate(Class<?> arrayType, int[] sizes) {
        return allocateImpl(arrayType, sizes, 0);
    }

    private static Object allocateImpl(Class<?> arrayType, int[] sizes, int sizeOffset) {
        if (sizeOffset == sizes.length - 1)
            return allocate(arrayType, sizes[sizeOffset]);
        else {
            Object[] arr = (Object[]) allocate(arrayType, sizes[sizeOffset]);
            for (int i = 0; i < arr.length; i++)
                arr[i] = allocateImpl(arrayType.getComponentType(), sizes, sizeOffset + 1);
            return arr;
        }
    }
}
