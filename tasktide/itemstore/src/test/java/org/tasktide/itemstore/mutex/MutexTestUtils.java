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
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import org.tasktide.itemstore.mutex.utils.MutexConstants;
import org.tasktide.itemstore.mutex.utils.MutexLabellingUtils;

import org.tasktide.itemstore.mutex.exceptions.MutexCheckedException;


/**
 *
 * @author Brendan Kenna
 */
public class MutexTestUtils {
    
    // Logger
    private static final Logger LOGGER = LogManager.getLogger(MutexTestUtils.class);
    
    
    /**
     * Configure paths
     * 
     */
    public static void configurePaths() {
    
        // Configure duration constants
        String nodeProcId = MutexLabellingUtils.getNodeProcId();
        MutexConstants.initializeDurations();
        
        // Configure paths and directories
        Path cwd = Paths.get("").toAbsolutePath();
        Path targetPath = cwd.resolve("ItemStore-Mutex");
        Path hostLockDir = targetPath.resolve("Host-Lock");
        Path electionDir = targetPath.resolve("Queue");
        
        // Configure files
        Path lockingFile = targetPath.resolve("lock-file.lock");
        Path electionFile = electionDir.resolve(
            System.currentTimeMillis() +
            nodeProcId +
            ".lock"
        );
        
        // Initialize paths
        MutexConstants.initializePaths(
            targetPath, lockingFile,
            electionDir, electionFile,
            hostLockDir
        );
    }
    
    
    /**
     * Fetch required number of locks, each lock is spawned in separate thread
     * 
     * @param execServ
     * @param nTasks
     * 
     * @return List-Future
     */
    public static List<Future<?>> fetchLockNoRelease(ExecutorService execServ, int nTasks) {
    
        // Initialize output
        ArrayList<Future<?>> output = new ArrayList<>();
        
        // Fire attempts
        for ( int i = 0; i < nTasks; i++ ) {
            String val = "Lock-" + i;
            Future<?> task = execServ.submit(() -> {
                try {
                    MutexOrchestrator.acquireLock();
                    LOGGER.info("Locked by the below mutex:\n'{}'", MutexOrchestrator.fetchActive().toJsonDoc());
                }
                catch ( MutexCheckedException ex ) {
                    LOGGER.error("Error acquring ", val);
                }
            });
            output.add(task);
        }
        
        // Let time ellapse
        try {
            Thread.sleep(10000);
        }
        catch (InterruptedException ex) { }
        
        // Return output
        return output;
    }
    
    
    /**
     * Release active lock
     * 
     * @param execServ
     * @param nTasks
     * @return List-Future
     */
    public static List<Future<?>> lockThenRelease(ExecutorService execServ, int nTasks) {
    
        // Initialize output
        ArrayList<Future<?>> output = new ArrayList<>();
        
        // Fire attempts
        for ( int i = 0; i < nTasks; i++ ) {
            String val = "Lock-" + i;
            Future<?> task = execServ.submit(() -> {
                try {
                    MutexOrchestrator.acquireLock();
                    LOGGER.info("Locked by the below mutex:\n'{}'", MutexOrchestrator.fetchActive().toJsonDoc());
                    MutexOrchestrator.releaseLock();
                    LOGGER.info("Locked now by the below mutex:\n'{}'", MutexOrchestrator.fetchActive().toJsonDoc());
                }
                catch ( MutexCheckedException ex ) {
                    LOGGER.error("Error acquring ", val);
                }
            });
            output.add(task);
        }
        
        // Let time ellapse
        try {
            Thread.sleep(10000);
        }
        catch (InterruptedException ex) { }
        
        // Return output
        return output;
    }
    
    
    /**
     * Fetch required number of locks, each lock is spawned in separate thread
     * 
     * @param execServ
     * @param nTasks
     * 
     * @return List-Future
     */
    public static List<Future<?>> releaseActiveLock(ExecutorService execServ, int nTasks) {
    
        // Initialize output
        ArrayList<Future<?>> output = new ArrayList<>();
        
        // Fire attempts
        for ( int i = 0; i < nTasks; i++ ) {
            String val = "Lock-" + i;
            Future<?> task = execServ.submit(() -> {
                try {
                    LOGGER.info("Releasing active mutex:\n'{}'", MutexOrchestrator.fetchActive());
                    MutexOrchestrator.releaseLock();
                    
                }
                catch ( MutexCheckedException ex ) {
                    LOGGER.error("Error releasing active mutex '{}'", val);
                }
            });
            output.add(task);
        }
        
        // Let time ellapse
        try {
            Thread.sleep(10000);
        }
        catch (InterruptedException ex) { }
        
        // Return output
        return output;
    }
}