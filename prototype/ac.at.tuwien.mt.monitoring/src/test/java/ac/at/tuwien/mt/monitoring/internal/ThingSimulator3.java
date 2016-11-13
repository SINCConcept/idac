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
public class ThingSimulator3 implements Runnable {

	private static final Logger LOGGER = LogManager.getLogger(ThingSimulator3.class);

	private final MonitoredDataContractDAO mdcDAO;
	private final DataContractDAO dcDAO;

	private Thing thing;
	private DataContract dc;

	public ThingSimulator3(Thing thing, DataContract dc, MonitoredDataContractDAO mdcDAO, DataContractDAO dcDAO) {
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
					ThingSimMessage2 tm = createThingMessageFromElements(elements);

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

	private ThingSimMessage2 createThingMessageFromElements(String[] elements) {
		ThingSimMessage2 tm = new ThingSimMessage2();
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
		tm.setC23(elements[23]);
		tm.setC24(elements[24]);
		tm.setC25(elements[25]);
		tm.setC26(elements[26]);
		tm.setC27(elements[27]);
		tm.setC28(elements[28]);
		tm.setC29(elements[29]);

		tm.setC30(elements[30]);
		tm.setC31(elements[31]);
		tm.setC32(elements[32]);
		tm.setC33(elements[33]);
		tm.setC34(elements[34]);
		tm.setC35(elements[35]);
		tm.setC36(elements[36]);
		tm.setC37(elements[37]);
		tm.setC38(elements[38]);
		tm.setC39(elements[39]);

		tm.setC40(elements[40]);
		tm.setC41(elements[41]);
		tm.setC42(elements[42]);
		tm.setC43(elements[43]);
		tm.setC44(elements[44]);
		tm.setC45(elements[45]);
		tm.setC46(elements[46]);
		tm.setC47(elements[47]);
		tm.setC48(elements[48]);
		tm.setC49(elements[49]);

		tm.setC50(elements[50]);
		tm.setC51(elements[51]);
		tm.setC52(elements[52]);
		tm.setC53(elements[53]);
		tm.setC54(elements[54]);
		tm.setC55(elements[55]);
		tm.setC56(elements[56]);
		tm.setC57(elements[57]);
		tm.setC58(elements[58]);
		tm.setC59(elements[59]);

		tm.setC60(elements[60]);
		tm.setC61(elements[61]);
		tm.setC62(elements[62]);
		tm.setC63(elements[63]);
		tm.setC64(elements[64]);
		tm.setC65(elements[65]);
		tm.setC66(elements[66]);
		tm.setC67(elements[67]);
		tm.setC68(elements[68]);
		tm.setC69(elements[69]);

		tm.setC70(elements[70]);
		tm.setC71(elements[71]);
		tm.setC72(elements[72]);
		tm.setC73(elements[73]);
		tm.setC74(elements[74]);
		tm.setC75(elements[75]);
		tm.setC86(elements[76]);
		tm.setC77(elements[77]);
		tm.setC78(elements[78]);
		tm.setC79(elements[79]);

		tm.setC80(elements[80]);
		tm.setC81(elements[81]);
		tm.setC82(elements[82]);
		tm.setC83(elements[83]);
		tm.setC84(elements[84]);
		tm.setC85(elements[85]);
		tm.setC86(elements[86]);
		tm.setC87(elements[87]);
		tm.setC88(elements[88]);
		tm.setC89(elements[89]);

		tm.setC90(elements[90]);
		tm.setC91(elements[91]);
		tm.setC92(elements[92]);
		tm.setC93(elements[93]);
		tm.setC94(elements[94]);
		tm.setC95(elements[95]);
		tm.setC96(elements[96]);
		tm.setC97(elements[97]);
		tm.setC98(elements[98]);
		tm.setC99(elements[99]);

		tm.setC100(elements[100]);
		tm.setC101(elements[101]);
		tm.setC102(elements[102]);
		tm.setC103(elements[103]);
		tm.setC104(elements[104]);
		tm.setC105(elements[105]);
		tm.setC106(elements[106]);
		tm.setC107(elements[107]);
		tm.setC108(elements[108]);
		tm.setC109(elements[109]);

		tm.setC110(elements[110]);
		tm.setC111(elements[111]);
		tm.setC112(elements[112]);
		tm.setC113(elements[113]);
		tm.setC114(elements[114]);
		tm.setC115(elements[115]);
		tm.setC116(elements[116]);
		tm.setC117(elements[117]);
		tm.setC118(elements[118]);
		tm.setC119(elements[119]);

		tm.setC120(elements[120]);
		tm.setC121(elements[121]);
		tm.setC122(elements[122]);
		tm.setC123(elements[123]);
		tm.setC124(elements[124]);
		tm.setC125(elements[125]);
		tm.setC126(elements[126]);
		tm.setC127(elements[127]);
		tm.setC128(elements[128]);
		tm.setC129(elements[129]);

		tm.setC130(elements[130]);
		tm.setC131(elements[131]);
		tm.setC132(elements[132]);
		tm.setC133(elements[133]);
		tm.setC134(elements[134]);
		tm.setC135(elements[135]);
		tm.setC136(elements[136]);
		tm.setC137(elements[137]);
		tm.setC138(elements[138]);
		tm.setC139(elements[139]);

		tm.setC140(elements[140]);
		tm.setC141(elements[141]);
		tm.setC142(elements[142]);
		tm.setC143(elements[143]);
		tm.setC144(elements[144]);
		tm.setC145(elements[145]);
		tm.setC146(elements[146]);
		tm.setC147(elements[147]);
		tm.setC148(elements[148]);
		tm.setC149(elements[149]);

		tm.setC150(elements[150]);
		tm.setC151(elements[151]);
		tm.setC152(elements[152]);
		tm.setC153(elements[153]);
		tm.setC154(elements[154]);
		tm.setC155(elements[155]);
		tm.setC156(elements[156]);
		tm.setC157(elements[157]);
		tm.setC158(elements[158]);
		tm.setC159(elements[159]);

		tm.setC160(elements[160]);
		tm.setC161(elements[161]);
		tm.setC162(elements[162]);
		tm.setC163(elements[163]);
		tm.setC164(elements[164]);
		tm.setC165(elements[165]);
		tm.setC166(elements[166]);
		tm.setC167(elements[167]);

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
