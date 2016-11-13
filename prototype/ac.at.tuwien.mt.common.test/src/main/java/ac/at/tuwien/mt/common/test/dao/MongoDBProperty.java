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

package ac.at.tuwien.mt.common.test.dao;

/**
 * ENUM representing all properties which are used by the system.
 * 
 * @author Florin Bogdan Balint
 *
 */
public enum MongoDBProperty {

	MONGO_DB_HOST("mongo_db_host"), //
	MONGO_DB_PORT("mongo_db_port"), //

	MONGO_DB_MAX_CONNECTIONS_PER_HOST("mongo_db_max_connections_per_host"), //
	MONGO_DB_SSL_ENABLED("mongo_db_ssl_enabled"), //
	MONGO_DB_SSL_INVALIDHOSTNAMEALLOWED("mongo_db_ssl_invalidhostnameallowed"), //

	MONGO_DB_NAME("mongo_db_name"), //
	MONGO_DB_USER_NAME("mongo_db_user_name"), //
	MONGO_DB_USER_PASSWORD("mongo_db_user_password"), //

	MONGO_DB_COLLECTION_PERSON("mongo_db_collection_person"), //
	MONGO_DB_COLLECTION_DATACONTRACT("mongo_db_collection_datacontract"), //
	MONGO_DB_COLLECTION_DATACONTRACT_TRAIL("mongo_db_collection_datacontract_trail"), //
	MONGO_DB_COLLECTION_THING("mongo_db_collection_thing"), //
	MONGO_DB_COLLECTION_THING_MONITOR_QOD("mongo_db_collection_thing_monitor_qod"), //
	MONGO_DB_COLLECTION_THING_MONITOR_QOS("mongo_db_collection_thing_monitor_qos"), //
	MONGO_DB_COLLECTION_DC_MONITOR("mongo_db_collection_dc_monitor") //
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
