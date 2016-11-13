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

package ac.at.tuwien.mt.gui.beans.datacontracts;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.ws.rs.ServiceUnavailableException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ac.at.tuwien.mt.gui.beans.UserControllerBean;
import ac.at.tuwien.mt.gui.primefaces.Messages;
import ac.at.tuwien.mt.gui.rest.RESTDataContractClient;
import ac.at.tuwien.mt.gui.rest.RESTMicroserviceLocator;
import ac.at.tuwien.mt.model.datacontract.DataContract;
import ac.at.tuwien.mt.model.microservice.MicroserviceInfo;
import ac.at.tuwien.mt.model.microservice.MicroserviceType;
import ac.at.tuwien.mt.model.thing.Thing;
import ac.at.tuwien.mt.model.thing.ThingId;

/**
 * @author Florin Bogdan Balint
 *
 */
@SuppressWarnings("serial")
@ManagedBean(name = "conclude_dc")
@SessionScoped
public class ConcludeDataContract implements Serializable {

	private static final Logger LOGGER = LogManager.getLogger(ConcludeDataContract.class);

	private String personId;
	private List<Thing> things = new ArrayList<Thing>();
	private List<Thing> selectedThings = new ArrayList<Thing>();

	// for the conclusion
	private DataContract dataContract = new DataContract();

	private boolean conclusionPossible;

	@ManagedProperty(value = "#{ucb}")
	private UserControllerBean userControllerBean;

	public String prepareContractNegotiation() {
		// check if there is only one thing selected
		if (selectedThings.size() == 1) {
			// in this case prefill the dataContract based on the data contract
			// template of the thing
			dataContract.setDataRights(selectedThings.get(0).getDataRights());
			dataContract.setPricingModel(selectedThings.get(0).getPricingModel());
			dataContract.setControlAndRelationship(selectedThings.get(0).getControlAndRelationship());
			dataContract.setPurchasingPolicy(selectedThings.get(0).getPurchasingPolicy());
		}

		return "dc_conclude.xhtml?faces-redirect=true";
	}

	public String sendOffer() {

		dataContract.getDataContractMetaInfo().setParty1Id(personId);
		dataContract.getDataContractMetaInfo().setParty2Id(userControllerBean.getPerson().getPersonId());
		dataContract.getDataContractMetaInfo().setParty1Accepted(false);
		dataContract.getDataContractMetaInfo().setParty2Accepted(true);
		dataContract.getDataContractMetaInfo().setActive(false);
		for (Thing selThing : selectedThings) {
			dataContract.getThingIds().add(new ThingId(selThing.getThingId()));
		}

		try {
			MicroserviceInfo locateDataContractMicroservice = RESTMicroserviceLocator.locateMicroservice(MicroserviceType.DATACONTRACT);
			RESTDataContractClient client = new RESTDataContractClient(locateDataContractMicroservice);
			client.insert(dataContract);
		} catch (ServiceUnavailableException e) {
			return returnToWelcome(FacesMessage.SEVERITY_ERROR, "Could not connect to server, please try again later!");
		}

		return returnToWelcome(FacesMessage.SEVERITY_INFO, "The contract offer has been sent successfully!");
	}

	/**
	 * Returns to the welcome page and removes this bean from the session
	 * 
	 * @param severity
	 * @param message
	 * @return
	 */
	private String returnToWelcome(Severity severity, String message) {
		Messages.addMessage(severity, message);
		FacesContext.getCurrentInstance().getExternalContext().getSessionMap().remove("conclude_dc");
		return "welcome.xhtml";
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
		LOGGER.debug("Getting all things for user: " + personId);
		try {
			MicroserviceInfo locateDataContractMicroservice = RESTMicroserviceLocator.locateMicroservice(MicroserviceType.DATACONTRACT);
			RESTDataContractClient client = new RESTDataContractClient(locateDataContractMicroservice);
			things = client.find(personId);
		} catch (Exception e) {
			Messages.addMessage(FacesMessage.SEVERITY_ERROR, "Could not connect to server, please try again later!");
		}
		return things;
	}

	// GENERATED GETTERS AND SETTERS

	/**
	 * @return the selectedThings
	 */
	public List<Thing> getSelectedThings() {
		return selectedThings;
	}

	/**
	 * @param selectedThings
	 *            the selectedThings to set
	 */
	public void setSelectedThings(List<Thing> selectedThings) {
		this.selectedThings = selectedThings;
	}

	/**
	 * @return the personId
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
	 * @param things
	 *            the things to set
	 */
	public void setThings(List<Thing> things) {
		this.things = things;
	}

	/**
	 * @return the dataContract
	 */
	public DataContract getDataContract() {
		return dataContract;
	}

	/**
	 * @param dataContract
	 *            the dataContract to set
	 */
	public void setDataContract(DataContract dataContract) {
		this.dataContract = dataContract;
	}

	/**
	 * @return the conclusionPossible
	 */
	public boolean isConclusionPossible() {
		if (things.isEmpty()) {
			conclusionPossible = false;
		} else {
			conclusionPossible = true;
		}
		return conclusionPossible;
	}

	/**
	 * @param conclusionPossible
	 *            the conclusionPossible to set
	 */
	public void setConclusionPossible(boolean conclusionPossible) {
		this.conclusionPossible = conclusionPossible;
	}

}
