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

import java.util.Date;

import org.bson.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author Florin Bogdan Balint
 *
 */
public class DataContractMetaInfo {

	private String contractId;
	private Date creationDate;
	private Boolean active;
	private Integer revision;

	private String party1Id;
	private String party2Id;
	private Boolean party1Accepted;
	private Boolean party2Accepted;

	public DataContractMetaInfo() {
		this.active = false;
		this.party1Accepted = false;
		this.party2Accepted = false;
	}

	public DataContractMetaInfo(Document document) {
		this.contractId = document.getString("contractId");
		Object dateAsObject = document.get("creationDate");
		if (dateAsObject instanceof Date) {
			this.creationDate = (Date) dateAsObject;
		}
		if (dateAsObject instanceof Long) {
			this.creationDate = new Date((Long) dateAsObject);
		}
		this.active = document.getBoolean("active", false);
		this.party1Accepted = document.getBoolean("party1Accepted");
		this.party2Accepted = document.getBoolean("party2Accepted");
		this.revision = document.getInteger("revision");
		this.party1Id = document.getString("party1Id");
		this.party2Id = document.getString("party2Id");
	}

	@JsonIgnore
	public Document getDocument() {
		Document document = new Document();
		document.put("contractId", getContractId());
		document.put("creationDate", getCreationDate());
		document.put("active", getActive());
		document.put("party1Accepted", getParty1Accepted());
		document.put("party2Accepted", getParty2Accepted());
		document.put("revision", getRevision());
		document.put("party1Id", getParty1Id());
		document.put("party2Id", getParty2Id());
		return document;
	}

	/**
	 * @return the contractId
	 */
	public String getContractId() {
		return contractId;
	}

	/**
	 * @param contractId
	 *            the contractId to set
	 */
	public void setContractId(String contractId) {
		this.contractId = contractId;
	}

	/**
	 * @return the creationDate
	 */
	public Date getCreationDate() {
		return creationDate;
	}

	/**
	 * @param creationDate
	 *            the creationDate to set
	 */
	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	/**
	 * @return the active
	 */
	public Boolean getActive() {
		return active;
	}

	/**
	 * @param active
	 *            the active to set
	 */
	public void setActive(Boolean active) {
		this.active = active;
	}

	/**
	 * @return the revision
	 */
	public Integer getRevision() {
		return revision;
	}

	/**
	 * @param revision
	 *            the revision to set
	 */
	public void setRevision(Integer revision) {
		this.revision = revision;
	}

	/**
	 * @return the party1Id
	 */
	public String getParty1Id() {
		return party1Id;
	}

	/**
	 * @param party1Id
	 *            the party1Id to set
	 */
	public void setParty1Id(String party1Id) {
		this.party1Id = party1Id;
	}

	/**
	 * @return the party2Id
	 */
	public String getParty2Id() {
		return party2Id;
	}

	/**
	 * @param party2Id
	 *            the party2Id to set
	 */
	public void setParty2Id(String party2Id) {
		this.party2Id = party2Id;
	}

	/**
	 * @return the party1Accepted
	 */
	public Boolean getParty1Accepted() {
		return party1Accepted;
	}

	/**
	 * @param party1Accepted
	 *            the party1Accepted to set
	 */
	public void setParty1Accepted(Boolean party1Accepted) {
		this.party1Accepted = party1Accepted;
	}

	/**
	 * @return the party2Accepted
	 */
	public Boolean getParty2Accepted() {
		return party2Accepted;
	}

	/**
	 * @param party2Accepted
	 *            the party2Accepted to set
	 */
	public void setParty2Accepted(Boolean party2Accepted) {
		this.party2Accepted = party2Accepted;
	}

}
