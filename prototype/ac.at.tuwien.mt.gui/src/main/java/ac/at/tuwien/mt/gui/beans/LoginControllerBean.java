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
import javax.faces.bean.ViewScoped;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ac.at.tuwien.mt.gui.primefaces.Messages;
import ac.at.tuwien.mt.gui.rest.RESTPersonClient;
import ac.at.tuwien.mt.model.exception.AuthenticationException;
import ac.at.tuwien.mt.model.exception.ObjectNotFoundException;
import ac.at.tuwien.mt.model.person.LegalPerson;
import ac.at.tuwien.mt.model.person.NaturalPerson;
import ac.at.tuwien.mt.model.person.Person;
import ac.at.tuwien.mt.model.person.PersonType;

/**
 * @author Florin Bogdan Balint
 *
 */
@SuppressWarnings("serial")
@ManagedBean(name = "lcb")
@ViewScoped
public class LoginControllerBean implements Serializable {

	private static final Logger LOGGER = LogManager.getLogger(LoginControllerBean.class);

	private String email;
	private String password;

	@ManagedProperty(value = "#{ucb}")
	private UserControllerBean userControllerBean;

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

	public String login() {
		LOGGER.info("Login attempt for user: " + email);
		try {
			Person person = RESTPersonClient.authenticate(email, password);
			userControllerBean.setLoggedIn(true);
			userControllerBean.setPerson(person);
			if (person.getPersonType().equals(PersonType.NATURAL)) {
				userControllerBean.setNaturalPerson((NaturalPerson) person);
				userControllerBean.setNatural(true);
			} else {
				userControllerBean.setLegalPerson((LegalPerson) person);
				userControllerBean.setNatural(false);
			}
			return "welcome.xhtml?faces-redirect=true";
		} catch (AuthenticationException | ObjectNotFoundException e) {
			Messages.addMessage(FacesMessage.SEVERITY_ERROR, "Invalid email and/or password!");
		} catch (Exception e) {
			LOGGER.error(e);
			Messages.addMessage(FacesMessage.SEVERITY_ERROR,
					"An unexpected technical error occurred, please contact support!");
		}
		return "index.xhtml";
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

}
