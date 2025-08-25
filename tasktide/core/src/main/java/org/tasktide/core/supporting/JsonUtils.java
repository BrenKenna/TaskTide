/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.tasktide.core.supporting;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.json.JsonValue;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbConfig;

import java.io.StringReader;

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
    
    
    /**
     * Provide JSON Binding to either (un)indented binder
     * 
     * @param prettyPrint
     * @return {@link Jsonb}
     */
    public static Jsonb getJsonb(boolean prettyPrint) {
        if ( prettyPrint ) {
            return PRETTY_JSON;
        }
        else {
            return JSON;
        }
    }
    
    
    /**
     * Convert string to json
     * 
     * @param jsonString
     * @return JsonObject
     */
    public static JsonObject stringToJson(String jsonString) {
        JsonReader reader = Json.createReader( new StringReader(jsonString) );
        return reader.readObject();
    }
    
    
    /**
     * Fetch normalized field from JsonObject. Normalization sets to lowercase
     *  , drops trailing whitespaces, and then drops spaces.
     * 
     * @param field
     * @param data
     * @return JsonValue
     */
    public static String fetchStringFieldFromJson(String field, JsonObject data) {
        field = field.toLowerCase().trim().replace(" ", "");
        field = field.replace("-", "").replace("_", "");
        return data.getString(field);
    }
    
    
    public static JsonValue fetchObjectFromJson(String field, JsonObject data) {
        field = field.toLowerCase().trim().replace(" ", "");
        field = field.replace("-", "").replace("_", "");
        return data.get(field);
    }
}