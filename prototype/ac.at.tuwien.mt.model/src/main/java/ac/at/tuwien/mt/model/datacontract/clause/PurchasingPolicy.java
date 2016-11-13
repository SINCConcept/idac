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
public class PurchasingPolicy implements Serializable {

	private String contractTermination;
	private String shipping;
	private String refund;

	public PurchasingPolicy() {
		// empty constructor
	}

	public PurchasingPolicy(Document document) {
		if (document != null) {
			this.contractTermination = document.getString("contractTermination");
			this.shipping = document.getString("shipping");
			this.refund = document.getString("refund");
		}
	}

	@JsonIgnore
	public Document getDocument() {
		Document document = new Document();
		document.put("contractTermination", getContractTermination());
		document.put("shipping", getShipping());
		document.put("refund", getRefund());
		return document;
	}

	/**
	 * @return the contractTermination
	 */
	public String getContractTermination() {
		return contractTermination;
	}

	/**
	 * @param contractTermination
	 *            the contractTermination to set
	 */
	public void setContractTermination(String contractTermination) {
		this.contractTermination = contractTermination;
	}

	/**
	 * @return the shipping
	 */
	public String getShipping() {
		return shipping;
	}

	/**
	 * @param shipping
	 *            the shipping to set
	 */
	public void setShipping(String shipping) {
		this.shipping = shipping;
	}

	/**
	 * @return the refund
	 */
	public String getRefund() {
		return refund;
	}

	/**
	 * @param refund
	 *            the refund to set
	 */
	public void setRefund(String refund) {
		this.refund = refund;
	}

}
