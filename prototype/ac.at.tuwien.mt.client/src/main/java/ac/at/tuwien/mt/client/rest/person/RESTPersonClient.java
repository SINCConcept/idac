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
package ac.at.tuwien.mt.client.rest.person;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.bson.Document;

import ac.at.tuwien.mt.client.internal.WebServiceProperty;
import ac.at.tuwien.mt.client.internal.WebServicesProvider;
import ac.at.tuwien.mt.model.exception.AuthenticationException;
import ac.at.tuwien.mt.model.exception.ObjectNotFoundException;
import ac.at.tuwien.mt.model.exception.ResourceOutOfDateException;
import ac.at.tuwien.mt.model.helper.DefaultJSONProvider;
import ac.at.tuwien.mt.model.person.LegalPerson;
import ac.at.tuwien.mt.model.person.NaturalPerson;
import ac.at.tuwien.mt.model.person.Person;

/**
 * 
 * @author Florin Bogdan Balint
 *
 */
public final class RESTPersonClient {

	protected RESTPersonClient() {
		// defeat instantiation
	}

	/**
	 * Authenticates a legal person. The LegalPerson object has to contain the
	 * email and the password.
	 * 
	 * @param person
	 * @return LegalPerson
	 * @throws ObjectNotFoundException
	 * @throws AuthenticationException
	 */
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

	/**
	 * Authenticates a natural person. The NaturalPerson object has to contain
	 * the email and the password.
	 * 
	 * @param person
	 * @return NaturalPerson
	 * @throws ObjectNotFoundException
	 * @throws AuthenticationException
	 */
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

	/**
	 * Authenticates any person for the provided email and password.
	 * 
	 * @param email
	 * @param password
	 * @return NaturalPerson or LegalPerson
	 * @throws AuthenticationException
	 * @throws ObjectNotFoundException
	 */
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

	/**
	 * Registers a legal person.
	 * 
	 * @param person
	 * @return the registered LegalPerson
	 * @throws ResourceOutOfDateException
	 */
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

	/**
	 * Registers a natural person.
	 * 
	 * @param person
	 * @return the registered NaturalPerson
	 * @throws ResourceOutOfDateException
	 */
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

	/**
	 * Updates a legal person.
	 * 
	 * @param person
	 * @return the updated legal person.
	 * @throws ObjectNotFoundException
	 * @throws AuthenticationException
	 */
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

	/**
	 * Updates a NaturalPerson.
	 * 
	 * @param person
	 * @return the updated NaturalPerson
	 * @throws ObjectNotFoundException
	 * @throws AuthenticationException
	 */
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

}
