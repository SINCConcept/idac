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
package ac.at.tuwien.mt.gui.beans;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ac.at.tuwien.mt.model.person.LegalPerson;
import ac.at.tuwien.mt.model.person.NaturalPerson;
import ac.at.tuwien.mt.model.person.Person;

/**
 * @author Florin Bogdan Balint
 *
 */
@SuppressWarnings("serial")
@ManagedBean(name = "ucb")
@SessionScoped
public class UserControllerBean implements Serializable {

	private static final Logger LOGGER = LogManager.getLogger(UserControllerBean.class);

	private NaturalPerson naturalPerson;
	private LegalPerson legalPerson;

	private Person person;
	private boolean loggedIn;
	private boolean natural;

	// menubar: the index of the active menu
	private int activeMenuParam;

	public UserControllerBean() {
		person = new NaturalPerson();
		naturalPerson = new NaturalPerson();
		legalPerson = new LegalPerson();
	}

	/**
	 * @return the person
	 */
	public Person getPerson() {
		return person;
	}

	/**
	 * @param person
	 *            the person to set
	 */
	public void setPerson(Person person) {
		LOGGER.debug("Setting person.");
		this.person = person;
	}

	/**
	 * @return the loggedIn
	 */
	public boolean isLoggedIn() {
		return loggedIn;
	}

	/**
	 * @param loggedIn
	 *            the loggedIn to set
	 */
	public void setLoggedIn(boolean loggedIn) {
		this.loggedIn = loggedIn;
	}

	/**
	 * @return the activeMenuParam
	 */
	public int getActiveMenuParam() {
		return activeMenuParam;
	}

	/**
	 * @param activeMenuParam
	 *            the activeMenuParam to set
	 */
	public void setActiveMenuParam(int activeMenuParam) {
		this.activeMenuParam = activeMenuParam;
	}

	/**
	 * @return the naturalPerson
	 */
	public NaturalPerson getNaturalPerson() {
		return naturalPerson;
	}

	/**
	 * @param naturalPerson
	 *            the naturalPerson to set
	 */
	public void setNaturalPerson(NaturalPerson naturalPerson) {
		this.naturalPerson = naturalPerson;
	}

	/**
	 * @return the legalPerson
	 */
	public LegalPerson getLegalPerson() {
		return legalPerson;
	}

	/**
	 * @param legalPerson
	 *            the legalPerson to set
	 */
	public void setLegalPerson(LegalPerson legalPerson) {
		this.legalPerson = legalPerson;
	}

	/**
	 * @return the natural
	 */
	public boolean isNatural() {
		return natural;
	}

	/**
	 * @param natural
	 *            the natural to set
	 */
	public void setNatural(boolean natural) {
		this.natural = natural;
	}

}
