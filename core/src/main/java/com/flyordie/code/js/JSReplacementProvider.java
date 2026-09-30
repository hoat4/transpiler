package com.flyordie.code.js;

import com.flyordie.code.*;
import com.flyordie.code.Clazz.Field;
import com.flyordie.code.Clazz.Method;
import com.flyordie.code.Node;
import com.flyordie.code.Interpreter.ClassObj;
import com.flyordie.code.annotation.DontInline;
import com.flyordie.code.annotation.EvaluateCompileType;
import com.flyordie.code.annotation.Inline;
import com.flyordie.code.annotation.MustBeInlined;
import com.flyordie.code.browserapi.DOM.Uint8Array;
import com.flyordie.code.browserapi.Events.MessageEvent;
import com.flyordie.code.browserapi.FrameAPI;
import com.flyordie.code.browserapi.FrameAPI.Console;
import com.flyordie.code.browserapi.FrameAPI.Window;
import com.flyordie.code.browserapi.FrameAPI.WindowTimers.TimeoutHandler;
import com.flyordie.code.browserapi.Globals;
import com.flyordie.code.browserapi.HTML.HTMLAnchorElement;
import com.flyordie.code.browserapi.WebSockets;
import com.flyordie.code.browserapi.WebSockets.CloseEvent;
import com.flyordie.code.js.JSValue.JSArray;
import com.flyordie.code.js.JSValue.JSObject;
import com.flyordie.code.runtime.ArrayHelper;
import com.flyordie.code.runtime.Fiber;
import com.flyordie.code.runtime.StreamImpl;
import com.flyordie.code.runtime.StreamImpl.DoubleStreamImpl;
import com.flyordie.code.runtime.StreamImpl.IntStreamImpl;
import com.flyordie.code.runtime.StreamImpl.LongStreamImpl;
import com.flyordie.code.runtime.StreamImpl.ReferenceStream;
import com.flyordie.code.runtime.Unsafe2;
/*
import com.flyordie.configbinder.type.structure.PropertyDefinition;
import com.flyordie.http.WebSocketWrapper;
import com.flyordie.http.WebSocketWrapper.WebSocketListener;
import com.flyordie.http.WebSocketWrapper.WebSocketListener.CloseStatus;
import com.flyordie.http.WebSocketWrapper.WebSocketListener.ErrorCloseStatus;
import com.flyordie.http.WebSocketWrapper.WebSocketListener.GracefulCloseStatus;
 */
import org.slf4j.ILoggerFactory;
import org.slf4j.IMarkerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.BasicMDCAdapter;
import org.slf4j.helpers.BasicMarkerFactory;
import org.slf4j.helpers.MarkerIgnoringBase;
import org.slf4j.helpers.MessageFormatter;
import org.slf4j.spi.MDCAdapter;
import org.slf4j.spi.SLF4JServiceProvider;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import java.awt.datatransfer.DataFlavor;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.PrintStream;
import java.lang.annotation.Annotation;
import java.lang.constant.ConstantDesc;
import java.lang.constant.MethodHandleDesc;
import java.lang.invoke.CallSite;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.module.ModuleReference;
import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.lang.reflect.Array;
import java.lang.reflect.Executable;
import java.lang.reflect.Modifier;
import java.lang.reflect.TypeVariable;
import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandler;
import java.net.http.HttpResponse.PushPromiseHandler;
import java.net.http.WebSocket;
import java.net.http.WebSocket.Builder;
import java.net.http.WebSocket.Listener;
import java.nio.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.PermissionCollection;
import java.security.ProtectionDomain;
import java.text.DateFormatSymbols;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.*;
import java.util.ServiceLoader.Provider;
import java.util.Spliterator.OfDouble;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.*;

import static com.flyordie.code.Type.PrimitiveType.Z;
import static java.util.Collections.singletonList;

public class JSReplacementProvider extends AbstractReplacementProvider {

    public static final String Undefined = JSValue.Undefined.class.getName().replace('.', '/');
    public static final String JSObject = JSValue.JSObject.class.getName().replace('.', '/');
    public static final String JSObjectImpl = JSObjectImpl.class.getName().replace('.', '/');
    public static final String ThreadLocal = ThreadLocal.class.getName().replace('.', '/');
    public static final String ThreadLocalImpl = ThreadLocalImpl.class.getName().replace('.', '/');

    // nem sebesség miatt kell cache-elés, hanem hogy ugyanazon konstans objektumból ne csináljunk több JS objektumot
    // (ha mutable (pl. HashMap), akkor helytelen működéshez is vezet ha ugyanahhoz eltérő replacementet adunk)
    // bár most redundáns, mert JSConstantPoolban is van egy ilyen logika. csak az most azért nem használható,
    // mert a replacementek ott a cache keyei.
    private final Map<Object, Object> constantReplacementCache = new HashMap<>();
    private final JSEmitter emitter;

    public JSReplacementProvider(CompilationContext ctx, EmissionContext emissionContext,
                                 JSEmitter emitter) {
        super(ctx, emissionContext);
        this.emitter = emitter;
    }

    @Override
    public boolean isReplacedInConstants(Clazz clazz) {
        return clazz.name.equals("java/util/ArrayList") ||
                clazz.name.equals("java/util/Arrays$ArrayList") ||
                clazz.name.equals("java/util/Collections$EmptyList") ||
                ctx.findClass("java/util/ImmutableCollections$AbstractImmutableList").isAssignableFrom(clazz);
    }


    @Override
    public Object constantReplacement(Object constant) {
        Object o = constantReplacementCache.get(constant);
        if (o != null)
            return o;

        if (constant instanceof ClassObj co) {
            if (co.type().knownClass == KnownClass.Locale) {
                Clazz LocaleImpl = ctx.findClass(LocaleImpl.class);
                Clazz String = ctx.findClass(KnownClass.STRING);
                // hashCode elromlik meg, ha voltak benne extensionök vagy script
                switch ((int) ctx.interpreter().execute(
                        ctx.method(KnownClass.Locale, "hasExtensions", Z), co).orElseThrow()) {
                    case 0 -> {
                    }
                    case 1 -> throw new RuntimeException("locale has extensions");
                    default -> throw new RuntimeException("unknown Locale.hasExtensions return value");
                }
                Object script = ctx.interpreter().execute(
                        ctx.method(KnownClass.Locale, "getScript", String), co).orElseThrow();
                if (!ctx.interpreter().fromStringObj((ClassObj) script).isEmpty())
                    throw new RuntimeException("locale has script");
                Object country = ctx.interpreter().execute(
                        ctx.method(KnownClass.Locale, "getCountry", String), co).orElseThrow();
                Object language = ctx.interpreter().execute(
                        ctx.method(KnownClass.Locale, "getLanguage", String), co).orElseThrow();
                Object variant = ctx.interpreter().execute(
                        ctx.method(KnownClass.Locale, "getVariant", String), co).orElseThrow();
                ClassObj newLocale = ctx.interpreter().createObject(LocaleImpl);
                newLocale.writeField(ctx.field(LocaleImpl, "country", String), country);
                newLocale.writeField(ctx.field(LocaleImpl, "language", String), language);
                newLocale.writeField(ctx.field(LocaleImpl, "variant", String), variant);

                constantReplacementCache.put(constant, newLocale);
                return newLocale;
            }

            if (co.type().knownClass == KnownClass.HashMap ||
                    co.type().knownClass == KnownClass.LinkedHashMap ||
                    co.type().knownClass == KnownClass.ConcurrentHashMap) {
                final MethodType mt = new MethodType(
                        List.of(ctx.findClass(KnownClass.Map)), ctx.interpreter().symbols.Object);
                Method exportMap = ctx.findMethodOrFail(JSReplacementProvider.class, "exportMap_" +
                        co.type().simpleName(), mt);
                Object jsMap = ctx.interpreter().execute(exportMap, co).orElseThrow();
                constantReplacementCache.put(constant, jsMap);
                return jsMap;
            }

            // maradék JSEmitter.constValue0-ban van egyelőre átalakítva. majd
            // át kéne hozni mindegyiket ide.
        }

        /*
        if (constant instanceof ClassObj co && ctx.findClass(ThreadLocal.class).isAssignableFrom(co.type())) {
            return constantReplacements.computeIfAbsent(co, __ -> {
                Clazz ThreadLocalImpl = ctx.findClass(ThreadLocalImpl.class);
                ClassObj co2 = ctx.interpreter().createObject(ThreadLocalImpl);
                co2.writeField(0, ctx.interpreter().fromString("TL_CONST_" + constantReplacements.size()));
                return co2;
            });
        }
         */
        return super.constantReplacement(constant);
    }

    @Override
    public boolean treatStaticFinalAsConstant(Field field) {
        if (field.clazz.name.equals(JSReplacementProvider.Undefined) && field.name.equals("UNDEFINED"))
            return false;
        return true;
    }

    @Override
    protected Node nativeMethodBody(Clazz.Method m, List<Node> args) {
        Node ns;
        if ((ns = super.nativeMethodBody(m, args)) != null)
            return ns;
        if ((ns = emitter.interopProvider.nativeMethodBody(m, args, emitter)) != null)
            return ns;
        return null;
    }

    @Override
    public boolean shouldNotSerializeInstanceField(Field f) {
        if (f.clazz.knownClass == KnownClass.MEMBER_NAME && f.name.equals("clazz"))
            // sokszor j.l.i.LambdaForm$MH-ra mutat, amiből kb. 23 osztálydeklaráció van azonos néven,
            // ráadásul mivel JSEmitter inline functiont generál MN helyett, nem is használjuk semmire.
            // ezért teljesen felesleges belerakni a kódba a MN.clazz által hivatkozott osztályokat.
            return true;
        return false;
    }

    // constantReplacementből vannak hívva
    private static <K, V> Object exportMap_HashMap(Map<K, V> map) {
        return exportMapImpl(map, HashMapImpl_HashMap::new);
    }

    private static <K, V> Object exportMap_LinkedHashMap(Map<K, V> map) {
        return exportMapImpl(map, HashMapImpl_LinkedHashMap::new);
    }

    private static <K, V> Object exportMap_ConcurrentHashMap(Map<K, V> map) {
        return exportMapImpl(map, HashMapImpl_ConcurrentHashMap::new);
    }

    @SuppressWarnings("unused")
    private static <K, V> Object exportMapImpl(Map<K, V> map,
                                               Supplier<? extends HashMapImpl_Common<K, V>> mapImplSupplier) {
        int size = map.size();

        Object[] m2keys = new Object[size];
        Object[] m2values = new Object[size];

        Map<Integer, List<Object>> byHash = new HashMap<>();

        NativeMapData m2 = new NativeMapData();
        m2.keys = m2keys;
        m2.values = m2values;
        int i = 0;
        for (Map.Entry<K, V> entry : map.entrySet()) {
            K k = entry.getKey();
            V v = entry.getValue();

            List<Object> bucket = byHash.computeIfAbsent(Objects.hashCode(k), __ -> new ArrayList<>());
            bucket.add(k);
            bucket.add(v);

            m2keys[i] = k;
            m2values[i] = v;
            i++;
        }
        assert i == size;

        int[] m1keys = new int[byHash.size()];
        Object[] m1values = new Object[byHash.size()];
        NativeMapData m1 = new NativeMapData();
        m1.keys = m1keys;
        m1.values = m1values;
        i = 0;
        for (Map.Entry<Integer, List<Object>> entry : byHash.entrySet()) {
            m1keys[i] = entry.getKey();
            m1values[i] = entry.getValue().toArray();
            i++;
        }
        assert i == byHash.size();

        HashMapImpl_Common<K, V> hm = mapImplSupplier.get();
        hm.kind = HashMapImpl_Common.KIND_REGULAR;
        hm.size = map.size();
        hm.nativeMap = m1;
        hm.order = m2;
        return hm;
    }

    static class NativeMapData {
        Object keys; // tetszőleges típusú tömb
        Object[] values;
    }

    @For(value = Object.class)
    private static class ObjectImpl {

        @Override
        public ObjectImpl clone() throws CloneNotSupportedException {
            if (this instanceof Cloneable)
                return cloneImpl();
            else if (getClass().isArray())
                return cloneArray();
            else
                throw new CloneNotSupportedException();
        }

        @Snippet("cloneImpl($0)")
        @Ignore
        private native ObjectImpl cloneImpl();

        @Snippet("cloneArray($0)")
        @Ignore
        private native ObjectImpl cloneArray();

        @Override
        public int hashCode() {
            return System.identityHashCode(this);
        }
    }

    // devirtualizálhatóság miatt final
    @For(value = String.class, replace = true, nativeType = "String")
    public static final class StringImpl {

        static final boolean COMPACT_STRINGS = true;

        @FactoryReplacementForConstructor
        static String create(byte[] value, byte coder) {
            String s = "";
            if (coder == 0) {
                for (byte b : value) {
                    s = s.concat(String.valueOf((char) b));
                }
            } else {
                for (int i = 0; i < value.length; i += 2) {
                    char ch = (char) ((value[i] & 0xFF) << 8 | (value[i + 1] & 0xFF));
                    s = s.concat(String.valueOf(ch));
                }
            }
            return s;
        }

        @FactoryReplacementForConstructor
        static String create(char[] value) {
            String s = "";
            for (char c : value)
                s = s.concat(String.valueOf(c));
            return s;
        }

        @FactoryReplacementForConstructor
        static String create(int[] codePoints, int offset, int count) {
            String s = "";
            for (int i = offset; i < offset + count; i++)
                s = s.concat(fromCodePoint(codePoints[i]));
            return s;
        }

        @FactoryReplacementForConstructor
        static String create(char[] chars, int offset, int count) {
            String s = "";
            for (int i = offset; i < offset + count; i++)
                s = s.concat(String.valueOf(chars[i]));
            return s;
        }

        @FactoryReplacementForConstructor
        static String create(byte[] bytes) {
            return create(bytes, 0, bytes.length);
        }

        @FactoryReplacementForConstructor
        @Snippet("utf8dec($0, $1, $2)")
        static native String create(byte[] src, int offset, int length);

        @FactoryReplacementForConstructor
        static String create(byte[] src, Charset charset) {
            return create(src, 0, src.length); // TODO
        }

        @FactoryReplacementForConstructor
        static String create(byte[] src, int offset, int length, Charset charset) {
            return create(src, offset, length); // TODO
        }

        @FactoryReplacementForConstructor
        @Snippet("new String($0)")
        static native String create(String s);

        @Snippet("String.fromCodePoint($0)")
        @Ignore
        static native String fromCodePoint(int cp);

        @Snippet("$0.length")
        native int length();

        boolean isEmpty() {
            return length() == 0;
        }

        @Override
        public String toString() {
            return Unsafe2.reinterpretCast(this, String.class);
        }

        @Snippet("($0+$1)")
        @Inline
        native StringImpl concat(StringImpl s);

        @Snippet("(''+$0)")
        static native StringImpl valueOf(float i);

        @Snippet("(''+$0)")
        static native StringImpl valueOf(double i);

        @Snippet("(''+$0)")
        static native StringImpl valueOf(int i);

        static String valueOf(boolean b) {
            return b ? "true" : "false";
        }

        static String valueOf(long l) {
            return Long.toString(l);
        }

        @Snippet("$0.indexOf($1)")
        native int indexOf(String s);

        @Snippet("$0.indexOf($1, $2)")
        native int indexOf(String s, int from);

        @Snippet("$0.indexOf(String.fromCharCode($1))")
        native int indexOf(int ch); // TODO mi van, ha érvénytelen a megadott char code?

        @Snippet("$0.indexOf(String.fromCharCode($1), $2)")
        native int indexOf(int ch, int fromIndex);

        @Snippet("$0.lastIndexOf($1)")
        native int lastIndexOf(StringImpl s);

        @Snippet("$0.lastIndexOf(String.fromCharCode($1))")
        native int lastIndexOf(int ch); // TODO mi van, ha érvénytelen a megadott char code?

        @Snippet("$0.lastIndexOf(String.fromCharCode($1), $2)")
        native int lastIndexOf(int ch, int fromIndex);

        @Snippet("$0.charCodeAt($1)")
        native char charAt(int i);

        public boolean contains(CharSequence cs) {
            return indexOf(cs.toString()) >= 0;
        }

        public boolean regionMatches(int toffset, String other, int ooffset, int len) {
            return regionMatches(false, toffset, other, ooffset, len);
        }

        @SuppressWarnings("EqualsBetweenInconvertibleTypes")
        public boolean regionMatches(boolean ignoreCase, int toffset, String other, int ooffset, int len) {
            if (toffset < 0 || ooffset < 0 || toffset + len > length() || ooffset + len > other.length())
                return false;

            StringImpl a = substring(toffset, toffset + len);
            String b = other.substring(ooffset, ooffset + len);
            if (ignoreCase)
                return a.equalsIgnoreCase(b);
            else
                return a.equals(b);
        }

        @Snippet("!!($0.match(new RegExp('^'+$1+'$')))")
        public native boolean matches(String s);

        int compareTo(String b) {
            for (int i = 0; i < Math.min(length(), b.length()); i++)
                if (charAt(i) < b.charAt(i))
                    return -1;
                else if (charAt(i) > b.charAt(i))
                    return 1;
            return Integer.compare(length(), b.length());
        }

        @Snippet("$0.substring($1)")
        native StringImpl substring(int i);

        @Snippet("$0.substring($1, $2)")
        native StringImpl substring(int i, int j);

        @Snippet("$0.substring($1, $2)")
        native CharSequence subSequence(int i, int j);

        @Snippet("String.fromCharCode($0)")
        native static StringImpl valueOf(char ch);

        String toLowerCase() {
            return toLowerCase_noLocales();  // TODO locale-ek
        }

        String toUpperCase() {
            return toUpperCase_noLocales();  // TODO locale-ek
        }

        // a Locale-eshez kéne Locale::toLanguageTag, de annak meg kell String.toUpperCase,
        // ezért nem tud toUpperCase(Locale.ROOT)-ot hívni
        @Snippet("$0.toLowerCase()")
        @Ignore
        native String toLowerCase_noLocales();

        @Snippet("$0.toUpperCase()")
        @Ignore
        native String toUpperCase_noLocales();

        public String toLowerCase(Locale locale) {
            if (locale == Locale.ROOT)
                return toLowerCase();

            return toLowerCaseImpl(locale);
        }

        @Ignore
        private String toLowerCaseImpl(Locale locale) {
            Objects.requireNonNull(locale);
            return toLowerCaseImpl2(locale.toLanguageTag());
        }

        public String toUpperCase(Locale locale) {
            if (locale == Locale.ROOT)
                return toUpperCase();

            return toUpperCaseImpl(locale);
        }

        @Ignore
        private String toUpperCaseImpl(Locale locale) {
            Objects.requireNonNull(locale);
            return toUpperCaseImpl2(locale.toLanguageTag());
        }

        @Snippet("$0.toLocaleLowerCase($1)")
        @Ignore
        native String toLowerCaseImpl2(String localeString);

        @Snippet("$0.toLocaleUpperCase($1)")
        @Ignore
        native String toUpperCaseImpl2(String localeString);

        @Override
        public int hashCode() {
            int h = 0;
            for (int i = 0; i < length(); i++)
                h = 31 * h + charAt(i);
            return h;
        }

        @Override
        @Snippet("($0==$1)")
        public native boolean equals(Object o);

        @Snippet("($0.toUpperCase('')==$1.toUpperCase(''))")
        public native boolean equalsIgnoreCase(String s);

        public StringImpl replace(CharSequence a, CharSequence b) {
            // egyelőre nincs CharSequence.toString
            return replaceImpl(a.toString(), b.toString());
        }

        @Snippet("strReplaceAll($0, $1, $2)")
        @Ignore
        private native StringImpl replaceImpl(String a, String b);

        StringImpl replaceFirst(String regex, String replacement) {
            Objects.requireNonNull(regex);
            Objects.requireNonNull(replacement);
            return replaceFirstImpl(regex, replacement);
        }

        @Snippet("$0.replace(new RegExp($1),$2)")
        @Ignore
        private native StringImpl replaceFirstImpl(String regex, String replacement);

        @Snippet("strReplaceAll($0,new RegExp($1,'g'),$2)")
        public native StringImpl replaceAll(StringImpl regex, StringImpl replacement);

        static String format(String fmt, Object... args) {
            // return new Formatter().format(fmt, args).toString();
            // kéne neki Locale, ezért nem működik

            for (int i = 0; i < args.length; i++)
                fmt = fmt.replaceFirst("%s", String.valueOf(args[i]));
            // TODO ellenőrizni kéne, hogy nincs-e benne más formázóbigyó %s-en kívül
            return fmt;
        }

        static String valueOf(Object obj) {
            return obj == null ? "null" : obj.toString();
        }

        static String valueOf(char[] chars) {
            String s = "";
            for (char ch : chars)
                s = s.concat(String.valueOf(ch));
            return s;
        }

        String repeat(int n) {
            String s = "";
            for (int i = 0; i < n; i++)
                s = s.concat(Unsafe2.reinterpretCast(this, String.class));
            return s;
        }

        void getChars(int srcBegin, int srcEnd, char[] dst, int dstBegin) {
            if (srcBegin < 0 || srcBegin >= srcEnd || srcEnd > length() || dstBegin < 0 || dstBegin + (srcEnd - srcBegin) > dst.length)
                throw new StringIndexOutOfBoundsException();
            for (int i = srcBegin; i < srcEnd; i++)
                dst[dstBegin + i - srcBegin] = charAt(i);
        }

        char[] toCharArray() {
            char[] c = new char[length()];
            for (int i = 0; i < length(); i++)
                c[i] = charAt(i);
            return c;
        }

        static String join(String prefix, String suffix, String d, String[] strings, int n) {
            String s = prefix;
            for (int i = 0; i < n; i++) {
                if (i != 0)
                    s = s.concat(d);
                s = s.concat(strings[i]);
            }
            return s.concat(suffix);
        }

        static String join(CharSequence delimiter, Iterable<? extends CharSequence> elements) {
            String delimStr = delimiter.toString(); // implicit null-check
            Objects.requireNonNull(elements);

            String s = "";
            boolean first = true;
            for (CharSequence elem : elements) {
                String elemStr = String.valueOf(elem);
                if (first) {
                    first = false;
                    s = elemStr;
                } else {
                    s = s.concat(delimStr.concat(elemStr));
                }
            }
            return s;
        }

        int compareTo(StringImpl si) {
            Objects.requireNonNull(si);
            return compareImpl(this, si);
        }

        int compareTo(Object obj) {
            return compareTo((StringImpl) obj);
        }

        @Ignore
        @Snippet("$0.localeCompare($1, 'en-US')") // TODO
        static native int compareImpl(StringImpl a, StringImpl b);


        String[] split(String regex) {
            Object split = splitImpl(regex);
            return Unsafe2.setType(split, String[].class);
        }

        @Ignore
        @Snippet("$0.split(new RegExp($1))")
        // TODO meg kéne csinálni, hogy ha final osztály replacementje
        //      akkor a metódusokat finalnak vegye
        final native Object splitImpl(String regex);

        @Snippet("$0.trim()")
        native String trim();

        StringImpl intern() {
            // JSben stringeknek nincs identitása
            return this;
        }

        @Snippet("$0.startsWith($1)")
        native boolean startsWith(String s2);

        @Snippet("$0.startsWith($1, $2)")
        native boolean startsWith(String s2, int toffset);

        @Snippet("$0.endsWith($1)")
        native boolean endsWith(String s2);

        @Snippet("$0.toLowerCase().localeCompare($1.toLowerCase(), '')")
        native int compareToIgnoreCase(String s); // TODO toLowerCase(Locale.ROOT).compareTo(s.toLowerCase(Locale.ROOT))

        @Snippet("$0.codePointAt($1)") // TODO caniuse
        native int codePointAt(int c);

        StringImpl replace(char a, char b) {
            return replace(String.valueOf(a), String.valueOf(b));
        }

        // belső függvények, StringConcatHelper használja
        byte coder() {
            return 1;
        }

        void getBytes(byte[] dst, int dstBegin, byte coder) {
            if (coder != 1)
                throw new RuntimeException();

            dstBegin *= 2;
            for (int i = 0; i < length(); i++) {
                dst[dstBegin + i * 2] = (byte) (charAt(i) >> 8);
                dst[dstBegin + i * 2 + 1] = (byte) charAt(i);
            }
        }

        public byte[] getBytes(Charset charset) {
            if (charset != StandardCharsets.UTF_8)
                throw new RuntimeException("TODO");

            // String.encodeUTF8_UTF16-ból másolva

            int dp = 0;
            int sp = 0;
            int sl = length();
            byte[] dst = new byte[sl * 3];
            while (sp < sl) {
                // ascii fast loop;
                char c = charAt(sp);
                if (c >= '\u0080') {
                    break;
                }
                dst[dp++] = (byte) c;
                sp++;
            }
            while (sp < sl) {
                char c = charAt(sp++);
                if (c < 0x80) {
                    dst[dp++] = (byte) c;
                } else if (c < 0x800) {
                    dst[dp++] = (byte) (0xc0 | (c >> 6));
                    dst[dp++] = (byte) (0x80 | (c & 0x3f));
                } else if (Character.isSurrogate(c)) {
                    int uc = -1;
                    char c2;
                    if (Character.isHighSurrogate(c) && sp < sl &&
                            Character.isLowSurrogate(c2 = charAt(sp))) {
                        uc = Character.toCodePoint(c, c2);
                    }
                    if (uc < 0) {
                        dst[dp++] = '?';
                    } else {
                        dst[dp++] = (byte) (0xf0 | ((uc >> 18)));
                        dst[dp++] = (byte) (0x80 | ((uc >> 12) & 0x3f));
                        dst[dp++] = (byte) (0x80 | ((uc >> 6) & 0x3f));
                        dst[dp++] = (byte) (0x80 | (uc & 0x3f));
                        sp++;  // 2 chars
                    }
                } else {
                    // 3 bytes, 16 bits
                    dst[dp++] = (byte) (0xe0 | ((c >> 12)));
                    dst[dp++] = (byte) (0x80 | ((c >> 6) & 0x3f));
                    dst[dp++] = (byte) (0x80 | (c & 0x3f));
                }
            }
            if (dp == dst.length) {
                return dst;
            }
            return Arrays.copyOf(dst, dp);
        }

        public IntStream chars() {
            IntStream.Builder b = IntStream.builder();
            for (int i = 0; i < length(); i++)
                b.add(charAt(i));
            return b.build();
        }

        public IntStream codePoints() {
            IntStream.Builder b = IntStream.builder();
            for (int i = 0; i < length(); ) {
                int cp = codePointAt(i);
                b.add(cp);
                i += Character.charCount(cp);
            }
            return b.build();
        }

        @SuppressWarnings("rawtypes")
        public Optional describeConstable() {
            return Optional.of(this);
        }

        public Object resolveConstantDesc(MethodHandles.Lookup lookup) {
            return this;
        }

        @Name("resolveConstantDesc")
        public StringImpl resolveConstantDesc_2(MethodHandles.Lookup lookup) {
            return this;
        }
    }

    @ForClass("java/lang/AbstractStringBuilder")
    private static class AbstractStringBuilderImpl implements CharSequence, Appendable {

        // Object, CharSequence, Appendable függvények implementációja

        @Override
        public int length() {
            return 0;
        }

        @Override
        public char charAt(int index) {
            return toString().charAt(index);
        }

        @Override
        @Nonnull
        public CharSequence subSequence(int start, int end) {
            return toString().substring(start, end);
        }

        @Override
        public String toString() {
            return Unsafe2.reinterpretCast(this, StringBuilderImpl.class).toString();
        }

        @Override
        public Appendable append(CharSequence s) {
            return Unsafe2.reinterpretCast(Unsafe2.reinterpretCast(this, StringBuilderImpl.class).
                    append(s == null ? "null" : s.toString()), Appendable.class);
        }

        @Override
        public Appendable append(CharSequence csq, int start, int end) throws IOException {
            if (csq == null)
                csq = "null";
            return append(csq.subSequence(start, end));
        }

        @Override
        public Appendable append(char c) throws IOException {
            return append(String.valueOf(c));
        }

        @Override
        public IntStream chars() {
            return toString().chars();
        }

        @Override
        public IntStream codePoints() {
            return toString().codePoints();
        }

        Spliterator.OfInt lambda$chars$0() {
            throw new UnsupportedOperationException();
        }

        Spliterator.OfInt lambda$codePoints$1() {
            throw new UnsupportedOperationException();
        }

        public void getChars(int srcBegin, int srcEnd, char[] dst, int dstBegin) {
            toString().getChars(srcBegin, srcEnd, dst, dstBegin);
        }
    }

    // ez most kopipésztelve van StringBufferImpl-ként is lejjebb.
    // meg kéne szüntetni valahogy ezt a kettősséget.
    // az a baj, hogy ha @For-ban value={StringBuilder.class, StringBuffer.class} lenne,
    // akkor a mindkettő natív típusának a neve ugyanaz lenne, és összekavarodnának
    // (ami meglepő módon nem okozott valódi problémát, csak onnan vettem észre hogy
    // CC pampogott hogy duplikált függvénynév)

    @For(value = StringBuilder.class, replace = true)
    private static class StringBuilderImpl implements Appendable, CharSequence {

        private String s = "";

        @Inline
        public StringBuilderImpl() {
        }

        @Inline
        public StringBuilderImpl(int initialCapacity) {
            // initialCapacity unused
        }

        @Inline
        public StringBuilderImpl(String s) {
            this.s = s;
        }

        @Inline
        StringBuilderImpl append(String string) {
            s = s.concat(string);
            return this;
        }

        StringBuilderImpl append(int string) {
            s = s.concat(String.valueOf(string));
            return this;
        }

        public StringBuilderImpl append(char ch) {
            s = s.concat(String.valueOf(ch));
            return this;
        }

        StringBuilderImpl append(Object obj) {
            s = s.concat(String.valueOf(obj));
            return this;
        }

        StringBuilderImpl append(float f) {
            s = s.concat(String.valueOf(f));
            return this;
        }

        StringBuilderImpl append(double f) {
            s = s.concat(String.valueOf(f));
            return this;
        }

        StringBuilderImpl append(boolean b) {
            s = s.concat(String.valueOf(b));
            return this;
        }

        StringBuilderImpl append(long l) {
            s = s.concat(String.valueOf(l));
            return this;
        }

        @Name("append")
        Appendable append_altsig(CharSequence s) {
            return Unsafe2.reinterpretCast(append(s == null ? "null" : s.toString()), Appendable.class);
        }

        public StringBuilderImpl append(char[] ch, int offset, int length) {
            for (int i = offset; i < offset + length; i++)
                s = s.concat(String.valueOf(ch[i]));
            return this;
        }

        public StringBuilderImpl append(CharSequence s) {
            return append(s == null ? "null" : s.toString());
        }

        public StringBuilderImpl append(CharSequence s, int start, int end) {
            if (s == null)
                s = "null";
            // ha elég hosszú az s, akkor nem kéne a teljeset stringgé alakítani
            append(((String) s).substring(start, end));
            return this;
        }

        int compareTo(Object other) {
            if (getClass() == other.getClass())
                return compareTo(Unsafe2.reinterpretCast(this, StringBuilderImpl.class));
            else
                throw new ClassCastException("SB cT");
        }

        int compareTo(StringBuilderImpl other) {
            return s.compareTo(other.s);
        }

        @Inline
        public int length() {
            return s.length();
        }

        @Override
        public char charAt(int index) {
            return s.charAt(index);
        }

        @Override
        public CharSequence subSequence(int start, int end) {
            return s.substring(start, end);
        }

        void setLength(int len) {
            if (len <= s.length())
                s = s.substring(0, len);
            while (s.length() < len)
                s = s.concat(String.valueOf((char) 0));
        }

        StringBuilderImpl insert(int i, String t) {
            checkIndex(i);
            s = s.substring(0, i).concat(t).concat(s.substring(i));
            return this;
        }

        StringBuilderImpl insert(int i, boolean t) {
            return insert(i, String.valueOf(t));
        }

        StringBuilderImpl insert(int i, char t) {
            return insert(i, String.valueOf(t));
        }

        StringBuilderImpl insert(int i, int t) {
            return insert(i, String.valueOf(t));
        }

        StringBuilderImpl insert(int i, float t) {
            return insert(i, String.valueOf(t));
        }

        StringBuilderImpl insert(int i, long t) {
            return insert(i, String.valueOf(t));
        }

        StringBuilderImpl insert(int i, double t) {
            return insert(i, String.valueOf(t));
        }

        private void checkIndex(int i) {
            if (i < 0 || i > s.length())
                throw new StringIndexOutOfBoundsException();
        }

        String substring(int i) {
            return s.substring(i);
        }

        String substring(int a, int b) {
            return s.substring(a, b);
        }

        @Override
        @Inline
        public String toString() {
            return s;
        }

        StringBuilderImpl appendCodePoint(int cp) {
            s = s.concat(StringImpl.fromCodePoint(cp));
            return this;
        }

        @Override
        public IntStream chars() {
            IntStream.Builder b = IntStream.builder();
            for (int i = 0; i < length(); i++)
                b.add(charAt(i));
            return b.build();
        }

        @Override
        public IntStream codePoints() {
            IntStream.Builder b = IntStream.builder();
            for (int i = 0; i < length(); ) {
                int cp = s.codePointAt(i);
                b.add(cp);
                i += Character.charCount(cp);
            }
            return b.build();
        }

        public StringBuilderImpl replace(int start, int end, String s) {
            if (start < 0 || end > s.length())
                throw new StringIndexOutOfBoundsException();
            s = s.substring(0, start) + s + s.substring(end);
            return this;
        }

        public void setCharAt(int index, char ch) {
            replace(index, index + 1, String.valueOf(ch));
        }

        public StringBuilderImpl deleteCharAt(int index) {
            replace(index, index + 1, "");
            return this;
        }

        public void getChars(int srcBegin, int srcEnd, char[] dst, int dstBegin) {
            toString().getChars(srcBegin, srcEnd, dst, dstBegin);
        }
    }

    @For(value = StringBuffer.class, replace = true)
    private static class StringBufferImpl implements Appendable, CharSequence {

        private String s = "";

        @Inline
        public StringBufferImpl() {
        }

        @Inline
        public StringBufferImpl(int initialCapacity) {
            // initialCapacity unused
        }

        @Inline
        public StringBufferImpl(String s) {
            this.s = s;
        }

        @Inline
        StringBufferImpl append(String string) {
            s = s.concat(string);
            return this;
        }

        StringBufferImpl append(int string) {
            s = s.concat(String.valueOf(string));
            return this;
        }

        public StringBufferImpl append(char ch) {
            s = s.concat(String.valueOf(ch));
            return this;
        }

        StringBufferImpl append(Object obj) {
            s = s.concat(String.valueOf(obj));
            return this;
        }

        StringBufferImpl append(float f) {
            s = s.concat(String.valueOf(f));
            return this;
        }

        StringBufferImpl append(double f) {
            s = s.concat(String.valueOf(f));
            return this;
        }

        StringBufferImpl append(boolean b) {
            s = s.concat(String.valueOf(b));
            return this;
        }

        StringBufferImpl append(long l) {
            s = s.concat(String.valueOf(l));
            return this;
        }

        @Name("append")
        Appendable append_altsig(CharSequence s) {
            return Unsafe2.reinterpretCast(append(s == null ? "null" : s.toString()), Appendable.class);
        }

        public StringBufferImpl append(char[] ch, int offset, int length) {
            for (int i = offset; i < offset + length; i++)
                s = s.concat(String.valueOf(ch[i]));
            return this;
        }

        public StringBufferImpl append(CharSequence s) {
            return append(s == null ? "null" : s.toString());
        }

        public StringBufferImpl append(CharSequence s, int start, int end) {
            if (s == null)
                s = "null";
            // ha elég hosszú az s, akkor nem kéne a teljeset stringgé alakítani
            append(((String) s).substring(start, end));
            return this;
        }

        int compareTo(Object other) {
            if (getClass() == other.getClass())
                return compareTo(Unsafe2.reinterpretCast(this, StringBufferImpl.class));
            else
                throw new ClassCastException("SB cT");
        }

        int compareTo(StringBufferImpl other) {
            return s.compareTo(other.s);
        }

        @Inline
        public int length() {
            return s.length();
        }

        @Override
        public char charAt(int index) {
            return s.charAt(index);
        }

        @Override
        public CharSequence subSequence(int start, int end) {
            return s.substring(start, end);
        }

        void setLength(int len) {
            if (len <= s.length())
                s = s.substring(0, len);
            while (s.length() < len)
                s = s.concat(String.valueOf((char) 0));
        }

        StringBufferImpl insert(int i, String t) {
            checkIndex(i);
            s = s.substring(0, i).concat(t).concat(s.substring(i));
            return this;
        }

        StringBufferImpl insert(int i, boolean t) {
            return insert(i, String.valueOf(t));
        }

        StringBufferImpl insert(int i, char t) {
            return insert(i, String.valueOf(t));
        }

        StringBufferImpl insert(int i, int t) {
            return insert(i, String.valueOf(t));
        }

        StringBufferImpl insert(int i, float t) {
            return insert(i, String.valueOf(t));
        }

        StringBufferImpl insert(int i, long t) {
            return insert(i, String.valueOf(t));
        }

        StringBufferImpl insert(int i, double t) {
            return insert(i, String.valueOf(t));
        }

        private void checkIndex(int i) {
            if (i < 0 || i > s.length())
                throw new StringIndexOutOfBoundsException();
        }

        String substring(int i) {
            return s.substring(i);
        }

        String substring(int a, int b) {
            return s.substring(a, b);
        }

        @Override
        @Inline
        public String toString() {
            return s;
        }

        StringBufferImpl appendCodePoint(int cp) {
            s = s.concat(StringImpl.fromCodePoint(cp));
            return this;
        }

        @Override
        public IntStream chars() {
            IntStream.Builder b = IntStream.builder();
            for (int i = 0; i < length(); i++)
                b.add(charAt(i));
            return b.build();
        }

        @Override
        public IntStream codePoints() {
            IntStream.Builder b = IntStream.builder();
            for (int i = 0; i < length(); ) {
                int cp = s.codePointAt(i);
                b.add(cp);
                i += Character.charCount(cp);
            }
            return b.build();
        }

        public StringBufferImpl replace(int start, int end, String s) {
            if (start < 0 || end > s.length())
                throw new StringIndexOutOfBoundsException();
            s = s.substring(0, start) + s + s.substring(end);
            return this;
        }

        public void setCharAt(int index, char ch) {
            replace(index, index + 1, String.valueOf(ch));
        }

        public StringBufferImpl deleteCharAt(int index) {
            replace(index, index + 1, "");
            return this;
        }

        public void getChars(int srcBegin, int srcEnd, char[] dst, int dstBegin) {
            toString().getChars(srcBegin, srcEnd, dst, dstBegin);
        }
    }

    @For(Integer.class)
    private static class IntegerImpl {

        @Snippet("(\"\"+$0)")
        public static native String toString(int i);

        public static String toUnsignedString(int i) {
            return i >= 0 ? toString(i) : toStringImpl(4294967296D - i);
        }

        public static String toUnsignedString(int i, int radix) {
            if (radix == 10)
                return toUnsignedString(i);
            else
                return i >= 0 ? toStringImpl2(i, radix) : toStringImpl2(4294967296D - i, radix);
        }

        public static String toString(int i, int radix) {
            return toStringImpl2(i, radix);
        }

        // ez ugyanaz mint a beépített, csak így rakhatunk @Inline-t
        @Inline
        static String toHexString(int i) {
            return toUnsignedString(i, 16);
        }

        @Inline
        static String toUnsignedString0(int i, int shift) {
            return toUnsignedString(i, 1 << shift);
        }

        @Snippet("$0.toString()")
        @Ignore
        private static native String toStringImpl(double d);

        @Snippet("$0.toString($1)")
        @Ignore
        private static native String toStringImpl2(double d, int radix);

        public Optional<? extends ConstantDesc> describeConstable() {
            return Optional.empty();
        }

        @SuppressWarnings({"removal", "UnnecessaryBoxing", "CachedNumberConstructorCall"})
        public static Integer valueOf(int i) {
            return new Integer(i);
        }
    }

    @For(Long.class)
    private static class LongImpl {

        @Snippet("$0.toString($1)")
        public static native String toString(long i, int radix);

        public Optional<? extends ConstantDesc> describeConstable() {
            return Optional.empty();
        }

        @SuppressWarnings({"removal", "UnnecessaryBoxing", "CachedNumberConstructorCall"})
        public static Long valueOf(long ch) {
            return new Long(ch);
        }
    }

    @For(System.class)
    private static class SystemImpl {

        static void arraycopy(Object src, int srcOffset, Object dst, int dstOffset, int length) {
            if (srcOffset < 0 || dstOffset < 0 || srcOffset + length > Array.getLength(src)
                    || dstOffset + length > Array.getLength(dst))
                throw new IndexOutOfBoundsException();

            for (int i = srcOffset; i < srcOffset + length; i++) {
                Array.set(dst, dstOffset - srcOffset + i, Array.get(src, i));
            }
        }

        static long currentTimeMillis() {
            return (long) currentTimeMillisImpl();
        }

        static long nanoTime() {
            return (long) (currentTimeMillisImpl() % 10000000) * 1000000;
        }

        @Snippet("+new Date") // ide nem kéne zárójel?
        @Ignore
        private static native double currentTimeMillisImpl();

        @Snippet("hc($0)")
        static native int identityHashCode(Object obj);

        @SuppressWarnings("removal")
        @MustBeInlined
        static SecurityManager getSecurityManager() {
            return null;
        }
    }

    @For(ArrayHelper.class)
    private static class ArrayHelperImpl {

        // jobb lenne felcserélni az argumentumok sorrendjét, úgy rémlik
        // hogy engedélyezve lesz valami optimalizáció
        @Snippet("allocArray($1, $0)")
        public static native Object allocate(Class<?> arrayType, int size);
    }

    @For(Array.class)
    private static class ArrayImpl {

        @Snippet("allocArray($1, arrayTypeFromCompType($0))")
        native static Object newArray(Class<?> componentType, int length);

        @Snippet("$0.length") // TODO ellenőrizzük hogy tényleg tömbről van-e szó
        static native int getLength(Object array);

        // TODO wrappelni kéne a visszaadott értéket
        @Snippet("$0[$1]")
        static native Object get(Object array, int index);

        @Snippet("$0[$1]=$2")
        static native void set(Object array, int index, Object value);
    }

    @For(Class.class)
    private static class ClassImpl {

        @DontInline
            // hogy Optimizer2 lecserélhesse konstans receiver esetén.
            // csak hát így meg ha nem konstans, akkor meg értelmetlen hogy nem lesz inline-olva.
        boolean isArray() {
            return getComponentType() != null;
        }

        @Snippet("($0.e||null)")
        native ClassImpl getComponentType(); // TODO descriptorStringben is van hivatkozás erre

        @Snippet("($0.e||null)")
        ClassImpl componentType() {
            return getComponentType();
        }

        @Snippet("$0.prim")
        native boolean isPrimitive();

        boolean isInstance(Object obj) {
            return Unsafe2.instanceOf_nonConst(obj, Unsafe2.reinterpretCast(this, Class.class));
        }

        boolean isAssignableFrom(ClassImpl o) {
            Objects.requireNonNull(o);
            return isAssignableFromImpl(this, o);
        }

        @Snippet("isIncomingUntypedValueType($0)")
        @Ignore
        private static native boolean isIncomingUntypedValueType(ClassImpl clazz);

        @Ignore
        @Snippet("addSupertype($1, $0)")
        private native void addSupertype(ClassImpl supertype);

        @Snippet("$1[$0.typeName]")
        @Ignore
        native static boolean isAssignableFromImpl(ClassImpl thiz, ClassImpl o);

        @Snippet("$0.jtn")
        native String getName();

        @Snippet("$0.jtn")
        native String getSimpleName(); // TODO

        @Snippet("$0.jtn")
        native String getSimpleBinaryName(); // LambdaForm$NamedFunctionnek kell

        boolean isInterface() {
            return false; // TODO
        }

        @Snippet("$0.st")
        native Class<?> getSuperclass();

        @Snippet("$0.declaringClass")
        native Class<?> getDeclaringClass();


        public Optional<? extends ConstantDesc> describeConstable() {
            return Optional.empty();
        }

        Object[] getEnumConstantsShared() {
            return EnumConstantsCV.enumConstantsCV.get(Unsafe2.reinterpretCast(this, Class.class));
        }

        boolean isRecord0() {
            return getSuperclass() == Record.class;
        }

        String toGenericString() {
            return toString();
        }

        TypeVariable<?>[] getTypeParameters() {
            throw new UnsupportedOperationException();
        }

        AnnotationDataImpl annotationData() {
            throw new UnsupportedOperationException();
        }

        ReflectionDataImpl newReflectionData(SoftReferenceImpl oldReflectionData, int redefineCount) {
            throw new UnsupportedOperationException();
        }
    }

    @ForClass("java/lang/Class$AnnotationData")
    static class AnnotationDataImpl {

    }

    @ForClass("java/lang/Class$ReflectionData")
    static class ReflectionDataImpl {

    }

    public static class EnumConstantsCV extends ClassValue<Object[]> {

        static final ClassValue<Object[]> enumConstantsCV = new EnumConstantsCV();

        @Override
        protected Object[] computeValue(Class<?> type) {
            return type.getEnumConstants();
        }
    }

    @For(Throwable.class)
    private static class ThrowableImpl {

        @Snippet("$0")
        native Throwable fillInStackTrace(int i);
    }

    @For(StackTraceElement.class)
    private static class StackTraceElementImpl {

        void computeFormat() {
            // ha nem lenne felülírva, bekerülne mindenféle modullista a JS-be
        }
    }

    @For(NullPointerException.class)
    private static class NullPointerExceptionImpl {

        String getExtendedNPEMessage() {
            return "enm";
        }
    }

    @For(Math.class)
    private static class MathImpl {

        @Snippet("ll.fromInt(Math.round($0))")
        static native long round(double d);

        @Snippet("Math.ceil($0)")
        static native double ceil(double d);

        @Snippet("Math.floor($0)")
        static native double floor(double d);

        @Snippet("Math.abs($0)")
        static native double abs(double d);

        @Snippet("Math.abs($0)")
        static native float abs(float d);

        @Snippet("Math.min($0, $1)")
        static native double min(double a, double b);

        @Snippet("Math.max($0, $1)")
        static native double max(double a, double b);

        @Snippet("Math.min($0, $1)")
        static native float min(float a, float b);

        @Snippet("Math.max($0, $1)")
        static native float max(float a, float b);

        @Snippet("Math.sqrt($0)")
        static native double sqrt(double a);

        @Snippet("Math.pow($0, $1)")
        static native double pow(double a, double b);

        @Snippet("Math.sin($0)")
        static native double sin(double a);

        @Snippet("Math.cos($0)")
        static native double cos(double a);

        @Snippet("Math.asin($0)")
        static native double asin(double a);

        @Snippet("Math.acos($0)")
        static native double acos(double a);

        @Snippet("Math.hypot($0, $1)")
        static native double hypot(double a, double b);

        @Snippet("Math.atan2($0, $1)")
        static native double atan2(double a, double b);

        @Snippet("Math.sign($0)")
        static native double signum(double a);
    }

    @For(StrictMath.class)
    private static class StrictMathImpl {

        @Snippet("Math.log($0)")
        static native double log(double d);

        @Snippet("Math.floor($0)")
        static native double floor(double d);

        @Snippet("Math.sin($0)")
        static native double sin(double d);

        @Snippet("Math.cos($0)")
        static native double cos(double d);

        @Snippet("Math.tan($0)")
        static native double tan(double d);
    }

    @For(Thread.class)
    private static class ThreadImpl {

        static Thread currentThread() {
            return Fiber.currentThread;
        }

        static void yield() {
        }

        void interrupt() {
            throw new UnsupportedOperationException("Thread.interrupt");
        }

        static void clearInterruptEvent() {
            throw new UnsupportedOperationException("Thread.clearInterruptEvent");
        }

        void start0() {
            throw new UnsupportedOperationException("Thread.start0");
        }
    }

    @ForClass("java/lang/invoke/MethodHandleNatives$CallSiteContext")
    private static class CallSiteContextImpl {

        static CallSiteContextImpl make(CallSite cs) {
            return null;
        }

        void run() {
        }
    }

    @ForClass(value = "java/lang/invoke/MemberName")
    private static class MemberNameImpl {

        @Override
        public int hashCode() {
            return System.identityHashCode(this); // TODO majd internelni kéne ezeket
        }

        @Override
        public boolean equals(Object obj) {
            return this == obj;
        }

        public boolean equals(MemberNameImpl obj) {
            return this == obj;
        }

        /*
        @Snippet("$0.memberNameReturnType")
        native Class<?> getReturnType();
         */
    }

    @ForClass("jdk/internal/ref/CleanerImpl")
    private static class CleanerImplImpl {

        void run() {
        }
    }

    @ForClass("java/lang/invoke/MethodHandleNatives")
    private static class MethodHandleNativesImpl {
        static void clearCallSiteContext(CallSiteContextImpl callSiteContext) {
        }

        static void expand(MemberNameImpl m) {
        }
    }

    @ForClass("java/lang/invoke/DirectMethodHandle")
    private static class DirectMethodHandleImpl {

        // a probléma, hogy shouldBeInitialized az akkori értéket adja vissza interpreterben,
        // viszont mire végzünk a fordítással, addigra minden osztály inicializálva lesz,
        // ezért bele fog kerülni internalMemberNameEnsureInit hívás a LF-be,
        // ami meg MemberName.getDeclaringClasst fog hívni, amit meg nem tudunk implementálni JS-ben.
        void ensureInitialized() {
        }

        static Object checkCast(Object mh, Object obj) {
            // return typeot kasztolná, fogalmam sincs hogy miért
            return obj;
        }
    }

    @ForClass("java/lang/invoke/MethodHandle")
    private static class MethodHandleImpl {

        // generic invoke()-ot ne használjunk, mert nem fog konvertálni semmit.
        // helyette fordítási időben asType, majd futáskor invokeExact

        public Optional<MethodHandleDesc> describeConstable() {
            return Optional.empty();
        }

        public MethodHandleImpl asType(java.lang.invoke.MethodType mt) {
            // "Length" rekord toStringjének valamiért kell.
            // ami logikus is, de azt nem értem hogy eddig miért nem kellett máshol.
            return this;
        }

        @Override
        public String toString() {
            return "dfjhkl";
        }
    }

    @ForClass("java/lang/invoke/MethodHandleImpl")
    private static class MethodHandleImplImpl {

        static boolean profileBoolean(boolean result, int[] counters) {
            return result;
        }
    }

    @ForClass("jdk/internal/misc/Unsafe")
    private static final class UnsafeImpl {

        @Snippet("new ($1)()")
        @DontInline // ha konstans, Optimizer2 majd átírja ObjectNode-dá,
        native Object allocateInstance(Class<?> clazz);

        void fullFence() {
        }

        void storeFence() {
        }

        void loadFence() {
        }

        boolean shouldBeInitialized0(Class<?> clazz) {
            // fordítási időben inicializálunk minden hivatkozott osztályt, ezért runtime nem kell már
            return false;
        }

        void ensureClassInitialized0(Class<?> clazz) {
            // ld. komment shouldBeInitialized0-ban
        }

        // ha absolute, akkor timeout ms, különben ns
        void park(boolean timeoutIsAbsolute, long timeout) {
            if (true)
                throw new RuntimeException("TODO park");
            if (timeout != 0L) {
                if (timeoutIsAbsolute)
                    timeout -= System.currentTimeMillis();
                else
                    timeout /= 1_000_000;
                initUnparkTimer(timeout, Thread.currentThread());
            }
            Fiber.suspend();
        }

        @Snippet("setTimeout(function() { vt_resume($1) }, $0)")
        @Ignore
        private static native void initUnparkTimer(double time, Thread thread);

        void unpark(Object o) {
            Fiber.resume((Thread) o);
        }

        @MustBeInlined
        Object getReference(Object obj, long offset) {
            return getField(obj, Math.toIntExact(offset));
        }

        @MustBeInlined
        Object getReferenceVolatile(Object obj, long offset) {
            return getField(obj, Math.toIntExact(offset));
        }

        @MustBeInlined
        byte getByte(Object obj, long offset) {
            if (obj != null && obj.getClass().isArray())
                return (byte) getByteFromArray(obj, offset);
            return Unsafe2.reinterpretAsByte(getField(obj, offset));
        }

        @MustBeInlined
        boolean getBoolean(Object obj, long offset) {
            byte b = getByte(obj, offset);
            if (b == 1)
                return true;
            if (b == 0)
                return false;
            throw new RuntimeException("invalid boolean value: " + b);
        }

        @MustBeInlined
        short getShort(Object obj, long offset) {
            if (obj != null && obj.getClass().isArray())
                return (short) (getByteFromArray(obj, offset + 1) << 8
                        | getByteFromArray(obj, offset));
            return Unsafe2.reinterpretAsShort(getField(obj, offset));
        }

        @MustBeInlined
        int getInt(Object obj, long offset) {
            if (obj != null && obj.getClass().isArray())
                return getByteFromArray(obj, offset + 3) << 24
                        | getByteFromArray(obj, offset + 2) << 16
                        | getByteFromArray(obj, offset + 1) << 8
                        | getByteFromArray(obj, offset);
            return Unsafe2.reinterpretAsInt(getField(obj, offset));
        }

        @MustBeInlined
        float getFloat(Object obj, long offset) {
            if (obj != null && obj.getClass().isArray() && obj.getClass() != float[].class)
                throw new UnsupportedOperationException("get float from non-float array");
            return Unsafe2.reinterpretAsFloat(getField(obj, offset));
        }

        @MustBeInlined
        long getLong(Object obj, long offset) {
            if (obj != null && obj.getClass().isArray())
                return (long) getByteFromArray(obj, offset + 7) << 56
                        | (long) getByteFromArray(obj, offset + 6) << 48
                        | (long) getByteFromArray(obj, offset + 5) << 40
                        | (long) getByteFromArray(obj, offset + 4) << 32
                        | (long) getByteFromArray(obj, offset + 3) << 24
                        | (long) getByteFromArray(obj, offset + 2) << 16
                        | (long) getByteFromArray(obj, offset + 1) << 8
                        | (long) getByteFromArray(obj, offset);
            return Unsafe2.reinterpretAsLong(getField(obj, offset));
        }

        @MustBeInlined
        double getDouble(Object obj, long offset) {
            if (obj != null && obj.getClass().isArray() && obj.getClass() != double[].class)
                throw new UnsupportedOperationException("get double from non-double array");
            return Unsafe2.reinterpretAsDouble(getField(obj, offset));
        }

        @Ignore
        private int getByteFromArray(Object array, long offset) {
            int i = Math.toIntExact(offset);
            return switch (array.getClass().getName().charAt(1)) {
                case 'B' -> Unsafe2.reinterpretCast(array, byte[].class)[i] & 0xFF;
                case 'S', 'C' -> (Unsafe2.reinterpretCast(array, short[].class)[i / 2] >>> (i % 2 * 8) & 0xFF);
                case 'I' -> (Unsafe2.reinterpretCast(array, int[].class)[i / 4] >>> (i % 4 * 8) & 0xFF);
                case 'J' -> (int) (Unsafe2.reinterpretCast(array, long[].class)[i / 8] >>> (i % 8 * 8) & 0xFF);
                default -> throw new UnsupportedOperationException("get byte from array with type " +
                        array.getClass().getName());
            };
        }

        @MustBeInlined
        void putReference(Object obj, long offset, Object value) {
            putField(obj, Math.toIntExact(offset), value);
        }

        @MustBeInlined
        void putReferenceVolatile(Object obj, long offset, Object value) {
            putField(obj, Math.toIntExact(offset), value);
        }

        @MustBeInlined
        void putBoolean(Object obj, long offset, boolean value) {
            putField(obj, Math.toIntExact(offset), Unsafe2.reinterpretAsObject(value));
        }

        @MustBeInlined
        void putByte(Object obj, long offset, byte value) {
            if (obj != null && obj.getClass().isArray() && obj.getClass() != byte[].class)
                throw new RuntimeException("TODO");
            putField(obj, Math.toIntExact(offset), Unsafe2.reinterpretAsObject(value));
        }

        @MustBeInlined
        void putShort(Object obj, long offset, short value) {
            if (obj != null && obj.getClass().isArray() && obj.getClass() != short[].class)
                throw new RuntimeException("TODO");
            putField(obj, Math.toIntExact(offset), Unsafe2.reinterpretAsObject(value));
        }

        @MustBeInlined
        void putChar(Object obj, long offset, char value) {
            if (obj != null && obj.getClass().isArray() && obj.getClass() != char[].class)
                throw new RuntimeException("TODO");
            putField(obj, Math.toIntExact(offset), Unsafe2.reinterpretAsObject(value));
        }

        @MustBeInlined
        void putInt(Object obj, long offset, int value) {
            if (obj != null && obj.getClass().isArray() && obj.getClass() != int[].class)
                throw new RuntimeException("TODO");
            putField(obj, Math.toIntExact(offset), Unsafe2.reinterpretAsObject(value));
        }

        @MustBeInlined
        void putFloat(Object obj, long offset, float value) {
            if (obj != null && obj.getClass().isArray() && obj.getClass() != float[].class)
                throw new RuntimeException("TODO");
            putField(obj, Math.toIntExact(offset), Unsafe2.reinterpretAsObject(value));
        }

        @MustBeInlined
        void putLong(Object obj, long offset, long value) {
            if (obj != null && obj.getClass().isArray() && obj.getClass() != long[].class)
                throw new RuntimeException("TODO");
            putField(obj, Math.toIntExact(offset), Unsafe2.reinterpretAsObject(value));
        }

        @MustBeInlined
        void putDouble(Object obj, long offset, double value) {
            if (obj != null && obj.getClass().isArray() && obj.getClass() != double[].class)
                throw new RuntimeException("TODO");
            putField(obj, Math.toIntExact(offset), Unsafe2.reinterpretAsObject(value));
        }

        @Ignore
        @MustBeInlined
        private static void putField(Object obj, int fieldNumber, Object value) {
            if (obj.getClass().isArray())
                setArrayElementValue(obj,
                        fieldNumberToArrayIndex(obj, fieldNumber), value);
            else
                Unsafe2.putInstanceField(obj, fieldNumber, value);
        }

        @Ignore
        @MustBeInlined // hogy ne vesszen el hogy konstans a fieldNumer
        private static Object getField(Object obj, long fieldNumber) {
            int fn2 = Math.toIntExact(fieldNumber);
            if (obj.getClass().isArray())
                return getArrayElementValue(obj, fieldNumberToArrayIndex(obj, fn2));
            else
                return Unsafe2.getInstanceField(obj, fn2);
        }

        @Ignore
        private static int fieldNumberToArrayIndex(Object obj, int fn2) {
            return fn2 /
                    Unsafe2.refSize(obj.getClass().getComponentType());
        }

        @Ignore
        @Snippet("$0[$1] = $2")
        private static native void setArrayElementValue(Object obj, int index, Object value);

        @Ignore
        @Snippet("$0[$1]")
        private static native Object getArrayElementValue(Object obj, int index);

        boolean compareAndSetReference(Object obj, long offset, Object expected, Object value) {
            int index = Math.toIntExact(offset);
            if (obj instanceof Object[] arr) {
                if (arr[index] == expected) {
                    arr[index] = value;
                    return true;
                } else
                    return false;
            } else {
                if (Unsafe2.getInstanceField(obj, index) == expected) {
                    putField(obj, index, value);
                    return true;
                } else
                    return false;
            }
        }

        boolean compareAndSetInt(Object obj, long offset, int expected, int value) {
            int index = Math.toIntExact(offset);
            if (obj instanceof int[] arr) {
                if (arr[index] == expected) {
                    arr[index] = value;
                    return true;
                } else
                    return false;
            } else {
                if (Unsafe2.reinterpretAsInt(Unsafe2.getInstanceField(obj, index)) == expected) {
                    putField(obj, index, Unsafe2.reinterpretAsObject(value));
                    return true;
                } else
                    return false;
            }
        }

        boolean compareAndSetFloat(Object obj, long offset, float expected, float value) {
            float[] arr = (float[]) obj;
            int index = Math.toIntExact(offset);
            if (arr[index] == expected) {
                arr[index] = value;
                return true;
            } else
                return false;
        }

        boolean compareAndSetDouble(Object obj, long offset, double expected, double value) {
            double[] arr = (double[]) obj;
            int index = Math.toIntExact(offset);
            if (arr[index] == expected) {
                arr[index] = value;
                return true;
            } else
                return false;
        }

        boolean compareAndSetLong(Object obj, long offset, long expected, long value) {
            int index = Math.toIntExact(offset);
            if (obj instanceof long[] arr) {
                if (arr[index] == expected) {
                    arr[index] = value;
                    return true;
                } else
                    return false;
            } else {
                if (Unsafe2.reinterpretAsLong(Unsafe2.getInstanceField(obj, index)) == expected) {
                    putField(obj, index, Unsafe2.reinterpretAsObject(value));
                    return true;
                } else
                    return false;
            }
        }

        @Snippet("($0.t.ais|0)")
        @Ignore
        private static native int arrayIndexScale(Object obj);

        @MustBeInlined
            // mert különben pampogni fog hogy receiver not constant
            // bár mondjuk amúgy sem értem, hogy a put* miért MustBeInlined-es
        void copyMemory0(Object srcBase, long srcOffset, Object destBase, long destOffset, long bytes) {
            for (int i = 0; i < bytes; i++)
                putByte(destBase, destOffset + i, getByte(srcBase, srcOffset + i));
        }
    }

    @ForClass("java/util/zip/Inflater$InflaterZStreamRef")
    private static class InflaterZStreamRefImpl {
        void run() {
        }
    }

    @ForClass("java/util/zip/ZipFile$CleanableResource")
    private static class ZipFileCleanableResourceImpl {
        void run() {
        }
    }

    @For(Reference.class)
    private static class ReferenceImpl {

        boolean refersTo(Object obj) {
            return Unsafe2.reinterpretCast(this, Reference.class).get() == obj;
        }

        void clear() {
            Unsafe2.putInstanceField(this, Unsafe2.fieldID(Reference.class, "referent", Object.class), null);
        }
    }

    @ForClass("jdk/internal/reflect/Reflection")
    private static class ReflectionImpl {
        // CallerSensitive metódusokat inline-oljuk, ezért ez nem lesz meghívva
        static Class<?> getCallerClass() {
            throw new UnsupportedOperationException("Reflection.getCallerClass");
        }
    }

    @ForClass(value = "java/lang/reflect/Method")
    private static class MethodImpl {

        public void setAccessible(boolean v) {
            // TODO
        }

        public Object invoke(Object receiver, Object[] args) {
            java.lang.reflect.Method m = Unsafe2.reinterpretCast(this, java.lang.reflect.Method.class);
            if (!Modifier.isStatic(m.getModifiers())) {
                Objects.requireNonNull(receiver); // JDK implementáció is NPE-t dob
                if (!m.getDeclaringClass().isInstance(receiver))
                    throw new IllegalArgumentException("wrong receiver type");
            }
            Class<?>[] paramTypes = m.getParameterTypes();
            args = checkAndUnboxParams(args, paramTypes);

            int slotFieldID = Unsafe2.fieldID(java.lang.reflect.Method.class, "slot", int.class);
            int methodID = Unsafe2.reinterpretAsInt(Unsafe2.getInstanceField(this, slotFieldID));

            if (Modifier.isStatic(m.getModifiers()))
                return Unsafe2.box(Unsafe2.invokeStatic(methodID, args), m.getReturnType());
            else
                return Unsafe2.box(Unsafe2.invokeVirtual(methodID, receiver, args), m.getReturnType());
        }

        Annotation[][] getParameterAnnotations() {
            throw new UnsupportedOperationException();
        }

        @Ignore
        static Object[] checkAndUnboxParams(Object[] args, Class<?>[] paramTypes) {
            if (paramTypes.length != args.length)
                throw new IllegalArgumentException("wrong arg count");
            Object[] unboxedArgs = new Object[args.length];
            for (int i = 0; i < args.length; i++) {
                unboxedArgs[i] = Unsafe2.unbox(args[i], paramTypes[i]);
                if (unboxedArgs[i] == args[i] /* nem primitív típus */ &&
                        (args[i] != null && !paramTypes[i].isInstance(args[i])))
                    throw new IllegalArgumentException("wrong arg type");
            }
            return unboxedArgs;
        }
    }

    @ForClass(value = "java/lang/reflect/Constructor")
    private static class ConstructorImpl {

        public void setAccessible(boolean v) {
            // TODO
        }

        public Object newInstance(Object[] args) {
            java.lang.reflect.Constructor<?> m
                    = Unsafe2.reinterpretCast(this, java.lang.reflect.Constructor.class);
            args = MethodImpl.checkAndUnboxParams(args, m.getParameterTypes());

            int slotFieldID = Unsafe2.fieldID(java.lang.reflect.Constructor.class, "slot", int.class);
            int methodID = Unsafe2.reinterpretAsInt(Unsafe2.getInstanceField(this, slotFieldID));

            Object obj = Unsafe2.allocateInstance(m.getDeclaringClass());
            Unsafe2.invokeSpecial(methodID, obj, args);
            return obj;
        }

        Annotation[][] getParameterAnnotations() {
            throw new UnsupportedOperationException();
        }
    }

    @ForClass(value = "java/lang/reflect/Field")
    private static class FieldImpl {

        public void setAccessible(boolean v) {
            // TODO
        }

        public Object get(Object obj) {
            return Unsafe2.box(getRawValue(obj), asField().getType());
        }

        public boolean getBoolean(Object obj) {
            if (asField().getType() != boolean.class)
                throw new IllegalArgumentException("can't get field as boolean");
            return Unsafe2.reinterpretAsBoolean(getRawValue(obj));
        }

        public byte getByte(Object obj) {
            if (asField().getType() != byte.class)
                throw new IllegalArgumentException("can't get field as byte");
            return Unsafe2.reinterpretAsByte(getRawValue(obj));
        }

        public short getShort(Object obj) {
            if (Unsafe2.canBeWidenedTo(asField().getType(), short.class))
                throw new IllegalArgumentException("can't get field as short");
            return Unsafe2.reinterpretAsShort(getRawValue(obj));
        }

        public char getChar(Object obj) {
            if (asField().getType() != char.class)
                throw new IllegalArgumentException("can't get field as char");
            return Unsafe2.reinterpretAsChar(getRawValue(obj));
        }

        public int getInt(Object obj) {
            if (Unsafe2.canBeWidenedTo(asField().getType(), int.class))
                throw new IllegalArgumentException("can't get field as short");
            return Unsafe2.reinterpretAsInt(getRawValue(obj));
        }

        public float getFloat(Object obj) {
            if (Unsafe2.canBeWidenedTo(asField().getType(), float.class))
                throw new IllegalArgumentException("can't get field as float");
            return Unsafe2.reinterpretAsFloat(getRawValue(obj));
        }

        @SuppressWarnings("ConstantConditions")
        public long getLong(Object obj) {
            if (Unsafe2.canBeWidenedTo(asField().getType(), float.class))
                throw new IllegalArgumentException("can't get field as float");

            Object rawValue = getRawValue(obj);
            if (rawValue.getClass() == long.class)
                return Unsafe2.reinterpretAsLong(rawValue);
            else
                return Unsafe2.reinterpretAsInt(rawValue);
        }

        @SuppressWarnings("ConstantConditions")
        public double getDouble(Object obj) {
            if (Unsafe2.canBeWidenedTo(asField().getType(), double.class))
                throw new IllegalArgumentException("can't get field as float");

            Object rawValue = getRawValue(obj);
            if (rawValue.getClass() == long.class)
                return Unsafe2.reinterpretAsLong(rawValue);
            else
                return Unsafe2.reinterpretAsDouble(rawValue);
        }

        public void set(Object obj, Object value) {
            setRawValue(obj, Unsafe2.unbox(value, asField().getType()));
        }

        // TODO többi setter

        @Ignore
        private Object getRawValue(Object obj) {
            // nincs egyelőre access check

            int slotFieldID = Unsafe2.fieldID(java.lang.reflect.Field.class, "slot", int.class);
            int slot = Unsafe2.reinterpretAsInt(Unsafe2.getInstanceField(this, slotFieldID));
            java.lang.reflect.Field f = asField();

            if ((f.getModifiers() & Modifier.STATIC) != 0)
                return Unsafe2.getStaticField(slot);
            else {
                if (!f.getDeclaringClass().isInstance(obj))
                    throw new IllegalArgumentException("wrong object type");

                return Unsafe2.getInstanceField(obj, slot);
            }
        }

        @Ignore
        private void setRawValue(Object obj, Object value) {
            // nincs egyelőre access check

            int slotFieldID = Unsafe2.fieldID(java.lang.reflect.Field.class, "slot", int.class);
            int slot = Unsafe2.reinterpretAsInt(Unsafe2.getInstanceField(this, slotFieldID));
            java.lang.reflect.Field f = asField();

            if ((f.getModifiers() & Modifier.STATIC) != 0)
                Unsafe2.putStaticField(slot, value);
            else {
                if (!f.getDeclaringClass().isInstance(obj))
                    throw new IllegalArgumentException("wrong object type");

                Unsafe2.putInstanceField(obj, slot, value);
            }
        }

        @Ignore
        private java.lang.reflect.Field asField() {
            return Unsafe2.reinterpretCast(this, java.lang.reflect.Field.class);
        }

        @SuppressWarnings("rawtypes")
        Map declaredAnnotations() {
            throw new UnsupportedOperationException();
        }
    }

    @ForClass("java/nio/BufferMismatch")
    private static class BufferMismatchImpl {

        static int mismatch(ByteBuffer a, int aOff, ByteBuffer b, int bOff, int length) {
            for (int i = 0; i < length; i++) {
                byte av = a.get(aOff + i);
                byte bv = b.get(bOff + i);
                if (av != bv)
                    return i;
            }
            return -1;
        }

        static int mismatch(ShortBuffer a, int aOff, ShortBuffer b, int bOff, int length) {
            for (int i = 0; i < length; i++) {
                short av = a.get(aOff + i);
                short bv = b.get(bOff + i);
                if (av != bv)
                    return i;
            }
            return -1;
        }

        static int mismatch(CharBuffer a, int aOff, CharBuffer b, int bOff, int length) {
            for (int i = 0; i < length; i++) {
                char av = a.get(aOff + i);
                char bv = b.get(bOff + i);
                if (av != bv)
                    return i;
            }
            return -1;
        }

        static int mismatch(IntBuffer a, int aOff, IntBuffer b, int bOff, int length) {
            for (int i = 0; i < length; i++) {
                int av = a.get(aOff + i);
                int bv = b.get(bOff + i);
                if (av != bv)
                    return i;
            }
            return -1;
        }

        static int mismatch(LongBuffer a, int aOff, LongBuffer b, int bOff, int length) {
            for (int i = 0; i < length; i++) {
                long av = a.get(aOff + i);
                long bv = b.get(bOff + i);
                if (av != bv)
                    return i;
            }
            return -1;
        }

        static int mismatch(FloatBuffer a, int aOff, FloatBuffer b, int bOff, int length) {
            for (int i = 0; i < length; i++) {
                float av = a.get(aOff + i);
                float bv = b.get(bOff + i);
                if (av != bv && (!Float.isNaN(av) || !Float.isNaN(bv)))
                    return i;
            }
            return -1;
        }

        static int mismatch(DoubleBuffer a, int aOff, DoubleBuffer b, int bOff, int length) {
            for (int i = 0; i < length; i++) {
                double av = a.get(aOff + i);
                double bv = b.get(bOff + i);
                if (av != bv && (!Double.isNaN(av) || !Double.isNaN(bv)))
                    return i;
            }
            return -1;
        }
    }

    @For(Double.class)
    private static class DoubleImpl {

        @Override
        @SuppressWarnings("ConstantConditions")
        public boolean equals(Object o) {
            double a = Unsafe2.reinterpretCast(this, Double.class);
            return o instanceof Double b && (a == b || Double.isNaN(a) && b.isNaN());
        }

        @SuppressWarnings("UseCompareMethod")
        public static int compare(double a, double b) {
            if (Double.isNaN(a))
                return Double.isNaN(b) ? 0 : 1;
            if (Double.isNaN(b))
                return -1;
            if (isPosZero(a) && isNegZero(b))
                return 1;
            if (isNegZero(a) && isPosZero(b))
                return -1;
            return a < b ? -1 : a > b ? 1 : 0;
        }

        @Ignore
        private static boolean isPosZero(double d) {
            return (1 / d) == Double.POSITIVE_INFINITY;
        }

        @Ignore
        private static boolean isNegZero(double d) {
            return (1 / d) == Double.NEGATIVE_INFINITY;
        }

        public Optional<? extends ConstantDesc> describeConstable() {
            return Optional.empty();
        }

        @Snippet("(''+$0)")
        public static native String toString(double f);

        @Snippet("parseFloat($0)")
        public static native double parseDouble(String s);

        // ez azért került, mert recordok hashCodejának (pl. Color) kellett.
        // viszont ott pont fölösleges használni, lehetne valamilyen hashcodeot double-ből máshogy is számolni, ami
        // JS-ben is kényelmesen megvalósítható.
        @Snippet("DoubleToIEEE($0)")
        public static native long doubleToRawLongBits(double d);
    }

    @For(Float.class)
    private static class FloatImpl {

        @Override
        @SuppressWarnings("ConstantConditions")
        public boolean equals(Object o) {
            float a = (Float) (Object) this;
            return o instanceof Float b && (a == b || Float.isNaN(a) && b.isNaN());
        }

        @SuppressWarnings("UseCompareMethod")
        public static int compare(float a, float b) {
            if (Float.isNaN(a))
                return Float.isNaN(b) ? 0 : 1;
            if (Float.isNaN(b))
                return -1;
            if (a == +0.0f && b == -0.0f)
                return 1;
            if (a == -0.0f && b == +0.0f)
                return -1;
            return a < b ? -1 : a > b ? 1 : 0;
        }

        public Optional<? extends ConstantDesc> describeConstable() {
            return Optional.empty();
        }

        @Snippet("(''+$0)")
        public static native String toString(float f);

        @Snippet("parseFloat($0)")
        public static native float parseFloat(String s);
    }

    @For(java.security.AccessController.class)
    @SuppressWarnings("removal")
    private static class AccessControllerImpl {

        static java.security.AccessControlContext getStackAccessControlContext() {
            return null;
        }

        static boolean isPrivileged() {
            return true;
        }

        static void ensureMaterializedForStackWalk(Object obj) {
        }
    }

    @For(DateFormatSymbols.class)
    private static class DateFormatSymbolsImpl {

        @SuppressWarnings("ConstantConditions")
        boolean isSubclassObject() {
            // érthetetlen okból getClass()getName().equals-zel hasonlítja össze a JDK-beli implementáció
            // mondjuk meg lehetne csinálni Optimizerben, hogy eliminálja a névösszehasonlítást és
            // sima típusegyezőséget vizsgáljon.
            return ((Class<?>) getClass()) == DateFormatSymbols.class;
        }
    }

    @For(ProtectionDomain.class)
    static class ProtectionDomainImpl {
        @Override
        public String toString() { // a default implementáció valami policy fájlt akarna betölteni
            return "ProtectionDomain";
        }
    }

    @For(PermissionCollection.class)
    static class PermissionCollectionImpl {

        // java.net.URL-eket próbálna megnyitni a toStringje

        @Override
        public String toString() {
            return "PermissionCollection";
        }
    }

    @ForClass("sun/security/util/LazyCodeSourcePermissionCollection")
    static class LazyCodeSourcePermissionCollectionImpl {
        @Override
        public String toString() {
            return "LazyCodeSourcePermissionCollection";
        }
    }

    @ForClass("java/lang/invoke/Invokers")
    static class InvokersImpl {
        static void checkCustomized(MethodHandle mh) {
            // nem tudunk JS-ben bytecode-ot generálni, ezért le kell tiltanunk az MH customize-olást.
            // Ezt megtehetnénk java.lang.invoke.MethodHandle.CUSTOMIZE_THRESHOLD system property
            // -1-re állításával is, de az nem működik, mert Invokers$Holder-ben jlink-időben generált
            // metódusok vannak, amik nem tudják figyelembe venni.
        }
    }

    @ForClass("jdk/internal/module/ModulePatcher")
    static class ModulePatcherImpl {
        ModuleReference patchIfNeeded(ModuleReference ref) {
            // muszáj felülírni, mert a benne lévő lambda megpróbálódna lefordulni
            // mivel implementál standard funkciónális interface-t, és eljutna jimage natív kódokig
            throw new UnsupportedOperationException();
        }
    }

    @ForClass("jdk/internal/loader/BuiltinClassLoader$LoadedModule")
    static class LoadedModule {
    }

    @ForClass("jdk/internal/loader/BuiltinClassLoader")
    static class BuiltinClassLoader {
        Class<?> defineClass(String cn, LoadedModule loadedModule) {
            throw new UnsupportedOperationException();
        }
    }

    @ForClass("java/util/stream/AbstractPipeline")
    static class AbstractPipeline {

        @MustBeInlined
        boolean isParallel() {
            // különben AbstractPipeline.evaluate betöltene mindenféle ForkJoinPoolos hülyeséget
            return false;
        }
    }

    @For(value = Pattern.class, replace = true)
    static class PatternImpl {

        private Object r;

        @Ignore
        public PatternImpl() {
        }

        static PatternImpl compile(String s) {
            PatternImpl p = new PatternImpl();
            p.r = compileImpl(s);
            return p;
        }


        static PatternImpl compile(String s, int flags) {
            String flagsString = "";
            if ((flags & Pattern.CASE_INSENSITIVE) == Pattern.CASE_INSENSITIVE) {
                flags &= ~Pattern.CASE_INSENSITIVE;
                flagsString += "i";
            }
            if ((flags & Pattern.MULTILINE) == Pattern.MULTILINE) {
                flags &= ~Pattern.MULTILINE;
                flagsString += "m";
            }
            if ((flags & Pattern.DOTALL) == Pattern.DOTALL) {
                flags &= ~Pattern.DOTALL;
                flagsString += "s";
            }
            if ((flags & (Pattern.UNICODE_CASE | Pattern.UNICODE_CHARACTER_CLASS)) == (Pattern.UNICODE_CASE | Pattern.UNICODE_CHARACTER_CLASS)) {
                flags &= ~(Pattern.UNICODE_CASE | Pattern.UNICODE_CHARACTER_CLASS);
                flagsString += "u";
            }
            if (flags != 0)
                throw new RuntimeException("unsupported regex flags: " + flags);

            PatternImpl p = new PatternImpl();
            p.r = compileImpl(s, flagsString);
            return p;
        }

        @Snippet("$0.replace(/[.?*+^$[\\]\\\\(){}|-]/g, \"\\\\$&\")")
        static native String quote(String s);

        @Snippet("new RegExp($0)")
        @Ignore
        private static native Object compileImpl(String s);

        @Snippet("new RegExp($0)")
        @Ignore
        private static native Object compileImpl(String s, String flags);

        MatcherImpl matcher(CharSequence s) {
            return new MatcherImpl(r, s.toString());
        }

        @Override
        public String toString() {
            return regexToString(r);
        }

        @Ignore
        @Snippet("(''+$0)")
        private static native String regexToString(Object regex);
    }

    @For(value = Matcher.class, replace = true)
    static class MatcherImpl {

        private final Object regexp;
        private final String s;
        private String[] groups;
        private int start;
        private boolean tried;

        @Ignore
        MatcherImpl(Object regexp, String s) {
            this.regexp = regexp;
            this.s = s;

            Object obj = matchImpl(s, regexp);
            if (obj != null) {
                groups = Unsafe2.setType(obj, String[].class);
            } else
                groups = null;
        }

        public boolean matches() {
            return groups != null;
        }

        public String group(int i) {
            return groups[i];
        }

        public boolean find() {
            if (tried)
                if (groups == null)
                    return false;
                else
                    return find(end());
            else {
                tried = true;
                return find(0);
            }
        }

        public boolean find(int i) {
            tried = true;
            start = i;
            Object obj = matchImpl(s.substring(i), regexp);
            if (obj != null) {
                groups = Unsafe2.setType(obj, String[].class);
            } else
                groups = null;
            return groups != null;
        }

        public int start() {
            if (groups == null)
                throw new IllegalStateException("No match available");
            return start;
        }

        public int end() {
            if (groups == null)
                throw new IllegalStateException("No match available");
            return start + groups[0].length();
        }

        @Ignore
        @Snippet("$0.match($1)")
        private static native Object matchImpl(String s, Object regexp);

        @Override
        public String toString() {
            // javadoc szerint a formátum unspecified

            return groups == null ? "no match" : Arrays.toString(groups);
        }
    }

    @For(Unsafe2.class)
    static class Unsafe2Impl {

        @Snippet("new ($0)()")
        static native <T> T allocateInstance(Class<T> type);

        @Snippet("setTypeImpl($0, $1)")
        private static native void setTypeImpl(Object o, Class<?> type);

        @Snippet("fname($1).fieldGetter($0)")
        @DontInline // különben nem működne Optimizer2-ben a helyettesítése GetFieldNode-dal
        public static native Object getInstanceField(Object obj, int fieldID);

        // nem jó a field sorrend, mert JSEmitter így kevesebb eséllyel inlineol
        @Snippet("fname($1).fieldSetter($0, $2)")
        public static native void putInstanceField(Object obj, int fieldID, Object value);

        @Snippet("fname($0).fieldGetter(window)")
        public static native Object getStaticField(int fieldID);

        @Snippet("fname($0).asStaticMethod.apply(null, $1)")
        public static native Object invokeStatic(int methodID, Object[] args);

        @Snippet("fname($0).asStaticMethod.apply($1, $2)")
        public static native Object invokeSpecial(int methodID, Object receiver, Object[] args);

        @Snippet("fname($0).asVirtualMethod($1).apply($1, $2)")
        public static native Object invokeVirtual(int methodID, Object receiver, Object[] args);

        @Snippet("$0['wt' + $1.typeName]")
        public static native boolean canBeWidenedTo(Class<?> type, Class<?> to);

        @Snippet("true")
        static native Object nativeTrue();

        @Snippet("false")
        static native Object nativeFalse();

        @Snippet("window[$0]")
        static native Object loadNativeObject(String n);

        @Snippet("globalObjAs($0)")
        static native Object globalObjectAsImpl(Class<?> a);

        @Snippet("(''+$0)")
        static native String nativeToString(Object obj);

        @Snippet("($0.refSize|1)")
        static native int refSize(Class<?> c);

        @MustBeInlined // hogy nativeTypeName() konstansként tudja megkapni a típust
        static boolean checkCast_fastpath(Object obj, Class<?> clazz) {
            return checkCastFastpathImpl(obj, Unsafe2.typeName(clazz));
        }

        // az &&$0.t azért kellett, mert nem sikerült checkCast_fastpath-ba
        // belerakni a JSObject/JSArray kezelést, nem-konstanssá vált a class
        @Ignore
        @Snippet("($0 !== null && $0.t && !$0.t[$1])")
        private static native boolean checkCastFastpathImpl(Object obj, String typeName);

        @MustBeInlined // hogy nativeTypeName() konstansként tudja megkapni a típust
        static boolean instanceOf(Object obj, Class<?> clazz) {
            if (clazz == JSArrayImpl.class)
                return jsValueIsArray(obj);
            if (clazz == JSObjectImpl.class)
                return jsValueIsObject(obj);

            assert Unsafe2.asJSValue(obj) != JSValue.Undefined.UNDEFINED;
            return instanceOfImpl(obj, Unsafe2.typeName(clazz));
        }

        @Ignore
        @Snippet("($0 !== null && $0.t[$1])")
        private static native boolean instanceOfImpl(Object obj, String typeName);

        static boolean checkCast_platformSpecific(Object obj, Class<?> clazz) {
            if (clazz == JSObjectImpl.class) {
                if (jsValueIsObject(obj))
                    return true;
                else
                    throw Unsafe2.cce(obj, clazz);
            }
            if (clazz == JSArrayImpl.class) {
                if (jsValueIsArray(obj))
                    return true;
                else
                    throw Unsafe2.cce(obj, clazz);
            }
            return false;
        }

        static boolean tryAddIncomingUntypedValueType(ClassImpl supertype, ClassImpl t) {
            if (ClassImpl.isIncomingUntypedValueType(t)) {
                t.addSupertype(supertype);
                // ide lehetne egy assert hogy ezután isAssignableFromImpl tényleg true-t adna-e vissza
                return true;
            }
            return false;
        }

        @Snippet("(typeof ($0) == 'object')")
        static native boolean jsValueIsObject(Object obj);

        @Snippet("Array.isArray($0)")
        @Ignore
        static native boolean jsValueIsArray(Object obj);
    }

    /* TODO
    @For(PropertyDefinition.class)
    static class PropertyDefinitionImpl {
        @Override
        public String toString() {
            return "PropertyDefinition";
        }
    }
     */

    @ForClass("sun/reflect/annotation/AnnotationInvocationHandler")
    static class AnnotationInvocationHandlerImpl {

        Boolean equalsImpl(Object proxy, Object o) {
            // beépített implementáció kénytelen reflectionnel hívogatni az o függvényeit, de az nekünk nem jó

            throw new UnsupportedOperationException();
        }
    }

    @ForClass("java/lang/reflect/Parameter")
    static class ParameterImpl {

        @Override
        public String toString() {
            throw new UnsupportedOperationException();
        }

        Annotation[] getAnnotations() {
            throw new UnsupportedOperationException();
        }

        Annotation[] getDeclaredAnnotations() {
            throw new UnsupportedOperationException();
        }
    }

    @For(Byte.class)
    static class ByteImpl {

        public Optional<? extends ConstantDesc> describeConstable() {
            return Optional.empty();
        }

        @SuppressWarnings({"removal", "UnnecessaryBoxing", "CachedNumberConstructorCall"})
        public static Byte valueOf(byte ch) {
            return new Byte(ch);
        }
    }


    @For(Boolean.class)
    static class BooleanImpl {

        public Optional<? extends ConstantDesc> describeConstable() {
            return Optional.empty();
        }
    }

    @For(Character.class)
    static class CharacterImpl {

        public Optional<? extends ConstantDesc> describeConstable() {
            return Optional.empty();
        }

        @SuppressWarnings({"removal", "UnnecessaryBoxing"})
        public static Character valueOf(char ch) {
            return new Character(ch);
        }
    }

    @For(Short.class)
    static class ShortImpl {

        public Optional<? extends ConstantDesc> describeConstable() {
            return Optional.empty();
        }

        @SuppressWarnings({"removal", "UnnecessaryBoxing", "CachedNumberConstructorCall"})
        public static Short valueOf(short ch) {
            return new Short(ch);
        }
    }


    @For(Enum.class)
    static class EnumImpl {

        public Optional<? extends ConstantDesc> describeConstable() {
            return Optional.empty();
        }
    }

    @For(java.lang.invoke.MethodType.class)
    static class MethodTypeImpl {

        public Optional<? extends ConstantDesc> describeConstable() {
            return Optional.empty();
        }

        @Override
        public String toString() {
            return super.toString();
        }
    }

    @For(value = ClassValue.class, replace = true)
    static class ClassValueImpl {

        @DontInline
        public Object get(Class<?> c) {
            return Unsafe2.compilationFailure();
        }
    }

    // LF-ben tele van felesleges dolgokkal, nekünk viszont csak a vmentry kell belőle
    @ForClass(value = "java/lang/invoke/LambdaForm", replace = true)
    static class LambdaFormImpl {

        MemberNameImpl vmentry;

        @Ignore
        public LambdaFormImpl() {
        }

        @Override
        public boolean equals(Object obj) {
            return this == obj;
        }

        @Override
        public int hashCode() {
            return super.hashCode();
        }

        @Override
        public String toString() {
            return super.toString();
        }
    }

    @SuppressWarnings("unchecked")
    @ForClass(value = "java/util/ArrayList", replace = true)
    static class ArrayListImpl<E> implements List<E> {

        // Erre a két fieldre hivatkozik Emitter.convertArrayList is
        // ezért ez legyen az első field ebben az osztályban
        private final Object[] array;
        private final int type;

        @Ignore
        static final int MUTABLE_VAR_SIZE = 0;
        @Ignore
        static final int MUTABLE_FIXED_SIZE = 1;
        @Ignore
        static final int EMPTY = 2;
        @Ignore
        static final int IMMUTABLE = 3;

        public ArrayListImpl() {
            this(0);
        }

        public ArrayListImpl(Collection<? extends E> coll) {
            this(coll.toArray().clone(), MUTABLE_VAR_SIZE);
        }

        public ArrayListImpl(int initialCapacity) {
            this(new Object[0], MUTABLE_VAR_SIZE);
            if (initialCapacity < 0)
                throw new IllegalArgumentException();
        }

        @Ignore
        ArrayListImpl(Object[] array, int type) {
            this.array = array;
            this.type = type;
        }

        @Override
        public int size() {
            return array.length;
        }

        @Override
        public boolean isEmpty() {
            return array.length == 0;
        }

        @Override
        public boolean contains(Object o) {
            for (int i = 0; i < array.length; i++)
                if (Objects.equals(o, array[i]))
                    return true;
            return false;
        }

        @Override
        @Ignore
        public boolean containsAll(Collection<?> c) {
            for (Object o : c)
                if (!contains(o))
                    return false;
            return true;
        }

        @Override
        public Iterator<E> iterator() {
            // csináljunk konkurrens módosítás detektálást?

            Objects.requireNonNull(array);
            return new Iterator<E>() {

                private int i;
                private boolean removable;

                @Override
                public boolean hasNext() {
                    return i < array.length;
                }

                @SuppressWarnings("unchecked")
                @Override
                public E next() {
                    removable = true;
                    return (E) array[i++];
                }

                @Override
                public void remove() {
                    if (removable) {
                        ArrayListImpl.this.remove(--i);
                        removable = false;
                    } else
                        throw new IllegalStateException();
                }
            };
        }

        @Override
        public Object[] toArray() {
            return Arrays.copyOf(array, array.length);
        }

        @SuppressWarnings({"unchecked", "ReassignedVariable"})
        @Override
        public <T> T[] toArray(T[] a) {
            if (array.length > a.length) {
                a = (T[]) Array.newInstance(a.getClass().componentType(), array.length);
            }

            System.arraycopy(array, 0, a, 0, array.length);
            return a;
        }

        @Override
        public boolean add(E e) {
            if (type != MUTABLE_VAR_SIZE)
                throw new UnsupportedOperationException();
            addImpl(array, e);
            return true;
        }

        @Snippet("$0.push($1)")
        private static native void addImpl(Object[] array, Object e);

        @Override
        public boolean remove(Object o) {
            int i = indexOf(o);
            if (type == IMMUTABLE || i != -1 && type != MUTABLE_VAR_SIZE)
                throw new UnsupportedOperationException();
            if (i == -1)
                return false;
            remove(i);
            return true;
        }

        @Override
        public boolean addAll(Collection<? extends E> c) {
            if (type == IMMUTABLE || !c.isEmpty() && type != MUTABLE_VAR_SIZE)
                throw new UnsupportedOperationException();
            boolean changed = false;
            for (E e : c) {
                changed = true;
                add(e);
            }
            return changed;
        }

        @Override
        public boolean addAll(int index, Collection<? extends E> c) {
            if (type == IMMUTABLE || !c.isEmpty() && type != MUTABLE_VAR_SIZE)
                throw new UnsupportedOperationException();
            int i = index;
            for (E e : c) {
                i++;
                add(i, e);
            }
            return i != index;
        }

        @Override
        public boolean removeAll(Collection<?> c) {
            boolean anyRemoved = false;
            for (Object o : c)
                anyRemoved |= remove(o);
            return anyRemoved;
        }

        @Override
        public boolean retainAll(Collection<?> c) {
            return removeIf(e -> !c.contains(e));
        }

        @Override
        public void clear() {
            if (type != MUTABLE_VAR_SIZE)
                throw new UnsupportedOperationException();
            removeAllFrom(array, 0);
        }

        @Snippet("$0.splice($1)")
        private static native void removeAllFrom(Object[] o, int begin);

        @Snippet("$0.splice($1, $2)")
        private static native void removeRangeImpl(Object[] o, int begin, int count);

        protected void removeRange(int fromIndex, int toIndex) {
            if (type != MUTABLE_VAR_SIZE)
                // ld. komment lejjebb
                throw new AssertionError();

            if (fromIndex > toIndex || fromIndex < 0 || toIndex > array.length)
                throw new IndexOutOfBoundsException();
            removeRangeImpl(array, fromIndex, toIndex - fromIndex);
        }

        public void ensureCapacity(int i) {
            if (type != MUTABLE_VAR_SIZE)
                throw new AssertionError(); // assertet nem használhatunk itt, mert nincs $assertionsDisabled field j.u.ArrayList-ben

            // nothing to do
        }

        @Override
        public E get(int index) {
            if (index < 0 || index >= array.length)
                throw new IndexOutOfBoundsException();
            return (E) array[index];
        }

        @SuppressWarnings("unchecked")
        @Override
        public E set(int index, E element) {
            if (type > 1)
                throw new UnsupportedOperationException();
            if (index < 0 || index >= array.length)
                throw new IndexOutOfBoundsException();
            E prev = (E) array[index];
            array[index] = element;
            return prev;
        }

        @Override
        public void add(int index, E element) {
            if (type != MUTABLE_VAR_SIZE)
                throw new UnsupportedOperationException();
            checkInsertionIndex(index);
            insert(array, index, element);
        }

        @Ignore
        private void checkInsertionIndex(int index) {
            if (index < 0 || index > array.length)
                throw new IndexOutOfBoundsException();
        }

        @Snippet("$0.splice($1, 0, $2)")
        private static native void insert(Object[] array, int i, Object elem);

        @Override
        public E remove(int index) {
            if (type != MUTABLE_VAR_SIZE)
                throw new UnsupportedOperationException();
            E e = (E) array[index];
            removeRangeImpl(array, index, 1);
            return e;
        }

        @Override
        public int indexOf(Object o) {
            for (int i = 0; i < array.length; i++) {
                if (Objects.equals(array[i], o))
                    return i;
            }
            return -1;
        }

        @Override
        public int lastIndexOf(Object o) {
            for (int i = array.length - 1; i >= 0; i--) {
                if (Objects.equals(array[i], o))
                    return i;
            }
            return -1;
        }

        @Override
        public ListIterator<E> listIterator() {
            return listIterator(0);
        }

        @Override
        public ListIterator<E> listIterator(int beginIndex) {
            return new ListIterator<E>() {

                private int cursor = beginIndex;

                private byte prevOrNext = -2;

                @Override
                public boolean hasNext() {
                    return cursor < array.length;
                }

                @Override
                public E next() {
                    if (!hasNext())
                        throw new NoSuchElementException();
                    prevOrNext = -1;
                    return (E) array[cursor++];
                }

                @Override
                public boolean hasPrevious() {
                    return cursor > 0;
                }

                @Override
                public E previous() {
                    if (!hasPrevious())
                        throw new NoSuchElementException();
                    prevOrNext = 0;
                    return (E) array[--cursor];
                }

                @Override
                public int nextIndex() {
                    return cursor;
                }

                @Override
                public int previousIndex() {
                    return cursor - 1;
                }

                @Override
                public void remove() {
                    if (prevOrNext == -2)
                        throw new IllegalStateException();
                    ArrayListImpl.this.remove(cursor + prevOrNext);
                    prevOrNext = -2;
                }

                @Override
                public void set(E e) {
                    if (prevOrNext == -2)
                        throw new IllegalStateException();
                    ArrayListImpl.this.set(cursor + prevOrNext, e);
                    prevOrNext = -2;
                }

                @Override
                public void add(E e) {
                    ArrayListImpl.this.add(cursor, e);
                }
            };
        }

        @Override
        public List<E> subList(int fromIndex, int toIndex) {
            return new SubList<>(this, fromIndex, toIndex);
        }

        @Override
        public void sort(Comparator<? super E> c) {
            if (type == IMMUTABLE || type == EMPTY && !isEmpty())
                throw new UnsupportedOperationException();
            List.super.sort(c); // TODO
        }

        @Override
        public Spliterator<E> spliterator() {
            return Spliterators.spliterator(array, 0);
        }

        @Override
        public void forEach(Consumer<? super E> action) {
            for (int i = 0; i < array.length; i++)
                action.accept((E) array[i]);
        }

        @Override
        public boolean removeIf(Predicate<? super E> filter) {
            boolean removedAny = false;
            for (int i = 0; i < array.length; i++) {
                if (filter.test((E) array[i])) {
                    remove(i); // ez végzi unmodifiable ellenőrzéset
                    i--;
                    removedAny = true;
                }
            }
            return removedAny;
        }

        @Override
        public void replaceAll(UnaryOperator<E> operator) {
            if (type == IMMUTABLE || type == EMPTY && !isEmpty())
                throw new UnsupportedOperationException();
            for (int i = 0; i < array.length; i++) {
                array[i] = operator.apply((E) array[i]);
            }
        }

        @Override
        public Object clone() throws CloneNotSupportedException {
            return new ArrayListImpl<>(toArray(), type);
        }

        @Override
        public boolean equals(Object o) {
            if (o == this)
                return true;
            if (!(o instanceof List<?> l))
                return false;

            Iterator<E> e1 = iterator();
            Iterator<?> e2 = l.iterator();
            while (e1.hasNext() && e2.hasNext()) {
                E o1 = e1.next();
                Object o2 = e2.next();
                if (!Objects.equals(o1, o2))
                    return false;
            }
            return !(e1.hasNext() || e2.hasNext());
        }

        @Override
        public int hashCode() {
            int hashCode = 1;
            for (E e : this)
                hashCode = 31 * hashCode + (e == null ? 0 : e.hashCode());
            return hashCode;
        }

        /*
        @Override
        public String toString() {
            Iterator<E> it = iterator();
            if (!it.hasNext())
                return "[]";

            StringBuilder sb = new StringBuilder();
            sb.append('[');
            for (; ; ) {
                E e = it.next();
                sb.append(e == this ? "(this Collection)" : e);
                if (!it.hasNext())
                    return sb.append(']').toString();
                sb.append(',').append(' ');
            }
        }
        */

        // java.util.AbstractListből másolva, modCountot törölve
        // inkább ki kéne egészíteni ArrayListImplet egy offset és length mezővel
        private static class SubList<E> extends AbstractList<E> {
            private final ArrayListImpl<E> root;
            private final SubList<E> parent;
            private final int offset;
            protected int size;

            /**
             * Constructs a sublist of an arbitrary AbstractList, which is not a SubList itself.
             */
            public SubList(ArrayListImpl<E> root, int fromIndex, int toIndex) {
                this.root = root;
                this.parent = null;
                this.offset = fromIndex;
                this.size = toIndex - fromIndex;
            }

            /**
             * Constructs a sublist of another SubList.
             */
            protected SubList(SubList<E> parent, int fromIndex, int toIndex) {
                this.root = parent.root;
                this.parent = parent;
                this.offset = parent.offset + fromIndex;
                this.size = toIndex - fromIndex;
            }

            @Override
            public boolean isEmpty() {
                return size == 0;
            }

            public E set(int index, E element) {
                Objects.checkIndex(index, size);
                return root.set(offset + index, element);
            }

            public E get(int index) {
                Objects.checkIndex(index, size);
                return root.get(offset + index);
            }

            public int size() {
                return size;
            }

            public void add(int index, E element) {
                rangeCheckForAdd(index);
                root.add(offset + index, element);
                updateSizeAndModCount(1);
            }

            public E remove(int index) {
                Objects.checkIndex(index, size);
                E result = root.remove(offset + index);
                updateSizeAndModCount(-1);
                return result;
            }

            protected void removeRange(int fromIndex, int toIndex) {
                root.removeRange(offset + fromIndex, offset + toIndex);
                updateSizeAndModCount(fromIndex - toIndex);
            }

            public boolean addAll(Collection<? extends E> c) {
                return addAll(size, c);
            }

            public boolean addAll(int index, Collection<? extends E> c) {
                rangeCheckForAdd(index);
                int cSize = c.size();
                if (cSize == 0)
                    return false;
                root.addAll(offset + index, c);
                updateSizeAndModCount(cSize);
                return true;
            }

            public Iterator<E> iterator() {
                return listIterator();
            }

            public ListIterator<E> listIterator(int index) {
                rangeCheckForAdd(index);

                return new ListIterator<E>() {
                    private final ListIterator<E> i =
                            root.listIterator(offset + index);

                    public boolean hasNext() {
                        return nextIndex() < size;
                    }

                    public E next() {
                        if (hasNext())
                            return i.next();
                        else
                            throw new NoSuchElementException();
                    }

                    public boolean hasPrevious() {
                        return previousIndex() >= 0;
                    }

                    public E previous() {
                        if (hasPrevious())
                            return i.previous();
                        else
                            throw new NoSuchElementException();
                    }

                    public int nextIndex() {
                        return i.nextIndex() - offset;
                    }

                    public int previousIndex() {
                        return i.previousIndex() - offset;
                    }

                    public void remove() {
                        i.remove();
                        updateSizeAndModCount(-1);
                    }

                    public void set(E e) {
                        i.set(e);
                    }

                    public void add(E e) {
                        i.add(e);
                        updateSizeAndModCount(1);
                    }
                };
            }

            public List<E> subList(int fromIndex, int toIndex) {
                return new SubList<>(this, fromIndex, toIndex);
            }

            private void rangeCheckForAdd(int index) {
                if (index < 0 || index > size)
                    throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
            }

            private String outOfBoundsMsg(int index) {
                return "Index: " + index + ", Size: " + size;
            }

            private void updateSizeAndModCount(int sizeChange) {
                SubList<E> slist = this;
                do {
                    slist.size += sizeChange;
                    slist = slist.parent;
                } while (slist != null);
            }
        }
    }

    @For(PrintStream.class)
    public class PrintStreamImpl {

        // itt rögtön nem lehet @Snippetet használni, mert szerializálná a PrintStreamet
        // 63k -> 59k
        void writeln(String s) {
            writelnImpl(s);
        }

        // valamiért berakja "String {" és "}" közé és legvágja a szöveg egy részét, ha nem írom oda az üres stringgel összefűzést
        @Snippet("console.log(''+$0)")
        @Ignore
        static native void writelnImpl(String s);
    }

    @ForClass("java/util/stream/Node")
    public static class StreamNodeImpl<T> {
    }

    @ForClass("java/util/stream/Nodes")
    public static class StreamNodesImpl {

        public static <T> StreamNodeImpl<T> flatten(StreamNodeImpl<T> node, IntFunction<T[]> generator) {
            // parallel streamet nem támogatunk

            return node;
        }
    }

    @For(value = StreamSupport.class, replace = true)
    public static class StreamSupportImpl {

        public static <T> Stream<T> stream(Spliterator<T> spliterator, boolean parallel) {
            Objects.requireNonNull(spliterator);
            if (parallel)
                throw new UnsupportedOperationException("parallel streams are not supported");
            return new ReferenceStream<>(null, StreamImpl.SOURCE, spliterator, -1);
        }

        public static <T> Stream<T> stream(Supplier<? extends Spliterator<T>> supplier,
                                           int characteristics,
                                           boolean parallel) {
            Objects.requireNonNull(supplier);
            if (parallel)
                throw new UnsupportedOperationException("parallel streams are not supported");
            throw new UnsupportedOperationException("TODO");
        }

        public static IntStream intStream(Spliterator.OfInt spliterator, boolean parallel) {
            Objects.requireNonNull(spliterator);
            if (parallel)
                throw new UnsupportedOperationException("parallel streams are not supported");
            return new IntStreamImpl(null, StreamImpl.SOURCE, spliterator, -1);
        }

        public static IntStream intStream(Supplier<? extends Spliterator.OfInt> supplier,
                                          int characteristics,
                                          boolean parallel) {
            Objects.requireNonNull(supplier);
            throw new UnsupportedOperationException("TODO");
        }

        public static LongStream longStream(Spliterator.OfLong spliterator,
                                            boolean parallel) {
            Objects.requireNonNull(spliterator);
            return new LongStreamImpl(null, StreamImpl.SOURCE, spliterator, -1);
        }

        public static LongStream longStream(Supplier<? extends Spliterator.OfLong> supplier,
                                            int characteristics,
                                            boolean parallel) {
            Objects.requireNonNull(supplier);
            throw new UnsupportedOperationException("TODO");
        }

        public static DoubleStream doubleStream(Spliterator.OfDouble spliterator,
                                                boolean parallel) {
            Objects.requireNonNull(spliterator);
            return new DoubleStreamImpl(null, StreamImpl.SOURCE, spliterator, -1);
        }

        public static DoubleStream doubleStream(Supplier<? extends OfDouble> supplier,
                                                int characteristics,
                                                boolean parallel) {
            Objects.requireNonNull(supplier);

            throw new UnsupportedOperationException("TODO");
        }
    }

    @ForClass("jdk/internal/misc/VM")
    static class InternalMiscVMImpl {


        static long getNanoTimeAdjustment(long l) {
            if (Unsafe2.timeOffsetMS == -1)
                Unsafe2.timeOffsetMS = System.currentTimeMillis() - perfNow();

            double relative = Unsafe2.timeOffsetMS + perfNow() - (double) l * 1000;
            return (long) (relative * 1_000_000);
        }

        @Snippet("performance.now()")
        @Ignore
        private static native double perfNow();
    }

    @For(SoftReference.class)
    static class SoftReferenceImpl {
        public Object get() {
            return Unsafe2.getInstanceField(this, Unsafe2.fieldID(Reference.class, "referent", Object.class));
        }
    }

    @For(Arrays.class)
    static class ArraysImpl {

        public static void sort(int[] array) {
            sort(array, 0, array.length);
        }

        public static void sort(int[] array, int from, int to) {
            Object[] o = Unsafe2.reinterpretCast(array, Object[].class);
            Arrays.sort(o, from, to, Comparator.comparingLong(Unsafe2::reinterpretAsInt));
        }

        public static void sort(long[] array) {
            sort(array, 0, array.length);
        }

        public static void sort(long[] array, int from, int to) {
            Object[] o = Unsafe2.reinterpretCast(array, Object[].class);
            Arrays.sort(o, from, to, Comparator.comparingLong(Unsafe2::reinterpretAsLong));
        }

        public static void sort(double[] array) {
            sort(array, 0, array.length);
        }

        public static void sort(double[] array, int from, int to) {
            Object[] o = Unsafe2.reinterpretCast(array, Object[].class);
            Arrays.sort(o, from, to, Comparator.comparingDouble(Unsafe2::reinterpretAsDouble));
        }

        // beépített Arrays.equals vectorizedMismatchot használ, ami 1000 sorra fordult le, mondjuk részben
        // a rossz inlineolási heurisztikák miatt. de ha jó lenne az inlineolás, akkor is nagyon hosszú.

        public static boolean equals(boolean[] a, boolean[] b) {
            return primArrayEquals(Unsafe2.reinterpretCast(a, Object[].class), Unsafe2.reinterpretCast(b, Object[].class));
        }

        public static boolean equals(byte[] a, byte[] b) {
            return primArrayEquals(Unsafe2.reinterpretCast(a, Object[].class), Unsafe2.reinterpretCast(b, Object[].class));
        }

        public static boolean equals(short[] a, short[] b) {
            return primArrayEquals(Unsafe2.reinterpretCast(a, Object[].class), Unsafe2.reinterpretCast(b, Object[].class));
        }

        public static boolean equals(char[] a, char[] b) {
            return primArrayEquals(Unsafe2.reinterpretCast(a, Object[].class), Unsafe2.reinterpretCast(b, Object[].class));
        }

        public static boolean equals(int[] a, int[] b) {
            return primArrayEquals(Unsafe2.reinterpretCast(a, Object[].class), Unsafe2.reinterpretCast(b, Object[].class));
        }

        @Ignore
        private static boolean primArrayEquals(Object[] a, Object[] b) {
            if (a.length != b.length)
                return false;
            for (int i = 0; i < a.length; i++)
                if (a[i] != b[i])
                    return false;
            return true;
        }

        public static boolean equals(long[] a, long[] b) {
            if (a.length != b.length)
                return false;
            for (int i = 0; i < a.length; i++)
                if (a[i] != b[i])
                    return false;
            return true;
        }

        // TODO equals floatra, doublere

        @SafeVarargs
        public static <T> List<T> asList(T... a) {
            return new ArrayListImpl<>(a, ArrayListImpl.MUTABLE_FIXED_SIZE);
        }
    }

    @For(value = ServiceLoader.class, replace = true)
    static class ServiceLoaderImpl<S> implements Iterable<S> {

        private final Class<S> c;
        // ha ez true, akkor obj != null && providers == null. ha false, akkor fordítva.
        private final boolean isNativeInterface;
        private final S obj;
        private final List<ServiceLoader.Provider<S>> providers;

        @Ignore
        private ServiceLoaderImpl(Class<S> c, S obj) {
            this.c = c;
            this.isNativeInterface = true;
            this.obj = obj;
            this.providers = null;
        }

        @Ignore
        private ServiceLoaderImpl(Class<S> c, List<ServiceLoader.Provider<S>> providers) {
            this.c = c;
            this.isNativeInterface = false;
            this.obj = null;
            this.providers = providers;
        }

        @MustBeInlined
        static <S> ServiceLoaderImpl<S> load(Class<S> c) {
            if (ServiceLoaderHelper.isNativeInterface(c))
                // ezt a globalObjectAst lecseréli JSTransformer, ha @Statics-os interfaceről
                // van szó. jobb lenne ezt is inkább @EvaluateCompileType-os iffel csinálni.
                return new ServiceLoaderImpl<>(c, Unsafe2.globalObjectAs(c));
            else
                return ServiceLoaderHelper.loadServices(c);
        }

        @SuppressWarnings("DataFlowIssue")
        @Inline
        public Optional<S> findFirst() {
            if (isNativeInterface)
                return Optional.ofNullable(obj);
            else
                return providers.stream().findFirst().map(Provider::get);
        }

        @Override
        public String toString() {
            return c.getName();
        }

        @Override
        public Iterator<S> iterator() {
            return stream().map(Provider::get).iterator();
        }

        @SuppressWarnings("DataFlowIssue")
        public Stream<ServiceLoader.Provider<S>> stream() {
            return providers.stream();
        }

        static void fail(Class<?> service, String msg, Throwable cause)
                throws ServiceConfigurationError {
            throw new ServiceConfigurationError(service.getName() + ": " + msg,
                    cause);
        }

        static void fail(Class<?> service, String msg)
                throws ServiceConfigurationError {
            throw new ServiceConfigurationError(service.getName() + ": " + msg);
        }

        static void fail(Class<?> service, URL u, int line, String msg)
                throws ServiceConfigurationError {
            fail(service, u + ":" + line + ": " + msg);
        }
    }

    static class ServiceLoaderHelper {

        @EvaluateCompileType
        static <S> ServiceLoaderImpl<S> loadServices(Class<S> s) {
            ServiceLoader<S> sl = ServiceLoader.load(s);
            List<Provider<S>> services = sl.stream().toList();
            System.out.println("SL "+s+": "+services.stream().map(p->p.type().getName()).toList());
            return new ServiceLoaderImpl<>(s, services);
        }

        @EvaluateCompileType
        static boolean isNativeInterface(Class<?> clazz) {
            return clazz.isAnnotationPresent(com.flyordie.code.jsinterop.Name.class) ||
                    clazz.isAnnotationPresent(com.flyordie.code.jsinterop.Statics.class);
        }
    }

    @ForClass("sun/util/locale/provider/LocaleResources")
    static class LocaleResourcesImpl {
    }

    @For(value = Locale.class, replace = true)
    static class LocaleImpl {

        private final String language, country, variant;

        // ha scriptet és/vagy extensionöket is támogatunk, hashcodeot és constantReplacementet is módosítsuk

        // ebben az osztályban ne használjunk String.toLower/UpperCase(Locale.ROOT)-ot,
        // mert belezavarodik a fordító, mert annak meg kéne Locale illetve Locale.toLanguageTag

        public LocaleImpl(String language, String country, String variant) {
            this.language = Unsafe2.reinterpretCast(language, StringImpl.class).toLowerCase_noLocales();
            this.country = Objects.requireNonNull(country);
            this.variant = Objects.requireNonNull(variant);
        }

        public LocaleImpl(String language, String country) {
            this(language, country, "");
        }

        String getLanguage() {
            return language;
        }

        String getCountry() {
            return country;
        }

        String getVariant() {
            return variant;
        }

        String getDisplayName() {
            // TODO
            return getDisplayLanguage();
        }

        String getDisplayName(Locale inLocale) {
            // TODO
            return getDisplayLanguage(inLocale);
        }

        String lambda$getDisplayName$1(LocaleResourcesImpl lr, Locale locale, String s) {
            throw new UnsupportedOperationException();
        }

        String getDisplayLanguage() {
            return getDisplayProp("language", getLanguage());
        }

        String getDisplayCountry() {
            return getDisplayProp("region", getCountry());
        }

        String getDisplayVariant() {
            return getDisplayProp("variant", getVariant());
        }

        String getDisplayLanguage(Locale inLocale) {
            return getDisplayProp("language", getLanguage(), inLocale.toLanguageTag());
        }

        String getDisplayCountry(Locale inLocale) {
            return getDisplayProp("region", getCountry(), inLocale.toLanguageTag());
        }

        String getDisplayVariant(Locale inLocale) {
            return getDisplayProp("variant", getVariant(), inLocale.toLanguageTag());
        }

        @Snippet("new Intl.DisplayNames([$2],{type:$0}).of($1)")
        @Ignore
        static native String getDisplayProp(String prop, String language, String inLanguage);

        @Snippet("new Intl.DisplayNames(undefined, {type:$0}).of($1)")
        @Ignore
        static native String getDisplayProp(String prop, String language);

        String getDisplayKeyTypeExtensionString(String key, LocaleResourcesImpl lr, Locale il) {
            throw new UnsupportedOperationException();
        }


        @Override
        public String toString() {
            // j.u.Localeből másolva

            boolean l = !getLanguage().isEmpty();
            // boolean s = !getScript().isEmpty();
            boolean r = !getCountry().isEmpty();
            boolean v = !getVariant().isEmpty();
            //boolean e = localeExtensions != null && !localeExtensions.getID().isEmpty();

            StringBuilder result = new StringBuilder(getLanguage());
            if (r || (l && (v/* || s || e*/))) {
                result.append('_').append(getClass()); // This may just append '_'
            }
            if (v && (l || r)) {
                result.append('_').append(getVariant());
            }

            /*
            if (s && (l || r)) {
                result.append("_#")
                        .append(baseLocale.getScript());
            }

            if (e && (l || r)) {
                result.append('_');
                if (!s) {
                    result.append('#');
                }
                result.append(localeExtensions.getID());
            }
             */

            return result.toString();
        }

        @Override
        public int hashCode() {
            int h;
            h = language.hashCode();
            h = 31 * h + "".hashCode(); // script
            h = 31 * h + country.hashCode();
            h = 31 * h + variant.hashCode();
            return h;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            LocaleImpl locale = Unsafe2.reinterpretCast(o, LocaleImpl.class);
            return Objects.equals(language, locale.language) &&
                    Objects.equals(country, locale.country) && Objects.equals(variant, locale.variant);
        }

        @Override
        public Object clone() {
            return new LocaleImpl(language, country, variant);
        }

        public String toLanguageTag() {
            String tag;

            if (language.matches("[a-zA-Z]{2,8}"))
                tag = switch (language) {
                    case "iw" -> "he";
                    case "ji" -> "yi";
                    case "in" -> "id";
                    default -> language; // a konstruktor csinálja a toLowerCaset
                };
            else
                tag = "und";

            if (country.matches("[a-zA-Z]{2}|[0-9]{3}"))
                tag += "-" + Unsafe2.reinterpretCast(country, StringImpl.class).toUpperCase_noLocales();

            // bonyolult variantot ellenőrizni meg canonicalizálni, egyelőre használjuk ami van
            // variant token: "[a-zA-Z0-9]{5,8}|[0-9][a-zA-Z0-9]{3}"
            if (!this.variant.isEmpty())
                tag += "_" + variant;

            return tag;
        }
    }

    @For(Instant.class)
    static class InstantImpl {

        // long->double konverzió miatt ennél távolabbi időpontot nem tud kezelni: +287396-10-12T08:59:00.991Z
        // de úgy látom hogy ettől függetlenül Chrome csak eddig tud dátumokat kezelni: +275760-09-12T23:59:59.999Z
        @Override
        public String toString() {
            Instant t = Unsafe2.reinterpretCast(this, Instant.class);
            return instantToIsoString(t.toEpochMilli());
        }

        @Snippet("new Date($0).toISOString()") // már FF2-ben is van ilyen
        @Ignore
        private static native String instantToIsoString(double ts);
    }

    @For(LocalDate.class)
    static class LocalDateImpl {

        public static LocalDate now() {
            Object d = nativeDate();
            return LocalDate.of(getYear(d), getMonth(d) + 1, getDay(d));
        }

        @Ignore
        @Snippet("new Date()")
        private static native Object nativeDate();

        @Ignore
        @Snippet("$0.getFullYear()")
        private static native int getYear(Object date);

        @Ignore
        @Snippet("$0.getMonth()")
        private static native int getMonth(Object date);

        @Ignore
        @Snippet("$0.getDate()")
        private static native int getDay(Object date);
    }

    @For(Stream.class)
    @SuppressWarnings("SimplifyStreamApiCallChains")
    static class StreamImpl_ {

        static <T> Stream<T> of(T t) {
            return singletonList(t).stream();
        }

        @SafeVarargs
        static <T> Stream<T> of(T... t) {
            return Arrays.asList(t).stream();
        }

        static <T> Stream<T> ofNullable(T t) {
            return t == null ? Stream.empty() : Stream.of(t);
        }
    }

    @For(Executable.class)
    static class ExecutableImpl {

        @SuppressWarnings("rawtypes")
        Map declaredAnnotations() {
            throw new UnsupportedOperationException();
        }
    }

    @For(URLEncoder.class)
    static class URLEncoderImpl {

        @Snippet("encodeURIComponent($0)")
        native static String encode(String a);

        static String encode(String a, Charset charset) {
            if (charset != StandardCharsets.UTF_8)
                throw new UnsupportedOperationException(charset.toString());
            return encode(a);
        }
    }

    @For(Collections.class)
    static class CollectionsImpl {

        @Ignore
        static final Object[] EMPTY_ELEMENT_DATA = new Object[0];

        @Ignore
        static final ArrayListImpl<?> EMPTY_LIST = new ArrayListImpl<>(EMPTY_ELEMENT_DATA, ArrayListImpl.EMPTY);

        @SuppressWarnings("unchecked")
        static <T> List<T> emptyList() {
            return (List<T>) EMPTY_LIST;
        }
    }

    @For(List.class)
    static class ListImpl {

        @Ignore
        private static final List<?> EMPTY_IMMUTABLE_LIST =
                new ArrayListImpl<>(CollectionsImpl.EMPTY_ELEMENT_DATA, ArrayListImpl.IMMUTABLE);

        @SuppressWarnings("unchecked")
        static <E> List<E> of() {
            return (List<E>) EMPTY_IMMUTABLE_LIST;
        }

        static <E> List<E> of(E e1) {
            return new ArrayListImpl<>(new Object[]{e1}, ArrayListImpl.IMMUTABLE);
        }

        static <E> List<E> of(E e1, E e2) {
            return new ArrayListImpl<>(new Object[]{e1, e2}, ArrayListImpl.IMMUTABLE);
        }

        static <E> List<E> of(E e1, E e2, E e3) {
            return new ArrayListImpl<>(new Object[]{e1, e2, e3}, ArrayListImpl.IMMUTABLE);
        }

        static <E> List<E> of(E e1, E e2, E e3, E e4) {
            return new ArrayListImpl<>(new Object[]{e1, e2, e3, e4}, ArrayListImpl.IMMUTABLE);
        }

        static <E> List<E> of(E e1, E e2, E e3, E e4, E e5) {
            return new ArrayListImpl<>(new Object[]{e1, e2, e3, e4, e5}, ArrayListImpl.IMMUTABLE);
        }

        static <E> List<E> of(E e1, E e2, E e3, E e4, E e5, E e6) {
            return new ArrayListImpl<>(new Object[]{e1, e2, e3, e4, e5, e6}, ArrayListImpl.IMMUTABLE);
        }

        static <E> List<E> of(E e1, E e2, E e3, E e4, E e5, E e6, E e7) {
            return new ArrayListImpl<>(new Object[]{e1, e2, e3, e4, e5, e6, e7}, ArrayListImpl.IMMUTABLE);
        }

        static <E> List<E> of(E e1, E e2, E e3, E e4, E e5, E e6, E e7, E e8) {
            return new ArrayListImpl<>(new Object[]{e1, e2, e3, e4, e5, e6, e7, e8}, ArrayListImpl.IMMUTABLE);
        }

        static <E> List<E> of(E e1, E e2, E e3, E e4, E e5, E e6, E e7, E e8, E e9) {
            return new ArrayListImpl<>(new Object[]{e1, e2, e3, e4, e5, e6, e7, e8, e9}, ArrayListImpl.IMMUTABLE);
        }

        static <E> List<E> of(E e1, E e2, E e3, E e4, E e5, E e6, E e7, E e8, E e9, E e10) {
            return new ArrayListImpl<>(new Object[]{e1, e2, e3, e4, e5, e6, e7, e8, e9, e10}, ArrayListImpl.IMMUTABLE);
        }

        static <E> List<E> of(E... elements) {
            return new ArrayListImpl<>(elements.clone(), ArrayListImpl.IMMUTABLE);
        }

        static <E> List<E> copyOf(Collection<? extends E> coll) {
            Object[] a = coll.toArray();
            return new ArrayListImpl<>(a.clone(), ArrayListImpl.IMMUTABLE);
        }
    }

    /*
    @SuppressWarnings("unchecked")
    static abstract class AbstractHashMapImpl<K, V> extends AbstractMap<K, V> {

        private JSObject keys = new JSObject();
        private JSObject values = new JSObject();
        private int size;

        public AbstractHashMapImpl() {
        }

        public AbstractHashMapImpl(Map<? extends K, ? extends V> map) {
            putAll(map);
        }

        public AbstractHashMapImpl(int initialCapacity) {
        }

        public AbstractHashMapImpl(int initialCapacity, float loadFactor) {
        }

        @Override
        public int size() {
            return size;
        }

        @Override
        public boolean isEmpty() {
            return size == 0;
        }

        @Ignore
        private String find(Object key, boolean forRead) {
            String prefix = key.hashCode() + "_";
            String lastIndex;
            int i = 0;
            JSValue foundKey;
            while ((foundKey = keys.get(lastIndex = (prefix + i))) != UNDEFINED) {
                if (Objects.equals(foundKey, key))
                    return prefix + i;
            }
            return forRead ? null : lastIndex;
        }

        @Override
        public boolean containsKey(Object key) {
            return find(key, true) != null;
        }

        @Override
        public V get(Object key) {
            String p = find(key, true);
            return p == null ? null : Unsafe2.asObject(values.get(p));
        }

        @Override
        public V put(K key, V value) {
            String index = find(key, false);
            V prev = Unsafe2.asObject(values.get(index));
            keys.put(index, Unsafe2.asJSValue(key));
            values.put(index, Unsafe2.asJSValue(value));
            if (Unsafe2.isUndefined(prev)) {
                size++;
                return null;
            } else
                return prev;
        }

        @Override
        public V remove(Object key) {
            String index = find(key, true);
            if (index == null)
                return null;
            V prev = Unsafe2.asObject(values.get(index));
            keys.delete(index);
            values.delete(index);
            size--;
            return prev;
        }

        @Override
        public void clear() {
            keys = new JSObject();
            values = new JSObject();
            size = 0;
        }

        @Override
        public Set<Entry<K, V>> entrySet() {
            // TODO ez nincs randomizálva

            return new AbstractSet<>() {
                @Override
                public Iterator<Entry<K, V>> iterator() {
                    return (Iterator<Entry<K, V>>) AbstractHashMapImpl.this.iterator(false);
                }

                @Override
                public int size() {
                    return size;
                }
            };
        }

        @Ignore
        Iterator<?> iterator(boolean onlyKeys) {
            return new Iterator<Object>() {

                private Iterator<String> indexIterator = keys.fieldNames();

                @Override
                public boolean hasNext() {
                    return indexIterator.hasNext();
                }

                @Override
                public Object next() {
                    String next = indexIterator.next();
                    return onlyKeys ? Unsafe2.asObject(keys.get(next)) : new EntryImpl(next);
                }
            };
        }

        private class EntryImpl implements Map.Entry<K, V> {

            private final K key;

            public EntryImpl(String index) {
                this.key = Unsafe2.asObject(keys.get(index));
            }

            @Override
            public K getKey() {
                return key;
            }

            @Override
            public V getValue() {
                return get(key);
            }

            @Override
            public V setValue(V value) {
                return put(key, value);
            }
        }
    }

    @For(value = HashMap.class, replace = true)
    static class HashMapImpl<K, V> extends AbstractHashMapImpl<K, V> {

        public HashMapImpl() {
        }

        public HashMapImpl(Map<? extends K, ? extends V> map) {
            super(map);
        }

        public HashMapImpl(int initialCapacity) {
            super(initialCapacity);
        }

        public HashMapImpl(int initialCapacity, float loadFactor) {
            super(initialCapacity, loadFactor);
        }
    }

    @For(value = WeakHashMap.class, replace = true)
    static class WeakHashMapImpl<K, V> extends AbstractHashMapImpl<K, V> {

        public WeakHashMapImpl() {
        }

        public WeakHashMapImpl(Map<? extends K, ? extends V> map) {
            super(map);
        }

        public WeakHashMapImpl(int initialCapacity) {
            super(initialCapacity);
        }

        public WeakHashMapImpl(int initialCapacity, float loadFactor) {
            super(initialCapacity, loadFactor);
        }
    }

    @For(value = LinkedHashMap.class, replace = true)
    static class LinkedHashMapImpl<K, V> extends AbstractHashMapImpl<K, V> {

        public LinkedHashMapImpl() {
        }

        public LinkedHashMapImpl(Map<? extends K, ? extends V> map) {
            super(map);
        }

        public LinkedHashMapImpl(int initialCapacity) {
            super(initialCapacity);
        }

        public LinkedHashMapImpl(int initialCapacity, float loadFactor) {
            super(initialCapacity, loadFactor);
        }
    }

    @For(value = ConcurrentHashMap.class, replace = true)
    static class ConcurrentHashMapImpl<K, V> extends AbstractHashMapImpl<K, V> {

        public ConcurrentHashMapImpl() {
        }

        public ConcurrentHashMapImpl(Map<? extends K, ? extends V> map) {
            super(map);
        }

        public ConcurrentHashMapImpl(int initialCapacity) {
            super(initialCapacity);
        }

        public ConcurrentHashMapImpl(int initialCapacity, float loadFactor) {
            super(initialCapacity, loadFactor);
        }

        public ConcurrentHashMapImpl(int initialCapacity, float loadFactor, int concurrencyLevel) {
            super(initialCapacity, loadFactor);
        }
    }

    @SuppressWarnings("unchecked")
    @For(value = {HashSet.class, LinkedHashSet.class}, replace = true)
    static class HashSetImpl<E> extends AbstractSet<E> {

        final HashMapImpl<E, ?> map;
        final boolean supportsAdd;

        public HashSetImpl() {
            this.map = new HashMapImpl<>();
            this.supportsAdd = true;
        }

        public HashSetImpl(Collection<? extends E> c) {
            this();
            addAll(c);
        }

        public HashSetImpl(int initialCapacity) {
            this();
        }

        public HashSetImpl(int initialCapacity, float loadFactor) {
            this();
        }

        @Ignore
        public HashSetImpl(HashMapImpl<E, ?> map, boolean supportsAdd) {
            this.map = map;
            this.supportsAdd = supportsAdd;
        }

        @Override
        public Iterator<E> iterator() {
            return (Iterator<E>) map.iterator(true);
        }

        @Override
        public int size() {
            return map.size();
        }

        @Override
        public boolean add(E e) {
            return ((HashMapImpl<E, Boolean>) map).put(e, Boolean.TRUE) == null;
        }

        @Override
        public boolean remove(Object o) {
            return map.remove(o) != null;
        }

        @Override
        protected Object clone() throws CloneNotSupportedException {
            return new HashSetImpl<>(this);
        }
    }
    */

    @ForClass("jdk/internal/misc/Blocker")
    static class BlockerImpl {
        public static long begin() {
            return -1;
        }

        public static void end(long l) {
        }
    }

    @For(value = ThreadLocal.class)
    static class ThreadLocalImpl<T> {

        @Ignore
        private String id() {
            // reméljük, hogy nem hozunk létre annyi TL-t, hogy kifogyjunk az ID-kből
            return "tl" + Unsafe2.reinterpretAsInt(Unsafe2.getInstanceField(this, Unsafe2.fieldID(ThreadLocal.class,
                    "threadLocalHashCode", int.class)));
        }

        public void set(T value) {
            ((JSObject) Unsafe2.asJSValue(Thread.currentThread())).put(id(), Unsafe2.asJSValue(value));
        }

        @SuppressWarnings("unchecked")
        public T get() {
            JSValue.JSObject threadObj = (JSObject) Unsafe2.asJSValue(Thread.currentThread());
            if (threadObj.get(id()) == JSValue.Undefined.UNDEFINED) {
                threadObj.put(id(), Unsafe2.asJSValue(initialValue()));
            }
            return (T) threadObj.get(id());
        }

        public void remove() {
            ((JSObject) Unsafe2.asJSValue(Thread.currentThread())).delete(id());
        }

        protected Object initialValue() {
            return null;
        }
    }

    @For(value = JSObject.class, replace = true)
    static class JSObjectImpl {

        @Snippet("$0[$1]")
        public native JSValue get(String fieldName);

        @Snippet("($0[$1]=$2)")
        public native void put(String fieldName, JSValue value);

        @Snippet("delete ($0[$1])")
        public native void delete(String fieldName);

        public Iterable<String> fieldNames() {
            return List.of(fieldNamesArray());
        }

        @Snippet("fieldNamesImpl($0)")
        @Ignore
        private native String[] fieldNamesArray();

        @Snippet("(''+$0)")
        public native String toString();
    }

    @For(value = JSArray.class, replace = true)
    static class JSArrayImpl {

        @Snippet("$0.length")
        public native int length();

        @Snippet("$0[$1]")
        public native JSValue get(int i);

        @Snippet("(''+$0)")
        public native String toString();
    }

    @For(value = LoggerFactory.class, scope = Scope.COMPILE_TIME_EVALUATION_AND_RUNTIME)
    static class LoggerFactoryImpl {

        public static SLF4JServiceProvider getProvider() {
            return JSConsoleLoggingServiceProvider.INSTANCE;
        }

        private static class JSConsoleLoggingServiceProvider implements ILoggerFactory,
                SLF4JServiceProvider {

            static final JSConsoleLoggingServiceProvider INSTANCE = new JSConsoleLoggingServiceProvider();
            static final BasicMarkerFactory markerFactory = new BasicMarkerFactory();
            static final BasicMDCAdapter mdcAdapter = new BasicMDCAdapter();

            @Override
            public Logger getLogger(String name) {
                return new JSConsoleLogger(name);
            }

            @Override
            public ILoggerFactory getLoggerFactory() {
                return this;
            }

            @Override
            public IMarkerFactory getMarkerFactory() {
                return markerFactory;
            }

            @Override
            public MDCAdapter getMDCAdapter() {
                return mdcAdapter;
            }

            @Override
            public String getRequestedApiVersion() {
                return "2.0.12";
            }

            @Override
            public void initialize() {
            }
        }

        @SuppressWarnings("ConstantValue")
        private static class JSConsoleLogger extends MarkerIgnoringBase {

            static final int T = 1, D = 2, I = 3, W = 4, E = 5;

            final int level = T;
            final String name;

            public JSConsoleLogger(String name) {
                this.name = name;
            }

            private Console console() {
                Optional<Window> o = ServiceLoader.load(Window.class).findFirst();
                return o.map(Window::console).orElse(null);
            }

            @Override
            public boolean isTraceEnabled() {
                return level <= T;
            }

            @Override
            public void trace(String msg) {
                if (isTraceEnabled())
                    if (console() == null)
                        System.out.println("TRACE " + msg);
                    else
                        console().trace(msg);
            }

            @Override
            public void trace(String format, Object arg) {
                if (isTraceEnabled())
                    if (console() == null)
                        System.out.println("TRACE " + MessageFormatter.format(format, arg));
                    else
                        console().trace(format, arg);
            }

            @Override
            public void trace(String format, Object arg1, Object arg2) {
                if (isTraceEnabled())
                    if (console() == null)
                        System.out.println("TRACE " + MessageFormatter.format(format, arg1, arg2));
                    else
                        console().trace(format, arg1, arg2);
            }

            @Override
            public void trace(String format, Object... arguments) {
                if (isTraceEnabled())
                    if (console() == null)
                        System.out.println("TRACE " + MessageFormatter.format(format, arguments));
                    else
                        console().trace(format, arguments);
            }

            @Override
            public void trace(String msg, Throwable t) {
                throw new UnsupportedOperationException("TODO");
            }

            @Override
            public boolean isDebugEnabled() {
                return level <= D;
            }

            @Override
            public void debug(String msg) {
                if (isDebugEnabled())
                    if (console() == null)
                        System.out.println("DEBUG " + msg);
                    else
                        console().debug(msg);
            }

            @Override
            public void debug(String format, Object arg) {
                if (isDebugEnabled())
                    if (console() == null)
                        System.out.println("DEBUG " + MessageFormatter.format(format, arg));
                    else
                        console().debug(format, arg);
            }

            @Override
            public void debug(String format, Object arg1, Object arg2) {
                if (isDebugEnabled())
                    if (console() == null)
                        System.out.println("DEBUG " + MessageFormatter.format(format, arg1, arg2));
                    else
                        console().debug(format, arg1, arg2);
            }

            @Override
            public void debug(String format, Object... arguments) {
                if (isDebugEnabled())
                    if (console() == null)
                        System.out.println("DEBUG " + MessageFormatter.format(format, arguments));
                    else
                        console().debug(format, arguments);
            }

            @Override
            public void debug(String msg, Throwable t) {
                throw new UnsupportedOperationException("TODO");
            }

            @Override
            public boolean isInfoEnabled() {
                return level <= I;
            }

            @Override
            public void info(String msg) {
                if (isInfoEnabled())
                    if (console() == null)
                        System.out.println("INFO  " + msg);
                    else
                        console().info(msg);
            }

            @Override
            public void info(String format, Object arg) {
                if (isInfoEnabled())
                    if (console() == null)
                        System.out.println("INFO  " + MessageFormatter.format(format, arg));
                    else
                        console().info(format, arg);
            }

            @Override
            public void info(String format, Object arg1, Object arg2) {
                if (isInfoEnabled())
                    if (console() == null)
                        System.out.println("INFO  " + MessageFormatter.format(format, arg1, arg2));
                    else
                        console().info(format, arg1, arg2);
            }

            @Override
            public void info(String format, Object... arguments) {
                if (isInfoEnabled())
                    if (console() == null)
                        System.out.println("INFO  " + MessageFormatter.format(format, arguments));
                    else
                        console().info(format, arguments);
            }

            @Override
            public void info(String msg, Throwable t) {
                throw new UnsupportedOperationException("TODO");
            }

            @Override
            public boolean isWarnEnabled() {
                return level <= W;
            }

            @Override
            public void warn(String msg) {
                if (isWarnEnabled())
                    if (console() == null)
                        System.out.println("WARN  " + msg);
                    else
                        console().warn(msg);
            }

            @Override
            public void warn(String format, Object arg) {
                if (isWarnEnabled())
                    if (console() == null)
                        System.out.println("WARN  " + MessageFormatter.format(format, arg));
                    else
                        console().warn(format, arg);
            }

            @Override
            public void warn(String format, Object arg1, Object arg2) {
                if (isWarnEnabled())
                    if (console() == null)
                        System.out.println("WARN  " + MessageFormatter.format(format, arg1, arg2));
                    else
                        console().warn(format, arg1, arg2);
            }

            @Override
            public void warn(String format, Object... arguments) {
                if (isWarnEnabled())
                    if (console() == null)
                        System.out.println("WARN  " + MessageFormatter.format(format, arguments));
                    else
                        console().warn(format, arguments);
            }

            @Override
            public void warn(String msg, Throwable t) {
                throw new UnsupportedOperationException("TODO");
            }

            @Override
            public boolean isErrorEnabled() {
                return level <= E;
            }

            @Override
            public void error(String msg) {
                if (isErrorEnabled())
                    if (console() == null)
                        System.out.println("ERROR " + msg);
                    else
                        console().error(msg);
            }

            @Override
            public void error(String format, Object arg) {
                if (isErrorEnabled())
                    if (console() == null)
                        System.out.println("ERROR " + MessageFormatter.format(format, arg));
                    else
                        console().error(format, arg);
            }

            @Override
            public void error(String format, Object arg1, Object arg2) {
                if (isErrorEnabled())
                    if (console() == null)
                        System.out.println("ERROR " + MessageFormatter.format(format, arg1, arg2));
                    else
                        console().error(format, arg1, arg2);
            }

            @Override
            public void error(String format, Object... arguments) {
                if (isErrorEnabled())
                    if (console() == null)
                        System.out.println("ERROR " + MessageFormatter.format(format, arguments));
                    else
                        console().error(format, arguments);
            }

            @Override
            public void error(String msg, Throwable t) {
                throw new UnsupportedOperationException("TODO");
            }
        }
    }

    @For(value = DataFlavor.class, scope = Scope.COMPILE_TIME_EVALUATION)
    public static class DataFlavorImpl {

        private static DataFlavor createConstant(String mt, String prn) {
            if (mt.contains("class=java.awt."))
                return null;
            try {
                return new DataFlavor(mt, prn);
            } catch (Exception e) {
                // DataFlavorban is ugyanilyen try-catch van
                return null;
            }
        }
    }

    @For(value = Charset.class)
    public static class CharsetImpl {
        public static boolean isSupported(String s) {
            return s.equalsIgnoreCase("utf-8") || s.equals("us-ascii");
        }

        public static Charset defaultCharset() {
            // a default impl ServiceLoaderrel vacakolna mindenfélét.
            // mondjuk jobb lenne @MustBeEvaluatedCompileTIme-má tenni, mint
            // itt hardcodeolni egy értéket.
            return StandardCharsets.UTF_8;
        }
    }

    @ForClass("sun/datatransfer/DataFlavorUtil")
    public static class DataFlavorUtilImpl {

        public static String canonicalName(String encoding) {
            // Charset.forName-et hívna a beépített implementáció
            return encoding.toUpperCase(Locale.ROOT);
        }
    }


    @For(value = URLStreamHandler.class, scope = Scope.COMPILE_TIME_EVALUATION_AND_RUNTIME)
    public static class URLStreamHandlerImpl {

        // InetAddressel vacakolna equals/hashCodehoz

        protected int hashCode(URL u) {
            int h = 0;

            // Generate the protocol part.
            String protocol = u.getProtocol();
            if (protocol != null)
                h += protocol.hashCode();

            /*
            // Generate the host part.
            InetAddress addr = getHostAddress(u);
            if (addr != null) {
                h += addr.hashCode();
            } else {
                String host = u.getHost();
                if (host != null)
                    h += host.toLowerCase().hashCode();
            }
             */

            // Generate the file part.
            String file = u.getFile();
            if (file != null)
                h += file.hashCode();

            /*
            // Generate the port part.
            if (u.getPort() == -1)
                h += getDefaultPort();
            else
                h += u.getPort();

             */

            // Generate the ref part.
            String ref = u.getRef();
            if (ref != null)
                h += ref.hashCode();

            return h;
        }

        protected boolean hostsEqual(URL u1, URL u2) {
            // itt volt még InetAddress összehasonlítás
            if (u1.getHost() != null && u2.getHost() != null)
                return u1.getHost().equalsIgnoreCase(u2.getHost());
            else
                return u1.getHost() == null && u2.getHost() == null;
        }
    }

    @For(value = InetAddress.class, scope = Scope.COMPILE_TIME_EVALUATION_AND_RUNTIME)
    public static class InetAddressImpl {

        public static void init() {
        }

        public static boolean isIPv4Available() {
            return true;
        }

        public static boolean isIPv6Supported() {
            return true;
        }
    }

    @For(ObjectInputStream.class)
    public static class ObjectInputStreamImpl {

        // TreeMap.buildFromSorted-ban valami kavarás van, azért kell ez

        public Object readObject() {
            throw new UnsupportedOperationException();
        }
    }

    @For(value = Date.class, replace = true)
    public static class DateImpl {

        private long time;

        public DateImpl(long t) {
            this.time = t;
        }

        @Override
        public boolean equals(Object o) {
            return o instanceof Date d && d.getTime() == time;
        }

        @Override
        public int hashCode() {
            return Long.hashCode(time) + 29347843;
        }

        public long getTime() {
            return time;
        }

        public void setTime(long time) {
            this.time = time;
        }

        @Override
        public String toString() {
            // TODO ez a helyes formátum?
            return toInstant().toString();
        }

        public Instant toInstant() {
            return Instant.ofEpochMilli(time);
        }

        @Override
        public Object clone() {
            return new DateImpl(time);
        }

        public int compareTo(Object obj) {
            return compareTo((DateImpl) obj);
        }

        public int compareTo(DateImpl anotherDate) {
            return Long.compare(time, anotherDate.time);
        }

        public static Date from(Instant instant) {
            return new Date(instant.toEpochMilli());
        }
    }

    @For(value = URL.class, replace = true)
    public static class URLImpl {

        private final String s;

        public URLImpl(String url) throws MalformedURLException {
            if (!url.matches("[a-zA-Z0-9+\\-.]+:.*"))
                throw new MalformedURLException("not absolute URL: " + url);
            this.s = url;
        }

        boolean isBuiltinStreamHandler(URLStreamHandler h) {
            throw new UnsupportedOperationException("TODO");
        }

        InputStream openStream() {
            // dinamikusan előállít a kliens kód cp resource URL-eket a képekhez, és próbálja megnyitni
            // azokhoz inputstreamet, amikhez jrt urlstreamhandlert akarná használni
            throw new RuntimeException("TODO");
        }

        public String getFile() {
            return asAnchor().pathname();
        }

        @Override
        public String toString() {
            return s;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof URLImpl u && u.s.equals(s);
        }

        @Override
        public int hashCode() {
            return s.hashCode();
        }

        @Ignore
        private HTMLAnchorElement asAnchor() {
            HTMLAnchorElement a = (HTMLAnchorElement) ServiceLoader.load(Window.class).findFirst().get().
                    document().createElement("a");
            a.href(s);
            return a;
        }
    }

    @ForClass("sun/reflect/annotation/AnnotationParser")
    public static class AnnotationParserImpl {
        public static Map<Class<? extends Annotation>, Annotation> parseAnnotations(
                byte[] rawAnnotations,
                ConstantPoolNatives constPool,
                Class<?> container) {
            throw new UnsupportedOperationException("API.pA");
        }
    }

    // ez csak azért kell, hogy AnnotationParserImpl::parseAnnotationsből tudjunk rá hivatkozni
    @ForClass("jdk/internal/reflect/ConstantPool")
    public static class ConstantPoolNatives {}

    @For(Base64.Encoder.class)
    public static class Base64EncoderImpl {

        @SuppressWarnings({"StringConcatenationInLoop", "ConstantValue"})
        public String encodeToString(byte[] src) {
            if ((Object) this != Base64.getEncoder())
                throw new RuntimeException("TODO");
            String s = "";
            for (byte b : src)
                s += String.valueOf((char) (b & 0xFF));
            return btoa(s);
        }

        @Snippet("btoa($0)")
        @Ignore
        private static native String btoa(String s);
    }

    @For(HttpClient.class)
    public static class HttpClientImpl {

        public static HttpClient newHttpClient() {
            return new JSHttpClient();
        }
    }

    private static class JSHttpClient extends HttpClient {

        @Override
        public WebSocket.Builder newWebSocketBuilder() {
            return new JSWebSocketBuilder();
        }

        @Override
        public Optional<CookieHandler> cookieHandler() {
            throw new RuntimeException("TODO");
        }

        @Override
        public Optional<Duration> connectTimeout() {
            throw new RuntimeException("TODO");
        }

        @Override
        public Redirect followRedirects() {
            throw new RuntimeException("TODO");
        }

        @Override
        public Optional<ProxySelector> proxy() {
            throw new RuntimeException("TODO");
        }

        @Override
        public SSLContext sslContext() {
            throw new RuntimeException("TODO");
        }

        @Override
        public SSLParameters sslParameters() {
            throw new RuntimeException("TODO");
        }

        @Override
        public Optional<Authenticator> authenticator() {
            throw new RuntimeException("TODO");
        }

        @Override
        public Version version() {
            throw new RuntimeException("TODO");
        }

        @Override
        public Optional<Executor> executor() {
            throw new RuntimeException("TODO");
        }

        @Override
        public <T> HttpResponse<T> send(HttpRequest request, BodyHandler<T> responseBodyHandler) throws IOException, InterruptedException {
            throw new RuntimeException("TODO");
        }

        @Override
        public <T> CompletableFuture<HttpResponse<T>> sendAsync(HttpRequest request, BodyHandler<T> responseBodyHandler) {
            throw new RuntimeException("TODO");
        }

        @Override
        public <T> CompletableFuture<HttpResponse<T>> sendAsync(HttpRequest request, BodyHandler<T> responseBodyHandler, PushPromiseHandler<T> pushPromiseHandler) {
            throw new RuntimeException("TODO");
        }
    }

    private static class JSWebSocketBuilder implements java.net.http.WebSocket.Builder {

        private List<String> subprotocols = new ArrayList<>();

        @Override
        public Builder header(String name, String value) {
            throw new RuntimeException("TODO");
        }

        @Override
        public Builder connectTimeout(Duration timeout) {
            // TODO
            return this;
        }

        @Override
        public Builder subprotocols(String mostPreferred, String... lesserPreferred) {
            subprotocols.clear();
            subprotocols.add(Objects.requireNonNull(mostPreferred));
            for (String p : lesserPreferred)
                subprotocols.add(Objects.requireNonNull(p));
            return this;
        }

        @Override
        public CompletableFuture<WebSocket> buildAsync(URI uri, Listener listener) {
            WebSockets webSockets = ServiceLoader.load(WebSockets.class).findFirst().get();
            WebSockets.WebSocket ws = webSockets.create(uri.toString(), subprotocols);
            JSWebSocket ws2 = new JSWebSocket(ws);
            ws.onopen(evt -> {
                listener.onOpen(ws2);
                return null;
            });
            ws.onmessage(evt -> {
                // TODO binary data
                listener.onText(ws2, (String) ((MessageEvent) evt).data(), true);
                return null;
            });
            ws.onclose(evt -> {
                CloseEvent closeEvent = (CloseEvent) evt;
                listener.onClose(ws2, closeEvent.code(), closeEvent.reason());
                return null;
            });
            ws.onerror(evt -> {
                listener.onError(ws2, new RuntimeException("WebSocket error: " + evt));
                return null;
            });
            // most nem jó az se, hogy a megnyitás sikertelenségéről CF-ben értesít
            // a JDK WS, míg mi a Listener.onerrort hívjuk a megnyitás sikertelenségekor
            return new CompletableFuture<>(); // TODO
        }
    }

    private static class JSWebSocket implements WebSocket {

        private final WebSockets.WebSocket ws;
        private final StringBuilder buffer = new StringBuilder();

        public JSWebSocket(WebSockets.WebSocket ws) {
            this.ws = ws;
        }

        @Override
        public CompletableFuture<WebSocket> sendText(CharSequence data, boolean last) {
            buffer.append(data);
            if (last) {
                ws.send(buffer.toString());
                buffer.setLength(0);
            }
            return null; // TODO
        }

        @Override
        public CompletableFuture<WebSocket> sendBinary(ByteBuffer data, boolean last) {
            throw new RuntimeException("TODO");
        }

        @Override
        public CompletableFuture<WebSocket> sendPing(ByteBuffer message) {
            throw new RuntimeException("TODO");
        }

        @Override
        public CompletableFuture<WebSocket> sendPong(ByteBuffer message) {
            throw new RuntimeException("TODO");
        }

        @Override
        public CompletableFuture<WebSocket> sendClose(int statusCode, String reason) {
            throw new RuntimeException("TODO");
        }

        @Override
        public void request(long n) {
            // TODO
        }

        @Override
        public String getSubprotocol() {
            return ws.protocol();
        }

        @Override
        public boolean isOutputClosed() {
            throw new RuntimeException("TODO");
        }

        @Override
        public boolean isInputClosed() {
            throw new RuntimeException("TODO");
        }

        @Override
        public void abort() {
            throw new RuntimeException("TODO");
        }
    }

    @SuppressWarnings("unchecked")
    static class HashMapImpl_Common<K, V> extends AbstractMap<K, V> {

        @Ignore
        static final byte KIND_IDENTITY = 0, KIND_REGULAR = 1;

        private /*final*/ byte kind;
        private /*final*/ Object nativeMap;
        private /*final*/ Object order;
        private int size;

        @Ignore
        final void init(byte kind) {
            // konstruktor nem lehet, mert több replaced osztály is van

            this.kind = kind;
            this.nativeMap = createNativeMap();
            this.order = createNativeMap();
        }

        @Ignore
        private static Object h(Object obj) {
            return Unsafe2.reinterpretAsObject(Objects.hashCode(obj));
        }

        @Override
        public int size() {
            return size;
        }

        @Override
        public boolean isEmpty() {
            return size == 0;
        }

        @SuppressWarnings("ConstantValue")
        @Override
        public boolean containsKey(Object key) {
            Object[] entry = Unsafe2.reinterpretCast(nativeGet(nativeMap, h(key)), Object[].class);
            if (JSValue.Undefined.UNDEFINED == (Object) entry)
                return false;
            for (int i = 0; i < entry.length; i += 2) {
                if (Objects.equals(entry[i], key))
                    return true;
            }
            return false;
        }

        @Override
        public boolean containsValue(Object value) {
            return super.containsValue(value);
        }

        @SuppressWarnings("ConstantValue")
        @Override
        public V get(Object key) {
            Object[] entry = Unsafe2.reinterpretCast(nativeGet(nativeMap, h(key)), Object[].class);
            if (JSValue.Undefined.UNDEFINED == (Object) entry)
                return null;
            for (int i = 0; i < entry.length; i += 2) {
                if (Objects.equals(entry[i], key))
                    return (V) entry[i + 1];
            }
            return null;
        }

        @SuppressWarnings("ConstantValue")
        @Override
        public V put(K key, V value) {
            final Object h = h(key);
            Object[] entry = Unsafe2.reinterpretCast(nativeGet(nativeMap, h), Object[].class);
            if (JSValue.Undefined.UNDEFINED == (Object) entry) {
                entry = new Object[0];
                nativeSet(nativeMap, h, entry);
            }
            for (int i = 0; i < entry.length; i += 2) {
                Object foundKey = entry[i];
                if (Objects.equals(foundKey, key)) {
                    V prev = (V) entry[i + 1];
                    entry[i + 1] = value;
                    nativeSet(order, foundKey, value);
                    return prev;
                }
            }
            arrayPush(entry, key);
            arrayPush(entry, value);
            nativeSet(order, key, value);
            size++;
            return null;
        }

        @SuppressWarnings("ConstantValue")
        @Override
        public V remove(Object key) {
            final Object h = h(key);
            Object[] entry = Unsafe2.reinterpretCast(nativeGet(nativeMap, h), Object[].class);
            if (JSValue.Undefined.UNDEFINED == (Object) entry)
                return null;
            for (int i = 0; i < entry.length; i += 2) {
                Object foundKey = entry[i];
                if (Objects.equals(foundKey, key)) {
                    V prevVal = (V) entry[i + 1];
                    arraySplice(entry, i, 2);
                    size--;
                    if (entry.length == 0)
                        nativeRemove(nativeMap, h);
                    nativeRemove(order, foundKey);
                    return prevVal;
                }
            }
            return null;
        }

        @Override
        public void putAll(Map<? extends K, ? extends V> m) {
            super.putAll(m);
        }

        @Override
        public void clear() {
            nativeClear(nativeMap);
            nativeClear(order);
            size = 0;
        }

        @Override
        public Set<K> keySet() {
            return new EntryCollection_Set(EntryCollection.MODE_KEYS);
        }

        @Override
        public Collection<V> values() {
            return new EntryCollection(EntryCollection.MODE_VALUES);
        }

        @Override
        public Set<Entry<K, V>> entrySet() {
            return new EntryCollection_Set(EntryCollection.MODE_ENTRIES);
        }

        @Override
        public V getOrDefault(Object key, V defaultValue) {
            return super.getOrDefault(key, defaultValue);
        }

        @Override
        public V putIfAbsent(K key, V value) {
            return super.putIfAbsent(key, value);
        }

        @Override
        public boolean remove(Object key, Object value) {
            return super.remove(key, value);
        }

        @Override
        public V replace(K key, V value) {
            return super.replace(key, value);
        }

        @Override
        public boolean replace(K key, V oldValue, V newValue) {
            return super.replace(key, oldValue, newValue);
        }

        @Override
        public V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction) {
            return super.computeIfAbsent(key, mappingFunction);
        }

        @Override
        public V computeIfPresent(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
            return super.computeIfPresent(key, remappingFunction);
        }

        @Override
        public V compute(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
            return super.compute(key, remappingFunction);
        }

        @Override
        public V merge(K key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
            return super.merge(key, value, remappingFunction);
        }

        @Override
        public void forEach(BiConsumer<? super K, ? super V> action) {
            super.forEach(action);
        }

        @Override
        public void replaceAll(BiFunction<? super K, ? super V, ? extends V> function) {
            super.replaceAll(function);
        }

        @Override
        public Object clone() {
            Class<?> c = getClass();
            Object o = Unsafe2.allocateInstance(c);
            HashMapImpl_Common<K, V> m = Unsafe2.reinterpretCast(
                    o, HashMapImpl_Common.class);
            m.init(kind);
            m.putAll(this);
            return m;
        }

        @SuppressWarnings("rawtypes")
        private class EntryCollection extends AbstractCollection {

            private static final int MODE_KEYS = 5, MODE_VALUES = 8, MODE_ENTRIES = 12;

            private final int mode;

            public EntryCollection(int mode) {
                this.mode = mode;
            }

            @Override
            public int size() {
                return size;
            }

            @Override
            public boolean isEmpty() {
                return size == 0;
            }

            @Override
            public boolean contains(Object o) {
                return switch (mode) {
                    case MODE_KEYS -> containsKey(o);
                    case MODE_VALUES -> containsValue(o);
                    case MODE_ENTRIES -> {
                        Map.Entry<?, ?> e = (Entry<?, ?>) o;
                        yield containsKey(e.getKey()) && Objects.equals(get(e.getKey()), e.getValue());
                    }
                    default -> throw new RuntimeException("should not reach here");
                };
            }

            @Override
            public Iterator iterator() {
                return new Iterator() {

                    private final Object iterator = nativeEntries(order);
                    private Object pos;
                    private K currentKey;
                    private V currentValue;
                    private boolean canBeRemoved;

                    {
                        advance();
                    }

                    private void advance() {
                        pos = nativeNext(iterator);
                        if (iteratorValue(pos) instanceof JSArray entry) {
                            currentKey = (K) entry.get(0);
                            currentValue = (V) entry.get(1);
                        }
                    }

                    @Override
                    public boolean hasNext() {
                        return !iteratorDone(pos);
                    }

                    @Override
                    public Object next() {
                        if (!hasNext())
                            throw new NoSuchElementException();

                        K k = currentKey;
                        V v = currentValue;

                        advance();
                        canBeRemoved = true;

                        return switch (mode) {
                            case MODE_KEYS -> k;
                            case MODE_VALUES -> v;
                            case MODE_ENTRIES -> new Map.Entry<K, V>() {

                                @Override
                                public K getKey() {
                                    return k;
                                }

                                @Override
                                public V getValue() {
                                    return v;
                                }

                                @Override
                                public V setValue(V value) {
                                    return put(k, value);
                                }
                            };
                            default -> throw new RuntimeException("should not reach here");
                        };
                    }

                    @Override
                    public void remove() {
                        if (!canBeRemoved)
                            throw new IllegalStateException();
                        HashMapImpl_Common.this.remove(currentKey);
                        canBeRemoved = false;
                    }
                };
            }

            @Override
            public boolean remove(Object o) {
                switch (mode) {
                    case MODE_KEYS -> {
                        boolean contains = containsKey(o);
                        if (contains)
                            remove(o);
                        return contains;
                    }
                    case MODE_VALUES -> {
                        return super.remove(o);
                    }
                    case MODE_ENTRIES -> {
                        Map.Entry entry = (Map.Entry) o;
                        return HashMapImpl_Common.this.remove(entry.getKey(), entry.getValue());
                    }
                    default -> throw new RuntimeException("should not reach here");
                }
            }

            @Override
            public void clear() {
                HashMapImpl_Common.this.clear();
            }
        }

        @SuppressWarnings("rawtypes")
        private class EntryCollection_Set extends EntryCollection implements Set {
            public EntryCollection_Set(int mode) {
                super(mode);
            }
        }

        @Snippet("new Map()")
        @Ignore
        private static native Object createNativeMap();

        @Snippet("$0.get($1)")
        @Ignore
        private static native Object nativeGet(Object map, Object key);

        @Snippet("$0.set($1, $2)")
        @Ignore
        private static native void nativeSet(Object map, Object key, Object value);

        @Snippet("$0.delete($1)")
        @Ignore
        private static native Object nativeRemove(Object map, Object key);

        @Snippet("$0.clear()")
        @Ignore
        private static native void nativeClear(Object map);

        @Snippet("$0.keys()")
        @Ignore
        private static native Object nativeKeys(Object map);

        @Snippet("$0.entries()")
        @Ignore
        private static native Object nativeEntries(Object map);

        @Snippet("$0.values()")
        @Ignore
        private static native Object nativeValues(Object map);

        @Snippet("$0.next()")
        @Ignore
        private static native Object nativeNext(Object iterator);

        @Snippet("$0.push($1)")
        @Ignore
        private static native Object arrayPush(Object[] array, Object val);

        @Snippet("$0.splice($1, $2)")
        @Ignore
        private static native Object arraySplice(Object[] array, int start, int deleteCount);

        @Snippet("$0.value")
        @Ignore
        private static native Object iteratorValue(Object iterator);

        @Snippet("$0.done")
        @Ignore
        private static native boolean iteratorDone(Object iterator);
    }

    @For(value = HashMap.class, replace = true)
    static class HashMapImpl_HashMap extends HashMapImpl_Common {

        @FactoryReplacementForConstructor
        public static HashMapImpl_HashMap create() {
            HashMapImpl_HashMap hm = new HashMapImpl_HashMap();
            hm.init(HashMapImpl_Common.KIND_IDENTITY);
            return hm;
        }

        @FactoryReplacementForConstructor
        public static HashMapImpl_Common create(int initialCapacity) {
            return create();
        }

        @FactoryReplacementForConstructor
        public static HashMapImpl_Common create(int initialCapacity, float loadFactor) {
            return create();
        }

        @FactoryReplacementForConstructor
        public static <K, V> HashMapImpl_Common<K, V> create(Map<? extends K, ? extends V> m) {
            HashMapImpl_Common m2 = create();
            m2.putAll(m);
            return m2;
        }

        public static HashMapImpl_HashMap newHashMap(int numMappings) {
            return create();
        }
    }

    // LHM.removeEldestEntry nincs támogatva
    @For(value = LinkedHashMap.class, replace = true)
    static class HashMapImpl_LinkedHashMap<K, V> extends HashMapImpl_Common<K, V> {

        @FactoryReplacementForConstructor
        static <K, V> HashMapImpl_LinkedHashMap<K, V> create() {
            HashMapImpl_LinkedHashMap<K, V> map = new HashMapImpl_LinkedHashMap<>();
            map.init(HashMapImpl_Common.KIND_REGULAR);
            return map;
        }

        @FactoryReplacementForConstructor
        static HashMapImpl_LinkedHashMap<?, ?> create(int initialCapacity) {
            return create();
        }

        @FactoryReplacementForConstructor
        static HashMapImpl_LinkedHashMap<?, ?> create(int initialCapacity, float loadFactor) {
            return create();
        }

        @FactoryReplacementForConstructor
        static <K, V> HashMapImpl_LinkedHashMap<K, V> create(Map<? extends K, ? extends V> map) {
            HashMapImpl_LinkedHashMap<K, V> m = create();
            m.putAll(map);
            return m;
        }
    }

    @For({HashMap.class, LinkedHashMap.class})
    static class HashMapImpl_HashMapAndLinkedHashMap {

        // HashSetnek kell ez a kettő

        final <T> T[] prepareArray(T[] a) {
            int size = (Unsafe2.reinterpretCast(this, HashMapImpl_Common.class)).size;
            if (a.length < size) {
                return (T[]) java.lang.reflect.Array
                        .newInstance(a.getClass().getComponentType(), size);
            }
            if (a.length > size) {
                a[size] = null;
            }
            return a;
        }

        /**
         * Fills an array with this map keys and returns it. This method assumes that input array is big enough to fit
         * all the keys. Use {@link #prepareArray(Object[])} to ensure this.
         *
         * @param a an array to fill
         * @return supplied array
         */
        final Object[] keysToArray(Object[] a) {
            return Unsafe2.reinterpretCast(this, HashMapImpl_Common.class).keySet().toArray(a);
        }
    }

    @For(value = ConcurrentHashMap.class, replace = true)
    static class HashMapImpl_ConcurrentHashMap<K, V> extends HashMapImpl_Common<K, V> {

        @FactoryReplacementForConstructor
        static HashMapImpl_ConcurrentHashMap<?, ?> create() {
            HashMapImpl_ConcurrentHashMap<Object, Object> map = new HashMapImpl_ConcurrentHashMap<>();
            map.init(HashMapImpl_Common.KIND_REGULAR);
            return map;
        }

        @FactoryReplacementForConstructor
        static HashMapImpl_ConcurrentHashMap<?, ?> create(int initialCapacity) {
            return create();
        }

        @FactoryReplacementForConstructor
        static HashMapImpl_ConcurrentHashMap<?, ?> create(int initialCapacity, float loadFactor) {
            return create();
        }

        @FactoryReplacementForConstructor
        static HashMapImpl_ConcurrentHashMap<?, ?> create(int initialCapacity,
                                                          float loadFactor,
                                                          int concurrencyLevel) {
            return create();
        }

        @FactoryReplacementForConstructor
        static <K, V> HashMapImpl_ConcurrentHashMap<K, V> create(Map<? extends K, ? extends V> map) {
            HashMapImpl_ConcurrentHashMap<K, V> m = new HashMapImpl_ConcurrentHashMap<>();
            m.putAll(map);
            return m;
        }
    }

    @For(ReentrantLock.class)
    static class ReentrantLockImpl {

        void lock() {
        }

        void unlock() {
        }
    }

    /*
    @ForClass("com/flyordie/ui/GlobalViewProviders$ReflectiveElementCreatorPeer")
    static class ReflectiveElementCreatorPeerReplacements {

        private static Object newInstanceJSHelper(Class<?> clazz) {
            return Unsafe2.allocateInstance(clazz); // TODO
        }
    }
    */
    @For(UUID.class)
    static class UUIDImpl {

        // JDK impl SecureRandomot próbál létrehozni, aminek a clinitje viszont
        // "java.security" fájlt meg mittudoménmiket próbál betölteni, ami viszont egyelőre nem megy
        public static UUID randomUUID() {
            Window window = ServiceLoader.load(Window.class).findFirst().get();
            Uint8Array buf = alloc16Byte();
            window.crypto().getRandomValues(buf);

            byte[] randomBytes = new byte[16];
            for (int i = 0; i < randomBytes.length; i++)
                randomBytes[i] = (byte) getByteOf(buf, i);

            // JDK implből másolva
            randomBytes[6] &= 0x0f;  /* clear version        */
            randomBytes[6] |= 0x40;  /* set to version 4     */
            randomBytes[8] &= 0x3f;  /* clear variant        */
            randomBytes[8] |= (byte) 0x80;  /* set to IETF variant  */

            // UUID privát konstruktorból másolva
            long msb = 0;
            long lsb = 0;
            for (int i = 0; i < 8; i++)
                msb = (msb << 8) | (randomBytes[i] & 0xff);
            for (int i = 8; i < 16; i++)
                lsb = (lsb << 8) | (randomBytes[i] & 0xff);
            return new UUID(msb, lsb);
        }

        @Snippet("$0[$1]")
        @Ignore
        private static native int getByteOf(Uint8Array uint8array, int index);

        @Snippet("new Uint8Array(16)")
        @Ignore
        private static native Uint8Array alloc16Byte();
    }

    /* TODO
    @For(WebSocketWrapper.class)
    static class WebSocketWrapperImpl {

        static WebSocketWrapper create(URI uri,
                                       Map<String, String> headers,
                                       List<String> subprotocols,
                                       @Nullable Duration connectTimeout,
                                       WebSocketListener listener,
                                       Executor executor) {
            return new JSWebSocketWrapper(uri, headers, subprotocols,
                    connectTimeout, listener, executor);
        }
    }

    private static class JSWebSocketWrapper implements WebSocketWrapper {

        private final WebSockets.WebSocket ws;
        private final WebSocketListener listener;
        private final Executor executor;
        private boolean closed;

        private List<Object /* String | GracefulCloseStatus* /> queue = new ArrayList<>();


        public JSWebSocketWrapper(URI uri,
                                  Map<String, String> headers,
                                  List<String> subprotocols,
                                  @Nullable Duration connectTimeout,
                                  WebSocketListener listener,
                                  Executor executor) {
            Objects.requireNonNull(uri);
            Objects.requireNonNull(headers);
            Objects.requireNonNull(subprotocols);
            Objects.requireNonNull(connectTimeout);
            Objects.requireNonNull(listener);
            Objects.requireNonNull(executor);

            this.listener = listener;
            this.executor = executor;

            // TODO headers, connectTimeout figyelembe vétele

            WebSockets webSockets = ServiceLoader.load(WebSockets.class).findFirst().get();
            ws = webSockets.create(uri.toString(), subprotocols);
            ws.onopen(evt -> {
                for (Object msg : queue) {
                    if (msg instanceof String s)
                        ws.send(s);
                    else if (msg instanceof GracefulCloseStatus s)
                        ws.close((short) s.code(), s.text());
                }
                queue = null;
                dispatch(listener::connected);
                return null;
            });
            ws.onmessage(evt -> {
                String msg = (String) ((MessageEvent) evt).data();
                dispatch(() -> listener.textReceived(msg));
                return null;
            });
            ws.onclose(evt -> {
                CloseEvent closeEvent = (CloseEvent) evt;
                System.out.println("ce wasclean " + closeEvent.wasClean());
                CloseStatus closeStatus;
                if (closeEvent.code() == 1006)
                    closeStatus = new ErrorCloseStatus(new IOException("Cannot open connection to " + uri));
                else
                    closeStatus = new GracefulCloseStatus(closeEvent.code(), closeEvent.reason());
                dispatch(() -> {
                    queue = null;
                    closed = true;
                    listener.closed(closeStatus);
                });
                return null;
            });
            ws.onerror(evt -> {
                ErrorCloseStatus c = new ErrorCloseStatus(
                        new IOException("Unknown error in WebSocket connection to " + uri));
                dispatch(() -> {
                    closed = true;
                    queue = null;
                    listener.closed(c);
                });
                return null;
            });
        }

        @Override
        public void send(String s) throws WebSocketClosedException {
            if (queue != null)
                queue.add(s);
            else if (closed)
                throw new WebSocketClosedException();
            else
                ws.send(s);
        }

        @Override
        public void send(ByteBuffer b) throws WebSocketClosedException {
            throw new RuntimeException("TODO");
        }

        @Override
        public void close(int statusCode, @Nonnull String statusText) {
            if (statusCode < 1000 || statusCode >= 5000)
                throw new IllegalArgumentException("invalid status code: " + statusCode);
            Objects.requireNonNull(statusText);

            if (queue != null)
                queue.add(new GracefulCloseStatus(statusCode, statusText));
            else if (!closed)
                ws.close((short) statusCode, statusText);
        }

        private void dispatch(Runnable r) {
            executor.execute(() -> {
                if (!closed)
                    r.run();
            });
        }
    }

    // TimerTask.cancel, TimerTask.scheduledExecutionTime nem használható
    @For(value = Timer.class, replace = true)
    static class TimerImpl {

        private List<Integer> timeouts = new ArrayList<>();
        private List<Integer> intervals = new ArrayList<>();

        public void schedule(TimerTask task, long delay) {
            if (delay < 0 || delay > Integer.MAX_VALUE)
                throw new IllegalArgumentException("invalid delay: " + delay);
            if (timeouts == null)
                throw new IllegalStateException("timer is cancelled");
            int[] a = new int[1];
            a[0] = Globals.window().setTimeout(()->{
                timeouts.remove(a[0]);
                task.run();
            }, Math.toIntExact(delay));
            timeouts.add(a[0]);
        }

        public void scheduleAtFixedRate(TimerTask task, long delay, long period) {
            if (delay < 0 || delay > Integer.MAX_VALUE)
                throw new IllegalArgumentException("invalid delay: " + delay);
            if (period < 0 || period > Integer.MAX_VALUE)
                throw new IllegalArgumentException("invalid period: " + period);

            int[] a = new int[1];
            a[0] = Globals.window().setTimeout(()->{
                timeouts.remove(a[0]);
                int intervalHandle = Globals.window().setInterval(task::run, Math.toIntExact(period));
                intervals.add(intervalHandle);
                task.run();
            }, Math.toIntExact(delay));
            timeouts.add(a[0]);
        }

        public void cancel() {
            for (Integer i : timeouts)
                Globals.window().clearTimeout(i);
            for (Integer i : intervals)
                Globals.window().clearInterval(i);
            timeouts = null;
            intervals = null;
        }
    }
     */
}
