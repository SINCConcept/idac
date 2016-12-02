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

package ac.at.tuwien.mt.recommending.component;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ac.at.tuwien.mt.recommending.beans.RegisterMicroserviceBean;

@Component
public class RESTRegistrationComponent extends RouteBuilder {

	private static final Logger LOGGER = LogManager.getLogger(RESTRegistrationComponent.class);
	private RegisterMicroserviceBean registerMicroserviceBean;

	@Autowired
	public RESTRegistrationComponent(RegisterMicroserviceBean registerMicroserviceBean) {
		this.registerMicroserviceBean = registerMicroserviceBean;
	}

	@Override
	public void configure() throws Exception {
		LOGGER.info("Configuring REST component...");

		// define the jetty component
		rest("") // set the path
				.get("register") // set the individual
				.produces(MediaType.APPLICATION_JSON) // set the producing type
				.to("direct:rest_recommending_register");

		from("direct:rest_recommending_register") // from rest
				.log(LoggingLevel.DEBUG, "Received REST request") // log
				.setHeader(Exchange.HTTP_RESPONSE_CODE, constant(Response.Status.OK.getStatusCode())) //
				.bean(registerMicroserviceBean) //
				.end();
	}

}
