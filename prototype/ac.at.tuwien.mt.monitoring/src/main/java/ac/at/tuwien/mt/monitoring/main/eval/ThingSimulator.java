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

import java.util.Calendar;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ac.at.tuwien.mt.dao.monitor.ThingMonitorQoDDAO;
import ac.at.tuwien.mt.dao.monitor.ThingMonitorQoSDAO;
import ac.at.tuwien.mt.model.helper.DefaultDateProvider;
import ac.at.tuwien.mt.model.thing.Thing;
import ac.at.tuwien.mt.model.thing.ThingMessage;
import ac.at.tuwien.mt.model.thing.message.Attribute;
import ac.at.tuwien.mt.model.thing.message.DataType;
import ac.at.tuwien.mt.model.thing.message.MetaModel;
import ac.at.tuwien.mt.model.thing.message.Property;
import ac.at.tuwien.mt.monitoring.thread.MessageMonitor;

/**
 * @author Florin Bogdan Balint
 *
 */
public class ThingSimulator implements Runnable {

	private static final Logger LOGGER = LogManager.getLogger(ThingSimulator.class);

	private String thingId;
	private ThingMonitorQoDDAO qodDAO;
	private ThingMonitorQoSDAO qosDAO;

	public ThingSimulator(String thingId, ThingMonitorQoDDAO qodDAO, ThingMonitorQoSDAO qosDAO) {
		this.thingId = thingId;
		this.qodDAO = qodDAO;
		this.qosDAO = qosDAO;
	}

	@Override
	public void run() {
		Thing thing = new Thing();
		thing.setThingId(thingId);
		thing.getQos().setFrequency(1000);
		thing.setMetaModel(getTestMetaModel());

		// update the data contract monitoring
		ExecutorService cachedPool = Executors.newCachedThreadPool();

		for (int i = 0; i < 1000; i++) {
			String currentTimeStamp = DefaultDateProvider.getCurrentTimeStamp();
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				System.err.println(e);
			}
			String message = "{ \"thingId\" : \"28-8000001ebda3\", \"temperature\" : \"28875\", \"scale\" : \"Celsius\", \"recordingTime\" : \"" + currentTimeStamp + "\"}";
			currentTimeStamp = DefaultDateProvider.getCurrentTimeStamp();
			ThingMessage thingMessage = new ThingMessage(thing, message, currentTimeStamp, currentTimeStamp);
			MessageGenerator messageGenerator = new MessageGenerator(thingMessage);
			cachedPool.submit(messageGenerator);
		}

		cachedPool.shutdown();
		while (!cachedPool.isTerminated()) {
			// do nothing
		}

	}

	private class MessageGenerator implements Runnable {
		private final ThingMessage thingMessage;

		public MessageGenerator(ThingMessage thingMessage) {
			this.thingMessage = thingMessage;
		}

		@Override
		public void run() {
			long timeInMillis1 = Calendar.getInstance().getTimeInMillis();
			MessageMonitor tm = new MessageMonitor(thingMessage, qodDAO, qosDAO);
			tm.monitorQoD();
			tm.monitorQoS();
			long timeInMillis2 = Calendar.getInstance().getTimeInMillis();
			long duration = timeInMillis2 - timeInMillis1;
			LOGGER.info(duration);
		}
	}

	private MetaModel getTestMetaModel() {
		Attribute attribute1 = new Attribute();
		attribute1.setDataType(DataType.STRING);
		attribute1.setName("thingId");
		Property property = new Property();
		property.setIdentifier(true);
		attribute1.setProperty(property);

		Attribute attribute2 = new Attribute();
		attribute2.setDataType(DataType.DOUBLE);
		attribute2.setName("temperature");

		Attribute attribute3 = new Attribute();
		attribute3.setDataType(DataType.STRING);
		attribute3.setName("scale");

		Attribute attribute4 = new Attribute();
		attribute4.setDataType(DataType.DATE);
		attribute4.setName("recordingTime");
		Property property4 = new Property();
		property4.setIdentifier(false);
		property4.setRecordingDate(true);
		property4.setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
		attribute4.setProperty(property4);

		MetaModel model1 = new MetaModel();
		model1.getAttributes().add(attribute1);
		model1.getAttributes().add(attribute2);
		model1.getAttributes().add(attribute3);
		model1.getAttributes().add(attribute4);

		return model1;
	}

}
