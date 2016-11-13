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

import java.util.Calendar;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ac.at.tuwien.mt.dao.datacontract.DataContractDAO;
import ac.at.tuwien.mt.dao.monitor.MonitoredDataContractDAO;
import ac.at.tuwien.mt.model.datacontract.DataContract;
import ac.at.tuwien.mt.model.helper.DefaultDateProvider;
import ac.at.tuwien.mt.model.thing.Thing;
import ac.at.tuwien.mt.model.thing.ThingMessage;
import ac.at.tuwien.mt.monitoring.thread.DataContractMonitor;
import ac.at.tuwien.mt.monitoring.thread.MessageMonitor;

/**
 * @author Florin Bogdan Balint
 *
 */
public class ThingSimulator1 implements Runnable {

	private static final Logger LOGGER = LogManager.getLogger(ThingSimulator1.class);

	private final MonitoredDataContractDAO mdcDAO;
	private final DataContractDAO dcDAO;

	private Thing thing;
	private DataContract dc;

	public ThingSimulator1(Thing thing, DataContract dc, MonitoredDataContractDAO mdcDAO, DataContractDAO dcDAO) {
		this.thing = thing;
		this.dc = dc;
		this.mdcDAO = mdcDAO;
		this.dcDAO = dcDAO;
	}

	@Override
	public void run() {

		// update the data contract monitoring
		ExecutorService threadPool = Executors.newCachedThreadPool();

		for (int i = 0; i < 1000; i++) {

			String currentTimeStamp = DefaultDateProvider.getCurrentTimeStamp();
			String msg = "{ \"thingId\" : \"28-8000001ebda3\", \"temperature\" : \"28875\", \"scale\" : \"Celsius\", \"recordingTime\" : \"" + currentTimeStamp + "\"}";
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				System.err.println(e);
			}
			currentTimeStamp = DefaultDateProvider.getCurrentTimeStamp();
			ac.at.tuwien.mt.model.thing.ThingMessage thingMessage = new ac.at.tuwien.mt.model.thing.ThingMessage(thing, msg, currentTimeStamp, currentTimeStamp);
			MessageGenerator messageGenerator = new MessageGenerator(thingMessage, dc, mdcDAO, dcDAO);
			threadPool.submit(messageGenerator);

		}

		threadPool.shutdown();

		while (!threadPool.isTerminated()) {
			// wait
		}

	}

	private class MessageGenerator implements Runnable {
		private final ThingMessage thingMessage;
		private final MonitoredDataContractDAO mdcDAO2;
		private final DataContractDAO dcDAO2;
		private final DataContract dc2;

		public MessageGenerator(ThingMessage thingMessage, DataContract dc2, MonitoredDataContractDAO mdcDAO2, DataContractDAO dcDAO2) {
			this.thingMessage = thingMessage;
			this.mdcDAO2 = mdcDAO2;
			this.dcDAO2 = dcDAO2;
			this.dc2 = dc2;
		}

		@Override
		public void run() {
			long timeInMillis1 = Calendar.getInstance().getTimeInMillis();
			MessageMonitor tm = new MessageMonitor(thingMessage, null, null);
			tm.monitorQoD();
			tm.monitorQoS();

			DataContractMonitor dcm = new DataContractMonitor(thingMessage.getThing().getThingId(), dc2, tm.getMonitoredQoD(), tm.getMonitoredQoS(), mdcDAO2, dcDAO2);
			dcm.updateValuesInDB();
			long timeInMillis2 = Calendar.getInstance().getTimeInMillis();
			long duration = timeInMillis2 - timeInMillis1;
			LOGGER.info(duration);
		}
	}

}
