package com.huazai.springframework.log;

import org.apache.log4j.Logger;

/**
 * @author pyh
 * @date 2020/3/30 20:03
 */
public class Log {
	public static void main(String[] args) {
		Logger logger = Logger.getLogger(Log.class);
		logger.info("log4j ....");
	}
}
