package com.huazai.spring.learning.postprocessor;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.PriorityOrdered;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @author pyh
 * @date 2020/3/25 19:58
 */
@Component
public class TestBeanPostProcessor1 implements BeanPostProcessor , PriorityOrdered {
    public TestBeanPostProcessor1() {
        System.out.println("BeanTest Construct...");
    }

    @PostConstruct
    public void init() {
        System.out.println("BeanTest init....");
    }

    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (beanName.equals("cityDao")) {
            System.out.println(beanName+"----------postProcessBeforeInitialization");
        }
        return bean;
    }

    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (beanName.equals("cityDao")) {
            System.out.println(beanName+"----------postProcessAfterInitialization");
        }
        return bean;
    }

    public int getOrder() {
        return 2;
    }
}
