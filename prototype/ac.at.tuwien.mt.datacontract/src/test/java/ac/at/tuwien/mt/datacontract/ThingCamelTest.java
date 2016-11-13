/*
 * Copyright (c) 2016. All rights reserved.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS".
 * IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES.
 * 
 * Author: Florin Bogdan Balint
 * resttest
 */

package ac.at.tuwien.mt.datacontract;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ac.at.tuwien.mt.common.test.dao.DAOTest;
import ac.at.tuwien.mt.common.test.dao.MongoDBPropertiesProvider;
import ac.at.tuwien.mt.common.test.dao.MongoDBProperty;
import ac.at.tuwien.mt.model.datacontract.clause.Currency;
import ac.at.tuwien.mt.model.datacontract.clause.DataRights;
import ac.at.tuwien.mt.model.datacontract.clause.PricingModel;
import ac.at.tuwien.mt.model.datacontract.clause.QoD;
import ac.at.tuwien.mt.model.helper.DefaultJSONProvider;
import ac.at.tuwien.mt.model.thing.Thing;

public class ThingCamelTest extends DAOTest {

	private static final Logger LOGGER = LogManager.getLogger(ThingCamelTest.class);

	private static BasicCamelStarter camelStarter = new BasicCamelStarter();

	private static final String HTTP_SERVER = "http://localhost:12770";

	private static final String THING_COLLECTION = MongoDBPropertiesProvider.getString(MongoDBProperty.MONGO_DB_COLLECTION_THING);
	private static final String DC_COLLECTION = MongoDBPropertiesProvider.getString(MongoDBProperty.MONGO_DB_COLLECTION_DATACONTRACT);
	private static final String DC_TRAIL_COLLECTION = MongoDBPropertiesProvider.getString(MongoDBProperty.MONGO_DB_COLLECTION_DATACONTRACT_TRAIL);

	@BeforeClass
	public static void setUp() {
		camelStarter.start();
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			LOGGER.error(e, e.getCause());
		}
	}

	@AfterClass
	public static void tearDown() {
		camelStarter.cancel();
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			LOGGER.error(e, e.getCause());
		}
	}

	@Before
	public void before() {
		mongoClient.getDatabase(DB_NAME).getCollection(THING_COLLECTION).drop();
		mongoClient.getDatabase(DB_NAME).getCollection(DC_COLLECTION).drop();
		mongoClient.getDatabase(DB_NAME).getCollection(DC_TRAIL_COLLECTION).drop();
	}

	@After
	public void after() {
		mongoClient.getDatabase(DB_NAME).getCollection(THING_COLLECTION).drop();
		mongoClient.getDatabase(DB_NAME).getCollection(DC_COLLECTION).drop();
		mongoClient.getDatabase(DB_NAME).getCollection(DC_TRAIL_COLLECTION).drop();
	}

	@Test
	public void testInsertThing() {
		Client client = ClientBuilder.newClient();

		// INSERT
		WebTarget target = client.target(HTTP_SERVER).path("rest/things/create");
		Thing thing = getSampleThing();
		Entity<String> entity = Entity.entity(DefaultJSONProvider.getObjectAsJson(thing), MediaType.APPLICATION_JSON);
		Response response = target.request(MediaType.APPLICATION_JSON_TYPE).put(entity);

		int status = response.getStatus();
		Assert.assertEquals(200, status);

		String responseAsString = response.readEntity(String.class);
		response.close();

		Document doc = Document.parse(responseAsString);
		Thing thingResponse = new Thing(doc);

		// check if the fields are set.
		Assert.assertNotNull(thingResponse.getCreationDate());
		Assert.assertEquals(1, thingResponse.getRevision().intValue());
	}

	@Test
	public void testUpdateNegative() {
		// insert data
		Client client = ClientBuilder.newClient();
		Thing thing = getSampleThing();
		WebTarget target = client.target(HTTP_SERVER).path("rest/things/create");
		Entity<String> entity = Entity.entity(DefaultJSONProvider.getObjectAsJson(thing), MediaType.APPLICATION_JSON);
		Response response = target.request(MediaType.APPLICATION_JSON_TYPE).put(entity);

		// UPDATE - negative test
		target = client.target(HTTP_SERVER).path("rest/things/update");
		entity = Entity.entity(DefaultJSONProvider.getObjectAsJson(thing), MediaType.APPLICATION_JSON);
		response = target.request(MediaType.APPLICATION_JSON_TYPE).post(entity);

		int status = response.getStatus();
		Assert.assertEquals(400, status);
		response.close();
	}

	@Test
	public void testUpdatePositive() {
		// insert data
		Client client = ClientBuilder.newClient();
		Thing thing = getSampleThing();
		WebTarget target = client.target(HTTP_SERVER).path("rest/things/create");
		Entity<String> entity = Entity.entity(DefaultJSONProvider.getObjectAsJson(thing), MediaType.APPLICATION_JSON);
		Response response = target.request(MediaType.APPLICATION_JSON_TYPE).put(entity);

		String responseAsString = response.readEntity(String.class);
		response.close();

		Document doc = Document.parse(responseAsString);
		Thing thingResponse = new Thing(doc);

		target = client.target(HTTP_SERVER).path("rest/things/update");
		entity = Entity.entity(DefaultJSONProvider.getObjectAsJson(thingResponse), MediaType.APPLICATION_JSON);
		response = target.request(MediaType.APPLICATION_JSON_TYPE).post(entity);

		int status = response.getStatus();
		Assert.assertEquals(200, status);
		responseAsString = response.readEntity(String.class);
		response.close();
		doc = Document.parse(responseAsString);
		thingResponse = new Thing(doc);

		// check if the fields are set.
		Assert.assertEquals(2, thingResponse.getRevision().intValue());
	}

	@Test
	public void testSearch() {
		// insert data
		Client client = ClientBuilder.newClient();
		Thing thing = getSampleThing();
		WebTarget target = client.target(HTTP_SERVER).path("rest/things/create");
		Entity<String> entity = Entity.entity(DefaultJSONProvider.getObjectAsJson(thing), MediaType.APPLICATION_JSON);
		Response response = target.request(MediaType.APPLICATION_JSON_TYPE).put(entity);

		// SEARCH - positive test
		target = client.target(HTTP_SERVER).path("rest/things/owner/owner123");
		response = target.request(MediaType.APPLICATION_JSON_TYPE).get();
		int status = response.getStatus();
		Assert.assertEquals(200, status);
		response.close();

		// NOT OWNER SEARCH - positive test
		target = client.target(HTTP_SERVER).path("rest/things/nowner/owner123");
		response = target.request(MediaType.APPLICATION_JSON_TYPE).get();
		status = response.getStatus();
		Assert.assertEquals(200, status);
		response.close();
	}

	@Test
	public void testDelete() {
		// insert data
		Client client = ClientBuilder.newClient();
		Thing thing = getSampleThing();
		WebTarget target = client.target(HTTP_SERVER).path("rest/things/create");
		Entity<String> entity = Entity.entity(DefaultJSONProvider.getObjectAsJson(thing), MediaType.APPLICATION_JSON);
		Response response = target.request(MediaType.APPLICATION_JSON_TYPE).put(entity);

		String responseAsString = response.readEntity(String.class);
		response.close();

		Document doc = Document.parse(responseAsString);
		Thing thingResponse = new Thing(doc);

		// DELETE - positive test
		thing.setRevision(2);
		target = client.target(HTTP_SERVER).path("rest/things/delete");
		entity = Entity.entity(DefaultJSONProvider.getObjectAsJson(thingResponse), MediaType.APPLICATION_JSON);
		response = target.request(MediaType.APPLICATION_JSON_TYPE).post(entity);

		int status = response.getStatus();
		Assert.assertEquals(200, status);
	}

	private static Thing getSampleThing() {
		Thing device1 = new Thing();
		device1.setResourceId("resource123");
		device1.setOwnerId("owner123");
		device1.setDataSample("{'temp','21°C'}");

		DataRights dataRights = new DataRights();
		dataRights.setCollection(true);
		dataRights.setCommercialUsage(true);
		dataRights.setDerivation(true);
		dataRights.setReproduction(true);
		device1.setDataRights(dataRights);

		QoD qod = new QoD();
		qod.setAccuracy(new Double(0.9));
		qod.setCompleteness(new Double(0.9));
		qod.setConsistency(new Double(0.9));
		qod.setCurrency(new Double(0.9));
		qod.setTimeliness(new Double(0.9));
		device1.setQod(qod);

		PricingModel pricingModel = new PricingModel();
		pricingModel.setCurrency(Currency.EUR);
		pricingModel.setPrice(new Double(0.01));
		pricingModel.setNumberOfTransactions(1);
		pricingModel.setTransaction(true);
		device1.setPricingModel(pricingModel);
		return device1;
	}

}
