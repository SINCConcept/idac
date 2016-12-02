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

package ac.at.tuwien.mt.monitoring.component;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ac.at.tuwien.mt.model.microservice.monitoring.QueueInfo;
import ac.at.tuwien.mt.monitoring.beans.RegisterMicroserviceBean;
import ac.at.tuwien.mt.monitoring.internal.MQPropertiesProvider;
import ac.at.tuwien.mt.monitoring.internal.MQProperty;

@Component
public class RESTRegistrationComponent extends RouteBuilder {

	private static final Logger LOGGER = LogManager.getLogger(RESTRegistrationComponent.class);
	private RegisterMicroserviceBean bean;

	@Autowired
	public RESTRegistrationComponent(RegisterMicroserviceBean bean) {
		this.bean = bean;
	}

	@Override
	public void configure() throws Exception {
		LOGGER.info("Configuring REST component...");
		QueueInfo queueInfo = new QueueInfo();
		String brokerURL = MQPropertiesProvider.getString(MQProperty.AMQ_BROKER_URL);
		queueInfo.setBrokerURL(brokerURL);

		// define the jetty component
		rest("") // set the path
				.get("register") // set the individual
				.produces(MediaType.APPLICATION_JSON) // set the producing type
				.to("direct:rest_monitoring_register");

		// define the jetty component
		rest("{{rest.monitoring.path}}") // set the path
				.get("{{rest.monitoring.brokerurl}}") // set the individual
				.produces(MediaType.APPLICATION_JSON) // set the producing type
				.to("direct:rest_monitoring_borkerurl");

		from("direct:rest_monitoring_register") // from rest
				.log(LoggingLevel.DEBUG, "Received REST request") // log
				.setHeader(Exchange.HTTP_RESPONSE_CODE, constant(Response.Status.OK.getStatusCode())) //
				.bean(bean) //
				.end();

		from("direct:rest_monitoring_borkerurl") // from rest
				.log(LoggingLevel.DEBUG, "Received REST request") // log
				.setHeader(Exchange.HTTP_RESPONSE_CODE, constant(Response.Status.OK.getStatusCode())) //
				.transform().constant(queueInfo) // response
				.end();
	}

}
