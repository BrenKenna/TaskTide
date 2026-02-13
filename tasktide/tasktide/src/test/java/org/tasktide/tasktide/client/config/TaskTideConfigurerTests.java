/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package org.tasktide.tasktide.client.config;

import org.tasktide.tasktide.client.config.EngineConfig;
import org.tasktide.tasktide.client.config.ManagerConfig;
import org.tasktide.tasktide.client.config.GlobalConfig;
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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Order;

import org.tasktide.parser.ArgumentTree;

import org.tasktide.parser.configuration.TaskTideConfig;

/**
 * Tests TaskTideConfigurer 
 * 
 * @author bkenna
 */
public class TaskTideConfigurerTests {
    
    private static final Logger logger = LogManager.getLogger(TaskTideConfigurerTests.class);
    private static final Jsonb PRETTY_JSON = JsonbBuilder.create(new JsonbConfig().withFormatting(true));
    
    public TaskTideConfigurerTests() {}
    
    @BeforeAll
    public static void setUpClass() {        
        String msg = "\n\n---------------- Initiating Configuration Tests ----------------\n";
        logger.info(msg);
    }
    
    @AfterAll
    public static void tearDownClass() {
        String msg = "\n\n---------------- Terminating Configuration Tests ----------------\n";
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
    public void canDisplayTree() {
    
        // Initialize data
        logger.info("\n\n================ Tests Displaying TaskTide Config  ================\n");
        ArgumentTree argTree;
        TaskTideConfig engineConfig, globalConfig, managerConfig;
        boolean assertionState;
        
        // Setup global config
        logger.info("Configuring TaskTide client");
        argTree = new ArgumentTree(" ");
        globalConfig = new GlobalConfig("");
        globalConfig.initConfig(argTree);
        logger.info("Records in tree:\n'{}'", argTree.getTree().size());
        logger.info("Displaying configured client:\n'{}'", PRETTY_JSON.toJson(argTree.getVerboseHelp()) );
        
        // Setup manager config
        managerConfig = new ManagerConfig("manager");
        managerConfig.initConfig(argTree);
        logger.info("Records in tree:\n'{}'", argTree.getTree().size());
        logger.info("Displaying configured client:\n'{}'", PRETTY_JSON.toJson(argTree.getVerboseHelp()) );
        
        // Setup engine config
        engineConfig = new EngineConfig("engine");
        engineConfig.initConfig(argTree);
        logger.info("Records in tree:\n'{}'", argTree.getTree().size());
        logger.info("Displaying configured client:\n'{}'", PRETTY_JSON.toJson(argTree.getVerboseHelp()) );
        
        // Fetch address map
        logger.info("Displaying Address Data map:\n'{}'", argTree.getTree().toAddressDataMap() );
        
        // Evaluate test
        assertionState = argTree.getTree().size() > 0;
        assertTrue(assertionState, "Not all records added");
        logger.info("\n\n================ Tests Displaying TaskTide Config ================\n");
    }
}
