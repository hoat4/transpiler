package com.flyordie.code;

import com.flyordie.code.Clazz.Field;
import com.flyordie.code.Clazz.Method;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.RecordComponentNode;

import java.util.Arrays;

public class ClassfileUtil {

    public static byte[] readClassAttribute(Clazz clazz, String attributeName) {
        ClassReader cr = clazz.classReader;
        int attrOffset = findClassAttribute(clazz, attributeName);
        if (attrOffset == -1)
            return null;
        else {
            int attrLength = cr.readInt(attrOffset + 2);
            return Arrays.copyOfRange(cr.b, attrOffset + 6, attrOffset + 6 + attrLength);
        }
    }

    // ez nem a tartalom kezdetét adja meg, hanem a teljes attribute struktúra kezdetét, tehát
    // amiben benne van az attribute_nam_index meg az attribute_length is
    public static int findClassAttribute(Clazz clazz, String attributeName) {
        ClassReader cr = clazz.classReader;
        int attrOffset = getFirstAttributeOffset(cr);
        while (attrOffset < cr.b.length) {
            int a = cr.readUnsignedShort(attrOffset);
            if (a == 0) // a byte[] túl van méretezve, ezért nem áll meg a ciklus ha kifutunk a classfileból
                break;
            if (cr.readUTF8(attrOffset, clazz.crCharBuffer).equals(attributeName))
                return attrOffset;

            int attrLength = cr.readInt(attrOffset + 2);
            attrOffset += attrLength + 6;
        }
        return -1;
    }

    public static byte[] readFieldAttribute(Field field, String attributeName) {
        Clazz clazz = field.clazz;
        ClassReader classReader = clazz.classReader;

        int currentOffset = classReader.header + 8 + classReader.readUnsignedShort(classReader.header + 6) * 2;
        int fieldsCount = classReader.readUnsignedShort(currentOffset);
        currentOffset += 2;
        while (fieldsCount-- > 0) {
            String name = classReader.readUTF8(currentOffset + 2, clazz.crCharBuffer);
            String type = classReader.readUTF8(currentOffset + 4, clazz.crCharBuffer);
            int attributesCount = classReader.readUnsignedShort(currentOffset + 6);
            currentOffset += 8;
            boolean thisField = name.equals(field.name) && type.equals(field.desc);
            while (attributesCount-- > 0) {
                final int attrLen = classReader.readInt(currentOffset + 2);
                if (thisField) {
                    String attrName = classReader.readUTF8(currentOffset, clazz.crCharBuffer);
                    if (attrName.equals(attributeName))
                        return Arrays.copyOfRange(classReader.b, currentOffset + 6, currentOffset + 6 + attrLen);
                }
                currentOffset += 6 + attrLen;
            }
            if (thisField)
                return null;
        }
        throw new RuntimeException("field not found: " + clazz.name + "." + field.name + " " + field.type());
    }

    public static byte[] readMethodAttribute(Method method, String attributeName) {
        Clazz clazz = method.clazz;
        ClassReader classReader = clazz.classReader;

        int currentOffset = classReader.header + 8 + classReader.readUnsignedShort(classReader.header + 6) * 2;

        int fieldCount = classReader.readUnsignedShort(currentOffset);
        currentOffset += 2;
        while (fieldCount-- > 0) {
            int attributesCount = classReader.readUnsignedShort(currentOffset + 6);
            currentOffset += 8;
            while (attributesCount-- > 0)
                currentOffset += 6 + classReader.readInt(currentOffset + 2);
        }

        int methodCount = classReader.readUnsignedShort(currentOffset);
        currentOffset += 2;
        while (methodCount-- > 0) {
            String name = classReader.readUTF8(currentOffset + 2, clazz.crCharBuffer);
            String type = classReader.readUTF8(currentOffset + 4, clazz.crCharBuffer);
            int attributesCount = classReader.readUnsignedShort(currentOffset + 6);
            currentOffset += 8;
            boolean thisMethod = name.equals(method.name) && type.equals(method.desc);
            while (attributesCount-- > 0) {
                final int attrLen = classReader.readInt(currentOffset + 2);
                if (thisMethod) {
                    String attrName = classReader.readUTF8(currentOffset, clazz.crCharBuffer);
                    if (attrName.equals(attributeName))
                        return Arrays.copyOfRange(classReader.b, currentOffset + 6, currentOffset + 6 + attrLen);
                }
                currentOffset += 6 + attrLen;
            }
            if (thisMethod)
                return null;
        }
        throw new RuntimeException("method not found: " + clazz.name + "." + method.name + " " + method.type());
    }

    public static byte[] readRecordComponentAttribute(Clazz clazz, RecordComponentNode rc, String name) {
        int p = findClassAttribute(clazz, "Record");
        if (p == -1)
            throw new RuntimeException();
        ClassReader cr = clazz.classReader;

        p += 6; // attribute_name_index, attribute_length
        int componentsCount = cr.readUnsignedShort(p) & 0xFFFF;
        p += 2;

        for (int i = 0; i < componentsCount; i++) {
            String rcName = cr.readUTF8(p, clazz.crCharBuffer);
            String rcDesc = cr.readUTF8(p + 2, clazz.crCharBuffer);
            p += 4; // name, desc
            int attrCount = cr.readUnsignedShort(p);
            p += 2;

            boolean found = rcName.equals(rc.name) && rcDesc.equals(rc.descriptor);
            for (int j = 0; j < attrCount; j++) {
                String attrName = cr.readUTF8(p, clazz.crCharBuffer);
                int attrLen = cr.readInt(p + 2);
                p += 6;
                if (found && attrName.equals(name)) {
                    return Arrays.copyOfRange(cr.b, p, p + attrLen);
                }
                p += attrLen;
            }
            if (found)
                return null;
        }
        throw new RuntimeException("record component not found");
    }

    // ez kimásolva ClassReaderből, ott nem volt publikus

    /**
     * Returns the offset in classFileBuffer of the first ClassFile's 'attributes' array field entry.
     *
     * @return the offset in classFileBuffer of the first ClassFile's 'attributes' array field entry.
     */
    private static final int getFirstAttributeOffset(ClassReader classReader) {
        // Skip the access_flags, this_class, super_class, and interfaces_count fields (using 2 bytes
        // each), as well as the interfaces array field (2 bytes per interface).
        int currentOffset = classReader.header + 8 + classReader.readUnsignedShort(classReader.header + 6) * 2;

        // Read the fields_count field.
        int fieldsCount = classReader.readUnsignedShort(currentOffset);
        currentOffset += 2;
        // Skip the 'fields' array field.
        while (fieldsCount-- > 0) {
            // Invariant: currentOffset is the offset of a field_info structure.
            // Skip the access_flags, name_index and descriptor_index fields (2 bytes each), and read the
            // attributes_count field.
            int attributesCount = classReader.readUnsignedShort(currentOffset + 6);
            currentOffset += 8;
            // Skip the 'attributes' array field.
            while (attributesCount-- > 0) {
                // Invariant: currentOffset is the offset of an attribute_info structure.
                // Read the attribute_length field (2 bytes after the start of the attribute_info) and skip
                // this many bytes, plus 6 for the attribute_name_index and attribute_length fields
                // (yielding the total size of the attribute_info structure).
                currentOffset += 6 + classReader.readInt(currentOffset + 2);
            }
        }

        // Skip the methods_count and 'methods' fields, using the same method as above.
        int methodsCount = classReader.readUnsignedShort(currentOffset);
        currentOffset += 2;
        while (methodsCount-- > 0) {
            int attributesCount = classReader.readUnsignedShort(currentOffset + 6);
            currentOffset += 8;
            while (attributesCount-- > 0) {
                currentOffset += 6 + classReader.readInt(currentOffset + 2);
            }
        }

        // Skip the ClassFile's attributes_count field.
        return currentOffset + 2;
    }
}
