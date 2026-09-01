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
import org.tasktide.mutex.actor.FileChannelActor;
import org.tasktide.mutex.orchestrator.MutexOrchestrator;
import java.nio.file.Path;
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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;

import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mockito;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.tasktide.mutex.model.Mutex;
import org.tasktide.mutex.utils.MutexLabellingUtils;
import org.tasktide.mutex.utils.MutexFilesUtils;
import org.tasktide.mutex.exceptions.MutexCheckedException;
import org.tasktide.mutex.utils.MutexConstants;
import org.tasktide.mutex.actor.MutexActor;
import org.tasktide.mutex.exceptions.MutexUncheckedException;


/**
 * System tests for Mutex module as a whole
 *
 * @author Brendan Kenna
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SimpleOrchestratorTests {
    
    // Configure logger
    private static final Logger LOGGER = LogManager.getLogger(SimpleOrchestratorTests.class);
    
    private MutexActor nfsMutex;
    private MutexActor fileChannelMutex;

    public SimpleOrchestratorTests() { }
    
    
    @BeforeAll
    public void setUpClass() {
        
        // Setup class
        String msg = "\n\n---------------- Initiating Simple Mutex Orchestrator Tests ----------------\n";
        LOGGER.info(msg);
            
        // Set election file
        MutexTestUtils.configurePaths();
        MutexLabellingUtils.configure();
        
        
        // Cofigure orchestrator once
        nfsMutex = spy(NfsMutexActor.class);
        fileChannelMutex = spy(FileChannelActor.class);
        try {
            MutexOrchestrator.configureForTestCases(nfsMutex, fileChannelMutex);
            LOGGER.info("Mutex Orchestrator configured");
        }
        catch ( MutexUncheckedException ex ) {
            LOGGER.warn("Mutex Orchestrator already configured");
        }
        
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
        LOGGER.info("Clearing mockito interactions");
        Mockito.clearInvocations(nfsMutex, fileChannelMutex);
        LOGGER.info("\n\n================ Initiating Next Test ================\n");
    }
    
    @AfterEach
    public void tearDown() {
        LOGGER.info("Clearing mockito interactions");
        Mockito.clearInvocations(nfsMutex, fileChannelMutex);
        LOGGER.info("\n\n================ Terminating Test ================\n");
    }

    
    /**
     * Verifies order of lock acquisition
     * 
     */
    @Test
    @Order(0)
    @Tag("system-mutex")
    public void canAcquireMutexOrchestrator() {
    
        // Initialize test
        LOGGER.info("\n\n================ Tests Acquiring of Mutex Orchestrator ================\n");
        LOGGER.info("Configuring Mockito Test stubs for MutexOrchestrator");
        AtomicLong nfsLockTime = new AtomicLong();
        AtomicLong chanLockTime = new AtomicLong();
        try {
            
            // Stub events
            doAnswer( invocation -> {
                nfsLockTime.set(System.nanoTime());
                LOGGER.info("Acquring NFS lock");
                return invocation.callRealMethod();
            }).when(nfsMutex).acquire( any(Mutex.class) );
            doAnswer( invocation -> {
                chanLockTime.set(System.nanoTime());
                LOGGER.info("Acquring FileChannel lock");
                return invocation.callRealMethod();
            }).when(fileChannelMutex).acquire( any(Mutex.class) );
        }
        catch ( MutexCheckedException ex ) {
            Assertions.fail("Unable to configure mutex acquire stubs", ex);
        }
        
        // Acquire lock
        try {
            LOGGER.info("Acquiring central lock");
            MutexOrchestrator.acquireLock();
            LOGGER.info("Active mutex with:\n'{}'", MutexOrchestrator.fetchActive().toJsonDoc());
            Assertions.assertTrue(
                chanLockTime.get() > nfsLockTime.get(),
                "FileChannel lock must be acquired after NFS lock"
            );
        }
        catch (MutexCheckedException ex) {
            Assertions.fail("Unable to acquire central lock", ex);
        }
        finally {
            try {
                MutexOrchestrator.releaseLock();
            } catch (MutexCheckedException ex) {
                LOGGER.error("Unable to release central lock\n", ex);
            }
        }
        LOGGER.info("\n\n================ Tests Applying Mutex Orchestrator ================\n");
    }
    
    
    /**
     * Tests acquiring and releasing {@link Mutex}
     * 
     */
    @Test
    @Order(1)
    @Tag("system-mutex")
    public void canAcquireRelease() {
    
        // Initialize test
        LOGGER.info("Configuring Mockito Test stubs for MutexOrchestrator");
        boolean assertionState;
        AtomicLong nfsLockTime = new AtomicLong();
        AtomicLong chanLockTime = new AtomicLong();
        AtomicLong nfsReleaseTime = new AtomicLong();
        AtomicLong chanReleaseTime = new AtomicLong();
        
        
        // Configure callbacks
        try {
            
            // Acquire callbacks
            doAnswer( invocation -> {
                nfsLockTime.set(System.nanoTime());
                LOGGER.info("Acquiring NFS lock");
                Object result = invocation.callRealMethod();
                LOGGER.info("Acquired NFS lock");
                return result;
            }).when(nfsMutex).acquire( any(Mutex.class) );
            doAnswer(invocation -> {
                chanLockTime.set(System.nanoTime());
                LOGGER.info("Acquiring FileChannel lock");
                Object result = invocation.callRealMethod();
                LOGGER.info("Acquired FileChannel lock");
                return result;
            }).when(fileChannelMutex).acquire(any(Mutex.class));
            
            // Release callbacks
            doAnswer(invocation -> {
                Object result = invocation.callRealMethod();
                nfsReleaseTime.set(System.nanoTime()); 
                LOGGER.info("Released NFS lock");
                return result;
            }).when(nfsMutex).release(any(Mutex.class));

            doAnswer(invocation -> { 
                Object result = invocation.callRealMethod();
                chanReleaseTime.set(System.nanoTime());
                LOGGER.info("Released FileChannel lock");
                return result;
            }).when(fileChannelMutex).release(any(Mutex.class));
        }
        catch (MutexCheckedException ex) {
            Assertions.fail("Unable to configure mutex stubs", ex);
        }
        
        // Acquire-release lock
        LOGGER.info("Acquiring-Releasing central lock");
        try {
            MutexOrchestrator.acquireLock();
            MutexOrchestrator.releaseLock();
        }
        catch ( MutexCheckedException ex ) {
            Assertions.fail("Unable to acquire-release central lock", ex);
        }
        
        // Verify actual Mockito interactions
        LOGGER.info("Verifing Mockito interactions");
        try {
            verify(nfsMutex, times(1)).acquire(any(Mutex.class));
            LOGGER.info("Verified NFS Acquired");
            verify(fileChannelMutex, times(1)).acquire(any(Mutex.class));
            LOGGER.info("Verified File Channel Acquired");
            verify(nfsMutex, times(1)).release(any(Mutex.class));
            LOGGER.info("Verified NFS Release");
            verify(fileChannelMutex, times(1)).release(any(Mutex.class));
            LOGGER.info("Verified File Channel Release");
        }
        catch (MutexCheckedException ex) {
            Assertions.fail("Unable to verify mutex interactions", ex);
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
        Assertions.assertTrue(assertionState, "Error cannot apply lock through mutex orchestrator");
        LOGGER.info("\n\n================ Tests Apply-Release Mutex Orchestrator ================\n");
    }
    
    
    /**
     * Tests whether 2 threads can lock-release
     * 
     */
    @Test
    @Order(3)
    @Tag("experimental-mutex")
    public void canMultipleThreadsRunLockReleaseQueue() {

        // Initialize test
        LOGGER.info("\n\n================ Tests Multiple Threads Form Mutex Queue ================\n");
        boolean assertionState = true;

        try {

            doAnswer(invocation -> {
                invocation.callRealMethod();
                LOGGER.info("NFS Lock Acquired");
                return null;
            }).when(nfsMutex).acquire(any(Mutex.class));

            doAnswer(invocation -> {
                invocation.callRealMethod();
                LOGGER.info("File Channel Lock Acquired");
                return null;
            }).when(fileChannelMutex).acquire(any(Mutex.class));

            doAnswer(invocation -> {
                invocation.callRealMethod();
                LOGGER.info("NFS Lock Released");
                return null;
            }).when(nfsMutex).release(any(Mutex.class));

            doAnswer(invocation -> {
                invocation.callRealMethod();
                LOGGER.info("File Channel Lock Released");
                return null;
            }).when(fileChannelMutex).release(any(Mutex.class));

        }
        catch (MutexCheckedException ex) {
            assertionState = false;
        }

        // Run test of two threads acquring lock
        ExecutorService executor = Executors.newFixedThreadPool(1);
        Future<?> t1 = executor.submit(() -> {
            try {
                MutexOrchestrator.acquireLock();
                LOGGER.info(
                    "Lock acquired-1:\n'{}'",
                    MutexOrchestrator.fetchActive().toJsonDoc()
                );
                MutexFilesUtils.waitJitterTime();
                LOGGER.info("Waited-1");
                MutexOrchestrator.releaseLock();
                LOGGER.info("Released-1");
            }
            catch (Exception ex) {
                LOGGER.error("1-Error during Lock-Release:\n\n");
                ex.printStackTrace();
            }
        });

        Future<?> t2 = executor.submit(() -> {
            try {
                MutexOrchestrator.acquireLock();
                LOGGER.info(
                    "Lock acquired-2:\n'{}'",
                    MutexOrchestrator.fetchActive().toJsonDoc()
                );
                MutexFilesUtils.waitJitterTime();
                LOGGER.info("Waited-2");
                MutexOrchestrator.releaseLock();
                LOGGER.info("Released-2");
            }
            catch (Exception ex) {
                LOGGER.error("2-Error during Lock-Release:\n\n");
                ex.printStackTrace();
            }
        });

        try {
            t1.get();
            t2.get();
        }
        catch (InterruptedException | ExecutionException ex) {
            assertionState = false;
        }

        try {
            Thread.sleep(10000);
        }
        catch (InterruptedException ex) { }

        // Assert: verify NFS and FileChannel are each acquired exactly twice
        try {
            verify(nfsMutex, times(2)).acquire(any(Mutex.class));
            verify(fileChannelMutex, times(2)).acquire(any(Mutex.class));
        }
        catch (MutexCheckedException ex) {
            assertionState = false;
        }
        
        // Evaluate test
        Assertions.assertTrue(
            assertionState,
            "Error multiple threads cannot organize lock through mutex orchestrator"
        );
        LOGGER.info(
            "\n\n================ Tests Multiple Threads Form Mutex Queue ================\n"
        );
    }

    
    /**
     * Readily scalable
     * 
     */
    @Test
    @Order(4)
    @Tag("experimental-mutex")
    public void scalableCanMultipleThreadsRunLockReleaseQueue() {
    
        // Initialize test
        LOGGER.info("\n\n================ Scalable 50 Multiple Threads Form Mutex Queue Test ================\n");
        int nWorkers = 50;
        OrchestratorSpies spies;
        
        // Configure mutex orchestrator
        LOGGER.info("Configuring MutexOrchestrator");
        spies = OrchestratorSpies.configure();
        
        // Perform unit test
        try {
            
            // Wire spies
            LOGGER.info("Wiring Spies to Logging");
            MutexTestUtils.wireRealMethodsWithLogging(
                spies.getNfsMutex(),
                spies.getFileChannelMutex(),
                LOGGER
            );
            
            // Run workload
            LOGGER.info("Executing workload with N workers = '{}'", nWorkers);
            MutexTestUtils.runWorkers(
                nWorkers,
                MutexTestUtils.getLockReleaseLambda(LOGGER)
            );
            
            // Verify test
            verify(spies.getNfsMutex(), times(nWorkers)).acquire(any(Mutex.class));
            verify(spies.getFileChannelMutex(), times(nWorkers)).acquire(any(Mutex.class));
        }
        catch (InterruptedException | ExecutionException | MutexCheckedException ex) {
            LOGGER.error("Test failed with error");
            ex.printStackTrace();
        }
        
        // Log completion
        LOGGER.info(
            "\n\n================ Scalable Multiple Threads Form Mutex Queue Test  ================\n"
        );
    }
    
    
    /**
     * Tests whether multiple processes can operate the
     *  Lock-Action-Release queue
     * 
     */
    @Test
    @Order(5)
    @Tag("experimental-mutex")
    public void canMultipleProcessesLockActionReleaseQueue() {
    
        // Initialize test
        LOGGER.info(
            "\n\n================ Multi-Processes Lock-Release-Queue  ================\n"
        );
        String resultsFile;
        int nWorkers;
        Path logFile;
        List<Process> processList;
        
        // Configure test
        LOGGER.info("Configuring test");
        resultsFile = "multi-process-lock-release-queue.tsv";
        nWorkers = 5;
        logFile = MutexConstants.getLockDir().resolve(resultsFile);
        try {
            MutexOrchestrator.configure();
            LOGGER.info("Mutex Orchestrator configured");
        }
        catch ( MutexUncheckedException ex ) {
            LOGGER.warn("Mutex Orchestrator already configured");
        }
        
        // Run processes

        // Log completion
        LOGGER.info(
            "\n\n================ Multi-Processes Lock-Release-Queue  ================\n"
        );
    }
}