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
import ac.at.tuwien.mt.model.helper.DefaultIDGenerator;
import ac.at.tuwien.mt.model.thing.Thing;
import ac.at.tuwien.mt.model.thing.rating.Rating;

/**
 * @author Florin Bogdan Balint
 *
 */
public class TagRecommendingDAOTest extends DAOTest {

	private static final Logger LOGGER = LogManager.getLogger(TagRecommendingDAOTest.class);
	private static final String THING_COLLECTION = MongoDBPropertiesProvider.getString(MongoDBProperty.MONGO_DB_COLLECTION_THING);

	private ThingDAO thingDAO;
	private RecommendingDAO recommendingDAO;

	@Before
	public void before() {
		LOGGER.debug("dropping test collection.");
		mongoClient.getDatabase(DB_NAME).getCollection(THING_COLLECTION).drop();
		thingDAO = new ThingDAOImpl(mongoClient, DB_NAME, THING_COLLECTION);
		recommendingDAO = new RecommendingDAOImpl(mongoClient, DB_NAME, THING_COLLECTION);
	}

	@After
	public void after() {
		mongoClient.getDatabase(DB_NAME).getCollection(THING_COLLECTION).drop();
	}

	@Test
	public void recommendThingForTagTest1() {
		Thing thing0 = getSampleThing1();
		thingDAO.insert(thing0);

		// no things present
		Thing recommended = recommendingDAO.recommendForTag("#test4");
		Assert.assertNull(recommended);
	}

	@Test
	public void recommendThingForTagTest2() {
		Thing thing0 = getSampleThing2();
		thingDAO.insert(thing0);

		// no things present
		Thing recommended = recommendingDAO.recommendForTag("#test2");
		Assert.assertNotNull(recommended);
		Assert.assertEquals(thing0.getThingId(), recommended.getThingId());
	}

	@Test
	public void recommendThingForTagTest3() {
		Thing thing0 = getSampleThing1();
		thingDAO.insert(thing0);

		Thing thing2 = getSampleThing2();
		thingDAO.insert(thing2);

		Thing thing3 = getSampleThing3();
		thingDAO.insert(thing3);

		// no things present
		Thing recommended = recommendingDAO.recommendForTag("#test2");
		Assert.assertNotNull(recommended);
		Assert.assertEquals(thing2.getThingId(), recommended.getThingId());
	}

	@Test
	public void getTopRatedThingForTagTest1() {
		Thing thing0 = getSampleThing1();
		thingDAO.insert(thing0);

		// no things present
		Thing recommended = recommendingDAO.getTopRatedThingForTag("#test4");
		Assert.assertNull(recommended);
	}

	@Test
	public void getTopRatedThingForTagTest2() {
		Thing thing0 = getSampleThing1();
		thingDAO.insert(thing0);

		// only one thing - should be covered by the random

		Thing recommended = recommendingDAO.getTopRatedThingForTag("#test2");
		Assert.assertNotNull(recommended);
		Assert.assertEquals(thing0.getThingId(), recommended.getThingId());
	}

	@Test
	public void getTopRatedThingForTagTest3() {
		Thing thing1 = getSampleThing1();
		thingDAO.insert(thing1);

		Thing thing2 = getSampleThing2();
		thingDAO.insert(thing2);

		Thing thing3 = getSampleThing3();
		thingDAO.insert(thing3);

		// only one thing - should be covered by the random

		Thing recommended = recommendingDAO.getTopRatedThingForTag("#test2");
		Assert.assertNotNull(recommended);
		Assert.assertEquals(thing2.getThingId(), recommended.getThingId());
	}

	@Test
	public void getRandomThingForTagTest1() {
		Thing thing0 = getSampleThing1();
		thingDAO.insert(thing0);

		// no things present
		Thing recommended = recommendingDAO.getRandomThingForTag("#test4");
		Assert.assertNull(recommended);
	}

	@Test
	public void getRandomThingForTagTest2() {
		Thing thing0 = getSampleThing1();
		thingDAO.insert(thing0);

		// only one thing - should be covered by the random

		Thing recommended = recommendingDAO.getRandomThingForTag("#test2");
		Assert.assertNotNull(recommended);
		Assert.assertEquals(thing0.getThingId(), recommended.getThingId());
	}

	@Test
	public void getRandomThingForTagTest3() {
		Thing thing1 = getSampleThing1();
		thingDAO.insert(thing1);

		Thing thing2 = getSampleThing2();
		thingDAO.insert(thing2);

		Thing thing3 = getSampleThing3();
		thingDAO.insert(thing3);

		// only one thing - should be covered by the random

		Thing recommended = recommendingDAO.getTopRatedThingForTag("#test2");
		Assert.assertNotNull(recommended);
		String recId = recommended.getThingId();
		boolean found = recId.equals(thing2.getThingId()) || recId.equals(thing3.getThingId());
		Assert.assertTrue(found);
	}

	private Thing getSampleThing1() {
		Thing thing = new Thing();
		thing.setResourceId("resource123");
		thing.setOwnerId("owner123");
		thing.setDataSample("{'temp','21°C'}");
		thing.setThingId(DefaultIDGenerator.generateID());

		Rating r1 = new Rating("user111", 3);
		thing.getRatings().add(r1);

		thing.getTags().add("#test1");
		thing.getTags().add("#test2");
		thing.getTags().add("#test3");

		return thing;
	}

	private Thing getSampleThing2() {
		Thing thing = new Thing();
		thing.setResourceId("resource123");
		thing.setOwnerId("owner123");
		thing.setDataSample("{'temp','21°C'}");
		thing.setThingId(DefaultIDGenerator.generateID());

		Rating r1 = new Rating("user111", 5);
		thing.getRatings().add(r1);

		thing.getTags().add("#test1");
		thing.getTags().add("#test2");
		thing.getTags().add("#test3");

		return thing;
	}

	private Thing getSampleThing3() {
		Thing thing = new Thing();
		thing.setResourceId("resource123");
		thing.setOwnerId("owner123");
		thing.setDataSample("{'temp','21°C'}");
		thing.setThingId(DefaultIDGenerator.generateID());

		Rating r1 = new Rating("user111", 4);
		thing.getRatings().add(r1);

		thing.getTags().add("#test1");
		thing.getTags().add("#test2");
		thing.getTags().add("#test3");

		return thing;
	}
}
