package com.flyordie.code;

import com.flyordie.code.Clazz.Field;
import com.flyordie.code.Clazz.Method;
import com.flyordie.code.Node.NativeSnippetNode;
import com.flyordie.code.Node.NativeSnippetNode.NativeSnippet;
import com.flyordie.code.Node.SequenceNode;
import com.flyordie.code.js.JSReplacementProvider;
import com.flyordie.code.util.MethodNameAndType;
import ui11.reflectutil.ReflectionUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Stream;

// a metódus replacementnek tekintésének szükséges feltétele, hogy ne legyen privát.
// viszont ez inkonzisztens azzal, hogyha van olyan privát member aminél nem találjuk
// hogy minek a replacementje, akkor exceptiont dobunk.

public abstract class AbstractReplacementProvider implements ReplacementProvider {

    protected final CompilationContext ctx;
    protected final EmissionContext emissionContext;

    public final Map<Clazz, Clazz> replacedClasses = new HashMap<>();
    public final Map<Clazz, Clazz> fullyReplacedClasses = new HashMap<>();
    public final Map<Clazz, List<Clazz>> replacedClassesReverse = new HashMap<>();
    public final Map<Clazz, List<Clazz>> fullyReplacedClassesReverse = new HashMap<>();
    public final Map<Method, Method> replacedMethods = new HashMap<>();
    public final Map<Method, Method> replacedMethodsReverse = new HashMap<>();
    public final Map<Method, NativeSnippet> nativeMethods = new HashMap<>();
    private final Map<Field, Field> replacedStatics = new HashMap<>();
    protected final Set<Method> constructorsReplacedWithFactory = new HashSet<>();

    record ReplacerClass(Class<?> replacementClass, List<Clazz> replacedClasses, boolean fullyReplace) {
    }

    public AbstractReplacementProvider(CompilationContext ctx, EmissionContext emissionContext) {
        this.ctx = ctx;
        this.emissionContext = emissionContext;

        List<ReplacerClass> replacerClasses = new ArrayList<>();

        for (Class<?> c : getClass().getDeclaredClasses()) {
            For forAnn = c.getAnnotation(For.class);
            ForClass forClassAnn;
            Scope scope;
            List<Clazz> replacedClasses;
            boolean replace;
            if (forAnn != null) {
                replacedClasses = Stream.of(forAnn.value()).map(ctx::findClass).toList();
                replace = forAnn.replace();
                scope = forAnn.scope();
            } else if ((forClassAnn = c.getAnnotation(ForClass.class)) != null) {
                replacedClasses = Stream.of(forClassAnn.value()).map(ctx::findClass).toList();
                replace = forClassAnn.replace();
                scope = forClassAnn.scope();
            } else
                continue;

            if (scope != Scope.COMPILE_TIME_EVALUATION) {
                Clazz replacer = ctx.findClass(c);
                this.replacedClassesReverse.put(replacer, replacedClasses);
                for (Clazz replacedClass : replacedClasses) {
                    this.replacedClasses.put(replacedClass, replacer);
                }
                replacerClasses.add(new ReplacerClass(c, replacedClasses, replace));
            }

            if (scope != Scope.RUNTIME) {
                for (Clazz replacedClass : replacedClasses)
                    emissionContext.compContext.interpreter().addReplacements(c, replacedClass);
            }
        }

        // azért kell ezeket kigyűjteni listába, mert replacedClassest kiolvassa addRuntimeReplacerClass,
        // és azt lehet hogy egy később beolvasott replacerosztállyal ki lesz még bővítve
        replacerClasses.forEach(this::addRuntimeReplacerClass);
    }

    private void addRuntimeReplacerClass(ReplacerClass r) {
        Clazz replacementClass = ctx.findClass(r.replacementClass);

        if (r.fullyReplace) {
            for (Clazz replacedClass : r.replacedClasses) {
                this.fullyReplacedClasses.put(replacedClass, replacementClass);
            }
            this.fullyReplacedClassesReverse.put(replacementClass, r.replacedClasses);
            for (java.lang.reflect.Constructor<?> construtor : r.replacementClass.getDeclaredConstructors())
                if (!construtor.isAnnotationPresent(Ignore.class))
                    addReplacementMethod(r.replacedClasses, replacementClass, construtor, true);
        }

        Set<MethodNameAndType> addedMNats = new HashSet<>();
        boolean notOrig = false;
        for (Class<?> replacementClass1 : ReflectionUtil.supertypesAndSelf(r.replacementClass)) {
            if (replacementClass1 == Object.class)
                continue;
            for (java.lang.reflect.Method method : replacementClass1.getDeclaredMethods()) {
                if (notOrig && !Modifier.isPublic(method.getModifiers()) && !Modifier.isProtected(method.getModifiers()))
                    continue;
                if (method.getName().startsWith("$$YJP$$"))
                    continue;

                if (addedMNats.add(MethodNameAndType.of(method))) {
                    if (!method.isAnnotationPresent(Ignore.class) &&
                            (!r.fullyReplace || !Modifier.isPrivate(method.getModifiers())))
                        addReplacementMethod(r.replacedClasses, replacementClass, method, !notOrig);
                    if (Modifier.isNative(method.getModifiers()))
                        addNativeMethodImpl(method);
                }
            }

            for (java.lang.reflect.Field field : replacementClass1.getDeclaredFields()) {
                if (notOrig && !Modifier.isPublic(field.getModifiers()) && !Modifier.isProtected(field.getModifiers()))
                    continue;
                if (!field.isAnnotationPresent(Ignore.class) && Modifier.isStatic(field.getModifiers())) {
                    for (Clazz replacedClass : r.replacedClasses) {
                        replacedStatics.put(
                                ctx.field(replacedClass, field.getName(), Type.of(field.getType(), emissionContext.compContext)),
                                ctx.field(replacementClass, field.getName(), Type.of(field.getType(), emissionContext.compContext))
                        );
                    }
                }
            }
        }
    }

    private void addNativeMethodImpl(java.lang.reflect.Method m) {
        JSReplacementProvider.Snippet ann = m.getAnnotation(JSReplacementProvider.Snippet.class);
        Objects.requireNonNull(ann, () -> ReflectionUtil.memberToShortString2(m));
        nativeMethods.put(ctx.findMethodOrNull(ctx.findClass(m.getDeclaringClass()), m.getName(),
                MethodType.parse(TypeUtil.getMethodDescriptor(m), ctx)), makeSnippet(ann));
    }

    private NativeSnippet makeSnippet(JSReplacementProvider.Snippet snippetAnn) {
        Objects.requireNonNull(snippetAnn);
        return new NativeSnippet() {
            @Override
            public boolean isOrderedInputEvaluation() {
                String s = snippetAnn.value();
                if (!s.contains("$"))
                    return true;

                int j = 0;

                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < s.length(); i++) {
                    char ch = s.charAt(i);
                    if (ch == '$') {
                        ch = s.charAt(++i);
                        if (ch >= '0' && ch <= '9') {
                            if (j++ != ch - '0')
                                return false;
                        } else
                            sb.append('$').append(ch);
                    } else
                        sb.append(ch);
                }
                return true;
            }

            @Override
            public String makeScript(List<String> args) {
                assert args.size() < 10;
                String s = snippetAnn.value();
                if (!s.contains("$"))
                    return s;

                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < s.length(); i++) {
                    char ch = s.charAt(i);
                    if (ch == '$') {
                        ch = s.charAt(++i);
                        if (ch >= '0' && ch < '0' + args.size())
                            sb.append(args.get(ch - '0'));
                        else
                            sb.append('$').append(ch);
                    } else
                        sb.append(ch);
                }
                return sb.toString();
            }
        };
    }

    @Override
    public Node nativeMethodImplementation(Method m) {
        try (var ignored = Location.with(new Location(List.of(new Location.MethodLocationElement(m))))) {
            Node[] args = new Node[m.fullArgTypes().size()];
            for (int i = 0; i < args.length; i++)
                args[i] = new Node.ReadLocalVar(m.parameter(i, m.rootMethodIdentity));
            List<Node> argsList = Arrays.asList(args);

            NativeSnippet snippet = nativeMethods.get(m);
            Node content;
            if (snippet != null) {
                Node.NativeSnippetNode snippetNode = new Node.NativeSnippetNode(snippet, m.type().returnType());
                snippetNode.args.addAll(argsList);
                content = snippetNode;
            }else {
                content = nativeMethodBody(m, argsList); // lehetne ezt cacheelni
                if (content == null)
                    return unsatisfiedLinkError(m);
            }

            Node.SequenceNode seq = new Node.SequenceNode(new Node.SequenceNode.MethodKey(m));
            seq.nodes.addAll(argsList);
            seq.nodes.add(content);
            seq.resultSlot.set(content);
            seq.root = true;
            return seq;
        }
    }

    @Nonnull
    private SequenceNode unsatisfiedLinkError(Method m) {
        String msg = "Native method has no implementation: " + m;
        // throw new UnsupportedOperationException(msg + ". " + emissionContext.usagesString(m));

        System.out.println(msg + emissionContext.usagesString(m));
        System.out.println();
        Clazz exceptionClass = ctx.findClass(UnsatisfiedLinkError.class);
        Node.ObjectNode obj = new Node.ObjectNode(new Node.ObjectNode.ObjectIdentity(exceptionClass));
        SequenceNode seq = new SequenceNode(
                new SequenceNode.MethodKey(m),
                obj,
                new Node.InvokeSpecialOrStatic(ctx.findMethodOrFail(exceptionClass, "<init>",
                        MethodType.parse("(Ljava/lang/String;)V", ctx)),
                        new Node[]{obj, new Node.ConstantNode(msg)}),
                new Node.ThrowNode(obj)
        );
        seq.root = true;
        return seq;
    }

    /**
     * ezt felülírhatjuk, ha kell még más is a {@linkplain Snippet @Snippeten} kívül
     */
    protected Node nativeMethodBody(Method m, List<Node> args) {
        Snippet ann = ctx.reflect(m).getAnnotation(Snippet.class);
        if (ann == null)
            return null;

        return new NativeSnippetNode(makeSnippet(ann), m.type().returnType(), args);
    }

    @Override
    public Method replacementFor(Method m) {
        assert !constructorsReplacedWithFactory.contains(m) : m;
        final Method m2 = replacedMethods.getOrDefault(m, m);
        if (m == m2 && fullyReplacedClasses.containsKey(m2.clazz)) {
            // itt még valszeg a optimalizációs fázisban vagyunk és inline-olni próbálják a függvényt,
            // szóval nem kapnánk értelmes eredményt usagesString-re
            throw new RuntimeException("no implementation: " + m);
        }
        return m2;
    }

    @Override
    public Method replacedMethod(Method m) {
        return replacedMethodsReverse.getOrDefault(m, m);
    }

    @Nullable
    @Override
    public Method constructorReplacement(Method method) {
        return constructorsReplacedWithFactory.contains(method)
                ? Objects.requireNonNull(replacedMethods.get(method))
                : null;
    }

    @Nullable
    @Override
    public Clazz replacementClass(Clazz c) {
        return fullyReplacedClasses.get(c);
    }

    @Nullable
    @Override
    public List<Clazz> replacedClass(Clazz c) {
        return fullyReplacedClassesReverse.get(c);
    }

    @Nullable
    @Override
    public Field replacedStaticField(Field field) {
        return replacedStatics.get(field);
    }

    @Override
    public Clazz classOrReplaced(Clazz c) {
        List<Clazz> c2 = replacedClassesReverse.get(c);
        if (c2 == null)
            return c;
        if (c2.size() > 2)
            throw new RuntimeException("multiple class replaced with " + c);
        return c2.get(0);
    }


    private void addReplacementMethod(List<Clazz> replacedClasses, Clazz replacementClass, java.lang.reflect.Executable m,
                                      boolean addReverseMapping) {
        for (Clazz replacedClass : replacedClasses) {
            String origDesc = TypeUtil.getMethodDescriptor(m);
            String methodName = TypeUtil.getMethodName(m);
            boolean constructorReplacedWithFactory = m.isAnnotationPresent(FactoryReplacementForConstructor.class);
            String transformedDesc = origDesc;
            for (var replacedClassEntry : this.replacedClasses.entrySet()) {
                if (replacedClassEntry.getValue() != replacementClass || replacedClassEntry.getKey() == replacedClass)
                    transformedDesc = transformedDesc.replace(
                            "L" + replacedClassEntry.getValue().name + ";",
                            "L" + replacedClassEntry.getKey().name + ";"
                    );
            }
            if (constructorReplacedWithFactory)
                transformedDesc = transformedDesc.substring(0, transformedDesc.indexOf(')') + 1) + 'V';

            String replacementMethodName = constructorReplacedWithFactory ?
                    "<init>" : m.isAnnotationPresent(Name.class)
                    ? m.getAnnotation(Name.class).value() : methodName;

            Method m1 = ctx.findMethodOrFail(replacementClass, methodName, MethodType.parse(origDesc, ctx));
            Method m2 = ctx.findMethodOrFail(replacedClass, replacementMethodName, MethodType.parse(transformedDesc, ctx));
            if (m2.clazz != replacedClass)
                //throw new RuntimeException(m2 + " not in " + replacedClass + ", but in " + m2.clazz + " (replacement class: " + replacementClass + ")");
                continue;

            replacedMethods.put(m2, m1);
            if (constructorReplacedWithFactory)
                constructorsReplacedWithFactory.add(m2);
            else if (addReverseMapping)
                replacedMethodsReverse.put(m1, m2);
        }
        replacementClass = replacementClass.superclass;
    }


    // ez amiatt publikus, mert Emitternek ki kell tudnia szedni a natív típust a nativeType() mezőből
    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface For {

        Class<?>[] value();

        boolean replace() default false;

        String nativeType() default "";

        Scope scope() default Scope.RUNTIME;
    }

    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    protected @interface ForClass {

        String[] value();

        boolean replace() default false;

        Scope scope() default Scope.RUNTIME;
    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    protected @interface Name {

        String value();
    }

    @Target({ElementType.METHOD, ElementType.CONSTRUCTOR, ElementType.FIELD})
    @Retention(RetentionPolicy.RUNTIME)
    protected @interface Ignore {
    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    protected @interface FactoryReplacementForConstructor {
    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Snippet {

        String value();
    }

    public enum Scope {
        RUNTIME, COMPILE_TIME_EVALUATION, COMPILE_TIME_EVALUATION_AND_RUNTIME
    }
}
