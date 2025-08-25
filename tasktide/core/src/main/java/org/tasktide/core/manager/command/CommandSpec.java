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

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbConfig;

import jakarta.json.bind.annotation.JsonbCreator;
import jakarta.json.bind.annotation.JsonbProperty;

import java.util.Map;
import java.util.Optional;


/**
 * Standardize peripheral requirements for {@link ManagerCommand}
 *
 * @author Brendan Kenna
 */
public class CommandSpec {
    
    // Attributes: Main args as fields
    @JsonbProperty("File Path")
    private Optional<String> filePath;
    
    @JsonbProperty("Query String")
    private Optional<String> queryString;
    
    @JsonbProperty("Options")
    private Optional<Map<String, Object>> options;
    
    
    /**
     * Default constructor for building
     */
    public CommandSpec() {}
    
    
    /**
     * Construct with all args
     * 
     * @param filePath
     * @param queryString
     * @param options 
     */
    @JsonbCreator
    public CommandSpec(
        @JsonbProperty("File Path") String filePath,
        @JsonbProperty("Query String") String queryString,
        @JsonbProperty("Options") Map<String, Object> options
    ) {
        this.filePath = Optional.ofNullable(filePath);
        this.queryString = Optional.ofNullable(queryString);
        this.options = Optional.ofNullable(options);
    }

    
    /**
     * Get the configured file path for {@link ManagerCommand}
     * 
     * @return Optional-String
     */
    public Optional<String> getFilePath() {
        return filePath;
    }

    
    /**
     * Sets file path for {@link ManagerCommand}
     * 
     * @param filePath 
     */
    public void setFilePath(Optional<String> filePath) {
        this.filePath = filePath;
    }

    
    /**
     * Gets query string for {@link ManagerCommand}
     * 
     * @return Optional-String
     */
    public Optional<String> getQueryString() {
        return queryString;
    }

    
    /**
     * Sets query string for {@link ManagerCommand}
     * 
     * @param queryString 
     */
    public void setQueryString(Optional<String> queryString) {
        this.queryString = queryString;
    }

    
    /**
     * Gets options map for {@link ManagerCommand}
     * 
     * @return Optional-Map-String,Object
     */
    public Optional<Map<String, Object>> getOptions() {
        return options;
    }

    
    /**
     * Sets option map for {@link ManagerCommand}
     * 
     * @param options 
     */
    public void setOptions(Optional<Map<String, Object>> options) {
        this.options = options;
    }

    
    /**
     * Represent as JSON string
     * 
     * @return String
     */
    public String toJsonString() {
        Jsonb json = JsonbBuilder.create();
        return json.toJson(this);
    }
    
    
    /**
     * Represent as JSON document
     * 
     * @return String
     */
    public String toJsonDoc() {
        JsonbConfig conf = new JsonbConfig().withFormatting(Boolean.TRUE);
        Jsonb json = JsonbBuilder.create(conf);
        return json.toJson(this);
    }
    
    
    /**
     * Represent as string
     * 
     * @return String
     */
    @Override
    public String toString() {
        return "CommandSpec{" +
            "filePath=" + filePath +
            ", queryString=" + queryString +
            ", options=" + options +
        '}';
    }
}