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
import org.tasktide.itemstore.mutex.model.Mutex;


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