/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package org.tasktide.tasktide.configurer;

import jakarta.enterprise.inject.se.SeContainer;
import jakarta.enterprise.inject.se.SeContainerInitializer;
import jakarta.enterprise.inject.spi.CDI;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbConfig;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.jboss.weld.junit5.auto.AddBeanClasses;
import org.jboss.weld.junit5.auto.EnableAutoWeld;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestInstance;

import org.tasktide.tasktide.parser.ArgumentTree;


/**
 *
 * @author bkenna
 */
@EnableAutoWeld
@AddBeanClasses( {GlobalConfig.class, ManagerConfig.class, EngineConfig.class} )
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
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

    
    
    @Test
    @Order(0)
    public void canConfigureFromProfile() {
    
        // Initialize data
        logger.info("\n\n================ Tests Displaying TaskTide Config  ================\n");
        ArgumentTree argTree;
        TaskTideConfigurer engineConfig, globalConfig, managerConfig;
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
        
        
        // Evaluate test
        logger.info("Displaying configured client:\n'{}'", PRETTY_JSON.toJson(argTree.getVerboseHelp()) );
        logger.info("Displaying Address Data map:\n'{}'", argTree.getTree().toAddressDataMap() );
        assertTrue(argTree.getTree().size() > 0, "Not all records added");
        logger.info("\n\n================ Tests Displaying TaskTide Config ================\n");
    }
}
