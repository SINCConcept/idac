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

import java.util.Arrays;
import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import ac.at.tuwien.mt.gui.internal.WebServiceProperty;
import ac.at.tuwien.mt.gui.internal.WebServicesProvider;
import ac.at.tuwien.mt.gui.primefaces.exception.CommunicationException;
import ac.at.tuwien.mt.model.helper.DefaultJSONProvider;
import ac.at.tuwien.mt.model.microservice.MicroserviceInfo;
import ac.at.tuwien.mt.model.microservice.monitoring.QueueInfo;
import ac.at.tuwien.mt.model.thing.monitor.MonitoredDataContract;
import ac.at.tuwien.mt.model.thing.monitor.MonitoredQoD;
import ac.at.tuwien.mt.model.thing.monitor.MonitoredQoS;

/**
 * @author Florin Bogdan Balint
 *
 */
public class RESTMonitoringClient {

	private MicroserviceInfo microserviceInfo;

	public RESTMonitoringClient(MicroserviceInfo microserviceInfo) {
		this.microserviceInfo = microserviceInfo;
	}

	public QueueInfo getQueueInfo() {
		Client client = ClientBuilder.newClient();
		String uri = microserviceInfo.getProtocol() + "://" + microserviceInfo.getHost() + ":" + microserviceInfo.getPort();
		WebTarget target = client.target(uri).path("rest/monitoring/brokerurl");
		Response response = target.request(MediaType.APPLICATION_JSON_TYPE).get();
		String responseAsString = response.readEntity(String.class);
		response.close();

		QueueInfo queueInfo = (QueueInfo) DefaultJSONProvider.getObjectFromJson(responseAsString, QueueInfo.class);
		return queueInfo;
	}

	public static MonitoredQoD findQoD(String thingId) throws CommunicationException {
		Client client = ClientBuilder.newClient();
		WebTarget target = client.target(WebServicesProvider.getString(WebServiceProperty.REST_MIDDLEWARE_BASEURL))
				.path(WebServicesProvider.getString(WebServiceProperty.REST_MIDDLEWARE_MICROSERVICE_PATH) + "monitoring/qod/" + thingId);

		Response response = target.request(MediaType.APPLICATION_JSON_TYPE).get();
		int status = response.getStatus();
		if (status != 200) {
			throw new CommunicationException("Could not connect to the server.");
		}
		String responseAsString = (String) response.readEntity(String.class);
		response.close();

		MonitoredQoD result = (MonitoredQoD) DefaultJSONProvider.getObjectFromJson(responseAsString, MonitoredQoD.class);
		return result;
	}

	public static MonitoredQoS findQoS(String thingId) throws CommunicationException {
		Client client = ClientBuilder.newClient();
		WebTarget target = client.target(WebServicesProvider.getString(WebServiceProperty.REST_MIDDLEWARE_BASEURL))
				.path(WebServicesProvider.getString(WebServiceProperty.REST_MIDDLEWARE_MICROSERVICE_PATH) + "monitoring/qos/" + thingId);

		Response response = target.request(MediaType.APPLICATION_JSON_TYPE).get();
		int status = response.getStatus();
		if (status != 200) {
			throw new CommunicationException("Could not connect to the server.");
		}
		String responseAsString = (String) response.readEntity(String.class);
		response.close();

		MonitoredQoS result = (MonitoredQoS) DefaultJSONProvider.getObjectFromJson(responseAsString, MonitoredQoS.class);
		return result;
	}

	public static List<MonitoredDataContract> findMonitoredDataContract(String contractId) throws CommunicationException {
		Client client = ClientBuilder.newClient();
		WebTarget target = client.target(WebServicesProvider.getString(WebServiceProperty.REST_MIDDLEWARE_BASEURL))
				.path(WebServicesProvider.getString(WebServiceProperty.REST_MIDDLEWARE_MICROSERVICE_PATH) + "monitoring/dc/" + contractId);

		Response response = target.request(MediaType.APPLICATION_JSON_TYPE).get();
		int status = response.getStatus();
		if (status != 200) {
			throw new CommunicationException("Could not connect to the server.");
		}
		String responseAsString = (String) response.readEntity(String.class);
		response.close();

		MonitoredDataContract[] list = (MonitoredDataContract[]) ac.at.tuwien.mt.model.helper.DefaultJSONProvider.getObjectFromJson(responseAsString, MonitoredDataContract[].class);
		List<MonitoredDataContract> resultList = Arrays.asList(list);
		return resultList;
	}

}
