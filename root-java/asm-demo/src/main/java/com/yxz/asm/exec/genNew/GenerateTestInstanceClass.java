package com.yxz.asm.exec.genNew;


import com.yxz.asm.util.FileUtils;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.*;

/**
 * 预期：
 * public class TestInstanceClass {
 * *    public void test() {
 * *        GoodChild child = new GoodChild("Lucy", 8);
 * *    }
 * }
 */
public class GenerateTestInstanceClass {
    public static void main(String[] args) throws Exception {
        String relative_path = "sample/TestInstanceClass.class";
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
        cw.visit(V1_8, ACC_PUBLIC + ACC_SUPER, "sample/TestInstanceClass", null, "java/lang/Object", null);

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
            MethodVisitor mv2 = cw.visitMethod(ACC_PUBLIC, "test", "()V", null, null);
            mv2.visitCode();
            mv2.visitTypeInsn(NEW, "sample/GoodChild");
            mv2.visitInsn(DUP);
            mv2.visitLdcInsn("Lucy");
            mv2.visitIntInsn(BIPUSH, 8);
            mv2.visitMethodInsn(INVOKESPECIAL, "sample/GoodChild", "<init>", "(Ljava/lang/String;I)V", false);
            mv2.visitVarInsn(ASTORE, 1);
            mv2.visitInsn(RETURN);
            mv2.visitMaxs(4, 2);
            mv2.visitEnd();
        }

        cw.visitEnd();

        // (3) 调用toByteArray()方法
        return cw.toByteArray();
    }

}
