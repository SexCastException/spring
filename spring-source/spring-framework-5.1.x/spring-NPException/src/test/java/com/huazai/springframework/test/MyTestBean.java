package com.huazai.springframework.test;

/**
 * @author pyh
 * @date 2020/2/28 18:36
 */
public class MyTestBean {
	private String name;
	private int age;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	@Override
	public String toString() {
		return "MyTestBean{" +
				"name='" + name + '\'' +
				", age=" + age +
				'}';
	}
}
