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
package ac.at.tuwien.mt.client.rest;

import javax.ws.rs.ServiceUnavailableException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;

import ac.at.tuwien.mt.client.internal.WebServiceProperty;
import ac.at.tuwien.mt.client.internal.WebServicesProvider;
import ac.at.tuwien.mt.model.microservice.MicroserviceInfo;
import ac.at.tuwien.mt.model.microservice.MicroserviceType;

/**
 * @author Florin Bogdan Balint
 *
 */
public final class RESTMicroserviceLocator {

	private static final Logger LOGGER = LogManager.getLogger(RESTMicroserviceLocator.class);

	protected RESTMicroserviceLocator() {
		// defeat instantiation
	}

	public static MicroserviceInfo locateMicroservice(MicroserviceType microserviceType) {
		LOGGER.debug("Locating microservice: " + microserviceType);
		Client client = ClientBuilder.newClient();
		WebTarget target = client.target(WebServicesProvider.getString(WebServiceProperty.REST_MIDDLEWARE_BASEURL))
				.path(WebServicesProvider.getString(WebServiceProperty.REST_MIDDLEWARE_MICROSERVICE_PATH) //
						+ WebServicesProvider.getString(WebServiceProperty.REST_MIDDLEWARE_MICROSERVICE_LOCATION_PATH) //
						+ microserviceType.getProperty()); //
		LOGGER.debug("Calling: " + target.getUri());
		Response response = target.request(MediaType.APPLICATION_JSON_TYPE).get();

		String responseAsString = null;
		int status = response.getStatus();

		if (status == 503) {
			throw new ServiceUnavailableException();
		}

		if (status == 200) {
			responseAsString = (String) response.readEntity(String.class);
		}
		response.close();

		if (responseAsString == null) {
			throw new ServiceUnavailableException();
		}

		MicroserviceInfo msInfo = new MicroserviceInfo(Document.parse(responseAsString));
		return msInfo;
	}
}
