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
import jakarta.json.bind.annotation.JsonbTransient;

import java.lang.reflect.Field;

import org.tasktide.core.TaskTideModel;
import org.tasktide.core.manager.BuilderUtility;
import org.tasktide.core.model.CustomAnnotation;
import org.tasktide.core.model.workitem.WorkItem;


/**
 *
 * Model class to hold the task related to a WorkItem
 * 
 * @author bkenna
 */
@jakarta.nosql.Embeddable
@jakarta.persistence.Embeddable
public class ItemTask implements TaskTideModel<ItemTask> {
    
    @jakarta.nosql.Column("ItemTaskId")
    @jakarta.persistence.Column(name = "ItemTaskId")
    @JsonbProperty("ItemTaskId")
    private String itemTaskId;
    
    @jakarta.nosql.Column("TaskName")
    @jakarta.persistence.Column(name = "TaskName")
    @JsonbProperty("Task Name")
    private String taskName;
    
    @jakarta.nosql.Column("Task")
    @jakarta.persistence.Column(name = "Task")
    @JsonbProperty("Task")
    private String task;
    
    @jakarta.nosql.Column("TaskState")
    @jakarta.persistence.Column(name = "TaskState")
    @JsonbProperty("Task State")
    private TaskState taskState;
    
    @jakarta.nosql.Column("TaskLog")
    @jakarta.persistence.Column(name = "TaskLog")
    @JsonbProperty("Task Log")
    private TaskLogging taskLog;
    
    @jakarta.nosql.Column("WorkItemId")
    @jakarta.persistence.Column(name = "WorkItemId")
    @JsonbProperty("Work Item Id")
    private String workItemId;
    
    @jakarta.nosql.Column("JobEnvironmentId")
    @jakarta.persistence.Column(name = "WorkItemId")
    @JsonbProperty("Job Environment Id")
    private String jobEnvId;
    
    @jakarta.nosql.Column("Annotations")
    @jakarta.persistence.Column(name = "Annotations")
    @JsonbProperty("Annotations")
    private CustomAnnotation anno;
    
    
    /**
     * Empty constructor
     */
    public ItemTask() {
        this.taskState = TaskState.PENDING;
        this.taskLog = new TaskLogging();
        this.anno = new CustomAnnotation();
    }


    /**
     * Construct with all fields
     * 
     * @param taskName
     * @param task
     * @param taskState
     * @param taskLog 
     */
    public ItemTask(String taskName, String task, TaskState taskState, TaskLogging taskLog) {
        this.taskName = taskName;
        this.taskState = taskState;
        this.taskLog = taskLog;
        this.task = task;
        this.workItemId = "";
        this.anno = new CustomAnnotation();
    }
    
    
    /**
     * Construct with all fields
     * 
     * @param itemTaskId
     * @param taskName
     * @param task
     * @param taskState
     * @param taskLog 
     */
    public ItemTask(String itemTaskId, String taskName, String task, TaskState taskState, TaskLogging taskLog) {
        this.itemTaskId = itemTaskId;
        this.taskName = taskName;
        this.taskState = taskState;
        this.taskLog = taskLog;
        this.task = task;
        this.anno = new CustomAnnotation();
    }
    
    
    /**
     * Construct with all fields
     * 
     * @param itemTaskId
     * @param taskName
     * @param task
     * @param taskState
     * @param taskLog 
     * @param anno
     */
    public ItemTask(String itemTaskId, String taskName, String task, TaskState taskState, TaskLogging taskLog, CustomAnnotation anno) {
        this.itemTaskId = itemTaskId;
        this.taskName = taskName;
        this.taskState = taskState;
        this.taskLog = taskLog;
        this.task = task;
        this.anno = anno;
    }
    
    
    /**
     * Constructor for JSON Deserialization
     * 
     * @param itemTaskId
     * @param taskName
     * @param task 
     * @param taskState
     * @param taskLog
     * @param workItemId
     * @param jobEnvId
     * @param anno
     */    
    @JsonbCreator
    public ItemTask(
        @JsonbProperty("ItemTaskId") String itemTaskId,
        @JsonbProperty("Task Name") String taskName,
        @JsonbProperty("Task") String task,
        @JsonbProperty("Task State") TaskState taskState,
        @JsonbProperty("Task Log") TaskLogging taskLog,
        @JsonbProperty("Work Item Id") String workItemId,
        @JsonbProperty("Job Environment Id") String jobEnvId,
        @JsonbProperty("Annotations") CustomAnnotation anno
    ) {
        this.itemTaskId = itemTaskId;
        this.taskName = taskName;
        this.task = task;
        this.taskState = taskState;
        this.taskLog = taskLog;
        this.workItemId = workItemId;
        this.jobEnvId = jobEnvId;
        this.anno = anno;
    }

    
    /**
     * Get annotations
     * 
     * @return {@link CustomAnnotation}
     */
    @Override
    public CustomAnnotation getAnnotations() {
        return this.anno;
    }
    
    
    /**
     * Set provided {@link CustomAnnotation}
     * 
     * @param anno 
     */
    @Override
    public void setAnnotations(CustomAnnotation anno) {
        this.anno = anno;
    }
    
    
    /**
     * Set ItemTask to pending state
     */
    @Override
    public void resetModel() {
        this.taskState = TaskState.PENDING;
        this.taskLog = BuilderUtility.buildEmptyTaskLogging();
    }
    
    
    /**
     * See if task names match
     * 
     * @param query
     * @return 
     */
    public boolean isTask(String query) {
        return this.taskName.equals(query);
    }
    
    
    /**
     * Get id
     * 
     * @return String 
     */
    @Override
    public String getId() {
        return itemTaskId;
    }

    
    /**
     * Set id
     * 
     * @param itemTaskId
     */
    public void setId(String itemTaskId) {
        this.itemTaskId = itemTaskId;
    }

    
    /**
     * Set Id of the parent {@link WorkItem}
     * 
     * @param workItemId 
     */
    public void setWorkItemId(String workItemId) {
        this.workItemId = workItemId;
    }
    
    
    /**
     * Get the Id of the parent {@link WorkItem}
     * 
     * @return String
     */
    public String getWorkItemId() {
        return this.workItemId;
    }
    
    
    /**
     * Get job environment Id
     * 
     * @return String
     */
    public String getJobEnvId() {
        return this.jobEnvId;
    }
    
    
    /**
     * Sets job environment Id
     * 
     * @param jobEnvId 
     */
    public void setJobEnvId(String jobEnvId) {
        this.jobEnvId = jobEnvId;
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
     * Get task state
     * 
     * @return {@link TaskState}
     */
    public TaskState getTaskState() {
        return taskState;
    }

    
    /**
     * Set task state
     * 
     * @param taskState 
     */
    public void setTaskState(TaskState taskState) {
        this.taskState = taskState;
    }

    
    /**
     * Get task log
     * 
     * @return {@link TaskLogging}
     */
    public TaskLogging getTaskLog() {
        return taskLog;
    }

    
    /**
     * Set task log
     * 
     * @param taskLog 
     */
    public void setTaskLog(TaskLogging taskLog) {
        this.taskLog = taskLog;
    }

    
    /**
     * Get task
     * 
     * @return String
     */
    public String getTask() {
        return task;
    }

    
    /**
     * Set task
     * 
     * @param task 
     */
    public void setTask(String task) {
        this.task = task;
    }
    
    
    /**
     * Returns work item id
     * 
     * @return String
     */
    @Override
    @JsonbTransient
    public String getCollection() {
        return this.workItemId;
    }

    
    /**
     * Represent as string
     * 
     * @return String
     */
    @Override
    public String toString() {
        return "ItemTask{" +
            "itemTaskId=" + itemTaskId +
            ", taskName=" + taskName +
            ", task=" + task +
            ", taskState=" + taskState +
            ", taskLog=" + taskLog +
            ", workItemId=" + workItemId +
            ", jobEnvId=" + jobEnvId +
            ", anno=" + anno +
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
    @Override
    public String toJsonDoc() {
        JsonbConfig conf = new JsonbConfig().withFormatting(Boolean.TRUE);
        Jsonb json = JsonbBuilder.create(conf);
        return json.toJson(this);
    }

    @Override
    public String toJson() {
        return toJsonDoc();
    }
    
    @Override
    @JsonbTransient
    public String getState() {
        return this.getTaskState().name();
    }

    @Override
    public Object getValueFromField(String field) {
        try {
            // Use reflection to get the declared field from this class
            Field declaredField = this.getClass().getDeclaredField(field);
            declaredField.setAccessible(true); // In case the field is private
            Object fieldValue = declaredField.get(this);

            return fieldValue;

        }
        catch (Exception ex) {
            // Optional: Log or rethrow if needed
            return null;
        }
    }
}