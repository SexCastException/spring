package com.huazai.springframework.test.replacedMethod;

import org.junit.Test;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.ClassPathResource;

/**
 * @author pyh
 * @date 2020/3/2 22:54
 */
public class Main {
	@Test
	public void testReplacedMethod() {
		BeanFactory beanFactory = new XmlBeanFactory(new ClassPathResource("replaced-test.xml"));
		TestChangeMethod bean = beanFactory.getBean(TestChangeMethod.class);
		bean.changeMe();
	}
}
