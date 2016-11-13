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
 * Abstract class representing the basis for each monitored Thing.
 * 
 * @author Florin Bogdan Balint
 *
 */
@SuppressWarnings("serial")
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class MonitoredThing implements Serializable {

	// standard monitoring fields
	private String thingId;
	private long revision;
	private long totalNrOfSamples;

	public MonitoredThing() {
		// empty constructor
	}

	public MonitoredThing(Document document) {
		this.thingId = document.getString("thingId");
		this.revision = document.getLong("revision");
		this.totalNrOfSamples = document.getLong("totalNrOfSamples");
	}

	@JsonIgnore
	public Document getDocument() {
		Document document = new Document();
		document.put("thingId", thingId);
		document.put("revision", revision);
		document.put("totalNrOfSamples", totalNrOfSamples);
		return document;
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

	/**
	 * @return the revision
	 */
	public long getRevision() {
		return revision;
	}

	/**
	 * @param revision
	 *            the revision to set
	 */
	public void setRevision(long revision) {
		this.revision = revision;
	}

	/**
	 * @return the totalNrOfSamples
	 */
	public long getTotalNrOfSamples() {
		return totalNrOfSamples;
	}

	/**
	 * @param totalNrOfSamples
	 *            the totalNrOfSamples to set
	 */
	public void setTotalNrOfSamples(long totalNrOfSamples) {
		this.totalNrOfSamples = totalNrOfSamples;
	}

}
