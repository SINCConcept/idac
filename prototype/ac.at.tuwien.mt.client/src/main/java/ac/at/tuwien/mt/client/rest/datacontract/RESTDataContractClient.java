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
package ac.at.tuwien.mt.client.rest.datacontract;

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

import ac.at.tuwien.mt.model.datacontract.DataContract;
import ac.at.tuwien.mt.model.datacontract.DataContractTrail;
import ac.at.tuwien.mt.model.helper.DefaultJSONProvider;
import ac.at.tuwien.mt.model.microservice.MicroserviceInfo;
import ac.at.tuwien.mt.model.thing.Thing;

/**
 * @author Florin Bogdan Balint
 *
 */
public class RESTDataContractClient {

	private MicroserviceInfo microserviceInfo;

	public RESTDataContractClient(MicroserviceInfo microserviceInfo) {
		this.microserviceInfo = microserviceInfo;
	}

	public DataContract insert(DataContract dc) {
		Client client = ClientBuilder.newClient();
		String uri = microserviceInfo.getProtocol() + "://" + microserviceInfo.getHost() + ":" + microserviceInfo.getPort();
		WebTarget target = client.target(uri).path("rest/datacontracts");
		Entity<String> entity = Entity.entity(DefaultJSONProvider.getObjectAsJson(dc), MediaType.APPLICATION_JSON);
		Response response = target.request(MediaType.APPLICATION_JSON_TYPE).put(entity);
		String responseAsString = response.readEntity(String.class);
		response.close();

		Document doc = Document.parse(responseAsString);
		DataContract thingResponse = new DataContract(doc);
		return thingResponse;
	}

	public DataContract update(DataContract dc) {
		Client client = ClientBuilder.newClient();
		String uri = microserviceInfo.getProtocol() + "://" + microserviceInfo.getHost() + ":" + microserviceInfo.getPort();
		WebTarget target = client.target(uri).path("rest/datacontracts");
		Entity<String> entity = Entity.entity(DefaultJSONProvider.getObjectAsJson(dc), MediaType.APPLICATION_JSON);
		Response response = target.request(MediaType.APPLICATION_JSON_TYPE).post(entity);
		String responseAsString = response.readEntity(String.class);
		response.close();

		Document doc = Document.parse(responseAsString);
		DataContract thingResponse = new DataContract(doc);
		return thingResponse;
	}

	public List<DataContract> findProviderOpenContracts(String personId) {
		Client client = ClientBuilder.newClient();
		String uri = microserviceInfo.getProtocol() + "://" + microserviceInfo.getHost() + ":" + microserviceInfo.getPort();
		WebTarget target = client.target(uri).path("rest/datacontracts/provider/open/" + personId);
		Response response = target.request(MediaType.APPLICATION_JSON_TYPE).get();
		String responseAsString = response.readEntity(String.class);
		response.close();

		DataContract[] list = (DataContract[]) ac.at.tuwien.mt.model.helper.DefaultJSONProvider.getObjectFromJson(responseAsString, DataContract[].class);
		List<DataContract> dataContracts = Arrays.asList(list);
		return dataContracts;
	}

	public List<DataContract> findBuyerOpenContracts(String personId) {
		Client client = ClientBuilder.newClient();
		String uri = microserviceInfo.getProtocol() + "://" + microserviceInfo.getHost() + ":" + microserviceInfo.getPort();
		WebTarget target = client.target(uri).path("rest/datacontracts/buyer/open/" + personId);
		Response response = target.request(MediaType.APPLICATION_JSON_TYPE).get();
		String responseAsString = response.readEntity(String.class);
		response.close();

		DataContract[] list = (DataContract[]) ac.at.tuwien.mt.model.helper.DefaultJSONProvider.getObjectFromJson(responseAsString, DataContract[].class);
		List<DataContract> dataContracts = Arrays.asList(list);
		return dataContracts;
	}

	public List<DataContract> findProviderConcludedContracts(String personId) {
		Client client = ClientBuilder.newClient();
		String uri = microserviceInfo.getProtocol() + "://" + microserviceInfo.getHost() + ":" + microserviceInfo.getPort();
		WebTarget target = client.target(uri).path("rest/datacontracts/provider/concluded/" + personId);
		Response response = target.request(MediaType.APPLICATION_JSON_TYPE).get();
		String responseAsString = response.readEntity(String.class);
		response.close();

		DataContract[] list = (DataContract[]) ac.at.tuwien.mt.model.helper.DefaultJSONProvider.getObjectFromJson(responseAsString, DataContract[].class);
		List<DataContract> dataContracts = Arrays.asList(list);
		return dataContracts;
	}

	public List<DataContract> findBuyerConcludedContracts(String personId) {
		Client client = ClientBuilder.newClient();
		String uri = microserviceInfo.getProtocol() + "://" + microserviceInfo.getHost() + ":" + microserviceInfo.getPort();
		WebTarget target = client.target(uri).path("rest/datacontracts/buyer/concluded/" + personId);
		Response response = target.request(MediaType.APPLICATION_JSON_TYPE).get();
		String responseAsString = response.readEntity(String.class);
		response.close();

		DataContract[] list = (DataContract[]) ac.at.tuwien.mt.model.helper.DefaultJSONProvider.getObjectFromJson(responseAsString, DataContract[].class);
		List<DataContract> dataContracts = Arrays.asList(list);
		return dataContracts;
	}

	public DataContractTrail getDataContractTrail(String contractId) {
		Client client = ClientBuilder.newClient();
		String uri = microserviceInfo.getProtocol() + "://" + microserviceInfo.getHost() + ":" + microserviceInfo.getPort();
		WebTarget target = client.target(uri).path("rest/datacontracttrail/" + contractId);
		Response response = target.request(MediaType.APPLICATION_JSON_TYPE).get();
		String responseAsString = response.readEntity(String.class);
		response.close();

		Document doc = Document.parse(responseAsString);
		DataContractTrail trail = new DataContractTrail(doc);
		return trail;
	}

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

	public List<Thing> find(String ownerid) {
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

	public List<Thing> findAllThingsExcept(String ownerid) {
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
