/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package org.tasktide.engine.worker.processor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Order;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import java.util.List;

import org.tasktide.core.manager.generator.ExampleGenerators;
import org.tasktide.core.manager.generator.TaskGenerator;

import org.tasktide.core.model.workitem.WorkItem;

import org.tasktide.engine.EngineTestUtils;


/**
 *
 * @author bkenna
 */
public class WorkItemProcessorTests {
    
    private static final Logger logger = LogManager.getLogger(WorkItemProcessorTests.class);
    
    public WorkItemProcessorTests() {}
    
    
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
     * Test that Tasks can be processed. Processing across groups in parallel
     */
    @Test
    @Order(0)
    public void canRunTasks() {
    
        // Initialize test
        logger.info("\n\n================ Can Process WorkItems Test ================\n");
        int nTasks = 16, processed;
        boolean assertionState;
        List<WorkItem> workload;
        TaskTideProcessor<WorkItem> workItemProcessor;
        
        // Make test workload
        logger.info("Configuring workload and WorkItemProcessor");
        workload = TaskGenerator.generateExampleWorkItem(ExampleGenerators.PING, 4, 4);
        ExecutorService executorService = Executors.newFixedThreadPool(4);
        workItemProcessor = new WorkItemProcessor(workload, 2, executorService);
        
        // Process work items
        logger.info("Processing workload of N Items = '{}'", workload.size());
        workItemProcessor.execute();
        EngineTestUtils.waitUntilDoneWorkItem(workload, 10, logger);
        
        
        // Evaluate test status
        processed = EngineTestUtils.countNonPending(workload);
        if ( processed == nTasks ) {
            logger.info("Processed task count '{}', matches expected '{}'", processed, nTasks);
            assertionState = true;
        }
        else {
            logger.error("Processed task count '{}', does not match expected '{}'", processed, nTasks);
            assertionState = false;
        }
        
        // Log test status
        logger.info("-------- Displaying Processed WorkItems --------");
        workload.stream().forEach( elm -> logger.info(elm.toJsonDoc()) );
        logger.info("-------- Displaying Execution Time Summary --------");
        EngineTestUtils.fetchExecutionTimesWorkItem(workload, logger);
        assertTrue(assertionState, "Not all tasks processed correctly");
        logger.info("\n\n================ Can Process WorkItems Test ================\n");
    }
}
