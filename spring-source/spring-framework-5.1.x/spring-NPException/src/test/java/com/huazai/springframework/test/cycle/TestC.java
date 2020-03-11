package com.huazai.springframework.test.cycle;

/**
 * @author pyh
 * @date 2020/3/11 21:25
 */
public class TestC {
	private TestA a;

	public TestC() {}

	public TestC(TestA testA) {
		this.a = testA;
	}

	public void c() {
		a.a();
	}

	public void setA(TestA a) {
		this.a = a;
	}

	public TestA getA() {
		return a;
	}
}
