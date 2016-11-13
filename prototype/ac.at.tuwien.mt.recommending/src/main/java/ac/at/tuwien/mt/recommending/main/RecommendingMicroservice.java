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
package ac.at.tuwien.mt.recommending.main;

import org.apache.camel.spring.Main;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import ac.at.tuwien.config.mt.RecommendingConfig;

/**
 * @author Florin Bogdan Balint
 *
 */
public class RecommendingMicroservice extends Main {

	private static final Logger LOGGER = LogManager.getLogger(RecommendingMicroservice.class);

	/**
	 * @param args
	 */
	public static void main(String... args) {
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(RecommendingConfig.class);
		RecommendingMicroservice recommending = new RecommendingMicroservice();
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
