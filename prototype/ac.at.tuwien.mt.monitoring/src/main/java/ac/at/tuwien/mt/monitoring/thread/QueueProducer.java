/*
 * Copyright (c) 2016. All rights reserved.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS".
 * IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES.
 * 
 * Author: Florin Bogdan Balint
 * 
 * Please see also: http://activemq.apache.org/hello-world.html
 */

package ac.at.tuwien.mt.monitoring.thread;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Producer which reads the temperature from a file and sends a JSON message
 * with it to a queue.
 * 
 * @author Florin Bogdan Balint
 *
 */
public class QueueProducer implements Runnable {

	private static final Logger LOGGER = LogManager.getLogger(QueueProducer.class);

	private final String mqUrl;
	private final String mqName;
	private final String textMessage;

	public QueueProducer(String mqUrl, String mqName, String textMessage) {
		this.mqUrl = mqUrl;
		this.mqName = mqName;
		this.textMessage = textMessage;
	}

	@Override
	public void run() {
		LOGGER.debug("Sending message.");
		// Create a ConnectionFactory
		ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(mqUrl);

		Connection connection = null;
		Session session = null;
		try {
			// Create a Connection
			connection = connectionFactory.createConnection();
			connection.start();

			// Create a Session
			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

			// Create the destination (Topic or Queue)
			Destination destination = session.createQueue(mqName);

			// Create a MessageProducer from the Session to the Topic or Queue
			MessageProducer producer = session.createProducer(destination);
			producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

			// Create a messages
			TextMessage message = session.createTextMessage(textMessage);

			// Tell the producer to send the message
			producer.send(message);
			LOGGER.debug("Message sent.");

		} catch (JMSException e) {
			LOGGER.error(e);
		} finally {
			// Clean up
			LOGGER.debug("Cleaning up connecions.");
			if (session != null) {
				try {
					session.close();
					LOGGER.debug("Session closed.");
				} catch (JMSException e) {
					LOGGER.error(e);
				}
			}
			if (connection != null) {
				try {
					connection.close();
					LOGGER.debug("Connection closed.");
				} catch (JMSException e) {
					LOGGER.error(e);
				}
			}
		}
	}

}
