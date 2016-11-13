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
package ac.at.tuwien.mt.model.thing.monitor;

import java.io.Serializable;

import org.bson.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Class representing the measurements for a thing. <br/>
 * All QoS (i.e., quality of service) measurements will be saved here.
 * 
 * @author Florin Bogdan Balint
 *
 */
@SuppressWarnings("serial")
@JsonIgnoreProperties(ignoreUnknown = true)
public class MonitoredQoS extends MonitoredThing implements Serializable {

	/**
	 * The expected frequency as provided in the thing registration. Measured
	 * in: time in milliseconds. E.g., a Thing will send a message every x
	 * milliseconds.
	 */
	private Integer expectedFrequency;

	/**
	 * Expected number of samples. Calculated starting from the
	 * firstMessageReceival using the expected frequency.
	 */
	private Long expectedNrOfSamples;

	/**
	 * The time when the last message was received for this Thing.
	 */
	private String lastMessageReceival;

	/**
	 * The time when the first message was received for this Thing. This is also
	 * the moment, from where the quality of service monitoring will start. This
	 * is needed to compute the availability.
	 */
	private String firstMessageReceival;

	/**
	 * Availability in percent: in accordance to the expected frequency
	 */
	private Double availability;

	public MonitoredQoS() {
		// empty constructor
		availability = null;
	}

	public MonitoredQoS(Document document) {
		super(document);
		this.expectedFrequency = document.getInteger("expectedFrequency");
		this.expectedNrOfSamples = document.getLong("expectedNrOfSamples");
		this.firstMessageReceival = document.getString("firstMessageReceival");
		this.lastMessageReceival = document.getString("lastMessageReceival");
		this.availability = document.getDouble("availability");
	}

	@JsonIgnore
	public Document getDocument() {
		Document document = super.getDocument();
		document.put("expectedFrequency", getExpectedFrequency());
		document.put("expectedNrOfSamples", getExpectedNrOfSamples());
		document.put("firstMessageReceival", getFirstMessageReceival());
		document.put("lastMessageReceival", getLastMessageReceival());
		document.put("availability", getAvailability());
		return document;
	}

	/**
	 * @return the expectedFrequency
	 */
	public Integer getExpectedFrequency() {
		return expectedFrequency;
	}

	/**
	 * @param expectedFrequency
	 *            the expectedFrequency to set
	 */
	public void setExpectedFrequency(Integer expectedFrequency) {
		this.expectedFrequency = expectedFrequency;
	}

	/**
	 * @return the expectedNrOfSamples
	 */
	public Long getExpectedNrOfSamples() {
		return expectedNrOfSamples;
	}

	/**
	 * @param expectedNrOfSamples
	 *            the expectedNrOfSamples to set
	 */
	public void setExpectedNrOfSamples(Long expectedNrOfSamples) {
		this.expectedNrOfSamples = expectedNrOfSamples;
	}

	/**
	 * @return the lastMessageReceival
	 */
	public String getLastMessageReceival() {
		return lastMessageReceival;
	}

	/**
	 * @param lastMessageReceival
	 *            the lastMessageReceival to set
	 */
	public void setLastMessageReceival(String lastMessageReceival) {
		this.lastMessageReceival = lastMessageReceival;
	}

	/**
	 * @return the firstMessageReceival
	 */
	public String getFirstMessageReceival() {
		return firstMessageReceival;
	}

	/**
	 * @param firstMessageReceival
	 *            the firstMessageReceival to set
	 */
	public void setFirstMessageReceival(String firstMessageReceival) {
		this.firstMessageReceival = firstMessageReceival;
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
