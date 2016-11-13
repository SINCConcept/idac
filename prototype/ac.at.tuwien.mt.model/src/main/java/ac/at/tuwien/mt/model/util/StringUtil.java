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
package ac.at.tuwien.mt.model.util;

/**
 * Utility class.
 * 
 * @author Florin Bogdan Balint
 *
 */
public final class StringUtil {

	/**
	 * Verifies whether a string is null, blank or empty.
	 * 
	 * @param string
	 * @return
	 */
	public static boolean isNullOrBlank(String string) {
		if (string == null) {
			return true;
		}
		if (string.isEmpty()) {
			return true;
		}
		if (string.trim().isEmpty()) {
			return true;
		}
		return false;
	}
}
