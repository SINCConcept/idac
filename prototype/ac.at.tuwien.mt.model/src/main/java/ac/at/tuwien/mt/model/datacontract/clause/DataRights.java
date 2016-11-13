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
public class DataRights implements Serializable {

	private Boolean derivation;
	private Boolean collection;
	private Boolean reproduction;
	private Boolean commercialUsage;

	public DataRights() {
		this.derivation = false;
		this.collection = false;
		this.reproduction = false;
		this.commercialUsage = false;
	}

	public DataRights(Document document) {
		if (document != null) {
			this.derivation = document.getBoolean("derivation");
			this.collection = document.getBoolean("collection");
			this.reproduction = document.getBoolean("reproduction");
			this.commercialUsage = document.getBoolean("commercialUsage");
		}
	}

	@JsonIgnore
	public Document getDocument() {
		Document document = new Document();
		document.put("derivation", getDerivation());
		document.put("collection", getCollection());
		document.put("reproduction", getReproduction());
		document.put("commercialUsage", getCommercialUsage());
		return document;
	}

	/**
	 * @return the derivation
	 */
	public Boolean getDerivation() {
		return derivation;
	}

	/**
	 * @param derivation
	 *            the derivation to set
	 */
	public void setDerivation(Boolean derivation) {
		this.derivation = derivation;
	}

	/**
	 * @return the collection
	 */
	public Boolean getCollection() {
		return collection;
	}

	/**
	 * @param collection
	 *            the collection to set
	 */
	public void setCollection(Boolean collection) {
		this.collection = collection;
	}

	/**
	 * @return the reproduction
	 */
	public Boolean getReproduction() {
		return reproduction;
	}

	/**
	 * @param reproduction
	 *            the reproduction to set
	 */
	public void setReproduction(Boolean reproduction) {
		this.reproduction = reproduction;
	}

	/**
	 * @return the commercialUsage
	 */
	public Boolean getCommercialUsage() {
		return commercialUsage;
	}

	/**
	 * @param commercialUsage
	 *            the commercialUsage to set
	 */
	public void setCommercialUsage(Boolean commercialUsage) {
		this.commercialUsage = commercialUsage;
	}

}
