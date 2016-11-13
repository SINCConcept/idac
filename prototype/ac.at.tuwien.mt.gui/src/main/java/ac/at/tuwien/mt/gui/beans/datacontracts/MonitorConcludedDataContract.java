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

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ac.at.tuwien.mt.gui.rest.RESTDataContractClient;
import ac.at.tuwien.mt.gui.rest.RESTMicroserviceLocator;
import ac.at.tuwien.mt.model.datacontract.DataContract;
import ac.at.tuwien.mt.model.microservice.MicroserviceInfo;
import ac.at.tuwien.mt.model.microservice.MicroserviceType;

/**
 * @author Florin Bogdan Balint
 *
 */
@SuppressWarnings("serial")
@ManagedBean(name = "monitor_concluded_dc")
@ViewScoped
public class MonitorConcludedDataContract implements Serializable {

	private static final Logger LOGGER = LogManager.getLogger(MonitorConcludedDataContract.class);

	private DataContract dataContract;

	public String monitor() {
		if (dataContract.getMonitoring() == null || dataContract.getMonitoring() == Boolean.FALSE) {
			LOGGER.debug("Enabling monitoring.");
			dataContract.setMonitoring(true);
		} else {
			LOGGER.debug("Disabling monitoring.");
			dataContract.setMonitoring(false);
		}

		MicroserviceInfo locateDataContractMicroservice = RESTMicroserviceLocator.locateMicroservice(MicroserviceType.DATACONTRACT);
		RESTDataContractClient client = new RESTDataContractClient(locateDataContractMicroservice);
		client.update(dataContract);

		// update
		return "dc_concluded_list.xhtml?faces-redirect=true";
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

}
