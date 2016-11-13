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
package ac.at.tuwien.mt.dao.test.monitor;

import java.util.Calendar;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import ac.at.tuwien.mt.common.test.dao.DAOTest;
import ac.at.tuwien.mt.common.test.dao.MongoDBPropertiesProvider;
import ac.at.tuwien.mt.common.test.dao.MongoDBProperty;
import ac.at.tuwien.mt.dao.monitor.MonitoredDataContractDAO;
import ac.at.tuwien.mt.dao.monitor.impl.MonitoredDataContractDAOImpl;
import ac.at.tuwien.mt.model.exception.InvalidObjectException;
import ac.at.tuwien.mt.model.exception.ResourceOutOfDateException;
import ac.at.tuwien.mt.model.helper.DefaultDateProvider;
import ac.at.tuwien.mt.model.helper.DefaultIDGenerator;
import ac.at.tuwien.mt.model.thing.monitor.MonitoredDataContract;
import ac.at.tuwien.mt.model.thing.monitor.MonitoredQoD;
import ac.at.tuwien.mt.model.thing.monitor.MonitoredQoS;

/**
 * @author Florin Bogdan Balint
 *
 */
public class MonitoredDataContractDAOTest extends DAOTest {

	private static final Logger LOGGER = LogManager.getLogger(MonitoredDataContractDAOTest.class);

	private static final String COLLECTION = MongoDBPropertiesProvider.getString(MongoDBProperty.MONGO_DB_COLLECTION_DC_MONITOR);

	private MonitoredDataContractDAO dao;

	@Before
	public void before() {
		mongoClient.getDatabase(DB_NAME).getCollection(COLLECTION).drop();
		dao = new MonitoredDataContractDAOImpl(mongoClient, DB_NAME, COLLECTION);
	}

	@After
	public void after() {
		mongoClient.getDatabase(DB_NAME).getCollection(COLLECTION).drop();
	}

	@Test
	public void testInsert() {
		LOGGER.debug("Test started.");
		MonitoredDataContract object = getTestObject();
		dao.insert(object);
		LOGGER.debug("Test finished.");
	}

	@Test
	public void testUpdatePositive() {
		LOGGER.debug("Test started.");
		MonitoredDataContract object = getTestObject();
		dao.insert(object);
		object.getMonitoredQoD().get(0).setAgeSamples(2);
		object.getMonitoredQoD().get(0).setTotalNrOfSamples(2);
		try {
			MonitoredDataContract updated = dao.update(object);
			Assert.assertEquals(2, updated.getMonitoredQoD().get(0).getAgeSamples());
		} catch (ResourceOutOfDateException | InvalidObjectException e) {
			// not expected
			Assert.fail();
		}
		LOGGER.debug("Test finished.");
	}

	@Test
	public void testUpdateNegative() {
		LOGGER.debug("Test started.");
		MonitoredDataContract object = getTestObject();
		dao.insert(object);
		object.getMonitoredQoD().get(0).setAgeSamples(2);
		object.getMonitoredQoD().get(0).setTotalNrOfSamples(2);
		try {
			MonitoredDataContract updated = dao.update(object);
			Assert.assertEquals(2, updated.getMonitoredQoD().get(0).getAgeSamples());
			Assert.assertEquals(2, updated.getRevision());

			updated.setRevision(1l);
			dao.update(updated);
			Assert.fail();
		} catch (ResourceOutOfDateException | InvalidObjectException e) {
			// expected
		}
		LOGGER.debug("Test finished.");
	}

	@Test
	public void testFindAll() {
		LOGGER.debug("Test started.");
		MonitoredDataContract object1 = getTestObject();
		dao.insert(object1);

		MonitoredDataContract object2 = getTestObject();
		dao.insert(object2);

		MonitoredDataContract find = dao.find(object1.getContractId()).get(0);
		Assert.assertNotNull(find);
		LOGGER.debug("Test finished.");
	}

	@Test
	public void testFindOpenPositive() {
		MonitoredDataContract object1 = getTestObject();
		object1.setMonitoringStart(Calendar.getInstance().getTime());
		dao.insert(object1);

		MonitoredDataContract find = dao.findOpen(object1.getContractId());
		Assert.assertNotNull(find);
	}

	@Test
	public void testFindOpenNegative() {
		MonitoredDataContract object1 = getTestObject();
		object1.setMonitoringStart(Calendar.getInstance().getTime());
		object1.setMonitoringEnd(Calendar.getInstance().getTime());
		dao.insert(object1);

		MonitoredDataContract find = dao.findOpen(object1.getContractId());
		Assert.assertNull(find);
	}

	private MonitoredDataContract getTestObject() {
		MonitoredDataContract object = new MonitoredDataContract();
		object.setContractId(DefaultIDGenerator.generateID());
		object.getMonitoredQoD().add(getTestMonitoredQoD());
		object.getMonitoredQoS().add(getTestMonitoredQoS());
		return object;
	}

	private MonitoredQoD getTestMonitoredQoD() {
		MonitoredQoD object = new MonitoredQoD();
		object.setThingId(DefaultIDGenerator.generateID());
		object.setAgeSamples(1);
		object.setAverageAge(1);
		object.setAverageCurrency(1);
		object.setSamplesComplete(1);
		object.setSamplesConform(1);
		object.setTotalNrOfSamples(1);
		return object;
	}

	private MonitoredQoS getTestMonitoredQoS() {
		MonitoredQoS object = new MonitoredQoS();
		object.setThingId(DefaultIDGenerator.generateID());
		object.setTotalNrOfSamples(1);
		object.setAvailability(100.0);
		object.setExpectedFrequency(5000);
		object.setFirstMessageReceival(DefaultDateProvider.getCurrentTimeStamp());
		object.setLastMessageReceival(DefaultDateProvider.getCurrentTimeStamp());
		object.setExpectedNrOfSamples(1l);
		return object;
	}

}
