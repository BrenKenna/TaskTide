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

import org.tasktide.itemstore.mutex.model.Mutex;
import org.tasktide.itemstore.mutex.model.MutexFactory;
import org.tasktide.itemstore.mutex.model.MutexFileType;
import org.tasktide.itemstore.mutex.model.MutexState;

import org.tasktide.itemstore.mutex.exceptions.MutexCheckedException;
import org.tasktide.itemstore.mutex.exceptions.MutexUncheckedException;
import org.tasktide.itemstore.mutex.utils.MutexFilesUtils;


/**
 * Package private implementation of the {@link InterProcessMutex} for
 *  shared file system
 *
 * @author Brendan Kenna
 */
class NfsMutex extends InterProcessMutex {
    
    // Attributes
    private volatile Mutex active;
    
    
    /**
     * Acquire lock for provided {@link Mutex}
     * 
     * @param mutex
     * @throws MutexCheckedException 
     */
    @Override
    public void acquire(Mutex mutex) throws MutexCheckedException {
        
        // Verify no visbily active lock
        if ( active != null ) {
            throw new MutexCheckedException("Visible lock already active");
        }
        
        // Set active once elected
        if ( MutexStrategy.ELECTION.apply(mutex, MutexFileType.ELECTION_FILE) ) {
            active = mutex;
        }
        
        // Otherwise throw error from rollback
        else {
            throw new MutexCheckedException("Self as leader rollback occured");
        }
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
        Mutex mutex;
        
        // Verify no visbily active lock
        if ( active != null ) {
            throw new MutexCheckedException("Visible lock already active");
        }
        
        // Create mutex enqueue instances election file/vote
        mutex = MutexFactory.create(targetFile);
        
        // Set active once elected
        if ( MutexStrategy.ELECTION.apply(mutex, MutexFileType.ELECTION_FILE) ) {
            active = mutex;
        }
        
        // Otherwise throw error from rollback
        else {
            throw new MutexCheckedException("Self as leader rollback occured");
        }
    }


    /**
     * Release lock on target file
     * 
     * @param targetFile
     * @throws MutexCheckedException 
     */
    @Override
    public void release(Path targetFile) throws MutexCheckedException {
        
        // Only leader can release
        if ( active == null ) {
            throw new MutexCheckedException("Not active leader");
        }
        
        // Initialize variables
        Mutex mutex = MutexFilesUtils
            .readMutexFromFile(targetFile)
            .orElseThrow(
                () -> new MutexUncheckedException("Error no mutex found for host lock")
        );
        
        // Release if valid
        this.release(mutex);
    }
    
    
    /**
     * Release provided {@link Mutex}
     * 
     * @param mutex
     * @throws MutexCheckedException 
     */
    @Override
    public void release(Mutex mutex) throws MutexCheckedException {
        
        // Only leader can release
        if ( active == null ) {
            throw new MutexCheckedException("Not active leader");
        }
        
        // Check that leader is query
        if ( active.getId().equals(mutex.getId()) ) {
            throw new MutexCheckedException("Not active leader");
        }
        
        // Release election and host file
        MutexStrategy.ELECTION.release(mutex, MutexFileType.ELECTION_FILE);
        
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
        
        // Only leader can release
        if ( active == null ) {
            throw new MutexCheckedException("Not active leader");
        }
        
        // Release election and host file
        MutexStrategy.ELECTION.release(this.active, MutexFileType.ELECTION_FILE);
        
        // Drop active mutex
        active = null;
    }
    
    
    /**
     * Get mutex state
     * 
     * @return {@link MutexState}
     */
    @Override
    public synchronized MutexState getState() {
        if ( active == null ) {
            return MutexState.OPEN;
        }
        else {
            return active.getState();
        }
    }

    
    /**
     * Set {@link Mutex} state
     * 
     * @param newState 
     */
    @Override
    public synchronized void setState(MutexState newState) {
        if ( active != null ) {
            active.setState(newState);
        }
    }

    
    @Override
    public boolean lockedByActiveHost() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public boolean lockedByActiveProcess() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}