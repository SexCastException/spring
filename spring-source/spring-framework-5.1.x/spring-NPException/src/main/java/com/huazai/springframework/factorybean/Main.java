package com.huazai.springframework.factorybean;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.ClassPathResource;

/**
 * @author pyh
 * @date 2020/3/7 23:34
 */
public class Main {
	public static void main(String[] args) throws Exception {
		BeanFactory beanFactory = new XmlBeanFactory(new ClassPathResource("factory-bean.xml"));
		CarFactoryBean carFactoryBean = beanFactory.getBean(CarFactoryBean.class);
		System.out.println(carFactoryBean);
		System.out.println(carFactoryBean.getObject());

		Car car = (Car) beanFactory.getBean("carFactoryBean");
		System.out.println(car);

		CarFactoryBean factoryBean = (CarFactoryBean) beanFactory.getBean("&carFactoryBean");
		System.out.println(factoryBean);
	}
}
