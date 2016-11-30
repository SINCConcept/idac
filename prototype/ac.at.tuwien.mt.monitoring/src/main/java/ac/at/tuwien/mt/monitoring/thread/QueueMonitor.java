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

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ac.at.tuwien.mt.dao.datacontract.DataContractDAO;
import ac.at.tuwien.mt.dao.monitor.MonitoredDataContractDAO;
import ac.at.tuwien.mt.model.datacontract.DataContract;
import ac.at.tuwien.mt.model.helper.DefaultDateProvider;
import ac.at.tuwien.mt.model.thing.Thing;
import ac.at.tuwien.mt.model.thing.ThingMessage;
import ac.at.tuwien.mt.model.thing.monitor.MonitoredQoD;
import ac.at.tuwien.mt.model.thing.monitor.MonitoredQoS;
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
		String[] queueInfo = queue.split(";");
		LOGGER.debug(queue);

		Connection connection = null;
		Session session = null;
		try {
			// Create a ConnectionFactory
			String brokerURL = "tcp://91.250.115.96:61616";
			if (StringUtils.isBlank(brokerURL)) {
				return;
			}

			ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(brokerURL);

			// Create a Connection
			connection = connectionFactory.createConnection();
			connection.start();

			connection.setExceptionListener(this);

			// Create a Session
			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

			// Create the destination (Topic or Queue)
			String queueName = queueInfo[1];
			Destination destination = session.createQueue(queueName);

			Set<Entry<Thing, List<DataContract>>> entrySet = map.entrySet();

			for (Entry<Thing, List<DataContract>> entry : entrySet) {
				Thing thing = entry.getKey();
				List<DataContract> dataContracts = entry.getValue();
				int nrOfConsumers = MQPropertiesProvider.getInteger(MQProperty.AMQ_CONCURRENT_CONSUMER);
				for (int i = 0; i < nrOfConsumers; i++) {

					MessageConsumer consumer = null;
					String messageToSave = null;

					try {
						// Create a MessageConsumer from the Session to the
						// Topic or Queue
						consumer = session.createConsumer(destination, "thingid = '" + thing.getThingId() + "'");

						// Wait for a message
						int timeout = 100;
						Message message = consumer.receive(timeout);

						// could not read from queue - nothing to do
						if (message == null) {
							return;
						}

						if (message instanceof TextMessage) {
							TextMessage textMessage = (TextMessage) message;
							String text = textMessage.getText();
							messageToSave = text;
							LOGGER.debug("Received TextMessage: " + text);
						} else {
							messageToSave = message.toString();
							LOGGER.debug("Received String: " + message);
						}

					} catch (Exception e) {
						LOGGER.error(e, e.getCause());
					} finally {
						if (consumer != null) {
							try {
								consumer.close();
							} catch (JMSException e) {
								LOGGER.error(e, e.getCause());
							}
						}
					}

					if (messageToSave == null) {
						return;
					}

					// if there are no data contracts, return - nothing to
					// monitor
					if (dataContracts.isEmpty()) {
						return;
					}

					ThingMessage thingMessage = new ThingMessage(thing, messageToSave,
							DefaultDateProvider.getCurrentTimeStamp());
					ExecutorService pool1 = Executors.newCachedThreadPool();
					// Send to all subscribers in separate threads.
					for (DataContract dataContract : dataContracts) {
						String brokerURLToSend = dataContract.getPricingModel().getSubscription().getBrokerURL();
						String queueNameToSend = dataContract.getPricingModel().getSubscription().getQueueName();
						QueueProducer qp = new QueueProducer(brokerURLToSend, queueNameToSend, messageToSave);
						pool1.submit(qp);
					}

					pool1.shutdown();
					while (!pool1.isTerminated()) {
						// wait...
					}

					// once the message is sent, set the delivery time:
					thingMessage.setDeliveryTime(DefaultDateProvider.getCurrentTimeStamp());

					// see if the messages from this thing should be monitored
					// by the
					// standard monitoring component
					if (thing.getStandardMonitoring() == Boolean.TRUE) {
						// Monitor the data flow
						MessageMonitor thingMonitor = new MessageMonitor(thingMessage);
						thingMonitor.monitorQoD();
						thingMonitor.monitorQoS();

						MonitoredQoD monitoredQoD = thingMonitor.getMonitoredQoD();
						MonitoredQoS monitoredQoS = thingMonitor.getMonitoredQoS();

						ExecutorService pool2 = Executors.newCachedThreadPool();

						// update the datacontract monitoring
						for (DataContract dataContract : dataContracts) {
							if (dataContract.getMonitoring() == Boolean.TRUE) {
								DataContractMonitor dcMonitor = new DataContractMonitor(thing.getThingId(),
										dataContract, monitoredQoD, monitoredQoS, monitoredDataContractDAO,
										dataContractDAO);
								pool2.submit(dcMonitor);
							}
						}

						pool2.shutdown();
						while (!pool2.isTerminated()) {
							// wait...
						}

					}
				}
			}

		} catch (Exception e) {
			LOGGER.error(e, e.getCause());
		} finally {
			if (session != null) {
				try {
					session.close();
				} catch (JMSException e) {
					LOGGER.error(e);
				}
			}
			if (connection != null) {
				try {
					connection.close();
				} catch (JMSException e) {
					LOGGER.error(e);
				}
			}
		}
	}

	public synchronized void onException(JMSException ex) {
		LOGGER.error("JMS Exception occured. Shutting down client.");
	}

}
