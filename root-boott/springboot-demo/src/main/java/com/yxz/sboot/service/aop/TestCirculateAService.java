package com.yxz.sboot.service.aop;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TestCirculateAService {

    @Autowired
    private TestCirculateBService testCirculateBService;

    public void circulateA() {
        log.info("TestCirculateAService.....circulateA...");
    }

}
