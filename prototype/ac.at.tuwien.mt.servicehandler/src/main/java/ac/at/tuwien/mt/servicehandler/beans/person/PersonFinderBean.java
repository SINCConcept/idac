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

import ac.at.tuwien.mt.dao.person.PersonDAO;
import ac.at.tuwien.mt.dao.person.impl.PersonDAOImpl;
import ac.at.tuwien.mt.model.person.Person;

@Component
public class PersonFinderBean {

	private static final Logger LOGGER = LogManager.getLogger(PersonFinderBean.class);

	private MongoClient mongoClient;

	@Value("${mongo_db_name}")
	private String database;
	@Value("${mongo_db_collection_person}")
	private String personCollection;

	@Autowired
	public PersonFinderBean(MongoClient mongoClient) {
		this.mongoClient = mongoClient;
	}

	@Handler
	public void process(@Header("personid") String personid, Exchange exchange) throws Exception {
		LOGGER.debug("Searching for persons.");
		PersonDAO userDAO = new PersonDAOImpl(mongoClient, database, personCollection);
		List<Person> list = userDAO.getAll(personid);
		exchange.getOut().setBody(list);
		LOGGER.debug("Search complete.");
	}
}
