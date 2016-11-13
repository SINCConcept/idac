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
package ac.at.tuwien.mt.model.thing;

import java.io.Serializable;

/**
 * 
 * Each thing produces messages. The messages can represent anything, but at the
 * bottom level they are just strings. This class represents such a message.
 * 
 * @author Florin Bogdan Balint
 *
 */
@SuppressWarnings("serial")
public class ThingMessage implements Serializable {

	// the thing which produced the message
	private final Thing thing;
	// the message that was produced
	private final String message;

	// the time the message was received
	private final String inputTime;

	// the time the message was sent
	private String deliveryTime;

	public ThingMessage(Thing thing, String message, String inputTime) {
		super();
		this.thing = thing;
		this.message = message;
		this.inputTime = inputTime;
	}

	public ThingMessage(Thing thing, String message, String inputTime, String deliveryTime) {
		super();
		this.thing = thing;
		this.message = message;
		this.inputTime = inputTime;
		this.deliveryTime = deliveryTime;
	}

	/**
	 * @return the thing
	 */
	public Thing getThing() {
		return thing;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @return the inputTime
	 */
	public String getInputTime() {
		return inputTime;
	}

	/**
	 * @return the deliveryTime
	 */
	public String getDeliveryTime() {
		return deliveryTime;
	}

	/**
	 * @param deliveryTime
	 *            the deliveryTime to set
	 */
	public void setDeliveryTime(String deliveryTime) {
		this.deliveryTime = deliveryTime;
	}

}
