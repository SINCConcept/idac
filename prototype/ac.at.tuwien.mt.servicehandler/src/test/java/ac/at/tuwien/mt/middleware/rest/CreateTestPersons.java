package ac.at.tuwien.mt.middleware.rest;

import java.util.Calendar;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import ac.at.tuwien.mt.middleware.BasicCamelStarter;
import ac.at.tuwien.mt.model.helper.DefaultJSONProvider;
import ac.at.tuwien.mt.model.person.Address;
import ac.at.tuwien.mt.model.person.LegalPerson;
import ac.at.tuwien.mt.model.person.NaturalPerson;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class CreateTestPersons {

	private static final Logger LOGGER = LogManager.getLogger(CreateTestPersons.class);

	private static BasicCamelStarter camelStarter = new BasicCamelStarter();

	private static final String HTTP_SERVER = "http://localhost:12760";
	private static NaturalPerson natPerson = getTestNatural();
	private static LegalPerson legalPerson = getTestLegal();

	@BeforeClass
	public static void setUp() {
		camelStarter.start();
		try {
			Thread.sleep(15000);
		} catch (InterruptedException e) {
			LOGGER.error(e, e.getCause());
		}
	}

	@AfterClass
	public static void tearDown() {
		camelStarter.cancel();
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			LOGGER.error(e, e.getCause());
		}
	}

	@Test
	public void testRegisterNatPerson() {
		Client client = ClientBuilder.newClient();
		WebTarget target = client.target(HTTP_SERVER).path("services/rest/persons/natural/register");
		Entity<String> entity = Entity.entity(DefaultJSONProvider.getObjectAsJson(natPerson), MediaType.APPLICATION_JSON);
		Response response = target.request(MediaType.APPLICATION_JSON_TYPE).put(entity);

		int status = response.getStatus();
		Assert.assertNotEquals(500, status);
		response.close();
	}

	@Test
	public void testRegisterLegalPerson() {
		Client client = ClientBuilder.newClient();
		WebTarget target = client.target(HTTP_SERVER).path("services/rest/persons/legal/register");
		Entity<String> entity = Entity.entity(legalPerson.getDocument().toJson(), MediaType.APPLICATION_JSON);
		Response response = target.request(MediaType.APPLICATION_JSON_TYPE).put(entity);

		int status = response.getStatus();
		Assert.assertNotEquals(500, status);
		response.close();
	}

	private static NaturalPerson getTestNatural() {
		natPerson = new NaturalPerson();
		natPerson.setFirstName("Florin");
		natPerson.setLastName("Balint");
		natPerson.setPassword("balint.florin@gmail.com");
		natPerson.setEmail("balint.florin@gmail.com");
		natPerson.setBirthDate(Calendar.getInstance().getTime());
		Address address = new Address();
		address.setCity("Vienna");
		address.setCountry("Austria");
		address.setNumber("27");
		address.setStreet("Spengergasse");
		address.setZipCode("1050");
		natPerson.setAddress(address);
		return natPerson;
	}

	private static LegalPerson getTestLegal() {
		legalPerson = new LegalPerson();
		legalPerson.setCompanyName("Test Industries");
		legalPerson.setPassword("e0725439@student.tuwien.ac.at");
		legalPerson.setEmail("e0725439@student.tuwien.ac.at");
		legalPerson.setRegistrationNumber("FN 1234");
		Address address = new Address();
		address.setCity("Vienna");
		address.setCountry("Austria");
		address.setNumber("27");
		address.setStreet("Spengergasse");
		address.setZipCode("1050");
		legalPerson.setAddress(address);
		return legalPerson;
	}
}
