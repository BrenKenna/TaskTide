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

import java.util.Optional;
import java.time.Duration;


/**
 * Task logging object
 * 
 * @author bkenna
 */
@Entity
@Dependent
public class TaskLogging {
    
    @Id
    @JsonbProperty("Id")
    private String id;
    
    @Column
    @JsonbProperty("Process Log")
    private ProcessLog procLog;
    
    @Column
    @JsonbProperty("Start Time")
    private String startTime;
    
    @Column
    @JsonbProperty("End Time")
    private String endTime;
    
    @Column
    @JsonbProperty("Thread Name")
    private String threadName;
    
    @Column
    @JsonbProperty("CPU Duration")
    private long cpuDuration;

    
    /**
     * Null constructor
     */
    public TaskLogging() {}

    
    /**
     * Deserialize from json
     * 
     * @param id
     * @param procLog
     * @param startTime
     * @param endTime
     * @param threadName 
     * @param cpuDuration 
     */
    @JsonbCreator
    public TaskLogging(
        @JsonbProperty("Id") String id,
        @JsonbProperty("Process Log") ProcessLog procLog,
        @JsonbProperty("Start Time") String startTime,
        @JsonbProperty("End Time") String endTime,
        @JsonbProperty("Thread Name") String threadName,
        @JsonbProperty("CPU Duration") long cpuDuration
    ) {
        this.id = id;
        this.procLog = procLog;
        this.startTime = startTime;
        this.endTime = endTime;
        this.threadName = threadName;
        this.cpuDuration = cpuDuration;
    }

    
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ProcessLog getProcLog() {
        return procLog;
    }

    public void setProcLog(ProcessLog procLog) {
        this.procLog = procLog;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getThreadName() {
        return threadName;
    }

    public void setThreadName(String threadName) {
        this.threadName = threadName;
    }

    public long getCpuDuration() {
        return cpuDuration;
    }

    public void setCpuDuration(Optional<Duration> cpuDuration) {
        this.cpuDuration = cpuDuration.get().getSeconds();
    }
    
    
    /**
     * Return ProcessLog from Json string
     * 
     * @param log
     * @return ProcessLog
    */
    private ProcessLog getProcLog(String log) {
        Jsonb json = JsonbBuilder.create();
        return json.fromJson(log, ProcessLog.class);
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
            ", procLog=" + procLog.toJson() +
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
     * Represent as JSON object
     * 
     * @return Jsonb
     */
    public Jsonb toJson() {
        Jsonb json = JsonbBuilder.create();
        return json;
    }
}
