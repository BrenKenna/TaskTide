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
package org.tasktide.tasktide.parser.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;


/**
 * Holds an {@link Argument} collection
 * 
 * @author bkenna
 */
public class ArgumentMap {
    
    // Attributes
    private final Map<String, Argument<?>> args;
    
    
    /**
     * Initialize argument map
     * 
     */
    public ArgumentMap() {
        this.args = new HashMap<>();
    }
    
    
    /**
     * Get the argument map
     * 
     * @return Map-String, {@link Argument}
     */
    public Map<String, Argument<?>> getArgMap() {
        return this.args;
    }
    
    
    /**
     * Add the argument to argument map
     * 
     * @param arg
     * @return boolean
     */
    public boolean putArgument(Argument<?> arg) {
        return this.args.put(arg.getName(), arg) != null;
    }
    
    
    /**
     * Extend argument map with provided {@link ArgumentMap}
     * 
     * @param map 
     */
    public void extend(ArgumentMap map) {
        for ( Entry<String, Argument<?>> elm : map.getArgMap().entrySet() ) {
            this.args.put(elm.getKey(), elm.getValue());
        }
    }
    
    
    /**
     * Get the argument with key of queried string
     * 
     * @param arg
     * @return {@link Argument}
     */
    public Argument<?> getArgument(String arg) {
        return this.args.get(arg);
    }
    
    public Map<String, Argument<?>> getArgumentMap() {
        return this.args;
    }

    @Override
    public String toString() {
        return "ArgumentMap{" +
            "args=" + args +
        '}';
    }
}
