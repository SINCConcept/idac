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
import org.junit.Test;

import ac.at.tuwien.mt.model.microservice.MicroserviceInfo;
import ac.at.tuwien.mt.model.microservice.MicroserviceType;

/**
 * @author Florin Bogdan Balint
 *
 */
public class EnumTest {

	@Test
	public void test() {
		MicroserviceInfo mi = new MicroserviceInfo();
		mi.setHost("localhost");
		mi.setPort(123);
		mi.setPath("test");
		mi.setMicroserviceType(MicroserviceType.DATACONTRACT);
		String json = mi.getDocument().toJson();
		System.out.println(json);

		Document parsed = Document.parse(json);
		MicroserviceInfo mi2 = new MicroserviceInfo(parsed);
		System.out.println(mi2.getMicroserviceType());

	}
}
