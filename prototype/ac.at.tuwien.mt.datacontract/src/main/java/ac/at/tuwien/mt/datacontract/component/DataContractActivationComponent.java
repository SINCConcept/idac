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

package ac.at.tuwien.mt.datacontract.component;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.mongodb.MongoClient;

import ac.at.tuwien.mt.dao.datacontract.DataContractDAO;
import ac.at.tuwien.mt.dao.datacontract.impl.DataContractDAOImpl;

/**
 * 
 * This component searches for data contracts which have been concluded and
 * updates the active flag automatically.
 * 
 * @author Florin Bogdan Balint
 *
 */
@Component
public class DataContractActivationComponent {

	private static final Logger LOGGER = Logger.getLogger(DataContractActivationComponent.class);

	private MongoClient mongoClient;

	@Value("${mongo_db_name}")
	private String database;
	@Value("${mongo_db_collection_datacontract}")
	private String dataContractCollection;

	private DataContractDAO dataContractDAO = null;

	@Autowired
	public DataContractActivationComponent(MongoClient mongoClient) {
		this.mongoClient = mongoClient;
	}

	@Scheduled(cron = "*/60 * * * * ?")
	public void activateDataContracts() {
		LOGGER.debug("Activating data contracts.");
		getDataContractDAO().activateDataContracts();
		LOGGER.debug("Activating data contracts completed.");
	}

	@Scheduled(cron = "*/60 * * * * ?")
	public void deactivateDataContracts() {
		LOGGER.debug("Deactivating data contracts.");
		getDataContractDAO().deactivateDataContracts();
		LOGGER.debug("Deactivating data contracts completed.");
	}

	private DataContractDAO getDataContractDAO() {
		if (dataContractDAO == null) {
			dataContractDAO = new DataContractDAOImpl(mongoClient, database, dataContractCollection);
		}
		return dataContractDAO;
	}
}
