package com.yxz.sboot.extend;

import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

import java.util.function.Predicate;

/**
 * 自定义逻辑，返回需要的组件
 */
public class MyImportSelector implements ImportSelector {


    /**
     * 返回值，就是要导入到组件中的全类名
     * AnnotationMetadata：当前标注@import注解的类的所有注解信息
     */
    @Override
    public String[] selectImports(AnnotationMetadata importingClassMetadata) {
        return new String[]{"com.yxz.sboot.entity.Blue", "com.yxz.sboot.entity.Red"};
    }

    @Override
    public Predicate<String> getExclusionFilter() {
        return ImportSelector.super.getExclusionFilter();
    }
}
