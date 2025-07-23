/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.tasktide.core.model.builders;

import org.tasktide.core.model.task.ProcessLog;
import org.tasktide.core.model.task.TaskLogging;


/**
 *
 * Allow TaskLogging objects to be built where all fields are optional
 * 
 * @author bkenna
 */
public class TaskLoggingBuilder extends ModelBuilder {
    
    // Attributes
    private String id, threadName = "";
    private ProcessLog procLog = null;
    private int exitCode;
    private long procId, cpuDuration, startTime, endTime;
    
    
    public TaskLoggingBuilder() {
        super();
    }
    
    
    /**
     * Add Id field
     * 
     * @param id
     * @return {@link TaskLoggingBuilder}
     */
    public TaskLoggingBuilder id(String id) {
        this.id = id;
        return this;
    }
    
    
    /**
     * Add Id field
     * 
     * @param procId
     * @return {@link TaskLoggingBuilder}
     */
    public TaskLoggingBuilder procId(long procId) {
        this.procId = procId;
        return this;
    }
    
    
    /**
     * Add ProcessLog field
     * 
     * @param procLog
     * @return {@link TaskLoggingBuilder}
     */
    public TaskLoggingBuilder processLog(ProcessLog procLog) {
        this.procLog = procLog;
        return this;
    }
    
    
    /**
     * Add start time
     * 
     * @param startTime
     * @return {@link TaskLoggingBuilder}
     */
    public TaskLoggingBuilder startTime(long startTime) {
        this.startTime = startTime;
        return this;
    }
    
    
    /**
     * Add end time field
     * 
     * @param endTime
     * @return {@link TaskLoggingBuilder}
     */
    public TaskLoggingBuilder endTime(long endTime) {
        this.endTime = endTime;
        return this;
    }
    
    
    /**
     * Add thread name field
     * 
     * @param threadName
     * @return {@link TaskLoggingBuilder}
     */
    public TaskLoggingBuilder threadName(String threadName) {
        this.threadName = threadName;
        return this;
    }
    
    
    /**
     * Add CPU duration field
     * 
     * @param cpuDuration
     * @return {@link TaskLoggingBuilder}
     */
    public TaskLoggingBuilder cpuDuration(long cpuDuration) {
        this.cpuDuration = cpuDuration;
        return this;
    }
    
    
    /**
     * Add Exit Code field
     * 
     * @param exitCode
     * @return {@link TaskLoggingBuilder}
     */
    public TaskLoggingBuilder exitCode(int exitCode) {
        this.exitCode = exitCode;
        return this;
    }
    
    
    /**
     * Construct TaskLogging object from provided fields
     * 
     * @return {@link TaskLogging}
     */
    @Override
    public TaskLogging build() {
        return new TaskLogging(id, procId, procLog, startTime, endTime, threadName, cpuDuration, exitCode);
    }
}
