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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;


/**
 * Unit tests to verify yasson version bump from below PR
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
    
    public StateSummaryTests() {
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
     * Tests using {@link StateSummary}
     * 
     */
    @Test
    @Order(0)
    public void canMakeAndUseStateSummary() {}
    
    
    /**
     * Tests serializing {@link StateSummary} to Json
     * 
     */
    @Test
    @Order(1)
    public void canSerializeStateSummaryToJson() {}
    
    
    /**
     * Tests deserializing {@link StateSummary} from Json
     * 
     */
    @Test
    @Order(2)
    public void canDeserializeStateSummaryFromJson() {}
    
    
    /**
     * Tests writing {@link StateSummary} to JSON document file
     * 
     */
    @Test
    @Order(3)
    public void canWriteStateSummaryToJsonDocFile() {} 
    
    
    /**
     * Tests reading {@link StateSummary} from JSON document file
     * 
     */
    @Test
    @Order(4)
    public void canReadStateSummaryFromJsonDocFile() {}
}
