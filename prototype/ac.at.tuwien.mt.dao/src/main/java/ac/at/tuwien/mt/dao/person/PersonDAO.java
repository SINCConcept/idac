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
package ac.at.tuwien.mt.dao.person;

import java.util.List;

import ac.at.tuwien.mt.model.exception.InvalidObjectException;
import ac.at.tuwien.mt.model.exception.ResourceOutOfDateException;
import ac.at.tuwien.mt.model.person.Person;

/**
 * @author Florin Bogdan Balint
 *
 */
public interface PersonDAO {

	/**
	 * 
	 * @param person
	 * @return registered person object
	 * @throws ResourceOutOfDateException
	 */
	public Person insert(Person person) throws ResourceOutOfDateException;

	/**
	 * 
	 * @param person
	 * @return registered person object
	 * @throws ResourceOutOfDateException
	 */
	public Person update(Person person) throws ResourceOutOfDateException, InvalidObjectException;

	/**
	 * Searches for a person and returns a person list
	 * 
	 * @param person
	 * @return
	 */
	public List<Person> findByEmail(Person person);

	/**
	 * Searches for a person based on the userId.
	 * 
	 * @param person
	 * @return
	 */
	public Person findById(String userId);

	/**
	 * Searches for a person and deletes it.
	 * 
	 * @param person
	 * @return
	 * @throws ResourceOutOfDateException
	 */
	public boolean delete(Person person) throws ResourceOutOfDateException;

	/**
	 * Searches for all persons, except the one provided.
	 * 
	 * @param personId
	 * @return
	 */
	public List<Person> getAll(String personId);

}
