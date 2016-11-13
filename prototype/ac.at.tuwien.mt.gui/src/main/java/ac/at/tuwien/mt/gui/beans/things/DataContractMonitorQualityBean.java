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
import javax.faces.bean.SessionScoped;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ac.at.tuwien.mt.gui.primefaces.Messages;
import ac.at.tuwien.mt.gui.primefaces.exception.CommunicationException;
import ac.at.tuwien.mt.gui.rest.RESTMonitoringClient;
import ac.at.tuwien.mt.model.datacontract.DataContract;
import ac.at.tuwien.mt.model.thing.monitor.MonitoredDataContract;
import ac.at.tuwien.mt.model.util.StringUtil;

/**
 * @author Florin Bogdan Balint
 *
 */
@SuppressWarnings("serial")
@ManagedBean(name = "dc_monitor_quality")
@SessionScoped
public class DataContractMonitorQualityBean implements Serializable {

	private static final Logger LOGGER = LogManager.getLogger(DataContractMonitorQualityBean.class);

	private DataContract dataContract;

	private List<MonitoredDataContract> monitoredDataContracts;

	public void init() {
		LOGGER.debug("Getting monitored statistics for DataContract.");
		String contractId = dataContract.getDataContractMetaInfo().getContractId();
		if (!StringUtil.isNullOrBlank(contractId)) {
			try {
				monitoredDataContracts = RESTMonitoringClient.findMonitoredDataContract(contractId);
			} catch (CommunicationException e) {
				Messages.addMessage(FacesMessage.SEVERITY_ERROR, "This service is currently unavailable!");
			}
		}
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
	 * @return the monitoredDataContract
	 */
	public List<MonitoredDataContract> getMonitoredDataContracts() {
		init();
		return monitoredDataContracts;
	}

	/**
	 * @param monitoredDataContract
	 *            the monitoredDataContract to set
	 */
	public void setMonitoredDataContracts(List<MonitoredDataContract> monitoredDataContracts) {
		this.monitoredDataContracts = monitoredDataContracts;
	}

}
