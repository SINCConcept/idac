package ac.at.tuwien.mt.middleware;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class PingTest {

	private static final Logger LOGGER = LogManager.getLogger(PingTest.class);

	private static BasicCamelStarter camelStarter = new BasicCamelStarter();

	private static final String HTTP_SERVER = "http://localhost:12760";

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

	@Test
	public void pingTest() {
		Client client = ClientBuilder.newClient();
		WebTarget target = client.target(HTTP_SERVER).path("ping");
		Response response = target.request(MediaType.APPLICATION_JSON_TYPE).get();

		int status = response.getStatus();
		Assert.assertEquals(204, status);
	}

}
