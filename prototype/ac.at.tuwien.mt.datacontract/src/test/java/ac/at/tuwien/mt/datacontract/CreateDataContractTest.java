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
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import ac.at.tuwien.mt.model.datacontract.DataContract;

public class CreateDataContractTest {

	private static final Logger LOGGER = LogManager.getLogger(CreateDataContractTest.class);

	private static BasicCamelStarter camelStarter = new BasicCamelStarter();

	private static final String HTTP_SERVER = "http://localhost:12770";
	private static DataContract dataContract = getDataContract();

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

	@Test
	public void testCreateDataContract() {
		Client client = ClientBuilder.newClient();
		WebTarget target = client.target(HTTP_SERVER).path("rest/datacontracts");
		Entity<String> entity = Entity.entity(dataContract.getDocument().toJson(), MediaType.APPLICATION_JSON);
		Response response = target.request(MediaType.APPLICATION_JSON_TYPE).put(entity);

		int status = response.getStatus();
		Assert.assertNotEquals(500, status);

		String responseAsString = response.readEntity(String.class);
		response.close();

		Document doc = Document.parse(responseAsString);
		DataContract responseAsDataContract = new DataContract(doc);

		// check if the fields are set.
		Assert.assertFalse(responseAsDataContract.getDataContractMetaInfo().getActive());
		Assert.assertNotNull(responseAsDataContract.getDataContractMetaInfo().getContractId());
		Assert.assertNotNull(responseAsDataContract.getDataContractMetaInfo().getCreationDate());
		Assert.assertEquals(1, responseAsDataContract.getDataContractMetaInfo().getRevision().intValue());
	}

	private static DataContract getDataContract() {
		dataContract = new DataContract();
		dataContract.getDataContractMetaInfo().setParty1Id("123");
		dataContract.getDataContractMetaInfo().setParty2Id("234");

		return dataContract;
	}

}
