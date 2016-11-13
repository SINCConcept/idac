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
package ac.at.tuwien.mt.gui.primefaces;

import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.context.FacesContext;

/**
 * @author Florin Bogdan Balint
 *
 */
public class Messages {

	/**
	 * 
	 * @param severity
	 * @param message
	 */
	public static void addMessage(Severity severity, String message) {
		FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, message, null));
	}

}
