package com.flyordie.code;

import com.flyordie.code.js.JSReplacementProvider;

import java.util.List;

// lehet hogy át kéne nevezni ClassValueOptPhase-re
public class OptPhase4 implements Node.Transformation {

    private final MethodCompilationContext mcc;
    private final CompilationContext ctx;

    public OptPhase4(MethodCompilationContext mcc) {
        this.ctx = mcc.compilationContext;
        this.mcc = mcc;
    }

    @Override
    public Node enter(Node node) {
        return node;
    }

    @Override
    public Node exit(Node node) {
        Node n2 = null;
        if (node instanceof Node.InvokeSpecialOrStatic invokeSpecialOrStatic &&
                invokeSpecialOrStatic.method.clazz.allAncestorTypesAndThis.stream().
                        anyMatch(cv -> cv.knownClass == KnownClass.ClassValue) &&
                invokeSpecialOrStatic.method.name.equals("get") && invokeSpecialOrStatic.method.type().parameterTypes().size() == 1) {
            // lehet invokespecial is ClassValue.get-ből, mert Optimizer1 észreveszi hogy konstans a receivere
            n2 = handleClassValueCall(invokeSpecialOrStatic.method, invokeSpecialOrStatic.args, invokeSpecialOrStatic);
        } else if (node instanceof Node.InvokeVirtualOrInterface invokeNode &&
                invokeNode.method.clazz.allAncestorTypesAndThis.stream().
                        anyMatch(cv -> cv.knownClass == KnownClass.ClassValue) &&
                invokeNode.method.name.equals("get") && invokeNode.method.type().parameterTypes().size() == 1) {
            n2 = handleClassValueCall(invokeNode.method, invokeNode.args, invokeNode);
        }
        return n2 == null ? node : n2;
    }

    @SuppressWarnings("RedundantCast")
    private Node handleClassValueCall(Clazz.Method method, List<Node> args, Node invokeNode) {
        if (args.get(0) instanceof Node.ConstantNode c1 && c1.value != null) {
            if (((Interpreter.ClassObj) c1.value).type().name.equals(JSReplacementProvider.EnumConstantsCV.class.getName().replace('.', '/')))
                return new ClassValueNode((Interpreter.ClassObj) c1.value,
                        ctx.findClass(KnownClass.Enum), args.get(1));
            if (args.get(1) instanceof Node.ConstantNode c2) {
                Interpreter.ClassObj classObj = ctx.interpreter().fromType(((Type) c2.value));
                return new Node.ConstantNode(ctx.interpreter().execute(method, c1.value, classObj).orElseThrow());
            } else if (args.get(1) instanceof Node.TypeOfNode typeof && typeof.inputSlot.get().type() != null) {
                return new ClassValueNode((Interpreter.ClassObj) c1.value, typeof.inputSlot.get().type(), typeof);
            }
        }

        mcc.markAsNonEmittable(invokeNode, "uneliminable ClassValue call: " + invokeNode);
        return invokeNode;
    }
}
