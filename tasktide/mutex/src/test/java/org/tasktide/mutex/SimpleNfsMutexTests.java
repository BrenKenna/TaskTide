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

import org.tasktide.mutex.actor.NfsMutexActor;
import java.nio.file.Path;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import org.tasktide.mutex.model.Mutex;
import org.tasktide.mutex.model.MutexFactory;
import org.tasktide.mutex.utils.MutexConstants;
import org.tasktide.mutex.utils.MutexLabellingUtils;

import org.tasktide.mutex.exceptions.MutexCheckedException;


/**
 * Suite of tests for bare functionality
 *  of {@link NfsMutexActor}
 *
 * @author Brendan Kenna
 */
@Tag("unit-mutex")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SimpleNfsMutexTests {
    
    // Configure logger
    private static final Logger LOGGER = LogManager.getLogger(SimpleNfsMutexTests.class);
    private final NfsMutexActor NFS_MUTEX;

    public SimpleNfsMutexTests() {
        NFS_MUTEX = new NfsMutexActor();
    }
    
    
    @BeforeAll
    public static void setUpClass() {
        
        // Setup class
        String msg = "\n\n---------------- Initiating SimpleNfsMutex Tests ----------------\n";
        LOGGER.info(msg);
            
        // Set election file
        MutexTestUtils.configurePaths();
        MutexLabellingUtils.configure();
        
        // Check config
        LOGGER.info("Displaying config state:\t'{}'", MutexLabellingUtils.isConfigured());
    }
    
    @AfterAll
    public static void tearDownClass() {
        String msg = "\n\n---------------- Terminating SimpleNfsMutex Tests ----------------\n";
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
    public void canApplySingleNfsLock() {
    
        // Initialize test
        LOGGER.info("\n\n================ Tests Applying Single NFS Lock ================\n");
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
        try {
            assertionState = NFS_MUTEX.acquire(mutex);
        }
        catch ( MutexCheckedException ex ) {
            assertionState = false;
        }
        
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
        assertTrue(assertionState, "Error cannot apply single NFS lock");
        LOGGER.info("\n\n================ Tests Applying Single NFS Lock ================\n");
    }
    
    
    /**
     * Quite a bit under this one atm?
     * 
     */
    @Test
    @Order(1)
    public void canReleaseSingleNfsLock() {
    
        // Initialize test
        LOGGER.info("\n\n================ Tests Releasing Single NFS Lock ================\n");
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
        try {
            assertionState = NFS_MUTEX.acquire(mutex);
            if ( assertionState ) {
                assertionState = NFS_MUTEX.release();
            }
        }
        catch ( MutexCheckedException ex ) {
            assertionState = false;
        }
        
        if ( assertionState ) {
            LOGGER.info(
                "Lock successfully applied-released\n'{}'",
                mutex.toJsonDoc()
            );
        }
        else {
            LOGGER.error(
                "Unable to apply-release lock\n'{}'",
                mutex.toJsonDoc()
            );
        }
        
        // Evaluate test
        assertTrue(assertionState, "Error cannot apply-releae single NFS lock");
        LOGGER.info("\n\n================ Tests Releasing Single NFS Lock ================\n");
    }
}