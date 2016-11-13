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
package ac.at.tuwien.mt.dao.monitor.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.FindOneAndReplaceOptions;
import com.mongodb.client.model.ReturnDocument;

import ac.at.tuwien.mt.dao.monitor.ThingMonitorQoSDAO;
import ac.at.tuwien.mt.model.exception.InvalidObjectException;
import ac.at.tuwien.mt.model.exception.ResourceOutOfDateException;
import ac.at.tuwien.mt.model.thing.monitor.MonitoredQoS;

/**
 * @author Florin Bogdan Balint
 *
 */
public class ThingMonitorQoSDAOImpl implements ThingMonitorQoSDAO {

	private static final Logger LOGGER = LogManager.getLogger(ThingMonitorQoSDAOImpl.class);

	private MongoClient mongoClient;
	private String database;
	private String thingMonitorQoSCollection;

	public ThingMonitorQoSDAOImpl(MongoClient mongoClient, String database, String thingMonitorQoSCollection) {
		this.mongoClient = mongoClient;
		this.database = database;
		this.thingMonitorQoSCollection = thingMonitorQoSCollection;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ac.at.tuwien.mt.dao.monitor.ThingMonitorQoSDAO#find(java.lang.String)
	 */
	@Override
	public MonitoredQoS find(String thingId) {
		LOGGER.debug("Returning the MonitoredQoD for the thingId: " + thingId);
		MongoCollection<Document> collection = mongoClient.getDatabase(database).getCollection(thingMonitorQoSCollection);
		FindIterable<Document> results = collection.find(Filters.eq("thingId", thingId));
		for (Document document : results) {
			MonitoredQoS result = new MonitoredQoS(document);
			return result;
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ac.at.tuwien.mt.dao.monitor.ThingMonitorQoSDAO#insert(ac.at.tuwien.mt.
	 * model.thing.monitor.MonitoredQoS)
	 */
	@Override
	public void insert(MonitoredQoS monitoredQoS) {
		LOGGER.debug("Inserting the MonitoredQoD for the thingId: " + monitoredQoS.getThingId());
		monitoredQoS.setRevision(1);
		MongoCollection<Document> collection = mongoClient.getDatabase(database).getCollection(thingMonitorQoSCollection);
		collection.insertOne(monitoredQoS.getDocument());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ac.at.tuwien.mt.dao.monitor.ThingMonitorQoSDAO#update(ac.at.tuwien.mt.
	 * model.thing.monitor.MonitoredQoS)
	 */
	@Override
	public MonitoredQoS update(MonitoredQoS monitoredQoS) throws ResourceOutOfDateException, InvalidObjectException {

		if (monitoredQoS.getThingId() == null) {
			throw new InvalidObjectException("Invalid JSON data!");
		}

		LOGGER.debug("Returning the MonitoredQoD for the thingId: " + monitoredQoS.getThingId());
		MongoCollection<Document> collection = mongoClient.getDatabase(database).getCollection(thingMonitorQoSCollection);

		long revision = monitoredQoS.getRevision();
		monitoredQoS.setRevision(revision + 1);

		FindOneAndReplaceOptions options = new FindOneAndReplaceOptions();
		options.returnDocument(ReturnDocument.AFTER);

		Document filter = new Document();
		filter.put("thingId", monitoredQoS.getThingId());
		filter.put("revision", revision);

		Document document = collection.findOneAndReplace(filter, monitoredQoS.getDocument(), options);
		if (document == null) {
			throw new ResourceOutOfDateException("Resource not found or out of date.");
		}

		return new MonitoredQoS(document);
	}

}
