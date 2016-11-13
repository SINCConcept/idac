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
package ac.at.tuwien.mt.servicehandler.component.person;

import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;

import ac.at.tuwien.mt.model.exception.ResourceOutOfDateException;
import ac.at.tuwien.mt.servicehandler.beans.person.PersonRemovalBean;

/**
 * @author Florin Bogdan Balint
 *
 */
@Component
public class RESTPersonRemovalRoute extends RouteBuilder {

	private static final Logger LOGGER = LogManager.getLogger(RESTPersonRemovalRoute.class);

	private PersonRemovalBean personRemovalBean;

	@Autowired
	public RESTPersonRemovalRoute(PersonRemovalBean personRemovalBean) {
		this.personRemovalBean = personRemovalBean;
	}

	@Override
	public void configure() throws Exception {

		LOGGER.debug("Configuring person removal component.");

		// DEFINE BEHAVIOR ON JSON SCHEMA PROBLEMS
		onException(UnrecognizedPropertyException.class) //
				.handled(true) //
				.setHeader(Exchange.HTTP_RESPONSE_CODE, constant(400)) //
				.setHeader(Exchange.CONTENT_TYPE, constant("text/plain")) //
				.setBody() //
				.constant("Invalid json data");

		onException(ResourceOutOfDateException.class) //
				.handled(true) //
				.setHeader(Exchange.HTTP_RESPONSE_CODE, constant(404)) //
				.setHeader(Exchange.CONTENT_TYPE, constant("text/plain")) //
				.setBody() //
				.constant("Not Found");

		rest("{{rest.person.natural.path}}") // set the path
				.delete("{{rest.person.remove}}/{personid}") //
				.produces("application/json") // set the producing type
				.bindingMode(RestBindingMode.json) //
				.to("direct:person_removal");

		rest("{{rest.person.legal.path}}") // set the path
				.delete("{{rest.person.remove}}/{personid}") //
				.produces("application/json") // set the producing type
				.bindingMode(RestBindingMode.json) //
				.to("direct:person_removal");

		from("direct:person_removal") //
				.log(LoggingLevel.DEBUG, "Received REST request: person removal") // log
				.setHeader(Exchange.HTTP_RESPONSE_CODE, constant(200)) // response
				.bean(personRemovalBean) //
				.end();

	}
}
