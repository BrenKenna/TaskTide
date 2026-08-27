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
package org.tasktide.itemstore;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.TestMethodOrder;


/**
 * Tests {@link Item} construction
 * 
 * @author bkenna
 */
@Tag("base")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ItemTests {
    
    private static final Logger logger = LogManager.getLogger(ItemTests.class);
    
    public ItemTests() {}
    
    
    @BeforeAll
    public static void setUpClass() {        
        String msg = "\n\n---------------- Initiating Item Tests ----------------\n";
        logger.info(msg);
    }
    
    @AfterAll
    public static void tearDownClass() {
        String msg = "\n\n---------------- Terminating Item Tests ----------------\n";
        logger.info(msg);
    }
    
    
    @BeforeEach
    public void setUp() {
        logger.info("\n\n================ Initiating Next Test ================\n");
    }
    
    @AfterEach
    public void tearDown() {
        logger.info("\n\n================ Terminating Test ================\n");
    }

    
    @Test
    @Order(0)
    public void canMakeItem() {
    
        // Initialize test
        logger.info("\n\n================ Can make Item Test ================\n");
        boolean assertionState = true;
        int passed = 0, expected = 3;
        String id = "myId", state = "todo", step = "my-step", payload = "my cool payload";
        Item testUnit;
        
        // Create an example ItemStore Item
        testUnit = new Item<String>(id, state, step, payload);
        
        // Verifying attributes
        logger.info("Verifying attributes of created Item:\n\n{}", testUnit.toString());
        if ( id.equals( testUnit.getId() ) ) {passed++;}
        if ( state.equals( testUnit.getState() ) ) {passed++;}
        if ( payload.equals( testUnit.getPayload() ) ) {passed++;}
        
        
        // End test
        assertTrue(passed == expected, "Not all tasks processed correctly");
        logger.info("\n\n================ Can make Item Test ================\n");
    }
}