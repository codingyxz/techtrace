package com.yxz.asm.exec.editOld;

import com.yxz.asm.util.FileUtils;
import org.objectweb.asm.*;

import static org.objectweb.asm.Opcodes.*;

public class EditClassMethod {

    public static void main(String[] args) {
        String relative_path = "sample/HelloWorldForEdit.class";
        String filepath = FileUtils.getFilePath(relative_path);
        byte[] bytes1 = FileUtils.readBytes(filepath);

        //（1）构建ClassReader
        ClassReader cr = new ClassReader(bytes1);

        //（2）构建ClassWriter
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);

        //（3）串连ClassVisitor
        int api = Opcodes.ASM9;
        ClassVisitor cv1 = new ClassRemoveMethodVisitor(api, cw, "add", "(II)I");
        ClassVisitor cv2 = new ClassAddMethodVisitor(api, cv1, Opcodes.ACC_PUBLIC, "mul", "(II)I", null, null);


        //（4）结合ClassReader和ClassVisitor
        int parsingOptions = ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES;
        cr.accept(cv2, parsingOptions);

        //（5）生成byte[]
        byte[] bytes2 = cw.toByteArray();

        FileUtils.writeBytes(filepath, bytes2);
    }


    static class ClassRemoveMethodVisitor extends ClassVisitor {
        private final String methodName;
        private final String methodDesc;

        public ClassRemoveMethodVisitor(int api, ClassVisitor cv, String methodName, String methodDesc) {
            super(api, cv);
            this.methodName = methodName;
            this.methodDesc = methodDesc;
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
            if (name.equals(methodName) && descriptor.equals(methodDesc)) {
                return null;
            }
            return super.visitMethod(access, name, descriptor, signature, exceptions);
        }
    }


    static class ClassAddMethodVisitor extends ClassVisitor {
        private final int methodAccess;
        private final String methodName;
        private final String methodDesc;
        private final String methodSignature;
        private final String[] methodExceptions;
        private boolean isMethodPresent;

        public ClassAddMethodVisitor(int api, ClassVisitor cv, int methodAccess, String methodName, String methodDesc,
                                     String signature, String[] exceptions) {
            super(api, cv);
            this.methodAccess = methodAccess;
            this.methodName = methodName;
            this.methodDesc = methodDesc;
            this.methodSignature = signature;
            this.methodExceptions = exceptions;
            this.isMethodPresent = false;
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
            if (name.equals(methodName) && descriptor.equals(methodDesc)) {
                isMethodPresent = true;
            }
            return super.visitMethod(access, name, descriptor, signature, exceptions);
        }

        @Override
        public void visitEnd() {
            if (!isMethodPresent) {
                MethodVisitor mv = super.visitMethod(methodAccess, methodName, methodDesc, methodSignature, methodExceptions);
                if (mv != null) {
                    // create method body
                    mv.visitCode();
                    mv.visitVarInsn(ILOAD, 1);
                    mv.visitVarInsn(ILOAD, 2);
                    mv.visitInsn(IMUL);
                    mv.visitInsn(IRETURN);
                    mv.visitMaxs(2, 3);
                    mv.visitEnd();
                }
            }
            super.visitEnd();
        }
    }

}
