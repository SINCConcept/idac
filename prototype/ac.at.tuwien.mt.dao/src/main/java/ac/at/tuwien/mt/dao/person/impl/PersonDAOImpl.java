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
package ac.at.tuwien.mt.dao.person.impl;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.FindOneAndReplaceOptions;
import com.mongodb.client.model.ReturnDocument;

import ac.at.tuwien.mt.dao.person.PersonDAO;
import ac.at.tuwien.mt.model.exception.InvalidObjectException;
import ac.at.tuwien.mt.model.exception.ResourceOutOfDateException;
import ac.at.tuwien.mt.model.helper.DefaultIDGenerator;
import ac.at.tuwien.mt.model.helper.DefaultPasswordEncryptionProvider;
import ac.at.tuwien.mt.model.person.LegalPerson;
import ac.at.tuwien.mt.model.person.NaturalPerson;
import ac.at.tuwien.mt.model.person.Person;
import ac.at.tuwien.mt.model.person.PersonType;
import ac.at.tuwien.mt.model.util.StringUtil;

/**
 * @author Florin Bogdan Balint
 *
 */
public class PersonDAOImpl implements PersonDAO {

	private static final Logger LOGGER = LogManager.getLogger(PersonDAOImpl.class);

	private MongoClient mongoClient;
	private String database;
	private String personCollection;

	public PersonDAOImpl(MongoClient mongoClient, String database, String personCollection) {
		this.mongoClient = mongoClient;
		this.database = database;
		this.personCollection = personCollection;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ac.at.tuwien.mt.dao.person.PersonDAO#insert(ac.at.tuwien.mt.model.person.
	 * Person)
	 */
	@Override
	public Person insert(Person person) throws ResourceOutOfDateException {
		// Step 1: check if the person is already registered
		List<Person> persons = findByEmail(person);
		if (!persons.isEmpty()) {
			LOGGER.error("Cannot register person. A person with the provided email: {} already exists.", person.getEmail());
			throw new ResourceOutOfDateException("email already registered:" + person.getEmail());
		}

		// Step 2: register the person
		if (StringUtil.isNullOrBlank(person.getPersonId())) {
			person.setPersonId(DefaultIDGenerator.generateID());
		}
		String encryptedPassword;
		try {
			encryptedPassword = DefaultPasswordEncryptionProvider.encryptPassword(person.getPassword());
			person.setPassword(encryptedPassword);
		} catch (NoSuchAlgorithmException | UnsupportedEncodingException | NoSuchProviderException e) {
			LOGGER.error(e);
		}
		person.setRevision(1);

		MongoCollection<Document> collection = mongoClient.getDatabase(database).getCollection(personCollection);
		if (person.getPersonType() != null && person.getPersonType().equals(PersonType.NATURAL)) {
			NaturalPerson natPerson = (NaturalPerson) person;
			collection.insertOne(natPerson.getDocument());
		} else {
			LegalPerson legalPerson = (LegalPerson) person;
			collection.insertOne(legalPerson.getDocument());
		}

		return person;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ac.at.tuwien.mt.dao.person.PersonDAO#findByEmail(ac.at.tuwien.mt.model.
	 * person.Person)
	 */
	@Override
	public List<Person> findByEmail(Person person) {
		List<Person> persons = new ArrayList<Person>();
		MongoCollection<Document> collection = mongoClient.getDatabase(database).getCollection(personCollection);

		Document personToSearchFor = new Document();
		personToSearchFor.put("email", person.getEmail());
		FindIterable<Document> search = collection.find(personToSearchFor);

		if (search == null) {
			return persons;
		}

		for (Document document : search) {
			String personTypeAsString = document.getString("personType");
			if (StringUtil.isNullOrBlank(personTypeAsString)) {
				continue;
			}
			if (PersonType.valueOf(personTypeAsString) == PersonType.NATURAL) {
				NaturalPerson nperson = new NaturalPerson(document);
				persons.add(nperson);
			} else {
				LegalPerson lperson = new LegalPerson(document);
				persons.add(lperson);
			}
		}

		return persons;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ac.at.tuwien.mt.dao.person.PersonDAO#delete(ac.at.tuwien.mt.model.person.
	 * Person)
	 */
	@Override
	public boolean delete(Person person) throws ResourceOutOfDateException {

		MongoCollection<Document> collection = mongoClient.getDatabase(database).getCollection(personCollection);

		Person foundPerson = findById(person.getPersonId());
		if (foundPerson == null) {
			LOGGER.error("Cannot delete person if not existent. Could not find person with email: {}.", person.getEmail());
			throw new ResourceOutOfDateException("Person not found.");
		}

		Document personToSearchFor = new Document();
		personToSearchFor.put("personId", person.getPersonId());
		collection.findOneAndDelete(personToSearchFor);

		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ac.at.tuwien.mt.dao.person.PersonDAO#findById(java.lang.String)
	 */
	@Override
	public Person findById(String personId) {
		List<Person> persons = new ArrayList<Person>();
		MongoCollection<Document> collection = mongoClient.getDatabase(database).getCollection(personCollection);

		Document personToSearchFor = new Document();
		personToSearchFor.put("personId", personId);
		FindIterable<Document> search = collection.find(personToSearchFor);

		if (search == null) {
			return null;
		}

		for (Document document : search) {
			if (document.containsKey("firstName")) {
				NaturalPerson person = new NaturalPerson(document);
				persons.add(person);
			} else {
				LegalPerson person = new LegalPerson(document);
				persons.add(person);
			}
		}

		if (persons.isEmpty()) {
			return null;
		}

		return persons.get(0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ac.at.tuwien.mt.dao.person.PersonDAO#update(ac.at.tuwien.mt.model.person.
	 * Person)
	 */
	@Override
	public Person update(Person person) throws ResourceOutOfDateException, InvalidObjectException {
		if (person.getPersonId() == null || person.getRevision() == null) {
			throw new InvalidObjectException("Invalid JSON data!");
		}

		Document toSearchFor = new Document();
		toSearchFor.put("personId", person.getPersonId());
		toSearchFor.put("revision", person.getRevision());

		person.setRevision((person.getRevision() + 1));

		FindOneAndReplaceOptions options = new FindOneAndReplaceOptions();
		options.returnDocument(ReturnDocument.AFTER);

		MongoCollection<Document> collection = mongoClient.getDatabase(database).getCollection(personCollection);
		Document updated = collection.findOneAndReplace(toSearchFor, person.getDocument(), options);
		if (updated == null) {
			// reset the revision, because this is a pointer
			throw new ResourceOutOfDateException("Resource not found or out of date!");
		}

		String personTypeAsString = updated.getString("personType");
		if (personTypeAsString.equals(PersonType.NATURAL.getProperty())) {
			NaturalPerson updatedPerson = new NaturalPerson(updated);
			return updatedPerson;
		}

		LegalPerson updatedPerson = new LegalPerson(updated);
		return updatedPerson;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ac.at.tuwien.mt.dao.person.PersonDAO#getAll(java.lang.String)
	 */
	@Override
	public List<Person> getAll(String personId) {
		List<Person> list = new ArrayList<Person>();
		MongoCollection<Document> collection = mongoClient.getDatabase(database).getCollection(personCollection);

		FindIterable<Document> docList = null;
		if (personId != null && !personId.isEmpty()) {
			docList = collection.find(Filters.ne("personId", personId));
		} else {
			docList = collection.find();
		}
		for (Document document : docList) {
			Person toAdd = null;
			String personType = document.getString("personType");
			if (personType == null) {
				continue;
			}
			if (PersonType.valueOf(personType) == PersonType.NATURAL) {
				toAdd = new NaturalPerson(document);
			} else {
				toAdd = new LegalPerson(document);
			}
			list.add(toAdd);
		}
		return list;
	}

}
