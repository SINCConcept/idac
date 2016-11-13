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

package ac.at.tuwien.mt.gui.beans.things;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.ws.rs.ServiceUnavailableException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ac.at.tuwien.mt.gui.beans.UserControllerBean;
import ac.at.tuwien.mt.gui.primefaces.Messages;
import ac.at.tuwien.mt.gui.rest.RESTDataContractClient;
import ac.at.tuwien.mt.gui.rest.RESTMicroserviceLocator;
import ac.at.tuwien.mt.model.microservice.MicroserviceInfo;
import ac.at.tuwien.mt.model.microservice.MicroserviceType;
import ac.at.tuwien.mt.model.thing.Thing;

/**
 * @author Florin Bogdan Balint
 *
 */
@SuppressWarnings("serial")
@ManagedBean(name = "tob")
@ViewScoped
public class ThingsOverviewBean implements Serializable {

	private static final Logger LOGGER = LogManager.getLogger(ThingsOverviewBean.class);

	private List<Thing> things = new ArrayList<Thing>();

	@ManagedProperty(value = "#{ucb}")
	private UserControllerBean userControllerBean;

	@PostConstruct
	public void init() {
		String personId = userControllerBean.getPerson().getPersonId();
		LOGGER.debug("Getting all things for user: " + personId);

		try {
			MicroserviceInfo locateDataContractMicroservice = RESTMicroserviceLocator.locateMicroservice(MicroserviceType.DATACONTRACT);
			RESTDataContractClient client = new RESTDataContractClient(locateDataContractMicroservice);
			things = client.find(personId);
		} catch (ServiceUnavailableException e) {
			Messages.addMessage(FacesMessage.SEVERITY_ERROR, "Could not connect to server, please try again later!");
		}
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

	/**
	 * @return the things
	 */
	public List<Thing> getThings() {
		return things;
	}

	/**
	 * @param things
	 *            the things to set
	 */
	public void setThings(List<Thing> things) {
		this.things = things;
	}

}
