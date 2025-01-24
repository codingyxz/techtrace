package com.yxz.java.jvm;

import org.junit.jupiter.api.Test;

public class ByteCodeTest {


    @Test
    public void test1() {
        int i = 10;
        i = i++;
        System.out.println(i);  // 10
    }


    @Test
    public void test2() {
        int i = 2;
        i *= i++;
        System.out.println(i);  // 4
    }


    @Test
    public void test3() {
        int k = 10;
        k = k + k++ + ++k;
        Integer i1 = 10;


        System.out.println(k); // 32
    }


    /**
     * 包装类对象的缓存问题
     */
    @Test
    public void test4() {
        Integer i1 = 10;
        Integer i2 = 10;
        System.out.println(i1 == i2);

        Integer i3 = 128;
        Integer i4 = 128;
        System.out.println(i3 == i4);

        Boolean b1 = true;
        Boolean b2 = true;
        System.out.println(b1 == b2);
    }


    /**
     * String 声明的字面量数据都放在字符串常量池中
     * jdk6中字符串常量池存放在方法区（即永久代中）
     * jdk7及以后字符串常量池存放在堆空间
     */
    @Test
    public void test5() {

        String name = "zhang3";
        String name2 = "zhang3";
        System.out.println(name == name2);


        String str = new String("hello") + new String("world");
        String str2 = "helloworld";

        System.out.println(str == str2);  // false

        // 将str链接到字符串常量池
        str.intern();
    }


    /**
     * Son.x = 0
     * Son.x = 30
     * 20
     */
    @Test
    public void test6() {
        Father f = new Son();
        System.out.println(f.x);
    }

}


class Father {
    int x = 10;

    public Father() {
        this.print();
        x = 20;
    }

    public void print() {
        System.out.println("Father.x = " + x);
    }
}

class Son extends Father {
    int x = 30;

    public Son() {
        this.print();
        x = 40;
    }

    @Override
    public void print() {
        System.out.println("Son.x = " + x);

    }
}
