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
import java.util.Map;

import org.bson.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * An Attribute represents for example a JSON attribute. A simple case would be
 * like {"name": "Florin"}. A more complex case would be: {"person": {"Name",
 * "Florin"}}
 * 
 * @author Florin Bogdan Balint
 *
 */
@SuppressWarnings("serial")
public class Attribute implements Serializable {

	// the name of the attribute
	private String name;
	// the type of the attribute, e.g. STRING, BOOLEAN, etc.
	private DataType dataType;
	// further attribute properties
	private Property property;

	// the meta-model in case the attribute contains further attributes
	private MetaModel metaModel;

	public Attribute() {
		// empty constructor
		property = new Property();
	}

	public Attribute(Document document) {
		this.name = document.getString("name");
		this.dataType = DataType.valueOf(document.getString("dataType"));
		Object propertyAsObject = document.get("property");
		if (propertyAsObject != null) {
			this.property = new Property((Document) propertyAsObject);
		}

		@SuppressWarnings("unchecked")
		Map<String, Object> objectMetaModel = (Map<String, Object>) document.get("metaModel");
		if (objectMetaModel != null) {
			Document addressDocument = new Document(objectMetaModel);
			this.metaModel = new MetaModel(addressDocument);
		}
	}

	@JsonIgnore
	public Document getDocument() {
		Document document = new Document();
		document.put("name", getName());
		if (getDataType() != null) {
			document.put("dataType", getDataType().getProperty());
		}
		if (getProperty() != null) {
			document.put("property", getProperty().getDocument());
		}
		if (getMetaModel() != null) {
			Document doc = getMetaModel().getDocument();
			document.put("metaModel", doc);
		}
		return document;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the dataType
	 */
	public DataType getDataType() {
		return dataType;
	}

	/**
	 * @param dataType
	 *            the dataType to set
	 */
	public void setDataType(DataType dataType) {
		this.dataType = dataType;
	}

	/**
	 * @return the property
	 */
	public Property getProperty() {
		return property;
	}

	/**
	 * @param property
	 *            the property to set
	 */
	public void setProperty(Property property) {
		this.property = property;
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Attribute [name=" + name + ", dataType=" + dataType + "]";
	}

}
