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
package org.tasktide.core.model.state_summary;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbException;

import java.util.List;
import java.util.Random;

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
 * Unit tests to verify yasson version bump from below PR. SerDe
 *  is all that is needed as JSON-B is already known to be "correct"
 * <br>
 * https://github.com/BrenKenna/TaskTide/pull/15
 * 
 * @author Bren
 */
@Tag("unit-core")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class StateSummaryTests {
    
    private final Logger LOGGER = LogManager.getLogger(StateSummaryTests.class);
    private final Random RAND;
    private final Jsonb JSON_B;
    
    public StateSummaryTests() {
        this.RAND = new Random();
        this.JSON_B = JsonbBuilder.create();
    }
    
    @BeforeAll
    public void setUpClass() {
        String msg = "\n\n---------------- Initiating State Summary Tests ----------------\n";
        LOGGER.info(msg);
    }
    
    @AfterAll
    public void tearDownClass() {
        String msg = "\n\n---------------- Terminating State Summary Tests ----------------\n";
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
     * Fetch randomly populated {@link ItemStateSummary}
     * 
     * @return {@link ItemStateSummary}
     */
    private StateSummary<ItemState> fetchRandomItemStateSummary() {
        
        // Initialize
        StateSummary<ItemState> itemStateSummary;
        itemStateSummary = StateSummaryProvider.makeItemStateSummary();
        
        // Populate with random values
        for ( ItemState elm : ItemState.values() ) {
            int val = this.RAND.nextInt(1, 10);
            itemStateSummary.setValueFor(elm, val);
        }
        
        // Return results
        return itemStateSummary;
    }
    
    
    /**
     * Fetch randomly populated {@link TaskStateSummary}
     * 
     * @return {@link TaskStateSummary}
     */
    private StateSummary<TaskState> fetchRandomTaskStateSummary() {
        
        // Initialize
        StateSummary<TaskState> taskStateSummary;
        taskStateSummary = StateSummaryProvider.makeTaskStateSummary();
        
        // Populate with random values
        for ( TaskState elm : TaskState.values() ) {
            int val = this.RAND.nextInt(1, 10);
            taskStateSummary.setValueFor(elm, val);
        }
        
        // Return results
        return taskStateSummary;
    }
    
    
    /**
     * Fetches a randomly generated {@link ItemStateSummary}
     *  JSON doc
     * 
     * @return String
     */
    private String fetchItemStateSummaryDoc() {
        return this.fetchRandomItemStateSummary().toJson(true);
    }
    
    
    /**
     * Fetches a randomly generated {@link TaskStateSummary}
     *  JSON doc
     * 
     * @return String
     */
    private String fetchTaskStateSummaryDoc() {
        return this.fetchRandomTaskStateSummary().toJson(true);
    }
    
    
    /**
     * Tests using {@link StateSummary}
     * 
     */
    @Test
    @Order(0)
    public void canMakeAndUseStateSummary() {
    
        // Initialize test parameters
        LOGGER.info("\n\n================ Can Make and Use State Summary ================\n");
        StateSummary<ItemState> itemStateSummary;
        int nFields,
            nExpected = ItemState.values().length,
            counter
        ;
        
        // Make item state summary
        itemStateSummary = StateSummaryProvider.makeItemStateSummary();
        nFields = itemStateSummary.summaryMap.size();
        LOGGER.info(
           "Verifying that the number of ItemState fields '{}' is the same as those from created '{}'",
           nExpected, nFields
        );
        Assertions.assertTrue(
            nFields == nExpected,
            "Unable to make item state summary"
        );
        
        // Generate random values for each, check that none 0 or below
        LOGGER.info(
            "Verifying that ItemState summary counts can be assigned per enum value:\t '{}'",
            List.of(ItemState.values())
        );
        counter = 0;
        for ( ItemState elm : ItemState.values() ) {
            int val = this.RAND.nextInt(1, 10);
            itemStateSummary.setValueFor(elm, val);
            val = itemStateSummary.getValueFor(elm);
            if ( val >= 1 ) {
                counter++;
            }
        }
        
        // Evaluate test
        Assertions.assertTrue(
            counter == ItemState.values().length,
            "Unable to verify that ItemState summary counts can be assigned per enum value"
        );
        LOGGER.info("\n\n================ assertionState ================\n");
    }
    
    
    /**
     * Tests serializing {@link StateSummary} to JSON doc
     *  resolving relevant PR means that this method does
     *  not fail because of an exception
     * 
     */
    @Test
    @Order(1)
    public void canSerializeStateSummaryToJson() {
    
        // Initialize test parameters
        LOGGER.info("\n\n================ Can Serialize to State Summary to JSON Doc ================\n");
        StateSummary<TaskState> taskStateSummary;
        taskStateSummary = this.fetchRandomTaskStateSummary();
        String json = "";
        
        // Try serialize
        try {
            json = taskStateSummary.toJson(true);
            LOGGER.info("JSON Serialization successful:\n\n'{}'", json);
        }
        catch (Exception ex) {
            LOGGER.error("JSON Serialization unsuccessful:\n\n", ex);
        }
        Assertions.assertTrue( !json.isEmpty(), "Unable to serialize StateSummary to JSON document");
        LOGGER.info("\n\n================ Can Serialize to State Summary to JSON Doc ================\n");
    }
    
    
    /**
     * Tests deserializing {@link StateSummary} from Json
     * resolving relevant PR means that this method does
     *  not fail because of an exception
     */
    @Test
    @Order(2)
    public void canDeserializeStateSummaryFromJson() {
    
        // Initialize test parameters
        LOGGER.info("\n\n================ Can Deserializing State Summary from JSON Doc ================\n");
        String JSON_DOC;
        StateSummary<ItemState> itemStateSummary = null;
        JSON_DOC = this.fetchItemStateSummaryDoc();
        
        // Try serialize
        try {
            LOGGER.info("Attempting to deserialize:\n'{}'", JSON_DOC);
            itemStateSummary = this.JSON_B.fromJson(JSON_DOC, ItemStateSummary.class);
            LOGGER.info("JSON Deserialization successful calling toString method:\n\n'{}'", itemStateSummary.toString());
        }
        catch (JsonbException ex) {
            LOGGER.error("JSON Serialization unsuccessful:\n\n", ex);
        }
        Assertions.assertTrue( itemStateSummary.summaryMap != null, "Unable to deerialize StateSummary from JSON document");
        LOGGER.info("\n\n================ Can Deserializing to State Summary from JSON Doc ================\n");
    }
}
