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

package org.springframework.util.xml;

import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.w3c.dom.CharacterData;
import org.w3c.dom.*;
import org.xml.sax.ContentHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Convenience methods for working with the DOM API,
 * in particular for working with DOM Nodes and DOM Elements.
 *
 * @author Juergen Hoeller
 * @author Rob Harrop
 * @author Costin Leau
 * @author Arjen Poutsma
 * @author Luke Taylor
 * @see org.w3c.dom.Node
 * @see org.w3c.dom.Element
 * @since 1.2
 */
public abstract class DomUtils {

	/**
	 * 检索与任何给定元素名称匹配的给定DOM元素的所有子元素，只查看给定元素的直接子级；
	 * （不深度匹配，即不匹配给定元素节点的子孙节点）<br>
	 * (与DOM API的{@code getElementsByTagName}方法形成对比)。
	 * Retrieves all child elements of the given DOM element that match any of the given element names.
	 * Only looks at the direct child level of the given element; do not go into further depth
	 * (in contrast to the DOM API's {@code getElementsByTagName} method).
	 *
	 * @param ele           the DOM element to analyze
	 * @param childEleNames the child element names to look for
	 * @return a List of child {@code org.w3c.dom.Element} instances
	 * @see org.w3c.dom.Element
	 * @see org.w3c.dom.Element#getElementsByTagName
	 */
	public static List<Element> getChildElementsByTagName(Element ele, String... childEleNames) {
		Assert.notNull(ele, "Element must not be null");
		Assert.notNull(childEleNames, "Element names collection must not be null");
		// 保存childEleNames数组转化集合的结果
		List<String> childEleNameList = Arrays.asList(childEleNames);
		// 获取ele节点的所有子节点
		NodeList nl = ele.getChildNodes();
		// 保存于指定名称相匹配的节点
		List<Element> childEles = new ArrayList<>();
		for (int i = 0; i < nl.getLength(); i++) {
			Node node = nl.item(i);
			// 判断childEleNameList中是否与node节点名字相匹配
			if (node instanceof Element && nodeNameMatch(node, childEleNameList)) {
				childEles.add((Element) node);
			}
		}
		return childEles;
	}

	/**
	 * Retrieves all child elements of the given DOM element that match the given element name.
	 * Only look at the direct child level of the given element; do not go into further depth
	 * (in contrast to the DOM API's {@code getElementsByTagName} method).
	 *
	 * @param ele          the DOM element to analyze
	 * @param childEleName the child element name to look for
	 * @return a List of child {@code org.w3c.dom.Element} instances
	 * @see org.w3c.dom.Element
	 * @see org.w3c.dom.Element#getElementsByTagName
	 */
	public static List<Element> getChildElementsByTagName(Element ele, String childEleName) {
		return getChildElementsByTagName(ele, new String[]{childEleName});
	}

	/**
	 * Utility method that returns the first child element identified by its name.
	 *
	 * @param ele          the DOM element to analyze
	 * @param childEleName the child element name to look for
	 * @return the {@code org.w3c.dom.Element} instance, or {@code null} if none found
	 */
	@Nullable
	public static Element getChildElementByTagName(Element ele, String childEleName) {
		Assert.notNull(ele, "Element must not be null");
		Assert.notNull(childEleName, "Element name must not be null");
		NodeList nl = ele.getChildNodes();
		for (int i = 0; i < nl.getLength(); i++) {
			Node node = nl.item(i);
			if (node instanceof Element && nodeNameMatch(node, childEleName)) {
				return (Element) node;
			}
		}
		return null;
	}

	/**
	 * Utility method that returns the first child element value identified by its name.
	 *
	 * @param ele          the DOM element to analyze
	 * @param childEleName the child element name to look for
	 * @return the extracted text value, or {@code null} if no child element found
	 */
	@Nullable
	public static String getChildElementValueByTagName(Element ele, String childEleName) {
		Element child = getChildElementByTagName(ele, childEleName);
		return (child != null ? getTextValue(child) : null);
	}

	/**
	 * Retrieves all child elements of the given DOM element.
	 *
	 * @param ele the DOM element to analyze
	 * @return a List of child {@code org.w3c.dom.Element} instances
	 */
	public static List<Element> getChildElements(Element ele) {
		Assert.notNull(ele, "Element must not be null");
		NodeList nl = ele.getChildNodes();
		List<Element> childEles = new ArrayList<>();
		for (int i = 0; i < nl.getLength(); i++) {
			Node node = nl.item(i);
			if (node instanceof Element) {
				childEles.add((Element) node);
			}
		}
		return childEles;
	}

	/**
	 * 从给定的DOM元素中提取文本值，忽略XML注释。<br>
	 * 获取valueEle的所有子节点类型CharacterData或EntityReference的文本值，不包含注释节点 <br>
	 * Extracts the text value from the given DOM element, ignoring XML comments.
	 * <p>Appends all CharacterData nodes and EntityReference nodes into a single
	 * String value, excluding Comment nodes. Only exposes actual user-specified
	 * text, no default values of any kind.
	 *
	 * @see CharacterData
	 * @see EntityReference
	 * @see Comment
	 */
	public static String getTextValue(Element valueEle) {
		Assert.notNull(valueEle, "Element must not be null");
		StringBuilder sb = new StringBuilder();
		// 获取并遍历valueEle节点的所有子节点
		NodeList nl = valueEle.getChildNodes();
		for (int i = 0; i < nl.getLength(); i++) {
			Node item = nl.item(i);
			// 获取CharacterData类型和EntityReference类型的文本值，不包含注释文本
			if ((item instanceof CharacterData && !(item instanceof Comment)) || item instanceof EntityReference) {
				sb.append(item.getNodeValue());
			}
		}
		return sb.toString();
	}

	/**
	 * Namespace-aware equals comparison. Returns {@code true} if either
	 * {@link Node#getLocalName} or {@link Node#getNodeName} equals
	 * {@code desiredName}, otherwise returns {@code false}.
	 */
	public static boolean nodeNameEquals(Node node, String desiredName) {
		Assert.notNull(node, "Node must not be null");
		Assert.notNull(desiredName, "Desired name must not be null");
		return nodeNameMatch(node, desiredName);
	}

	/**
	 * Returns a SAX {@code ContentHandler} that transforms callback calls to DOM {@code Node}s.
	 *
	 * @param node the node to publish events to
	 * @return the content handler
	 */
	public static ContentHandler createContentHandler(Node node) {
		return new DomContentHandler(node);
	}

	/**
	 * Matches the given node's name and local name against the given desired name.
	 */
	private static boolean nodeNameMatch(Node node, String desiredName) {
		return (desiredName.equals(node.getNodeName()) || desiredName.equals(node.getLocalName()));
	}

	/**
	 * 从desiredNames集合中判断是否node节点的名字或者node节点本地名字 <br>
	 * Matches the given node's name and local name against the given desired names.
	 */
	private static boolean nodeNameMatch(Node node, Collection<?> desiredNames) {
		return (desiredNames.contains(node.getNodeName()) || desiredNames.contains(node.getLocalName()));
	}

}
