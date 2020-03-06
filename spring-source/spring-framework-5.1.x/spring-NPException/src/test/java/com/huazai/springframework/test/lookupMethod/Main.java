package com.huazai.springframework.test.lookupMethod;

import org.junit.Test;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.ClassPathResource;

/**
 * @author pyh
 * @date 2020/3/2 22:06
 */
public class Main {
	@Test
	public void test() {
		BeanFactory beanFactory = new XmlBeanFactory(new ClassPathResource("lookup-test.xml"));
		GetBeanTest bean = beanFactory.getBean(GetBeanTest.class);
		bean.showMe();
	}

}
