package com.yxz.java.jvm.stack;


/**
 * 栈上分配测试
 * -Xmx256m -Xms256m -XX:+DoEscapeAnalysis -XX:+PrintGCDetails
 */
public class StackAllocTest {

    public static void main(String[] args) {
        long start = System.currentTimeMillis();

        for (int i = 0; i < 10000000; i++) {
            alloc();
        }

        long end = System.currentTimeMillis();

        System.out.println("花费的时间为：" + (end - start) + " ms");

        // 方便查看堆中对象个数，线程sleep
        try {
            Thread.sleep(1000000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void alloc() {
        User u = new User();
    }

    static class User {
//        Byte[] aByte = new Byte[1024 * 10];
    }
}
