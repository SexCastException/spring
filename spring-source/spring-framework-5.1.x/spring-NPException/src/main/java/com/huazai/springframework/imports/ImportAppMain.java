package com.huazai.springframework.imports;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @author pyh
 * @date 2020/3/28 20:36
 */
public class ImportAppMain {
	public static void main(String[] args) {
		AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(ImportAppConfig.class);

		UserService userService = applicationContext.getBean(UserService.class);
		System.out.println(userService);

		UserDao userDao = applicationContext.getBean(UserDao.class);
		userDao.queryUser();

		ParentBean parentBean = (ParentBean) applicationContext.getBean("parentBean");
		System.out.println(parentBean.getName());

		ChildBean childBean = (ChildBean) applicationContext.getBean("childBean");
		System.out.println(childBean);

		User user = applicationContext.getBean(User.class);
		System.out.println(user);
	}
}
