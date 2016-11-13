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
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import ac.at.tuwien.mt.common.test.dao.DAOTest;
import ac.at.tuwien.mt.common.test.dao.MongoDBPropertiesProvider;
import ac.at.tuwien.mt.common.test.dao.MongoDBProperty;
import ac.at.tuwien.mt.dao.thing.RecommendingDAO;
import ac.at.tuwien.mt.dao.thing.ThingDAO;
import ac.at.tuwien.mt.dao.thing.impl.RecommendingDAOImpl;
import ac.at.tuwien.mt.dao.thing.impl.ThingDAOImpl;
import ac.at.tuwien.mt.model.datacontract.clause.Currency;
import ac.at.tuwien.mt.model.datacontract.clause.DataRights;
import ac.at.tuwien.mt.model.datacontract.clause.PricingModel;
import ac.at.tuwien.mt.model.datacontract.clause.QoD;
import ac.at.tuwien.mt.model.helper.DefaultIDGenerator;
import ac.at.tuwien.mt.model.thing.Thing;
import ac.at.tuwien.mt.model.thing.rating.Rating;

/**
 * @author Florin Bogdan Balint
 *
 */
public class RecommendingDAOTest extends DAOTest {

	private static final Logger LOGGER = LogManager.getLogger(RecommendingDAOTest.class);
	private static final String THING_COLLECTION = MongoDBPropertiesProvider.getString(MongoDBProperty.MONGO_DB_COLLECTION_THING);

	private ThingDAO thingDAO;
	private RecommendingDAO recommendingDAO;

	@Before
	public void before() {
		mongoClient.getDatabase(DB_NAME).getCollection(THING_COLLECTION).drop();
		thingDAO = new ThingDAOImpl(mongoClient, DB_NAME, THING_COLLECTION);
		recommendingDAO = new RecommendingDAOImpl(mongoClient, DB_NAME, THING_COLLECTION);
	}

	@After
	public void after() {
		mongoClient.getDatabase(DB_NAME).getCollection(THING_COLLECTION).drop();
	}

	@Test
	public void recommendTest1() {
		// no things present
		Thing recommended = recommendingDAO.recommend("userf");
		Assert.assertNull(recommended);
	}

	@Test
	public void recommendTest2() {
		Thing thing0 = getSampleThing();
		thingDAO.insert(thing0);

		// only one thing - should be covered by the random

		Thing recommended = recommendingDAO.recommend("userf");
		Assert.assertNotNull(recommended);
		Assert.assertEquals(thing0.getThingId(), recommended.getThingId());
	}

	@Test
	public void recommendTest3() {
		Thing thing0 = getSampleThing();
		Rating t0f = new Rating("userf", 2);
		Rating t0r1 = new Rating("user111", 2);
		thing0.getRatings().add(t0f);
		thing0.getRatings().add(t0r1);
		thingDAO.insert(thing0);

		Thing thing1 = getSampleThing();
		Rating t1f = new Rating("userf", 5);
		Rating t1r1 = new Rating("user111", 5);
		thing1.getRatings().add(t1f);
		thing1.getRatings().add(t1r1);
		thingDAO.insert(thing1);

		Thing thing2 = getSampleThing();
		Rating t2f = new Rating("userf", 3);
		Rating t2r1 = new Rating("user111", 3);
		thing2.getRatings().add(t2f);
		thing2.getRatings().add(t2r1);
		thingDAO.insert(thing2);

		// top rated thing
		Thing recommended = recommendingDAO.recommend("userf123");
		Assert.assertNotNull(recommended);
		Assert.assertEquals(thing1.getThingId(), recommended.getThingId());
	}

	@Test
	public void recommendTest4() {
		Thing thing0 = getSampleThing();
		Rating t0f = new Rating("userf", 5);
		Rating t0r1 = new Rating("user111", 5);
		Rating t0r2 = new Rating("user222", 5);
		Rating t0r3 = new Rating("user333", 4);
		Rating t0r4 = new Rating("user444", 5);
		Rating t0r5 = new Rating("user555", 3);
		thing0.getRatings().add(t0f);
		thing0.getRatings().add(t0r1);
		thing0.getRatings().add(t0r2);
		thing0.getRatings().add(t0r3);
		thing0.getRatings().add(t0r4);
		thing0.getRatings().add(t0r5);
		thingDAO.insert(thing0);

		Thing thing1 = getSampleThing();
		Rating t1f = new Rating("userf", 4);
		Rating t1r1 = new Rating("user111", 5);
		Rating t1r2 = new Rating("user333", 5);
		Rating t1r3 = new Rating("user555", 4);
		Rating t1r4 = new Rating("user444", 5);
		thing1.getRatings().add(t1f);
		thing1.getRatings().add(t1r1);
		thing1.getRatings().add(t1r2);
		thing1.getRatings().add(t1r3);
		thing1.getRatings().add(t1r4);
		thingDAO.insert(thing1);

		Thing thing2 = getSampleThing();
		Rating t2f = new Rating("userf", 4);
		Rating t2r1 = new Rating("user111", 5);
		Rating t2r2 = new Rating("user333", 1);
		Rating t2r3 = new Rating("user555", 4);
		thing2.getRatings().add(t2f);
		thing2.getRatings().add(t2r1);
		thing2.getRatings().add(t2r2);
		thing2.getRatings().add(t2r3);
		thingDAO.insert(thing2);

		// this last item could change everything
		Thing thing3 = getSampleThing();
		Rating t3f = new Rating("user111", 5);
		thing3.getRatings().add(t3f);
		thingDAO.insert(thing3);

		// this last item could change everything
		Thing thing4 = getSampleThing();
		Rating t4f = new Rating("user555", 5);
		thing4.getRatings().add(t4f);
		thingDAO.insert(thing4);

		Thing recommended = recommendingDAO.recommend("userf");
		Assert.assertNotNull(recommended);
		Assert.assertEquals(thing3.getThingId(), recommended.getThingId());
	}

	@Test
	public void getTopRatedThingTest1() {
		Thing topRatedThing = recommendingDAO.getTopRatedThing("userf1");
		Assert.assertNull(topRatedThing);
	}

	@Test
	public void getTopRatedThingTest2() {
		Thing thing0 = getSampleThing();
		thingDAO.insert(thing0);

		Thing thing1 = getSampleThing();
		thingDAO.insert(thing1);

		Thing thing2 = getSampleThing();
		thingDAO.insert(thing2);

		Thing topRatedThing = recommendingDAO.getTopRatedThing("userf1");
		Assert.assertNull(topRatedThing);
	}

	@Test
	public void getTopRatedThingTest3() {
		Thing thing4 = getSampleThing();
		thingDAO.insert(thing4);

		Thing thing0 = getSampleThing();
		Rating t0f = new Rating("userf", 2);
		Rating t0r1 = new Rating("user111", 2);
		thing0.getRatings().add(t0f);
		thing0.getRatings().add(t0r1);
		thingDAO.insert(thing0);

		Thing thing1 = getSampleThing();
		Rating t1f = new Rating("userf", 5);
		Rating t1r1 = new Rating("user111", 5);
		thing1.getRatings().add(t1f);
		thing1.getRatings().add(t1r1);
		thingDAO.insert(thing1);

		Thing thing2 = getSampleThing();
		Rating t2f = new Rating("userf", 3);
		Rating t2r1 = new Rating("user111", 3);
		thing2.getRatings().add(t2f);
		thing2.getRatings().add(t2r1);
		thingDAO.insert(thing2);

		Thing thing3 = getSampleThing();
		thingDAO.insert(thing3);

		Thing topRatedThing = recommendingDAO.getTopRatedThing("userf1");
		Assert.assertNotNull(topRatedThing);
		Assert.assertEquals(thing1.getThingId(), topRatedThing.getThingId());
	}

	@Test
	public void getRandomThingTest1() {
		Thing randomItem = recommendingDAO.getRandomThing("userf1");
		Assert.assertNull(randomItem);
	}

	@Test
	public void getRandomThingTest2() {
		Thing thing0 = getSampleThing();
		thingDAO.insert(thing0);

		Thing randomItem = recommendingDAO.getRandomThing("userf1");
		Assert.assertNotNull(randomItem);
		Assert.assertEquals(thing0.getThingId(), randomItem.getThingId());
	}

	@Test
	public void getRandomThingTest3() {
		Thing thing0 = getSampleThing();
		thingDAO.insert(thing0);

		Thing thing1 = getSampleThing();
		thingDAO.insert(thing1);

		boolean nr1Rec = false;
		boolean nr2Rec = false;

		// check the random function 100 times - each item should be there at
		// least once.
		for (int i = 0; i < 100; i++) {
			Thing randomItem = recommendingDAO.getRandomThing("userf1");
			if (randomItem.getThingId().equals(thing0.getThingId())) {
				nr1Rec = true;
			}
			if (randomItem.getThingId().equals(thing1.getThingId())) {
				nr2Rec = true;
			}
		}
		Assert.assertTrue(nr1Rec);
		Assert.assertTrue(nr2Rec);
	}

	@Test
	public void getAllNeighborButNotUserRatedThingsTest1() {
		Thing thing0 = getSampleThing();
		Rating t0f = new Rating("userf", 5);
		Rating t0r1 = new Rating("user111", 5);
		Rating t0r2 = new Rating("user222", 5);
		Rating t0r3 = new Rating("user333", 4);
		Rating t0r4 = new Rating("user444", 5);
		Rating t0r5 = new Rating("user555", 3);
		thing0.getRatings().add(t0f);
		thing0.getRatings().add(t0r1);
		thing0.getRatings().add(t0r2);
		thing0.getRatings().add(t0r3);
		thing0.getRatings().add(t0r4);
		thing0.getRatings().add(t0r5);
		thingDAO.insert(thing0);

		Thing thing1 = getSampleThing();
		Rating t1f = new Rating("userf", 4);
		Rating t1r1 = new Rating("user111", 5);
		Rating t1r2 = new Rating("user333", 5);
		Rating t1r3 = new Rating("user555", 4);
		Rating t1r4 = new Rating("user444", 5);
		thing1.getRatings().add(t1f);
		thing1.getRatings().add(t1r1);
		thing1.getRatings().add(t1r2);
		thing1.getRatings().add(t1r3);
		thing1.getRatings().add(t1r4);
		thingDAO.insert(thing1);

		Thing thing2 = getSampleThing();
		Rating t2f = new Rating("userf", 4);
		Rating t2r1 = new Rating("user111", 5);
		Rating t2r2 = new Rating("user333", 1);
		Rating t2r3 = new Rating("user555", 4);
		thing2.getRatings().add(t2f);
		thing2.getRatings().add(t2r1);
		thing2.getRatings().add(t2r2);
		thing2.getRatings().add(t2r3);
		thingDAO.insert(thing2);

		Thing thing3 = getSampleThing();
		Rating t3f = new Rating("user111", 5);
		thing3.getRatings().add(t3f);
		thingDAO.insert(thing3);

		List<String> allCommonRatedThings = recommendingDAO.getAllNeighborButNotUserRatedThings("userf", "user111");
		Assert.assertEquals(1, allCommonRatedThings.size());

		allCommonRatedThings = recommendingDAO.getAllNeighborButNotUserRatedThings("user111", "userf");
		Assert.assertEquals(0, allCommonRatedThings.size());
	}

	@Test
	public void getNeighborhoodAndEDistanceTest() {
		Thing thing0 = getSampleThing();
		Rating t0f = new Rating("userf", 5);
		Rating t0r1 = new Rating("user111", 5);
		Rating t0r2 = new Rating("user222", 5);
		Rating t0r3 = new Rating("user333", 4);
		Rating t0r4 = new Rating("user444", 5);
		Rating t0r5 = new Rating("user555", 3);
		thing0.getRatings().add(t0f);
		thing0.getRatings().add(t0r1);
		thing0.getRatings().add(t0r2);
		thing0.getRatings().add(t0r3);
		thing0.getRatings().add(t0r4);
		thing0.getRatings().add(t0r5);
		thingDAO.insert(thing0);

		Thing thing1 = getSampleThing();
		Rating t1f = new Rating("userf", 4);
		Rating t1r1 = new Rating("user111", 5);
		Rating t1r2 = new Rating("user333", 5);
		Rating t1r3 = new Rating("user555", 4);
		Rating t1r4 = new Rating("user444", 5);
		thing1.getRatings().add(t1f);
		thing1.getRatings().add(t1r1);
		thing1.getRatings().add(t1r2);
		thing1.getRatings().add(t1r3);
		thing1.getRatings().add(t1r4);
		thingDAO.insert(thing1);

		Thing thing2 = getSampleThing();
		Rating t2f = new Rating("userf", 4);
		Rating t2r1 = new Rating("user111", 5);
		Rating t2r2 = new Rating("user333", 1);
		Rating t2r3 = new Rating("user555", 4);
		thing2.getRatings().add(t2f);
		thing2.getRatings().add(t2r1);
		thing2.getRatings().add(t2r2);
		thing2.getRatings().add(t2r3);
		thingDAO.insert(thing2);

		// this last item could change everything
		Thing thing3 = getSampleThing();
		Rating t3f = new Rating("userf", 5);
		// Rating t3r4 = new Rating("user444", 5);
		thing3.getRatings().add(t3f);
		// thing3.getRatings().add(t3r4);
		thingDAO.insert(thing3);

		Set<String> neighborhood = recommendingDAO.getNeighborhood("userf");
		neighborhood.stream().forEach(r -> LOGGER.debug(r));

		Assert.assertEquals(3, neighborhood.size());
		Assert.assertTrue(neighborhood.contains("user111"));
		Assert.assertTrue(neighborhood.contains("user333"));
		Assert.assertTrue(neighborhood.contains("user555"));

		// calculate & test the euclidean distance to the neighbours
		Map<String, Double> euclideanNeighborhood = recommendingDAO.getEuclideanNeighborhood("userf");
		Assert.assertEquals(1.41, euclideanNeighborhood.get("user111").doubleValue(), 0.01);
		Assert.assertEquals(3.31, euclideanNeighborhood.get("user333").doubleValue(), 0.01);
		Assert.assertEquals(2.00, euclideanNeighborhood.get("user555").doubleValue(), 0.01);
	}

	@Test
	public void getAllCommonRatedThingsTest() {
		Thing thing0 = getSampleThing();
		Rating t0f = new Rating("userf", 5);
		Rating t0r1 = new Rating("user111", 5);
		Rating t0r2 = new Rating("user222", 5);
		Rating t0r3 = new Rating("user333", 4);
		Rating t0r4 = new Rating("user444", 5);
		Rating t0r5 = new Rating("user555", 3);
		thing0.getRatings().add(t0f);
		thing0.getRatings().add(t0r1);
		thing0.getRatings().add(t0r2);
		thing0.getRatings().add(t0r3);
		thing0.getRatings().add(t0r4);
		thing0.getRatings().add(t0r5);
		thingDAO.insert(thing0);

		Thing thing1 = getSampleThing();
		Rating t1f = new Rating("userf", 4);
		Rating t1r1 = new Rating("user111", 5);
		Rating t1r2 = new Rating("user333", 5);
		Rating t1r3 = new Rating("user555", 4);
		Rating t1r4 = new Rating("user444", 5);
		thing1.getRatings().add(t1f);
		thing1.getRatings().add(t1r1);
		thing1.getRatings().add(t1r2);
		thing1.getRatings().add(t1r3);
		thing1.getRatings().add(t1r4);
		thingDAO.insert(thing1);

		Thing thing2 = getSampleThing();
		Rating t2f = new Rating("userf", 4);
		Rating t2r1 = new Rating("user111", 5);
		Rating t2r2 = new Rating("user333", 1);
		Rating t2r3 = new Rating("user555", 4);
		thing2.getRatings().add(t2f);
		thing2.getRatings().add(t2r1);
		thing2.getRatings().add(t2r2);
		thing2.getRatings().add(t2r3);
		thingDAO.insert(thing2);

		// this last item could change everything
		Thing thing3 = getSampleThing();
		Rating t3f = new Rating("userf", 5);
		// Rating t3r4 = new Rating("user444", 5);
		thing3.getRatings().add(t3f);
		// thing3.getRatings().add(t3r4);
		thingDAO.insert(thing3);

		List<String> commonRatedThingIDs = recommendingDAO.getAllCommonRatedThings("userf", "user111");
		Assert.assertEquals(3, commonRatedThingIDs.size());
	}

	@Test
	public void getUserRatedThingsTest1() {
		Thing thing1 = getSampleThing();
		Rating t1r1 = new Rating("user123", 5);
		Rating t1r2 = new Rating("user124", 5);
		Rating t1r3 = new Rating("user125", 5);
		thing1.getRatings().add(t1r1);
		thing1.getRatings().add(t1r2);
		thing1.getRatings().add(t1r3);
		thingDAO.insert(thing1);

		Thing thing2 = getSampleThing();
		Rating t2r1 = new Rating("user223", 5);
		Rating t2r2 = new Rating("user224", 5);
		Rating t2r3 = new Rating("user225", 5);
		thing2.getRatings().add(t2r1);
		thing2.getRatings().add(t2r2);
		thing2.getRatings().add(t2r3);
		thingDAO.insert(thing2);

		List<String> allUserRatedThings = recommendingDAO.getAllUserRatedThings("user124");
		Assert.assertEquals(1, allUserRatedThings.size());
		Assert.assertEquals(thing1.getThingId(), allUserRatedThings.get(0));
	}

	@Test
	public void getUserRatedThingsTest2() {
		Thing thing0 = getSampleThing();
		Rating t0r1 = new Rating("user023", 5);
		Rating t0r2 = new Rating("user024", 5);
		Rating t0r3 = new Rating("user025", 5);
		thing0.getRatings().add(t0r1);
		thing0.getRatings().add(t0r2);
		thing0.getRatings().add(t0r3);
		thingDAO.insert(thing0);

		Thing thing1 = getSampleThing();
		Rating t1r1 = new Rating("user123", 5);
		Rating t1r2 = new Rating("user124", 5);
		Rating t1r3 = new Rating("user125", 5);
		thing1.getRatings().add(t1r1);
		thing1.getRatings().add(t1r2);
		thing1.getRatings().add(t1r3);
		thingDAO.insert(thing1);

		Thing thing2 = getSampleThing();
		Rating t2r1 = new Rating("user223", 5);
		Rating t2r2 = new Rating("user224", 5);
		Rating t2r3 = new Rating("user225", 5);
		thing2.getRatings().add(t2r1);
		thing2.getRatings().add(t2r2);
		thing2.getRatings().add(t2r3);
		thingDAO.insert(thing2);

		List<String> allUserRatedThings = recommendingDAO.getAllUserRatedThings("user124");
		Assert.assertEquals(1, allUserRatedThings.size());
		Assert.assertEquals(thing1.getThingId(), allUserRatedThings.get(0));
	}

	@Test
	public void getUserRatedThingsTest3() {
		Thing thing0 = getSampleThing();
		Rating t0r1 = new Rating("user023", 5);
		Rating t0r2 = new Rating("user124", 5);
		Rating t0r3 = new Rating("user025", 5);
		thing0.getRatings().add(t0r1);
		thing0.getRatings().add(t0r2);
		thing0.getRatings().add(t0r3);
		thingDAO.insert(thing0);

		Thing thing1 = getSampleThing();
		Rating t1r1 = new Rating("user123", 5);
		Rating t1r2 = new Rating("user124", 5);
		Rating t1r3 = new Rating("user125", 5);
		thing1.getRatings().add(t1r1);
		thing1.getRatings().add(t1r2);
		thing1.getRatings().add(t1r3);
		thingDAO.insert(thing1);

		Thing thing2 = getSampleThing();
		Rating t2r1 = new Rating("user223", 5);
		Rating t2r2 = new Rating("user224", 5);
		Rating t2r3 = new Rating("user225", 5);
		thing2.getRatings().add(t2r1);
		thing2.getRatings().add(t2r2);
		thing2.getRatings().add(t2r3);
		thingDAO.insert(thing2);

		List<String> allUserRatedThings = recommendingDAO.getAllUserRatedThings("user124");
		Assert.assertEquals(2, allUserRatedThings.size());
		Assert.assertEquals(thing0.getThingId(), allUserRatedThings.get(0));
		Assert.assertEquals(thing1.getThingId(), allUserRatedThings.get(1));
	}

	private Thing getSampleThing() {
		Thing device1 = new Thing();
		device1.setResourceId("resource123");
		device1.setOwnerId("owner123");
		device1.setDataSample("{'temp','21°C'}");
		device1.setThingId(DefaultIDGenerator.generateID());

		DataRights dataRights = new DataRights();
		dataRights.setCollection(true);
		dataRights.setCommercialUsage(true);
		dataRights.setDerivation(true);
		dataRights.setReproduction(true);
		device1.setDataRights(dataRights);

		QoD qod = new QoD();
		qod.setAccuracy(new Double(0.9));
		qod.setCompleteness(new Double(0.9));
		qod.setConsistency(new Double(0.9));
		qod.setCurrency(new Double(0.9));
		qod.setTimeliness(new Double(0.9));
		device1.setQod(qod);

		PricingModel pricingModel = new PricingModel();
		pricingModel.setCurrency(Currency.EUR);
		pricingModel.setPrice(new Double(0.01));
		pricingModel.setNumberOfTransactions(1);
		pricingModel.setTransaction(true);
		device1.setPricingModel(pricingModel);

		return device1;
	}
}
