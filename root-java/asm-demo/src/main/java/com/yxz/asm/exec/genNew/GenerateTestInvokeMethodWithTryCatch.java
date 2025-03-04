package com.yxz.asm.exec.genNew;

import com.yxz.asm.util.FileUtils;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

import java.lang.reflect.Method;

import static org.objectweb.asm.Opcodes.*;

/**
 * public class TestInvokeMethodWithTryCatch {
 * *    public void test() {
 * *        try {
 * *            System.out.println("Before Sleep");
 * *            Thread.sleep(1000);
 * *            System.out.println("After Sleep");
 * *        } catch (InterruptedException e) {
 * *            e.printStackTrace();
 * *        }
 * *    }
 * }
 */
public class GenerateTestInvokeMethodWithTryCatch {

    public static void main(String[] args) throws Exception {
        String relative_path = "sample/TestInvokeMethodWithTryCatch.class";
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
        cw.visit(V1_8, ACC_PUBLIC + ACC_SUPER, "sample/TestInvokeMethodWithTryCatch",
                null, "java/lang/Object", null);

        {
            MethodVisitor mv1 = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
            mv1.visitCode();
            mv1.visitVarInsn(ALOAD, 0);
            mv1.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
            mv1.visitInsn(RETURN);
            mv1.visitMaxs(0, 0);
            mv1.visitEnd();
        }

        {
            MethodVisitor mv2 = cw.visitMethod(ACC_PUBLIC, "test", "()V", null, null);
            Label startLabel = new Label();
            Label endLabel = new Label();
            Label handlerLabel = new Label();
            Label returnLabel = new Label();

            // 第1段
            mv2.visitCode();
            // visitTryCatchBlock可以在这里访问
            mv2.visitTryCatchBlock(startLabel, endLabel, handlerLabel, "java/lang/InterruptedException");

            // 第2段
            mv2.visitLabel(startLabel);
            mv2.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
            mv2.visitLdcInsn("Before Sleep");
            mv2.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
            mv2.visitLdcInsn(new Long(1000L));
            mv2.visitMethodInsn(INVOKESTATIC, "java/lang/Thread", "sleep", "(J)V", false);
            mv2.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
            mv2.visitLdcInsn("After Sleep");
            mv2.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);

            // 第3段
            mv2.visitLabel(endLabel);
            mv2.visitJumpInsn(GOTO, returnLabel);

            // 第4段
            mv2.visitLabel(handlerLabel);
            mv2.visitVarInsn(ASTORE, 1);
            mv2.visitVarInsn(ALOAD, 1);
            mv2.visitMethodInsn(INVOKEVIRTUAL, "java/lang/InterruptedException", "printStackTrace", "()V", false);

            // 第5段
            mv2.visitLabel(returnLabel);
            mv2.visitInsn(RETURN);

            // 第6段
            // visitTryCatchBlock也可以在这里访问
            // mv2.visitTryCatchBlock(startLabel, endLabel, handlerLabel, "java/lang/InterruptedException");
            mv2.visitMaxs(0, 0);
            mv2.visitEnd();
        }

        cw.visitEnd();

        // (3) 调用toByteArray()方法
        return cw.toByteArray();
    }

    public static void testMethod() throws Exception {
        Class<?> clazz = Class.forName("sample.TestInvokeMethodWithTryCatch");
        Object instance = clazz.getDeclaredConstructor().newInstance();

        Method method = clazz.getDeclaredMethod("test");
        method.invoke(instance);
    }

}
