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


import java.util.HashMap;
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
import org.junit.jupiter.api.TestMethodOrder;

import org.tasktide.core.model.task.TaskLogging;


/**
 * Test class for the process execution package
 * 
 *
 * @author bkenna
 */
@Tag("unit-engine-active")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
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
    public void canRunNsLookupProcess() {
    
        // Initialize test
        LOGGER.info("\n\n================ Can Run NS Lookup Test ================\n");
        ProcessExecutor procExec;
        Process proc;
        String taskScript;
        boolean assertionState;
        
        // Run
        LOGGER.info("Configuring process executor");
        procExec = new ProcessExecutor();
        taskScript = "nslookup google.com";
        try {
            LOGGER.info("Processing task:\t'{}'", taskScript);
            proc = procExec.executeScript(taskScript, "ns-lookup-task", new HashMap<>());
            LOGGER.info("Task processing complete with exit code '{}'", proc.exitValue());
            assertionState = true;
        }
        catch (Exception ex) {
            LOGGER.error("Unable to process task:\n'{}'", ex);
            assertionState = false;
        }
        
        // Evaluate test
        assertTrue(assertionState, "Failed to process test task");
        LOGGER.info("\n\n================ Can Run NS Lookup Test ================\n");
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
            proc = procExec.executeScript(taskScript, "hostname-task", new HashMap<>());
            LOGGER.info("Task processing complete with exit code '{}'", proc.exitValue());
            assertionState = true;
        }
        catch (Exception ex) {
            LOGGER.error("Unable to process task:\n'{}'", ex);
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
        taskScript = "nslookup google.com";
        try {
            LOGGER.info("Processing task:\t'{}'", taskScript);
            taskLog = procExec.execute(taskScript, "nslookup-task", new HashMap<>());
            LOGGER.info("Task processing complete with exit code '{}'", taskLog.toJsonDoc());
            assertionState = true;
        }
        catch (Exception ex) {
            LOGGER.error("Unable to process task:\n'{}'", ex);
            assertionState = false;
        }
        
        // Evaluate test
        assertTrue(assertionState, "Failed to process test task");
        LOGGER.info("\n\n================ Can Process Into TaskLogging Test ================\n");
    }
}