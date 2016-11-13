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
package ac.at.tuwien.mt.dao.datacontract.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.FindOneAndReplaceOptions;
import com.mongodb.client.model.ReturnDocument;

import ac.at.tuwien.mt.dao.datacontract.DataContractDAO;
import ac.at.tuwien.mt.model.datacontract.DataContract;
import ac.at.tuwien.mt.model.exception.InvalidObjectException;
import ac.at.tuwien.mt.model.exception.ResourceOutOfDateException;
import ac.at.tuwien.mt.model.helper.DefaultIDGenerator;
import ac.at.tuwien.mt.model.thing.Thing;
import ac.at.tuwien.mt.model.thing.ThingId;
import ac.at.tuwien.mt.model.util.StringUtil;

/**
 * @author Florin Bogdan Balint
 *
 */
public class DataContractDAOImpl implements DataContractDAO {

	private static final Logger LOGGER = LogManager.getLogger(DataContractDAOImpl.class);

	private MongoClient mongoClient;
	private String database;
	private String dataContractCollection;

	public DataContractDAOImpl(MongoClient mongoClient, String database, String dataContractCollection) {
		this.mongoClient = mongoClient;
		this.database = database;
		this.dataContractCollection = dataContractCollection;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ac.at.tuwien.mt.dao.datacontract.DataContractDAO#insert(ac.at.tuwien.mt.
	 * model.datacontract.DataContract)
	 */
	@Override
	public DataContract insert(DataContract dataContract) {
		LOGGER.debug("Inserting data contract into DB.");

		// set the necessary fields
		if (StringUtil.isNullOrBlank(dataContract.getDataContractMetaInfo().getContractId())) {
			dataContract.getDataContractMetaInfo().setContractId(DefaultIDGenerator.generateID());
		}
		dataContract.getDataContractMetaInfo().setCreationDate(Calendar.getInstance().getTime());
		dataContract.getDataContractMetaInfo().setRevision(1);

		MongoCollection<Document> collection = mongoClient.getDatabase(database).getCollection(dataContractCollection);
		collection.insertOne(dataContract.getDocument());

		return dataContract;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ac.at.tuwien.mt.dao.datacontract.DataContractDAO#readById(java.lang.
	 * String)
	 */
	@Override
	public DataContract readById(String contractId) {
		Document toSearchFor = new Document();
		toSearchFor.put("dataContractMetaInfo.contractId", contractId);

		MongoCollection<Document> collection = mongoClient.getDatabase(database).getCollection(dataContractCollection);
		FindIterable<Document> found = collection.find(toSearchFor);

		if (found == null) {
			return null;
		}

		// this should return only one data contract based in its id
		for (Document document : found) {
			DataContract dataContract = new DataContract(document);
			return dataContract;
		}

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ac.at.tuwien.mt.dao.datacontract.DataContractDAO#update(ac.at.tuwien.mt.
	 * model.datacontract.DataContract)
	 */
	@Override
	public DataContract update(DataContract dataContract) throws ResourceOutOfDateException, InvalidObjectException {

		if (StringUtil.isNullOrBlank(dataContract.getDataContractMetaInfo().getContractId())) {
			throw new InvalidObjectException("Invalid JSON data!");
		}

		int revision = dataContract.getDataContractMetaInfo().getRevision();

		Document toSearchFor = new Document();
		toSearchFor.put("dataContractMetaInfo.contractId", dataContract.getDataContractMetaInfo().getContractId());
		toSearchFor.put("dataContractMetaInfo.revision", revision);

		dataContract.getDataContractMetaInfo().setRevision(revision + 1);

		FindOneAndReplaceOptions options = new FindOneAndReplaceOptions();
		options.returnDocument(ReturnDocument.AFTER);

		MongoCollection<Document> collection = mongoClient.getDatabase(database).getCollection(dataContractCollection);
		Document updated = collection.findOneAndReplace(toSearchFor, dataContract.getDocument(), options);
		if (updated == null) {
			throw new ResourceOutOfDateException("Resource not found or out of date!");
		}

		DataContract updatedContract = new DataContract(updated);
		return updatedContract;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ac.at.tuwien.mt.dao.datacontract.DataContractDAO#
	 * findProviderConcludedContracts(java.lang.String)
	 */
	@Override
	public List<DataContract> findProviderConcludedContracts(String personId) {
		List<DataContract> list = new ArrayList<DataContract>();
		MongoCollection<Document> collection = mongoClient.getDatabase(database).getCollection(dataContractCollection);

		Document toSearchFor = new Document();
		toSearchFor.put("dataContractMetaInfo.party1Id", personId);
		toSearchFor.put("dataContractMetaInfo.party1Accepted", true);
		toSearchFor.put("dataContractMetaInfo.party2Accepted", true);

		FindIterable<Document> thingsList = collection.find(toSearchFor);

		for (Document document : thingsList) {
			DataContract toAdd = new DataContract(document);
			list.add(toAdd);
		}
		return list;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ac.at.tuwien.mt.dao.datacontract.DataContractDAO#
	 * findProviderOpenContracts(java.lang.String)
	 */
	@Override
	public List<DataContract> findProviderOpenContracts(String personId) {
		List<DataContract> list = new ArrayList<DataContract>();
		MongoCollection<Document> collection = mongoClient.getDatabase(database).getCollection(dataContractCollection);

		Document toSearchFor = new Document();
		toSearchFor.put("dataContractMetaInfo.party1Id", personId);
		toSearchFor.put("dataContractMetaInfo.party1Accepted", false);

		FindIterable<Document> thingsList = collection.find(toSearchFor);

		for (Document document : thingsList) {
			DataContract toAdd = new DataContract(document);
			list.add(toAdd);
		}
		return list;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ac.at.tuwien.mt.dao.datacontract.DataContractDAO#
	 * findBuyerConcludedContracts(java.lang.String)
	 */
	@Override
	public List<DataContract> findBuyerConcludedContracts(String personId) {
		List<DataContract> list = new ArrayList<DataContract>();
		MongoCollection<Document> collection = mongoClient.getDatabase(database).getCollection(dataContractCollection);

		Document toSearchFor = new Document();
		toSearchFor.put("dataContractMetaInfo.party2Id", personId);
		toSearchFor.put("dataContractMetaInfo.party1Accepted", true);
		toSearchFor.put("dataContractMetaInfo.party2Accepted", true);

		FindIterable<Document> thingsList = collection.find(toSearchFor);

		for (Document document : thingsList) {
			DataContract toAdd = new DataContract(document);
			list.add(toAdd);
		}
		return list;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ac.at.tuwien.mt.dao.datacontract.DataContractDAO#findBuyerOpenContracts(
	 * java.lang.String)
	 */
	@Override
	public List<DataContract> findBuyerOpenContracts(String personId) {
		List<DataContract> list = new ArrayList<DataContract>();
		MongoCollection<Document> collection = mongoClient.getDatabase(database).getCollection(dataContractCollection);

		Document toSearchFor = new Document();
		toSearchFor.put("dataContractMetaInfo.party2Id", personId);
		toSearchFor.put("dataContractMetaInfo.party2Accepted", false);

		FindIterable<Document> thingsList = collection.find(toSearchFor);

		for (Document document : thingsList) {
			DataContract toAdd = new DataContract(document);
			list.add(toAdd);
		}
		return list;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ac.at.tuwien.mt.dao.datacontract.DataContractDAO#findDataContracts(java.
	 * lang.String)
	 */
	@Override
	public List<DataContract> findDataContracts(String thingId) {
		LOGGER.debug("Searching for concluded data contracts for specified thingId.");

		DataContract contract = new DataContract();
		contract.getDataContractMetaInfo().setParty1Accepted(true);
		contract.getDataContractMetaInfo().setParty2Accepted(true);
		contract.getDataContractMetaInfo().setActive(true);
		Thing thing = new Thing();
		thing.setThingId(thingId);
		contract.getThingIds().add(new ThingId(thing.getThingId()));

		MongoCollection<Document> collection = mongoClient.getDatabase(database).getCollection(dataContractCollection);
		FindIterable<Document> results = collection.find( //
				Filters.and( //
						Filters.elemMatch("thingIds", Filters.eq("thingId", thingId)), //
						Filters.eq("dataContractMetaInfo.active", true), // has
																			// to
																			// be
																			// active
						Filters.eq("dataContractMetaInfo.party1Accepted", true), // accepted1
						Filters.eq("dataContractMetaInfo.party2Accepted", true)) // accepted2

		);

		List<DataContract> list = new ArrayList<DataContract>();
		for (Document document : results) {
			DataContract toAdd = new DataContract(document);
			list.add(toAdd);
		}
		return list;
	}

	@Override
	public void activateDataContracts() {
		final Date currentDate = Calendar.getInstance().getTime();

		// find all the accounts which need to be activated
		Bson filter = Filters.and( //
				Filters.eq("dataContractMetaInfo.active", false), // has to be
																	// active
				Filters.eq("dataContractMetaInfo.party1Accepted", true), // accepted1
				Filters.eq("dataContractMetaInfo.party2Accepted", true), // accepted
																			// 2
				Filters.lte("pricingModel.subscription.startDate", currentDate), //
				Filters.gte("pricingModel.subscription.endDate", currentDate) //
		);

		MongoCollection<Document> collection = mongoClient.getDatabase(database).getCollection(dataContractCollection);
		FindIterable<Document> results = collection.find(filter);

		// set the active flag and update the contract - in case of error repeat
		for (Document document : results) {
			DataContract dataContract = new DataContract(document);
			while (true) {
				dataContract.getDataContractMetaInfo().setActive(true);
				try {
					update(dataContract);
					break;
				} catch (ResourceOutOfDateException | InvalidObjectException e) {
					// in case of error - reload and retry
					String contractId = dataContract.getDataContractMetaInfo().getContractId();
					dataContract = readById(contractId);
				}
			}
		}
	}

	@Override
	public void deactivateDataContracts() {
		final Date currentDate = Calendar.getInstance().getTime();

		// find all the accounts which need to be activated
		MongoCollection<Document> collection = mongoClient.getDatabase(database).getCollection(dataContractCollection);
		FindIterable<Document> results = collection.find( //
				Filters.and( //
						Filters.eq("dataContractMetaInfo.active", true), // has
																			// to
																			// be
																			// active
						Filters.eq("dataContractMetaInfo.party1Accepted", true), // accepted1
						Filters.eq("dataContractMetaInfo.party2Accepted", true), // accepted
																					// 2
						Filters.lt("pricingModel.subscription.endDate", currentDate) //
				));

		// set the active flag and update the contract - in case of error repeat
		for (Document document : results) {
			DataContract dataContract = new DataContract(document);
			while (true) {
				dataContract.getDataContractMetaInfo().setActive(false);
				dataContract.setMonitoring(false);
				try {
					update(dataContract);
					break;
				} catch (ResourceOutOfDateException | InvalidObjectException e) {
					// in case of error - reload and retry
					String contractId = dataContract.getDataContractMetaInfo().getContractId();
					dataContract = readById(contractId);
				}
			}
		}
	}
}
