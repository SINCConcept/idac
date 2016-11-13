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
package ac.at.tuwien.mt.monitoring.beans;

import java.util.Arrays;
import java.util.List;

import org.apache.camel.Body;
import org.apache.camel.Exchange;
import org.apache.camel.Handler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import ac.at.tuwien.mt.model.microservice.monitoring.QueueInfo;
import ac.at.tuwien.mt.monitoring.thread.QueueMonitoringManager;

/**
 * @author Florin Bogdan Balint
 *
 */
@Component
public class QueueMonitoringBean {

	private static final Logger LOGGER = LogManager.getLogger(QueueMonitoringBean.class);

	@Handler
	public void process(Exchange exchange, @Body QueueInfo[] queueInfos) {
		LOGGER.debug("Received request to update the list of queues which are monitored.");
		List<QueueInfo> qInfoList = Arrays.asList(queueInfos);
		for (QueueInfo queueInfo : qInfoList) {
			LOGGER.debug("Will listen to broker URL: " + queueInfo.getBrokerURL() + ", queue name: " + queueInfo.getQueueName());
		}
		QueueMonitoringManager.getInstance().getQueueInfos().clear();
		QueueMonitoringManager.getInstance().getQueueInfos().addAll(qInfoList);
	}

}
