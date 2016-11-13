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
package ac.at.tuwien.mt.dao.datacontract;

import ac.at.tuwien.mt.model.datacontract.DataContractTrail;
import ac.at.tuwien.mt.model.exception.InvalidObjectException;
import ac.at.tuwien.mt.model.exception.ResourceOutOfDateException;

/**
 * @author Florin Bogdan Balint
 *
 */
public interface DataContractTrailDAO {

	/**
	 * Saves the new dataContractTrail in the database. Also sets the necessary
	 * fields (e.g., revision, creation date, etc.)
	 * 
	 * @param dataContractTrail
	 * @return DataContractTrail
	 * @throws InvalidObjectException
	 */
	public DataContractTrail insert(DataContractTrail dataContractTrail) throws InvalidObjectException;

	/**
	 * Returns the dataContractTrail with the corresponding id.
	 * 
	 * @param id
	 * @return DataContract or NULL
	 */
	public DataContractTrail find(String id);

	/**
	 * Updates a dataContractTrail based on its id.
	 * 
	 * @param dataContractTrail
	 * @return
	 * @throws ResourceOutOfDateException
	 * @throws InvalidObjectException
	 */
	public DataContractTrail update(DataContractTrail dataContractTrail) throws ResourceOutOfDateException, InvalidObjectException;

	/**
	 * Returns the average number of negotiations for a thing with the provided
	 * thingId. If no data contracts were concluded for the thing, NULL is
	 * returned.
	 * 
	 * @param thingId
	 * @return Integer or NULL
	 * @throws InvalidObjectException
	 */
	public Integer getAvgNrOfNegotiationsForThing(String thingId) throws InvalidObjectException;
}
