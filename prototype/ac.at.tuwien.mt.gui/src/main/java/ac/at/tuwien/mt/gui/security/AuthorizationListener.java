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
package ac.at.tuwien.mt.gui.security;

import javax.faces.application.NavigationHandler;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;
import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ac.at.tuwien.mt.gui.beans.UserControllerBean;

/**
 * @author Florin Bogdan Balint
 *
 */
@SuppressWarnings("serial")
public class AuthorizationListener implements PhaseListener {

	private static final Logger LOGGER = LogManager.getLogger(AuthorizationListener.class);
	private static final String INDEX = "index.xhtml?faces-redirect=true";

	private static final String[] VALID_PAGES = new String[] { "/index.xhtml", "/register.xhtml",
			"/register_natural.xhtml", "/register_legal.xhtml" };

	@Override
	public void afterPhase(PhaseEvent event) {
		LOGGER.debug("Filtering navigation ...");

		FacesContext facesContext = event.getFacesContext();
		HttpSession session = (HttpSession) facesContext.getExternalContext().getSession(false);
		String currentPage = facesContext.getViewRoot().getViewId();

		boolean isLoggedIn = false;

		if (session != null) {
			UserControllerBean ucb = (UserControllerBean) session.getAttribute("ucb");
			if (ucb != null) {
				isLoggedIn = ucb.isLoggedIn();
			}
		}

		// pages without login
		if (contains(VALID_PAGES, currentPage) || isLoggedIn) {
			LOGGER.debug("Filtering navigation: not required.");
			return;
		}

		if (!isLoggedIn) {
			LOGGER.debug("Filtering navigation: required.");
			NavigationHandler nh = facesContext.getApplication().getNavigationHandler();
			nh.handleNavigation(facesContext, null, INDEX);
		}

	}

	public static <T> boolean contains(final T[] array, final T v) {
		for (final T e : array)
			if (e == v || v != null && v.equals(e))
				return true;

		return false;
	}

	@Override
	public void beforePhase(PhaseEvent event) {
		// TODO Auto-generated method stub

	}

	@Override
	public PhaseId getPhaseId() {
		return PhaseId.RESTORE_VIEW;
	}

}
