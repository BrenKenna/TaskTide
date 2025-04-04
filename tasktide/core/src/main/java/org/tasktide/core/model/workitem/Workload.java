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

import jakarta.nosql.Column;
import jakarta.nosql.Entity;
import jakarta.nosql.Id;

import java.util.ArrayList;
import java.util.List;

import org.tasktide.core.model.task.ItemTask;


/**
 * Model class for worklaod
 * 
 * @author bkenna
 */
@Entity
@Dependent
public class Workload {
    
    @Id
    @JsonbProperty("Id")
    private String id;
    
    @Column
    @JsonbProperty("Workload")
    private List<ItemTask> workload;
    
    @Column
    @JsonbProperty("Workload State")
    private ItemState workloadState;
    
    @Column
    @JsonbProperty("Workload Type")
    private ItemType workloadType;
    
    
    /**
     * Null constructor
     */
    public Workload() {
        this.workload = new ArrayList<>();
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
        @JsonbProperty("Workload") List<ItemTask> workload,
        @JsonbProperty("Workload State") ItemState workloadState,
        @JsonbProperty("Workload Type") ItemType workloadType
    ) {
        this.id = id;
        this.workload = workload;
        this.workloadState = workloadState;
        this.workloadType = workloadType;
    }

    
    /**
     * Fetch index of work item from workload
     * 
     * @param query
     * @return int
     */
    public int indexOf(ItemTask query) {
        return this.workload.indexOf(query);
    }
    
    
    /**
     * Fetch index query
     * 
     * @param query
     * @return int
     */
    public int indexOf(String query) {
        
        // Initialize vars
        int workIndex = -1;
        int counter = 0;
        boolean found = false;
        
        // Search until found
        while ( !found && counter < this.workload.size() ) {
            ItemTask task = this.workload.get(counter);
            if ( task.isTask(query) ) {
                found = true;
                workIndex = counter;
            }
            else {
                counter++;
            }
        }
        
        // Return result
        return workIndex;
    }
    
    
    /**
     * Add a new work item to workload
     * 
     * @param work
     * @return boolean
     */
    public boolean addTask(ItemTask work) {
        
        // Handle whether to add task
        int workInd = indexOf(work);
        if ( workInd > -1 ) {
            return false;
        }
        
        // Otherwise add task
        this.workload.add(work);
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
     * @param task
     * @return ItemTask
     */
    public ItemTask getTask(String task) {
        int index = this.indexOf(task);
        if ( index > -1 ) {
            return this.workload.get(index);
        }
        else {
            return null;
        }
    }
    
    
    /**
     * Drop named task if present in workload
     * 
     * @param task
     * @return boolean
     */
    public boolean dropTask(String task) {
    
        // Handle whether to add task
        int workIndex = indexOf(task);
        if ( workIndex < 0) {
            return false;
        }
        
        // Add task
        this.workload.remove(workIndex);
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
        int taskInd = this.workload.indexOf(task);
        if ( taskInd < 0 ) {
            return false;
        }
        
        // Drop task
        this.workload.remove(taskInd);
        
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
     * @return List-ItemTask
     */
    public List<ItemTask> getWorkload() {
        return workload;
    }

    
    /**
     * Set workload
     * 
     * @param workload 
     */
    public void setWorkload(List<ItemTask> workload) {
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
