package com.huazai.springframework.instance;

/**
 * @author pyh
 * @date 2020/4/15 20:57
 */
public class User extends Person {

	private String userName;
	private String password;

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
