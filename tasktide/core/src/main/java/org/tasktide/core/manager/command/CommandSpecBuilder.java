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
package org.tasktide.core.manager.command;


import java.util.Map;
import java.util.HashMap;


/**
 * For building {@link CommandSpecBuilder}
 * 
 * @author Brendan Kenna
 */
public class CommandSpecBuilder {

    // Attributes
    private String targetFile, queryString;
    private Map<String, Object> opts;
    
    
    /**
     * Construct with empty options Map
     * 
     */
    public CommandSpecBuilder() {
        this.opts = new HashMap<>();
    }
    
    
    /**
     * Build with provided target file 
     * 
     * @param targetFile
     * @return {@link CommandSpecBuilder}
     */
    public CommandSpecBuilder withTargetFile(String targetFile) {
        this.targetFile = targetFile;
        return this;
    }
    
    
    /**
     * Build with provided query string
     * 
     * @param queryString
     * @return {@link CommandSpecBuilder}
     */
    public CommandSpecBuilder withQueryString(String queryString) {
        this.queryString = queryString;
        return this;
    }
    
    
    /**
     * Build with provided option
     * 
     * 
     * @param key
     * @param value
     * @return {@link CommandSpecBuilder}
     */
    public CommandSpecBuilder withOption(String key, Object value) {
        this.opts.put(key, value);
        return this;
    }
    
    
    /**
     * Build with provided option map
     * 
     * @param opts
     * @return {@link CommandSpecBuilder}
     */
    public CommandSpecBuilder withOptionMap(Map<String, Object> opts) {
        this.opts = opts;
        return this;
    }
    
    
    /**
     * Build with provided options
     * 
     * @return {@link CommandSpec}
     */
    public CommandSpec build() {
        return new CommandSpec(this.targetFile, this.queryString, this.opts);
    }
}