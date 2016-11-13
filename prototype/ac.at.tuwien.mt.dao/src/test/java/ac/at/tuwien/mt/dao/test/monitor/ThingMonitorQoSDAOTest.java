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

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import ac.at.tuwien.mt.common.test.dao.DAOTest;
import ac.at.tuwien.mt.common.test.dao.MongoDBPropertiesProvider;
import ac.at.tuwien.mt.common.test.dao.MongoDBProperty;
import ac.at.tuwien.mt.dao.monitor.ThingMonitorQoSDAO;
import ac.at.tuwien.mt.dao.monitor.impl.ThingMonitorQoSDAOImpl;
import ac.at.tuwien.mt.model.exception.InvalidObjectException;
import ac.at.tuwien.mt.model.exception.ResourceOutOfDateException;
import ac.at.tuwien.mt.model.helper.DefaultDateProvider;
import ac.at.tuwien.mt.model.helper.DefaultIDGenerator;
import ac.at.tuwien.mt.model.thing.monitor.MonitoredQoS;

/**
 * @author Florin Bogdan Balint
 *
 */
public class ThingMonitorQoSDAOTest extends DAOTest {

	private static final String QOS_COLLECTION = MongoDBPropertiesProvider.getString(MongoDBProperty.MONGO_DB_COLLECTION_THING_MONITOR_QOS);

	private ThingMonitorQoSDAO dao;

	@Before
	public void before() {
		mongoClient.getDatabase(DB_NAME).getCollection(QOS_COLLECTION).drop();
		dao = new ThingMonitorQoSDAOImpl(mongoClient, DB_NAME, QOS_COLLECTION);
	}

	@After
	public void after() {
		mongoClient.getDatabase(DB_NAME).getCollection(QOS_COLLECTION).drop();
	}

	@Test
	public void testInsert() {
		MonitoredQoS monitoredQoS = getTestMonitoredQoS();
		dao.insert(monitoredQoS);
	}

	@Test
	public void testUpdatePositive() {
		MonitoredQoS monitoredQoS = getTestMonitoredQoS();
		dao.insert(monitoredQoS);

		monitoredQoS.setExpectedNrOfSamples(2l);
		monitoredQoS.setTotalNrOfSamples(2);
		try {
			MonitoredQoS updated = dao.update(monitoredQoS);
			Assert.assertEquals(2, updated.getExpectedNrOfSamples().longValue());
			Assert.assertEquals(2, updated.getRevision());

		} catch (ResourceOutOfDateException | InvalidObjectException e) {
			// not expected
			Assert.fail();
		}
	}

	@Test
	public void testUpdateNegative() {
		MonitoredQoS monitoredQoS = getTestMonitoredQoS();
		dao.insert(monitoredQoS);
		monitoredQoS.setExpectedNrOfSamples(2l);
		monitoredQoS.setTotalNrOfSamples(2);
		try {
			MonitoredQoS updated = dao.update(monitoredQoS);
			Assert.assertEquals(2, updated.getExpectedNrOfSamples().longValue());
			Assert.assertEquals(2, updated.getRevision());

			updated.setRevision(1l);
			dao.update(updated);
			Assert.fail();
		} catch (ResourceOutOfDateException | InvalidObjectException e) {
			// expected
		}
	}

	@Test
	public void testFind() {

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
