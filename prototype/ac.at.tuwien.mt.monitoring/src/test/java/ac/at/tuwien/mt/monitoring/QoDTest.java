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
import java.util.List;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;

import ac.at.tuwien.mt.dao.monitor.ThingMonitorQoDDAO;
import ac.at.tuwien.mt.dao.monitor.ThingMonitorQoSDAO;
import ac.at.tuwien.mt.dao.monitor.impl.ThingMonitorQoDDAOImpl;
import ac.at.tuwien.mt.dao.monitor.impl.ThingMonitorQoSDAOImpl;
import ac.at.tuwien.mt.model.helper.DefaultDateProvider;
import ac.at.tuwien.mt.model.helper.DefaultIDGenerator;
import ac.at.tuwien.mt.model.thing.Thing;
import ac.at.tuwien.mt.model.thing.ThingMessage;
import ac.at.tuwien.mt.model.thing.message.Attribute;
import ac.at.tuwien.mt.model.thing.message.DataType;
import ac.at.tuwien.mt.model.thing.message.MetaModel;
import ac.at.tuwien.mt.model.thing.message.Property;
import ac.at.tuwien.mt.monitoring.thread.MessageMonitor;

/**
 * @author Florin Bogdan Balint
 *
 */
public class QoDTest {

	private static MongoClient mongoClient;
	private static ThingMonitorQoDDAO qodDAO;
	private static ThingMonitorQoSDAO qosDAO;

	@BeforeClass
	public static void beforeClass() {
		// set the mongo client options
		MongoClientOptions.Builder builder = new MongoClientOptions.Builder();
		builder.connectionsPerHost(100);
		builder.sslEnabled(false);
		builder.sslInvalidHostNameAllowed(false);
		MongoClientOptions options = builder.build();

		// set the credentials
		MongoCredential credential = MongoCredential.createCredential("usermttest1", "mttest1_local", "usermttest1_31415".toCharArray());
		List<MongoCredential> list = new ArrayList<MongoCredential>();
		list.add(credential);

		ServerAddress serverAddress = new ServerAddress("localhost", 27017);
		mongoClient = new MongoClient(serverAddress, list, options);
		qodDAO = new ThingMonitorQoDDAOImpl(mongoClient, "mttest1_local", "mt.thing.monitor.qod");
		qosDAO = new ThingMonitorQoSDAOImpl(mongoClient, "mttest1_local", "mt.thing.monitor.qos");
	}

	@AfterClass
	public static void afterClass() {
		mongoClient.close();
	}

	@Test
	public void testComplexPositiveConsistencyAndConformity() {
		String message = "{\"thingId\": \"28-8000001ebda3\", \"value\": { \"temp\": 123, \"scale\" : { \"temp\": 123, \"scale\" : \"Celsius\" } }}";
		Thing thing = new Thing();
		thing.setThingId(DefaultIDGenerator.generateID());
		thing.getQos().setFrequency(10000);
		thing.setMetaModel(getTestModel2());
		ThingMessage thingMessage = new ThingMessage(thing, message, DefaultDateProvider.getCurrentTimeStamp());
		MessageMonitor tm = new MessageMonitor(thingMessage, qodDAO, qosDAO);
		tm.monitorQoD();
		tm.monitorQoS();
		Assert.assertTrue(tm.isComplete());
		Assert.assertTrue(tm.isConform());
	}

	@Test
	public void testComplexNegativeConformity() {
		String message = "{\"thingId\": \"28-8000001ebda3\", \"value\": { \"temp\": 123, \"scale\" : { \"temp\": \"123s\", \"scale\" : \"Celsius\" } }}";
		Thing thing = new Thing();
		thing.setThingId(DefaultIDGenerator.generateID());
		thing.setMetaModel(getTestModel2());
		thing.getQos().setFrequency(10000);
		ThingMessage thingMessage = new ThingMessage(thing, message, DefaultDateProvider.getCurrentTimeStamp());
		MessageMonitor tm = new MessageMonitor(thingMessage, qodDAO, qosDAO);
		tm.monitorQoD();
		tm.monitorQoS();
		Assert.assertTrue(tm.isComplete());
		Assert.assertFalse(tm.isConform());
	}

	@Test
	public void testComplexNegative() {
		String message = "{\"thingId\": \"28-8000001ebda3\", \"value\": { \"temp\": 123, \"scales\" : { \"temp\": 123, \"scale\" : \"Celsius\" } }}";
		Thing thing = new Thing();
		thing.setThingId(DefaultIDGenerator.generateID());
		thing.setMetaModel(getTestModel2());
		thing.getQos().setFrequency(10000);
		ThingMessage thingMessage = new ThingMessage(thing, message, DefaultDateProvider.getCurrentTimeStamp());
		MessageMonitor tm = new MessageMonitor(thingMessage, qodDAO, qosDAO);
		tm.monitorQoD();
		tm.monitorQoS();
		Assert.assertFalse(tm.isComplete());
		Assert.assertTrue(tm.isConform());
	}

	@Test
	public void testSimplePositive() {
		String message = "{ \"thingId\" : \"28-8000001ebda3\", \"temperature\" : -20812, \"scale\" : \"Celsius\", \"time\" : \"2016-07-01T00:41:29.205+02:00\"}";
		Thing thing = new Thing();
		thing.setThingId(DefaultIDGenerator.generateID());
		thing.setMetaModel(getTestModel1());
		thing.getQos().setFrequency(10000);
		ThingMessage thingMessage = new ThingMessage(thing, message, DefaultDateProvider.getCurrentTimeStamp());
		MessageMonitor tm = new MessageMonitor(thingMessage, qodDAO, qosDAO);
		tm.monitorQoD();
		tm.monitorQoS();
		Assert.assertTrue(tm.isComplete());
		Assert.assertTrue(tm.isConform());
	}

	@Test
	public void testSimpleNegativeConformity() {
		String message = "{ \"thingId\" : \"28-8000001ebda3\", \"temperature\" : -20812, \"scale\" : \"Celsius\", \"time\" : false, \"timePattern\" : \"yyyy-MM-dd'T'HH:mm:ss.SSSXXX\" }";
		Thing thing = new Thing();
		thing.setThingId(DefaultIDGenerator.generateID());
		thing.setMetaModel(getTestModel1());
		thing.getQos().setFrequency(10000);
		ThingMessage thingMessage = new ThingMessage(thing, message, DefaultDateProvider.getCurrentTimeStamp());
		MessageMonitor tm = new MessageMonitor(thingMessage, qodDAO, qosDAO);
		tm.monitorQoD();
		tm.monitorQoS();
		Assert.assertTrue(tm.isComplete());
		Assert.assertFalse(tm.isConform());
	}

	@Test
	public void testSimpleNegative() {
		String message = "{ \"thingId\" : \"28-8000001ebda3\", \"temperature2\" : \"-20812\", \"scale\" : \"Celsius\", \"time\" : \"2016-07-01T00:41:29.205+02:00\"}";
		Thing thing = new Thing();
		thing.setThingId(DefaultIDGenerator.generateID());
		thing.setMetaModel(getTestModel1());
		thing.getQos().setFrequency(10000);
		ThingMessage thingMessage = new ThingMessage(thing, message, DefaultDateProvider.getCurrentTimeStamp());
		MessageMonitor tm = new MessageMonitor(thingMessage, qodDAO, qosDAO);
		tm.monitorQoD();
		tm.monitorQoS();
		Assert.assertFalse(tm.isComplete());
		Assert.assertTrue(tm.isConform());
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
		attribute4.setProperty(a4Property);

		MetaModel model1 = new MetaModel();
		model1.getAttributes().add(attribute1);
		model1.getAttributes().add(attribute2);
		model1.getAttributes().add(attribute3);
		model1.getAttributes().add(attribute4);
		return model1;
	}

	private MetaModel getTestModel2() {
		Attribute attribute1 = new Attribute();
		attribute1.setDataType(DataType.STRING);
		attribute1.setName("thingId");
		Property property = new Property();
		property.setIdentifier(true);
		attribute1.setProperty(property);

		Attribute attribute2 = new Attribute();
		attribute2.setDataType(DataType.ATTRIBUTE);
		attribute2.setName("value");

		Attribute attribute3 = new Attribute();
		attribute3.setDataType(DataType.DOUBLE);
		attribute3.setName("temp");

		Attribute attribute4 = new Attribute();
		attribute4.setDataType(DataType.ATTRIBUTE);
		attribute4.setName("scale");

		Attribute attribute5 = new Attribute();
		attribute5.setDataType(DataType.DOUBLE);
		attribute5.setName("temp");

		Attribute attribute6 = new Attribute();
		attribute6.setDataType(DataType.STRING);
		attribute6.setName("scale");

		MetaModel model3 = new MetaModel();
		model3.getAttributes().add(attribute5);
		model3.getAttributes().add(attribute6);
		attribute4.setMetaModel(model3);

		MetaModel model2 = new MetaModel();
		model2.getAttributes().add(attribute3);
		model2.getAttributes().add(attribute4);
		attribute2.setMetaModel(model2);

		MetaModel model1 = new MetaModel();
		model1.getAttributes().add(attribute1);
		model1.getAttributes().add(attribute2);

		return model1;
	}
}
