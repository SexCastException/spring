package com.huazai.springframework.customtag.xml;

import com.huazai.springframework.customtag.User;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;

/**
 * @author pyh
 * @date 2020/3/6 20:44
 */
public class UserBeanDefinitionParser extends AbstractSingleBeanDefinitionParser {
	/**
	 * {@link Element} 对应的类
	 *
	 * @param element the {@code Element} that is being parsed
	 * @return
	 */
	@Override
	protected Class<?> getBeanClass(Element element) {
		return User.class;
	}

	/**
	 * 从 {@code element}中解析并提取对应元素，将提取的结果封装到 {@link BeanDefinitionBuilder}对象，
	 * 待完成所有的bean的解析后统一注册到 {@link BeanFactory}中
	 *
	 * @param element the XML element being parsed
	 * @param builder used to define the {@code BeanDefinition}
	 */
	@Override
	protected void doParse(Element element, BeanDefinitionBuilder builder) {
		String userName = element.getAttribute("userName");
		String email = element.getAttribute("email");

		if (StringUtils.hasText(userName)) {
			builder.addPropertyValue("userName", userName);
		}
		if (StringUtils.hasText(email)) {
			builder.addPropertyValue("email", email);
		}
		super.doParse(element, builder);
	}
}
