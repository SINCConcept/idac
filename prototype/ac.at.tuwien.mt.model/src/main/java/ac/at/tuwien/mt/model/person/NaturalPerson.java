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

import java.util.Date;

import org.bson.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * @author Florin Bogdan Balint
 *
 */
@SuppressWarnings("serial")
@JsonIgnoreProperties(ignoreUnknown = true)
public class NaturalPerson extends Person {

	private String firstName;
	private String lastName;
	private Date birthDate;

	public NaturalPerson() {
		super.setPersonType(PersonType.NATURAL);
	}

	public NaturalPerson(Document document) {
		super(document);
		super.setPersonType(PersonType.NATURAL);
		this.firstName = document.getString("firstName");
		this.lastName = document.getString("lastName");
		Object birthDateAsObject = document.get("birthDate");
		if (birthDateAsObject instanceof Date) {
			this.birthDate = (Date) birthDateAsObject;
		}
		if (birthDateAsObject instanceof Long) {
			this.birthDate = new Date((Long) birthDateAsObject);
		}
	}

	@JsonIgnore
	public Document getDocument() {
		Document document = super.getDocument();
		document.put("firstName", getFirstName());
		document.put("lastName", getLastName());
		document.put("birthDate", getBirthDate());
		return document;
	}

	/**
	 * @return the firstName
	 */
	public String getFirstName() {
		return firstName;
	}

	/**
	 * @param firstName
	 *            the firstName to set
	 */
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	/**
	 * @return the lastName
	 */
	public String getLastName() {
		return lastName;
	}

	/**
	 * @param lastName
	 *            the lastName to set
	 */
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	/**
	 * @return the birthDate
	 */
	public Date getBirthDate() {
		return birthDate;
	}

	/**
	 * @param birthDate
	 *            the birthDate to set
	 */
	public void setBirthDate(Date birthDate) {
		this.birthDate = birthDate;
	}

}
