package ac.at.tuwien.mt.datacontract.bean;
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

import java.util.Calendar;

import org.apache.camel.Body;
import org.apache.camel.Exchange;
import org.apache.camel.Handler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.mongodb.MongoClient;

import ac.at.tuwien.mt.dao.datacontract.DataContractDAO;
import ac.at.tuwien.mt.dao.datacontract.DataContractTrailDAO;
import ac.at.tuwien.mt.dao.datacontract.impl.DataContractDAOImpl;
import ac.at.tuwien.mt.dao.datacontract.impl.DataContractTrailDAOImpl;
import ac.at.tuwien.mt.dao.monitor.MonitoredDataContractDAO;
import ac.at.tuwien.mt.dao.monitor.impl.MonitoredDataContractDAOImpl;
import ac.at.tuwien.mt.model.datacontract.ClausesTrailEntry;
import ac.at.tuwien.mt.model.datacontract.DataContract;
import ac.at.tuwien.mt.model.datacontract.DataContractTrail;
import ac.at.tuwien.mt.model.exception.InvalidObjectException;
import ac.at.tuwien.mt.model.exception.ResourceOutOfDateException;
import ac.at.tuwien.mt.model.thing.monitor.MonitoredDataContract;

@Component
public class DataContractUpdateBean {

	private static final Logger LOGGER = LogManager.getLogger(DataContractUpdateBean.class);

	private MongoClient mongoClient;

	@Value("${mongo_db_name}")
	private String database;
	@Value("${mongo_db_collection_datacontract}")
	private String dataContractCollection;
	@Value("${mongo_db_collection_datacontract_trail}")
	private String dataContractTrailCollection;
	@Value("${mongo_db_collection_dc_monitor}")
	private String dataContractMonitorCollection;

	@Autowired
	public DataContractUpdateBean(MongoClient mongoClient) {
		this.mongoClient = mongoClient;
	}

	@Handler
	public void process(Exchange exchange, @Body DataContract dataContract) throws Exception {
		LOGGER.debug("Authenticating legal person.");

		// update the contract
		DataContractDAO dataContractDAO = new DataContractDAOImpl(mongoClient, database, dataContractCollection);
		DataContract updated = dataContractDAO.update(dataContract);

		ClausesTrailEntry trailEntry = new ClausesTrailEntry();
		trailEntry.setControlAndRelationship(updated.getControlAndRelationship());
		trailEntry.setDataRights(updated.getDataRights());
		trailEntry.setPricingModel(updated.getPricingModel());
		trailEntry.setPurchasingPolicy(updated.getPurchasingPolicy());

		// update the trail
		DataContractTrailDAO dao = new DataContractTrailDAOImpl(mongoClient, database, dataContractCollection, dataContractTrailCollection);
		DataContractTrail trail = dao.find(updated.getDataContractMetaInfo().getContractId());
		trail.getClausesTrail().add(trailEntry);
		dao.update(trail);

		// in case the monitoring has just been disabled for a data contract -
		// set the end date in the monitoring dao
		if (dataContract.getMonitoring() == Boolean.FALSE) {
			endMonitoring(dataContract);
		}

		// return
		exchange.getOut().setBody(updated);
	}

	private void endMonitoring(DataContract dataContract) throws ResourceOutOfDateException, InvalidObjectException {
		MonitoredDataContractDAO mdc = new MonitoredDataContractDAOImpl(mongoClient, database, dataContractMonitorCollection);
		MonitoredDataContract toClose = mdc.findOpen(dataContract.getDataContractMetaInfo().getContractId());
		if (toClose != null) {
			toClose.setMonitoringEnd(Calendar.getInstance().getTime());
			mdc.update(toClose);
		}
	}
}
