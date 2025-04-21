/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.tasktide.core.model.collection;

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

import org.tasktide.core.model.workitem.WorkItem;


/**
 *
 * Model class for storing a collection of work items
 *      => This could be an internal class?
 * 
 * @author bkenna
 */
@Entity
@Dependent
public class WorkItemCollection {
    
    @Id
    @JsonbProperty("Step Id")
    private String id;
    
    @Column
    @JsonbProperty("Step Name")
    private String stepName;
    
    @Column
    @JsonbProperty("Work Items")
    private List<WorkItem> workItems;
    
    @Column
    @JsonbProperty("Items Done")
    private int itemsDone;
    
    @Column
    @JsonbProperty("Items Locked")
    private int itemsLocked;
    
    @Column
    @JsonbProperty("Item Count")
    private int itemCount;
    
    
    /**
     * Null constructor
     */
    public WorkItemCollection() {
        this.workItems = new ArrayList<>();
    }
    
    
    /**
     * Construct with data
     * 
     * @param workItems 
     */
    public WorkItemCollection(List<WorkItem> workItems) {
        this.workItems = workItems;
    }
    
    
    /**
     * Constructor for JSON deserialization
     * 
     * @param stepId
     * @param stepName
     * @param workItems
     * @param itemsDone
     * @param itemsLocked
     * @param itemCount 
     */
    @JsonbCreator
    public WorkItemCollection(
        @JsonbProperty("Step Id") String stepId,
        @JsonbProperty("Step Name") String stepName,
        @JsonbProperty("Work Items") List<WorkItem> workItems,
        @JsonbProperty("Items Done") int itemsDone,
        @JsonbProperty("Items Locked") int itemsLocked,
        @JsonbProperty("Item Count") int itemCount
    ) {
        this.id = stepId;
        this.stepName = stepName;
        this.workItems = workItems;
        this.itemsDone = itemsDone;
        this.itemsLocked = itemsLocked;
        this.itemCount = itemCount;
    }

    
    /**
     * Add a work item
     * 
     * @param item 
     */
    public void addItem(WorkItem item) {
        this.workItems.add(item);
        handleCount();
    }
    
    
    /**
     * Pop an item if available
     * 
     * @return WorkItem
     */
    public WorkItem popItem() {
        if ( !this.workItems.isEmpty() ) {
            return this.workItems.remove(0);
        }
        else {
            return null;
        }
    }
    
    
    /**
     * Check if there are work items in list
     * 
     * @return boolean
     */
    public boolean hasNext() {
        return !this.workItems.isEmpty();
    }
    
    
    /**
     * Handle item count
     * 
     */
    public void handleCount() {
        this.itemCount = workItems.size();
    }
    
    
    /**
     * Get step Id
     * 
     * @return String
     */
    public String getId() {
        return id;
    }

    
    /**
     * Set step Id
     * 
     * @param id 
     */
    public void setId(String id) {
        this.id = id;
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
     * Get work items
     * 
     * @return List-WorkItem
     */
    public List<WorkItem> getWorkItems() {
        return workItems;
    }

    
    /**
     * Set work items
     * 
     * @param workItems 
     */
    public void setWorkItems(List<WorkItem> workItems) {
        this.workItems = workItems;
    }

    
    /**
     * Get count of completed items
     * 
     * @return int
     */
    public int getItemsDone() {
        return itemsDone;
    }

    
    /**
     * Set count of items done
     * 
     * @param itemsDone 
     */
    public void setItemsDone(int itemsDone) {
        this.itemsDone = itemsDone;
    }

    
    /**
     * Get count of items locked
     * 
     * @return int
     */
    public int getItemsLocked() {
        return itemsLocked;
    }

    
    /**
     * Set count of items locked
     * 
     * @param itemsLocked 
     */
    public void setItemsLocked(int itemsLocked) {
        this.itemsLocked = itemsLocked;
    }

    
    /**
     * Get count of items
     * 
     * @return int
     */
    public int getItemCount() {
        return itemCount;
    }

    
    /**
     * Set count of items
     * 
     * @param itemCount 
     */
    public void setItemCount(int itemCount) {
        this.itemCount = itemCount;
    }

    
    /**
     * Represent as string
     * 
     * @return String
     */
    @Override
    public String toString() {
        return "WorkItemCollection{" +
            "id=" + id +
            ", stepName=" + stepName +
            ", workItems=" + workItems +
            ", itemsDone=" + itemsDone +
            ", itemsLocked=" + itemsLocked +
            ", itemCount=" + itemCount +
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
