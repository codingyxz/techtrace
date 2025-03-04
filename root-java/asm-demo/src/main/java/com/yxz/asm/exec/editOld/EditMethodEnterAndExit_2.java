package com.yxz.asm.exec.editOld;

import com.yxz.asm.util.FileUtils;
import org.objectweb.asm.*;

import java.lang.reflect.Method;

/**
 * 方式二：将所有代码放到一个 ClassVisitor
 * public class HelloWorld {
 * *    public void test() {
 * *        System.out.println("Method Enter...");
 * *        System.out.println("this is a test method.");
 * *        System.out.println("Method Exit...");
 * *    }
 * }
 */
public class EditMethodEnterAndExit_2 {

    public static void main(String[] args) throws Exception {
        editDump();
        testMethod();
    }

    public static void testMethod() throws Exception {
        Class<?> clazz = Class.forName("sample.HelloWorldForEdit");
        Object instance = clazz.getDeclaredConstructor().newInstance();

        Method method = clazz.getDeclaredMethod("test");
        method.invoke(instance);
    }

    public static void editDump() {
        String relative_path = "sample/HelloWorldForEdit.class";
        String filepath = FileUtils.getFilePath(relative_path);
        byte[] bytes1 = FileUtils.readBytes(filepath);

        //（1）构建ClassReader
        ClassReader cr = new ClassReader(bytes1);

        //（2）构建ClassWriter
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);

        //（3）串连ClassVisitor
        int api = Opcodes.ASM9;
        ClassVisitor cv = new MethodAroundVisitor(api, cw);

        //（4）结合ClassReader和ClassVisitor
        int parsingOptions = ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES;
        cr.accept(cv, parsingOptions);

        //（5）生成byte[]
        byte[] bytes2 = cw.toByteArray();

        FileUtils.writeBytes(filepath, bytes2);
    }


    /**
     * 方法执行前后插入 输出语句
     */
    static class MethodAroundVisitor extends ClassVisitor {
        public MethodAroundVisitor(int api, ClassVisitor classVisitor) {
            super(api, classVisitor);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
            MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);
            if (mv != null && !"<init>".equals(name)) {
                mv = new MethodAroundAdapter(api, mv);
            }
            return mv;
        }

        private static class MethodAroundAdapter extends MethodVisitor {
            public MethodAroundAdapter(int api, MethodVisitor methodVisitor) {
                super(api, methodVisitor);
            }

            @Override
            public void visitCode() {
                // 首先，处理自己的代码逻辑
                super.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
                super.visitLdcInsn("Method AROUND Enter...");
                super.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);

                // 其次，调用父类的方法实现
                super.visitCode();
            }

            @Override
            public void visitInsn(int opcode) {
                // 首先，处理自己的代码逻辑
                if (opcode == Opcodes.ATHROW || (opcode >= Opcodes.IRETURN && opcode <= Opcodes.RETURN)) {
                    super.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
                    super.visitLdcInsn("Method AROUND Exit...");
                    super.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
                }
                // 其次，调用父类的方法实现
                super.visitInsn(opcode);
            }
        }
    }

}
