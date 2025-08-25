/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.tasktide.core.manager;


import org.tasktide.core.manager.command.ManagerTarget;
import org.tasktide.core.TaskTideService;
import org.tasktide.core.model.collection.Step;
import org.tasktide.core.model.collection.Workflow;
import org.tasktide.core.model.workitem.WorkItem;

import org.tasktide.core.services.WorkItemService;
import org.tasktide.core.services.StepService;
import org.tasktide.core.services.WorkflowService;


/**
 * Container class {@link TaskTideService} for {@link Workflow},
 *   {@link Step}, and {@link Workflow}
 *
 * @author bkenna
 */
public final class TaskTideServiceManager {
    
    // There can be only one
    private static volatile TaskTideServiceManager INSTANCE;
    
    // Attributes
    private final TaskTideService<WorkItem> workItemServ;
    private final TaskTideService<Step> stepServ;
    private final TaskTideService<Workflow> workflowServ;
    
    
    /**
     * Package private construction with the {@link TaskTideService}
     * 
     * @param workItemServ
     * @param stepServ
     * @param workflowServ
     */
    private TaskTideServiceManager(
        TaskTideService<WorkItem> workItemServ,
        TaskTideService<Step> stepServ,
        TaskTideService<Workflow> workflowServ
    ) {
        this.workItemServ = workItemServ;
        this.stepServ = stepServ;
        this.workflowServ = workflowServ;
    }

    /**
     * Returns whether the service manager is initialized
     * 
     * @return boolean
     */
    public static boolean isInitialized() {
        return INSTANCE != null;
    }
    
    
    /**
     * Initialize service manager, throws error if already initialized
     * 
     * @param workItemServ
     * @param stepServ
     * @param workflowServ 
     */
    public static synchronized void initialize(TaskTideService<WorkItem> workItemServ,
        TaskTideService<Step> stepServ,
        TaskTideService<Workflow> workflowServ
    ) {
        if ( INSTANCE != null ) {
            throw new IllegalStateException("TaskTideServiceManager already initialized");
        }
        INSTANCE = new TaskTideServiceManager(workItemServ, stepServ, workflowServ);
    }
    

    /**
     * Fetches {@link WorkItemService} if initialized
     * 
     * @return {@link TaskTideService}-{@link WorkItem}
     */
    public static TaskTideService<WorkItem> fetchWorkItemService() {
        if ( INSTANCE != null ) {
            return INSTANCE.getWorkItemService();
        }
        throw new IllegalStateException("TaskTideServiceManager must be initialized first");
    }
    
    
    /**
     * Fetches {@link StepService} if initialized
     * 
     * @return {@link TaskTideService}-{@link Step}
     */
    public static TaskTideService<Step> fetchStepService() {
        if ( INSTANCE != null ) {
            return INSTANCE.getStepService();
        }
        throw new IllegalStateException("TaskTideServiceManager must be initialized first");
    }
    
    
    /**
     * Fetches {@link WorkflowService} if initialized
     * 
     * @return {@link TaskTideService}-{@link Workflow}
     */
    public static TaskTideService<Workflow> fetchWorkflowService() {
        if ( INSTANCE != null ) {
            return INSTANCE.getWorkflowService();
        }
        throw new IllegalStateException("TaskTideServiceManager must be initialized first");
    }
    
    
    /**
     * Get {@link TaskTideService} mapping to {@link ManagerTarget}
     * 
     * @param tgt
     * @return TaskTideService
     */
    public static TaskTideService getService(ManagerTarget tgt) {
        switch (tgt) {
            case WORKITEM -> {
                return fetchWorkItemService();
            }
            
            case STEP -> {
                return fetchStepService();
            }
            
            case WORKFLOW -> {
                return fetchWorkflowService();
            }
            
            default -> {
                String msg = String.format("Error target must be on of:\t'%s'", ManagerTarget.valuesString());
                throw new IllegalArgumentException(msg);
            }
        }
    }
    
    
    /**
     * Get {@link WorkItemService}
     * 
     * @return {@link TaskTideService} of {@link WorkItem}
     */
    public TaskTideService<WorkItem> getWorkItemService() {
        return this.workItemServ;
    }
    
    
    /**
     * Get {@link StepService}
     * 
     * @return {@link TaskTideService} of {@link Step}
     */
    public TaskTideService<Step> getStepService() {
        return this.stepServ;
    }
    
    
    /**
     * Get {@link WorkflowService}
     * 
     * @return {@link TaskTideService} of {@link Workflow}
     */
    public TaskTideService<Workflow> getWorkflowService() {
        return this.workflowServ;
    }
}