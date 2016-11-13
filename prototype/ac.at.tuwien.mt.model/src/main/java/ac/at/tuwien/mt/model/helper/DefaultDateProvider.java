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
package ac.at.tuwien.mt.model.helper;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 
 * @author Florin Bogdan Balint
 *
 */
public class DefaultDateProvider {

	private static final Logger LOGGER = LogManager.getLogger(DefaultDateProvider.class);
	public static final String DEFAULT_TIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX";

	private DefaultDateProvider() {
		// this is a helper class
	}

	/**
	 * Returns the current timestamp as a string with the following format:
	 * yyyy-MM-dd'T'HH:mm:ss.SSSXXX
	 * 
	 * @return String
	 */
	public static String getCurrentTimeStamp() {
		Date currentDateTime = Calendar.getInstance().getTime();
		SimpleDateFormat sdf = new SimpleDateFormat(DEFAULT_TIME_PATTERN);
		String currentDateTimeAsString = sdf.format(currentDateTime);
		return currentDateTimeAsString;
	}

	/**
	 * Returns the current timestamp as a string with the following format:
	 * yyyy-MM-dd'T'HH:mm:ss.SSSXXX
	 * 
	 * @param date
	 * @return
	 */
	public static String getCurrentTimeStamp(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat(DEFAULT_TIME_PATTERN);
		String currentDateTimeAsString = sdf.format(date);
		return currentDateTimeAsString;
	}

	/**
	 * Returns the date from a string with the following format:
	 * yyyy-MM-dd'T'HH:mm:ss.SSSXXX
	 * 
	 * @param dateTimeAsString
	 * @return Date
	 */
	public static Date getDateFromTimeStamp(String dateTimeAsString) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(DEFAULT_TIME_PATTERN);
			Date parsedDate = sdf.parse(dateTimeAsString);
			return parsedDate;
		} catch (Exception e) {
			LOGGER.error(e);
		}
		return null;
	}

}
