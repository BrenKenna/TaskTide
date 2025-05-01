/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package org.tasktide.engine.observer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;


/**
 * Test module for the {@link TaskTideWorkerObserver}
 * 
 * @author bkenna
 */
public class TaskTideWorkerObserverTests {
    
    private static final Logger logger = LogManager.getLogger(TaskTideWorkerObserverTests.class);
    
    public TaskTideWorkerObserverTests() {}
    
    
    @BeforeAll
    public static void setUpClass() {        
        String msg = "\n\n---------------- Initiating TaskTideObserver Tests ----------------\n";
        logger.info(msg);
    }
    
    @AfterAll
    public static void tearDownClass() {
        String msg = "\n\n---------------- Terminating TaskTideObserver Tests ----------------\n";
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
    
    
    @Test
    @Order(0)
    public void canObserve() {
    
        // Initialize test
        logger.info("\n\n================ TaskTideObserver Test ================\n");
        boolean assertionState = true;
        
        
        // End test
        assertTrue(assertionState, "Not all tasks processed correctly");
        logger.info("\n\n================ TaskTideObserver Test ================\n");
    }
}
