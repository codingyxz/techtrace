package com.yxz.sboot.listener;

import org.springframework.context.ApplicationEvent;

import java.time.Clock;

public class MyApplicationEvent extends ApplicationEvent {

    public MyApplicationEvent(Object source) {
        super(source);
    }

    public MyApplicationEvent(Object source, Clock clock) {
        super(source, clock);
    }
}
