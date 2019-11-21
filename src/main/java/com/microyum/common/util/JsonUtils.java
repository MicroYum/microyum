package com.microyum.common.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;


public class JsonUtils {

    private static ObjectMapper mapper = new ObjectMapper();

    public static String toJson(Object obj) {
        try {
            return mapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
           throw new RuntimeException("to Json failed");
        }
    }

    public static <T> T fromJson(String json, Class<T> classOfT){
        try {
            return mapper.readValue(json, classOfT);
        } catch (IOException e) {
            throw new RuntimeException("from Json failed");
        }
    }

    public static <T> T fromJson(String json, TypeReference typeOfT) throws IOException {
        return mapper.readValue(json, typeOfT);
    }

    public static <T> T convertValue(Object value, TypeReference typeReference) {
        return mapper.convertValue(value, typeReference);
    }

    public static String prettyJson(Object object) {
        try {
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("prettyJson failed");
        }
    }

}
