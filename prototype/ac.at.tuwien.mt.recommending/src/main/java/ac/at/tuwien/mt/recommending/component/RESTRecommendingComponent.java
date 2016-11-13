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
package ac.at.tuwien.mt.recommending.component;

import javax.ws.rs.core.MediaType;

import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ac.at.tuwien.mt.model.exception.InvalidObjectException;
import ac.at.tuwien.mt.recommending.beans.ThingAvgNegBean;
import ac.at.tuwien.mt.recommending.beans.ThingRecommendationBean;

/**
 * @author Florin Bogdan Balint
 *
 */
@Component
public class RESTRecommendingComponent extends RouteBuilder {

	private static final Logger LOGGER = LogManager.getLogger(RESTRecommendingComponent.class);

	private ThingRecommendationBean thingRecommendationBean;
	private ThingAvgNegBean thingAvgNegBean;

	@Autowired
	public RESTRecommendingComponent(ThingRecommendationBean thingRecommendationBean, ThingAvgNegBean thingAvgNegBean) {
		this.thingRecommendationBean = thingRecommendationBean;
		this.thingAvgNegBean = thingAvgNegBean;
	}

	@Override
	public void configure() throws Exception {

		LOGGER.debug("Configuring ping component.");

		onException(InvalidObjectException.class) //
				.handled(true) //
				.setHeader(Exchange.HTTP_RESPONSE_CODE, constant(400)) //
				.setHeader(Exchange.CONTENT_TYPE, constant("text/plain")) //
				.setBody() //
				.constant("Bad Request");

		rest("{{rest.recommending.path}}") // set the path
				.get("{{rest.recommending.recommend}}/{userid}") //
				.produces(MediaType.APPLICATION_JSON) // set the producing type
				.to("direct:thing_recommend");

		rest("{{rest.recommending.path}}") // set the path
				.get("{{rest.recommending.thingavgneg}}/{thingid}") //
				.produces(MediaType.APPLICATION_JSON) // set the producing type
				.to("direct:thingavgneg");

		from("direct:thing_recommend") //
				.log(LoggingLevel.DEBUG, "Received REST request: thing recommendation") // log
				.bean(thingRecommendationBean) //
				.end();

		from("direct:thingavgneg") //
				.log(LoggingLevel.DEBUG, "Received REST request: thing avg neg") // log
				.bean(thingAvgNegBean) //
				.end();

	}
}
