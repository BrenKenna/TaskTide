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
import jakarta.nosql.Column;

import jakarta.nosql.Embeddable;


/**
 *
 * Model class to hold the task related to a WorkItem
 * 
 * @author bkenna
 */
@Embeddable
@Dependent
public class ItemTask {
    
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
    }
    
    
    /**
     * Constructor for JSON Deserialization
     * 
     * @param itemTaskId
     * @param taskName
     * @param task 
     * @param taskState
     * @param taskLog
     */
    @JsonbCreator
    public ItemTask(
        @JsonbProperty("Id") String itemTaskId,
        @JsonbProperty("Task Name") String taskName,
        @JsonbProperty("Task") String task,
        @JsonbProperty("Task State") TaskState taskState,
        @JsonbProperty("Task Log") TaskLogging taskLog
    ) {
        this.itemTaskId = itemTaskId;
        this.taskName = taskName;
        this.task = task;
        this.taskState = taskState;
        this.taskLog = taskLog;
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
     * @return TaskState
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
     * @return TaskLogging
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