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
package org.tasktide.mutex.model;

import java.nio.file.Path;
import java.util.UUID;

import org.tasktide.mutex.utils.MutexConstants;
import org.tasktide.mutex.utils.MutexLabellingUtils;

/**
 * Class for creating {@link Mutex}, assuming {@link MutexConstants}
 *  have been configured
 *
 * @author Brendan Kenna
 */
public class MutexFactory {
    
    
    /**
     * Build with host file path override
     * 
     * @param hostFile
     * 
     * @return {@link Mutex}
     */
    public static Mutex create(Path hostFile) {
        
        // Init vars
        String id;
        Path electionFile;
        Mutex output;
        
        // Fetch variables
        id = UUID.randomUUID().toString();
        electionFile = MutexConstants.getElectionDir().resolve(
                System.currentTimeMillis() + 
                "." +
                MutexLabellingUtils.getNodeProcId() +
                "_" +
                id +
                ".lock"
       );      
        
        // Create mutex
        output = new Mutex(
            id,
            System.currentTimeMillis(),
            MutexConstants.getLockDir(),
            MutexConstants.getLockFile(),
            hostFile,
            electionFile,
            MutexConstants.getRetryInterval(),
            MutexConstants.getStaleFileThreshold(),
            MutexConstants.getStartJitter(),
            MutexConstants.getEndJitter(),
            MutexState.INITIALIZATION
        );
        
        // Return
        return output;
    }
    
    
    /**
     * Build with new lock entry generated internally
     * 
     * @return {@link Mutex}
     */
    public static Mutex create() {
        
        // Init vars
        String id, mutexFile;
        Path hostFile, electionFile;
        Mutex output;
        
        // Fetch variables
        // System.out.println("I run");
        id = UUID.randomUUID().toString();
        mutexFile = MutexLabellingUtils.getMutexFileName();
        hostFile = MutexConstants.getHostDir().resolve(mutexFile);
        electionFile = MutexConstants.getElectionDir().resolve(
                System.currentTimeMillis() + 
                "." +
                MutexLabellingUtils.getNodeProcId() +
                "_" +
                id +
                ".lock"
       ); 
        
        // Create mutex
        output = new Mutex(
            id,
            System.currentTimeMillis(),
            MutexConstants.getLockDir(),
            MutexConstants.getLockFile(),
            hostFile,
            electionFile,
            MutexConstants.getRetryInterval(),
            MutexConstants.getStaleFileThreshold(),
            MutexConstants.getStartJitter(),
            MutexConstants.getEndJitter(),
            MutexState.INITIALIZATION
        );
        
        // Return
        return output;
    }
    
    
    /**
     * Build with new lock entry generated internally
     * 
     * @param timestamp
     * @return {@link Mutex}
     */
    public static Mutex create(long timestamp) {
        
        // Init vars
        String id, mutexFile;
        Path hostFile, electionFile;
        Mutex output;
        
        // Fetch variables
        id = UUID.randomUUID().toString();
        mutexFile = MutexLabellingUtils.getMutexFileName();
        hostFile = MutexConstants.getHostDir().resolve(mutexFile);
        electionFile = MutexConstants.getElectionFile();
                
        
        // Create mutex
        output = new Mutex(
            id,
            timestamp,
            MutexConstants.getLockDir(),
            MutexConstants.getLockFile(),
            hostFile,
            electionFile,
            MutexConstants.getRetryInterval(),
            MutexConstants.getStaleFileThreshold(),
            MutexConstants.getStartJitter(),
            MutexConstants.getEndJitter(),
            MutexState.INITIALIZATION
        );
        
        // Return
        return output;
    }
}