package ac.at.tuwien.mt.gui.internal;

import java.util.ResourceBundle;

public class WebServicesProvider {

	private static final ResourceBundle BUNDLE = ResourceBundle.getBundle("webservices");

	/**
	 * Returns the value as String for the given key.
	 *
	 * @param query
	 *            the properties key
	 * @return String value of the property
	 * @see ResourceBundle#getString(String)
	 */
	public static String getString(WebServiceProperty property) {
		return BUNDLE.getString(property.getProperty());
	}
}
