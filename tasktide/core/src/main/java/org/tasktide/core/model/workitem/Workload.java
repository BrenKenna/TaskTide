/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.tasktide.core.model.workitem;

import jakarta.enterprise.context.Dependent;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbConfig;

import jakarta.json.bind.annotation.JsonbCreator;
import jakarta.json.bind.annotation.JsonbProperty;

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
@Dependent
public class Workload {
    
    @JsonbProperty("Id")
    private String id;
    
    @JsonbProperty("Workload")
    private Map<String, ItemTask> workload;
    
    @JsonbProperty("Workload State")
    private ItemState workloadState;
    
    @JsonbProperty("Workload Type")
    private ItemType workloadType;
    
    
    /**
     * Null constructor
     */
    public Workload() {
        this.workload = new HashMap<>();
        this.workloadType = ItemType.SINGLE;
        this.workloadState = ItemState.TODO;
    }
    
    
    /**
     * Constructor for JSON Deserialization
     * 
     * @param id
     * @param workload
     * @param workloadState
     * @param workloadType 
     */
    @JsonbCreator
    public Workload(
        @JsonbProperty("Id") String id,
        @JsonbProperty("Workload") Map<String, ItemTask> workload,
        @JsonbProperty("Workload State") ItemState workloadState,
        @JsonbProperty("Workload Type") ItemType workloadType
    ) {
        this.id = id;
        this.workload = workload;
        this.workloadState = workloadState;
        this.workloadType = workloadType;
    }

    
    /**
     * Add a new work item to workload
     * 
     * @param work
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
        
        // Set workload state
        this.workloadState = ItemState.TODO;
        
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
        
        // Set workload state
        this.workloadState = ItemState.TODO;
        
        // Return flag
        return true;
    }
    
    
    /**
     * Fetch task if present
     * 
     * @param taskName
     * @return ItemTask
     */
    public ItemTask getTask(String taskName) {
        return workload.get(taskName);
    }
    
    
    /**
     * Drop named task if present in workload
     * 
     * @param task
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
     * Summarize ItemTasks by count of their TaskSate
     * 
     * @return Map-TaskState, Integer
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
     * @return Map-TaskState, List-ItemTask
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
        return id;
    }

    
    /**
     * Set workload Id
     * 
     * @param id 
     */
    public void setId(String id) {
        this.id = id;
    }

    
    /**
     * Get workload
     * 
     * @return Map-TaskName, ItemTask
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
        this.workloadState = ItemState.TODO;
        if( this.workload.size() >= 2 ) {
            this.workloadType = ItemType.NESTED;
        }
        else {
            this.workloadType = ItemType.SINGLE;
        }
    }

    
    /**
     * Get workload state
     * 
     * @return 
     */
    public ItemState getWorkloadState() {
        return workloadState;
    }

    
    /**
     * Set workload state
     * 
     * @param workloadState 
     */
    public void setWorkloadState(ItemState workloadState) {
        this.workloadState = workloadState;
    }

    
    /**
     * Get workload type
     * 
     * @return ItemType
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
            "id=" + id +
            ", workload=" + workload +
            ", workloadState=" + workloadState +
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
