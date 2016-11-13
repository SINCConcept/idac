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
import ac.at.tuwien.mt.dao.monitor.ThingMonitorQoDDAO;
import ac.at.tuwien.mt.dao.monitor.impl.ThingMonitorQoDDAOImpl;
import ac.at.tuwien.mt.model.exception.InvalidObjectException;
import ac.at.tuwien.mt.model.exception.ResourceOutOfDateException;
import ac.at.tuwien.mt.model.helper.DefaultIDGenerator;
import ac.at.tuwien.mt.model.thing.monitor.MonitoredQoD;

/**
 * @author Florin Bogdan Balint
 *
 */
public class ThingMonitorQoDDAOTest extends DAOTest {

	private static final String QOD_COLLECTION = MongoDBPropertiesProvider.getString(MongoDBProperty.MONGO_DB_COLLECTION_THING_MONITOR_QOD);

	private ThingMonitorQoDDAO dao;

	@Before
	public void before() {
		mongoClient.getDatabase(DB_NAME).getCollection(QOD_COLLECTION).drop();
		dao = new ThingMonitorQoDDAOImpl(mongoClient, DB_NAME, QOD_COLLECTION);
	}

	@After
	public void after() {
		mongoClient.getDatabase(DB_NAME).getCollection(QOD_COLLECTION).drop();
	}

	@Test
	public void testInsert() {
		MonitoredQoD monitoredQoD = getTestMonitoredQoD();
		dao.insert(monitoredQoD);
	}

	@Test
	public void testUpdatePositive() {
		MonitoredQoD monitoredQoD = getTestMonitoredQoD();
		dao.insert(monitoredQoD);
		monitoredQoD.setAgeSamples(2);
		monitoredQoD.setTotalNrOfSamples(2);
		try {
			MonitoredQoD updated = dao.update(monitoredQoD);
			Assert.assertEquals(2, updated.getAgeSamples());
		} catch (ResourceOutOfDateException | InvalidObjectException e) {
			// not expected
			Assert.fail();
		}
	}

	@Test
	public void testUpdateNegative() {
		MonitoredQoD monitoredQoD = getTestMonitoredQoD();
		dao.insert(monitoredQoD);
		monitoredQoD.setAgeSamples(2);
		monitoredQoD.setTotalNrOfSamples(2);
		try {
			MonitoredQoD updated = dao.update(monitoredQoD);
			Assert.assertEquals(2, updated.getAgeSamples());
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
		MonitoredQoD monitoredQoD = getTestMonitoredQoD();
		dao.insert(monitoredQoD);

		MonitoredQoD monitoredQoD2 = getTestMonitoredQoD();
		dao.insert(monitoredQoD2);

		MonitoredQoD find = dao.find(monitoredQoD.getThingId());
		Assert.assertNotNull(find);
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

}
