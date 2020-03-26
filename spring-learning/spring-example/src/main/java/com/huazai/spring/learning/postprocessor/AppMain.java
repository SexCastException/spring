package com.huazai.spring.learning.postprocessor;

import com.huazai.spring.learning.aop.AppConfig;
import com.huazai.spring.learning.aop.dao.Dao;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @author pyh
 * @date 2020/3/25 19:55
 */
public class AppMain {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(AppConfig.class);
//        BeanTest beanTest = applicationContext.getBean(BeanTest.class);
    }
}
