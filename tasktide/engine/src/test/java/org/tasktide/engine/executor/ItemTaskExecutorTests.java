/*
 * Copyright 2026 Bren.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.tasktide.engine.executor;

import jakarta.enterprise.inject.se.SeContainer;
import jakarta.nosql.Template;
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
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;

import org.tasktide.core.model.task.TaskState;
import org.tasktide.core.manager.generator.ExampleGenerators;
import org.tasktide.core.model.task.ItemTask;
import org.tasktide.core.model.workitem.WorkItem;
import org.tasktide.core.repository.RepositoryType;

import org.tasktide.engine.EngineTestUtils;
import org.tasktide.engine.TestEnvironment;
import org.tasktide.engine.TestUtils;

import org.tasktide.engine.workerunit.TaskTideWorkerUnit;


/**
 * Test module for the {@link TaskTideExecutor} {@link TaskTideWorkerUnit}
 * 
 * @author bkenna
 */
@Tag("integration-engine")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ItemTaskExecutorTests {
    
    private static final Logger logger = LogManager.getLogger(ItemTaskExecutorTests.class);

    
    // CouchDB container
    //@Rule
    //public GenericContainer<?> couchDB = (GenericContainer<?>) TestEnvironment.couchDbContainer("tasktide_database", false);
    
    public ItemTaskExecutorTests() {}
    
    
    @BeforeAll
    public void setUpClass() {        
        String msg = "\n\n---------------- Initiating ItemTask Executor Tests ----------------\n";
        logger.info(msg);
        TestUtils.initSeContainer();
    }
    
    
    @AfterAll
    public void tearDownClass() {
        String msg = "\n\n---------------- Terminating ItemTask Executor Tests ----------------\n";
        logger.info(msg);

        //couchDB.stop();
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
    public void canSingleRunTask() {
    
        // Initialize test
        logger.info("\n\n================ Can Process Task Test ================\n");
        int nTasks = 1, processed = 0;
        boolean assertionState;
        WorkItem task;
        List<ItemTask> workload;
        ItemTaskExecutor taskExecutor;
        
        // Configuring workload
        task = TestUtils.registerWorkItemTasks(ExampleGenerators.NSLOOKUPS, nTasks);
        workload = new ArrayList<>(task.getWorkload().getTaskMap().values());
        logger.info("\nDisplaying first task for reference:\n{}", task.toJsonDoc());
        
        // Create worker to process tasks
        logger.info("Configuring ItemTaskExecutor for processing");
        taskExecutor = new ItemTaskExecutor();
        try {
            taskExecutor.executeTask(workload.get(0));
            logger.info("Display task post processing:\n'{}'", workload.get(0).toJsonDoc());
            assertionState = true;
        }
        catch ( Exception ex ) {
            logger.error("Error during processing of task:\t'{}'", workload.get(0), ex);
            assertionState = false;
        }
        
        // Log test status
        assertTrue(assertionState, "Not all tasks processed correctly");
        logger.info("\n\n================ Can Process Task Test ================\n");
    }
    
    
    /**
     * Test non pending tasks are skipped
     */
    @Test
    @Order(1)
    public void canSkipNonPending() {
        
        // Initialize test
        logger.info("\n\n================ Skip Processed Task Test ================\n");
        int nTasks = 2, processed = 0;
        boolean assertionState;
        WorkItem task;
        List<ItemTask> workload;
        ItemTaskExecutor taskExecutor;
        
        // Configuring workload
        task = TestUtils.registerWorkItemTasks(ExampleGenerators.NSLOOKUPS, nTasks, TaskState.ACTIVE);
        workload = new ArrayList<>(task.getWorkload().getTaskMap().values());
        logger.info("\nDisplaying first task for reference:\n{}", task.toJsonDoc());
        
        // Create worker to process tasks
        logger.info("Configuring ItemTaskExecutor for processing");
        taskExecutor = new ItemTaskExecutor();
        try {
            taskExecutor.runTasks(workload);
            logger.info("Display task post processing:\n'{}'", workload.get(0).toJsonDoc());
            assertionState = true;
        }
        catch ( Exception ex ) {
            logger.error("Error during processing of task:\t'{}'", workload.get(0), ex);
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
    @Order(2)
    public void emptyListDoesNotBreak() {
    
        // Initialize test
        logger.info("\n\n================ Skip Processed Task Test ================\n");
        int nTasks = 0, processed = 0;
        boolean assertionState;
        WorkItem task;
        List<ItemTask> workload;
        ItemTaskExecutor taskExecutor;
        
        // Configuring workload
        task = TestUtils.registerWorkItemTasks(ExampleGenerators.NSLOOKUPS, nTasks);
        workload = new ArrayList<>(task.getWorkload().getTaskMap().values());
        logger.info("\nDisplaying first task for reference:\n{}", task.toJsonDoc());
        
        // Create worker to process tasks
        logger.info("Configuring ItemTaskExecutor for processing");
        taskExecutor = new ItemTaskExecutor();
        taskExecutor.runTasks(workload);
        
        // Check workload
        try {
            if ( processed == 0 ) {
                logger.info("Processed task count '{}', matches expected '{}'", processed, nTasks);
                assertionState = true;
            }
            else {
                logger.error("Processed task count '{}', does not match expected '{}'", processed, nTasks);
                assertionState = false;
            }
        }
        catch (Exception ex) {
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
    @Order(3)
    public void canHandleIoException() {
    
        // Initialize test
        logger.info("\n\n================ IO Exception Handling Test ================\n");
        int nTasks = 2, processed = 0;
        boolean assertionState;
        WorkItem task;
        List<ItemTask> workload;
        ItemTaskExecutor taskExecutor;
        
        // Configuring workload
        task = TestUtils.registerWorkItemTasks(ExampleGenerators.NSLOOKUPS, nTasks);
        workload = new ArrayList<>(task.getWorkload().getTaskMap().values());
        logger.info("\nDisplaying first task for reference:\n{}", task.toJsonDoc());
        
        // Create worker to process tasks
        logger.info("Configuring ItemTaskExecutor for processing");
        taskExecutor = new ItemTaskExecutor() {
            @Override
            public boolean executeTask(ItemTask t) throws IOException {
                throw new IOException(" >>> IO Failed From Integration Test <<<");
            }
        };
        taskExecutor.runTasks(workload);
        
        
        // Check workload
        processed = EngineTestUtils.countNotActive(workload);
        if ( processed == nTasks / 2 ) {
            logger.info("Processed task count '{}', matches expected '{}'", processed, nTasks);
            assertionState = false;
        }
        else {
            logger.error("Processed task count '{}', does not match expected '{}'", processed, nTasks);
            assertionState = true;
        }
        
        // Log test status
        assertTrue(assertionState, "Tasks were not supposed to be processed correctly");
        logger.info("\n\n================ IO Exception Handling Test ================\n");
    }
    
    
    /**
     * Test Interrupted Exception handling
     */
    @Test
    @Order(4)
    public void canHandleInterruptException() {
    
        // Initialize test
        logger.info("\n\n================ Interrupt Exception Handling Test ================\n");
        int nTasks = 0, processed = 0;
        boolean assertionState;
        WorkItem task;
        List<ItemTask> workload;
        ItemTaskExecutor taskExecutor;
        
        // Configuring workload
        task = TestUtils.registerWorkItemTasks(ExampleGenerators.NSLOOKUPS, nTasks);
        workload = new ArrayList<>(task.getWorkload().getTaskMap().values());
        logger.info("\nDisplaying first task for reference:\n{}", task.toJsonDoc());
        
        // Create worker to process tasks
        logger.info("Configuring ItemTaskExecutor for processing");
        taskExecutor = new ItemTaskExecutor() {
            @Override
            public boolean executeTask(ItemTask t) throws InterruptedException {
                throw new InterruptedException(" >>> Interrupt Failed From Integration Test <<<");
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
        logger.info("\n\n================ Interrupt Exception Test ================\n");
    }
}