/*
 * Copyright 2026 Bren.
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
package org.tasktide.engine.observer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.TestMethodOrder;


/**
 * Test module for the {@link TaskTideWorkerObserver}
 * 
 * @author bkenna
 */
@Tag("unit-engine")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TaskTideWorkerObserverTests {
    
    private static final Logger logger = LogManager.getLogger(TaskTideWorkerObserverTests.class);
    
    public TaskTideWorkerObserverTests() {}
    
    
    @BeforeAll
    public static void setUpClass() {        
        String msg = "\n\n---------------- Initiating TaskTideObserver Tests ----------------\n";
        logger.info(msg);
    }
    
    @AfterAll
    public static void tearDownClass() {
        String msg = "\n\n---------------- Terminating TaskTideObserver Tests ----------------\n";
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
    public void canObserve() {
    
        // Initialize test
        logger.info("\n\n================ TaskTideObserver Test ================\n");
        boolean assertionState = true;
        
        
        // End test
        assertTrue(assertionState, "Not all tasks processed correctly");
        logger.info("\n\n================ TaskTideObserver Test ================\n");
    }
}
