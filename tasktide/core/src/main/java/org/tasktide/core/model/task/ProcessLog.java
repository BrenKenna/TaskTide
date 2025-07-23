/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.tasktide.core.model.task;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbConfig;
import jakarta.json.bind.annotation.JsonbCreator;
import jakarta.json.bind.annotation.JsonbProperty;

import jakarta.nosql.Column;
import jakarta.nosql.Embeddable;


/**
 * 
 * Model class to hold standard error and out logs
 * 
 * @author bkenna
 */
@Embeddable
public class ProcessLog {
    
    @Column
    @JsonbProperty("Id")
    private String procLogId;
    
    
    @Column
    @JsonbProperty("Stdout")
    private String[] stdout;
    
    @Column
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
        @JsonbProperty("Id") String procLogId,
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