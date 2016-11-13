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

package ac.at.tuwien.mt.monitoring.internal;

/**
 * ENUM representing all properties which are used by the system.
 * 
 * @author Florin Bogdan Balint
 *
 */
public enum MQProperty {

	/**
	 * The number of concurrent consumer that will be started for each queue.
	 */
	AMQ_CONCURRENT_CONSUMER("amq.concurrentconsumers"), //
	AMQ_BROKER_URL("amq.brokerurl") //
	;

	private String property;

	private MQProperty(String property) {
		this.property = property;
	}

	/**
	 * @return the property
	 */
	public String getProperty() {
		return property;
	}

	/**
	 * @param property
	 *            the property to set
	 */
	public void setProperty(String property) {
		this.property = property;
	}
}
