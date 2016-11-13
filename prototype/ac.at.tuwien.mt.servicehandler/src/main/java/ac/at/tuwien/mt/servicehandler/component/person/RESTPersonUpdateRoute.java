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

import ac.at.tuwien.mt.model.exception.ObjectNotFoundException;
import ac.at.tuwien.mt.model.exception.ResourceOutOfDateException;
import ac.at.tuwien.mt.model.person.LegalPerson;
import ac.at.tuwien.mt.model.person.NaturalPerson;
import ac.at.tuwien.mt.servicehandler.beans.person.LegalPersonUpdateBean;
import ac.at.tuwien.mt.servicehandler.beans.person.NaturalPersonUpdateBean;

/**
 * @author Florin Bogdan Balint
 *
 */
@Component
public class RESTPersonUpdateRoute extends RouteBuilder {

	private static final Logger LOGGER = LogManager.getLogger(RESTPersonUpdateRoute.class);

	private NaturalPersonUpdateBean naturalPersonUpdateBean;
	private LegalPersonUpdateBean legalPersonUpdateBean;

	@Autowired
	public RESTPersonUpdateRoute(NaturalPersonUpdateBean naturalPersonUpdateBean, LegalPersonUpdateBean legalPersonUpdateBean) {
		this.naturalPersonUpdateBean = naturalPersonUpdateBean;
		this.legalPersonUpdateBean = legalPersonUpdateBean;
	}

	@Override
	public void configure() throws Exception {

		LOGGER.debug("Configuring person update component.");

		// DEFINE BEHAVIOR ON JSON SCHEMA PROBLEMS
		onException(UnrecognizedPropertyException.class) //
				.handled(true) //
				.setHeader(Exchange.HTTP_RESPONSE_CODE, constant(400)) //
				.setHeader(Exchange.CONTENT_TYPE, constant("text/plain")) //
				.setBody() //
				.constant("Invalid json data");

		onException(ObjectNotFoundException.class) //
				.handled(true) //
				.setHeader(Exchange.HTTP_RESPONSE_CODE, constant(404)) //
				.setHeader(Exchange.CONTENT_TYPE, constant("text/plain")) //
				.setBody() //
				.constant("Not Found");

		onException(ResourceOutOfDateException.class) //
				.handled(true) //
				.setHeader(Exchange.HTTP_RESPONSE_CODE, constant(409)) //
				.setHeader(Exchange.CONTENT_TYPE, constant("text/plain")) //
				.setBody() //
				.constant("Conflict");

		rest("{{rest.person.natural.path}}") // set the path
				.post("{{rest.person.update}}") // set the individual
				.produces("application/json") // set the producing type
				.bindingMode(RestBindingMode.json) //
				.type(NaturalPerson.class) //
				.to("direct:natural_person_update");

		rest("{{rest.person.legal.path}}") // set the path
				.post("{{rest.person.update}}") // set the individual
				.produces("application/json") // set the producing type
				.bindingMode(RestBindingMode.json) //
				.type(LegalPerson.class) //
				.to("direct:legal_person_update");

		from("direct:natural_person_update") //
				.log(LoggingLevel.DEBUG, "Received REST request: natural person update") // log
				.setHeader(Exchange.HTTP_RESPONSE_CODE, constant(200)) // response
				.bean(naturalPersonUpdateBean) //
				.end();

		from("direct:legal_person_update") //
				.log(LoggingLevel.DEBUG, "Received REST request: legal person update") // log
				.setHeader(Exchange.HTTP_RESPONSE_CODE, constant(200)) // response
				.bean(legalPersonUpdateBean) //
				.end();

	}
}
