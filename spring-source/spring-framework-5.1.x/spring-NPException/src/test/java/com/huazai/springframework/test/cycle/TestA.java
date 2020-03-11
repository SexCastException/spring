package com.huazai.springframework.test.cycle;

/**
 * @author pyh
 * @date 2020/3/11 21:24
 */
public class TestA {
	private TestB b;

	public TestA() {}

	public TestA(TestB testB) {
		this.b = testB;
	}

	public void a() {
		b.b();
	}

	public void setB(TestB b) {
		this.b = b;
	}

	public TestB getB() {
		return b;
	}
}
