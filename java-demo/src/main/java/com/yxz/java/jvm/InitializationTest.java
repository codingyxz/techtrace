package com.yxz.java.jvm;


/**
 * Java编译器并不会为所有的类都产生clinit()初始化方法。
 * 哪些类在编译为字节码后，字节码文件中将不会包含clinit()方法？
 * 1、一个类中并没有声明任何的类变量，也没有静态代码块时
 * 2、一个类中声明类变量，但是没有明确使用类变量的初始化语句以及静态代码块来执行初始化操作时
 * 3、一个类中包含static final修饰的基本数据类型的字段，这些类字段初始化语句采用编译时常量表达式
 */
public class InitializationTest {

    //场景1：对于非静态的字段，不管是否进行了显式赋值，都不会生成<clinit>()方法
    public int num = 1;
    //场景2：静态的字段，没有显式的赋值，不会生成<clinit>()方法
    public static int num1;
    //场景3：比如对于声明为static final的基本数据类型的字段，不管是否进行了显式赋值，都不会生成<clinit>()方法
    public static final int num2 = 1;

}
