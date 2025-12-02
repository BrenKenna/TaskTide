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
import java.time.Duration;
import java.util.Random;


/**
 * Holds constants for building {@link Mutex}
 *
 * @author Brendan Kenna
 */
public class MutexConstants {
    
    // Attributes configured once
    private static volatile boolean durationsInitialized = false;
    private static volatile boolean pathsInitialized = false;
    
    // Duration properties
    private static volatile Duration
        retryInterval, startJitter, endJitter, staleFileThreshold;
    
    // Path properties
    private static volatile Path
        lockDir, lockFile, hostDir, hostFile;
    
    // For random long creation 
    private final static Random RAND = new Random();
    
    
    /**
     * Initialize duration properties with hard coded defaults
     * 
     */
    public static synchronized void initializeDurations() {
        if ( !durationsInitialized ) {
           retryInterval = Duration.ofMillis( RAND.nextLong(10L, 300L) );
           startJitter = Duration.ofMillis( RAND.nextLong(100L, 500L) );
           endJitter = Duration.ofMillis( RAND.nextLong(100L, 500L) );
           staleFileThreshold = Duration.ofSeconds(10L);
           durationsInitialized = true;
        }
    }
    
    
    /**
     * Initialize duration properties with provided values
     * 
     * @param retry
     * @param start
     * @param end
     * @param stale 
     */
    public static synchronized void initializeDurations(long retry, long start, long end, long stale) {
        if ( !durationsInitialized ) {
           retryInterval = Duration.ofMillis(retry);
           startJitter = Duration.ofMillis(start);
           endJitter = Duration.ofMillis(end);
           staleFileThreshold = Duration.ofDays(stale);
           durationsInitialized = true;
        }
    }

    
    /**
     * Get retry interval
     * 
     * @return {@link Duration}
     */
    public static Duration getRetryInterval() {
        if (durationsInitialized ) {
            return retryInterval;
        }
        else {
            throw new MutexRuntimeException("Durations must be initialized");
        }
    }

    
    /**
     * Get start jitter
     * 
     * @return {@link Duration}
     */
    public static Duration getStartJitter() {
        if (durationsInitialized) {
            return startJitter;
        }
        else {
            throw new MutexRuntimeException("Durations must be initialized");
        }
    }

    
    /**
     * Get end jitter
     * 
     * @return {@link Duration}
     */
    public static Duration getEndJitter() {
        if (durationsInitialized) {
            return endJitter;
        }
        else {
            throw new MutexRuntimeException("Durations must be initialized");
        }
    }

    
    /**
     * Get stale file threshold
     * 
     * @return {@link Duration}
     */
    public static Duration getStaleFileThreshold() {
        if (durationsInitialized) {
            return staleFileThreshold;
        }
        else {
            throw new MutexRuntimeException("Durations must be initialized");
        }
    }
    
    
    /**
     * Initialize paths for {@link InterProcessMutex}
     * 
     */
    public static synchronized void initializePaths() {
        if ( !pathsInitialized ) {
            
            // Set root directory structure
            lockDir = Path.of("./ItemStore-Mutex");
            lockFile = lockDir.resolve("lock-file.lock");
            
            // Fetch node, and node-proc identifiers
            String nodeProcId = MutexLabellingUtils.getNodeProcId();
            // String nodeId = MutexLabellingUtils.getNodeId();
            
            // Fetch queue directory
            // hostDir = lockDir.resolve("queue").resolve(nodeId);
            hostDir = lockDir.resolve("queue");
            hostFile = hostDir.resolve(nodeProcId);
            
            // Update state
            pathsInitialized = true;
        }
    }
    
    
    /**
     * Initialize paths for {@link InterProcessMutex}
     * 
     * @param lockingDir
     * @param lockingFile
     * @param hostingDir
     */
    public static synchronized void initializePaths(Path lockingDir, Path lockingFile, Path hostingDir) {
        if ( !pathsInitialized ) {
        
            // Set root directory structure
            lockDir = lockingDir;
            lockFile = lockingFile;
            
            // Fetch queue directory
            hostDir = hostingDir;
            
            // Update state
            pathsInitialized = true;
        }
    }

    
    /**
     * Get directory holding lock file
     * 
     * @return {@link Path}
     */
    public static Path getLockDir() {
        if (pathsInitialized) {
            return lockDir;
        }
        else {
            throw new MutexRuntimeException("Paths must be initialized");
        }
    }

    
    /**
     * Get central lock file
     * 
     * @return {@link Path}
     */
    public static Path getLockFile() {
        if (pathsInitialized) {
            return lockFile;
        }
        else {
            throw new MutexRuntimeException("Paths must be initialized");
        }
    }

    
    /**
     * Get host directory under locking directory
     * 
     * @return {@link Path}
     */
    public static Path getHostDir() {
        if (pathsInitialized) {
            return hostDir;
        }
        else {
            throw new MutexRuntimeException("Paths must be initialized");
        }
    }

    
    /**
     * Get current Time.Node_Instance.lock file
     * 
     * @return {@link Path}
     */
    public static Path getHostFile() {
        if (pathsInitialized) {
            return hostFile;
        }
        else {
            throw new MutexRuntimeException("Paths must be initialized");
        }
    }
    
    
    /**
     * Checks whether duration and path
     *  properties have been configured
     * 
     * @return boolean
     */
    public static boolean isConfigured() {
        return durationsInitialized && pathsInitialized;
    }
    
    
    /**
     * Returns whether paths have been configured
     * 
     * @return boolean
     */
    public static boolean pathsConfigured() {
        return pathsInitialized;
    }
    
    
    /**
     * Checks whether durations have been configured
     * 
     * @return boolean
     */
    public static boolean durationsConfigured() {
        return durationsInitialized;
    }
}