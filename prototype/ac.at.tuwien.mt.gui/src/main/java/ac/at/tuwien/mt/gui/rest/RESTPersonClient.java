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
package ac.at.tuwien.mt.gui.rest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.bson.Document;

import ac.at.tuwien.mt.gui.internal.WebServiceProperty;
import ac.at.tuwien.mt.gui.internal.WebServicesProvider;
import ac.at.tuwien.mt.gui.primefaces.exception.CommunicationException;
import ac.at.tuwien.mt.model.exception.AuthenticationException;
import ac.at.tuwien.mt.model.exception.ObjectNotFoundException;
import ac.at.tuwien.mt.model.exception.ResourceOutOfDateException;
import ac.at.tuwien.mt.model.helper.DefaultJSONProvider;
import ac.at.tuwien.mt.model.person.LegalPerson;
import ac.at.tuwien.mt.model.person.NaturalPerson;
import ac.at.tuwien.mt.model.person.Person;
import ac.at.tuwien.mt.model.person.PersonType;

/**
 * @author Florin Bogdan Balint
 *
 */
public final class RESTPersonClient {

	protected RESTPersonClient() {
		// defeat instantiation
	}

	public static List<Person> findExcept(String personId) throws CommunicationException {
		Client client = ClientBuilder.newClient();
		WebTarget target = client.target(WebServicesProvider.getString(WebServiceProperty.REST_MIDDLEWARE_BASEURL))
				.path(WebServicesProvider.getString(WebServiceProperty.REST_PERSON_FIND_PATH) + "/" + personId);

		Response response = target.request(MediaType.APPLICATION_JSON_TYPE).get();

		int status = response.getStatus();
		if (status != 200) {
			throw new CommunicationException("Could not connect to the server.");
		}
		String responseAsString = (String) response.readEntity(String.class);
		response.close();

		// Document parse = Document.parse(responseAsString);
		List<Person> persons = new ArrayList<Person>();
		Document[] list = (Document[]) ac.at.tuwien.mt.model.helper.DefaultJSONProvider.getObjectFromJson(responseAsString, Document[].class);
		List<Document> documents = Arrays.asList(list);
		for (Document doc : documents) {
			PersonType personType = PersonType.valueOf(doc.getString("personType"));
			if (personType.equals(PersonType.NATURAL)) {
				NaturalPerson np = new NaturalPerson(doc);
				persons.add(np);
			} else {
				LegalPerson lp = new LegalPerson(doc);
				persons.add(lp);
			}
		}

		return persons;
	}

	public static NaturalPerson register(NaturalPerson person) throws ResourceOutOfDateException {
		Client client = ClientBuilder.newClient();
		WebTarget target = client.target(WebServicesProvider.getString(WebServiceProperty.REST_MIDDLEWARE_BASEURL))
				.path(WebServicesProvider.getString(WebServiceProperty.REST_PERSON_NATURAL_PATH) + "/register");
		Entity<String> entity = Entity.entity(DefaultJSONProvider.getObjectAsJson(person), MediaType.APPLICATION_JSON);

		Response response = target.request(MediaType.APPLICATION_JSON_TYPE).put(entity);

		int status = response.getStatus();
		if (status == 409) {
			throw new ResourceOutOfDateException("Person already registered.");
		}
		String personAsString = (String) response.readEntity(String.class);
		response.close();

		NaturalPerson personResponse = new NaturalPerson(Document.parse(personAsString));
		return personResponse;
	}

	public static LegalPerson register(LegalPerson person) throws ResourceOutOfDateException {
		Client client = ClientBuilder.newClient();
		WebTarget target = client.target(WebServicesProvider.getString(WebServiceProperty.REST_MIDDLEWARE_BASEURL))
				.path(WebServicesProvider.getString(WebServiceProperty.REST_PERSON_LEGAL_PATH) + "/register");
		Entity<String> entity = Entity.entity(DefaultJSONProvider.getObjectAsJson(person), MediaType.APPLICATION_JSON);

		Response response = target.request(MediaType.APPLICATION_JSON_TYPE).put(entity);

		int status = response.getStatus();
		if (status == 409) {
			throw new ResourceOutOfDateException("Person already registered.");
		}
		String personAsString = (String) response.readEntity(String.class);
		response.close();

		LegalPerson personResponse = new LegalPerson(Document.parse(personAsString));
		return personResponse;
	}

	public static Person authenticate(String email, String password) throws AuthenticationException, ObjectNotFoundException {
		try {
			NaturalPerson naturalPerson = new NaturalPerson();
			naturalPerson.setEmail(email);
			naturalPerson.setPassword(password);
			NaturalPerson natPers = authenticate(naturalPerson);
			return natPers;
		} catch (ObjectNotFoundException e) {
			LegalPerson legalPerson = new LegalPerson();
			legalPerson.setEmail(email);
			legalPerson.setPassword(password);
			LegalPerson legalPers = authenticate(legalPerson);
			return legalPers;
		}
	}

	public static NaturalPerson authenticate(NaturalPerson person) throws ObjectNotFoundException, AuthenticationException {
		Client client = ClientBuilder.newClient();
		WebTarget target = client.target(WebServicesProvider.getString(WebServiceProperty.REST_MIDDLEWARE_BASEURL))
				.path(WebServicesProvider.getString(WebServiceProperty.REST_PERSON_NATURAL_PATH) + "/authenticate");
		Entity<String> entity = Entity.entity(DefaultJSONProvider.getObjectAsJson(person), MediaType.APPLICATION_JSON);

		Response response = target.request(MediaType.APPLICATION_JSON_TYPE).post(entity);

		int status = response.getStatus();

		if (status == 404) {
			throw new ObjectNotFoundException("Person not found.");
		}
		if (status == 401) {
			throw new AuthenticationException("Unauthorized");
		}

		String personAsString = (String) response.readEntity(String.class);
		response.close();

		NaturalPerson personResponse = new NaturalPerson(Document.parse(personAsString));
		return personResponse;
	}

	public static NaturalPerson update(NaturalPerson person) throws ObjectNotFoundException, AuthenticationException {
		Client client = ClientBuilder.newClient();
		WebTarget target = client.target(WebServicesProvider.getString(WebServiceProperty.REST_MIDDLEWARE_BASEURL))
				.path(WebServicesProvider.getString(WebServiceProperty.REST_PERSON_NATURAL_PATH) + "/update");
		Entity<String> entity = Entity.entity(DefaultJSONProvider.getObjectAsJson(person), MediaType.APPLICATION_JSON);

		Response response = target.request(MediaType.APPLICATION_JSON_TYPE).post(entity);

		int status = response.getStatus();

		if (status == 404) {
			throw new ObjectNotFoundException("Person not found.");
		}
		if (status == 401) {
			throw new AuthenticationException("Unauthorized");
		}

		String personAsString = (String) response.readEntity(String.class);
		response.close();

		NaturalPerson personResponse = new NaturalPerson(Document.parse(personAsString));
		return personResponse;
	}

	public static LegalPerson authenticate(LegalPerson person) throws ObjectNotFoundException, AuthenticationException {
		Client client = ClientBuilder.newClient();
		WebTarget target = client.target(WebServicesProvider.getString(WebServiceProperty.REST_MIDDLEWARE_BASEURL))
				.path(WebServicesProvider.getString(WebServiceProperty.REST_PERSON_LEGAL_PATH) + "/authenticate");
		Entity<String> entity = Entity.entity(DefaultJSONProvider.getObjectAsJson(person), MediaType.APPLICATION_JSON);

		Response response = target.request(MediaType.APPLICATION_JSON_TYPE).post(entity);

		int status = response.getStatus();

		if (status == 404) {
			throw new ObjectNotFoundException("Person already registered.");
		}
		if (status == 401) {
			throw new AuthenticationException("Unauthorized");
		}

		String personAsString = (String) response.readEntity(String.class);
		response.close();

		LegalPerson personResponse = new LegalPerson(Document.parse(personAsString));
		return personResponse;
	}

	public static LegalPerson update(LegalPerson person) throws ObjectNotFoundException, AuthenticationException {
		Client client = ClientBuilder.newClient();
		WebTarget target = client.target(WebServicesProvider.getString(WebServiceProperty.REST_MIDDLEWARE_BASEURL))
				.path(WebServicesProvider.getString(WebServiceProperty.REST_PERSON_LEGAL_PATH) + "/update");
		Entity<String> entity = Entity.entity(DefaultJSONProvider.getObjectAsJson(person), MediaType.APPLICATION_JSON);

		Response response = target.request(MediaType.APPLICATION_JSON_TYPE).post(entity);

		int status = response.getStatus();

		if (status == 404) {
			throw new ObjectNotFoundException("Person already registered.");
		}
		if (status == 401) {
			throw new AuthenticationException("Unauthorized");
		}

		String personAsString = (String) response.readEntity(String.class);
		response.close();

		LegalPerson personResponse = new LegalPerson(Document.parse(personAsString));
		return personResponse;
	}

}
