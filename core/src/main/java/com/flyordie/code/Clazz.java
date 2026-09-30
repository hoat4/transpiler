package com.flyordie.code;

import com.flyordie.code.Type.ReferenceType;
import com.flyordie.code.Variable.LocalVar;
import com.flyordie.code.Variable.LocalVar.Kind;
import com.flyordie.code.Variable.LocalVar.MethodIdentity;
import com.flyordie.code.annotation.DontSerializeWhileCompilingCode;
import com.flyordie.code.runtime.ForeignLinkerImpl;
import com.flyordie.code.runtime.NioDelegatingFileSystem;
import ui11.reflectutil.ReflectionUtil;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

import java.lang.foreign.Linker;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import static org.objectweb.asm.Opcodes.*;

/**
 * @apiNote "Class" helyett azért lett "Clazz" a neve, hogy ne ütközzön java.lang.Class-szal, mert anélkül ki kéne annak
 * a teljes nevét írni, ha valahol hivatkozunk rá (HotSpot forrásában Klassnak nevezik).
 */
public class Clazz extends ClassNode implements ReferenceType {

    public final CompilationContext context;
    public final Lookup lookup;
    public String packageName;
    public List<Clazz> allDescendants;
    public List<Clazz> allAncestorTypesAndThis; // felfele halad
    public List<Clazz> directSupertypes;
    public List<Clazz> superinterfaces;
    public Clazz superclass;
    public boolean hidden;

    public ClassReader classReader;
    public char[] crCharBuffer;
    public KnownClass knownClass;

    // mezők száma ebben az osztályban és ősosztályokban. field index generáláshoz kell.
    int ancestorFieldCount = -1;
    int ancestorInstanceFieldCount = -1;
    int allFields = -1;
    int allInstanceFields = -1;
    /**
     * ebben benne vannak a statikusak is!
     */
    public List<Field> allFieldList;
    public List<Field> allInstanceFieldList;
    public boolean headerFilled;

    public Clazz(CompilationContext context, ClassReader classReader) {
        super(ASM9);
        this.context = context;
        this.classReader = classReader;
        crCharBuffer = new char[classReader.getMaxStringLength()];

        lookup = name -> {
            if (this.name == null)
                throw new IllegalStateException();
            if (name.equals(this.name))
                return this;
            else
                return context.findClass(name);
        };
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        if (name.equals(NioDelegatingFileSystem.class.getName().replace('.', '/')))
            superName = "java/io/FileSystem";
        else if (name.equals(ForeignLinkerImpl.class.getName().replace('.', '/')))
            interfaces = new String[]{Linker.class.getName().replace('.', '/')};

        super.visit(version, access, name, signature, superName, interfaces);
        knownClass = KnownClass.COMMON_TYPES.get(name);

        packageName = name.substring(0, name.lastIndexOf('/') + 1);
        directSupertypes = new ArrayList<>();
        superinterfaces = new ArrayList<>();
        allDescendants = new ArrayList<>();
        allAncestorTypesAndThis = new ArrayList<>();

        if (superName != null) {
            superclass = context.findClassAndForceParse(superName);
            assert allFields == -1;
            directSupertypes.add(superclass);
        }

        for (String interfaceName : interfaces) {
            Clazz superinterface = context.findClassAndForceParse(interfaceName);
            directSupertypes.add(superinterface);
            superinterfaces.add(superinterface);
        }

        registerSelfAsDescendant(this);

        headerFilled = true;
    }

    private void registerSelfAsDescendant(Clazz c) {
        if (allAncestorTypesAndThis.contains(c))
            return;
        allAncestorTypesAndThis.add(c);
        for (Clazz supertype : c.directSupertypes) {
            assert supertype.allDescendants != null : supertype.name + ", " + this;
            supertype.allDescendants.add(this);
            registerSelfAsDescendant(supertype);
        }
    }

    @Override
    public Method visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        Method method = new Method(this, access, name, descriptor, signature, exceptions);
        this.methods.add(method);
        return method;
    }

    @Override
    public Field visitField(int access, String name, String descriptor, String signature, Object value) {
        Field field = new Field(this, access, name, descriptor, signature, value);
        this.fields.add(field);
        return field;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void visitEnd() {
        if (name.equals(ReflectionUtil.internalName(Linker.class)))
            permittedSubclasses = List.of(ReflectionUtil.internalName(ForeignLinkerImpl.class));

        ancestorFieldCount = superclass == null ? 0 : superclass.allFields;
        assert ancestorFieldCount >= 0 : superclass.name + ", " + name;
        allFields = ancestorFieldCount + fields.size();
        allFieldList = new ArrayList<>();
        for (Clazz c = this; c.superclass != null; c = c.superclass)
            allFieldList.addAll(0, (List<Field>) (List<? extends FieldNode>) c.fields);
        allInstanceFieldList = new ArrayList<>(allFields);
        for (Field f : allFieldList)
            if (!f.isStatic())
                allInstanceFieldList.add(f);
        ancestorInstanceFieldCount = superclass == null ? 0 : superclass.allInstanceFields;
        allInstanceFields = allInstanceFieldList.size();
        assert allFieldList.size() == allFields;
        int j = 0;
        for (FieldNode f : fields)
            if (!((Field) f).isStatic())
                ((Field) f).index = ancestorInstanceFieldCount + j++;
        assert j == allInstanceFields - ancestorInstanceFieldCount;
    }

    public boolean isInterface() {
        return (access & ACC_INTERFACE) != 0;
    }

    public Stream<Field> allFields() {
        Stream<Field> fields = this.fields.stream().map(f -> (Field) f);
        return superclass == null ? fields : Stream.concat(superclass.allFields(), fields);
    }

    // ez most visszaad duplicate-eket is. ezt majd lehet hogy meg kéne szüntetni.
    // most pl. Emitterben külön szűrni kell a duplicate-ekre.
    public Stream<Method> allMethods() {
        return Stream.concat(
                directSupertypes.stream().flatMap(Clazz::allMethods),
                methods.stream().map(Method.class::cast)
        );
    }

    @Override
    public String descriptor() {
        if (name == null)
            throw new IllegalStateException();
        return "L" + name + ";";
    }

    @Override
    public String referenceTypeName() {
        return name;
    }

    @Override
    public String displayName() {
        return name.replace('/', '.');
    }

    @Override
    public org.objectweb.asm.Type type() {
        return org.objectweb.asm.Type.getObjectType(name);
    }

    @Override
    public Type supertype() {
        return superclass;
    }

    @Override
    public boolean isAssignableFrom(Type otherType) {
        return otherType instanceof Clazz c && c.allAncestorTypesAndThis.contains(this)
                || name.equals("java/lang/Object") && otherType instanceof ArrayType;
    }

    @Override
    public int slotSize() {
        return 1;
    }

    @Override
    public int modifiers() {
        return access;
    }

    @Override
    public String toString() {
        return name == null ? super.toString() : name;
    }

    /**
     * Ez nem a valós simpleName-et adja vissza, mert inner classokat nem nézi, csak az utolsó perjelig levág
     */
    public String simpleName() {
        return name.substring(name.lastIndexOf('/') + 1);
    }

    @Override
    public Collection<? extends Type> allAncestorsAndThis() {
        return allAncestorTypesAndThis;
    }

    public boolean hasFunctionalInterfaceAnnotation() {
        if (visibleAnnotations != null)
            for (AnnotationNode ann : visibleAnnotations) {
                if (ann.desc.equals("Ljava/lang/FunctionalInterface;"))
                    return true;
            }
        return false;
    }

    @SuppressWarnings("unchecked")
    public List<Method> methods() {
        return (List<Method>) (List<? extends MethodNode>) methods;
    }

    public AnnotationNode findAnnotation(String annotationType) {
        if (invisibleAnnotations != null)
            for (AnnotationNode ann : invisibleAnnotations)
                if (ann.desc.equals(annotationType))
                    return ann;
        if (visibleAnnotations != null)
            for (AnnotationNode ann : visibleAnnotations)
                if (ann.desc.equals(annotationType))
                    return ann;
        return null;
    }

    public sealed interface Member permits Field, Method {

        int index();

        Clazz clazz();

        boolean isStatic();
    }

    public static final class Method extends MethodNode implements Member {

        public final Clazz clazz;

        // ennek a kettőnek az inicializálását későbbre toljuk, hogy ne keveredjen egy osztály betöltése
        // rekurzióba
        private MethodType type;
        private List<Type> fullArgTypes;

        public boolean isPolySigMethodSpecialization;
        public boolean forceInline;
        private final int index;
        public final int globalNumber;
        private boolean isPolySigMethod;
        public final MethodIdentity rootMethodIdentity = new MethodIdentity();

        public Method(Clazz clazz, int access, String name, String descriptor, String signature, String[] exceptions) {
            super(ASM9, access, name,
                    descriptor = replaceNioFSProviderClassName(descriptor),
                    signature, exceptions);
            this.clazz = clazz;
            this.index = clazz.methods.size();
            this.globalNumber = clazz.context.allMembers.size() * 8;
            clazz.context.allMembers.add(this);

            if (clazz.knownClass == KnownClass.METHOD_HANDLE) // TODO VH
                this.isPolySigMethod =
                        hasVisibleAnnotation("Ljava/lang/invoke/MethodHandle$PolymorphicSignature;");
        }

        private static String replaceNioFSProviderClassName(String descriptor) {
            String returnType = descriptor.substring(descriptor.lastIndexOf(')') + 1);
            if (returnType.startsWith("Lsun/nio/fs/") && returnType.endsWith("FileSystemProvider;")) {
                return descriptor.substring(0, descriptor.lastIndexOf(')') + 1) +
                        "Ljava/nio/file/spi/FileSystemProvider;";
            } else
                return descriptor;
        }

        @Override
        public int index() {
            // JS reflection implementációnak kell
            return index;
        }

        @Override
        public Clazz clazz() {
            return clazz;
        }

        @Override
        public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
            if (descriptor.equals("Ljdk/internal/vm/annotation/ForceInline;"))
                forceInline = true;
            return super.visitAnnotation(descriptor, visible);
        }

        @Override
        public void visitMethodInsn(int opcodeAndSource, String owner, String name, String descriptor, boolean isInterface) {
            super.visitMethodInsn(opcodeAndSource, owner, name,
                    replaceNioFSProviderClassName(descriptor), isInterface);
        }

        @Override
        public String toString() {
            return clazz.name + "." + name + desc;
        }

        private void ensureTypeResolved() {
            if (type == null) {
                type = MethodType.parse(desc, clazz.lookup);
                if (isStatic())
                    fullArgTypes = type.parameterTypes();
                else {
                    List<Type> l = new ArrayList<>();
                    l.add(clazz);
                    l.addAll(type.parameterTypes());
                    fullArgTypes = l;
                }
            }
        }

        public MethodType type() {
            assert !isPolySigMethod || isPolySigMethodSpecialization;
            ensureTypeResolved();
            return type;
        }

        public List<Type> fullArgTypes() {
            assert !isPolySigMethod || isPolySigMethodSpecialization;
            ensureTypeResolved();
            return fullArgTypes;
        }

        public boolean hasBody() {
            return (access & (ACC_ABSTRACT | ACC_NATIVE)) == 0;
        }

        public boolean isStatic() {
            return (access & ACC_STATIC) != 0;
        }

        public boolean isStaticOrConstructor() {
            return isStatic() || name.equals("<init>");
        }

        public Variable parameter(int i) {
            return parameter(i, rootMethodIdentity);
        }

        public Variable parameter(int i, MethodIdentity methodIdentity) {
            ensureTypeResolved();
            int number = 0;
            for (int j = 0; j < i; j++)
                number += fullArgTypes.get(j).slotSize();
            return new LocalVar(this, number, Kind.ofType(fullArgTypes.get(i)), methodIdentity);
        }

        public boolean isParameter(LocalVar var) {
            ensureTypeResolved();
            int n = 0;
            for (Type t : fullArgTypes)
                n += t.slotSize();
            return var.method() == this && var.number() < n;
        }

        public boolean hasVisibleAnnotation(String annotationTypeDescriptor) {
            assert annotationTypeDescriptor.startsWith("L") && annotationTypeDescriptor.endsWith(";");
            if (visibleAnnotations != null)
                for (AnnotationNode ann : visibleAnnotations) {
                    if (ann.desc.equals(annotationTypeDescriptor))
                        return true;
                }
            return false;
        }

        public boolean hasInvisibleAnnotation(String annotationTypeDescriptor) {
            assert annotationTypeDescriptor.startsWith("L") && annotationTypeDescriptor.endsWith(";");
            if (invisibleAnnotations != null)
                for (AnnotationNode ann : invisibleAnnotations) {
                    if (ann.desc.equals(annotationTypeDescriptor))
                        return true;
                }
            return false;
        }

        public AnnotationNode findAnnotation(String annotationType) {
            if (invisibleAnnotations != null)
                for (AnnotationNode ann : invisibleAnnotations)
                    if (ann.desc.equals(annotationType))
                        return ann;
            if (visibleAnnotations != null)
                for (AnnotationNode ann : visibleAnnotations)  // JSTransformerben lévő annotációk ott vannak
                    if (ann.desc.equals(annotationType))
                        return ann;
            return null;
        }


        public String toShortString() {
            return clazz.simpleName() + "." + name +
                    desc.replace("Ljava/lang/Object;", "Obj");
        }

        public boolean isAbstract() {
            return (access & ACC_ABSTRACT) != 0;
        }
    }

    public static final class Field extends FieldNode implements Member {

        private static final String DONT_SERIALIZE = DontSerializeWhileCompilingCode.class.descriptorString();
        private static final String STABLE = "Ljdk/internal/vm/annotation/Stable;";

        public final Clazz clazz;
        public int index = Integer.MAX_VALUE;
        public boolean annotatedWithStable;
        private Type type;

        // ez meg van szorozva vmiért nyolccal, tehát ez nem a valódi sorszám.
        // ez alapján a membert a CompilationContext::memberFromGlobalID-val lehet megszerezni.
        public final int globalNumber;
        public boolean dontSerialize;

        public Field(Clazz clazz, int access, String name, String descriptor, String signature, Object value) {
            super(ASM9, access, name, descriptor, signature, value);
            this.clazz = clazz;
            this.globalNumber = clazz.context.allMembers.size() * 8;
            clazz.context.allMembers.add(this);
        }

        @Override
        public int index() {
            return index;
        }

        @Override
        public Clazz clazz() {
            return clazz;
        }

        public Type type() {
            if (type == null)
                type = Type.parse(desc, clazz.lookup);
            return type;
        }


        public boolean isStatic() {
            return (access & ACC_STATIC) != 0;
        }

        @Override
        public String toString() {
            return clazz + "." + name;
        }

        @Override
        public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
            if (descriptor.equals(DONT_SERIALIZE))
                dontSerialize = true;
            if (descriptor.equals(STABLE))
                annotatedWithStable = true;
            return super.visitAnnotation(descriptor, visible);
        }
    }
}
