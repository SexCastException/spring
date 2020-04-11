package com.huazai.springframework.processor;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @author pyh
 * @date 2020/3/25 22:34
 */
public class ProcessorAppMain {
	public static void main(String[] args) {
		AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
		applicationContext.register(ProcessorAppConfig.class);
		applicationContext.addBeanFactoryPostProcessor(new MyBeanFactoryPostProcessor());
		applicationContext.addBeanFactoryPostProcessor(new CityDao());
		/*applicationContext.setBeanNameGenerator(new BeanNameGenerator() {
			@Override
			public String generateBeanName(BeanDefinition definition, BeanDefinitionRegistry registry) {
				return definition.getBeanClassName()+"1";
			}
		});*/
		applicationContext.refresh();
		CityDao cityDao = (CityDao) applicationContext.getBean("cityDao");
		cityDao.query();
	}
}
