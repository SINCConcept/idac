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
package ac.at.tuwien.mt.servicehandler.beans.person;

import org.apache.camel.Body;
import org.apache.camel.Exchange;
import org.apache.camel.Handler;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.mongodb.MongoClient;

import ac.at.tuwien.mt.dao.person.PersonDAO;
import ac.at.tuwien.mt.dao.person.impl.PersonDAOImpl;
import ac.at.tuwien.mt.model.person.LegalPerson;

@Component
public class LegalPersonUpdateBean {

	private static final Logger LOGGER = Logger.getLogger(LegalPersonUpdateBean.class);

	private MongoClient mongoClient;

	@Value("${mongo_db_name}")
	private String database;
	@Value("${mongo_db_collection_person}")
	private String personCollection;

	@Autowired
	public LegalPersonUpdateBean(MongoClient mongoClient) {
		this.mongoClient = mongoClient;
	}

	@Handler
	public void process(Exchange exchange, @Body LegalPerson person) throws Exception {
		LOGGER.debug("Updating legal person.");
		// register the user
		PersonDAO userDAO = new PersonDAOImpl(mongoClient, database, personCollection);
		userDAO.update(person);

		// return the registered user
		exchange.getOut().setBody(person);
	}
}
