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
import org.tasktide.core.model.CustomAnnotation;

import org.tasktide.core.model.builders.ItemTaskBuilder;
import org.tasktide.core.model.builders.ProcessLogBuilder;
import org.tasktide.core.model.builders.TaskLoggingBuilder;
import org.tasktide.core.model.builders.WorkItemBuilder;

import org.tasktide.core.model.task.ItemTask;
import org.tasktide.core.model.task.ProcessLog;
import org.tasktide.core.model.task.TaskLogging;
import org.tasktide.core.model.task.TaskState;
import org.tasktide.core.model.workitem.WorkItem;
import org.tasktide.core.model.workitem.Workload;


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
    
    @JsonbProperty("Step Name")
    private String stepName;
    
    @JsonbProperty("Annotations")
    private CustomAnnotation anno;
    
    private final ItemTaskBuilder itemTaskBuilder;
    private final ProcessLogBuilder procLogBuilder;
    private final TaskLoggingBuilder taskLogBuilder;
    private final WorkItemBuilder workItemBuilder;

    
    /**
     * Null constructor
     */
    public ManagerTask() {
        this.workItemBuilder = new WorkItemBuilder();
        this.itemTaskBuilder = new ItemTaskBuilder();
        this.procLogBuilder = new ProcessLogBuilder();
        this.taskLogBuilder = new TaskLoggingBuilder();
        
        this.stepName = "";
        this.anno = new CustomAnnotation();
    }
    
    
    /**
     * Construct with task name and script
     * 
     * @param taskName
     * @param taskScript 
     */
    public ManagerTask(String taskName, String taskScript) {
        this.taskName = taskName;
        this.taskScript = taskScript;
        this.stepName = "";
        this.anno = new CustomAnnotation();
        
        this.workItemBuilder = new WorkItemBuilder();
        this.itemTaskBuilder = new ItemTaskBuilder();
        this.procLogBuilder = new ProcessLogBuilder();
        this.taskLogBuilder = new TaskLoggingBuilder();
    }
    
    
    /**
     * Construct with attributes
     * 
     * @param taskName
     * @param taskScript 
     * @param stepName
     * @param anno
     */
    public ManagerTask(
        @JsonbProperty("Task Name") String taskName,
        @JsonbProperty("Task Script") String taskScript,
        @JsonbProperty("Step Name") String stepName,
        @JsonbProperty("Annotations") CustomAnnotation anno
    ) {
        this.taskName = taskName;
        this.taskScript = taskScript;
        this.stepName = stepName;
        this.anno = anno;
        
        this.workItemBuilder = new WorkItemBuilder();
        this.itemTaskBuilder = new ItemTaskBuilder();
        this.procLogBuilder = new ProcessLogBuilder();
        this.taskLogBuilder = new TaskLoggingBuilder();
    }
    
    
    /**
     * Generate a command map from {@link ManagerTask}
     * 
     * @return Map-String, Object
     */
    public Map<String, Object> asMap() {
        Map<String, Object> output = new HashMap<>();
        output.put("Task Script", this.taskScript);
        output.put("Task Name", this.taskName);
        output.put("Step Name", this.stepName);
        output.put("Annotations", this.anno);
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
            .withId("ProcessLog-" + Utils.generateSalt())
            .withStderr(emptyArr)
            .withStdout(emptyArr)
        .build();
        TaskLogging taskLog = this.taskLogBuilder
            .withId("TaskLog-" + Utils.generateSalt())
            .withProcessLog(procLog)
            .withEndTime(0L)
            .withExitCode(-1)
            .withStartTime(0L)
            .withProcId(0L)
            .withCpuDuration(0L)
            .withThreadName("NA")
        .build();
        
        return this.itemTaskBuilder
            .withId( "ItemTask-" + Utils.generateSalt() )
            .withTaskName(taskName)
            .withTask(taskScript)
            .withTaskState(TaskState.PENDING)
            .withTaskLog(taskLog)
            .withAnnotation(this.anno)
            .withJobEnvId("")
        .build();
    }
    
    
    /**
     * Represent as {@link WorkItem}
     * 
     * @return {@link WorkItem}
     */
    public WorkItem asWorkItem() {
        ItemTask task = this.asItemTask();
        Workload workload = BuilderUtility.buildWorkload(task);
        if ( this.stepName != null ) {
            String stepId = TaskTideManagerUtility.fetchStepId(stepName);
            return BuilderUtility.buildWorkItem(task.getTaskName(), workload, stepName, stepId);
        }
        return BuilderUtility.buildWorkItem(stepName, workload, "Arbitrary");
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
     * Get step name
     * 
     * @return String
     */
    public String getStepName() {
        return stepName;
    }

    
    /**
     * Set step name
     * 
     * @param stepName 
     */
    public void setStepName(String stepName) {
        this.stepName = stepName;
    }

    
    /**
     * Get {@link CustomAnnotation}
     * 
     * @return {@link CustomAnnotation}
     */
    public CustomAnnotation getAnno() {
        return anno;
    }

    
    /**
     * Set {@link CustomAnnotation}
     * 
     * @param anno 
     */
    public void setAnno(CustomAnnotation anno) {
        this.anno = anno;
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
            ", stepName=" + stepName +
            ", anno=" + anno +
        '}';
    }
}