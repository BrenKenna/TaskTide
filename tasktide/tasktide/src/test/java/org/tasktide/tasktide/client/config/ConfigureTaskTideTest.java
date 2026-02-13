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
import org.tasktide.engine.TaskTideEngineUtility;

import org.tasktide.engine.TaskTideExecutorServiceProvider;
import org.tasktide.engine.TaskTideWorkerUnitProvider;
import org.tasktide.engine.observer.TaskTideEngineObserver;
import org.tasktide.engine.worker.executor.TaskTideExecutor;
import org.tasktide.engine.worker.processor.TaskTideProcessor;

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
        
        TaskTideWorkerUnitProvider unitProvider;
        ExecutorService executorService;
        TaskTideEngineObserver<WorkItem> observer;
        TaskTideProcessor<WorkItem> processor;
        TaskTideExecutor<WorkItem> executor;
        List<WorkItem> workload;
        
        // Fetch engine parameters into argument tree
        argTree = new ArgumentTree(" ");
        engineConfig = CDI.current().select(EngineConfig.class).get();
        engineConfig.initConfig(argTree);
        
        // Fetch configure values
        int workItemThreads, workItemThreshold, itemTaskThreads, itemTaskThreshold;
        workItemThreads = (int) argTree.getTree().getDataForAddress("engine").getArgument("WorkItem Threads").getValue();
        workItemThreshold = (int) argTree.getTree().getDataForAddress("engine").getArgument("WorkItem SubTasking Threshold").getValue();
        itemTaskThreads = (int) argTree.getTree().getDataForAddress("engine").getArgument("ItemTask Threads").getValue();
        itemTaskThreshold = (int) argTree.getTree().getDataForAddress("engine").getArgument("ItemTask SubTasking Threshold").getValue();
        
        // Configure executor service for work items, and item tasks
        TaskTideExecutorServiceProvider.initialize(workItemThreads, itemTaskThreads);
        executorService = TaskTideExecutorServiceProvider.workItemExecutorService();
        
        // Configure builders, task tracker and workload
        int nWorkItems = 4, nItemTask = 3;
        unitProvider = new TaskTideWorkerUnitProvider();
        workload = TaskGenerator.generateExampleWorkItem(ExampleGenerators.PING, nWorkItems, nItemTask);
        
        // Configure observer
        int timeKeeperObserverMaxTime = (int) argTree.getTree().getDataForAddress("engine").getArgument("TimeKeeper Wall Time").getValue();
        observer = unitProvider.getWorkItemObsBuilder()
            .withMaxTime(timeKeeperObserverMaxTime)
        .build();
        
        // Configure executor
        executor = unitProvider.getWorkItemExecBuilder()
            .withWorkItemObserver(observer)
            .withSubThreads(workItemThreads)
            .withSubTaskThreshold(workItemThreshold)
        .build();
        
        // Configure processor
        processor = unitProvider.getWorkItemProcBuilder()
            .withWorkload(workload)
            .withExecutorService(executorService)
            .withThreshold(workItemThreshold)
            .withSubExecutor(executor)
        .build();
        
        // Process work
        processor.process();
        TaskTideEngineUtility.waitOnExecutorTrackerWorkItem(nWorkItems, logger);
        
        // Evaluate test status
        boolean assertionState;
        int expected = nWorkItems * nItemTask;
        int nProcessed = TaskTideEngineUtility.countNonActive(workload);
        if ( nProcessed == expected ) {
            logger.info("Processed task count '{}', matches expected '{}'", nProcessed, expected);
            assertionState = true;
        }
        else {
            logger.error("Processed task count '{}', does not match expected '{}'", nProcessed, expected);
            assertionState = false;
        }
        
        
        // Process evaluation
        logger.info("\n-------- Displaying Execution Time Summary --------\n");
        TaskTideEngineUtility.fetchExecutionTimesWorkItem(workload, logger);
        logger.info("\n-------- Displaying Execution Time Summary --------\n");
        String template = String.format("Not all tasks processed correctl:\tTotal = '%d', Processed = '%d'", expected, nProcessed);
        assertTrue(assertionState, template);
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