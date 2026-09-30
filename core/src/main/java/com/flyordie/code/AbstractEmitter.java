package com.flyordie.code;

import com.flyordie.code.Clazz.Field;
import com.flyordie.code.Clazz.Method;
import com.flyordie.code.Interpreter.Obj;
import com.flyordie.code.Node.LoopNode.LoopKey;
import com.flyordie.code.Node.SequenceNode;
import com.flyordie.code.Node.SequenceNode.MethodKey;
import com.flyordie.code.Node.Switch;
import com.flyordie.code.Node.Switch.SwitchBreakKey;
import com.flyordie.code.Type.ArrayType;
import com.flyordie.code.Type.PrimitiveType;
import com.flyordie.code.Variable.LocalVar;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.Writer;
import java.util.*;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.function.Consumer;
import java.util.function.Function;

import static com.flyordie.code.CompilationContext.context;
import static org.objectweb.asm.Opcodes.ACC_STATIC;

public abstract class AbstractEmitter implements Emitter {

    protected final boolean compact = false, compactTypeNames;
    private int idGen;
    // ez csak akkor van használva, ha compactTypeNames==true
    private final Map<Type, String> refTypeNames = new HashMap<>();

    public EmissionContext emissionContext;
    protected final Map<Node, String> nodeIDs = new HashMap<>();
    protected final Map<Method, String> methodIDs = new HashMap<>();
    protected final Map<Field, String> fields = new HashMap<>();
    protected final Map<LoopKey, String> loopIDs = new HashMap<>();
    protected final Map<Variable, String> syntheticLocalVarNames = new HashMap<>();
    protected final Map<ArrayType, String> arrayTypeNames = new HashMap<>();
    protected final Map<SwitchBreakKey, Switch> switches = new HashMap<>();
    protected final Map<MethodKey, String> labels = new HashMap<>();

    // azért kell eltárolni, mert kell rakni rájuk suffixet, mert Java 21-től egyes osztályoknak ugyanaz a neve
    // (pl. a lambda implek)
    // ez a Map replacementekkel meg natívokkal nem foglalkozik, csak a sima osztályokkal
    private final Map<Clazz, String> classTypeNames = new HashMap<>();

    protected Location prevLoc;
    protected SequenceNode rootBlock;

    /**
     * Replacement esetén ez a mező a replacementet tartalmazza
     */
    protected Method compilingMethod;

    // ez most fölöslegesen tartalmazza azokat az argumentumokat, amiket nem is olvasunk
    protected final Set<String> usedLocalVars = new LinkedHashSet<>();

    protected final CompilationContext ctx = context();

    protected int indent;
    protected Writer out;

    public AbstractEmitter(Writer out) {
        this(out, false);
    }

    public AbstractEmitter(Writer out, boolean compactTypeNames) {
        this.out = out;
        this.compactTypeNames = compactTypeNames;
    }

    @Override
    public void setEmissionContext(EmissionContext emissionContext) {
        this.emissionContext = emissionContext;
    }

    public String n(Node node) {
        String id = id(node);
        if (id != null)
            return id;

        return printExpression(node);
    }

    protected String id(Node node) {
        assert node != null;
        return nodeIDs.get(node);
    }

    protected String id(Variable var) {
        assert var != null;

        if (var instanceof LocalVar v) {
            if (v.method() == compilingMethod && v.number() < compilingMethod.type().argumentsSize() +
                    ((v.method().access & ACC_STATIC) == 0 ? 1 : 0)) {
                if ((v.method().access & ACC_STATIC) == 0 && v.number() == 0)
                    return receiverArgName();
                return "arg" + v.number();
            } else {
                return makeNameOfRealLocalVar(v);
            }
        } else
            return syntheticLocalVarNames.computeIfAbsent(var, __ -> var.toString() + "_" + syntheticLocalVarNames.size());
    }

    protected String makeNameOfRealLocalVar(LocalVar v) {
        final String mangledMethodName = v.method().name.replace('<', '$').replace(">", "");
        return simpleName(v.method().clazz) + "_" + mangledMethodName + "_v" + v.number();
    }

    protected String receiverArgName() {
        return "this";
    }

    protected String methodToFunctionName(Method method) {
        String id = methodIDs.get(method);
        if (id != null)
            return id;

        id = computeFunctionName(method);
        String id2 = methodIDs.put(method, id);
        assert id2 == null;
        return id;
    }

    protected String computeFunctionName(Method method) {
        Method m2 = emissionContext.transformationChain.replacementProvider.replacedMethod(method);
        if (m2 != method)
            return methodToFunctionName(m2);

        String n = nativeMethodName(method);
        if (n != null) {
            return n;
        }

        if (compact)
            if (method.name.equals("<init>"))
                return "c" + methodIDs.size();
            else
                return "m" + methodIDs.size();

        String ownerSimpleName = simpleName(method.clazz);
        if (method.name.equals("<init>"))
            return "c" + methodIDs.size() + "_" + ownerSimpleName;
        else
            return "m" + methodIDs.size() + "_" + ownerSimpleName + "_" + method.name;
    }


    public String typeName(Type type) {
        Objects.requireNonNull(type);
        emissionContext.additionalUsedType(type);
        return typeName2(type);
    }

    public String typeName2(Type type) {
        if (type instanceof PrimitiveType primitiveType)
            return "T" + primitiveType.descriptor();

        if (compactTypeNames)
            return refTypeNames.computeIfAbsent(type, __->"T"+refTypeNames.size());

        if (type instanceof Clazz clazz) {
            Clazz replacement = emissionContext.transformationChain.replacementProvider.replacementClass(clazz);
            if (replacement != null)
                clazz = replacement;

            String nativeTypeName = nativeTypeName2(clazz);
            if (nativeTypeName != null)
                return nativeTypeName;

            return classTypeNames.computeIfAbsent(clazz,
                    c -> "T_" + c.name.replace('/', '_') + "_" + classTypeNames.size());
        } else {
            ArrayType arrayType = (ArrayType) type;
            String n = arrayTypeNames.get(arrayType);
            if (n == null) {
                arrayType.allAncestorsAndThis().stream().
                        filter(ArrayType.class::isInstance).
                        forEach(t -> {
                            ArrayType at = (ArrayType) t;
                            Type elementType = at.endingElementType();
                            String arrayPrefix = "T" + "A".repeat(at.dimensions());
                            arrayTypeNames.put(at, arrayPrefix + (elementType instanceof Clazz clazz
                                    ? "_" + clazz.name.replace('/', '_') :
                                    elementType.descriptor()));
                        });
            }
            return arrayTypeNames.get(arrayType);
        }
    }

    protected abstract String nativeTypeName2(Clazz clazz);

    @Nullable
    protected abstract String nativeMethodName(Method m);

    protected String fieldName(Field field) {
        return fields.computeIfAbsent(field, f ->
                compact ? "f" + fields.size() : "f" + fields.size() + "_" + f.name);
    }

    private static String simpleName(Clazz type) {
        String cn = type.name;
        int i = cn.lastIndexOf('/');
        return cn.substring(i + 1);
    }

    protected void assignId(Node n) {
        assert n != null;
        nodeIDs.computeIfAbsent(n, __ -> compact ? "n" + idGen++ : n.preferredName() + idGen++);
    }


    @Override
    public void print(Method method, Node body) {
        assert usedLocalVars.isEmpty();
        idGen = 0;
        nodeIDs.clear();
        loopIDs.clear();
        switches.clear();
        compilingMethod = emissionContext.transformationChain.replacementProvider.replacementFor(method);
        rootBlock = body instanceof SequenceNode seq ? seq : null;
        prevLoc = null;
        printMethodImpl(method, body);
        usedLocalVars.clear();
    }

    protected abstract void printMethodImpl(Method method, Node body);

    protected abstract String printExpression(Node node);

    public Object replaceConstant(Object val) {
        if (val instanceof Obj obj)
            val = ctx.interpreter().toConstant(obj);

        val = emissionContext.transformationChain.replacementProvider.constantReplacement(val);
        return val;
    }

    protected void printArgList(StringBuilder sb, List<Node> args, int firstArg) {
        sb.append('(');
        for (int i = firstArg; i < args.size(); i++) {
            if (i != firstArg)
                sb.append(',').append(' ');
            sb.append(n(args.get(i)));
        }
        sb.append(')');
    }

    private boolean midLine;

    public void printLineBegin(String s) {
        try {
            printIndent();
            out.write(s);
            midLine = true;
        } catch (IOException e) {
            handleIOError(e);
        }
    }

    public void print(String s) {
        try {
            out.write(s);
        } catch (IOException e) {
            handleIOError(e);
        }
    }

    private String nextLineComment;

    public void printLine(String s) {
        assert indent >= 0;
        try {
            if (midLine) {
                midLine = false;
            } else {
                printIndent();
            }
            out.write(s.replace("é", "e"));
            if (nextLineComment != null) {
                out.write(nextLineComment);
                nextLineComment = null;
            }
            out.write('\r');
            out.write('\n');
        } catch (IOException e) {
            handleIOError(e);
        }
    }

    // TODO el kéne dönteni, hogy mit csináljunk az IOExceptionökkel
    protected void handleIOError(IOException e) {
        throw new RuntimeException(e);
    }

    protected String lineSeparator() {
        return "\r\n";
    }

    protected void printIndent() throws IOException {
        for (int i = 0; i < indent; i++) {
            out.write(' ');
            out.write(' ');
        }
    }
}
