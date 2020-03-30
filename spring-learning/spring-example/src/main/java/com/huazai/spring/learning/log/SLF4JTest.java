package com.huazai.spring.learning.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author pyh
 * @date 2020/3/30 21:21
 */
public class SLF4JTest {
    public static void main(String[] args) {
        Logger logger = LoggerFactory.getLogger(SLF4JTest.class);
        logger.info("slf4j test..");
    }
}
