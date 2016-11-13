package ac.at.tuwien.mt.model.helper;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;

public class PasswordEncryptionProviderTest {

	private static final Logger LOGGER = LogManager.getLogger(PasswordEncryptionProviderTest.class);

	@Test
	public void testUniquePasswordEncryption() {
		try {
			// Test if the password encryption is unique (because of the salt)
			// for each password.
			String p1 = DefaultPasswordEncryptionProvider.encryptPassword("test213");
			String p2 = DefaultPasswordEncryptionProvider.encryptPassword("test213");
			Assert.assertNotEquals(p1, p2);
		} catch (Exception e) {
			// In case of any errors this test should fail
			LOGGER.error(e.getMessage(), e);
			Assert.fail();
		}
	}

	/**
	 * Test if an already encrypted password with salt from the database can be
	 * encrypted again.
	 */
	@Test
	public void testPasswordCorrectness() {
		try {
			// Test if the password encryption is unique for each password.
			String p1 = DefaultPasswordEncryptionProvider.encryptPassword("test213");
			boolean passwordCorrect = DefaultPasswordEncryptionProvider.isPasswordCorrect(p1, "test213");
			Assert.assertTrue(passwordCorrect);
		} catch (Exception e) {
			// In case of any errors this test should fail
			LOGGER.error(e.getMessage(), e);
			Assert.fail();
		}
	}
}
