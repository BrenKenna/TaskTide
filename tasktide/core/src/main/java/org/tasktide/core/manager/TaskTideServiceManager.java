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
package org.tasktide.core.manager;

import org.tasktide.core.TaskTideModel;
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
     * @param <T> of {@link TaskTideModel}
     * @param tgt
     * @return TaskTideService of {@link WorkItem}, {@link Step}, or {@link Workflow}
     */
    @SuppressWarnings("unchecked")
    public static <T extends TaskTideModel<T>> TaskTideService<T> getService(ManagerTarget tgt) {
        switch (tgt) {
            case WORKITEM -> {
                TaskTideService<T> output = (TaskTideService<T>) fetchWorkItemService();
                return output;
            }
            
            case STEP -> {
                TaskTideService<T> output = (TaskTideService<T>) fetchStepService();
                return output;
            }
            
            case WORKFLOW -> {
                TaskTideService<T> output = (TaskTideService<T>) fetchWorkflowService();
                return output;
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