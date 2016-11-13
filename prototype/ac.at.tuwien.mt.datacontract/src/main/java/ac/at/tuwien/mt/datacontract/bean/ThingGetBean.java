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
package ac.at.tuwien.mt.datacontract.bean;

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

import ac.at.tuwien.mt.dao.thing.ThingDAO;
import ac.at.tuwien.mt.dao.thing.impl.ThingDAOImpl;
import ac.at.tuwien.mt.model.thing.Thing;

/**
 * @author Florin Bogdan Balint
 *
 */
@Component
public class ThingGetBean {

	private static final Logger LOGGER = LogManager.getLogger(DataContractCreationBean.class);

	private MongoClient mongoClient;

	@Value("${mongo_db_name}")
	private String database;
	@Value("${mongo_db_collection_thing}")
	private String thingCollection;

	@Autowired
	public ThingGetBean(MongoClient mongoClient) {
		this.mongoClient = mongoClient;
	}

	@Handler
	public void process(@Header("thingid") String thingid, Exchange exchange) throws Exception {
		LOGGER.debug("Updating thing.");

		// register the user
		ThingDAO dao = new ThingDAOImpl(mongoClient, database, thingCollection);
		Thing thing = dao.getThing(thingid);

		if (thing == null) {
			LOGGER.debug("No thing found for thingId: " + thingid);
			exchange.getOut().setHeader(Exchange.HTTP_RESPONSE_CODE, Response.Status.NO_CONTENT.getStatusCode());
		} else {
			exchange.getOut().setBody(thing);
		}
	}

}
