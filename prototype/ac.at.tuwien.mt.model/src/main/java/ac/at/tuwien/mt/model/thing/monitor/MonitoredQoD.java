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
 * All QoD (i.e., quality of data) measurements will be saved here.
 * 
 * @author Florin Bogdan Balint
 *
 */
@SuppressWarnings("serial")
@JsonIgnoreProperties(ignoreUnknown = true)
public class MonitoredQoD extends MonitoredThing implements Serializable {

	private long samplesComplete;
	private long samplesConform;

	/**
	 * Average age of the produced messages. <br/>
	 * Age = messageReceivalTime - messageRecordedTime. <br/>
	 * AverageAge = (age1 + age2 + ... + ageN) / N <br/>
	 * Recursively: AverageAge(N+1) = [AverageAge * N + age(N+1)] / (N+1)
	 */
	private double averageAge;

	private double averageCurrency;

	private long ageSamples;

	private double completeness;
	private double conformity;

	public MonitoredQoD() {
		// empty constructor
	}

	public MonitoredQoD(Document document) {
		super(document);
		this.samplesComplete = document.getLong("samplesComplete");
		this.samplesConform = document.getLong("samplesConform");
		this.averageAge = document.getDouble("averageAge");
		this.averageCurrency = document.getDouble("averageCurrency");
		this.ageSamples = document.getLong("ageSamples");
		this.completeness = document.getDouble("completeness");
		this.conformity = document.getDouble("conformity");
	}

	@JsonIgnore
	public Document getDocument() {
		Document document = super.getDocument();
		document.put("samplesComplete", samplesComplete);
		document.put("samplesConform", samplesConform);
		document.put("averageAge", averageAge);
		document.put("averageCurrency", averageCurrency);
		document.put("ageSamples", ageSamples);
		document.put("completeness", completeness);
		document.put("conformity", conformity);
		return document;
	}

	/**
	 * @return the samplesComplete
	 */
	public long getSamplesComplete() {
		return samplesComplete;
	}

	/**
	 * @param samplesComplete
	 *            the samplesComplete to set
	 */
	public void setSamplesComplete(long samplesComplete) {
		this.samplesComplete = samplesComplete;
	}

	/**
	 * @return the samplesConform
	 */
	public long getSamplesConform() {
		return samplesConform;
	}

	/**
	 * @param samplesConform
	 *            the samplesConform to set
	 */
	public void setSamplesConform(long samplesConform) {
		this.samplesConform = samplesConform;
	}

	/**
	 * @return the averageAge
	 */
	public double getAverageAge() {
		return averageAge;
	}

	/**
	 * @param averageAge
	 *            the averageAge to set
	 */
	public void setAverageAge(double averageAge) {
		this.averageAge = averageAge;
	}

	/**
	 * @return the ageSamples
	 */
	public long getAgeSamples() {
		return ageSamples;
	}

	/**
	 * @param ageSamples
	 *            the ageSamples to set
	 */
	public void setAgeSamples(long ageSamples) {
		this.ageSamples = ageSamples;
	}

	/**
	 * @return the averageCurrency
	 */
	public double getAverageCurrency() {
		return averageCurrency;
	}

	/**
	 * @param averageCurrency
	 *            the averageCurrency to set
	 */
	public void setAverageCurrency(double averageCurrency) {
		this.averageCurrency = averageCurrency;
	}

	/**
	 * @return the completeness
	 */
	public double getCompleteness() {
		return completeness;
	}

	/**
	 * @param completeness
	 *            the completeness to set
	 */
	public void setCompleteness(double completeness) {
		this.completeness = completeness;
	}

	/**
	 * @return the conformity
	 */
	public double getConformity() {
		return conformity;
	}

	/**
	 * @param conformity
	 *            the conformity to set
	 */
	public void setConformity(double conformity) {
		this.conformity = conformity;
	}

}
