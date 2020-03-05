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

package org.springframework.beans.factory.xml;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.parsing.BeanComponentDefinition;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Default implementation of the {@link BeanDefinitionDocumentReader} interface that
 * reads bean definitions according to the "spring-beans" DTD and XSD format
 * (Spring's default XML bean definition format).
 *
 * <p>The structure, elements, and attribute names of the required XML document
 * are hard-coded in this class. (Of course a transform could be run if necessary
 * to produce this format). {@code <beans>} does not need to be the root
 * element of the XML document: this class will parse all bean definition elements
 * in the XML file, regardless of the actual root element.
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @author Rob Harrop
 * @author Erik Wiersma
 * @since 18.12.2003
 */
public class DefaultBeanDefinitionDocumentReader implements BeanDefinitionDocumentReader {

	public static final String BEAN_ELEMENT = BeanDefinitionParserDelegate.BEAN_ELEMENT;

	public static final String NESTED_BEANS_ELEMENT = "beans";

	public static final String ALIAS_ELEMENT = "alias";

	public static final String NAME_ATTRIBUTE = "name";

	public static final String ALIAS_ATTRIBUTE = "alias";

	public static final String IMPORT_ELEMENT = "import";

	public static final String RESOURCE_ATTRIBUTE = "resource";

	/**
	 * 对应<beans>标签的profile属性名
	 */
	public static final String PROFILE_ATTRIBUTE = "profile";


	protected final Log logger = LogFactory.getLog(getClass());

	@Nullable
	private XmlReaderContext readerContext;

	@Nullable
	private BeanDefinitionParserDelegate delegate;


	/**
	 * This implementation parses bean definitions according to the "spring-beans" XSD
	 * (or DTD, historically).
	 * <p>Opens a DOM Document; then initializes the default settings
	 * specified at the {@code <beans/>} level; then parses the contained bean definitions.
	 */
	@Override
	public void registerBeanDefinitions(Document doc, XmlReaderContext readerContext) {
		this.readerContext = readerContext;
		// 注册BeanDefinition
		doRegisterBeanDefinitions(doc.getDocumentElement());
	}

	/**
	 * Return the descriptor for the XML resource that this parser works on.
	 */
	protected final XmlReaderContext getReaderContext() {
		Assert.state(this.readerContext != null, "No XmlReaderContext available");
		return this.readerContext;
	}

	/**
	 * Invoke the {@link org.springframework.beans.factory.parsing.SourceExtractor}
	 * to pull the source metadata from the supplied {@link Element}.
	 */
	@Nullable
	protected Object extractSource(Element ele) {
		return getReaderContext().extractSource(ele);
	}


	/**
	 * 在给定的根{@code <beans/>}元素中注册每个bean definition
	 * Register each bean definition within the given root {@code <beans/>} element.
	 *
	 * @param root 文档根节点
	 */
	@SuppressWarnings("deprecation")  // for Environment.acceptsProfiles(String...)
	protected void doRegisterBeanDefinitions(Element root) {
		// Any nested <beans> elements will cause recursion in this method. In
		// order to propagate and preserve <beans> default-* attributes correctly,
		// keep track of the current (parent) delegate, which may be null. Create
		// the new (child) delegate with a reference to the parent for fallback purposes,
		// then ultimately reset this.delegate back to its original (parent) reference.
		// this behavior emulates a stack of delegates without actually necessitating one.
		/*
			翻译：任何嵌套的<beans>元素都会在这个方法中引起递归。在
			为了正确地传播和保存<beans> default-*属性，
			跟踪当前(父)委托，可能为空。创建
			新的(子)委托，带有对父类的引用，用于回退，
			然后最终将this.delegate重置为其原始(父)引用。
			这种行为模拟了一堆委托，但实际上并不需要一个。
		 */
		BeanDefinitionParserDelegate parent = this.delegate;
		this.delegate = createDelegate(getReaderContext(), root, parent);

		if (this.delegate.isDefaultNamespace(root)) {
			// 获取<beans>标签的profile属性值
			String profileSpec = root.getAttribute(PROFILE_ATTRIBUTE);
			if (StringUtils.hasText(profileSpec)) {
				// 分隔beans标签的profile复合属性值，分隔符“,;”
				String[] specifiedProfiles = StringUtils.tokenizeToStringArray(
						profileSpec, BeanDefinitionParserDelegate.MULTI_VALUE_ATTRIBUTE_DELIMITERS);
				// We cannot use Profiles.of(...) since profile expressions are not supported
				// in XML config. See SPR-12458 for details.
				if (!getReaderContext().getEnvironment().acceptsProfiles(specifiedProfiles)) {
					if (logger.isDebugEnabled()) {
						logger.debug("Skipped XML bean definition file due to specified profiles [" + profileSpec +
								"] not matching: " + getReaderContext().getResource());
					}
					return;
				}
			}
		}

		// 解析前处理，空实现，留给子类实现，模板方法模式
		preProcessXml(root);
		parseBeanDefinitions(root, this.delegate);
		// 解析后处理，空实现，留给子类实现，模板方法模式
		postProcessXml(root);

		this.delegate = parent;
	}

	protected BeanDefinitionParserDelegate createDelegate(
			XmlReaderContext readerContext, Element root, @Nullable BeanDefinitionParserDelegate parentDelegate) {

		BeanDefinitionParserDelegate delegate = new BeanDefinitionParserDelegate(readerContext);
		delegate.initDefaults(root, parentDelegate);
		return delegate;
	}

	/**
	 * Parse the elements at the root level in the document:
	 * "import", "alias", "bean".
	 *
	 * @param root the DOM root element of the document 文档根元素
	 */
	protected void parseBeanDefinitions(Element root, BeanDefinitionParserDelegate delegate) {
		if (delegate.isDefaultNamespace(root)) {
			NodeList nl = root.getChildNodes();
			// 遍历子节点
			for (int i = 0; i < nl.getLength(); i++) {
				Node node = nl.item(i);
				// 只处理元素节点
				if (node instanceof Element) {
					Element ele = (Element) node;
					if (delegate.isDefaultNamespace(ele)) {
						// 处理根节点内的自定义标签
						parseDefaultElement(ele, delegate);
					} else {
						// 解析根节点内的自定义标签
						delegate.parseCustomElement(ele);
					}
				}
			}
		} else {    // 解析根节点为自定义标签
			delegate.parseCustomElement(root);
		}
	}

	/**
	 * 解析默认标签
	 *
	 * @param ele
	 * @param delegate
	 */
	private void parseDefaultElement(Element ele, BeanDefinitionParserDelegate delegate) {
		if (delegate.nodeNameEquals(ele, IMPORT_ELEMENT)) {    // 处理import标签
			importBeanDefinitionResource(ele);
		} else if (delegate.nodeNameEquals(ele, ALIAS_ELEMENT)) {    // 处理alias标签
			processAliasRegistration(ele);
		} else if (delegate.nodeNameEquals(ele, BEAN_ELEMENT)) {    // 处理bean标签
			processBeanDefinition(ele, delegate);
		} else if (delegate.nodeNameEquals(ele, NESTED_BEANS_ELEMENT)) {    // 递归处理嵌入式beans标签
			// recurse
			doRegisterBeanDefinitions(ele);
		}
	}

	/**
	 * 解析一个 {@code <import>}元素并从给定的资源到bean工厂加载 {@link BeanDefinition}。<br>
	 * 步骤如下：<br>
	 * (1)获取resource属性所表示的路径。<br>
	 * (2)解析路径中的系统属性，格式如“${user.dir}"。<br>
	 * (3)判定location是绝对路径还是相对路径。<br>
	 * (4)如果是绝对路径则递归调用bean的解析过程，进行另一次的解析。<br>
	 * (5)如果是相对路径则计算出绝对路径并进行解析。<br>
	 * (6)通知监听器，解析完成。<br>
	 * Parse an "import" element and load the bean definitions
	 * from the given resource into the bean factory.
	 *
	 * @param ele import标签对象
	 */
	protected void importBeanDefinitionResource(Element ele) {
		// 解析impor标签的resource属性值
		String location = ele.getAttribute(RESOURCE_ATTRIBUTE);
		if (!StringUtils.hasText(location)) {    // 如果resource属性没有指定则结束方法解析
			getReaderContext().error("Resource location must not be empty", ele);
			return;
		}

		// Resolve system properties: e.g. "${user.dir}"
		// 解析系统属性，比如："${user.dir}"
		location = getReaderContext().getEnvironment().resolveRequiredPlaceholders(location);

		Set<Resource> actualResources = new LinkedHashSet<>(4);

		// Discover whether the location is an absolute or relative URI
		// 判定location是绝对URI还是相对URI
		boolean absoluteLocation = false;
		try {
			absoluteLocation = ResourcePatternUtils.isUrl(location) || ResourceUtils.toURI(location).isAbsolute();
		} catch (URISyntaxException ex) {
			// cannot convert to an URI, considering the location relative
			// unless it is the well-known Spring prefix "classpath*:"
			/*
				考虑到位置相关，无法转换为URI，除非它是众所周知的Spring前缀“classpath*:”
			 */
		}

		// Absolute or relative?
		// 如果是绝对URI则直接根据地址加载对应的配置文件
		if (absoluteLocation) {
			try {
				int importCount = getReaderContext().getReader().loadBeanDefinitions(location, actualResources);
				if (logger.isTraceEnabled()) {
					logger.trace("Imported " + importCount + " bean definitions from URL location [" + location + "]");
				}
			} catch (BeanDefinitionStoreException ex) {
				getReaderContext().error(
						"Failed to import bean definitions from URL location [" + location + "]", ele, ex);
			}
		} else {    // 如果是相对地址则根据相对地址计算出绝对地址
			// No URL -> considering resource location as relative to the current file.
			try {
				int importCount;
				/*
					Resource存在多个子实现类，如VfsResource、FileSystemResource 等，
					而每个resource的createRelative方式实现都不一样，所以这里先使用子类的方法
				 */
				Resource relativeResource = getReaderContext().getResource().createRelative(location);
				if (relativeResource.exists()) {
					importCount = getReaderContext().getReader().loadBeanDefinitions(relativeResource);
					actualResources.add(relativeResource);
				} else {	// 如果解析不成功，则使用默认的解析器ResourcePatternResolver进行解析
					String baseLocation = getReaderContext().getResource().getURL().toString();
					importCount = getReaderContext().getReader().loadBeanDefinitions(
							StringUtils.applyRelativePath(baseLocation, location), actualResources);
				}
				if (logger.isTraceEnabled()) {
					logger.trace("Imported " + importCount + " bean definitions from relative location [" + location + "]");
				}
			} catch (IOException ex) {
				getReaderContext().error("Failed to resolve current resource location", ele, ex);
			} catch (BeanDefinitionStoreException ex) {
				getReaderContext().error(
						"Failed to import bean definitions from relative location [" + location + "]", ele, ex);
			}
		}
		Resource[] actResArray = actualResources.toArray(new Resource[0]);
		// 解析后进行监听器激活处理
		getReaderContext().fireImportProcessed(location, actResArray, extractSource(ele));
	}

	/**
	 * 处理给定的别名元素，向注册中心注册别名。<br>
	 * Process the given alias element, registering the alias with the registry.
	 *
	 * @param ele alias标签兑现
	 */
	protected void processAliasRegistration(Element ele) {
		// 获取alias标签的name属性值
		String name = ele.getAttribute(NAME_ATTRIBUTE);
		// 获取alias标签的alias属性值
		String alias = ele.getAttribute(ALIAS_ATTRIBUTE);
		// alias标签的属性至少有一项没有配置，则为false
		boolean valid = true;
		if (!StringUtils.hasText(name)) {
			getReaderContext().error("Name must not be empty", ele);
			valid = false;
		}
		if (!StringUtils.hasText(alias)) {
			getReaderContext().error("Alias must not be empty", ele);
			valid = false;
		}
		if (valid) {
			try {
				// 通过BeanDefinitionRegister注册别名
				getReaderContext().getRegistry().registerAlias(name, alias);
			} catch (Exception ex) {
				getReaderContext().error("Failed to register alias '" + alias +
						"' for bean with name '" + name + "'", ele, ex);
			}
			getReaderContext().fireAliasRegistered(name, alias, extractSource(ele));
		}
	}

	/**
	 * 处理给定的bean元素，解析beanDefinition并在登记处注册。<br>
	 * <p>
	 * (1)首先委托 {@link BeanDefinitionParserDelegate#parseBeanDefinitionElement(Element)}方法进行默认标签解析，
	 * 返回 {@link BeanDefinitionHolder}类型的实例bdHolder，经过这个方法后，bdHolder 实例已经包含我们配置文件中配置的各种属性了，
	 * 例如class、name、 id、 alias 之类的属性。<br>
	 * (2)当返回的bdHolder不为空的情况下若存在默认标签的子节点下再有自定义属性，还需要再次对自定义标签进行解析。<br>
	 * (3)解析完成后，需要对解析后的bdHolder 进行注册，同样，注册操作委托给了 {@link BeanDefinitionParserDelegate#decorateBeanDefinitionIfRequired}方法。<br>
	 * (4)最后发出响应事件，通知相关的监听器，这个bean已经加载完成了。
	 * <p>
	 * Process the given bean element, parsing the bean definition
	 * and registering it with the registry.
	 *
	 * @param ele      bean元素节点对象
	 * @param delegate
	 */
	protected void processBeanDefinition(Element ele, BeanDefinitionParserDelegate delegate) {
		// (1)、解析默认标签
		BeanDefinitionHolder bdHolder = delegate.parseBeanDefinitionElement(ele);
		if (bdHolder != null) {
			// (2)、解析自定义标签
			bdHolder = delegate.decorateBeanDefinitionIfRequired(ele, bdHolder);
			try {
				// Register the final decorated instance.
				// (3)、注册beanDefinition
				BeanDefinitionReaderUtils.registerBeanDefinition(bdHolder, getReaderContext().getRegistry());
			} catch (BeanDefinitionStoreException ex) {
				getReaderContext().error("Failed to register bean definition with name '" +
						bdHolder.getBeanName() + "'", ele, ex);
			}
			// Send registration event.
			// （4）、发送注册事件
			getReaderContext().fireComponentRegistered(new BeanComponentDefinition(bdHolder));
		}
	}


	/**
	 * Allow the XML to be extensible by processing any custom element types first,
	 * before we start to process the bean definitions. This method is a natural
	 * extension point for any other custom pre-processing of the XML.
	 * <p>The default implementation is empty. Subclasses can override this method to
	 * convert custom elements into standard Spring bean definitions, for example.
	 * Implementors have access to the parser's bean definition reader and the
	 * underlying XML resource, through the corresponding accessors.
	 *
	 * @see #getReaderContext()
	 */
	protected void preProcessXml(Element root) {
	}

	/**
	 * Allow the XML to be extensible by processing any custom element types last,
	 * after we finished processing the bean definitions. This method is a natural
	 * extension point for any other custom post-processing of the XML.
	 * <p>The default implementation is empty. Subclasses can override this method to
	 * convert custom elements into standard Spring bean definitions, for example.
	 * Implementors have access to the parser's bean definition reader and the
	 * underlying XML resource, through the corresponding accessors.
	 *
	 * @see #getReaderContext()
	 */
	protected void postProcessXml(Element root) {
	}

}
