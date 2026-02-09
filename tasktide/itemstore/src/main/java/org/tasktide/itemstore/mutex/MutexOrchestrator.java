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

import java.nio.channels.FileChannel;
import java.nio.file.Path;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.tasktide.itemstore.FileUtility;
import org.tasktide.itemstore.mutex.exceptions.ActiveMutexCheckedException;

import org.tasktide.itemstore.mutex.model.Mutex;
import org.tasktide.itemstore.mutex.model.MutexFactory;

import org.tasktide.itemstore.mutex.utils.MutexFilesUtils;
import org.tasktide.itemstore.mutex.utils.MutexConstants;

import org.tasktide.itemstore.mutex.exceptions.MutexCheckedException;
import org.tasktide.itemstore.mutex.exceptions.MutexUncheckedException;
import org.tasktide.itemstore.mutex.model.MutexFileType;


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
    private static volatile boolean isConfigured = false, pathsInit;
    
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
        
        // Check if orchestrator is configured
        if ( !isConfigured ) {
            throw new MutexCheckedException("Mutex Orhcestrator must be configured");
        }
        if ( !pathsInit ) {
            MutexConstants.initializeDurations();
            MutexConstants.initializePaths();
            pathsInit = true;
        }
        
        // Make mutex
        Mutex mutex;
        MutexFilesUtils.waitJitterTime();
        mutex = MutexFactory.create();

        // Perform locking
        performLock(mutex);
    }
    
    
    /**
     * Try acquire lock until successful
     */
    public static void tryAcquireUntilSuccess() {
        boolean done = false;
        while ( !done ) {
            boolean toFinish;
            try {
                acquireLock();
                toFinish = true;
            }
            
            catch ( Exception ex ) {
                LOGGER.warn("Lock acquisition failed, retrying:\n{}", ex.getMessage());
                MutexFilesUtils.waitJitterTime();
                toFinish = false;
            }
            
            if ( toFinish ) {
                done = true;
            }
        }
    }
    
    
    /**
     * Performs 
     * 
     * @param mutex
     * @throws MutexCheckedException 
     */
    private static void performLock(Mutex mutex) throws MutexCheckedException {
    
        // Acquire NFS lock
        try {
            LOGGER.info("Waiting for NFS lock");
            NFS_MUTEX.acquire(mutex);
            activeMutex = mutex;
            LOGGER.info("NFS Lock acquired");
        }
        catch (MutexCheckedException ex) {
            cleanUp(mutex);
            activeMutex = null;
            throw new MutexCheckedException("Unable to acquire target NFS mutex:\t" + mutex.getId());
        }
        
        // Confirm leadership
        try {
            LOGGER.info("Confirming leadership:\t'{}'", mutex.getId());
            confirmLeader(mutex);
            LOGGER.info("Leadership confirmed:\t'{}'", mutex.getId());
        }
        catch (MutexCheckedException ex) {
            cleanUp(mutex);
            activeMutex = null;
            throw new MutexCheckedException("Unable to confirm NFS mutex:\t" + mutex.getId());
        }
        
        // Acquire File Channel lock
        try {
            LOGGER.info("Waiting for FileChannel lock");
            FILE_CHANNEL_MUTEX.acquire(mutex);
            LOGGER.info("File Channel Lock acquired");
        }
        catch (MutexCheckedException ex) {
            cleanUp(mutex);
            activeMutex = null;
            throw new MutexCheckedException("Unable to acquire target FileChannel mutex:\t" + mutex.getId());
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
        
        // Release both locks
        LOGGER.info("Releasing active lock");
        releaseActiveLock();
    }
    
    
    /**
     * Releases active lock under 
     * 
     * @throws MutexCheckedException 
     */
    private static void releaseActiveLock() throws MutexCheckedException {
    
        // Release File Channel lock
        try {
            LOGGER.info("Releasing FileChannel mutex");
            FILE_CHANNEL_MUTEX.release(activeMutex);
        }
        catch (MutexCheckedException ex) {
            throw new MutexCheckedException("Unable to release file channel mutex");
        }
        
        // Release NFS mutex
        try {
            LOGGER.info(
                "Releasing NFS mutex:\n'{}'",
                activeMutex.toJsonDoc()
            );
            NFS_MUTEX.release(activeMutex);
        }
        catch (MutexUncheckedException ex) {
            throw new MutexCheckedException("Unable to release NFS mutex");
        }
    }
    
    
    /**
     * Fetch active {@link Mutex}
     * 
     * @return {@link Mutex}
     */
    public static Mutex fetchActive() {
        return activeMutex;
    }

    
    /**
     * Confirm active leadership or dropout
     * 
     * @param mutex
     * @throws MutexCheckedException 
     */
    public static void confirmLeader(Mutex mutex) throws MutexCheckedException {
    
        // Fetch leader
        Mutex confirmLeaderMut;
        Path confirmLeader;
        Path leader = NFS_MUTEX.getStrategy()
            .inferLeader(MutexFileType.ELECTION_FILE)
            .orElseThrow( () ->
                new MutexCheckedException("Unable to fetch active leader:\t" + mutex.getId())
        );
        
        // Verify leadership
        MutexFilesUtils.waitJitterTime();
        Mutex leaderMut = MutexFilesUtils.readMutexFromFile(leader)
            .orElseThrow( () -> 
                new MutexCheckedException("Unable to read elected leader\t" + mutex.getId())
        );
        if ( !leaderMut.getId().equals( mutex.getId() ) ) {
            throw new ActiveMutexCheckedException("Sanity checked leader does not match current\t" + mutex.getId());
        }
        
        // Perform second round
        Path confirmDir = MutexConstants.getLockDir().resolve("Confirm");
        if (!MutexFilesUtils.writeConfirmatoryBallot(mutex, confirmDir)) {
            throw new ActiveMutexCheckedException("Unable to write confirmatory ballot" + mutex.getId());
        }
        confirmLeader = MutexFilesUtils
            .fetchOldest(confirmDir)
            .orElseThrow( () -> {
               MutexFilesUtils.removeConfirmatoryBallot(mutex);
               return new ActiveMutexCheckedException("Unable to retrieve confirmatory ballots" + mutex.getId());
            });
        
        // Check confirmed leader is me
        confirmLeaderMut = MutexFilesUtils.readMutexFromFile(leader)
            .orElseThrow( () ->
                new MutexCheckedException("Unable to read confirmatory leader" + mutex.getId())
            );
        if ( !confirmLeaderMut.getId().equals(mutex.getId()) ) {
            LOGGER.warn("Unable to confirm leadership, recasting ballot" + mutex.getId());
            MutexFilesUtils.removeConfirmatoryBallot(mutex);
            NFS_MUTEX.release(mutex);
        }
    }
    
    
    /**
     * 
     * 
     * @param mutex 
     */
    public static void cleanUp(Mutex mutex) {
        
        LOGGER.debug(
            "Cleaning up lock file:\t'{}'",
            mutex.getId()
        );
        FileUtility.dropFile(mutex.getLockFile());
        LOGGER.debug(
            "Cleaning up host file:\t'{}'",
            mutex.getId()
        );
        FileUtility.dropFile(mutex.getHostFile());
        LOGGER.debug(
            "Cleaning up confirm ballot file:\t'{}'",
            mutex.getId()
        );
        FileUtility.dropFile(mutex.getConfirmBallot());
        LOGGER.debug(
            "Cleaning up election file:\t'{}'",
            mutex.getId()
        );
        FileUtility.dropFile(mutex.getElectionFile());
    }
}