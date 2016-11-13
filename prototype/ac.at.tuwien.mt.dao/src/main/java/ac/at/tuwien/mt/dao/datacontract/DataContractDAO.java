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

import java.util.List;

import ac.at.tuwien.mt.model.datacontract.DataContract;
import ac.at.tuwien.mt.model.exception.InvalidObjectException;
import ac.at.tuwien.mt.model.exception.ResourceOutOfDateException;

/**
 * @author Florin Bogdan Balint
 *
 */
public interface DataContractDAO {

	/**
	 * Saves the new dataContract in the database. Also sets the necessary
	 * fields (e.g., revision, creation date, etc.)
	 * 
	 * @param dataContract
	 * @return DataContract or NULL
	 */
	public DataContract insert(DataContract dataContract);

	/**
	 * Returns the dataContract with the corresponding id.
	 * 
	 * @param contractId
	 * @return DataContract or NULL
	 */
	public DataContract readById(String contractId);

	/**
	 * Updates a dataContract based on its id.
	 * 
	 * @param dataContract
	 * @return
	 * @throws ResourceOutOfDateException
	 * @throws InvalidObjectException
	 */
	public DataContract update(DataContract dataContract) throws ResourceOutOfDateException, InvalidObjectException;

	/**
	 * Returns all concluded contracts where the respective person is a
	 * provider.
	 * 
	 * @param person
	 * @return List<DataContract>
	 */
	public List<DataContract> findProviderConcludedContracts(String personId);

	/**
	 * Returns all open contracts where the respective person is a provider.
	 * 
	 * @param person
	 * @return List<DataContract>
	 */
	public List<DataContract> findProviderOpenContracts(String personId);

	/**
	 * Returns all concluded contracts where the respective person is a buyer.
	 * 
	 * @param person
	 * @return List<DataContract>
	 */
	public List<DataContract> findBuyerConcludedContracts(String personId);

	/**
	 * Returns all open contracts where the respective person is a buyer.
	 * 
	 * @param person
	 * @return List<DataContract>
	 */
	public List<DataContract> findBuyerOpenContracts(String personId);

	/**
	 * Returns all concluded contracts which contain the provided thing.
	 * 
	 * @param thingId
	 * @return List<DataContract>
	 */
	public List<DataContract> findDataContracts(String thingId);

	/**
	 * Activates all data contracts which should be activated, i.e., both
	 * parties accepted the terms and the subscription period has started.
	 */
	public void activateDataContracts();

	/**
	 * Deactivates all data contracts which should be deactivated, i.e., both
	 * parties accepted the terms and the subscription period has ended.
	 */
	public void deactivateDataContracts();

}
