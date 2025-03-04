package com.yxz.asm.exec.editOld;

import com.yxz.asm.util.FileUtils;
import org.objectweb.asm.*;
import sample.HelloWorldForTimer;

import java.lang.reflect.Field;
import java.util.Random;

import static org.objectweb.asm.Opcodes.*;

/**
 * 计算每个方法的运行时间
 */
public class EditMethodTimer_1 {

    public static void main(String[] args) throws Exception {
        editDump();
        testMethod();
    }

    public static void testMethod() throws Exception {
        // 第一部分，先让“子弹飞一会儿”，让程序运行一段时间
        Class<?> clazz = Class.forName("sample.HelloWorldForTimer");
        HelloWorldForTimer instance = (HelloWorldForTimer) clazz.getDeclaredConstructor().newInstance();

        Random rand = new Random(System.currentTimeMillis());
        for (int i = 0; i < 10; i++) {
            boolean flag = rand.nextBoolean();
            int a = rand.nextInt(50);
            int b = rand.nextInt(50);
            if (flag) {
                int c = instance.add(a, b);
                String line = String.format("%d + %d = %d", a, b, c);
                System.out.println(line);
            } else {
                int c = instance.sub(a, b);
                String line = String.format("%d - %d = %d", a, b, c);
                System.out.println(line);
            }
        }

        // 第二部分，来查看方法运行的时间
        Field[] declaredFields = clazz.getDeclaredFields();
        for (Field f : declaredFields) {
            String fieldName = f.getName();
            f.setAccessible(true);
            if (fieldName.startsWith("timer")) {
                Object FieldValue = f.get(null);
                System.out.println(fieldName + " = " + FieldValue);
            }
        }
    }

    public static void editDump() {
        String relative_path = "sample/HelloWorldForTimer.class";
        String filepath = FileUtils.getFilePath(relative_path);
        byte[] bytes1 = FileUtils.readBytes(filepath);

        //（1）构建ClassReader
        ClassReader cr = new ClassReader(bytes1);

        //（2）构建ClassWriter
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);

        //（3）串连ClassVisitor
        int api = Opcodes.ASM9;
        ClassVisitor cv = new MethodTimerVisitor(api, cw);

        //（4）结合ClassReader和ClassVisitor
        int parsingOptions = ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES;
        cr.accept(cv, parsingOptions);

        //（5）生成byte[]
        byte[] bytes2 = cw.toByteArray();

        FileUtils.writeBytes(filepath, bytes2);
    }

    static class MethodTimerVisitor extends ClassVisitor {
        private String owner;
        private boolean isInterface;

        public MethodTimerVisitor(int api, ClassVisitor classVisitor) {
            super(api, classVisitor);
        }

        @Override
        public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
            super.visit(version, access, name, signature, superName, interfaces);
            owner = name;
            isInterface = (access & ACC_INTERFACE) != 0;
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
            MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);
            if (!isInterface && mv != null && !"<init>".equals(name) && !"<clinit>".equals(name)) {
                boolean isAbstractMethod = (access & ACC_ABSTRACT) != 0;
                boolean isNativeMethod = (access & ACC_NATIVE) != 0;
                if (!isAbstractMethod && !isNativeMethod) {
                    // 每遇到一个合适的方法，就添加一个相应的字段
                    FieldVisitor fv = super.visitField(ACC_PUBLIC | ACC_STATIC, getFieldName(name), "J", null, null);
                    if (fv != null) {
                        fv.visitEnd();
                    }

                    mv = new MethodTimerAdapter(api, mv, owner, name);
                }

            }
            return mv;
        }

        private String getFieldName(String methodName) {
            return "timer_" + methodName;
        }

        private class MethodTimerAdapter extends MethodVisitor {
            private final String owner;
            private final String methodName;

            public MethodTimerAdapter(int api, MethodVisitor mv, String owner, String methodName) {
                super(api, mv);
                this.owner = owner;
                this.methodName = methodName;
            }

            @Override
            public void visitCode() {
                // 首先，处理自己的代码逻辑
                super.visitFieldInsn(GETSTATIC, owner, getFieldName(methodName), "J"); // 注意，字段名字要对应
                super.visitMethodInsn(INVOKESTATIC, "java/lang/System", "currentTimeMillis", "()J", false);
                super.visitInsn(LSUB);
                super.visitFieldInsn(PUTSTATIC, owner, getFieldName(methodName), "J"); // 注意，字段名字要对应

                // 其次，调用父类的方法实现
                super.visitCode();
            }

            @Override
            public void visitInsn(int opcode) {
                // 首先，处理自己的代码逻辑
                if ((opcode >= IRETURN && opcode <= RETURN) || opcode == ATHROW) {
                    super.visitFieldInsn(GETSTATIC, owner, getFieldName(methodName), "J"); // 注意，字段名字要对应
                    super.visitMethodInsn(INVOKESTATIC, "java/lang/System", "currentTimeMillis", "()J", false);
                    super.visitInsn(LADD);
                    super.visitFieldInsn(PUTSTATIC, owner, getFieldName(methodName), "J"); // 注意，字段名字要对应
                }

                // 其次，调用父类的方法实现
                super.visitInsn(opcode);
            }
        }
    }

}
