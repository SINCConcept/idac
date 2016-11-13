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
package ac.at.tuwien.mt.model.thing;

import java.io.Serializable;

import org.bson.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * @author Florin Bogdan Balint
 *
 */
@SuppressWarnings("serial")
@JsonIgnoreProperties(ignoreUnknown = true)
public class ThingId implements Serializable {

	// Thing Basic Database Information
	private String thingId;

	public ThingId() {
		// empty constructor
	}

	public ThingId(String thingId) {
		this.thingId = thingId;
	}

	public ThingId(Document document) {
		this.thingId = document.getString("thingId");
	}

	@JsonIgnore
	public Document getDocument() {
		Document document = new Document();
		document.put("thingId", getThingId());
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

}
