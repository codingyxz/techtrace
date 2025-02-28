package com.yxz.sboot.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Slf4j
@Component
public class TestDIService {

    @Autowired
    private TestBeanService testBeanService;

    @Resource
    private TestComponentService testComponentService;

}
