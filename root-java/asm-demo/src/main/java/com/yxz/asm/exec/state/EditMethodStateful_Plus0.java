package com.yxz.asm.exec.state;

import com.yxz.asm.util.FileUtils;
import org.objectweb.asm.*;

import java.lang.reflect.Method;

import static org.objectweb.asm.Opcodes.*;

/**
 * 示例一：加零
 */
public class EditMethodStateful_Plus0 {

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
        ClassVisitor cv = new MethodRemoveAddZeroVisitor(api, cw);

        //（4）结合ClassReader和ClassVisitor
        int parsingOptions = ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES;
        cr.accept(cv, parsingOptions);

        //（5）生成byte[]
        byte[] bytes2 = cw.toByteArray();

        FileUtils.writeBytes(filepath, bytes2);
    }


    static class MethodRemoveAddZeroVisitor extends ClassVisitor {
        public MethodRemoveAddZeroVisitor(int api, ClassVisitor classVisitor) {
            super(api, classVisitor);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
            MethodVisitor mv = cv.visitMethod(access, name, descriptor, signature, exceptions);
            if (mv != null && !"<init>".equals(name) && !"<clinit>".equals(name)) {
                boolean isAbstractMethod = (access & ACC_ABSTRACT) != 0;
                boolean isNativeMethod = (access & ACC_NATIVE) != 0;
                if (!isAbstractMethod && !isNativeMethod) {
                    mv = new MethodRemoveAddZeroAdapter(api, mv);
                }
            }
            return mv;
        }

        private class MethodRemoveAddZeroAdapter extends MethodPatternAdapter {
            private static final int SEEN_ICONST_0 = 1;

            public MethodRemoveAddZeroAdapter(int api, MethodVisitor methodVisitor) {
                super(api, methodVisitor);
            }

            @Override
            public void visitInsn(int opcode) {
                // 第一，对于感兴趣的状态进行处理
                switch (state) {
                    case SEEN_NOTHING:
                        if (opcode == ICONST_0) {
                            state = SEEN_ICONST_0;
                            return;
                        }
                        break;
                    case SEEN_ICONST_0:
                        if (opcode == IADD) {
                            state = SEEN_NOTHING;
                            return;
                        } else if (opcode == ICONST_0) {
                            mv.visitInsn(ICONST_0);
                            return;
                        }
                        break;
                }

                // 第二，对于不感兴趣的状态，交给父类进行处理
                super.visitInsn(opcode);
            }

            @Override
            protected void visitInsn() {
                if (state == SEEN_ICONST_0) {
                    mv.visitInsn(ICONST_0);
                }
                state = SEEN_NOTHING;
            }
        }
    }


}
