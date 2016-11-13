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
package ac.at.tuwien.mt.servicehandler.beans.ms;

import javax.ws.rs.core.Response;

import org.apache.camel.Body;
import org.apache.camel.Exchange;
import org.apache.camel.Handler;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.mongodb.MongoClient;

import ac.at.tuwien.mt.model.microservice.MicroserviceInfo;
import ac.at.tuwien.mt.model.microservice.MicroserviceType;
import ac.at.tuwien.mt.servicehandler.beans.internal.MSMonitoringBalancer;

@Component
public class MSRegistrationBean {

	private static final Logger LOGGER = Logger.getLogger(MSRegistrationBean.class);

	private MongoClient mongoClient;

	@Value("${mongo_db_name}")
	private String database;
	@Value("${mongo_db_collection_thing}")
	private String thingCollection;

	@Autowired
	public MSRegistrationBean(MongoClient mongoClient) {
		this.mongoClient = mongoClient;
	}

	@Handler
	public void process(Exchange exchange, @Body MicroserviceInfo microserviceInfo) throws Exception {
		LOGGER.info("Registering new microservice. " + microserviceInfo.toString());
		boolean registrationSuccessfull = MSManager.getInstance().registerMicroservice(microserviceInfo);
		if (registrationSuccessfull) {
			exchange.getOut().setHeader(Exchange.HTTP_RESPONSE_CODE, Response.Status.ACCEPTED.getStatusCode());
			exchange.getOut().setBody(microserviceInfo.getDocument());
		} else {
			exchange.getOut().setHeader(Exchange.HTTP_RESPONSE_CODE, Response.Status.CONFLICT.getStatusCode());
			exchange.getOut().setBody(Response.Status.CONFLICT.getReasonPhrase());
		}
		// automatic balancing in case of monitoring microservice
		if (microserviceInfo.getMicroserviceType() == MicroserviceType.MONITORING) {
			LOGGER.info("Received Monitoring Microservice Registration: Automatic Balancing Calibration.");
			MSMonitoringBalancer monitoringBalancer = new MSMonitoringBalancer(mongoClient, database, thingCollection);
			monitoringBalancer.balance();
			LOGGER.info("Automatic Balancing Calibration - Completed!");
		}
	}
}
