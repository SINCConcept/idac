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
import ac.at.tuwien.mt.model.exception.InvalidObjectException;
import ac.at.tuwien.mt.model.exception.ResourceOutOfDateException;

/**
 * @author Florin Bogdan Balint
 *
 */
public class DataContractDAOTest extends DAOTest {

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
	public void testInsert() {
		DataContract dc = getSampleDataContract();
		DataContract created = dcDAO.insert(dc);

		Assert.assertNotNull(created.getDataContractMetaInfo().getContractId());
		Assert.assertNotNull(created.getDataContractMetaInfo().getCreationDate());
	}

	@Test
	public void testReadById() {
		DataContract dc = getSampleDataContract();
		DataContract created = dcDAO.insert(dc);

		Assert.assertNotNull(created.getDataContractMetaInfo().getContractId());
		DataContract readById = dcDAO.readById(created.getDataContractMetaInfo().getContractId());
		Assert.assertNotNull(readById);
	}

	@Test
	public void testUpdate() {
		DataContract dc = getSampleDataContract();
		DataContract created = dcDAO.insert(dc);

		created.getDataContractMetaInfo().setParty1Accepted(true);
		try {
			DataContract updated = dcDAO.update(created);
			Assert.assertEquals(2, updated.getDataContractMetaInfo().getRevision().intValue());
		} catch (ResourceOutOfDateException | InvalidObjectException e) {
			Assert.fail();
		}
	}

	@Test
	public void testFindBuyer() {
		DataContract dc1 = getSampleDataContract();
		dcDAO.insert(dc1);

		DataContract dc2 = getSampleDataContract();
		dc2.getDataContractMetaInfo().setParty1Accepted(true);
		dc2.getDataContractMetaInfo().setParty2Accepted(true);
		dc2.getDataContractMetaInfo().setActive(true);
		dcDAO.insert(dc2);

		List<DataContract> concludedContracts = dcDAO.findBuyerConcludedContracts("party2Id");
		Assert.assertEquals(1, concludedContracts.size());

		List<DataContract> openContracts = dcDAO.findBuyerOpenContracts("party2Id");
		Assert.assertEquals(1, openContracts.size());
	}

	@Test
	public void testFindProvider() {
		DataContract dc1 = getSampleDataContract();
		dcDAO.insert(dc1);

		DataContract dc2 = getSampleDataContract();
		dc2.getDataContractMetaInfo().setParty1Accepted(true);
		dc2.getDataContractMetaInfo().setParty2Accepted(true);
		dc2.getDataContractMetaInfo().setActive(true);
		dcDAO.insert(dc2);

		List<DataContract> concludedContracts = dcDAO.findProviderConcludedContracts("party1Id");
		Assert.assertEquals(1, concludedContracts.size());

		List<DataContract> openContracts = dcDAO.findProviderOpenContracts("party1Id");
		Assert.assertEquals(1, openContracts.size());
	}

	private DataContract getSampleDataContract() {
		DataContract dataContract = new DataContract();
		dataContract.getDataContractMetaInfo().setParty1Id("party1Id");
		dataContract.getDataContractMetaInfo().setParty2Id("party2Id");
		dataContract.getDataContractMetaInfo().setParty1Accepted(false);
		dataContract.getDataContractMetaInfo().setParty2Accepted(false);
		dataContract.getDataContractMetaInfo().setActive(false);
		return dataContract;
	}
}
