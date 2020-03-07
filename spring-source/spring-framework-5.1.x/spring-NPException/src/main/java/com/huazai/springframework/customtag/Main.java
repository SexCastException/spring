package com.huazai.springframework.customtag;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.ClassPathResource;

/**
 * @author pyh
 * @date 2020/3/6 21:14
 */
public class Main {
	public static void main(String[] args) {
		BeanFactory beanFactory = new XmlBeanFactory(new ClassPathResource("custom-tag.xml"));
		User bean = (User) beanFactory.getBean("testBean");
		System.out.println(bean);
	}
}
