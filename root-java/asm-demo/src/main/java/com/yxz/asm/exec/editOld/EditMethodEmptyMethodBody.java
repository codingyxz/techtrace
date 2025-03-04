package com.yxz.asm.exec.editOld;

import com.yxz.asm.util.FileUtils;
import org.objectweb.asm.*;

import java.lang.reflect.Method;

import static org.objectweb.asm.Opcodes.*;

/**
 * 清空方法体
 * 步骤：
 * * 第一步，对于它“前面”的MethodVisitor，它返回null值，就相当于原来的方法丢失了；
 * * 第二步，对于它“后面”的MethodVisitor，它添加同名、同类型的方法，然后生成新的方法体，
 * *    这就相当于又添加了一个新的方法。
 */
public class EditMethodEmptyMethodBody {

    public static void main(String[] args) throws Exception {
        editDump();
        testMethod();
    }

    public static void testMethod() throws Exception {
        Class<?> clazz = Class.forName("sample.HelloWorldForEmptyMethod");
        Object instance = clazz.getDeclaredConstructor().newInstance();

        Method method = clazz.getDeclaredMethod("verify", String.class, String.class);
        method.invoke(instance, "li4", "123");
    }

    public static void editDump() {
        String relative_path = "sample/HelloWorldForEmptyMethod.class";
        String filepath = FileUtils.getFilePath(relative_path);
        byte[] bytes1 = FileUtils.readBytes(filepath);

        //（1）构建ClassReader
        ClassReader cr = new ClassReader(bytes1);

        //（2）构建ClassWriter
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);

        //（3）串连ClassVisitor
        int api = Opcodes.ASM9;
        ClassVisitor cv = new MethodEmptyBodyVisitor(api, cw, "verify", "(Ljava/lang/String;Ljava/lang/String;)V");

        //（4）结合ClassReader和ClassVisitor
        int parsingOptions = ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES;
        cr.accept(cv, parsingOptions);

        //（5）生成byte[]
        byte[] bytes2 = cw.toByteArray();

        FileUtils.writeBytes(filepath, bytes2);
    }


    static class MethodEmptyBodyVisitor extends ClassVisitor {
        private String owner;
        private final String methodName;
        private final String methodDesc;

        public MethodEmptyBodyVisitor(int api, ClassVisitor classVisitor, String methodName, String methodDesc) {
            super(api, classVisitor);
            this.methodName = methodName;
            this.methodDesc = methodDesc;
        }

        @Override
        public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
            super.visit(version, access, name, signature, superName, interfaces);
            this.owner = name;
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
            MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);
            if (mv != null && methodName.equals(name) && methodDesc.equals(descriptor)) {
                boolean isAbstractMethod = (access & ACC_ABSTRACT) != 0;
                boolean isNativeMethod = (access & ACC_NATIVE) != 0;
                if (!isAbstractMethod && !isNativeMethod) {
                    generateNewBody(mv, owner, access, name, descriptor);
                    return null;
                }
            }
            return mv;
        }

        protected void generateNewBody(MethodVisitor mv, String owner, int methodAccess, String methodName, String methodDesc) {
            // (1) method argument types and return type
            Type t = Type.getType(methodDesc);
            Type[] argumentTypes = t.getArgumentTypes();
            Type returnType = t.getReturnType();


            // (2) compute the size of local variable and operand stack
            boolean isStaticMethod = ((methodAccess & Opcodes.ACC_STATIC) != 0);
            int localSize = isStaticMethod ? 0 : 1;
            for (Type argType : argumentTypes) {
                localSize += argType.getSize();
            }
            int stackSize = returnType.getSize();


            // (3) method body
            mv.visitCode();
            if (returnType.getSort() == Type.VOID) {
                mv.visitInsn(RETURN);
            } else if (returnType.getSort() >= Type.BOOLEAN && returnType.getSort() <= Type.INT) {
                mv.visitInsn(ICONST_1);
                mv.visitInsn(IRETURN);
            } else if (returnType.getSort() == Type.LONG) {
                mv.visitInsn(LCONST_0);
                mv.visitInsn(LRETURN);
            } else if (returnType.getSort() == Type.FLOAT) {
                mv.visitInsn(FCONST_0);
                mv.visitInsn(FRETURN);
            } else if (returnType.getSort() == Type.DOUBLE) {
                mv.visitInsn(DCONST_0);
                mv.visitInsn(DRETURN);
            } else {
                mv.visitInsn(ACONST_NULL);
                mv.visitInsn(ARETURN);
            }
            mv.visitMaxs(stackSize, localSize);
            mv.visitEnd();
        }
    }
}
