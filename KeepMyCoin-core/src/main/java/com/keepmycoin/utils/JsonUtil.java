package com.keepmycoin.utils;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.keepmycoin.data.AbstractKMCData;

public class JsonUtil {

	public static <T extends AbstractKMCData> T parse(String content, Class<T> clz) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			return mapper.readValue(content, clz);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static <T extends AbstractKMCData> String toJSon(T obj) {
		try {
			return new ObjectMapper().writeValueAsString(obj);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}
}
