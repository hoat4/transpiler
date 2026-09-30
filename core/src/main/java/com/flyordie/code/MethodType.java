package com.flyordie.code;

import com.flyordie.code.Type.ArrayType;
import com.flyordie.code.Type.Lookup;
import com.flyordie.code.Type.PrimitiveType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public record MethodType(List<Type> parameterTypes, Type returnType) {

    public MethodType {
        parameterTypes = Collections.unmodifiableList(parameterTypes);
    }

    public int argumentsSize() {
        return parameterTypes.stream().mapToInt(Type::slotSize).sum();
    }

    public MethodType withArgTypes(List<Type> argTypes) {
        return new MethodType(argTypes, returnType);
    }

    public String descriptor() {
        StringBuilder sb = new StringBuilder();
        sb.append('(');
        for (Type paramType : parameterTypes)
            sb.append(paramType.descriptor());
        sb.append(')');
        sb.append(returnType.descriptor());
        return sb.toString();
    }

    public MethodType dropFirstArgument() {
        return new MethodType(parameterTypes.subList(1, parameterTypes.size()), returnType);
    }

    public MethodType prependArg(Type type) {
        List<Type> args = new ArrayList<>();
        args.add(type);
        args.addAll(parameterTypes);
        return new MethodType(args, returnType);
    }

    public MethodType withReturnType(Type type) {
        return new MethodType(parameterTypes, type);
    }

    @Override
    public String toString() {
        return descriptor();
    }

    public static MethodType parse(String descriptor, Lookup lookup) {
        if (descriptor.charAt(0) != '(')
            throw new IllegalArgumentException("not a valid method type descriptor: " + descriptor);

        List<Type> parameterTypes = new ArrayList<>();
        boolean returnType = false;

        for (int i = 1, dims = 0; ; i++) {
            char ch = descriptor.charAt(i);
            if (ch == '[') {
                dims++;
                continue;
            }
            if (ch == ')') {
                returnType = true;
                continue;
            }

            Type t;
            if (ch == 'L') {
                int end = descriptor.indexOf(';', ++i);
                t = lookup.findClass(descriptor.substring(i, end));
                i = end;
            } else
                t = PrimitiveType.ofDescriptorCharacter(ch);

            while (dims != 0) {
                t = new ArrayType(t);
                dims--;
            }

            if (returnType)
                return new MethodType(parameterTypes, t);
            else
                parameterTypes.add(t);
        }
    }
}
