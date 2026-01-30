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
import java.nio.file.Path;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.tasktide.itemstore.FileUtility;
import org.tasktide.itemstore.mutex.exceptions.MutexUncheckedException;

import org.tasktide.itemstore.mutex.model.HostLock;
import org.tasktide.itemstore.mutex.model.HostLockFactory;

import org.tasktide.itemstore.mutex.model.Mutex;
import org.tasktide.itemstore.mutex.model.MutexFileType;
import org.tasktide.itemstore.mutex.model.MutexState;
import org.tasktide.itemstore.mutex.utils.MutexConstants;
import org.tasktide.itemstore.mutex.utils.MutexFilesUtils;


/**
 * Package private num to support strategic phsyical locking/and
 *  release method. Allows the broader implemention elements
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
        public synchronized boolean apply(Mutex mutex, MutexFileType fileType) {
            
            // Pass if already active
            if ( mutex.getHostLock() != null ) {
                return false;
            }
        
            // Fetch file
            Path targetFile = mutex.getFileForType(fileType);
            HostLock hostLock = HostLockFactory.create(mutex.getHostFile());
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
                return true;
            }
            catch (IOException ex) {
                return false;
            }
        }
        
        @Override
        public synchronized boolean release(Mutex mutex, MutexFileType fileType) {
        
            // Any sanity check
            Path targetFile = mutex.getFileForType(fileType);
            
            
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
        public boolean apply(Mutex mutex, MutexFileType fileType) {
            
            // Initialize variables
            int pos;
            Path activeLeader;
        
            // Set state as initialization
            mutex.setState(MutexState.INITIALIZATION);
            FileUtility.makeFile(mutex.getElectionFile());
            MutexFilesUtils.writeMutex(mutex, MutexFileType.ELECTION_FILE);

            // Fetch position and active leader
            pos = inferPosition(mutex);
            activeLeader = inferLeader().orElseThrow(
                () -> new MutexUncheckedException("Error no election files found")
            );
            mutex.setState(MutexState.WAITING);
            MutexFilesUtils.writeMutex(mutex, MutexFileType.ELECTION_FILE);

            // Wait until become leader
            while( pos != 0 ) {

                // Wait
                MutexFilesUtils.waitJitterTime();

                // Fetch position
                pos = inferPosition(mutex);
            }


            // Write mutex to lock file
            //   - Sanity check these here first?
            mutex.setState(MutexState.HOST_LOCKED);
            MutexFilesUtils.writeMutex(mutex, MutexFileType.ELECTION_FILE);
            if (MutexFilesUtils.writeHostFile(mutex)) {
                MutexFilesUtils.writeHostFile(mutex);
                return true;
            }
            
            // Otherwise rollback
            else {
                MutexFilesUtils.deleteFile(mutex.getElectionFile());
                MutexFilesUtils.deleteFile(mutex.getHostFile());
                return false;
            }
        }

        @Override
        public boolean release(Mutex mutex, MutexFileType fileType) {
            return (
                MutexFilesUtils.deleteFile(mutex.getElectionFile()) &&
                MutexFilesUtils.deleteFile(mutex.getHostFile())
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
     * @param fileType
     * @return boolean
     */
    public abstract boolean apply(Mutex mutex, MutexFileType fileType);

    
    /**
     * Abstract method to allow implementations to handle how
     *  one of the files associated with {@link Mutex} are
     *  released. Acting as bridge between structural and functional
     *  logic of {@link Mutex}-{@link MutexElection}
     * 
     * @param mutex
     * @param fileType
     * @return boolean
     */
    public abstract boolean release(Mutex mutex, MutexFileType fileType);
    

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
     * @return 
     */
    public static Optional<Path> inferLeader() {
        return MutexFilesUtils
            .fetchFiles(MutexConstants.getHostDir())
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
        
        // Fetch all election files
        List<Path> paths = MutexFilesUtils.fetchFiles(MutexConstants.getHostDir())
            .sorted()
            .toList();
        
        // Search until found
        while( counter < paths.size() && !found ) {
            Path active = paths.get(counter);
            if ( mutex.getElectionFile() == active ) {
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
}