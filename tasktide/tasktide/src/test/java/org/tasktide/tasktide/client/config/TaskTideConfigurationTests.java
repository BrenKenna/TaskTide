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

import jakarta.enterprise.inject.se.SeContainer;

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

import org.tasktide.parser.ArgumentTree;
import org.tasktide.parser.HelpMessageType;
import org.tasktide.parser.configuration.TaskTideConfig;
import org.tasktide.tasktide.TestUtils;


/**
 * Verifies that the {@link GlobalConfig}, {@link ManagerConfig},
 *  {@link EngineConfig}, and {@link WebApiConfig}
 * 
 * @author bkenna
 */
@Tag("unit-tasktide")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TaskTideConfigurationTests {
    
    private static final Logger LOGGER = LogManager.getLogger(TaskTideConfigurationTests.class);
  
    private SeContainer container;
    
    public TaskTideConfigurationTests() {}
    
    
    @BeforeAll
    public void setUpClass() {
        String msg = "\n\n---------------- Initiating Configuration from TaskTide-Config Tests ----------------\n";
        TestUtils.initSeContainer();
        this.container = TestUtils.fetchConfiguredContainer();
        LOGGER.info(msg);
    }
    
    
    @AfterAll
    public void tearDownClass() {
        String msg = "\n\n---------------- Terminating Configuration from TaskTide-Config Tests ----------------\n";
        LOGGER.info(msg);
    }
    
    
    @BeforeEach
    public void setUp() {
    }
    
    @AfterEach
    public void tearDown() {
    }

    
    /**
     * Tests that the global client can be configured from micro-profile config
     * 
     */
    @Test
    @Order(0)
    public void canConfigureGlobalClient() {
    
        // Initialize data
        LOGGER.info("\n\n================ Tests Configuring Global Client Through Config  ================\n");
        ArgumentTree argTree;
        TaskTideConfig globalConfig;
        
        // Configure client parameters
        argTree = new ArgumentTree("tasktide");
        globalConfig = this.container.select(GlobalConfig.class).get();
        globalConfig.initConfig(argTree);
        
        // Verify
        Assertions.assertTrue(globalConfig.getArgumentMap() != null, "Argument map should not be null");
        Assertions.assertTrue(globalConfig.getArgumentMap().getArgMap().size() >= 1, "Argument map should contain more than one parameter");
        LOGGER.info("Displaying configured client:\n'{}'", globalConfig.getArgumentMap());
        Assertions.assertTrue(argTree.getVerboseHelp() != null, "Verbose help should not be null");
        LOGGER.info("Displaying verbose help:\n'{}'", argTree.getFormattedHelp(HelpMessageType.GLOBAL));
        LOGGER.info("\n\n================ Tests Running EngineClient Through Config  ================\n");
    }
    
    
    /**
     * Tests that the engine can be configured from micro-profile config
     * 
     */
    @Test
    @Order(1)
    public void canConfigureEngineClient() {
    
        // Initialize data
        LOGGER.info("\n\n================ Tests Running EngineClient Through Config  ================\n");
        ArgumentTree argTree;
        TaskTideConfig globalConfig, engineConfig;
        
        // Configure client parameters
        argTree = new ArgumentTree("tasktide");
        globalConfig = this.container.select(GlobalConfig.class).get();
        globalConfig.initConfig(argTree);
        engineConfig = this.container.select(EngineConfig.class).get();
        engineConfig.initConfig(argTree);
        
        // Verify
        Assertions.assertTrue(engineConfig.getArgumentMap() != null, "Argument map should not be null");
        Assertions.assertTrue(engineConfig.getArgumentMap().getArgMap().size() >= 1, "Argument map should contain more than one parameter");
        LOGGER.info("Displaying configured client:\n'{}'", engineConfig.getArgumentMap());
        Assertions.assertTrue(argTree.getVerboseHelp() != null, "Verbose help should not be null");
        LOGGER.info("Displaying verbose help:\n'{}'", argTree.getFormattedHelp(HelpMessageType.VERBOSE));
        LOGGER.info("\n\n================ Tests Running EngineClient Through Config  ================\n");
    }
    
    
    /**
     * Tests that the manager client can be configured from micro-profile config
     * 
     */
    @Test
    @Order(2)
    public void canConfigureManagerClient() {
    
        // Initialize data
        LOGGER.info("\n\n================ Tests Configuring Manager Client Through Config  ================\n");
        ArgumentTree argTree;
        TaskTideConfig globalConfig, managerConfig;
        
        // Configure client parameters
        argTree = new ArgumentTree("tasktide");
        globalConfig = this.container.select(GlobalConfig.class).get();
        globalConfig.initConfig(argTree);
        managerConfig = this.container.select(EngineConfig.class).get();
        managerConfig.initConfig(argTree);
        
        // Verify
        Assertions.assertTrue(managerConfig.getArgumentMap() != null, "Argument map should not be null");
        Assertions.assertTrue(managerConfig.getArgumentMap().getArgMap().size() >= 1, "Argument map should contain more than one parameter");
        LOGGER.info("Displaying configured client:\n'{}'", managerConfig.getArgumentMap());
        Assertions.assertTrue(argTree.getVerboseHelp() != null, "Verbose help should not be null");
        LOGGER.info("Displaying verbose help:\n'{}'", argTree.getFormattedHelp(HelpMessageType.VERBOSE));
        LOGGER.info("\n\n================ Tests Running Manager Client Through Config  ================\n");
    }
    
    
    /**
     * Tests that the web-api client can be configured from micro-profile config
     * 
     */
    @Test
    @Order(3)
    public void canConfigureWebApi() {
    
        // Initialize data
        LOGGER.info("\n\n================ Tests Configuring Web API Through Config  ================\n");
        ArgumentTree argTree;
        TaskTideConfig globalConfig, webApiConfig;
        
        // Configure client parameters
        argTree = new ArgumentTree("tasktide");
        globalConfig = this.container.select(GlobalConfig.class).get();
        globalConfig.initConfig(argTree);
        webApiConfig = new WebApiConfig();
        webApiConfig.initConfig(argTree);
        
        // Verify
        Assertions.assertTrue(webApiConfig.getArgumentMap() != null, "Argument map should not be null");
        Assertions.assertTrue(webApiConfig.getArgumentMap().getArgMap().size() >= 1, "Argument map should contain more than one parameter");
        LOGGER.info("Displaying configured client:\n'{}'", webApiConfig.getArgumentMap());
        Assertions.assertTrue(argTree.getVerboseHelp() != null, "Verbose help should not be null");
        LOGGER.info("Displaying verbose help:\n\n'{}'", argTree.getFormattedHelp(HelpMessageType.VERBOSE));
        LOGGER.info("\n\n================ Tests Running Web API Through Config  ================\n");
    }
}