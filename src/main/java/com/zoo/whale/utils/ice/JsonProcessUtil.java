package com.zoo.whale.utils.ice;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonProcessUtil {
	private static volatile ObjectMapper objectMapper;

	public static ObjectMapper getMapperInstance() {
		if (objectMapper == null) {
			synchronized (ObjectMapper.class) {
				if (objectMapper == null) {
					objectMapper = new ObjectMapper();
					objectMapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
					objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
					objectMapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
					objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
					objectMapper.configure(JsonParser.Feature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER, true);
					objectMapper.setSerializationInclusion(Include.NON_NULL);

					/**
					 * 解决传入参数和bean不对等的问题
					 */
					objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
					objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
				}
			}
		}
		return objectMapper;
	}
}