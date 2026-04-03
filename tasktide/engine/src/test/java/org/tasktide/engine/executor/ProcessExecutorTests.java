/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit4TestClass.java to edit this template
 */
package org.tasktide.engine.executor;


import org.tasktide.engine.executor.ProcessExecutor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.tasktide.core.model.task.TaskLogging;


/**
 * Test class for the process execution package
 * 
 *
 * @author bkenna
 */
public class ProcessExecutorTests {
    
    private static final Logger LOGGER = LogManager.getLogger(ProcessExecutorTests.class);
    
    public ProcessExecutorTests() {
    }
    
    @BeforeAll
    public static void setUpClass() {
        String msg = "\n\n---------------- Initiating ProcessExecutor Tests ----------------\n";
        LOGGER.info(msg);
    }
    
    @AfterAll
    public static void tearDownClass() {
        String msg = "\n\n---------------- Terminating ProcessExecutor Tests ----------------\n";
        LOGGER.info(msg);
    }
    
    @BeforeEach
    public void setUp() {
        LOGGER.info("\n\n================ Initiating Next Test ================\n");
    }
    
    @AfterEach
    public void tearDown() {
        LOGGER.info("\n\n================ Terminating Next Test ================\n");
    }

    
    /**
     * Tests command with argument
     */
    @Test
    @Order(0)
    public void canRunPingProcess() {
    
        // Initialize test
        LOGGER.info("\n\n================ Can Run Ping Test ================\n");
        ProcessExecutor procExec;
        Process proc;
        String taskScript;
        boolean assertionState;
        
        // Run
        LOGGER.info("Configuring process executor");
        procExec = new ProcessExecutor();
        taskScript = "ping google.com";
        try {
            LOGGER.info("Processing task:\t'{}'", taskScript);
            proc = procExec.executeScript(taskScript);
            LOGGER.info("Task processing complete with exit code '{}'", proc.exitValue());
            assertionState = true;
        }
        catch (Exception ex) {
            LOGGER.error("Unable to process task:\n'{}'", ex.getMessage());
            ex.printStackTrace();
            assertionState = false;
        }
        
        // Evaluate test
        assertTrue(assertionState, "Failed to process test task");
        LOGGER.info("\n\n================ Can Run Ping Test ================\n");
    }
    
    
    /**
     * Tests command no arguments
     */
    @Test
    @Order(1)
    public void canRunHostNameProcess() {
    
        // Initialize test
        LOGGER.info("\n\n================ Can Run HostName Test ================\n");
        ProcessExecutor procExec;
        Process proc;
        String taskScript;
        boolean assertionState;
        
        // Run
        LOGGER.info("Configuring process executor");
        procExec = new ProcessExecutor();
        taskScript = "hostname";
        try {
            LOGGER.info("Processing task:\t'{}'", taskScript);
            proc = procExec.executeScript(taskScript);
            LOGGER.info("Task processing complete with exit code '{}'", proc.exitValue());
            assertionState = true;
        }
        catch (Exception ex) {
            LOGGER.error("Unable to process task:\n'{}'", ex.getMessage());
            ex.printStackTrace();
            assertionState = false;
        }
        
        // Evaluate test
        assertTrue(assertionState, "Failed to process test task");
        LOGGER.info("\n\n================ Can Run HostName Test ================\n");
    }
    

    /**
     * Tests the process of getting a task log from process execution
     */
    @Test
    @Order(2)
    public void canMakeTaskLogging() {
    
        // Initialize test
        LOGGER.info("\n\n================ Can Process Into TaskLogging Test ================\n");
        ProcessExecutor procExec;
        TaskLogging taskLog;
        String taskScript;
        boolean assertionState;
        
        // Run
        LOGGER.info("Configuring process executor");
        procExec = new ProcessExecutor();
        taskScript = "ping google.com";
        try {
            LOGGER.info("Processing task:\t'{}'", taskScript);
            taskLog = procExec.execute(taskScript);
            LOGGER.info("Task processing complete with exit code '{}'", taskLog.toJsonDoc());
            assertionState = true;
        }
        catch (Exception ex) {
            LOGGER.error("Unable to process task:\n'{}'", ex.getMessage());
            ex.printStackTrace();
            assertionState = false;
        }
        
        // Evaluate test
        assertTrue(assertionState, "Failed to process test task");
        LOGGER.info("\n\n================ Can Process Into TaskLogging Test ================\n");
    }
}