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
import java.lang.reflect.Field;

import org.tasktide.core.TaskTideModel;


/**
 *
 * Model class to hold the task related to a WorkItem
 * 
 * @author bkenna
 */
@Embeddable
public class ItemTask implements TaskTideModel<ItemTask> {
    
    @Column
    @JsonbProperty("Id")
    private String itemTaskId;
    
    
    @Column
    @JsonbProperty("Task Name")
    private String taskName;
    
    
    @Column
    @JsonbProperty("Task")
    private String task;
    
    
    @Column
    @JsonbProperty("Task State")
    private TaskState taskState;
    
    
    @Column
    @JsonbProperty("Task Log")
    private TaskLogging taskLog;
    
    @Column
    @JsonbProperty("Work Item Id")
    private String workItemId;
    
    /**
     * Empty constructor
     */
    public ItemTask() {
        this.taskState = TaskState.PENDING;
        this.taskLog = new TaskLogging();
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
     */    
    @JsonbCreator
    public ItemTask(
        @JsonbProperty("Id") String itemTaskId,
        @JsonbProperty("Task Name") String taskName,
        @JsonbProperty("Task") String task,
        @JsonbProperty("Task State") TaskState taskState,
        @JsonbProperty("Task Log") TaskLogging taskLog,
        @JsonbProperty("Work Item Id") String workItemId
    ) {
        this.itemTaskId = itemTaskId;
        this.taskName = taskName;
        this.task = task;
        this.taskState = taskState;
        this.taskLog = taskLog;
        this.workItemId = workItemId;
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
     * Represent as string
     * 
     * @return String
     */
    @Override
    public String toString() {
        return "ItemTask{" + 
            "itemTaskId=" + itemTaskId +
            ", taskName=" + taskName +
            ", taskState=" + taskState +
            ", taskLog=" + taskLog +
            ", task=" + task +
            ", workItemId=" + workItemId +
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

    @Override
    public String toJson() {
        return toJsonDoc();
    }
    
    @Override
    public String getState() {
        return this.getTask();
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