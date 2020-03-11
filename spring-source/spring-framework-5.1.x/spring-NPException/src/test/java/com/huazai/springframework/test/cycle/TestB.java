package com.huazai.springframework.test.cycle;

/**
 * @author pyh
 * @date 2020/3/11 21:24
 */
public class TestB {
	private TestC c;

	public TestB() {}

	public TestB(TestC testC) {
		this.c = testC;
	}

	public void b() {
		c.c();
	}

	public void setC(TestC c) {
		this.c = c;
	}

	public TestC getC() {
		return c;
	}
}
