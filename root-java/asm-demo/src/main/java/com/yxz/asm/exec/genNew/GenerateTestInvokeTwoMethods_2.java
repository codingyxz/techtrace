package com.yxz.asm.exec.genNew;

import com.yxz.asm.util.FileUtils;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.*;

/**
 * import java.util.Date;
 * <p>
 * public class TestInvokeTwoMethods_2 {
 * *    public void test() {
 * *        System.out.println("This is a test method.");
 * *    }
 * *
 * *    public void printDate() {
 * *        Date now = new Date();
 * *        System.out.println(now);
 * *    }
 * }
 */
public class GenerateTestInvokeTwoMethods_2 {

    public static void main(String[] args) throws Exception {
        String relative_path = "sample/TestInvokeTwoMethods_2.class";
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
        cw.visit(V1_8, ACC_PUBLIC + ACC_SUPER, "sample/TestInvokeTwoMethods_2",
                null, "java/lang/Object", null);

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
            // 第1部分，mv2
            MethodVisitor mv2 = cw.visitMethod(ACC_PUBLIC, "test", "()V", null, null);

            // 第2部分，mv3
            MethodVisitor mv3 = cw.visitMethod(ACC_PUBLIC, "printDate", "()V", null, null);
            mv3.visitCode();
            mv3.visitTypeInsn(NEW, "java/util/Date");
            mv3.visitInsn(DUP);
            mv3.visitMethodInsn(INVOKESPECIAL, "java/util/Date", "<init>", "()V", false);

            // 第3部分，mv2
            mv2.visitCode();
            mv2.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
            mv2.visitLdcInsn("This is a test method.");
            mv2.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);

            // 第4部分，mv3
            mv3.visitVarInsn(ASTORE, 1);
            mv3.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
            mv3.visitVarInsn(ALOAD, 1);
            mv3.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/Object;)V", false);

            // 第5部分，mv2
            mv2.visitInsn(RETURN);
            mv2.visitMaxs(2, 1);
            mv2.visitEnd();

            // 第6部分，mv3
            mv3.visitInsn(RETURN);
            mv3.visitMaxs(2, 2);
            mv3.visitEnd();
        }

        cw.visitEnd();

        // (3) 调用toByteArray()方法
        return cw.toByteArray();
    }
}
