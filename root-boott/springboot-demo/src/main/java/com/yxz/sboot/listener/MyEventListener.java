package com.yxz.sboot.listener;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class MyEventListener {

    @EventListener(MyApplicationEvent.class)
    public void customEventListener(ApplicationEvent event){
        System.out.println(Thread.currentThread().getName() + "--MyEventListener.......customEventListener....");
    }

}
