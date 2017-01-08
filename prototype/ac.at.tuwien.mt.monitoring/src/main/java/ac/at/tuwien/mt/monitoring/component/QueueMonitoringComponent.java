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

package ac.at.tuwien.mt.monitoring.component;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.mongodb.MongoClient;

import ac.at.tuwien.mt.dao.datacontract.DataContractDAO;
import ac.at.tuwien.mt.dao.datacontract.impl.DataContractDAOImpl;
import ac.at.tuwien.mt.dao.monitor.MonitoredDataContractDAO;
import ac.at.tuwien.mt.dao.monitor.impl.MonitoredDataContractDAOImpl;
import ac.at.tuwien.mt.dao.thing.ThingDAO;
import ac.at.tuwien.mt.dao.thing.impl.ThingDAOImpl;
import ac.at.tuwien.mt.model.datacontract.DataContract;
import ac.at.tuwien.mt.model.microservice.monitoring.QueueInfo;
import ac.at.tuwien.mt.model.thing.Thing;
import ac.at.tuwien.mt.monitoring.thread.QueueMonitor;
import ac.at.tuwien.mt.monitoring.thread.QueueMonitoringManager;

/**
 * @author Florin Bogdan Balint
 *
 */
@Component
public class QueueMonitoringComponent {

	private static final Logger LOGGER = Logger.getLogger(QueueMonitoringComponent.class);

	private MongoClient mongoClient;

	@Value("${mongo_db_name}")
	private String database;
	@Value("${mongo_db_collection_datacontract}")
	private String dataContractCollection;
	@Value("${mongo_db_collection_thing}")
	private String thingCollection;
	@Value("${mongo_db_collection_thing_monitor_qod}")
	private String monitorThingQoDCollection;
	@Value("${mongo_db_collection_thing_monitor_qos}")
	private String monitorThingQoSCollection;
	@Value("${mongo_db_collection_dc_monitor}")
	private String monitorDCCollection;

	private static Map<String, Map<Thing, List<DataContract>>> qm = Collections.synchronizedMap(new HashMap<>());

	private ThingDAO thingDAO;
	private DataContractDAO dataContractDAO;
	private MonitoredDataContractDAO monitoredDataContractDAO;

	@Autowired
	public QueueMonitoringComponent(MongoClient mongoClient) {
		this.mongoClient = mongoClient;
	}

	@Scheduled(fixedRate = 60000)
	public void initInternalCachingMaps() {
		LOGGER.debug("Setting up internal map.");
		List<QueueInfo> queueInfos = QueueMonitoringManager.getInstance().getQueueInfos();
		synchronized (queueInfos) {
			synchronized (qm) {
				qm.clear();
				for (QueueInfo queueInfo : queueInfos) {
					// get the thing id
					final String thingId = queueInfo.getThingId();

					List<DataContract> foundDataContracts = getDataContractDAO().findDataContracts(thingId);
					Thing thing = getThingDAO().getThing(thingId);

					boolean found = false;
					String keyToCompare = queueInfo.getBrokerURL() + ";" + queueInfo.getQueueName();
					Set<Entry<String, Map<Thing, List<DataContract>>>> entrySet = qm.entrySet();
					for (Entry<String, Map<Thing, List<DataContract>>> entry : entrySet) {
						String key = entry.getKey();
						if (key.equals(keyToCompare)) {
							found = true;
						}
					}
					if (!found) {
						Map<Thing, List<DataContract>> tcmap = new HashMap<>();
						tcmap.put(thing, foundDataContracts);
						qm.put(keyToCompare, tcmap);
					} else {
						qm.get(keyToCompare).put(thing, foundDataContracts);
					}
				}
			}
		}
		LOGGER.debug("Internal map set up.");
	}

	@Scheduled(fixedRate = 10000)
	public void readQueues() {
		LOGGER.debug("Reading queues: ");
		if (qm.isEmpty()) {
			initInternalCachingMaps();
		}

		synchronized (qm) {
			LOGGER.info(qm.size());
			for (Entry<String, Map<Thing, List<DataContract>>> entry : qm.entrySet()) {
				Map<Thing, List<DataContract>> value = entry.getValue();
				for (Entry<Thing, List<DataContract>> entry2 : value.entrySet()) {
					Thing value2 = entry2.getKey();
					LOGGER.debug(value2.getThingId());
				}
			}

			ExecutorService pool = Executors.newCachedThreadPool();

			Set<Entry<String, Map<Thing, List<DataContract>>>> entrySet = qm.entrySet();
			for (Entry<String, Map<Thing, List<DataContract>>> entry : entrySet) {
				QueueMonitor monitor = new QueueMonitor(entry.getKey(), entry.getValue(), getMonitoredDataContractDAO(),
						getDataContractDAO());
				pool.submit(monitor);
			}

			pool.shutdown();
			while (!pool.isTerminated()) {
				// wait...
			}
		}

		LOGGER.debug("Reading messages finished.");

	}

	/**
	 * 
	 * @return the thingDAO
	 */
	public ThingDAO getThingDAO() {
		if (thingDAO == null) {
			thingDAO = new ThingDAOImpl(mongoClient, database, thingCollection);
		}
		return thingDAO;
	}

	public DataContractDAO getDataContractDAO() {
		if (dataContractDAO == null) {
			dataContractDAO = new DataContractDAOImpl(mongoClient, database, dataContractCollection);
		}
		return dataContractDAO;
	}

	/**
	 * @return the monitoredDataContractDAO
	 */
	public MonitoredDataContractDAO getMonitoredDataContractDAO() {
		if (monitoredDataContractDAO == null) {
			monitoredDataContractDAO = new MonitoredDataContractDAOImpl(mongoClient, database, monitorDCCollection);
		}
		return monitoredDataContractDAO;
	}

}
