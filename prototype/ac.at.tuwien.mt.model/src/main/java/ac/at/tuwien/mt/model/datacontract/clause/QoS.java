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

import org.bson.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * 
 * @author Florin Bogdan Balint
 *
 */
@SuppressWarnings("serial")
public class QoS implements Serializable {

	// frequency as specified in the dataContract.
	private Integer frequency;

	// device availability
	private Double availability;

	public QoS() {
		// empty constructor
	}

	public QoS(Document document) {
		this.frequency = document.getInteger("frequency");
		this.availability = document.getDouble("availability");
	}

	@JsonIgnore
	public Document getDocument() {
		Document document = new Document();
		document.put("frequency", getFrequency());
		document.put("availability", getAvailability());
		return document;
	}

	/**
	 * @return the frequency
	 */
	public Integer getFrequency() {
		return frequency;
	}

	/**
	 * @param frequency
	 *            the frequency to set
	 */
	public void setFrequency(Integer frequency) {
		this.frequency = frequency;
	}

	/**
	 * @return the availability
	 */
	public Double getAvailability() {
		return availability;
	}

	/**
	 * @param availability
	 *            the availability to set
	 */
	public void setAvailability(Double availability) {
		this.availability = availability;
	}

}
