/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package org.tasktide.engine;

import java.util.List;
import java.util.concurrent.ExecutorService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.tasktide.core.manager.generator.ExampleGenerators;
import org.tasktide.core.manager.generator.TaskGenerator;
import org.tasktide.core.model.workitem.WorkItem;
import org.tasktide.engine.observer.TaskTideEngineObserver;
import org.tasktide.engine.tasktracker.TaskTracker;
import org.tasktide.engine.worker.executor.TaskTideExecutor;
import org.tasktide.engine.worker.processor.TaskTideProcessor;



/**
 *
 * @author bkenna
 */
public class InitialEngineTests {
    
    private static final Logger logger = LogManager.getLogger(InitialEngineTests.class);
    
    public InitialEngineTests() {}
    
    
    @BeforeAll
    public static void setUpClass() {        
        String msg = "\n\n---------------- Initiating WorkItemProcessor Tests ----------------\n";
        logger.info(msg);
    }
    
    @AfterAll
    public static void tearDownClass() {
        String msg = "\n\n---------------- Terminating WorkItemProcessor Tests ----------------\n";
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
     * Test using the {@link TaskTideWorkerUnitProvider} and {@link TaskTideExecutorServiceProvider}
     *  for deploying {@link WorkItem} processing via {@link TaskTideProcessor}
     */
    @Test
    @Order(0)
    public void canRunThroughProviders() {
    
        // Initialize test
        logger.info("\n\n================ Can Run Through Providers Test ================\n");
        int nProcessed = 0, expected = 24;
        boolean assertionState;
        TaskTideWorkerUnitProvider unitProvider;
        ExecutorService executorService;
        TaskTideEngineObserver<WorkItem> observer;
        TaskTideProcessor<WorkItem> processor;
        TaskTideExecutor<WorkItem> executor;
        TaskTracker taskTracker;
        List<WorkItem> workload;
        
        // Configure executor service for work items, and item tasks
        int nWorkItemThreads = 2, nItemTaskThreads = 3;
        TaskTideExecutorServiceProvider.initialize(nWorkItemThreads, nItemTaskThreads);
        executorService = TaskTideExecutorServiceProvider.workItemExecutorService();
        
        // Configure builders, task tracker and workload
        unitProvider = new TaskTideWorkerUnitProvider();
        taskTracker = new TaskTracker();
        workload = TaskGenerator.generateExampleWorkItem(ExampleGenerators.PING, 4, 6);
        
        // Configure observer
        observer = unitProvider.getWorkItemObsBuilder()
            .withTaskTracker(taskTracker)
            .withMaxTime(1000000)
        .build();
        
        // Configure executor
        executor = unitProvider.getWorkItemExecBuilder()
            .withWorkItemObserver(observer)
            .withWorkItemObserver(observer)
            .withSubThreads(2)
            .withSubTaskThreshold(3)
        .build();
        
        // Configure processor
        processor = unitProvider.getWorkItemProcBuilder()
            .withWorkload(workload)
            .withExecutorService(executorService)
            .withThreshold(2)
            .withSubExecutor(executor)
        .build();
        
        // Process tasks
        processor.process();
        EngineTestUtils.waitUntilDoneWorkItem(workload, 30, logger);
        
        // Evaluate test status
        nProcessed = EngineTestUtils.countNonActive(workload);
        if ( nProcessed >= 0 ) {
            logger.info("Processed task count '{}', matches expected '{}'", nProcessed, expected);
            assertionState = true;
        }
        else {
            logger.error("Processed task count '{}', does not match expected '{}'", nProcessed, expected);
            assertionState = false;
        }
        
        // Log test status
        logger.info("-------- Displaying Processed WorkItems --------");
        workload.stream().forEach( elm -> logger.info(elm.toJsonDoc()) );
        logger.info("-------- Displaying Execution Time Summary --------");
        EngineTestUtils.fetchExecutionTimesWorkItem(workload, logger);
        String template = String.format("Not all tasks processed correctl:\tTotal = '%d', Processed = '%d'", expected, nProcessed);
        assertTrue(assertionState, template);
        logger.info("\n\n================ Can Process WorkItems Test ================\n");
    }
}
