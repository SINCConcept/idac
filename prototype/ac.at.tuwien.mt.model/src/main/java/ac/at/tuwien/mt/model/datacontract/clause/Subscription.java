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
package ac.at.tuwien.mt.model.datacontract.clause;

import java.io.Serializable;
import java.util.Date;

import org.bson.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author Florin Bogdan Balint
 *
 */
@SuppressWarnings("serial")
public class Subscription implements Serializable {

	private Date startDate;
	private Date endDate;

	private String brokerURL;
	private String queueName;

	public Subscription() {
		// empty constructor
	}

	public Subscription(Document document) {
		if (document != null) {
			Object startDateAsObject = document.get("startDate");
			if (startDateAsObject instanceof Date) {
				this.startDate = (Date) startDateAsObject;
			}
			if (startDateAsObject instanceof Long) {
				this.startDate = new Date((Long) startDateAsObject);
			}
			Object endDateAsObject = document.get("endDate");
			if (endDateAsObject instanceof Date) {
				this.endDate = (Date) endDateAsObject;
			}
			if (endDateAsObject instanceof Long) {
				this.endDate = new Date((Long) endDateAsObject);
			}
			this.brokerURL = document.getString("brokerURL");
			this.queueName = document.getString("queueName");
		}
	}

	@JsonIgnore
	public Document getDocument() {
		Document document = new Document();
		document.put("startDate", getStartDate());
		document.put("endDate", getEndDate());
		document.put("brokerURL", getBrokerURL());
		document.put("queueName", getQueueName());
		return document;
	}

	/**
	 * @return the startDate
	 */
	public Date getStartDate() {
		return startDate;
	}

	/**
	 * @param startDate
	 *            the startDate to set
	 */
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	/**
	 * @return the endDate
	 */
	public Date getEndDate() {
		return endDate;
	}

	/**
	 * @param endDate
	 *            the endDate to set
	 */
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
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

}
