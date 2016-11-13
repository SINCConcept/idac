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
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ac.at.tuwien.mt.model.microservice.MicroserviceInfo;
import ac.at.tuwien.mt.servicehandler.beans.ms.MSMonitoringBalanceBean;
import ac.at.tuwien.mt.servicehandler.beans.ms.MSRegistrationBean;

@Component
public class RESTMicroserviceRegistrationComponent extends RouteBuilder {

	private static final Logger LOGGER = LogManager.getLogger(RESTMicroserviceRegistrationComponent.class);
	private MSRegistrationBean msRegistrationBean;
	private MSMonitoringBalanceBean msBalanceMonitoringBean;

	@Autowired
	public RESTMicroserviceRegistrationComponent(MSRegistrationBean msRegistrationBean,
			MSMonitoringBalanceBean msBalanceMonitoringBean) {
		this.msRegistrationBean = msRegistrationBean;
		this.msBalanceMonitoringBean = msBalanceMonitoringBean;
	}

	@Override
	public void configure() throws Exception {
		LOGGER.info("Setting up the microservice registration component...");

		rest("{{rest.microservice.path}}") // set the path
				.post("{{rest.microservice.register}}") // set the individual
				.produces(MediaType.APPLICATION_JSON) // set the producing type
				.bindingMode(RestBindingMode.json) //
				.type(MicroserviceInfo.class) //
				.to("direct:rest_microservice_registration");

		from("direct:rest_microservice_registration") // from rest
				.log(LoggingLevel.DEBUG, "Received microservice registration") // log
				.bean(msRegistrationBean) //
				.to("direct:rest_ms_reg_balance");

		// this should automatically balance all monitoring components upon registration
		from("direct:rest_ms_reg_balance")
				.log(LoggingLevel.DEBUG, "Received microservice registration - balancing monitoring components") // log
				.bean(msBalanceMonitoringBean) //
				.end();
	}

}
