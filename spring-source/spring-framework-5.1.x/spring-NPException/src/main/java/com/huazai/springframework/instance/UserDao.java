package com.huazai.springframework.instance;

/**
 * @author pyh
 * @date 2020/4/15 20:58
 */
public class UserDao {
	Person person;

	public UserDao() {}

	public UserDao(User user) {
		this.person = user;
	}

	public UserDao(Person person) {
		this.person = person;
	}

	public UserDao(User user, Person person) {

	}

	protected UserDao(int a, int b, String c) {

	}
}
