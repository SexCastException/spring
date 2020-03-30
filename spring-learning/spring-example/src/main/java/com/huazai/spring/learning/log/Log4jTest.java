package com.huazai.spring.learning.log;

import org.apache.log4j.Logger;

/**
 * @author pyh
 * @date 2020/3/30 20:19
 */
public class Log4jTest {
    public static void main(String[] args) {
        Logger logger = Logger.getLogger(Log4jTest.class);
        logger.info("log4j test...");
    }
}
