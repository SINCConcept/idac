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

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ac.at.tuwien.mt.gui.beans.UserControllerBean;
import ac.at.tuwien.mt.gui.primefaces.Messages;
import ac.at.tuwien.mt.gui.primefaces.exception.CommunicationException;
import ac.at.tuwien.mt.gui.rest.RESTMicroserviceLocator;
import ac.at.tuwien.mt.gui.rest.RESTRecommendingClient;
import ac.at.tuwien.mt.model.microservice.MicroserviceInfo;
import ac.at.tuwien.mt.model.microservice.MicroserviceType;
import ac.at.tuwien.mt.model.thing.Thing;

/**
 * @author Florin Bogdan Balint
 *
 */
@SuppressWarnings("serial")
@ManagedBean(name = "recommend_thing")
@SessionScoped
public class RecommendThing implements Serializable {

	private static final Logger LOGGER = LogManager.getLogger(RecommendThing.class);

	@ManagedProperty(value = "#{ucb}")
	private UserControllerBean userControllerBean;

	@ManagedProperty(value = "#{conclude_dc}")
	private ConcludeDataContract concludeDataContract;

	public String recommend() {
		String personId = userControllerBean.getPerson().getPersonId();
		LOGGER.debug("Recommending contract for user: " + personId);

		MicroserviceInfo recommendingMicroservice = RESTMicroserviceLocator.locateMicroservice(MicroserviceType.RECOMMENDING);

		RESTRecommendingClient client = new RESTRecommendingClient(recommendingMicroservice);
		try {
			Thing recommendedThing = client.recommend(personId);
			if (recommendedThing == null) {
				// no recommendation could be made
				Messages.addMessage(FacesMessage.SEVERITY_INFO, "Unfortunately, no recommendations could be made for you at the moment.");
				return "welcome.xhtml?faces-redirect=true";
			} else {
				concludeDataContract.getSelectedThings().clear();
				concludeDataContract.getSelectedThings().add(recommendedThing);
			}

			Integer thingAvgNeg = client.getThingAvgNeg(recommendedThing.getThingId());
			if (thingAvgNeg != null) {
				// add message
				Messages.addMessage(FacesMessage.SEVERITY_INFO, "On average people concluded a contract using only " + thingAvgNeg + " negotiation steps.");
			}

		} catch (CommunicationException e) {
			Messages.addMessage(FacesMessage.SEVERITY_ERROR, "Could not connect to server, please try again later!");
		}

		return "dc_conclude.xhtml?faces-redirect=true";
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
	 * @return the concludeDataContract
	 */
	public ConcludeDataContract getConcludeDataContract() {
		return concludeDataContract;
	}

	/**
	 * @param concludeDataContract
	 *            the concludeDataContract to set
	 */
	public void setConcludeDataContract(ConcludeDataContract concludeDataContract) {
		this.concludeDataContract = concludeDataContract;
	}

}
