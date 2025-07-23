/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.tasktide.core.services;

import org.tasktide.core.TaskTideRepository;
import org.tasktide.core.TaskTideService;

import org.tasktide.core.model.collection.Step;
import org.tasktide.core.model.collection.Workflow;
import org.tasktide.core.model.workitem.WorkItem;

import org.tasktide.core.repository.RepositoryType;


/**
 * Class to create {@link TaskTideService}
 * 
 * @author bkenna
 */
public class ServiceFactory {
    
    
    /**
     * Make required {@link TaskTideService}
     * 
     * @param repoType
     * @param backend
     * @param collectionName
     * @param lockWait
     * @param utilDate
     * @return {@link WorkItemService}
     */
    public static TaskTideService<WorkItem> makeWorkItemService(RepositoryType repoType, Object backend, String collectionName, int lockWait, String utilDate) {
        TaskTideRepository<WorkItem> repo = repoType.createRepository(WorkItem.class, backend, collectionName);
        return new WorkItemService(repo, lockWait, utilDate);
    }
    
    
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
     * Make {@link WorkItemService}
     * 
     * @param repo
     * 
     * @return {@link WorkItemService}
     */
    public static WorkItemService makeWorkItemService(TaskTideRepository<WorkItem> repo) {
        return new WorkItemService(repo);
    }
    
    
    /**
     * Make required {@link WorkItem}
     * 
     * @param repoType
     * @param backend
     * @param collectionName
     * @return {@link TaskTideService}
     */
    public static TaskTideService<WorkItem> makeWorkItemService(RepositoryType repoType, Object backend, String collectionName) {
        TaskTideRepository<WorkItem> repo = repoType.createRepository(WorkItem.class, backend, collectionName);
        return new WorkItemService(repo);
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
     * Make required {@link StepService}
     * 
     * @param repoType
     * @param backend
     * @param collectionName
     * @return {@link TaskTideService}
     */
    public static TaskTideService<Step> makeStepService(RepositoryType repoType, Object backend, String collectionName) {
        TaskTideRepository<Step> repo = repoType.createRepository(Step.class, backend, collectionName);
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
    
    
    /**
     * Make required {@link StepService}
     * 
     * @param repoType
     * @param backend
     * @param collectionName
     * @return {@link TaskTideService}
     */
    public static TaskTideService<Workflow> makeWorkflowService(RepositoryType repoType, Object backend, String collectionName) {
        TaskTideRepository<Workflow> repo = repoType.createRepository(Workflow.class, backend, collectionName);
        return new WorkflowService(repo);
    }
}