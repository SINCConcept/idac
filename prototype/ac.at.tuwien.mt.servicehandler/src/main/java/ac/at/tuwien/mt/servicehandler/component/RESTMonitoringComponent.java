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

import ac.at.tuwien.mt.servicehandler.beans.ms.DataContractMonitoringBean;
import ac.at.tuwien.mt.servicehandler.beans.ms.MSMonitoringBalanceBean;
import ac.at.tuwien.mt.servicehandler.beans.ms.ThingMonitoringQoDBean;
import ac.at.tuwien.mt.servicehandler.beans.ms.ThingMonitoringQoSBean;

@Component
public class RESTMonitoringComponent extends RouteBuilder {

	private static final Logger LOGGER = LogManager.getLogger(RESTMonitoringComponent.class);
	private MSMonitoringBalanceBean msBalanceMonitoringBean;
	private ThingMonitoringQoDBean tMonitoringQoDBean;
	private ThingMonitoringQoSBean tMonitoringQoSBean;
	private DataContractMonitoringBean dcMonitoringBean;

	@Autowired
	public RESTMonitoringComponent(MSMonitoringBalanceBean msBalanceMonitoringBean, ThingMonitoringQoDBean tMonitoringQoDBean, ThingMonitoringQoSBean tMonitoringQoSBean,
			DataContractMonitoringBean dcMonitoringBean) {
		this.msBalanceMonitoringBean = msBalanceMonitoringBean;
		this.tMonitoringQoDBean = tMonitoringQoDBean;
		this.tMonitoringQoSBean = tMonitoringQoSBean;
		this.dcMonitoringBean = dcMonitoringBean;
	}

	@Override
	public void configure() throws Exception {
		LOGGER.info("Setting up the microservice registration component...");

		rest("{{rest.microservice.path}}") // set the path
				.get("{{rest.microservice.monitoring.balance}}") //
				.produces(MediaType.APPLICATION_JSON) // set the producing type
				.to("direct:rest_microservice_monitoring_balance");

		rest("{{rest.microservice.path}}") // set the path
				.get("{{rest.microservice.monitoring}}/{{rest.microservice.monitoring.qod}}/{thingid}") //
				.produces(MediaType.APPLICATION_JSON) // set the producing type
				.to("direct:rest_microservice_monitoring_qod_tid");

		rest("{{rest.microservice.path}}") // set the path
				.get("{{rest.microservice.monitoring}}/{{rest.microservice.monitoring.qos}}/{thingid}") //
				.produces(MediaType.APPLICATION_JSON) // set the producing type
				.to("direct:rest_microservice_monitoring_qos_tid");

		rest("{{rest.microservice.path}}") // set the path
				.get("{{rest.microservice.monitoring}}/{{rest.microservice.monitoring.dc}}/{contractid}") //
				.produces(MediaType.APPLICATION_JSON) // set the producing type
				.to("direct:rest_microservice_monitoring_dc_cid");

		from("direct:rest_microservice_monitoring_dc_cid") // from rest
				.log(LoggingLevel.DEBUG, "Received datacontract microservice request") // log
				.bean(dcMonitoringBean) //
				.end();

		from("direct:rest_microservice_monitoring_qod_tid") // from rest
				.log(LoggingLevel.DEBUG, "Received datacontract microservice request") // log
				.bean(tMonitoringQoDBean) //
				.end();

		from("direct:rest_microservice_monitoring_qos_tid") // from rest
				.log(LoggingLevel.DEBUG, "Received datacontract microservice request") // log
				.bean(tMonitoringQoSBean) //
				.end();

		from("direct:rest_microservice_monitoring_balance") // from rest
				.log(LoggingLevel.DEBUG, "Received datacontract microservice balance request") // log
				.bean(msBalanceMonitoringBean) //
				.end();
	}

}
