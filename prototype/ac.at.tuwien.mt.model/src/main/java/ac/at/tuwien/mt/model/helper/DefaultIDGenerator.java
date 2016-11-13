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
import java.util.Random;

public final class DefaultIDGenerator {

	private DefaultIDGenerator() {
		// defeat instantiation
	}

	/**
	 * Generates a default id based on the current timestamp and finally adding
	 * two randomly generated numbers.
	 * 
	 * @return
	 */
	public static String generateID() {
		String id = "";

		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSS");
		id = sdf.format(cal.getTime());

		Random random = new Random();
		int low = 100000;
		int high = 999999;

		int randomNumber1 = random.nextInt(high - low) + low;
		int randomNumber2 = random.nextInt(high - low) + low;

		id = id + randomNumber1 + randomNumber2;

		return id;
	}

}
