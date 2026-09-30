package com.flyordie.code.js.teavm_jso_interop;

import com.flyordie.code.AnnotationUtil;
import com.flyordie.code.Clazz;
import com.flyordie.code.Clazz.Method;
import com.flyordie.code.Node;
import com.flyordie.code.Node.NativeSnippetNode;
import com.flyordie.code.Node.NativeSnippetNode.NativeSnippet;
import com.flyordie.code.Node.SequenceNode;
import com.flyordie.code.Node.SequenceNode.MethodKey;
import com.flyordie.code.js.JSEmitter;
import com.flyordie.code.js.JSInteropProvider;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AnnotationNode;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.joining;

public class JSONativeInteropProvider implements JSInteropProvider {

    private static final String JSObject = "org/teavm/jso/JSObject";
    private static final String JSFunctor_DESC = "Lorg/teavm/jso/JSFunctor;";
    private static final String JSBody_DESC = "Lorg/teavm/jso/JSBody;";
    private static final String JSProperty_DESC = "Lorg/teavm/jso/JSProperty;";
    private static final String JSIndexer_DESC = "Lorg/teavm/jso/JSIndexer;";
    private static final String JSMethod_DESC = "Lorg/teavm/jso/JSMethod;";

    @Nullable
    @Override
    public NativeTypeKind nativeTypeKind(Clazz clazz) {
        assert clazz.allAncestorTypesAndThis != null; // tehát hogy be lett-e már töltve teljesen az osztály

        if (clazz.allAncestorTypesAndThis.stream().noneMatch(c -> c.name.equals(JSObject)))
            return null;

        // TODO meg kéne nézni, hogy @Inherited hogy működik
        if (clazz.findAnnotation(JSFunctor_DESC) != null)
            return NativeTypeKind.FUNCTOR;

        if (clazz.allAncestorTypesAndThis.stream().anyMatch(c -> c.findAnnotation(JSFunctor_DESC) != null))
            // functor interface implementációja (tipikusan lambda)
            return null;

        // mivel nincs megadva hogy van-e interfaceobjektum hozzá, ezért feltesszük hogy nincs
        return NativeTypeKind.NO_INTERFACE_OBJECT;
    }

    @Nullable
    @Override
    public String nativeTypeName(Clazz clazz) {
        return null; // mivel soha nem adunk vissza nativeTypeKind-ból HAS_INTERFACE_OBJECT-et
    }

    @Nullable
    @Override
    public NativeMethodKind nativeMethodKind(Method method) {
        if ((method.access & Opcodes.ACC_ABSTRACT) == 0) {
            if (method.findAnnotation(JSMethod_DESC) != null)
                // meg kéne nézni hogy ilyet enged-e TeaVM, és ha igen, mit csinál. dokumentáció hiányos.
                throw new RuntimeException("@JSMethod on non-abstract method: " + method);
            return null;
        }
        if (nativeTypeKind(method.clazz) == null)
            return null;
        else if (method.findAnnotation(JSProperty_DESC) != null)
            return switch (method.type().parameterTypes().size()) {
                case 0 -> NativeMethodKind.GETTER;
                case 1 -> NativeMethodKind.SETTER;
                default -> throw new RuntimeException("unknown getter or setter: " + method);
            };
        else if (method.findAnnotation(JSIndexer_DESC) != null)
            return switch (method.type().parameterTypes().size()) {
                case 1 -> NativeMethodKind.DYNAMIC_GETTER;
                case 2 -> NativeMethodKind.DYNAMIC_SETTER;
                default -> throw new RuntimeException("unknown getter or setter: " + method);
            };
        else
            return NativeMethodKind.REGULAR_METHOD;
        // úgy tűnik hogy nincs külön annotációjuk konstruktorra
    }

    @Nullable
    @Override
    public String nativeMethodName(Method m) {
        NativeMethodKind k = nativeMethodKind(m);
        return switch (k) {
            case REGULAR_METHOD -> {
                AnnotationNode ann = m.findAnnotation(JSMethod_DESC);
                String name = (String) AnnotationUtil.findValue(ann);
                if (name == null || name.isEmpty())
                    yield m.name;
                else
                    yield name;
            }
            case GETTER, SETTER -> {
                AnnotationNode ann = m.findAnnotation(JSProperty_DESC);
                String name = (String) AnnotationUtil.findValue(ann);
                if (name == null || name.isEmpty())
                    if (m.name.startsWith("get") && k == NativeMethodKind.GETTER ||
                            m.name.startsWith("set") && k == NativeMethodKind.SETTER) {
                        int firstChar = m.name.codePointAt(3);
                        // TeaVM valószínűleg nem foglalkozik surrogate paroikkal. ha valóban nem, itt is szedjük ki.
                        yield new String(new int[]{Character.toLowerCase(firstChar)}, 0, 1) +
                                m.name.substring(3 + Character.charCount(firstChar));
                    } else
                        throw new RuntimeException("unknown @JSProperty: " + m);
                else
                    yield name;
            }
            case DYNAMIC_GETTER, DYNAMIC_SETTER -> throw new RuntimeException("TODO"); // ilyenkor mit kéne visszaadni
            case CONSTRUCTOR -> throw new RuntimeException("should not reach here");
            case null -> null;
        };
    }

    @Override
    public Node nativeMethodBody(Method m, List<Node> args, JSEmitter emitter) {
        NativeSnippet ns = makeNativeSnippet(m);
        if (ns == null)
            return null;
        if (args.isEmpty())
            return new NativeSnippetNode(ns, m.type().returnType(), args);
        else {
            if (args.size() != m.fullArgTypes().size())
                throw new RuntimeException("wrong arg count for " + m + ": " + args.size());

            SequenceNode seq = new SequenceNode(new MethodKey("downcall arg converts for " + m));
            args = new ArrayList<>(args);
            for (int i = 0; i < args.size(); i++)
                args.set(i, emitter.downcallArgConvert(seq, m.fullArgTypes().get(i), args.get(i)));
            NativeSnippetNode nsn = new NativeSnippetNode(ns, m.type().returnType(), args);
            seq.nodes.add(nsn);
            seq.resultSlot.set(nsn);
            return seq;
        }
    }

    @Nullable
    private static NativeSnippet makeNativeSnippet(Method m) {
        AnnotationNode ann = m.findAnnotation(JSBody_DESC);
        if (ann == null)
            return null;
        if (AnnotationUtil.findValue(ann, "imports") != null)
            throw new RuntimeException("TODO");
        List<?> params = (List<?>) AnnotationUtil.findValueOrDefault(ann, "params", emptyList());
        String script = (String) Objects.requireNonNull(AnnotationUtil.findValue(ann, "script"));

        if (params.size() != m.type().parameterTypes().size())
            throw new RuntimeException("wrong params array: " + params);

        StringBuilder sb = new StringBuilder();
        sb.append("(function(");
        boolean first = true;
        for (Object paramName : params) {
            if (first)
                first = false;
            else
                sb.append(", ");
            sb.append(paramName);
        }
        sb.append(") { ").append(script).append(" })(");
        if (params.isEmpty())
            sb.append(")");
        String wrapperSnippet = sb.toString();

        return args -> {
            if (args.size() != params.size())
                throw new RuntimeException();

            if (args.isEmpty())
                return wrapperSnippet;
            else
                return args.stream().collect(joining(", ", wrapperSnippet, ")"));
        };
    }

    @Override
    public Boolean isFunctor(Clazz clazz) {
        NativeTypeKind k = nativeTypeKind(clazz);
        return k == null ? null : k == NativeTypeKind.FUNCTOR;
    }
}
