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

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.FindOneAndReplaceOptions;
import com.mongodb.client.model.ReturnDocument;

import ac.at.tuwien.mt.model.exception.InvalidObjectException;
import ac.at.tuwien.mt.model.exception.ResourceOutOfDateException;
import ac.at.tuwien.mt.model.thing.monitor.MonitoredDataContract;

/**
 * @author White
 *
 */
public class MonitoredDataContractDAOImpl implements ac.at.tuwien.mt.dao.monitor.MonitoredDataContractDAO {

	private static final Logger LOGGER = LogManager.getLogger(ThingMonitorQoDDAOImpl.class);

	private MongoClient mongoClient;
	private String database;
	private String collectionName;

	public MonitoredDataContractDAOImpl(MongoClient mongoClient, String database, String collectionName) {
		this.mongoClient = mongoClient;
		this.database = database;
		this.collectionName = collectionName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ac.at.tuwien.mt.dao.monitor.MonitoredDataContractDAO#find(java.lang.
	 * String)
	 */
	@Override
	public List<MonitoredDataContract> find(String contractId) {
		List<MonitoredDataContract> resultList = new ArrayList<MonitoredDataContract>();
		LOGGER.debug("Returning the MonitoredDataContract for the contractId: " + contractId);
		MongoCollection<Document> collection = mongoClient.getDatabase(database).getCollection(collectionName);
		FindIterable<Document> results = collection.find(Filters.eq("contractId", contractId));
		for (Document document : results) {
			MonitoredDataContract result = new MonitoredDataContract(document);
			resultList.add(result);
		}
		return resultList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ac.at.tuwien.mt.dao.monitor.MonitoredDataContractDAO#findOpen(java.lang.
	 * String)
	 */
	@Override
	public MonitoredDataContract findOpen(String contractId) {
		LOGGER.debug("Returning the MonitoredDataContract for the contractId: " + contractId);
		MongoCollection<Document> collection = mongoClient.getDatabase(database).getCollection(collectionName);

		Document filter = new Document();
		filter.put("contractId", contractId);
		filter.put("monitoringEnd", null);

		FindIterable<Document> results = collection.find(filter);
		for (Document document : results) {
			MonitoredDataContract result = new MonitoredDataContract(document);
			return result;
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ac.at.tuwien.mt.dao.monitor.MonitoredDataContractDAO#insert(ac.at.tuwien.
	 * mt.model.thing.monitor.MonitoredDataContract)
	 */
	@Override
	public void insert(MonitoredDataContract monitoredDataContract) {
		LOGGER.debug("Inserting the MonitoredDataContract for the contractId: " + monitoredDataContract.getContractId());
		monitoredDataContract.setRevision(1);
		MongoCollection<Document> collection = mongoClient.getDatabase(database).getCollection(collectionName);
		collection.insertOne(monitoredDataContract.getDocument());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ac.at.tuwien.mt.dao.monitor.MonitoredDataContractDAO#update(ac.at.tuwien.
	 * mt.model.thing.monitor.MonitoredDataContract)
	 */
	@Override
	public MonitoredDataContract update(MonitoredDataContract monitoredDataContract) throws ResourceOutOfDateException, InvalidObjectException {
		if (monitoredDataContract.getContractId() == null) {
			throw new InvalidObjectException("Invalid JSON data!");
		}

		LOGGER.debug("Returning the MonitoredQoD for the contractId: " + monitoredDataContract.getContractId());
		MongoCollection<Document> collection = mongoClient.getDatabase(database).getCollection(collectionName);

		FindOneAndReplaceOptions options = new FindOneAndReplaceOptions();
		options.returnDocument(ReturnDocument.AFTER);

		long revision = monitoredDataContract.getRevision();
		monitoredDataContract.setRevision(revision + 1);

		Document filter = new Document();
		filter.put("contractId", monitoredDataContract.getContractId());
		filter.put("revision", revision);
		filter.put("monitoringStart", monitoredDataContract.getMonitoringStart());

		Document document = collection.findOneAndReplace(filter, monitoredDataContract.getDocument(), options);
		if (document == null) {
			throw new ResourceOutOfDateException("Resource not found or out of date!");
		}

		return new MonitoredDataContract(document);
	}

}
