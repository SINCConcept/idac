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
package ac.at.tuwien.mt.dao.monitor;

import java.util.List;

import ac.at.tuwien.mt.model.exception.InvalidObjectException;
import ac.at.tuwien.mt.model.exception.ResourceOutOfDateException;
import ac.at.tuwien.mt.model.thing.monitor.MonitoredDataContract;

/**
 * @author Florin Bogdan Balint
 *
 */
public interface MonitoredDataContractDAO {

	/**
	 * Finds all MonitoredDataContracts based on the provided contractId.
	 * 
	 * @param contractId
	 * @return
	 */
	public List<MonitoredDataContract> find(String contractId);

	/**
	 * Finds the open MonitoredDataContract based on the provided contractId.
	 * 
	 * @param contractId
	 * @return
	 */
	public MonitoredDataContract findOpen(String contractId);

	/**
	 * Inserts a new object.
	 * 
	 * @param monitoredDataContract
	 */
	public void insert(MonitoredDataContract monitoredDataContract);

	/**
	 * Finds a MonitoredDataContract based on the provided contractId and
	 * revision and updates it. The revision number is increased automatically.
	 * 
	 * @param monitoredDataContract
	 * @return
	 * @throws ResourceOutOfDateException
	 * @throws InvalidObjectException
	 */
	public MonitoredDataContract update(MonitoredDataContract monitoredDataContract) throws ResourceOutOfDateException, InvalidObjectException;

}
