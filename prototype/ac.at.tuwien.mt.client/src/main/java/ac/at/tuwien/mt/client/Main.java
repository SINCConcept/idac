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
package ac.at.tuwien.mt.client;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.GregorianCalendar;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

import ac.at.tuwien.mt.client.internal.CommandLineValues;
import ac.at.tuwien.mt.client.internal.DefaultCharsetProvider;
import ac.at.tuwien.mt.client.internal.Mode;
import ac.at.tuwien.mt.client.rest.RESTMicroserviceLocator;
import ac.at.tuwien.mt.client.rest.datacontract.RESTDataContractClient;
import ac.at.tuwien.mt.client.rest.monitoring.RESTMonitoringClient;
import ac.at.tuwien.mt.client.rest.person.RESTPersonClient;
import ac.at.tuwien.mt.model.datacontract.clause.ControlAndRelationship;
import ac.at.tuwien.mt.model.datacontract.clause.Currency;
import ac.at.tuwien.mt.model.datacontract.clause.DataRights;
import ac.at.tuwien.mt.model.datacontract.clause.PricingModel;
import ac.at.tuwien.mt.model.datacontract.clause.PurchasingPolicy;
import ac.at.tuwien.mt.model.datacontract.clause.QoD;
import ac.at.tuwien.mt.model.datacontract.clause.QoS;
import ac.at.tuwien.mt.model.datacontract.clause.Subscription;
import ac.at.tuwien.mt.model.exception.AuthenticationException;
import ac.at.tuwien.mt.model.exception.ObjectNotFoundException;
import ac.at.tuwien.mt.model.helper.DefaultIDGenerator;
import ac.at.tuwien.mt.model.helper.DefaultJSONProvider;
import ac.at.tuwien.mt.model.microservice.MicroserviceInfo;
import ac.at.tuwien.mt.model.microservice.MicroserviceType;
import ac.at.tuwien.mt.model.microservice.monitoring.QueueInfo;
import ac.at.tuwien.mt.model.person.Address;
import ac.at.tuwien.mt.model.person.LegalPerson;
import ac.at.tuwien.mt.model.person.NaturalPerson;
import ac.at.tuwien.mt.model.person.Person;
import ac.at.tuwien.mt.model.person.PersonType;
import ac.at.tuwien.mt.model.thing.Thing;
import ac.at.tuwien.mt.model.thing.message.Attribute;
import ac.at.tuwien.mt.model.thing.message.DataType;
import ac.at.tuwien.mt.model.thing.message.MetaModel;
import ac.at.tuwien.mt.model.thing.message.Property;

/**
 * @author Florin Bogdan Balint
 *
 */
public class Main {

	private static final Logger LOGGER = LogManager.getLogger(Main.class);

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		CommandLineValues values = new CommandLineValues(args);
		CmdLineParser parser = new CmdLineParser(values);

		try {
			parser.parseArgument(args);
		} catch (CmdLineException e) {
			System.exit(1);
		}

		Mode mode = values.getMode();
		switch (mode) {
		case AUTHENTICATE:
			LOGGER.info("Authentication started for: " + values.getEmail());
			try {
				Person person = RESTPersonClient.authenticate(values.getEmail(), values.getPassword());
				LOGGER.info("Authentication succeeded, your ID is: " + person.getPersonId());
			} catch (AuthenticationException | ObjectNotFoundException e) {
				LOGGER.info("Authentication failed.");
			}
			LOGGER.info("Authentication finished.");
			break;
		case REGISTER_THING_DIR:
			LOGGER.info("Authentication started for: " + values.getEmail());
			try {
				// authenticate the user
				Person person = RESTPersonClient.authenticate(values.getEmail(), values.getPassword());
				LOGGER.info("Authentication succeeded, your ID is: " + person.getPersonId());

				if (values.getInputDir() == null) {
					LOGGER.error("The inputDir is null - aborting!");
				}

				// get the directory path
				String absolutePath = values.getInputDir().getAbsolutePath();
				LOGGER.info("Reading things from directory: " + absolutePath);

				// walk through all the files
				Files.walk(Paths.get(absolutePath)).forEach(filePath -> {
					if (Files.isRegularFile(filePath)) {
						try {
							// read the thing
							String json = new String(Files.readAllBytes(filePath));
							Document doc = Document.parse(json);
							Thing thingToRegister = new Thing(doc);
							LOGGER.info("Thing read successfully!");

							File file = filePath.toFile();
							registerThingForUser(file, values.getAmount(), person, thingToRegister);
						} catch (IOException e) {
							LOGGER.error("Could not read json from file - Reading thing failed.");
						}
					}
				});

			} catch (IOException e) {
				LOGGER.error("Could not read json from file - Reading thing failed.");
			} catch (AuthenticationException | ObjectNotFoundException e) {
				LOGGER.error("Authentication failed.");
			}
			LOGGER.info("Authentication finished.");
			break;
		case REGISTER_THING:
			LOGGER.info("Authentication started for: " + values.getEmail());
			try {
				// authenticate the user
				Person person = RESTPersonClient.authenticate(values.getEmail(), values.getPassword());
				LOGGER.info("Authentication succeeded, your ID is: " + person.getPersonId());

				if (values.getInputFile() == null) {
					LOGGER.error("The inputFile is null - aborting!");
				}
				// read the thing
				String absolutePath = values.getInputFile().getAbsolutePath();
				LOGGER.info("Reading thing from file: " + absolutePath);
				Path filePath = Paths.get(absolutePath);
				String json = new String(Files.readAllBytes(filePath));
				Document doc = Document.parse(json);
				Thing thingToRegister = new Thing(doc);
				LOGGER.info("Thing read successfully!");

				registerThingForUser(values.getInputFile(), values.getAmount(), person, thingToRegister);
			} catch (IOException e) {
				LOGGER.error("Could not read json from file - Reading thing failed.");
			} catch (AuthenticationException | ObjectNotFoundException e) {
				LOGGER.error("Authentication failed.");
			}
			LOGGER.info("Authentication finished.");
			break;
		case GENERATE_THING_DEMO_JSON:
			LOGGER.info("Generating thing demo JSON.");
			Thing thing = getSampleThing();
			String objectAsJson = DefaultJSONProvider.getObjectAsJson(thing, false);
			LOGGER.info(objectAsJson);
			LOGGER.info("Generating demo JSON - finished.");
			break;
		case GENERATE_DEMO_JSON_NATURAL_PERSON:
			LOGGER.info("Generating natural person demo JSON.");
			NaturalPerson naturalPerson = getSampleNaturalPerson();
			String nPersonAsJson = DefaultJSONProvider.getObjectAsJson(naturalPerson, false);
			LOGGER.info(nPersonAsJson);
			LOGGER.info("Generating demo JSON - finished.");
			break;
		case GENERATE_DEMO_JSON_LEGAL_PERSON:
			LOGGER.info("Generating legal person demo JSON.");
			LegalPerson legalPerson = getSampleLegalPerson();
			String legalPersonAsJson = DefaultJSONProvider.getObjectAsJson(legalPerson, false);
			LOGGER.info(legalPersonAsJson);
			LOGGER.info("Generating demo JSON - finished.");
			break;
		default:
			break;
		}
	}

	private static void registerThingForUser(File inputFile, Integer amount, Person person, Thing thingToRegister) throws IOException {
		LOGGER.info("Registering thing...!");
		// make sure the amount is at least one
		if (amount == null) {
			amount = 1;
		}
		if (amount <= 0) {
			amount = 1;
		}

		for (int current = 0; current < amount; current++) {
			// set the ids
			thingToRegister.setOwnerId(person.getPersonId());
			thingToRegister.setThingId(DefaultIDGenerator.generateID());

			// set the monitoring info
			MicroserviceInfo monitoringMSInfo = RESTMicroserviceLocator.locateMicroservice(MicroserviceType.MONITORING);
			RESTMonitoringClient monitoringClient = new RESTMonitoringClient(monitoringMSInfo);
			QueueInfo microserviceQueueInfo = monitoringClient.getQueueInfo();

			QueueInfo queueInfoToSet = new QueueInfo();
			queueInfoToSet.setQueueName(thingToRegister.getThingId());
			queueInfoToSet.setBrokerURL(microserviceQueueInfo.getBrokerURL());
			thingToRegister.setQueueInfo(queueInfoToSet);

			// set the monitoring info
			MicroserviceInfo dataContractMSInfo = RESTMicroserviceLocator.locateMicroservice(MicroserviceType.DATACONTRACT);
			RESTDataContractClient dataContractClient = new RESTDataContractClient(dataContractMSInfo);
			Thing insertedThing = dataContractClient.insertThing(thingToRegister);

			// save the thing - so we can get the queue info
			String fileAbsPath = inputFile.getAbsolutePath();
			fileAbsPath = fileAbsPath.replace(".json", "_" + current + "_registered.json");
			File file = new File(fileAbsPath);
			FileUtils.writeStringToFile(file, insertedThing.getDocument().toJson(), DefaultCharsetProvider.CHARSET_UTF_8, false);

			LOGGER.info("Thing registered successfully!");
		}
	}

	private static NaturalPerson getSampleNaturalPerson() {
		NaturalPerson person = new NaturalPerson();
		person.setPersonType(PersonType.NATURAL);
		person.setEmail("john.doe@mail.com");
		person.setPassword("john.doe@mail.com");
		person.setFirstName("John");
		person.setLastName("Doe");
		person.setBirthDate(new GregorianCalendar(1980, 2, 3).getTime());
		person.setAddress(getSampleAddress());
		return person;
	}

	private static Address getSampleAddress() {
		Address address = new Address();
		address.setCity("Vienna");
		address.setCountry("Austria");
		address.setZipCode("1040");
		address.setStreet("Wiedner Hauptstraße");
		address.setNumber("27");
		return address;
	}

	private static LegalPerson getSampleLegalPerson() {
		LegalPerson person = new LegalPerson();
		person.setAddress(getSampleAddress());
		person.setPersonType(PersonType.LEGAL);
		person.setEmail("myholdings@myholdings.com");
		person.setPassword("myholdings@myholdings.com");
		person.setCompanyName("My Holdings Inc");
		person.setRegistrationNumber("123456x");
		return person;
	}

	private static Thing getSampleThing() {
		Thing thing = new Thing();
		thing.setResourceId("resource123");
		thing.setDataSample("{'temp','21°C'}");
		thing.setDescription("Temperature sensor.");
		QueueInfo qi = new QueueInfo();
		qi.setBrokerURL("tcp://127.0.0.1:61616");
		qi.setQueueName("123");
		thing.setQueueInfo(qi);

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

		QoS qos = new QoS();
		qos.setAvailability(98.55);
		qos.setFrequency(10000);
		thing.setQos(qos);

		PurchasingPolicy purchasingPolicy = new PurchasingPolicy();
		purchasingPolicy.setContractTermination("Sample clause...");
		purchasingPolicy.setRefund("Sample clause...");
		purchasingPolicy.setShipping("Sample clause...");
		thing.setPurchasingPolicy(purchasingPolicy);

		ControlAndRelationship cr = new ControlAndRelationship();
		cr.setIndemnity("Sample clause...");
		cr.setJuristiction("Sample clause...");
		cr.setLiability("Sample clause...");
		cr.setWarranty("Sample clause...");
		thing.setControlAndRelationship(cr);

		PricingModel pricingModel = new PricingModel();
		pricingModel.setCurrency(Currency.EUR);
		pricingModel.setPrice(new Double(0.01));

		Subscription subscription = new Subscription();
		subscription.setBrokerURL("");
		subscription.setQueueName("resultQueue");
		subscription.setStartDate(new GregorianCalendar(2016, 8, 3).getTime());
		subscription.setEndDate(new GregorianCalendar(2016, 8, 3).getTime());
		pricingModel.setSubscription(subscription);

		pricingModel.setTransaction(false);
		thing.setPricingModel(pricingModel);
		thing.setStandardMonitoring(true);

		thing.setMetaModel(getTestMetaModel());
		return thing;
	}

	private static MetaModel getTestMetaModel() {
		Attribute attribute1 = new Attribute();
		attribute1.setDataType(DataType.STRING);
		attribute1.setName("thingId");
		Property property = new Property();
		property.setIdentifier(true);
		attribute1.setProperty(property);

		Attribute attribute2 = new Attribute();
		attribute2.setDataType(DataType.DOUBLE);
		attribute2.setName("temperature");

		Attribute attribute3 = new Attribute();
		attribute3.setDataType(DataType.STRING);
		attribute3.setName("scale");

		Attribute attribute4 = new Attribute();
		attribute4.setDataType(DataType.DATE);
		attribute4.setName("time");
		Property a4Property = new Property();
		a4Property.setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
		attribute4.setProperty(a4Property);

		MetaModel model1 = new MetaModel();
		model1.getAttributes().add(attribute1);
		model1.getAttributes().add(attribute2);
		model1.getAttributes().add(attribute3);
		model1.getAttributes().add(attribute4);
		return model1;
	}

}
