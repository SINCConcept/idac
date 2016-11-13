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
package ac.at.tuwien.mt.middleware.internal.model.jms;

import java.io.Serializable;
import java.util.Date;

import org.bson.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * @author Florin Bogdan Balint
 *
 */
@SuppressWarnings("serial")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Message implements Serializable {

	private String messageId;
	private String queueId;
	private String message;
	private Date creationDate;

	public Message() {
		// empty constructor
	}

	/**
	 * @return the messageId
	 */
	public String getMessageId() {
		return messageId;
	}

	@JsonIgnore
	public Document getDocument() {
		Document document = new Document();
		document.put("messageId", getMessageId());
		document.put("queueId", getMessageId());
		document.put("message", getMessage());
		document.put("creationDate", getCreationDate());
		return document;
	}

	/**
	 * @param messageId
	 *            the messageId to set
	 */
	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @param message
	 *            the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * @return the creationDate
	 */
	public Date getCreationDate() {
		return creationDate;
	}

	/**
	 * @param creationDate
	 *            the creationDate to set
	 */
	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	/**
	 * @return the queueId
	 */
	public String getQueueId() {
		return queueId;
	}

	/**
	 * @param queueId
	 *            the queueId to set
	 */
	public void setQueueId(String queueId) {
		this.queueId = queueId;
	}

}
