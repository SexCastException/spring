package com.huazai.springframework.test.lookupMethod;

/**
 * @author pyh
 * @date 2020/3/2 22:06
 */
public class Teacher extends User {
	@Override
	public void showMe() {
		System.out.println("I am a teacher");
	}
}
