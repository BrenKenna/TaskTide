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
package org.tasktide.itemstore.mutex.strategy;

import java.nio.file.Files;
import java.nio.file.Path;

import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.tasktide.itemstore.mutex.model.Mutex;
import org.tasktide.itemstore.mutex.model.MutexFileType;
import org.tasktide.itemstore.mutex.MutexStrategyType;

import org.tasktide.itemstore.mutex.utils.MutexConstants;
import org.tasktide.itemstore.mutex.utils.MutexFilesUtils;


/**
 *
 * @author Brendan Kenna
 */
public abstract class MutexStrategy {

    // Logger mutex strategy
    private final Logger LOGGER = LogManager.getLogger(MutexStrategy.class);
    private final MutexStrategyType strategyType;
    
    
    /**
     * Constructs
     * 
     * @param stratType 
     */
    public MutexStrategy(MutexStrategyType stratType) {
        strategyType = stratType;
    }
    
    
    /**
     * Apply provided {@link Mutex}
     * 
     * @param mutex
     * @return boolean
     */
    public abstract boolean apply(Mutex mutex);
    
    
    /**
     * Release provided {@link Mutex}
     * 
     * @param mutex
     * @return {@link Mutex}
     */
    public abstract boolean release(Mutex mutex);
    
    
    /**
     * Clean up provided {@link Mutex}
     * 
     * @param mutex
     * @return boolean
     */
    public abstract boolean cleanUp(Mutex mutex);
    

    /**
     * Infer position of {@link Mutex}
     * 
     * @param mutex
     * @return int
     */
    public int inferPosition(Mutex mutex) {
        
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
     * Returns the full file path of the leader
     * 
     * @param fileType
     * @return Optional-Path
     */
    public Optional<Path> inferLeader(MutexFileType fileType) {
        Path targetPath = fileType.fetchPathForDir();
        return MutexFilesUtils
            .fetchFiles(targetPath)
            .sorted()
            .findFirst()
        ;
    }
    
    
    /**
     * Return queue size
     * 
     * @return queue size
     */
    public int queueSize() {
        return Math.toIntExact(MutexFilesUtils.fetchFiles(MutexConstants.getHostDir()).count());
    }
    
    
    /**
     * Get {@link MutexStrategyType}
     * 
     * @return {@link MutexStrategyType}
     */
    public MutexStrategyType getStrategyType() {
        return this.strategyType;
    }
}