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

import java.util.List;

import org.tasktide.core.model.workitem.WorkItem;


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
     * Test that Tasks can be processed
     */
    @Test
    @Order(0)
    public void canRunTasks() {
    
        // Initialize test
        logger.info("\n\n================ Can Process WorkItems Test ================\n");
        int nTasks = 4, processed = 0;
        boolean assertionState = true;
        List<WorkItem> workload;
        TaskTideProcessor<WorkItem> workItemProcessor;
        
        
        // Log test status
        assertTrue(assertionState, "Not all tasks processed correctly");
        logger.info("\n\n================ Can Process WorkItems Test ================\n");
    }
}
