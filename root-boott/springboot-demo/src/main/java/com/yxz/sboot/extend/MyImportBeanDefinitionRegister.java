package com.yxz.sboot.extend;

import com.yxz.sboot.entity.Green;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

public class MyImportBeanDefinitionRegister implements ImportBeanDefinitionRegistrar {


    /**
     * AnnotationMetadata：当前标注@import注解的类的所有注解信息
     * BeanDefinitionRegistry：BeanDefinition注册器
     */
    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        RootBeanDefinition rootBeanDefinition = new RootBeanDefinition(Green.class);
        registry.registerBeanDefinition("green", rootBeanDefinition);
    }
}
