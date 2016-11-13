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

package ac.at.tuwien.mt.monitoring.internal;

/**
 * ENUM representing all properties which are used by the system.
 * 
 * @author Florin Bogdan Balint
 *
 */
public enum MongoDBProperty {

	/**
	 * The number of concurrent consumer that will be started for each queue.
	 */
	MONGO_DB_HOST("MONGO_DB_HOST"), //
	MONGO_DB_PORT("MONGO_DB_PORT"), //
	MONGO_DB_NAME("MONGO_DB_NAME"), //
	MONGO_DB_USER_NAME("MONGO_DB_NAME"), //
	MONGO_DB_USER_PASSWORD("MONGO_DB_NAME"), //
	MONGO_DB_COLLECTION_PERSON("MONGO_DB_NAME"), //
	MONGO_DB_COLLECTION_DATACONTRACT("MONGO_DB_NAME"), //
	MONGO_DB_COLLECTION_THING("MONGO_DB_NAME"), //
	MONGO_DB_COLLECTION_THING_MONITOR_QOD("MONGO_DB_NAME"), //
	;

	private String property;

	private MongoDBProperty(String property) {
		this.property = property;
	}

	/**
	 * @return the property
	 */
	public String getProperty() {
		return property;
	}

	/**
	 * @param property
	 *            the property to set
	 */
	public void setProperty(String property) {
		this.property = property;
	}
}
