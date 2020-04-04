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

package org.springframework.context.support;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.*;
import org.springframework.core.OrderComparator;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;
import org.springframework.lang.Nullable;

import java.util.*;

/**
 * 委托对 {@link AbstractApplicationContext}的后置处理器处理 <br>
 * 处理的后置处理器包括 {@link BeanPostProcessor}、{@link BeanDefinitionRegistryPostProcessor}
 * 和 {@link BeanFactoryPostProcessor} <br>
 * <p>
 * Delegate for AbstractApplicationContext's post-processor handling.
 *
 * @author Juergen Hoeller
 * @since 4.0
 */
final class PostProcessorRegistrationDelegate {

	private PostProcessorRegistrationDelegate() {
	}


	/**
	 * 1、先处理开发者自定义的 {@link BeanDefinitionRegistryPostProcessor}，然后再处理spring内置的 {@link BeanDefinitionRegistryPostProcessor} <br>
	 * <p>
	 * 2、先处理开发者自定义的 {@link BeanFactoryPostProcessor}，然后再处理spring内置的 {@link BeanFactoryPostProcessor} <br>
	 *
	 * @param beanFactory
	 * @param beanFactoryPostProcessors
	 */
	public static void invokeBeanFactoryPostProcessors(
			ConfigurableListableBeanFactory beanFactory, List<BeanFactoryPostProcessor> beanFactoryPostProcessors) {

		// Invoke BeanDefinitionRegistryPostProcessors first, if any.
		/*
			记录已经执行过 BeanDefinitionRegistryPostProcessor 实现类的 beanName，方便判断不在重复执行
			BeanDefinitionRegistryPostProcessor 实现类
		 */
		Set<String> processedBeans = new HashSet<>();

		if (beanFactory instanceof BeanDefinitionRegistry) {
			BeanDefinitionRegistry registry = (BeanDefinitionRegistry) beanFactory;
			// 保存开发者提供的BeanFactoryPostProcessor实现类，通过 setBeanFactoryPostProcessors()提供
			List<BeanFactoryPostProcessor> regularPostProcessors = new ArrayList<>();
			// 保存开发者提供的BeanDefinitionRegistryPostProcessor实现类，通过 setBeanFactoryPostProcessors()提供
			// BeanDefinitionRegistryPostProcessor扩展了BeanFactoryPostProcessor接口，在调用父接口的方法之前先调用
			// 自己的扩展方法，该变量在后序代码中会将spring内部的BeanDefinitionRegistryPostProcessor实现类集合合并到此处
			List<BeanDefinitionRegistryPostProcessor> registryProcessors = new ArrayList<>();

			for (BeanFactoryPostProcessor postProcessor : beanFactoryPostProcessors) {
				/*
					将BeanFactoryPostProcessor接口实现了和该接口的子接口BeanDefinitionRegistryPostProcessor实现类
					区分出来，然后逐一调用子接口实现类的扩展方法
				 */
				if (postProcessor instanceof BeanDefinitionRegistryPostProcessor) {
					BeanDefinitionRegistryPostProcessor registryProcessor =
							(BeanDefinitionRegistryPostProcessor) postProcessor;
					// 调用自定义开发的 BeanDefinitionRegistryPostProcessor 类型的实现方法
					// 自定义开发的实现类执行优先于spring内置的实现类
					registryProcessor.postProcessBeanDefinitionRegistry(registry);
					registryProcessors.add(registryProcessor);
				} else {
					regularPostProcessors.add(postProcessor);
				}
			}

			// Do not initialize FactoryBeans here: We need to leave all regular beans
			// uninitialized to let the bean factory post-processors apply to them!
			// Separate between BeanDefinitionRegistryPostProcessors that implement
			// PriorityOrdered, Ordered, and the rest.
			// 临时保存当前要执行的spring提供的 BeanDefinitionRegistryPostProcessor类型的实现方法
			List<BeanDefinitionRegistryPostProcessor> currentRegistryProcessors = new ArrayList<>();

			// 首先调用实现了 PriorityOrdered 接口且同时又实现了BeanDefinitionRegistryPostProcessor接口的方法
			// First, invoke the BeanDefinitionRegistryPostProcessors that implement PriorityOrdered.
			/*
				通过 ConfigurationClassPostProcessor 类型获取所有实现类的beanName，如 ConfigurationClassPostProcessor 对象，
				ConfigurationClassPostProcessor对象beanName为org.springframework.context.annotation.internalConfigurationAnnotationProcessor
			 	该beanName是在实例化 AnnotatedBeanDefinitionReader 对象时，调用AnnotationConfigUtils.registerAnnotationConfigProcessors()方法时加入的
			 	ConfigurationClassPostProcessor对象需要在spring工厂初始化之前插手spring factory的相关操作
			*/
			String[] postProcessorNames =
					beanFactory.getBeanNamesForType(BeanDefinitionRegistryPostProcessor.class, true, false);
			// 遍历BeanDefinitionRegistryPostProcessor实现类的beanName
			for (String ppName : postProcessorNames) {
				// 匹配BeanDefinitionRegistryPostProcessor实现类是否实现了 PriorityOrdered接口
				if (beanFactory.isTypeMatch(ppName, PriorityOrdered.class)) {
					// 通过beanName获取spring提供的BeanDefinitionRegistryPostProcessor实现bean并加入到currentRegistryProcessors集合
					currentRegistryProcessors.add(beanFactory.getBean(ppName, BeanDefinitionRegistryPostProcessor.class));
					processedBeans.add(ppName);
				}
			}
			// 排序
			sortPostProcessors(currentRegistryProcessors, beanFactory);
			// 将用户定义的 BeanDefinitionRegistryPostProcessor实现类和spring内部定义的该实现类合并
			registryProcessors.addAll(currentRegistryProcessors);
			// 遍历执行 currentRegistryProcessors 集合中每个对象（同时实现了PriorityOrdered接口）的BeanDefinitionRegistryPostProcessor实现方法
			invokeBeanDefinitionRegistryPostProcessors(currentRegistryProcessors, registry);
			// 执行完后清空 currentRegistryProcessors 集合
			currentRegistryProcessors.clear();

			// Next, invoke the BeanDefinitionRegistryPostProcessors that implement Ordered.
			// 再调用实现了 Ordered 接口且同时又实现了BeanDefinitionRegistryPostProcessor接口的方法
			postProcessorNames = beanFactory.getBeanNamesForType(BeanDefinitionRegistryPostProcessor.class, true, false);
			for (String ppName : postProcessorNames) {
				// 匹配BeanDefinitionRegistryPostProcessor实现类是否实现了 Ordered接口
				/*
					注意此处比上述多了 !processedBeans.contains(ppName) 代码，即剔除已经被执行过了的
					BeanDefinitionRegistryPostProcessor 实现类，即在上述invokeBeanDefinitionRegistryPostProcessors方法中执行过了
				 */
				if (!processedBeans.contains(ppName) && beanFactory.isTypeMatch(ppName, Ordered.class)) {
					currentRegistryProcessors.add(beanFactory.getBean(ppName, BeanDefinitionRegistryPostProcessor.class));
					processedBeans.add(ppName);
				}
			}
			// 排序
			sortPostProcessors(currentRegistryProcessors, beanFactory);
			// 将用户定义的 BeanDefinitionRegistryPostProcessor实现类和spring内部定义的该实现类合并
			registryProcessors.addAll(currentRegistryProcessors);
			// 遍历执行 currentRegistryProcessors 集合中每个对象（同时实现了Ordered接口）的BeanDefinitionRegistryPostProcessor实现方法
			invokeBeanDefinitionRegistryPostProcessors(currentRegistryProcessors, registry);
			// 执行完后清空 currentRegistryProcessors 集合
			currentRegistryProcessors.clear();

			// Finally, invoke all other BeanDefinitionRegistryPostProcessors until no further ones appear.
			// 最后执行既没有实现PriorityOrdered接口，也没有实现Ordered接口的BeanDefinitionRegistryPostProcessor实现类的实现方法
			boolean reiterate = true;
			while (reiterate) {
				reiterate = false;
				postProcessorNames = beanFactory.getBeanNamesForType(BeanDefinitionRegistryPostProcessor.class, true, false);
				for (String ppName : postProcessorNames) {
					// 剔除已经处理过的 BeanDefinitionRegistryPostProcessor 实现类
					if (!processedBeans.contains(ppName)) {
						currentRegistryProcessors.add(beanFactory.getBean(ppName, BeanDefinitionRegistryPostProcessor.class));
						processedBeans.add(ppName);
						reiterate = true;
					}
				}
				// 对后置处理器进行排序
				sortPostProcessors(currentRegistryProcessors, beanFactory);
				/*
				 	将spring内置的BeanDefinitionRegistryPostProcessor实现类（不包括实现PriorityOrdered和Ordered接口）
				 	和自定义的BeanDefinitionRegistryPostProcessor实现类进行合并，然后遍历集合逐一调用该接口被实现的方法
				 */
				registryProcessors.addAll(currentRegistryProcessors);
				// 遍历执行被实现的方法
				invokeBeanDefinitionRegistryPostProcessors(currentRegistryProcessors, registry);
				currentRegistryProcessors.clear();
			}

			// Now, invoke the postProcessBeanFactory callback of all processors handled so far.
			/*
			    到此为止，不管自定义的还是spring内置的BeanDefinitionRegistryPostProcessor实现类都已经处理完了，
			    现在回调自定义的 BeanFactoryPostProcessor 接口中被实现的方法（包括子接口 BeanDefinitionRegistryPostProcessor的实现类）
			*/
			invokeBeanFactoryPostProcessors(registryProcessors, beanFactory);
			invokeBeanFactoryPostProcessors(regularPostProcessors, beanFactory);
		} else {    // else，beanFactory不是 BeanDefinitionRegistry
			// Invoke factory processors registered with the context instance.
			invokeBeanFactoryPostProcessors(beanFactoryPostProcessors, beanFactory);
		}

		// Do not initialize FactoryBeans here: We need to leave all regular beans
		// uninitialized to let the bean factory post-processors apply to them!
		// 翻译：不要在这里初始化factoryBean:我们需要保留所有常规bean未初始化，以让bean工厂的后处理器应用于它们!
		// 获取spring内置的BeanFactoryPostProcessor 类型的实现类的beanName
		String[] postProcessorNames =
				beanFactory.getBeanNamesForType(BeanFactoryPostProcessor.class, true, false);

		// Separate between BeanFactoryPostProcessors that implement PriorityOrdered,
		// Ordered, and the rest.
		// 保存实现 PriorityOrdered 接口的BeanFactoryPostProcessor类型对象
		List<BeanFactoryPostProcessor> priorityOrderedPostProcessors = new ArrayList<>();
		// 保存实现 Ordered 接口的BeanFactoryPostProcessor类型对象
		List<String> orderedPostProcessorNames = new ArrayList<>();
		// 保存没有实现 PriorityOrdered 和 Ordered 接口的BeanFactoryPostProcessor类型对象
		List<String> nonOrderedPostProcessorNames = new ArrayList<>();
		for (String ppName : postProcessorNames) {
			if (processedBeans.contains(ppName)) {
				// skip - already processed in first phase above
			} else if (beanFactory.isTypeMatch(ppName, PriorityOrdered.class)) {    // 匹配 PriorityOrdered 类型的BeanFactoryPostProcessor对象
				priorityOrderedPostProcessors.add(beanFactory.getBean(ppName, BeanFactoryPostProcessor.class));
			} else if (beanFactory.isTypeMatch(ppName, Ordered.class)) {    // 匹配 Ordered 类型的BeanFactoryPostProcessor对象
				orderedPostProcessorNames.add(ppName);
			} else {    // 匹配既不是 PriorityOrdered 类型和 Ordered 类型的 BeanFactoryPostProcessor对象
				nonOrderedPostProcessorNames.add(ppName);
			}
		}

		// First, invoke the BeanFactoryPostProcessors that implement PriorityOrdered.
		// 首先调用实现了 PriorityOrdered 接口的BeanFactoryPostProcessors对象

		// 排序
		sortPostProcessors(priorityOrderedPostProcessors, beanFactory);
		// 遍历调用 BeanFactoryPostProcessor的postProcessBeanFactory方法
		invokeBeanFactoryPostProcessors(priorityOrderedPostProcessors, beanFactory);

		// Next, invoke the BeanFactoryPostProcessors that implement Ordered.
		// 然后调用实现了 Ordered 接口的BeanFactoryPostProcessors对象
		List<BeanFactoryPostProcessor> orderedPostProcessors = new ArrayList<>();
		for (String postProcessorName : orderedPostProcessorNames) {
			orderedPostProcessors.add(beanFactory.getBean(postProcessorName, BeanFactoryPostProcessor.class));
		}
		// 排序
		sortPostProcessors(orderedPostProcessors, beanFactory);
		// 遍历调用 BeanFactoryPostProcessor的postProcessBeanFactory方法
		invokeBeanFactoryPostProcessors(orderedPostProcessors, beanFactory);

		// Finally, invoke all other BeanFactoryPostProcessors.
		// 最后处理其他的BeanFactoryPostProcessors对象（既没有实现PriorityOrdered接口和Ordered接口）
		List<BeanFactoryPostProcessor> nonOrderedPostProcessors = new ArrayList<>();
		for (String postProcessorName : nonOrderedPostProcessorNames) {
			nonOrderedPostProcessors.add(beanFactory.getBean(postProcessorName, BeanFactoryPostProcessor.class));
		}
		// 遍历调用 BeanFactoryPostProcessor的postProcessBeanFactory方法
		invokeBeanFactoryPostProcessors(nonOrderedPostProcessors, beanFactory);

		// Clear cached merged bean definitions since the post-processors might have
		// modified the original metadata, e.g. replacing placeholders in values...
		// 翻译：清除缓存的合并bean定义，因为后处理器可能有修改原始元数据，例如替换值中的占位符…
		beanFactory.clearMetadataCache();
	}

	/**
	 * 注册 {@link BeanPostProcessor}，包括用户自定义的和spring内置的 <br>
	 *
	 * @param beanFactory
	 * @param applicationContext
	 */
	public static void registerBeanPostProcessors(
			ConfigurableListableBeanFactory beanFactory, AbstractApplicationContext applicationContext) {

		/*
			获取BeanPostProcessor实现类，包括自定义的和spring内置的
			spring内置的bean后置处理器在AnnotationConfigUtils.registerAnnotationConfigProcessors()中被加入
		*/
		String[] postProcessorNames = beanFactory.getBeanNamesForType(BeanPostProcessor.class, true, false);

		// Register BeanPostProcessorChecker that logs an info message when
		// a bean is created during BeanPostProcessor instantiation, i.e. when
		// a bean is not eligible for getting processed by all BeanPostProcessors.
		/*
		 	将将要注册的以及已经注册了的后置处理器数量进行累加
		 	+1代表即将要加入的BeanPostProcessorChecker后置处理器，或者是方法末尾的加入的ApplicationListenerDetector后置处理器
		 */
		int beanProcessorTargetCount = beanFactory.getBeanPostProcessorCount() + 1 + postProcessorNames.length;
		// 添加BeanPostProcessorChecker后置处理器用来记录其他bean后置处理不能处理时则打印相关信息
		beanFactory.addBeanPostProcessor(new BeanPostProcessorChecker(beanFactory, beanProcessorTargetCount));

		// Separate between BeanPostProcessors that implement PriorityOrdered,
		// Ordered, and the rest.
		// 保存实现了PriorityOrdered接口的bean后置处理器
		List<BeanPostProcessor> priorityOrderedPostProcessors = new ArrayList<>();
		// 保存spring内置的bean后置处理器，可能其他集合的后置处理器存在重复
		List<BeanPostProcessor> internalPostProcessors = new ArrayList<>();
		// 保存实现了Ordered接口的bean后置处理器的beanName，后续根据该名字获取相应的bean后置处理器并注册
		List<String> orderedPostProcessorNames = new ArrayList<>();
		// 保存既没有实现PriorityOrdered接口和Ordered接口的bean后置处理器的beanName，后续根据该名字获取相应的bean后置处理器并注册
		List<String> nonOrderedPostProcessorNames = new ArrayList<>();
		for (String ppName : postProcessorNames) {
			// 匹配实现了priorityOrderedPostProcessors接口的priorityOrderedPostProcessors后置处理器
			if (beanFactory.isTypeMatch(ppName, PriorityOrdered.class)) {
				BeanPostProcessor pp = beanFactory.getBean(ppName, BeanPostProcessor.class);
				priorityOrderedPostProcessors.add(pp);
				if (pp instanceof MergedBeanDefinitionPostProcessor) {
					internalPostProcessors.add(pp);
				}
				// 匹配实现了Ordered接口的bean后置处理器
			} else if (beanFactory.isTypeMatch(ppName, Ordered.class)) {
				orderedPostProcessorNames.add(ppName);
			} else {
				nonOrderedPostProcessorNames.add(ppName);
			}
		}

		// First, register the BeanPostProcessors that implement PriorityOrdered.
		// 首先注册实现了 PriorityOrdered 接口的 Bean后置处理器
		sortPostProcessors(priorityOrderedPostProcessors, beanFactory);
		registerBeanPostProcessors(beanFactory, priorityOrderedPostProcessors);

		// Next, register the BeanPostProcessors that implement Ordered.
		// 再注册实现了 Ordered 接口的 Bean后置处理器
		List<BeanPostProcessor> orderedPostProcessors = new ArrayList<>();
		for (String ppName : orderedPostProcessorNames) {
			BeanPostProcessor pp = beanFactory.getBean(ppName, BeanPostProcessor.class);
			orderedPostProcessors.add(pp);
			if (pp instanceof MergedBeanDefinitionPostProcessor) {
				internalPostProcessors.add(pp);
			}
		}
		sortPostProcessors(orderedPostProcessors, beanFactory);
		registerBeanPostProcessors(beanFactory, orderedPostProcessors);

		// Now, register all regular BeanPostProcessors.
		// 然后注册既没有实现 PriorityOrdered 和 Ordered接口的bean后置处理器
		List<BeanPostProcessor> nonOrderedPostProcessors = new ArrayList<>();
		for (String ppName : nonOrderedPostProcessorNames) {
			BeanPostProcessor pp = beanFactory.getBean(ppName, BeanPostProcessor.class);
			nonOrderedPostProcessors.add(pp);
			if (pp instanceof MergedBeanDefinitionPostProcessor) {
				internalPostProcessors.add(pp);
			}
		}
		registerBeanPostProcessors(beanFactory, nonOrderedPostProcessors);

		// Finally, re-register all internal BeanPostProcessors.
		// 最后注册spring内置的bean后置处理器
		sortPostProcessors(internalPostProcessors, beanFactory);
		registerBeanPostProcessors(beanFactory, internalPostProcessors);

		// Re-register post-processor for detecting inner beans as ApplicationListeners,
		// moving it to the end of the processor chain (for picking up proxies etc).
		// 注册 ApplicationListenerDetector bean后置处理器
		beanFactory.addBeanPostProcessor(new ApplicationListenerDetector(applicationContext));
	}

	private static void sortPostProcessors(List<?> postProcessors, ConfigurableListableBeanFactory beanFactory) {
		Comparator<Object> comparatorToUse = null;
		if (beanFactory instanceof DefaultListableBeanFactory) {
			// 从beanFactory获取依赖比较器
			comparatorToUse = ((DefaultListableBeanFactory) beanFactory).getDependencyComparator();
		}
		// 获取不到则使用默认的
		if (comparatorToUse == null) {
			comparatorToUse = OrderComparator.INSTANCE;
		}
		// 对后置处理器进行培训
		postProcessors.sort(comparatorToUse);
	}

	/**
	 * 遍历执行实现类的 {@link BeanDefinitionRegistryPostProcessor#postProcessBeanDefinitionRegistry}实现类的方法
	 * <p>
	 * Invoke the given BeanDefinitionRegistryPostProcessor beans.
	 */
	private static void invokeBeanDefinitionRegistryPostProcessors(
			Collection<? extends BeanDefinitionRegistryPostProcessor> postProcessors, BeanDefinitionRegistry registry) {

		for (BeanDefinitionRegistryPostProcessor postProcessor : postProcessors) {
			postProcessor.postProcessBeanDefinitionRegistry(registry);
		}
	}

	/**
	 * 遍历调用 {@link BeanFactoryPostProcessor#postProcessBeanFactory(ConfigurableListableBeanFactory)}方法
	 * <p>
	 * Invoke the given BeanFactoryPostProcessor beans.
	 */
	private static void invokeBeanFactoryPostProcessors(
			Collection<? extends BeanFactoryPostProcessor> postProcessors, ConfigurableListableBeanFactory beanFactory) {

		for (BeanFactoryPostProcessor postProcessor : postProcessors) {
			postProcessor.postProcessBeanFactory(beanFactory);
		}
	}

	/**
	 * Register the given BeanPostProcessor beans.
	 */
	private static void registerBeanPostProcessors(
			ConfigurableListableBeanFactory beanFactory, List<BeanPostProcessor> postProcessors) {

		for (BeanPostProcessor postProcessor : postProcessors) {
			beanFactory.addBeanPostProcessor(postProcessor);
		}
	}


	/**
	 * 当spring的配置中的后置处理器还没有被注册就开始bean的初始化，便会打印出该类中设定的信息
	 * <p>
	 * BeanPostProcessor that logs an info message when a bean is created during
	 * BeanPostProcessor instantiation, i.e. when a bean is not eligible for
	 * getting processed by all BeanPostProcessors.
	 */
	private static final class BeanPostProcessorChecker implements BeanPostProcessor {

		private static final Log logger = LogFactory.getLog(BeanPostProcessorChecker.class);

		private final ConfigurableListableBeanFactory beanFactory;

		private final int beanPostProcessorTargetCount;

		public BeanPostProcessorChecker(ConfigurableListableBeanFactory beanFactory, int beanPostProcessorTargetCount) {
			this.beanFactory = beanFactory;
			this.beanPostProcessorTargetCount = beanPostProcessorTargetCount;
		}

		@Override
		public Object postProcessBeforeInitialization(Object bean, String beanName) {
			return bean;
		}

		@Override
		public Object postProcessAfterInitialization(Object bean, String beanName) {
			if (!(bean instanceof BeanPostProcessor) && !isInfrastructureBean(beanName) &&
					this.beanFactory.getBeanPostProcessorCount() < this.beanPostProcessorTargetCount) {
				if (logger.isInfoEnabled()) {
					logger.info("Bean '" + beanName + "' of type [" + bean.getClass().getName() +
							"] is not eligible for getting processed by all BeanPostProcessors " +
							"(for example: not eligible for auto-proxying)");
				}
			}
			return bean;
		}

		private boolean isInfrastructureBean(@Nullable String beanName) {
			if (beanName != null && this.beanFactory.containsBeanDefinition(beanName)) {
				BeanDefinition bd = this.beanFactory.getBeanDefinition(beanName);
				return (bd.getRole() == RootBeanDefinition.ROLE_INFRASTRUCTURE);
			}
			return false;
		}
	}

}
