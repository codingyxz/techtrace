package com.yxz.java.proxy;

import org.springframework.cglib.core.DebuggingClassWriter;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

public class CGLibProxyTest {

    public static void main(String[] args) {
//        System.setProperty(DebuggingClassWriter.DEBUG_LOCATION_PROPERTY, "./root-java/java-demo/cglib");

        testMethodInterceptor();
    }


    public static void testMethodInterceptor() {
        HelloServiceImpl helloService = new HelloServiceImpl();
        helloService.setFlag(true);

        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(HelloServiceImpl.class);
        enhancer.setCallback(new HelloServiceMethodInterceptor(null));

        HelloService proxyHelloService = (HelloService) enhancer.create();
        proxyHelloService.sayHello("hah");

    }

    static class HelloServiceMethodInterceptor implements MethodInterceptor {

        private HelloService helloService;

        public HelloServiceMethodInterceptor(HelloService helloService) {
            this.helloService = helloService;
        }

        @Override
        public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {

            System.out.println("cglib .......");
            Object res = methodProxy.invokeSuper(o, objects);
//            Object res = method.invoke(o, objects);
//            Object res = method.invoke(helloService,objects);
            return res;
        }

    }

}
