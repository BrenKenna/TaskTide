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

import org.tasktide.itemstore.mutex.model.Mutex;
import org.tasktide.itemstore.mutex.model.MutexFactory;

import org.tasktide.itemstore.mutex.utils.MutexFilesUtils;
import org.tasktide.itemstore.mutex.utils.MutexConstants;

import org.tasktide.itemstore.mutex.exceptions.MutexCheckedException;
import org.tasktide.itemstore.mutex.exceptions.MutexUncheckedException;


/**
 * Implementing {@link MutexElection} lock by composing the {@link NfsMutex},
 *  and {@link FileChannelMutex} which collectively offer a {@link Mutex} which
 *  can be acquired and released.
 * <br>
 * Currently the algorithm only runs as a waiter/holds the executing thread.
 *
 * @author Brendan Kenna
 */
public class MutexOrchestrator {
    
    // Logger
    private static final Logger LOGGER = LogManager.getLogger(MutexOrchestrator.class);
    
    // Flag for initialization
    private static volatile boolean isConfigured = false;
    
    // NFS & FileChannel mutexes
    private static volatile
        MutexElection NFS_MUTEX, FILE_CHANNEL_MUTEX;
    
    // Active mutex
    private static Mutex activeMutex;
    
    
    /**
     * Returns whether {@link MutexOrchestrator} is configured
     * 
     * @return boolean
     */
    public static synchronized boolean isInitialized() {
        return isConfigured;
    }
    
    
    /**
     * Configures {@link MutexOrchestrator} if not already
     * 
     * @throws {@link MutexUncheckedException}
     */
    public static synchronized void configure() {
        if ( isConfigured ) {
            throw new MutexUncheckedException("Mutex Orchestator already configured");
        }
        else {
            NFS_MUTEX = new NfsMutex();
            FILE_CHANNEL_MUTEX = new FileChannelMutex();
            isConfigured = true;
        }
    }
    
    
    /**
     * Configures {@link MutexOrchestrator} with provided
     *  {@link MutexElection}
     * 
     * @param nfsMutex
     * @param fileChannelMutex 
     */
    public static synchronized void configureForTestCases(MutexElection nfsMutex, MutexElection fileChannelMutex) {
        if ( isConfigured ) {
            throw new MutexUncheckedException("Mutex Orchestator already configured");
        }
        else {
            NFS_MUTEX = nfsMutex;
            FILE_CHANNEL_MUTEX = fileChannelMutex;
            isConfigured = true;
        }
    }

    
    /**
     * Acquire central {@link MutexElection} through {@link NfsMutex} lock
     *  then requesting {@link FileChannel} lock
     * 
     * @throws MutexCheckedException 
     */
    public static void acquireLock()
      throws MutexCheckedException {
    
        // Initialize variables
        Mutex mutex;
        Path targetDir;
        
        // Check if orchestrator is configured
        if ( !isConfigured ) {
            throw new MutexCheckedException("Mutex Orhcestrator must be configured");
        }
        MutexConstants.initializeDurations();
        MutexConstants.initializePaths();
        
        // Make mutex
        targetDir = MutexConstants.getElectionFile();
        mutex = MutexFactory.create(targetDir);
        
        // Acquire NFS lock
        try {
            LOGGER.info("Waiting for NFS lock");
            NFS_MUTEX.acquire(mutex);
            activeMutex = mutex;
        }
        catch (MutexUncheckedException ex) {
            throw new MutexCheckedException("Unable to acquire target mutex");
        }
        
        // Acquire File Channel lock
        try {
            LOGGER.info("Waiting for FileChannel lock");
            FILE_CHANNEL_MUTEX.acquire(mutex);
        }
        catch (MutexCheckedException ex) {
            throw new MutexCheckedException("Unable to acquire target mutex");
        }
    }
    
    
    /**
     * Releases {@link FileChannelLock} before the central {@link MutexElection}
     *  on {@link NfsMutex} using the {@link MutexFilesUtils} jitter time
     *  configured through {@link MutexConstants}
     * 
     * @throws MutexCheckedException 
     */
    public static void releaseLock()
      throws MutexCheckedException {
        
        // Check if the orchestrator is configured
        if ( !isConfigured ) {
            throw new MutexCheckedException("Mutex Orhcestrator must be configured");
        }
        if ( activeMutex == null ) {
            throw new MutexCheckedException("No active mutex detected");
        }
        
        // Release File Channel lock
        try {
            LOGGER.info("Releasing FileChannel mutex");
            FILE_CHANNEL_MUTEX.release(activeMutex);
            MutexFilesUtils.waitJitterTime();
        }
        catch (MutexCheckedException ex) {
            throw new MutexCheckedException("Unable to release file channel mutex");
        }
        
        // Release NFS mutex
        try {
            LOGGER.info("Releasing NFS mutex");
            NFS_MUTEX.release(activeMutex);
            MutexFilesUtils.waitJitterTime();
        }
        catch (MutexUncheckedException ex) {
            throw new MutexCheckedException("Unable to release NFS mutex");
        }
    }
}