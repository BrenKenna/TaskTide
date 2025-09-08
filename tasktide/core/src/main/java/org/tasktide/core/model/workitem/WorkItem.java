/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.tasktide.core.model.workitem;

import org.tasktide.core.model.CustomAnnotation;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbConfig;

import jakarta.json.bind.annotation.JsonbCreator;
import jakarta.json.bind.annotation.JsonbProperty;
import jakarta.json.bind.annotation.JsonbTransient;

import java.lang.reflect.Field;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.tasktide.core.model.task.ItemTask;
import org.tasktide.core.model.task.TaskState;
import org.tasktide.core.TaskTideModel;


/**
 * TaskTideModel class for Work Items
 * 
 * @author bkenna
 */
@jakarta.nosql.Entity("WorkItem")
@jakarta.persistence.Entity
@jakarta.persistence.Table(name = "WorkItem")
public class WorkItem implements TaskTideModel<WorkItem> {
    
    @jakarta.nosql.Id
    @jakarta.persistence.Id
    @JsonbProperty("Id")
    private String id;

    @jakarta.persistence.Column(name = "ItemName")
    @jakarta.nosql.Column("ItemName")
    @JsonbProperty("ItemName")
    private String itemName;
    
    @jakarta.persistence.Enumerated(jakarta.persistence.EnumType.STRING)
    @jakarta.nosql.Column("ItemType")
    @JsonbProperty("ItemType")
    private ItemType itemType;
    
    @jakarta.persistence.Enumerated(jakarta.persistence.EnumType.STRING)
    @jakarta.persistence.Column(name = "ItemState")
    @jakarta.nosql.Column("ItemState")
    @JsonbProperty("ItemState")
    private ItemState itemState;
    
    @jakarta.persistence.Column(name = "LockId")
    @jakarta.nosql.Column("LockId")
    @JsonbProperty("LockId")
    private String lockId;
    
    @jakarta.persistence.Column(name = "LockDate")
    @jakarta.nosql.Column("LockDate")
    @JsonbProperty("LockDate")
    private long lockDate;
    
    @jakarta.persistence.Column(name = "DoneDate")
    @jakarta.nosql.Column("DoneDate")
    @JsonbProperty("DoneDate")
    private long doneDate;
    
    @jakarta.persistence.Column(name = "TaskCount")
    @jakarta.nosql.Column("TaskCount")
    @JsonbProperty("TaskCount")
    private int taskCount;
    
    @jakarta.persistence.Column(name = "TaskDone")
    @jakarta.nosql.Column("TaskDone")
    @JsonbProperty("TaskDone")
    private int taskDone;
    
    @jakarta.nosql.Column("Workload")
    @jakarta.persistence.Embedded
    @JsonbProperty("Workload")
    private Workload workload;
    
    @jakarta.persistence.Column(name = "StepName")
    @jakarta.nosql.Column("StepName")
    @JsonbProperty("StepName")
    private String stepName;
    
    @jakarta.nosql.Column("StepId")
    @jakarta.persistence.Column(name = "StepId")
    @JsonbProperty("StepId")
    private String stepId;
    
    @jakarta.nosql.Column("Annotations")
    @jakarta.persistence.Column(name = "Annotations")
    @JsonbProperty("Annotations")
    private CustomAnnotation anno;
    
    
    /**
     * Null constructor
     */
    public WorkItem(){}

    
    /**
     * Construct with fields
     * 
     * @param id
     * @param itemName
     * @param itemType
     * @param itemState
     * @param lockId
     * @param lockDate
     * @param doneDate
     * @param taskCount
     * @param taskDone
     * @param workload
     * @param stepName 
     */
    public WorkItem(
        String id,
        String itemName,
        ItemType itemType,
        ItemState itemState,
        String lockId,
        long lockDate,
        long doneDate,
        int taskCount,
        int taskDone,
        Workload workload,
        String stepName
    ) {
        this.id = id;
        this.itemName = itemName;
        this.itemType = itemType;
        this.itemState = itemState;
        this.lockId = lockId;
        this.lockDate = lockDate;
        this.doneDate = doneDate;
        this.taskCount = taskCount;
        this.taskDone = taskDone;
        this.workload = workload;
        this.stepName = stepName;
    }
    
    
    /**
     * Constructor for JSON Deserialization
     * 
     * @param id
     * @param itemName
     * @param itemType
     * @param itemState
     * @param lockId
     * @param lockDate
     * @param doneDate
     * @param taskCount
     * @param taskDone
     * @param workload 
     * @param stepName
     * @param stepId
     */
    @JsonbCreator
    public WorkItem(
        @JsonbProperty("Id") String id,
        @JsonbProperty("Item Name") String itemName,
        @JsonbProperty("Item Type") ItemType itemType,
        @JsonbProperty("Item State") ItemState itemState,
        @JsonbProperty("Lock Id") String lockId,
        @JsonbProperty("Lock Date") long lockDate,
        @JsonbProperty("Done Date") long doneDate,
        @JsonbProperty("Task Count") int taskCount,
        @JsonbProperty("Task Done") int taskDone,
        @JsonbProperty("Workload") Workload workload,
        @JsonbProperty("Step Name") String stepName,
        @JsonbProperty("Step Id") String stepId
    ) {
        this.id = id;
        this.itemName = itemName;
        this.itemType = itemType;
        this.itemState = itemState;
        this.lockId = lockId;
        this.lockDate = lockDate;
        this.doneDate = doneDate;
        this.taskCount = taskCount;
        this.taskDone = taskDone;
        this.workload = workload;
        this.stepName = stepName;
        this.stepId = stepId;
    }
    
    
    /**
     * Reset model
     */
    @Override
    public void resetModel() {
        this.itemState = ItemState.TODO;
        this.lockDate = 0l;
        this.lockId = "";
        this.taskDone = 0;
        this.workload.resetModel();
        this.setTaskCounts();
    }
    
    
    /**
     * Unlock {@link ItemTask} associated with provided Id
     * 
     * @param id 
     */
    public void resetTask(String id) {
        this.itemState = ItemState.TODO;
        this.lockDate = 0L;
        this.lockId = "";
        this.workload.resetTask(id);
        this.setTaskCounts();
    }
    
    
    /**
     * Add task
     * 
     * @param task
     * @return boolean
     */
    public boolean addTask(ItemTask task) {
        boolean output;
        if ( workload.addTask(task) ) {
            taskCount++;
            output = true;
        }
        else {
            output = false;
        }
        
        this.setTaskCounts();
        return output;
    }
    
    
    /**
     * Drop task
     * 
     * @param task
     * @return boolean
     */
    public boolean dropTask(ItemTask task) {
        boolean output;
        if ( workload.dropTask(task) ) {
            taskCount--;
            output = true;
        }
        else {
            output = false;
        }
        
        this.setTaskCounts();
        return output;
    }
    
    
    /**
     * Get workload size
     * 
     * @return int
     */
    public int getWorkloadSize() {
        return this.getWorkload().getWorkloadSize();
    }
    
    
    /**
     * Summarize {@link ItemTask} by {@link ItemState}
     * 
     * @return Map-{@link ItemState}, Integer
     */
    public Map<ItemState, Integer> summarizeByState() {
    
        // Initialize vars
        Map<ItemState, Integer> results = new HashMap<>();
        Map<TaskState, Integer> counts = this.workload.summarizeWorkload();
        
        // Map to results
        for ( Entry<TaskState, Integer> elm : counts.entrySet() ) {
            results.put(elm.getKey().mapToItemState(), elm.getValue());
        }
        
        // Return results
        return results;
    }
    
    
    /**
     * Fetch {@link ItemTask} by their {@link ItemState}
     * 
     * @return Map-{@link ItemState}, List-{@link ItemTask}
     */
    public Map<ItemState, List<ItemTask>> fetchByStates() {
    
        // Initialize vars
        Map<ItemState, List<ItemTask>> results = new HashMap<>();
        Map<TaskState, List<ItemTask>> data = this.workload.fetchByState();
        
        // Map to results
        for ( Entry<TaskState, List<ItemTask>> elm : data.entrySet() ) {
            results.put(elm.getKey().mapToItemState(), elm.getValue());
        }
        
        // Return results
        return results;
    }
    
    
    /**
     * Set task count fields
     * 
     */
    public void setTaskCounts() {
        this.setTaskCount();
        this.setTaskDone();
        if ( this.taskCount == this.taskDone ) {
            this.itemState = ItemState.DONE;
        }
        this.itemState = ItemState.TODO;
        this.lockDate = 0L;
        this.lockId = "";
    }
    
    
    /**
     * Get work item Id
     * 
     * @return String
     */
    @Override
    public String getId() {
        return id;
    }

    
    /**
     * Set work item id
     * 
     * @param id 
     */
    public void setId(String id) {
        this.id = id;
    }

    
    /**
     * Get item name
     * 
     * @return String
     */
    public String getItemName() {
        return itemName;
    }

    
    /**
     * Set item name
     * 
     * @param itemName 
     */
    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    
    /**
     * Get annotations
     * 
     * @return {@link CustomAnnotation}
     */
    public CustomAnnotation getAnntations() {
        return this.anno;
    }
    
    
    /**
     * Set annotation provided
     * 
     * @param anno 
     */
    public void setAnnotations(CustomAnnotation anno) {
        this.anno = anno;
    }
    
    
    /**
     * Get item type
     * 
     * @return {@link ItemType}
     */
    public ItemType getItemType() {
        return itemType;
    }

    
    /**
     * Set item type
     * 
     * @param itemType 
     */
    public void setItemType(ItemType itemType) {
        this.itemType = itemType;
    }
    
    
    /**
     * Get item state
     * 
     * @return {@link ItemState}
     */
    public ItemState getItemState() {
        return itemState;
    }

    
    /**
     * Set item state
     * 
     * @param itemState 
     */
    public void setItemState(ItemState itemState) {
        this.itemState = itemState;
    }

    
    /**
     * Get lock Id
     * 
     * @return String
     */
    public String getLockId() {
        return lockId;
    }

    
    /**
     * Set lock Id
     * 
     * @param lockId 
     */
    public void setLockId(String lockId) {
        this.lockId = lockId;
    }

    
    /**
     * Get lock date
     * 
     * @return long
     */
    public long getLockDate() {
        return lockDate;
    }

    
    /**
     * Set lock date
     * 
     * @param lockDate 
     */
    public void setLockDate(long lockDate) {
        this.lockDate = lockDate;
    }

    
    /**
     * Get done date
     * 
     * @return long
     */
    public long getDoneDate() {
        return doneDate;
    }

    
    /**
     * Set done date
     * 
     * @param doneDate 
     */
    public void setDoneDate(long doneDate) {
        this.doneDate = doneDate;
    }

    
    /**
     * Get total task count
     * 
     * @return int
     */
    public int getTaskCount() {
        return taskCount;
    }

    
    /**
     * Set total task count
     * 
     * @param taskCount 
     */
    public void setTaskCount(int taskCount) {
        this.taskCount = taskCount;
    }

    
    /**
     * Set task count from workload size
     * 
     */
    public void setTaskCount() {
        this.taskCount = this.getWorkloadSize();
    }
    
    
    /**
     * Get task done count
     * 
     * @return int
     */
    public int getTaskDone() {
        return taskDone;
    }

    
    /**
     * Set task done count
     * 
     * @param taskDone 
     */
    public void setTaskDone(int taskDone) {
        this.taskDone = taskDone;
    }

    
    /**
     * Set task done count from summary
     */
    public void setTaskDone() {
        Map<ItemState, Integer> data = this.summarizeByState();
        this.taskDone = (int) data.get(ItemState.DONE);
    }
    
    
    /**
     * Get workload
     * 
     * @return {@link Workload}
     */
    public Workload getWorkload() {
        return workload;
    }

    
    /**
     * Set workload
     * 
     * @param workload 
     */
    public void setWorkload(Workload workload) {
        this.workload = workload;
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
     * Set the parent {@link Step} Id
     * @param stepId 
     */
    public void setStepId(String stepId) {
        this.stepId = stepId;
    }
    
    
    /**
     * Get the parent {@link StepId}
     * 
     * @return String
     */
    public String getStepId() {
        return this.stepId;
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
     * Get step
     * 
     * @return 
     */
    @Override
    public String getCollection() {
        return this.stepName;
    }
    
    
    /**
     * Represent work item as string
     * 
     * @return 
     */
    @Override
    public String toString() {
        return "WorkItem{" +
           "id=" + id +
           ", itemName=" + itemName +
           ", itemType=" + itemType +
           ", itemState=" + itemState +
           ", lockId=" + lockId +
           ", lockDate=" + lockDate +
           ", doneDate=" + doneDate +
           ", taskCount=" + taskCount +
           ", taskDone=" + taskDone +
           ", workload=" + workload +
           ", stepName=" + stepName +
           ", stepId=" + stepId +
           ", anno=" + anno +
        '}';
    }

   
    @JsonbTransient
    @Override
    public String getState() {
        return this.getItemState().toString();
    }
    
    /**
     * Represent as JSON doc
     * 
     * @return String
     */
    @Override
    public String toJson() {
        return this.toJsonDoc();
    }
    
    
    /**
     * Return value of field. Must match toString() names
     * 
     * @param field
     * @return Object
     */
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