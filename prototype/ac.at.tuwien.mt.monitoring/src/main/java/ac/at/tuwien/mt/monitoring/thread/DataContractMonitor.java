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
package ac.at.tuwien.mt.monitoring.thread;

import java.util.Calendar;

import ac.at.tuwien.mt.dao.datacontract.DataContractDAO;
import ac.at.tuwien.mt.dao.monitor.MonitoredDataContractDAO;
import ac.at.tuwien.mt.model.datacontract.DataContract;
import ac.at.tuwien.mt.model.exception.InvalidObjectException;
import ac.at.tuwien.mt.model.exception.ResourceOutOfDateException;
import ac.at.tuwien.mt.model.thing.monitor.MonitoredDataContract;
import ac.at.tuwien.mt.model.thing.monitor.MonitoredQoD;
import ac.at.tuwien.mt.model.thing.monitor.MonitoredQoS;
import ac.at.tuwien.mt.monitoring.thread.internal.MonitoringProcessingHelper;

/**
 * @author Florin Bogdan Balint
 *
 */
public class DataContractMonitor implements Runnable {

	private final String thingId;
	private final DataContract dataContract;
	private final MonitoredQoD monitoredQoD;
	private final MonitoredQoS monitoredQoS;

	private MonitoredDataContractDAO dao;
	private DataContractDAO dcDAO;

	public DataContractMonitor(String thingId, DataContract dataContract, MonitoredQoD monitoredQoD, MonitoredQoS monitoredQoS, MonitoredDataContractDAO monitoredDataContractDAO,
			DataContractDAO dcDAO) {
		this.thingId = thingId;
		this.dao = monitoredDataContractDAO;
		this.dataContract = dataContract;
		this.monitoredQoD = monitoredQoD;
		this.monitoredQoS = monitoredQoS;
		this.dcDAO = dcDAO;
	}

	@Override
	public void run() {
		updateValuesInDB();
	}

	public void updateValuesInDB() {
		// Create a new entry
		MonitoredDataContract monitoredEntry = getFirstMonitoredEntry();

		// see if there are any existing entries
		MonitoredDataContract foundEntry = dao.findOpen(monitoredEntry.getContractId());
		if (foundEntry == null) {
			// the monitoring might have been disabled and we might have
			// out-of-date-data
			if (isMonitoringActive() == false) {
				return;
			}
			// if not insert them
			monitoredEntry.setMonitoringStart(Calendar.getInstance().getTime());
			dao.insert(monitoredEntry);
			return;
		}
		// otherwise compute the new values
		computeNewValues(foundEntry);

		// update
		try {
			dao.update(foundEntry);
		} catch (ResourceOutOfDateException | InvalidObjectException e) {
			// object might have been updated in the mean time -> retry
			updateValuesInDB();
		}
	}

	private boolean isMonitoringActive() {
		DataContract dc = dcDAO.readById(dataContract.getDataContractMetaInfo().getContractId());
		if (dc.getMonitoring() == Boolean.TRUE) {
			return true;
		}
		return false;
	}

	private void computeNewValues(MonitoredDataContract monitoredDataContract) {
		// update the monitored quality of data
		boolean isQoDInexistent = true;
		for (MonitoredQoD oldEntry : monitoredDataContract.getMonitoredQoD()) {
			if (oldEntry.getThingId().equals(thingId)) {
				isQoDInexistent = false;
				// update QoD for this data contract
				oldEntry = MonitoringProcessingHelper.computeNewValues(oldEntry, monitoredQoD);
			}
		}
		if (isQoDInexistent) {
			monitoredDataContract.getMonitoredQoD().add(monitoredQoD);
		}

		// update the monitored quality of service
		boolean isQoSInexistent = true;
		for (MonitoredQoS oldEntry : monitoredDataContract.getMonitoredQoS()) {
			if (oldEntry.getThingId().equals(thingId)) {
				isQoSInexistent = false;
				// update QoS for this data contract
				oldEntry = MonitoringProcessingHelper.computeNewValues(oldEntry, monitoredQoS);
			}
		}
		if (isQoSInexistent) {
			monitoredDataContract.getMonitoredQoS().add(monitoredQoS);
		}
	}

	private MonitoredDataContract getFirstMonitoredEntry() {
		MonitoredDataContract monitoredDataContract = new MonitoredDataContract();
		monitoredDataContract.setContractId(dataContract.getDataContractMetaInfo().getContractId());
		monitoredDataContract.getMonitoredQoD().add(monitoredQoD);
		monitoredDataContract.getMonitoredQoS().add(monitoredQoS);
		return monitoredDataContract;
	}

}
