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

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;

import ac.at.tuwien.mt.gui.beans.UserControllerBean;
import ac.at.tuwien.mt.gui.rest.RESTDataContractClient;
import ac.at.tuwien.mt.gui.rest.RESTMicroserviceLocator;
import ac.at.tuwien.mt.model.datacontract.DataContract;
import ac.at.tuwien.mt.model.microservice.MicroserviceInfo;
import ac.at.tuwien.mt.model.microservice.MicroserviceType;
import ac.at.tuwien.mt.model.thing.Thing;
import ac.at.tuwien.mt.model.thing.ThingId;

@SuppressWarnings("serial")
@ManagedBean(name = "cdc")
@SessionScoped
public class ConcludedDataContract implements Serializable {

	@ManagedProperty(value = "#{ucb}")
	private UserControllerBean userControllerBean;

	private DataContract dataContract;

	private List<Thing> things = new ArrayList<Thing>();

	// the role of the current user with respect to this contract
	public boolean buyerRole;

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

		MicroserviceInfo locateDataContractMicroservice = RESTMicroserviceLocator.locateMicroservice(MicroserviceType.DATACONTRACT);
		RESTDataContractClient client = new RESTDataContractClient(locateDataContractMicroservice);
		things.clear();
		for (ThingId thingId : dataContract.getThingIds()) {
			Thing thing = client.getThing(thingId.getThingId());
			things.add(thing);
		}
	}

	/**
	 * @return the buyerRole
	 */
	public boolean isBuyerRole() {
		String loggedInId = userControllerBean.getPerson().getPersonId();
		String buyerId = dataContract.getDataContractMetaInfo().getParty2Id();
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
