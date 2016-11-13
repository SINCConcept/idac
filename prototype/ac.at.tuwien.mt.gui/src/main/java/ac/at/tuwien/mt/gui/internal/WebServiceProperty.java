package ac.at.tuwien.mt.gui.internal;

public enum WebServiceProperty {

	REST_MIDDLEWARE_BASEURL("rest.middleware.baseurl"), //
	REST_MIDDLEWARE_MICROSERVICE_PATH("rest.middleware.microservice.path"), //
	REST_MIDDLEWARE_MICROSERVICE_LOCATION_PATH("rest.middleware.microservice.location.path"), //

	REST_PERSON_LEGAL_PATH("rest.person.legal.path"), //
	REST_PERSON_NATURAL_PATH("rest.person.natural.path"), //
	REST_PERSON_FIND_PATH("rest.person.find.path")//
	;

	private String property;

	private WebServiceProperty(String property) {
		this.property = property;
	}

	/**
	 * @return the property
	 */
	public String getProperty() {
		return property;
	}

	/**
	 * @param property
	 *            the property to set
	 */
	public void setProperty(String property) {
		this.property = property;
	}
}
