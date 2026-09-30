package com.flyordie.code;

import com.flyordie.code.Clazz.Method;
import com.flyordie.code.CompilationContext.TransformationChain;
import com.flyordie.code.Node.*;
import com.flyordie.code.Variable.LocalVar;
import com.flyordie.code.js.JSReplacementProvider;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class OptPhase3 implements Transformation {

    private final Set<Variable> usedVars = new HashSet<>();
    private final Method m;
    private final Set<Node> removed = new HashSet<>();
    private Node root;

    public OptPhase3(MethodCompilationContext mcc) {
        this.m = mcc.method;
    }

    @Override
    public void reset() {
        usedVars.clear();
        removed.clear();
        root = null;
    }

    @Override
    public Node enter(Node node) {
        if (root == null) {
            root = node;
            root.walk(n -> {
                Variable var = n.readenVariable();
                if (var != null && var.isExact())
                    usedVars.add(var);
            });
        }

        if (node != null) {
            WrittenVariableAndValue write = node.writtenVariable();
            if (write != null && write.variable() instanceof LocalVar && !usedVars.contains(write.variable())) {
                return new NopNode("eliminated local variable write");
            }
        }
        if (node instanceof ObjectNode n) {
            if (n.usages().stream().allMatch(u -> u instanceof PutFieldNode putfield && putfield.objectSlot.get() == n)) {
                final NopNode eliminatedObject = new NopNode("eliminated object");
                removed.add(eliminatedObject);
                return eliminatedObject;
            }
        }
        if (node instanceof PutFieldNode putfield) {
            if (removed.contains(putfield.objectSlot.get()))
                return new NopNode("eliminated scalarized object putfield: " + putfield);
        }
        return node;
    }

    @Override
    public Node exit(Node node) {
        if (node instanceof SequenceNode seq) {
            seq.nodes.removeIf(n -> n.sideEffects().isEmpty() && !n.hasUsagesIncludingChildren());
        }
        return node;
    }
}
