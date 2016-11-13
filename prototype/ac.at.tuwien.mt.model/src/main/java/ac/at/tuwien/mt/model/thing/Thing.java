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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.bson.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import ac.at.tuwien.mt.model.datacontract.clause.ControlAndRelationship;
import ac.at.tuwien.mt.model.datacontract.clause.DataRights;
import ac.at.tuwien.mt.model.datacontract.clause.PricingModel;
import ac.at.tuwien.mt.model.datacontract.clause.PurchasingPolicy;
import ac.at.tuwien.mt.model.datacontract.clause.QoD;
import ac.at.tuwien.mt.model.datacontract.clause.QoS;
import ac.at.tuwien.mt.model.microservice.monitoring.QueueInfo;
import ac.at.tuwien.mt.model.thing.message.MetaModel;
import ac.at.tuwien.mt.model.thing.rating.Rating;

/**
 * @author Florin Bogdan Balint
 *
 */
@SuppressWarnings("serial")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Thing implements Serializable {

	// Thing Basic Database Information
	private String thingId;
	private QueueInfo queueInfo;

	private Integer revision;
	private Date creationDate;

	private String ownerId;
	private String resourceId;
	private String dataSample;
	private String description;

	// Thing quantifiable contract/thing information
	private DataRights dataRights;
	private QoD qod;
	private QoS qos;
	private PricingModel pricingModel;
	private PurchasingPolicy purchasingPolicy;
	private ControlAndRelationship controlAndRelationship;

	/**
	 * Flag used to indicate that this Thing should be monitored by the standard
	 * monitoring component or not.
	 */
	private Boolean standardMonitoring;

	// Thing MetaModel
	private MetaModel metaModel;

	private List<Rating> ratings = new ArrayList<Rating>();

	private List<String> tags = new ArrayList<String>();

	private Double rating;

	public Thing() {
		this.queueInfo = new QueueInfo();
		this.dataRights = new DataRights();
		this.qod = new QoD();
		this.qos = new QoS();
		this.pricingModel = new PricingModel();
		this.purchasingPolicy = new PurchasingPolicy();
		this.controlAndRelationship = new ControlAndRelationship();
		this.metaModel = new MetaModel();
		this.standardMonitoring = true;
	}

	public Thing(Document document) {
		this.thingId = document.getString("thingId");
		this.queueInfo = new QueueInfo((Document) document.get("queueInfo"));
		this.revision = document.getInteger("revision");
		Object creationDateAsObject = document.get("creationDate");
		if (creationDateAsObject instanceof Date) {
			this.creationDate = (Date) creationDateAsObject;
		}
		if (creationDateAsObject instanceof Long) {
			this.creationDate = new Date((Long) creationDateAsObject);
		}
		this.ownerId = document.getString("ownerId");
		this.resourceId = document.getString("resourceId");
		this.dataSample = document.getString("dataSample");
		this.description = document.getString("description");
		this.dataRights = new DataRights((Document) document.get("dataRights"));
		this.qod = new QoD((Document) document.get("qod"));
		this.qos = new QoS((Document) document.get("qos"));
		this.pricingModel = new PricingModel((Document) document.get("pricingModel"));
		this.purchasingPolicy = new PurchasingPolicy((Document) document.get("purchasingPolicy"));
		this.controlAndRelationship = new ControlAndRelationship((Document) document.get("controlAndRelationship"));
		this.metaModel = new MetaModel((Document) document.get("metaModel"));
		this.standardMonitoring = document.getBoolean("standardMonitoring");

		// set the ratings list
		Object listObject = document.get("ratings");
		if (listObject instanceof List) {
			List<?> list = (List<?>) listObject;
			for (Object object : list) {
				if (object instanceof Document) {
					Document objectAsDoc = (Document) object;
					this.ratings.add(new Rating(objectAsDoc));
				}
			}
		}

		// set the ratings list
		Object tagsDoc = document.get("tags");
		if (tagsDoc instanceof List) {
			List<?> list = (List<?>) tagsDoc;
			for (Object object : list) {
				if (object instanceof String) {
					String objectAsString = (String) object;
					this.tags.add(objectAsString);
				}
			}
		}

		this.rating = document.getDouble("rating");
	}

	@JsonIgnore
	public Document getDocument() {
		Document document = new Document();
		document.put("thingId", getThingId());
		document.put("queueInfo", getQueueInfo().getDocument());
		document.put("revision", getRevision());
		document.put("creationDate", getCreationDate());
		document.put("ownerId", getOwnerId());
		document.put("resourceId", getResourceId());
		document.put("dataSample", getDataSample());
		document.put("description", getDescription());
		if (getDataRights() != null) {
			document.put("dataRights", getDataRights().getDocument());
		}
		if (getQod() != null) {
			document.put("qod", getQod().getDocument());
		}
		if (getQos() != null) {
			document.put("qos", getQos().getDocument());
		}
		if (getPricingModel() != null) {
			document.put("pricingModel", getPricingModel().getDocument());
		}
		if (getPurchasingPolicy() != null) {
			document.put("purchasingPolicy", getPurchasingPolicy().getDocument());
		}
		if (getControlAndRelationship() != null) {
			document.put("controlAndRelationship", getControlAndRelationship().getDocument());
		}
		if (getMetaModel() != null) {
			document.put("metaModel", getMetaModel().getDocument());
		}
		document.put("standardMonitoring", getStandardMonitoring());

		// add the ratings list
		List<Document> ratingsDocument = new ArrayList<Document>();
		for (Rating rating : ratings) {
			ratingsDocument.add(rating.getDocument());
		}
		document.put("ratings", ratingsDocument);

		document.put("tags", tags);

		computeRating();

		document.put("rating", getRating());

		return document;
	}

	private void computeRating() {
		rating = 0.0;
		if (ratings.size() == 0) {
			return;
		}

		double currentRating = 0.0;
		int count = 0;
		for (Rating rating : ratings) {
			if (rating.getRating() != null) {
				currentRating += rating.getRating();
				count++;
			}
		}

		if (count > 0) {
			rating = currentRating / count;
		}
	}

	/**
	 * @return the thingId
	 */
	public String getThingId() {
		return thingId;
	}

	/**
	 * @param thingId
	 *            the thingId to set
	 */
	public void setThingId(String thingId) {
		this.thingId = thingId;
	}

	/**
	 * @return the queueInfo
	 */
	public QueueInfo getQueueInfo() {
		return queueInfo;
	}

	/**
	 * @param queueInfo
	 *            the queueInfo to set
	 */
	public void setQueueInfo(QueueInfo queueInfo) {
		this.queueInfo = queueInfo;
	}

	/**
	 * @return the revision
	 */
	public Integer getRevision() {
		return revision;
	}

	/**
	 * @param revision
	 *            the revision to set
	 */
	public void setRevision(Integer revision) {
		this.revision = revision;
	}

	/**
	 * @return the creationDate
	 */
	public Date getCreationDate() {
		return creationDate;
	}

	/**
	 * @param creationDate
	 *            the creationDate to set
	 */
	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	/**
	 * @return the ownerId
	 */
	public String getOwnerId() {
		return ownerId;
	}

	/**
	 * @param ownerId
	 *            the ownerId to set
	 */
	public void setOwnerId(String ownerId) {
		this.ownerId = ownerId;
	}

	/**
	 * @return the resourceId
	 */
	public String getResourceId() {
		return resourceId;
	}

	/**
	 * @param resourceId
	 *            the resourceId to set
	 */
	public void setResourceId(String resourceId) {
		this.resourceId = resourceId;
	}

	/**
	 * @return the dataSample
	 */
	public String getDataSample() {
		return dataSample;
	}

	/**
	 * @param dataSample
	 *            the dataSample to set
	 */
	public void setDataSample(String dataSample) {
		this.dataSample = dataSample;
	}

	/**
	 * @return the dataRights
	 */
	public DataRights getDataRights() {
		return dataRights;
	}

	/**
	 * @param dataRights
	 *            the dataRights to set
	 */
	public void setDataRights(DataRights dataRights) {
		this.dataRights = dataRights;
	}

	/**
	 * @return the qod
	 */
	public QoD getQod() {
		return qod;
	}

	/**
	 * @param qod
	 *            the qod to set
	 */
	public void setQod(QoD qod) {
		this.qod = qod;
	}

	/**
	 * @return the qos
	 */
	public QoS getQos() {
		return qos;
	}

	/**
	 * @param qos
	 *            the qos to set
	 */
	public void setQos(QoS qos) {
		this.qos = qos;
	}

	/**
	 * @return the pricingModel
	 */
	public PricingModel getPricingModel() {
		return pricingModel;
	}

	/**
	 * @param pricingModel
	 *            the pricingModel to set
	 */
	public void setPricingModel(PricingModel pricingModel) {
		this.pricingModel = pricingModel;
	}

	/**
	 * @return the purchasingPolicy
	 */
	public PurchasingPolicy getPurchasingPolicy() {
		return purchasingPolicy;
	}

	/**
	 * @param purchasingPolicy
	 *            the purchasingPolicy to set
	 */
	public void setPurchasingPolicy(PurchasingPolicy purchasingPolicy) {
		this.purchasingPolicy = purchasingPolicy;
	}

	/**
	 * @return the controlAndRelationship
	 */
	public ControlAndRelationship getControlAndRelationship() {
		return controlAndRelationship;
	}

	/**
	 * @param controlAndRelationship
	 *            the controlAndRelationship to set
	 */
	public void setControlAndRelationship(ControlAndRelationship controlAndRelationship) {
		this.controlAndRelationship = controlAndRelationship;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description
	 *            the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the metaModel
	 */
	public MetaModel getMetaModel() {
		return metaModel;
	}

	/**
	 * @param metaModel
	 *            the metaModel to set
	 */
	public void setMetaModel(MetaModel metaModel) {
		this.metaModel = metaModel;
	}

	/**
	 * @return the standardMonitoring
	 */
	public Boolean getStandardMonitoring() {
		return standardMonitoring;
	}

	/**
	 * @param standardMonitoring
	 *            the standardMonitoring to set
	 */
	public void setStandardMonitoring(Boolean standardMonitoring) {
		this.standardMonitoring = standardMonitoring;
	}

	/**
	 * @return the ratings
	 */
	public List<Rating> getRatings() {
		return ratings;
	}

	/**
	 * @param ratings
	 *            the ratings to set
	 */
	public void setRatings(List<Rating> ratings) {
		this.ratings = ratings;
	}

	/**
	 * @return the tags
	 */
	public List<String> getTags() {
		return tags;
	}

	/**
	 * @param tags
	 *            the tags to set
	 */
	public void setTags(List<String> tags) {
		this.tags = tags;
	}

	/**
	 * @return the rating
	 */
	public Double getRating() {
		return rating;
	}

	/**
	 * @param rating
	 *            the rating to set
	 */
	public void setRating(Double rating) {
		this.rating = rating;
	}

}
