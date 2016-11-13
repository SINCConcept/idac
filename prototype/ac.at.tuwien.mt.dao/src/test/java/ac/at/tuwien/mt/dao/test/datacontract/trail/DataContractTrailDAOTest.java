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
package ac.at.tuwien.mt.dao.test.datacontract.trail;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import ac.at.tuwien.mt.common.test.dao.DAOTest;
import ac.at.tuwien.mt.common.test.dao.MongoDBPropertiesProvider;
import ac.at.tuwien.mt.common.test.dao.MongoDBProperty;
import ac.at.tuwien.mt.common.test.sample.SampleData;
import ac.at.tuwien.mt.dao.datacontract.DataContractDAO;
import ac.at.tuwien.mt.dao.datacontract.DataContractTrailDAO;
import ac.at.tuwien.mt.dao.datacontract.impl.DataContractDAOImpl;
import ac.at.tuwien.mt.dao.datacontract.impl.DataContractTrailDAOImpl;
import ac.at.tuwien.mt.model.datacontract.DataContract;
import ac.at.tuwien.mt.model.datacontract.DataContractTrail;
import ac.at.tuwien.mt.model.exception.InvalidObjectException;
import ac.at.tuwien.mt.model.exception.ResourceOutOfDateException;

/**
 * @author Florin Bogdan Balint
 *
 */
public class DataContractTrailDAOTest extends DAOTest {

	private static final String DC_COLLECTION = MongoDBPropertiesProvider.getString(MongoDBProperty.MONGO_DB_COLLECTION_DATACONTRACT);
	private static final String DC_TRAIL_COLLECTION = MongoDBPropertiesProvider.getString(MongoDBProperty.MONGO_DB_COLLECTION_DATACONTRACT_TRAIL);

	private DataContractTrailDAO dctDAO;
	private DataContractDAO dcDAO;

	@Before
	public void before() {
		mongoClient.getDatabase(DB_NAME).getCollection(DC_COLLECTION).drop();
		mongoClient.getDatabase(DB_NAME).getCollection(DC_TRAIL_COLLECTION).drop();

		dctDAO = new DataContractTrailDAOImpl(mongoClient, DB_NAME, DC_COLLECTION, DC_TRAIL_COLLECTION);
		dcDAO = new DataContractDAOImpl(mongoClient, DB_NAME, DC_COLLECTION);
	}

	@After
	public void after() {
		mongoClient.getDatabase(DB_NAME).getCollection(DC_COLLECTION).drop();
		mongoClient.getDatabase(DB_NAME).getCollection(DC_TRAIL_COLLECTION).drop();
	}

	@Test
	public void testInsert() {
		DataContractTrail trail = SampleData.getSampleDataContractTrail();
		try {
			trail = dctDAO.insert(trail);
			Assert.assertNotNull(trail.getRevision());
		} catch (InvalidObjectException e) {
			Assert.fail();
		}
	}

	@Test
	public void testUpdate() {
		DataContractTrail trail = SampleData.getSampleDataContractTrail();
		try {
			trail = dctDAO.insert(trail);
			Assert.assertNotNull(trail.getRevision());

			trail.getClausesTrail().add(SampleData.getSampleClausesEntry());
			trail = dctDAO.update(trail);

			Assert.assertEquals(2, trail.getRevision().intValue());
			Assert.assertEquals(2, trail.getClausesTrail().size());
		} catch (InvalidObjectException e) {
			Assert.fail();
		} catch (ResourceOutOfDateException e) {
			Assert.fail();
		}
	}

	@Test
	public void testFind() {
		DataContractTrail trail = SampleData.getSampleDataContractTrail();
		try {
			trail = dctDAO.insert(trail);
			Assert.assertNotNull(trail.getRevision());

			DataContractTrail found1 = dctDAO.find("12390x");
			Assert.assertNull(found1);

			DataContractTrail found2 = dctDAO.find(trail.getContractId());
			Assert.assertNotNull(found2);
		} catch (InvalidObjectException e) {
			Assert.fail();
		}
	}

	@Test
	public void getAvgNrOfNegotiationsForThingTest1() {
		try {
			dctDAO.getAvgNrOfNegotiationsForThing(null);
		} catch (InvalidObjectException e) {
			// expected
			return;
		}
		Assert.fail();
	}

	@Test
	public void getAvgNrOfNegotiationsForThingTest2() {
		try {
			Integer result = dctDAO.getAvgNrOfNegotiationsForThing("123");
			Assert.assertNull(result);
		} catch (InvalidObjectException e) {
			// not expected
			Assert.fail();
		}
	}

	@Test
	public void getAvgNrOfNegotiationsForThingTest3() {
		try {
			// insert data
			DataContract dc = SampleData.getSampleDataContract();
			dcDAO.insert(dc);
			DataContractTrail dct = SampleData.getSampleDataContractTrail();
			dct.getClausesTrail().add(dct.getClausesTrail().get(0));
			dctDAO.insert(dct);

			// get results
			String thingId = dc.getThingIds().get(0).getThingId();
			Integer result = dctDAO.getAvgNrOfNegotiationsForThing(thingId);
			Assert.assertNotNull(result);
			Assert.assertEquals(2, result.intValue());
		} catch (InvalidObjectException e) {
			// not expected
			Assert.fail();
		}
	}

}
