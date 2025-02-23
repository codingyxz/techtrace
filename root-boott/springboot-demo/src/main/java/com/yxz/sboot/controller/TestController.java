package com.yxz.sboot.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/test/{msg}")
    public String sayTest(@PathVariable(value = "msg") String msg) {
        return msg + "--" + System.currentTimeMillis();
    }
}
