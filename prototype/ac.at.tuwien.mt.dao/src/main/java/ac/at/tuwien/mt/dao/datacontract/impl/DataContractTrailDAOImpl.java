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

import ac.at.tuwien.mt.dao.datacontract.DataContractTrailDAO;
import ac.at.tuwien.mt.model.datacontract.DataContract;
import ac.at.tuwien.mt.model.datacontract.DataContractTrail;
import ac.at.tuwien.mt.model.exception.InvalidObjectException;
import ac.at.tuwien.mt.model.exception.ResourceOutOfDateException;
import ac.at.tuwien.mt.model.util.StringUtil;

/**
 * @author Florin Bogdan Balint
 *
 */
public class DataContractTrailDAOImpl implements DataContractTrailDAO {

	private static final Logger LOGGER = LogManager.getLogger(DataContractTrailDAOImpl.class);

	private MongoClient mongoClient;
	private String database;
	private String dataContractCollection;
	private String dataContractTrailCollection;

	public DataContractTrailDAOImpl(MongoClient mongoClient, String database, String dataContractCollection, String dataContractTrailCollection) {
		this.mongoClient = mongoClient;
		this.database = database;
		this.dataContractCollection = dataContractCollection;
		this.dataContractTrailCollection = dataContractTrailCollection;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ac.at.tuwien.mt.dao.datacontract.DataContractTrailDAO#insert(ac.at.tuwien
	 * .mt.model.datacontract.DataContractTrail)
	 */
	@Override
	public DataContractTrail insert(DataContractTrail dataContractTrail) throws InvalidObjectException {
		LOGGER.debug("Inserting data contract into DB.");

		// set the necessary fields
		if (StringUtil.isNullOrBlank(dataContractTrail.getContractId())) {
			throw new InvalidObjectException("Cannot save trail without a contractId!");
		}

		// verify if the object already exists
		DataContractTrail found = find(dataContractTrail.getContractId());
		if (found != null) {
			throw new InvalidObjectException("Cannot save trail with an existent contractId!");
		}

		dataContractTrail.setRevision(1);

		MongoCollection<Document> collection = mongoClient.getDatabase(database).getCollection(dataContractTrailCollection);
		collection.insertOne(dataContractTrail.getDocument());

		return dataContractTrail;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ac.at.tuwien.mt.dao.datacontract.DataContractTrailDAO#find(java.lang.
	 * String)
	 */
	@Override
	public DataContractTrail find(String id) {
		Document personToSearchFor = new Document();
		personToSearchFor.put("contractId", id);

		MongoCollection<Document> collection = mongoClient.getDatabase(database).getCollection(dataContractTrailCollection);
		FindIterable<Document> found = collection.find(personToSearchFor);

		if (found == null) {
			return null;
		}

		// this should return only one data contract based in its id
		for (Document document : found) {
			DataContractTrail dataContractTrail = new DataContractTrail(document);
			return dataContractTrail;
		}

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ac.at.tuwien.mt.dao.datacontract.DataContractTrailDAO#update(ac.at.tuwien
	 * .mt.model.datacontract.DataContractTrail)
	 */
	@Override
	public DataContractTrail update(DataContractTrail dataContractTrail) throws ResourceOutOfDateException, InvalidObjectException {
		if (StringUtil.isNullOrBlank(dataContractTrail.getContractId())) {
			throw new InvalidObjectException("Invalid JSON data!");
		}

		int revision = dataContractTrail.getRevision();

		Document toSearchFor = new Document();
		toSearchFor.put("contractId", dataContractTrail.getContractId());
		toSearchFor.put("revision", revision);

		dataContractTrail.setRevision(revision + 1);

		FindOneAndReplaceOptions options = new FindOneAndReplaceOptions();
		options.returnDocument(ReturnDocument.AFTER);

		MongoCollection<Document> collection = mongoClient.getDatabase(database).getCollection(dataContractTrailCollection);
		Document updated = collection.findOneAndReplace(toSearchFor, dataContractTrail.getDocument(), options);
		if (updated == null) {
			throw new ResourceOutOfDateException("Resource not found or out of date!");
		}

		DataContractTrail updatedDataContractTrail = new DataContractTrail(updated);
		return updatedDataContractTrail;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ac.at.tuwien.mt.dao.datacontract.DataContractTrailDAO#
	 * getAvgNrOfNegotiationsForThing(java.lang.String)
	 */
	@Override
	public Integer getAvgNrOfNegotiationsForThing(String thingId) throws InvalidObjectException {
		if (StringUtil.isNullOrBlank(thingId)) {
			throw new InvalidObjectException("Cannot retrieve anything for NULL or empty object!");
		}

		// get all the dataContracts where the thing has been sold
		MongoCollection<Document> collection = mongoClient.getDatabase(database).getCollection(dataContractCollection);
		FindIterable<Document> dataContracts = collection.find(Filters.elemMatch("thingIds", Filters.eq("thingId", thingId)));

		// get the contract ids
		List<String> contractIds = new ArrayList<String>();
		for (Document document : dataContracts) {
			DataContract contract = new DataContract(document);
			contractIds.add(contract.getDataContractMetaInfo().getContractId());
		}

		// if no contracts are available - we cannot compute anything
		if (contractIds.isEmpty()) {
			return null;
		}

		// for each dataContract get the number of negotiations and compute the
		// average
		int totalNr = 0;
		int sum = 0;
		for (String contractId : contractIds) {
			DataContractTrail dcTrail = find(contractId);
			if (dcTrail == null) {
				continue;
			}
			totalNr++;
			sum += dcTrail.getClausesTrail().size();
		}

		int avgNeg = (int) sum / totalNr;

		if (avgNeg >= 1) {
			return avgNeg;
		}

		return null;
	}

}
