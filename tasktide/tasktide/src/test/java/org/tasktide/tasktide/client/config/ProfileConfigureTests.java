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

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbConfig;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;

import org.jboss.weld.junit5.auto.AddBeanClasses;
import org.jboss.weld.junit5.auto.EnableAutoWeld;

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
import org.tasktide.parser.model.ArgumentMap;
import org.tasktide.tasktide.TestUtils;


/**
 * Class to test 
 * 
 * @author bkenna
 */
@Tag("base-tasktide")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@EnableAutoWeld
@AddBeanClasses( {GlobalConfig.class, ManagerConfig.class, EngineConfig.class} )
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ProfileConfigureTests {
    
    private static final Logger logger = LogManager.getLogger(ProfileConfigureTests.class);
    private static final Jsonb PRETTY_JSON = JsonbBuilder.create(new JsonbConfig().withFormatting(true));
    private static final Jsonb JSON = JsonbBuilder.create(new JsonbConfig());
  
    private SeContainer container;
    
    public ProfileConfigureTests() {}
    
    
    @BeforeAll
    public void setUpClass() {        
        String msg = "\n\n---------------- Initiating Configuration from Micro-Profile Tests ----------------\n";
        TestUtils.initSeContainer();
        container = TestUtils.fetchConfiguredContainer();
        logger.info(msg);
    }
    
    @AfterAll
    public void tearDownClass() {
        String msg = "\n\n---------------- Terminating Configuration from Micro-Profile Tests ----------------\n";
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

    
    /**
     * Tests configuring from profile
     */
    @Test
    @Order(0)
    public void canConfigureFromProfile() {
    
        // Initialize data
        logger.info("\n\n================ Tests Displaying TaskTide Config  ================\n");
        ArgumentTree argTree;
        TaskTideConfig engineConfig, globalConfig, managerConfig;
        argTree = new ArgumentTree("");
        
        
        // Setup global config
        logger.info("Configuring TaskTide client");
        globalConfig = this.container.select(GlobalConfig.class).get();
        globalConfig.initConfig(argTree);
        logger.info("Records in tree:\n'{}'", argTree.getTree().size());
        
        
        // Setup manager config
        managerConfig = this.container.select(ManagerConfig.class).get();
        managerConfig.initConfig(argTree);
        logger.info("Records in tree:\n'{}'", argTree.getTree().size());
        
        
        // Setup engine config
        engineConfig = this.container.select(EngineConfig.class).get();
        engineConfig.initConfig(argTree);
        logger.info("Records in tree:\n'{}'", argTree.getTree().size());
        
        
        // Inspect config
        logger.info("Displaying config sources");
        Config config = ConfigProvider.getConfig();
        config.getConfigSources().forEach(
            elm -> logger.info("Displaying config source:\t'{}'", elm.getProperties())
        );
        logger.info("Displaying config properties");
        config.getPropertyNames().forEach(
                elm -> logger.info("Property:\t'{}'", elm)
        );
        
        // Evaluate test
        ArgumentMap argMap = argTree.getTree().getDataForAddress("");
        logger.info("Value for NoSQL Database Type:\t'{}'", argMap.getArgument("NoSQL Database Type"));
        logger.info("Displaying configured client:\n'{}'", PRETTY_JSON.toJson(argTree.getVerboseHelp()) );
        logger.info("Displaying Address Data Entries:\n'{}'", argTree.getTree().getDataForAddress("").getArgMap().keySet() );
        assertTrue(argMap.getArgMap().get("NoSQL Database Type").getValue() == null, "Not all records added");
        logger.info("\n\n================ Tests Displaying TaskTide Config ================\n");
    }
    
    
    /**
     * Tests parsing 
     */
    @Test
    @Order(1)
    public void canParseArguments() {
        
        // Initialize data
        logger.info("\n\n================ Tests Applying Command-Line Config  ================\n");
        ArgumentTree argTree;
        TaskTideConfig engineConfig, globalConfig, managerConfig;
        argTree = new ArgumentTree("");
        
        // Configure
        logger.info("Configuring TaskTide client");
        globalConfig = this.container.select(GlobalConfig.class).get();
        globalConfig.initConfig(argTree);
        managerConfig = this.container.select(ManagerConfig.class).get();
        managerConfig.initConfig(argTree);
        engineConfig = this.container.select(EngineConfig.class).get();
        engineConfig.initConfig(argTree);
        
        // Simulate command-line
        String[] argsIn = {"--file-path='my/Cool/Path'", "-d ','", "-w 2", "-i 4"};
        globalConfig.parseCommandLineArguments(argsIn, argTree);
        logger.info("Value for FliePath:\t'{}'", argTree.getTree().getDataForAddress("").getArgument("File Path"));
        
        // Verify
        assertTrue(argTree.getTree().getDataForAddress("").getArgument("File Path").getValue() != null, "Not all records added");
        logger.info("\n\n================ Tests Applying Command-Line Config ================\n");
    }
}
