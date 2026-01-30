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

import org.tasktide.itemstore.mutex.exceptions.MutexCheckedException;
import org.tasktide.itemstore.mutex.model.Mutex;
import org.tasktide.itemstore.mutex.model.MutexFileType;
import org.tasktide.itemstore.mutex.model.MutexState;
import org.tasktide.itemstore.mutex.utils.MutexFilesUtils;


/**
 * Package private class for managing the acquisitions and
 *  releasing of {@link FileChannel}
 *
 * @author Brendan Kenna
 */
class FileChannelMutex extends IntraProcessMutex {
    
    // Attributes
    private Mutex active;
    
    
    /**
     * Ensure no active lock
     * 
     * @throws MutexCheckedException 
     */
    private synchronized void ensureNoActiveLock() throws MutexCheckedException {
        if (active != null) {
            throw new MutexCheckedException("Lock already active");
        }
    }
    
    
    /**
     * Wait for lock to be acquired
     * 
     * @param mutex 
     */
    private void waitForLock(Mutex mutex) {
        
        // Wait until acquired
        boolean locked = false;
        while (!locked) {
            MutexFilesUtils.waitJitterTime();
            locked = MutexStrategy.FILE_CHANNEL.apply(mutex, MutexFileType.HOST_FILE);
        }
    }
    
    
    /**
     * Set active {@link Mutex}
     * 
     * @param mutex
     * @throws MutexCheckedException 
     */
    private synchronized void setLock(Mutex mutex) throws MutexCheckedException {
        
        // Check if a lock is already active
        if ( active != null ) {
            throw new MutexCheckedException("Lock already active");
        }
        
        // Try lock
        try {
            active = mutex;
            mutex.setState(MutexState.LOCKED);
        }
        
        // Otherwise rollback
        catch (RuntimeException | Error ex) {
            MutexStrategy.FILE_CHANNEL.release(mutex, MutexFileType.HOST_FILE);
            throw ex;
        }
    }
    
    
    /**
     * Acquire a lock on composed target
     * 
     * @param targetFile
     * @throws MutexCheckedException 
     */
    @Override
    public void acquire(Path targetFile) throws MutexCheckedException {
    
        // Fail early if already locked
        this.ensureNoActiveLock();
        
        // Initialize mutex parameters
        Mutex mutex = MutexFilesUtils.readMutexFromFile(targetFile).get();

        // Wait for lock
        this.waitForLock(mutex);
        this.setLock(mutex);
    }
    
    
    /**
     * Acquire lock on provided {@link Mutex}
     * 
     * @param mutex
     * @throws MutexCheckedException 
     */
    @Override
    public void acquire(Mutex mutex) throws MutexCheckedException {
        
        // Fail early if already locked
        this.ensureNoActiveLock();
        
        // Wait until acquired
        this.waitForLock(mutex);
        
        // Set active
        this.setLock(mutex);
    }

    
    /**
     * Release lock
     * 
     */
    @Override
    public synchronized void release(Mutex mutex) throws MutexCheckedException {
        if ( mutex.getHostLock() == null ) {
            throw new MutexCheckedException("No host lock to release on mutex");
        }
        
        if ( !active.getId().equals(mutex.getId()) ) {
            throw new MutexCheckedException("Mutex does not equal active");
        }
        
        MutexStrategy.FILE_CHANNEL.release(mutex, MutexFileType.HOST_FILE);
        active = null;
    }
    
    
    /**
     * Release lock on target file provided
     * 
     * @param targetFile
     * @throws MutexCheckedException 
     */
    @Override
    public synchronized void release(Path targetFile) throws MutexCheckedException {
        if ( active == null ) {
            throw new MutexCheckedException("No host lock to release on mutex");
        }
        
        if ( !active.getHostFile().equals(targetFile) ) {
            throw new MutexCheckedException("Mutex does not equal active");
        }
        
        MutexStrategy.FILE_CHANNEL.release(active, MutexFileType.HOST_FILE);
        active = null;
    }
    
    
    /**
     * Release lock
     * 
     * @throws MutexCheckedException
     */
    @Override
    public synchronized void release() throws MutexCheckedException {
        if ( active != null ) {
            MutexStrategy.FILE_CHANNEL.release(active, MutexFileType.HOST_FILE);
            active = null;
        }
        
        else {
            throw new MutexCheckedException("No active lock to release");
        }
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