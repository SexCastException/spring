package com.huazai.spring.learning.aop;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * @author pyh
 * @date 2020/3/22 12:33
 */
@Configurable
@ComponentScan("com.huazai.spring.learning.aop")
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class AppConfig {
}
