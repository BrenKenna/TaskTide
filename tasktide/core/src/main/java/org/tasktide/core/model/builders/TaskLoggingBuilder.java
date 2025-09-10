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
    public TaskLoggingBuilder withId(String id) {
        this.id = id;
        return this;
    }
    
    
    /**
     * Add Id field
     * 
     * @param procId
     * @return {@link TaskLoggingBuilder}
     */
    public TaskLoggingBuilder withProcId(long procId) {
        this.procId = procId;
        return this;
    }
    
    
    /**
     * Add ProcessLog field
     * 
     * @param procLog
     * @return {@link TaskLoggingBuilder}
     */
    public TaskLoggingBuilder withProcessLog(ProcessLog procLog) {
        this.procLog = procLog;
        return this;
    }
    
    
    /**
     * Add start time
     * 
     * @param startTime
     * @return {@link TaskLoggingBuilder}
     */
    public TaskLoggingBuilder withStartTime(long startTime) {
        this.startTime = startTime;
        return this;
    }
    
    
    /**
     * Add end time field
     * 
     * @param endTime
     * @return {@link TaskLoggingBuilder}
     */
    public TaskLoggingBuilder withEndTime(long endTime) {
        this.endTime = endTime;
        return this;
    }
    
    
    /**
     * Add thread name field
     * 
     * @param threadName
     * @return {@link TaskLoggingBuilder}
     */
    public TaskLoggingBuilder withThreadName(String threadName) {
        this.threadName = threadName;
        return this;
    }
    
    
    /**
     * Add CPU duration field
     * 
     * @param cpuDuration
     * @return {@link TaskLoggingBuilder}
     */
    public TaskLoggingBuilder withCpuDuration(long cpuDuration) {
        this.cpuDuration = cpuDuration;
        return this;
    }
    
    
    /**
     * Add Exit Code field
     * 
     * @param exitCode
     * @return {@link TaskLoggingBuilder}
     */
    public TaskLoggingBuilder withExitCode(int exitCode) {
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
