package com.yxz.java.base;

import org.junit.jupiter.api.Test;

public class StringInternTest {

    public static void main(String[] args) {

        new StringInternTest().testIntern();

//        try {
//            Thread.sleep(100000000l);
//        } catch (InterruptedException ex) {
//        }

    }

    @Test
    public void testIntern() {
        //当前代码执行完以后，字符串常量池中并没有"ab"
        String str1 = new String("a") + new String("b");

        //jdk6中：在串池中创建一个字符串"ab"
        //jdk8中：串池中没有创建字符串"ab",而是创建一个引用，指向new String("ab")，将此引用返回
        String str2 = str1.intern();

        System.out.println(str1 == "ab"); //jdk6:false  jdk8:true
        System.out.println(str2 == "ab"); //jdk6:true  jdk8:true
    }

    /**
     * new String("xxx")创建几个对象
     */
    @Test
    public void testString1() {


        /**
         *  1、new 关键字在堆空间创建的
         *  2、字符串常量池中的对象"1"，字节码指令：ldc
         */
        String str1 = new String("1");

        /**
         *  1、new StringBuilder()
         *  2、new String("a")
         *  3、ldc "a"
         *  4、new String("b")
         *  5、ldc "b"
         *  6、StringBuilder的toString()方法，内部调用new String("ab")
         *      强调：toString()的调用，在字符串常量池中没有生成"ab"
         */
        String str2 = new String("a") + new String("b");
    }


    /**
     * String的垃圾回收
     * -Xms15m -Xmx15m -XX:+PrintStringTableStatistics -XX:+PrintGCDetails
     */
    @Test
    public void testStringGC() {
        for (int i = 0; i < 100000; i++) {
            String.valueOf(i).intern();
        }
    }


}
