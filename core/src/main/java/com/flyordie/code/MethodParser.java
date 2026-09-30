package com.flyordie.code;

import com.flyordie.code.Clazz.Field;
import com.flyordie.code.Clazz.Method;
import com.flyordie.code.Interpreter.ClassObj;
import com.flyordie.code.Interpreter.IndyLinkResult;
import com.flyordie.code.Location.MethodLocationElement;
import com.flyordie.code.Node.*;
import com.flyordie.code.Node.ObjectNode.ObjectIdentity;
import com.flyordie.code.Node.SequenceNode.MethodKey;
import com.flyordie.code.Node.Switch.SwitchType;
import com.flyordie.code.NumericOps.OpInfo;
import com.flyordie.code.Type.ArrayType;
import com.flyordie.code.Type.PrimitiveType;
import com.flyordie.code.Type.ReferenceType;
import com.flyordie.code.Variable.LocalVar;
import com.flyordie.code.Variable.LocalVar.Kind;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.tree.*;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.stream.IntStream;

import static org.objectweb.asm.Opcodes.*;

// TODO logicalOr-ra el lett felejtve az add() meghívása
public class MethodParser extends MethodVisitor {

    private final CompilationContext context;
    private final Clazz declaringClass;
    private final Method method;

    private final Deque<Block> blockStack = new LinkedList<>();

    private AbstractInsnNode[] instructions;
    private int pc;
    private int lineNumber = -1;

    private final BitSet allSwitchCases = new BitSet();

    public MethodParser(CompilationContext context, Method method) {
        super(ASM7);
        this.context = context;
        this.declaringClass = method.clazz;
        this.method = method;
    }

    public Node parse() {
        int cnt = 0;
        for (AbstractInsnNode instruction : method.instructions) {
            if (leavedInstruction(instruction))
                cnt++;
        }
        instructions = new AbstractInsnNode[cnt];

        int i = 0;
        for (AbstractInsnNode instruction : method.instructions) {
            if (instruction instanceof LabelNode)
                ((LabelNode) instruction).getLabel().info = i;
            else if (leavedInstruction(instruction))
                instructions[i++] = instruction;
        }
        assert i == cnt;
        assert pc == 0;

        Block rootBlock;
        try (var ignored = Location.with(new Location(List.of(new MethodLocationElement(method))))) {
            rootBlock = new Block(0, instructions.length, new MethodKey(method));
            rootBlock.root = true;
            rootBlock.comment = declaringClass.name + "." + method.name;
            blockStack.push(rootBlock);

            int[] catchBlockEnds = new int[instructions.length];
            for (TryCatchBlockNode tryCatchBlock : method.tryCatchBlocks) {
                int minJumpTarget = Integer.MAX_VALUE;
                int catchBegin = (int) tryCatchBlock.handler.getLabel().info;
                for (i = 0; i < catchBegin; i++) {
                    if (instructions[i] instanceof JumpInsnNode jump && (int) jump.label.getLabel().info > catchBegin)
                        minJumpTarget = Math.min(minJumpTarget, (int) jump.label.getLabel().info);
                }
                catchBlockEnds[catchBegin] = minJumpTarget;
            }

            int skipUntil = 0;
            beforeInstruction();
            for (AbstractInsnNode instruction : method.instructions) {
                if (pc >= skipUntil)
                    instruction.accept(this);
                if (leavedInstruction(instruction)) {
                    pc++;
                    beforeInstruction();
                    if (pc != instructions.length) {
                        if (catchBlockEnds[pc] != 0)
                            skipUntil = catchBlockEnds[pc];
                    }
                }
            }
        }

        SequenceNode n = blockStack.pop();
        assert n == rootBlock : n + ", " + rootBlock + ", " + blockStack;
        assert blockStack.isEmpty() : blockStack;
        return n;
    }

    private boolean leavedInstruction(AbstractInsnNode instruction) {
        return !(instruction instanceof LabelNode) && !(instruction instanceof LineNumberNode)
                && !(instruction instanceof FrameNode);
    }

    @Override
    public void visitLineNumber(int line, Label start) {
        this.lineNumber = line;
    }

    private void beforeInstruction() {
        Block blk;
        while ((blk = block()).end == pc
                && (blk.containingIf != null || blk.containingLoop != null || blk.containingSwitch != null)) {
            blockStack.pop();
            Block parent = block();

            if (!blk.skipConditions.isEmpty()) {
                for (Block.SkipCondition sc : blk.skipConditions) {
                    Node negContainerCond;
                    if (blk.containingIf != null) {
                        Node and = LogicalAnd.and(
                                add(UnaryNumericOpNode.negate(blk.containingIf.conditionSlot.get())),
                                sc.condition);
                        if (and instanceof LogicalAnd)
                            add(and);
                        negContainerCond = and;
                    } else if (blk.containingLoop != null || blk.containingSwitch != null)
                        negContainerCond = add(addEscapingConditionVar(blk, sc.condition));
                    else
                        throw unsupportedCase();


                    parent.skipConditions.add(new Block.SkipCondition(sc.until, negContainerCond));
                }
            }

            if (blk.containingLoop != null || blk.containingIf != null) {
                if (false) {
                    System.out.println(pc);
                    System.out.println(blk.operandStack);
                    System.out.println(parent.operandStack);
                    System.out.println(parent.skipConditions);
                    System.out.println();
                }

                if (blk.operandStack.size() != parent.operandStack.size()) {
                    if (blk.containingLoop != null || blk.operandStack.size() - parent.operandStack.size() != 1)
                        throw unsupportedCase();

                    Node condition = blk.containingIf.conditionSlot.get();
                    parent.conditionalStackElement.add(new Block.ConditionalStackElement(
                            condition, blk.operandStack.pop()));
                }

                if (!activateSkipConditions(parent) && !parent.conditionalStackElement.isEmpty()) {
                    Node phi = merge(parent.conditionalStackElement);
                    parent.nodes.add(phi);
                    assert phi.isAttached();
                    parent.operandStack.push(phi);
                    parent.conditionalStackElement.clear();
                }

                if (blk.containingIf != null) {
                    @SuppressWarnings("UnnecessaryLocalVariable") /* IntelliJ bug, nem veszi észre hogy blk változik */
                            Block blk2 = blk;

                    parent.conditionalStackElement.addAll(blk.conditionalStackElement.stream().
                            map(cse -> {
                                return new Block.ConditionalStackElement(
                                        LogicalOr.or(blk2.containingIf.conditionSlot.get(), cse.condition), cse.result);
                            }).
                            toList());
                }

                if (blk.containingLoop != null)
                    blk.nodes.add(new BreakLoopNode(blk.containingLoop.loopKey));
            } else if (blk.containingSwitch != null) {
                if (pc < instructions.length)
                    addSwitchCaseIfPresent(blk);

                for (int i = 0; i < blk.switchCases.length; i++)
                    assert blk.switchCases[i] == null : method;

                // a switchType lehet hogy null, mert ha csak egyetlen ág van, akkor nem feltétlen tudjuk
                // megállapítani a switch típusát.

                if (blk.operandStack.size() != block().operandStack.size()
                        && blk.containingSwitch.switchType == SwitchType.SWITCH_EXPRESSION) {
                    blockStack.push(blk);
                    breakSwitch(blk, new ConstantNode(1), blk.end);
                    blockStack.pop();
                }

                if (blk.operandStack.size() != block().operandStack.size())
                    // fölösleges elemek a stacken vagy nem is switch expression
                    throw unsupportedCase();

                if (blk.containingSwitch.switchType == SwitchType.SWITCH_EXPRESSION)
                    push(blk.containingSwitch);

                activateSkipConditions(parent);
            } else
                break;
        }

        addSwitchCaseIfPresent(blk);

        int lastUsePC = lastUseOf(pc);
        if (lastUsePC >= pc) {
            // ez a kavarás először ConcurrentHashMap.ForwardingNode::find miatt lett berakva
            int rangeBegin = pc + 1;
            int rangeEnd = lastUsePC;
            do {
                lastUsePC = rangeEnd;
                for (int i = rangeBegin; i < lastUsePC; i++)
                    rangeEnd = Math.max(rangeEnd, lastUseOf(i));
                rangeBegin = lastUsePC;
            } while (rangeEnd != lastUsePC);

            // A loop végének a kiszámítása jelenleg nem ad mindig pontos eredményt.
            // Pl. ha egy while-on belül van egy feltételes return, akkor az úgy fordul le, hogy a return előtt van
            // egy feltételes ugrás a ciklus elejére, és nem pedig átugorja a returnt és utána gotozik a ciklus elejére.
            // Ezért ha ilyen eset van, akkor kénytelen fallbackelni a LoopEscapingJump változók generálására.
            // Nem tudom, hogy lehetne értelmesen detektálni hogy hol van a loop vége.

            Block loopBody = new Block(pc, lastUsePC + 1, new MethodKey("loop body"));
            LoopNode loop = new LoopNode(loopBody);
            loopBody.containingLoop = loop;
            verifyBlockEnd(loopBody);
            add(loop);
            blockStack.push(loopBody);
        }
    }

    @Nonnull
    private static Node addEscapingConditionVar(Block blk, Node skipCondition) {
        LoopEscapingJumpVariable escapeV = new LoopEscapingJumpVariable();
        blk.nodes.add(new WriteLocalVar(escapeV, skipCondition));
        Node containingLoopOrSwitch = blk.containingLoop == null ? blk.containingSwitch : blk.containingLoop;
        blk.parent.nodes.add(blk.parent.nodes.indexOf(containingLoopOrSwitch),
                new WriteLocalVar(escapeV, new ConstantNode(0)));
        return new ReadLocalVar(escapeV);
    }

    private boolean activateSkipConditions(Block parent) {
        parent.skipConditions.removeIf(sc -> sc.until <= pc);
        if (!parent.skipConditions.isEmpty()) {
            int minTarget = parent.end;
            for (Block.SkipCondition c : parent.skipConditions)
                minTarget = Integer.min(minTarget, c.until);

            if (pc != parent.end)
                beginIf(pc, minTarget, parent.skipConditions.stream().
                        map(s -> s.condition).
                        reduce(LogicalOr::or).get());
            return true;
        } else
            return false;
    }

    private void addSwitchCaseIfPresent(Block blk) {
        if (pc < instructions.length && blk.switchCases != null && blk.switchCases[pc] != null) {
            blk.nodes.add(blk.switchCases[pc]);
            blk.switchCases[pc] = null;
        }
    }

    private Node merge(List<Block.ConditionalStackElement> skips) {
        PhiNode phiNode = new PhiNode();
        for (Block.ConditionalStackElement conditionalStackElement : skips) {
            phiNode.conditionList.add(conditionalStackElement.condition);
            phiNode.valueList.add(conditionalStackElement.result);
        }
        return phiNode;
    }

    @Override
    public void visitLookupSwitchInsn(Label dflt, int[] keys, Label[] labels) {
        int lastCase = (int) dflt.info;
        for (int i = 0; i < labels.length; i++) {
            int target = (int) labels[i].info;
            lastCase = Math.max(lastCase, target);
        }

        // Lehet hogy a switch "túlnyúlik" a tartalmazó loopon. Ez akkor van, ha a switch a ciklus
        // utolsó utasítása, mert akkor a switch break-et egyből a ciklus elejére ugranak.
        // Ez esetben kiszámítjuk úgy a switch hosszát hogy nem vesszük figyelembe hogy hol van a loop vége,
        // majd a loop végét meghosszabbítjuk a switch végéig.
        // pl. xml Parser.batok, illetve switchInWhileTrue teszt. Utóbbiban mondjuk nem találja el a switch
        // hosszát, de még értelmes kódot generál így is. De lehet hogy van olyan szerkezet, amikor már nem értelmes
        // kód lesz belőle.
        int containingBlockEnd = block().end;
        boolean changeContainingBlockEnd = false;
        if (lastCase > containingBlockEnd)
            if (lastCase <= block().parent.end && block().containingLoop != null) {
                containingBlockEnd = block().parent.end;
                changeContainingBlockEnd = true;
            } else
                throw unsupportedCase();

        int switchEnd;
        if (allSwitchCases.get(lastCase)) // pl. java.util.regex.Grapheme.getType
            switchEnd = lastCase;
        else {
            switchEnd = Integer.MAX_VALUE;
            for (int i = pc + 1; i < lastCase; i++) {
                if (!(instructions[i] instanceof JumpInsnNode jump))
                    continue;
                int target = (int) jump.label.getLabel().info;
                if (target >= lastCase && target < switchEnd) {
                    if (target > containingBlockEnd) {
                        // Ez akkor van, ha egy if-ben utolsó utasítás a switch, ilyenkor a break rögtön az else ágra ugrik.
                        // TODO Azt jó lenne ellenőrizni, hogy tényleg nincs-e ezután más utasítás a blokkban.
                        // pl. java.util.regex.Pattern.group0

                        int j;
                        if (block().containingSwitch != null &&
                                (j = allSwitchCases.nextSetBit(i + 1)) >= 0 && j < containingBlockEnd) {
                            target = j;
                        } else if ((block().containingIf == null
                                || block() != block().containingIf.failureBranchSlot.get()))
                            throw unsupportedCase();
                        else
                            // itt miért block().end van containingBlockEnd helyett?
                            target = block().end;
                    } else {
                        // lehet hogy a switch egy másik switchben van, ilyenkor a break közvetlenül a külső switch
                        // végére ugrik, ha nincs a külső switch-beli case-en belül a belső switch után már más
                        for (int j = lastCase + 1; j < target; j++)
                            if (allSwitchCases.get(j)) { // látszólag a külső switch másik case-ébe ugrana. ez nem lehetséges.
                                // tehát a belső switch vége ugyanaz mint a külső switch következő case-ének helye
                                target = j;
                                break;
                            }
                    }

                    switchEnd = target;
                }
            }
        }
        if (switchEnd == Integer.MAX_VALUE)
            switchEnd = lastCase;

        if (changeContainingBlockEnd)
            block().end = switchEnd;

        // System.out.println("switchEnd: " + switchEnd + " " + method);

        Switch switchNode = new Switch();
        switchNode.input.set(pop());
        add(switchNode);
        Block blk = new Block(pc, switchEnd, new MethodKey("switch body"));
        switchNode.child.set(blk);
        blk.containingSwitch = switchNode;
        blk.switchCases = new SwitchCase[switchEnd + 1];
        blockStack.push(blk);

        // egy pc tartozhat több switchben lévő case-hez is, pl. java.util.regex.Grapheme.getType
        blk.switchCases[(int) dflt.info] = SwitchCase.ofDefault();
        allSwitchCases.set((int) dflt.info);

        for (int i = 0; i < keys.length; i++) {
            int pc = (int) labels[i].info;
            if (blk.switchCases[pc] != null) {
                if (!blk.switchCases[pc].isDefault())
                    blk.switchCases[pc].values().add(keys[i]);
            } else {
                blk.switchCases[pc] = SwitchCase.ofValue(keys[i]);
                allSwitchCases.set(pc);
            }
        }
    }

    @Override
    public void visitTableSwitchInsn(int min, int max, Label dflt, Label... labels) {
        visitLookupSwitchInsn(dflt, IntStream.rangeClosed(min, max).toArray(), labels);
    }

    @Override
    public void visitJumpInsn(int opcode, Label label) {
        Node condition;
        if (opcode == GOTO) {
            condition = null;
        } else {
            condition = numericOp(opcode);
            if (condition == null)
                throw new UnsupportedOperationException(Integer.toString(opcode));
            add(condition);
        }

        int targetPC = (int) label.info;

        int a = 0;

        for (Block block : blockStack) {
            if (block.begin == targetPC) {
                assert block.containingLoop != null; // a blokk egy ciklushoz tartozik-e

                if (condition == null)
                    add(new ContinueLoopNode(block.containingLoop.loopKey));
                else
                    add(new IfElseNode(condition, new ContinueLoopNode(block.containingLoop.loopKey), null));
                return;
            } else if (block.end == targetPC) {
                if (block.containingLoop != null) {
                    if (condition == null)
                        add(new BreakLoopNode(block.containingLoop.loopKey));
                    else
                        add(new IfElseNode(condition, new BreakLoopNode(block.containingLoop.loopKey), null));
                    return;
                } else
                    break;
            }
        }

        if (condition == null)
            condition = new ConstantNode(1);

        Block foundSwitch = null;
        for (Block blk : blockStack)
            if (blk.containingSwitch != null && targetPC >= blk.end)
                foundSwitch = blk;
        if (foundSwitch != null) {
            if (targetPC > foundSwitch.parent.end) {
                if (foundSwitch.parent.containingIf != null
                        && foundSwitch.parent.containingIf.failureBranchSlot.get() == foundSwitch.parent
                        && foundSwitch.parent.nodes.indexOf(foundSwitch.containingSwitch) == foundSwitch.parent.nodes.size() - 1) {
                    // Egy ifben az utolsó utasítás a switch, és most vagyunk a breaknél.
                    // Ilyen esetben a break rögtön az else ágra ugrik, nem a switch végére.
                    // Lásd visitLookupSwitch-ben lévő komment.
                    targetPC = foundSwitch.parent.end;
                } else
                    throw unsupportedCase("jump target is " + targetPC + ", switch end is " + foundSwitch.end +
                            ", switch parent end is " + foundSwitch.parent.end);
            }
            breakSwitch(foundSwitch, condition, targetPC);
            return;
        }

        if (targetPC <= pc)
            throw unsupportedCase();

        int to = targetPC;

        // TODO kéne teszt arra is, hogy kiugrik egyszerre 2 egymásba ágyazott switch-ből későbbre

        Block blk = block();
        if (to >= blk.end) {
            if (blk.containingIf != null) {
                blk.skipConditions.add(new Block.SkipCondition(to, condition));
            } else if (blk.containingLoop != null) {
                Block blk2 = new Block(pc + 1, pc + 1, new MethodKey("jump outside loop"));
                // beforeInstruction() arra épít, hogy if-nek mindenképp a false blokkjában vagyunk
                // ezért a SkipCondition felgörgetésekor negálja az if feltételét. ezért kell negálni itt is.
                IfElseNode ifNode = new IfElseNode(add(UnaryNumericOpNode.negate(condition)), null, blk2);
                blk2.containingIf = ifNode;
                blk2.skipConditions.add(new Block.SkipCondition(to, new ConstantNode(1)));
                add(ifNode);
                verifyBlockEnd(blk2);
                blockStack.push(blk2);
                return;
            } else
                throw unsupportedCase();

            to = blk.end;
        }

        beginIf(pc + 1, to, condition);
    }

    private void breakSwitch(Block foundSwitch, Node condition, int targetPC) {
        Deque<Node> caseStack = block().operandStack;
        Deque<Node> parentStack = foundSwitch.parent.operandStack;

        BreakSwitch breakNode;
        if (caseStack.size() != parentStack.size()) {
            // switch expression

            if (caseStack.size() - parentStack.size() != 1
                    || foundSwitch.containingSwitch.switchType != null &&
                    foundSwitch.containingSwitch.switchType != SwitchType.SWITCH_EXPRESSION)
                throw unsupportedCase();

            Node value = caseStack.pop();
            breakNode = new BreakSwitch(foundSwitch.containingSwitch.switchBreakKey, value);
            foundSwitch.containingSwitch.switchType = SwitchType.SWITCH_EXPRESSION;
            foundSwitch.containingSwitch.switchExprResultType = context.lub(
                    foundSwitch.containingSwitch.switchExprResultType, value.type());
        } else {
            // switch statement

            if (foundSwitch.containingSwitch.switchType != null &&
                    foundSwitch.containingSwitch.switchType != SwitchType.SWITCH_STATEMENT)
                throw unsupportedCase();

            breakNode = new BreakSwitch(foundSwitch.containingSwitch.switchBreakKey, null);
            foundSwitch.containingSwitch.switchType = SwitchType.SWITCH_STATEMENT;
        }

        assert targetPC >= foundSwitch.end;
        if (targetPC > foundSwitch.end) {
            Node cond = addEscapingConditionVar(foundSwitch, condition);
            foundSwitch.parent.nodes.add(cond);
            foundSwitch.parent.skipConditions.add(new Block.SkipCondition(targetPC, cond));
        }

        add(new IfElseNode(condition, breakNode, null));
    }

    private void beginIf(int from, int to, Node condition) {
        IfElseNode ifNode = new IfElseNode(condition == null ? new ConstantNode(1) : condition);
        Block falseBlock = new Block(from, to, new MethodKey("if-else"));
        falseBlock.containingIf = ifNode;
        ifNode.failureBranch(falseBlock);
        verifyBlockEnd(falseBlock);
        add(ifNode);
        blockStack.push(falseBlock);
    }

    private static class LoopEscapingJumpVariable implements Variable {
        @Override
        public boolean isExact() {
            return false;
        }

        @Override
        public String toString() {
            return "LoopEscapingJump";
        }

        @Override
        public Type type() {
            return PrimitiveType.I;
        }
    }

    private RuntimeException unsupportedCase() {
        return unsupportedCase(null);
    }

    private RuntimeException unsupportedCase(String msg) {
        return new RuntimeException("unsupported case in " + declaringClass.name + " " + method.name
                + " (" + pc + "th instruction, line number " + lineNumber + ")" + (msg != null ? ": " + msg : ""));
    }

    private void verifyBlockEnd(Block blk) {
        for (Block ancestorBlock : blockStack)
            if (blk.end > ancestorBlock.end)
                throw unsupportedCase(blk.begin + "-" + blk.end + " outside " + ancestorBlock.begin + "-" + ancestorBlock.end);
    }

    private void copyStackTop(Block from) {
        List<Node> stackTop = new ArrayList<>();
        for (int rem = from.operandStack.size() - block().operandStack.size(); rem >= 0; rem--)
            stackTop.add(from.operandStack.pop());
        Collections.reverse(stackTop);
        stackTop.forEach(block().operandStack::push);
    }

    @Override
    public void visitVarInsn(int opcode, int var) {
        switch (opcode) {
            case ILOAD, LLOAD, FLOAD, DLOAD, ALOAD -> {
                LocalVar localVar = new LocalVar(method, var, Kind.KINDS.get(opcode - ILOAD), method.rootMethodIdentity);
                push(add(new ReadLocalVar(localVar)));
            }

            case ISTORE, LSTORE, FSTORE, DSTORE, ASTORE -> {
                LocalVar localVar = new LocalVar(method, var, Kind.KINDS.get(opcode - ISTORE), method.rootMethodIdentity);
                add(new WriteLocalVar(localVar, pop()));
            }

            default -> throw new RuntimeException(Integer.toString(opcode));
        }
    }

    @Override
    public void visitIincInsn(int var, int increment) {
        visitVarInsn(ILOAD, var);
        visitLdcInsn(increment);
        visitInsn(IADD);
        visitVarInsn(ISTORE, var);
    }

    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
        if (owner.startsWith("["))
            owner = "java/lang/Object";

        Clazz ownerClass = method.clazz.lookup.findClass(owner);
        MethodType methodType = MethodType.parse(descriptor, method.clazz.lookup);
        Method method = context.findMethodOrNull(ownerClass, name, methodType);
        if (method == null)
            throw new RuntimeException("no such method " + name + methodType + " in " +
                    ownerClass.name + " (opcode: " + opcode + ")\nReferenced by " + this.method);

        List<Type> argTypes = new ArrayList<>();
        if (opcode != INVOKESTATIC)
            argTypes.add(ownerClass);
        argTypes.addAll(methodType.parameterTypes());
        Node[] args = new Node[argTypes.size()];
        for (int i = argTypes.size() - 1; i >= 0; i--)
            args[i] = pop();

        Node node = switch (opcode) {
            case INVOKESPECIAL, INVOKESTATIC -> {
                if (CompilationContext.INLINE) {
                    Node n = context.parse(method);

                    for (int i = 0, j = 0; i < args.length; i++) {
                        add(new WriteLocalVar(new LocalVar(method, j, LocalVar.Kind.ofType(argTypes.get(i)),
                                method.rootMethodIdentity), args[i]));
                        j += argTypes.get(i).slotSize();
                    }

                    yield n;
                } else {
                    yield new InvokeSpecialOrStatic(method, args);
                }
            }
            case INVOKEVIRTUAL, INVOKEINTERFACE -> new InvokeVirtualOrInterface(method, args);
            default -> throw new UnsupportedOperationException(Integer.toString(opcode));
        };

        add(node);
        if (!descriptor.endsWith(")V"))
            push(node);
    }

    @Override
    public void visitInvokeDynamicInsn(String name, String descriptor, Handle bootstrapMethodHandle, Object... bsmArgs) {

        if (bootstrapMethodHandle.getOwner().equals("java/lang/invoke/StringConcatFactory")) {
            if (bootstrapMethodHandle.getTag() != H_INVOKESTATIC)
                throw new UnsupportedOperationException("MH tag not invokestatic on " + bootstrapMethodHandle);

            // TODO ez nem jól működik, ha pl. chart kasztolom intté és úgy rakom be a concatba
            // ha meg kikapcsolom, akkor pontosvessző nélküli utasítások kerülnek be a generált kódba
            handleStringConcat(MethodType.parse(descriptor, method.clazz.lookup), bootstrapMethodHandle.getName(), bsmArgs);
            return;
        }

        if (bootstrapMethodHandle.getOwner().equals("java/lang/runtime/ObjectMethods")
                && name.equals("toString")) {
            handleRecordToString(bsmArgs);
            return;
        }

        MethodType methodType = MethodType.parse(descriptor, method.clazz.lookup);

        Interpreter interpreter = context.interpreter();

        for (int i = 0; i < bsmArgs.length; i++)
            bsmArgs[i] = convertConstant(bsmArgs[i]);

        // System.out.println("bsm "+bootstrapMethodHandle+" "+Arrays.toString(bsmArgs));
        IndyLinkResult result = interpreter.linkIndy(method.clazz, bootstrapMethodHandle, name, methodType, bsmArgs);

        List<Type> realArgTypes = methodType.parameterTypes();
        Node[] args = new Node[realArgTypes.size() + (result.appendix() == null ? 0 : 1)];
        for (int i = realArgTypes.size() - 1; i >= 0; i--)
            args[i] = pop(realArgTypes.get(i));
        if (result.appendix() != null)
            args[args.length - 1] = new ConstantNode(interpreter.toConstant(result.appendix()));

        Node node = new InvokeSpecialOrStatic(result.method(), args);
        add(node);
        if (node.type() != PrimitiveType.V)
            push(node);
    }

    private void handleRecordToString(Object[] bsmArgs) {
        Type recordType = Type.parse(((org.objectweb.asm.Type) bsmArgs[0]).getDescriptor(), method.clazz.lookup);
        String[] names = ((String) bsmArgs[1]).split(";");
        ClassObj[] getters = new ClassObj[bsmArgs.length - 2];
        for (int i = 2; i < bsmArgs.length; i++)
            getters[i - 2] = (ClassObj) context.interpreter().constant(bsmArgs[i] /* MH */, method.clazz);

        Node inputNode = pop(recordType);

        String typeName = recordType.displayName(); // TODO simplename kéne
        if (getters.length == 0) {
            push(new ConstantNode(typeName + "[]"));
            return;
        }

        Node[] valNodes = new Node[getters.length];
        Type[] valTypes = new Type[getters.length];
        for (int i = 0; i < valNodes.length; i++) {
            ClassObj getter = getters[i];
            MethodType mtype = context.interpreter().methodHandleType(getter);
            Method mhInvoke = context.findMethodOrFail(context.findClass(KnownClass.METHOD_HANDLE),
                    "invokeExact", mtype);
            InvokeVirtualOrInterface invokeNode = new InvokeVirtualOrInterface(mhInvoke, new ConstantNode(getter), inputNode);
            add(invokeNode);
            valNodes[i] = invokeNode;
            valTypes[i] = mtype.returnType();
        }

        StringConcatBuilder concatBuilder = new StringConcatBuilder();
        for (int i = 0; i < names.length; i++) {
            String prefix = i == 0 ?
                    typeName + "[" + names[0] + "=" :
                    ", " + names[i] + "=";
            concatBuilder.addConstantInput(prefix);
            concatBuilder.addInput(valNodes[i], valTypes[i]);
        }
        concatBuilder.addConstantInput("]");
        push(concatBuilder.buildConcat());
    }

    private void handleStringConcat(MethodType type, String concatName, Object[] args) {
        List<Type> inputTypes = type.parameterTypes();
        Node[] inputs = new Node[inputTypes.size()];
        for (int i = inputs.length - 1; i >= 0; i--)
            inputs[i] = pop(inputTypes.get(i));

        StringConcatBuilder concatBuilder = new StringConcatBuilder();
        switch (concatName) {
            case "makeConcat" -> {
                for (int i = 0; i < inputs.length; i++)
                    concatBuilder.addInput(inputs[i], inputTypes.get(i));
            }
            case "makeConcatWithConstants" -> {
                String recipe = (java.lang.String) args[0];

                int dynArg = 0, staticArg = 1;
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < recipe.length(); i++) {
                    char ch = recipe.charAt(i);
                    switch (ch) {
                        case '\1' -> {
                            if (!sb.isEmpty()) {
                                concatBuilder.addConstantInput(sb.toString());
                                sb.setLength(0);
                            }
                            concatBuilder.addInput(inputs[dynArg], inputTypes.get(dynArg));
                            dynArg++;
                        }
                        case '\2' -> {
                            Object arg = args[staticArg++];
                            sb.append(concatBuilder.constantToString(arg));
                        }
                        default -> sb.append(ch);
                    }
                }
                if (!sb.isEmpty()) {
                    concatBuilder.addConstantInput(sb.toString());
                    sb.setLength(0);
                }
            }
        }
        push(concatBuilder.buildConcat());
    }

    private class StringConcatBuilder {

        private final Clazz String = context.findClass(KnownClass.STRING);
        private final Clazz Object = context.findClass(KnownClass.OBJECT);
        private final Method String_valueOf = context.findMethodOrFail(String, "valueOf",
                new MethodType(List.of(Object), String));
        private final Method String_concat = context.findMethodOrFail(String, "concat",
                new MethodType(List.of(String), String));

        private Node currentNode;

        public void addInput(Node n, Type type) {
            if (!(n instanceof ConstantNode constantNode && constantNode.value instanceof String))
                // ha ConstantNode de más típusú, akkor meg lehetne hívni constantToStringet rajta, bár
                // valszeg nem nagyon vannak olyan esetek amikor ez hasznos lenne
                n = add(new InvokeSpecialOrStatic(converter(type), new Node[]{n}));

            addInputImpl(n);
        }

        public void addConstantInput(String s) {
            addInputImpl(new ConstantNode(s));
        }

        private void addInputImpl(Node n) {
            if (currentNode == null)
                currentNode = n;
            else
                currentNode = add(new InvokeVirtualOrInterface(String_concat, new Node[]{currentNode, n}));
        }

        private Method converter(Type type) {
            if (type instanceof ReferenceType)
                return String_valueOf;
            else {
                if (type == PrimitiveType.B || type == PrimitiveType.S)
                    type = PrimitiveType.I;
                return context.findMethodOrFail(String, "valueOf",
                        new MethodType(List.of(type), String));
            }
        }

        public Node buildConcat() {
            if (currentNode == null)
                return new ConstantNode("");
            else
                return currentNode;
        }

        public String constantToString(Object constant) {
            if (constant == null)
                return "null";
            if (constant instanceof String s)
                return s;
            // nem világos, hogy charral mi van
            if (constant instanceof Number n)
                return n.toString();
            // ha bonyolultabb objektum, interpreterrel meg kéne hívni a toStringjét
            throw new UnsupportedOperationException("unsupported static argument type in" +
                    " string concat: " + constant.getClass().getName());
        }
    }

    @Override
    public void visitFieldInsn(int opcode, String owner, String name, String descriptor) {
        Clazz clazz = method.clazz.lookup.findClass(owner);
        Field field = context.field(clazz, name, Type.parse(descriptor, method.clazz.lookup));

        switch (opcode) {
            case PUTFIELD -> {
                Node value = pop();
                Node object = pop();
                add(new PutFieldNode(object, field, value));
            }
            case GETFIELD -> {
                Node object = pop();
                push(add(new GetFieldNode(object, field)));
            }
            case GETSTATIC -> push(add(new GetStaticNode(field)));
            case PUTSTATIC -> add(new PutStaticNode(field, pop()));
            default -> throw new UnsupportedOperationException(Integer.toString(opcode));
        }
    }

    @Override
    public void visitInsn(int opcode) {
        switch (opcode) {
            case ACONST_NULL -> visitLdcInsn(null);
            case ICONST_M1 -> visitLdcInsn(-1);
            case ICONST_0 -> visitLdcInsn(0);
            case ICONST_1 -> visitLdcInsn(1);
            case ICONST_2 -> visitLdcInsn(2);
            case ICONST_3 -> visitLdcInsn(3);
            case ICONST_4 -> visitLdcInsn(4);
            case ICONST_5 -> visitLdcInsn(5);
            case LCONST_0 -> visitLdcInsn(0L);
            case LCONST_1 -> visitLdcInsn(1L);
            case FCONST_0 -> visitLdcInsn(0F);
            case FCONST_1 -> visitLdcInsn(1F);
            case FCONST_2 -> visitLdcInsn(2F);
            case DCONST_0 -> visitLdcInsn(0D);
            case DCONST_1 -> visitLdcInsn(1D);
            case RETURN -> add(new ReturnNode(blockStack.getLast().methodKey, null));
            case ARETURN, IRETURN, FRETURN, DRETURN, LRETURN ->
                    add(new ReturnNode(blockStack.getLast().methodKey, pop()));
            case DUP -> push(block().operandStack.peek());
            case DUP_X1 -> {
                Node v1 = block().operandStack.pop();
                Node v2 = block().operandStack.pop();
                block().operandStack.push(v1);
                block().operandStack.push(v2);
                block().operandStack.push(v1);
            }
            case DUP_X2 -> {
                Node v1 = block().operandStack.pop();
                Node v2 = block().operandStack.pop();
                // ez a komment még aktuális?
                // ilyen van DMH.preparedFieldLambdaFormban. megnézhetnénk, hogy mire van használva.
                // kéne csinálni ReadLocalVarnak type() implementációt, mert ehhez kéne
                if (v2.type().slotSize() == 2) {
                    block().operandStack.push(v1);
                    block().operandStack.push(v2);
                    block().operandStack.push(v1);
                } else {
                    Node v3 = block().operandStack.pop();
                    block().operandStack.push(v1);
                    block().operandStack.push(v3);
                    block().operandStack.push(v2);
                    block().operandStack.push(v1);
                }
            }
            case DUP2 -> {
                Node v1 = block().operandStack.pop();
                if (v1.type().slotSize() == 2) {
                    block().operandStack.push(v1);
                    block().operandStack.push(v1);
                } else {
                    Node v2 = block().operandStack.pop();
                    block().operandStack.push(v2);
                    block().operandStack.push(v1);
                    block().operandStack.push(v2);
                    block().operandStack.push(v1);
                }
            }
            case DUP2_X1 -> {
                Node v1 = block().operandStack.pop();
                Node v2 = block().operandStack.pop();
                if (v1.type().slotSize() == 2) {
                    block().operandStack.push(v1);
                    block().operandStack.push(v2);
                    block().operandStack.push(v1);
                } else {
                    Node v3 = block().operandStack.pop();
                    block().operandStack.push(v2);
                    block().operandStack.push(v1);
                    block().operandStack.push(v3);
                    block().operandStack.push(v2);
                    block().operandStack.push(v1);
                }
            }
            case IASTORE, SASTORE, BASTORE, CASTORE, AASTORE, LASTORE, DASTORE, FASTORE -> {
                Node value = pop();
                Node index = pop();
                Node array = pop();
                add(new ArrayStore(array, index, value,
                        estimateArrayElementType(opcode - IASTORE, array.type(), value.type())));
            }
            case AALOAD, BALOAD, CALOAD, DALOAD, FALOAD, IALOAD, LALOAD, SALOAD -> {
                Node index = pop();
                Node array = pop();
                push(add(new ArrayLoad(array, index,
                        estimateArrayElementType(opcode - IALOAD, array.type(), null))));
            }
            case POP, MONITORENTER, MONITOREXIT -> pop();
            case POP2 -> {
                Node o = pop();
                if (o.type().slotSize() != 2)
                    pop();
            }
            case ATHROW -> add(new ThrowNode(pop()));
            case ARRAYLENGTH -> push(add(new ArrayLength(pop())));
            default -> {
                Node n = numericOp(opcode);
                if (n != null)
                    push(add(n));
                else
                    throw new UnsupportedOperationException(Integer.toString(opcode));
            }
        }
    }

    private Type estimateArrayElementType(int arrayInstructionOffset, Type arrayType, Type valueType) {
        if (arrayInstructionOffset == 4) { // ha nem reference típus az elemtípus, akkor pontosan lehet tudni a bytecode-ból
            if (valueType != null)
                return valueType;
            if (arrayType instanceof ArrayType arrayType1)
                return arrayType1.elementType();
        }
        return switch (arrayInstructionOffset) {
            case 0 -> PrimitiveType.I;
            case 1 -> PrimitiveType.J;
            case 2 -> PrimitiveType.F;
            case 3 -> PrimitiveType.D;
            case 4 -> context.findClass(KnownClass.OBJECT);
            case 5 -> PrimitiveType.B;
            case 6 -> PrimitiveType.C;
            case 7 -> PrimitiveType.S;
            default -> throw new IllegalArgumentException();
        };
    }

    @Override
    public void visitTypeInsn(int opcode, String typeString) {
        Type type = typeString.startsWith("[")
                ? Type.parse(typeString, method.clazz.lookup)
                : method.clazz.lookup.findClass(typeString);

        switch (opcode) {
            case NEW -> push(add(new ObjectNode(new ObjectIdentity((Clazz) type))));
            case ANEWARRAY -> push(add(new AllocateArray(new ArrayType(type), pop())));
            case CHECKCAST -> push(add(new CheckCast(pop(), type)));
            case INSTANCEOF -> push(add(new InstanceOf(pop(), type)));
            default -> throw new UnsupportedOperationException(Integer.toString(opcode));
        }
    }

    @Override
    public void visitMultiANewArrayInsn(String descriptor, int numDimensions) {
        List<Node> lengths = new ArrayList<>();
        for (int i = 0; i < numDimensions; i++)
            lengths.add(0, pop());
        push(add(new AllocateArray((ArrayType) Type.parse(descriptor, method.clazz.lookup), lengths)));
    }

    private static final ArrayType[] PRIMITIVE_ARRAY_TYPES = {
            null, null, null, null,
            (ArrayType) Type.of(boolean[].class), (ArrayType) Type.of(char[].class), (ArrayType) Type.of(float[].class),
            (ArrayType) Type.of(double[].class), (ArrayType) Type.of(byte[].class), (ArrayType) Type.of(short[].class),
            (ArrayType) Type.of(int[].class), (ArrayType) Type.of(long[].class)
    };

    @Override
    public void visitIntInsn(int opcode, int operand) {
        switch (opcode) {
            case BIPUSH, SIPUSH -> visitLdcInsn(operand);
            case NEWARRAY -> push(add(new AllocateArray(PRIMITIVE_ARRAY_TYPES[operand], pop())));
            default -> throw new UnsupportedOperationException(Integer.toString(opcode));
        }
    }

    @Override
    public void visitLdcInsn(Object value) {
        value = convertConstant(value);
        push(add(new ConstantNode(value)));
    }

    private Object convertConstant(Object value) {
        if (value instanceof org.objectweb.asm.Type t) {
            if (t.getSort() == org.objectweb.asm.Type.METHOD)
                value = MethodType.parse(t.getDescriptor(), method.clazz.lookup);
            else
                value = Type.parse(t.getDescriptor(), method.clazz.lookup);
        }
        return value;
    }

    private Node numericOp(int op) {
        OpInfo opInfo = NumericOps.ops[op];
        if (opInfo == null)
            return null;
        return switch (opInfo.inputArgCount) {
            case 1 -> new UnaryNumericOpNode(pop(), opInfo);
            case 2 -> {
                Node input2 = pop();
                Node input1 = pop();
                yield new BinaryNumericOpNode(input1, input2, opInfo);
            }
            default -> throw new RuntimeException(opInfo.toString());
        };
    }

    private void push(Node node) {
        assert node != null;
        Block block = block();
        //assert block.containsNodeOrAncestorContainsIt(node);
        block.operandStack.push(node);
    }

    private Node pop() {
        return block().operandStack.pop();
    }

    private Node pop(Type expectedType) {
        if (expectedType instanceof PrimitiveType primitiveType)
            expectedType = switch (primitiveType) {
                case B, Z, S, C -> PrimitiveType.I;
                default -> primitiveType;
            };

        Node node = pop();
        Type actualType = node.type();
        if (actualType != null) {
            if (actualType instanceof PrimitiveType primitiveType)
                actualType = switch (primitiveType) {
                    case B, Z, S, C -> PrimitiveType.I;
                    default -> primitiveType;
                };
            if (expectedType != actualType
                    && !(expectedType instanceof ReferenceType && actualType instanceof ReferenceType))
                throw new RuntimeException("type mismatch on popped entry: " + node + " has type " + actualType +
                        ", but expected " + expectedType);
        }
        return node;
    }

    private int lastUseOf(int label) {
        int lastUse = -1;
        // i=pc+1 volt, de feltétel nélküli végtelen ciklusok miatt átírtam i=pc-re
        for (int i = pc; i < instructions.length; i++) {
            AbstractInsnNode insn = instructions[i];
            if (insn instanceof JumpInsnNode && (int) ((JumpInsnNode) insn).label.getLabel().info == label)
                lastUse = i;
        }
        return lastUse;
    }

    @Nonnull
    private Block block() {
        assert !blockStack.isEmpty();
        return blockStack.peek();
    }

    private Node add(Node node) {
        assert node != null;
        block().nodes.add(node);
        return node;
    }

    private class Block extends SequenceNode {

        final Block parent;
        final int begin;

        /**
         * exclusive
         */
        int end;

        IfElseNode containingIf;
        LoopNode containingLoop;
        Switch containingSwitch;

        SwitchCase[] switchCases;

        final Deque<Node> operandStack = new LinkedList<>();

        final List<SkipCondition> skipConditions = new ArrayList<>();

        final List<ConditionalStackElement> conditionalStackElement = new ArrayList<>();

        /**
         * @param until
         * @param condition ha ez false, akkor nem hajtódik végre a megjelölt kódrészlet
         */
        record SkipCondition(int until, Node condition) {
        }

        record ConditionalStackElement(Node condition, Node result) {
        }

        public Block(int begin, int end, MethodKey methodKey) {
            super(methodKey);
            this.parent = blockStack.peek();
            this.begin = begin;
            this.end = end;

            if (parent != null)
                operandStack.addAll(parent.operandStack);
        }
    }
}
