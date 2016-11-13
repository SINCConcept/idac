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

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ac.at.tuwien.mt.gui.primefaces.Messages;
import ac.at.tuwien.mt.gui.rest.RESTPersonClient;
import ac.at.tuwien.mt.model.exception.ResourceOutOfDateException;
import ac.at.tuwien.mt.model.person.Address;
import ac.at.tuwien.mt.model.person.LegalPerson;
import ac.at.tuwien.mt.model.person.NaturalPerson;
import ac.at.tuwien.mt.model.person.PersonType;

/**
 * @author Florin Bogdan Balint
 *
 */
@SuppressWarnings("serial")
@ManagedBean(name = "rcb")
@SessionScoped
public class RegistrationControllerBean implements Serializable {

	private static final Logger LOGGER = LogManager.getLogger(RegistrationControllerBean.class);

	private NaturalPerson naturalPerson = new NaturalPerson();
	private LegalPerson legalPerson = new LegalPerson();
	private String passwordRepeat;
	private boolean typeNaturalPerson;

	@ManagedProperty(value = "#{ucb}")
	private UserControllerBean userControllerBean;

	public RegistrationControllerBean() {
		naturalPerson.setAddress(new Address());
		legalPerson.setAddress(new Address());
	}

	/**
	 * @return the userControllerBean
	 */
	public UserControllerBean getUserControllerBean() {
		return userControllerBean;
	}

	/**
	 * @param userControllerBean
	 *            the userControllerBean to set
	 */
	public void setUserControllerBean(UserControllerBean userControllerBean) {
		this.userControllerBean = userControllerBean;
	}

	public String registerStep2() {
		if (typeNaturalPerson) {
			return "register_natural.xhtml?faces-redirect=true";
		} else {
			return "register_legal.xhtml?faces-redirect=true";
		}
	}

	public String registerNatural() {
		if (!passwordRepeat.equals(naturalPerson.getPassword())) {
			Messages.addMessage(FacesMessage.SEVERITY_ERROR, "The password and the password confirmation do not match");
			return "register_natural.xhtml";
		}
		LOGGER.info("Registration attempt for user: " + naturalPerson.getEmail());
		try {
			naturalPerson.setPersonType(PersonType.NATURAL);
			NaturalPerson registerdPerson = (NaturalPerson) RESTPersonClient.register(naturalPerson);
			userControllerBean.setPerson(registerdPerson);
			userControllerBean.setNaturalPerson((NaturalPerson) registerdPerson);
			userControllerBean.setNatural(true);
			userControllerBean.setLoggedIn(true);
		} catch (ResourceOutOfDateException e) {
			Messages.addMessage(FacesMessage.SEVERITY_ERROR, "This email is already registered!");
			return "register_natural.xhtml";
		} catch (Exception e) {
			Messages.addMessage(FacesMessage.SEVERITY_ERROR, "Registration failed - please contact support / check the configuration!");
			return "register_natural.xhtml";
		}
		return "welcome.xhtml?faces-redirect=true";
	}

	public String registerLegal() {
		if (!passwordRepeat.equals(legalPerson.getPassword())) {
			Messages.addMessage(FacesMessage.SEVERITY_ERROR, "The password and the password confirmation do not match");
			return "register_legal.xhtml";
		}
		LOGGER.info("Registration attempt for user: " + legalPerson.getEmail());
		try {
			legalPerson.setPersonType(PersonType.LEGAL);
			LegalPerson registerdPerson = (LegalPerson) RESTPersonClient.register(legalPerson);

			userControllerBean.setPerson(registerdPerson);
			userControllerBean.setLegalPerson((LegalPerson) registerdPerson);
			userControllerBean.setNatural(false);
			userControllerBean.setLoggedIn(true);
		} catch (ResourceOutOfDateException e) {
			Messages.addMessage(FacesMessage.SEVERITY_ERROR, "This email is already registered!");
			return "register_legal.xhtml";
		} catch (Exception e) {
			Messages.addMessage(FacesMessage.SEVERITY_ERROR, "Registration failed - please contact support / check the configuration!");
			return "register_legal.xhtml";
		}
		return "welcome.xhtml?faces-redirect=true";
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
	 * @return the passwordRepeat
	 */
	public String getPasswordRepeat() {
		return passwordRepeat;
	}

	/**
	 * @param passwordRepeat
	 *            the passwordRepeat to set
	 */
	public void setPasswordRepeat(String passwordRepeat) {
		this.passwordRepeat = passwordRepeat;
	}

	/**
	 * @return the typeNaturalPerson
	 */
	public boolean isTypeNaturalPerson() {
		return typeNaturalPerson;
	}

	/**
	 * @param typeNaturalPerson
	 *            the typeNaturalPerson to set
	 */
	public void setTypeNaturalPerson(boolean typeNaturalPerson) {
		this.typeNaturalPerson = typeNaturalPerson;
	}

}
