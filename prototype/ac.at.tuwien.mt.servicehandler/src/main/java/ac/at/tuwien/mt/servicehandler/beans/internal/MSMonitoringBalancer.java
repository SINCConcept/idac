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
package ac.at.tuwien.mt.servicehandler.beans.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;

import ac.at.tuwien.mt.model.helper.DefaultJSONProvider;
import ac.at.tuwien.mt.model.microservice.MicroserviceInfo;
import ac.at.tuwien.mt.model.microservice.MicroserviceType;
import ac.at.tuwien.mt.model.microservice.monitoring.QueueInfo;
import ac.at.tuwien.mt.model.thing.Thing;
import ac.at.tuwien.mt.servicehandler.beans.ms.MSManager;

/**
 * 
 * @author Florin Bogdan Balint
 *
 */
public class MSMonitoringBalancer {

	private static final Logger LOGGER = Logger.getLogger(MSMonitoringBalancer.class);

	private MongoClient mongoClient;
	private String database;
	private String thingCollection;

	public MSMonitoringBalancer(MongoClient mongoClient, String database, String thingCollection) {
		this.mongoClient = mongoClient;
		this.database = database;
		this.thingCollection = thingCollection;
	}

	public List<MicroserviceInfo> balance() throws Exception {
		LOGGER.debug("Balancing the monitoring microservice.");

		// get all monitoring microservices
		List<MicroserviceInfo> microservices = MSManager.getInstance().getMicroservices(MicroserviceType.MONITORING);

		// get all things that need to be monitored
		List<QueueInfo> queueInfos = getCompleteQueueInfoList();

		// in case any of them is null - return
		if (queueInfos.size() == 0 || microservices.size() == 0) {
			LOGGER.debug("Nothing to balance.");
			return microservices;
		}

		LOGGER.debug("Balancing between " + microservices.size() + " microservices and " + queueInfos.size() + " things.");

		// Balance equally
		Map<MicroserviceInfo, ArrayList<QueueInfo>> monitoringMap = new HashMap<MicroserviceInfo, ArrayList<QueueInfo>>();

		for (MicroserviceInfo msInfo : microservices) {
			monitoringMap.put(msInfo, new ArrayList<QueueInfo>());
		}

		int sizeMicroservices = microservices.size();
		int currentPosition = 0;

		for (int i = 0; i < queueInfos.size(); i++) {
			QueueInfo qInfo = queueInfos.get(i);
			monitoringMap.get(microservices.get(currentPosition)).add(qInfo);
			if (currentPosition < (sizeMicroservices - 1)) {
				currentPosition++;
			} else {
				currentPosition = 0;
			}
		}

		// for each monitoring microservice: send the list of things to monitor
		Set<Entry<MicroserviceInfo, ArrayList<QueueInfo>>> entrySet = monitoringMap.entrySet();
		for (Entry<MicroserviceInfo, ArrayList<QueueInfo>> entry : entrySet) {
			MicroserviceInfo msInfo = entry.getKey();
			ArrayList<QueueInfo> queuesToMonitor = entry.getValue();

			Client client = ClientBuilder.newClient();
			String url = msInfo.getProtocol() + "://" + msInfo.getHost() + ":" + msInfo.getPort() + "/" + msInfo.getPath();
			WebTarget target = client.target(url).path("monitoring/monitor");
			Entity<String> entity = Entity.entity(DefaultJSONProvider.getObjectAsJson(queuesToMonitor), MediaType.APPLICATION_JSON);

			Response response = target.request(MediaType.APPLICATION_JSON_TYPE).put(entity);

			int status = response.getStatus();
			if (status != 200) {
				LOGGER.error("Could not send a PUT request to the middleware to monitor the data flow!");
			}
			response.close();
		}

		return microservices;
	}

	private List<QueueInfo> getCompleteQueueInfoList() {
		List<QueueInfo> queueInfos = new ArrayList<QueueInfo>();

		MongoCollection<Document> collection = mongoClient.getDatabase(database).getCollection(thingCollection);
		FindIterable<Document> foundList = collection.find();

		for (Document document : foundList) {
			Thing thing = new Thing(document);
			QueueInfo queueInfo = thing.getQueueInfo();
			queueInfo.setThingId(thing.getThingId());
			if (queueInfo != null && queueInfo.getBrokerURL() != null) {
				queueInfos.add(queueInfo);
			}
		}

		return queueInfos;
	}
}
