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
import org.tasktide.itemstore.mutex.model.MutexState;

import org.tasktide.itemstore.mutex.utils.MutexFilesUtils;

import org.tasktide.itemstore.mutex.strategy.ElectionStrategy;
import org.tasktide.itemstore.mutex.strategy.MutexStrategy;

import org.tasktide.itemstore.mutex.exceptions.MutexCheckedException;
import org.tasktide.itemstore.mutex.exceptions.MutexUncheckedException;


/**
 * Package private implementation of the {@link InterProcessMutex} for
 *  shared file system
 *
 * @author Brendan Kenna
 */
class NfsMutex extends InterProcessMutex {
    
    // Attributes
    private final MutexStrategy ELECTION_STRAT = new ElectionStrategy();
    private volatile Mutex active;
    
    
    /**
     * Get {@link MutexStrategy}
     * 
     * @return {@link ElectionStrategy}
     */
    @Override
    public MutexStrategy getStrategy() {
        return this.ELECTION_STRAT;
    }
    
    
    /**
     * Waits for provided {@link Mutex} to become the leader
     * 
     * @param mutex
     * @throws MutexCheckedException 
     */
    @Override
    public void waitForLock(Mutex mutex) throws MutexCheckedException {
        
        // Wait until no active leader
        boolean hasActive = true;
        while ( hasActive ) {
            MutexFilesUtils.waitJitterTime();
            hasActive = active != null;
        }
        
        // Set active once elected
        if ( ELECTION_STRAT.apply(mutex) ) {
            active = mutex;
        }
        
        // Otherwise throw error from rollback
        else {
            throw new MutexCheckedException(
                "Self as NFS-Leader rollback occured with mutex:\t" + 
                mutex.getId()
            );
        }
    }
    
    
    /**
     * Acquire lock for provided {@link Mutex}
     * 
     * @param mutex
     * @returns boolean
     * @throws MutexCheckedException 
     */
    @Override
    public synchronized boolean acquire(Mutex mutex) throws MutexCheckedException {
        
        // Wait for lock
        this.waitForLock(mutex);
        
        // Verify state
        return active.getState().isLockState(MutexState.HOST_LOCKED);
    }

    
    /**
     * Acquire lock for provided target file
     * 
     * @param targetFile
     * @throws MutexCheckedException 
     */
    @Override
    public synchronized boolean acquire(Path targetFile) throws MutexCheckedException {
        
        // Create mutex enqueue instances election file/vote
        Mutex mutex;
        mutex = MutexFactory.create(targetFile);
        
        // Wait for lock
        this.waitForLock(mutex);
        
        // Verify state
        return active.getState().isLockState(MutexState.HOST_LOCKED);
    }


    /**
     * Release lock on target file
     * 
     * @param targetFile
     * @returns boolean
     * @throws MutexCheckedException 
     */
    @Override
    public synchronized boolean release(Path targetFile) throws MutexCheckedException {
        
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
        return this.release(mutex);
    }
    
    
    /**
     * Release provided {@link Mutex}
     * 
     * @param mutex
     * @return boolean
     * @throws MutexCheckedException 
     */
    @Override
    public boolean release(Mutex mutex) throws MutexCheckedException {
        
        // Only leader can release
        if ( active == null ) {
            throw new MutexCheckedException("No active leader");
        }
        
        // Check that leader is query
        if ( !active.getId().equals(mutex.getId()) ) {
            throw new MutexCheckedException("Not active leader");
        }
        
        // Release election and host file
        boolean state;
        state = ELECTION_STRAT.release(mutex);
        active = null;
        
        // Drop active mutex
        return state;
    }
    
    
    /**
     * Release active {@link Mutex} if present
     * 
     * @returns boolean
     * @throws MutexCheckedException 
     */
    @Override
    public synchronized boolean release() throws MutexCheckedException {
        
        // Only leader can release
        if ( active == null ) {
            throw new MutexCheckedException("Not active leader");
        }
        
        // Release election and host file
        boolean state;
        state = ELECTION_STRAT.release(active);
        active = null;
        
        return state;
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

    
    /**
     * Provides the value for the active {@link Mutex}
     * 
     * @return {@link Mutex}
     */
    @Override
    public Mutex fetchActiveFieldValue() {
        return this.active;
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