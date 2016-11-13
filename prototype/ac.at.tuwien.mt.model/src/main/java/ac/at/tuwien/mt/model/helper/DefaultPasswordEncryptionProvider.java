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
package ac.at.tuwien.mt.model.helper;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;

import org.apache.commons.codec.binary.Base64;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 
 * Encrypts passwords using SHA-512 and a randomly generated salt for each
 * password.
 * 
 * @author Florin Bogdan Balint
 *
 */
public class DefaultPasswordEncryptionProvider {

	private static final Logger LOGGER = LogManager.getLogger(DefaultPasswordEncryptionProvider.class);
	private static final String SECURE_RANDOM_PROVIDER = "SUN";
	private static final String SECURE_RANDOM_ALG = "SHA1PRNG";
	private static final String ENCODING = "UTF-8";
	private static final String SHA_ALG = "SHA-512";

	/**
	 * Description: encrypts a password using the SHA-512 algorithm. For each
	 * password, a random salt is generated.
	 * 
	 * @param password
	 * @return encrypted password
	 * @throws NoSuchAlgorithmException
	 * @throws UnsupportedEncodingException
	 * @throws NoSuchProviderException
	 */
	public static String encryptPassword(String password) throws NoSuchAlgorithmException, UnsupportedEncodingException, NoSuchProviderException {
		LOGGER.debug("Encrypting password...");

		// generate a new salt for each password
		SecureRandom saltRandomizer = SecureRandom.getInstance(SECURE_RANDOM_ALG, SECURE_RANDOM_PROVIDER);
		// The same size as the output of SHA-512 (512 bits = 64 bytes)
		byte[] salt = new byte[64];
		saltRandomizer.nextBytes(salt);
		String encodedSalt = new String(Base64.encodeBase64(salt));

		// encrypt the password with the salt
		String toEncrypt = encodedSalt + password;
		String enrypted = enrypt(toEncrypt);

		String encryptedPasswordWithSalt = encodedSalt + ":" + enrypted;

		return encryptedPasswordWithSalt;
	}

	/**
	 * Description: Checks if a password equals to an already encrypted password
	 * (which already contains the salt).
	 * 
	 * @param encryptedPassword
	 * @param password
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws UnsupportedEncodingException
	 */
	public static boolean isPasswordCorrect(String encryptedPassword, String password) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		String[] params = encryptedPassword.split(":");
		String encodedSalt = params[0];
		String encodedPasswordWithSalt = params[1];
		String toEncrypt = encodedSalt + password;

		String passwordWithSaltToCheck = enrypt(toEncrypt);

		if (passwordWithSaltToCheck.equals(encodedPasswordWithSalt)) {
			return true;
		}

		return false;
	}

	/**
	 * Encrypts a string using the SHA-512 algorithm.
	 * 
	 * @param toEncrypt
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws UnsupportedEncodingException
	 */
	private static String enrypt(String toEncrypt) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		MessageDigest msgDigest = MessageDigest.getInstance(SHA_ALG);
		byte[] hash = msgDigest.digest(toEncrypt.getBytes(ENCODING));
		String passwordWithSaltToCheck = new String(Base64.encodeBase64(hash));
		LOGGER.debug("Password encrypted successfully: " + passwordWithSaltToCheck);
		return passwordWithSaltToCheck;
	}
}
