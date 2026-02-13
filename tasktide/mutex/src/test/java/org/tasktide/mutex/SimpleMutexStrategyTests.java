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
package org.tasktide.mutex;

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

import org.tasktide.mutex.model.Mutex;
import org.tasktide.mutex.model.MutexFactory;

import org.tasktide.mutex.strategy.ElectionStrategy;
import org.tasktide.mutex.strategy.FileChannelStrategy;
import org.tasktide.mutex.strategy.MutexStrategy;

import org.tasktide.mutex.utils.MutexConstants;
import org.tasktide.mutex.utils.MutexFilesUtils;
import org.tasktide.mutex.utils.MutexLabellingUtils;


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
    private final MutexStrategy ELECTION, FILE_CHANNEL;
    
    public SimpleMutexStrategyTests() {
        this.ELECTION = new ElectionStrategy();
        this.FILE_CHANNEL = new FileChannelStrategy();
    }
    
    
    @BeforeAll
    public static void setUpClass() {
        
        // Setup class
        String msg = "\n\n---------------- Initiating Simple Mutex Strategy Tests ----------------\n";
        LOGGER.info(msg);
            
        // Set election file
        MutexTestUtils.configurePaths();
        MutexLabellingUtils.configure();
        
        // Check config
        LOGGER.info(
            "Displaying config state:\t'{}'",
            MutexLabellingUtils.isConfigured()
        );
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
    public void canApplyReleaseSingleElectionLock() {
    
        // Initialize test
        LOGGER.info("\n\n================ Tests Applying Single Election Lock ================\n");
        int count = 0;
        Mutex mutex;
        
        // Create a mutex
        mutex = MutexFactory.create();
        LOGGER.info(
            "Created mutex:\t'{}'\n'{}'",
            MutexConstants.getElectionFile(),
            mutex.toJsonDoc()
        );
        
        // Fetch election lock
        if ( ELECTION.apply(mutex) ) {
            LOGGER.info(
                "Lock successfully applied\n'{}'",
                mutex.toJsonDoc()
            );
            count++;
        }
        else {
            LOGGER.error(
                "Unable to apply lock\n'{}'",
                mutex.toJsonDoc()
            );
        }
        
        // Let time pass then release lock
        MutexFilesUtils.waitJitterTime();
        if ( ELECTION.release(mutex) ) {
            LOGGER.info(
                "Lock successfully applied\n'{}'",
                mutex.toJsonDoc()
            );
            count++;
        }
        else {
            LOGGER.error(
                "Unable to apply lock\n'{}'",
                mutex.toJsonDoc()
            );
        }
        
        // Evaluate test
        assertTrue(count == 2, "Error cannot apply single election lock");
        LOGGER.info("\n\n================ Tests Applying Single Election Lock ================\n");
    }
    
    
    /**
     * Quite a bit under this one atm?
     * 
     */
    @Test
    @Order(1)
    public void canApplyReleaseSingleFileChannelLock() {
    
        // Initialize test
        LOGGER.info("\n\n================ Tests Releasing Single File Channel Lock ================\n");
        int count = 0;
        Mutex mutex;
        
        // Create a mutex
        mutex = MutexFactory.create();
        LOGGER.info(
            "Created mutex:\t'{}'\n'{}'",
            MutexConstants.getElectionFile(),
            mutex.toJsonDoc()
        );
        
        // Fetch election lock
        if ( FILE_CHANNEL.apply(mutex) ) {
            LOGGER.info(
                "Lock successfully applied\n'{}'",
                mutex.toJsonDoc()
            );
            count++;
        }
        else {
            LOGGER.error(
                "Unable to apply lock\n'{}'",
                mutex.toJsonDoc()
            );
        }
        
        // Let time pass then release lock
        MutexFilesUtils.waitJitterTime();
        if ( FILE_CHANNEL.release(mutex) ) {
            LOGGER.info(
                "Lock successfully applied\n'{}'",
                mutex.toJsonDoc()
            );
            count++;
        }
        else {
            LOGGER.error(
                "Unable to apply lock\n'{}'",
                mutex.toJsonDoc()
            );
        }
        
        // Evaluate test
        assertTrue(count == 2, "Error cannot release single file channel lock");
        LOGGER.info("\n\n================ Tests Releasing Single File Channel Lock ================\n");
    }
}