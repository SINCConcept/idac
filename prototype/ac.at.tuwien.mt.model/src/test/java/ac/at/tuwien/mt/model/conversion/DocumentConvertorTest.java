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
package ac.at.tuwien.mt.model.conversion;

import java.util.Calendar;
import java.util.GregorianCalendar;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.junit.Assert;
import org.junit.Test;

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
import ac.at.tuwien.mt.model.microservice.MicroserviceInfo;
import ac.at.tuwien.mt.model.microservice.MicroserviceType;
import ac.at.tuwien.mt.model.person.Address;
import ac.at.tuwien.mt.model.person.NaturalPerson;
import ac.at.tuwien.mt.model.thing.Thing;
import ac.at.tuwien.mt.model.thing.ThingId;

/**
 * @author Florin Bogdan Balint
 *
 */
public class DocumentConvertorTest {

	private static final Logger LOGGER = LogManager.getLogger(DocumentConvertorTest.class);

	public static void main(String args[]) {
		DataContractTrail dct = new DataContractTrail();
		dct.setContractId("c123");
		dct.setRevision(1);

		ClausesTrailEntry ce1 = new ClausesTrailEntry();
		DataRights dataRights1 = new DataRights();
		dataRights1.setCollection(true);
		dataRights1.setCommercialUsage(true);
		dataRights1.setDerivation(true);
		dataRights1.setReproduction(true);
		ce1.setDataRights(dataRights1);

		PricingModel pricingModel = new PricingModel();
		pricingModel.setCurrency(Currency.EUR);
		pricingModel.setPrice(new Double(3));
		pricingModel.setNumberOfTransactions(0);
		pricingModel.setTransaction(false);
		Subscription sub = new Subscription();
		sub.setBrokerURL("tcp://127.0.0.1:61616");
		sub.setQueueName("customQueue");
		sub.setStartDate(new GregorianCalendar(2016, 10, 1, 0, 0, 0).getTime());
		sub.setEndDate(new GregorianCalendar(2016, 10, 31, 23, 59, 59).getTime());
		pricingModel.setSubscription(sub);
		ce1.setPricingModel(pricingModel);

		PurchasingPolicy purchasingPolicy = new PurchasingPolicy();
		purchasingPolicy.setContractTermination("Automatic");
		purchasingPolicy.setRefund("Full");
		purchasingPolicy.setShipping("Automatic");
		ce1.setPurchasingPolicy(purchasingPolicy);

		ControlAndRelationship controlAndRelationship = new ControlAndRelationship();
		controlAndRelationship.setIndemnity("None");
		controlAndRelationship.setJuristiction("Austria/Graz");
		controlAndRelationship.setLiability("None");
		controlAndRelationship.setWarranty("2 years");
		ce1.setControlAndRelationship(controlAndRelationship);

		ClausesTrailEntry ce2 = new ClausesTrailEntry();
		DataRights dataRights2 = new DataRights();
		dataRights2.setCollection(true);
		dataRights2.setCommercialUsage(true);
		dataRights2.setDerivation(false);
		dataRights2.setReproduction(false);
		ce2.setDataRights(dataRights2);

		PricingModel pricingModel2 = new PricingModel();
		pricingModel2.setCurrency(Currency.EUR);
		pricingModel2.setPrice(new Double(10));
		pricingModel2.setNumberOfTransactions(0);
		pricingModel2.setTransaction(false);
		pricingModel2.setSubscription(sub);
		ce2.setPricingModel(pricingModel2);

		PurchasingPolicy purchasingPolicy2 = new PurchasingPolicy();
		purchasingPolicy2.setContractTermination("Automatic");
		purchasingPolicy2.setRefund("None");
		purchasingPolicy2.setShipping("Automatic");
		ce2.setPurchasingPolicy(purchasingPolicy2);

		ControlAndRelationship controlAndRelationship2 = new ControlAndRelationship();
		controlAndRelationship2.setIndemnity("None");
		controlAndRelationship2.setJuristiction("Austria/Vienna");
		controlAndRelationship2.setLiability("None");
		controlAndRelationship2.setWarranty("None");
		ce2.setControlAndRelationship(controlAndRelationship2);

		dct.getClausesTrail().add(ce1);
		dct.getClausesTrail().add(ce2);

		// System.out.println(dct.getDocument().toJson());

		MicroserviceInfo msi = new MicroserviceInfo();
		msi.setDescription("Monitoring microservice");
		msi.setHost("127.0.0.1");
		msi.setPort(12780);
		msi.setProtocol("http");
		msi.setMicroserviceType(MicroserviceType.MONITORING);
		msi.setPath("rest/monitoring/");

		System.out.println(msi.getDocument().toJson());

	}

	@Test
	public void testThing() {
		Thing thing1 = getSampleThing();

		String json1 = thing1.getDocument().toJson();
		LOGGER.debug(json1);

		String json2 = new Thing(Document.parse(json1)).getDocument().toJson();
		LOGGER.debug(json2);
		Assert.assertEquals(json1, json2);
	}

	private Thing getSampleThing() {
		Thing device1 = new Thing();
		device1.setResourceId("resource123");
		device1.setOwnerId("owner123");
		device1.setDataSample("{'temp','21°C'}");

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

	@Test
	public void testConversionIsAlwaysEqual() {
		NaturalPerson natPerson = new NaturalPerson();
		natPerson.setEmail("test@test.com");
		natPerson.setPassword("123password");
		natPerson.setFirstName("Florin");
		natPerson.setLastName("Test");
		natPerson.setPersonId("1988");
		natPerson.setPersonId("myPersonID1234");
		natPerson.setRevision(1);
		natPerson.setBirthDate(Calendar.getInstance().getTime());

		Address address = new Address();
		address.setCity("Timisoara");
		address.setCountry("23");

		natPerson.setAddress(address);

		Document document = natPerson.getDocument();
		String json1 = document.toJson();
		LOGGER.debug(json1);

		NaturalPerson naturalPerson = new NaturalPerson(document);
		Document document2 = naturalPerson.getDocument();
		String json2 = document2.toJson();

		Assert.assertEquals(json1, json2);
	}

	@Test
	public void testConversionModel() {
		NaturalPerson natPerson = getSampleNatPerson();

		Document document = natPerson.getDocument();
		String json1 = document.toJson();
		LOGGER.debug(json1);

		Document personAsDocument = Document.parse(json1);
		NaturalPerson natPerson2 = new NaturalPerson(personAsDocument);
		Document document2 = natPerson2.getDocument();
		String json2 = document2.toJson();
		LOGGER.debug(json2);
		Assert.assertEquals(json1, json2);
	}

	@Test
	public void testConversionDataContract() {
		DataContract dc = new DataContract();
		Thing thing = getSampleThing();
		dc.getThingIds().add(new ThingId(thing.getThingId()));

		dc.getDataContractMetaInfo().setParty1Id("123");
		dc.getDataContractMetaInfo().setParty2Id("123");
		dc.getDataContractMetaInfo().setActive(true);
		dc.setDataRights(thing.getDataRights());
		dc.setPricingModel(thing.getPricingModel());
		dc.setControlAndRelationship(thing.getControlAndRelationship());
		dc.setPurchasingPolicy(thing.getPurchasingPolicy());
		dc.getThingIds().add(new ThingId(thing.getThingId()));

		ClausesTrailEntry ce = new ClausesTrailEntry();
		ce.setControlAndRelationship(dc.getControlAndRelationship());
		ce.setDataRights(dc.getDataRights());
		ce.setPricingModel(dc.getPricingModel());
		ce.setPurchasingPolicy(dc.getPurchasingPolicy());

		Document document = dc.getDocument();
		String json1 = document.toJson();
		LOGGER.debug(json1);
	}

	private NaturalPerson getSampleNatPerson() {
		NaturalPerson natPerson = new NaturalPerson();
		natPerson.setEmail("test@test.com");
		natPerson.setPassword("123password");
		natPerson.setFirstName("Florin");
		natPerson.setLastName("Test");
		natPerson.setPersonId("1988");
		natPerson.setPersonId("myPersonID1234");
		natPerson.setRevision(1);
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, 1988);
		cal.set(Calendar.MONTH, 0);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		natPerson.setBirthDate(cal.getTime());

		Address address = new Address();
		address.setCity("Vienna");
		address.setCountry("23");
		address.setZipCode("1050");
		address.setStreet("Mustergasse");
		address.setNumber("23");
		natPerson.setAddress(address);
		return natPerson;
	}
}
