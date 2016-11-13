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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;

import ac.at.tuwien.mt.dao.datacontract.DataContractDAO;
import ac.at.tuwien.mt.dao.datacontract.impl.DataContractDAOImpl;
import ac.at.tuwien.mt.dao.monitor.MonitoredDataContractDAO;
import ac.at.tuwien.mt.dao.monitor.impl.MonitoredDataContractDAOImpl;
import ac.at.tuwien.mt.dao.thing.ThingDAO;
import ac.at.tuwien.mt.dao.thing.impl.ThingDAOImpl;
import ac.at.tuwien.mt.model.datacontract.DataContract;
import ac.at.tuwien.mt.model.thing.Thing;

/**
 * @author Florin Bogdan Balint
 *
 */
public class ComputationEvaluation {

	private static MongoClient mongoClient;

	// private static ThingMonitorQoDDAO qodDAO;
	// private static ThingMonitorQoSDAO qosDAO;
	private static MonitoredDataContractDAO mdcDAO;
	private static DataContractDAO dcDAO;
	private static ThingDAO tDAO;

	public static void init() {
		// set the mongo client options
		MongoClientOptions.Builder builder = new MongoClientOptions.Builder();
		builder.connectionsPerHost(10000);
		builder.sslEnabled(false);
		builder.sslInvalidHostNameAllowed(false);
		builder.socketKeepAlive(true);
		MongoClientOptions options = builder.build();

		// set the credentials
		MongoCredential credential = MongoCredential.createCredential("usermttest1", "mttest1_local", "usermttest1_31415".toCharArray());
		List<MongoCredential> list = new ArrayList<MongoCredential>();
		list.add(credential);

		ServerAddress serverAddress = new ServerAddress("localhost", 27017);
		mongoClient = new MongoClient(serverAddress, list, options);

		mongoClient.getDatabase("mttest1_local").getCollection("mt.test.thing.monitor.qod").drop();
		mongoClient.getDatabase("mttest1_local").getCollection("mt.test.thing.monitor.qos").drop();
		mongoClient.getDatabase("mttest1_local").getCollection("mt.test.dc.monitor").drop();
		mongoClient.getDatabase("mttest1_local").getCollection("mt.test.dc").drop();
		mongoClient.getDatabase("mttest1_local").getCollection("mt.test.thing").drop();

		// qodDAO = new ThingMonitorQoDDAOImpl(mongoClient, "mttest1_local",
		// "mt.test.thing.monitor.qod");
		// qosDAO = new ThingMonitorQoSDAOImpl(mongoClient, "mttest1_local",
		// "mt.test.thing.monitor.qos");
		mdcDAO = new MonitoredDataContractDAOImpl(mongoClient, "mttest1_local", "mt.test.dc.monitor");
		dcDAO = new DataContractDAOImpl(mongoClient, "mttest1_local", "mt.test.dc");
		tDAO = new ThingDAOImpl(mongoClient, "mttest1_local", "mt.test.thing");

	}

	public static void cleanUp() {
		mongoClient.close();
	}

	public static void main(String[] args) throws Exception {
		init();

		// update the datacontract monitoring
		ExecutorService cachedPool = Executors.newCachedThreadPool();

		for (int i = 0; i < 100; i++) {
			// insert the thing and the data contract
			Thing thing = tDAO.insert(ThingProvider.getThing3(i + ""));
			DataContract dc = dcDAO.insert(ThingProvider.getDataContract(i + ""));

			Thread.sleep(200);

			ThingSimulator3 ts = new ThingSimulator3(thing, dc, mdcDAO, dcDAO);
			cachedPool.submit(ts);
		}

		cachedPool.shutdown();
		while (!cachedPool.isTerminated()) {
		}

		cleanUp();
	}

}
