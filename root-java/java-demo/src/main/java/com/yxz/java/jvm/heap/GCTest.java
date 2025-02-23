package com.yxz.java.jvm.heap;

import java.time.LocalDateTime;

/**
 * -Xms100m -Xmx100m -Xmn50m -XX:SurvivorRatio=8 -XX:+PrintGCDetails -XX:+PrintGCDateStamps -XX:+PrintHeapAtGC
 * <p>
 * <p>
 * -XX:+UseParallelOldGC
 * -XX:+UseSerialGC
 * -XX:+UseConcMarkSweepGC
 * -XX:+UseG1GC
 */
public class GCTest {

    public static void main(String[] args) throws InterruptedException {
        test2();

    }


    /**
     * 测试二： ParallelGC中，Yong GC后，如果老年代的可用内存小于yongGC平均晋升到老年代的大小，则触发Full GC
     *
     * @throws InterruptedException
     */
    public static void test2() throws InterruptedException {
        byte[][] use = new byte[8][];
        use[0] = allocM(10);
        use[1] = allocM(10);
        use[2] = allocM(10);
        use[3] = allocM(10);
        use[4] = allocM(10);
        use[5] = allocM(10);
        use[6] = allocM(10);
        use[7] = allocM(10);

        Thread.sleep(1000);
    }

    /**
     * 测试一：Young GC 的触发时间
     * <p>
     * 对于常规收集器来说，当Eden区无法分配内存时，便会触发一次YongGC，但是对Parallel GC有点变化：
     * 1、当整个新生代剩余空间无法存放某个对象时，Parallel GC中该对象会直接进入老年代
     * 2、如果整个新生代剩余的空间可以存放，但只是Eden区空间不足，则会尝试一次Minor GC
     *
     * @throws InterruptedException
     */
    public static void test1() throws InterruptedException {
        allocM(10);
        allocM(10);
        allocM(10);
        allocM(20);
//        allocM(20);
//        allocM(5);
        Thread.sleep(1000);
    }

    private static byte[] allocM(int n) throws InterruptedException {
        byte[] ret = new byte[1024 * 1024 * n];
        System.out.println(String.format("%s:Alloc %d MB", LocalDateTime.now().toString(), n));
        Thread.sleep(500);
        return ret;
    }


}
