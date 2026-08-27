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
package org.tasktide.tasktide.client.config;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbConfig;

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
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;

import org.tasktide.parser.ArgumentTree;

import org.tasktide.parser.configuration.TaskTideConfig;

/**
 * Tests TaskTideConfigurer 
 * 
 * @author bkenna
 */
@Tag("unit-client")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TaskTideConfigurerTests {
    
    private static final Logger LOGGER = LogManager.getLogger(TaskTideConfigurerTests.class);
    private static final Jsonb PRETTY_JSON = JsonbBuilder.create(new JsonbConfig().withFormatting(true));
    
    public TaskTideConfigurerTests() {}
    
    @BeforeAll
    public void setUpClass() {        
        String msg = "\n\n---------------- Initiating Configuration Tests ----------------\n";
        LOGGER.info(msg);
    }
    
    @AfterAll
    public void tearDownClass() {
        String msg = "\n\n---------------- Terminating Configuration Tests ----------------\n";
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
     * Tests {@link GlobalConfig} {@link ArgumentTree}
     * 
     */
    @Test
    @Order(0)
    public void canDisplayGlobalArgTree() {
    
        // Initialize data
        LOGGER.info("\n\n================ Tests Displaying Global Configs  ================\n");
        ArgumentTree argTree;
        TaskTideConfig config;
        boolean assertionState;
        
        // Setup global config
        LOGGER.info("Applying global configs for client");
        argTree = new ArgumentTree(" ");
        config = new GlobalConfig("");
        config.initConfig(argTree);
        LOGGER.info("Records in tree:\n'{}'", argTree.getTree().size());
        LOGGER.info("Displaying configured client:\n'{}'", PRETTY_JSON.toJson(argTree.getVerboseHelp()) );
        
        // Evaluate test
        assertionState = argTree.getTree().size() > 0;
        assertTrue(assertionState, "Not all records added");
        LOGGER.info("\n\n================ Tests Displaying Global Configs ================\n");
    }
    
    
    /**
     * Tests {@link ManagerConfig} {@link ArgumentTree}
     * 
     */
    @Test
    @Order(1)
    public void canDisplayManagerArgTree() {
    
        // Initialize data
        LOGGER.info("\n\n================ Tests Displaying Manager Configs  ================\n");
        ArgumentTree argTree;
        TaskTideConfig config;
        boolean assertionState;
        
        // Setup global config
        LOGGER.info("Applying manager configs for client");
        argTree = new ArgumentTree(" ");
        config = new ManagerConfig("");
        config.initConfig(argTree);
        LOGGER.info("Records in tree:\n'{}'", argTree.getTree().size());
        LOGGER.info("Displaying configured client:\n'{}'", PRETTY_JSON.toJson(argTree.getVerboseHelp()) );
        
        // Evaluate test
        assertionState = argTree.getTree().size() > 0;
        assertTrue(assertionState, "Not all records added");
        LOGGER.info("\n\n================ Tests Displaying Manager Configs ================\n");
    }
    
    
    /**
     * Tests {@link EngineConfig} {@link ArgumentTree}
     * 
     */
    @Test
    @Order(2)
    public void canDisplayEngineArgTree() {
    
        // Initialize data
        LOGGER.info("\n\n================ Tests Displaying Engine Configs  ================\n");
        ArgumentTree argTree;
        TaskTideConfig config;
        boolean assertionState;
        
        // Setup global config
        LOGGER.info("Applying engine configs for client");
        argTree = new ArgumentTree(" ");
        config = new EngineConfig("");
        config.initConfig(argTree);
        LOGGER.info("Records in tree:\n'{}'", argTree.getTree().size());
        LOGGER.info("Displaying configured client:\n'{}'", PRETTY_JSON.toJson(argTree.getVerboseHelp()) );
        
        // Evaluate test
        assertionState = argTree.getTree().size() > 0;
        assertTrue(assertionState, "Not all records added");
        LOGGER.info("\n\n================ Tests Displaying Engine Configs ================\n");
    }
    
    
    /**
     * Tests {@link WebApiConfig} {@link ArgumentTree}
     * 
     */
    @Test
    @Order(3)
    public void canDisplayApiArgTree() {
    
        // Initialize data
        LOGGER.info("\n\n================ Tests Displaying WebApiConfig Configs  ================\n");
        ArgumentTree argTree;
        TaskTideConfig config;
        boolean assertionState;
        
        // Setup global config
        LOGGER.info("Applying WebApiConfig configs for client");
        argTree = new ArgumentTree(" ");
        config = new WebApiConfig("");
        config.initConfig(argTree);
        LOGGER.info("Records in tree:\n'{}'", argTree.getTree().size());
        LOGGER.info("Displaying configured client:\n'{}'", PRETTY_JSON.toJson(argTree.getVerboseHelp()) );
        
        // Evaluate test
        assertionState = argTree.getTree().size() > 0;
        assertTrue(assertionState, "Not all records added");
        LOGGER.info("\n\n================ Tests Displaying WebApiConfig Configs ================\n");
    }
}