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
package ac.at.tuwien.mt.model.conversion;

import org.bson.Document;
import org.junit.Assert;
import org.junit.Test;

import ac.at.tuwien.mt.model.thing.message.Attribute;
import ac.at.tuwien.mt.model.thing.message.DataType;
import ac.at.tuwien.mt.model.thing.message.MetaModel;
import ac.at.tuwien.mt.model.thing.message.Property;

/**
 * @author Florin Bogdan Balint
 *
 */
public class MetaModelTest {

	@Test
	public void testSimple() {

		Attribute attribute1 = new Attribute();
		attribute1.setDataType(DataType.STRING);
		attribute1.setName("thing_id");
		Property property = new Property();
		property.setIdentifier(true);
		attribute1.setProperty(property);

		Attribute attribute2 = new Attribute();
		attribute2.setDataType(DataType.ATTRIBUTE);
		attribute2.setName("value");

		Attribute attribute3 = new Attribute();
		attribute3.setDataType(DataType.DOUBLE);
		attribute3.setName("temp");

		Attribute attribute4 = new Attribute();
		attribute4.setDataType(DataType.STRING);
		attribute4.setName("scale");

		MetaModel model2 = new MetaModel();
		model2.getAttributes().add(attribute3);
		model2.getAttributes().add(attribute4);
		attribute2.setMetaModel(model2);

		MetaModel model1 = new MetaModel();
		model1.getAttributes().add(attribute1);
		model1.getAttributes().add(attribute2);

		String json1 = model1.getDocument().toJson();
		Document parsed = Document.parse(json1);
		MetaModel tm2 = new MetaModel(parsed);
		String json2 = tm2.getDocument().toJson();
		// System.out.println(json1);
		Assert.assertEquals(json1, json2);
	}

	@Test
	public void testComplex() {
		Attribute attribute1 = new Attribute();
		attribute1.setDataType(DataType.STRING);
		attribute1.setName("thing_id");
		Property property = new Property();
		property.setIdentifier(true);
		attribute1.setProperty(property);

		Attribute attribute2 = new Attribute();
		attribute2.setDataType(DataType.ATTRIBUTE);
		attribute2.setName("value");

		Attribute attribute3 = new Attribute();
		attribute3.setDataType(DataType.DOUBLE);
		attribute3.setName("temp");

		Attribute attribute4 = new Attribute();
		attribute4.setDataType(DataType.ATTRIBUTE);
		attribute4.setName("scale");

		Attribute attribute5 = new Attribute();
		attribute5.setDataType(DataType.DOUBLE);
		attribute5.setName("temp");

		Attribute attribute6 = new Attribute();
		attribute6.setDataType(DataType.STRING);
		attribute6.setName("scale");

		MetaModel model3 = new MetaModel();
		model3.getAttributes().add(attribute5);
		model3.getAttributes().add(attribute6);
		attribute4.setMetaModel(model3);

		MetaModel model2 = new MetaModel();
		model2.getAttributes().add(attribute3);
		model2.getAttributes().add(attribute4);
		attribute2.setMetaModel(model2);

		MetaModel model1 = new MetaModel();
		model1.getAttributes().add(attribute1);
		model1.getAttributes().add(attribute2);

		String json1 = model1.getDocument().toJson();
		Document parsed = Document.parse(json1);
		MetaModel tm2 = new MetaModel(parsed);
		String json2 = tm2.getDocument().toJson();
		// System.out.println(json1);
		Assert.assertEquals(json1, json2);
	}
}
