package com.huazai.springframework.test.cycle;

import org.junit.Test;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.BeanCurrentlyInCreationException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.ClassPathResource;

/**
 * @author pyh
 * @date 2020/3/11 21:29
 */
public class Main {
	/**
	 * 1、Spring容器创建“testA”的bean, 首先去“当前创建bean池”查找是否当前bean正在创建，此时发现不存在，
	 * 则继续准备其需要的构造器参数“testB"， 并将"testA” 标识符放到“当前创建bean池”。<br>
	 * <p>
	 * 2、Spring容器创建“testB”的bean, 首先去“当前创建bean池”查找是否当前bean正在创建，此时发现不存在，
	 * 则继续准备其需要的构造器参数“testC"， 并将"testB” 标识符放到“当前创建bean池”。<br>
	 * <p>
	 * 3、Spring容器创建“testC”的bean, 首先去“当前创建bean池”查找是否当前bean正在创建，此时发现不存在，
	 * * 则继续准备其需要的构造器参数“testA"， 并将"testC” 标识符放到“当前创建bean池”。<br>
	 * <p>
	 * 4、到此为止Spring 容器要去创建“testA” bean,发现该bean标识符在“当前创建bean池”中，因为表示循环依赖，
	 * 抛出 {@link BeanCurrentlyInCreationException} <br>
	 */
	@Test
	public void testConstructorDependOnTheLoop() {
		BeanFactory beanFactory = new XmlBeanFactory(new ClassPathResource("cycle-test.xml"));
		try {
			TestA testA = (TestA) beanFactory.getBean("testA");
		} catch (BeanCreationException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 对于“prototype”作用域bean, Spring 容器无法完成依赖注入，因为Spring容器不缓
	 * 存“prototype”作用域的bean，因此无法提前暴露一个创建中的bean。
	 */
	@Test
	public void testPrototypeDependOnTheLoop() {
		BeanFactory beanFactory = new XmlBeanFactory(new ClassPathResource("cycle-test.xml"));
		try {
			TestA testA = (TestA) beanFactory.getBean("testA1");
		} catch (BeanCreationException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}
}
