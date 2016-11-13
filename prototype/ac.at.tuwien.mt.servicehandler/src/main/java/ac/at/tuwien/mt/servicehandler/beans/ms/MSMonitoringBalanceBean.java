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

import java.util.List;

import javax.ws.rs.core.Response;

import org.apache.camel.Exchange;
import org.apache.camel.Handler;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.mongodb.MongoClient;

import ac.at.tuwien.mt.model.microservice.MicroserviceInfo;
import ac.at.tuwien.mt.servicehandler.beans.internal.MSMonitoringBalancer;

@Component
public class MSMonitoringBalanceBean {

	private static final Logger LOGGER = Logger.getLogger(MSMonitoringBalanceBean.class);

	private MongoClient mongoClient;

	@Value("${mongo_db_name}")
	private String database;
	@Value("${mongo_db_collection_thing}")
	private String thingCollection;

	@Autowired
	public MSMonitoringBalanceBean(MongoClient mongoClient) {
		this.mongoClient = mongoClient;
	}

	@Handler
	public void process(Exchange exchange) throws Exception {
		LOGGER.debug("Balancing all components.");

		MSMonitoringBalancer monitoringBalancer = new MSMonitoringBalancer(mongoClient, database, thingCollection);
		List<MicroserviceInfo> balancedMicroservices = monitoringBalancer.balance();

		exchange.getOut().setHeader(Exchange.HTTP_RESPONSE_CODE, Response.Status.OK.getStatusCode());
		exchange.getOut().setBody(balancedMicroservices);
	}

}
