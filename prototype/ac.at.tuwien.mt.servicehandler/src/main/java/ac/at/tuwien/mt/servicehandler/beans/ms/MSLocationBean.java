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
package ac.at.tuwien.mt.servicehandler.beans.ms;

import java.util.List;

import javax.ws.rs.core.Response;

import org.apache.camel.Exchange;
import org.apache.camel.Handler;
import org.apache.camel.Header;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import ac.at.tuwien.mt.model.helper.DefaultJSONProvider;
import ac.at.tuwien.mt.model.microservice.MicroserviceInfo;
import ac.at.tuwien.mt.model.microservice.MicroserviceType;
import ac.at.tuwien.mt.model.util.StringUtil;

@Component
public class MSLocationBean {

	private static final Logger LOGGER = Logger.getLogger(MSLocationBean.class);

	@Handler
	public void process(@Header("microservice") String microservice, Exchange exchange) throws Exception {
		LOGGER.info("Requesting a microservice: datacontract.");

		if (StringUtil.isNullOrBlank(microservice)) {
			exchange.getOut().setHeader(Exchange.HTTP_RESPONSE_CODE, Response.Status.NOT_FOUND.getStatusCode());
			exchange.getOut().setBody(Response.Status.NOT_FOUND.getReasonPhrase());
			return;
		}

		MicroserviceType microserviceType = null;
		try {
			microserviceType = MicroserviceType.valueOf(microservice.trim().toUpperCase());
		} catch (java.lang.IllegalArgumentException e) {
			LOGGER.error(e);
			exchange.getOut().setHeader(Exchange.HTTP_RESPONSE_CODE, Response.Status.NOT_FOUND.getStatusCode());
			exchange.getOut().setBody(Response.Status.NOT_FOUND.getReasonPhrase());
			return;
		}

		if (microserviceType == MicroserviceType.CUSTOM) {
			List<MicroserviceInfo> customMicroservices = MSManager.getInstance().getCustomMicroservices();
			if (customMicroservices == null || customMicroservices.isEmpty()) {
				exchange.getOut().setHeader(Exchange.HTTP_RESPONSE_CODE, Response.Status.SERVICE_UNAVAILABLE.getStatusCode());
				exchange.getOut().setBody(Response.Status.SERVICE_UNAVAILABLE.getReasonPhrase());
			} else {
				exchange.getOut().setHeader(Exchange.HTTP_RESPONSE_CODE, Response.Status.OK.getStatusCode());
				String objectAsJson = DefaultJSONProvider.getObjectAsJson(customMicroservices);
				exchange.getOut().setBody(objectAsJson);
			}
		}

		MicroserviceInfo microserviceInfo = MSManager.getInstance().getMicroservice(microserviceType);
		if (microserviceInfo == null) {
			exchange.getOut().setHeader(Exchange.HTTP_RESPONSE_CODE, Response.Status.SERVICE_UNAVAILABLE.getStatusCode());
			exchange.getOut().setBody(Response.Status.SERVICE_UNAVAILABLE.getReasonPhrase());
		} else {
			exchange.getOut().setHeader(Exchange.HTTP_RESPONSE_CODE, Response.Status.OK.getStatusCode());
			exchange.getOut().setBody(microserviceInfo.getDocument());
		}
	}

}
