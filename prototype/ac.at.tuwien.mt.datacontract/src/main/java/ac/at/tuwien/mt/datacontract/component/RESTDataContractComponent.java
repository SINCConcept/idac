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
package ac.at.tuwien.mt.datacontract.component;

import javax.ws.rs.core.MediaType;

import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;

import ac.at.tuwien.mt.datacontract.bean.BuyerConcludedContractsBean;
import ac.at.tuwien.mt.datacontract.bean.BuyerOpenContractsBean;
import ac.at.tuwien.mt.datacontract.bean.DataContractCreationBean;
import ac.at.tuwien.mt.datacontract.bean.DataContractTrailBean;
import ac.at.tuwien.mt.datacontract.bean.DataContractUpdateBean;
import ac.at.tuwien.mt.datacontract.bean.ProviderConcludedContractsBean;
import ac.at.tuwien.mt.datacontract.bean.ProviderOpenContractsBean;
import ac.at.tuwien.mt.model.datacontract.DataContract;
import ac.at.tuwien.mt.model.exception.ObjectNotFoundException;

/**
 * @author Florin Bogdan Balint
 *
 */
@Component
public class RESTDataContractComponent extends RouteBuilder {

	private static final Logger LOGGER = LogManager.getLogger(RESTDataContractComponent.class);

	private DataContractCreationBean dataContractCreationBean;
	private DataContractTrailBean dataContractTrailBean;
	private DataContractUpdateBean dataContractUpdateBean;
	private ProviderConcludedContractsBean providerConcludedContractsBean;
	private ProviderOpenContractsBean providerOpenContractsBean;
	private BuyerOpenContractsBean buyerOpenContractsBean;
	private BuyerConcludedContractsBean buyerConcludedContractsBean;

	@Autowired
	public RESTDataContractComponent(DataContractCreationBean dataContractCreationBean, DataContractTrailBean dataContractTrailBean, DataContractUpdateBean dataContractUpdateBean,
			ProviderConcludedContractsBean providerConcludedContractsBean, ProviderOpenContractsBean providerOpenContractsBean, BuyerOpenContractsBean buyerOpenContractsBean,
			BuyerConcludedContractsBean buyerConcludedContractsBean) {
		this.dataContractCreationBean = dataContractCreationBean;
		this.dataContractTrailBean = dataContractTrailBean;
		this.dataContractUpdateBean = dataContractUpdateBean;
		this.providerConcludedContractsBean = providerConcludedContractsBean;
		this.providerOpenContractsBean = providerOpenContractsBean;
		this.buyerOpenContractsBean = buyerOpenContractsBean;
		this.buyerConcludedContractsBean = buyerConcludedContractsBean;
	}

	@Override
	public void configure() throws Exception {

		LOGGER.debug("Configuring person authentication component.");

		// DEFINE BEHAVIOR ON JSON SCHEMA PROBLEMS
		onException(UnrecognizedPropertyException.class) //
				.handled(true) //
				.setHeader(Exchange.HTTP_RESPONSE_CODE, constant(400)) //
				.setHeader(Exchange.CONTENT_TYPE, constant("text/plain")) //
				.setBody() //
				.constant("Invalid json data");

		onException(ObjectNotFoundException.class) //
				.handled(true) //
				.setHeader(Exchange.HTTP_RESPONSE_CODE, constant(404)) //
				.setHeader(Exchange.CONTENT_TYPE, constant("text/plain")) //
				.setBody() //
				.constant("Not Found");

		rest("{{rest.datacontract.path}}") // set the path
				.put() // set the individual
				.produces(MediaType.APPLICATION_JSON) // set the producing type
				.bindingMode(RestBindingMode.json) //
				.type(DataContract.class) //
				.to("direct:datacontract_creation");

		rest("{{rest.datacontract.path}}") // set the path
				.post() // set the individual
				.produces(MediaType.APPLICATION_JSON) // set the producing type
				.bindingMode(RestBindingMode.json) //
				.type(DataContract.class) //
				.to("direct:datacontract_update");

		rest("{{rest.datacontract.path}}") // set the path
				.get("{{rest.datacontract.provider.open}}/{personid}") //
				.produces(MediaType.APPLICATION_JSON) // set the producing type
				.bindingMode(RestBindingMode.json) //
				.to("direct:datacontract_provider_open");

		rest("{{rest.datacontracttrail.path}}") // set the path
				.get("{contractid}") //
				.produces(MediaType.APPLICATION_JSON) // set the producing type
				.bindingMode(RestBindingMode.json) //
				.type(DataContract.class) //
				.to("direct:datacontracttrail");

		rest("{{rest.datacontract.path}}") // set the path
				.get("{{rest.datacontract.provider.concluded}}/{personid}") //
				.produces(MediaType.APPLICATION_JSON) // set the producing type
				.bindingMode(RestBindingMode.json) //
				.to("direct:datacontract_provider_concluded");

		rest("{{rest.datacontract.path}}") // set the path
				.get("{{rest.datacontract.buyer.open}}/{personid}") //
				.produces(MediaType.APPLICATION_JSON) // set the producing type
				.bindingMode(RestBindingMode.json) //
				.to("direct:datacontract_buyer_open");

		rest("{{rest.datacontract.path}}") // set the path
				.get("{{rest.datacontract.buyer.concluded}}/{personid}") //
				.produces(MediaType.APPLICATION_JSON) // set the producing type
				.bindingMode(RestBindingMode.json) //
				.to("direct:datacontract_buyer_concluded");

		from("direct:datacontract_creation") //
				.log(LoggingLevel.DEBUG, "Received REST request: data contract creation") // log
				.bean(dataContractCreationBean) //
				.setHeader(Exchange.HTTP_RESPONSE_CODE, constant(200)) // response
				.end();

		from("direct:datacontracttrail") //
				.log(LoggingLevel.DEBUG, "Received REST request: get data contracttrail") // log
				.bean(dataContractTrailBean) //
				.setHeader(Exchange.HTTP_RESPONSE_CODE, constant(200)) // response
				.end();

		from("direct:datacontract_update") //
				.log(LoggingLevel.DEBUG, "Received REST request: data contract update") // log
				.bean(dataContractUpdateBean) //
				.setHeader(Exchange.HTTP_RESPONSE_CODE, constant(200)) // response
				.end();

		from("direct:datacontract_provider_open") //
				.log(LoggingLevel.DEBUG, "Received REST request: data contract provider open") // log
				.bean(providerOpenContractsBean) //
				.setHeader(Exchange.HTTP_RESPONSE_CODE, constant(200)) // response
				.end();

		from("direct:datacontract_provider_concluded") //
				.log(LoggingLevel.DEBUG, "Received REST request: data contract provider concluded") // log
				.bean(providerConcludedContractsBean) //
				.setHeader(Exchange.HTTP_RESPONSE_CODE, constant(200)) // response
				.end();

		from("direct:datacontract_buyer_open") //
				.log(LoggingLevel.DEBUG, "Received REST request: data contract buyer open") // log
				.bean(buyerOpenContractsBean) //
				.setHeader(Exchange.HTTP_RESPONSE_CODE, constant(200)) // response
				.end();

		from("direct:datacontract_buyer_concluded") //
				.log(LoggingLevel.DEBUG, "Received REST request: data contract buyer concluded") // log
				.bean(buyerConcludedContractsBean) //
				.setHeader(Exchange.HTTP_RESPONSE_CODE, constant(200)) // response
				.end();

	}
}
