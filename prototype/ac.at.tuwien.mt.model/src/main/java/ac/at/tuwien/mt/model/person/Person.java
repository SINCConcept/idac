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

import java.io.Serializable;
import java.util.Map;

import org.bson.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import ac.at.tuwien.mt.model.util.StringUtil;

/**
 * Author: Florin Bogdan Balint
 *
 */
@SuppressWarnings("serial")
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class Person implements Serializable {

	private String personId;
	private PersonType personType;
	private String email;
	private String password;
	private Integer revision;
	private Address address;

	public Person() {
		// empty constructor
	}

	public Person(Document document) {
		this.personId = document.getString("personId");
		String personTypeAsString = document.getString("personType");
		if (!StringUtil.isNullOrBlank(personTypeAsString)) {
			this.personType = PersonType.valueOf(personTypeAsString);
		}
		this.email = document.getString("email");
		this.password = document.getString("password");
		this.revision = document.getInteger("revision");

		// set the address
		@SuppressWarnings("unchecked")
		Map<String, Object> object = (Map<String, Object>) document.get("address");
		Document addressDocument = new Document(object);
		this.address = new Address(addressDocument);
	}

	@JsonIgnore
	public Document getDocument() {
		Document document = new Document();
		document.put("personId", getPersonId());
		if (getPersonType() != null) {
			document.put("personType", getPersonType().getProperty());
		}
		document.put("email", getEmail());
		document.put("password", getPassword());
		document.put("revision", getRevision());
		document.put("address", getAddress().getDocument());
		return document;
	}

	/**
	 * @return the userId
	 */
	public String getPersonId() {
		return personId;
	}

	/**
	 * @param personId
	 *            the personId to set
	 */
	public void setPersonId(String personId) {
		this.personId = personId;
	}

	/**
	 * @return the personType
	 */
	public PersonType getPersonType() {
		return personType;
	}

	/**
	 * @param personType
	 *            the personType to set
	 */
	public void setPersonType(PersonType personType) {
		this.personType = personType;
	}

	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @param email
	 *            the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password
	 *            the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @return the address
	 */
	public Address getAddress() {
		if (address == null) {
			address = new Address();
		}
		return address;
	}

	/**
	 * @param address
	 *            the address to set
	 */
	public void setAddress(Address address) {
		this.address = address;
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

}
