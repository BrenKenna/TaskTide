/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.tasktide.core.supporting;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbConfig;

import java.lang.reflect.Type;


/**
 * Single class for JSON Serde with(out) indentation
 * 
 * @author bkenna
 */
public class JsonUtils {
    
    private static final JsonbConfig PRETTY_CONFIG = new JsonbConfig().withFormatting(true);
    private static final Jsonb PRETTY_JSON = JsonbBuilder.create(PRETTY_CONFIG);
    private static final Jsonb JSON = JsonbBuilder.create();
    
    
    /**
     * Serialize to JSON string
     * 
     * @param indent
     * @param input
     * @return String
     */
    public static String toJson(boolean indent, Object input) {
        if (indent) {
            return PRETTY_JSON.toJson(input);
        }
        return JSON.toJson(input);
    }
    
    
    /**
     * Deserialize class from JSON string
     * 
     * @param <T>
     * @param json
     * @param type
     * @return T
     */
    public static <T> T fromJson(String json, Class<T> type) {
        return JSON.fromJson(json, type);
    }
    
    
    /**
     * Deserialize class from JSON string
     * 
     * @param <T>
     * @param json
     * @param type
     * @return T
     */
    public static <T> T fromJson(String json, Type type) {
        return JSON.fromJson(json, type);
    }
}
