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
package ac.at.tuwien.mt.model.thing.monitor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.bson.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Represents a monitored data contract component.
 * 
 * @author Florin Bogdan Balint
 *
 */
@SuppressWarnings("serial")
@JsonIgnoreProperties(ignoreUnknown = true)
public class MonitoredDataContract implements Serializable {

	private String contractId;
	private long revision;

	private Date monitoringStart;
	private Date monitoringEnd;

	private List<MonitoredQoD> monitoredQoD = new ArrayList<MonitoredQoD>();
	private List<MonitoredQoS> monitoredQoS = new ArrayList<MonitoredQoS>();

	public MonitoredDataContract() {
		// empty constructor
	}

	public MonitoredDataContract(Document document) {
		this.contractId = document.getString("contractId");
		this.revision = document.getLong("revision");

		Object listObject1 = document.get("monitoredQoD");
		if (listObject1 instanceof List) {
			List<?> list = (List<?>) listObject1;
			for (Object object : list) {
				if (object instanceof Document) {
					Document objectAsDoc = (Document) object;
					this.monitoredQoD.add(new MonitoredQoD(objectAsDoc));
				}
			}
		}

		Object listObject2 = document.get("monitoredQoS");
		if (listObject2 instanceof List) {
			List<?> list = (List<?>) listObject2;
			for (Object object : list) {
				if (object instanceof Document) {
					Document objectAsDoc = (Document) object;
					this.monitoredQoS.add(new MonitoredQoS(objectAsDoc));
				}
			}
		}

		Object monitoringStart = document.get("monitoringStart");
		if (monitoringStart instanceof Date) {
			this.monitoringStart = (Date) monitoringStart;
		}
		if (monitoringStart instanceof Long) {
			this.monitoringStart = new Date((Long) monitoringStart);
		}

		Object monitoringEnd = document.get("monitoringEnd");
		if (monitoringEnd instanceof Date) {
			this.monitoringEnd = (Date) monitoringEnd;
		}
		if (monitoringEnd instanceof Long) {
			this.monitoringEnd = new Date((Long) monitoringEnd);
		}
	}

	@JsonIgnore
	public Document getDocument() {
		Document document = new Document();
		document.put("contractId", contractId);
		document.put("revision", revision);

		// add the qod
		List<Document> qodDocument = new ArrayList<Document>();
		for (MonitoredQoD doc : monitoredQoD) {
			qodDocument.add(doc.getDocument());
		}
		document.put("monitoredQoD", qodDocument);

		// add the qos
		List<Document> qosDocument = new ArrayList<Document>();
		for (MonitoredQoS doc : monitoredQoS) {
			qosDocument.add(doc.getDocument());
		}
		document.put("monitoredQoS", qosDocument);

		document.put("monitoringStart", monitoringStart);
		document.put("monitoringEnd", monitoringEnd);

		return document;
	}

	/**
	 * @return the contractId
	 */
	public String getContractId() {
		return contractId;
	}

	/**
	 * @param contractId
	 *            the contractId to set
	 */
	public void setContractId(String contractId) {
		this.contractId = contractId;
	}

	/**
	 * @return the revision
	 */
	public long getRevision() {
		return revision;
	}

	/**
	 * @param revision
	 *            the revision to set
	 */
	public void setRevision(long revision) {
		this.revision = revision;
	}

	/**
	 * @return the monitoredQoD
	 */
	public List<MonitoredQoD> getMonitoredQoD() {
		return monitoredQoD;
	}

	/**
	 * @param monitoredQoD
	 *            the monitoredQoD to set
	 */
	public void setMonitoredQoD(List<MonitoredQoD> monitoredQoD) {
		this.monitoredQoD = monitoredQoD;
	}

	/**
	 * @return the monitoredQoS
	 */
	public List<MonitoredQoS> getMonitoredQoS() {
		return monitoredQoS;
	}

	/**
	 * @param monitoredQoS
	 *            the monitoredQoS to set
	 */
	public void setMonitoredQoS(List<MonitoredQoS> monitoredQoS) {
		this.monitoredQoS = monitoredQoS;
	}

	/**
	 * @return the monitoringStart
	 */
	public Date getMonitoringStart() {
		return monitoringStart;
	}

	/**
	 * @param monitoringStart
	 *            the monitoringStart to set
	 */
	public void setMonitoringStart(Date monitoringStart) {
		this.monitoringStart = monitoringStart;
	}

	/**
	 * @return the monitoringEnd
	 */
	public Date getMonitoringEnd() {
		return monitoringEnd;
	}

	/**
	 * @param monitoringEnd
	 *            the monitoringEnd to set
	 */
	public void setMonitoringEnd(Date monitoringEnd) {
		this.monitoringEnd = monitoringEnd;
	}

}
