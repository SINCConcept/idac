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
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.jms.ExceptionListener;
import javax.jms.JMSException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ac.at.tuwien.mt.dao.datacontract.DataContractDAO;
import ac.at.tuwien.mt.dao.monitor.MonitoredDataContractDAO;
import ac.at.tuwien.mt.model.datacontract.DataContract;
import ac.at.tuwien.mt.model.thing.Thing;
import ac.at.tuwien.mt.monitoring.internal.MQPropertiesProvider;
import ac.at.tuwien.mt.monitoring.internal.MQProperty;

/**
 * @author Florin Bogdan Balint
 *
 */
public class QueueMonitor implements Runnable, ExceptionListener {

	private static final Logger LOGGER = LogManager.getLogger(QueueMonitor.class);

	private final Map<Thing, List<DataContract>> map;
	private final String queue;

	private MonitoredDataContractDAO monitoredDataContractDAO;
	private DataContractDAO dataContractDAO;

	public QueueMonitor(String queue, Map<Thing, List<DataContract>> map,
			MonitoredDataContractDAO monitoredDataContractDAO, DataContractDAO dataContractDAO) {
		this.queue = queue;
		this.map = map;
		this.monitoredDataContractDAO = monitoredDataContractDAO;
		this.dataContractDAO = dataContractDAO;
	}

	@Override
	public void run() {
		LOGGER.debug("Queue monitor started.");

		Set<Entry<Thing, List<DataContract>>> entrySet = map.entrySet();

		ExecutorService consumerPool = Executors.newCachedThreadPool();
		for (Entry<Thing, List<DataContract>> entry : entrySet) {

			Thing thing = entry.getKey();
			List<DataContract> dataContracts = entry.getValue();

			int nrOfConsumers = MQPropertiesProvider.getInteger(MQProperty.AMQ_CONCURRENT_CONSUMER);
			for (int i = 0; i < nrOfConsumers; i++) {
				QueueMonitorConsumer qmc = new QueueMonitorConsumer(thing, dataContracts, queue,
						monitoredDataContractDAO, dataContractDAO);
				consumerPool.submit(qmc);
			}

		}

		consumerPool.shutdown();
		while (!consumerPool.isTerminated()) {
			// wait...
		}

	}

	public synchronized void onException(JMSException ex) {
		LOGGER.error("JMS Exception occured. Shutting down client.");
	}

}
