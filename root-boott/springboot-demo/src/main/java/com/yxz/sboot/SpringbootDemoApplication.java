package com.yxz.sboot;

import com.yxz.sboot.listener.MyApplicationEvent;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableAsync;


@EnableAsync
@SpringBootApplication
public class SpringbootDemoApplication {

    public static void main(String[] args) {

        ConfigurableApplicationContext applicationContext = SpringApplication.run(SpringbootDemoApplication.class, args);

        // 发布事件
//        applicationContext.publishEvent(new MyApplicationEvent("hah"));
//        System.out.println(Thread.currentThread().getName() + "--SpringbootDemoApplication.........main.....");

        // 打印beanName
//        String[] definitionNames = applicationContext.getBeanDefinitionNames();
//        for (String name : definitionNames) {
//            System.out.println(name);
//        }

        // 打印beanClass
        String[] definitionNames = applicationContext.getBeanDefinitionNames();
        for (String name : definitionNames) {
            System.out.println(name + "--" + applicationContext.getBean(name).getClass());
        }


    }

}
