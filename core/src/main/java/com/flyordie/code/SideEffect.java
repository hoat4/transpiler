package com.flyordie.code;

import com.flyordie.code.Variable.ArrayElement;
import com.flyordie.code.Variable.InstanceField;
import com.flyordie.code.Variable.LocalVar;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.FieldNode;

import java.util.List;
import java.util.Objects;

public interface SideEffect {

    boolean interferesWith(List<SideEffect> o);

    Variable readVariable();

    Variable writtenVariable();

    class BreakControlFlow implements SideEffect {
        public static final BreakControlFlow INSTANCE = new BreakControlFlow();

        @Override
        public boolean interferesWith(List<SideEffect> o) {
            return o.contains(INSTANCE);
        }

        @Override
        public Variable readVariable() {
            return null;
        }

        @Override
        public Variable writtenVariable() {
            return null;
        }
    }

    class Allocation implements SideEffect {
        public static final Allocation INSTANCE = new Allocation();

        @Override
        public boolean interferesWith(List<SideEffect> o) {
            return o.contains(INSTANCE);
        }

        @Override
        public Variable readVariable() {
            return null;
        }

        @Override
        public Variable writtenVariable() {
            return null;
        }
    }

    record Write(Variable variable) implements SideEffect {

        @Override
        public boolean interferesWith(List<SideEffect> o) {
            return o.stream().anyMatch(a -> a instanceof Write w && w.variable.equals(variable)
                    || a instanceof Read r && r.variable.equals2(variable));
        }

        @Override
        public Variable readVariable() {
            return null;
        }

        @Override
        public Variable writtenVariable() {
            return variable;
        }
    }

    record Read(Variable variable) implements SideEffect {
        @Override
        public boolean interferesWith(List<SideEffect> o) {
            return o.stream().anyMatch(a -> a instanceof Write w && w.variable.equals(variable));
        }

        @Override
        public Variable readVariable() {
            return variable;
        }

        @Override
        public Variable writtenVariable() {
            return null;
        }
    }
}
