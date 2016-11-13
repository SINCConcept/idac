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
package ac.at.tuwien.mt.model.microservice;

/**
 * ENUM representing the supported types of microservice. <br/>
 * <br/>
 * Currently supported:
 * <ul>
 * <li><b>DATACONTRACT</b></li>
 * <li><b>MONITORING</b></li>
 * <li><b>RECOMMEDING</b></li>
 * <li><b>CUSTOM</b></li>
 * </ul>
 * <br/>
 * 
 * @author Florin Bogdan Balint
 *
 */
public enum MicroserviceType {

	DATACONTRACT("datacontract"), //
	MONITORING("monitoring"), //
	RECOMMENDING("recommending"), //
	CUSTOM("custom");

	private String property;

	private MicroserviceType(String property) {
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
