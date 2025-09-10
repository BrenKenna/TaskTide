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
package org.tasktide.core.manager;

import org.tasktide.core.supporting.Utils;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbConfig;
import jakarta.json.bind.annotation.JsonbProperty;

import java.util.HashMap;
import java.util.Map;

import org.tasktide.core.model.builders.ItemTaskBuilder;
import org.tasktide.core.model.builders.ProcessLogBuilder;
import org.tasktide.core.model.builders.TaskLoggingBuilder;

import org.tasktide.core.model.task.ItemTask;
import org.tasktide.core.model.task.ProcessLog;
import org.tasktide.core.model.task.TaskLogging;
import org.tasktide.core.model.task.TaskState;


/**
 * Simple converter class for converting user input into {@link ItemTask}
 * 
 * @author bkenna
 */
public class ManagerTask {

    @JsonbProperty("Task Name")
    private String taskName;
    
    @JsonbProperty("Task Script")
    private String taskScript;
    
    private final ItemTaskBuilder itemTaskBuilder;
    private final ProcessLogBuilder procLogBuilder;
    private final TaskLoggingBuilder taskLogBuilder;
    private final Utils utils;
    
    /**
     * Null constructor
     */
    public ManagerTask() {
        this.itemTaskBuilder = new ItemTaskBuilder();
        this.procLogBuilder = new ProcessLogBuilder();
        this.taskLogBuilder = new TaskLoggingBuilder();
        this.utils = new Utils("dd/MM/yy HH:mm:ss", 4);
    }
    
    
    /**
     * Construct with attributes
     * 
     * @param taskName
     * @param taskScript 
     */
    public ManagerTask(
        @JsonbProperty("Task Name") String taskName,
        @JsonbProperty("Task Script") String taskScript
    ) {
        this.taskName = taskName;
        this.taskScript = taskScript;
        this.itemTaskBuilder = new ItemTaskBuilder();
        this.procLogBuilder = new ProcessLogBuilder();
        this.taskLogBuilder = new TaskLoggingBuilder();
        this.utils = new Utils("dd/MM/yy HH:mm:ss", 4);
    }
    
    
    /**
     * Generate a command map from {@link ManagerTask}
     * 
     * @return Map-String, String
     */
    public Map<String, String> asMap() {
        Map<String, String> output = new HashMap<>();
        output.put("Task Script", taskScript);
        output.put("Task Name", taskName);
        return output;
    }
        
    
    /**
     * Represent as {@link ItemTask}
     * 
     * @return {@link ItemTask}
     */
    public ItemTask asItemTask() {
        
        String[] emptyArr = {""};
        ProcessLog procLog = this.procLogBuilder
            .withId("ProcessLog-" + utils.generateSalt())
            .withStderr(emptyArr)
            .withStdout(emptyArr)
        .build();
        TaskLogging taskLog = this.taskLogBuilder
            .withId("TaskLog-" + utils.generateSalt())
            .withProcessLog(procLog)
            .withEndTime(0L)
            .withExitCode(-1)
            .withStartTime(0L)
            .withProcId(0L)
            .withCpuDuration(0L)
            .withThreadName("NA")
        .build();
        
        return itemTaskBuilder
            .withId( "ItemTask-" + utils.generateSalt() )
            .withTaskName(taskName)
            .withTask(taskScript)
            .withTaskState(TaskState.PENDING)
            .withTaskLog(taskLog)
        .build();
    }
    
    
    /**
     * Get task name
     * 
     * @return String
     */
    public String getTaskName() {
        return taskName;
    }

    
    /**
     * Set task name
     * 
     * @param taskName 
     */
    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    
    /**
     * Get task script
     * 
     * @return String
     */
    public String getTaskScript() {
        return taskScript;
    }

    
    /**
     * Set task script
     * 
     * @param taskScript 
     */
    public void setTaskScript(String taskScript) {
        this.taskScript = taskScript;
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

    
    /**
     * Represent as String
     * 
     * @return String
     */
    @Override
    public String toString() {
        return "ManagerTask{" +
            "taskName=" + taskName +
            ", taskScript=" + taskScript +
            ", itemTaskBuilder=" + itemTaskBuilder + 
        '}';
    }
}