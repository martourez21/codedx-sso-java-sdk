package com.codedstreams.codedx.sso.sdk.util;

import com.codedstreams.codedx.sso.sdk.exception.SsoException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;

/**
 * Utility class for JSON serialization and deserialization.
 * <p>
 * This class provides methods to convert objects to JSON and vice versa
 * using Jackson ObjectMapper with proper configuration for Java 8 dates.
 * </p>
 *
 * @author Nestor Martourez
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
public class JsonUtils {

    private static final ObjectMapper objectMapper = createObjectMapper();

    /**
     * Creates and configures an ObjectMapper instance.
     *
     * @return configured ObjectMapper instance
     */
    private static ObjectMapper createObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        return mapper;
    }

    /**
     * Converts an object to JSON string.
     *
     * @param object the object to convert
     * @return JSON string representation
     * @throws SsoException if conversion fails
     */
    public static String toJson(Object object) throws SsoException {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (Exception e) {
            log.error("Failed to convert object to JSON", e);
            throw new SsoException("JSON conversion failed", e);
        }
    }

    /**
     * Parses JSON string into an object of the specified class.
     *
     * @param <T> the type of the object
     * @param json the JSON string to parse
     * @param clazz the class of the object
     * @return parsed object
     * @throws SsoException if parsing fails
     */
    public static <T> T fromJson(String json, Class<T> clazz) throws SsoException {
        try {
            return objectMapper.readValue(json, clazz);
        } catch (Exception e) {
            log.error("Failed to parse JSON: {}", json, e);
            throw new SsoException("JSON parsing failed: " + e.getMessage(), e);
        }
    }

    /**
     * Gets the shared ObjectMapper instance.
     *
     * @return the ObjectMapper instance
     */
    public static ObjectMapper getObjectMapper() {
        return objectMapper;
    }
}