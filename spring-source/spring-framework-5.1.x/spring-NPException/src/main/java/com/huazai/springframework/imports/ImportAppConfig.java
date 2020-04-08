package com.huazai.springframework.imports;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ImportResource;

/**
 * @author pyh
 * @date 2020/3/28 21:07
 */
@ComponentScan("com.huazai.springframework.imports")
@ImportResource("classpath:applicationContext.xml")
@EnableNPException
public class ImportAppConfig {
}
