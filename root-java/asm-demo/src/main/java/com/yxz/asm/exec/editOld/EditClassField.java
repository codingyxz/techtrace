package com.yxz.asm.exec.editOld;

import com.yxz.asm.util.FileUtils;
import org.objectweb.asm.*;

/**
 * 修改类的字段
 */
public class EditClassField {

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
        // 删除属性
        ClassVisitor cv1 = new ClassRemoveFieldVisitor(api, cw, "strValue", "Ljava/lang/String;");
        // 添加属性
        ClassVisitor cv2 = new ClassAddFieldVisitor(api, cv1, Opcodes.ACC_PUBLIC, "objValue", "Ljava/lang/Object;");

        //（4）结合ClassReader和ClassVisitor
        int parsingOptions = ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES;
        cr.accept(cv2, parsingOptions);

        //（5）生成byte[]
        byte[] bytes2 = cw.toByteArray();

        FileUtils.writeBytes(filepath, bytes2);
    }


    static class ClassRemoveFieldVisitor extends ClassVisitor {
        private final String fieldName;
        private final String fieldDesc;

        public ClassRemoveFieldVisitor(int api, ClassVisitor cv, String fieldName, String fieldDesc) {
            super(api, cv);
            this.fieldName = fieldName;
            this.fieldDesc = fieldDesc;
        }

        @Override
        public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {
            if (name.equals(fieldName) && descriptor.equals(fieldDesc)) {
                return null;
            }
            return super.visitField(access, name, descriptor, signature, value);
        }
    }

    static class ClassAddFieldVisitor extends ClassVisitor {
        private final int fieldAccess;
        private final String fieldName;
        private final String fieldDesc;
        private boolean isFieldPresent;

        public ClassAddFieldVisitor(int api, ClassVisitor classVisitor, int fieldAccess, String fieldName, String fieldDesc) {
            super(api, classVisitor);
            this.fieldAccess = fieldAccess;
            this.fieldName = fieldName;
            this.fieldDesc = fieldDesc;
        }

        @Override
        public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {
            if (name.equals(fieldName)) {
                isFieldPresent = true;
            }
            return super.visitField(access, name, descriptor, signature, value);
        }

        @Override
        public void visitEnd() {
            if (!isFieldPresent) {
                FieldVisitor fv = cv.visitField(fieldAccess, fieldName, fieldDesc, null, null);
                if (fv != null) {
                    fv.visitEnd();
                }
            }
            super.visitEnd();
        }
    }

}
