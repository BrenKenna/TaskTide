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

import jakarta.nosql.Column;
import jakarta.nosql.Embeddable;

import java.util.Optional;
import java.time.Duration;


/**
 * 
 * Model class to hold the data from task processing
 * 
 * @author bkenna
 */
@jakarta.nosql.Embeddable
@jakarta.persistence.Embeddable
public class TaskLogging {
    
    @jakarta.nosql.Column("TaskLogggingId")
    @jakarta.persistence.Column(name = "TaskLogggingId")
    @JsonbProperty("Task Loggging Id")
    private String taskLogId;
    
    @jakarta.nosql.Column("ProcessId")
    @jakarta.persistence.Column(name = "ProcessId")
    @JsonbProperty("Process Id")
    private long procId;
    
    @jakarta.nosql.Column("ProcessLog")
    @jakarta.persistence.Column(name = "ProcessLog")
    @JsonbProperty("Process Log")
    private ProcessLog procLog;
    
    @jakarta.nosql.Column("StartTime")
    @jakarta.persistence.Column(name = "StartTime")
    @JsonbProperty("Start Time")
    private long startTime;
    
    @jakarta.nosql.Column("EndTime")
    @jakarta.persistence.Column(name = "EndTime")
    @JsonbProperty("End Time")
    private long endTime;
    
    @jakarta.nosql.Column("ThreadName")
    @jakarta.persistence.Column(name = "ThreadName")
    @JsonbProperty("Thread Name")
    private String threadName;
    
    @jakarta.nosql.Column("CpuDuration")
    @jakarta.persistence.Column(name = "CpuDuration")
    @JsonbProperty("CPU Duration")
    private long cpuDuration;

    @jakarta.nosql.Column("ExitCode")
    @jakarta.persistence.Column(name = "ExitCode")
    @JsonbProperty("Exit Code")
    private int exitCode;
    
    
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
     * @param exitCode
     */
    public TaskLogging(long procId, ProcessLog procLog, long startTime, long endTime, String threadName, long cpuDuration, int exitCode) {
        this.procId = procId;
        this.procLog = procLog;
        this.startTime = endTime;
        this.threadName = threadName;
        this.cpuDuration = cpuDuration;
        this.exitCode = exitCode;
    }
    
    
    /**
     * Constructor for JSON Deserialization
     * 
     * @param taskLogId
     * @param procId
     * @param procLog
     * @param startTime
     * @param endTime
     * @param threadName 
     * @param cpuDuration
     * @param exitCode
     */
    @JsonbCreator
    public TaskLogging(
        @JsonbProperty("Task Loggging Id") String taskLogId,
        @JsonbProperty("Process Id") long procId,
        @JsonbProperty("Process Log") ProcessLog procLog,
        @JsonbProperty("Start Time") long startTime,
        @JsonbProperty("End Time") long endTime,
        @JsonbProperty("Thread Name") String threadName,
        @JsonbProperty("CPU Duration") long cpuDuration,
        @JsonbProperty("Exit Code") int exitCode
    ) {
        this.taskLogId = taskLogId;
        this.procId = procId;
        this.procLog = procLog;
        this.startTime = startTime;
        this.endTime = endTime;
        this.threadName = threadName;
        this.cpuDuration = cpuDuration;
        this.exitCode = exitCode;
    }
    
    
    /**
     * Get Id
     * 
     * @return String
     */
    public String getId() {
        return taskLogId;
    }

    
    /**
     * Set Id
     * 
     * @param taskLogId
     */
    public void setId(String taskLogId) {
        this.taskLogId = taskLogId;
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
     * Get exit code
     * 
     * @return int
     */
    public int getExitCode() {
        return this.exitCode;
    }
    
    
    /**
     * Set exit code
     * 
     * @param exitCode 
     */
    public void setExitCode(int exitCode) {
        this.exitCode = exitCode;
    }
    
    
    /**
     * Get process log
     * 
     * @return {@link ProcessLog}
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
            "taskLogId=" + taskLogId +
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