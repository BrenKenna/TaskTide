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
package org.tasktide.itemstore.mutex.model;

import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.tasktide.itemstore.mutex.utils.MutexFilesUtils;
import org.tasktide.itemstore.mutex.utils.MutexLabellingUtils;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Order;
import org.tasktide.itemstore.mutex.MutexTestUtils;


/**
 * Confirms creation of {@link HostLock}, and {@link Mutex}
 *  with {@link HostLockFactory} and {@link MutexFactory}.
 * 
 * @author Brendan Kenna
 */
public class FactoryTests {
    
    // Configure logger
    private static final Logger LOGGER = LogManager.getLogger(FactoryTests.class);

    public FactoryTests() {}
    
    @BeforeAll
    public static void setUpClass() {
        
        // Setup class
        String msg = "\n\n---------------- Initiating FactoryTests Tests ----------------\n";
        LOGGER.info(msg);
            
        // Set election file
        MutexTestUtils.configurePaths();
        MutexLabellingUtils.configure();
        
        // Check config
        LOGGER.info("Displaying config state:\t'{}'", MutexLabellingUtils.isConfigured());
    }
    
    @AfterAll
    public static void tearDownClass() {
        String msg = "\n\n---------------- Terminating FactoryTests Tests ----------------\n";
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
     * Confirms reading/writing {@link Mutex}
     * 
     */
    @Test
    @Order(0)
    public void canMakeAndReadMutex() {
    
        // Initialize test
        LOGGER.info("\n\n================ Can Make and Read Mutex Test ================\n");
        boolean assertionState;
        
        // Create mutex
        Optional<Mutex> mut;
        Mutex canon, fromFile;
        canon = MutexFactory.create();
        
        // Display record for reference
        LOGGER.info(
            "Displaying mutex for reference:\n\n'{}'",
            canon.toJsonDoc()
        );
        
        // Write & read mutex
        MutexFilesUtils.writeElectionFile(canon);
        mut = MutexFilesUtils.readMutexFromFile(canon.getElectionFile());
        if ( mut.isPresent() ) {
            fromFile = mut.get();
            assertionState = canon.getId().equals(fromFile.getId());
        }
        else {
            assertionState = false;
        }
        
        // Evaluate test
        assertTrue(assertionState, "Error cannot persist mutex");
        LOGGER.info("\n\n================ Can Make and Read Mutex Test ================\n");
    }
    
    
    /**
     * Confirms reading/writing {@link Mutex}
     * 
     */
    @Test
    @Order(1)
    public void canMakeAndReadHostLock() {
    
        // Initialize test
        LOGGER.info("\n\n================ Can Make and Display HostLock Test ================\n");
        
        // Create mutex
        HostLock hostLock;
        Mutex mutex;
        mutex = MutexFactory.create();
        hostLock = HostLockFactory.create(mutex.getElectionFile());
        
        // Display record for reference
        LOGGER.info(
            "Displaying host lock:\n\n'{}'",
            hostLock.toJson()
        );
        
        // Evaluate test
        LOGGER.info("\n\n================ Can Make and Display HostLock Test ================\n");
    }
}