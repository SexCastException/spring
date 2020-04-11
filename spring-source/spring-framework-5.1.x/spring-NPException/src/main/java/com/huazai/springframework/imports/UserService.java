package com.huazai.springframework.imports;

import org.springframework.context.annotation.Bean;

/**
 * @author pyh
 * @date 2020/4/11 21:40
 */
public class UserService {

	@Bean
	public UserDao getUserDao() {
		return new UserDao();
	}
}
