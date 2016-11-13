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
package ac.at.tuwien.mt.model.datacontract.clause;

import java.io.Serializable;

import org.bson.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author Florin Bogdan Balint
 *
 */
@SuppressWarnings("serial")
public class PricingModel implements Serializable {

	// pricing
	private Double price;
	private Currency currency;

	// in case of transactional contract
	private Boolean transaction;
	private Integer numberOfTransactions;

	// in case of a subscription
	private Subscription subscription;

	public PricingModel() {
		this.subscription = new Subscription();
	}

	public PricingModel(Document document) {
		if (document != null) {
			this.price = document.getDouble("price");
			String currencyAsString = document.getString("currency");
			if (currencyAsString != null) {
				this.currency = Currency.valueOf(currencyAsString);
			}
			this.transaction = document.getBoolean("transaction");
			this.numberOfTransactions = document.getInteger("numberOfTransactions");
			Object subscriptionAsObject = document.get("subscription");
			if (subscriptionAsObject instanceof Document) {
				Document subscriptionAsDoc = (Document) subscriptionAsObject;
				this.subscription = new Subscription(subscriptionAsDoc);
			}
		}
	}

	@JsonIgnore
	public Document getDocument() {
		Document document = new Document();
		document.put("price", getPrice());
		if (getCurrency() != null) {
			document.put("currency", getCurrency().getProperty());
		}
		document.put("transaction", getTransaction());
		document.put("numberOfTransactions", getNumberOfTransactions());
		if (subscription != null) {
			document.put("subscription", subscription.getDocument());
		}
		return document;
	}

	/**
	 * @return the price
	 */
	public Double getPrice() {
		return price;
	}

	/**
	 * @param price
	 *            the price to set
	 */
	public void setPrice(Double price) {
		this.price = price;
	}

	/**
	 * @return the currency
	 */
	public Currency getCurrency() {
		return currency;
	}

	/**
	 * @param currency
	 *            the currency to set
	 */
	public void setCurrency(Currency currency) {
		this.currency = currency;
	}

	/**
	 * @return the transaction
	 */
	public Boolean getTransaction() {
		return transaction;
	}

	/**
	 * @param transaction
	 *            the transaction to set
	 */
	public void setTransaction(Boolean transaction) {
		this.transaction = transaction;
	}

	/**
	 * @return the numberOfTransactions
	 */
	public Integer getNumberOfTransactions() {
		return numberOfTransactions;
	}

	/**
	 * @param numberOfTransactions
	 *            the numberOfTransactions to set
	 */
	public void setNumberOfTransactions(Integer numberOfTransactions) {
		this.numberOfTransactions = numberOfTransactions;
	}

	/**
	 * @return the subscription
	 */
	public Subscription getSubscription() {
		return subscription;
	}

	/**
	 * @param subscription
	 *            the subscription to set
	 */
	public void setSubscription(Subscription subscription) {
		this.subscription = subscription;
	}

}
