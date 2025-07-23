/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.tasktide.core.model.workitem;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbConfig;

import jakarta.json.bind.annotation.JsonbCreator;
import jakarta.json.bind.annotation.JsonbProperty;

import jakarta.nosql.Column;
import jakarta.nosql.Embeddable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.tasktide.core.model.task.ItemTask;
import org.tasktide.core.model.task.TaskState;


/**
 * Model class for worklaod
 * 
 * @author bkenna
 */
@Embeddable
public class Workload {
    
    @Column
    @JsonbProperty("Id")
    private String workloadId;
    
    
    @Column
    @JsonbProperty("Workload")
    private Map<String, ItemTask> workload;
    
    @Column
    @JsonbProperty("Workload Type")
    private ItemType workloadType;
    
    
    /**
     * Null constructor
     */
    public Workload() {
        this.workload = new HashMap<>();
        this.workloadType = ItemType.SINGLE;
    }
    
    
    /**
     * Constructor for JSON Deserialization
     * 
     * @param workloadId
     * @param workload
     * @param workloadType 
     */
    @JsonbCreator
    public Workload(
        @JsonbProperty("Id") String workloadId,
        @JsonbProperty("Workload") Map<String, ItemTask> workload,
        @JsonbProperty("Workload Type") ItemType workloadType
    ) {
        this.workloadId = workloadId;
        this.workload = workload;
        this.workloadType = workloadType;
    }

    
    /**
     * Add a new work item to workload
     * 
     * @param work
     * @param taskName
     * @return boolean
     */
    public boolean addTask(String taskName, ItemTask work) {
        
        // Handle whether to add task
        if ( workload.containsKey(taskName) ) {
            return false;
        }
        
        // Otherwise add task
        this.workload.put(taskName, work);
        if ( this.workload.size() >= 2 ) {
            this.workloadType = ItemType.NESTED;
        }
        else {
            this.workloadType = ItemType.SINGLE;
        }
        
        // Return flag
        return true;
    }
    
    
    /**
     * Add provided task if not present
     * 
     * @param task
     * @return boolean
     */
    public boolean addTask(ItemTask task) {
        
        // Handle whether to add task
        if ( workload.containsKey(task.getTaskName()) ) {
            return false;
        }
        
        // Otherwise add task
        this.workload.put(task.getTaskName(), task);
        if ( this.workload.size() >= 2 ) {
            this.workloadType = ItemType.NESTED;
        }
        else {
            this.workloadType = ItemType.SINGLE;
        }
        
        // Return flag
        return true;
    }
    
    
    /**
     * Fetch task if present
     * 
     * @param taskName
     * @return {@link ItemTask}
     */
    public ItemTask getTask(String taskName) {
        return workload.get(taskName);
    }
    
    
    /**
     * Drop named task if present in workload
     * 
     * @param taskName
     * @return boolean
     */
    public boolean dropTask(String taskName) {
    
        // Handle whether to add task
        if ( !this.workload.containsKey(taskName) ) {
            return false;
        }
        
        // Add task
        this.workload.remove(taskName);
        if ( this.workload.size() >= 2 ) {
            this.workloadType = ItemType.NESTED;
        }
        else {
            this.workloadType = ItemType.NESTED;
        }
        
        // Return flag
        return true;
    }
    
    
    /**
     * Drop provided task if present from workload
     * 
     * @param task
     * @return boolean
     */
    public boolean dropTask(ItemTask task) {
        
        // Handle whether to drop task
        if ( !this.workload.containsKey(task.getTaskName()) ) {
            return false;
        }
        
        // Drop task
        this.workload.remove(task.getTaskName());
        
        // Handle task type
        if ( this.workload.size() >= 2 ) {
            this.workloadType = ItemType.NESTED;
        }
        else {
            this.workloadType = ItemType.SINGLE;
        }
        
        // Return flag
        return true;
    }
    
    
    /**
     * Summarize {@link ItemTask} by count of their {@link TaskState}
     * 
     * @return Map-{@link TaskState}, Integer
     */
    public Map<TaskState, Integer> summarizeWorkload() {
        
        // Initialize results
        Map<TaskState, Integer> results = new HashMap<>();
        
        // Fetch count of tasks for state
        for( TaskState taskState : TaskState.values() ) {
            int count = 0;
            for ( ItemTask task : this.workload.values() ) {
                if ( task.getTaskState().equals(taskState) ) {
                    count++;
                }
            }
            results.put(taskState, count);
        }
        
        // Return results
        return results;
    }
    
    
    /**
     * Fetch tasks by their state
     * 
     * @return Map-{@link TaskState}, List-{@link ItemTask}
     */
    public Map<TaskState, List<ItemTask>> fetchByState() {
    
        // Initialize results
        Map<TaskState, List<ItemTask>> results = new HashMap<>();
        
        // Fetch tasks by their state
        for( TaskState taskState : TaskState.values() ) {
            List<ItemTask> tasks = new ArrayList<>();
            for ( ItemTask task : this.workload.values() ) {
                if ( task.getTaskState().equals(taskState) ) {
                    tasks.add(task);
                }
            }
            results.put(taskState, tasks);
        }
        
        // Return results
        return results;
    }
    
    
    /**
     * Fetch last {@link ItemTask} to complete
     * 
     * @return long
     */
    public long getLatestDone() {
        long output = 0L;
        for ( ItemTask task : workload.values() ) {
            if ( task.getTaskState() == TaskState.COMPLETE ) {
                if ( task.getTaskLog().getEndTime() > output ) {
                    output = task.getTaskLog().getEndTime();
                }
            }
        }
        return output;
    }
    
    
    /**
     * Fetch last {@link ItemTask} to complete
     * 
     * @return long
     */
    public long getEarliestDone() {
        
        // Set comparison to max long value
        long output = Long.MAX_VALUE;
        
        // Fetch smallest done time
        for ( ItemTask task : workload.values() ) {
            if ( task.getTaskState() == TaskState.COMPLETE ) {
                if ( task.getTaskLog().getEndTime() < output ) {
                    output = task.getTaskLog().getEndTime();
                }
            }
        }
        
        // Handle no changes to output, or output
        return output == Long.MAX_VALUE ? -1 : output;
    }
    
    
    /**
     * Get workload size
     * 
     * @return int
     */
    public int getWorkloadSize() {
        return this.workload.size();
    }
    
    
    /**
     * Get workload Id
     * 
     * @return String
     */
    public String getId() {
        return this.workloadId;
    }

    
    /**
     * Set workload Id
     * 
     * @param workloadId 
     */
    public void setId(String workloadId) {
        this.workloadId = workloadId;
    }

    
    /**
     * Get workload
     * 
     * @return Map-TaskName, {@link ItemTask}
     */
    public Map<String, ItemTask> getWorkload() {
        return workload;
    }

    
    /**
     * Set workload
     * 
     * @param workload 
     */
    public void setWorkload(Map<String, ItemTask> workload) {
        this.workload = workload;
        if( this.workload.size() >= 2 ) {
            this.workloadType = ItemType.NESTED;
        }
        else {
            this.workloadType = ItemType.SINGLE;
        }
    }

    
    /**
     * Get workload type
     * 
     * @return {@link ItemType}
     */
    public ItemType getWorkloadType() {
        return workloadType;
    }

    
    /**
     * Set workload type
     * 
     * @param workloadType 
     */
    public void setWorkloadType(ItemType workloadType) {
        this.workloadType = workloadType;
    }

    
    /**
     * Represent workload as string
     * 
     * @return String
     */
    @Override
    public String toString() {
        return "Workload{" +
            "workloadId=" + workloadId +
            ", workload=" + workload +
            ", workloadType=" + workloadType +
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
