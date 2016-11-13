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
package ac.at.tuwien.mt.model.person;

import org.bson.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * @author Florin Bogdan Balint
 *
 */
@SuppressWarnings("serial")
@JsonIgnoreProperties(ignoreUnknown = true)
public class LegalPerson extends Person {

	private String companyName;
	private String registrationNumber;

	public LegalPerson() {
		super.setPersonType(PersonType.LEGAL);
	}

	public LegalPerson(Document document) {
		super(document);
		super.setPersonType(PersonType.LEGAL);
		this.companyName = document.getString("companyName");
		this.registrationNumber = document.getString("registrationNumber");
	}

	/**
	 * @return the companyName
	 */
	public String getCompanyName() {
		return companyName;
	}

	/**
	 * @param companyName
	 *            the companyName to set
	 */
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	/**
	 * @return the registrationNumber
	 */
	public String getRegistrationNumber() {
		return registrationNumber;
	}

	/**
	 * @param registrationNumber
	 *            the registrationNumber to set
	 */
	public void setRegistrationNumber(String registrationNumber) {
		this.registrationNumber = registrationNumber;
	}

	@JsonIgnore
	public Document getDocument() {
		Document document = super.getDocument();
		document.put("companyName", getCompanyName());
		document.put("registrationNumber", getRegistrationNumber());
		return document;
	}
}
