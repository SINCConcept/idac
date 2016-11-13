package ac.at.tuwien.mt.model.helper;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class DefaultJSONProvider {

	private static final Logger LOGGER = LogManager.getLogger(DefaultJSONProvider.class);

	public static String getObjectAsJson(Object object) {
		try {
			LOGGER.trace("Transforming object to JSON: initializing");
			ObjectMapper objectMapper = new ObjectMapper();
			String objectAsJson = objectMapper.writeValueAsString(object);
			LOGGER.trace("Transforming object to JSON: completed.");
			LOGGER.trace(objectAsJson);
			return objectAsJson;
		} catch (JsonGenerationException e) {
			LOGGER.error("Could not transform object to JSON", e);
		} catch (JsonMappingException e) {
			LOGGER.error("Could not transform object to JSON", e);
		} catch (IOException e) {
			LOGGER.error("Could not transform object to JSON", e);
		}
		return null;
	}

	public static String getObjectAsJson(Object object, boolean includeNullValues) {
		try {
			LOGGER.trace("Transforming object to JSON: initializing");
			ObjectMapper objectMapper = new ObjectMapper();
			if (includeNullValues == false) {
				objectMapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
			} else {
				// do nothing
			}
			String objectAsJson = objectMapper.writeValueAsString(object);
			LOGGER.trace("Transforming object to JSON: completed.");
			LOGGER.trace(objectAsJson);
			return objectAsJson;
		} catch (JsonGenerationException e) {
			LOGGER.error("Could not transform object to JSON", e);
		} catch (JsonMappingException e) {
			LOGGER.error("Could not transform object to JSON", e);
		} catch (IOException e) {
			LOGGER.error("Could not transform object to JSON", e);
		}
		return null;
	}

	public static Object getObjectFromJson(String json, Class<?> clazz) {
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			return objectMapper.readValue(json, clazz);
		} catch (JsonParseException e) {
			LOGGER.error("Could not transform JSON to object", e);
		} catch (JsonMappingException e) {
			LOGGER.error("Could not transform JSON to object", e);
		} catch (IOException e) {
			LOGGER.error("Could not transform JSON to object", e);
		}
		return null;
	}

	public static Object getObjectFromJson(byte[] json, Class<?> clazz) {
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			return objectMapper.readValue(json, clazz);
		} catch (JsonParseException e) {
			LOGGER.error("Could not transform JSON to object", e);
		} catch (JsonMappingException e) {
			LOGGER.error("Could not transform JSON to object", e);
		} catch (IOException e) {
			LOGGER.error("Could not transform JSON to object", e);
		}
		return null;
	}
}
