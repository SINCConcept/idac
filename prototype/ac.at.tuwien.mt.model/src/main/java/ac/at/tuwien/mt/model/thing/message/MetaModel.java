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
import java.util.ArrayList;
import java.util.List;

import org.bson.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * 
 * This class represents the meta-model of a message. Each message has a certain
 * model (which typically is a list of fields with values). The model of this
 * model is called a meta-model.
 * 
 * @author Florin Bogdan Balint
 *
 */
@SuppressWarnings("serial")
public class MetaModel implements Serializable {

	List<Attribute> attributes = new ArrayList<Attribute>();

	public MetaModel() {
		// empty constructor
	}

	public MetaModel(Document document) {
		Object listObject = document.get("attributes");
		if (listObject instanceof List) {
			List<?> list = (List<?>) listObject;
			for (Object object : list) {
				if (object instanceof Document) {
					Document objectAsDoc = (Document) object;
					this.attributes.add(new Attribute(objectAsDoc));
				}
			}
		}
	}

	@JsonIgnore
	public Document getDocument() {
		Document document = new Document();
		// add the clauses list
		List<Document> attributesDocument = new ArrayList<Document>();
		for (Attribute attribute : attributes) {
			attributesDocument.add(attribute.getDocument());
		}
		document.put("attributes", attributesDocument);
		return document;
	}

	/**
	 * @return the attributes
	 */
	public List<Attribute> getAttributes() {
		return attributes;
	}

	/**
	 * @param attributes
	 *            the attributes to set
	 */
	public void setAttributes(List<Attribute> attributes) {
		this.attributes = attributes;
	}

}
