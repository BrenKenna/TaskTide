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
package org.tasktide.mutex.orchestrator;

import java.nio.channels.FileChannel;
import java.nio.file.Path;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.tasktide.mutex.utils.FileUtility;
import org.tasktide.mutex.actor.FileChannelActor;
import org.tasktide.mutex.actor.NfsMutexActor;
import org.tasktide.mutex.exceptions.ActiveMutexCheckedException;

import org.tasktide.mutex.model.Mutex;
import org.tasktide.mutex.model.MutexFactory;

import org.tasktide.mutex.utils.MutexFilesUtils;
import org.tasktide.mutex.utils.MutexConstants;

import org.tasktide.mutex.exceptions.MutexCheckedException;
import org.tasktide.mutex.exceptions.MutexUncheckedException;
import org.tasktide.mutex.model.MutexFileType;
import org.tasktide.mutex.actor.MutexActor;


/**
 * Implementing {@link MutexActor} lock by composing the {@link NfsMutexActor},
 *  and {@link FileChannelActor} which collectively offer a {@link Mutex} which
 *  can be acquired and released.<br>
 Currently the algorithm only runs as a waiter/holds the executing thread.
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
        MutexActor NFS_MUTEX, FILE_CHANNEL_MUTEX;
    
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
            NFS_MUTEX = new NfsMutexActor();
            FILE_CHANNEL_MUTEX = new FileChannelActor();
            isConfigured = true;
        }
    }
    
    
    /**
     * Configures {@link MutexOrchestrator} with provided
     *  {@link MutexActor}
     * 
     * @param nfsMutex
     * @param fileChannelMutex 
     */
    public static synchronized void configureForTestCases(MutexActor nfsMutex, MutexActor fileChannelMutex) {
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
     * Acquire central {@link MutexActor} through {@link NfsMutexActor} lock
     *  then requesting {@link FileChannel} lock
     * 
     * @return OrchestratorResult
     * @throws MutexCheckedException
     */
    public static OrchestratorResult acquireLock() throws MutexCheckedException {
        
        // Handle orchestrator configuration
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
        return performLock(mutex);
    }
    
    
    /**
     * Try acquire lock until successful
     * @throws java.lang.Exception
     */
    public static void tryAcquireUntilSuccess() throws Exception {
        
        // Initialize variables
        int counter = 0;
        long processId = ProcessHandle.current().pid();
        boolean done = false;
        OrchestratorResult result;
        
        while ( !done ) {
            
            // Try acquire process
            result = acquireLock();
            
            // Flag sucess
            if ( result.isSuccess() ) {
                done = true;
                LOGGER.info("Process-{} Completed with counter value:\t'{}'", processId, counter);
            }
            
            // Otherwise handle failure
            else {
                LOGGER.warn(
                    "Process-{} Attempt {} failed: {} | Exception: {}",
                    processId,
                    counter,
                    result.getStatus(),
                    result.getException() != null ? result.getException().getMessage() : "none"
                );
                counter++;
                MutexFilesUtils.waitJitterTime();
            }
        }
        
        // Log completion
        LOGGER.info(
            "Process-{} Loop terminating with done = '{}', and counter = '{}'",
            processId,
            done,
            counter
        );
    }
    
    
    /**
     * Performs 
     * 
     * @param mutex
     * @throws MutexCheckedException 
     */
    private static OrchestratorResult performLock(Mutex mutex) {

        // Acquire NFS lock
        try {
            LOGGER.info("Waiting for NFS lock for:\t'{}'", mutex.getId());
            NFS_MUTEX.acquire(mutex);
            activeMutex = mutex;
            LOGGER.info("NFS Lock acquired for:\t'{}'", mutex.getId());
        }
        catch (MutexCheckedException ex) {
            cleanUp(mutex);
            activeMutex = null;
            return new OrchestratorResult(
                false,
                OrchestratorStatus.NFS_LOCK_FAILED,
                OrchestratorAction.LOCK,
                ex
            );
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
            return new OrchestratorResult(
                false,
                OrchestratorStatus.LEADERSHIP_CONFIRMATION_FAILED,
                OrchestratorAction.LOCK,
                ex
            );
        }
        
        // Acquire File Channel lock
        try {
            LOGGER.info("Waiting for FileChannel lock for:\t'{}'", mutex.getId());
            FILE_CHANNEL_MUTEX.acquire(mutex);
            LOGGER.info("File Channel Lock acquired for:\t'{}'", mutex.getId());
        }
        catch (MutexCheckedException ex) {
            cleanUp(mutex);
            activeMutex = null;
            return new OrchestratorResult(
                false,
                OrchestratorStatus.FILE_CHANNEL_LOCK_FAILED,
                OrchestratorAction.LOCK,
                ex
            );
        }
        
        // Successful result
        LOGGER.info("Lock successful for:\t'{}'", mutex.getId());
        return new OrchestratorResult(
            true,
            OrchestratorStatus.SUCCESS,
            OrchestratorAction.LOCK
        );
    }
    
    
    /**
     * Releases {@link FileChannelActor} before the central {@link MutexActor}
     *  on {@link NfsMutexActor} using the {@link MutexFilesUtils} jitter time
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
        
        // Check if active lock exists
        if ( activeMutex != null ) {
            
            // Release both locks
            LOGGER.info("Releasing active lock");
            releaseActiveLock();
        }
        
        // Otherwise pass
        else {
            LOGGER.info("No active mutex detected");
        }
    }
    
    
    /**
     * Releases active lock under 
     * 
     * @throws MutexCheckedException 
     */
    private static void releaseActiveLock() throws MutexCheckedException {
        
        // Initialize vars
        Mutex local = activeMutex;
        String msg;
        MutexCheckedException
            fileChannelError = null,
            nfsError = null
        ;
    
        // Release File Channel lock
        try {
            LOGGER.info("Releasing FileChannel mutex");
            FILE_CHANNEL_MUTEX.release(local);
        }
        catch (Exception ex) {
            msg = "Unable to release file channel mutex\n\n" + ex;
            fileChannelError = new MutexCheckedException(msg);
        }
        
        // Release NFS mutex
        try {
            LOGGER.info(
                "Releasing NFS mutex:\n'{}'",
                activeMutex.toJsonDoc()
            );
            NFS_MUTEX.release(local);
        }
        catch (Exception ex) {
            msg = "Unable to release NFS mutex\n\n" + ex;
            nfsError = new MutexCheckedException(msg);
        }
        
        // Examine error after cleanup
        activeMutex = null;
        if ( fileChannelError != null && nfsError != null ) {
            throw new MutexCheckedException(
                "Failed to release both FileChannel and NFS mutex for ID: " + local.getId() +
                "\nFileChannel error: " + fileChannelError.getMessage() +
                "\nNFS error: " + nfsError.getMessage()
            );
        }
        if ( fileChannelError != null ) {
            throw fileChannelError;
        }
        if ( nfsError != null ) {
            throw nfsError;
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
                new ActiveMutexCheckedException("Unable to fetch active leader:\t" + mutex.getId())
        );
        
        // Verify leadership
        MutexFilesUtils.waitJitterTime();
        Mutex leaderMut = MutexFilesUtils.readMutexFromFile(leader)
            .orElseThrow( () -> 
                new ActiveMutexCheckedException("Unable to read elected leader:\t" + mutex.getId())
        );
        if ( !leaderMut.getId().equals( mutex.getId() ) ) {
            throw new ActiveMutexCheckedException("Sanity checked leader does not match current:\t" + mutex.getId());
        }
        
        // Perform second round
        Path confirmDir = MutexConstants.getLockDir().resolve("Confirm");
        if (!MutexFilesUtils.writeConfirmatoryBallot(mutex, confirmDir)) {
            throw new ActiveMutexCheckedException("Unable to write confirmatory ballot:\t" + mutex.getId());
        }
        confirmLeader = MutexFilesUtils
            .fetchOldest(confirmDir)
            .orElseThrow( () -> {
               MutexFilesUtils.removeConfirmatoryBallot(mutex);
               return new ActiveMutexCheckedException("Unable to retrieve confirmatory ballots:\t" + mutex.getId());
            });
        
        // Check confirmed leader is me
        confirmLeaderMut = MutexFilesUtils.readMutexFromFile(leader)
            .orElseThrow( () ->
                new ActiveMutexCheckedException("Unable to read confirmatory leader:\t" + mutex.getId())
            );
        if ( !confirmLeaderMut.getId().equals(mutex.getId()) ) {
            LOGGER.warn("Unable to confirm leadership, recasting ballot:\t'{}'" + mutex.getId());
            MutexFilesUtils.removeConfirmatoryBallot(mutex);
            cleanUp(mutex);
            throw new ActiveMutexCheckedException("Leadership confirmation failed:\t" + mutex.getId());
        }
        else {
            LOGGER.info("Leadership confirmed:\t'{}'", mutex.getId());
        }
    }
    
    
    /**
     * Clear files
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