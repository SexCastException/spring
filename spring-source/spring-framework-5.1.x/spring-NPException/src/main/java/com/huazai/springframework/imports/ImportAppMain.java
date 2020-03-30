package com.huazai.springframework.imports;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @author pyh
 * @date 2020/3/28 20:36
 */
public class ImportAppMain {
	public static void main(String[] args) {
		AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(ImportAppConfig.class);
		UserDao userDao = applicationContext.getBean(UserDao.class);
//		userDao = (UserDao) applicationContext.getBean("com.huazai.springframework.imports.UserDao");
		userDao.queryUser();
	}
}
