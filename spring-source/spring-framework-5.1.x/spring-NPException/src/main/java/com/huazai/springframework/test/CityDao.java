package com.huazai.springframework.test;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.stereotype.Repository;

/**
 * @author pyh
 * @date 2020/3/25 22:33
 */
@Repository
public class CityDao implements BeanFactoryPostProcessor {
	public void query() {
		System.out.println("query .....");
	}

	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		System.out.println("CityDao----postProcessBeanFactory");
	}
}
