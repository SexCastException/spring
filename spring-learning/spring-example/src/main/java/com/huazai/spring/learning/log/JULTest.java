package com.huazai.spring.learning.log;

import java.util.logging.Logger;

/**
 * @author pyh
 * @date 2020/3/30 20:31
 */
public class JULTest {
    public static void main(String[] args) {
        Logger jul = Logger.getLogger("JUL");
        jul.info("jul test..");
    }
}
