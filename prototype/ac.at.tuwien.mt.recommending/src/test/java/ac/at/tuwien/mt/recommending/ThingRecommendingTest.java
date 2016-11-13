package ac.at.tuwien.mt.recommending;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ac.at.tuwien.mt.common.test.dao.DAOTest;
import ac.at.tuwien.mt.common.test.dao.MongoDBPropertiesProvider;
import ac.at.tuwien.mt.common.test.dao.MongoDBProperty;
import ac.at.tuwien.mt.common.test.sample.SampleData;
import ac.at.tuwien.mt.dao.datacontract.DataContractDAO;
import ac.at.tuwien.mt.dao.datacontract.DataContractTrailDAO;
import ac.at.tuwien.mt.dao.datacontract.impl.DataContractDAOImpl;
import ac.at.tuwien.mt.dao.datacontract.impl.DataContractTrailDAOImpl;
import ac.at.tuwien.mt.dao.thing.ThingDAO;
import ac.at.tuwien.mt.dao.thing.impl.ThingDAOImpl;
import ac.at.tuwien.mt.model.datacontract.DataContract;
import ac.at.tuwien.mt.model.datacontract.DataContractTrail;
import ac.at.tuwien.mt.model.thing.Thing;

public class ThingRecommendingTest extends DAOTest {

	private static final Logger LOGGER = LogManager.getLogger(ThingRecommendingTest.class);

	private static final String THING_COLLECTION = MongoDBPropertiesProvider.getString(MongoDBProperty.MONGO_DB_COLLECTION_THING);
	private static final String DC_COLLECTION = MongoDBPropertiesProvider.getString(MongoDBProperty.MONGO_DB_COLLECTION_DATACONTRACT);
	private static final String DC_TRAIL_COLLECTION = MongoDBPropertiesProvider.getString(MongoDBProperty.MONGO_DB_COLLECTION_DATACONTRACT_TRAIL);

	private static BasicCamelStarter camelStarter = new BasicCamelStarter();
	private static final String HTTP_SERVER = "http://localhost:12790";

	private ThingDAO thingDAO;
	private DataContractTrailDAO dctDAO;
	private DataContractDAO dcDAO;

	@BeforeClass
	public static void setUp() {
		camelStarter.start();
		try {
			Thread.sleep(10000);
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

	@Before
	public void before() {
		mongoClient.getDatabase(DB_NAME).getCollection(THING_COLLECTION).drop();
		mongoClient.getDatabase(DB_NAME).getCollection(DC_COLLECTION).drop();
		mongoClient.getDatabase(DB_NAME).getCollection(DC_TRAIL_COLLECTION).drop();

		thingDAO = new ThingDAOImpl(mongoClient, DB_NAME, THING_COLLECTION);
		dctDAO = new DataContractTrailDAOImpl(mongoClient, DB_NAME, DC_COLLECTION, DC_TRAIL_COLLECTION);
		dcDAO = new DataContractDAOImpl(mongoClient, DB_NAME, DC_COLLECTION);
	}

	@After
	public void after() {
		mongoClient.getDatabase(DB_NAME).getCollection(THING_COLLECTION).drop();
		mongoClient.getDatabase(DB_NAME).getCollection(DC_COLLECTION).drop();
		mongoClient.getDatabase(DB_NAME).getCollection(DC_TRAIL_COLLECTION).drop();
	}

	@Test
	public void recommendViaRESTTest2() {
		// insert some test data
		Thing thing = SampleData.getSampleThing();
		thingDAO.insert(thing);

		// try to access via REST
		Client client = ClientBuilder.newClient();
		WebTarget target = client.target(HTTP_SERVER).path("rest/recommending/recommend/someuserid");
		Response response = target.request(MediaType.APPLICATION_JSON_TYPE).get();

		// check the status and the response
		int status = response.getStatus();
		Assert.assertEquals(200, status);

		String responseAsString = response.readEntity(String.class);
		response.close();

		Document document = Document.parse(responseAsString);
		Thing thingToCheck = new Thing(document);
		Assert.assertEquals(thing.getResourceId(), thingToCheck.getResourceId());
	}

	@Test
	public void recommendViaRESTTest3() {
		// insert some test data
		Thing thing = SampleData.getSampleThing();
		thingDAO.insert(thing);

		// try to access via REST
		Client client = ClientBuilder.newClient();
		WebTarget target = client.target(HTTP_SERVER).path("rest/recommending/recommend/owner123");
		Response response = target.request(MediaType.APPLICATION_JSON_TYPE).get();

		// check the status and the response
		int status = response.getStatus();
		Assert.assertEquals(204, status);
	}

	@Test
	public void recommendViaRESTTest1() {
		Client client = ClientBuilder.newClient();
		WebTarget target = client.target(HTTP_SERVER).path("rest/recommending/recommend/someuserid");
		Response response = target.request(MediaType.APPLICATION_JSON_TYPE).get();

		int status = response.getStatus();
		Assert.assertEquals(204, status);
	}

	@Test
	public void avgThingNegViaRESTTest1() {
		Client client = ClientBuilder.newClient();
		WebTarget target = client.target(HTTP_SERVER).path("rest/recommending/thingavgneg/%20");
		Response response = target.request(MediaType.APPLICATION_JSON_TYPE).get();

		int status = response.getStatus();
		Assert.assertEquals(400, status);
	}

	@Test
	public void avgThingNegViaRESTTest2() {
		Client client = ClientBuilder.newClient();
		WebTarget target = client.target(HTTP_SERVER).path("rest/recommending/thingavgneg/t213");
		Response response = target.request(MediaType.APPLICATION_JSON_TYPE).get();

		int status = response.getStatus();
		Assert.assertEquals(204, status);
	}

	@Test
	public void avgThingNegViaRESTTest3() {
		// insert data
		try {
			DataContract dc = SampleData.getSampleDataContract();
			dcDAO.insert(dc);
			DataContractTrail dct = SampleData.getSampleDataContractTrail();
			dct.getClausesTrail().add(dct.getClausesTrail().get(0));
			dctDAO.insert(dct);

			String thingId = dc.getThingIds().get(0).getThingId();
			Client client = ClientBuilder.newClient();
			WebTarget target = client.target(HTTP_SERVER).path("rest/recommending/thingavgneg/" + thingId);
			Response response = target.request(MediaType.APPLICATION_JSON_TYPE).get();

			int status = response.getStatus();
			Assert.assertEquals(200, status);

			String responseAsString = response.readEntity(String.class);
			response.close();

			Assert.assertNotNull(responseAsString);
			Assert.assertEquals(2, Integer.parseInt(responseAsString));
		} catch (Exception e) {
			// not expected
			Assert.fail();
		}
	}

}
