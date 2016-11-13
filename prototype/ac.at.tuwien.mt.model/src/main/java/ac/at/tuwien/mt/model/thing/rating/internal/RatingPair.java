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
package ac.at.tuwien.mt.model.thing.rating.internal;

import java.io.Serializable;

import ac.at.tuwien.mt.model.thing.rating.Rating;

/**
 * @author Florin Bogdan Balint
 *
 */
@SuppressWarnings("serial")
public class RatingPair implements Serializable {

	private Rating rating1;
	private Rating rating2;

	public RatingPair(Rating rating1, Rating rating2) {
		this.rating1 = rating1;
		this.rating2 = rating2;
	}

	/**
	 * @return the rating1
	 */
	public Rating getRating1() {
		return rating1;
	}

	/**
	 * @param rating1
	 *            the rating1 to set
	 */
	public void setRating1(Rating rating1) {
		this.rating1 = rating1;
	}

	/**
	 * @return the rating2
	 */
	public Rating getRating2() {
		return rating2;
	}

	/**
	 * @param rating2
	 *            the rating2 to set
	 */
	public void setRating2(Rating rating2) {
		this.rating2 = rating2;
	}

}
