package com.flyordie.code;

import com.flyordie.code.Clazz.Field;
import com.flyordie.code.Clazz.Member;
import com.flyordie.code.Clazz.Method;
import com.flyordie.code.CompilationContext.TransformationChain;
import com.flyordie.code.Interpreter.Array;
import com.flyordie.code.Interpreter.ClassObj;
import com.flyordie.code.Interpreter.Obj;
import com.flyordie.code.Location.FieldLocationElement;
import com.flyordie.code.Location.LocationElement;
import com.flyordie.code.Location.MethodLocationElement;
import com.flyordie.code.Node.*;
import com.flyordie.code.Type.ArrayType;
import com.flyordie.code.Type.PrimitiveType;
import com.flyordie.code.Variable.InstanceField;
import com.flyordie.code.Variable.StaticField;
import com.flyordie.code.js.JSEmitter;
import ui11.reflectutil.ReflectionUtil;

import java.util.*;
import java.util.stream.Stream;

import static java.util.Collections.*;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;
import static org.objectweb.asm.Opcodes.*;

// felmerült furcsaság (2023-11-24): használt JSO-s native interface
// (T_org_teavm_jso_typedarrays_ArrayBufferView) superclassa T_org_teavm_jso_typedarrays_ArrayBufferView
// csak additionalusedclass lett, nem rendes
public class EmissionContext {

    public final CompilationContext compContext;
    public final Emitter emitter;

    private Queue<Clazz> clinitNeeded = new LinkedList<>();
    public final Set<Clazz> usedClasses = new HashSet<>();
    private final Set<ArrayType> usedArrayTypes = new HashSet<>();
    private List<UsedConstant> usedConstants = new ArrayList<>();
    private final Set<PrimitiveType> usedPrimitiveTypes = EnumSet.noneOf(PrimitiveType.class);
    public final Set<Field> usedFields = new HashSet<>();
    public final Map<Method, Set<Location>> usedMethodsS = new HashMap<>();
    public final Map<Method, Set<Location>> usedMethodsV = new HashMap<>();

    private final Queue<Method> toCompile = new LinkedList<>();

    // ha van egy tömb amit soha nem hozunk létre, akkor az bekerül ide és kiíródik a definíciója,
    // de ennek semmi értelme
    private final Set<Type.ReferenceType> additionalUsedTypes = new LinkedHashSet<>();
    private final List<Type.ReferenceType> additionalUsedTypeList = new ArrayList<>();

    private final Set<Clazz> neededNativeUpcallStubs = new HashSet<>();
    private final Map<Type, Set<ClassObj>> needsClassValueForDescendants = new HashMap<>();
    private final Map<Type, Map<ClassObj, Object>> classValueMaps = new HashMap<>(); // a valueban a map keye a CV példány

    private final Map<Field, Object> staticFieldValues = new HashMap<>();

    public final TransformationChain transformationChain;

    public EmissionContext(CompilationContext compContext, Emitter emitter) {
        this.compContext = compContext;
        this.emitter = emitter;
        emitter.setEmissionContext(this);

        ReplacementProvider replacementProvider = emitter.makeReplacementProvider();
        TransformationChain inliningTC = new TransformationChain(
                replacementProvider,
                List.of(
                        context -> new OptPhase1(context, context.transformationChain, true),
                        OptPhase2::new,
                        OptPhase3::new,
                        OptPhase4::new
                ),
                List.of()
        );

        transformationChain = new TransformationChain(
                replacementProvider,
                Stream.concat(
                        Stream.of(
                                context -> new OptPhase1(context, inliningTC, true),
                                OptPhase2::new,
                                OptPhase3::new,
                                OptPhase4::new
                        ),
                        emitter.intermediateTransformations().stream()
                ).toList(),
                emitter.finalTransformations()
        );
    }

    public void enqueue(Clazz c, Collection<Location> usedBy) {
        if (c.name.equals("picosdk/RuntimeHelper$$Lambda$17"))
            throw new RuntimeException();

        if (c.name.contains("DirectMethodHandleDescI") || c.name.startsWith("java/lang/invoke/VarHandle$VarHandleDesc")
                || c.name.contains("ClassDesc") || c.name.equals("java/lang/invoke/MethodHandleImpl$Makers$1")
                //|| c.name.contains("ConcurrentSkipListMap")
                || c.name.equals("java/io/ObjectOutputStream") || c.name.equals("java/lang/reflect/Proxy$ProxyBuilder")
                || c.name.equals("java/util/concurrent/FutureTask")
                || c.name.equals("sun/security/x509/X509CertImpl")
                || c.name.equals("java/security/cert/Certificate")
                || c.name.equals("java/security/CodeSource")
            // || c.name.equals("java/lang/invoke/VarHandle")
            /* || c.name.equals("java/util/WeakHashMap$Entry") || c.name.equals("java/util/LinkedHashMap$LinkedValues") */)
            throw new RuntimeException(c + ", " + usedBy.toString());

        if (c.name.startsWith("sun/reflect/generics/") || c.name.startsWith("sun/reflect/annotation/AnnotatedTypeFactory")) {
            // ne fordítsa le a generikes hülyeségek equalsét meg hashCodeját, mert
            // nincs meg a szükséges információ
            additionalUsedType(c);
            return;
        }

        List<Clazz> replaced = transformationChain.replacementProvider.replacedClass(c);
        if (replaced != null) {
            for (Clazz r : replaced)
                enqueueClassImpl(r, usedBy);
        } else
            enqueueClassImpl(c, usedBy);
    }

    private void enqueueClassImpl(Clazz c, Collection<Location> usedBy) {
        if (usedClasses.add(c)) {
            if (c.superclass != null)
                enqueue(c.superclass, usedBy);

            queueClinit(c, usedBy);

            if ((c.access & ACC_ENUM) != 0) {
                // replacement provider beleírhatja a Class objektumotum replacementjébe. ezért előre tudnunk kell,
                // hivatkoznak az enumkonstans tömbre.
                compContext.interpreter().execute(compContext.interpreter().symbols.Class_getEnumConstantsShared,
                        compContext.interpreter().fromType(c)).orElseThrow();
                usedArrayTypes.add(new ArrayType(c));
            }

            Set<Method> overridenMethods = new HashSet<>();
            // azt is figyelembe kell venni, ha egy superclass felülír egy olyan metódust,
            // ami egy olyan interface-ben van definiálva, amit a subclass implementál
            for (Method m : c.allMethods().toList()) {
                overridenMethods.clear();
                compContext.overriddenMethods(m, c, overridenMethods);

                // ilyenkor nem kell foglalkozni azzal hogy lehet hogy ez egy absztrakt interface
                // metódus ami "felülír" egy default (vagy Object?) metódust,
                // mert ezzel csak akkor kell foglalkozni ha meg is hívja valaki a "felülíró" metódust,
                // de ha használva van, akkor meg úgyis meghívjuk enqueue(Method, ...)-ot,
                // ami kezeli ezt az esetet

                if (overridenMethods.stream().filter(om -> usedClasses.contains(om.clazz)).anyMatch(usedMethodsV::containsKey))
                    // ha statikus lenne, overridenMethods nem adta volna vissza
                    enqueue(m,
                            true, overridenMethods.stream().filter(om -> usedClasses.contains(om.clazz)).
                                    flatMap(om -> usedMethodsV.getOrDefault(om, emptySet()).stream()).toList());
            }

            Type type = c;
            type.allAncestorsAndThis().stream().
                    flatMap(t -> needsClassValueForDescendants.getOrDefault(t, Collections.emptySet()).stream()).
                    distinct(). // lehet hogy egy típus valamelyik felmenője is be van jegyezve
                    forEach(cvObj -> {

                computeAndStoreClassValues(type, cvObj, usedBy);
            });
        }
    }

    private boolean queueClinit(Clazz c, Collection<Location> usages) {
        if (c.name.contains("sun/net/www/protocol/jrt/JavaRuntimeURLConnection"))
            throw new RuntimeException("jrt protocol URL handler not available in static environment. " + usagesString(usages));

        if (compContext.interpreter().isFullyInitialized(c))
            // ha nem ellenőriznénk, végtelen ciklus lenne EmissionContext::runban
            return false;

        return clinitNeeded.add(c);
    }

    private void computeAndStoreClassValues(Type type, ClassObj cvObj, Collection<Location> usages) {
        if (type instanceof Clazz c && (c.access & ACC_ABSTRACT) != 0)
            return;
        ClassObj jlClassCO = compContext.interpreter().fromType(type);
        Object value = compContext.interpreter().execute(
                compContext.interpreter().symbols.ClassValue_get,
                cvObj, jlClassCO).orElseThrow();
        classValueMaps.computeIfAbsent(type, __ -> new HashMap<>()).put(cvObj, value);
        transformationChain.replacementProvider.registerClassValue(jlClassCO, cvObj, value);
        usedConstants.add(new UsedConstant(value, usages));
    }

    private boolean addUsedMethod(Map<Method, Set<Location>> set, Method m, Collection<Location> usedBy) {
        // computeIfAbsent nem jó, mert nem tudjuk megkülönböztet az üres usedBy-t attól
        // hogy volt már bejegyezve csak üres volt hozzáadáskor a usedBy.
        // majd javítani kéne az összes olyat, ahol nem tudjuk megállapítani a usedBy-t
        // és emiatt üres listát adunk meg a valós usage-ok helyett.
        // most tudtommal egy ilyen van, enqueue(Clazz)-ban, amikor interface absztrakt metódus
        // felülír egy nem absztrakt metódust.
        Set<Location> l = set.get(m);
        boolean result = l == null;
        if (l == null)
            set.put(m, l = new HashSet<>());
        l.addAll(usedBy);
        return result;
    }

    @SuppressWarnings("ForLoopReplaceableByForEach")
    public void enqueue(Method m, boolean virtualCall, Collection<Location> usedBy) {
        // if (m.name.contains("isAssignableFrom"))
        //     System.out.println("Pattern used by " + usedBy);
        Method replaced = transformationChain.replacementProvider.replacedMethod(m);
        if (transformationChain.replacementProvider.constructorReplacement(replaced) == null)
            m = replaced;

        assert !virtualCall || !(m.name.equals("<init>") || (m.access & ACC_STATIC) != 0);
        assert !m.clazz.name.startsWith("sun/reflect/generics/") ||
                (!m.name.equals("hashCode") && !m.name.equals("toString") && !m.name.equals("equals"));

        if (m.clazz.name.equals("java/lang/invoke/MemberName$Factory") ||
                (/*m.name.equals("<init>") ez maradhat, foreign upcall support (MH.bindTo) miatt szükség van rá|| */
                        m.name.equals("make")) && m.clazz.name.startsWith("java/lang/invoke/")
                        && (m.clazz.name.endsWith("MethodHandle") || m.clazz.name.contains("MethodHandle$"))
                || m.clazz.name.equals("java/lang/invoke/MethodType")
                && (m.name.equals("makeImpl") || m.name.equals("changeReturnType"))
                || m.clazz.name.startsWith("sun/reflect/generics") && m.name.equals("<init>")
                || m.clazz.name.equals("java/lang/reflect/Proxy") && m.name.equals("getProxyConstructor")
                || m.name.endsWith("$MH") /* jextract */
                || m.name.equals("readObject")
                || m.clazz.name.startsWith("java/lang/invoke/") && m.name.equals("editor")
                || m.clazz.name.equals("jdk/internal/foreign/abi/AbstractLinker")
                || m.clazz.name.equals("java/lang/invoke/LambdaFormEditor")
                /* TODO || m.name.equals("createParser") &&
                m.clazz.name.equals(ReflectionUtil.internalName(com.flyordie.configbinder.model.Node.class))
                || m.name.equals("withType") &&
                m.clazz.name.equals(ReflectionUtil.internalName(com.flyordie.configbinder.model.Node.class)) */)
            throw new RuntimeException("Runtime creation of reflection objects is not possible: " + m + ". " + usagesString(usedBy));

        if (!virtualCall) {
            // OptPhase1 átalakíthat egy virtuális hívást nem virtuálissá
            if (addUsedMethod(usedMethodsS, m, usedBy) && !usedMethodsV.containsKey(m))
                toCompile.add(m);
            return;
        }

        if (addUsedMethod(usedMethodsV, m, usedBy)) {
            enqueue(m.clazz, usedBy);
            if (!usedMethodsS.containsKey(m))
                toCompile.add(m);
            queueClinit(m.clazz, usedBy);

            List<Clazz> allDescendants = m.clazz.allDescendants;
            // azért nem for-each, mert előfordult, bekerült az allDescendantsba
            // a belső enqueue által még valami került, ezáltal CME lett.
            // Object.equals esetén AbstractMap.equalshöz
            // érve volt rá példa. nem tudom hogy mi okozza, ki kéne deríteni.
            for (int i = 0; i < allDescendants.size(); i++) {
                Clazz descendant = allDescendants.get(i);
                if (!usedClasses.contains(descendant))
                    continue;

                Method overrider = compContext.resolveVirtualMethodOrNull(m, descendant);
                if (overrider != null && overrider != m)
                    enqueue(overrider, true, usedBy);
            }

            if ((m.access & ACC_ABSTRACT) != 0 && (m.clazz.access & ACC_INTERFACE) != 0)
                // interface-ekben lehet olyan, hogy a leszármazott "override"-ol
                // nem default (azaz absztrakt) függvénnyel egy ős interface-ében
                // lévő default metódust
                for (Clazz ancestor : m.clazz.allAncestorTypesAndThis) {
                    if (!usedClasses.contains(ancestor) && ancestor != m.clazz)
                        continue;

                    Method overrider = compContext.findMethodExcludingOverrides(ancestor, m.name, m.type());
                    if (overrider != null && overrider != m && compContext.canOverride(overrider, m))
                        enqueue(overrider, true, usedBy);
                }
        }
    }

    public void enqueue(Field f, Collection<Location> usages) {
        if (f.clazz.knownClass == KnownClass.CLASS && f.name.equals("annotationData"))
            throw new RuntimeException("Runtime access of reflection objects is not possible: " + f + ". " + usagesString(usages));

        if (usedFields.add(f)) {
            enqueue(f, usages);
            queueClinit(f.clazz, usages);
            if ((f.access & ACC_STATIC) != 0) {
                Object val = compContext.interpreter().readStaticField(f);
                // elmentjük, hogy ne kavarodjunk bele abba, ha esetleg változna fordítás közben az egyik mező értéke
                // mondjuk ez elég fura működés, nem tudom hogy érdemes-e megtartani
                staticFieldValues.put(f, val);
                usedConstants.add(new UsedConstant(val, usages));
            }
        }
    }

    public Collection<Location> usagesOf(Method m) {
        List<Location> l = new ArrayList<>();
        l.addAll(usedMethodsS.getOrDefault(m, emptySet()));
        l.addAll(usedMethodsV.getOrDefault(m, emptySet()));
        return l;
    }

    public void additionalUsedType(Type type) {
        if (type instanceof Type.ReferenceType rt) {
            if (additionalUsedTypes.add(rt))
                additionalUsedTypeList.add(rt);
        }
    }

    public Map<ClassObj, Object> classValueMap(Type type) {
        //if (!(type instanceof Clazz))
        //    throw new UnsupportedOperationException("TODO ClassValue needed for non-class: " + type);
        return classValueMaps.getOrDefault(type, emptyMap());
    }

    @SuppressWarnings("ForLoopReplaceableByForEach")
    public void run() {
        emitter.begin();
        while (!toCompile.isEmpty() || !clinitNeeded.isEmpty()) {
            System.out.println(toCompile);
            System.out.println(clinitNeeded);
            while (!toCompile.isEmpty()) {
                Method m = toCompile.remove();
                if ((m.access & ACC_ABSTRACT) == 0) {
                    try {
                        CompilationContext.CompilationResult cr = compContext.compile(m, transformationChain);
                        Node n = cr.emittableRoot();

                        findUsedTypesAndMethods(m, n);
                        emitter.print(m, n);
                    } catch (RuntimeException e) {
                        throw new RuntimeException("Couldn't transpile method " + m + ": " + e + "\n" +
                                usagesString(m), e);
                    }
                }

                while (!clinitNeeded.isEmpty())
                    compContext.interpreter().ensureInitialized2(clinitNeeded.remove());
            }


            Set<Obj> processedConstants = Collections.newSetFromMap(new IdentityHashMap<>());
            // szándékosan az elejéről megyünk, mert lehet hogy a második (vagy még többedik)
            // iterációban felülírta valami
            for (int i = 0; i < usedConstants.size(); i++) {
                UsedConstant constant = usedConstants.get(i);
                //System.out.println(i + ": " + constant);
                findUsedTypesAndMethodsInConstant(constant.usages, constant.value, processedConstants);
            }
        }

        clinitNeeded = null; // hogy kiderüljön ha valaki túl későn akar beleírni
        usedConstants = null;

        for (Clazz nativeUpcallFunctionalInterface : neededNativeUpcallStubs)
            emitter.printNativeUpcallStub(nativeUpcallFunctionalInterface);

        emitter.printPrimitiveTypes(usedPrimitiveTypes);

        for (Clazz usedClass : usedClasses) {
            Clazz replacement = transformationChain.replacementProvider.replacementClass(usedClass);
            if (replacement != null) { // lehet hogy inkább enqueue-ban kéne kezelni a replacementeket
                if (!usedClasses.contains(replacement)) {
                    compContext.interpreter().ensureInitialized2(replacement);
                    emitter.printType(replacement, usedClass);
                }
            } else {
                List<Clazz> origClasses = transformationChain.replacementProvider.replacedClass(usedClass);
                if (origClasses != null) {
                    for (Clazz origClass : origClasses) {
                        compContext.interpreter().ensureInitialized2(origClass);
                        emitter.printType(usedClass, origClass);
                    }
                } else
                    emitter.printType(usedClass, usedClass);
            }
        }


        staticFieldValues.forEach(emitter::printStaticField);

        // benne van a componenttype az egyik mezőben, ezért ha több dimenziós a több,
        // akkor először az alacsony dimenziójút kell kiírni, mert JS-ben nem
        // lehet ilyenkor előre hivatkozni
        usedArrayTypes.stream().
                sorted(comparing(ArrayType::dimensions)).
                forEach(emitter::printArrayType);

        Set<Clazz> printedAdditionalUsedTypes = new HashSet<>();
        for (int i = 0; i < additionalUsedTypeList.size(); i++) {
            Type.ReferenceType rt = additionalUsedTypeList.get(i);

            if (rt instanceof ArrayType arrayType) {
                // ez így marhaság, ld. komment additionalUsedTypesnál
                if (!usedArrayTypes.contains(arrayType))
                    emitter.printArrayType(arrayType);
            } else {
                Clazz c = (Clazz) rt;
                Clazz c1 = transformationChain.replacementProvider.replacementClass(c);
                List<Clazz> c2 = transformationChain.replacementProvider.replacedClass(c);
                List<Clazz> replacedsOrOrig = c2 == null ? List.of(c) : c2;

                for (Clazz replacedOrOrig : replacedsOrOrig) {
                    if (!printedAdditionalUsedTypes.add(replacedOrOrig))
                        // replacementek esetén enélkül kétszer írná be a típust
                        continue;

                    if (!usedClasses.contains(replacedOrOrig)) {
                        assert c1 == null || c2 == null;
                        emitter.printStubType(c1 == null ? c : c1, replacedOrOrig);
                    }
                }
            }
        }


        emitter.end();
    }

    public String usagesString(Method m) {
        return usagesString(usagesOf(m));
    }

    public String usagesString(Method m, int maxDepth) {
        return usagesString(usagesOf(m), maxDepth);
    }

    public String usagesString(Collection<Location> m) {
        return usagesString(m, 30);
    }

    public String usagesString(Collection<Location> m, int maxDepth) {
        if (m.isEmpty())
            return "Used by none";
        StringBuilder sb = new StringBuilder("Used by: \n");
        usagesString(m, new LinkedList<>(), sb, maxDepth);
        return sb.substring(0, sb.length() - 1);
    }

    private boolean usagesString(Collection<Location> usages, List<LocationElement> stack, StringBuilder out, int maxDepth) {
        if (out.length() > 10000) {
            String cutMsg = "Cut after 10000 characters\n";
            if (!out.substring(out.length() - cutMsg.length()).equals(cutMsg)) {
                out.append(cutMsg);
            }
            return false;
        }
        usages:
        for (Location usage : usages) {
            MethodLocationElement last = null;
            int added = 0;
            List<LocationElement> elements = usage.elements();
            for (int i = 0; i < elements.size(); i++) {
                LocationElement m = elements.get(i);
                if (stack.size() == maxDepth) {
                    out.append(" ".repeat(stack.size() + 1)).append("...\n");
                    break;
                }
                if (stack.contains(m) && !(m instanceof FieldLocationElement
                        /* fieldek esetén csak a value alapján lehetne detektálni a rekurziót */
                )) {
                    out.append(" ".repeat(stack.size())).append("Recursion: ").append(m).append("\n");
                    return true;
                }
                out.append(" ".repeat(stack.size())).append(m.toString());
                if (elements.size() != i + 1 && m instanceof MethodLocationElement)
                    out.append(" (inlined)");
                out.append("\n");
                added++;
                if (m.isTooBroad())
                    continue usages;
                stack.add(m);
                if (m instanceof MethodLocationElement mle)
                    last = mle;
            }
            if (last != null && !usagesString(usagesOf(last.method()), stack, out, maxDepth))
                return false;
            for (int i = 0; i < added; i++)
                stack.remove(stack.size() - 1);
        }
        return true;
    }

    private int recursionCounter;

    private void findUsedTypesAndMethods(Method m, Node node) {
        assert node != null;

        if (node.location == null)
            throw new RuntimeException("null location in " + m + " for " + node);

        if (m.clazz.name.equals("java/util/regex/EmojiData") && m.name.equals("isHigh"))
            // túl mély OR műveletek fája, viszont nincsenek hivatkozások benne, ezért nem kell foglalkozni itt vele
            return;

        Type nodeType = node.type();
        if (nodeType instanceof PrimitiveType p)
            usedPrimitiveTypes.add(p);

        if (node instanceof InvokeVirtualOrInterface invokeNode) {
            enqueue(invokeNode.method, true, List.of(node.location));
        } else if (node instanceof InvokeSpecialOrStatic invokeNode) {
            if (!isMhLinker(invokeNode.method))
                enqueue(invokeNode.method, false, List.of(node.location));
            if (invokeNode.method.clazz.knownClass == KnownClass.UNSAFE2 &&
                    invokeNode.method.name.equals("getInstanceField")
                    && invokeNode.args.get(1) instanceof ConstantNode memberIDConst) {
                usedFields.add((Field) compContext.fieldFromAddress((Clazz) invokeNode.args.get(0).type(),
                        (Integer) memberIDConst.value));
            }
        } else if (node instanceof ObjectNode obj)
            enqueue((Clazz) obj.type(), List.of(node.location));
        else if (node instanceof AllocateArray allocateArray)
            // C esetén kidobódik az AllocateArray, helyette függvényhívás lesz belőle
            usedArrayTypes.add(allocateArray.arrayType);
        else if (node instanceof ConstantNode constantNode) {
            if (constantNode.hasAttachedUsage()) // CFinalTransformer otthagy szemetet
                usedConstants.add(new UsedConstant(constantNode.value, List.of(node.location)));
        } else if (node instanceof NativeUpcallStubNode nativeUpcallStubNode) {
            neededNativeUpcallStubs.add(nativeUpcallStubNode.functionalInterfaceType);
            enqueue(methodInFunctionalInterface(nativeUpcallStubNode.functionalInterfaceType), true,
                    Set.of(nativeUpcallStubNode.location));
        } else if (node instanceof InstanceOf instanceOf && instanceOf.type instanceof Clazz clazz) {
            // TODO mi van ha van egy instanceof egy natív interface-ből képzett tömbre?

            // Ha nem natív típus, akkor nem kell ellenőrizni, mert ha nem hozunk létre példányt belőle,
            // akkor nyilván fixen false lesz az instanceof értéke, ezért nem is kell
            // foglalkozni annak a típusnak a definiálásával, mert allokálásnál (vagy reflection/MH
            // esetén ConstantNode-nál úgyis enqueue-oljuk azt).
            // Natív típusnál viszont enqueue-olni kell, mert böngésző is létrehozhat belőle példányt.

            if (emitter.isNativeType(clazz))
                enqueue(clazz, List.of(node.location));
        } else if (node instanceof CheckCast cc && cc.type instanceof Clazz clazz) {
            // ld. megjegyzés fent instanceofnál
            if (emitter.isNativeType(clazz))
                enqueue(clazz, List.of(node.location));
        } else if (node instanceof ClassValueNode cv) {
            needsClassValueForDescendants.computeIfAbsent(cv.baseType, __ -> new HashSet<>()).add(cv.classValueInstance);

            Clazz type = (Clazz) cv.baseType;
            computeAndStoreClassValues(type, cv.classValueInstance, List.of(node.location));

            // ha a bejárás közben újabb descendantok jelennének meg, akkor enqueue(Clazz) úgyis csinálja
            // a CV kiszámítást, mert fent már beleraktuk needsClassValueForDescendantsba
            int descendantCount = type.allDescendants.size();

            for (int i = 0; i < descendantCount; i++) {
                Clazz c = type.allDescendants.get(i);
                if (usedClasses.contains(c))
                    computeAndStoreClassValues(c, cv.classValueInstance, List.of(node.location));
            }

            if (!(emitter instanceof JSEmitter))
                enqueue(compContext.method(KnownClass.ClassValue, "get",
                                compContext.findClass(KnownClass.OBJECT), compContext.findClass(KnownClass.CLASS)),
                        false, List.of(node.location));
        } else if (node instanceof NativeCallNode nativeCallNode) {
            // ha visszaadunk natív kódból objektumot, annak mindenképpen be kell állítanunk
            // a típus változóját (".t"), mert lehet hogy csinálnánk instanceofot valamilyen (tetszőleges) típusra
            // akkor is, ha magára a kérdéses interface nincs is említve sehol se a kódban
            // TODO upcall paraméternél is ugyanezt kéne csinálni
            Type returnType = nativeCallNode.method.type().returnType();
            if (returnType instanceof Clazz c)
                enqueue(c, List.of(node.location));
            else if (returnType instanceof ArrayType arrayType && arrayType.endingElementType() instanceof Clazz c)
                enqueue(c, List.of(node.location));

            // talán ReplacementProviderbe kéne rakni egy additionalDependencies függvényt,
            // és akkor nem kéne direkt hivatkozás Emitterbe
            if ((nativeCallNode.method.access & ACC_VARARGS) != 0) {
                Type objArray = compContext.interpreter().symbols.objectArray;
                enqueue(compContext.method(KnownClass.UNSAFE2, "nativeInterfaceVarargs",
                        objArray, objArray, objArray), false, List.of(node.location));
            }
        } else {
            Variable v = node.readenVariable();
            if (v instanceof StaticField f) {
                enqueue(f.field(), List.of(node.location));
            } else if (v instanceof InstanceField f) {
                enqueue(f.field(), List.of(node.location));
            }

            WrittenVariableAndValue v2 = node.writtenVariable();
            if (v2 != null && v2.variable() instanceof StaticField f) {
                // Attól még hogy írnak egy mezőt, még nem feltétlen kéne enqueue-olni,
                // mert lehet hogy nincs is kiolvasva sehol.
                // Viszont ha nem enqueue-oljuk a nem olvasott mezőket,
                // akkor Emitterben meg kéne csinálni, hogy nem is írjuk,
                // ami meg azért bonyolult, mert lehet hogy be lett inline-olva
                // egy side effectes kifejezés az írás inputjába.

                enqueue(f.field(), List.of(node.location));
                enqueue(f.field().clazz, List.of(node.location));
            } else if (v2 != null && v2.variable() instanceof InstanceField f) {
                enqueue(f.field(), List.of(node.location));
                enqueue(f.field().clazz, List.of(node.location));
            }
        }

        if (recursionCounter >= 50) throw new RuntimeException("recursion in tree, or too deep: " + node);
        recursionCounter++;
        try {
            for (Node child : node.children())
                if (child != null)
                    findUsedTypesAndMethods(m, child);
            for (Node input : node.inputs()) // nem kéne végigjárni újra, ha már childként végigjártuk
                if (input != null)
                    findUsedTypesAndMethods(m, input);
        } finally {
            recursionCounter--;
        }
    }

    // ez most csak instance fieldekre van meghívva
    @SuppressWarnings("RedundantIfStatement")
    public boolean shouldSerializeField(Field f) {
        //if (f.clazz.name.equals("java/lang/invoke/LambdaForm"))
        //    return f.name.equals("vmentry");
        // TODO azokat a fieldeket se kéne szerializálni, amik nincsenek sose kiolvasva

        if (f.dontSerialize)
            return false;
        if (f.clazz.knownClass == KnownClass.CLASS && (f.name.equals("reflectionData") || f.name.equals(
                "classValueMap") || f.name.equals("annotationData") /* erre van is ellenőrzés enqueue(Field)-ben */))
            return false;
        if (f.clazz.knownClass == KnownClass.METHOD_TYPE && (f.name.equals("form") || f.name.equals("wrapAlt") || f.name.equals("invokers")))
            return false;
        if ((f.clazz.knownClass == KnownClass.Field || f.clazz.knownClass == KnownClass.Executable) && f.name.equals(
                "declaredAnnotations"))
            return false;
        if (f.type() instanceof Clazz c && c.knownClass == KnownClass.ThreadLocalMap)
            return false;
        if (f.clazz.name.equals("java/security/ProtectionDomain") || f.name.equals("codesource"))
            return false;
        if (transformationChain.replacementProvider.shouldNotSerializeInstanceField(f))
            return false;
        return true;
    }

    public static Method methodInFunctionalInterface(Clazz clazz) {
        assert clazz.isInterface();
        List<Method> m = clazz.allMethods().
                filter(m2 -> (m2.access & ACC_ABSTRACT) != 0 && !m2.clazz.name.equals("java/lang/Object")).
                toList();
        if (m.isEmpty())
            throw new RuntimeException("no abstract method in " + clazz);
        if (m.size() > 1)
            throw new RuntimeException("multiple abstract methods in SAM interface " + clazz);
        return m.getFirst();
    }

    private static boolean isMhLinker(Method m) {
        return m.clazz.name.equals("java/lang/invoke/MethodHandle") && m.name.startsWith("linkTo");
    }

    public static final Set<String> FILTERED_OUT_CLASSNAMES_FOR_HEAP_SERIALIZATION = Set.of(
            "java/lang/invoke/MethodTypeForm", "java/lang/Module",
            "java/lang/invoke/BoundMethodHandle$Specializer",
            "java/lang/invoke/LambdaForm$Name",
            "jdk/internal/loader/ClassLoaders$AppClassLoader"
    );

    // a processedObjects paraméter lehetne mező is
    private void findUsedTypesAndMethodsInConstant(Collection<Location> usages, Object value, Set<Obj> processedObjects) {
        if (value instanceof Type t) {
            // lehet hogy lent constantReplacement belőle XYReplacementPRovider.ClassImpl-et csinál belőle,
            // azért kell korábban
            enqueueType(usages, t);
        }

        value = transformationChain.replacementProvider.constantReplacement(value);

        if (value == null)
            return;
        else if (value instanceof Type t) {
            enqueueType(usages, t);
        } else if (value instanceof ClassObj co) {
            if (!processedObjects.add(co))
                return;

            final Clazz coType = co.type();
            if (coType.name.equals("jdk/internal/ref/CleanerImpl$PhantomCleanableRef"))
                // ha ezt engedjük, mindenféle random objektumokhoz tartozó függvények
                // enqueueolódnak, amiket sokszor nem is tudunk lefordítani (pl. Linker::defaultLookup).
                // SegmentAllocator.implicitAllocator esetén próbált PhantomCleaneble vackokat
                // eltárolni.
                throw new RuntimeException();

            if (coType.name.contains("AnnotatedClassImpl"))
                System.out.println("AnnotatedClassImpl usage: " + usagesString(usages));

            if (coType.knownClass == KnownClass.CLASS ||
                    coType.knownClass == KnownClass.Field ||
                    coType.knownClass == KnownClass.Method ||
                    coType.knownClass == KnownClass.RecordComponent) {
                // feltöltjük az annotatáció cache-t, hogy a JS kód szükség esetén ki tudja olvasni.
                // valójában csak azokat az annotációkat kéne, amikre hivatkozik a kód, de nem tudjuk, hogy melyikre

                compContext.interpreter().executeVirtual(
                        compContext.interpreter().symbols.AnnotatedElement_getAnnotations, co).orElseThrow();
            }

            Method preSaveMethod = compContext.findMethodOrNull(coType,
                    "__preSave", new MethodType(List.of(), PrimitiveType.V));
            if (preSaveMethod != null) {
                compContext.interpreter().execute(preSaveMethod, co).orElseThrow();
            }

            if (!transformationChain.replacementProvider.isReplacedInConstants(coType)) {
                if (FILTERED_OUT_CLASSNAMES_FOR_HEAP_SERIALIZATION.contains(co.type().name)) {
                    additionalUsedType(co.type());
                    return;
                }

                enqueue(co.type(), usages);
            } else if (FILTERED_OUT_CLASSNAMES_FOR_HEAP_SERIALIZATION.contains(co.type().name))
                return;

            transformationChain.replacementProvider.prepareForSerialization(co);

            switch (co.type().name) {
                case "java/lang/Class" -> {
                    findUsedTypesAndMethodsInConstant(usages, co.representedData(), processedObjects);
                }
                case "java/lang/reflect/Field" -> {
                    Member e = compContext.memberFromGlobalID(compContext.interpreter().fieldIndexFromReflectionObj(co));
                    compContext.interpreter().reflectivelyUsedMembers.add(e);
                }
                case "java/lang/reflect/Method" -> {
                    Method m = compContext.interpreter().methodFromReflectionObject(co);
                    compContext.interpreter().reflectivelyUsedMembers.add(m);
                    if (transformationChain.replacementProvider.replacementClass(m.clazz) == null ||
                            transformationChain.replacementProvider.replacedMethod(m) != m)
                        enqueue(m, !m.isStatic(), usages);
                }
                case "java/lang/reflect/Constructor" -> {
                    Method m = compContext.interpreter().methodFromConstructorReflectionObject(co);
                    compContext.interpreter().reflectivelyUsedMembers.add(m);
                    if (transformationChain.replacementProvider.replacementClass(m.clazz) == null ||
                            transformationChain.replacementProvider.replacedMethod(m) != m)
                        enqueue(m, false, usages);
                }
                case "java/lang/invoke/MemberName" -> {
                    int refKind = compContext.interpreter().memberNameRefKind(co);
                    switch (refKind) {
                        case H_GETFIELD, H_GETSTATIC, H_PUTFIELD, H_PUTSTATIC -> {
                            Field f = compContext.interpreter().memberNameReferredField(co);
                            enqueue(f, usages);
                        }
                        case H_INVOKESTATIC, H_INVOKESPECIAL, H_NEWINVOKESPECIAL -> {
                            Method m = compContext.interpreter().memberNameReferredMethod(co);
                            //      if (!isMhLinker(m))
                            enqueue(m, false, usages);
                        }
                        case H_INVOKEVIRTUAL, H_INVOKEINTERFACE -> {
                            Method m = compContext.interpreter().memberNameReferredMethod(co);
                            enqueue(m, true, usages);
                        }
                        default -> throw new RuntimeException("unknown ref kind: " + refKind);
                    }
                }
            }

            List<Field> allInstanceFieldList = co.type().allInstanceFieldList;
            for (int i = 0; i < allInstanceFieldList.size(); i++) {
                Field f = allInstanceFieldList.get(i);
                if (shouldSerializeField(f)) {
                    List<Location> newUsages = usages.stream().map(l -> l.prepend(new FieldLocationElement(f))).toList();
                    findUsedTypesAndMethodsInConstant(newUsages, co.readField(i), processedObjects);
                }
            }
        } else if (value instanceof Array array) {
            if (!processedObjects.add(array))
                return;

            usedArrayTypes.add(array.type());
            for (int i = 0; i < array.length(); i++)
                findUsedTypesAndMethodsInConstant(usages, array.readElement(i), processedObjects);
        } else if (value instanceof String) {
            enqueue(compContext.findClass(String.class), usages);
        } else if (value instanceof Character) {
            usedPrimitiveTypes.add(PrimitiveType.C);
        } else if (value instanceof Boolean) {
            usedPrimitiveTypes.add(PrimitiveType.Z);
        } else if (value instanceof Number) {
            switch (value) {
                case Integer a -> usedPrimitiveTypes.add(PrimitiveType.I);
                case Float a -> usedPrimitiveTypes.add(PrimitiveType.F);
                case Double a -> usedPrimitiveTypes.add(PrimitiveType.D);
                case Short a -> usedPrimitiveTypes.add(PrimitiveType.S);
                case Long a -> usedPrimitiveTypes.add(PrimitiveType.J);
                case Byte a -> usedPrimitiveTypes.add(PrimitiveType.B);
                default -> {
                }
            }
        } else if (value instanceof MethodType methodType) {
            findUsedTypesAndMethodsInConstant(usages, methodType.returnType(), processedObjects);
            for (Type t : methodType.parameterTypes())
                findUsedTypesAndMethodsInConstant(usages, t, processedObjects);
        } else
            throw new RuntimeException("unknown constant value (" + value.getClass() + "): " + value);
    }

    private void enqueueType(Collection<Location> usages, Type t) {
        while (t instanceof ArrayType arrayType) {
            usedArrayTypes.add(arrayType);
            t = arrayType.elementType();
        }
        if (t instanceof Clazz c) {
            enqueue(c, usages);
        }
    }

    private record UsedConstant(Object value, Collection<Location> usages) {
    }
}
