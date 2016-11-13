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

import java.util.ArrayList;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;

/**
 * @author Florin Bogdan Balint
 *
 */
public class DAOTest {

	public static final String DB_NAME = MongoDBPropertiesProvider.getString(MongoDBProperty.MONGO_DB_NAME);

	public static MongoClient mongoClient;

	@BeforeClass
	public static void beforeClass() {
		// set the mongo client options
		MongoClientOptions.Builder builder = new MongoClientOptions.Builder();
		builder.connectionsPerHost(MongoDBPropertiesProvider.getInteger(MongoDBProperty.MONGO_DB_MAX_CONNECTIONS_PER_HOST));
		builder.sslEnabled(MongoDBPropertiesProvider.getBoolean(MongoDBProperty.MONGO_DB_SSL_ENABLED));
		builder.sslInvalidHostNameAllowed(MongoDBPropertiesProvider.getBoolean(MongoDBProperty.MONGO_DB_SSL_INVALIDHOSTNAMEALLOWED));
		MongoClientOptions options = builder.build();

		// set the credentials
		MongoCredential credential = MongoCredential.createCredential( //
				MongoDBPropertiesProvider.getString(MongoDBProperty.MONGO_DB_USER_NAME), //
				DB_NAME, //
				MongoDBPropertiesProvider.getString(MongoDBProperty.MONGO_DB_USER_PASSWORD).toCharArray());
		List<MongoCredential> list = new ArrayList<MongoCredential>();
		list.add(credential);

		ServerAddress serverAddress = new ServerAddress( //
				MongoDBPropertiesProvider.getString(MongoDBProperty.MONGO_DB_HOST), //
				MongoDBPropertiesProvider.getInteger(MongoDBProperty.MONGO_DB_PORT));
		mongoClient = new MongoClient(serverAddress, list, options);
	}

	@AfterClass
	public static void afterClass() {
		mongoClient.close();
	}

}
