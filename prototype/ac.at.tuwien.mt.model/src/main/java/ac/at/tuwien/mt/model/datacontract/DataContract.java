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
import java.util.ArrayList;
import java.util.List;

import org.bson.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import ac.at.tuwien.mt.model.datacontract.clause.ControlAndRelationship;
import ac.at.tuwien.mt.model.datacontract.clause.DataRights;
import ac.at.tuwien.mt.model.datacontract.clause.PricingModel;
import ac.at.tuwien.mt.model.datacontract.clause.PurchasingPolicy;
import ac.at.tuwien.mt.model.person.Person;
import ac.at.tuwien.mt.model.thing.ThingId;

/**
 * @author Florin Bogdan Balint
 *
 */
@SuppressWarnings("serial")
@JsonIgnoreProperties(ignoreUnknown = true)
public class DataContract implements Serializable {

	private DataContractMetaInfo dataContractMetaInfo;

	// data contractual clauses
	private DataRights dataRights;
	private PricingModel pricingModel;
	private ControlAndRelationship controlAndRelationship;
	private PurchasingPolicy purchasingPolicy;

	private List<ThingId> thingIds = new ArrayList<ThingId>();

	// flag indicating if the data contract is or should be monitored
	private Boolean monitoring;

	@JsonIgnore
	private Person party1;
	@JsonIgnore
	private Person party2;

	public DataContract() {
		this.dataContractMetaInfo = new DataContractMetaInfo();
		this.dataRights = new DataRights();
		this.pricingModel = new PricingModel();
		this.controlAndRelationship = new ControlAndRelationship();
		this.purchasingPolicy = new PurchasingPolicy();
		this.monitoring = false;
	}

	public DataContract(Document document) {
		this.dataContractMetaInfo = new DataContractMetaInfo((Document) document.get("dataContractMetaInfo"));
		this.dataRights = new DataRights((Document) document.get("dataRights"));
		this.pricingModel = new PricingModel((Document) document.get("pricingModel"));
		this.controlAndRelationship = new ControlAndRelationship((Document) document.get("controlAndRelationship"));
		this.purchasingPolicy = new PurchasingPolicy((Document) document.get("purchasingPolicy"));

		Object listObject = document.get("thingIds");
		if (listObject instanceof List) {
			List<?> list = (List<?>) listObject;
			for (Object object : list) {
				if (object instanceof Document) {
					Document objectAsDoc = (Document) object;
					this.thingIds.add(new ThingId(objectAsDoc));
				}
			}
		}
		this.monitoring = document.getBoolean("monitoring");
	}

	@JsonIgnore
	public Document getDocument() {
		Document document = new Document();
		document.put("dataContractMetaInfo", getDataContractMetaInfo().getDocument());
		if (dataRights != null) {
			document.put("dataRights", getDataRights().getDocument());
		}
		if (pricingModel != null) {
			document.put("pricingModel", getPricingModel().getDocument());
		}
		if (controlAndRelationship != null) {
			document.put("controlAndRelationship", getControlAndRelationship().getDocument());
		}
		if (purchasingPolicy != null) {
			document.put("purchasingPolicy", getPurchasingPolicy().getDocument());
		}
		// add the clauses list
		List<Document> thingsDocument = new ArrayList<Document>();
		for (ThingId thingid : thingIds) {
			thingsDocument.add(thingid.getDocument());
		}
		document.put("thingIds", thingsDocument);
		document.put("monitoring", getMonitoring());
		return document;
	}

	/**
	 * @return the dataContractMetaInfo
	 */
	public DataContractMetaInfo getDataContractMetaInfo() {
		return dataContractMetaInfo;
	}

	/**
	 * @param dataContractMetaInfo
	 *            the dataContractMetaInfo to set
	 */
	public void setDataContractMetaInfo(DataContractMetaInfo dataContractMetaInfo) {
		this.dataContractMetaInfo = dataContractMetaInfo;
	}

	/**
	 * @return the party1
	 */
	@JsonIgnore
	public Person getParty1() {
		return party1;
	}

	/**
	 * @param party1
	 *            the party1 to set
	 */
	@JsonIgnore
	public void setParty1(Person party1) {
		this.party1 = party1;
	}

	/**
	 * @return the party2
	 */
	@JsonIgnore
	public Person getParty2() {
		return party2;
	}

	/**
	 * @param party2
	 *            the party2 to set
	 */
	@JsonIgnore
	public void setParty2(Person party2) {
		this.party2 = party2;
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

	/**
	 * @return the things
	 */
	public List<ThingId> getThingIds() {
		return thingIds;
	}

	/**
	 * @param things
	 *            the things to set
	 */
	public void setThingIds(List<ThingId> thingIds) {
		this.thingIds = thingIds;
	}

	/**
	 * @return the monitoring
	 */
	public Boolean getMonitoring() {
		return monitoring;
	}

	/**
	 * @param monitoring
	 *            the monitoring to set
	 */
	public void setMonitoring(Boolean monitoring) {
		this.monitoring = monitoring;
	}

}
