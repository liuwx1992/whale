package com.zoo.whale.utils.ice;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JacksonUtil {
	private static volatile ObjectMapper objectMapper;

	public static ObjectMapper getMapperInstance() {
		if (objectMapper == null) {
			synchronized (JacksonUtil.class) {
				if (objectMapper == null) {
					objectMapper = new ObjectMapper();
				}
			}
		}
		return objectMapper;

	}

	public static boolean isEmpty(JsonNode jsonNode) {
		if (jsonNode == null) {
			return true;
		} else if (jsonNode.isObject()) {
			if (jsonNode.size() == 0) {
				return true;
			}
		} else if (jsonNode.isArray()) {
			if (jsonNode.size() == 0) {
				return true;
			}
		} else if (jsonNode.asText().trim().length() == 0) {
			return true;
		}
		return false;
	}

	public static boolean isNotEmpty(JsonNode jsonNode) {
		return !isEmpty(jsonNode);
	}
}
