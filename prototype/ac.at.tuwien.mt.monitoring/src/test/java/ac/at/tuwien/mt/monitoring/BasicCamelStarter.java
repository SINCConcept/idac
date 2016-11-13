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

package ac.at.tuwien.mt.monitoring;

import org.junit.Ignore;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import ac.at.tuwien.mt.monitoring.config.MonitoringConfig;
import ac.at.tuwien.mt.monitoring.main.MonitoringMicroservice;

@Ignore
public class BasicCamelStarter extends Thread {

	private AnnotationConfigApplicationContext context;
	private MonitoringMicroservice microservice;

	public void run() {
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MonitoringConfig.class);
		microservice = new MonitoringMicroservice();
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
