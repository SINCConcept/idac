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
package ac.at.tuwien.mt.dao.thing;

import java.util.List;

import ac.at.tuwien.mt.model.exception.InvalidObjectException;
import ac.at.tuwien.mt.model.exception.ResourceOutOfDateException;
import ac.at.tuwien.mt.model.thing.Thing;

/**
 * @author Florin Bogdan Balint
 *
 */
public interface ThingDAO {

	/**
	 * Inserts the respective thing into the database. If the Thing does not
	 * have an thingId, the thingId will be provided automatically.
	 * 
	 * @param thing
	 * @return
	 */
	public Thing insert(Thing thing);

	/**
	 * Updates the respective thing from the database. The revision number is
	 * increased automatically.
	 * 
	 * @param thing
	 * @return
	 * @throws ResourceOutOfDateException
	 * @throws InvalidObjectException
	 */
	public Thing update(Thing thing) throws ResourceOutOfDateException, InvalidObjectException;

	/**
	 * Deletes the respective thing from the database.
	 * 
	 * @param thing
	 * @return
	 */
	public Thing delete(Thing thing) throws ResourceOutOfDateException, InvalidObjectException;

	/**
	 * Find all the things, where the owner has the specified ownerId/personId.
	 * 
	 * @param person
	 * @return
	 */
	public List<Thing> findThingsForOwner(String ownerId);

	/**
	 * Find all the things, where the owner does not have the specified
	 * ownerId/personId.
	 * 
	 * @param person
	 * @return
	 */
	public List<Thing> findThingsExceptOwner(String ownerId);

	/**
	 * Returns the thing which has the provided thingId.
	 * 
	 * @param thingId
	 * @return
	 */
	public Thing getThing(String thingId);

}
