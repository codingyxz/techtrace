package com.yxz.sboot;

import com.yxz.sboot.service.aop.TestAopService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SpringbootDemoApplicationTests {

    @Autowired
    private TestAopService testAopService;

    @Test
    void contextLoads() {
    }

    @Test
    public void testAop(){
        testAopService.sayHi("today");
    }

}
