package com.yxz.sboot.config;


import com.yxz.sboot.entity.Color;
import com.yxz.sboot.entity.Student;
import com.yxz.sboot.extend.MyImportBeanDefinitionRegister;
import com.yxz.sboot.extend.MyImportSelector;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({Student.class, Color.class, MyImportSelector.class, MyImportBeanDefinitionRegister.class})  // 快速注册，id默认全类名

public class ImportConfig {
}
