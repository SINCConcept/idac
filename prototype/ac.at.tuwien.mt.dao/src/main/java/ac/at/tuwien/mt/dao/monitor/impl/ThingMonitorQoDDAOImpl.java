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

import ac.at.tuwien.mt.dao.monitor.ThingMonitorQoDDAO;
import ac.at.tuwien.mt.model.exception.InvalidObjectException;
import ac.at.tuwien.mt.model.exception.ResourceOutOfDateException;
import ac.at.tuwien.mt.model.thing.monitor.MonitoredQoD;

/**
 * @author Florin Bogdan Balint
 *
 */
public class ThingMonitorQoDDAOImpl implements ThingMonitorQoDDAO {

	private static final Logger LOGGER = LogManager.getLogger(ThingMonitorQoDDAOImpl.class);

	private MongoClient mongoClient;
	private String database;
	private String thingMonitorQoDCollection;

	public ThingMonitorQoDDAOImpl(MongoClient mongoClient, String database, String thingMonitorQoDCollection) {
		this.mongoClient = mongoClient;
		this.database = database;
		this.thingMonitorQoDCollection = thingMonitorQoDCollection;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ac.at.tuwien.mt.dao.monitor.ThingMonitorQoDDAO#find(java.lang.String)
	 */
	@Override
	public MonitoredQoD find(String thingId) {
		LOGGER.debug("Returning the MonitoredQoD for the thingId: " + thingId);
		MongoCollection<Document> collection = mongoClient.getDatabase(database).getCollection(thingMonitorQoDCollection);
		FindIterable<Document> results = collection.find(Filters.eq("thingId", thingId));
		for (Document document : results) {
			MonitoredQoD result = new MonitoredQoD(document);
			return result;
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ac.at.tuwien.mt.dao.monitor.ThingMonitorQoDDAO#insert(ac.at.tuwien.mt.
	 * model.thing.monitor.MonitoredQoD)
	 */
	@Override
	public void insert(MonitoredQoD monitoredQoD) {
		LOGGER.debug("Inserting the MonitoredQoD for the thingId: " + monitoredQoD.getThingId());
		monitoredQoD.setRevision(1);
		MongoCollection<Document> collection = mongoClient.getDatabase(database).getCollection(thingMonitorQoDCollection);
		collection.insertOne(monitoredQoD.getDocument());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ac.at.tuwien.mt.dao.monitor.ThingMonitorQoDDAO#update(ac.at.tuwien.mt.
	 * model.thing.monitor.MonitoredQoD)
	 */
	@Override
	public MonitoredQoD update(MonitoredQoD monitoredQoD) throws ResourceOutOfDateException, InvalidObjectException {

		if (monitoredQoD.getThingId() == null) {
			throw new InvalidObjectException("Invalid JSON data!");
		}

		LOGGER.debug("Returning the MonitoredQoD for the thingId: " + monitoredQoD.getThingId());
		MongoCollection<Document> collection = mongoClient.getDatabase(database).getCollection(thingMonitorQoDCollection);

		FindOneAndReplaceOptions options = new FindOneAndReplaceOptions();
		options.returnDocument(ReturnDocument.AFTER);

		long revision = monitoredQoD.getRevision();
		monitoredQoD.setRevision(revision + 1);

		Document filter = new Document();
		filter.put("thingId", monitoredQoD.getThingId());
		filter.put("revision", revision);

		Document document = collection.findOneAndReplace(filter, monitoredQoD.getDocument(), options);
		if (document == null) {
			throw new ResourceOutOfDateException("Resource not found or out of date!");
		}

		return new MonitoredQoD(document);
	}

}
