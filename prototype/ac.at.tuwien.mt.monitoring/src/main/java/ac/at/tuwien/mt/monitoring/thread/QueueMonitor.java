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
package ac.at.tuwien.mt.monitoring.thread;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ac.at.tuwien.mt.dao.datacontract.DataContractDAO;
import ac.at.tuwien.mt.dao.monitor.MonitoredDataContractDAO;
import ac.at.tuwien.mt.dao.monitor.ThingMonitorQoDDAO;
import ac.at.tuwien.mt.dao.monitor.ThingMonitorQoSDAO;
import ac.at.tuwien.mt.model.datacontract.DataContract;
import ac.at.tuwien.mt.model.microservice.monitoring.QueueInfo;
import ac.at.tuwien.mt.model.thing.Thing;
import ac.at.tuwien.mt.monitoring.internal.MQPropertiesProvider;
import ac.at.tuwien.mt.monitoring.internal.MQProperty;

/**
 * @author Florin Bogdan Balint
 *
 */
public class QueueMonitor implements Runnable {

	private static final Logger LOGGER = LogManager.getLogger(QueueMonitor.class);

	private final QueueInfo queueInfo;
	private final Thing thing;
	private final List<DataContract> dataContracts;

	private ThingMonitorQoDDAO thingMonitorQoDDAO;
	private ThingMonitorQoSDAO thingMonitorQoSDAO;
	private MonitoredDataContractDAO monitoredDataContractDAO;
	private DataContractDAO dataContractDAO;

	public QueueMonitor(QueueInfo queueInfo, Thing thing, List<DataContract> dataContracts, ThingMonitorQoDDAO thingMonitorQoDDAO, ThingMonitorQoSDAO thingMonitorQoSDAO,
			MonitoredDataContractDAO monitoredDataContractDAO, DataContractDAO dataContractDAO) {
		this.queueInfo = queueInfo;
		this.thing = thing;
		this.dataContracts = dataContracts;
		this.thingMonitorQoDDAO = thingMonitorQoDDAO;
		this.thingMonitorQoSDAO = thingMonitorQoSDAO;
		this.monitoredDataContractDAO = monitoredDataContractDAO;
		this.dataContractDAO = dataContractDAO;
	}

	@Override
	public void run() {
		LOGGER.debug("Starting new monitor for brokerURL: " + queueInfo.getBrokerURL());
		LOGGER.debug("Starting new monitor for queue: " + queueInfo.getQueueName());

		// get the number of threads which need to be started
		int nrOfConsumers = MQPropertiesProvider.getInteger(MQProperty.AMQ_CONCURRENT_CONSUMER);

		ExecutorService pool = Executors.newCachedThreadPool();

		for (int i = 0; i < nrOfConsumers; i++) {
			QueueConsumer consumer = new QueueConsumer(queueInfo, thing, dataContracts, thingMonitorQoDDAO, thingMonitorQoSDAO, monitoredDataContractDAO, dataContractDAO);
			pool.submit(consumer);
		}

		pool.shutdown();
		while (!pool.isTerminated()) {
			// wait...
		}

	}

}
