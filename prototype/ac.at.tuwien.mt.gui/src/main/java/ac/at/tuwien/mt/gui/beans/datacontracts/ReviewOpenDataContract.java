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

import org.apache.commons.lang3.SerializationUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ac.at.tuwien.mt.gui.beans.UserControllerBean;
import ac.at.tuwien.mt.gui.primefaces.Messages;
import ac.at.tuwien.mt.gui.rest.RESTDataContractClient;
import ac.at.tuwien.mt.gui.rest.RESTMicroserviceLocator;
import ac.at.tuwien.mt.model.datacontract.ClausesTrailEntry;
import ac.at.tuwien.mt.model.datacontract.DataContract;
import ac.at.tuwien.mt.model.datacontract.DataContractTrail;
import ac.at.tuwien.mt.model.microservice.MicroserviceInfo;
import ac.at.tuwien.mt.model.microservice.MicroserviceType;
import ac.at.tuwien.mt.model.thing.Thing;
import ac.at.tuwien.mt.model.thing.ThingId;

/**
 * @author Florin Bogdan Balint
 *
 */
@SuppressWarnings("serial")
@ManagedBean(name = "rodc")
@SessionScoped
public class ReviewOpenDataContract implements Serializable {

	private static final Logger LOGGER = LogManager.getLogger(ReviewOpenDataContract.class);

	// original data contract - a copy from the database
	private DataContract originalDataContract;
	// current data contract
	private DataContract contract;
	// latest history entry
	private ClausesTrailEntry latestHistEntry;
	// flag indicating if the contract has been changed.
	public boolean unchanged = false;
	// the role of the current user with respect to this contract
	public boolean buyerRole;
	// this flag disables all fields from the GUI
	// Idea: if the buyer send an offer, he can see what offer he send, but he
	// cannot edit it!
	public boolean contractAwaitingAnswer;

	private List<Thing> things = new ArrayList<Thing>();

	@ManagedProperty(value = "#{ucb}")
	private UserControllerBean userControllerBean;

	public String concludeContract() {
		LOGGER.debug("Accepting offer!");
		try {
			// update contract
			contract.getDataContractMetaInfo().setParty1Accepted(true);
			contract.getDataContractMetaInfo().setParty2Accepted(true);
			contract.getDataContractMetaInfo().setActive(false);

			// save contract
			MicroserviceInfo locateDataContractMicroservice = RESTMicroserviceLocator.locateMicroservice(MicroserviceType.DATACONTRACT);
			RESTDataContractClient client = new RESTDataContractClient(locateDataContractMicroservice);
			client.update(contract);
		} catch (ServiceUnavailableException e) {
			return returnToWelcome(FacesMessage.SEVERITY_ERROR, "Could not connect to server. Please try again later.");
		}
		return returnToWelcome(FacesMessage.SEVERITY_INFO, "The contract was concluded succesfully!");
	}

	public String buyerOffer() {
		LOGGER.debug("Accepting offer!");
		try {
			// update contract
			contract.getDataContractMetaInfo().setParty1Accepted(false);
			contract.getDataContractMetaInfo().setParty2Accepted(true);
			contract.getDataContractMetaInfo().setActive(false);
			ClausesTrailEntry entry = new ClausesTrailEntry();
			entry.setControlAndRelationship(originalDataContract.getControlAndRelationship());
			entry.setDataRights(originalDataContract.getDataRights());
			entry.setPricingModel(originalDataContract.getPricingModel());
			entry.setPurchasingPolicy(originalDataContract.getPurchasingPolicy());

			// save contract
			MicroserviceInfo locateDataContractMicroservice = RESTMicroserviceLocator.locateMicroservice(MicroserviceType.DATACONTRACT);
			RESTDataContractClient client = new RESTDataContractClient(locateDataContractMicroservice);
			client.update(contract);
		} catch (ServiceUnavailableException e) {
			return returnToWelcome(FacesMessage.SEVERITY_ERROR, "Could not connect to server. Please try again later.");
		}
		return returnToWelcome(FacesMessage.SEVERITY_INFO, "The contract offer was sent succesfully!");
	}

	public String providerOffer() {
		LOGGER.debug("Accepting offer!");
		try {
			// update contract
			contract.getDataContractMetaInfo().setParty1Accepted(true);
			contract.getDataContractMetaInfo().setParty2Accepted(false);
			contract.getDataContractMetaInfo().setActive(false);
			ClausesTrailEntry entry = new ClausesTrailEntry();
			entry.setControlAndRelationship(originalDataContract.getControlAndRelationship());
			entry.setDataRights(originalDataContract.getDataRights());
			entry.setPricingModel(originalDataContract.getPricingModel());
			entry.setPurchasingPolicy(originalDataContract.getPurchasingPolicy());

			// save contract
			MicroserviceInfo locateDataContractMicroservice = RESTMicroserviceLocator.locateMicroservice(MicroserviceType.DATACONTRACT);
			RESTDataContractClient client = new RESTDataContractClient(locateDataContractMicroservice);
			client.update(contract);
		} catch (ServiceUnavailableException e) {
			return returnToWelcome(FacesMessage.SEVERITY_ERROR, "Could not connect to server. Please try again later.");
		}
		return returnToWelcome(FacesMessage.SEVERITY_INFO, "The contract offer was sent succesfully!");
	}

	private String returnToWelcome(Severity severity, String message) {
		Messages.addMessage(severity, message);
		FacesContext.getCurrentInstance().getExternalContext().getSessionMap().remove("rodc");
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
	 * @return the contract
	 */
	public DataContract getContract() {
		return contract;
	}

	/**
	 * @param contract
	 *            the contract to set
	 */
	public void setContract(DataContract contract) {
		this.contract = contract;
		originalDataContract = SerializationUtils.clone(contract);
		getDataContractTrail(contract.getDataContractMetaInfo().getContractId());

		MicroserviceInfo locateDataContractMicroservice = RESTMicroserviceLocator.locateMicroservice(MicroserviceType.DATACONTRACT);
		RESTDataContractClient client = new RESTDataContractClient(locateDataContractMicroservice);
		things.clear();
		for (ThingId thingId : contract.getThingIds()) {
			Thing thing = client.getThing(thingId.getThingId());
			things.add(thing);
		}
	}

	public void getDataContractTrail(String contractId) {
		if (contractId == null) {
			return;
		}
		MicroserviceInfo locateDataContractMicroservice = RESTMicroserviceLocator.locateMicroservice(MicroserviceType.DATACONTRACT);
		RESTDataContractClient client = new RESTDataContractClient(locateDataContractMicroservice);
		DataContractTrail dataContractTrail = client.getDataContractTrail(contractId);
		List<ClausesTrailEntry> clausesHistory = dataContractTrail.getClausesTrail();
		if (!clausesHistory.isEmpty()) {
			int size = clausesHistory.size();
			// always make sure to get the last offer - which was mine
			int index = size - 1;
			if (size >= 2) {
				index = size - 2;
			}
			latestHistEntry = clausesHistory.get(index);
		}
	}

	/**
	 * @return the latestHistEntry
	 */
	public ClausesTrailEntry getLatestHistEntry() {
		return latestHistEntry;
	}

	/**
	 * @param latestHistEntry
	 *            the latestHistEntry to set
	 */
	public void setLatestHistEntry(ClausesTrailEntry latestHistEntry) {
		this.latestHistEntry = latestHistEntry;
	}

	public void setUnchanged(boolean unchanged) {
		this.unchanged = unchanged;
	}

	public boolean isUnchanged() {
		if (originalDataContract.getDocument().toJson().equalsIgnoreCase(contract.getDocument().toJson())) {
			return true;
		}
		return false;
	}

	/**
	 * @return the contractAwaitingAnswer
	 */
	public boolean isContractAwaitingAnswer() {
		// case 1: I am a buyer and I am awaiting an answer
		if (isBuyerRole()) {
			if (contract.getDataContractMetaInfo().getParty1Accepted().booleanValue() == false && contract.getDataContractMetaInfo().getParty2Accepted().booleanValue() == true) {
				// in this case I already sent the contract for review
				contractAwaitingAnswer = true;
			} else {
				contractAwaitingAnswer = false;
			}
		} else {
			// I am a provider
			if (contract.getDataContractMetaInfo().getParty1Accepted().booleanValue() == true && contract.getDataContractMetaInfo().getParty2Accepted().booleanValue() == false) {
				// in this case I already sent the contract for review
				contractAwaitingAnswer = true;
			} else {
				contractAwaitingAnswer = false;
			}
		}
		// case 2: I am a provider and I am awaiting an answer
		return contractAwaitingAnswer;
	}

	/**
	 * @param contractAwaitingAnswer
	 *            the contractAwaitingAnswer to set
	 */
	public void setContractAwaitingAnswer(boolean contractAwaitingAnswer) {
		this.contractAwaitingAnswer = contractAwaitingAnswer;
	}

	/**
	 * @return the buyerRole
	 */
	public boolean isBuyerRole() {
		String loggedInId = userControllerBean.getPerson().getPersonId();
		String buyerId = contract.getDataContractMetaInfo().getParty2Id();
		if (loggedInId.equals(buyerId)) {
			buyerRole = true;
		} else {
			buyerRole = false;
		}
		return buyerRole;
	}

	/**
	 * @param buyerRole
	 *            the buyerRole to set
	 */
	public void setBuyerRole(boolean buyerRole) {
		this.buyerRole = buyerRole;
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
