/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.tasktide.core.manager;

import org.tasktide.core.TaskTideService;

import org.tasktide.core.model.collection.Workflow;
import org.tasktide.core.model.collection.Step;
import org.tasktide.core.model.workitem.WorkItem;


/**
 * Container class {@link TaskTideService} for {@link Workflow},
 *   {@link Step}, and {@link Workflow}
 *
 * @author bkenna
 */
public class TaskTideServiceManager {
    
    
    // Attributes
    private final TaskTideService<WorkItem> workItemServ;
    private final TaskTideService<Step> stepServ;
    private final TaskTideService<Workflow> workflowServ;
    
    
    /**
     * Construct with the {@link TaskTideService TaskTideServices}
     * 
     * @param workItemServ
     * @param stepServ
     * @param workflowServ
     */
    // @Inject
    public TaskTideServiceManager(
        TaskTideService<WorkItem> workItemServ,
        TaskTideService<Step> stepServ,
        TaskTideService<Workflow> workflowServ
    ) {
        this.workItemServ = workItemServ;
        this.stepServ = stepServ;
        this.workflowServ = workflowServ;
    }

    
    
    public TaskTideService<WorkItem> getWorkItemService() {
        return this.workItemServ;
    }
    
    
    
    public TaskTideService<Step> getStepService() {
        return this.stepServ;
    }
    
    
    
    public TaskTideService<Workflow> getWorkflowService() {
        return this.workflowServ;
    }
}
