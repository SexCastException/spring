package com.huazai.springframework.instance;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

/**
 * @author pyh
 * @date 2020/4/8 23:41
 */
@Configuration
@ComponentScan("com.huazai.springframework.instance")
@ImportResource("classpath:instance-applicationContext.xml")
public class InstanceAppConfig {
}
