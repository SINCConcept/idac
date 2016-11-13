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
package ac.at.tuwien.mt.model.thing.message;

import java.io.Serializable;

/**
 * This enumeration represents a list of the supported data types. Used for the
 * meta-modeling.
 * 
 * @author Florin Bogdan Balint
 *
 */
public enum DataType implements Serializable {

	/**
	 * String data type. E.g., "Lorem ipsum..."
	 */
	STRING("STRING"), //
	/**
	 * Boolean data type, E.g., TRUE, FALSE
	 */
	BOOLEAN("BOOLEAN"), //
	/**
	 * DATE data type, E.g., Fri Jul 01 00:41:29 CEST 2016
	 */
	DATE("DATE"), //
	/**
	 * INTEGER data type, E.g., 1234
	 */
	INTEGER("INTEGER"), //
	/**
	 * DOUBLE data type, E.g., 1234.56
	 */
	DOUBLE("DOUBLE"), //
	/**
	 * ATTRIBUTE data type, which means this attribute contains further
	 * attributes.
	 */
	ATTRIBUTE("ATTRIBUTE"); //

	private String property;

	private DataType(String property) {
		this.property = property;
	}

	/**
	 * @return the property
	 */
	public String getProperty() {
		return property;
	}

	/**
	 * @param property
	 *            the property to set
	 */
	public void setProperty(String property) {
		this.property = property;
	}
}
