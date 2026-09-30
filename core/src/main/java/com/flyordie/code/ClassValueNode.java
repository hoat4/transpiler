package com.flyordie.code;

import com.flyordie.code.Interpreter.ClassObj;

import java.util.Collections;
import java.util.List;

import static com.flyordie.code.CompilationContext.context;

public class ClassValueNode extends Node {

    public final ClassObj classValueInstance;
    public final Type baseType;
    public final InputSlot typeObjSlot = new InputSlot();

    public ClassValueNode(ClassObj classValueInstance, Type baseType, Node n) {
        //if (baseType instanceof Clazz c && c.knownClass == KnownClass.OBJECT)
        //    throw new RuntimeException("ClassValue scope too broad: " + baseType + ". Location: " + n.location);
        this.baseType = baseType;
        this.classValueInstance = classValueInstance;
        typeObjSlot.set(n);
    }

    @Override
    public Type type() {
        return context().findClass(KnownClass.OBJECT);
    }

    @Override
    public List<SideEffect> sideEffects() {
        return Collections.emptyList();
    }

    @Override
    protected Node cloneImpl(Cloner cloner) {
        return new ClassValueNode(classValueInstance, baseType, cloner.clone(typeObjSlot));
    }
}
