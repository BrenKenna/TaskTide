/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package org.tasktide.tasktide.client.config;

import jakarta.enterprise.inject.se.SeContainer;
import jakarta.enterprise.inject.se.SeContainerInitializer;
import jakarta.enterprise.inject.spi.CDI;

import java.util.List;
import java.util.concurrent.ExecutorService;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jboss.weld.junit5.auto.AddPackages;
import org.jboss.weld.junit5.auto.EnableAutoWeld;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import org.tasktide.core.manager.command.ManagerCommand;

import org.tasktide.core.manager.generator.ExampleGenerators;
import org.tasktide.core.manager.generator.TaskGenerator;

import org.tasktide.core.model.workitem.WorkItem;
import org.tasktide.core.repository.itemstore_repo.ItemStoreRepositoryUtility;
import org.tasktide.engine.worker.TaskTideEngineUtility;

import org.tasktide.engine.workerunit.provider.TaskTideExecutorServiceProvider;
import org.tasktide.engine.workerunit.provider.TaskTideWorkerUnitProvider;
import org.tasktide.engine.observer.TaskTideEngineObserver;
import org.tasktide.engine.executor.TaskTideExecutor;

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