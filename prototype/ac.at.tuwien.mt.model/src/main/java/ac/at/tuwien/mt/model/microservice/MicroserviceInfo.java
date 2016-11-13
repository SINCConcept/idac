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
package ac.at.tuwien.mt.model.microservice;

import java.io.Serializable;

import org.bson.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * @author Florin Bogdan Balint
 *
 */
@SuppressWarnings("serial")
@JsonIgnoreProperties(ignoreUnknown = true)
public class MicroserviceInfo implements Serializable {

	private String protocol;
	private String host;
	private Integer port;
	private String path;
	private MicroserviceType microserviceType;
	private String description;

	public MicroserviceInfo() {
		// simple constructor
	}

	public MicroserviceInfo(Document document) {
		this.protocol = document.getString("protocol");
		this.host = document.getString("host");
		this.port = document.getInteger("port");
		this.path = document.getString("path");
		String microserviceTypeAsString = document.getString("microserviceType");
		MicroserviceType microserviceType = MicroserviceType.valueOf(microserviceTypeAsString);
		this.microserviceType = microserviceType;
		this.description = document.getString("description");
	}

	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public Integer getPort() {
		return port;
	}

	public void setPort(Integer port) {
		this.port = port;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public MicroserviceType getMicroserviceType() {
		return microserviceType;
	}

	public void setMicroserviceType(MicroserviceType microserviceType) {
		this.microserviceType = microserviceType;
	}

	/**
	 * @return the description
	 */
	public String description() {
		return description;
	}

	/**
	 * @param description
	 *            the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	@JsonIgnore
	public Document getDocument() {
		Document document = new Document();
		document.put("protocol", getProtocol());
		document.put("host", getHost());
		document.put("port", getPort());
		document.put("path", getPath());
		document.put("microserviceType", getMicroserviceType().toString());
		document.put("description", description());
		return document;
	}

	@Override
	public String toString() {
		return "MicroserviceInfo [protocol=" + protocol + ", host=" + host + ", port=" + port + ", path=" + path + ", microserviceType=" + microserviceType + "]";
	}

}
