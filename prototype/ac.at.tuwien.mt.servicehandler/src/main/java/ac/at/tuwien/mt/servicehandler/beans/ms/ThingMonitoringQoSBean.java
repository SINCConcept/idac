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

import org.apache.camel.Exchange;
import org.apache.camel.Handler;
import org.apache.camel.Header;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.mongodb.MongoClient;

import ac.at.tuwien.mt.dao.monitor.ThingMonitorQoSDAO;
import ac.at.tuwien.mt.dao.monitor.impl.ThingMonitorQoSDAOImpl;
import ac.at.tuwien.mt.model.thing.monitor.MonitoredQoS;
import ac.at.tuwien.mt.model.util.StringUtil;

@Component
public class ThingMonitoringQoSBean {

	private static final Logger LOGGER = Logger.getLogger(ThingMonitoringQoSBean.class);

	private MongoClient mongoClient;

	@Value("${mongo_db_name}")
	private String database;
	@Value("${mongo_db_collection_thing_monitor_qos}")
	private String thingMonitoringCollection;

	@Autowired
	public ThingMonitoringQoSBean(MongoClient mongoClient) {
		this.mongoClient = mongoClient;
	}

	@Handler
	public void process(@Header("thingid") String thingId, Exchange exchange) throws Exception {
		LOGGER.info("Requesting monitoring information about the thingid: " + thingId);

		if (StringUtil.isNullOrBlank(thingId)) {
			exchange.getOut().setHeader(Exchange.HTTP_RESPONSE_CODE, Response.Status.BAD_REQUEST.getStatusCode());
			exchange.getOut().setBody(Response.Status.BAD_REQUEST.getReasonPhrase());
		}

		ThingMonitorQoSDAO mDAO = new ThingMonitorQoSDAOImpl(mongoClient, database, thingMonitoringCollection);
		MonitoredQoS monitoredQoS = mDAO.find(thingId);

		if (monitoredQoS == null) {
			exchange.getOut().setHeader(Exchange.HTTP_RESPONSE_CODE, Response.Status.NOT_FOUND.getStatusCode());
			exchange.getOut().setBody(Response.Status.NOT_FOUND.getReasonPhrase());
		} else {
			// return what was found
			exchange.getOut().setHeader(Exchange.HTTP_RESPONSE_CODE, Response.Status.OK.getStatusCode());
			exchange.getOut().setBody(monitoredQoS);
		}
	}

}
