/*
 * Copyright 2002-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.beans.factory.support;

import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

/**
 * Utility methods that are useful for bean definition reader implementations.
 * Mainly intended for internal use.
 *
 * @author Juergen Hoeller
 * @author Rob Harrop
 * @see PropertiesBeanDefinitionReader
 * @see org.springframework.beans.factory.xml.DefaultBeanDefinitionDocumentReader
 * @since 1.1
 */
public abstract class BeanDefinitionReaderUtils {

	/**
	 * Separator for generated bean names. If a class name or parent name is not
	 * unique, "#1", "#2" etc will be appended, until the name becomes unique.
	 */
	public static final String GENERATED_BEAN_NAME_SEPARATOR = BeanFactoryUtils.GENERATED_BEAN_NAME_SEPARATOR;


	/**
	 * Create a new GenericBeanDefinition for the given parent name and class name,
	 * eagerly loading the bean class if a ClassLoader has been specified.
	 *
	 * @param parentName  the name of the parent bean, if any
	 * @param className   the name of the bean class, if any
	 * @param classLoader the ClassLoader to use for loading bean classes
	 *                    (can be {@code null} to just register bean classes by name)
	 * @return the bean definition
	 * @throws ClassNotFoundException if the bean class could not be loaded
	 */
	public static AbstractBeanDefinition createBeanDefinition(
			@Nullable String parentName, @Nullable String className, @Nullable ClassLoader classLoader) throws ClassNotFoundException {

		GenericBeanDefinition bd = new GenericBeanDefinition();
		// parentName可能为空
		bd.setParentName(parentName);
		// parent属性值不为空，则记录class属性值值，或者其对应的类对象
		if (className != null) {
			if (classLoader != null) {
				// 如果classLoader不为空，则使用以传入的classLoader加载类对象并保存到beanClass
				bd.setBeanClass(ClassUtils.forName(className, classLoader));
			} else {    // 否则不加载只是记录className
				bd.setBeanClassName(className);
			}
		}
		return bd;
	}

	/**
	 * Generate a bean name for the given top-level bean definition,
	 * unique within the given bean factory.
	 *
	 * @param beanDefinition the bean definition to generate a bean name for
	 * @param registry       the bean factory that the definition is going to be
	 *                       registered with (to check for existing bean names)
	 * @return the generated bean name
	 * @throws BeanDefinitionStoreException if no unique name can be generated
	 *                                      for the given bean definition
	 * @see #generateBeanName(BeanDefinition, BeanDefinitionRegistry, boolean)
	 */
	public static String generateBeanName(BeanDefinition beanDefinition, BeanDefinitionRegistry registry)
			throws BeanDefinitionStoreException {

		return generateBeanName(beanDefinition, registry, false);
	}

	/**
	 * 为给定的bean定义生成唯一的bean名称 <br>
	 * Generate a bean name for the given bean definition, unique within the
	 * given bean factory.
	 *
	 * @param definition  the bean definition to generate a bean name for
	 * @param registry    the bean factory that the definition is going to be
	 *                    registered with (to check for existing bean names). beanDefinition注册器
	 * @param isInnerBean whether the given bean definition will be registered
	 *                    as inner bean or as top-level bean (allowing for special name generation
	 *                    for inner beans versus top-level beans)
	 * @return the generated bean name
	 * @throws BeanDefinitionStoreException if no unique name can be generated
	 *                                      for the given bean definition
	 */
	public static String generateBeanName(
			BeanDefinition definition, BeanDefinitionRegistry registry, boolean isInnerBean)
			throws BeanDefinitionStoreException {

		String generatedBeanName = definition.getBeanClassName();
		if (generatedBeanName == null) {    // 如果不存在beanName，则为其生成相应名称
			if (definition.getParentName() != null) {    // 通过parent属性生成beanName
				generatedBeanName = definition.getParentName() + "$child";
			} else if (definition.getFactoryBeanName() != null) {    // 通过factory-bean生成beanName
				generatedBeanName = definition.getFactoryBeanName() + "$created";
			}
		}
		if (!StringUtils.hasText(generatedBeanName)) {    // 如果没有指定parent或factory-bean，则生成beanName失败，则抛出异常
			throw new BeanDefinitionStoreException("Unnamed bean definition specifies neither " +
					"'class' nor 'parent' nor 'factory-bean' - can't generate bean name");
		}

		String id = generatedBeanName;
		if (isInnerBean) {    // 嵌套bean
			// Inner bean: generate identity hashcode suffix.
			// 为嵌套bean添加十六进制的hashCode作为后缀
			id = generatedBeanName + GENERATED_BEAN_NAME_SEPARATOR + ObjectUtils.getIdentityHexString(definition);
		} else {    // 顶级bean
			// Top-level bean: use plain class name with unique suffix if necessary.
			return uniqueBeanName(generatedBeanName, registry);
		}
		return id;
	}

	/**
	 * Turn the given bean name into a unique bean name for the given bean factory,
	 * appending a unique counter as suffix if necessary.
	 *
	 * @param beanName the original bean name
	 * @param registry the bean factory that the definition is going to be
	 *                 registered with (to check for existing bean names)
	 * @return the unique bean name to use
	 * @since 5.1
	 */
	public static String uniqueBeanName(String beanName, BeanDefinitionRegistry registry) {
		String id = beanName;
		int counter = -1;

		// Increase counter until the id is unique.
		// 一直从-1累加直至拼接的id唯一
		while (counter == -1 || registry.containsBeanDefinition(id)) {
			counter++;
			id = beanName + GENERATED_BEAN_NAME_SEPARATOR + counter;
		}
		return id;
	}

	/**
	 * 向给定的bean工厂注册给定的bean定义。<br>
	 * 1、通过beanName注册 <br>
	 * 2、通过别名注册 <br>
	 * Register the given bean definition with the given bean factory.
	 *
	 * @param definitionHolder the bean definition including name and aliases
	 * @param registry         the bean factory to register with
	 * @throws BeanDefinitionStoreException if registration failed
	 */
	public static void registerBeanDefinition(
			BeanDefinitionHolder definitionHolder, BeanDefinitionRegistry registry)
			throws BeanDefinitionStoreException {

		// Register bean definition under primary name.
		// 1、使用beanName做唯一标识注册
		String beanName = definitionHolder.getBeanName();
		registry.registerBeanDefinition(beanName, definitionHolder.getBeanDefinition());

		// Register aliases for bean name, if any.
		// 2、注册该beanDefinition所有别名
		String[] aliases = definitionHolder.getAliases();
		if (aliases != null) {
			for (String alias : aliases) {
				registry.registerAlias(beanName, alias);
			}
		}
	}

	/**
	 * Register the given bean definition with a generated name,
	 * unique within the given bean factory.
	 *
	 * @param definition the bean definition to generate a bean name for
	 * @param registry   the bean factory to register with
	 * @return the generated bean name
	 * @throws BeanDefinitionStoreException if no unique name can be generated
	 *                                      for the given bean definition or the definition cannot be registered
	 */
	public static String registerWithGeneratedName(
			AbstractBeanDefinition definition, BeanDefinitionRegistry registry)
			throws BeanDefinitionStoreException {

		String generatedName = generateBeanName(definition, registry, false);
		registry.registerBeanDefinition(generatedName, definition);
		return generatedName;
	}

}
