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
package ac.at.tuwien.mt.monitoring.beans;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Handler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

@Component
public class ShutdownBean {

	private static final Logger LOGGER = LogManager.getLogger(ShutdownBean.class);

	@Handler
	public void process(Exchange exchange) throws Exception {
		LOGGER.warn("\n\nShutting down application!\n");

		final CamelContext camelContext = exchange.getContext();

		Thread shutdownThread = new Thread(() -> {
			Thread.currentThread().setName("ShutdownThread");
			try {
				camelContext.stop();
			} catch (Exception e) {
				LOGGER.error("Error during shutdown", e);
			}
		});

		shutdownThread.start();
	}
}
