package com.yxz.sboot.service.aop;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TestCirculateBService {

    @Autowired
    private TestCirculateAService testCirculateAService;

    public void circulateB() {
        log.info("TestCirculateBService.....circulateB...");
    }

}
