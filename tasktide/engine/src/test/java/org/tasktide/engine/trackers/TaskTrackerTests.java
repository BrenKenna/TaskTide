/*
 * Copyright 2026 Brendan Kenna.
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
package org.tasktide.engine.trackers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Order;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;


/**
 * Unit tests for TaskTracker
 * 
 * @author bkenna
 */
@Tag("unit-base")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TaskTrackerTests {
    
    
    private static final Logger LOGGER = LogManager.getLogger(TaskTrackerTests.class);
    
    public TaskTrackerTests() {}
    
    
    @BeforeAll
    public static void setUpClass() {        
        String msg = "\n\n---------------- Initiating TaskTracker Tests ----------------\n";
        LOGGER.info(msg);
    }
    
    @AfterAll
    public static void tearDownClass() {
        String msg = "\n\n---------------- Terminating TaskTracker Tests ----------------\n";
        LOGGER.info(msg);
    }
    
    
    @BeforeEach
    public void setUp() {
        LOGGER.info("\n\n================ Initiating Next Test ================\n");
    }
    
    @AfterEach
    public void tearDown() {
        LOGGER.info("\n\n================ Terminating Test ================\n");
    }
    
    
    /**
     * Test that tasks can be registered
     */
    @Test
    @Order(0)
    public void shouldRegisterTasks(){
    
        // Initialize test
        LOGGER.info("\n\n================ Can Register Test ================\n");
        int nTasks = 0;
        boolean assertionState;
        
        // Add tasks
        LOGGER.info("Registering three tasks in tracker");
        TaskTrackers.WORK_ITEM_TRACKER.markTask("Task-1", ExecutionState.QUEUED);
        TaskTrackers.WORK_ITEM_TRACKER.markTask("Task-2", ExecutionState.QUEUED);
        TaskTrackers.WORK_ITEM_TRACKER.markTask("Task-3", ExecutionState.QUEUED);
        LOGGER.info("Tasks registered = '{}'", TaskTrackers.WORK_ITEM_TRACKER.taskCount());
        
        // Check registered tasks
        assertionState = TaskTrackers.WORK_ITEM_TRACKER.taskCount() == 3;
        assertTrue(assertionState, "Not all three tasks were registered");
        LOGGER.info("\n\n================ Can Register Tasks Test ================\n");
    }
    
    
    /**
     * Check task can tracked
     */
    @Test
    @Order(1)
    public void shouldTrackTask(){
    
        // Initialize test
        LOGGER.info("\n\n================ Can Track Tasks Test ================\n");
        
        // Add tasks
        LOGGER.info("Registering task");
        TaskTrackers.WORK_ITEM_TRACKER.markTask("Task-1", ExecutionState.QUEUED);
        ExecutionState oldState = TaskTrackers.WORK_ITEM_TRACKER.get("Task-1");

        // Mark new state
        LOGGER.info("Marking task as '{}'", ExecutionState.RUNNING);
        TaskTrackers.WORK_ITEM_TRACKER.markTask("Task-1", ExecutionState.RUNNING);
        ExecutionState currentState = TaskTrackers.WORK_ITEM_TRACKER.get("Task-1");
        
        // Check state has changed
        assertFalse(oldState.isState(currentState), "Task Not Tracked");
        LOGGER.info("\n\n================ Can Track Tasks Test ================\n");
    }
    
    
    
    /**
     * Check that task can progress as expected
     */
    @Test
    @Order(2)
    public void stateShouldMatch() {
    
        // Initialize test
        LOGGER.info("\n\n================ Can Check State Test ================\n");
        String taskId = "Task-1";
        int progress = 0;
        
        // Check task is held
        LOGGER.info("Registering task");
        TaskTrackers.WORK_ITEM_TRACKER.markTask(taskId, ExecutionState.QUEUED);
        if ( TaskTrackers.WORK_ITEM_TRACKER.isHeld(taskId) ) {
            progress++;
        }
        else {
            LOGGER.warn("Warning Task not in Held ExecutoionState");
        }
        
        // Check task is active
        LOGGER.info("Marking task '{}' as running", taskId);
        TaskTrackers.WORK_ITEM_TRACKER.markTask(taskId, ExecutionState.RUNNING);
        if ( TaskTrackers.WORK_ITEM_TRACKER.isActive(taskId) ) {
            progress++;
        }
        else {
            LOGGER.warn("Warning task in an active ExecutionState");
        }
        
        // Check task is done
        TaskTrackers.WORK_ITEM_TRACKER.markTask(taskId, ExecutionState.COMPLETED);
        if ( TaskTrackers.WORK_ITEM_TRACKER.isDone(taskId) ) {
            progress++;
        }
        else {
            LOGGER.warn("Warning task in a completed ExecutionState");
        }
        
        // Check state progressed
        assertTrue(progress == 3, "Not all three tasks were registered");
        LOGGER.info("\n\n================ Can Check State Test ================\n");
    }
}
