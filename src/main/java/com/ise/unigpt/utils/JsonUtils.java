package com.ise.unigpt.utils;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * JsonUtils
 * Json的序列化和反序列化工具类
 */
public class JsonUtils {
    public static String toJson(Object obj) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(obj);
            return json;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
    public static <T> T fromJson(String json, Class<T> clazz) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            T obj = mapper.readValue(json, clazz);
            return obj;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
