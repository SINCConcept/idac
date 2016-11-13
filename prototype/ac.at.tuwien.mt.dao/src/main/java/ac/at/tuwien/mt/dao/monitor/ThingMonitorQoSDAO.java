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

import ac.at.tuwien.mt.model.exception.InvalidObjectException;
import ac.at.tuwien.mt.model.exception.ResourceOutOfDateException;
import ac.at.tuwien.mt.model.thing.monitor.MonitoredQoS;

/**
 * @author Florin Bogdan Balint
 *
 */
public interface ThingMonitorQoSDAO {

	/**
	 * Finds a MonitoredQoS based on the provided thingId.
	 * 
	 * @param monitoredQoS
	 * @return
	 */
	public MonitoredQoS find(String thingId);

	/**
	 * Insert a new object.
	 * 
	 * @param monitoredQoS
	 */
	public void insert(MonitoredQoS monitoredQoS);

	/**
	 * Finds a monitoredQoS based on the provided thingId and revision and
	 * updates it. The revision number is increased automatically.
	 * 
	 * @param monitoredQoS
	 * @return
	 * @throws ResourceOutOfDateException
	 * @throws InvalidObjectException
	 */
	public MonitoredQoS update(MonitoredQoS monitoredQoS) throws ResourceOutOfDateException, InvalidObjectException;

}
