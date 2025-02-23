package com.yxz.java.jvm.stack;


/**
 * 演示 StackOverflowError
 * <p>
 * 默认栈 1024kb
 * 设置栈大小参数：-Xss256k
 */
public class StackErrorTest {

    private static int count = 1;

    public static void main(String[] args) {

        System.out.println(count);
        count++;
        Byte[] bytes = new Byte[1024 * 10];
        bytes = null;
        main(args);
    }
}
