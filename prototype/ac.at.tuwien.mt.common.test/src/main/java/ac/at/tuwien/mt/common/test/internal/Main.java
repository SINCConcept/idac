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
package ac.at.tuwien.mt.common.test.internal;

import ac.at.tuwien.mt.common.test.sample.SampleData;
import ac.at.tuwien.mt.model.datacontract.DataContract;
import ac.at.tuwien.mt.model.helper.DefaultJSONProvider;

/**
 * @author Florin Bogdan Balint
 *
 */
public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		DataContract dc = SampleData.getSampleDataContract();
		String objectAsJson = DefaultJSONProvider.getObjectAsJson(dc);
		System.out.println(objectAsJson);

	}

}
