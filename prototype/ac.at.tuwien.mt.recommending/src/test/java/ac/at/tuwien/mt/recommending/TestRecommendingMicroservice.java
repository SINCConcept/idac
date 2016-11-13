/*
 * Copyright (c) 2016. All rights reserved.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS".
 * IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES.
 * 
 * Author: Florin Bogdan Balint
 * 
 */
package ac.at.tuwien.mt.recommending;

import org.apache.camel.spring.Main;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import config.ac.at.tuwien.mt.CommonTestConfig;

/**
 * @author Florin Bogdan Balint
 *
 */
public class TestRecommendingMicroservice extends Main {

	private static final Logger LOGGER = LogManager.getLogger(TestRecommendingMicroservice.class);

	/**
	 * @param args
	 */
	public static void main(String... args) {
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(CommonTestConfig.class);
		TestRecommendingMicroservice recommending = new TestRecommendingMicroservice();
		recommending.setApplicationContext(context);
		recommending.run();
	}

	public void run() {
		try {
			LOGGER.info("Running server!");
			super.run();
		} catch (Exception e) {
			LOGGER.error("Cannot start app with Spring, caused by " + e.getMessage(), e);
		}
	}

}
