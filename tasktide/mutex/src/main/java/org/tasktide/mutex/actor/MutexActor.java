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
package org.tasktide.mutex.actor;

import java.nio.file.Path;

import org.tasktide.mutex.model.MutexState;
import org.tasktide.mutex.exceptions.MutexCheckedException;
import org.tasktide.mutex.model.Mutex;
import org.tasktide.mutex.strategy.MutexStrategy;


/**
 * Defines the package private semantics for electing leader mutex
 *
 * @author Brendan Kenna
 */
public interface MutexActor {
    
    
    /**
     * Fetch the composed {@link MutexStrategy}
     * 
     * @return {@link MutexStrategy}
     */
    public MutexStrategy getStrategy();
    
    
    /**
     * Fetch active {@link Mutex} field value
     * 
     * @return {@link Mutex}
     */
    public Mutex fetchActiveFieldValue();
    
    
    /**
     * Acquire lock on {@link Mutex}
     * 
     * @param mutex
     * @return boolean
     * 
     * @throws MutexCheckedException 
     */
    public boolean acquire(Mutex mutex) throws MutexCheckedException;
    
    
    /**
     * Acquire a lock on target file
     * 
     * @param targetFile
     * @return boolean
     * 
     * @throws MutexCheckedException 
     */
    public boolean acquire(Path targetFile) throws MutexCheckedException;

    
    /**
     * 
     * @param mutex
     * @throws MutexCheckedException 
     */
    public void waitForLock(Mutex mutex) throws MutexCheckedException;
    
    
    /**
     * Release lock
     * 
     * @param mutex
     * @return boolean
     * 
     * @throws MutexCheckedException 
     */
    public boolean release(Mutex mutex) throws MutexCheckedException;
    
    
    /**
     * Release lock
     * 
     * @param targetFile
     * @return boolean
     * 
     * @throws MutexCheckedException 
     */
    public boolean release(Path targetFile) throws MutexCheckedException;
    
    
    
    /**
     * Release active lock
     * 
     * @return boolean
     * 
     * @throws MutexCheckedException 
     */
    public boolean release() throws MutexCheckedException;
    
    
    /**
     * Checks whether this host has locked
     *  
     * 
     * @return boolean
     */
    public boolean lockedByActiveHost();
    
    
    /**
     * Checks whether locked by active process
     * 
     * @return boolean
     */
    public boolean lockedByActiveProcess();
    
    
    /**
     * Return {@link MutexState}
     * 
     * @return {@link MutexState}
     */
    public MutexState getState();
    
    
    /**
     * Set new state
     * 
     * @param newState 
     */
    public void setState(MutexState newState);
}