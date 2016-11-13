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
package ac.at.tuwien.mt.model.datacontract;

import java.io.Serializable;

import org.bson.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;

import ac.at.tuwien.mt.model.datacontract.clause.ControlAndRelationship;
import ac.at.tuwien.mt.model.datacontract.clause.DataRights;
import ac.at.tuwien.mt.model.datacontract.clause.PricingModel;
import ac.at.tuwien.mt.model.datacontract.clause.PurchasingPolicy;

/**
 * @author Florin Bogdan Balint
 *
 */
@SuppressWarnings("serial")
public class ClausesTrailEntry implements Serializable {

	private DataRights dataRights;
	private PricingModel pricingModel;
	private ControlAndRelationship controlAndRelationship;
	private PurchasingPolicy purchasingPolicy;

	public ClausesTrailEntry() {
		this.dataRights = new DataRights();
		this.pricingModel = new PricingModel();
		this.purchasingPolicy = new PurchasingPolicy();
		this.controlAndRelationship = new ControlAndRelationship();
	}

	public ClausesTrailEntry(Document document) {
		this.dataRights = new DataRights((Document) document.get("dataRights"));
		this.pricingModel = new PricingModel((Document) document.get("pricingModel"));
		this.purchasingPolicy = new PurchasingPolicy((Document) document.get("purchasingPolicy"));
		this.controlAndRelationship = new ControlAndRelationship((Document) document.get("controlAndRelationship"));
	}

	@JsonIgnore
	public Document getDocument() {
		Document document = new Document();
		if (getDataRights() != null) {
			document.put("dataRights", getDataRights().getDocument());
		}
		if (getPricingModel() != null) {
			document.put("pricingModel", getPricingModel().getDocument());
		}
		if (getPurchasingPolicy() != null) {
			document.put("purchasingPolicy", getPurchasingPolicy().getDocument());
		}
		if (getControlAndRelationship() != null) {
			document.put("controlAndRelationship", getControlAndRelationship().getDocument());
		}
		return document;
	}

	/**
	 * @return the dataRights
	 */
	public DataRights getDataRights() {
		return dataRights;
	}

	/**
	 * @param dataRights
	 *            the dataRights to set
	 */
	public void setDataRights(DataRights dataRights) {
		this.dataRights = dataRights;
	}

	/**
	 * @return the pricingModel
	 */
	public PricingModel getPricingModel() {
		return pricingModel;
	}

	/**
	 * @param pricingModel
	 *            the pricingModel to set
	 */
	public void setPricingModel(PricingModel pricingModel) {
		this.pricingModel = pricingModel;
	}

	/**
	 * @return the controlAndRelationship
	 */
	public ControlAndRelationship getControlAndRelationship() {
		return controlAndRelationship;
	}

	/**
	 * @param controlAndRelationship
	 *            the controlAndRelationship to set
	 */
	public void setControlAndRelationship(ControlAndRelationship controlAndRelationship) {
		this.controlAndRelationship = controlAndRelationship;
	}

	/**
	 * @return the purchasingPolicy
	 */
	public PurchasingPolicy getPurchasingPolicy() {
		return purchasingPolicy;
	}

	/**
	 * @param purchasingPolicy
	 *            the purchasingPolicy to set
	 */
	public void setPurchasingPolicy(PurchasingPolicy purchasingPolicy) {
		this.purchasingPolicy = purchasingPolicy;
	}

}
