package com.yxz.asm.exec.editOld;

import com.yxz.asm.util.FileUtils;
import org.objectweb.asm.*;

import java.lang.reflect.Method;

import static org.objectweb.asm.Opcodes.*;


/**
 * 替换指令
 * 预期：
 * * 第一个，就是将静态方法Math.max()方法替换掉。
 * * 第二个，就是将非静态方法PrintStream.println()方法替换掉。
 */
public class EditClassReplaceInstruction {

    public static void main(String[] args) throws Exception {
        editDump();
        testMethod();
    }

    public static void testMethod() throws Exception {
        Class<?> clazz = Class.forName("sample.HelloWorldForReplace");
        Object instance = clazz.getDeclaredConstructor().newInstance();

        Method method = clazz.getDeclaredMethod("test", int.class, int.class);
        method.invoke(instance, 10, 20);
    }

    public static void editDump() {
        String relative_path = "sample/HelloWorldForReplace.class";
        String filepath = FileUtils.getFilePath(relative_path);
        byte[] bytes1 = FileUtils.readBytes(filepath);

        //（1）构建ClassReader
        ClassReader cr = new ClassReader(bytes1);

        //（2）构建ClassWriter
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);

        //（3）串连ClassVisitor
        int api = Opcodes.ASM9;
        ClassVisitor cv1 = new MethodReplaceInvokeVisitor(api, cw,
                "java/lang/Math", "max", "(II)I",
                Opcodes.INVOKESTATIC, "java/lang/Math",
                "min", "(II)I");

        ClassVisitor cv2 = new MethodReplaceInvokeVisitor(api, cv1,
                "java/io/PrintStream", "println", "(I)V",
                Opcodes.INVOKESTATIC, "com/yxz/asm/util/ParameterUtils", "output", "(Ljava/io/PrintStream;I)V");
        ClassVisitor cv = cv2;
        //（4）结合ClassReader和ClassVisitor
        int parsingOptions = ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES;
        cr.accept(cv, parsingOptions);

        //（5）生成byte[]
        byte[] bytes2 = cw.toByteArray();

        FileUtils.writeBytes(filepath, bytes2);
    }


    static class MethodReplaceInvokeVisitor extends ClassVisitor {
        private final String oldOwner;
        private final String oldMethodName;
        private final String oldMethodDesc;

        private final int newOpcode;
        private final String newOwner;
        private final String newMethodName;
        private final String newMethodDesc;

        public MethodReplaceInvokeVisitor(int api, ClassVisitor classVisitor,
                                          String oldOwner, String oldMethodName, String oldMethodDesc,
                                          int newOpcode, String newOwner, String newMethodName, String newMethodDesc) {
            super(api, classVisitor);
            this.oldOwner = oldOwner;
            this.oldMethodName = oldMethodName;
            this.oldMethodDesc = oldMethodDesc;

            this.newOpcode = newOpcode;
            this.newOwner = newOwner;
            this.newMethodName = newMethodName;
            this.newMethodDesc = newMethodDesc;
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
            MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);
            if (mv != null && !"<init>".equals(name) && !"<clinit>".equals(name)) {
                boolean isAbstractMethod = (access & ACC_ABSTRACT) != 0;
                boolean isNativeMethod = (access & ACC_NATIVE) != 0;
                if (!isAbstractMethod && !isNativeMethod) {
                    mv = new MethodReplaceInvokeAdapter(api, mv);
                }
            }
            return mv;
        }

        private class MethodReplaceInvokeAdapter extends MethodVisitor {
            public MethodReplaceInvokeAdapter(int api, MethodVisitor methodVisitor) {
                super(api, methodVisitor);
            }

            @Override
            public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
                if (oldOwner.equals(owner) && oldMethodName.equals(name) && oldMethodDesc.equals(descriptor)) {
                    // 注意，最后一个参数是false，会不会太武断呢？
                    super.visitMethodInsn(newOpcode, newOwner, newMethodName, newMethodDesc, false);
                } else {
                    super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
                }
            }
        }
    }

}
