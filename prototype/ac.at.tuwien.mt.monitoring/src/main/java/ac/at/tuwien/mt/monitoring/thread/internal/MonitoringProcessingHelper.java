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
package ac.at.tuwien.mt.monitoring.thread.internal;

import java.util.Date;

import ac.at.tuwien.mt.model.helper.DefaultDateProvider;
import ac.at.tuwien.mt.model.thing.monitor.MonitoredQoD;
import ac.at.tuwien.mt.model.thing.monitor.MonitoredQoS;

/**
 * @author Florin Bogdan Balint
 *
 */
public final class MonitoringProcessingHelper {

	private MonitoringProcessingHelper() {
		// empty constructor
	}

	public static MonitoredQoD computeNewValues(MonitoredQoD foundEntry, MonitoredQoD newEntry) {
		long totalNrOfSamples = foundEntry.getTotalNrOfSamples();
		// compute the age only if it can be computed
		double age = newEntry.getAverageAge();
		if (age > 0) {
			double foundAvgAge = foundEntry.getAverageAge();
			long foundAvgAgeSamples = foundEntry.getAgeSamples();
			if (foundAvgAgeSamples == 0) {
				// no age specified until now
				foundEntry.setAverageAge(age);
			} else {
				double newAvgAge = (foundAvgAgeSamples * foundAvgAge + age) / (foundAvgAgeSamples + 1);
				foundEntry.setAverageAge(newAvgAge);
			}
			foundEntry.setAgeSamples(foundAvgAgeSamples + 1);
		}

		// compute and set the completeness
		if (newEntry.getCompleteness() > 0) {
			long samplesComplete = foundEntry.getSamplesComplete();
			samplesComplete++;
			foundEntry.setSamplesComplete(samplesComplete);
			double completeness = (foundEntry.getSamplesComplete() / foundEntry.getTotalNrOfSamples() * 100);
			foundEntry.setCompleteness(completeness);
		}

		// compute and set the conformity
		if (newEntry.getConformity() > 0) {
			long samplesConform = foundEntry.getSamplesConform();
			samplesConform++;
			foundEntry.setSamplesConform(samplesConform);
			double conformity = (foundEntry.getSamplesConform() / foundEntry.getTotalNrOfSamples() * 100);
			foundEntry.setConformity(conformity);
		}

		// compute and set the currency
		double currency = newEntry.getAverageCurrency();
		if (currency > 0) {
			double foundAvgCurrency = foundEntry.getAverageCurrency();
			long foundAvgCurrencySamples = foundEntry.getAgeSamples();
			double newCurrencyAge = (foundAvgCurrencySamples * foundAvgCurrency + currency) / (foundAvgCurrencySamples + 1);
			foundEntry.setAverageCurrency(newCurrencyAge);
		}
		foundEntry.setTotalNrOfSamples(totalNrOfSamples + 1);
		return foundEntry;
	}

	public static MonitoredQoS computeNewValues(MonitoredQoS foundEntry, MonitoredQoS newEntry) {
		Integer expectedFrequency = foundEntry.getExpectedFrequency();
		Date firstMessageReceival = DefaultDateProvider.getDateFromTimeStamp(foundEntry.getFirstMessageReceival());
		Date currentMessageReceival = DefaultDateProvider.getDateFromTimeStamp(newEntry.getLastMessageReceival());
		foundEntry.setLastMessageReceival(DefaultDateProvider.getCurrentTimeStamp(currentMessageReceival));

		long timeDiffInMs = currentMessageReceival.getTime() - firstMessageReceival.getTime();
		long expectedNrOfSamples = (long) (timeDiffInMs / expectedFrequency.longValue());
		foundEntry.setExpectedNrOfSamples(expectedNrOfSamples);

		long totalNrOfSamples = foundEntry.getTotalNrOfSamples();
		foundEntry.setTotalNrOfSamples(totalNrOfSamples + 1);

		double availability = (double) ((double) totalNrOfSamples / (double) expectedNrOfSamples * 100);
		// this is a fail-safe, in case more messages than necessary get
		// transmitted.
		if (availability > 100) {
			availability = 100;
		}
		foundEntry.setAvailability(availability);

		return foundEntry;
	}

}
