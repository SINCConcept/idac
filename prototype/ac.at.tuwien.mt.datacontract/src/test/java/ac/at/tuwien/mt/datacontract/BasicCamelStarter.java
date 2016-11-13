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

package ac.at.tuwien.mt.datacontract;

import org.junit.Ignore;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import config.ac.at.tuwien.mt.CommonTestConfig;

@Ignore
public class BasicCamelStarter extends Thread {

	private AnnotationConfigApplicationContext context;
	private TestDataContractMicroservice microservice;

	public void run() {
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(CommonTestConfig.class);
		microservice = new TestDataContractMicroservice();
		microservice.setApplicationContext(context);
		microservice.run();
	}

	public void cancel() {
		try {
			microservice.stop();
		} catch (Exception e) {
			// nothing to do here
		}
		interrupt();
	}

	public AnnotationConfigApplicationContext getContext() {
		return context;
	}

	public void setContext(AnnotationConfigApplicationContext context) {
		this.context = context;
	}

}
