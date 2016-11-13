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

package ac.at.tuwien.mt.servicehandler.component;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

@Component
public class RESTComponent extends RouteBuilder {

	private static final Logger LOGGER = LogManager.getLogger(RESTComponent.class);

	@Override
	public void configure() throws Exception {
		LOGGER.info("Starting Jetty server...");

		// define the jetty component
		restConfiguration().component("jetty") // component
				.host("{{rest.jetty.host}}") // set the host
				.port("{{rest.jetty.port}}") // set the port
				.bindingMode(RestBindingMode.json) // automatic binding mode
				.dataFormatProperty("prettyPrint", "true"); // format property

		LOGGER.info("Jetty server started succesfully.");
	}

}
