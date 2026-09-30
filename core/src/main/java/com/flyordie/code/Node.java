package com.flyordie.code;

import com.flyordie.code.Clazz.Field;
import com.flyordie.code.Clazz.Method;
import com.flyordie.code.Node.ErrorNode.ErrorType;
import com.flyordie.code.Node.LoopNode.LoopKey;
import com.flyordie.code.Node.SequenceNode.MethodKey;
import com.flyordie.code.Node.Switch.SwitchBreakKey;
import com.flyordie.code.NumericOps.OpInfo;
import com.flyordie.code.OptPhase2.InlineableSequenceNode;
import com.flyordie.code.SideEffect.Allocation;
import com.flyordie.code.SideEffect.BreakControlFlow;
import com.flyordie.code.Type.ArrayType;
import com.flyordie.code.Type.PrimitiveType;
import com.flyordie.code.Variable.ArrayElement;
import com.flyordie.code.Variable.InstanceField;
import com.flyordie.code.Variable.LocalVar;
import com.flyordie.code.Variable.LocalVar.MethodIdentity;
import com.flyordie.code.Variable.StaticField;
import com.flyordie.code.js.JSInteropProvider.NativeMethodKind;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.flyordie.code.CompilationContext.context;
import static java.util.Collections.emptyList;
import static java.util.function.UnaryOperator.identity;
import static org.objectweb.asm.Opcodes.*;

public abstract class Node {

    public boolean root;
    private Node parent;
    private Node origParent;
    private final List<Node> inputs = new ArrayList<>();
    private final List<Node> usages = new ArrayList<>();
    private final AllChildrenList inputList = new AllChildrenList(true);
    private final AllChildrenList childList = new AllChildrenList(false);
    private final List<ChildContainer> inputContainers = new ArrayList<>();
    private final List<ChildContainer> childContainers = new ArrayList<>();

    List<String> nonEmittable;
    public String comment;
    public final Location location = Location.location();
    public boolean invalid;

    public abstract Type type();

    public abstract List<SideEffect> sideEffects();

    /**
     * Ezt akkor kell felülírni, ha csak változóolvasást végez az utasítás és annak a változónak az értékét adja
     * vissza.
     */
    public Variable readenVariable() {
        return null;
    }

    public WrittenVariableAndValue writtenVariable() {
        return null;
    }

    public final boolean alwaysBreaks() {
        if (parent instanceof SequenceNode && parent.parent instanceof Switch)
            return false;
        return alwaysBreaksImpl();
    }

    protected boolean alwaysBreaksImpl() {
        return false;
    }

    public record WrittenVariableAndValue(Variable variable, Node value) {
    }

    public boolean deterministicInputEvaluationOrder() {
        // feltesszük, hogy nincs olyan node, aminek pontosan egy inputja van, de nem biztos hogy azt ki is értékeli
        return inputs.size() <= 1;
    }

    public String preferredName() {
        return getClass().getSimpleName();
    }

    public List<Node> inputs() {
        return inputList;
    }

    /**
     * @return tartalmazhat null elemeket is
     */
    public List<Node> children() {
        return childList;
    }

    public boolean hasParent() {
        return parent != null;
    }

    public Node parent() {
        if (parent == null)
            throw new IllegalStateException();
        return parent;
    }

    // TODO usagesben benne kéne lennie hogy ha if-else-ben van és számít az értéke, de egyelőre
    public List<Node> usages() {
        return usages.stream().filter(Node::isAttached).toList();
    }

    public boolean hasUsagesIncludingChildren() {
        // majd kéne nézni usages() felhasználásait, hogy nem kéne-e lecserélni erre
        // OptPhase3-ban volt lényeges, mert eltüntetett olyan seqnode-okat, amikben volt egy node amire később volt
        // hivatkozás.
        // pl. ez a datamapperben lévő kifejezés nem fordult le:
        // n instanceof UnionNodeOrPseudoNode u && u.containsRealNode() || n instanceof NodeWrapper
        if (!usages().isEmpty())
            return true;
        return children().stream().anyMatch(n -> n != null && n.hasUsagesIncludingChildren());
    }

    public List<Node> usages2() {
        return usages.stream().filter(Node::isAttached2).toList();
    }

    public boolean isAttached() {
        if (sideEffects().isEmpty())
            return true;
        return parent != null || root;
/*
        Node n = this;
        while (n.parent != null) {
            assert !n.root;
            n = n.parent;
        }
        return n.root;*/
    }


    public boolean hasAttachedUsage() {
        return usages.stream().anyMatch(Node::isAttached2);
    }

    public boolean isAttached2() {
        return parent != null || root || hasAttachedUsage();
    }

    public boolean hasAncestor(Node n) {
        for (Node m = this; m != null; m = m.parent)
            if (m == n)
                return true;
        return false;
    }

    public void walk(Consumer<Node> consumer) {
        consumer.accept(this);
        for (Node n : children()) {
            if (n != null) {
                n.walk(consumer);

                if (n.alwaysBreaks()) // ezt hosszabb távon ki kéne szedni. valamint transformer szintén.
                    break;
            }
        }
    }

    public void walk2(Consumer<Node> consumer) {
        consumer.accept(this);
        children().forEach(n -> {
            if (n != null)
                n.walk2(consumer);
        });
        inputs().forEach(n -> {
            if (n != null)
                n.walk2(consumer);
        });
    }

    public boolean anyMatchesInSubtree(Predicate<Node> predicate) {
        if (predicate.test(this))
            return true;
        for (Node n : children())
            if (n != null && n.anyMatchesInSubtree(predicate))
                return true;
        return false;
    }

    public Node copyInfoFrom(Node n) {
        if (n.nonEmittable != null)
            n.nonEmittable.forEach(this::markAsNonEmittable);
        return this;
    }

    void markAsNonEmittable(String reason) {
        if (nonEmittable == null)
            nonEmittable = new ArrayList<>();
        nonEmittable.add(reason);
    }

    public final Node transform(Transformation transformation) {
        Node result = new Transformer(transformation).doTransform(this, true);
        if (result == null) {
            return new NopNode("empty transform result");
        }
        if (root && this != result) {
            assert !result.root;
            result.root = true;
        }

        if (false) {
            // lassú ellenőrzés, ezért kikapcsoljuk
            result.walk2(n -> {
                assert !(n instanceof CheckCast cc) || cc.isAttached();
            });
        }

        return result;
    }

    public interface Transformation {

        default void reset() {
        }

        Node enter(Node node);

        Node exit(Node node);
    }

    private class Transformer {

        private final Transformation transformation;

        public Transformer(Transformation transformation) {
            this.transformation = transformation;
            transformation.reset();
        }

        Node doTransform(Node node, boolean replaceInParent) {
            if (node != null)
                node.verifyUsagesConsistent();
            Node prev = node, prev2 = node;
            assert node == null || node.parent == null || node.parent.childList.stream().filter(n -> Objects.equals(prev2, n)).count() <= 1;
            try (var ignored = node == null ? null : Location.with(node.location)) {
                node = transformation.enter(node);
            }
            afterNodeTransformed(prev, node);
            prev = node;
            if (node == null) {
                if (replaceInParent && prev2 != null && prev2.parent != null)
                    prev2.parent.childList.remove(prev);
                try (var ignored = prev2 == null ? null : Location.with(prev2.location)) {
                    transformation.exit(node);
                }
                return null;
            }

            // lehet hogy egy optimalizáció a belsejében dependelne arra, hogy a mostani node
            // be van-e rakva valahova
            if (replaceInParent && prev2 != null && prev2.parent != null)
                prev2.parent.childList.set(prev2.parent.childList.indexOf(prev2), node);

            if (node instanceof SequenceNode seq) {
                inNodes:
                for (ListIterator<Node> li = seq.nodes.listIterator(); li.hasNext(); ) {
                    Node n = li.next();
                    n = doTransform(n, true);
                    if (n instanceof OptPhase2.InlineableSequenceNode inlineableSequenceNode) {
                        li.remove();
                        List<Node> nodes = new ArrayList<>(inlineableSequenceNode.nodes);
                        inlineableSequenceNode.nodes.clear();
                        for (Node n2 : nodes) {
                            li.add(n2);
                            if (n2.alwaysBreaks())
                                break inNodes;
                        }
                    } else {
                        if (n == null)
                            li.remove();
                        else {
                            li.set(n);
                            if (n.alwaysBreaks()) // TODO
                                break;
                        }
                    }
                }
            } else
                node.children().replaceAll(n -> doTransform(n, true));
            try (var ignored = Location.with(node.location)) {
                node = transformation.exit(node);
            }
            afterNodeTransformed(prev, node);
            return node;
        }

        private void afterNodeTransformed(Node prev, Node node) {
            if (prev != node) {
                Node.replaceInInputsOfUsages(prev, node instanceof InlineableSequenceNode isn ? isn.resultSlot.get() : node);
                assert prev.usages.stream().noneMatch(Node::isAttached);
            }
            if (node != null) {
                node.verifyUsagesConsistent();
            }
        }
    }

    // ez is másolja a methodidentityket
    @Override
    public final Node clone() {
        return new Cloner(identity()).clone(this);
    }

    public final Node clone(UnaryOperator<Location> locationFunction) {
        return new Cloner(locationFunction).clone(this);
    }

    public static class Cloner {

        private final Map<Node, Node> nodes = new HashMap<>();
        private final Set<Node> cloneInProgress = new LinkedHashSet<>();
        private final UnaryOperator<Location> locationFunction;
        private final Map<Object, Object> keys = new HashMap<>();
        public boolean preserveKeys;

        public Cloner(UnaryOperator<Location> locationFunction) {
            this.locationFunction = locationFunction;
        }

        public Node clone(Node n) {
            if (n == null)
                return null;

            Node n2 = nodes.get(n);
            if (n2 == null) {
                if (!cloneInProgress.add(n))
                    throw new RuntimeException(cloneInProgress.toString());
                try (var ignored = Location.with(locationFunction.apply(n.location))) {
                    n2 = n.cloneImpl(this);
                    n2.root = n.root;
                    assert n2.nonEmittable == null;
                    n2.copyInfoFrom(n);
                    nodes.put(n, n2);
                } finally {
                    cloneInProgress.remove(n);
                }
            }
            return n2;
        }

        public Node clone(InputSlot slot) {
            Node n = slot.get();
            if (n == null)
                return null;
            assert n.isAttached();
            Node n2 = nodes.get(n);
            assert n2 != null : "no existing clone for " + n;
            assert n2.isAttached() : "clone not attached: " + n2;
            return n2;
        }

        public List<Node> clone(List<Node> n) {
            return n.stream().map(this::clone).toList();
        }

        public void cloneFromTo(ChildSlot from, ChildSlot to) {
            to.set(clone(from.get()));
        }

        public void cloneFromTo(List<Node> from, List<Node> to) {
            for (Node n : from)
                to.add(clone(n));
        }

        @SuppressWarnings("unchecked")
        public <K> K cloneKey(K k, Supplier<K> supplier) {
            if (preserveKeys)
                return k;
            return (K) keys.computeIfAbsent(k, __ -> supplier.get());
        }

        @SuppressWarnings("unchecked")
        public <K> K cloneKey2(K k, UnaryOperator<K> supplier) {
            if (preserveKeys)
                return k;
            return (K) keys.computeIfAbsent(k, __ -> supplier.apply(k));
        }

        public Variable var(Variable var) {
            if (var instanceof LocalVar lv)
                return new LocalVar(lv.method(), lv.number(), lv.kind(),
                        cloneKey(lv.methodIdentity(), MethodIdentity::new));
            else
                return var;
        }

    }

    protected abstract Node cloneImpl(Cloner cloner);

    private class AllChildrenList extends AbstractList<Node> {

        private List<Node> children = emptyList();
        private List<ChildContainer> containers = emptyList();

        private final boolean inputs;

        public AllChildrenList(boolean inputs) {
            this.inputs = inputs;
        }

        void invalidate() {
            children = null;
        }

        private void ensureInitialized() {
            if (children == null) {
                children = new ArrayList<>();
                containers = new ArrayList<>();
                for (ChildContainer c : inputs ? inputContainers : childContainers) {
                    List<Node> c1 = c.values().toList();
                    children.addAll(c1);
                    containers.addAll(Collections.nCopies(c1.size(), c));
                }
            }
        }

        @Override
        public Node get(int index) {
            ensureInitialized();
            return children.get(index);
        }

        @Override
        public int size() {
            ensureInitialized();
            return children.size();
        }

        @Override
        public Node set(int index, Node element) {
            ensureInitialized();
            Node prev = children.get(index);
            containers.get(index).replace(prev, element);
            return prev;
        }
    }

    private interface ChildContainer {

        Stream<Node> values();

        void replace(Node oldNode, Node newNode);
    }

    public class ChildSlot {

        private Node value;

        {
            (this instanceof InputSlot ? inputContainers : childContainers).add(new ChildContainer() {
                @Override
                public Stream<Node> values() {
                    return Stream.of(value);
                }

                @Override
                public void replace(Node oldNode, Node newNode) {
                    assert value == oldNode;
                    set(newNode);
                }
            });
        }

        public ChildSlot() {
        }

        public ChildSlot(Node value) {
            set(value);
        }

        public Node get() {
            if (value != null)
                if (this instanceof InputSlot)
                    assert value.usages.contains(Node.this);
                else
                    assert value.parent == Node.this;
            return value;
        }

        public void set(Node node) {
            notifyChange(this.value, this.value = node);
            if (node != null)
                if (this instanceof InputSlot)
                    assert node.usages.contains(Node.this);
                else
                    assert node.parent == Node.this;
            assert node == value;
        }

        public void notifyChange(Node oldValue, Node value) {
            childChanged(oldValue, value);
        }
    }

    public class InputSlot extends ChildSlot {

        public InputSlot() {
        }

        public InputSlot(Node value) {
            set(value);
        }

        @Override
        public void notifyChange(Node oldValue, Node value) {
            changeInput(oldValue, value);
        }
    }

    public class ChildList extends AbstractList<Node> {

        private final List<Node> l = new ArrayList<>();

        {
            (this instanceof InputList ? inputContainers : childContainers).add(new ChildContainer() {
                @Override
                public Stream<Node> values() {
                    return l.stream();
                }

                @Override
                public void replace(Node oldNode, Node newNode) {
                    Node n = set(l.indexOf(oldNode), newNode);
                    assert n == oldNode;
                }
            });
        }

        @Override
        public Node get(int index) {
            return l.get(index);
        }

        @Override
        public int size() {
            return l.size();
        }

        @Override
        public Node set(int index, Node element) {
            Node prev = l.set(index, element);
            assert element != null;
            notifyChange(prev, element);
            return prev;
        }

        @Override
        public void add(int index, Node element) {
            l.add(index, element);
            notifyChange(null, element);
        }

        @Override
        public Node remove(int index) {
            Node e = l.remove(index);
            notifyChange(e, null);
            return e;
        }

        protected void notifyChange(Node oldValue, Node newValue) {
            childChanged(oldValue, newValue);
        }
    }

    public class InputList extends ChildList {

        public InputList() {
        }

        public InputList(Node[] nodes) {
            addAll(List.of(nodes));
        }

        public InputList(List<Node> nodes) {
            assert !nodes.contains(null);
            addAll(nodes);
        }

        @Override
        protected void notifyChange(Node oldValue, Node newValue) {
            changeInput(oldValue, newValue);
        }
    }


    private void childChanged(Node oldValue, Node newValue) {
        if (oldValue == newValue)
            return;

        if (newValue != null && newValue.parent != null)
            if (newValue.parent == this)
                throw new IllegalArgumentException("child would be duplicate: " + newValue + " (container: " + this + ")");
            else
                throw new IllegalArgumentException(newValue + " has already attached to a parent: " + newValue.parent);

        if (oldValue != null) {
            assert oldValue.parent == this;
            oldValue.parent = null;
        }

        if (newValue != null) {
            if (newValue.origParent == null)
                newValue.origParent = this;
            newValue.parent = this;
        }

        childList.invalidate();
    }

    private void changeInput(Node oldInput, Node newInput) {
        assert newInput != this;

        if (oldInput != null) {
            boolean removed = inputs.remove(oldInput);
            assert removed;
            removed = oldInput.usages.remove(this);
            assert removed;
        }
        if (newInput != null) {
            inputs.add(newInput);
            newInput.usages.add(this);
        }

        inputList.invalidate();
    }

    public static void replaceInInputsOfUsages(@Nonnull Node oldNode, @Nullable Node newNode) {
        oldNode.verifyUsagesConsistent();
        for (Node usage : List.copyOf(oldNode.usages)) {

            // kéne asserteket rakni attachedre, amikor annak kell lennie.
            // egy óráig tartott kideríteni, hogy EndThrow::toStringből azért hivatkozik nem létező változóra
            // a JS-ben, mert egy MH.invokeExact-ot elfelejtette attacholni
            // MethodParser::handleRecordToString

            if (usage.isAttached()) {
                // InputSlot esetén szabályos ha nullra cseréljük, csak InputList esetén nem
                // assert newNode != null : oldNode + ", " + newNode + ", " + usage;
                usage.inputs().replaceAll(n -> n == oldNode ? newNode : n);
            }
        }
    }

    private void verifyUsagesConsistent() {
        assert usages.stream().allMatch(u -> u.inputs().contains(this)) : this + ", " + usages;
    }

    public sealed interface Returnable permits SequenceNode.MethodKey, LoopNode.LoopKey, SwitchBreakKey {
    }

    public static class ConstantNode extends Node {
        public final Object value;

        public ConstantNode(Object value) {
            if (value instanceof Boolean b)
                value = b ? 1 : 0;
            this.value = value;
            if (value != null && value.equals(596040))
                System.out.println();
        }

        @Override
        public Type type() {
            if (value == null)
                return context().findClass(KnownClass.OBJECT);
            if (value instanceof Byte)
                return PrimitiveType.B;
            if (value instanceof Short)
                return PrimitiveType.S;
            if (value instanceof Character)
                return PrimitiveType.C;
            if (value instanceof Boolean)
                return PrimitiveType.Z;
            if (value instanceof Integer)
                return PrimitiveType.I;
            if (value instanceof Long)
                return PrimitiveType.J;
            if (value instanceof Float)
                return PrimitiveType.F;
            if (value instanceof Double)
                return PrimitiveType.D;
            if (value instanceof String)
                return context().findClass(KnownClass.STRING);
            if (value instanceof Interpreter.Obj obj)
                return obj.type();
            if (value instanceof MethodType)
                return context().findClass(KnownClass.METHOD_TYPE);
            if (value instanceof Type)
                return context().findClass(KnownClass.CLASS);
            throw new UnsupportedOperationException(value.getClass().getName());
        }

        @Override
        public List<SideEffect> sideEffects() {
            return emptyList();
        }

        @Override
        public String toString() {
            return "const " + value;
        }

        @Override
        public String preferredName() {
            return "const";
        }

        @Override
        protected Node cloneImpl(Cloner cloneContext) {
            return new ConstantNode(value);
        }
    }

    public static class SequenceNode extends Node {

        public final MethodKey methodKey;

        public final List<Node> nodes = new ChildList();

        public final InputSlot resultSlot = new InputSlot();

        public SequenceNode(MethodKey mk, Node... nodes) {
            this(mk, false, nodes);
        }

        // ha notRresultSlotIfVoid defaultban true, akkor valami kavarodás lesz
        // LambdaForm::toString (vagy LambdaForm$Name::toString) környékén, és InstanceOf
        // nodeok kikerülnek a sequencenodeokból, ezáltal CFinalTransformer nem találja meg őket
        public SequenceNode(MethodKey mk, boolean notResultSlotIfVoid, Node... nodes) {
            this(mk);
            this.nodes.addAll(Arrays.asList(nodes));
            final Node n = nodes[nodes.length - 1];
            if (!notResultSlotIfVoid || n.type() != PrimitiveType.V)
                this.resultSlot.set(n);
        }

        public SequenceNode(MethodKey methodKey) {
            this.methodKey = methodKey;
        }

        @Override
        public Type type() {
            Type[] t = new Type[]{null};
            if (resultSlot.get() != null) {
                t[0] = Objects.requireNonNull(resultSlot.get().type());
            }
            walk(n -> {
                if (n instanceof ReturnNode ret && ret.returnFrom == methodKey) {
                    Node retVal = ret.returnValueSlot.get();
                    Type retValType = retVal == null ? PrimitiveType.V : retVal.type();
                    assert retValType != null;
                    Type lub = context().lub(t[0], retValType);
                    assert lub != null : t[0] + ", " + retValType;
                    t[0] = lub;
                }
            });
            return t[0] == null ? PrimitiveType.V : t[0];
        }

        @Override
        public List<SideEffect> sideEffects() {
            return nodes.stream().flatMap(n -> n.sideEffects().stream()).collect(Collectors.toList());
        }

        @Override
        public String toString() {
            return "SequenceNode" + hashCode() + " " + methodKey + " " + (comment == null ? "" : "(" + comment + ")") + "{\n" +
                    nodes.stream().map(n -> "    " + n.toString().replace("\n", "\n    ") + "\n").collect(Collectors.joining()) +
                    '}';
        }

        @Override
        protected Node cloneImpl(Cloner cloner) {
            SequenceNode seq = new SequenceNode(cloner.cloneKey2(methodKey, MethodKey::clone));
            cloner.cloneFromTo(nodes, seq.nodes);
            cloner.cloneFromTo(resultSlot, seq.resultSlot);
            return seq;
        }

        public static final class MethodKey implements Returnable, Cloneable {

            public final Method method;
            public final String description;

            public MethodKey(String description) {
                this.description = description;
                this.method = null;
            }

            public MethodKey(Method method) {
                this.method = method;
                this.description = method.toShortString();
            }

            @Override
            public String toString() {
                return "MK " + description;
            }

            @Override
            public MethodKey clone() {
                try {
                    return (MethodKey) super.clone();
                } catch (CloneNotSupportedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public static class LoopNode extends Node {

        public final LoopKey loopKey;

        public final ChildSlot bodySlot = new ChildSlot();

        public LoopNode(Node body) {
            this(new LoopKey(), body);
        }

        public LoopNode(LoopKey loopKey, Node body) {
            this.loopKey = loopKey;
            this.bodySlot.set(body);
        }

        @Override
        public List<SideEffect> sideEffects() {
            return bodySlot.get().sideEffects();
        }

        @Override
        public Type type() {
            return PrimitiveType.V;
        }

        public static final class LoopKey implements Returnable {
        }

        @Override
        public String toString() {
            return "Loop: " + bodySlot.get();
        }

        @Override
        protected Node cloneImpl(Cloner cloner) {
            return new LoopNode(
                    cloner.cloneKey(loopKey, LoopKey::new),
                    cloner.clone(bodySlot.get())
            );
        }
    }

    public static class IfElseNode extends Node {

        public final InputSlot conditionSlot = new InputSlot();
        public final ChildSlot successBranchSlot = new ChildSlot();
        public final ChildSlot failureBranchSlot = new ChildSlot();

        public IfElseNode(Node condition) {
            this.conditionSlot.set(condition);
        }

        public IfElseNode(Node condition, Node successBranch, Node failureBranch) {
            this.conditionSlot.set(condition);
            this.successBranchSlot.set(successBranch);
            this.failureBranchSlot.set(failureBranch);
        }

        @Override
        public Type type() {
            return context().lub(successBranchSlot.get() == null ? null : successBranchSlot.get().type(),
                    failureBranchSlot.get() == null ? null : failureBranchSlot.get().type());
        }

        @Override
        public List<SideEffect> sideEffects() {
            return Stream.concat(successBranchSlot.get() == null ? Stream.empty() : successBranchSlot.get().sideEffects().stream(),
                    failureBranchSlot.get() == null ? Stream.empty() : failureBranchSlot.get().sideEffects().stream()).collect(Collectors.toList());
        }

        @Override
        public String toString() {
            return conditionSlot.get() + " ? " + successBranchSlot.get() + " : " + failureBranchSlot.get();
        }

        @Deprecated
        public Node successBranch() {
            return successBranchSlot.get();
        }

        @Deprecated
        public void successBranch(Node successBranch) {
            this.successBranchSlot.set(successBranch);
        }

        @Deprecated
        public Node failureBranch() {
            return failureBranchSlot.get();
        }

        @Deprecated
        public void failureBranch(Node failureBranch) {
            this.failureBranchSlot.set(failureBranch);
        }

        @Override
        protected Node cloneImpl(Cloner cloner) {
            return new IfElseNode(
                    cloner.clone(conditionSlot.get()),
                    cloner.clone(successBranchSlot.get()),
                    cloner.clone(failureBranchSlot.get())
            );
        }
    }

    public static class PhiNode extends Node {

        /**
         * Ezek negálva vannak, tehát ha {@code false}, akkor visszaadja a hozzá tartozó értéket, ha pedig {@code true},
         * akkor továbbmegy.
         */
        public final InputList conditionList = new InputList();
        public final InputList valueList = new InputList();

        @Override
        public Type type() {
            Type t = null;
            for (Node n : valueList) {
                Type t2 = n.type();
                assert t2 != null : n;

                if (t2 == ErrorType.ErrorType)
                    continue;
                if (t2 == PrimitiveType.V)
                    // bekerülhet ThrowNode is PhiNode-ba zavaros körülmények között
                    // (csak profilerrel indítva jön elő a probléma)
                    // ilyenkor eliminálni kéne az egész PhiNode-ot Optimizer2-nek.
                    // egyelőre csak nem csinálunk lubot.
                    t2 = null;

                Type prevT = t;
                t = context().lub(t, t2);
                if (t == null)
                    throw new RuntimeException(prevT + ", " + t2);
            }
            assert t != null : this;
            return t;
        }

        @Override
        public List<SideEffect> sideEffects() {
            return emptyList();
        }

        @Override
        protected Node cloneImpl(Cloner cloner) {
            PhiNode n = new PhiNode();
            cloner.cloneFromTo(conditionList, n.conditionList);
            cloner.cloneFromTo(valueList, n.valueList);
            return n;
        }

        @Override
        public String toString() {
            return "PhiNode{" +
                    "conditionList=" + conditionList +
                    ", valueList=" + valueList +
                    '}';
        }
    }

    public static class ReturnNode extends Node {

        public MethodKey returnFrom;
        public final InputSlot returnValueSlot = new InputSlot();

        public ReturnNode(MethodKey returnFrom, Node returnValue) {
            this.returnFrom = returnFrom;
            this.returnValueSlot.set(returnValue);
            assert returnValueSlot.get() == null || returnValueSlot.get().isAttached();
        }

        @Override
        public List<SideEffect> sideEffects() {
            return List.of(SideEffect.BreakControlFlow.INSTANCE);
        }

        @Override
        public Type type() {
            return PrimitiveType.V;
        }

        @Override
        public String toString() {
            return (returnValueSlot.get() == null ? "return" : "return " + returnValueSlot.get()) +
                    " from " + returnFrom;
        }

        @Override
        protected Node cloneImpl(Cloner cloner) {
            assert returnValueSlot.get() == null || returnValueSlot.get().isAttached();
            return new ReturnNode(
                    cloner.cloneKey2(returnFrom, MethodKey::clone),
                    cloner.clone(returnValueSlot));
        }

        @Override
        protected boolean alwaysBreaksImpl() {
            return true;
        }
    }

    public static class ContinueLoopNode extends Node {

        public final LoopKey loop;

        public ContinueLoopNode(LoopKey loop) {
            this.loop = loop;
        }

        @Override
        public List<SideEffect> sideEffects() {
            return List.of(SideEffect.BreakControlFlow.INSTANCE);
        }

        @Override
        public Type type() {
            return PrimitiveType.V;
        }

        @Override
        protected Node cloneImpl(Cloner cloner) {
            return new ContinueLoopNode(cloner.cloneKey(loop, LoopKey::new));
        }

        @Override
        protected boolean alwaysBreaksImpl() {
            return true;
        }
    }

    public static class BreakLoopNode extends Node {

        public final LoopKey loop;

        public BreakLoopNode(LoopKey loop) {
            this.loop = loop;
        }

        @Override
        public List<SideEffect> sideEffects() {
            return List.of(SideEffect.BreakControlFlow.INSTANCE);
        }

        @Override
        public Type type() {
            return PrimitiveType.V;
        }

        @Override
        protected Node cloneImpl(Cloner cloner) {
            return new BreakLoopNode(cloner.cloneKey(loop, LoopKey::new));
        }

        @Override
        protected boolean alwaysBreaksImpl() {
            return true;
        }
    }

    public static class AllocateArray extends Node {

        public final ArrayType arrayType;
        public final InputList lengthList = new InputList();

        public AllocateArray(ArrayType arrayType, Node length) {
            this(arrayType, List.of(length));
        }

        public AllocateArray(ArrayType arrayType, List<Node> dimensions) {
            Objects.requireNonNull(arrayType);
            if (dimensions.isEmpty())
                throw new IllegalArgumentException();
            this.arrayType = arrayType;
            this.lengthList.addAll(dimensions);
        }

        @Override
        public List<SideEffect> sideEffects() {
            return List.of(SideEffect.Allocation.INSTANCE);
        }

        @Override
        public ArrayType type() {
            return arrayType;
        }

        @Override
        protected Node cloneImpl(Cloner cloner) {
            return new AllocateArray(arrayType, cloner.clone(lengthList));
        }
    }

    public static class ArrayStore extends Node {

        public final InputSlot arraySlot = new InputSlot();
        public final InputSlot indexSlot = new InputSlot();
        public final InputSlot valueSlot = new InputSlot();
        public final Type elementType;

        public ArrayStore(Node array, Node index, Node value, Type elementType) {
            assert array.type() == null || // pl. ArrayLoad általában nem tudja megmondani a típusát, mert ha localvarból olvassa be a tömböt, akkor csak Object típust érzékel a tömbnek
                    array.type() instanceof ArrayType || array.type() instanceof Clazz : array.type();
            this.arraySlot.set(array);
            this.indexSlot.set(index);
            this.valueSlot.set(value);
            this.elementType = elementType;
        }

        @Override
        public List<SideEffect> sideEffects() {
            Type arrayType = arraySlot.get().type();
            return List.of(new SideEffect.Write(new Variable.ArrayElement(
                    arrayType instanceof ArrayType at ? at : null)));
        }

        @Override
        public Type type() {
            return PrimitiveType.V;
        }

        @Override
        public boolean deterministicInputEvaluationOrder() {
            return true;
        }

        @Override
        protected Node cloneImpl(Cloner cloner) {
            return new ArrayStore(
                    cloner.clone(arraySlot.get()),
                    cloner.clone(indexSlot.get()),
                    cloner.clone(valueSlot.get()),
                    elementType
            );
        }
    }

    public static class ArrayLoad extends Node {

        public final InputSlot arraySlot = new InputSlot();
        public final InputSlot indexSlot = new InputSlot();
        public final Type elementType;

        public ArrayLoad(Node array, Node index, Type elementType) {
            this.arraySlot.set(array);
            this.indexSlot.set(index);
            this.elementType = elementType;
        }

        @Override
        public List<SideEffect> sideEffects() {
            return List.of(new SideEffect.Read(readenVariable()));
        }

        @Nonnull
        @Override
        public ArrayElement readenVariable() {
            return new ArrayElement(arraySlot.get().type() instanceof ArrayType arrayType ? arrayType : null);
        }

        @Override
        public Type type() {
            return elementType;
        }

        @Override
        public boolean deterministicInputEvaluationOrder() {
            return true;
        }

        @Override
        public String toString() {
            return arraySlot.get() + "[" + indexSlot.get() + "]";
        }

        @Override
        protected Node cloneImpl(Cloner cloner) {
            return new ArrayLoad(
                    cloner.clone(arraySlot.get()),
                    cloner.clone(indexSlot.get()),
                    elementType);
        }
    }

    public static class ArrayLength extends Node {

        public final InputSlot arraySlot = new InputSlot();

        public ArrayLength(Node array) {
            this.arraySlot.set(array);
        }

        @Override
        public List<SideEffect> sideEffects() {
            return emptyList();
        }

        @Override
        public Type type() {
            return PrimitiveType.I;
        }

        @Override
        protected Node cloneImpl(Cloner cloner) {
            return new ArrayLength(cloner.clone(arraySlot.get()));
        }
    }

    public static class ThrowNode extends Node {

        public final InputSlot exceptionSlot = new InputSlot();

        public ThrowNode(Node exception) {
            this.exceptionSlot.set(exception);
        }

        @Override
        public List<SideEffect> sideEffects() {
            return List.of(BreakControlFlow.INSTANCE);
        }

        @Override
        public Type type() {
            return PrimitiveType.V;
        }

        @Override
        protected Node cloneImpl(Cloner cloner) {
            return new ThrowNode(cloner.clone(exceptionSlot.get()));
        }

        @Override
        protected boolean alwaysBreaksImpl() {
            return true;
        }
    }

    public static class ObjectNode extends Node {

        public final ObjectIdentity objectIdentity;

        public ObjectNode(ObjectIdentity objectIdentity) {
            this.objectIdentity = objectIdentity;
        }

        @Override
        public List<SideEffect> sideEffects() {
            return List.of(SideEffect.Allocation.INSTANCE);
        }

        @Override
        public Type type() {
            return objectIdentity.type;
        }

        @Override
        public String toString() {
            return "obj" + objectIdentity.hashCode();
        }

        @Override
        protected Node cloneImpl(Cloner cloner) {
            return new ObjectNode(cloner.cloneKey2(objectIdentity, oi->new ObjectIdentity(oi.type)));
        }

        public static class ObjectIdentity {

            public Clazz type;

            public ObjectIdentity(Clazz type) {
                this.type = type;
            }

            @Override
            public String toString() {
                return type == null ? super.toString() : type.name;
            }
        }
    }

    // ha ezzel kapcsolatban probléma merül fel, Node::transformban visszakapcsohatjuk a checkcasttal
    // összefüggő assertiont
    public static class CheckCast extends Node {

        public final InputSlot valueSlot = new InputSlot();
        public final Type type;

        public CheckCast(Node value, Type type) {
            this.valueSlot.set(value);
            this.type = type;
        }

        @Override
        public List<SideEffect> sideEffects() {
            return List.of(BreakControlFlow.INSTANCE);
        }

        @Override
        public Type type() {
            return type; // TODO
        }

        @Override
        protected Node cloneImpl(Cloner cloner) {
            return new CheckCast(cloner.clone(valueSlot.get()), type);
        }

        @Override
        public String toString() {
            return "CheckCast{" +
                    "valueSlot=" + valueSlot.get() +
                    ", type=" + type +
                    '}';
        }
    }

    public static class InstanceOf extends Node {

        public final InputSlot valueSlot = new InputSlot();
        public final Type type;

        public InstanceOf(Node value, Type type) {
            this.valueSlot.set(value);
            this.type = type;
        }

        @Override
        public List<SideEffect> sideEffects() {
            return emptyList();
        }

        @Override
        public Type type() {
            return PrimitiveType.I;
        }

        @Override
        protected Node cloneImpl(Cloner cloner) {
            return new InstanceOf(cloner.clone(valueSlot.get()), type);
        }

        @Override
        public String toString() {
            return valueSlot.get() + " instanceof " + type;
        }
    }

    public static class GetStaticNode extends Node {
        public final Field field;

        public GetStaticNode(Field field) {
            this.field = field;
        }

        @Override
        public List<SideEffect> sideEffects() {
            return List.of(new SideEffect.Read(readenVariable()));
        }

        @Nonnull
        @Override
        public Variable.StaticField readenVariable() {
            return new Variable.StaticField(field);
        }

        @Override
        public Type type() {
            return field.type();
        }

        @Override
        public String toString() {
            return readenVariable().toString();
        }

        @Override
        protected Node cloneImpl(Cloner cloner) {
            return new GetStaticNode(field);
        }
    }

    public static class PutStaticNode extends Node {

        public final InputSlot valueSlot = new InputSlot();
        public final Field field;

        public PutStaticNode(Field field, Node value) {
            this.field = field;
            this.valueSlot.set(value);
        }

        @Override
        public List<SideEffect> sideEffects() {
            return List.of(new SideEffect.Write(writtenVariable().variable));
        }

        @Nonnull
        @Override
        public WrittenVariableAndValue writtenVariable() {
            return new WrittenVariableAndValue(new StaticField(field), valueSlot.get());
        }

        @Override
        public Type type() {
            return PrimitiveType.V;
        }

        @Override
        public String toString() {
            return writtenVariable().variable.toString() + " = " + value();
        }

        public Node value() {
            return valueSlot.get();
        }

        @Override
        protected Node cloneImpl(Cloner cloner) {
            return new PutStaticNode(field, cloner.clone(valueSlot.get()));
        }
    }

    public static class GetFieldNode extends Node {

        public final InputSlot objectSlot = new InputSlot();
        public final Field field;

        public GetFieldNode(Node input, Field field) {
            this.objectSlot.set(input);
            this.field = field;
        }

        @Override
        public List<SideEffect> sideEffects() {
            return List.of(new SideEffect.Read(readenVariable()));
        }

        @Nonnull
        @Override
        public InstanceField readenVariable() {
            Node obj = objectSlot.get();
            return new InstanceField(field,
                    obj instanceof ObjectNode objectNode ? objectNode.objectIdentity : null);
        }

        @Override
        public Type type() {
            return field.type();
        }

        @Override
        public String preferredName() {
            return "getfield_" + field.name + "_";
        }

        @Override
        public String toString() {
            return objectSlot.get() + "." + field.name;
        }

        @Override
        protected Node cloneImpl(Cloner cloner) {
            return new GetFieldNode(cloner.clone(objectSlot.get()), field);
        }
    }

    public static class PutFieldNode extends Node {

        public final InputSlot objectSlot = new InputSlot();
        public final Field field;
        public final InputSlot valueSlot = new InputSlot();

        public PutFieldNode(Node object, Field field, Node value) {
            this.objectSlot.set(object);
            this.field = field;
            this.valueSlot.set(value);
            assert field.index < field.clazz.allInstanceFields : field.index + ", " + field.clazz.allInstanceFields;
        }

        @Override
        public List<SideEffect> sideEffects() {
            return List.of(new SideEffect.Write(writtenVariable().variable));
        }

        @Nonnull
        @Override
        public WrittenVariableAndValue writtenVariable() {
            Node obj = objectSlot.get();
            return new WrittenVariableAndValue(new InstanceField(field,
                    obj instanceof ObjectNode objectNode ? objectNode.objectIdentity : null),
                    valueSlot.get());
        }

        @Override
        public Type type() {
            return PrimitiveType.V;
        }

        @Override
        public String toString() {
            return object() + "." + field.name + " = " + value();
        }

        @Deprecated
        public Node object() {
            return objectSlot.get();
        }

        @Deprecated
        public Node value() {
            return valueSlot.get();
        }

        @Override
        public boolean deterministicInputEvaluationOrder() {
            return true;
        }

        @Override
        protected Node cloneImpl(Cloner cloner) {
            return new PutFieldNode(
                    cloner.clone(objectSlot.get()),
                    field,
                    cloner.clone(valueSlot.get())
            );
        }
    }

    public static class UnaryNumericOpNode extends Node {

        public final InputSlot inputSlot = new InputSlot();
        public final OpInfo op;

        public UnaryNumericOpNode(Node input, OpInfo op) {
            this.inputSlot.set(input);
            this.op = op;
        }

        @Override
        public Type type() {
            return op.outputType;
        }

        @Override
        public List<SideEffect> sideEffects() {
            return emptyList();
        }

        @Override
        public String toString() {
            return op.symbol + inputSlot.get();
        }

        @Override
        protected Node cloneImpl(Cloner cloner) {
            return new UnaryNumericOpNode(cloner.clone(inputSlot.get()), op);
        }

        public static Node negate(Node n) {
            if (n instanceof UnaryNumericOpNode un && un.op.code == IFEQ)
                return new UnaryNumericOpNode(un.inputSlot.get(), NumericOps.ops[IFNE]);
            if (n instanceof UnaryNumericOpNode un && un.op.code == IFNE)
                return new UnaryNumericOpNode(un.inputSlot.get(), NumericOps.ops[IFEQ]);
            return new UnaryNumericOpNode(n, NumericOps.ops[IFEQ]);
        }
    }

    public static class BinaryNumericOpNode extends Node {

        public final InputSlot input1Slot = new InputSlot();
        public final InputSlot input2Slot = new InputSlot();
        public final OpInfo op;

        public BinaryNumericOpNode(Node input1, Node input2, OpInfo op) {
            this.input1Slot.set(input1);
            this.input2Slot.set(input2);
            this.op = op;
        }

        @Override
        public Type type() {
            return op.outputType;
        }

        @Override
        public List<SideEffect> sideEffects() {
            return emptyList();
        }

        @Override
        public String preferredName() {
            return "binop";
        }

        @Override
        public boolean deterministicInputEvaluationOrder() {
            return true;
        }

        @Override
        public String toString() {
            if (input1Slot.get() instanceof BinaryNumericOpNode || input2Slot.get() instanceof BinaryNumericOpNode)
                return "(" + input1Slot.get() + ")" + op.symbol + "(" + input2Slot.get() + ")";
            else
                return input1Slot.get() + op.symbol + input2Slot.get();
        }

        @Override
        protected Node cloneImpl(Cloner cloner) {
            return new BinaryNumericOpNode(
                    cloner.clone(input1Slot.get()),
                    cloner.clone(input2Slot.get()),
                    op
            );
        }
    }

    public static class LogicalAnd extends Node {

        public final InputSlot input1 = new InputSlot();
        public final InputSlot input2 = new InputSlot();

        public LogicalAnd(Node input1, Node input2) {
            this.input1.set(input1);
            this.input2.set(input2);
        }

        @Override
        public String preferredName() {
            return "and";
        }

        @Override
        public Type type() {
            return PrimitiveType.I;
        }

        @Override
        public List<SideEffect> sideEffects() {
            return emptyList();
        }

        @Override
        public String toString() {
            return input1.get() + " && " + input2.get();
        }

        @Override
        protected Node cloneImpl(Cloner cloner) {
            return new LogicalAnd(
                    cloner.clone(input1.get()),
                    cloner.clone(input2.get())
            );
        }

        public static Node and(Node a, Node b) {
            if (a == null)
                if (b == null)
                    return new ConstantNode(1);
                else
                    return b;
            if (b == null)
                return a;


            if (a instanceof ConstantNode ac && b instanceof ConstantNode bc)
                return new ConstantNode((int) ac.value & (int) bc.value);
            if (a instanceof ConstantNode ac && (int) ac.value == 0 || b instanceof ConstantNode bc && (int) bc.value == 0)
                return new ConstantNode(0);
            if (a instanceof ConstantNode ac && (int) ac.value == 1)
                return b;
            if (b instanceof ConstantNode bc && (int) bc.value == 1)
                return a;
            if (a instanceof UnaryNumericOpNode ua && ua.op.code == IFEQ && ua.inputSlot.get().equals(b))
                return new ConstantNode(0);
            if (b instanceof UnaryNumericOpNode ub && ub.op.code == IFEQ && ub.inputSlot.get().equals(a))
                return new ConstantNode(0);
            if (a instanceof UnaryNumericOpNode ua && ua.op.code == IFEQ &&
                    b instanceof LogicalAnd bb &&
                    (bb.input1.get().equals(ua.inputSlot.get())
                            || bb.input2.get().equals(ua.inputSlot.get())))
                return new ConstantNode(0);
            return new LogicalAnd(a, b);
        }
    }

    public static class LogicalOr extends Node {

        public final InputSlot input1 = new InputSlot();
        public final InputSlot input2 = new InputSlot();

        public LogicalOr(Node input1, Node input2) {
            this.input1.set(input1);
            this.input2.set(input2);
        }

        @Override
        public String preferredName() {
            return "or";
        }

        @Override
        public Type type() {
            return PrimitiveType.I;
        }

        @Override
        public List<SideEffect> sideEffects() {
            return emptyList();
        }

        @Override
        protected Node cloneImpl(Cloner cloner) {
            return new LogicalOr(
                    cloner.clone(input1.get()),
                    cloner.clone(input2.get())
            );
        }

        @Override
        public String toString() {
            return input1.get() + " || " + input2.get();
        }

        public static Node or(Node a, Node b) {
            if (a instanceof ConstantNode ac && b instanceof ConstantNode bc)
                return new ConstantNode((int) ac.value | (int) bc.value);
            if (a instanceof ConstantNode ac)
                return switch ((int) ac.value) {
                    case 0 -> b;
                    case 1 -> new ConstantNode(1);
                    default -> throw new IllegalArgumentException();
                };
            if (b instanceof ConstantNode bc)
                return switch ((int) bc.value) {
                    case 0 -> a;
                    case 1 -> new ConstantNode(1);
                    default -> throw new IllegalArgumentException();
                };

            return new LogicalOr(a, b);
        }
    }

    public static class WriteLocalVar extends Node {

        public final InputSlot valueSlot = new InputSlot();
        public final Variable localVar;

        public WriteLocalVar(Variable localVar, Node value) {
            Objects.requireNonNull(localVar);
            Objects.requireNonNull(value);
            this.valueSlot.set(value);
            this.localVar = localVar;
        }

        @Override
        public Type type() {
            return PrimitiveType.V;
        }

        @Override
        public List<SideEffect> sideEffects() {
            return List.of(new SideEffect.Write(localVar));
        }

        @Override
        public String toString() {
            return "writevar " + valueSlot.get() + "->" + localVar;
        }

        @Override
        public WrittenVariableAndValue writtenVariable() {
            return new WrittenVariableAndValue(localVar, valueSlot.get());
        }

        @Override
        protected Node cloneImpl(Cloner cloner) {
            return new WriteLocalVar(cloner.var(localVar), cloner.clone(valueSlot.get()));
        }
    }

    public static class ReadLocalVar extends Node {

        public final Variable localVar;
        public Type type;

        public ReadLocalVar(Variable localVar) {
            Objects.requireNonNull(localVar);
            this.localVar = localVar;
            this.type = localVar.type(); // OptPhase2 majd átállítja, ha talál pontosabbat
        }

        @Override
        public Type type() {
            return type;
        }

        @Override
        public List<SideEffect> sideEffects() {
            return List.of(new SideEffect.Read(localVar));
        }

        @Override
        public String toString() {
            if (localVar instanceof LocalVar v)
                return "var" + v.number() + (comment == null ? "" : "(" + comment + ") ");
            else
                return "ReadVar " + localVar + (comment == null ? "" : "(" + comment + ")");
        }

        @Override
        public Variable readenVariable() {
            return localVar;
        }

        @Override
        protected Node cloneImpl(Cloner cloner) {
            ReadLocalVar rlv = new ReadLocalVar(cloner.var(localVar));
            rlv.type = type;
            return rlv;
        }
    }

    public static class NopNode extends Node {
        private final String description;

        public NopNode(String description) {
            this.description = description;
        }

        @Override
        public Type type() {
            return PrimitiveType.V;
        }

        @Override
        public List<SideEffect> sideEffects() {
            return emptyList();
        }

        @Override
        protected Node cloneImpl(Cloner cloner) {
            return new NopNode(description);
        }

        @Override
        public String toString() {
            return "NOP (" + description + ")";
        }
    }

    public static abstract class InvokeNode extends Node {

        public final Method method;
        public final InputList args;

        public InvokeNode(Method method, Node... args) {
            this.method = method;
            this.args = new InputList(args);
        }

        @Override
        public Type type() {
            if (method.clazz.knownClass == KnownClass.CLASS && method.name.equals("cast") &&
                    args.get(0) instanceof ConstantNode constantNode) {
                return constantNode.value instanceof Interpreter.ClassObj o
                        ? context().interpreter().fromClass(o)
                        : (Type) constantNode.value;
            }

            return method.type().returnType();
        }

        @Override
        public List<SideEffect> sideEffects() {
            return List.of(BreakControlFlow.INSTANCE);
        }

        @Override
        public boolean deterministicInputEvaluationOrder() {
            return true; // legalábbis JS esetén
        }
    }

    public static class InvokeSpecialOrStatic extends InvokeNode {

        public InvokeSpecialOrStatic(Method method, Node... args) {
            super(method, args);
            if ((method.access & ACC_ABSTRACT) != 0)
                throw new IllegalArgumentException(method.toString());
        }

        @Override
        public String preferredName() {
            if (method.name.equals("<init>"))
                return "constr_" + method.clazz.name.substring(method.clazz.name.lastIndexOf('/') + 1);
            else
                return "inv_" + method.name;
        }

        @Override
        public String toString() {
            return method + args.toString();
        }

        @Override
        protected Node cloneImpl(Cloner cloner) {
            return new InvokeSpecialOrStatic(
                    method,
                    cloner.clone(args).toArray(Node[]::new)
            );
        }
    }

    public static class InvokeVirtualOrInterface extends InvokeNode {

        public InvokeVirtualOrInterface(Method method, Node... args) {
            super(method, args);
        }

        @Override
        public String preferredName() {
            return "inv_" + method.name;
        }

        @Override
        public String toString() {
            return method.clazz.name.substring(method.clazz.name.lastIndexOf('/') + 1) + "." + method.name
                    + args.stream().map(Node::toString).collect(Collectors.joining(", ", "(", ")"));
        }

        @Override
        protected Node cloneImpl(Cloner cloner) {
            return new InvokeVirtualOrInterface(
                    method,
                    cloner.clone(args).toArray(Node[]::new)
            );
        }
    }

    public static final class Switch extends Node {

        public final InputSlot input = new InputSlot();

        /**
         * Ennek a tartalma mindenképpen {@linkplain SequenceNode} legyen, amikben van valahány {@linkplain SwitchCase},
         * amik közül pontosan egy default, míg a többi értékalapú
         */
        public final ChildSlot child = new ChildSlot();

        public final SwitchBreakKey switchBreakKey;

        /**
         * Ez lehet null is, mert ha minden ágon pl. exceptiont dob vagy visszatér a függvényből, akkor nem lehet
         * megállapítani, hogy switch expression vagy switch statementről van-e szó (pl. List.of(E[])).
         */
        public SwitchType switchType;

        public Type switchExprResultType;

        public Switch() {
            this.switchBreakKey = new SwitchBreakKey();
        }

        public Switch(Node input, SequenceNode child,
                      SwitchBreakKey switchBreakKey, SwitchType switchType,
                      Type switchExprResultType) {
            this.input.set(input);
            this.child.set(child);
            this.switchBreakKey = switchBreakKey;
            this.switchType = switchType;
            this.switchExprResultType = switchExprResultType;
        }

        @Override
        public Type type() {
            return switch (switchType) {
                case SWITCH_STATEMENT -> PrimitiveType.V;
                case null -> PrimitiveType.V;
                case SWITCH_EXPRESSION -> {
                    assert switchExprResultType != null;
                    yield switchExprResultType;
                }
            };
        }

        @Override
        public List<SideEffect> sideEffects() {
            return child.get().sideEffects();
        }

        @Override
        protected Node cloneImpl(Cloner cloner) {
            return new Switch(
                    cloner.clone(input.get()),
                    (SequenceNode) cloner.clone(child.get()),
                    cloner.cloneKey(switchBreakKey, SwitchBreakKey::new),
                    switchType,
                    switchExprResultType
            );
        }

        public enum SwitchType {
            SWITCH_STATEMENT, SWITCH_EXPRESSION
        }

        public static final class SwitchBreakKey implements Returnable {
        }

        @Override
        public String toString() {
            return "switch (" + input.get() + ") " + child.get();
        }
    }

    public static class SwitchCase extends Node {

        private final List<Integer> values;

        private SwitchCase(List<Integer> values) {
            this.values = values;
        }

        public List<Integer> values() {
            assert !isDefault();
            return values;
        }

        public boolean isDefault() {
            return values == null;
        }

        public static SwitchCase ofValue(int value) {
            return new SwitchCase(new ArrayList<>(List.of(value)));
        }

        public static SwitchCase ofDefault() {
            return new SwitchCase(null);
        }

        @Override
        public Type type() {
            return PrimitiveType.V;
        }

        @Override
        public List<SideEffect> sideEffects() {
            return List.of(BreakControlFlow.INSTANCE);
        }

        @Override
        protected Node cloneImpl(Cloner cloner) {
            return new SwitchCase(values == null ? null : List.copyOf(values));
        }

        @Override
        public String toString() {
            return "case " + values;
        }
    }

    public static class BreakSwitch extends Node {

        public final SwitchBreakKey switchBreakKey;

        public final InputSlot resultSlot = new InputSlot();

        public BreakSwitch(SwitchBreakKey switchBreakKey, Node result) {
            this.switchBreakKey = switchBreakKey;
            this.resultSlot.set(result);
        }

        @Override
        public Type type() {
            return PrimitiveType.V;
        }

        @Override
        public List<SideEffect> sideEffects() {
            return List.of(BreakControlFlow.INSTANCE);
        }

        @Override
        protected Node cloneImpl(Cloner cloner) {
            return new BreakSwitch(
                    cloner.cloneKey(switchBreakKey, SwitchBreakKey::new),
                    cloner.clone(resultSlot.get())
            );
        }
    }


    // MethodHandle.linkTo*
    // ez a Node most nincs használva, helyette EmissionContext/Emitter nézi hogy
    // InvokeSpecialOrStatic linker metódust hív-e
    public static class CallDynamicFunctionNode extends Node {


        public final InputSlot functionObjectSlot;
        public final InputList args;
        public final Type type; // erased to L,I,J,F,D

        public CallDynamicFunctionNode(Node functionObjectNode, List<Node> args, Type type) {
            this.functionObjectSlot = new InputSlot(functionObjectNode);
            this.args = new InputList(args);
            this.type = type;
        }

        @Override
        public Type type() {
            return type;
        }

        @Override
        public List<SideEffect> sideEffects() {
            return List.of(BreakControlFlow.INSTANCE);
        }

        @Override
        protected Node cloneImpl(Cloner cloner) {
            return new CallDynamicFunctionNode(
                    cloner.clone(functionObjectSlot.get()),
                    cloner.clone(args),
                    type
            );
        }
    }

    public static class NativeCallNode extends Node {
        public final Method method;
        public final InputList args;
        public final NativeMethodKind nativeMethodKind;

        public NativeCallNode(Method method, List<Node> arguments, NativeMethodKind nativeMethodKind) {
            this.method = method;
            this.args = new InputList(arguments);
            this.nativeMethodKind = nativeMethodKind;
        }

        @Override
        public Type type() {
            return method.type().returnType();
        }

        @Override
        public List<SideEffect> sideEffects() {
            return List.of(BreakControlFlow.INSTANCE);
        }

        @Override
        public String preferredName() {
            return "nativecall_" + method.name;
        }

        @Override
        public boolean deterministicInputEvaluationOrder() {
            return true;
        }

        @Override
        public String toString() {
            return method.clazz.name.substring(method.clazz.name.lastIndexOf('/') + 1) + "." + method.name
                    + args.stream().map(Node::toString).collect(Collectors.joining(", ", "(", ")"));
        }

        @Override
        protected Node cloneImpl(Cloner cloner) {
            return new NativeCallNode(
                    method,
                    cloner.clone(args),
                    nativeMethodKind
            );
        }
    }

    public static class NativeUpcallStubNode extends Node {

        public final Clazz functionalInterfaceType;
        public final InputSlot functionalInterfaceSlot;

        public NativeUpcallStubNode(Clazz functionalInterfaceType, Node functionInterfaceInstance) {
            this.functionalInterfaceType = functionalInterfaceType;
            functionalInterfaceSlot = new InputSlot(functionInterfaceInstance);
        }

        @Override
        public Type type() {
            return functionalInterfaceType;
        }

        @Override
        public List<SideEffect> sideEffects() {
            // vagy var read?
            return List.of(Allocation.INSTANCE);
        }

        @Override
        protected Node cloneImpl(Cloner cloner) {
            return new NativeUpcallStubNode(functionalInterfaceType, cloner.clone(functionalInterfaceSlot));
        }
    }

    public static class TypeOfNode extends Node {

        public final InputSlot inputSlot;

        public TypeOfNode(Node input) {
            inputSlot = new InputSlot(input);
        }

        @Override
        public Type type() {
            return context().findClass(KnownClass.CLASS);
        }

        @Override
        public List<SideEffect> sideEffects() {
            // TODO ez nem igaz, NPE-t dobhat
            return Collections.emptyList();
        }

        @Override
        protected Node cloneImpl(Cloner cloner) {
            return new TypeOfNode(cloner.clone(inputSlot));
        }
    }

    public static class ErrorNode extends Node {
        private final String description;

        public ErrorNode(String description) {
            this.description = description;
        }

        @Override
        public Type type() {
            return ErrorType.ErrorType;
        }

        @Override
        public List<SideEffect> sideEffects() {
            return List.of(BreakControlFlow.INSTANCE);
        }

        @Override
        protected Node cloneImpl(Cloner cloner) {
            return new ErrorNode(description);
        }

        @Override
        public String toString() {
            return "ErrorNode (" + description + ")";
        }

        public enum ErrorType implements Type {
            ErrorType;

            @Override
            public String descriptor() {
                throw new UnsupportedOperationException();
            }

            @Override
            public boolean isAssignableFrom(Type otherType) {
                throw new UnsupportedOperationException();
            }

            @Override
            public int slotSize() {
                throw new UnsupportedOperationException();
            }

            @Override
            public int modifiers() {
                throw new UnsupportedOperationException();
            }

            @Override
            public String displayName() {
                throw new UnsupportedOperationException();
            }

            @Override
            public Type supertype() {
                return null;
            }

            @Override
            public int bytesSize() {
                throw new UnsupportedOperationException();
            }

            @Override
            public Collection<? extends Type> allAncestorsAndThis() {
                throw new UnsupportedOperationException();
            }
        }
    }

    public static class NativeSnippetNode extends Node {

        public final NativeSnippet snippet;
        public final List<Node> args;
        public final Type type;

        public NativeSnippetNode(NativeSnippet snippet, Type type) {
            this.snippet = Objects.requireNonNull(snippet);
            this.type = Objects.requireNonNull(type);
            this.args = new InputList();
        }

        public NativeSnippetNode(NativeSnippet snippet, Type type, List<Node> args) {
            this.snippet = Objects.requireNonNull(snippet);
            this.type = Objects.requireNonNull(type);
            this.args = new InputList(args);
        }

        @Override
        public Type type() {
            return type;
        }

        @Override
        public List<SideEffect> sideEffects() {
            return List.of(Allocation.INSTANCE); // TODO
        }

        @Override
        protected Node cloneImpl(Cloner cloner) {
            NativeSnippetNode jsn = new NativeSnippetNode(snippet, type);
            cloner.cloneFromTo(args, jsn.args);
            return jsn;
        }

        @Override
        public boolean deterministicInputEvaluationOrder() {
            return snippet.isOrderedInputEvaluation();
        }

        public interface NativeSnippet {

            default boolean isOrderedInputEvaluation() {
                return false;
            }

            String makeScript(List<String> args);
        }
    }

    public static class ReinterpretCastNode extends Node {

        public final InputSlot input;
        public final Type type;

        public ReinterpretCastNode(Node input, Type type) {
            this.input = new InputSlot(input);
            this.type = type;
        }

        @Override
        public Type type() {
            return type;
        }

        @Override
        public List<SideEffect> sideEffects() {
            return Collections.emptyList();
        }

        @Override
        protected Node cloneImpl(Cloner cloner) {
            return new ReinterpretCastNode(cloner.clone(input), type);
        }
    }
}
