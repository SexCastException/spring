package com.huazai.springframework.factorybean;

import org.springframework.beans.factory.FactoryBean;

/**
 * @author pyh
 * @date 2020/3/7 23:26
 */
public class CarFactoryBean implements FactoryBean<Car> {
	private Car car;

	@Override
	public Car getObject() throws Exception {
		return car;
	}

	@Override
	public Class<?> getObjectType() {
		return Car.class;
	}

	public void setCar(Car car) {
		this.car = car;
	}

	public Car getCar() {
		return car;
	}
}
