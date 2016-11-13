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
package ac.at.tuwien.mt.model.microservice.monitoring;

import java.io.Serializable;

import org.bson.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * This class represents the most important information of a queue. Its broker
 * URL and optionally the queue name.
 * 
 * @author Florin Bogdan Balint
 *
 */
@SuppressWarnings("serial")
public class QueueInfo implements Serializable {

	private String brokerURL;
	private String queueName;
	private String thingId;

	/**
	 * Empty constructor
	 */
	public QueueInfo() {
		// empty constructor
	}

	/**
	 * 
	 * @param document
	 */
	public QueueInfo(Document document) {
		if (document != null) {
			this.brokerURL = document.getString("brokerURL");
			this.queueName = document.getString("queueName");
			this.thingId = document.getString("thingId");
		}
	}

	@JsonIgnore
	public Document getDocument() {
		Document document = new Document();
		document.put("brokerURL", getBrokerURL());
		document.put("queueName", getQueueName());
		document.put("thingId", getThingId());
		return document;
	}

	/**
	 * @return the brokerURL
	 */
	public String getBrokerURL() {
		return brokerURL;
	}

	/**
	 * @param brokerURL
	 *            the brokerURL to set
	 */
	public void setBrokerURL(String brokerURL) {
		this.brokerURL = brokerURL;
	}

	/**
	 * @return the queueName
	 */
	public String getQueueName() {
		return queueName;
	}

	/**
	 * @param queueName
	 *            the queueName to set
	 */
	public void setQueueName(String queueName) {
		this.queueName = queueName;
	}

	/**
	 * @return the thingId
	 */
	public String getThingId() {
		return thingId;
	}

	/**
	 * @param thingId
	 *            the thingId to set
	 */
	public void setThingId(String thingId) {
		this.thingId = thingId;
	}

}
