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
package ac.at.tuwien.mt.datacontract.bean;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.camel.Exchange;
import org.apache.camel.Handler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import ac.at.tuwien.mt.model.microservice.MicroserviceInfo;
import ac.at.tuwien.mt.model.microservice.MicroserviceType;

@PropertySource("classpath:webservices.properties")
@Component
public class RegisterMicroserviceBean {

	private static final Logger LOGGER = LogManager.getLogger(RegisterMicroserviceBean.class);

	@Value("${current.host}")
	private String currentHost;
	@Value("${rest.jetty.port}")
	private Integer currentPort;

	@Value("${rest.middleware.baseurl}")
	private String baseurl;
	@Value("${rest.middleware.microservice.register}")
	private String registrationPath;

	@Handler
	public void process(Exchange exchange) throws Exception {
		LOGGER.debug("Registering microservice.");

		Client client = ClientBuilder.newClient();

		WebTarget target = client.target(baseurl).path(registrationPath);
		MicroserviceInfo mi = new MicroserviceInfo();
		mi.setProtocol("http");
		mi.setHost(currentHost);
		mi.setPort(currentPort);
		mi.setPath("rest/");
		mi.setMicroserviceType(MicroserviceType.DATACONTRACT);
		Entity<String> entity = Entity.entity(mi.getDocument().toJson(), MediaType.APPLICATION_JSON);

		try {
			Response response = target.request(MediaType.APPLICATION_JSON_TYPE).post(entity);
			exchange.getOut().setHeader(Exchange.HTTP_RESPONSE_CODE, response.getStatusInfo().getStatusCode());
			exchange.getOut().setHeader(Exchange.HTTP_RESPONSE_TEXT, response.getStatusInfo().getReasonPhrase());
			exchange.getOut().setBody(response.getStatusInfo().getReasonPhrase());
			response.close();
		} catch (Exception e) {
			exchange.getOut().setHeader(Exchange.HTTP_RESPONSE_CODE, Response.Status.SERVICE_UNAVAILABLE.getStatusCode());
			exchange.getOut().setHeader(Exchange.HTTP_RESPONSE_TEXT, Response.Status.SERVICE_UNAVAILABLE.getReasonPhrase());
			exchange.getOut().setBody(Response.Status.SERVICE_UNAVAILABLE.getReasonPhrase());
		}

	}
}
