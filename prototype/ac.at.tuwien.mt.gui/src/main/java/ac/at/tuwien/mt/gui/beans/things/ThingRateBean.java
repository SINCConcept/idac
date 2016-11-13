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
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ac.at.tuwien.mt.gui.beans.UserControllerBean;
import ac.at.tuwien.mt.gui.primefaces.Messages;
import ac.at.tuwien.mt.gui.rest.RESTDataContractClient;
import ac.at.tuwien.mt.gui.rest.RESTMicroserviceLocator;
import ac.at.tuwien.mt.model.microservice.MicroserviceInfo;
import ac.at.tuwien.mt.model.microservice.MicroserviceType;
import ac.at.tuwien.mt.model.thing.Thing;
import ac.at.tuwien.mt.model.thing.rating.Rating;

/**
 * @author Florin Bogdan Balint
 *
 */
@SuppressWarnings("serial")
@ManagedBean(name = "thing_rate")
@SessionScoped
public class ThingRateBean implements Serializable {

	private static final Logger LOGGER = LogManager.getLogger(ThingRateBean.class);

	@ManagedProperty(value = "#{ucb}")
	private UserControllerBean userControllerBean;

	private Thing thing;

	private Integer rating;

	private boolean rated = false;

	public void init() {
		MicroserviceInfo msInfo = RESTMicroserviceLocator.locateMicroservice(MicroserviceType.DATACONTRACT);
		RESTDataContractClient client = new RESTDataContractClient(msInfo);
		this.thing = client.getThing(thing.getThingId());

		rated = false;
		String userId = userControllerBean.getPerson().getPersonId();
		List<Rating> ratings = thing.getRatings();
		for (Rating rating : ratings) {
			if (rating.getUserId().equals(userId)) {
				rated = true;
				this.rating = rating.getRating();
				Messages.addMessage(FacesMessage.SEVERITY_INFO, "You have already rated this Thing!");
			}
		}
	}

	public String rate() {
		String userId = userControllerBean.getPerson().getPersonId();

		LOGGER.info("Rating thing: " + thing.getThingId() + " with: " + rating);
		if (rating == null) {
			Messages.addMessage(FacesMessage.SEVERITY_ERROR, "Please provide a rating of at least 1.");
			return "thing_rate.xhtml?faces-redirect=true";
		}
		if (rating < 1) {
			Messages.addMessage(FacesMessage.SEVERITY_ERROR, "Please provide a rating of at least 1.");
			return "thing_rate.xhtml?faces-redirect=true";
		}

		MicroserviceInfo msInfo = RESTMicroserviceLocator.locateMicroservice(MicroserviceType.DATACONTRACT);
		RESTDataContractClient client = new RESTDataContractClient(msInfo);
		this.thing = client.getThing(thing.getThingId());

		init();
		if (rated) {
			return "dc_concluded_rate.xhtml?faces-redirect=true";
		}

		thing.getRatings().add(new Rating(userId, rating));
		Thing updateThing = client.updateThing(thing);
		if (updateThing == null) {
			LOGGER.error("Could not submit rating, please try again later.");
			Messages.addMessage(FacesMessage.SEVERITY_ERROR, "Could not submit rating, please try again later.");
		} else {
			Messages.addMessage(FacesMessage.SEVERITY_INFO, "Thank you for rating!");
		}

		return "dc_concluded_rate.xhtml?faces-redirect=true";
	}

	/**
	 * @return the rated
	 */
	public boolean isRated() {
		return rated;
	}

	/**
	 * @param rated
	 *            the rated to set
	 */
	public void setRated(boolean rated) {
		this.rated = rated;
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
	 * @return the thing
	 */
	public Thing getThing() {
		return thing;
	}

	/**
	 * @param thing
	 *            the thing to set
	 */
	public void setThing(Thing thing) {
		this.thing = thing;
		init();
	}

	/**
	 * @return the rating
	 */
	public Integer getRating() {
		return rating;
	}

	/**
	 * @param rating
	 *            the rating to set
	 */
	public void setRating(Integer rating) {
		this.rating = rating;
	}

}
