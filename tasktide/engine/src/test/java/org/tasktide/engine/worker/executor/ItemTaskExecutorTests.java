/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package org.tasktide.engine.worker.executor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.tasktide.core.model.task.ItemTask;
import org.tasktide.core.model.task.TaskState;

import org.tasktide.core.manager.generator.ExampleGenerators;
import org.tasktide.core.manager.generator.TaskGenerator;
import org.tasktide.engine.EngineTestUtils;


/**
 * Test module for the {@link TaskTideExecutor} {@link TaskTideWorkerUnit}
 * 
 * @author bkenna
 */
public class ItemTaskExecutorTests {
    
    private static final Logger logger = LogManager.getLogger(ItemTaskExecutorTests.class);
    
    public ItemTaskExecutorTests() {}
    
    
    @BeforeAll
    public static void setUpClass() {        
        String msg = "\n\n---------------- Initiating ItemTaskExecutor Tests ----------------\n";
        logger.info(msg);
    }
    
    @AfterAll
    public static void tearDownClass() {
        String msg = "\n\n---------------- Terminating ItemTaskExecutor Tests ----------------\n";
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
    public void canRunTask() {
    
        // Initialize test
        logger.info("\n\n================ Can Process Task Test ================\n");
        int nTasks = 2, processed = 0;
        boolean assertionState;
        List<ItemTask> workload;
        TaskTideExecutor<ItemTask> taskExecutor;
        
        // Configuring workload
        logger.info("Creating '{}' tasks for testing", nTasks);
        workload = TaskGenerator.generateItemTasks(ExampleGenerators.PING, nTasks);
        logger.info("\nDisplaying first task for reference:\n{}", workload.get(0).toJsonDoc());
        
        // Create worker to process tasks
        logger.info("Configuring ItemTaskExecutor for processing");
        taskExecutor = new ItemTaskExecutor();
        taskExecutor.runTasks(workload);
        
        // Check workload
        processed = EngineTestUtils.countNotActive(workload);
        if ( processed == nTasks ) {
            logger.info("Processed task count '{}', matches expected '{}'", processed, nTasks);
            assertionState = true;
        }
        else {
            logger.error("Processed task count '{}', does not match expected '{}'", processed, nTasks);
            assertionState = false;
        }
        
        // Log test status
        assertTrue(assertionState, "Not all tasks processed correctly");
        logger.info("\n\n================ Can Process Task Test ================\n");
    }
    
    
    
    /**
     * Test processing of single task workload
     */
    @Test
    @Order(1)
    public void canProcessSingleTaskWorkload() {
    
        // Initialize test
        logger.info("\n\n================ Single Task Test ================\n");
        int nTasks = 1, processed = 0;
        boolean assertionState;
        List<ItemTask> workload;
        TaskTideExecutor<ItemTask> taskExecutor;
        
        // Configuring workload
        logger.info("Creating '{}' tasks for testing", nTasks);
        workload = TaskGenerator.generateItemTasks(ExampleGenerators.PING, nTasks);
        logger.info("\nDisplaying first task for reference:\n{}", workload.get(0).toJsonDoc());
        
        // Create worker to process tasks
        logger.info("Configuring ItemTaskExecutor for processing");
        taskExecutor = new ItemTaskExecutor();
        taskExecutor.runTasks(workload);
        
        // Check workload
        processed = EngineTestUtils.countNotActive(workload);
        if ( processed == nTasks ) {
            logger.info("Processed task count '{}', matches expected '{}'", processed, nTasks);
            assertionState = true;
        }
        else {
            logger.error("Processed task count '{}', does not match expected '{}'", processed, nTasks);
            assertionState = false;
        }
        
        // Log test status
        assertTrue(assertionState, "Not all tasks processed correctly");
        logger.info("\n\n================ Single Task Test ================\n");
    }
    
    
    /**
     * Test non pending tasks are skipped
     */
    @Test
    @Order(2)
    public void canSkipNonPending() {
        
        // Initialize test
        logger.info("\n\n================ Skip Processed Task Test ================\n");
        int nTasks = 2, processed = 0;
        boolean assertionState;
        List<ItemTask> workload;
        TaskTideExecutor<ItemTask> taskExecutor;
        
        // Configuring workload
        logger.info("Creating '{}' tasks for testing", nTasks);
        workload = TaskGenerator.generateItemTasks(ExampleGenerators.PING, nTasks);
        workload.get(0).setTaskState(TaskState.ACTIVE);
        logger.info("\nDisplaying first task for reference:\n{}", workload.get(0).toJsonDoc());
        
        // Create worker to process tasks
        logger.info("Configuring ItemTaskExecutor for processing");
        taskExecutor = new ItemTaskExecutor();
        taskExecutor.runTasks(workload);
        
        // Check workload
        processed = EngineTestUtils.countNotActive(workload);
        if ( processed == nTasks / 2 ) {
            logger.info("Processed task count '{}', matches expected '{}'", processed, nTasks);
            assertionState = true;
        }
        else {
            logger.error("Processed task count '{}', does not match expected '{}'", processed, nTasks);
            assertionState = false;
        }
        
        // Log test status
        assertTrue(assertionState, "Not all tasks processed correctly");
        logger.info("\n\n================ Skip Processed Task Test ================\n");
    }
    
    
    /**
     * Test an empty list does not break program
     */
    @Test
    @Order(3)
    public void emptyListDoesNotBreak() {
    
        // Initialize test
        logger.info("\n\n================ Empty List Test ================\n");
        int nTasks = 2, processed = 0;
        boolean assertionState;
        List<ItemTask> workload;
        TaskTideExecutor<ItemTask> taskExecutor;
        
        // Configuring workload
        logger.info("Creating '{}' tasks for testing", nTasks);
        workload = new ArrayList<>();
        
        // Create worker to process tasks
        logger.info("Configuring ItemTaskExecutor for processing");
        taskExecutor = new ItemTaskExecutor();
        taskExecutor.runTasks(workload);
        
        // Check workload
        if ( processed == 0 ) {
            logger.info("Processed task count '{}', matches expected '{}'", processed, nTasks);
            assertionState = true;
        }
        else {
            logger.error("Processed task count '{}', does not match expected '{}'", processed, nTasks);
            assertionState = false;
        }
        
        // Log test status
        assertTrue(assertionState, "Something ain't right here");
        logger.info("\n\n================ Empty List Test ================\n");
    }
    
    
    /**
     * Test IO-Exception handling
     */
    @Test
    @Order(4)
    public void test_IO_ExceptionHandling() {
    
        // Initialize test
        logger.info("\n\n================ Execute Task IO-Exception Test ================\n");
        int nTasks = 2, processed = 0;
        boolean assertionState;
        List<ItemTask> workload;
        TaskTideExecutor<ItemTask> taskExecutor;
        
        // Configuring workload
        logger.info("Creating '{}' tasks for testing", nTasks);
        workload = TaskGenerator.generateItemTasks(ExampleGenerators.PING, nTasks);
        workload.get(0).setTaskState(TaskState.ACTIVE);
        logger.info("\nDisplaying first task for reference:\n{}", workload.get(0).toJsonDoc());
        
        // Create worker to process tasks
        logger.info("Configuring ItemTaskExecutor for processing");
        taskExecutor = new ItemTaskExecutor() {
            @Override
            protected boolean executeTask(ItemTask t) throws IOException {
                throw new IOException("IO Failed");
            }
        };
        taskExecutor.runTasks(workload);
        
        
        // Check workload
        processed = EngineTestUtils.countNotActive(workload);
        if ( processed == nTasks / 2 ) {
            logger.info("Processed task count '{}', matches expected '{}'", processed, nTasks);
            assertionState = true;
        }
        else {
            logger.error("Processed task count '{}', does not match expected '{}'", processed, nTasks);
            assertionState = false;
        }
        
        // Log test status
        assertTrue(assertionState, "Not all tasks processed correctly");
        logger.info("\n\n================ Execute Task IO-Exception Test ================\n");
    }
    
    
    /**
     * Test Interrupted Exception handling
     */
    @Test
    @Order(5)
    public void test_InterruptExceptionHandling() {
    
        // Initialize test
        logger.info("\n\n================ Execute Task Interrupted-Exception Test ================\n");
        int nTasks = 2, processed = 0;
        boolean assertionState;
        List<ItemTask> workload;
        TaskTideExecutor<ItemTask> taskExecutor;
        
        // Configuring workload
        logger.info("Creating '{}' tasks for testing", nTasks);
        workload = TaskGenerator.generateItemTasks(ExampleGenerators.PING, nTasks);
        workload.get(0).setTaskState(TaskState.ACTIVE);
        logger.info("\nDisplaying first task for reference:\n{}", workload.get(0).toJsonDoc());
        
        // Create worker to process tasks
        logger.info("Configuring ItemTaskExecutor for processing");
        taskExecutor = new ItemTaskExecutor() {
            @Override
            protected boolean executeTask(ItemTask t) throws InterruptedException {
                throw new InterruptedException("IO Failed");
            }
        };
        taskExecutor.runTasks(workload);
        
        
        // Check workload
        processed = EngineTestUtils.countNotActive(workload);
        if ( processed == nTasks / 2 ) {
            logger.info("Processed task count '{}', matches expected '{}'", processed, nTasks);
            assertionState = true;
        }
        else {
            logger.error("Processed task count '{}', does not match expected '{}'", processed, nTasks);
            assertionState = false;
        }
        
        // Log test status
        assertTrue(assertionState, "Not all tasks processed correctly");
        logger.info("\n\n================ Execute Task Interrupted-Exception Test ================\n");
    }
}
