package com.yxz.asm.exec.editOld;

import com.yxz.asm.util.FileUtils;
import org.objectweb.asm.*;
import sample.GoodChild;

import java.lang.reflect.Method;

import static org.objectweb.asm.Opcodes.*;

/**
 * 方式一：打印方法入参出参
 */
public class EditMethodPrintParamAndRes_1 {

    public static void main(String[] args) throws Exception {
//        editDump();
        testMethod();
    }

    public static void testMethod() throws Exception {
        Class<?> clazz = Class.forName("sample.HelloWorldForEdit");
        Object instance = clazz.getDeclaredConstructor().newInstance();

        Method method = clazz.getDeclaredMethod("test", String.class, int.class, long.class, Object.class);
        method.invoke(instance, "abc", 6, 1000l, new GoodChild("zhang3", 18));
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
        ClassVisitor cv = new MethodParameterVisitor(api, cw);

        //（4）结合ClassReader和ClassVisitor
        int parsingOptions = ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES;
        cr.accept(cv, parsingOptions);

        //（5）生成byte[]
        byte[] bytes2 = cw.toByteArray();

        FileUtils.writeBytes(filepath, bytes2);
    }


    static class MethodParameterVisitor extends ClassVisitor {

        public MethodParameterVisitor(int api, ClassVisitor classVisitor) {
            super(api, classVisitor);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
            MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);
            if (mv != null && !"<init>".equals(name)) {
                boolean isAbstractMethod = (access & Opcodes.ACC_ABSTRACT) == Opcodes.ACC_ABSTRACT;
                boolean isNativeMethod = (access & Opcodes.ACC_NATIVE) == Opcodes.ACC_NATIVE;
                if (!isAbstractMethod && !isNativeMethod) {
                    mv = new MethodParameterAdapter(api, mv, access, name, descriptor);
                }
            }
            return mv;
        }


        private static class MethodParameterAdapter extends MethodVisitor {
            private final int methodAccess;
            private final String methodName;
            private final String methodDesc;

            public MethodParameterAdapter(int api, MethodVisitor mv, int methodAccess, String methodName, String methodDesc) {
                super(api, mv);
                this.methodAccess = methodAccess;
                this.methodName = methodName;
                this.methodDesc = methodDesc;
            }

            @Override
            public void visitCode() {
                // 首先，处理自己的代码逻辑
                boolean isStatic = ((methodAccess & ACC_STATIC) != 0);
                int slotIndex = isStatic ? 0 : 1;

                printMessage("Method Parameter Enter: " + methodName + methodDesc);

                Type methodType = Type.getMethodType(methodDesc);
                Type[] argumentTypes = methodType.getArgumentTypes();
                for (Type t : argumentTypes) {
                    int sort = t.getSort();
                    int size = t.getSize();
                    String descriptor = t.getDescriptor();

                    int opcode = t.getOpcode(ILOAD);
                    super.visitVarInsn(opcode, slotIndex);

                    if (sort == Type.BOOLEAN) {
                        printBoolean();
                    } else if (sort == Type.CHAR) {
                        printChar();
                    } else if (sort == Type.BYTE || sort == Type.SHORT || sort == Type.INT) {
                        printInt();
                    } else if (sort == Type.FLOAT) {
                        printFloat();
                    } else if (sort == Type.LONG) {
                        printLong();
                    } else if (sort == Type.DOUBLE) {
                        printDouble();
                    } else if (sort == Type.OBJECT && "Ljava/lang/String;".equals(descriptor)) {
                        printString();
                    } else if (sort == Type.OBJECT) {
                        printObject();
                    } else {
                        printMessage("No Support");
                    }
                    slotIndex += size;
                }

                // 其次，调用父类的方法实现
                super.visitCode();
            }

            @Override
            public void visitInsn(int opcode) {
                // 首先，处理自己的代码逻辑
                if ((opcode >= IRETURN && opcode <= RETURN) || opcode == ATHROW) {
                    printMessage("Method Parameter Exit:");
                    if (opcode == IRETURN) {
                        super.visitInsn(DUP);
                        printInt();
                    } else if (opcode == FRETURN) {
                        super.visitInsn(DUP);
                        printFloat();
                    } else if (opcode == LRETURN) {
                        super.visitInsn(DUP2);
                        printLong();
                    } else if (opcode == DRETURN) {
                        super.visitInsn(DUP2);
                        printDouble();
                    } else if (opcode == ARETURN) {
                        super.visitInsn(DUP);
                        printObject();
                    } else if (opcode == RETURN) {
                        printMessage("    return void");
                    } else {
                        printMessage("    abnormal return");
                    }
                }

                // 其次，调用父类的方法实现
                super.visitInsn(opcode);
            }

            private void printBoolean() {
                super.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
                super.visitInsn(SWAP);
                super.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Z)V", false);
            }

            private void printChar() {
                super.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
                super.visitInsn(SWAP);
                super.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(C)V", false);
            }

            private void printInt() {
                super.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
                super.visitInsn(SWAP);
                super.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(I)V", false);
            }

            private void printFloat() {
                super.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
                super.visitInsn(SWAP);
                super.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(F)V", false);
            }

            private void printLong() {
                super.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
                super.visitInsn(DUP_X2);
                super.visitInsn(POP);
                super.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(J)V", false);
            }

            private void printDouble() {
                super.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
                super.visitInsn(DUP_X2);
                super.visitInsn(POP);
                super.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(D)V", false);
            }

            private void printString() {
                super.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
                super.visitInsn(SWAP);
                super.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
            }

            private void printObject() {
                super.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
                super.visitInsn(SWAP);
                super.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/Object;)V", false);
            }

            private void printMessage(String str) {
                super.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
                super.visitLdcInsn(str);
                super.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
            }
        }
    }
}
