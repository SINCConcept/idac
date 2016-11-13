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
package ac.at.tuwien.mt.monitoring.thread;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;

import ac.at.tuwien.mt.dao.monitor.ThingMonitorQoDDAO;
import ac.at.tuwien.mt.dao.monitor.ThingMonitorQoSDAO;
import ac.at.tuwien.mt.model.exception.InvalidObjectException;
import ac.at.tuwien.mt.model.exception.ResourceOutOfDateException;
import ac.at.tuwien.mt.model.helper.DefaultDateProvider;
import ac.at.tuwien.mt.model.thing.Thing;
import ac.at.tuwien.mt.model.thing.ThingMessage;
import ac.at.tuwien.mt.model.thing.message.Attribute;
import ac.at.tuwien.mt.model.thing.message.DataType;
import ac.at.tuwien.mt.model.thing.message.MetaModel;
import ac.at.tuwien.mt.model.thing.message.Property;
import ac.at.tuwien.mt.model.thing.monitor.MonitoredQoD;
import ac.at.tuwien.mt.model.thing.monitor.MonitoredQoS;
import ac.at.tuwien.mt.monitoring.thread.internal.MonitoringProcessingHelper;

/**
 * @author Florin Bogdan Balint
 *
 */
public class MessageMonitor {

	private static final Logger LOGGER = LogManager.getLogger(MessageMonitor.class);

	private final ThingMessage thingMessage;
	private final String message;
	private final Thing thing;

	private Date recordingDate;

	// are all fields there?
	private boolean complete = true;
	// do the fields have the specified data type?
	private boolean conform = true;

	private long age = -1;
	private long currency = -1;

	// DAOs to update the current monitoring status
	private ThingMonitorQoDDAO thingMonitorQoDDAO;
	private ThingMonitorQoSDAO thingMonitorQoSDAO;

	// the monitored qod - the same one as saved in the db
	private MonitoredQoD monitoredQoD;
	// the monitored qos - the same one as saved in the db
	private MonitoredQoS monitoredQoS;

	public MessageMonitor(ThingMessage thingMessage, ThingMonitorQoDDAO thingMonitorQoDDAO, ThingMonitorQoSDAO thingMonitorQoSDAO) {
		this.thingMonitorQoDDAO = thingMonitorQoDDAO;
		this.thingMonitorQoSDAO = thingMonitorQoSDAO;
		this.message = thingMessage.getMessage();
		this.thing = thingMessage.getThing();
		this.thingMessage = thingMessage;
	}

	/**
	 * Monitoring the quality of the data.
	 */
	public void monitorQoD() {
		// start with the completeness, based on the data model.
		traverseMetaModel();
		// compute the age and other time measurements
		computeAgeAndCurrency();

		// Create a new entry
		this.monitoredQoD = getFirstMonitoredQoDEntry();

		// save the values in the database
		// updateQoDValuesInDB();
	}

	public void monitorQoS() {
		this.monitoredQoS = getFirstMonitoredQoSEntry();
		// updateQoSValuesInDB();
	}

	private void updateQoSValuesInDB() {
		// see if there is an existing entry
		MonitoredQoS foundEntry = thingMonitorQoSDAO.find(thing.getThingId());
		if (foundEntry == null) {
			// if not insert the first entry and return - nothing else to do.
			thingMonitorQoSDAO.insert(monitoredQoS);
			return;
		}

		// otherwise compute the new values
		foundEntry = MonitoringProcessingHelper.computeNewValues(foundEntry, monitoredQoS);

		// update
		try {
			thingMonitorQoSDAO.update(foundEntry);
		} catch (ResourceOutOfDateException | InvalidObjectException e) {
			// object might have been updated in the mean time -> retry
			updateQoSValuesInDB();
		}
	}

	private void updateQoDValuesInDB() {
		// see if there are any existing entries
		MonitoredQoD foundEntry = thingMonitorQoDDAO.find(thing.getThingId());
		if (foundEntry == null) {
			// if not insert them
			thingMonitorQoDDAO.insert(monitoredQoD);
			return;
		}
		// otherwise compute the new values
		foundEntry = MonitoringProcessingHelper.computeNewValues(foundEntry, monitoredQoD);

		// update
		try {
			thingMonitorQoDDAO.update(foundEntry);
		} catch (ResourceOutOfDateException | InvalidObjectException e) {
			// object might have been updated in the mean time -> retry
			updateQoDValuesInDB();
		}
	}

	private MonitoredQoD getFirstMonitoredQoDEntry() {
		MonitoredQoD monitoredQoD = new MonitoredQoD();
		monitoredQoD.setThingId(thing.getThingId());
		monitoredQoD.setTotalNrOfSamples(1);
		if (complete) {
			monitoredQoD.setSamplesComplete(1);
			monitoredQoD.setCompleteness(100.00);
		}
		if (conform) {
			monitoredQoD.setSamplesConform(1);
			monitoredQoD.setConformity(100.00);
		}
		if (age > 0) {
			monitoredQoD.setAverageAge(age);
			monitoredQoD.setAgeSamples(1);
		}
		if (currency > 0) {
			monitoredQoD.setAverageCurrency(currency);
		}

		return monitoredQoD;
	}

	private MonitoredQoS getFirstMonitoredQoSEntry() {
		MonitoredQoS monitoredQoS = new MonitoredQoS();
		monitoredQoS.setThingId(thing.getThingId());
		monitoredQoS.setTotalNrOfSamples(1);
		monitoredQoS.setFirstMessageReceival(DefaultDateProvider.getCurrentTimeStamp());
		monitoredQoS.setLastMessageReceival(DefaultDateProvider.getCurrentTimeStamp());
		monitoredQoS.setExpectedNrOfSamples(1l);
		monitoredQoS.setAvailability(100.00);
		monitoredQoS.setExpectedFrequency(thing.getQos().getFrequency());
		return monitoredQoS;
	}

	private void computeAgeAndCurrency() {
		LOGGER.trace("Computing age");
		if (recordingDate != null) {
			long recordingDateInMs = recordingDate.getTime();
			long currentTimeInMs = Calendar.getInstance().getTime().getTime();
			age = currentTimeInMs - recordingDateInMs;
			LOGGER.trace("Message age (in ms): " + age);
		} else {
			LOGGER.trace("Cannot compute age, probably because the date of the recording cannot be parsed.");
		}

		LOGGER.trace("Computing the currency");
		if (age >= 0) {
			long deliveryTimeInMs = DefaultDateProvider.getDateFromTimeStamp(thingMessage.getDeliveryTime()).getTime();
			long inputTimeInMs = DefaultDateProvider.getDateFromTimeStamp(thingMessage.getInputTime()).getTime();
			currency = age + (deliveryTimeInMs - inputTimeInMs);
			LOGGER.trace("Message currency (in ms): " + currency);
		} else {
			LOGGER.trace("Cannot compute currency, because the message age is unknown.");
		}
	}

	public void traverseMetaModel() {
		LOGGER.trace("Traversing meta-model.");
		MetaModel metaModel = thing.getMetaModel();
		Document document = Document.parse(message);
		traverseMetaModel(metaModel, document);
		LOGGER.trace("Traversing meta-model completed.");
	}

	private void traverseMetaModel(MetaModel metaModel, Document document) {
		// for each attribute
		for (Attribute attribute : metaModel.getAttributes()) {
			String attributeName = attribute.getName();
			DataType dataType = attribute.getDataType();
			boolean containsKey = document.containsKey(attributeName);
			// if the key is not present, the message does not contain this
			// attribute, which also means that it is not consistent.
			if (!containsKey) {
				complete = false;
				// we can skip further checks for this attribute, since it does
				// not exist.
				continue;
			}
			// check the conformity
			checkAttributeConformity(attribute, document);

			// check the property
			Property property = attribute.getProperty();
			if (conform && property != null && dataType == DataType.DATE) {
				if (property.getRecordingDate() == true) {
					LOGGER.trace("Computing message age.");
					String date = document.getString(attributeName);
					String dateFormat = property.getDateFormat();
					SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
					try {
						recordingDate = sdf.parse(date);
					} catch (ParseException e) {
						LOGGER.trace("This should not happen: Cannot parse recording date!");
					}
				}
			}

			// recursive call in case of objects
			if (dataType == DataType.ATTRIBUTE) {
				MetaModel attributeMetaModel = attribute.getMetaModel();
				Object object = document.get(attributeName);
				if (object instanceof Document) {
					traverseMetaModel(attributeMetaModel, (Document) object);
				}
			}
		}
	}

	private void checkAttributeConformity(Attribute attribute, Document document) {
		LOGGER.trace("Checking attribute conformity for: " + attribute.getName());
		try {
			Object object = document.get(attribute.getName());
			// if the object does not exist - return
			if (object == null) {
				return;
			}

			Property property = attribute.getProperty();
			switch (attribute.getDataType()) {
			case STRING:
				LOGGER.trace("Found STRING");
				String stringAttr = document.getString(attribute.getName());
				if (property != null) {
					String dateFormat = property.getDateFormat();
					if (dateFormat != null) {
						SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
						Date parsedDate = sdf.parse(stringAttr);
						LOGGER.trace("Date parsed: " + parsedDate);
					}
					if (property.getRecordingDate()) {
						SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
						recordingDate = sdf.parse(stringAttr);
						LOGGER.trace("Recording date parsed: " + recordingDate);
					}
				}
				break;
			case BOOLEAN:
				LOGGER.trace("Found BOOLEAN");
				document.getBoolean(attribute.getName());
				break;
			case DATE:
				LOGGER.trace("Found DATE");
				String string = document.getString(attribute.getName());
				if (property != null) {
					String dateFormat = property.getDateFormat();
					if (dateFormat != null) {
						SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
						Date parsedDate = sdf.parse(string);
						LOGGER.trace("Date parsed: " + parsedDate);
					}
					if (property.getRecordingDate()) {
						SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
						recordingDate = sdf.parse(string);
						LOGGER.trace("Recording date parsed: " + recordingDate);
					}
				}
				break;
			case INTEGER:
				LOGGER.trace("Found INTEGER");
				document.getInteger(attribute.getName());
				break;
			case DOUBLE:
				LOGGER.trace("Found DOUBLE");
				try {
					Double.parseDouble(object.toString());
				} catch (Exception e) {
					conform = false;
				}
				break;
			case ATTRIBUTE:
				LOGGER.trace("Found ATTRIBUTE");
				if (object instanceof Document == false) {
					conform = false;
				}
				break;
			default:
				conform = false;
				break;
			}
		} catch (ClassCastException e) {
			LOGGER.error("Cannot cast attribute: " + attribute.getName());
			conform = false;
		} catch (ParseException e) {
			LOGGER.error("Cannot parse attribute: " + attribute.getName());
			conform = false;
		}
	}

	/**
	 * @return the complete
	 */
	public boolean isComplete() {
		return complete;
	}

	/**
	 * @param complete
	 *            the complete to set
	 */
	public void setComplete(boolean complete) {
		this.complete = complete;
	}

	/**
	 * @return the conform
	 */
	public boolean isConform() {
		return conform;
	}

	/**
	 * @param conform
	 *            the conform to set
	 */
	public void setConform(boolean conform) {
		this.conform = conform;
	}

	/**
	 * @return the recordingDate
	 */
	public Date getRecordingDate() {
		return recordingDate;
	}

	/**
	 * @param recordingDate
	 *            the recordingDate to set
	 */
	public void setRecordingDate(Date recordingDate) {
		this.recordingDate = recordingDate;
	}

	/**
	 * @return the age
	 */
	public long getAge() {
		return age;
	}

	/**
	 * @param age
	 *            the age to set
	 */
	public void setAge(long age) {
		this.age = age;
	}

	/**
	 * @return the currency
	 */
	public long getCurrency() {
		return currency;
	}

	/**
	 * @param currency
	 *            the currency to set
	 */
	public void setCurrency(long currency) {
		this.currency = currency;
	}

	/**
	 * @return the monitoredQoD
	 */
	public MonitoredQoD getMonitoredQoD() {
		return monitoredQoD;
	}

	/**
	 * @param monitoredQoD
	 *            the monitoredQoD to set
	 */
	public void setMonitoredQoD(MonitoredQoD monitoredQoD) {
		this.monitoredQoD = monitoredQoD;
	}

	/**
	 * @return the monitoredQoS
	 */
	public MonitoredQoS getMonitoredQoS() {
		return monitoredQoS;
	}

	/**
	 * @param monitoredQoS
	 *            the monitoredQoS to set
	 */
	public void setMonitoredQoS(MonitoredQoS monitoredQoS) {
		this.monitoredQoS = monitoredQoS;
	}

}
