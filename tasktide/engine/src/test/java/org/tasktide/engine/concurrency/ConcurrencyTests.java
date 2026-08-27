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
package org.tasktide.engine.concurrency;

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
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.TestMethodOrder;

import org.tasktide.core.manager.generator.ExampleGenerators;
import org.tasktide.core.manager.generator.TaskGenerator;
import org.tasktide.core.model.task.ItemTask;
import org.tasktide.core.model.task.TaskState;


/**
 *
 * @author bkenna
 */
@Deprecated
@Tag("unit-engine")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ConcurrencyTests {
    
    private static final Logger logger = LogManager.getLogger(ConcurrencyTests.class);
    
    public ConcurrencyTests() {}
    
    
    @BeforeAll
    public static void setUpClass() {        
        String msg = "\n\n---------------- Initiating Concurrency Tests ----------------\n";
        logger.info(msg);
    }
    
    @AfterAll
    public static void tearDownClass() {
        String msg = "\n\n---------------- Terminating Concurrency Tests ----------------\n";
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
     * Wait for a number of iterations until limit is reached
     * 
     * @param limit
     * @param waitTime
     * @param itemTaskExecutor 
     */
    private void waitByCountedTime(int limit, int waitTime, ParallelItemTaskExecutor itemTaskExecutor) {
        int counter = 0;
        while (counter <= limit || itemTaskExecutor.getTotalExecuted() >= 4) {
            String template = String.format("Oberserving tasks iteration:\t'%d'. Task done count:\t'%d'", counter, itemTaskExecutor.getTotalExecuted());
            logger.info(template);
            counter++;
            try {Thread.sleep(waitTime);} catch(Exception ex) {logger.warn("Unable to sleep for iteration:\t" + counter);}
        }
    }
    
    
    /**
     * Wait until tasks are done
     * 
     * @param workload
     * @param waitTime 
     */
    private void waitUntilDoneTarget(List<ItemTask> workload, int waitTime) {
        int counter = 0;
        int nInactive = countNotActive(workload);
        while ( nInactive < workload.size() ) {
            
            // Log done
            String template = String.format("Oberserving tasks iteration:\t'%d'. Task done count:\t'%d'", counter, nInactive);
            logger.info(template);
            
            // Wait and check
            try {Thread.sleep(waitTime);} catch(Exception ex) {logger.warn("Unable to sleep for iteration:\t" + counter);}
            nInactive = countNotActive(workload);
            logger.debug("\n\n=========== Count of Tasks Done = " + nInactive + " ===============\n\n");
            counter++;
            
            // Display
            logger.debug("\n\n======== Displaying Workload From Main Thread ========");
            workload.forEach(task -> logger.debug((task.toJsonDoc()))) ;
        }
        logger.info("Workload processing completed");
    }
    
    
    /**
     * Scan tasks for count of done
     * 
     * @param workload
     * @return int
     */
    private int countNotActive(List<ItemTask> workload) {
        return (int) workload.stream()
            .parallel()
            .filter( task -> task.getTaskState() == TaskState.COMPLETE || task.getTaskState() == TaskState.ERROR)
        .count();
    }
    
    
    /**
     * Log execution times on INFO level
     * 
     * @param workload 
     */
    private void fetchExecutionTimes(List<ItemTask> workload) {
        for (ItemTask task : workload) {
            String template = String.format(
           "\n\nTask '%s' started '%d' finished '%d'\n\n",
            task.getTaskName(),
            task.getTaskLog().getStartTime(),
                task.getTaskLog().getEndTime()
            );
            logger.info(template);
        }
    }
    
    
    /**
     * Test out instantiating ProcessLog
     */
    @Test
    @Order(0)
    public void canProcessTasks_Executor() {
    
        // Initialize test
        logger.info("\n\n================ Process Tasks Test ================\n");
        List<ItemTask> workload;
        ExecutorService executor;
        ParallelItemTaskExecutor itemTaskExecutor;
        boolean assertionState;
        int count, expectedCount;
        
        // Make test ping tasks
        logger.info("Creating 4 test ping tasks");
        workload = TaskGenerator.generateItemTasks(ExampleGenerators.PING, 4);
        logger.info("\n\nDisplaying first element of workload:\n\n" + workload.get(0).toJsonDoc() + "\n\n");
        
        // Setup executor
        logger.info("Configuring the ParallelItemTaskExecutor");
        executor = Executors.newFixedThreadPool(4);
        itemTaskExecutor = new ParallelItemTaskExecutor(workload, 2, executor);
        
        // Run work
        logger.info("Processing workload");
        try {
            itemTaskExecutor.execute();
        }
        catch (Exception ex) {
            logger.error("Error processing workload:\n" + ex);
        }
        
        // Wait until done
        logger.info("Waiting until tasks are done");
        waitUntilDoneTarget(workload, 3000);
        
        // Evaluate processed workload
        logger.info("Workload complete, evaluating if all tasks were successfully completed");
        expectedCount = 4; count = 0;
        for ( ItemTask task : workload) {
            if ( task.getTaskLog() != null ) {
                count++;
                logger.info(String.format("\n\nDisplaying processed task '%d' of '%d':\n\n%s", count, expectedCount, task.toJsonDoc()));
            }
            else {
                logger.error(String.format("\n\nError processing task '%d' of '%d':\n\n%s", count, expectedCount, task.toJsonDoc()));
            }
        }
        assertionState = count == expectedCount;
        
        // Log execution times
        logger.info("Logging execution times of N successful tasks = " + itemTaskExecutor.getTotalExecuted());
        if ( assertionState ) {
            fetchExecutionTimes(workload);
        }
        
        // End test
        assertTrue(assertionState, "Not all tasks processed correctly");
        logger.info("\n\n================ Process Log Test ================\n");
    }
}
