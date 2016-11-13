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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.mongodb.MongoClient;

import ac.at.tuwien.mt.dao.datacontract.DataContractDAO;
import ac.at.tuwien.mt.dao.datacontract.impl.DataContractDAOImpl;
import ac.at.tuwien.mt.dao.monitor.MonitoredDataContractDAO;
import ac.at.tuwien.mt.dao.monitor.ThingMonitorQoDDAO;
import ac.at.tuwien.mt.dao.monitor.ThingMonitorQoSDAO;
import ac.at.tuwien.mt.dao.monitor.impl.MonitoredDataContractDAOImpl;
import ac.at.tuwien.mt.dao.monitor.impl.ThingMonitorQoDDAOImpl;
import ac.at.tuwien.mt.dao.monitor.impl.ThingMonitorQoSDAOImpl;
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

	private static Map<String, List<DataContract>> thingDataContractMap = new HashMap<String, List<DataContract>>();
	private static Map<String, Thing> thingMap = new HashMap<String, Thing>();

	private ThingDAO thingDAO;
	private DataContractDAO dataContractDAO;
	private ThingMonitorQoDDAO thingMonitorQoDDAO;
	private ThingMonitorQoSDAO thingMonitorQoSDAO;
	private MonitoredDataContractDAO monitoredDataContractDAO;

	private static List<QueueInfo> queues = Collections.synchronizedList(new ArrayList<QueueInfo>());

	@Autowired
	public QueueMonitoringComponent(MongoClient mongoClient) {
		this.mongoClient = mongoClient;
	}

	// @Scheduled(cron = "*/120 * * * * ?")
	public void garbageCollect() {
		System.gc();
	}

	// @Scheduled(cron = "*/120 * * * * ?")
	public void initInternalCachingMaps() {
		LOGGER.debug("Setting up");
		List<QueueInfo> queueInfos = QueueMonitoringManager.getInstance().getQueueInfos();
		synchronized (queueInfos) {
			synchronized (queues) {
				for (QueueInfo queueInfo : queueInfos) {
					// get the thing id
					final String thingId = queueInfo.getThingId();

					// get all the contracts which we should monitor
					List<DataContract> foundDataContracts = getDataContractDAO().findDataContracts(thingId);
					thingDataContractMap.remove(thingId);
					thingDataContractMap.put(thingId, foundDataContracts);

					// get the thing
					final Thing thing = getThingDAO().getThing(thingId);
					thingMap.remove(thingId);
					thingMap.put(thingId, thing);

					boolean found = false;
					for (QueueInfo qi : queues) {
						if (qi.getBrokerURL().equals(queueInfo.getBrokerURL()) && qi.getQueueName().equals(queueInfo.getQueueName())) {
							found = true;
						}
					}
					if (!found) {
						queues.add(queueInfo);
					}
				}
			}
		}
	}

	// @Scheduled(cron = "*/10 * * * * ?")
	public void readQueues() {
		LOGGER.debug("Reading messages");
		if (thingDataContractMap.isEmpty()) {
			initInternalCachingMaps();
		}

		List<QueueInfo> queueInfos = QueueMonitoringManager.getInstance().getQueueInfos();
		synchronized (queueInfos) {

			ExecutorService pool = Executors.newCachedThreadPool();

			for (QueueInfo queueInfo : queueInfos) {
				String thingId = queueInfo.getThingId();
				List<DataContract> dataContracts = thingDataContractMap.get(thingId);
				// failsafe
				if (dataContracts == null) {
					List<DataContract> foundDataContracts = getDataContractDAO().findDataContracts(thingId);
					thingDataContractMap.remove(thingId);
					thingDataContractMap.put(thingId, foundDataContracts);
				}

				Thing thing = thingMap.get(thingId);

				// failsafe
				if (thing == null) {
					LOGGER.error("Thing could not be found, retrying.");
					thing = getThingDAO().getThing(thingId);
				}
				if (thing != null) {
					QueueMonitor monitor = new QueueMonitor(queueInfo, thing, dataContracts, getThingMonitorQoDDAO(), getThingMonitorQoSDAO(), getMonitoredDataContractDAO(), getDataContractDAO());
					pool.submit(monitor);
				} else {
					LOGGER.error("Could not retrieve Thing from db for id: " + thingId);
				}
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
	 * @return the thingMonitorQoSDAO
	 */
	public ThingMonitorQoSDAO getThingMonitorQoSDAO() {
		if (thingMonitorQoSDAO == null) {
			thingMonitorQoSDAO = new ThingMonitorQoSDAOImpl(mongoClient, database, monitorThingQoSCollection);
		}
		return thingMonitorQoSDAO;
	}

	/**
	 * @return the thingMonitorQoDDAO
	 */
	public ThingMonitorQoDDAO getThingMonitorQoDDAO() {
		if (thingMonitorQoDDAO == null) {
			thingMonitorQoDDAO = new ThingMonitorQoDDAOImpl(mongoClient, database, monitorThingQoDCollection);
		}
		return thingMonitorQoDDAO;
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
