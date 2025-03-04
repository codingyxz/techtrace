package com.yxz.asm.exec.editOld;

import com.yxz.asm.util.FileUtils;
import org.objectweb.asm.*;

import static org.objectweb.asm.Opcodes.*;

public class EditMethodRemoveInstruction {


    public static void main(String[] args) throws Exception {

        /**
         * 1、生成带有NOP指令的class文件
         */
        String relative_path = "sample/HelloWorldForRemoveIn.class";
        String filepath = FileUtils.getFilePath(relative_path);
        byte[] bytes = dump();
        FileUtils.writeBytes(filepath, bytes);

        /**
         * 2、测试删除NOP指令
         */
//        editDump();

    }


    public static void editDump() {
        String relative_path = "sample/HelloWorldForRemoveIn.class";
        String filepath = FileUtils.getFilePath(relative_path);
        byte[] bytes1 = FileUtils.readBytes(filepath);

        //（1）构建ClassReader
        ClassReader cr = new ClassReader(bytes1);

        //（2）构建ClassWriter
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);

        //（3）串连ClassVisitor
        int api = Opcodes.ASM9;
        ClassVisitor cv = new MethodRemoveNopVisitor(api, cw);

        //（4）结合ClassReader和ClassVisitor
        int parsingOptions = ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES;
        cr.accept(cv, parsingOptions);

        //（5）生成byte[]
        byte[] bytes2 = cw.toByteArray();

        FileUtils.writeBytes(filepath, bytes2);
    }


    /**
     * 自定义 visitor 处理 NOP指令
     */
    static class MethodRemoveNopVisitor extends ClassVisitor {
        public MethodRemoveNopVisitor(int api, ClassVisitor classVisitor) {
            super(api, classVisitor);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
            MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);
            if (mv != null && !"<init>".equals(name) && !"<clinit>".equals(name)) {
                boolean isAbstractMethod = (access & ACC_ABSTRACT) != 0;
                boolean isNativeMethod = (access & ACC_NATIVE) != 0;
                if (!isAbstractMethod && !isNativeMethod) {
                    mv = new MethodRemoveNopAdapter(api, mv);
                }

            }
            return mv;
        }

        private static class MethodRemoveNopAdapter extends MethodVisitor {
            public MethodRemoveNopAdapter(int api, MethodVisitor methodVisitor) {
                super(api, methodVisitor);
            }

            @Override
            public void visitInsn(int opcode) {
                if (opcode != NOP) {
                    super.visitInsn(opcode);
                }
            }
        }
    }


    public static byte[] dump() throws Exception {
        // (1) 创建ClassWriter对象
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);

        // (2) 调用visitXxx()方法
        cw.visit(V1_8, ACC_PUBLIC + ACC_SUPER, "sample/HelloWorldForRemoveIn",
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
            MethodVisitor mv2 = cw.visitMethod(ACC_PUBLIC, "test", "()V", null, null);
            mv2.visitCode();
            mv2.visitInsn(NOP);
            mv2.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
            mv2.visitInsn(NOP);
            mv2.visitLdcInsn("Hello World");
            mv2.visitInsn(NOP);
            mv2.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
            mv2.visitInsn(NOP);
            mv2.visitInsn(RETURN);
            mv2.visitMaxs(2, 1);
            mv2.visitEnd();
        }
        cw.visitEnd();

        // (3) 调用toByteArray()方法
        return cw.toByteArray();
    }

}
