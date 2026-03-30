/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package org.tasktide.engine.trackers;

import org.tasktide.engine.trackers.ExecutionState;

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
import org.tasktide.engine.trackers.TaskTrackers;



/**
 * Unit tests for TaskTracker
 * 
 * @author bkenna
 */
public class TaskTrackerTests {
    
    
    private static final Logger logger = LogManager.getLogger(TaskTrackerTests.class);
    
    public TaskTrackerTests() {}
    
    
    @BeforeAll
    public static void setUpClass() {        
        String msg = "\n\n---------------- Initiating TaskTracker Tests ----------------\n";
        logger.info(msg);
    }
    
    @AfterAll
    public static void tearDownClass() {
        String msg = "\n\n---------------- Terminating TaskTracker Tests ----------------\n";
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
     * Test that tasks can be registered
     */
    @Test
    @Order(0)
    public void shouldRegisterTasks(){
    
        // Initialize test
        logger.info("\n\n================ Can Register Test ================\n");
        int nTasks = 0;
        boolean assertionState;
        
        // Add tasks
        logger.info("Registering three tasks in tracker");
        TaskTrackers.WORK_ITEM_TRACKER.markTask("Task-1", ExecutionState.QUEUED);
        TaskTrackers.WORK_ITEM_TRACKER.markTask("Task-2", ExecutionState.QUEUED);
        TaskTrackers.WORK_ITEM_TRACKER.markTask("Task-3", ExecutionState.QUEUED);
        logger.info("Tasks registered = '{}'", TaskTrackers.WORK_ITEM_TRACKER.taskCount());
        
        // Check registered tasks
        assertionState = TaskTrackers.WORK_ITEM_TRACKER.taskCount() == 3;
        assertTrue(assertionState, "Not all three tasks were registered");
        logger.info("\n\n================ Can Register Tasks Test ================\n");
    }
    
    
    /**
     * Check task can tracked
     */
    @Test
    @Order(1)
    public void shouldTrackTask(){
    
        // Initialize test
        logger.info("\n\n================ Can Track Tasks Test ================\n");
        
        // Add tasks
        logger.info("Registering task");
        TaskTrackers.WORK_ITEM_TRACKER.markTask("Task-1", ExecutionState.QUEUED);
        ExecutionState oldState = TaskTrackers.WORK_ITEM_TRACKER.get("Task-1");

        // Mark new state
        logger.info("Marking task as '{}'", ExecutionState.RUNNING);
        TaskTrackers.WORK_ITEM_TRACKER.markTask("Task-1", ExecutionState.RUNNING);
        ExecutionState currentState = TaskTrackers.WORK_ITEM_TRACKER.get("Task-1");
        
        // Check state has changed
        assertFalse(oldState.isState(currentState), "Task Not Tracked");
        logger.info("\n\n================ Can Track Tasks Test ================\n");
    }
    
    
    
    /**
     * Check that task can progress as expected
     */
    @Test
    @Order(2)
    public void stateShouldMatch() {
    
        // Initialize test
        logger.info("\n\n================ Can Check State Test ================\n");
        String taskId = "Task-1";
        int progress = 0;
        
        // Check task is held
        logger.info("Registering task");
        TaskTrackers.WORK_ITEM_TRACKER.markTask(taskId, ExecutionState.QUEUED);
        if ( TaskTrackers.WORK_ITEM_TRACKER.isHeld(taskId) ) {
            progress++;
        }
        else {
            logger.warn("Warning Task not in Held ExecutoionState");
        }
        
        // Check task is active
        logger.info("Marking task '{}' as running", taskId);
        TaskTrackers.WORK_ITEM_TRACKER.markTask(taskId, ExecutionState.RUNNING);
        if ( TaskTrackers.WORK_ITEM_TRACKER.isActive(taskId) ) {
            progress++;
        }
        else {
            logger.warn("Warning task in an active ExecutionState");
        }
        
        // Check task is done
        TaskTrackers.WORK_ITEM_TRACKER.markTask(taskId, ExecutionState.COMPLETED);
        if ( TaskTrackers.WORK_ITEM_TRACKER.isDone(taskId) ) {
            progress++;
        }
        else {
            logger.warn("Warning task in a completed ExecutionState");
        }
        
        // Check state progressed
        assertTrue(progress == 3, "Not all three tasks were registered");
        logger.info("\n\n================ Can Check State Test ================\n");
    }
}
