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
package ac.at.tuwien.mt.monitoring;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;

import ac.at.tuwien.mt.dao.datacontract.DataContractDAO;
import ac.at.tuwien.mt.dao.datacontract.impl.DataContractDAOImpl;
import ac.at.tuwien.mt.dao.monitor.MonitoredDataContractDAO;
import ac.at.tuwien.mt.dao.monitor.impl.MonitoredDataContractDAOImpl;
import ac.at.tuwien.mt.model.datacontract.DataContract;
import ac.at.tuwien.mt.model.helper.DefaultDateProvider;
import ac.at.tuwien.mt.model.thing.Thing;
import ac.at.tuwien.mt.model.thing.ThingId;
import ac.at.tuwien.mt.model.thing.ThingMessage;
import ac.at.tuwien.mt.model.thing.message.Attribute;
import ac.at.tuwien.mt.model.thing.message.DataType;
import ac.at.tuwien.mt.model.thing.message.MetaModel;
import ac.at.tuwien.mt.model.thing.message.Property;
import ac.at.tuwien.mt.model.thing.monitor.MonitoredDataContract;
import ac.at.tuwien.mt.monitoring.thread.DataContractMonitor;
import ac.at.tuwien.mt.monitoring.thread.MessageMonitor;

/**
 * @author Florin Bogdan Balint
 *
 */
public class QoDTest2 {

	private static final String DB_NAME = "mttest1_local";

	private static final String MT_THING = "mt.thing";
	private static final String MT_DATACONTRACT = "mt.datacontract";
	private static final String MT_DATACONTRACT_MONITOR = "mt.datacontract.monitor";

	private static MongoClient mongoClient;

	private Thing thing = null;
	private DataContract dataContract = null;

	private MonitoredDataContractDAO monitoredDataContractDAO;
	private DataContractDAOImpl dataContractDAO;

	@BeforeClass
	public static void beforeClass() {
		// set the mongo client options
		MongoClientOptions.Builder builder = new MongoClientOptions.Builder();
		builder.connectionsPerHost(100);
		builder.sslEnabled(false);
		builder.sslInvalidHostNameAllowed(false);
		MongoClientOptions options = builder.build();

		// set the credentials
		MongoCredential credential = MongoCredential.createCredential("usermttest1", DB_NAME,
				"usermttest1_31415".toCharArray());
		List<MongoCredential> list = new ArrayList<MongoCredential>();
		list.add(credential);

		ServerAddress serverAddress = new ServerAddress("localhost", 27017);
		mongoClient = new MongoClient(serverAddress, list, options);

		mongoClient.getDatabase(DB_NAME).getCollection(MT_THING).drop();
		mongoClient.getDatabase(DB_NAME).getCollection(MT_DATACONTRACT).drop();
		mongoClient.getDatabase(DB_NAME).getCollection(MT_DATACONTRACT_MONITOR).drop();
	}

	@AfterClass
	public static void afterClass() {
		mongoClient.getDatabase(DB_NAME).getCollection(MT_THING).drop();
		mongoClient.getDatabase(DB_NAME).getCollection(MT_DATACONTRACT).drop();
		mongoClient.getDatabase(DB_NAME).getCollection(MT_DATACONTRACT_MONITOR).drop();
		mongoClient.close();
	}

	@Before
	public void before() {
		init();
		mongoClient.getDatabase(DB_NAME).getCollection(MT_THING).insertOne(thing.getDocument());
		mongoClient.getDatabase(DB_NAME).getCollection(MT_DATACONTRACT).insertOne(dataContract.getDocument());
	}

	@Test
	public void testSimplePositive() throws Exception {

		for (int i = 1; i <= 5; i++) {
			System.out.println("Checking entry: " + i);

			String t1 = DefaultDateProvider.getCurrentTimeStamp();
			String message1 = "{ \"thingId\" : \"t1\", \"temperature\" : -20812, \"scale\" : \"Celsius\", \"time\" : \""
					+ t1 + "\"}";
			ThingMessage thingMessage = new ThingMessage(thing, message1, t1, t1);

			// simulate 2 messages.
			MessageMonitor tm = new MessageMonitor(thingMessage);
			tm.monitorQoD();
			tm.monitorQoS();

			ExecutorService pool = Executors.newCachedThreadPool();

			// update the datacontract monitoring
			DataContractMonitor dcMonitor = new DataContractMonitor(thing.getThingId(), dataContract,
					tm.getMonitoredQoD(), tm.getMonitoredQoS(), getMonitoredDataContractDAO(), getDataContractDAO());
			pool.submit(dcMonitor);

			pool.shutdown();
			while (!pool.isTerminated()) {
				// wait...
			}

			MonitoredDataContract foundEntry = getMonitoredDataContractDAO()
					.findOpen(dataContract.getDataContractMetaInfo().getContractId());

			// verify revision
			Assert.assertEquals(i, foundEntry.getRevision());

			// verify QoD
			Assert.assertEquals(i, (int) foundEntry.getMonitoredQoD().get(0).getTotalNrOfSamples());
			Assert.assertEquals(i, (int) foundEntry.getMonitoredQoD().get(0).getSamplesComplete());
			Assert.assertEquals(i, (int) foundEntry.getMonitoredQoD().get(0).getSamplesConform());
			Assert.assertEquals(100, (int) foundEntry.getMonitoredQoD().get(0).getCompleteness());
			Assert.assertEquals(100, (int) foundEntry.getMonitoredQoD().get(0).getConformity());

			// verify QoS
			Assert.assertEquals(i, (int) foundEntry.getMonitoredQoS().get(0).getTotalNrOfSamples());
			Assert.assertEquals(i, foundEntry.getMonitoredQoS().get(0).getExpectedNrOfSamples().intValue());
			Assert.assertEquals(100, foundEntry.getMonitoredQoS().get(0).getAvailability().intValue());

			Thread.sleep(1000);
		}
	}

	private void init() {
		thing = new Thing();
		thing.setThingId("t1");
		thing.setMetaModel(getTestModel1());
		thing.getQos().setFrequency(1000);
		thing.setRevision(1);

		dataContract = new DataContract();
		dataContract.getDataContractMetaInfo().setRevision(1);
		dataContract.getThingIds().add(new ThingId("t1"));
		dataContract.setMonitoring(true);
		dataContract.getDataContractMetaInfo().setActive(true);
		dataContract.getDataContractMetaInfo().setContractId("888");
		dataContract.getDataContractMetaInfo().setCreationDate(Calendar.getInstance().getTime());
		dataContract.getDataContractMetaInfo().setParty1Accepted(true);
		dataContract.getDataContractMetaInfo().setParty2Accepted(true);
		dataContract.getDataContractMetaInfo().setCreationDate(Calendar.getInstance().getTime());
		dataContract.getDataContractMetaInfo().setParty1Id("111");
		dataContract.getDataContractMetaInfo().setParty2Id("222");
	}

	private MetaModel getTestModel1() {
		Attribute attribute1 = new Attribute();
		attribute1.setDataType(DataType.STRING);
		attribute1.setName("thingId");
		Property property = new Property();
		property.setIdentifier(true);
		attribute1.setProperty(property);

		Attribute attribute2 = new Attribute();
		attribute2.setDataType(DataType.DOUBLE);
		attribute2.setName("temperature");

		Attribute attribute3 = new Attribute();
		attribute3.setDataType(DataType.STRING);
		attribute3.setName("scale");

		Attribute attribute4 = new Attribute();
		attribute4.setDataType(DataType.DATE);
		attribute4.setName("time");
		Property a4Property = new Property();
		a4Property.setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
		a4Property.setRecordingDate(true);
		attribute4.setProperty(a4Property);

		MetaModel model1 = new MetaModel();
		model1.getAttributes().add(attribute1);
		model1.getAttributes().add(attribute2);
		model1.getAttributes().add(attribute3);
		model1.getAttributes().add(attribute4);
		return model1;
	}

	public DataContractDAO getDataContractDAO() {
		if (dataContractDAO == null) {
			dataContractDAO = new DataContractDAOImpl(mongoClient, DB_NAME, MT_DATACONTRACT);
		}
		return dataContractDAO;
	}

	/**
	 * @return the monitoredDataContractDAO
	 */
	public MonitoredDataContractDAO getMonitoredDataContractDAO() {
		if (monitoredDataContractDAO == null) {
			monitoredDataContractDAO = new MonitoredDataContractDAOImpl(mongoClient, DB_NAME, MT_DATACONTRACT_MONITOR);
		}
		return monitoredDataContractDAO;
	}

}
