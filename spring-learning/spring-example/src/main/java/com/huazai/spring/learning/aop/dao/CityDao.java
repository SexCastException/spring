package com.huazai.spring.learning.aop.dao;

import com.huazai.spring.learning.aop.MyAspectAnnotation;
import org.springframework.stereotype.Repository;

/**
 * @author pyh
 * @date 2020/3/22 12:46
 */
@Repository("cityDao")
public class CityDao implements Dao{

	public void query(String param) {

		System.out.println("query with param:" + param);
	}

	@MyAspectAnnotation
	public void query() {
		System.out.println("query....");
	}
}
