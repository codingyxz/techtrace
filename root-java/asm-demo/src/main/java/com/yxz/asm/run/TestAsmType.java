package com.yxz.asm.run;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.util.Printer;

public class TestAsmType {

    public static void main(String[] args) throws Exception {
        testTypeInfo();
        System.out.println("--------------------------");
        testArrayRelatedMethod();
        System.out.println("--------------------------");
        testMethodRelatedMethod();
        System.out.println("--------------------------");
        testSizeRelatedMethod();
        System.out.println("--------------------------");
        testOpcodeRelatedMethod();
    }

    /**
     * opcode相关
     */
    public static void testOpcodeRelatedMethod() {
        Type t = Type.FLOAT_TYPE;

        int[] opcodes = new int[]{
                Opcodes.IALOAD,
                Opcodes.IASTORE,
                Opcodes.ILOAD,
                Opcodes.ISTORE,
                Opcodes.IADD,
                Opcodes.ISUB,
                Opcodes.IRETURN,
        };

        for (int oldOpcode : opcodes) {
            int newOpcode = t.getOpcode(oldOpcode);

            String oldName = Printer.OPCODES[oldOpcode];
            String newName = Printer.OPCODES[newOpcode];

            System.out.printf("%-7s --- %-7s%n", oldName, newName);
        }
    }

    /**
     * 获取类型所对应的slot空间大小、与方法所对应的slot空间大小
     */
    public static void testSizeRelatedMethod() {
        Type t1 = Type.LONG_TYPE;
        System.out.println(t1.getSize()); // 2

        Type t2 = Type.INT_TYPE;
        System.out.println(t2.getSize()); // 1

        Type t = Type.getMethodType("(II)I");
        int value = t.getArgumentsAndReturnSizes();
        int argumentsSize = value >> 2;
        int returnSize = value & 0b11;
        System.out.println(value);
        System.out.println(argumentsSize); // 3
        System.out.println(returnSize);    // 1
    }


    /**
     * 获取方法接收的参数类型与返回值类型
     */
    public static void testMethodRelatedMethod() {
        Type methodType = Type.getMethodType("(Ljava/lang/String;I)V");

        String descriptor = methodType.getDescriptor();
        Type[] argumentTypes = methodType.getArgumentTypes();
        Type returnType = methodType.getReturnType();

        System.out.println("Descriptor: " + descriptor);
        System.out.println("Argument Types:");
        for (Type t : argumentTypes) {
            System.out.println("    " + t);
        }
        System.out.println("Return Type: " + returnType);
    }

    /**
     * 获取数组维度、与元素类型
     */
    public static void testArrayRelatedMethod() {
        Type t = Type.getType("[[[[[Ljava/lang/String;");

        int dimensions = t.getDimensions();
        Type elementType = t.getElementType();

        System.out.println(dimensions);    // 5
        System.out.println(elementType);   // Ljava/lang/String;
    }


    /**
     * 获取type类型以及详细信息
     */
    public static void testTypeInfo() {
        Type t = Type.getType("Ljava/lang/String;");

        int sort = t.getSort();                    // ASM
        String className = t.getClassName();       // Java File
        String internalName = t.getInternalName(); // Class File
        String descriptor = t.getDescriptor();     // Class File

        System.out.println(sort);         // 10，它对应于Type.OBJECT字段
        System.out.println(className);    // java.lang.String   注意，分隔符是“.”
        System.out.println(internalName); // java/lang/String   注意，分隔符是“/”
        System.out.println(descriptor);   // Ljava/lang/String; 注意，分隔符是“/”，前有“L”，后有“;”
    }


}
