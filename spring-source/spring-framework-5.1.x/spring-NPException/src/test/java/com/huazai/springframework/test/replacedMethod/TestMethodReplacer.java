package com.huazai.springframework.test.replacedMethod;

import org.springframework.beans.factory.support.MethodReplacer;

import java.lang.reflect.Method;

/**
 * @author pyh
 * @date 2020/3/2 22:50
 */
public class TestMethodReplacer implements MethodReplacer {
	@Override
	public Object reimplement(Object obj, Method method, Object[] args) throws Throwable {
		System.out.println("我是方法替换者，我替换了原来的方法" + method.getName());
		return null;
	}
}
