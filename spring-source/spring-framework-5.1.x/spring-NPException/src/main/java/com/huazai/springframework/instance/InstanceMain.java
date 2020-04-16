package com.huazai.springframework.instance;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @author pyh
 * @date 2020/4/8 23:39
 */
public class InstanceMain {
	public static void main(String[] args) {
		AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(InstanceAppConfig.class);
		System.out.println(applicationContext.getBean("userDao", new Person()));
	}
}
