package com.huazai.springframework.customtag.xml;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

/**
 * 负责将组件祖册到spring容器
 *
 * @author pyh
 * @date 2020/3/6 20:58
 */
public class MyNamespaceHandler extends NamespaceHandlerSupport {
	@Override
	public void init() {
		registerBeanDefinitionParser("user", new UserBeanDefinitionParser());
	}
}
