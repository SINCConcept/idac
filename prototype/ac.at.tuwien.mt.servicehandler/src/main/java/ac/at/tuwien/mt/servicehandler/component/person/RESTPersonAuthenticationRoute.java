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

import ac.at.tuwien.mt.model.exception.AuthenticationException;
import ac.at.tuwien.mt.model.exception.ObjectNotFoundException;
import ac.at.tuwien.mt.model.person.LegalPerson;
import ac.at.tuwien.mt.model.person.NaturalPerson;
import ac.at.tuwien.mt.servicehandler.beans.person.LegalPersonAuthenticationBean;
import ac.at.tuwien.mt.servicehandler.beans.person.NaturalPersonAuthenticationBean;
import ac.at.tuwien.mt.servicehandler.beans.person.PersonFinderBean;

/**
 * @author White
 *
 */
@Component
public class RESTPersonAuthenticationRoute extends RouteBuilder {

	private static final Logger LOGGER = LogManager.getLogger(RESTPersonAuthenticationRoute.class);

	private NaturalPersonAuthenticationBean naturalPersonAuthenticationBean;
	private LegalPersonAuthenticationBean legalAuthenticationBean;
	private PersonFinderBean personFinderBean;

	@Autowired
	public RESTPersonAuthenticationRoute(NaturalPersonAuthenticationBean naturalPersonAuthenticationBean, LegalPersonAuthenticationBean legalAuthenticationBean, PersonFinderBean personFinderBean) {
		this.naturalPersonAuthenticationBean = naturalPersonAuthenticationBean;
		this.legalAuthenticationBean = legalAuthenticationBean;
		this.personFinderBean = personFinderBean;
	}

	@Override
	public void configure() throws Exception {

		LOGGER.debug("Configuring person authentication component.");

		// DEFINE BEHAVIOR ON JSON SCHEMA PROBLEMS
		onException(UnrecognizedPropertyException.class) //
				.handled(true) //
				.setHeader(Exchange.HTTP_RESPONSE_CODE, constant(400)) //
				.setHeader(Exchange.CONTENT_TYPE, constant("text/plain")) //
				.setBody() //
				.constant("Bad Request");

		onException(ObjectNotFoundException.class) //
				.handled(true) //
				.setHeader(Exchange.HTTP_RESPONSE_CODE, constant(404)) //
				.setHeader(Exchange.CONTENT_TYPE, constant("text/plain")) //
				.setBody() //
				.constant("Not Found");

		onException(AuthenticationException.class) //
				.handled(true) //
				.setHeader(Exchange.HTTP_RESPONSE_CODE, constant(401)) //
				.setHeader(Exchange.CONTENT_TYPE, constant("text/plain")) //
				.setBody() //
				.constant("Unauthorized");

		rest("{{rest.person.natural.path}}") // set the path
				.post("{{rest.person.authenticate}}") // set the individual
				.produces("application/json") // set the producing type
				.bindingMode(RestBindingMode.json) //
				.type(NaturalPerson.class) //
				.to("direct:natural_person_authentication");

		rest("{{rest.person.legal.path}}") // set the path
				.post("{{rest.person.authenticate}}") // set the individual
				.produces("application/json") // set the producing type
				.bindingMode(RestBindingMode.json) //
				.type(LegalPerson.class) //
				.to("direct:legal_person_authentication");

		rest("{{rest.person.find.path}}") // set the path
				.get("find/{personid}") // set the individual
				.produces("application/json") // set the producing type
				.to("direct:find_person");

		from("direct:natural_person_authentication") //
				.log(LoggingLevel.DEBUG, "Received REST request: natural person authentication") // log
				.setHeader(Exchange.HTTP_RESPONSE_CODE, constant(200)) // response
				.bean(naturalPersonAuthenticationBean) //
				.end();

		from("direct:legal_person_authentication") //
				.log(LoggingLevel.DEBUG, "Received REST request: legal person authentication") // log
				.setHeader(Exchange.HTTP_RESPONSE_CODE, constant(200)) // response
				.bean(legalAuthenticationBean) //
				.end();

		from("direct:find_person") //
				.log(LoggingLevel.DEBUG, "Received REST request: find persons") // log
				.setHeader(Exchange.HTTP_RESPONSE_CODE, constant(200)) // response
				.bean(personFinderBean) //
				.end();

	}
}
