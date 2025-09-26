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
package org.tasktide.core.model.workitem;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbConfig;

import jakarta.json.bind.annotation.JsonbCreator;
import jakarta.json.bind.annotation.JsonbProperty;

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
@jakarta.nosql.Embeddable
@jakarta.persistence.Embeddable
public class Workload {
    
    @jakarta.nosql.Column("WorkloadId")
    @jakarta.persistence.Column(name = "WorkloadId")
    @JsonbProperty("WorkloadId")
    private String workloadId;
    
    @jakarta.nosql.Column("TaskMap")
    @jakarta.persistence.Column(name = "TaskMap", columnDefinition = "BLOB")
    @jakarta.persistence.Convert(converter = WorkloadJpaConverter.class)
    @JsonbProperty("TaskMap")
    private Map<String, ItemTask> taskMap;
    
    @jakarta.nosql.Column("WorkloadType")
    @jakarta.persistence.Enumerated(jakarta.persistence.EnumType.STRING)
    @JsonbProperty("WorkloadType")
    private ItemType workloadType;
    
    
    /**
     * Null constructor
     */
    public Workload() {
        this.taskMap = new HashMap<>();
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
        @JsonbProperty("WorkloadId") String workloadId,
        @JsonbProperty("Workload") Map<String, ItemTask> workload,
        @JsonbProperty("WorkloadType") ItemType workloadType
    ) {
        this.workloadId = workloadId;
        this.taskMap = workload;
        this.workloadType = workloadType;
    }

    
    /**
     * Reset ItemTasks
     * 
     */
    public void resetModel() {
        for ( ItemTask elm : taskMap.values() ) {
            elm.resetModel();
        }
    }
    
    
    /**
     * Unlock provided task
     * 
     * @param id 
     */
    public void resetTask(String id) {
        ItemTask task = this.getById(id);
        if ( task != null ) {
            task.resetModel();
        }
    }
    
    
    /**
     * Fetch {@link ItemTask} having Id
     * 
     * @param id
     * @return {@link ItemTask}
     */
    public ItemTask getById(String id) {
        for ( ItemTask elm : taskMap.values() ) {
            if ( elm.getId().equals(id) ) {
                return elm;
            }
        }
        return null;
    }
    
    
    /**
     * Add a new work item to taskMap
     * 
     * @param work
     * @param taskName
     * @return boolean
     */
    public boolean addTask(String taskName, ItemTask work) {
        
        // Handle whether to add task
        if ( taskMap.containsKey(taskName) ) {
            return false;
        }
        
        // Otherwise add task
        this.taskMap.put(taskName, work);
        if ( this.taskMap.size() >= 2 ) {
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
        if ( taskMap.containsKey(task.getTaskName()) ) {
            return false;
        }
        
        // Otherwise add task
        this.taskMap.put(task.getTaskName(), task);
        if ( this.taskMap.size() >= 2 ) {
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
        return taskMap.get(taskName);
    }
    
    
    /**
     * Drop named task if present in taskMap
     * 
     * @param taskName
     * @return boolean
     */
    public boolean dropTask(String taskName) {
    
        // Handle whether to add task
        if ( !this.taskMap.containsKey(taskName) ) {
            return false;
        }
        
        // Add task
        this.taskMap.remove(taskName);
        if ( this.taskMap.size() >= 2 ) {
            this.workloadType = ItemType.NESTED;
        }
        else {
            this.workloadType = ItemType.NESTED;
        }
        
        // Return flag
        return true;
    }
    
    
    /**
     * Drop task by Id if present in taskMap
     * 
     * @param taskId
     * @return boolean
     */
    public boolean dropTaskById(String taskId) {
    
        // Handle whether to add task
        ItemTask task = this.getById(taskId);
        if ( task == null ) {
            return false;
        }
        
        // Add task
        return this.dropTask(task);
    }
    
    
    /**
     * Drop provided task if present from taskMap
     * 
     * @param task
     * @return boolean
     */
    public boolean dropTask(ItemTask task) {
        
        // Handle whether to drop task
        if ( !this.taskMap.containsKey(task.getTaskName()) ) {
            return false;
        }
        
        // Drop task
        this.taskMap.remove(task.getTaskName());
        
        // Handle task type
        if ( this.taskMap.size() >= 2 ) {
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
            for ( ItemTask task : this.taskMap.values() ) {
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
            for ( ItemTask task : this.taskMap.values() ) {
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
        for ( ItemTask task : taskMap.values() ) {
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
        for ( ItemTask task : taskMap.values() ) {
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
     * Get taskMap size
     * 
     * @return int
     */
    public int getWorkloadSize() {
        return this.taskMap.size();
    }
    
    
    /**
     * Get taskMap Id
     * 
     * @return String
     */
    public String getId() {
        return this.workloadId;
    }

    
    /**
     * Set taskMap Id
     * 
     * @param workloadId 
     */
    public void setId(String workloadId) {
        this.workloadId = workloadId;
    }

    
    /**
     * Get taskMap
     * 
     * @return Map-TaskName, {@link ItemTask}
     */
    public Map<String, ItemTask> getTaskMap() {
        return taskMap;
    }

    
    /**
     * Set taskMap
     * 
     * @param taskMap 
     */
    public void setTaskMap(Map<String, ItemTask> taskMap) {
        this.taskMap = taskMap;
        if( this.taskMap.size() >= 2 ) {
            this.workloadType = ItemType.NESTED;
        }
        else {
            this.workloadType = ItemType.SINGLE;
        }
    }

    
    /**
     * Get taskMap type
     * 
     * @return {@link ItemType}
     */
    public ItemType getWorkloadType() {
        return workloadType;
    }

    
    /**
     * Set taskMap type
     * 
     * @param workloadType 
     */
    public void setWorkloadType(ItemType workloadType) {
        this.workloadType = workloadType;
    }

    
    /**
     * Represent as string
     * 
     * @return String
     */
    @Override
    public String toString() {
        return "Workload{" +
            "workloadId=" + workloadId +
            ", taskMap=" + taskMap +
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