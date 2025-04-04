/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.tasktide.core.model.builders;

import java.util.List;

import org.tasktide.core.model.task.ItemTask;
import org.tasktide.core.model.workitem.ItemState;
import org.tasktide.core.model.workitem.ItemType;
import org.tasktide.core.model.workitem.Workload;


/**
 *
 * Allow ProcessLog objects to be built where all fields are optional
 * 
 * @author bkenna
 */
public class WorkloadBuilder extends ModelBuilder {
    
    // Attributes
    private String id;
    private List<ItemTask> workload;
    private ItemState workloadState;
    private ItemType workloadType;
    
    
    public WorkloadBuilder() {
        super();
    }
    
    
    /**
     * Add Id field
     * 
     * @param id
     * @return WorkloadBuilder 
     */
    public WorkloadBuilder id(String id) {
        this.id = id;
        return this;
    }
    
    
    /**
     * Add workload field
     * 
     * @param workload
     * @return WorkloadBuilder
     */
    public WorkloadBuilder workload(List<ItemTask> workload) {
        this.workload = workload;
        return this;
    }
    
    
    /**
     * Add workload state field
     * 
     * @param workloadState
     * @return WorkloadBuilder
     */
    public WorkloadBuilder workloadState(ItemState workloadState) {
        this.workloadState = workloadState;
        return this;
    }
    
    
    /**
     * Add workload type field
     * 
     * @param workloadType
     * @return WorkloadBuilder
     */
    public WorkloadBuilder workloadType(ItemType workloadType) {
        this.workloadType = workloadType;
        return this;
    }
    
    
    /**
     * Build workload from provided fields
     * 
     * @return Workload
     */
    @Override
    public Workload build() {
        return new Workload(id, workload, workloadState, workloadType);
    }
}
