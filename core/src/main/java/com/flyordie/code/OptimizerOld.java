package com.flyordie.code;

import com.flyordie.code.Node.*;
import com.flyordie.code.Node.SequenceNode.MethodKey;
import com.flyordie.code.SideEffect.Read;
import com.flyordie.code.SideEffect.Write;
import com.flyordie.code.Variable.InstanceField;
import com.flyordie.code.Variable.LocalVar;

import java.util.*;
import java.util.function.Predicate;

import static org.objectweb.asm.Opcodes.*;

@Deprecated
public class OptimizerOld implements Node.Transformation{

    private Node root;

    public Node optimize(Node n) {
        assert root == null;
        root = n;

        //Thread.dumpStack();
        //System.out.println("root:" + n);
            n = n.transform(this);
        //System.out.println("done:" + n);
        if (n == null)
            return new NopNode("asdf");
        return n;
    }

    @Override
    public Node enter(Node node) {
        return node;
    }

    @Override
    public Node exit(Node node) {
        Node oldNode = node;
        if (node instanceof IfElseNode) {
            IfElseNode ifNode = (IfElseNode) node;
            if (ifNode.successBranch() instanceof ConstantNode && Objects.equals(((ConstantNode) ifNode.successBranch()).value, 1) &&
                    ifNode.failureBranch() instanceof ConstantNode && Objects.equals(((ConstantNode) ifNode.failureBranch()).value, 0))
                node = ifNode.conditionSlot.get();
            else if (ifNode.successBranch() instanceof ConstantNode && Objects.equals(((ConstantNode) ifNode.successBranch()).value, 0) &&
                    ifNode.failureBranch() instanceof ConstantNode && Objects.equals(((ConstantNode) ifNode.failureBranch()).value, 1)) {

                node = negate(ifNode.conditionSlot.get());
                System.out.println(oldNode + " " + oldNode.hashCode() + " -> " + ifNode + " " + ifNode.hashCode() + "->" + node + " " + node.hashCode());
            } else if (ifNode.conditionSlot.get() instanceof ConstantNode) {
                switch ((int) ((ConstantNode) ifNode.conditionSlot.get()).value) {
                    case 0:
                        node = ifNode.failureBranch();
                        break;
                    case 1:
                        node = ifNode.successBranch();
                        break;
                    default:
                        throw new RuntimeException("invalid value in condition: " + ifNode);
                }
            } else if (ifNode.successBranch() == null && ifNode.failureBranch() != null) {
                ifNode.successBranch(ifNode.failureBranch());
                ifNode.failureBranch(null);
                UnaryNumericOpNode newCondition = new UnaryNumericOpNode(ifNode.conditionSlot.get(), NumericOps.ops[IFEQ]);
                ifNode.conditionSlot.set(newCondition);
            }
        }

        if (node instanceof UnaryNumericOpNode) {
            UnaryNumericOpNode n1 = (UnaryNumericOpNode) node;
            if (n1.inputSlot.get() instanceof BinaryNumericOpNode) {
                BinaryNumericOpNode n2 = (BinaryNumericOpNode) n1.inputSlot.get();
                if (n1.op.code == IFEQ) {
                    int negated = negated(n2.op.code);
                    if (negated != -1) {
                        node = new BinaryNumericOpNode(n2.input1Slot.get(), n2.input2Slot.get(), NumericOps.ops[negated]);
                    }
                }
            } else if (n1.inputSlot.get() instanceof UnaryNumericOpNode) {
                UnaryNumericOpNode n2 = (UnaryNumericOpNode) n1.inputSlot.get();
                if (n1.op.code == IFEQ) {
                    int negated = negated(n2.op.code);
                    if (negated != -1) {
                        node = new UnaryNumericOpNode(n2.inputSlot.get(), NumericOps.ops[negated]);
                    }
                }
            }
        }
        if (node instanceof SequenceNode) {
            SequenceNode seq = (SequenceNode) node;
            if (seq.nodes.isEmpty())
                return null;
            if (seq.nodes.get(seq.nodes.size() - 1) instanceof ReturnNode) {
                ReturnNode ret = (ReturnNode) seq.nodes.get(seq.nodes.size() - 1);
                if (ret.returnFrom == seq.methodKey && ret.returnValueSlot.get() == null)
                    seq.nodes.remove(seq.nodes.size() - 1);
            }
            if (seq.nodes.size() == 1) {
                if (seq.nodes.get(0) instanceof ReturnNode) {
                    ReturnNode ret = (ReturnNode) seq.nodes.get(0);
                    if (ret.returnFrom == seq.methodKey)
                        node = ret.returnValueSlot.get();
                } else if (seq.resultSlot.get() == null || seq.resultSlot.get() == seq.nodes.get(0))
                    node = seq.nodes.get(0);
            } else if (seq.nodes.size() == 2 && seq.nodes.get(1) instanceof ReturnNode) {
                ReturnNode ret = (ReturnNode) seq.nodes.get(1);
                if (ret.returnValueSlot.get().equals(seq.nodes.get(0)) && ret.returnFrom == seq.methodKey)
                    node = ret.returnValueSlot.get();
            } else {
                // nested sequencenode-ok laposítása
                List<Node> l = new ArrayList<>();
                flattenSequenceNode(seq, l);
                seq.nodes.clear();
                seq.nodes.addAll(l);

                System.out.println("BEFORE: " + seq);
                new VariableVirtualizer(seq).virtualizeVariables();
                System.out.println("AFTER: " + seq);

                // redundáns node-okat kiszedjük
                l.clear();
                flattenSequenceNode(seq, l);
                seq.nodes.clear();
                seq.nodes.addAll(l);

                //
                seq.nodes.removeIf(this::isInlineable);
            }
        }

        return node;
    }


    private void flattenSequenceNode(SequenceNode seq, List<Node> result) {
        for (Node n : seq.nodes) {
            if (n instanceof SequenceNode) {
                SequenceNode seq2 = (SequenceNode) n;
                if (!hasReturnStatement(seq2, seq2.methodKey)) {
                    result.addAll(seq2.nodes);
                    seq2.nodes.clear();
                    continue;
                }
            }

            result.add(n);
        }
    }

    private class VariableVirtualizer {

        private final SequenceNode seq;
        private final Map<Variable, Node> vars = new HashMap<>();
        private final List<Node> newNodes = new ArrayList<>();

        public VariableVirtualizer(SequenceNode seq) {
            this.seq = seq;
        }

        private void virtualizeVariables() {
            assert newNodes.isEmpty();
            for (Node n : seq.nodes) {
                System.out.println("process subnode: " + n);

                //List<Node> writes = findAll(n, n2 -> writtenVariable(n2) != null);
                //writes.remove(n);
                //writes.forEach(this::handleConditionalWrite);

                //System.out.println("ABEFORE: "+n);
                List<Variable> readedVars = n.sideEffects().stream().
                        filter(se -> se instanceof Read).map(se -> ((Read) se).variable()).toList();
                List<Variable> writtenVars = n.sideEffects().stream().
                        filter(se -> se instanceof Write).map(se -> ((Write) se).variable()).toList();

                // TODO flush

                if (true)
                    throw new UnsupportedOperationException("TODO");
                //n = n.optimizeSubtreeAndInputs(n2 -> vars.getOrDefault(n2.readenVariable(), n2));

                WrittenVariableAndValue write = n.writtenVariable();
                if (write != null) {
                    n = write.value();
                    vars.put(write.variable(), write.value());
                }
                System.out.println("end process subnode: " + n);

                //System.out.println("AAFTER:"+n);
                if (!newNodes.contains(n))
                    newNodes.add(n);
            }
            seq.nodes.clear();
            seq.nodes.addAll(newNodes);
        }

        private Node makeWrite(Variable var, Node value) {
            if (var instanceof LocalVar)
                return new WriteLocalVar((LocalVar) var, value);
            else {
                InstanceField f = (InstanceField) var;
                return new PutFieldNode(new ObjectNode(f.object()), f.field(), value);
            }
        }
    }

    private static boolean hasReturnStatement(Node n, MethodKey target) {
        return (n instanceof ReturnNode && ((ReturnNode) n).returnFrom == target)
                || n.children().stream().anyMatch(n2 -> hasReturnStatement(n2, target));
    }

    private static List<Node> findAll(Node n, Predicate<Node> predicate) {
        List<Node> l = new ArrayList<>();
        findAllImpl(n, predicate, l);
        return l;
    }

    private static void findAllImpl(Node n, Predicate<Node> predicate, List<Node> result) {
        if (predicate.test(n))
            result.add(n);
        n.inputs().forEach(n2 -> findAllImpl(n2, predicate, result));
        n.children().forEach(n2 -> findAllImpl(n2, predicate, result));
    }

    /**
     * @return ez 0/1 értéket adjon vissza
     */
    private static Node negate(Node n) {
        return new UnaryNumericOpNode(n, NumericOps.ops[IFEQ]);
    }

    private static int negated(int opcode) {
        switch (opcode) {
            case IFNE:
                return IFEQ;
            case IFEQ:
                return IFNE;
            case IFLT:
                return IFGE;
            case IFLE:
                return IFGT;
            case IFGT:
                return IFLE;
            case IFGE:
                return IFLT;
            case IF_ICMPEQ:
                return IF_ICMPNE;
            case IF_ICMPNE:
                return IF_ICMPEQ;
            case IF_ICMPLT:
                return IF_ICMPGE;
            case IF_ICMPLE:
                return IF_ICMPGT;
            case IF_ICMPGT:
                return IF_ICMPLE;
            case IF_ICMPGE:
                return IF_ICMPLT;
            default:
                return -1;
        }
    }

    private boolean isInlineable(Node n) {
        assert n != null;

        if (n.usages().isEmpty() && n.sideEffects().stream().allMatch(se -> se instanceof SideEffect.Read)) {
            return true;
        }
        if (hasExactlyOneSafeUsage(n)) {
            return true;
        }
        if (n instanceof ConstantNode)
            return true;
        if (n instanceof SequenceNode) {
            SequenceNode seq = (SequenceNode) n;
            if (seq.nodes.isEmpty())
                return true;
        }
        if (n instanceof IfElseNode) {
            IfElseNode ifNode = (IfElseNode) n;
            if ((ifNode.successBranch() == null || isInlineable(ifNode.successBranch())) &&
                    (ifNode.failureBranch() == null || isInlineable(ifNode.failureBranch())))
                return true;
        }
        if (n instanceof LoopNode && ((LoopNode) n).bodySlot.get() instanceof SequenceNode) {
            SequenceNode seq = (SequenceNode) ((LoopNode) n).bodySlot.get();
            int i = seq.nodes.size() - 1;
            if (seq.nodes.get(i) instanceof ContinueLoopNode &&
                    ((ContinueLoopNode) seq.nodes.get(i)).loop == ((LoopNode) n).loopKey)
                seq.nodes.remove(i);
        }
        return false;
    }

    private boolean hasExactlyOneSafeUsage(Node node) {
        Node container = node.parent();
        assert container != null : node;

        List<Node> containerChildren = container.children();
        int thisIndex = containerChildren.indexOf(node);

        for (Node usage : node.usages()) {
            if (container != usage.parent())
                return false;

            int usageIndex = containerChildren.indexOf(usage);
            assert usageIndex >= thisIndex : node + ", " + container + ", " + thisIndex + " vs " + usageIndex;

            for (int i = thisIndex + 1; i < usageIndex; i++) {
                List<SideEffect> se = containerChildren.get(i).sideEffects();
                if (se.stream().anyMatch(se2 -> se2.interferesWith(usage.sideEffects())) ||
                        usage.sideEffects().stream().anyMatch(se2 -> se2.interferesWith(se))) {
                    System.out.println("UNSAFE: " + usage + " input " + node + " because " + containerChildren.get(i));
                    return false;
                }
            }
        }

        return node.usages().size() == 1;
    }
}
