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
package ac.at.tuwien.mt.client.internal;

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

/**
 * @author Florin Bogdan Balint
 *
 */
public class CommandLineValues {

	private static final Logger LOGGER = LogManager.getLogger(CommandLineValues.class);

	@Option(name = "-m", aliases = { "--mode" }, required = true, usage = "mode")
	private Mode mode;

	@Option(name = "-u", aliases = { "--email" }, required = true, usage = "user email")
	private String email;

	@Option(name = "-p", aliases = { "--password" }, required = true, usage = "user password")
	private String password;

	@Option(name = "-f", aliases = { "--inputFile" }, required = false, usage = "input file")
	private File inputFile;

	@Option(name = "-d", aliases = { "--inputDir" }, required = false, usage = "input directory")
	private File inputDir;

	@Option(name = "-a", aliases = { "--amount" }, required = false, usage = "amount of multiplication")
	private Integer amount;

	private boolean errorFree = false;

	public CommandLineValues(String... args) {
		CmdLineParser parser = new CmdLineParser(this);
		try {
			parser.parseArgument(args);
			if (mode == Mode.REGISTER_THING) {
				if (inputFile == null) {
					errorFree = false;
					LOGGER.error("Cannot register thing without a proper JSON input file.");
				} else {
					if (inputFile.isDirectory()) {
						errorFree = false;
						LOGGER.error("Cannot register thing without a proper JSON input file.");
					}
				}
			}
			if (mode == Mode.REGISTER_THING_DIR) {
				if (inputDir == null) {
					errorFree = false;
					LOGGER.error("Cannot register thing without a proper input directory.");
				} else {
					if (!inputDir.isDirectory()) {
						errorFree = false;
						LOGGER.error("Cannot register thing without a proper input directory.");
					}
				}
			}
			errorFree = true;
		} catch (CmdLineException e) {
			LOGGER.error(e);
			parser.printUsage(System.err);
		}
	}

	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @param email
	 *            the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password
	 *            the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @return the mode
	 */
	public Mode getMode() {
		return mode;
	}

	/**
	 * @param mode
	 *            the mode to set
	 */
	public void setMode(Mode mode) {
		this.mode = mode;
	}

	/**
	 * Returns whether the parameters could be parsed without an error.
	 *
	 * @return true if no error occurred.
	 */
	public boolean isErrorFree() {
		return errorFree;
	}

	/**
	 * @return the inputFile
	 */
	public File getInputFile() {
		return inputFile;
	}

	/**
	 * @param inputFile
	 *            the inputFile to set
	 */
	public void setInputFile(File inputFile) {
		this.inputFile = inputFile;
	}

	/**
	 * @return the inputDir
	 */
	public File getInputDir() {
		return inputDir;
	}

	/**
	 * @param inputDir
	 *            the inputDir to set
	 */
	public void setInputDir(File inputDir) {
		this.inputDir = inputDir;
	}

	/**
	 * @return the amount
	 */
	public Integer getAmount() {
		return amount;
	}

	/**
	 * @param amount
	 *            the amount to set
	 */
	public void setAmount(Integer amount) {
		this.amount = amount;
	}

}
