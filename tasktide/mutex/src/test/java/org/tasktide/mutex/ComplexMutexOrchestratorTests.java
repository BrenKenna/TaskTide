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

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

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
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.tasktide.mutex.model.Mutex;
import org.tasktide.mutex.utils.MutexLabellingUtils;
import org.tasktide.mutex.utils.MutexFilesUtils;
import org.tasktide.mutex.exceptions.MutexCheckedException;
import org.tasktide.mutex.utils.MutexConstants;
import org.tasktide.mutex.actor.MutexActor;

import org.tasktide.mutex.actor.NfsMutexActor;
import org.tasktide.mutex.actor.FileChannelActor;
import org.tasktide.mutex.orchestrator.MutexOrchestrator;


/**
 * Test suite for evaluating multi-threaded and multi-process
 *  efficiency of the {@link MutexOrchestrator} supported by
 *  methods from {@link MutexTestUtils}, and the
 *  {@link LockReleaseProcess} application
 * 
 * @author Brendan Kenna
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ComplexMutexOrchestratorTests {
    
    // Configure logger
    private static final Logger LOGGER = LogManager.getLogger(ComplexMutexOrchestratorTests.class);

    public ComplexMutexOrchestratorTests() { }
    
    
    @BeforeAll
    public static void setUpClass() {
        
        // Setup class
        String msg = "\n\n---------------- Initiating Complex Mutex Orchestrator Tests ----------------\n";
        LOGGER.info(msg);
            
        // Set election file
        MutexTestUtils.configurePaths();
        MutexLabellingUtils.configure();
        
        // Check config
        LOGGER.info("Displaying config state:\t'{}'", MutexLabellingUtils.isConfigured());
    }
    
    @AfterAll
    public static void tearDownClass() {
        String msg = "\n\n---------------- Terminating Complex Mutex Orchestrator Tests ----------------\n";
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
     * Tests whether 2 threads can lock-release
     * 
     */
    @Test
    @Order(0)
    public void canMultipleThreadsRunLockReleaseQueue() {

        // Initialize test
        LOGGER.info("\n\n================ Tests Multiple Threads Form Mutex Queue ================\n");
        MutexActor nfsMutex;
        MutexActor fileChannelMutex;
        boolean assertionState = true;

        // Arrange test spy NFS & FileChannelActor
        nfsMutex = spy(new NfsMutexActor());
        fileChannelMutex = spy(new FileChannelActor());
        MutexOrchestrator.configureForTestCases(nfsMutex, fileChannelMutex);

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
        assertTrue(
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
    @Order(1)
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
     *  Lock-Action-Release queue. Silent failure happening
     *   somewhere, should track-down have client handler
     *   try again if such a MutexProcessError occurs.
     * 
     */
    @Test
    @Order(2)
    public void canMultipleProcessesLockActionReleaseQueue() {
    
        // Initialize test
        LOGGER.info(
            "\n\n================ Multi-Processes Lock-Release-Queue  ===================\n"
        );
        boolean assertionState;
        int nWorkers;
        Path resultsFile;
        List<Process> processList;
        List<Integer> results = new ArrayList<>();
        
        // Configure test
        LOGGER.info("Configuring test");
        nWorkers = 40;
        resultsFile = MutexConstants.getLockDir().resolve("multi-process.txt");
        MutexOrchestrator.configure();
        
        // Run processes
        LOGGER.info(
            "Running N = '{}' LockActionReleaseApplication processes",
            nWorkers
        );
        try {
            
            // Run applications
            processList = MutexTestUtils.runLockReleaseProcesses(nWorkers, resultsFile);
            
            // Wait for processes
            LOGGER.info("Processes submitted, waiting for completion");
            for(Process proc : processList) {
                int exitCode = proc.waitFor();
                LOGGER.info("Process exited with code:\t'{}'", exitCode);
            }
            
            // Try fetch results
            LOGGER.info(
                "Processing complete, fetching results from '{}'",
                resultsFile
            );
            try {
                
                // Fetch results
                results = MutexTestUtils
                 .fetchLockReleaseProcessResultFile(nWorkers, resultsFile);
                
                // Examine results
                LOGGER.info("Results acquired searching for overlapping start-end times");
                assertionState = MutexTestUtils.examineResultList(results);
            }
            catch (Exception ex) {
                LOGGER.error("Test failed result parsing phase");
                assertionState = false;
            }
        }
        catch (IOException | InterruptedException ex) {
            LOGGER.error("Test failed application phase");
            assertionState = false;
        }
        
        
        // Display results if successful
        LOGGER.info(
            "Displaying results for reference:\n\n'{}'",
            MutexFilesUtils.toJson(results)
        );
        
        // Evaluate test
        assertTrue(
            assertionState,
            "Error multiple threads cannot organize lock through mutex orchestrator"
        );
        LOGGER.info(
            "\n\n================ Multi-Processes Lock-Release-Queue  ================\n"
        );
    }
}