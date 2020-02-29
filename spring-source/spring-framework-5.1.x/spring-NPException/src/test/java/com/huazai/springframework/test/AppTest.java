package com.huazai.springframework.test;

import org.junit.Test;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.ClassPathResource;

/**
 * @author pyh
 * @date 2020/2/28 18:43
 */
public class AppTest {
	@Test
	public void testMyTestBean() {
		System.out.println("hell world");
		BeanFactory beanFactory = new XmlBeanFactory(new ClassPathResource("application-context-test.xml"));
		MyTestBean myTestBean = beanFactory.getBean(MyTestBean.class);
		System.out.println(myTestBean);
	}

}
