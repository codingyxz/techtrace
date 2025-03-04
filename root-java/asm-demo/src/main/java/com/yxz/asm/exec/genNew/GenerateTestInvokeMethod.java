package com.yxz.asm.exec.genNew;

import com.yxz.asm.util.FileUtils;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.*;

/**
 * 期望:
 * public class TestInvokeMethod {
 * *    public void test(int a, int b) {
 * *        int val = Math.max(a, b);  // 对static方法进行调用
 * *        System.out.println(val);   // 对non-static方法进行调用
 * *    }
 * }
 */
public class GenerateTestInvokeMethod {

    public static void main(String[] args) throws Exception {
        String relative_path = "sample/TestInvokeMethod.class";
        String filepath = FileUtils.getFilePath(relative_path);

        // (1) 生成byte[]内容
        byte[] bytes = dump();

        // (2) 保存byte[]到文件
        FileUtils.writeBytes(filepath, bytes);
    }

    public static byte[] dump() throws Exception {
        // (1) 创建ClassWriter对象
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);

        // (2) 调用visitXxx()方法
        cw.visit(V1_8, ACC_PUBLIC + ACC_SUPER, "sample/TestInvokeMethod", null, "java/lang/Object", null);

        {
            MethodVisitor mv1 = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
            mv1.visitCode();
            mv1.visitVarInsn(ALOAD, 0);
            mv1.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
            mv1.visitInsn(RETURN);
            mv1.visitMaxs(1, 1);
            mv1.visitEnd();
        }

        {
            MethodVisitor mv2 = cw.visitMethod(ACC_PUBLIC, "test", "(II)V", null, null);
            mv2.visitCode();
            mv2.visitVarInsn(ILOAD, 1);
            mv2.visitVarInsn(ILOAD, 2);
            mv2.visitMethodInsn(INVOKESTATIC, "java/lang/Math", "max", "(II)I", false);
            mv2.visitVarInsn(ISTORE, 3);
            mv2.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
            mv2.visitVarInsn(ILOAD, 3);
            mv2.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(I)V", false);
            mv2.visitInsn(RETURN);
            mv2.visitMaxs(2, 4);
            mv2.visitEnd();
        }

        cw.visitEnd();

        // (3) 调用toByteArray()方法
        return cw.toByteArray();
    }

}
