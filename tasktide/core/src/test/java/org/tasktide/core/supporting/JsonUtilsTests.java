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
import jakarta.json.JsonNumber;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.json.JsonValue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;


/**
 * Suite of tests against how TaskTide uses JSON SerDe to help capture
 *  any issues in how the {@link JsonUtils} uses its dependency
 * <br>
 * Need arised from below issues:
 * <br>
 * 1). https://github.com/BrenKenna/TaskTide/issues/90
 * <br>
 * 2). https://github.com/BrenKenna/TaskTide/issues/79
 *
 * @author Bren
 */
@Tag("unit-core")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class JsonUtilsTests {
    
    private final Logger LOGGER = LogManager.getLogger(JsonUtilsTests.class);
    
    public JsonUtilsTests() {
    }
    
    @BeforeAll
    public void setUpClass() {
        String msg = "\n\n---------------- Initiating Json Utils Tests ----------------\n";
        LOGGER.info(msg);
    }
    
    @AfterAll
    public void tearDownClass() {
        String msg = "\n\n---------------- Terminating Json Utils Tests ----------------\n";
        LOGGER.info(msg);
    }
    
    @BeforeEach
    public void setUp() {
        LOGGER.info("\n\n================ Initiating Next Test ================\n");
    }
    
    @AfterEach
    public void tearDown() {
        LOGGER.info("\n\n================ Terminating Test ================\n");
    }

    
    /**
     * Verifies representing Objects as JsonValue
     * 
     */
    @Test
    @Order(0)
    public void canRepresentValue() {
    
        // Initialize data
        LOGGER.info("\n\n================ Can Represent a Value under a Key as JSON ================\n");
        JsonValue jsonVal;
        Object value = 10;
        
        // Represent as json value
        jsonVal = JsonUtils.asJsonValue(value);
        LOGGER.info(
            "Value '{}' was represented as '{}'",
            value, jsonVal
        );
        
        // Evaluate json value
        LOGGER.info("Verifying that the JsonValue is a 'JsonValue.ValueType.NUMBER'");
        Assertions.assertEquals(
            JsonValue.ValueType.NUMBER,
            jsonVal.getValueType()
        );
        LOGGER.info(
            "Verifying that the JsonValue is '{}'",
            value
        );
        Assertions.assertEquals(value, ((JsonNumber) jsonVal).intValue());
        LOGGER.info("\n\n================ Can Represent a Value under a Key as JSON ================\n");
    }
    
    
    /**
     * Verifies that the JsonUtils can detect value format
     * 
     */
    @Test
    @Order(1)
    public void canDetectValueFormat() {
    
        // Initialize data
        LOGGER.info("\n\n================ Can Detect Value Format ================\n");
        Object value = 10;
        boolean isJsonObject;
        
        // Perform test
        LOGGER.info("Exmaning format of value");
        isJsonObject = JsonUtils.detectFormat(value);
        Assertions.assertFalse(isJsonObject);
        
        // Log state for clarity
        LOGGER.info("Handling test state");
        if ( !isJsonObject ) {
            LOGGER.info("Test successful, detected that Object is just a value");
        }
        else {
            LOGGER.error("Test failed, unable to detect that Object is just a value");
        }
        LOGGER.info("\n\n================ Can Detect Value Format ================\n");
    }
    
    
    /**
     * Verifies that the {@link JsonUtils} can detect
     *  JsonObjects
     * 
     */
    @Test
    @Order(2)
    public void canDetectJsonFormat() {
    
        // Initialize data
        LOGGER.info("\n\n================ Can Detect JSON Format ================\n");
        Object value = 10;
        String key = "Value";
        JsonObject data;
        boolean isJsonObject;
        
        // Build JSON object
        LOGGER.info("Creating JsonObject");
        JsonObjectBuilder builder = Json.createObjectBuilder();
        data = builder
            .add(key, (int) value)
        .build();
        
        // Verify format
        LOGGER.info("Detecting format");
        isJsonObject = JsonUtils.detectFormat(data);
        Assertions.assertTrue(isJsonObject);
        
        // Log state for clarity
        LOGGER.info("Handling test state");
        if ( isJsonObject ) {
            LOGGER.info("Test successful, detected that Object is JSON formatted");
        }
        else {
            LOGGER.error("Test failed, unable to detect that Object is JSON formatted");
        }
        LOGGER.info("\n\n================ Can Detect Value Format ================\n");
    }
    
    
    /**
     * Verifies that the {@link JsonUtils} can represent data as Json generally.
     *  Using its public static JsonObject asJson(String key, Object value)
     * 
     */
    @Test
    @Order(3)
    public void canRepresentValueDataAsJsonGenerally() {
    
        // Initialize data
        LOGGER.info("\n\n================ Can Represent Value in JSON Format ================\n");
        Object value = 10;
        String key = "Value";
        JsonObject data;
        boolean testState;
        
        // Convert data
        data = JsonUtils.asJson(key, value);
        LOGGER.info(
            "Displaying JSON object:\t'{}'",
            data
        );
        
        // Examine object
        LOGGER.info(
            "Examining the represented value is:\t'{}'",
            value
        );
        testState = data.getInt(key) == (int) value;
        Assertions.assertTrue(testState);
        
        // Log test state for clarity
        if ( testState ) {
            LOGGER.info(
                "Value successfully represented as JSON:\t'{}'",
                testState
            );
        }
        else {
            LOGGER.error(
                "Unable to represent value as JSON:\t'{}'",
                testState
            );
        }
        LOGGER.info("\n\n================ Can Represent Value in JSON Format ================\n");
    }
    
    
    /**
     * Verifies that the {@link JsonUtils} can represent data as Json generally.
     *  Using its public static JsonObject asJson(String key, Object value)
     * 
     */
    @Test
    @Order(4)
    public void canRepresentJsonObjectDataAsJsonGenerally() {
    
        // Initialize data
        LOGGER.info("\n\n================ Can Represent Data JSON Format ================\n");
        Object value = 10;
        String key = "Json Value", objectKey = "Key";
        JsonObject data, jsonData;
        boolean testState;
        
        // Convert data
        data = JsonUtils.asJson(key, value);
        jsonData = JsonUtils.asJson(objectKey, data);
        LOGGER.info(
            "Displaying converted object:\t'{}'",
            jsonData
        );
        testState = jsonData.containsKey("Key");
        
        // Log test state for clarity
        if ( testState ) {
            LOGGER.info(
                "Value successfully represented as JSON:\t'{}'",
                testState
            );
        }
        else {
            LOGGER.error(
                "Unable to represent value as JSON:\t'{}'",
                testState
            );
        }
        LOGGER.info("\n\n================ Can Represent Data JSON Format ================\n");
    }
    
    
    /**
     * Verifies that {@link JsonUtils} can represent JsonObjects
     *  as JSON document string
     * 
     */
    @Test
    @Order(5)
    public void canRepresentJsonObjectAsJsonDocString() {
    
        // Initialize data
        LOGGER.info("\n\n================ Can Represent Data JSON-Doc String Format ================\n");
        Object value = 10;
        String key = "Json Value", objectKey = "Key";
        JsonObject data, jsonData;
        String jsonDoc;
        boolean testState;
        
        // Convert data
        data = JsonUtils.asJson(key, value);
        jsonData = JsonUtils.asJson(objectKey, data);
        LOGGER.info(
            "Displaying converted object:\t'{}'",
            jsonData
        );
        
        // Represent as JSON doc string
        LOGGER.info("Representing the json data as a json documented");
        jsonDoc = JsonUtils.toJson(true, jsonData);
        testState = jsonDoc.length() > 10;
        LOGGER.info(
            "Test state detected as '{}'. Displaying JSON Doc:\n\n'{}'",
            testState, jsonDoc
        );
        Assertions.assertTrue(jsonDoc.length() > 10);
        
        // Log test state for clarity
        if ( testState ) {
            LOGGER.info("Successfully represented data as JSON-Document string");
        }
        else {
            LOGGER.error("Unable to represent data as JSON-Document string");
        }
        LOGGER.info("\n\n================ Can Represent Data JSON-Doc String Format ================\n");
        
    }
}
