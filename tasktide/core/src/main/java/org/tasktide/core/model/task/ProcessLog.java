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
package org.tasktide.core.model.task;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbConfig;

import jakarta.json.bind.annotation.JsonbCreator;
import jakarta.json.bind.annotation.JsonbProperty;


/**
 * 
 * Model class to hold standard error and out logs
 * 
 * @author bkenna
 */
@jakarta.nosql.Embeddable
@jakarta.persistence.Embeddable
public class ProcessLog {
    
    @jakarta.nosql.Column("ProcessLogId")
    @jakarta.persistence.Column(name = "ProcessLogId")
    @JsonbProperty("Process Log Id")
    private String procLogId;
    
    @jakarta.nosql.Column("Stdout")
    @jakarta.persistence.Column(name = "Stdout")
    @JsonbProperty("Stdout")
    private String[] stdout;
    
    @jakarta.nosql.Column("Stderr")
    @jakarta.persistence.Column(name = "Stderr")
    @JsonbProperty("Stderr")
    private String[] stderr;
    
    
    /**
     * Null constructor
     */
    public ProcessLog() {}
    
    
    /**
     * Construct with all data for builder
     * 
     * @param stdout
     * @param stderr 
     */
    public ProcessLog(String[] stdout, String[] stderr) {
        this.stdout = stdout;
        this.stderr = stderr;
    }
    
    
    /**
     * Constructor for JSON Deserialization
     * 
     * @param procLogId
     * @param stdout
     * @param stderr 
     */
    @JsonbCreator
    public ProcessLog(
        @JsonbProperty("Process Log Id") String procLogId,
        @JsonbProperty("Stdout") String[] stdout,
        @JsonbProperty("Stderr") String[] stderr
    ) {
        this.procLogId = procLogId;
        this.stdout = stdout;
        this.stderr = stderr;
    }
    
    
    /**
     * Get Id
     * 
     * @return String 
     */
    public String getId() {
        return procLogId;
    }

    
    /**
     * Set Id
     * 
     * @param procLogId
     */
    public void setId(String procLogId) {
        this.procLogId = procLogId;
    }
    
    
    /**
     * Get stdout
     * 
     * @return String[] 
     */
    public String[] getStdout() {
        return stdout;
    }

    
    /**
     * Set the stdout
     * 
     * @param stdout 
     */
    public void setStdout(String[] stdout) {
        this.stdout = stdout;
    }

    
    /**
     * Get stderr log
     * 
     * @return String[] 
     */
    public String[] getStderr() {
        return stderr;
    }

    
    /**
     * Set the stderr log
     * 
     * @param stderr 
     */
    public void setStderr(String[] stderr) {
        this.stderr = stderr;
    }

    
    /**
     * Represent as string
     * 
     * @return String
     */
    @Override
    public String toString() {
        return "ProcessLog{" +
            "procLogId=" + procLogId +
            ", stdout=" + stdout +
            ", stderr=" + stderr +
        '}';
    }
    
    
    /**
     * Serialize to JSON string
     * 
     * @return String
     */
    public String toJsonString() {
        Jsonb json = JsonbBuilder.create();
        return json.toJson(this);
    }
    
    
    /**
     * Serialize to a human readable formatted JSON string
     * 
     * @return String
     */
    public String toJsonDoc() {
        JsonbConfig conf = new JsonbConfig().withFormatting(Boolean.TRUE);
        Jsonb json = JsonbBuilder.create(conf);
        return json.toJson(this);
    }
}