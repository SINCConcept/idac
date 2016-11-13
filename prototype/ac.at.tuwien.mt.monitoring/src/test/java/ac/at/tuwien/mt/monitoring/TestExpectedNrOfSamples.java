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
package ac.at.tuwien.mt.monitoring;

import java.util.Calendar;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Florin Bogdan Balint
 *
 */
public class TestExpectedNrOfSamples {

	@Test
	public void test() {
		Calendar cal1 = Calendar.getInstance();
		cal1.set(Calendar.YEAR, 2016);
		cal1.set(Calendar.DAY_OF_YEAR, 10);
		cal1.set(Calendar.HOUR, 0);
		cal1.set(Calendar.MINUTE, 0);
		cal1.set(Calendar.SECOND, 0);
		cal1.set(Calendar.MILLISECOND, 0);
		Calendar cal2 = Calendar.getInstance();
		cal2.set(Calendar.YEAR, 2016);
		cal2.set(Calendar.DAY_OF_YEAR, 10);
		cal2.set(Calendar.HOUR, 0);
		cal2.set(Calendar.MINUTE, 1);
		cal2.set(Calendar.SECOND, 0);
		cal2.set(Calendar.MILLISECOND, 0);

		long timeDiffInMs = cal2.getTime().getTime() - cal1.getTime().getTime();
		Assert.assertEquals(60000, timeDiffInMs);

		long expectedNrOfSamples = (long) (timeDiffInMs / 10000);
		Assert.assertEquals(6, expectedNrOfSamples);
	}
}
