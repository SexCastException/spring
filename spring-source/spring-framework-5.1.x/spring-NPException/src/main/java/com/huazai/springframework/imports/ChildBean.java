package com.huazai.springframework.imports;

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

	@Override
	public String toString() {
		return "ChildBean{" +
				"age=" + age +
				", name='" + name + '\'' +
				'}';
	}
}
