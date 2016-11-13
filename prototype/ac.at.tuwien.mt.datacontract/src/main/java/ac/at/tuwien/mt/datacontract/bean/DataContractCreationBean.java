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
package ac.at.tuwien.mt.datacontract.bean;

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
import ac.at.tuwien.mt.model.datacontract.ClausesTrailEntry;
import ac.at.tuwien.mt.model.datacontract.DataContract;
import ac.at.tuwien.mt.model.datacontract.DataContractTrail;

@Component
public class DataContractCreationBean {

	private static final Logger LOGGER = LogManager.getLogger(DataContractCreationBean.class);

	private MongoClient mongoClient;

	@Value("${mongo_db_name}")
	private String database;
	@Value("${mongo_db_collection_datacontract}")
	private String dataContractCollection;
	@Value("${mongo_db_collection_datacontract_trail}")
	private String dataContractTrailCollection;

	@Autowired
	public DataContractCreationBean(MongoClient mongoClient) {
		this.mongoClient = mongoClient;
	}

	@Handler
	public void process(Exchange exchange, @Body DataContract dataContract) throws Exception {
		LOGGER.debug("Authenticating legal person.");

		// register the contract
		DataContractDAO dataContractDAO = new DataContractDAOImpl(mongoClient, database, dataContractCollection);
		DataContract inserted = dataContractDAO.insert(dataContract);

		// create the trail
		DataContractTrail dcTrail = new DataContractTrail();
		dcTrail.setContractId(inserted.getDataContractMetaInfo().getContractId());
		ClausesTrailEntry trailEntry = new ClausesTrailEntry();
		trailEntry.setControlAndRelationship(inserted.getControlAndRelationship());
		trailEntry.setDataRights(inserted.getDataRights());
		trailEntry.setPricingModel(inserted.getPricingModel());
		trailEntry.setPurchasingPolicy(inserted.getPurchasingPolicy());
		dcTrail.getClausesTrail().add(trailEntry);

		// persist the trail
		DataContractTrailDAO dao = new DataContractTrailDAOImpl(mongoClient, database, dataContractCollection, dataContractTrailCollection);
		dao.insert(dcTrail);

		// return the registered user
		exchange.getOut().setBody(inserted);
	}
}
