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

import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ac.at.tuwien.mt.model.microservice.monitoring.QueueInfo;
import ac.at.tuwien.mt.monitoring.beans.QueueMonitoringBean;

@Component
public class RESTQueueMonitoringComponent extends RouteBuilder {

	private static final Logger LOGGER = LogManager.getLogger(RESTQueueMonitoringComponent.class);

	private QueueMonitoringBean queueMonitoringBean;

	@Autowired
	public RESTQueueMonitoringComponent(QueueMonitoringBean queueMonitoringBean) {
		this.queueMonitoringBean = queueMonitoringBean;
	}

	@Override
	public void configure() throws Exception {
		LOGGER.info("Configuring monitoring/monitor route...");

		// define the jetty component
		rest("{{rest.monitoring.path}}") // set the path
				.put("{{rest.monitoring.monitor}}") // set the individual
				.bindingMode(RestBindingMode.json) //
				.type(QueueInfo[].class) //
				.to("direct:monitoring_update");

		from("direct:monitoring_update") //
				.log(LoggingLevel.DEBUG, "Received REST request: monitoring update") // log
				.setHeader(Exchange.HTTP_RESPONSE_CODE, constant(200)) // response
				.bean(queueMonitoringBean) //
				.end();

		LOGGER.info("Monitoring/monitor route configured succesfully.");
	}

}
