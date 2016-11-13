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
package ac.at.tuwien.mt.dao.test.datacontract;

import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import ac.at.tuwien.mt.common.test.dao.DAOTest;
import ac.at.tuwien.mt.common.test.dao.MongoDBPropertiesProvider;
import ac.at.tuwien.mt.common.test.dao.MongoDBProperty;
import ac.at.tuwien.mt.dao.datacontract.DataContractDAO;
import ac.at.tuwien.mt.dao.datacontract.impl.DataContractDAOImpl;
import ac.at.tuwien.mt.model.datacontract.DataContract;
import ac.at.tuwien.mt.model.thing.Thing;
import ac.at.tuwien.mt.model.thing.ThingId;

/**
 * @author Florin Bogdan Balint
 *
 */
public class DataContractDAOSearchTest extends DAOTest {

	private static final String DC_COLLECTION = MongoDBPropertiesProvider.getString(MongoDBProperty.MONGO_DB_COLLECTION_DATACONTRACT);

	private DataContractDAO dcDAO;

	@Before
	public void before() {
		mongoClient.getDatabase(DB_NAME).getCollection(DC_COLLECTION).drop();
		dcDAO = new DataContractDAOImpl(mongoClient, DB_NAME, DC_COLLECTION);
	}

	@After
	public void after() {
		mongoClient.getDatabase(DB_NAME).getCollection(DC_COLLECTION).drop();
	}

	@Test
	public void testSearch() {
		DataContract dc1 = getSampleDataContract1();
		dcDAO.insert(dc1);

		DataContract dc2 = getSampleDataContract2();
		dcDAO.insert(dc2);

		DataContract dc3 = getSampleDataContract3();
		dcDAO.insert(dc3);

		List<DataContract> contracts1 = dcDAO.findDataContracts("thing1");
		Assert.assertEquals(2, contracts1.size());

		List<DataContract> contracts2 = dcDAO.findDataContracts("thing2");
		Assert.assertEquals(2, contracts2.size());

		List<DataContract> contracts3 = dcDAO.findDataContracts("thing3");
		Assert.assertEquals(1, contracts3.size());
	}

	private DataContract getSampleDataContract3() {
		DataContract dataContract = new DataContract();
		dataContract.getDataContractMetaInfo().setParty1Id("party1Id");
		dataContract.getDataContractMetaInfo().setParty2Id("party2Id");
		dataContract.getDataContractMetaInfo().setParty1Accepted(false);
		dataContract.getDataContractMetaInfo().setParty2Accepted(false);
		dataContract.getDataContractMetaInfo().setActive(false);

		dataContract.getThingIds().add(new ThingId(getSampleThing2().getThingId()));
		dataContract.getThingIds().add(new ThingId(getSampleThing3().getThingId()));
		return dataContract;
	}

	private DataContract getSampleDataContract2() {
		DataContract dataContract = new DataContract();
		dataContract.getDataContractMetaInfo().setParty1Id("party1Id");
		dataContract.getDataContractMetaInfo().setParty2Id("party2Id");
		dataContract.getDataContractMetaInfo().setParty1Accepted(true);
		dataContract.getDataContractMetaInfo().setParty2Accepted(true);
		dataContract.getDataContractMetaInfo().setActive(true);

		dataContract.getThingIds().add(new ThingId(getSampleThing1().getThingId()));
		dataContract.getThingIds().add(new ThingId(getSampleThing2().getThingId()));
		return dataContract;
	}

	private DataContract getSampleDataContract1() {
		DataContract dataContract = new DataContract();
		dataContract.getDataContractMetaInfo().setParty1Id("party1Id");
		dataContract.getDataContractMetaInfo().setParty2Id("party2Id");
		dataContract.getDataContractMetaInfo().setParty1Accepted(true);
		dataContract.getDataContractMetaInfo().setParty2Accepted(true);
		dataContract.getDataContractMetaInfo().setActive(true);

		dataContract.getThingIds().add(new ThingId(getSampleThing1().getThingId()));
		dataContract.getThingIds().add(new ThingId(getSampleThing2().getThingId()));
		dataContract.getThingIds().add(new ThingId(getSampleThing3().getThingId()));
		return dataContract;
	}

	private Thing getSampleThing1() {
		Thing device1 = new Thing();
		device1.setResourceId("resource123");
		device1.setOwnerId("owner123");
		device1.setDataSample("{'temp','21°C'}");
		device1.setThingId("thing1");
		return device1;
	}

	private Thing getSampleThing2() {
		Thing device1 = new Thing();
		device1.setResourceId("resource123");
		device1.setOwnerId("owner123");
		device1.setDataSample("{'temp','21°C'}");
		device1.setThingId("thing2");
		return device1;
	}

	private Thing getSampleThing3() {
		Thing device1 = new Thing();
		device1.setResourceId("resource123");
		device1.setOwnerId("owner123");
		device1.setDataSample("{'temp','21°C'}");
		device1.setThingId("thing3");
		return device1;
	}
}
