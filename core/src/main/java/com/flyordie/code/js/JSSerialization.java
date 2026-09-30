package com.flyordie.code.js;

import com.flyordie.code.js.JSCodeMap.JSClassMap;
import com.flyordie.code.util.UTF8Writer;
import ui11.reflectutil.ReflectionUtil;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.ConstantDynamic;
import org.objectweb.asm.Handle;
import org.objectweb.asm.MethodVisitor;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.nio.charset.StandardCharsets;

import static java.lang.invoke.MethodHandles.lookup;
import static java.lang.invoke.MethodType.methodType;
import static org.objectweb.asm.Opcodes.*;

public class JSSerialization {

    static final byte[] NULL = "null".getBytes(StandardCharsets.US_ASCII);

    private static final String JSSerialization = JSSerialization.class.getName().replace('.', '/');
    private static final String UTF8Writer = UTF8Writer.class.getName().replace('.', '/');
    private static final String JSCodeMap = JSCodeMap.class.getName().replace('.', '/');

    public static void serialize(Object object, JSCodeMap codeMap, UTF8Writer out) {
        if (object == null) {
            out.writeBytes(NULL);
            return;
        }

        JSClassMap clazz = codeMap.clazz(object.getClass().getName());
        if (clazz.serializer == null)
            clazz.serializer = generateSerializer(clazz, codeMap);
        serializeImpl(clazz.serializer, object, out);
    }

    @SuppressWarnings("unchecked")
    private static <T> void serializeImpl(Serializer<?> serializer,
                                          T object, UTF8Writer out) {
        ((Serializer<T>) serializer).serialize(object, out);
    }

    private static Serializer<?> generateSerializer(JSClassMap clazz, JSCodeMap codeMap) {
        if (clazz.javaClass.descriptorString().equals("Ljava/lang/Class;"))
            return (Serializer<String>)
                    com.flyordie.code.js.JSSerialization::writeJSString;

        String className = ReflectionUtil.internalName(clazz.javaClass);

        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
        cw.visit(V19, ACC_PUBLIC, JSSerialization
                        + "$$GeneratedSerializer$" + className.replace('/', '_'),
                null, "java/lang/Object", new String[]{Serializer.class.getName().replace('.', '/')});

        MethodVisitor constructor = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
        constructor.visitVarInsn(ALOAD, 0);
        constructor.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
        constructor.visitInsn(RETURN);

        MethodVisitor m = cw.visitMethod(ACC_PUBLIC, "serialize",
                methodType(void.class, Object.class, UTF8Writer.class).descriptorString(), null, null);

        m.visitVarInsn(ALOAD, 2);
        m.visitLdcInsn(byteArrayConstant("{t:" + clazz.name + ",hc:"));
        callWriteBytes(m);

        m.visitVarInsn(ALOAD, 2);
        m.visitVarInsn(ALOAD, 1);
        m.visitMethodInsn(INVOKESTATIC, "java/lang/System", "identityHashCode", "(Ljava/lang/Object;)I", false);
        m.visitMethodInsn(INVOKEVIRTUAL, UTF8Writer, "writeInt", "(I)V", false);

        m.visitVarInsn(ALOAD, 1);
        m.visitTypeInsn(CHECKCAST, className);
        m.visitVarInsn(ALOAD, 3);

        clazz.fieldNames.forEach((nat, jsName) -> {
            m.visitVarInsn(ALOAD, 2);
            m.visitLdcInsn(byteArrayConstant("," + jsName + ":"));
            callWriteBytes(m);

            String fieldDesc = nat.type().descriptorString();
            if (fieldDesc.charAt(0) == 'L') {
                m.visitVarInsn(ALOAD, 3);
                m.visitFieldInsn(GETFIELD, className, nat.name(), fieldDesc);
                m.visitLdcInsn(new ConstantDynamic("_", "L" + JSCodeMap + ";",
                        new Handle(H_INVOKESTATIC, "java/lang/invoke/MethodHandles",
                                "classData",
                                "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/Class;)" +
                                        "Ljava/lang/Object;", false)));
                m.visitMethodInsn(INVOKESTATIC, JSSerialization, "serializaze",
                        "(Ljava/lang/Object;L" + JSCodeMap + ";L" + UTF8Writer + ";)V", false);
            } else {
                m.visitVarInsn(ALOAD, 2);
                m.visitVarInsn(ALOAD, 3);
                m.visitFieldInsn(GETFIELD, className, nat.name(), fieldDesc);
                if (fieldDesc.equals("I")) {
                    m.visitMethodInsn(INVOKEVIRTUAL, UTF8Writer, "writeInt", "(I)V", false);
                } else {
                    m.visitMethodInsn(INVOKESTATIC, "java/lang/String",
                            "valueOf", "(" + fieldDesc + ")Ljava/lang/String;", false);
                }
            }
        });

        m.visitIntInsn(SIPUSH, '}');
        m.visitMethodInsn(INVOKEVIRTUAL, UTF8Writer,
                "writeBytes", "([B)V", false);

        try {
            Lookup lookup = lookup();
            Class<?> c = lookup.defineHiddenClassWithClassData(cw.toByteArray(),
                    codeMap, true).lookupClass();
            MethodHandle mh = lookup.findConstructor(c, methodType(void.class)).
                    asType(methodType(Serializer.class));
            return (Serializer) mh.invokeExact();
        } catch (RuntimeException | Error e) {
            throw e;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    private static ConstantDynamic byteArrayConstant(String s) {
        return new ConstantDynamic("byteArrayConstant", "Ljava/lang/String;",
                new Handle(H_INVOKESTATIC, JSSerialization, "byteArrayBSM",
                        "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/Class;" +
                                "Ljava/lang/String;)[B", false), s);
    }

    private static void callWriteBytes(MethodVisitor m) {
        m.visitMethodInsn(INVOKEVIRTUAL, UTF8Writer,
                "writeBytes", "([B)V", false);
    }

    static byte[] byteArrayBSM(MethodHandles.Lookup lookup, String name, Class<?> type, String value) {
        return value.getBytes(StandardCharsets.UTF_8);
    }

    public static void writeJSString(String s, UTF8Writer out) {
        out.writeByte('"');

        int size = s.length();
        int i = 0;

        while (i < size) {
            char ch = s.charAt(i++);

            switch (ch) {
                case '"' -> out.writeBytes('\\', '"');
                case '\'' -> out.writeBytes('\\', '\'');
                case '\\' -> out.writeBytes('\\', '\\');
                case '\b' -> out.writeBytes('\\', 'b');
                case '\t' -> out.writeBytes('\\', 't');
                case '\n' -> out.writeBytes('\\', 'n');
                case '\u000B' -> out.writeBytes('\\', 'v');
                case '\f' -> out.writeBytes('\\', 'f');
                case '\r' -> out.writeBytes('\\', 'r');
                case '\u2028' -> out.writeBytes(U2028);
                case '\u2029' -> out.writeBytes(U2029);
                default -> out.write(ch);
            }
        }

        out.writeByte('"');
    }

    private static final byte[] U2028 = "\\u2028".getBytes(StandardCharsets.US_ASCII);
    private static final byte[] U2029 = "\\u2029".getBytes(StandardCharsets.US_ASCII);

    interface Serializer<T> {

        void serialize(T obj, UTF8Writer out);
    }
}
