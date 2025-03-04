package com.yxz.asm.exec.state;

import com.yxz.asm.util.FileUtils;
import org.objectweb.asm.*;

import static org.objectweb.asm.Opcodes.*;

/**
 * 示例二：字段赋值
 */
public class EditMethodStateful_AssignValue {

    public static void main(String[] args) throws Exception {
        editDump();
    }

    public static void editDump() {
        String relative_path = "sample/HelloWorldForState.class";
        String filepath = FileUtils.getFilePath(relative_path);
        byte[] bytes1 = FileUtils.readBytes(filepath);

        //（1）构建ClassReader
        ClassReader cr = new ClassReader(bytes1);

        //（2）构建ClassWriter
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);

        //（3）串连ClassVisitor
        int api = Opcodes.ASM9;
        ClassVisitor cv = new MethodRemoveGetFieldPutFieldVisitor(api, cw);

        //（4）结合ClassReader和ClassVisitor
        int parsingOptions = ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES;
        cr.accept(cv, parsingOptions);

        //（5）生成byte[]
        byte[] bytes2 = cw.toByteArray();

        FileUtils.writeBytes(filepath, bytes2);
    }


    static class MethodRemoveGetFieldPutFieldVisitor extends ClassVisitor {
        public MethodRemoveGetFieldPutFieldVisitor(int api, ClassVisitor classVisitor) {
            super(api, classVisitor);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
            MethodVisitor mv = cv.visitMethod(access, name, descriptor, signature, exceptions);
            if (mv != null && !"<init>".equals(name) && !"<clinit>".equals(name)) {
                boolean isAbstractMethod = (access & ACC_ABSTRACT) != 0;
                boolean isNativeMethod = (access & ACC_NATIVE) != 0;
                if (!isAbstractMethod && !isNativeMethod) {
                    mv = new MethodRemoveGetFieldPutFieldAdapter(api, mv);
                }
            }
            return mv;
        }

        private class MethodRemoveGetFieldPutFieldAdapter extends MethodPatternAdapter {
            private final static int SEEN_ALOAD_0 = 1;
            private final static int SEEN_ALOAD_0_ALOAD_0 = 2;
            private final static int SEEN_ALOAD_0_ALOAD_0_GETFIELD = 3;

            private String fieldOwner;
            private String fieldName;
            private String fieldDesc;

            public MethodRemoveGetFieldPutFieldAdapter(int api, MethodVisitor methodVisitor) {
                super(api, methodVisitor);
            }

            @Override
            public void visitVarInsn(int opcode, int var) {
                // 第一，对于感兴趣的状态进行处理
                switch (state) {
                    case SEEN_NOTHING:
                        if (opcode == ALOAD && var == 0) {
                            state = SEEN_ALOAD_0;
                            return;
                        }
                        break;
                    case SEEN_ALOAD_0:
                        if (opcode == ALOAD && var == 0) {
                            state = SEEN_ALOAD_0_ALOAD_0;
                            return;
                        }
                        break;
                    case SEEN_ALOAD_0_ALOAD_0:
                        if (opcode == ALOAD && var == 0) {
                            mv.visitVarInsn(opcode, var);
                            return;
                        }
                        break;
                }

                // 第二，对于不感兴趣的状态，交给父类进行处理
                super.visitVarInsn(opcode, var);
            }

            @Override
            public void visitFieldInsn(int opcode, String owner, String name, String descriptor) {
                // 第一，对于感兴趣的状态进行处理
                switch (state) {
                    case SEEN_ALOAD_0_ALOAD_0:
                        if (opcode == GETFIELD) {
                            state = SEEN_ALOAD_0_ALOAD_0_GETFIELD;
                            fieldOwner = owner;
                            fieldName = name;
                            fieldDesc = descriptor;
                            return;
                        }
                        break;
                    case SEEN_ALOAD_0_ALOAD_0_GETFIELD:
                        if (opcode == PUTFIELD && name.equals(fieldName)) {
                            state = SEEN_NOTHING;
                            return;
                        }
                        break;
                }

                // 第二，对于不感兴趣的状态，交给父类进行处理
                super.visitFieldInsn(opcode, owner, name, descriptor);
            }

            @Override
            protected void visitInsn() {
                switch (state) {
                    case SEEN_ALOAD_0:
                        mv.visitVarInsn(ALOAD, 0);
                        break;
                    case SEEN_ALOAD_0_ALOAD_0:
                        mv.visitVarInsn(ALOAD, 0);
                        mv.visitVarInsn(ALOAD, 0);
                        break;
                    case SEEN_ALOAD_0_ALOAD_0_GETFIELD:
                        mv.visitVarInsn(ALOAD, 0);
                        mv.visitVarInsn(ALOAD, 0);
                        mv.visitFieldInsn(GETFIELD, fieldOwner, fieldName, fieldDesc);
                        break;
                }
                state = SEEN_NOTHING;
            }
        }
    }


}
