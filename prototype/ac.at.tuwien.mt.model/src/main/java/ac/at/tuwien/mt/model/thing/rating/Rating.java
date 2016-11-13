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
package ac.at.tuwien.mt.model.thing.rating;

import java.io.Serializable;

import org.bson.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * @author Florin Bogdan Balint
 *
 */
@SuppressWarnings("serial")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Rating implements Serializable {

	/**
	 * The userId of the user who rated.
	 */
	private String userId;

	/**
	 * The rating.
	 */
	private Integer rating;

	public Rating() {
		// empty constructor
	}

	public Rating(String userId, Integer rating) {
		this.userId = userId;
		this.rating = rating;
	}

	public Rating(Document document) {
		this.userId = document.getString("userId");
		this.rating = document.getInteger("rating").intValue();
	}

	@JsonIgnore
	public Document getDocument() {
		Document document = new Document();
		document.put("userId", userId);
		document.put("rating", rating);
		return document;
	}

	/**
	 * @return the userId
	 */
	public String getUserId() {
		return userId;
	}

	/**
	 * @param userId
	 *            the userId to set
	 */
	public void setUserId(String userId) {
		this.userId = userId;
	}

	/**
	 * @return the rating
	 */
	public Integer getRating() {
		return rating;
	}

	/**
	 * @param rating
	 *            the rating to set
	 */
	public void setRating(Integer rating) {
		this.rating = rating;
	}

}
