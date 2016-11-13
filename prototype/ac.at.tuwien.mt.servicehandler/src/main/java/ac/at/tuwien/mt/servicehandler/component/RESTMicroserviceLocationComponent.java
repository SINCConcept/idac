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

import javax.ws.rs.core.MediaType;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ac.at.tuwien.mt.servicehandler.beans.ms.MSLocationBean;

@Component
public class RESTMicroserviceLocationComponent extends RouteBuilder {

	private static final Logger LOGGER = LogManager.getLogger(RESTMicroserviceLocationComponent.class);
	private MSLocationBean locationBean;

	@Autowired
	public RESTMicroserviceLocationComponent(MSLocationBean locationBean) {
		this.locationBean = locationBean;
	}

	@Override
	public void configure() throws Exception {
		LOGGER.info("Setting up the microservice location component...");

		rest("{{rest.microservice.path}}") // set the path
				.get("{{rest.microservice.location}}/{microservice}") //
				.produces(MediaType.APPLICATION_JSON) // set the producing type
				.to("direct:rest_microservice_location");

		from("direct:rest_microservice_location") // from rest
				.log(LoggingLevel.DEBUG, "Received microservice location request") // log
				.bean(locationBean) //
				.end();
	}

}
