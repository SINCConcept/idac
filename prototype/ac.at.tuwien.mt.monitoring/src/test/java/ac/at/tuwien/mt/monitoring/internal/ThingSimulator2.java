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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
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
public class ThingSimulator2 implements Runnable {

	private static final Logger LOGGER = LogManager.getLogger(ThingSimulator2.class);

	private final MonitoredDataContractDAO mdcDAO;
	private final DataContractDAO dcDAO;

	private Thing thing;
	private DataContract dc;

	public ThingSimulator2(Thing thing, DataContract dc, MonitoredDataContractDAO mdcDAO, DataContractDAO dcDAO) {
		this.thing = thing;
		this.dc = dc;
		this.mdcDAO = mdcDAO;
		this.dcDAO = dcDAO;
	}

	@Override
	public void run() {

		// update the data contract monitoring
		ExecutorService cachedPool = Executors.newCachedThreadPool();

		String csvFile = System.getProperty("user.dir") + File.separator + "data.csv";
		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = ";";

		try {
			br = new BufferedReader(new FileReader(csvFile));
			// consume first line
			br.readLine();

			for (int i = 0; i < 1000; i++) {
				while ((line = br.readLine()) != null) {
					String[] elements = line.split(cvsSplitBy);
					ThingSimMessage tm = createThingMessageFromElements(elements);

					String msg = tm.getDocument().toJson();

					String currentTimeStamp = DefaultDateProvider.getCurrentTimeStamp();
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						System.err.println(e);
					}
					currentTimeStamp = DefaultDateProvider.getCurrentTimeStamp();
					ac.at.tuwien.mt.model.thing.ThingMessage thingMessage = new ac.at.tuwien.mt.model.thing.ThingMessage(thing, msg, currentTimeStamp, currentTimeStamp);
					MessageGenerator messageGenerator = new MessageGenerator(thingMessage, dc, mdcDAO, dcDAO);
					cachedPool.submit(messageGenerator);

					break;
				}
			}
		} catch (FileNotFoundException e) {
			LOGGER.error(e);
		} catch (IOException e) {
			LOGGER.error(e);
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					LOGGER.error(e);
				}
			}
		}

		cachedPool.shutdown();
		while (!cachedPool.isTerminated()) {
			// do nothing
		}

	}

	private ThingSimMessage createThingMessageFromElements(String[] elements) {
		ThingSimMessage tm = new ThingSimMessage();
		if (elements.length == 23) {
			tm.setC0(elements[0]);
			tm.setC1(elements[1]);
			tm.setC2(elements[2]);
			tm.setC3(elements[3]);
			tm.setC4(elements[4]);
			tm.setC5(elements[5]);
			tm.setC6(elements[6]);
			tm.setC7(elements[7]);
			tm.setC8(elements[8]);
			tm.setC9(elements[9]);
			tm.setC10(elements[10]);
			tm.setC11(elements[11]);
			tm.setC12(elements[12]);
			tm.setC13(elements[13]);
			tm.setC14(elements[14]);
			tm.setC15(elements[15]);
			tm.setC16(elements[16]);
			tm.setC17(elements[17]);
			tm.setC18(elements[18]);
			tm.setC19(elements[19]);
			tm.setC20(elements[20]);
			tm.setC21(elements[21]);
			tm.setC22(elements[22]);
		}

		if (elements.length == 24) {
			tm.setC23(elements[23]);
		}

		return tm;
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
