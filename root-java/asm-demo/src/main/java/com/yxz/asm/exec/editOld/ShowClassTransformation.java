package com.yxz.asm.exec.editOld;

import com.yxz.asm.util.FileUtils;
import org.objectweb.asm.*;
import org.objectweb.asm.util.Printer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ShowClassTransformation {


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
        ClassVisitor cv = new InfoClassVisitor(api, cw);

        //（4）结合Cla***eader和ClassVisitor
        int parsingOptions = ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES;
        cr.accept(cv, parsingOptions);

        //（5）生成byte[]
        byte[] bytes2 = cw.toByteArray();

        FileUtils.writeBytes(filepath, bytes2);
    }


    /**
     * class
     */
    static class InfoClassVisitor extends ClassVisitor {
        public InfoClassVisitor(int api, ClassVisitor classVisitor) {
            super(api, classVisitor);
        }

        @Override
        public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
            String line = String.format("ClassVisitor.visit(%d, %s, %s, %s, %s, %s);",
                    version, getAccess(access), name, signature, superName, Arrays.toString(interfaces));
            System.out.println(line);
            super.visit(version, access, name, signature, superName, interfaces);
        }

        @Override
        public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {
            String line = String.format("ClassVisitor.visitField(%s, %s, %s, %s, %s);",
                    getAccess(access), name, descriptor, signature, value);
            System.out.println(line);

            FieldVisitor fv = super.visitField(access, name, descriptor, signature, value);
            return new InfoFieldVisitor(api, fv);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
            String line = String.format("ClassVisitor.visitMethod(%s, %s, %s, %s, %s);",
                    getAccess(access), name, descriptor, signature, exceptions);
            System.out.println(line);

            MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);
            return new InfoMethodVisitor(api, mv);
        }

        @Override
        public void visitEnd() {
            String line = String.format("ClassVisitor.visitEnd();");
            System.out.println(line);
            super.visitEnd();
        }

        private String getAccess(int access) {
            List<String> list = new ArrayList<>();
            if ((access & Opcodes.ACC_PUBLIC) != 0) {
                list.add("ACC_PUBLIC");
            } else if ((access & Opcodes.ACC_PROTECTED) != 0) {
                list.add("ACC_PROTECTED");
            } else if ((access & Opcodes.ACC_PRIVATE) != 0) {
                list.add("ACC_PRIVATE");
            }
            return list.toString();
        }
    }


    /**
     * field
     */
    static class InfoFieldVisitor extends FieldVisitor {
        public InfoFieldVisitor(int api, FieldVisitor fieldVisitor) {
            super(api, fieldVisitor);
        }

        @Override
        public void visitEnd() {
            String line = String.format("    FieldVisitor.visitEnd();");
            System.out.println(line);
            super.visitEnd();
        }
    }


    /**
     * method
     */
    static class InfoMethodVisitor extends MethodVisitor {
        public InfoMethodVisitor(int api, MethodVisitor methodVisitor) {
            super(api, methodVisitor);
        }

        @Override
        public void visitCode() {
            String line = String.format("    MethodVisitor.visitCode();");
            System.out.println(line);
            super.visitCode();
        }

        @Override
        public void visitInsn(int opcode) {
            String line = String.format("    MethodVisitor.visitInsn(%s);", Printer.OPCODES[opcode]);
            System.out.println(line);
            super.visitInsn(opcode);
        }

        @Override
        public void visitIntInsn(int opcode, int operand) {
            String line = String.format("    MethodVisitor.visitIntInsn(%s, %s);", Printer.OPCODES[opcode], operand);
            System.out.println(line);
            super.visitIntInsn(opcode, operand);
        }

        @Override
        public void visitVarInsn(int opcode, int var) {
            String line = String.format("    MethodVisitor.visitVarInsn(%s, %s);", Printer.OPCODES[opcode], var);
            System.out.println(line);
            super.visitVarInsn(opcode, var);
        }

        @Override
        public void visitTypeInsn(int opcode, String type) {
            String line = String.format("    MethodVisitor.visitTypeInsn(%s, %s);", Printer.OPCODES[opcode], type);
            System.out.println(line);
            super.visitTypeInsn(opcode, type);
        }

        @Override
        public void visitFieldInsn(int opcode, String owner, String name, String descriptor) {
            String line = String.format("    MethodVisitor.visitFieldInsn(%s, %s, %s, %s);",
                    Printer.OPCODES[opcode], owner, name, descriptor);
            System.out.println(line);
            super.visitFieldInsn(opcode, owner, name, descriptor);
        }

        @Override
        public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
            String line = String.format("    MethodVisitor.visitMethodInsn(%s, %s, %s, %s, %s);",
                    Printer.OPCODES[opcode], owner, name, descriptor, isInterface);
            System.out.println(line);
            super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
        }

        @Override
        public void visitJumpInsn(int opcode, Label label) {
            String line = String.format("    MethodVisitor.visitJumpInsn(%s, %s);", Printer.OPCODES[opcode], label);
            System.out.println(line);
            super.visitJumpInsn(opcode, label);
        }

        @Override
        public void visitLabel(Label label) {
            String line = String.format("    MethodVisitor.visitLabel(%s);", label);
            System.out.println(line);
            super.visitLabel(label);
        }

        @Override
        public void visitLdcInsn(Object value) {
            String line = String.format("    MethodVisitor.visitLdcInsn(%s);", value);
            System.out.println(line);
            super.visitLdcInsn(value);
        }

        @Override
        public void visitIincInsn(int var, int increment) {
            String line = String.format("    MethodVisitor.visitIincInsn(%s, %s);", var, increment);
            System.out.println(line);
            super.visitIincInsn(var, increment);
        }

        @Override
        public void visitMaxs(int maxStack, int maxLocals) {
            String line = String.format("    MethodVisitor.visitMaxs(%s, %s);", maxStack, maxLocals);
            System.out.println(line);
            super.visitMaxs(maxStack, maxLocals);
        }

        @Override
        public void visitEnd() {
            String line = String.format("    MethodVisitor.visitEnd();");
            System.out.println(line);
            super.visitEnd();
        }
    }

}
