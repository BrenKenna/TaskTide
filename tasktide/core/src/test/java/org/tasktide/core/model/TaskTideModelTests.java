/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package org.tasktide.core.model;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Order;

import org.tasktide.TestUtils;
import org.tasktide.core.model.builders.WorkItemBuilder;
import org.tasktide.core.model.workitem.WorkItem;
import org.tasktide.core.repository.TaskTideModel;


/**
 * Tests of the TaskTideModelInterface 
 * 
 * @author bkenna
 */
public class TaskTideModelTests {
    
    private static final Logger logger = LogManager.getLogger(TaskTideModelTests.class);
    
    public TaskTideModelTests() {}
    
    @BeforeAll
    public static void setUpClass() {        
        String msg = "\n\n---------------- Initiating TaskTide Model Interface Tests ----------------\n";
        logger.info(msg);
    }
    
    @AfterAll
    public static void tearDownClass() {
        String msg = "\n\n---------------- Terminating TaskTide Model Interface Tests ----------------\n";
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
     * Should print json doc from casting
     */
    @Test
    @Order(0)
    public void castedDocShouldEqualDoc() {
    
        // Construct work item
        logger.info("\n\n================ Casting Model Test ================\n");
        String msg, castedMsg;
        boolean assertionState;
        WorkItem workItem;
        
        // Build work item
        workItem = new WorkItemBuilder()
                                .id("My WorkItem")
                                .itemName("My WorkItem Name")
                                .workload(TestUtils.makeTestWorkload())
                                .lockId("Some random hexadecimal string")
                                .lockDate(0L)
                                .doneDate(0L)
                                .taskCount(2)
                                .taskDone(0)
                                .build();
        logger.info("\nDisplaying WorkItem:\n" + workItem.toJsonDoc() + "\n");
        
        // Convert work item to json docs
        msg = workItem.toJsonDoc();
        castedMsg = ((TaskTideModel) workItem).toJson();
        logger.info("\nDisplaying casted JSON doc:\n" + castedMsg + "\n");
        
        // Run test
        assertionState = msg.equals(castedMsg);
        if (assertionState) {
            logger.info("\nTest successful with state:\t" + assertionState);
        }
        else {
            logger.error("\nTest unsuccessful with state:\t" + assertionState);
            logger.error("\nDisplaying WorkItem Doc:\n" + msg);
            logger.error("\nDisplaying TaskTideModel Doc:\n" + castedMsg);
        }
        
        // Log state
        logger.info("\n\n================ Casting Model Test ================\n");
        assertTrue(assertionState);
    }
    
    
    /**
     * Test that value for specified field is retrievable
     */
    @Test
    @Order(1)
    public void canFetchValueByField() {
    
        // Construct work item
        logger.info("\n\n================ Fetch Value for Field Model Test ================\n");
        boolean assertionState;
        TaskTideModel model;
        
        // Get model and check
        logger.info("Comparing reference to retrieved value");
        model = TestUtils.makeTestWorkItem();
        String modelId = model.getId();
        assertionState = modelId.equals( model.getValueFromField("id") );
        
        // Handle state
        if ( assertionState ) {
            logger.info("Fetch value for field test successful, state:\t" + assertionState + "\n");
        }
        else {
            logger.error("Fetch value for field test unsuccessful, state:\t" + assertionState + "\n");
        }
        assertTrue(assertionState);
        logger.info("\n\n================ Fetch Value for Field Model Test ================\n");
    }
}
