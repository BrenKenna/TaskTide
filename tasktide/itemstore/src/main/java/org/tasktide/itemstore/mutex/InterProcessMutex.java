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

import org.tasktide.itemstore.mutex.exceptions.MutexCheckedException;
import org.tasktide.itemstore.mutex.model.MutexState;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;


/**
 * Class for coordinating locks on NSF where member
 *  nodes are unknown and dynamic. Multi-stage process
 *  first forms host queue based on epcoh time, and host label.
 *  Once locked, base Java is for file channel lock (process level),
 *  and relevant methods can be synchronized from there (thread level).
 *
 * @author Brendan Kenna
 */
public interface InterProcessMutex {
    
    
    /**
     * Acquire a lock on composed target
     * 
     * @throws MutexCheckedException 
     */
    void acquire() throws MutexCheckedException;
    
    
    /**
     * Acquire a lock on composed target,
     *  constrained by time units
     * 
     * @param time
     * @param unit
     * 
     * @return boolean
     * @throws MutexCheckedException 
     */
    boolean acquire(long time, TimeUnit unit) throws MutexCheckedException;
    
    
    /**
     * Release lock
     * 
     * @throws MutexCheckedException 
     */
    void release() throws MutexCheckedException;
    
    
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
     * @return 
     */
    Path inferLeader();
    
    
    /**
     * Get queue size
     * 
     * @return int
     */
    int queueSize();
}