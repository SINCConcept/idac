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
 * @author Florin Bogdan Balint
 *
 */
@SuppressWarnings("serial")
public class QoD implements Serializable {

	/**
	 * Is the requisite information available? Are data values missing?
	 */
	private Double completeness;

	/**
	 * Do data objects accurately represent the “real-world” values they are
	 * expected to model? Incorrect spellings of product or person names
	 */
	private Double accuracy;

	/**
	 * Conformity means the data is following the set of standard data
	 * definitions like data type, size and format. For example, date of birth
	 * of customer is in the format “mm/dd/yyyy”
	 */
	private Double conformity;

	/**
	 * Do distinct data instances provide conflicting information about the same
	 * underlying data object? Are values consistent across data sets?
	 */
	private Double consistency;

	/**
	 * Defined as: Currency = Age + (DeliveryTime - InputTime). <br/>
	 * <br/>
	 * Age measures how old the data unit is when received, DeliveryTime is the
	 * time the information product is delivered to the customer, and InputTime
	 * is the time the data unit is obtained
	 */
	private Double currency;

	/**
	 * Average data age.
	 */
	private Double timeliness;

	public QoD() {
		// empty constructor
	}

	public QoD(Document document) {
		if (document != null) {
			this.completeness = document.getDouble("completeness");
			this.accuracy = document.getDouble("accuracy");
			this.conformity = document.getDouble("conformity");
			this.consistency = document.getDouble("consistency");
			this.currency = document.getDouble("currency");
			this.timeliness = document.getDouble("timeliness");
		}
	}

	@JsonIgnore
	public Document getDocument() {
		Document document = new Document();
		document.put("completeness", getCompleteness());
		document.put("accuracy", getAccuracy());
		document.put("conformity", getConformity());
		document.put("consistency", getConsistency());
		document.put("currency", getCurrency());
		document.put("timeliness", getTimeliness());
		return document;
	}

	/**
	 * @return the completeness
	 */
	public Double getCompleteness() {
		return completeness;
	}

	/**
	 * @param completeness
	 *            the completeness to set
	 */
	public void setCompleteness(Double completeness) {
		this.completeness = completeness;
	}

	/**
	 * @return the accuracy
	 */
	public Double getAccuracy() {
		return accuracy;
	}

	/**
	 * @param accuracy
	 *            the accuracy to set
	 */
	public void setAccuracy(Double accuracy) {
		this.accuracy = accuracy;
	}

	/**
	 * @return the conformity
	 */
	public Double getConformity() {
		return conformity;
	}

	/**
	 * @param conformity
	 *            the conformity to set
	 */
	public void setConformity(Double conformity) {
		this.conformity = conformity;
	}

	/**
	 * @return the consistency
	 */
	public Double getConsistency() {
		return consistency;
	}

	/**
	 * @param consistency
	 *            the consistency to set
	 */
	public void setConsistency(Double consistency) {
		this.consistency = consistency;
	}

	/**
	 * @return the currency
	 */
	public Double getCurrency() {
		return currency;
	}

	/**
	 * @param currency
	 *            the currency to set
	 */
	public void setCurrency(Double currency) {
		this.currency = currency;
	}

	/**
	 * @return the timeliness
	 */
	public Double getTimeliness() {
		return timeliness;
	}

	/**
	 * @param timeliness
	 *            the timeliness to set
	 */
	public void setTimeliness(Double timeliness) {
		this.timeliness = timeliness;
	}

}
