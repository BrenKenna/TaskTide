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
import jakarta.enterprise.inject.se.SeContainerInitializer;
import jakarta.enterprise.inject.spi.CDI;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jboss.weld.junit5.auto.AddPackages;
import org.jboss.weld.junit5.auto.EnableAutoWeld;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import org.tasktide.core.manager.command.ManagerCommand;

import org.tasktide.core.repository.itemstore_repo.ItemStoreRepositoryUtility;

import org.tasktide.itemstore.ItemStoreType;
import org.tasktide.tasktide.client.TaskTideManagerClient;
import org.tasktide.parser.ArgumentTree;

import org.tasktide.parser.configuration.TaskTideConfig;
        
/**
 * Verifies that the TaskTide-Engine can be configured and used from
 *  configurations
 * 
 * @author bkenna
 */
@Tag("base-tasktide")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@EnableAutoWeld
@AddPackages(value = {
    ManagerConfig.class, EngineConfig.class, GlobalConfig.class
})
public class ConfigureTaskTideTest {
    
    private static final Logger logger = LogManager.getLogger(ConfigureTaskTideTest.class);
  
    private static SeContainer container;
    
    public ConfigureTaskTideTest() {}
    
    
    @BeforeAll
    public static void setUpClass() {
        String msg = "\n\n---------------- Initiating Configuration from TaskTide-Engine-Config Tests ----------------\n";
        container = SeContainerInitializer.newInstance().initialize();
        ItemStoreRepositoryUtility.initialize(ItemStoreType.SQLITE, "ConfigTests/SQLite");
        ItemStoreRepositoryUtility.get().initServiceManager();
        logger.info(msg);
    }
    
    
    @AfterAll
    public static void tearDownClass() {
        String msg = "\n\n---------------- Terminating Configuration from TaskTide-Engine-Config Tests ----------------\n";
        container.close();
        logger.info(msg);
    }
    
    
    @BeforeEach
    public void setUp() {
    }
    
    @AfterEach
    public void tearDown() {
    }

    
    /**
     * Tests that the engine can be configured from micro-profile config
     * 
     */
    @Test
    @Order(0)
    public void canConfigureEngineClient() {
    
        // Initialize data
        logger.info("\n\n================ Tests Running EngineClient Through Config  ================\n");
        ArgumentTree argTree;
        TaskTideConfig engineConfig;
        
        // Fetch engine parameters into argument tree
        argTree = new ArgumentTree(" ");
        engineConfig = CDI.current().select(EngineConfig.class).get();
        engineConfig.initConfig(argTree);
        
        
        assertTrue(true, "");
        logger.info("\n\n================ Tests Running EngineClient Through Config  ================\n");
    }
    
    
    /**
     * Would prefer to restrict to an sqlite profile, coming back
     */
    @Test
    @Order(1)
    public void canConfigureManagerCommand() {
    
        // Initialize data
        logger.info("\n\n================ Tests ManagerCommand Can be Configured  ================\n");
        ArgumentTree argTree;
        TaskTideConfig globalConfig, managerConfig;
        boolean assertionState;
        
        // Initialize configuration
        argTree = new ArgumentTree(" ");
        globalConfig = CDI.current().select(GlobalConfig.class).get();
        globalConfig.initConfig(argTree);
        managerConfig = CDI.current().select(ManagerConfig.class).get();
        managerConfig.initConfig(argTree);
        
        // Construct config map
        TaskTideManagerClient client = new TaskTideManagerClient(argTree);
        ManagerCommand cmd = client.getManagerCommand();
        
        // Add records
        logger.info("Performing validation on ManagerCommand:\n\n'{}'", cmd.toJsonDoc());
        assertionState = cmd.validateCommand();
        logger.info("Validation status:\t'{}'", assertionState);
        
        // Log test state
        logger.info("\n\n================ Tests ManagerCommand Can be Configured  ================\n");
        assertTrue(assertionState, "Reference record could not be retrieved from backend repository");
    }
}