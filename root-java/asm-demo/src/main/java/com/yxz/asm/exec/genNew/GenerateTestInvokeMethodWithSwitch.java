package com.yxz.asm.exec.genNew;

import com.yxz.asm.util.FileUtils;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

import java.lang.reflect.Method;

import static org.objectweb.asm.Opcodes.*;

/**
 * 期望
 * public class TestInvokeMethodWithSwitch {
 * *    public void test(int val) {
 * *        switch (val) {
 * *            case 1:
 * *                System.out.println("val = 1");
 * *                break;
 * *            case 2:
 * *                System.out.println("val = 2");
 * *                break;
 * *            case 3:
 * *                System.out.println("val = 3");
 * *                break;
 * *            case 4:
 * *                System.out.println("val = 4");
 * *                break;
 * *            default:
 * *                System.out.println("val is unknown");
 * *        }
 * *    }
 * }
 */
public class GenerateTestInvokeMethodWithSwitch {

    public static void main(String[] args) throws Exception {
        String relative_path = "sample/TestInvokeMethodWithSwitch.class";
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
        cw.visit(V1_8, ACC_PUBLIC + ACC_SUPER, "sample/TestInvokeMethodWithSwitch",
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
            MethodVisitor mv2 = cw.visitMethod(ACC_PUBLIC, "test", "(I)V", null, null);
            Label caseLabel1 = new Label();
            Label caseLabel2 = new Label();
            Label caseLabel3 = new Label();
            Label caseLabel4 = new Label();
            Label defaultLabel = new Label();
            Label returnLabel = new Label();

            // 第1段
            mv2.visitCode();
            mv2.visitVarInsn(ILOAD, 1);
            mv2.visitTableSwitchInsn(1, 4, defaultLabel, new Label[]{caseLabel1, caseLabel2, caseLabel3, caseLabel4});

            // 第2段
            mv2.visitLabel(caseLabel1);
            mv2.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
            mv2.visitLdcInsn("val = 1");
            mv2.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
            mv2.visitJumpInsn(GOTO, returnLabel);

            // 第3段
            mv2.visitLabel(caseLabel2);
            mv2.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
            mv2.visitLdcInsn("val = 2");
            mv2.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
            mv2.visitJumpInsn(GOTO, returnLabel);

            // 第4段
            mv2.visitLabel(caseLabel3);
            mv2.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
            mv2.visitLdcInsn("val = 3");
            mv2.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
            mv2.visitJumpInsn(GOTO, returnLabel);

            // 第5段
            mv2.visitLabel(caseLabel4);
            mv2.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
            mv2.visitLdcInsn("val = 4");
            mv2.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
            mv2.visitJumpInsn(GOTO, returnLabel);

            // 第6段
            mv2.visitLabel(defaultLabel);
            mv2.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
            mv2.visitLdcInsn("val is unknown");
            mv2.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);

            // 第7段
            mv2.visitLabel(returnLabel);
            mv2.visitInsn(RETURN);
            mv2.visitMaxs(0, 0);
            mv2.visitEnd();
        }

        cw.visitEnd();

        // (3) 调用toByteArray()方法
        return cw.toByteArray();
    }

    public static void testMethod() throws Exception {
        Class<?> clazz = Class.forName("sample.TestInvokeMethodWithSwitch");
        Object instance = clazz.getDeclaredConstructor().newInstance();

        Method method = clazz.getDeclaredMethod("test", int.class);
        for (int i = 1; i < 6; i++) {
            method.invoke(instance, i);
        }
    }

}
