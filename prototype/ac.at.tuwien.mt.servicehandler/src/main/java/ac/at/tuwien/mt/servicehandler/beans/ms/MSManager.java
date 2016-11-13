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

import java.util.ArrayList;
import java.util.List;

import ac.at.tuwien.mt.model.microservice.MicroserviceInfo;
import ac.at.tuwien.mt.model.microservice.MicroserviceType;

/**
 * Microservice Manager class.
 * 
 * @author Florin Bogdan Balint
 *
 */
public class MSManager {

	private static MSManager instance = null;
	private List<MicroserviceInfo> microservices = new ArrayList<MicroserviceInfo>();

	protected MSManager() {
		// defeat instantiation
	}

	public static MSManager getInstance() {
		if (instance == null) {
			instance = new MSManager();
		}
		return instance;
	}

	public List<MicroserviceInfo> getMicroservices() {
		return microservices;
	}

	/**
	 * Registers a microservice - i.e. adds it to the microservice list.
	 * 
	 * @return true if the microservice was added successfully, false if it is
	 *         already registered or otherwise
	 */
	public boolean registerMicroservice(MicroserviceInfo microserviceInfo) {
		for (MicroserviceInfo microservice : microservices) {
			if (microservice.getMicroserviceType().equals(microserviceInfo.getMicroserviceType()) && microservice.getProtocol().equals(microserviceInfo.getProtocol())
					&& microservice.getHost().equals(microserviceInfo.getHost()) && microservice.getPort().intValue() == microserviceInfo.getPort().intValue()
					&& microservice.getPath().equals(microserviceInfo.getPath())) {
				// duplicate entry found
				return false;
			}
		}
		microservices.add(microserviceInfo);
		return true;
	}

	public List<MicroserviceInfo> getCustomMicroservices() {
		List<MicroserviceInfo> msList = new ArrayList<MicroserviceInfo>();
		List<MicroserviceInfo> microservices = MSManager.getInstance().getMicroservices();
		for (MicroserviceInfo microservice : microservices) {
			if (microservice.getMicroserviceType().equals(MicroserviceType.CUSTOM)) {
				msList.add(microservice);
			}
		}
		return msList;
	}

	public MicroserviceInfo getMicroservice(MicroserviceType msType) {
		// get all the relevant microservices
		List<MicroserviceInfo> msList = new ArrayList<MicroserviceInfo>();
		List<MicroserviceInfo> microservices = MSManager.getInstance().getMicroservices();
		for (MicroserviceInfo microservice : microservices) {
			// ignore custom made microservices
			if (microservice.getMicroserviceType().equals(MicroserviceType.CUSTOM)) {
				continue;
			}
			if (microservice.getMicroserviceType().equals(msType)) {
				msList.add(microservice);
			}
		}

		if (msList.isEmpty()) {
			return null;
		}

		// balance them using the same probability for everyone
		int size = msList.size();
		int randomNum = 1 + (int) (Math.random() * size);
		randomNum--;
		return msList.get(randomNum);
	}

	public List<MicroserviceInfo> getMicroservices(MicroserviceType msType) {
		// get all the relevant microservices
		List<MicroserviceInfo> msList = new ArrayList<MicroserviceInfo>();
		List<MicroserviceInfo> microservices = MSManager.getInstance().getMicroservices();
		for (MicroserviceInfo microservice : microservices) {
			if (microservice.getMicroserviceType().equals(msType)) {
				msList.add(microservice);
			}
		}
		return msList;
	}

}
