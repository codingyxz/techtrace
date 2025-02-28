package com.yxz.sboot.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;


/**
 * JSR-250注解
 *
 * @PostConstruct 相当于 init-method
 * @preDestroy 相当与 destroy-method
 */
@Slf4j
public class TestEnhanceBeanService implements InitializingBean, DisposableBean {

    public TestEnhanceBeanService() {
        log.info("TestInitDestroyService.....Constructor....");
    }

    @Override
    public void destroy() throws Exception {
        log.info("TestInitDestroyService.....DisposableBean.destroy....");
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        log.info("TestInitDestroyService.....afterPropertiesSet....");
    }

    public void xxxInit() {
        log.info("TestInitDestroyService.....xxxInit....");
    }

    public void xxxDestroy() {
        log.info("TestInitDestroyService.....xxxDestroy....");
    }

    @PostConstruct
    public void xxxPostConstruct() {
        log.info("TestInitDestroyService.....xxxPostConstruct....");
    }

    @PreDestroy
    public void xxxPreDestroy() {
        log.info("TestInitDestroyService.....xxxPreDestroy....");
    }

}
