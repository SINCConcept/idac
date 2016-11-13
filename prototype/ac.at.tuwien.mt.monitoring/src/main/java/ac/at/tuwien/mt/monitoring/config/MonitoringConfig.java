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

package ac.at.tuwien.mt.monitoring.config;

import java.util.ArrayList;
import java.util.List;

import org.apache.camel.CamelContext;
import org.apache.camel.component.properties.PropertiesComponent;
import org.apache.camel.spring.SpringCamelContext;
import org.apache.camel.spring.javaconfig.CamelConfiguration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;

@Configuration
@ComponentScan("ac.at.tuwien.mt")
@PropertySource({ "classpath:mongo_db.properties" })
@EnableScheduling
public class MonitoringConfig extends CamelConfiguration implements InitializingBean {

	private static final Logger LOGGER = LogManager.getLogger(MonitoringConfig.class);

	@Override
	protected CamelContext createCamelContext() throws Exception {
		LOGGER.info("Creating spring camel context...");
		ApplicationContext applicationContext = getApplicationContext();
		SpringCamelContext springCamelContext = new SpringCamelContext(applicationContext);
		return springCamelContext;
	}

	@Override
	protected void setupCamelContext(CamelContext camelContext) throws Exception {
		// setup steps
	}

	@Bean
	public PropertiesComponent properties() {
		String[] locations = new String[] { "classpath:webservices.properties", "classpath:mongo_db.properties" };
		PropertiesComponent propertiesComponent = new PropertiesComponent();
		propertiesComponent.setLocations(locations);
		return propertiesComponent;
	}

	@Bean
	public MongoClient mongoDatabase(@Value("${mongo_db_user_name}") String username,
			@Value("${mongo_db_user_password}") String password, //
			@Value("${mongo_db_name}") String database, //
			@Value("${mongo_db_host}") String host, //
			@Value("${mongo_db_port}") int port, //
			@Value("${mongo_db_max_connections_per_host}") int maxconnections, //
			@Value("${mongo_db_ssl_enabled}") boolean sslEnabled, //
			@Value("${mongo_db_ssl_invalidhostnameallowed}") boolean sslInvalidHostnameAllowed //
	) {

		// set the mongo client options
		MongoClientOptions.Builder builder = new MongoClientOptions.Builder();
		builder.connectionsPerHost(maxconnections);
		builder.sslEnabled(sslEnabled);
		builder.sslInvalidHostNameAllowed(sslInvalidHostnameAllowed);
		MongoClientOptions options = builder.build();

		// set the credentials
		MongoCredential credential = MongoCredential.createCredential(username, database, password.toCharArray());
		List<MongoCredential> list = new ArrayList<MongoCredential>();
		list.add(credential);

		ServerAddress serverAddress = new ServerAddress(host, port);
		MongoClient mongoClient = new MongoClient(serverAddress, list, options);
		return mongoClient;
	}

	// To resolve ${} in @Value
	@Bean
	public static PropertySourcesPlaceholderConfigurer propertyConfigInDev() {
		return new PropertySourcesPlaceholderConfigurer();
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		// just to make SpringDM happy do nothing here
	}

}
