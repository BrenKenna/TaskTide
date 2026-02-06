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
package org.tasktide.itemstore.mutex;

import java.nio.file.Path;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;

import org.tasktide.itemstore.mutex.model.Mutex;
import org.tasktide.itemstore.mutex.model.MutexFactory;

import org.tasktide.itemstore.mutex.utils.MutexConstants;
import org.tasktide.itemstore.mutex.utils.MutexLabellingUtils;


/**
 * Suite of simple tests for bare functionality of 
 *  {@link MutexStrategy}
 *
 * @author Brendan Kenna
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SimpleMutexStrategyTests {
    
    // Configure logger
    private static final Logger LOGGER = LogManager.getLogger(SimpleMutexStrategyTests.class);
    
    public SimpleMutexStrategyTests() { }
    
    
    @BeforeAll
    public static void setUpClass() {
        
        // Setup class
        String msg = "\n\n---------------- Initiating Simple Mutex Strategy Tests ----------------\n";
        LOGGER.info(msg);
            
        // Set election file
        MutexTestUtils.configurePaths();
        MutexLabellingUtils.configure();
        
        // Check config
        LOGGER.info("Displaying config state:\t'{}'", MutexLabellingUtils.isConfigured());
    }
    
    
    @AfterAll
    public static void tearDownClass() {
        String msg = "\n\n---------------- Terminating Simple Mutex Strategy Tests ----------------\n";
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
     * Quite a bit under this one atm?
     * 
     */
    @Test
    @Order(0)
    public void canApplySingleElectionLock() {
    
        // Initialize test
        LOGGER.info("\n\n================ Tests Applying Single Election Lock ================\n");
        Path targetDir;
        Mutex mutex;
        boolean assertionState;
        
        // Create a mutex
        mutex = MutexFactory.create();
        LOGGER.info(
            "Created mutex:\t'{}'\n'{}'",
            MutexConstants.getElectionFile(),
            mutex.toJsonDoc()
        );
        
        // Fetch election lock
        assertionState = MutexStrategy.ELECTION.apply(mutex);
        if ( assertionState ) {
            LOGGER.info(
                "Lock successfully applied\n'{}'",
                mutex.toJsonDoc()
            );
        }
        else {
            LOGGER.error(
                "Unable to apply lock\n'{}'",
                mutex.toJsonDoc()
            );
        }
        
        // Evaluate test
        assertTrue(assertionState, "Error cannot apply single election lock");
        LOGGER.info("\n\n================ Tests Applying Single Election Lock ================\n");
    }
    
    
    /**
     * Quite a bit under this one atm?
     * 
     */
    @Test
    @Order(1)
    public void canReleaseSingleElectionLock() {
    
        // Initialize test
        LOGGER.info("\n\n================ Tests Releasing Single Election Lock ================\n");
        Path targetDir;
        Mutex mutex;
        boolean assertionState;
        
        // Create a mutex
        mutex = MutexFactory.create();
        LOGGER.info(
            "Created mutex:\t'{}'\n'{}'",
            MutexConstants.getElectionFile(),
            mutex.toJsonDoc()
        );
        
        // Fetch election lock
        assertionState = MutexStrategy.ELECTION.apply(mutex);
        
        // Evaluate test
        if ( assertionState ) {
            LOGGER.info("Lock successfully applied");
            assertionState = MutexStrategy.ELECTION.release(mutex);
            if ( assertionState ) {
                LOGGER.info("Released lock\n'{}'", mutex.toJsonDoc());
            }
            else {
                LOGGER.info("Unable to release lock\n'{}'", mutex.toJsonDoc());
            }
        }
        else {
            LOGGER.error("Unable to apply lock");
        }
        
        // Evaluate test
        assertTrue(assertionState, "Error cannot release single election lock");
        LOGGER.info("\n\n================ Tests Releasing Single Election Lock ================\n");
    }
    
    
    /**
     * Quite a bit under this one atm?
     * 
     */
    @Test
    @Order(2)
    public void canReleaseSingleFileChannelLock() {
    
        // Initialize test
        LOGGER.info("\n\n================ Tests Releasing Single FileChannel Lock ================\n");
        Path targetDir;
        Mutex mutex;
        boolean assertionState;
        
        // Create a mutex
        mutex = MutexFactory.create();
        LOGGER.info(
            "Created mutex:\t'{}'\n'{}'",
            MutexConstants.getLockFile(),
            mutex.toJsonDoc()
        );
        
        // Fetch election lock
        assertionState = MutexStrategy.FILE_CHANNEL.apply(mutex);
        
        // Evaluate test
        if ( assertionState ) {
            LOGGER.info("Lock successfully applied");
            assertionState = MutexStrategy.FILE_CHANNEL.release(mutex);
            if ( assertionState ) {
                LOGGER.info("Released lock\n'{}'", mutex.toJsonDoc());
            }
            else {
                LOGGER.info("Unable to release lock\n'{}'", mutex.toJsonDoc());
            }
        }
        else {
            LOGGER.error("Unable to apply lock");
        }
        
        // Evaluate test
        assertTrue(assertionState, "Error cannot release single FileChannel lock");
        LOGGER.info("\n\n================ Tests Releasing Single FileChannel Lock ================\n");
    }
}