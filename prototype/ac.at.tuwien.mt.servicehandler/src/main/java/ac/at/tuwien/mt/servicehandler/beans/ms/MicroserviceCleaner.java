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
package ac.at.tuwien.mt.servicehandler.beans.ms;

import java.util.Iterator;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.mongodb.MongoClient;

import ac.at.tuwien.mt.model.microservice.MicroserviceInfo;
import ac.at.tuwien.mt.model.microservice.MicroserviceType;
import ac.at.tuwien.mt.servicehandler.beans.internal.MSMonitoringBalancer;

/**
 * @author Florin Bogdan Balint
 *
 */
@Component
public class MicroserviceCleaner {

	private static final Logger LOGGER = Logger.getLogger(MicroserviceCleaner.class);

	private MongoClient mongoClient;

	@Value("${mongo_db_name}")
	private String database;
	@Value("${mongo_db_collection_thing}")
	private String thingCollection;

	@Autowired
	public MicroserviceCleaner(MongoClient mongoClient) {
		this.mongoClient = mongoClient;
	}

	/**
	 * This cron job is rebalancing regularly the load - just in case it is
	 * missed out by some other component.
	 */
	@Scheduled(cron = "*/60 * * * * ?")
	public void balanceMonitoringMicroservices() {
		try {
			LOGGER.info("Redistributing load among remaining monitoring microservices.");
			MSMonitoringBalancer monitoringBalancer = new MSMonitoringBalancer(mongoClient, database, thingCollection);
			monitoringBalancer.balance();
		} catch (Exception e) {
			LOGGER.error(e);
		}
	}

	@Scheduled(cron = "*/10 * * * * ?")
	public void cleanMicroservices() {
		LOGGER.debug("Checking if microservices are still alive.");
		Iterator<MicroserviceInfo> iterator = MSManager.getInstance().getMicroservices().iterator();
		while (iterator.hasNext()) {
			MicroserviceInfo next = iterator.next();
			boolean isAlive = isMicroserviceStillAlive(next);
			if (!isAlive) {
				iterator.remove();

				// was it a monitoring component
				if (next.getMicroserviceType() == MicroserviceType.MONITORING) {
					// redistribute the load
					try {
						LOGGER.info("Redistributing load among remaining monitoring microservices.");
						MSMonitoringBalancer monitoringBalancer = new MSMonitoringBalancer(mongoClient, database, thingCollection);
						monitoringBalancer.balance();
					} catch (Exception e) {
						LOGGER.error(e);
					}
				}

			}
		}
		LOGGER.debug(MSManager.getInstance().getMicroservices().size() + " microservice(s) found.");
	}

	/**
	 * Checks if a microservice is alive by trying to access its 'test' service.
	 * 
	 * @param microservice
	 * @return
	 */
	private boolean isMicroserviceStillAlive(MicroserviceInfo microservice) {
		String baseURL = microservice.getProtocol() + "://" + microservice.getHost() + ":" + microservice.getPort();
		Response response = null;
		try {
			Client client = ClientBuilder.newClient();
			WebTarget target = client.target(baseURL).path("ping");
			response = target.request().get();
			int status = response.getStatus();
			response.close();
			if (status == 204) {
				return true;
			}
		} catch (Exception e) {
			// in case of any exception remove it from the list
			return false;
		} finally {
			if (response != null) {
				response.close();
			}
		}
		return false;
	}
}
