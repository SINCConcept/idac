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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ac.at.tuwien.mt.model.microservice.monitoring.QueueInfo;

/**
 * @author Florin Bogdan Balint
 *
 */
public class QueueMonitoringManager {

	private static final QueueMonitoringManager instance = new QueueMonitoringManager();

	private static List<QueueInfo> queueInfos = Collections.synchronizedList(new ArrayList<QueueInfo>());

	private QueueMonitoringManager() {
		// nothing here!
	}

	public static QueueMonitoringManager getInstance() {
		return instance;
	}

	/**
	 * @return the queueInfos
	 */
	public List<QueueInfo> getQueueInfos() {
		return queueInfos;
	}

}
