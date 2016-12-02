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
package ac.at.tuwien.mt.common.test.sample;

import java.util.Calendar;
import java.util.GregorianCalendar;

import ac.at.tuwien.mt.model.datacontract.ClausesTrailEntry;
import ac.at.tuwien.mt.model.datacontract.DataContract;
import ac.at.tuwien.mt.model.datacontract.DataContractTrail;
import ac.at.tuwien.mt.model.datacontract.clause.ControlAndRelationship;
import ac.at.tuwien.mt.model.datacontract.clause.Currency;
import ac.at.tuwien.mt.model.datacontract.clause.DataRights;
import ac.at.tuwien.mt.model.datacontract.clause.PricingModel;
import ac.at.tuwien.mt.model.datacontract.clause.PurchasingPolicy;
import ac.at.tuwien.mt.model.datacontract.clause.QoD;
import ac.at.tuwien.mt.model.datacontract.clause.Subscription;
import ac.at.tuwien.mt.model.helper.DefaultIDGenerator;
import ac.at.tuwien.mt.model.thing.Thing;
import ac.at.tuwien.mt.model.thing.ThingId;
import ac.at.tuwien.mt.model.thing.rating.Rating;

/**
 * @author Florin Bogdan Balint
 *
 */
public final class SampleData {

	private SampleData() {
		// defeat instantiation for utility class
	}

	public static DataContract getSampleDataContract() {
		DataContract dataContract = new DataContract();
		dataContract.getDataContractMetaInfo().setContractId("123456");
		dataContract.getDataContractMetaInfo().setParty1Id("party1Id");
		dataContract.getDataContractMetaInfo().setParty2Id("party2Id");
		dataContract.getDataContractMetaInfo().setParty1Accepted(false);
		dataContract.getDataContractMetaInfo().setParty2Accepted(false);
		dataContract.getThingIds().add(new ThingId(getSampleThing().getThingId()));
		dataContract.getDataContractMetaInfo().setActive(false);
		dataContract.setControlAndRelationship(getSampleClausesEntry().getControlAndRelationship());
		dataContract.getDataContractMetaInfo().setCreationDate(new GregorianCalendar(2016, 8, 3).getTime());
		dataContract.setDataRights(getSampleClausesEntry().getDataRights());
		dataContract.setPricingModel(getSampleClausesEntry().getPricingModel());
		dataContract.setPurchasingPolicy(getSampleClausesEntry().getPurchasingPolicy());
		return dataContract;
	}

	public static DataContractTrail getSampleDataContractTrail() {
		DataContractTrail trail = new DataContractTrail();
		trail.setContractId("123456");
		ClausesTrailEntry entry = getSampleClausesEntry();
		trail.getClausesTrail().add(entry);
		return trail;
	}

	public static ClausesTrailEntry getSampleClausesEntry() {
		ClausesTrailEntry entry = new ClausesTrailEntry();
		ControlAndRelationship controlAndRelationship = new ControlAndRelationship();
		controlAndRelationship.setIndemnity("Sample clause");
		controlAndRelationship.setJuristiction("Sample clause");
		controlAndRelationship.setLiability("Sample clause");
		controlAndRelationship.setWarranty("Sample clause");
		entry.setControlAndRelationship(controlAndRelationship);

		DataRights dataRights = new DataRights();
		dataRights.setCollection(true);
		dataRights.setCommercialUsage(true);
		dataRights.setDerivation(true);
		dataRights.setReproduction(true);
		entry.setDataRights(dataRights);

		QoD qod = new QoD();
		qod.setAccuracy(90.0);
		qod.setCompleteness(90.0);
		qod.setConformity(90.0);
		qod.setConsistency(90.0);
		qod.setCurrency(90.0);
		qod.setTimeliness(90.0);

		PricingModel pricingModel = new PricingModel();
		pricingModel.setCurrency(Currency.EUR);
		pricingModel.setPrice(100.00);
		Subscription subscription = new Subscription();
		subscription.setStartDate(Calendar.getInstance().getTime());
		subscription.setEndDate(Calendar.getInstance().getTime());
		pricingModel.setSubscription(subscription);
		entry.setPricingModel(pricingModel);

		PurchasingPolicy purchasingPolicy = new PurchasingPolicy();
		purchasingPolicy.setContractTermination("Sample clause");
		purchasingPolicy.setRefund("Sample clause");
		purchasingPolicy.setShipping("Sample clause");
		entry.setPurchasingPolicy(purchasingPolicy);
		return entry;
	}

	public static Thing getSampleThing() {
		Thing thing = new Thing();
		thing.setResourceId("resource123");
		thing.setOwnerId("owner123");
		thing.setDataSample("{'temp','21°C'}");
		thing.setThingId(DefaultIDGenerator.generateID());

		DataRights dataRights = new DataRights();
		dataRights.setCollection(true);
		dataRights.setCommercialUsage(true);
		dataRights.setDerivation(true);
		dataRights.setReproduction(true);
		thing.setDataRights(dataRights);

		QoD qod = new QoD();
		qod.setAccuracy(new Double(0.9));
		qod.setCompleteness(new Double(0.9));
		qod.setConsistency(new Double(0.9));
		qod.setCurrency(new Double(0.9));
		qod.setTimeliness(new Double(0.9));
		thing.setQod(qod);

		PricingModel pricingModel = new PricingModel();
		pricingModel.setCurrency(Currency.EUR);
		pricingModel.setPrice(new Double(0.01));
		pricingModel.setNumberOfTransactions(1);
		pricingModel.setTransaction(true);
		thing.setPricingModel(pricingModel);

		Rating r1 = new Rating("user123", 5);
		Rating r2 = new Rating("user124", 5);

		thing.getRatings().add(r1);
		thing.getRatings().add(r2);

		return thing;
	}

}
