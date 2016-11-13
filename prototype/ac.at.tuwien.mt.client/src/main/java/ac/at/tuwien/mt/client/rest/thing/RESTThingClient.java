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
package ac.at.tuwien.mt.client.rest.thing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.bson.Document;

import ac.at.tuwien.mt.model.helper.DefaultJSONProvider;
import ac.at.tuwien.mt.model.microservice.MicroserviceInfo;
import ac.at.tuwien.mt.model.thing.Thing;

/**
 * @author Florin Bogdan Balint
 *
 */
public class RESTThingClient {

	private MicroserviceInfo microserviceInfo;

	public RESTThingClient(MicroserviceInfo microserviceInfo) {
		this.microserviceInfo = microserviceInfo;
	}

	/**
	 * Inserts a thing into the database
	 * 
	 * @param thing
	 * @return inserted Thing.
	 */
	public Thing insertThing(Thing thing) {
		Client client = ClientBuilder.newClient();
		String uri = microserviceInfo.getProtocol() + "://" + microserviceInfo.getHost() + ":" + microserviceInfo.getPort();
		WebTarget target = client.target(uri).path("rest/things/create");
		Entity<String> entity = Entity.entity(DefaultJSONProvider.getObjectAsJson(thing), MediaType.APPLICATION_JSON);
		Response response = target.request(MediaType.APPLICATION_JSON_TYPE).put(entity);
		String responseAsString = response.readEntity(String.class);
		response.close();

		Document doc = Document.parse(responseAsString);
		Thing thingResponse = new Thing(doc);
		return thingResponse;
	}

	/**
	 * Updates the respective thing.
	 * 
	 * @param thing
	 * @return updated Thing.
	 */
	public Thing updateThing(Thing thing) {
		Client client = ClientBuilder.newClient();
		String uri = microserviceInfo.getProtocol() + "://" + microserviceInfo.getHost() + ":" + microserviceInfo.getPort();
		WebTarget target = client.target(uri).path("rest/things/update");
		Entity<String> entity = Entity.entity(DefaultJSONProvider.getObjectAsJson(thing), MediaType.APPLICATION_JSON);
		Response response = target.request(MediaType.APPLICATION_JSON_TYPE).post(entity);
		String responseAsString = response.readEntity(String.class);
		response.close();

		Document doc = Document.parse(responseAsString);
		Thing thingResponse = new Thing(doc);
		return thingResponse;
	}

	/**
	 * Deletes the respective thing based on its ID.
	 * 
	 * @param thing
	 * @return deleted Thing.
	 */
	public Thing deleteThing(Thing thing) {
		Client client = ClientBuilder.newClient();
		String uri = microserviceInfo.getProtocol() + "://" + microserviceInfo.getHost() + ":" + microserviceInfo.getPort();
		WebTarget target = client.target(uri).path("rest/things/delete");
		Entity<String> entity = Entity.entity(DefaultJSONProvider.getObjectAsJson(thing), MediaType.APPLICATION_JSON);
		Response response = target.request(MediaType.APPLICATION_JSON_TYPE).post(entity);
		String responseAsString = response.readEntity(String.class);
		response.close();

		Document doc = Document.parse(responseAsString);
		Thing thingResponse = new Thing(doc);
		return thingResponse;
	}

	/**
	 * Finds all things, where the owner id equals to the provided ownerid.
	 * 
	 * @param ownerid
	 * @return List<Thing>
	 */
	public List<Thing> findThingsForOwner(String ownerid) {
		List<Thing> things = new ArrayList<Thing>();
		Client client = ClientBuilder.newClient();
		String uri = microserviceInfo.getProtocol() + "://" + microserviceInfo.getHost() + ":" + microserviceInfo.getPort();
		WebTarget target = client.target(uri).path("rest/things/owner/" + ownerid);
		Response response = target.request(MediaType.APPLICATION_JSON_TYPE).get();
		String responseAsString = response.readEntity(String.class);
		response.close();

		Thing[] list = (Thing[]) ac.at.tuwien.mt.model.helper.DefaultJSONProvider.getObjectFromJson(responseAsString, Thing[].class);
		things = Arrays.asList(list);

		return things;
	}

	/**
	 * Finds all things, where the owner id does not equal to the provided
	 * ownerid.
	 * 
	 * @param ownerid
	 * @return List<Thing>
	 */
	public List<Thing> findThingsExceptOwner(String ownerid) {
		List<Thing> things = new ArrayList<Thing>();
		Client client = ClientBuilder.newClient();
		String uri = microserviceInfo.getProtocol() + "://" + microserviceInfo.getHost() + ":" + microserviceInfo.getPort();
		WebTarget target = client.target(uri).path("rest/things/nowner/" + ownerid);
		Response response = target.request(MediaType.APPLICATION_JSON_TYPE).get();
		String responseAsString = response.readEntity(String.class);
		response.close();

		Thing[] list = (Thing[]) ac.at.tuwien.mt.model.helper.DefaultJSONProvider.getObjectFromJson(responseAsString, Thing[].class);
		things = Arrays.asList(list);

		return things;
	}

}
