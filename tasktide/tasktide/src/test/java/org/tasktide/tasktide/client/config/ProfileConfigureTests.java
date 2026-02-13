/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package org.tasktide.tasktide.client.config;

import org.tasktide.tasktide.client.config.EngineConfig;
import org.tasktide.tasktide.client.config.ManagerConfig;
import org.tasktide.tasktide.client.config.GlobalConfig;
import jakarta.enterprise.inject.se.SeContainer;
import jakarta.enterprise.inject.se.SeContainerInitializer;
import jakarta.enterprise.inject.spi.CDI;

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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Order;

import org.tasktide.parser.ArgumentTree;

import org.tasktide.parser.configuration.TaskTideConfig;

/**
 * Class to test 
 * 
 * @author bkenna
 */
@EnableAutoWeld
@AddBeanClasses( {GlobalConfig.class, ManagerConfig.class, EngineConfig.class} )
public class ProfileConfigureTests {
    
    private static final Logger logger = LogManager.getLogger(ProfileConfigureTests.class);
    private static final Jsonb PRETTY_JSON = JsonbBuilder.create(new JsonbConfig().withFormatting(true));
    private static final Jsonb JSON = JsonbBuilder.create(new JsonbConfig());
  
    private static SeContainer container;
    
    public ProfileConfigureTests() {}
    
    
    @BeforeAll
    public static void setUpClass() {        
        String msg = "\n\n---------------- Initiating Configuration from Micro-Profile Tests ----------------\n";
        container = SeContainerInitializer.newInstance().initialize();
        logger.info(msg);
    }
    
    @AfterAll
    public static void tearDownClass() {
        String msg = "\n\n---------------- Terminating Configuration from Micro-Profile Tests ----------------\n";
        container.close();
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
        argTree = new ArgumentTree(" ");
        
        
        // Setup global config
        logger.info("Configuring TaskTide client");
        globalConfig = CDI.current().select(GlobalConfig.class).get();
        globalConfig.initConfig(argTree);
        logger.info("Records in tree:\n'{}'", argTree.getTree().size());
        
        
        // Setup manager config
        managerConfig = CDI.current().select(ManagerConfig.class).get();
        managerConfig.initConfig(argTree);
        logger.info("Records in tree:\n'{}'", argTree.getTree().size());
        
        
        // Setup engine config
        engineConfig = CDI.current().select(EngineConfig.class).get();
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
        logger.info("Value for NoSQL Host:Port:\t'{}'", argTree.getTree().getDataForAddress("").getArgument("NoSQL Host:Port").getValue());
        logger.info("Displaying configured client:\n'{}'", PRETTY_JSON.toJson(argTree.getVerboseHelp()) );
        logger.info("Displaying Address Data map:\n'{}'", argTree.getTree().toAddressDataMap() );
        assertTrue(argTree.getTree().getDataForAddress("").getArgument("NoSQL Host:Port").getValue() != null, "Not all records added");
        logger.info("\n\n================ Tests Displaying TaskTide Config ================\n");
    }
    
    
    
    @Test
    @Order(1)
    public void canParseArguments() {
        
        // Initialize data
        logger.info("\n\n================ Tests Applying Command-Line Config  ================\n");
        ArgumentTree argTree;
        TaskTideConfig engineConfig, globalConfig, managerConfig;
        argTree = new ArgumentTree(" ");
        
        // Configure
        logger.info("Configuring TaskTide client");
        globalConfig = CDI.current().select(GlobalConfig.class).get();
        globalConfig.initConfig(argTree);
        managerConfig = CDI.current().select(ManagerConfig.class).get();
        managerConfig.initConfig(argTree);
        engineConfig = CDI.current().select(EngineConfig.class).get();
        engineConfig.initConfig(argTree);
        
        // Simulate command-line
        String[] argsIn = {"--file-path='my/Cool/Path'", "-d ','", "-w 2", "-i 4"};
        globalConfig.parseCommandLineArguments(argsIn, argTree);
        logger.info("Value for FliePath:\t'{}'", argTree.getTree().getDataForAddress("").getArgument("File Path").getValue());
        
        managerConfig.parseCommandLineArguments(argsIn, argTree);
        logger.info("Value for Delmiiter:\t'{}'", argTree.getTree().getDataForAddress("manager").getArgument("Delimiter").getValue());
        
        engineConfig.parseCommandLineArguments(argsIn, argTree);
        logger.info("Value for WorkItem Threads:\t'{}'", argTree.getTree().getDataForAddress("engine").getArgument("WorkItem Threads").getValue());
        
        // Verify
        assertTrue(argTree.getTree().getDataForAddress("").getArgument("File Path").getValue() != null, "Not all records added");
        logger.info("\n\n================ Tests Applying Command-Line Config ================\n");
    }
}
