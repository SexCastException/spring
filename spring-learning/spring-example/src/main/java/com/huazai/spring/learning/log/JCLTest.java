package com.huazai.spring.learning.log;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author pyh
 * @date 2020/3/30 20:35
 */
public class JCLTest {
    public static void main(String[] args) {
        Log log = LogFactory.getLog(JCLTest.class);
        log.info("JCL Test..");
    }
}
