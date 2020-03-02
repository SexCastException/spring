package com.huazai.springframework.test.lookupMethod;

/**
 * @author pyh
 * @date 2020/3/2 22:07
 */
public abstract class GetBeanTest {
	public void showMe() {
		this.getBean().showMe();
	}

	/**
	 * 此抽象方法通过spring来实现
	 *
	 * @return
	 */
	public abstract User getBean();
}
