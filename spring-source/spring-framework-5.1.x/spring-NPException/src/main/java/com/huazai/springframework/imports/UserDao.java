package com.huazai.springframework.imports;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

/**
 * @author pyh
 * @date 2020/3/28 21:07
 */
public class UserDao {
	public void queryUser() {
		System.out.println("query user ....");
	}
}
