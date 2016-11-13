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
import org.bson.Document;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import ac.at.tuwien.mt.middleware.BasicCamelStarter;
import ac.at.tuwien.mt.model.helper.DefaultJSONProvider;
import ac.at.tuwien.mt.model.person.Address;
import ac.at.tuwien.mt.model.person.NaturalPerson;
import ac.at.tuwien.mt.model.person.Person;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class NaturalPersonRestTest {

	private static final Logger LOGGER = LogManager.getLogger(CreateTestPersons.class);

	private static BasicCamelStarter camelStarter = new BasicCamelStarter();

	private static final String HTTP_SERVER = "http://localhost:12760";
	private static NaturalPerson naturalPerson = getTestNaturalPerson();

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
	public void test1RegisterOK() {
		Client client = ClientBuilder.newClient();
		WebTarget target = client.target(HTTP_SERVER).path("services/rest/persons/natural/register");
		Entity<String> entity = Entity.entity(DefaultJSONProvider.getObjectAsJson(naturalPerson), MediaType.APPLICATION_JSON);
		Response response = target.request(MediaType.APPLICATION_JSON_TYPE).put(entity);

		int status = response.getStatus();
		Assert.assertEquals(200, status);
		String personAsString = (String) response.readEntity(String.class);
		Person personResponse = new NaturalPerson(Document.parse(personAsString));
		naturalPerson.setPersonId(personResponse.getPersonId());
		naturalPerson.setRevision(personResponse.getRevision());
		response.close();
	}

	@Test
	public void test2RegisterNOK() {
		Client client = ClientBuilder.newClient();
		WebTarget target = client.target(HTTP_SERVER).path("services/rest/persons/natural/register");
		Entity<String> entity = Entity.entity(DefaultJSONProvider.getObjectAsJson(naturalPerson), MediaType.APPLICATION_JSON);
		Response response = target.request(MediaType.APPLICATION_JSON_TYPE).put(entity);

		int status = response.getStatus();
		Assert.assertEquals(409, status);
		response.close();
	}

	@Test
	public void test3AuthOK() {
		Client client = ClientBuilder.newClient();
		WebTarget target = client.target(HTTP_SERVER).path("services/rest/persons/natural/authenticate");
		Entity<String> entity = Entity.entity(DefaultJSONProvider.getObjectAsJson(naturalPerson), MediaType.APPLICATION_JSON);
		Response response = target.request(MediaType.APPLICATION_JSON_TYPE).post(entity);

		int status = response.getStatus();
		Assert.assertEquals(200, status);
		response.close();
	}

	@Test
	public void test4AuthNOK1() {
		naturalPerson.setPassword("xxx");
		Client client = ClientBuilder.newClient();
		WebTarget target = client.target(HTTP_SERVER).path("services/rest/persons/natural/authenticate");
		Entity<String> entity = Entity.entity(DefaultJSONProvider.getObjectAsJson(naturalPerson), MediaType.APPLICATION_JSON);
		Response response = target.request(MediaType.APPLICATION_JSON_TYPE).post(entity);

		int status = response.getStatus();
		Assert.assertEquals(401, status);
		response.close();
	}

	@Test
	public void test4AuthNOK2() {
		naturalPerson.setEmail(naturalPerson.getEmail() + "asdfg");
		Client client = ClientBuilder.newClient();
		WebTarget target = client.target(HTTP_SERVER).path("services/rest/persons/natural/authenticate");
		Entity<String> entity = Entity.entity(DefaultJSONProvider.getObjectAsJson(naturalPerson), MediaType.APPLICATION_JSON);
		Response response = target.request(MediaType.APPLICATION_JSON_TYPE).post(entity);

		int status = response.getStatus();
		Assert.assertEquals(404, status);
		response.close();
	}

	@Test
	public void test5Update() {
		naturalPerson.setFirstName("NewFirstName");
		naturalPerson.setEmail(naturalPerson.getEmail() + "asdfg");
		Client client = ClientBuilder.newClient();
		WebTarget target = client.target(HTTP_SERVER).path("services/rest/persons/natural/update");
		Entity<String> entity = Entity.entity(DefaultJSONProvider.getObjectAsJson(naturalPerson), MediaType.APPLICATION_JSON);
		Response response = target.request(MediaType.APPLICATION_JSON_TYPE).post(entity);

		int status = response.getStatus();
		Assert.assertEquals(200, status);
		response.close();
	}

	@Test
	public void test6DeleteNOK() {
		Client client = ClientBuilder.newClient();
		WebTarget target = client.target(HTTP_SERVER).path("services/rest/persons/natural/remove/" + naturalPerson.getPersonId() + "123");
		Response response = target.request(MediaType.APPLICATION_JSON_TYPE).delete();

		int status = response.getStatus();
		Assert.assertEquals(404, status);
		response.close();
	}

	@Test
	public void test7DeleteOK() {
		Client client = ClientBuilder.newClient();
		WebTarget target = client.target(HTTP_SERVER).path("services/rest/persons/natural/remove/" + naturalPerson.getPersonId());
		Response response = target.request(MediaType.APPLICATION_JSON_TYPE).delete();

		int status = response.getStatus();
		Assert.assertEquals(200, status);
		response.close();

		target = client.target(HTTP_SERVER).path("services/rest/persons/natural/remove/" + naturalPerson.getPersonId());
		response = target.request(MediaType.APPLICATION_JSON_TYPE).delete();

		status = response.getStatus();
		Assert.assertEquals(404, status);
		response.close();
	}

	private static NaturalPerson getTestNaturalPerson() {
		naturalPerson = new NaturalPerson();
		naturalPerson.setFirstName("FirstName");
		naturalPerson.setLastName("LastName");
		naturalPerson.setPassword("asdf");
		naturalPerson.setEmail("test1@test1.com");
		naturalPerson.setBirthDate(Calendar.getInstance().getTime());
		Address address = new Address();
		address.setCity("Vienna");
		address.setCountry("Austria");
		address.setNumber("27");
		address.setStreet("Spengergasse");
		address.setZipCode("1050");
		naturalPerson.setAddress(address);
		return naturalPerson;
	}
}
