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

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbConfig;
import java.nio.file.Path;

import java.util.List;
import java.util.Optional;

import org.tasktide.itemstore.mutex.model.Mutex;
import org.tasktide.itemstore.mutex.utils.MutexConstants;
import org.tasktide.itemstore.mutex.utils.MutexFilesUtilis;


/**
 * Class for coordinating locks on NSF where member
 *  nodes are unknown and dynamic. Multi-stage process
 *  first forms host queue based on epcoh time, and host label.
 *  Once locked, base Java is for file channel lock (process level),
 *  and relevant methods can be synchronized from there (thread level).
 *
 * @author Brendan Kenna
 */
public abstract class InterProcessMutex implements MutexElection {
    

    // Attributes
    protected final Jsonb JSON, PRETTY_JSON;
    
    
    /**
     * Construct
     * 
     */
    public InterProcessMutex() {
        JSON = JsonbBuilder.create();
        PRETTY_JSON = JsonbBuilder.create(
            new JsonbConfig().withFormatting(true)
        );
    }
    
    
    
    /**
     * Returns the full file path of the leader
     * 
     * @return 
     */
    @Override
    public Optional<Path> inferLeader() {
        return MutexFilesUtilis
            .fetchFiles(MutexConstants.getHostDir())
            .sorted()
            .findFirst()
        ;
    }
    
    
    /**
     * Infer position of {@link Mutex} in queue
     * 
     * @param mutex
     * @return int
     */
    @Override
    public int inferPosition(Mutex mutex) {
        
        // Search params
        int counter = 0;
        boolean found = false;
        
        // Fetch all election files
        List<Path> paths = MutexFilesUtilis.fetchFiles(MutexConstants.getHostDir())
            .sorted()
            .toList();
        
        // Search until found
        while( counter < paths.size() && !found ) {
            Path active = paths.get(counter);
            if ( mutex.getElectionFile() == active ) {
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
     * Return queue size
     * 
     * @return 
     */
    @Override
    public int queueSize() {
        return Math.toIntExact(MutexFilesUtilis.fetchFiles(MutexConstants.getHostDir()).count());
    }
    
    
    /**
     * Handle first instance of mutex
     * 
     * @param mutex
     * @return 
     */
    public boolean handleFirstInstance(Mutex mutex) {
        
        // Create files
        
        // Return state
        return true;
    }
}