package com.yxz.sboot.service.aop;

import org.springframework.stereotype.Component;

@Component
public class TestAopService {

    public void sayHi(String name) {
        System.out.println(name + "------------");
    }

}
