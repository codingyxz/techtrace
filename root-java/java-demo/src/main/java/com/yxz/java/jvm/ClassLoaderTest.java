package com.yxz.java.jvm;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.InputStream;

public class ClassLoaderTest {


    public static void main(String[] args) {
        new ClassLoaderTest().testJava8();
    }


    /**
     * 需设置Jdk8
     */
    @Test
    public void testJava8() {
//        String classpath = System.getProperty("sun.boot.class.path");
//        File[] files = getClassPath(classpath);
//        URLClassPath bootstrapClassPath = Launcher.getBootstrapClassPath();
//        MetaIndex metaIndex = MetaIndex.forJar(new File("a.txt"));
//        System.out.println("hello");
    }

    private static File[] getClassPath(String var0) {
        File[] var1;
        if (var0 != null) {
            int var2 = 0;
            int var3 = 1;
            boolean var4 = false;

            int var5;
            int var7;
            for (var5 = 0; (var7 = var0.indexOf(File.pathSeparator, var5)) != -1; var5 = var7 + 1) {
                ++var3;
            }

            var1 = new File[var3];
            var4 = false;

            for (var5 = 0; (var7 = var0.indexOf(File.pathSeparator, var5)) != -1; var5 = var7 + 1) {
                if (var7 - var5 > 0) {
                    var1[var2++] = new File(var0.substring(var5, var7));
                } else {
                    var1[var2++] = new File(".");
                }
            }

            if (var5 < var0.length()) {
                var1[var2++] = new File(var0.substring(var5));
            } else {
                var1[var2++] = new File(".");
            }

            if (var2 != var3) {
                File[] var6 = new File[var2];
                System.arraycopy(var1, 0, var6, 0, var2);
                var1 = var6;
            }
        } else {
            var1 = new File[0];
        }

        return var1;
    }


    /**
     * 站在程序的角度看，引导类加载器与另外两种类加载器（系统类加载器和扩展类加载器）并不是同一个层次意义上的加载器，
     * 引导类加载器是使用C++语言编写而成的，而另外两种类加载器则是使用Java语言编写而成的。
     * 由于引导类加载器压根儿就不是一个Java类，因此在Java程序中只能打印出空值。
     */


    /**
     * 测试不同的类加载器
     */
    @Test
    public void test1() {

        //获得当前类的ClassLoader
        System.out.println(this.getClass().getClassLoader());
        //获得当前线程上下文的ClassLoader
        System.out.println(Thread.currentThread().getContextClassLoader());
        //获得系统的ClassLoader
        System.out.println(ClassLoader.getSystemClassLoader());

    }

    /**
     * 数组的类加载器
     * <p>
     * 数组类的Class对象，不是由类加载器去创建的，而是在Java运行期JVM根据需要自动创建的。
     * 对于数组类的类加载器来说，是通过Class.getClassLoader()返回的，与数组当中元素类型的类加载器是一样的；
     * 如果数组当中的元素类型是基本数据类型，数组类是没有类加载器的。
     */
    @Test
    public void test2() {
        String[] strArr = new String[6];
        System.out.println(strArr.getClass().getClassLoader());
        // 运行结果：null

        ClassLoaderTest[] test = new ClassLoaderTest[1];
        System.out.println(test.getClass().getClassLoader());
        // 运行结果：sun.misc.Launcher$AppClassLoader@18b4aac2
        // 运行结果：jdk.internal.loader.ClassLoaders$AppClassLoader@2c13da15

        int[] ints = new int[2];
        System.out.println(ints.getClass().getClassLoader());
        // 运行结果：null
    }


    /**
     * URL getResource(String name)
     * InputStream getResourceAsStream(String name)
     * Enumeration<URL> getResources(String name)
     * getSystemResource,
     * getSystemResources,
     * getSystemResourceAsStream
     * <p>
     * 其中，前两个方法是Class和ClassLoader类都有的，后面的方法只有ClassLoader类有。
     * 借助这些方法，可以实现从classpath下读取资源，或者相对于当前class文件所在的目录读取资源。
     * <p>
     * 那么，这两个类中获取资源的方法的区别在哪里呢？
     * 区别在于ClassLoader类中的这两个方法仅支持相对于classpath的路径（开头不能加/，加了就获取不到classpath下的文件了）。
     * 而Class类中的这两个方法除了支持相对于classpath的路径外（以/开头），还支持相对于当前class文件所在目录的路径（开头不加/）
     */
    @Test
    public void test3() {

        InputStream aa = ClassLoaderTest.class.getClassLoader().getResourceAsStream("b.txt");
        InputStream resourceAsStream = ClassLoaderTest.class.getResourceAsStream("/b.txt");
        System.out.println("----");
    }
}
