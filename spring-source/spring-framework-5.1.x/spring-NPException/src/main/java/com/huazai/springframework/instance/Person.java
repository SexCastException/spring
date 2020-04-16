package com.huazai.springframework.instance;

/**
 * @author pyh
 * @date 2020/4/8 23:42
 */
public class Person {
	private String name;
	private String nickName;
	private int age;

	public Person() {

	}

	public Person(String name, String nickName) {
		this.name = name;
		this.nickName = nickName;
	}

	protected Person(Object object, Object object2) {

	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	@Override
	public String toString() {
		return "Person{" +
				"name='" + name + '\'' +
				", nickName='" + nickName + '\'' +
				", age=" + age +
				'}';
	}
}
