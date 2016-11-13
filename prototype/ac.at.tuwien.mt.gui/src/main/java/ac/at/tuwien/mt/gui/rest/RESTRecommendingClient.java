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
package ac.at.tuwien.mt.gui.rest;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import ac.at.tuwien.mt.gui.primefaces.exception.CommunicationException;
import ac.at.tuwien.mt.model.helper.DefaultJSONProvider;
import ac.at.tuwien.mt.model.microservice.MicroserviceInfo;
import ac.at.tuwien.mt.model.thing.Thing;

/**
 * @author Florin Bogdan Balint
 *
 */
public class RESTRecommendingClient {

	private MicroserviceInfo microserviceInfo;

	public RESTRecommendingClient(MicroserviceInfo microserviceInfo) {
		this.microserviceInfo = microserviceInfo;
	}

	public Thing recommend(String userId) throws CommunicationException {

		Client client = ClientBuilder.newClient();
		String uri = microserviceInfo.getProtocol() + "://" + microserviceInfo.getHost() + ":" + microserviceInfo.getPort();
		WebTarget target = client.target(uri).path("rest/recommending/recommend/" + userId);
		Response response = target.request(MediaType.APPLICATION_JSON_TYPE).get();

		int status = response.getStatus();
		if (!(status == 204 || status == 200)) {
			throw new CommunicationException("Could not communicate correctly with server.");
		}

		if (status == 204) {
			response.close();
			return null;
		}

		// now we have the status 200
		String responseAsString = response.readEntity(String.class);
		response.close();

		Thing result = (Thing) DefaultJSONProvider.getObjectFromJson(responseAsString, Thing.class);
		return result;
	}

	public Integer getThingAvgNeg(String thingId) throws CommunicationException {

		Client client = ClientBuilder.newClient();
		String uri = microserviceInfo.getProtocol() + "://" + microserviceInfo.getHost() + ":" + microserviceInfo.getPort();
		WebTarget target = client.target(uri).path("rest/recommending/thingavgneg/" + thingId);
		Response response = target.request(MediaType.APPLICATION_JSON_TYPE).get();

		int status = response.getStatus();
		if (!(status == 204 || status == 200)) {
			throw new CommunicationException("Could not communicate correctly with server.");
		}

		if (status == 204) {
			response.close();
			return null;
		}

		// now we have the status 200
		String responseAsString = response.readEntity(String.class);
		response.close();

		int result = Integer.parseInt(responseAsString);
		return result;
	}

}
