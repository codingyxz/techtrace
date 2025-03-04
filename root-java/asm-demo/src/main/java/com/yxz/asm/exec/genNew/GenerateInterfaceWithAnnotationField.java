package com.yxz.asm.exec.genNew;


import com.yxz.asm.util.FileUtils;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;

import static org.objectweb.asm.Opcodes.*;
import static org.objectweb.asm.Opcodes.ACC_STATIC;

/**
 * 预期：
 * public interface InterfaceWithAnnotationField {
 * *   @MyTag(name = "tomcat", age = 10)
 * *   int intValue = 100;
 * }
 * <p>
 * 其中，MyTag定义如下：
 * public @interface MyTag {
 * *    String name();
 * *    int age();
 * }
 */
public class GenerateInterfaceWithAnnotationField {

    public static void main(String[] args) throws Exception {
        String relative_path = "sample/InterfaceWithAnnotationField.class";
        String filepath = FileUtils.getFilePath(relative_path);

        // (1) 生成byte[]内容
        byte[] bytes = dump();

        // (2) 保存byte[]到文件
        FileUtils.writeBytes(filepath, bytes);
    }

    public static byte[] dump() throws Exception {
        // (1) 创建ClassWriter对象
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);

        // (2) 调用visitXxx()方法
        cw.visit(V1_8, ACC_PUBLIC + ACC_ABSTRACT + ACC_INTERFACE, "sample/InterfaceWithAnnotationField", null, "java/lang/Object", null);

        {
            FieldVisitor fv1 = cw.visitField(ACC_PUBLIC | ACC_FINAL | ACC_STATIC, "intValue", "I", null, 100);

            {
                AnnotationVisitor ann = fv1.visitAnnotation("Lsample/MyTag;", false);
                ann.visit("name", "tomcat");
                ann.visit("age", 10);
                ann.visitEnd();
            }

            fv1.visitEnd();
        }

        cw.visitEnd();

        // (3) 调用toByteArray()方法
        return cw.toByteArray();
    }

}
