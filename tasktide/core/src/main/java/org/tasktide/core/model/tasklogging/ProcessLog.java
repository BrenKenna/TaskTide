/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.tasktide.core.model.tasklogging;

import jakarta.enterprise.context.Dependent;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.annotation.JsonbCreator;
import jakarta.json.bind.annotation.JsonbProperty;

import jakarta.nosql.Column;
import jakarta.nosql.Entity;
import jakarta.nosql.Id;


/**
 * Model class to hold standard error and out logs
 * 
 * @author bkenna
 */
@Entity
@Dependent
public class ProcessLog {
    
    @Id
    @JsonbProperty("Id")
    private String id;
    
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
     * Constructor for JSON Deserialization
     * 
     * @param id
     * @param stdout
     * @param stderr 
     */
    @JsonbCreator
    public ProcessLog(
        @JsonbProperty("Id") String id,
        @JsonbProperty("Stdout") String[] stdout,
        @JsonbProperty("Stderr") String[] stderr
    ) {
        this.id = id;
        this.stdout = stdout;
        this.stderr = stderr;
    }
    
    
    /**
     * Get Id
     * 
     * @return String 
     */
    public String getId() {
        return id;
    }

    
    /**
     * Set Id
     * 
     * @param id 
     */
    public void setId(String id) {
        this.id = id;
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
            "id=" + id +
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
     * Represent as JSON object
     * 
     * @return Jsonb
     */
    public Jsonb toJson() {
        Jsonb json = JsonbBuilder.create();
        return json;
    }
}
