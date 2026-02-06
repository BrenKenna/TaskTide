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

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.Files;
import java.nio.file.Path;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.tasktide.itemstore.FileUtility;
import org.tasktide.itemstore.mutex.exceptions.MutexCheckedException;

import org.tasktide.itemstore.mutex.model.HostLock;
import org.tasktide.itemstore.mutex.model.HostLockFactory;
import org.tasktide.itemstore.mutex.model.Mutex;
import org.tasktide.itemstore.mutex.model.MutexFileType;
import org.tasktide.itemstore.mutex.model.MutexState;

import org.tasktide.itemstore.mutex.utils.MutexConstants;
import org.tasktide.itemstore.mutex.utils.MutexFilesUtils;

import org.tasktide.itemstore.mutex.exceptions.MutexUncheckedException;


/**
 * Package private enum to support strategic physical locking/and
 *  release method. Allows the broader implementation elements
 *  to handled distinctly from their specifics. In addition
 *  to cleaner/digestable approach which simplifies
 *  responsibilities of testing less repetitively.
 *
 * @author Brendan Kenna
 */
public enum MutexStrategy {

    FILE_CHANNEL {
        @Override
        public String toString() {
            return name();
        }

        @Override
        public boolean isApplierStrategy(String query) {
            return name().equalsIgnoreCase(query);
        }

        @Override
        public boolean isApplierStrategy(MutexStrategy query) {
            return this == query;
        }
        
        @Override
        public synchronized boolean apply(Mutex mutex) {
            
            // Pass if already active
            if ( mutex.getHostLock() != null ) {
                return false;
            }
        
            // Fetch file
            MutexFilesUtils.waitJitterTime();
            Path targetFile = mutex.getFileForType(MutexFileType.HOST_FILE);
            HostLock hostLock = HostLockFactory.create(targetFile);
            mutex.setHostLock(hostLock);
        
            // Fetch channel to file and lock
            try {
                
                // Acquire lock
                FileChannel fileChannel = new RandomAccessFile(targetFile.toFile(), "rw").getChannel();
                FileLock fileLock = fileChannel.lock();
                
                // Set property
                hostLock.setFileChannel(fileChannel);
                hostLock.setFileLock(fileLock);
                
                // Return success flag
                mutex.setState(MutexState.LOCKED);
                MutexFilesUtils.waitJitterTime();
                return true;
            }
            catch (IOException ex) {
                return false;
            }
        }
        
        @Override
        public synchronized boolean release(Mutex mutex) {
        
            // Any sanity check
            Path targetFile = mutex.getFileForType(MutexFileType.HOST_FILE);
            
            // Fetch path and lock
            HostLock hostLock = mutex.getHostLock();
            try {
            
                // Release lock on file channel
                if ( hostLock.getFileLock() != null && hostLock.getFileLock().isValid() ) {
                hostLock.getFileLock().release();
                }

                // Close file channel
                if ( hostLock.getFileChannel() != null && hostLock.getFileChannel().isOpen() ) {
                    hostLock.getFileChannel().close();
                }

                // Return closure state
                MutexFilesUtils.waitJitterTime();
                return true;
            }
            
            catch (IOException ex) {
                return false;
            }
        }
    },

    ELECTION {
        @Override
        public String toString() {
            return name();
        }

        @Override
        public boolean isApplierStrategy(String query) {
            return name().equalsIgnoreCase(query);
        }

        @Override
        public boolean isApplierStrategy(MutexStrategy query) {
            return this == query;
        }

        @Override
        public boolean apply(Mutex mutex) {
            
            // Initialize variables
            int pos, lastPos = -1, streak = -1, limit = 10;
            Path activeLeader;
        
            // Set state as initialization
            MutexFilesUtils.waitJitterTime();
            LOGGER.debug("Initializing mutex");
            mutex.setState(MutexState.INITIALIZATION);
            FileUtility.makeFile(mutex.getElectionFile());
            MutexFilesUtils.writeMutex(mutex, MutexFileType.ELECTION_FILE);

            // Fetch position and active leader
            LOGGER.debug("Mutex initialized, inferring queue position");
            pos = inferPosition(mutex);
            activeLeader = inferLeader( MutexFileType.ELECTION_FILE ).orElseThrow(
                () -> new MutexUncheckedException("Error no election files found")
            );
            mutex.setState(MutexState.WAITING);
            MutexFilesUtils.writeMutex(mutex, MutexFileType.ELECTION_FILE);
            LOGGER.debug("Queue position:\t'{}'\n\n'{}'", pos, mutex.toJsonDoc());

            // Wait until become leader
            boolean acquired = false, predecessorMissing;
            Path predecessor = null;
            while(!acquired) {

                // Buffer times
                // LOGGER.debug("Waiting to be leader");
                MutexFilesUtils.waitJitterTime();

                // Proceed if none before
                if(predecessor == null) {
                    
                    // Measure position
                    pos = inferPosition(mutex);
                    if(pos >= 0) {
                        if(pos == 0) {
                            acquired = true;
                            MutexFilesUtils.waitJitterTime();
                        }
                        
                        else {
                            // Exmaine position unchanging
                            if ( lastPos == pos ) {
                                LOGGER.debug("Incrementing streak now to '{}' for mutex:\t'{}'", streak, mutex.getId());
                                streak++;
                                if ( streak == limit ) {
                                    LOGGER.debug("Streak limit hit '{}' recasting ballot:\t'{}'", limit, mutex.getId());
                                    streak = -1;
                                    lastPos = -1;
                                    MutexFilesUtils.recastBallot(mutex, true);
                                }
                            }
                            lastPos = pos;
                            
                            // Examine predecessor
                            predecessor = MutexFilesUtils.findPredecessor(mutex, MutexFileType.ELECTION_FILE, pos);
                            if ( predecessor != null ) {

                                // Evaluate if predecessor is missing
                                predecessorMissing = !Files.exists(predecessor);
                                
                                if ( !predecessorMissing && pos <= 5 ) {

                                    // Evaluate TTL
                                    try {
                                        
                                        // Examine whether leader has gone stale
                                        Path leader = MutexFilesUtils.getLeader();
                                        if ( MutexFilesUtils.evaluateLeaderTimeToLive(leader) ) {
                                            LOGGER.warn(
                                                "Removing below stale leader status:\t'{}'\n\n'{}'",
                                                MutexFilesUtils.clearStaleLeader(leader),
                                                leader
                                            );
                                            predecessorMissing = true;
                                        }
                                        else {
                                            LOGGER.info("Leader changed since TTL check-in");
                                        }
                                    }
                                    catch ( MutexCheckedException ex ) {
                                        LOGGER.info("Leader removed during TTL check-in");
                                        predecessorMissing = true;
                                    }
                                }

                                // Examine predecessor state
                                if ( predecessorMissing ) {
                                    predecessor = null;
                                }
                            }
                        }
                    }
                }
                
                else {
                    acquired = !Files.exists(predecessor);
                }
            }

            // Write mutex to lock file
            LOGGER.debug("Active leader with Id:\t'{}'", mutex.getId());
            mutex.setState(MutexState.HOST_LOCKED);
            LOGGER.debug("State set, writing mutex for:\t'{}'", mutex.getId());
            MutexFilesUtils.writeMutex(mutex, MutexFileType.ELECTION_FILE);
            LOGGER.debug("Mutex written for:\t'{}'", mutex.getId());
            if ( MutexFilesUtils.writeHostFile(mutex) ) {
                LOGGER.debug("Host lock file created for:\t'{}'", mutex.getId());
                MutexFilesUtils.waitJitterTime();
                return true;
            }
            
            // Otherwise rollback
            else {
                LOGGER.debug("Unable to write host lock file:\t'{}'", mutex.getId());
                MutexFilesUtils.deleteFile(mutex.getElectionFile());
                MutexFilesUtils.deleteFile(mutex.getHostFile());
                return false;
            }
        }

        @Override
        public boolean release(Mutex mutex) {
            return (
                MutexFilesUtils.deleteFile(mutex.getHostFile()) &&
                MutexFilesUtils.deleteFile(mutex.getConfirmBallot()) &&
                MutexFilesUtils.deleteFile(mutex.getElectionFile())
            );
        }
    };
    
    
    /**
     * Abstract method to allow implementations to handle how
     *  one of the files associated with {@link Mutex} are
     *  locked. Acting as bridge between structural and functional
     *  logic of {@link Mutex}-{@link MutexElection}
     * 
     * @param mutex
     * 
     * @return boolean
     */
    public abstract boolean apply(Mutex mutex);

    
    /**
     * Abstract method to allow implementations to handle how
     *  one of the files associated with {@link Mutex} are
     *  released. Acting as bridge between structural and functional
     *  logic of {@link Mutex}-{@link MutexElection}
     * 
     * @param mutex
     * 
     * @return boolean
     */
    public abstract boolean release(Mutex mutex);
    

    /**
     * Abstract method check if query is enum value
     *
     * @param query
     * @return boolean
     */
    public abstract boolean isApplierStrategy(String query);


    /**
     * Abstract method check if query is enum value
     *
     * @param query
     * @return boolean
     */
    public abstract boolean isApplierStrategy(MutexStrategy query);

    
    /**
     * Returns the full file path of the leader
     * 
     * @param fileType
     * @return Optional-Path
     */
    public static Optional<Path> inferLeader(MutexFileType fileType) {
        Path targetPath = fileType.fetchPathForDir();
        return MutexFilesUtils
            .fetchFiles(targetPath)
            .sorted()
            .findFirst()
        ;
    }
    
    
    /**
     * Infer position of {@link Mutex} in queue
     * 
     * @param mutex
     * @return int
     */
    public static int inferPosition(Mutex mutex) {
        
        // Search params
        int counter = 0;
        boolean found = false;
        
        
        // Recast a new ballot if removed
        if ( !Files.exists( mutex.getElectionFile() ) ) {
            MutexFilesUtils.recastBallot(mutex, false);
        }
        
        
        // Fetch all election files
        List<Path> paths = MutexFilesUtils.fetchFiles(MutexConstants.getElectionDir())
            .sorted()
            .toList();
        
        // Search until found
        while( counter < paths.size() && !found ) {
            Path active = paths.get(counter);
            if ( active.equals( mutex.getElectionFile() ) ) {
                found = true;
            }
            else {
                counter++;
            }
        }
        
        // Return value
        return counter;
    }
    
    
    /**
     * Return queue size
     * 
     * @return queue size
     */
    public static int queueSize() {
        return Math.toIntExact(MutexFilesUtils.fetchFiles(MutexConstants.getHostDir()).count());
    }
    

    /**
     * Fetch the index for mapped query string
     *
     * @param query
     * @return >0/-1
     */
    public static int indexOf(String query) {
        for ( MutexStrategy elm : values() ) {
            if ( elm.isApplierStrategy(query) ) {
                return elm.ordinal();
            }
        }
        return -1;
    }


    /**
     * Check if query maps to enum value
     *
     * @param query
     * @return boolean
     */
    public static boolean hasQuery(String query) {
        if ( query == null ) {
            return false;
        }

        for ( MutexStrategy elm : values() ) {
            if ( elm.isApplierStrategy(query) ) {
                return true;
            }
        }
        return false;
    }
    

    /**
     * Map query to enum value
     *
     * @param query
     * @return MutexStrategy
     */
    public static MutexStrategy get(String query) {
        int ind = indexOf(query);
        if ( ind >= 0 ) {
            return values()[ind];
        }
        return null;
    }


    /**
     * Represent enum as string
     *
     * @return String
     */
    public static String valuesString() {
        return Arrays.stream(values())
            .map( elm -> elm.name() )
        .collect(Collectors.joining(","));
    }
    
    // Logger mutex strategy
    private static final Logger LOGGER = LogManager.getLogger(MutexStrategy.class);
}