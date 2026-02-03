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

import java.util.List;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
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
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.tasktide.itemstore.mutex.model.Mutex;
import org.tasktide.itemstore.mutex.utils.MutexLabellingUtils;
import org.tasktide.itemstore.mutex.utils.MutexFilesUtils;
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
        nfsMutex = spy(NfsMutex.class);
        fileChannelMutex = spy(FileChannelMutex.class);
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
                return invocation.callRealMethod();
            }).when(nfsMutex).acquire( any(Mutex.class) );
            doAnswer( invocation -> {
                chanLockTime.set(System.currentTimeMillis());
                LOGGER.info("Acquring FileChannel lock");
                return invocation.callRealMethod();
            }).when(fileChannelMutex).acquire( any(Mutex.class) );
        }
        catch ( MutexCheckedException ex ) {
            LOGGER.error("Unable to acquire lock via MutexOrchestrator");
        }
        
        // Acquire lock
        LOGGER.info("Acquiring central lock");
        try {
            MutexOrchestrator.acquireLock();
            LOGGER.info("Active mutex with:\n'{}'", MutexOrchestrator.fetchActive().toJsonDoc());
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
        System.out.println();
        MutexElection nfsMutex, fileChannelMutex;
        boolean assertionState;
        
        // Configure mutex orchestrator
        nfsMutex = spy(NfsMutex.class);
        fileChannelMutex = spy(FileChannelMutex.class);
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
                return invocation.callRealMethod();
            }).when(nfsMutex).acquire( any(Mutex.class) );
            doAnswer( invocation -> {
                chanLockTime.set(System.currentTimeMillis());
                LOGGER.info("Acquring FileChannel lock");
                return invocation.callRealMethod();
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
                LOGGER.info("Releasing NFS lock");
                return invocation.callRealMethod();
            }).when(nfsMutex).release( any(Mutex.class) );
            doAnswer( invocation -> {
                chanReleaseTime.set( System.nanoTime() );
                LOGGER.info("Releasing FileChannel lock");
                return invocation.callRealMethod();
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
            ex.printStackTrace();
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
        
        // Log state
        assertTrue(assertionState, "Error cannot apply lock through mutex orchestrator");
        LOGGER.info("\n\n================ Tests Apply-Release Mutex Orchestrator ================\n");
    }
    
    
    /**
     * 
     */
    @Test
    @Order(3)
    public void multipleThreadsQueueLocksA() {
    
        // Initialize test
        LOGGER.info("\n\n================ Tests Multiple Threads Form Mutex Queue ================\n");
        MutexElection nfsMutex, fileChannelMutex;
        Mutex initial, current;
        List<Future<?>> lockReqs;
        boolean assertionState;
        
        // Arrange: spy MutexElection and FileChannelMutex process
        nfsMutex = spy(NfsMutex.class);
        fileChannelMutex = spy(FileChannelMutex.class);
        MutexOrchestrator.configureForTestCases(nfsMutex, fileChannelMutex);

        try {
            doAnswer(invocation -> {
                invocation.callRealMethod();
                LOGGER.info("NFS Lock Acquired:\n'{}'", MutexOrchestrator.fetchActive().toJsonDoc());
                return null;
            }).when(nfsMutex).acquire(any(Mutex.class));
            
            doAnswer(invocation -> {
                invocation.callRealMethod();
                LOGGER.info("File Channel Lock Acquired:\n'{}'", MutexOrchestrator.fetchActive().toJsonDoc());
                return null;
            }).when(fileChannelMutex).acquire(any(Mutex.class));
            
            doAnswer(invocation -> {
                invocation.callRealMethod();
                LOGGER.info("NFS Lock Released:\n'{}'", MutexOrchestrator.fetchActive());
                return null;
            }).when(nfsMutex).release(any(Mutex.class));
            
            doAnswer(invocation -> {
                invocation.callRealMethod();
                LOGGER.info("File Channel Lock Released:\n'{}'", MutexOrchestrator.fetchActive());
                return null;
            }).when(fileChannelMutex).release(any(Mutex.class));
        }
        catch (MutexCheckedException ex) {
            assertionState = false;
        }

        // Act: run two threads that both try to acquire the lock
        ExecutorService executor = Executors.newFixedThreadPool(2);
        Future<?> t1 = executor.submit(() -> {
            try {
                MutexOrchestrator.acquireLock();
                MutexOrchestrator.releaseLock();
            } catch (Exception ex) { }
        });
        Future<?> t2 = executor.submit(() -> {
            try {
                MutexOrchestrator.acquireLock();
            } catch (Exception ex) { }
        });

        try {
            t1.get();
        } catch (InterruptedException | ExecutionException ex ) {
            assertionState = false;
        } 
        try {
            t2.get();
        } catch (InterruptedException | ExecutionException ex ) {
            assertionState = false;
        } 
        
        try {
            Thread.sleep(10000);
        }
        catch (InterruptedException ex) { }

        // Assert: verify NFS and FileChannel are each acquired exactly twice
        try {
            if ( verify(nfsMutex, times(2)).acquire(any(Mutex.class)) ) {
                assertionState = verify(fileChannelMutex, times(2)).acquire(any(Mutex.class));
            }
            else {
                assertionState = false;
            }
        }
        catch ( MutexCheckedException ex ) {
            assertionState = false;
        }
        
        
        // Evaluate test
        assertTrue(assertionState, "Error multiple threads cannot organize lock through mutex orchestrator");
        LOGGER.info("\n\n================ Tests Multiple Threads Form Mutex Queue ================\n");
    }
    
    
    /**
     * 
     */
    @Test
    @Order(3)
    public void multipleThreadsQueueLocks() {
    
        // Initialize test
        LOGGER.info("\n\n================ Tests Multiple Threads Form Mutex Queue ================\n");
        MutexElection nfsMutex, fileChannelMutex;
        Mutex initial, current;
        List<Future<?>> lockReqs;
        boolean assertionState;
        
        // Configure mutex orchestrator
        LOGGER.info("Configuring MutexOrchestrator");
        nfsMutex = spy(NfsMutex.class);
        fileChannelMutex = spy(FileChannelMutex.class);
        MutexOrchestrator.configureForTestCases(nfsMutex, fileChannelMutex);
        
        // Submit lock requests
        LOGGER.info("Acquiring locks");
        ExecutorService execServ = Executors.newFixedThreadPool(3);
        lockReqs = MutexTestUtils.lockThenRelease(execServ, 3);
        
        // Display active
        MutexFilesUtils.waitJitterTime();
        initial = MutexOrchestrator.fetchActive();
        if ( initial != null ) {
            LOGGER.info("Displaying active mutex:\n'{}'", initial.toJsonDoc());
            try {
                MutexOrchestrator.releaseLock();
                MutexFilesUtils.waitJitterTime();
                current = MutexOrchestrator.fetchActive();
                assertionState = !initial.getId().equals(current.getId());
            }
            catch ( MutexCheckedException ex ) {
                assertionState = false;
            }
        }
        else {
            assertionState = false;
        }
        
        // Evaluate test
        assertTrue(assertionState, "Error multiple threads cannot organize lock through mutex orchestrator");
        LOGGER.info("\n\n================ Tests Multiple Threads Form Mutex Queue ================\n");
    }
}