/*
 * Copyright 2025 Brendan Kenna.
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

import org.tasktide.itemstore.FileUtility;

import org.tasktide.itemstore.mutex.model.HostLock;
import org.tasktide.itemstore.mutex.model.Mutex;
import org.tasktide.itemstore.mutex.model.MutexFactory;
import org.tasktide.itemstore.mutex.model.MutexFileType;
import org.tasktide.itemstore.mutex.model.MutexState;

import org.tasktide.itemstore.mutex.exceptions.MutexCheckedException;
import org.tasktide.itemstore.mutex.exceptions.MutexUncheckedException;
import org.tasktide.itemstore.mutex.utils.MutexFilesUtilis;


/**
 * Implements the {@link InterProcessMutex} for shared file system
 *
 * @author Brendan Kenna
 */
public class NfsMutex extends InterProcessMutex {
    
    
    //
    private volatile Mutex active;
    
    
    /**
     * Acquire lock for provided {@link Mutex}
     * 
     * @param mutex
     * @throws MutexCheckedException 
     */
    @Override
    public void acquire(Mutex mutex) throws MutexCheckedException {
        
        // Initialize variables
        int pos;
        Path activeLeader;
        
        // Set state as initialization
        mutex.setState(MutexState.INITIALIZATION);
        FileUtility.makeFile(mutex.getElectionFile());
        MutexFilesUtilis.writeMutex(mutex, MutexFileType.ELECTION_FILE);
        
        // Fetch position and active leader
        pos = this.inferPosition(mutex);
        activeLeader = this.inferLeader().orElseThrow(
            () -> new MutexUncheckedException("Error no election files found")
        );
        mutex.setState(MutexState.WAITING);
        MutexFilesUtilis.writeMutex(mutex, MutexFileType.ELECTION_FILE);
        
        // Wait until become leader
        while( pos != 0 ) {
            
            // Wait
            MutexFilesUtilis.waitJitterTime();

            // Fetch position
            pos = this.inferPosition(mutex);
        }
        
        
        // Write mutex to lock file
        //   - Sanity check these here first?
        mutex.setState(MutexState.HOST_LOCKED);
        MutexFilesUtilis.writeMutex(mutex, MutexFileType.ELECTION_FILE);
        MutexFilesUtilis.writeHostFile(mutex);
        active = mutex;
    }

    
    /**
     * Acquire lock for provided target file
     * 
     * @param targetFile
     * @throws MutexCheckedException 
     */
    @Override
    public void acquire(Path targetFile) throws MutexCheckedException {
        
        // Initialize variables
        int pos;
        Path activeLeader;
        Mutex mutex;
        
        // Verify no visbily active lock
        if ( active != null ) {
            throw new MutexCheckedException("Visible lock already active");
        }
        
        
        // Create mutex enqueue instances election file/vote
        mutex = MutexFactory.create(targetFile);
        mutex.setState(MutexState.INITIALIZATION);
        FileUtility.makeFile(targetFile);
        MutexFilesUtilis.writeMutex(mutex, MutexFileType.ELECTION_FILE);
        
        // Handle first instance?
        
        // Fetch position and active leader
        pos = this.inferPosition(mutex);
        activeLeader = this.inferLeader().orElseThrow(
            () -> new MutexUncheckedException("Error no election files found")
        );
        mutex.setState(MutexState.WAITING);
        MutexFilesUtilis.writeMutex(mutex, MutexFileType.ELECTION_FILE);
        
        // Wait until become leader
        while( pos != 0 ) {
            
            // Wait
            MutexFilesUtilis.waitJitterTime();

            // Fetch position
            pos = this.inferPosition(mutex);
        }
        
        
        // Write mutex to lock file
        //   - Sanity check these here first?
        mutex.setState(MutexState.HOST_LOCKED);
        MutexFilesUtilis.writeMutex(mutex, MutexFileType.ELECTION_FILE);
        MutexFilesUtilis.writeHostFile(mutex);
        active = mutex;
    }


    /**
     * Release lock on target file
     * 
     * @param targetFile
     * @throws MutexCheckedException 
     */
    @Override
    public void release(Path targetFile) throws MutexCheckedException {
        
        // Initialize variables
        Mutex mutex = MutexFilesUtilis
            .readMutexFromFile(targetFile)
            .orElseThrow(
                () -> new MutexUncheckedException("Error no mutex found for host lock")
        );
        
        // Remove all files
        MutexFilesUtilis.deleteFile(mutex.getElectionFile());
        MutexFilesUtilis.deleteFile(mutex.getHostFile());
        MutexFilesUtilis.deleteFile(mutex.getLockFile());
        
        // Drop active mutex
        active = null;
    }
    
    
    /**
     * Release provided {@link Mutex}
     * 
     * @param mutex
     * @throws MutexCheckedException 
     */
    @Override
    public void release(Mutex mutex) throws MutexCheckedException {
        
        // Remove all files
        MutexFilesUtilis.deleteFile(mutex.getElectionFile());
        MutexFilesUtilis.deleteFile(mutex.getHostFile());
        MutexFilesUtilis.deleteFile(mutex.getLockFile());
        
        // Drop active mutex
        active = null;
    }
    
    
    /**
     * Release active {@link Mutex} if present
     * 
     * @throws MutexCheckedException 
     */
    @Override
    public void release() throws MutexCheckedException {
        if ( active == null ) {
            throw new MutexCheckedException("");
        }
        
        // Remove all files
        MutexFilesUtilis.deleteFile(this.active.getElectionFile());
        MutexFilesUtilis.deleteFile(this.active.getHostFile());
        MutexFilesUtilis.deleteFile(this.active.getLockFile());
        
        // Drop active mutex
        active = null;
    }

    
    @Override
    public boolean lockedByActiveHost() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public boolean lockedByActiveProcess() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public MutexState getState() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void setState(MutexState newState) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}