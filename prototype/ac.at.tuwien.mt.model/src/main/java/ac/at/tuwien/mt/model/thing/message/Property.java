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
package ac.at.tuwien.mt.model.thing.message;

import java.io.Serializable;

import org.bson.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Each attribute can have further properties. One of them would be if the
 * attribute is used as an identifier.
 * 
 * @author Florin Bogdan Balint
 *
 */
@SuppressWarnings("serial")
public class Property implements Serializable {

	/**
	 * If an attribute has this flag set to TRUE, it acts as an identifier.
	 */
	private boolean identifier;
	/**
	 * If an attribute has this flag set to TRUE, it means that the attribute is
	 * a date which represents the date of the recording. E.g., If a thing
	 * records a temperature, than the attribute which describes the temperature
	 * date will have this flag set to true.
	 */
	private boolean recordingDate;
	/**
	 * If an attribute has the data type DATE, the date representation can be
	 * specified in this field.
	 */
	private String dateFormat;

	public Property() {
		// empty constructor
	}

	public Property(Document document) {
		this.identifier = document.getBoolean("identifier");
		this.recordingDate = document.getBoolean("recordingDate");
		this.dateFormat = document.getString("dateFormat");
	}

	@JsonIgnore
	public Document getDocument() {
		Document document = new Document();
		document.put("identifier", getIdentifier());
		document.put("recordingDate", getRecordingDate());
		if (getDateFormat() != null) {
			document.put("dateFormat", getDateFormat());
		}
		return document;
	}

	/**
	 * If an attribute has this flag set to TRUE, it acts as an identifier.
	 * 
	 * @return Boolean
	 */
	public boolean getIdentifier() {
		return identifier;
	}

	/**
	 * @param identifier
	 *            the identifier to set
	 */
	public void setIdentifier(boolean identifier) {
		this.identifier = identifier;
	}

	/**
	 * If an attribute has this flag set to TRUE, it means that the attribute is
	 * a date which represents the date of the recording. E.g., If a thing
	 * records a temperature, than the attribute which describes the temperature
	 * date will have this flag set to true.
	 * 
	 * @return the recordingDate
	 */
	public boolean getRecordingDate() {
		return recordingDate;
	}

	/**
	 * @param recordingDate
	 *            the recordingDate to set
	 */
	public void setRecordingDate(boolean recordingDate) {
		this.recordingDate = recordingDate;
	}

	/**
	 * @return the dateFormat
	 */
	public String getDateFormat() {
		return dateFormat;
	}

	/**
	 * If an attribute has the data type DATE, the date representation can be
	 * specified in this field.
	 * 
	 * @param dateFormat
	 *            the dateFormat to set
	 */
	public void setDateFormat(String dateFormat) {
		this.dateFormat = dateFormat;
	}

}
