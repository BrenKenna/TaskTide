/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package org.tasktide.engine.worker.executor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Order;

import java.util.List;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.tasktide.core.manager.generator.ExampleGenerators;
import org.tasktide.core.manager.generator.TaskGenerator;


import org.tasktide.core.model.workitem.WorkItem;
import org.tasktide.engine.EngineTestUtils;


/**
 * Class for testing {@link WorkItemExecutor}
 * 
 * @author bkenna
 */
public class WorkItemExecutorTests {
    
    private static final Logger logger = LogManager.getLogger(WorkItemExecutorTests.class);
    
    public WorkItemExecutorTests() {}
    
    
    @BeforeAll
    public static void setUpClass() {        
        String msg = "\n\n---------------- Initiating WorkItemExecutor Tests ----------------\n";
        logger.info(msg);
    }
    
    @AfterAll
    public static void tearDownClass() {
        String msg = "\n\n---------------- Terminating WorkItemExecutor Tests ----------------\n";
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
     * Test that Tasks can be processed, processing across groups is serial
     */
    @Test
    @Order(0)
    public void canRunTasks() {
    
        // Initialize test
        logger.info("\n\n================ Can Execute WorkItems Test ================\n");
        int nTasks = 16, processed;
        boolean assertionState;
        List<WorkItem> workload;
        TaskTideExecutor<WorkItem> workItemExecutor;
        
        // Make test workload
        logger.info("Configuring workload and WorkItemExecutor");
        workload = TaskGenerator.generateExampleWorkItem(ExampleGenerators.PING, 4, 4);
        workItemExecutor = new WorkItemExecutor();
        
        // Process workload
        logger.info("Process workload");
        workItemExecutor.runTasks(workload);
        EngineTestUtils.waitUntilDoneWorkItem(workload, 30, logger);
        
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
