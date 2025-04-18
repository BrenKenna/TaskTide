/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.tasktide.core.model.task;

import jakarta.enterprise.context.Dependent;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbConfig;
import jakarta.json.bind.annotation.JsonbCreator;
import jakarta.json.bind.annotation.JsonbProperty;

import jakarta.nosql.Embeddable;

import java.util.Optional;
import java.time.Duration;


/**
 * 
 * Model class to hold the data from task processing
 * 
 * @author bkenna
 */
@Embeddable
@Dependent
public class TaskLogging {
    
    @JsonbProperty("Id")
    private String id;
    
    @JsonbProperty("Process Id")
    private long procId;
    
    @JsonbProperty("Process Log")
    private ProcessLog procLog;
    
    @JsonbProperty("Start Time")
    private long startTime;
    
    @JsonbProperty("End Time")
    private long endTime;
    
    @JsonbProperty("Thread Name")
    private String threadName;
    
    @JsonbProperty("CPU Duration")
    private long cpuDuration;

    
    /**
     * Null constructor
     */
    public TaskLogging() {
        this.procLog = new ProcessLog();
    }

    
    /**
     * Construct with all data for builder
     * 
     * @param procId
     * @param procLog
     * @param startTime
     * @param endTime
     * @param threadName
     * @param cpuDuration 
     */
    public TaskLogging(long procId, ProcessLog procLog, long startTime, long endTime, String threadName, long cpuDuration) {
        this.procId = procId;
        this.procLog = procLog;
        this.startTime = endTime;
        this.threadName = threadName;
        this.cpuDuration = cpuDuration;
    }
    
    
    /**
     * Constructor for JSON Deserialization
     * 
     * @param id
     * @param procId
     * @param procLog
     * @param startTime
     * @param endTime
     * @param threadName 
     * @param cpuDuration 
     */
    @JsonbCreator
    public TaskLogging(
        @JsonbProperty("Id") String id,
        @JsonbProperty("Process Id") long procId,
        @JsonbProperty("Process Log") ProcessLog procLog,
        @JsonbProperty("Start Time") long startTime,
        @JsonbProperty("End Time") long endTime,
        @JsonbProperty("Thread Name") String threadName,
        @JsonbProperty("CPU Duration") long cpuDuration
    ) {
        this.id = id;
        this.procId = procId;
        this.procLog = procLog;
        this.startTime = startTime;
        this.endTime = endTime;
        this.threadName = threadName;
        this.cpuDuration = cpuDuration;
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
     * Get process Id
     * 
     * @return long
     */
    public long getProcId() {
        return procId;
    }

    /**
     * Set process Id
     * 
     * @param procId
     */
    public void procId(long procId) {
        this.procId = procId;
    }

    
    /**
     * Get process log
     * 
     * @return Process Log
     */
    public ProcessLog getProcLog() {
        return procLog;
    }

    
    /**
     * Set process log
     * 
     * @param procLog 
     */
    public void setProcLog(ProcessLog procLog) {
        this.procLog = procLog;
    }

    
    /**
     * Get start time
     * 
     * @return long
     */
    public long getStartTime() {
        return startTime;
    }

    
    /**
     * Set start time
     * 
     * @param startTime 
     */
    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    
    /**
     * Get end time
     * 
     * @return long
     */
    public long getEndTime() {
        return endTime;
    }

    
    /**
     * Set end time
     * 
     * @param endTime 
     */
    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    
    /**
     * Get thread name
     * 
     * @return String
     */
    public String getThreadName() {
        return threadName;
    }

    
    /**
     * Set thread name
     * 
     * @param threadName 
     */
    public void setThreadName(String threadName) {
        this.threadName = threadName;
    }

    
    /**
     * Get CPU duration
     * 
     * @return 
     */
    public long getCpuDuration() {
        return cpuDuration;
    }

    
    /**
     * Set CPU duration
     * 
     * @param cpuDuration 
     */
    public void setCpuDuration(long cpuDuration) {
        this.cpuDuration = cpuDuration;
    }
    
    /**
     * Set CPU duration
     * 
     * @param cpuDuration 
     */
    public void setCpuDuration(Optional<Duration> cpuDuration) {
        this.cpuDuration = cpuDuration.get().getSeconds();
    }

    
    /**
     * Represent as string
     * 
     * @return String 
     */
    @Override
    public String toString() {
        return "TaskLogging{" +
            "id=" + id +
            ", procId=" + procId +
            ", procLog=" + procLog.toString() +
            ", startTime=" + startTime +
            ", endTime=" + endTime +
            ", threadName=" + threadName +
            ", cpuDuration=" + cpuDuration +
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