package com.flyordie.code.webidl;

import com.flyordie.code.util.StringEscape;
import com.flyordie.code.webidl.Type.*;
import com.flyordie.code.webidl.parser.WebIDLBaseListener;
import com.flyordie.code.webidl.parser.WebIDLLexer;
import com.flyordie.code.webidl.parser.WebIDLParser;
import com.flyordie.code.webidl.parser.WebIDLParser.*;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.antlr.v4.runtime.tree.TerminalNode;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.groupingBy;

// TODO mielőtt ezt újra használjuk, csináljuk meg hogy odaírva a @DynamicGetter és @DynamicSetter annotációkat is
@SuppressWarnings("StringConcatenationInsideStringBufferAppend")
public class WebIDLBindingGenerator extends WebIDLBaseListener {

    private final Map<String, TypeWithExtendedAttributesContext> typedefs = new HashMap<>();
    private final Set<String> enumNames = new HashSet<>();

    private final Map<String, Unit> units = new HashMap<>();
    private Unit unit;
    private final Map<String, WebIDLContext> idls = new HashMap<>();
    private boolean unitExtensionOnly;
    private String currentFilename;
    private String memberIndent;

    private void process(WebIDLContext tree, String filename) {
        if (idls.put(filename, tree) != null)
            throw new RuntimeException("duplicate filename: " + filename);
        ParseTreeWalker.DEFAULT.walk(new WebIDLBaseListener() {
            @Override
            public void enterEnum_(Enum_Context ctx) {
                enumNames.add(ctx.IDENTIFIER_WEBIDL().getSymbol().getText());
            }

            @Override
            public void enterTypedef_(Typedef_Context ctx) {
                String typedefName = ctx.IDENTIFIER_WEBIDL().getSymbol().getText();
                TypeWithExtendedAttributesContext old = typedefs.putIfAbsent(typedefName, ctx.typeWithExtendedAttributes());

                // fetch/Body.idl-ben és push_messaging/PushMessageData.idl-ben is van egy typedef object JSON;
                // ezért egyelőre megnézzük hogy forrásszinten megegyezik-e a két typedefben típus,
                // és ha igen, akkor nem pampogunk.
                if (old != null && !old.getText().equals(ctx.typeWithExtendedAttributes().getText()))
                    throw new RuntimeException("duplicate typedef: " + typedefName);
            }
        }, tree);
    }

    private void generate() {
        for (Map.Entry<String, WebIDLContext> entry : idls.entrySet()) {
            System.err.println(entry.getKey());
            currentFilename = entry.getKey();
            ParseTreeWalker.DEFAULT.walk(this, entry.getValue());
        }
    }

    @Override
    public void enterInterface(InterfaceContext ctx) {
        String name = ctx.IDENTIFIER_WEBIDL().getSymbol().getText();
        newUnit(name, ctx.PARTIAL() != null, isExtensionOnly(ctx.extendedAttributeList()));
        handleInheritance(ctx.inheritance());

        for (ExtendedAttributeContext attr : ctx.extendedAttributeList().extendedAttribute()) {
            var argListAttr = attr.extendedAttributeArgList();
            if (argListAttr == null || !argListAttr.IDENTIFIER_WEBIDL().getSymbol().getText().equals("Constructor"))
                continue;

            StringBuilder b = unit.statics;
            b.append(memberIndent).append(unit.name).append(" create");
            printArgumentList(argListAttr.argumentList(), b);
            b.append(";\n");
        }
    }

    @Override
    public void exitInterface(InterfaceContext ctx) {
        unit = null;
    }

    private void handleInheritance(InheritanceContext inheritance) {
        TerminalNode identifier = inheritance.IDENTIFIER_WEBIDL();
        if (identifier != null) {
            String inheritFrom = identifier.getSymbol().getText();
            if (unit.inheritFrom != null)
                throw new RuntimeException("multiple inheritance declared for " + unit.name);
            unit.inheritFrom = importIfNeeded(inheritFrom);
        }
    }

    @Override
    public void enterNamespace(NamespaceContext ctx) {
        // meg kéne nézni hogy mik ezek. talán olyan interface-féleségek, amiben minden operation implicit statikus.
        // Chrome-ban viszont nincs ilyen, ezért egyelőre nem foglalkozunk vele.

        if (true)
            throw new UnsupportedOperationException("TODO namespacesa");

        String name = ctx.IDENTIFIER_WEBIDL().getSymbol().getText();
        newUnit(name, ctx.PARTIAL() != null, isExtensionOnly(ctx.extendedAttributeList()));
    }

    @Override
    public void exitNamespace(NamespaceContext ctx) {
        unit = null;
    }

    @Override
    public void enterCallbackInterface(CallbackInterfaceContext ctx) {
        String name = ctx.IDENTIFIER_WEBIDL().getSymbol().getText();
        newUnit(name, false, isExtensionOnly(ctx.extendedAttributeList()));
        unit.callbackInterface = true;
    }

    @Override
    public void exitCallbackInterface(CallbackInterfaceContext ctx) {
        unit = null;
    }

    @Override
    public void enterInterfaceMixin(InterfaceMixinContext ctx) {
        String name = ctx.IDENTIFIER_WEBIDL().getSymbol().getText();
        newUnit(name, ctx.PARTIAL() != null, isExtensionOnly(ctx.extendedAttributeList()));
    }

    @Override
    public void exitInterfaceMixin(InterfaceMixinContext ctx) {
        unit = null;
    }

    @Override
    public void enterDictionary(DictionaryContext ctx) {
        newUnit(ctx.IDENTIFIER_WEBIDL().getSymbol().getText(), ctx.PARTIAL() != null,
                isExtensionOnly(ctx.extendedAttributeList()));
        unit.kind = Unit.Kind.DICTIONARY;
        handleInheritance(ctx.inheritance());
    }

    @Override
    public void enterDictionaryMember(DictionaryMemberContext ctx) {
        String name = ctx.IDENTIFIER_WEBIDL().getSymbol().getText();
        String mappedName = name(name);
        Type type;
        boolean required;

        switch (ctx.altNum) {
            case 1 -> {
                required = true;
                type = type(ctx.typeWithExtendedAttributes());
            }
            case 2 -> {
                required = false;
                type = type(ctx.type_());
            }
            default -> throw new RuntimeException("unknown dictionary member type: " + ctx);
        }

        StringBuilder b = unit.operations;
        b.append(memberIndent);
        if (!mappedName.equals(name))
            b.append("@Name(\"" + escapeJavaString(name) + "\")");
        b.append("public ").append(toJavaType(type, TypeUseLocation.VARIABLE_DECLARATION));
        b.append(' ').append(mappedName);
        if (ctx.default_() != null && ctx.default_().defaultValue() != null)
            b.append(" = ").append(defaultValue(ctx.default_().defaultValue(), type));
        b.append(";\n");
    }


    @Override
    public void exitDictionary(DictionaryContext ctx) {
        unit = null;
    }

    @Override
    public void enterEnum_(Enum_Context ctx) {
        newUnit(ctx.IDENTIFIER_WEBIDL().getSymbol().getText(), false, isExtensionOnly(ctx.extendedAttributeList()));
        unit.kind = Unit.Kind.ENUM;
        List<TerminalNode> elements = ctx.STRING_WEBIDL();
        StringBuilder b = unit.constants;
        for (int i = 0; i < elements.size(); i++) {
            if (i % 4 == 0)
                b.append(memberIndent);
            String name = name(stringValue(elements.get(i)));
            unit.constants.append(name);
            if (i != elements.size() - 1) {
                if (i % 4 == 3)
                    b.append(", \n");
                else
                    b.append(", ");
            }
        }
        b.append('\n');
    }

    @Override
    public void exitEnum_(Enum_Context ctx) {
        unit = null;
    }

    @Nonnull
    private static String stringValue(TerminalNode stringLiteral) {
        String n = stringLiteral.getSymbol().getText();
        // TODO unescape
        return n.substring(1, n.length() - 1);
    }

    @Nonnull
    private static String escapeJavaString(String name) {
        return StringEscape.escapeJSString(name);
    }

    private String defaultValue(DefaultValueContext defaultValueContext, Type type) {
        return switch (defaultValueContext.altNum) {
            case 1 -> constValue(defaultValueContext.constValue(), type);
            case 2 -> {
                String content = stringValue(defaultValueContext.STRING_WEBIDL());
                if (type instanceof EnumType enumType)
                    yield enumType.enumName() + "." + name(content);
                else
                    yield "\"" + escapeJavaString(content) + "\"";
            }
            case 3 -> "Collections.emptyList()";
            case 4 -> "new " + findInterfaceType(type).interfaceName() + "()";
            case 5 -> "null";
            default -> throw new RuntimeException("unknown default value kind: " + defaultValueContext);
        };
    }

    private InterfaceType findInterfaceType(Type type) {
        if (type instanceof InterfaceType interfaceType)
            return interfaceType;
        if (type instanceof UnionType unionType) {
            for (Type t : unionType.types()) {
                InterfaceType interfaceType = findInterfaceType(t);
                if (interfaceType != null)
                    return interfaceType;
            }
        }
        return null;
    }

    /**
     * Ez beállítja a {@link #unit} mezőt
     */
    private Unit newUnit(String name, boolean partial, boolean extensionsOnly) {
        Unit u = units.computeIfAbsent(name, Unit::new);
        if (!partial) {
            u.extensionOnly = extensionsOnly;
            u.fullDefinitionCount++;
            if (u.fullDefinitionCount > 1)
                throw new RuntimeException("duplicate callback interface: " + name +
                        " (in file: " + currentFilename + ", old: " + u.filenames + ")");

            String folderName = Path.of(currentFilename).getName(1).toString(); //  TODO itt ne parzoljuk újra a patht
            u.category = mapCategoryName(folderName);
        }
        unit = u;
        unit.filenames.add(currentFilename);
        unitExtensionOnly = extensionsOnly;
        memberIndent = unit.name.equals(unit.category) ? "    " : "        ";
        assert unit.name.equals(name);
        return u;
    }

    @Override
    public void enterIncludesStatement(IncludesStatementContext ctx) {
        Unit u1 = newUnit(ctx.IDENTIFIER_WEBIDL(0).getSymbol().getText(), true, false);
        u1.implementedInterfaces.add(importIfNeeded(ctx.IDENTIFIER_WEBIDL(1).getSymbol().getText()));
        unit = null;
    }

    @Override
    public void exitIncludesStatement(IncludesStatementContext ctx) {
        unit = null;
    }

    @Override
    public void enterConstructor(ConstructorContext constructor) {
        if (unitExtensionOnly || isExtensionOnly(constructor.extendedAttributeList())) return;

        StringBuilder b = unit.statics;
        b.append(memberIndent).append(unit.name).append(" create");
        printArgumentList(constructor.argumentList(), b);
        b.append(";\n");
    }

    @Override
    public void enterConst_(Const_Context ctx) {
        if (unitExtensionOnly)
            return;

        Type type = constType(ctx.constType());
        String name = ctx.IDENTIFIER_WEBIDL().getSymbol().getText();
        ConstValueContext constValue = ctx.constValue();
        String value = constValue(constValue, type);
        if (unit.kind == Unit.Kind.INTERFACE)
            unit.constants.append(memberIndent);
        else
            unit.constants.append(memberIndent + "public static final ");
        unit.constants.append(toJavaType(type, TypeUseLocation.VARIABLE_DECLARATION) + " " + name + " = " + value + ";\n");
    }

    private String constValue(ConstValueContext constValue, Type type) {
        String value = constValue.getText();
        if (type instanceof UnionType unionType) {
            Set<PrimitiveType> primitiveTypes = EnumSet.noneOf(PrimitiveType.class);
            findPossiblePrimitiveTypes(unionType, primitiveTypes);
            if (primitiveTypes.isEmpty())
                throw new RuntimeException("not applicable for constants: " + type);
            if (primitiveTypes.size() > 1)
                throw new RuntimeException("more than one possible types for constant: " + type);
            type = primitiveTypes.iterator().next();
        }

        if (type instanceof PrimitiveType p)
            switch (p) {
                case BYTE -> value = "(byte) " + value;
                case SHORT -> value = "(short) " + value;
                case INT ->
                        value = Integer.toString((int) (long) Long.decode(value)); // ha unsigned és túl nagy, akkor így negatívvá alakítjuk
                case LONG -> value += "L";
                case BIG_INTEGER -> value = "new BigInteger(\"" + value + "\")";
                case FLOAT -> {
                    if (value.equals("Infinity"))
                        value = "Float.POSITIVE_INFINITY";
                }
                case DOUBLE -> {
                    if (value.equals("Infinity"))
                        value = "Double.POSITIVE_INFINITY";
                }
            }
        else
            throw new UnsupportedOperationException(type.toString());
        return value;
    }

    private static void findPossiblePrimitiveTypes(UnionType unionType, Set<PrimitiveType> result) {
        for (Type t : unionType.types())
            if (t instanceof PrimitiveType primitiveType)
                result.add(primitiveType);
            else if (t instanceof UnionType t2)
                findPossiblePrimitiveTypes(t2, result);
    }

    @Override
    public void enterAttribute(AttributeContext attr) {
        if (unitExtensionOnly || isExtensionOnly(attr.extendedAttributeList())) return;

        boolean readOnly = attr.READONLY() != null;
        boolean isStatic = attr.STATIC() != null;

        String name = attr.attributeName().attributeNameKeyword() != null
                ? attr.attributeName().attributeNameKeyword().getText()
                : attr.attributeName().IDENTIFIER_WEBIDL().getText();
        String mappedName = name(name);
        String type = toJavaType(type(attr.typeWithExtendedAttributes()),
                readOnly ? TypeUseLocation.RETURN_TYPE : TypeUseLocation.VARIABLE_DECLARATION);

        // Elementnél className elvileg Stringet adna vissza, de SVGElement ennek hiába leszármazottja,
        // mégis helyette SVGAnimatedStringet ad vissza, ami nem egy String.
        // valamit kéne csinálni Element.className-mel is, mert így heap pollution lesz.
        if (unit.name.equals("SVGElement") && name.equals("className"))
            mappedName = "classNameSVG";

        StringBuilder b = isStatic ? unit.statics : unit.operations;
        b.append(memberIndent);
        if (!mappedName.equals(name))
            b.append("@Name(\"").append(escapeJavaString(name)).append("\") ");

        b.append("@Getter ").append(type).append(' ').append(mappedName).append("();\n");

        if (!readOnly) {
            b.append(memberIndent);
            if (!mappedName.equals(name))
                b.append("@Name(\"").append(escapeJavaString(name)).append("\") ");
            b.append("@Setter void ").append(mappedName).append("(").append(type).append(" v);\n");
            // TODO értelmes paraméternév
        }
    }

    @Override
    public void enterOperation(OperationContext op) {
        if (unitExtensionOnly || isExtensionOnly(op.extendedAttributeList())) return;

        boolean isStatic = op.STATIC() != null;
        boolean isStringifier = op.STRINGIFIER() != null;

        OperationNameContext opNameElem = op.operationName();
        String opName;
        if (opNameElem == null) {
            opName = switch (op.operationKind().altNum) {
                case 1 -> {
                    if (isStringifier)
                        yield "__stringify";
                    else
                        throw new RuntimeException("no name on regular operation: " + op.start.getLine() + " in " + unit.name);
                }
                case 2 -> "get";
                case 3 -> "set";
                case 4 -> "delete";
                case 5 -> "__legacycall";
                default -> throw new UnsupportedOperationException("unknown operation kind: " + op);
            };
        } else {
            if (opNameElem.INCLUDES() != null)
                throw new RuntimeException("'includes' operation name not supported");
            opName = opNameElem.IDENTIFIER_WEBIDL().getSymbol().getText();
        }
        String mappedOpName = name(opName);

        Type returnType = type(op.type_());

        // Chrome-ban van CallWith=(ScriptArguments,ScriptState) extendedattribute Console-ban lévő operationökön.
        // A ScriptState nem tom hogy mit jelent, de a ScriptArguments gondolom azt hogy az egész olyan mint
        // ha varargs lenne a paraméterlista. Lehetne csinálni hozzá supportot, de egyelőre egyszerűbb
        // Console-ba kézzel beírni, mert csak ott szerepel ilyen.

        StringBuilder b = isStatic ? unit.statics : unit.operations;
        b.append(memberIndent);
        if (!mappedOpName.equals(opName))
            b.append("@Name(\"").append(escapeJavaString(opName)).append("\") ");
        b.append(toJavaType(returnType, TypeUseLocation.VARIABLE_DECLARATION)).append(' ');
        b.append(mappedOpName);
        printArgumentList(op.argumentList(), b);
        b.append(";\n");
    }

    @Override
    public void enterCallback(CallbackContext callbackFunction) {
        String name = callbackFunction.IDENTIFIER_WEBIDL().getSymbol().getText();
        Type returnType = type(callbackFunction.type_());

        newUnit(name, false, isExtensionOnly(callbackFunction.extendedAttributeList()));
        unit.callbackInterface = true;
        unit.operations.append(memberIndent + toJavaType(returnType, TypeUseLocation.VARIABLE_DECLARATION) + " apply");
        printArgumentList(callbackFunction.argumentList(), unit.operations);
        unit.operations.append(";\n");
    }

    @Override
    public void exitCallback(CallbackContext ctx) {
        unit = null;
    }

    private void printArgumentList(ArgumentListContext argumentList, StringBuilder b) {
        b.append('(');
        int argNumber = 0;
        for (ArgumentContext arg : argumentList.argument()) {
            if (++argNumber > 1)
                b.append(", ");

            boolean varargs;

            Type argType;
            if (arg.typeWithExtendedAttributes() != null) {
                argType = type(arg.typeWithExtendedAttributes());
                varargs = false;
            } else {
                argType = type(arg.type_());
                varargs = arg.ellipsis().altNum == 1;
            }

            b.append(toJavaType(argType, TypeUseLocation.VARIABLE_DECLARATION));
            if (varargs)
                b.append("... ");
            else
                b.append(' ');

            String argName;
            if (arg.argumentName() == null)
                argName = "arg" + argNumber;
            else {
                if (arg.argumentName().argumentNameKeyword() != null) {
                    argName = arg.argumentName().argumentNameKeyword().getText();
                } else
                    argName = arg.argumentName().IDENTIFIER_WEBIDL().getSymbol().getText();
            }
            b.append(name(argName));
        }
        b.append(")");
    }

    private static boolean isExtensionOnly(ExtendedAttributeListContext attrList) {
        for (ExtendedAttributeContext attr : attrList.extendedAttribute()) {
            if (attr.extendedAttributeNoArgs() != null) {
                String val = attr.extendedAttributeNoArgs().IDENTIFIER_WEBIDL().getSymbol().getText();
                if (val.equals("ChromeOnly"))
                    return true;
            } else if (attr.extendedAttributeString() != null) {
                String name = attr.extendedAttributeString().IDENTIFIER_WEBIDL().getSymbol().getText();
                String value = stringValue(attr.extendedAttributeString().STRING_WEBIDL());
                if (name.equals("Pref") && value.startsWith("extensions.") ||
                        name.equals("Func") && value.equals("nsGlobalWindowInner::IsPrivilegedChromeWindow") ||
                        name.equals("Func") && value.equals("nsContentUtils::IsCallerChromeOrFuzzingEnabled"))
                    return true;
            }
            return false;
        }
        return false;
    }

    private enum TypeUseLocation {
        RETURN_TYPE, VARIABLE_DECLARATION, TYPE_VARIABLE
    }

    private String toJavaType(Type t, TypeUseLocation useLocation) {
        if (t instanceof PrimitiveType p) {
            if (useLocation == TypeUseLocation.TYPE_VARIABLE)
                return switch (p) {
                    case ANY, OBJECT -> "Object";
                    case BIG_INTEGER -> "BigInteger";
                    case BOOLEAN -> "Boolean";
                    case BYTE -> "Byte";
                    case FLOAT -> "Float";
                    case INT -> "Integer";
                    case LONG -> "Long";
                    case SHORT -> "Short";
                    case SYMBOL -> "Object";
                    case DOUBLE -> "Double";
                    case VOID -> "Void";
                };
            else
                return switch (p) {
                    case ANY, OBJECT -> "Object";
                    case BIG_INTEGER -> "BigInteger";
                    case BOOLEAN -> "boolean";
                    case BYTE -> "byte";
                    case FLOAT -> "float";
                    case INT -> "int";
                    case LONG -> "long";
                    case SHORT -> "short";
                    case SYMBOL -> "Object";
                    case DOUBLE -> "double";
                    case VOID -> "void";
                };
        } else if (t instanceof StringType)
            return "String";
        else if (t instanceof ListType listType)
            return "List<" + toJavaType(listType.content(), TypeUseLocation.TYPE_VARIABLE) + ">";
        else if (t instanceof InterfaceType interfaceType) {
            return importIfNeeded(interfaceType.interfaceName());
        } else if (t instanceof EnumType enumType)
            return enumType.enumName();
        else if (t instanceof FrozenArrayType frozenArrayType)
            return "List<" + toJavaType(frozenArrayType.elementType(), TypeUseLocation.TYPE_VARIABLE) + ">";
        else if (t instanceof ObservableArrayType observableArrayType)
            return "List<" + toJavaType(observableArrayType.elementType(), TypeUseLocation.TYPE_VARIABLE) + ">";
        else if (t instanceof UnionType unionType) {
            return unionType.types().stream().
                    map(t2 -> toJavaType(t2, TypeUseLocation.TYPE_VARIABLE).replace("/*", "(").replace("*/", ")")).
                    collect(Collectors.joining(" or ", "/* ", " */ Object"));
        } else if (t instanceof PromiseType promiseType) {
            String j = toJavaType(promiseType.type(), TypeUseLocation.TYPE_VARIABLE);
            assert !j.equals("void");
            return "Future<" + j + ">";
        } else if (t instanceof NullableType nullableType) {
            final String s = toJavaType(nullableType.content(), TypeUseLocation.TYPE_VARIABLE);
            return switch (useLocation) {
                case RETURN_TYPE -> "Optional<" + s + ">";
                case VARIABLE_DECLARATION -> "@Nullable " + s;
                case TYPE_VARIABLE -> "/* optional */ " + s;
            };
        } else if (t instanceof BufferType bufferType) {
            return bufferType.name();
        } else if (t instanceof RecordType recordType) {
            return "Map<" + toJavaType(recordType.keyType(), TypeUseLocation.TYPE_VARIABLE) + ", "
                    + toJavaType(recordType.value(), TypeUseLocation.TYPE_VARIABLE) + ">";
        }
        throw new RuntimeException("unknown type: " + t);
    }

    private String importIfNeeded(String interfaceName) {
        unit.imports.add(interfaceName);
        return interfaceName;
    }

    private Type type(TypeWithExtendedAttributesContext t) {
        return putAttributesOnType(t.extendedAttributeList(), type(t.type_()));
    }

    private static Type handleArray(Type type, List<ArrayDimensionContext> arrayDims) {
        int arrayDimensions = arrayDims.size();
        while (arrayDimensions > 0) {
            type = new ListType(type);
            arrayDimensions--;
        }
        return type;
    }

    private Type putAttributesOnType(ExtendedAttributeListContext attrList, Type type) {
        return type;
    }

    private Type type(Type_Context type) {
        Type t;
        if (type.distinguishableType() != null) {
            DistinguishableTypeContext distinguishableType = type.distinguishableType();
            t = distinguishableType2(distinguishableType);
            t = handleArray(t, distinguishableType.arrayDimension());
            if (distinguishableType.null_().altNum == 1)
                t = new NullableType(t);
        } else if (type.ANY() != null)
            t = PrimitiveType.ANY;
        else if (type.promiseType() != null) {
            Type_Context t2 = type.promiseType().type_();
            t = t2 == null ? PrimitiveType.ANY : type(t2);
            t = new PromiseType(t);
        } else if (type.unionType() != null)
            t = new UnionType(type.unionType().unionMemberType().stream().
                    map(a -> putAttributesOnType(a.extendedAttributeList(),
                            distinguishableType2(a.distinguishableType()))).toList());
        else
            throw new RuntimeException("unknown type: " + type);

        t = handleArray(t, type.arrayDimension());
        if (type.null_() != null && type.null_().altNum == 1)
            t = new NullableType(t);
        return t;
    }

    private Type distinguishableType2(DistinguishableTypeContext t) {
        return switch (t.altNum) {
            case 1 -> primitiveType(t.primitiveType());
            case 2 -> stringType(t.stringType());
            case 3 -> {
                String identifier = t.IDENTIFIER_WEBIDL().getSymbol().getText();
                TypeWithExtendedAttributesContext typedef = typedefs.get(identifier);
                if (typedef != null)
                    yield type(typedef);
                if (enumNames.contains(identifier))
                    yield new EnumType(identifier);
                yield switch (identifier) {
                    case "UTF8String" -> StringType.UTF8String;
                    case "JSString" -> StringType.JSString;
                    case "Dictionary" -> new RecordType(StringType.DOMString, PrimitiveType.ANY);
                    case "Date" -> new InterfaceType("JSDate");

                    // Chrome-only hülyeségek
                    case "Function" -> new InterfaceType("MethodHandle");
                    case "SerializedScriptValue" -> PrimitiveType.ANY;
                    default -> new InterfaceType(identifier);
                };
            }
            case 4 -> new ListType(type(t.typeWithExtendedAttributes()));
            case 5 -> PrimitiveType.OBJECT;
            case 6 -> PrimitiveType.SYMBOL;
//            case 7 -> BufferType.values()[t.bufferRelatedType().altNum - 1];
            case 7 -> new FrozenArrayType(type(t.typeWithExtendedAttributes()));
            case 8 -> new ObservableArrayType(type(t.typeWithExtendedAttributes()));
            case 9 -> new RecordType(stringType(t.recordType().stringType()),
                    type(t.recordType().typeWithExtendedAttributes()));
            default -> throw new RuntimeException("unsupported distinguisabletype: " + t);
        };
    }

    @Nonnull
    private static StringType stringType(StringTypeContext t) {
        return switch (t.altNum) {
            case 1 -> StringType.ByteString;
            case 2 -> StringType.DOMString;
            case 3 -> StringType.USVString;
            default -> throw new RuntimeException();
        };
    }

    private Type constType(ConstTypeContext type) {
        if (type.primitiveType() != null)
            return primitiveType(type.primitiveType());

        String identifier = type.IDENTIFIER_WEBIDL().getSymbol().getText();
        TypeWithExtendedAttributesContext typedef = typedefs.get(identifier);
        if (typedef != null)
            return type(typedef);
        return new InterfaceType(identifier);
    }

    @Nonnull
    private static Type primitiveType(PrimitiveTypeContext p) {
        return switch (p.altNum) {
            case 1 -> switch (p.unsignedIntegerType().integerType().getAltNumber()) {
                case 1 -> PrimitiveType.SHORT;
                case 2 -> p.unsignedIntegerType().integerType().optionalLong().altNum == 0 ?
                        PrimitiveType.LONG : PrimitiveType.INT;
                default -> throw new UnsupportedOperationException();
            };
            case 2 -> p.unrestrictedFloatType().floatType().altNum == 0 ? PrimitiveType.FLOAT : PrimitiveType.DOUBLE;
            case 3 -> PrimitiveType.VOID;
            case 4 -> PrimitiveType.BOOLEAN;
            case 5, 6 -> PrimitiveType.BYTE;
            case 7 -> PrimitiveType.BIG_INTEGER;
            case 8 -> PrimitiveType.VOID;
            default -> throw new UnsupportedOperationException();
        };
    }

    private static final Set<String> JAVA_KEYWORDS = Set.of("public", "static", "default",
            "interface", "float", "new",
            "assert", "private", "continue",
            "native", "extends", "class",
            "import");

    private String name(String originalName) {
        if (originalName.isEmpty())
            return "_EMPTY";

        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < originalName.length(); i++) {
            if (originalName.charAt(i) == '-')
                continue;
            char ch = originalName.charAt(i);
            if (i > 0 && originalName.charAt(i - 1) == '-')
                ch = Character.toUpperCase(ch);
            if (ch == '/' || ch == '+') // MIME type-oknál van
                ch = '_';
            stringBuilder.append(ch);
        }
        if (stringBuilder.isEmpty())
            throw new RuntimeException(unit.filenames.toString());
        if (!Character.isJavaIdentifierStart(stringBuilder.codePointAt(0)) &&
                Character.isJavaIdentifierPart(stringBuilder.codePointAt(0)))
            stringBuilder.insert(0, '_');
        String newName = stringBuilder.toString();
        if (JAVA_KEYWORDS.contains(newName))
            newName += "_";

        for (int i = 0, ch; i < newName.length(); i += Character.charCount(ch)) {
            ch = newName.codePointAt(i);
            if (i == 0 ? !Character.isJavaIdentifierStart(ch) : !Character.isJavaIdentifierPart(ch))
                throw new RuntimeException("name contains invalid characters: " + originalName + " (in " + unit.filenames + ")");
        }
        return newName;
    }

    private static class Unit {

        final String name;
        final List<String> filenames = new ArrayList<>();
        final StringBuilder constants = new StringBuilder();
        final StringBuilder statics = new StringBuilder();
        final StringBuilder operations = new StringBuilder();
        final Set<String> imports = new HashSet<>();
        final List<String> implementedInterfaces = new ArrayList<>();
        boolean extensionOnly;
        String inheritFrom;
        Kind kind = Kind.INTERFACE;
        boolean callbackInterface;
        int fullDefinitionCount;
        String category;

        public Unit(String name) {
            this.name = name;
        }

        enum Kind {

            INTERFACE, DICTIONARY, ENUM
        }
    }

    private static final Set<String> FIREFOX_FILTERED_OUT_FILENAMES =
            Set.of("Localization.webidl", "MozApplicationEvent.webidl", "DOMLocalization.webidl", "AddonManager.webidl",
                    "MozFrameLoaderOwner.webidl");

    @SuppressWarnings("resource")
    public static void main(String[] args) throws IOException {
        WebIDLBindingGenerator gen = new WebIDLBindingGenerator();
        Path root = Path.of("C:\\Users\\Attila\\Documents\\f\\blink\\Source");
        try (Stream<Path> paths = Files.walk(root)) {
            for (Path p : (Iterable<Path>) paths::iterator) {
                final Path relativePath = root.relativize(p);
                String filename = relativePath.toString();
                if (!filename.endsWith(".idl") || filename.contains("tests") || filename.contains("inspector")
                        || filename.contains("DevTools") || filename.contains("testing") || filename.contains("navigatorconnect"))
                    continue;
                //if (FIREFOX_FILTERED_OUT_FILENAMES.contains(filename))
                //    continue;
                System.err.println(filename);
                try (InputStream in = Files.newInputStream(p)) {
                    WebIDLLexer lexer = new WebIDLLexer(CharStreams.fromReader(new InputStreamReader(in)));
                    CommonTokenStream tokens = new CommonTokenStream(lexer);
                    WebIDLParser parser = new WebIDLParser(tokens);
                    WebIDLParser.WebIDLContext tree = parser.webIDL();
                    gen.process(tree, filename);
                }
            }
        }

        gen.generate();

        Map<String, Writer> openedWriters = new HashMap<>();
        Map<String, List<Unit>> unitsByCategory = gen.units.values().stream().collect(groupingBy(u -> u.category));

        for (Unit u : gen.units.values()) {
            if (u.extensionOnly)
                continue;

            if (u.fullDefinitionCount == 0)
                throw new RuntimeException("no full definition of " + u.name + " " + u.filenames.toString());

            String category = u.category;

            Writer w = openedWriters.get(category);
            if (w == null) {
                w = Files.newBufferedWriter(Path.of("fodutil/source/com/flyordie/code/browserapi/" + category + ".java"));
                openedWriters.put(category, w);
                w.write("""
                        package com.flyordie.code.browserapi;
                                            
                        import javax.annotation.Nullable;
                        import java.util.List;
                        import java.util.Map;
                        import java.util.Collections;
                        import java.util.Optional;
                        import java.util.concurrent.Future;
                        import java.lang.invoke.MethodHandle;
                                            
                        import com.flyordie.code.jsinterop.Getter;             
                        import com.flyordie.code.jsinterop.Setter;
                        import com.flyordie.code.jsinterop.Name;
                        import com.flyordie.code.jsinterop.Statics;
                                                
                        """);

                Set<String> imports = new HashSet<>();
                for (Unit u2 : unitsByCategory.get(category))
                    imports.addAll(u2.imports);

                for (String importedTypeName : imports) {
                    Unit importedType = gen.units.get(importedTypeName);
                    if (importedType != null && !importedType.name.equals(importedType.category))
                        w.write("import com.flyordie.code.browserapi." + importedType.category + "." + importedType.name + ";\n");
                }
                if (!imports.isEmpty())
                    w.write("\n");


                Unit mainUnit = gen.units.get(category);
                if (mainUnit != null) {
                    gen.writeUnit(mainUnit, w, true);
                } else
                    w.write("""
                            public class $CATEGORYNAME {
                                   
                                private $CATEGORYNAME() {
                                    throw new Error("should not instantiate");
                                }
                                                    
                            """.replace("$CATEGORYNAME", category));
            }

            if (!u.name.equals(category)) // ha a unit neve ugyanaz mint a kategória neve, akkor már kiírtuk a kategória elkezdésekor toplevel classként 
                gen.writeUnit(u, w, false);
        }
        for (Writer writer : openedWriters.values()) {
            writer.write("}\n");
            writer.close();
        }
    }

    private void writeUnit(Unit u, Writer w, boolean topLevel) throws IOException {
        boolean allStatics = !u.statics.isEmpty() && !u.callbackInterface
                && u.operations.isEmpty() && u.kind == Unit.Kind.INTERFACE;

        boolean enclosedByInterface = !topLevel && units.containsKey(u.category)
                && units.get(u.category).kind == Unit.Kind.INTERFACE;

        String indent = topLevel ? "" : "    ";

        w.write(indent);
        w.write("// Generated from " + String.join(", ", u.filenames) + "\n");
        w.write(indent);
        if (allStatics)
            w.write("@Statics(\"" + u.name + "\")\n");
        else
            w.write("@Name(\"" + u.name + "\")\n");
        switch (u.kind) {
            case INTERFACE -> {
                if (u.callbackInterface)
                    w.write(indent + "@FunctionalInterface\n");
                if (enclosedByInterface)
                    w.write(indent + "interface " + u.name);
                else
                    w.write(indent + "public interface " + u.name);
            }
            case DICTIONARY -> {
                if (topLevel)
                    w.write("public class " + u.name);
                else if (enclosedByInterface)
                    w.write("    class " + u.name);
                else
                    w.write("    public static class " + u.name);
            }
            case ENUM -> {
                if (enclosedByInterface)
                    w.write(indent + "enum " + u.name);
                else
                    w.write(indent + "public enum " + u.name);
            }
        }
        if (u.inheritFrom != null)
            w.write(" extends " + u.inheritFrom);
        if (!u.implementedInterfaces.isEmpty()) {
            if (u.kind == Unit.Kind.INTERFACE)
                w.write(u.inheritFrom == null ? " extends " : ", ");
            else
                w.write(" implements ");
            int i = 0;
            for (String implementedUnitName : u.implementedInterfaces) {
                if (i++ != 0)
                    w.write(", ");
                w.write(implementedUnitName);
            }
        }
        w.write(" {\n");
        if (topLevel)
            w.write('\n');
        if (allStatics)
            w.append(u.statics);
        else {
            w.append(u.constants);
            w.append(u.operations);
        }
        if (!topLevel)
            w.write("    }\n");
        if (!u.statics.isEmpty() && !allStatics) {
            String pluralName = u.name.endsWith("y")
                    ? u.name.substring(0, u.name.length() - 1) + "ies"
                    : u.name.endsWith("s")
                    ? u.name.substring(0, u.name.length() - 1) + "es"
                    : u.name + "s";

            w.write("\n    @Statics(\"" + u.name + "\")\n");
            w.write("    public interface " + pluralName + " {\n");
            w.append(u.statics);
            w.write("    }\n");
        }
        w.write('\n');
    }

    @Nonnull
    private static String mapCategoryName(String category) {
        if (category.contains("_")) {
            StringBuilder sb = new StringBuilder();
            boolean toUpperCase = true;
            for (int i = 0, ch; i < category.length(); i += Character.charCount(ch)) {
                ch = category.codePointAt(i);
                if (ch != '_')
                    sb.appendCodePoint(toUpperCase ? Character.toUpperCase(ch) : ch);
                toUpperCase = ch == '_';
            }
            category = sb.toString();
        } else category = switch (category) {
            case "animation" -> "AnimationAPI";
            case "bluetooth" -> "BluetoothAPI";
            case "canvas2d" -> "CanvasAPI";
            case "credentialmanager" -> "CredentialManager";
            case "compositorworker" -> "CompositorWorkerAPI";
            case "cachestorage" -> "CacheStorage";
            case "crypto" -> "CryptoAPI";
            case "css" -> "CSS";
            case "dom" -> "DOM";
            case "editing" -> "EditingAPI";
            case "filesystem" -> "FileSystemAPI";
            case "frame" -> "FrameAPI"; // nem lehet toplevel class a Window, mert akkor belebonyolódna abba hogy az inner classát extendselné
            case "fileapi" -> "FileAPI";
            case "gamepad" -> "GamePad";
            case "geofencing" -> "GeoFencing";
            case "geolocation" -> "GeoLocation";
            case "html" -> "HTML";
            case "imagebitmap" -> "ImageBitmapAPI";
            case "input" -> "InputAPI";
            case "indexeddb" -> "IndexedDB";
            case "encryptedmedia", "mediarecorder", "mediastream", "mediasource", "mediasession" -> "Media";
            case "netinfo" -> "NetworkInformation";
            case "nfc" -> "NFC";
            case "page" -> "PageAPI";
            case "notifications" -> "NotificationsAPI";
            case "presentation" -> "PresentationAPI";
            case "permissions" -> "PermissionsAPI";
            case "serviceworkers" -> "ServiceWorkers";
            case "svg" -> "SVG";
            case "vr" -> "WebVR";
            case "webaudio" -> "WebAudio";
            case "webdatabase" -> "WebDatabase";
            case "webgl" -> "WebGL";
            case "webmidi" -> "WebMidi"; // ?
            case "websockets" -> "WebSockets";
            case "webusb" -> "WebUSB";
            case "xml" -> "XML";
            case "xmlhttprequest" -> "XMLHttpRequestAPI"; // szintén nem lehet toplevel class, ld. frame-nél írtak
            default -> category.substring(0, 1).toUpperCase(Locale.ROOT) + category.substring(1);
        };
        return category;
    }
}
