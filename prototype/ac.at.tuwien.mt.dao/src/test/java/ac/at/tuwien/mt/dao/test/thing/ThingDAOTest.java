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
package ac.at.tuwien.mt.dao.test.thing;

import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import ac.at.tuwien.mt.common.test.dao.DAOTest;
import ac.at.tuwien.mt.common.test.dao.MongoDBPropertiesProvider;
import ac.at.tuwien.mt.common.test.dao.MongoDBProperty;
import ac.at.tuwien.mt.dao.thing.ThingDAO;
import ac.at.tuwien.mt.dao.thing.impl.ThingDAOImpl;
import ac.at.tuwien.mt.model.datacontract.clause.Currency;
import ac.at.tuwien.mt.model.datacontract.clause.DataRights;
import ac.at.tuwien.mt.model.datacontract.clause.PricingModel;
import ac.at.tuwien.mt.model.datacontract.clause.QoD;
import ac.at.tuwien.mt.model.exception.InvalidObjectException;
import ac.at.tuwien.mt.model.exception.ResourceOutOfDateException;
import ac.at.tuwien.mt.model.helper.DefaultIDGenerator;
import ac.at.tuwien.mt.model.thing.Thing;
import ac.at.tuwien.mt.model.thing.rating.Rating;

/**
 * @author Florin Bogdan Balint
 *
 */
public class ThingDAOTest extends DAOTest {

	private static final String THING_COLLECTION = MongoDBPropertiesProvider.getString(MongoDBProperty.MONGO_DB_COLLECTION_THING);

	private ThingDAO thingDAO;

	@Before
	public void before() {
		mongoClient.getDatabase(DB_NAME).getCollection(THING_COLLECTION).drop();
		thingDAO = new ThingDAOImpl(mongoClient, DB_NAME, THING_COLLECTION);
	}

	@After
	public void after() {
		mongoClient.getDatabase(DB_NAME).getCollection(THING_COLLECTION).drop();
	}

	@Test
	public void testInsert() {
		Thing thing = getSampleThing();
		Thing inserted = thingDAO.insert(thing);
		Assert.assertNotNull(inserted);
	}

	@Test
	public void testUpdatePositive() {
		Thing thing = getSampleThing();
		Thing inserted = thingDAO.insert(thing);

		inserted.setDataSample("{'temp','22°C'}");

		try {
			Thing updated = thingDAO.update(inserted);
			Assert.assertEquals(2, updated.getRevision().intValue());
			Assert.assertEquals("{'temp','22°C'}", updated.getDataSample());
		} catch (ResourceOutOfDateException | InvalidObjectException e) {
			Assert.fail("Exception caught - but none expected.");
		}
	}

	@Test
	public void testUpdateNegative() {
		Thing thing = getSampleThing();
		Thing inserted = thingDAO.insert(thing);

		inserted.setDataSample("{'temp','22°C'}");

		try {
			thingDAO.update(inserted);
			inserted.setRevision(1);
			thingDAO.update(inserted);
			Assert.fail("Exception expected.");
		} catch (ResourceOutOfDateException | InvalidObjectException e) {
			// negative test - this is expected.
		}
	}

	@Test
	public void testDeletePositive() {
		Thing thing = getSampleThing();
		Thing inserted = thingDAO.insert(thing);

		try {
			thingDAO.delete(inserted);
		} catch (ResourceOutOfDateException | InvalidObjectException e) {
			Assert.fail("Exception caught - but none expected.");
		}

	}

	@Test
	public void testFind() {
		Thing thing = getSampleThing();
		thingDAO.insert(thing);

		List<Thing> findThingsForOwner = thingDAO.findThingsForOwner("owner123");
		Assert.assertEquals(1, findThingsForOwner.size());

		List<Thing> findThingsExceptOwner = thingDAO.findThingsExceptOwner("owner123");
		Assert.assertEquals(0, findThingsExceptOwner.size());
	}

	@Test
	public void testFindById() {
		Thing thing = getSampleThing();
		thingDAO.insert(thing);

		Thing found = thingDAO.getThing(thing.getThingId());
		Assert.assertNotNull(found);
	}

	private Thing getSampleThing() {
		Thing thing = new Thing();
		thing.setResourceId("resource123");
		thing.setOwnerId("owner123");
		thing.setDataSample("{'temp','21°C'}");
		thing.setThingId(DefaultIDGenerator.generateID());

		DataRights dataRights = new DataRights();
		dataRights.setCollection(true);
		dataRights.setCommercialUsage(true);
		dataRights.setDerivation(true);
		dataRights.setReproduction(true);
		thing.setDataRights(dataRights);

		QoD qod = new QoD();
		qod.setAccuracy(new Double(0.9));
		qod.setCompleteness(new Double(0.9));
		qod.setConsistency(new Double(0.9));
		qod.setCurrency(new Double(0.9));
		qod.setTimeliness(new Double(0.9));
		thing.setQod(qod);

		PricingModel pricingModel = new PricingModel();
		pricingModel.setCurrency(Currency.EUR);
		pricingModel.setPrice(new Double(0.01));
		pricingModel.setNumberOfTransactions(1);
		pricingModel.setTransaction(true);
		thing.setPricingModel(pricingModel);

		Rating r1 = new Rating("user123", 5);
		Rating r2 = new Rating("user124", 5);

		thing.getRatings().add(r1);
		thing.getRatings().add(r2);

		thing.getTags().add("#Vienna");
		thing.getTags().add("#Austria");

		return thing;
	}
}
