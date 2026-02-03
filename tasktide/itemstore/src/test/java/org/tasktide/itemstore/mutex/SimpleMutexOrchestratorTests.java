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

import java.util.concurrent.atomic.AtomicLong;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;

import org.tasktide.itemstore.mutex.model.Mutex;
import org.tasktide.itemstore.mutex.utils.MutexLabellingUtils;
import org.tasktide.itemstore.mutex.exceptions.MutexCheckedException;


/**
 * 
 *
 * @author Brendan Kenna
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SimpleMutexOrchestratorTests {
    
    // Configure logger
    private static final Logger LOGGER = LogManager.getLogger(SimpleMutexOrchestratorTests.class);

    public SimpleMutexOrchestratorTests() { }
    
    
    @BeforeAll
    public static void setUpClass() {
        
        // Setup class
        String msg = "\n\n---------------- Initiating Simple Mutex Orchestrator Tests ----------------\n";
        LOGGER.info(msg);
            
        // Set election file
        MutexTestUtils.configurePaths();
        MutexLabellingUtils.configure();
        
        // Check config
        LOGGER.info("Displaying config state:\t'{}'", MutexLabellingUtils.isConfigured());
    }
    
    @AfterAll
    public static void tearDownClass() {
        String msg = "\n\n---------------- Terminating Simple Mutex Orchestrator Tests ----------------\n";
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
     * Verifies order of lock acquisition
     * 
     */
    @Test
    @Order(0)
    public void canAcquireMutexOrchestrator() {
    
        // Initialize test
        LOGGER.info("\n\n================ Tests Acquiring Mutex Orchestrator ================\n");
        MutexElection nfsMutex, fileChannelMutex;
        boolean assertionState;
        
        // Configure mutex orchestrator
        nfsMutex = mock(NfsMutex.class);
        fileChannelMutex = mock(FileChannelMutex.class);
        MutexOrchestrator.configureForTestCases(nfsMutex, fileChannelMutex);
        
        // Configure event callbacks
        LOGGER.info("Configuring Mockito Test stubs for MutexOrchestrator");
        AtomicLong nfsLockTime = new AtomicLong();
        AtomicLong chanLockTime = new AtomicLong();
        try {
            
            // Stub events
            doAnswer( invocation -> {
                nfsLockTime.set(System.currentTimeMillis());
                LOGGER.info("Acquring NFS lock");
                return null;
            }).when(nfsMutex).acquire( any(Mutex.class) );
            doAnswer( invocation -> {
                chanLockTime.set(System.currentTimeMillis());
                LOGGER.info("Acquring FileChannel lock");
                return null;
            }).when(fileChannelMutex).acquire( any(Mutex.class) );
        }
        catch ( MutexCheckedException ex ) {
            LOGGER.error("Unable to acquire lock via MutexOrchestrator");
        }
        
        // Acquire lock
        LOGGER.info("Acquiring central lock");
        try {
            MutexOrchestrator.acquireLock();
        }
        catch ( MutexCheckedException ex ) {
            LOGGER.error("Unable to acquire central lock");
        }
        
        // Verify ordering
        long nfsTime = nfsLockTime.get();
        long chanTime = chanLockTime.get();
        LOGGER.info(
            "\nNFS Lock Time:\t\t\t'{}'\nFile Channel Lock Time:\t\t'{}'",
            nfsTime, chanTime
        );
        if ( chanTime > nfsTime ) {
            assertionState = true;
            LOGGER.info("Success file channel lock acquired after the NFS lock");
        }
        else {
            assertionState = false;
            LOGGER.error("Error file channel lock acquired before the NFS lock");
        }
        
        // 
        assertTrue(assertionState, "Error cannot apply lock through mutex orchestrator");
        LOGGER.info("\n\n================ Tests Applying Mutex Orchestrator ================\n");
    }
    
    
    /**
     * Tests acquiring and releasing {@link Mutex}
     * 
     */
    @Test
    @Order(1)
    public void canAcquireRelease() {
    
        // Initialize test
        LOGGER.info("\n\n================ Tests Apply-Release Mutex Orchestrator ================\n");
        MutexElection nfsMutex, fileChannelMutex;
        boolean assertionState;
        
        // Configure mutex orchestrator
        nfsMutex = mock(NfsMutex.class);
        fileChannelMutex = mock(FileChannelMutex.class);
        MutexOrchestrator.configureForTestCases(nfsMutex, fileChannelMutex);
        
        // Configure event callbacks
        LOGGER.info("Configuring Mockito Test stubs for MutexOrchestrator");
        AtomicLong nfsLockTime = new AtomicLong();
        AtomicLong chanLockTime = new AtomicLong();
        
        // Configure acquire stubs
        try {
            
            // Stub events
            doAnswer( invocation -> {
                nfsLockTime.set(System.currentTimeMillis());
                LOGGER.info("Acquring NFS lock");
                return null;
            }).when(nfsMutex).acquire( any(Mutex.class) );
            doAnswer( invocation -> {
                chanLockTime.set(System.currentTimeMillis());
                LOGGER.info("Acquring FileChannel lock");
                return null;
            }).when(fileChannelMutex).acquire( any(Mutex.class) );
        }
        catch ( MutexCheckedException ex ) {
            LOGGER.error("Unable to acquire lock via MutexOrchestrator");
        }
        
        // Configure release stubs
        AtomicLong nfsReleaseTime = new AtomicLong();
        AtomicLong chanReleaseTime = new AtomicLong();
        try {
            
            // Stub events
            doAnswer( invocation -> {
                nfsReleaseTime.set( System.nanoTime() );
                LOGGER.info("Acquring NFS release time");
                return null;
            }).when(nfsMutex).release( any(Mutex.class) );
            doAnswer( invocation -> {
                chanReleaseTime.set( System.nanoTime() );
                LOGGER.info("Acquring FileChannel releae time");
                return null;
            }).when(fileChannelMutex).release( any(Mutex.class) );
        }
        catch ( MutexCheckedException ex ) {
            LOGGER.error("Unable to release lock via MutexOrchestrator");
        }
        
        // Acquire-release lock
        LOGGER.info("Acquiring-Releasing central lock");
        try {
            MutexOrchestrator.acquireLock();
            MutexOrchestrator.releaseLock();
        }
        catch ( MutexCheckedException ex ) {
            LOGGER.error("Unable to acquire-release central lock");
        }
        
        // Verify ordering
        long nfsLock = nfsLockTime.get();
        long chanLock = chanLockTime.get();
        long nfsRelease = nfsReleaseTime.get();
        long chanRelease = chanReleaseTime.get();
        
        LOGGER.info(
            "\nNFS Lock Time:\t\t\t'{}'\nFile Channel Lock Time:\t\t'{}'\nNFS Release:\t\t\t'{}'\nFile Channel Release:\t\t'{}'",
            nfsLock, chanLock, nfsRelease, chanRelease
        );
        if ( nfsRelease > chanRelease ) {
            assertionState = true;
            LOGGER.info("Success file channel lock-release acquired after the NFS lock");
        }
        else {
            assertionState = false;
            LOGGER.error("Error file channel lock-release acquired before the NFS lock");
        }
        
        // 
        assertTrue(assertionState, "Error cannot apply lock through mutex orchestrator");
        LOGGER.info("\n\n================ Tests Apply-Release Mutex Orchestrator ================\n");
    }
}