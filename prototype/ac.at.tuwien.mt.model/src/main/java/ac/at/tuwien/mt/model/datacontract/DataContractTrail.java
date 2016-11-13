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

/**
 * @author Florin Bogdan Balint
 *
 */
@SuppressWarnings("serial")
@JsonIgnoreProperties(ignoreUnknown = true)
public class DataContractTrail implements Serializable {

	// data object specific params
	private String contractId;
	private Integer revision;

	private List<ClausesTrailEntry> clausesTrail = new ArrayList<ClausesTrailEntry>();

	public DataContractTrail() {
		// empty constructor
	}

	public DataContractTrail(Document document) {
		this.contractId = document.getString("contractId");
		this.revision = document.getInteger("revision");

		Object listObject2 = document.get("clausesTrail");
		if (listObject2 instanceof List) {
			List<?> list2 = (List<?>) listObject2;
			for (Object object : list2) {
				if (object instanceof Document) {
					Document objectAsDoc = (Document) object;
					this.clausesTrail.add(new ClausesTrailEntry(objectAsDoc));
				}
			}
		}
	}

	@JsonIgnore
	public Document getDocument() {
		Document document = new Document();
		document.put("contractId", contractId);
		document.put("revision", revision);

		// add the history
		List<Document> trailDocument = new ArrayList<Document>();
		for (ClausesTrailEntry dc : clausesTrail) {
			trailDocument.add(dc.getDocument());
		}
		document.put("clausesTrail", trailDocument);
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
	 * @return the clausesTrail
	 */
	public List<ClausesTrailEntry> getClausesTrail() {
		return clausesTrail;
	}

	/**
	 * @param clausesTrail
	 *            the clausesTrail to set
	 */
	public void setClausesTrail(List<ClausesTrailEntry> clausesTrail) {
		this.clausesTrail = clausesTrail;
	}

}
