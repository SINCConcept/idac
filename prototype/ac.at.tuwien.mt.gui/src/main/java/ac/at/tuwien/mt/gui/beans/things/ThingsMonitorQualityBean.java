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

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ac.at.tuwien.mt.gui.primefaces.Messages;
import ac.at.tuwien.mt.gui.primefaces.exception.CommunicationException;
import ac.at.tuwien.mt.gui.rest.RESTMonitoringClient;
import ac.at.tuwien.mt.model.thing.monitor.MonitoredQoD;
import ac.at.tuwien.mt.model.thing.monitor.MonitoredQoS;
import ac.at.tuwien.mt.model.util.StringUtil;

/**
 * @author Florin Bogdan Balint
 *
 */
@SuppressWarnings("serial")
@ManagedBean(name = "t_monitor_quality")
@SessionScoped
public class ThingsMonitorQualityBean implements Serializable {

	private static final Logger LOGGER = LogManager.getLogger(ThingsMonitorQualityBean.class);

	private MonitoredQoD monitoredQoD;
	private MonitoredQoS monitoredQoS;

	private String thingId;

	public void initQoD() {
		LOGGER.debug("Getting monitored statistics for QoD.");
		if (!StringUtil.isNullOrBlank(thingId)) {
			try {
				monitoredQoD = RESTMonitoringClient.findQoD(thingId);
			} catch (CommunicationException e) {
				Messages.addMessage(FacesMessage.SEVERITY_ERROR, "This service is currently unavailable!");
			}
		}
	}

	public void initQoS() {
		LOGGER.debug("Getting monitored statistics for QoS.");
		if (!StringUtil.isNullOrBlank(thingId)) {
			try {
				monitoredQoS = RESTMonitoringClient.findQoS(thingId);
			} catch (CommunicationException e) {
				Messages.addMessage(FacesMessage.SEVERITY_ERROR, "This service is currently unavailable!");
			}
		}
	}

	/**
	 * @return the thingId
	 */
	public String getThingId() {
		return thingId;
	}

	/**
	 * @param thingId
	 *            the thingId to set
	 */
	public void setThingId(String thingId) {
		this.thingId = thingId;
	}

	/**
	 * @return the monitoredQoD
	 */
	public MonitoredQoD getMonitoredQoD() {
		initQoD();
		return monitoredQoD;
	}

	/**
	 * @param monitoredQoD
	 *            the monitoredQoD to set
	 */
	public void setMonitoredQoD(MonitoredQoD monitoredQoD) {
		this.monitoredQoD = monitoredQoD;
	}

	/**
	 * @return the monitoredQoS
	 */
	public MonitoredQoS getMonitoredQoS() {
		initQoS();
		return monitoredQoS;
	}

	/**
	 * @param monitoredQoS
	 *            the monitoredQoS to set
	 */
	public void setMonitoredQoS(MonitoredQoS monitoredQoS) {
		this.monitoredQoS = monitoredQoS;
	}

}
