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
package ac.at.tuwien.mt.monitoring.main.eval;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;

import ac.at.tuwien.mt.dao.monitor.ThingMonitorQoDDAO;
import ac.at.tuwien.mt.dao.monitor.ThingMonitorQoSDAO;
import ac.at.tuwien.mt.dao.monitor.impl.ThingMonitorQoDDAOImpl;
import ac.at.tuwien.mt.dao.monitor.impl.ThingMonitorQoSDAOImpl;

/**
 * @author Florin Bogdan Balint
 *
 */
public class ComputationEvaluation {

	private static MongoClient mongoClient;
	private static ThingMonitorQoDDAO qodDAO;
	private static ThingMonitorQoSDAO qosDAO;

	public static void init() {
		// set the mongo client options
		MongoClientOptions.Builder builder = new MongoClientOptions.Builder();
		builder.connectionsPerHost(100);
		builder.sslEnabled(false);
		builder.sslInvalidHostNameAllowed(false);
		MongoClientOptions options = builder.build();

		// set the credentials
		MongoCredential credential = MongoCredential.createCredential("usermttest1", "mttest1_local", "usermttest1_31415".toCharArray());
		List<MongoCredential> list = new ArrayList<MongoCredential>();
		list.add(credential);

		ServerAddress serverAddress = new ServerAddress("localhost", 27017);
		mongoClient = new MongoClient(serverAddress, list, options);
		qodDAO = new ThingMonitorQoDDAOImpl(mongoClient, "mttest1_local", "mt.test.thing.monitor.qod");
		qosDAO = new ThingMonitorQoSDAOImpl(mongoClient, "mttest1_local", "mt.test.thing.monitor.qos");
	}

	public static void cleanUp() {
		mongoClient.close();
	}

	public static void main(String[] args) throws Exception {
		init();

		System.out.println("Started - Sleeping 30 seconds...");
		Thread.sleep(30000);

		// update the datacontract monitoring
		ExecutorService cachedPool = Executors.newCachedThreadPool();

		for (int i = 0; i < 500; i++) {
			ThingSimulator ts = new ThingSimulator(i + "", qodDAO, qosDAO);
			cachedPool.submit(ts);
		}

		cachedPool.shutdown();
		while (!cachedPool.isTerminated()) {
		}

		cleanUp();

		System.out.println("Finished - Sleeping 30 seconds...");
		Thread.sleep(30000);
	}

}
