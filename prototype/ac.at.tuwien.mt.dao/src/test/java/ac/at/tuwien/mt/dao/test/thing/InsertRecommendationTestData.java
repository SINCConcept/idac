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
package ac.at.tuwien.mt.dao.test.thing;

import java.util.Random;

import org.junit.Test;

import ac.at.tuwien.mt.common.test.dao.DAOTest;
import ac.at.tuwien.mt.common.test.dao.MongoDBPropertiesProvider;
import ac.at.tuwien.mt.common.test.dao.MongoDBProperty;
import ac.at.tuwien.mt.dao.thing.ThingDAO;
import ac.at.tuwien.mt.dao.thing.impl.ThingDAOImpl;
import ac.at.tuwien.mt.model.datacontract.clause.Currency;
import ac.at.tuwien.mt.model.datacontract.clause.DataRights;
import ac.at.tuwien.mt.model.datacontract.clause.PricingModel;
import ac.at.tuwien.mt.model.datacontract.clause.QoD;
import ac.at.tuwien.mt.model.helper.DefaultIDGenerator;
import ac.at.tuwien.mt.model.thing.Thing;
import ac.at.tuwien.mt.model.thing.rating.Rating;

/**
 * @author Florin Bogdan Balint
 *
 */
public class InsertRecommendationTestData extends DAOTest {

	private static final String THING_COLLECTION = MongoDBPropertiesProvider.getString(MongoDBProperty.MONGO_DB_COLLECTION_THING);

	private ThingDAO thingDAO = new ThingDAOImpl(mongoClient, DB_NAME, THING_COLLECTION);;

	@Test
	public void inserTestData() {
		// no things present
		for (int i = 1; i <= 10000; i++) {
			Thing thing = getSampleThing();
			thing.setThingId(i + "");
			thing.setResourceId("resource" + i);

			for (int j = 1; j <= getRandomRatingsNr(); j++) {
				// add ratings
				thing.getRatings().add(new Rating(getRandomRatingsUser() + "", getRandomRating()));
			}

			System.out.println(i);

			// finally insert the thing
			thingDAO.insert(thing);
		}
	}

	private int getRandomRatingsUser() {
		Random random = new Random();
		int randomNum = random.nextInt(10000) + 1;
		return randomNum;
	}

	private int getRandomRatingsNr() {
		Random random = new Random();
		int randomNum = random.nextInt(1000) + 1;
		return randomNum;
	}

	private int getRandomRating() {
		Random random = new Random();
		int randomNum = random.nextInt(5) + 1;
		return randomNum;
	}

	private Thing getSampleThing() {
		Thing device1 = new Thing();
		device1.setStandardMonitoring(false);
		device1.setResourceId("resource123");
		device1.setOwnerId(getRandomRatingsUser() + "");
		device1.setDataSample("{'temp','21°C'}");
		device1.setThingId(DefaultIDGenerator.generateID());

		DataRights dataRights = new DataRights();
		dataRights.setCollection(true);
		dataRights.setCommercialUsage(true);
		dataRights.setDerivation(true);
		dataRights.setReproduction(true);
		device1.setDataRights(dataRights);

		QoD qod = new QoD();
		qod.setAccuracy(new Double(0.9));
		qod.setCompleteness(new Double(0.9));
		qod.setConsistency(new Double(0.9));
		qod.setCurrency(new Double(0.9));
		qod.setTimeliness(new Double(0.9));
		device1.setQod(qod);

		PricingModel pricingModel = new PricingModel();
		pricingModel.setCurrency(Currency.EUR);
		pricingModel.setPrice(new Double(0.01));
		pricingModel.setNumberOfTransactions(1);
		pricingModel.setTransaction(true);
		device1.setPricingModel(pricingModel);

		return device1;
	}
}
