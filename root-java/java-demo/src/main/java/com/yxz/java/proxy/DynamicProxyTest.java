package com.yxz.java.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;


/**
 * -Djdk.proxy.ProxyGenerator.saveGeneratedFiles=true
 * 或者
 * System.getProperties().put("jdk.proxy.ProxyGenerator.saveGeneratedFiles", "true");
 */
public class DynamicProxyTest {
    public static void main(String[] args) throws InterruptedException {

        /**
         * 生成代理类文件
         */
        // 1、旧版本jdk
//        System.getProperties().put("sun.misc.ProxyGenerator.saveGeneratedFiles", "true");
        // 2、新版本jdk
        System.getProperties().put("jdk.proxy.ProxyGenerator.saveGeneratedFiles", "true");

        HelloService proxyInstance = (HelloService) Proxy.newProxyInstance(
                DynamicProxyTest.class.getClassLoader(),
                new Class[]{HelloService.class},
                new HelloServiceInvocationHandler(new HelloServiceImpl()));


        proxyInstance.sayHello("hello");

        Thread.sleep(10000l);

    }

    static class HelloServiceInvocationHandler implements InvocationHandler {

        private HelloService helloService;

        public HelloServiceInvocationHandler(HelloService helloService) {
            this.helloService = helloService;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            System.out.println("说我是光-----" + args[0]);
            return method.invoke(helloService, args);
        }
    }

}
