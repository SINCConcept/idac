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

import ac.at.tuwien.mt.dao.thing.RecommendingDAO;
import ac.at.tuwien.mt.dao.thing.impl.RecommendingDAOImpl;
import ac.at.tuwien.mt.model.thing.Thing;
import ac.at.tuwien.mt.model.util.StringUtil;

/**
 * @author Florin Bogdan Balint
 *
 */
@Component
public class ThingTagRecommendationBean {

	private static final Logger LOGGER = LogManager.getLogger(ThingTagRecommendationBean.class);

	private MongoClient mongoClient;

	@Value("${mongo_db_name}")
	private String database;
	@Value("${mongo_db_collection_thing}")
	private String thingCollection;

	@Autowired
	public ThingTagRecommendationBean(MongoClient mongoClient) {
		this.mongoClient = mongoClient;
	}

	@Handler
	public void process(@Header("tag") String tag, Exchange exchange) throws Exception {
		LOGGER.debug("Recommending thing for user: " + tag);

		if (StringUtil.isNullOrBlank(tag)) {
			LOGGER.error("Cannot recommend thing for unknown/empty/NULL user.");
			exchange.getOut().setHeader(Exchange.HTTP_RESPONSE_CODE, Response.Status.NO_CONTENT.getStatusCode());
		}
		// register the user
		RecommendingDAO dao = new RecommendingDAOImpl(mongoClient, database, thingCollection);
		Thing thing = dao.recommendForTag(tag);

		if (thing == null) {
			LOGGER.debug("No thing recommendation found for user: " + tag);
			exchange.getOut().setHeader(Exchange.HTTP_RESPONSE_CODE, Response.Status.NO_CONTENT.getStatusCode());
		} else {
			exchange.getOut().setBody(thing);
		}
	}

}
