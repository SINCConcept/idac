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

import org.apache.camel.Exchange;
import org.apache.camel.Handler;
import org.apache.camel.Header;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.mongodb.MongoClient;

import ac.at.tuwien.mt.dao.datacontract.DataContractTrailDAO;
import ac.at.tuwien.mt.dao.datacontract.impl.DataContractTrailDAOImpl;
import ac.at.tuwien.mt.model.datacontract.DataContractTrail;

@Component
public class DataContractTrailBean {

	private static final Logger LOGGER = LogManager.getLogger(DataContractTrailBean.class);

	private MongoClient mongoClient;

	@Value("${mongo_db_name}")
	private String database;
	@Value("${mongo_db_collection_datacontract}")
	private String dataContractCollection;
	@Value("${mongo_db_collection_datacontract_trail}")
	private String dataContractTrailCollection;

	@Autowired
	public DataContractTrailBean(MongoClient mongoClient) {
		this.mongoClient = mongoClient;
	}

	@Handler
	public void process(@Header("contractid") String contractid, Exchange exchange) throws Exception {
		LOGGER.debug("Authenticating legal person.");

		// register the user
		DataContractTrailDAO dao = new DataContractTrailDAOImpl(mongoClient, database, dataContractCollection, dataContractTrailCollection);
		DataContractTrail found = dao.find(contractid);

		// return the registered user
		exchange.getOut().setBody(found);
	}
}
