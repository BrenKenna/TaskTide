/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.tasktide.core.services;

import org.tasktide.core.TaskTideRepository;
import org.tasktide.core.model.collection.Step;
import org.tasktide.core.model.collection.Workflow;
import org.tasktide.core.model.workitem.WorkItem;


/**
 * Class to create {@link TaskTideService}
 * 
 * @author bkenna
 */
public class ServiceFactory {
    
    
    /**
     * Make {@link WorkItemService}
     * 
     * @param repo
     * @param lockWait
     * @param utilDate
     * @return {@link WorkItemService}
     */
    public static WorkItemService makeWorkItemService(TaskTideRepository<WorkItem> repo, int lockWait, String utilDate) {
        return new WorkItemService(repo, lockWait, utilDate);
    }
    
    
    /**
     * Make {@link StepService}
     * 
     * @param repo
     * @return {@link StepService}
     */
    public static StepService makeStepService(TaskTideRepository<Step> repo) {
        return new StepService(repo);
    }
    
    
    /**
     * Make {@link WorkflowService}
     * 
     * @param repo
     * @return {@link WorkflowService}
     */
    public static WorkflowService makeWorkflowService(TaskTideRepository<Workflow> repo) {
        return new WorkflowService(repo);
    }
}
