/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package org.tasktide.engine.observer.worker;

import org.tasktide.engine.observer.worker.timekeeper.ItemTaskTimeKeeper;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Order;

import org.tasktide.core.manager.generator.TaskGenerator;
import org.tasktide.core.model.task.ItemTask;
import org.tasktide.engine.EngineTestUtils;



/**
 * Test module for the {@link TimeKeeperObserver} {@link TaskTideWorkerObserver}
 * 
 * @author bkenna
 */
public class ItemTaskTimeKeeperTests {
    
    private static final Logger logger = LogManager.getLogger(ItemTaskTimeKeeperTests.class);
    
    public ItemTaskTimeKeeperTests() {}
    
    
    @BeforeAll
    public static void setUpClass() {        
        String msg = "\n\n---------------- Initiating TimeKeeper Tests ----------------\n";
        logger.info(msg);
    }
    
    @AfterAll
    public static void tearDownClass() {
        String msg = "\n\n---------------- Terminating TimeKeeper Tests ----------------\n";
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
     * Test evaluating on start task actions
     */
    @Test
    @Order(0)
    public void canEvaluateStartEvents() {
    
        // Initialize test
        logger.info("\n\n================ Evaluating Start Events Test ================\n");
        int passCount = 0;
        boolean assertionState;
        TimeKeeperObserver<ItemTask> timeKeeper;
        ItemTask task;
        
        // Configure task and time keeper
        task = TaskGenerator.generatePingTask().asItemTask();
        timeKeeper = new ItemTaskTimeKeeper(2L);
        logger.info("Displaying task for ItemTaskTimeKeeper evaluation:\n\n{}", task.toJsonDoc());
        
        // Evalute first run
        logger.info("Evaluating first run");
        if ( timeKeeper.onTaskStart(task).isSuccess() ) {
            logger.info("Configuration successful");
            passCount++;
        }
        else {
            logger.error("Error, configuration unsuccessful");
        }
        
        // Test on start actions
        logger.info("Letting time elapse for evaluating enough time for re-running the same task");
        EngineTestUtils.wait(20000, logger);
        if ( !timeKeeper.onTaskStart(task).isSuccess() ) {
            logger.info("Measuring time elapsed successful");
            passCount++;
        }
        else {
            logger.error("Error, measuring elapsed time unsuccessful");
        }
        
        // End test
        assertionState = passCount == 2;
        assertTrue(assertionState, "Not all tests passed ");
        logger.info("\n\n================ Evaluating Start Events Test ================\n");
    }
    
    
    /**
     * Test evaluating on end task actions
     */
    @Test
    @Order(1)
    public void canEvaluateEndEvents() {
        
        // Initialize test
        logger.info("\n\n================ Evaluating End Events Test ================\n");
        boolean assertionState;
        long maxAllowedTime = 5000; // 5 seconds
        ItemTask task = TaskGenerator.generatePingTask().asItemTask();

        // Use a concrete subclass with overrideable internals
        TimeKeeperObserver<ItemTask> timeKeeper = new ItemTaskTimeKeeper(maxAllowedTime) {
            {
                // Prepopulate with high durations to inflate meanDuration
                executionTimes.addAll(List.of(10000L, 10500L, 10200L));
                startTime.set(System.currentTimeMillis() - 1000); // Simulate short task
            }

        };
        assertionState = timeKeeper.onTaskEnd(task).isSuccess();
        
        // End test
        assertFalse(assertionState, "Not all tests passed ");
        logger.info("\n\n================ Evaluating End Events Test ================\n");
    }
}
