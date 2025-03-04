package com.yxz.asm.exec.state;

import com.yxz.asm.util.FileUtils;
import org.objectweb.asm.*;

import static org.objectweb.asm.Opcodes.*;

/**
 * 示例三：删除打印语句
 */
public class EditMethodStateful_RemovePrint {

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
        ClassVisitor cv = new MethodRemovePrintVisitor(api, cw);

        //（4）结合ClassReader和ClassVisitor
        int parsingOptions = ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES;
        cr.accept(cv, parsingOptions);

        //（5）生成byte[]
        byte[] bytes2 = cw.toByteArray();

        FileUtils.writeBytes(filepath, bytes2);
    }


    static class MethodRemovePrintVisitor extends ClassVisitor {
        public MethodRemovePrintVisitor(int api, ClassVisitor classVisitor) {
            super(api, classVisitor);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
            MethodVisitor mv = cv.visitMethod(access, name, descriptor, signature, exceptions);
            if (mv != null && !"<init>".equals(name) && !"<clinit>".equals(name)) {
                boolean isAbstractMethod = (access & ACC_ABSTRACT) != 0;
                boolean isNativeMethod = (access & ACC_NATIVE) != 0;
                if (!isAbstractMethod && !isNativeMethod) {
                    mv = new MethodRemovePrintAdaptor(api, mv);
                }
            }
            return mv;
        }

        private class MethodRemovePrintAdaptor extends MethodPatternAdapter {
            private static final int SEEN_GETSTATIC = 1;
            private static final int SEEN_GETSTATIC_LDC = 2;

            private String message;

            public MethodRemovePrintAdaptor(int api, MethodVisitor methodVisitor) {
                super(api, methodVisitor);
            }

            @Override
            public void visitFieldInsn(int opcode, String owner, String name, String descriptor) {
                // 第一，对于感兴趣的状态进行处理
                boolean flag = (opcode == GETSTATIC && owner.equals("java/lang/System") && name.equals("out")
                        && descriptor.equals("Ljava/io/PrintStream;"));
                switch (state) {
                    case SEEN_NOTHING:
                        if (flag) {
                            state = SEEN_GETSTATIC;
                            return;
                        }
                        break;
                    case SEEN_GETSTATIC:
                        if (flag) {
                            mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
                            return;
                        }
                }

                // 第二，对于不感兴趣的状态，交给父类进行处理
                super.visitFieldInsn(opcode, owner, name, descriptor);
            }

            @Override
            public void visitLdcInsn(Object value) {
                // 第一，对于感兴趣的状态进行处理
                switch (state) {
                    case SEEN_GETSTATIC:
                        if (value instanceof String) {
                            state = SEEN_GETSTATIC_LDC;
                            message = (String) value;
                            return;
                        }
                        break;
                }

                // 第二，对于不感兴趣的状态，交给父类进行处理
                super.visitLdcInsn(value);
            }

            @Override
            public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
                // 第一，对于感兴趣的状态进行处理
                switch (state) {
                    case SEEN_GETSTATIC_LDC:
                        if (opcode == INVOKEVIRTUAL && owner.equals("java/io/PrintStream") &&
                                name.equals("println") && descriptor.equals("(Ljava/lang/String;)V")) {
                            state = SEEN_NOTHING;
                            return;
                        }
                        break;
                }

                // 第二，对于不感兴趣的状态，交给父类进行处理
                super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
            }

            @Override
            protected void visitInsn() {
                switch (state) {
                    case SEEN_GETSTATIC:
                        mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
                        break;
                    case SEEN_GETSTATIC_LDC:
                        mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
                        mv.visitLdcInsn(message);
                        break;
                }

                state = SEEN_NOTHING;
            }
        }
    }


}
