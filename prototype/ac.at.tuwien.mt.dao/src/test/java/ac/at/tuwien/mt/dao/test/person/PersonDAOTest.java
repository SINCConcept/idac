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
package ac.at.tuwien.mt.dao.test.person;

import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import ac.at.tuwien.mt.common.test.dao.DAOTest;
import ac.at.tuwien.mt.common.test.dao.MongoDBPropertiesProvider;
import ac.at.tuwien.mt.common.test.dao.MongoDBProperty;
import ac.at.tuwien.mt.dao.person.PersonDAO;
import ac.at.tuwien.mt.dao.person.impl.PersonDAOImpl;
import ac.at.tuwien.mt.model.exception.InvalidObjectException;
import ac.at.tuwien.mt.model.exception.ResourceOutOfDateException;
import ac.at.tuwien.mt.model.person.LegalPerson;
import ac.at.tuwien.mt.model.person.NaturalPerson;
import ac.at.tuwien.mt.model.person.Person;
import ac.at.tuwien.mt.model.person.PersonType;

/**
 * @author Florin Bogdan Balint
 *
 */
public class PersonDAOTest extends DAOTest {

	private static final String PERSON_COLLECTION = MongoDBPropertiesProvider.getString(MongoDBProperty.MONGO_DB_COLLECTION_PERSON);

	private PersonDAO personDAO;

	@Before
	public void before() {
		mongoClient.getDatabase(DB_NAME).getCollection(PERSON_COLLECTION).drop();
		personDAO = new PersonDAOImpl(mongoClient, DB_NAME, PERSON_COLLECTION);
	}

	@After
	public void after() {
		mongoClient.getDatabase(DB_NAME).getCollection(PERSON_COLLECTION).drop();
	}

	@Test
	public void testInsertPositive() {
		Person natural = getTestNaturalPerson();
		Person legal = getTestLegalPerson();

		try {
			personDAO.insert(natural);
			personDAO.insert(legal);
		} catch (ResourceOutOfDateException e) {
			// not expected
			Assert.fail();
		}
	}

	@Test
	public void testInsertNegative() {
		Person natural = getTestNaturalPerson();

		try {
			personDAO.insert(natural);
			personDAO.insert(natural);
			Assert.fail();
		} catch (ResourceOutOfDateException e) {
			// expected
		}
	}

	@Test
	public void testUpdate() {
		Person natural = getTestNaturalPerson();

		try {
			Person inserted = personDAO.insert(natural);

			inserted.setEmail("john.doe@test2.com");
			Person updated = personDAO.update(inserted);
			Assert.assertEquals("john.doe@test2.com", updated.getEmail());
		} catch (ResourceOutOfDateException | InvalidObjectException e) {
			// not expected
			Assert.fail();
		}
	}

	@Test
	public void testFindByMail() {
		Person natural = getTestNaturalPerson();
		Person legal = getTestLegalPerson();
		try {
			personDAO.insert(natural);
			personDAO.insert(legal);
		} catch (ResourceOutOfDateException e) {
			// not expected
			Assert.fail();
		}

		List<Person> list = personDAO.findByEmail(natural);
		Assert.assertEquals(1, list.size());
	}

	private NaturalPerson getTestNaturalPerson() {
		NaturalPerson person = new NaturalPerson();
		person.setPersonType(PersonType.NATURAL);
		person.setFirstName("John");
		person.setLastName("Doe");
		person.setEmail("john.doe@test.com");
		person.setPassword("secret");
		return person;
	}

	private LegalPerson getTestLegalPerson() {
		LegalPerson person = new LegalPerson();
		person.setPersonType(PersonType.LEGAL);
		person.setCompanyName("mycompany");
		person.setEmail("company@company.com");
		person.setRegistrationNumber("2134565U");
		return person;
	}
}
