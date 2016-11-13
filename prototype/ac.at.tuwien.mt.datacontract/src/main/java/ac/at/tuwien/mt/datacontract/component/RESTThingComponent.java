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

import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;

import ac.at.tuwien.mt.datacontract.bean.ThingCreationBean;
import ac.at.tuwien.mt.datacontract.bean.ThingDeleteBean;
import ac.at.tuwien.mt.datacontract.bean.ThingGetBean;
import ac.at.tuwien.mt.datacontract.bean.ThingNOwnerBean;
import ac.at.tuwien.mt.datacontract.bean.ThingOwnerBean;
import ac.at.tuwien.mt.datacontract.bean.ThingUpdateBean;
import ac.at.tuwien.mt.model.exception.InvalidObjectException;
import ac.at.tuwien.mt.model.exception.ResourceOutOfDateException;
import ac.at.tuwien.mt.model.thing.Thing;

/**
 * @author Florin Bogdan Balint
 *
 */
@Component
public class RESTThingComponent extends RouteBuilder {

	private static final Logger LOGGER = LogManager.getLogger(RESTThingComponent.class);
	private ThingCreationBean thingCreationBean;
	private ThingUpdateBean thingUpdateBean;
	private ThingDeleteBean thingDeleteBean;
	private ThingOwnerBean thingOwnerBean;
	private ThingNOwnerBean thingNOwnerBean;
	private ThingGetBean thingGetBean;

	@Autowired
	public RESTThingComponent(ThingCreationBean thingCreationBean, ThingUpdateBean thingUpdateBean, ThingDeleteBean thingDeleteBean, ThingOwnerBean thingOwnerBean, ThingNOwnerBean thingNOwnerBean,
			ThingGetBean thingGetBean) {
		this.thingCreationBean = thingCreationBean;
		this.thingUpdateBean = thingUpdateBean;
		this.thingDeleteBean = thingDeleteBean;
		this.thingOwnerBean = thingOwnerBean;
		this.thingNOwnerBean = thingNOwnerBean;
		this.thingGetBean = thingGetBean;
	}

	@Override
	public void configure() throws Exception {

		LOGGER.debug("Configuring rest thing component.");

		// DEFINE BEHAVIOR ON JSON SCHEMA PROBLEMS
		onException(UnrecognizedPropertyException.class) //
				.handled(true) //
				.setHeader(Exchange.HTTP_RESPONSE_CODE, constant(400)) //
				.setHeader(Exchange.CONTENT_TYPE, constant("text/plain")) //
				.setBody() //
				.constant("Invalid json data");

		onException(InvalidObjectException.class) //
				.handled(true) //
				.setHeader(Exchange.HTTP_RESPONSE_CODE, constant(400)) //
				.setHeader(Exchange.CONTENT_TYPE, constant("text/plain")) //
				.setBody() //
				.constant("Invalid json data");

		onException(ResourceOutOfDateException.class) //
				.handled(true) //
				.setHeader(Exchange.HTTP_RESPONSE_CODE, constant(409)) //
				.setHeader(Exchange.CONTENT_TYPE, constant("text/plain")) //
				.setBody() //
				.constant("Conflict");

		rest("{{rest.things.path}}") // set the path
				.put("{{rest.things.create}}") // set the individual
				.produces(MediaType.APPLICATION_JSON) // set the producing type
				.bindingMode(RestBindingMode.json) //
				.type(Thing.class) //
				.to("direct:thing_creation");

		rest("{{rest.things.path}}") // set the path
				.post("{{rest.things.update}}") // set the individual
				.produces(MediaType.APPLICATION_JSON) // set the producing type
				.bindingMode(RestBindingMode.json) //
				.type(Thing.class) //
				.to("direct:thing_update");

		rest("{{rest.things.path}}") // set the path
				.get("{thingid}") // set the individual
				.produces(MediaType.APPLICATION_JSON) // set the producing type
				.bindingMode(RestBindingMode.json) //
				.type(Thing.class) //
				.to("direct:thing_get");

		rest("{{rest.things.path}}") // set the path
				.post("{{rest.things.delete}}") //
				.produces(MediaType.APPLICATION_JSON) // set the producing type
				.bindingMode(RestBindingMode.json) //
				.type(Thing.class) //
				.to("direct:thing_delete");

		rest("{{rest.things.path}}") // set the path
				.get("{{rest.things.owner}}/{ownerid}") //
				.produces(MediaType.APPLICATION_JSON) // set the producing type
				.to("direct:thing_owner");

		rest("{{rest.things.path}}") // set the path
				.get("{{rest.things.nowner}}/{ownerid}") //
				.produces(MediaType.APPLICATION_JSON) // set the producing type
				.to("direct:thing_nowner");

		from("direct:thing_creation") //
				.log(LoggingLevel.DEBUG, "Received REST request: thing creation") // log
				.bean(thingCreationBean) //
				.setHeader(Exchange.HTTP_RESPONSE_CODE, constant(200)) // response
				.end();

		from("direct:thing_get") //
				.log(LoggingLevel.DEBUG, "Received REST request: thing creation") // log
				.bean(thingGetBean) //
				.setHeader(Exchange.HTTP_RESPONSE_CODE, constant(200)) // response
				.end();

		from("direct:thing_update") //
				.log(LoggingLevel.DEBUG, "Received REST request: thing update") // log
				.bean(thingUpdateBean) //
				.setHeader(Exchange.HTTP_RESPONSE_CODE, constant(200)) // response
				.end();

		from("direct:thing_delete") //
				.log(LoggingLevel.DEBUG, "Received REST request: thing delete") // log
				.bean(thingDeleteBean) //
				.setHeader(Exchange.HTTP_RESPONSE_CODE, constant(200)) // response
				.end();

		from("direct:thing_owner") //
				.log(LoggingLevel.DEBUG, "Received REST request: thing owner") // log
				.bean(thingOwnerBean) //
				.setHeader(Exchange.HTTP_RESPONSE_CODE, constant(200)) // response
				.end();

		from("direct:thing_nowner") //
				.log(LoggingLevel.DEBUG, "Received REST request: thing nowner") // log
				.bean(thingNOwnerBean) //
				.setHeader(Exchange.HTTP_RESPONSE_CODE, constant(200)) // response
				.end();

	}
}
