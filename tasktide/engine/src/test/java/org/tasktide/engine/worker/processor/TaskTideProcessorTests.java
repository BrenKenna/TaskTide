/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package org.tasktide.engine.worker.processor;

import org.tasktide.engine.worker.processor.TaskTideProcessor;
import org.tasktide.engine.worker.processor.ItemTaskProcessor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Order;
import org.tasktide.core.manager.generator.ExampleGenerators;
import org.tasktide.core.manager.generator.TaskGenerator;

import org.tasktide.core.model.task.ItemTask;
import org.tasktide.engine.EngineTestUtils;


/**
 * Suite of tests for the TaskTideProcessor
 * 
 * @author bkenna
 */
public class TaskTideProcessorTests {
    
    private static final Logger logger = LogManager.getLogger(TaskTideProcessorTests.class);
    
    public TaskTideProcessorTests() {}
    
    
    @BeforeAll
    public static void setUpClass() {        
        String msg = "\n\n---------------- Initiating TaskTideProcessor Tests ----------------\n";
        logger.info(msg);
    }
    
    @AfterAll
    public static void tearDownClass() {
        String msg = "\n\n---------------- Terminating TaskTideProcessor Tests ----------------\n";
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
     * Test that Tasks can be processed
     */
    @Test
    @Order(0)
    public void canRunTasks() {
    
        // Initialize test
        logger.info("\n\n================ Can Process Tasks Test ================\n");
        int nTasks = 4, processed = 0;
        boolean assertionState;
        List<ItemTask> workload;
        TaskTideProcessor<ItemTask> itemTaskProcessor;
        
        // Configuring workload
        logger.info("Creating '{}' tasks for testing", nTasks);
        workload = TaskGenerator.generateItemTasks(ExampleGenerators.PING, nTasks);
        logger.info("\nDisplaying first task for reference:\n{}", workload.get(0).toJsonDoc());
        
        // Configure task processor
        logger.info("Configuring task processor");
        ExecutorService executorService = Executors.newFixedThreadPool(4);
        itemTaskProcessor = new ItemTaskProcessor(workload, 2, executorService);
        
        // Process tasks
        itemTaskProcessor.execute();
        EngineTestUtils.waitUntilDoneTarget(workload, 10, logger);
        processed = EngineTestUtils.countNotActive(workload);
        if ( processed > 0 ) {
            logger.info("Processed task count '{}', matches expected '{}'", processed, nTasks);
            assertionState = true;
        }
        else {
            logger.error("Processed task count '{}', does not match expected '{}'", processed, nTasks);
            assertionState = false;
        }
        
        // Log execution times
        logger.info("Logging execution times of N successful tasks = '{}'" + processed);
        if ( assertionState ) {
            EngineTestUtils.fetchExecutionTimes(workload, logger);
        }
        
        // Log test status
        assertTrue(assertionState, "Not all tasks processed correctly");
        logger.info("\n\n================ Can Process Tasks Test ================\n");
    }
}
