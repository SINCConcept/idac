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

import java.util.List;

import org.apache.camel.Exchange;
import org.apache.camel.Handler;
import org.apache.camel.Header;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.mongodb.MongoClient;

import ac.at.tuwien.mt.dao.datacontract.DataContractDAO;
import ac.at.tuwien.mt.dao.datacontract.impl.DataContractDAOImpl;
import ac.at.tuwien.mt.model.datacontract.DataContract;

@Component
public class ProviderOpenContractsBean {

	private static final Logger LOGGER = LogManager.getLogger(ProviderOpenContractsBean.class);

	private MongoClient mongoClient;

	@Value("${mongo_db_name}")
	private String database;
	@Value("${mongo_db_collection_datacontract}")
	private String dataContractCollection;

	@Autowired
	public ProviderOpenContractsBean(MongoClient mongoClient) {
		this.mongoClient = mongoClient;
	}

	@Handler
	public void process(@Header("personid") String personid, Exchange exchange) throws Exception {
		LOGGER.debug("Searching for contracts.");

		// register the user
		DataContractDAO dataContractDAO = new DataContractDAOImpl(mongoClient, database, dataContractCollection);
		List<DataContract> list = dataContractDAO.findProviderOpenContracts(personid);

		// return the registered user
		exchange.getOut().setBody(list);
	}
}
