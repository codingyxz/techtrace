package com.yxz.sboot.config;

import com.yxz.sboot.service.TestBeanService;
import com.yxz.sboot.service.TestEnhanceBeanService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfig {

    @Bean
    public TestBeanService testBeanService() {
        return new TestBeanService();
    }

    @Bean(initMethod = "xxxInit", destroyMethod = "xxxDestroy")
    public TestEnhanceBeanService testEnhanceBeanService() {
        return new TestEnhanceBeanService();
    }

}
