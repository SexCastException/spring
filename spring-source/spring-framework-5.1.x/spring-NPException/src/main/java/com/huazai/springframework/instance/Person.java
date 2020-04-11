package com.huazai.springframework.instance;

/**
 * @author pyh
 * @date 2020/4/8 23:42
 */
public class Person {
	private String name;
	private String nickName;
	private int age;

	private static String factoryMethod(int iii) {
		return "hello world";
	}

	public Person() {

	}

	public Person(String name) {
		this.name = name;
	}

	public Person(Object object) {

	}

	public Person(String name, String nickName) {
		this.name = name;
		this.nickName = nickName;
	}

	protected Person(Object object, Object object2) {

	}

	public Person(String name, String nickName, int age) {
		this.name = name;
		this.nickName = nickName;
		this.age = age;
	}

	protected Person(Object object, Object object1, Object object2) {
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
