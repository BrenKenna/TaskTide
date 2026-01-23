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
import java.util.concurrent.TimeUnit;

import org.tasktide.itemstore.mutex.model.MutexState;
import org.tasktide.itemstore.mutex.exceptions.MutexCheckedException;
import org.tasktide.itemstore.mutex.model.HostLock;
import org.tasktide.itemstore.mutex.model.Mutex;


/**
 * Defines semantics for electing leader mutex
 *
 * @author Brendan Kenna
 */
public interface MutexElection {
    
    
    /**
     * Acquire lock on {@link Mutex}
     * 
     * @param mutex
     * @throws MutexCheckedException 
     */
    void acquire(Mutex mutex) throws MutexCheckedException;
    
    
    /**
     * Acquire a lock on target file
     * 
     * @param targetFile
     * @throws MutexCheckedException 
     */
    void acquire(Path targetFile) throws MutexCheckedException;
    
    
    /**
     * Acquire a lock on composed target,
     *  constrained by time units
     * 
     * @param mutex
     * @param time
     * @param unit
     * 
     * @return boolean
     * @throws MutexCheckedException 
     */
    boolean acquire(Mutex mutex, long time, TimeUnit unit) throws MutexCheckedException;
    
    
    /**
     * Acquire a lock on composed target,
     *  constrained by time units
     * 
     * @param targetFile
     * @param time
     * @param unit
     * 
     * @return boolean
     * @throws MutexCheckedException 
     */
    boolean acquire(Path targetFile, long time, TimeUnit unit) throws MutexCheckedException;
    
    
    /**
     * Release lock
     * 
     * @param hostLock
     * @throws MutexCheckedException 
     */
    void release(HostLock hostLock) throws MutexCheckedException;
    
    
    /**
     * Release lock
     * 
     * @param mutex
     * @throws MutexCheckedException 
     */
    void release(Mutex mutex) throws MutexCheckedException;
    
    
    /**
     * Checks whether this host has locked
     *  
     * 
     * @return boolean
     */
    boolean lockedByActiveHost();
    
    
    /**
     * Checks whether locked by active process
     * 
     * @return boolean
     */
    boolean lockedByActiveProcess();
    
    
    /**
     * Return {@link MutexState}
     * 
     * @return {@link MutexState}
     */
    MutexState getState();
    
    
    /**
     * Set new state
     * 
     * @param newState 
     */
    void setState(MutexState newState);
    
    
    /**
     * Given file naming scheme,
     *  infer position of host in queue
     * 
     * @return int
     */
    int inferPosition();
    
    
    /**
     * Infer leader
     * 
     * @param hostLock
     * @return 
     */
    Path inferLeader(HostLock hostLock);
    
    
    /**
     * Get queue size
     * 
     * @return int
     */
    int queueSize();
}