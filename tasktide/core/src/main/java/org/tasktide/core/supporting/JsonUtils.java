/*
 * Copyright 2025 Brendan Kenna.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.tasktide.core.supporting;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.json.JsonReader;
import jakarta.json.JsonValue;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbConfig;

import java.io.StringReader;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * Single class for JSON Serde with(out) indentation
 * 
 * @author bkenna
 */
public class JsonUtils {
    
    private static final Logger LOGGER = LogManager.getLogger(JsonUtils.class);
    private static final JsonbConfig PRETTY_CONFIG = new JsonbConfig().withFormatting(true);
    private static final Jsonb PRETTY_JSON = JsonbBuilder.create(PRETTY_CONFIG);
    private static final Jsonb JSON = JsonbBuilder.create();
    
    
    /**
     * Parse provided JSON doc into formatted JSON doc
     * 
     * @param jsonDoc
     * @return String
     */
    public static String toJson(String jsonDoc) {
        JsonObject json = Json
            .createReader(
                new StringReader(jsonDoc)
            )
        .readObject();
        return PRETTY_JSON.toJson(json);
    }

    
    /**
     * Serialize to JSON string
     * 
     * @param indent
     * @param input
     * 
     * @return String
     */
    public static String toJson(boolean indent, Object input) {
        if (indent) {
            String doc = PRETTY_JSON.toJson(input);
            return JsonUtils.toJson(doc);
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
     * Fetch map from json string
     * 
     * @param json
     * @return Map-String, Object
     */
    public static Map<String, Object> mapFromJson(String json) {
        Type type = new HashMap<String, Object>() {}.getClass().getGenericSuperclass();
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
    
    
    /**
     * Represent provided JSON string as {@link JsonObject}
     * 
     * @param jsonString
     * 
     * @return {@link JsonObject}
     */
    public static JsonObject asJson(String jsonString) {
        return Json.createReader(
            new StringReader(jsonString)
        ).readObject();
    }
    
    
    /**
     * Parse data as JsonValue
     * 
     * @param value
     * 
     * @return JsonValue
     */
    public static JsonValue asJsonValue(Object value) {
        return Json.createReader(
            new StringReader(JSON.toJson(value))
        ).readValue();
    }
    
    
    /**
     * Represent provided object as {@link JsonObject}
     * 
     * @param indent
     * @param input
     * 
     * @return {@link JsonObject} 
     */
    public static JsonObject asJson(boolean indent, Object input) {
        String jsonString = JsonUtils.toJson(indent, input);
        return JsonUtils.asJson(jsonString);
    }
    
    
    /**
     * Detects whether provided data is JSON formatted
     * 
     * @param value
     * 
     * @return boolean
     * 
     * @throws IllegalStateException
     */
    public static boolean detectFormat(Object value) {
    
        // Try parse
        try {
            String parsed = JsonUtils.toJson(true, value);
            if ( parsed != null ) {
                LOGGER.info("Detected provided data as a JSON Object");
                return true;
            }
            else {
                LOGGER.warn("Unable to parse provided data as JSON object");
                throw new IllegalStateException("Unable to parse provided data as JSON object");
            }
        }
        
        // Otherwise any exception forces a false
        catch (Exception ex) {
            LOGGER.debug(
                "Unable to parse provided data as JSON object, falling back to value\n\n",
                ex
            );
            return false;
        }
    
    }
    
    
    /**
     * Represent provided value under provided key as JSON object
     * 
     * @param key
     * @param value
     * @return JsonObject
     */
    public static JsonObject asJson(String key, Object value) {
        
        // Initialize required variables
        boolean isJson;
        JsonObjectBuilder builder;
        
        // Configure builder
        builder = Json.createObjectBuilder();
        
        // Determine value state
        isJson = JsonUtils.detectFormat(value);
        
        // Parse the provided json object
        if ( isJson ) {
            JsonObject jsonValue = JsonUtils.asJson(true, value);
            builder.add(key, jsonValue);
        }
        
        // Otherwise parse value
        else {
            JsonValue jsonVal = JsonUtils.asJsonValue(value);
            builder.add(key, jsonVal);
        }
        
        // Return resulting json object
        return builder.build();
    }
    
    
    /**
     * Wrapper method for callers to represent the provided value data
     *  under provided key. Boolean flag for JSON string/document
     * 
     * @param indent
     * @param key
     * @param value
     * 
     * @return String
     */
    public static String toJson(boolean indent, String key, Object value) {
        JsonObject jsonData = JsonUtils.asJson(key, value);
        if (indent) {
            return PRETTY_JSON.toJson(jsonData);
        }
        return JSON.toJson(jsonData);
    }
}