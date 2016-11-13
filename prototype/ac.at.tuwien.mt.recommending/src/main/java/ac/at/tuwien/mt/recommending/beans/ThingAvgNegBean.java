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
package ac.at.tuwien.mt.recommending.beans;

import javax.ws.rs.core.Response;

import org.apache.camel.Exchange;
import org.apache.camel.Handler;
import org.apache.camel.Header;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.mongodb.MongoClient;

import ac.at.tuwien.mt.dao.datacontract.DataContractTrailDAO;
import ac.at.tuwien.mt.dao.datacontract.impl.DataContractTrailDAOImpl;

/**
 * @author Florin Bogdan Balint
 *
 */
@Component
public class ThingAvgNegBean {

	private static final Logger LOGGER = LogManager.getLogger(ThingAvgNegBean.class);

	private MongoClient mongoClient;

	@Value("${mongo_db_name}")
	private String database;
	@Value("${mongo_db_collection_datacontract}")
	private String dataContractCollection;
	@Value("${mongo_db_collection_datacontract_trail}")
	private String dataContractTrailCollection;

	@Autowired
	public ThingAvgNegBean(MongoClient mongoClient) {
		this.mongoClient = mongoClient;
	}

	@Handler
	public void process(@Header("thingid") String thingid, Exchange exchange) throws Exception {
		LOGGER.debug("Computing average negotiation steps for thing: " + thingid);

		DataContractTrailDAO dao = new DataContractTrailDAOImpl(mongoClient, database, dataContractCollection, dataContractTrailCollection);
		Integer result = dao.getAvgNrOfNegotiationsForThing(thingid);

		if (result == null) {
			LOGGER.debug("Cannot compute average negotiation steps for thing: " + thingid);
			exchange.getOut().setHeader(Exchange.HTTP_RESPONSE_CODE, Response.Status.NO_CONTENT.getStatusCode());
		} else {
			exchange.getOut().setBody(result);
		}
	}

}
