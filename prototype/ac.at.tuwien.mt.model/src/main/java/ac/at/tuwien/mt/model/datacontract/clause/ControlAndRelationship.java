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
public class ControlAndRelationship implements Serializable {

	private String warranty;
	private String indemnity;
	private String liability;
	private String juristiction;

	public ControlAndRelationship() {
		// empty construct
	}

	public ControlAndRelationship(Document document) {
		if (document != null) {
			this.warranty = document.getString("warranty");
			this.indemnity = document.getString("indemnity");
			this.liability = document.getString("liability");
			this.juristiction = document.getString("juristiction");
		}
	}

	@JsonIgnore
	public Document getDocument() {
		Document document = new Document();
		document.put("warranty", getWarranty());
		document.put("indemnity", getIndemnity());
		document.put("liability", getLiability());
		document.put("juristiction", getJuristiction());
		return document;
	}

	/**
	 * @return the warranty
	 */
	public String getWarranty() {
		return warranty;
	}

	/**
	 * @param warranty
	 *            the warranty to set
	 */
	public void setWarranty(String warranty) {
		this.warranty = warranty;
	}

	/**
	 * @return the indemnity
	 */
	public String getIndemnity() {
		return indemnity;
	}

	/**
	 * @param indemnity
	 *            the indemnity to set
	 */
	public void setIndemnity(String indemnity) {
		this.indemnity = indemnity;
	}

	/**
	 * @return the liability
	 */
	public String getLiability() {
		return liability;
	}

	/**
	 * @param liability
	 *            the liability to set
	 */
	public void setLiability(String liability) {
		this.liability = liability;
	}

	/**
	 * @return the juristiction
	 */
	public String getJuristiction() {
		return juristiction;
	}

	/**
	 * @param juristiction
	 *            the juristiction to set
	 */
	public void setJuristiction(String juristiction) {
		this.juristiction = juristiction;
	}

}
