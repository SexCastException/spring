package com.huazai.springframework.imports;

import org.springframework.context.annotation.Bean;

/**
 * @author pyh
 * @date 2020/4/5 17:53
 */
public class ChildBean extends ParentBean {
	private int age;

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	@Bean
	public User getUser() {
		return new User();
	}

	@Override
	public String toString() {
		return "ChildBean{" +
				"age=" + age +
				", name='" + name + '\'' +
				'}';
	}
}
