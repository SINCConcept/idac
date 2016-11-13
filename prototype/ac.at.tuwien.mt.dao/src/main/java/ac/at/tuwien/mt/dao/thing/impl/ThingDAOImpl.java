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
package ac.at.tuwien.mt.dao.thing.impl;

import java.util.ArrayList;
import java.util.Calendar;
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

import ac.at.tuwien.mt.dao.thing.ThingDAO;
import ac.at.tuwien.mt.model.exception.InvalidObjectException;
import ac.at.tuwien.mt.model.exception.ResourceOutOfDateException;
import ac.at.tuwien.mt.model.helper.DefaultIDGenerator;
import ac.at.tuwien.mt.model.thing.Thing;
import ac.at.tuwien.mt.model.util.StringUtil;

/**
 * @author Florin Bogdan Balint
 *
 */
public class ThingDAOImpl implements ThingDAO {

	private static final Logger LOGGER = LogManager.getLogger(ThingDAOImpl.class);

	private MongoClient mongoClient;
	private String database;
	private String thingCollection;

	public ThingDAOImpl(MongoClient mongoClient, String database, String thingCollection) {
		this.mongoClient = mongoClient;
		this.database = database;
		this.thingCollection = thingCollection;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ac.at.tuwien.mt.datacontract.dao.ThingDAO#insert(ac.at.tuwien.mt.model.
	 * thing.Thing)
	 */
	@Override
	public Thing insert(Thing thing) {
		LOGGER.debug("Inserting thing into DB.");

		// set an ID if it does not exist already.
		if (StringUtil.isNullOrBlank(thing.getThingId())) {
			thing.setThingId(DefaultIDGenerator.generateID());
		}

		// set the necessary fields
		thing.setCreationDate(Calendar.getInstance().getTime());
		thing.setRevision(1);

		MongoCollection<Document> collection = mongoClient.getDatabase(database).getCollection(thingCollection);
		collection.insertOne(thing.getDocument());

		return thing;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ac.at.tuwien.mt.datacontract.dao.ThingDAO#update(ac.at.tuwien.mt.model.
	 * thing.Thing)
	 */
	@Override
	public Thing update(Thing thing) throws ResourceOutOfDateException, InvalidObjectException {

		if (thing.getThingId() == null) {
			throw new InvalidObjectException("Invalid JSON data!");
		}

		Integer revisionToUpdate = thing.getRevision();
		thing.setRevision((revisionToUpdate + 1));

		MongoCollection<Document> collection = mongoClient.getDatabase(database).getCollection(thingCollection);
		Document toSearchFor = new Document();
		toSearchFor.put("thingId", thing.getThingId());
		toSearchFor.put("revision", revisionToUpdate);

		FindOneAndReplaceOptions options = new FindOneAndReplaceOptions();
		options.returnDocument(ReturnDocument.AFTER);

		Document updated = collection.findOneAndReplace(toSearchFor, thing.getDocument(), options);
		if (updated == null) {
			throw new ResourceOutOfDateException("Resource not found or out of date!");
		}

		Thing updatedThing = new Thing(updated);
		return updatedThing;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ac.at.tuwien.mt.datacontract.dao.ThingDAO#delete(ac.at.tuwien.mt.model.
	 * thing.Thing)
	 */
	@Override
	public Thing delete(Thing thing) throws ResourceOutOfDateException {
		MongoCollection<Document> collection = mongoClient.getDatabase(database).getCollection(thingCollection);

		Document toSearchFor = new Document();
		toSearchFor.put("thingId", thing.getThingId());
		toSearchFor.put("revision", thing.getRevision());

		Document findOneAndDelete = collection.findOneAndDelete(toSearchFor);
		if (findOneAndDelete == null) {
			throw new ResourceOutOfDateException("Resource not found or out of date!");
		}

		Thing deletedThing = new Thing(findOneAndDelete);
		return deletedThing;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ac.at.tuwien.mt.dao.thing.ThingDAO#findThingsForOwner(java.lang.String)
	 */
	@Override
	public List<Thing> findThingsForOwner(String ownerId) {
		List<Thing> things = new ArrayList<Thing>();
		MongoCollection<Document> collection = mongoClient.getDatabase(database).getCollection(thingCollection);

		FindIterable<Document> thingsList = collection.find(Filters.eq("ownerId", ownerId));
		for (Document document : thingsList) {
			Thing toAdd = new Thing(document);
			things.add(toAdd);
		}
		return things;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ac.at.tuwien.mt.dao.thing.ThingDAO#findThingsExceptOwner(java.lang.
	 * String)
	 */
	@Override
	public List<Thing> findThingsExceptOwner(String ownerId) {
		List<Thing> things = new ArrayList<Thing>();
		MongoCollection<Document> collection = mongoClient.getDatabase(database).getCollection(thingCollection);

		FindIterable<Document> thingsList = collection.find(Filters.ne("ownerId", ownerId));
		for (Document document : thingsList) {
			Thing toAdd = new Thing(document);
			things.add(toAdd);
		}
		return things;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ac.at.tuwien.mt.dao.thing.ThingDAO#getThing(java.lang.String)
	 */
	@Override
	public Thing getThing(String thingId) {
		LOGGER.debug("Returning Thing for specified id.");
		MongoCollection<Document> collection = mongoClient.getDatabase(database).getCollection(thingCollection);
		FindIterable<Document> results = collection.find(Filters.eq("thingId", thingId));
		for (Document document : results) {
			Thing thing = new Thing(document);
			return thing;
		}
		return null;
	}

}
