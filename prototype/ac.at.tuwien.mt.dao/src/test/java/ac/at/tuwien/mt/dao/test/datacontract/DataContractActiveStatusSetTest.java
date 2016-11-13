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

import java.util.Calendar;
import java.util.Date;

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

/**
 * @author Florin Bogdan Balint
 *
 */
public class DataContractActiveStatusSetTest extends DAOTest {

	private static final String DC_COLLECTION = MongoDBPropertiesProvider.getString(MongoDBProperty.MONGO_DB_COLLECTION_DATACONTRACT);

	private DataContractDAO dao;

	@Before
	public void before() {
		mongoClient.getDatabase(DB_NAME).getCollection(DC_COLLECTION).drop();
		dao = new DataContractDAOImpl(mongoClient, DB_NAME, DC_COLLECTION);
	}

	@After
	public void after() {
		mongoClient.getDatabase(DB_NAME).getCollection(DC_COLLECTION).drop();
	}

	@Test
	public void testActivate() {
		DataContract dc1 = getSampleValidDataContract();
		DataContract dc2 = getSampleInvalidDataContract();
		dao.insert(dc1);
		dao.insert(dc2);

		dao.activateDataContracts();
		DataContract dc3 = dao.readById("validContractId");
		DataContract dc4 = dao.readById("invalidContractId");
		Assert.assertEquals(true, dc3.getDataContractMetaInfo().getActive());
		Assert.assertEquals(2, dc3.getDataContractMetaInfo().getRevision().intValue());
		Assert.assertEquals(1, dc4.getDataContractMetaInfo().getRevision().intValue());
	}

	@Test
	public void testDeactivate() {
		DataContract dc1 = getSampleValidDataContract();
		DataContract dc2 = getSampleInvalidDataContract();
		dao.insert(dc1);
		dao.insert(dc2);

		dao.deactivateDataContracts();
		DataContract dc3 = dao.readById("validContractId");
		DataContract dc4 = dao.readById("invalidContractId");
		Assert.assertEquals(false, dc4.getDataContractMetaInfo().getActive());
		Assert.assertEquals(2, dc4.getDataContractMetaInfo().getRevision().intValue());
		Assert.assertEquals(1, dc3.getDataContractMetaInfo().getRevision().intValue());
	}

	private DataContract getSampleValidDataContract() {
		DataContract dataContract = new DataContract();
		dataContract.getDataContractMetaInfo().setContractId("validContractId");
		dataContract.getDataContractMetaInfo().setParty1Id("party1Id");
		dataContract.getDataContractMetaInfo().setParty2Id("party2Id");
		dataContract.getDataContractMetaInfo().setParty1Accepted(true);
		dataContract.getDataContractMetaInfo().setParty2Accepted(true);
		dataContract.getDataContractMetaInfo().setActive(false);

		Calendar calStart = Calendar.getInstance();
		calStart.add(Calendar.DAY_OF_YEAR, -1);
		Date startDate = calStart.getTime();
		dataContract.getPricingModel().getSubscription().setStartDate(startDate);

		Calendar calEnd = Calendar.getInstance();
		calEnd.add(Calendar.DAY_OF_YEAR, 1);
		Date endDate = calEnd.getTime();
		dataContract.getPricingModel().getSubscription().setEndDate(endDate);

		return dataContract;
	}

	private DataContract getSampleInvalidDataContract() {
		DataContract dataContract = new DataContract();
		dataContract.getDataContractMetaInfo().setContractId("invalidContractId");
		dataContract.getDataContractMetaInfo().setParty1Id("party1Id");
		dataContract.getDataContractMetaInfo().setParty2Id("party2Id");
		dataContract.getDataContractMetaInfo().setParty1Accepted(true);
		dataContract.getDataContractMetaInfo().setParty2Accepted(true);
		dataContract.getDataContractMetaInfo().setActive(true);

		Calendar calStart = Calendar.getInstance();
		calStart.add(Calendar.DAY_OF_YEAR, -5);
		Date startDate = calStart.getTime();
		dataContract.getPricingModel().getSubscription().setStartDate(startDate);

		Calendar calEnd = Calendar.getInstance();
		calEnd.add(Calendar.DAY_OF_YEAR, -1);
		Date endDate = calEnd.getTime();
		dataContract.getPricingModel().getSubscription().setEndDate(endDate);

		return dataContract;
	}

}
