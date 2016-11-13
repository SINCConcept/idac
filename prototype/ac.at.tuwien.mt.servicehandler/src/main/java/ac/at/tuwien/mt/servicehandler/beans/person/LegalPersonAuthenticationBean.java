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

import org.apache.camel.Body;
import org.apache.camel.Exchange;
import org.apache.camel.Handler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.mongodb.MongoClient;

import ac.at.tuwien.mt.dao.person.PersonDAO;
import ac.at.tuwien.mt.dao.person.impl.PersonDAOImpl;
import ac.at.tuwien.mt.model.exception.AuthenticationException;
import ac.at.tuwien.mt.model.exception.ObjectNotFoundException;
import ac.at.tuwien.mt.model.helper.DefaultPasswordEncryptionProvider;
import ac.at.tuwien.mt.model.person.LegalPerson;
import ac.at.tuwien.mt.model.person.Person;
import ac.at.tuwien.mt.model.person.PersonType;

@Component
public class LegalPersonAuthenticationBean {

	private static final Logger LOGGER = LogManager.getLogger(LegalPersonAuthenticationBean.class);

	private MongoClient mongoClient;

	@Value("${mongo_db_name}")
	private String database;
	@Value("${mongo_db_collection_person}")
	private String personCollection;

	@Autowired
	public LegalPersonAuthenticationBean(MongoClient mongoClient) {
		this.mongoClient = mongoClient;
	}

	@Handler
	public void process(Exchange exchange, @Body LegalPerson person) throws Exception {
		LOGGER.debug("Authenticating legal person.");

		// register the user
		PersonDAO userDAO = new PersonDAOImpl(mongoClient, database, personCollection);
		List<Person> users = userDAO.findByEmail(person);
		if (users.isEmpty()) {
			LOGGER.error("Could not find user with email: {}.", person.getEmail());
			throw new ObjectNotFoundException("User not found.");
		}

		Person foundPerson = users.get(0);
		if (foundPerson.getPersonType().equals(PersonType.NATURAL)) {
			throw new ObjectNotFoundException("User not found.");
		}
		boolean passwordCorrect = DefaultPasswordEncryptionProvider.isPasswordCorrect(foundPerson.getPassword(), person.getPassword());
		if (!passwordCorrect) {
			throw new AuthenticationException("Invalid email or password.");
		} else {
			// return the registered user
			exchange.getOut().setBody(foundPerson);
		}
	}
}
