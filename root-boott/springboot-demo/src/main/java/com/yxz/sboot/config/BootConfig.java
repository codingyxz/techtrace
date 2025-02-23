package com.yxz.sboot.config;

import com.yxz.sboot.service.TestBeanService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BootConfig {

    @Bean
    public TestBeanService testBeanService() {
        return new TestBeanService();
    }
}
