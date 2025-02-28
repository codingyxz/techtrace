package com.yxz.asm.exec;

import com.yxz.asm.util.FileUtils;
import org.objectweb.asm.ClassWriter;

import static org.objectweb.asm.Opcodes.*;

/**
 * 预期目标
 * public interface NullInterface {
 * }
 */
public class GenerateNullInterface {

    public static void main(String[] args) throws Exception {
        String relative_path = "sample/NullInterface.class";
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
        cw.visit(V1_8,        // version
                ACC_PUBLIC + ACC_SUPER + ACC_INTERFACE,  // access
                "sample/NullInterface",       // name
                null,                      // signature
                "java/lang/Object",       // superName
                null                       // interfaces
        );
        cw.visitEnd();

        // (3) 调用toByteArray()方法
        return cw.toByteArray();
    }

}
