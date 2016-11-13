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
package ac.at.tuwien.mt.datacontract.component;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ac.at.tuwien.mt.datacontract.bean.ShutdownBean;

/**
 * @author Florin Bogdan Balint
 *
 */
@Component
public class RESTPingComponent extends RouteBuilder {

	private static final Logger LOGGER = LogManager.getLogger(RESTPingComponent.class);

	private ShutdownBean shutdownBean;

	@Autowired
	public RESTPingComponent(ShutdownBean shutdownBean) {
		this.shutdownBean = shutdownBean;
	}

	@Override
	public void configure() throws Exception {

		LOGGER.debug("Configuring ping component.");

		rest("") // set the path
				.get("ping") // set the individual
				.produces(MediaType.TEXT_HTML) // set the producing type
				.to("direct:ping");

		from("direct:ping") //
				.log(LoggingLevel.DEBUG, "Received REST request: ping") // log
				.setHeader(Exchange.HTTP_RESPONSE_CODE, constant(Response.Status.NO_CONTENT.getStatusCode())) // response
				.setHeader(Exchange.HTTP_RESPONSE_TEXT, constant(Response.Status.NO_CONTENT.getReasonPhrase())) // response
				.end();

		rest("").get("shutdown").to("direct:shutdown");

		from("direct:shutdown") //
				.setHeader(Exchange.HTTP_RESPONSE_CODE, constant(Response.Status.NO_CONTENT.getStatusCode())) // response
				.setHeader(Exchange.HTTP_RESPONSE_TEXT, constant(Response.Status.NO_CONTENT.getReasonPhrase())) // response
				.bean(shutdownBean).end();

	}
}
