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
package ac.at.tuwien.mt.gui.beans;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author Florin Bogdan Balint
 *
 */
@SuppressWarnings("serial")
@ManagedBean(name = "navi")
@RequestScoped
public class NavigationControllerBean implements Serializable {

	private static final Logger LOGGER = LogManager.getLogger(NavigationControllerBean.class);

	public String welcome() {
		return "welcome.xhtml?faces-redirect=true";
	}

	public String register() {
		return "register.xhtml?faces-redirect=true";
	}

	public String login() {
		return "index.xhtml?faces-redirect=true";
	}

	public String addThing() {
		return "thing_add.xhtml?faces-redirect=true";
	}

	public String manageThings() {
		return "things_overview.xhtml?faces-redirect=true";
	}

	public String buyOverview() {
		return "things_buy_overview.xhtml?faces-redirect=true";
	}

	public String thingUpdate() {
		return "thing_edit.xhtml?faces-redirect=true";
	}

	public String thingClone() {
		return "thing_clone.xhtml?faces-redirect=true";
	}

	public String thingDelete() {
		return "thing_delete.xhtml?faces-redirect=true";
	}

	public String openDataContracts() {
		return "dc_open_contracts.xhtml?faces-redirect=true";
	}

	public String listConcludedContracts() {
		return "dc_concluded_list.xhtml?faces-redirect=true";
	}

	public String reviewOpenContract() {
		return "dc_open_review.xhtml?faces-redirect=true";
	}

	public String dataspaces() {
		return "dataspaces.xhtml?faces-redirect=true";
	}

	public String dcConclude() {
		return "dc_conclude.xhtml?faces-redirect=true";
	}

	public String dataspaceThings() {
		return "dataspace_things.xhtml?faces-redirect=true";
	}

	public String viewConcludedContract() {
		return "dc_concluded_view.xhtml?faces-redirect=true";
	}

	public String thingQualityMonitor() {
		return "thing_quality_monitor.xhtml?faces-redirect=true";
	}

	public String monitorConcludedContract() {
		return "dc_quality_monitor.xhtml?faces-redirect=true";
	}

	public String rateConcludedContract() {
		return "dc_concluded_rate.xhtml?faces-redirect=true";
	}

	public String rateThing() {
		return "thing_rate.xhtml?faces-redirect=true";
	}

	public String logout() {
		LOGGER.info("Invalidating session!");
		FacesContext facesContext = FacesContext.getCurrentInstance();
		facesContext.getExternalContext().invalidateSession();
		return "index.xhtml?faces-redirect=true";
	}

	public static void removeSessionBean(final String beanName) {
		FacesContext.getCurrentInstance().getExternalContext().getSessionMap().remove(beanName);
	}

}
