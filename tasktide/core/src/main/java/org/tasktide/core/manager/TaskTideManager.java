/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.tasktide.core.manager;

import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;

import java.util.HashMap;
import java.util.Map;

import org.tasktide.core.TaskTideService;
import org.tasktide.core.services.WorkItemService;

import org.tasktide.core.model.builders.ModelBuilderProvider;
import org.tasktide.core.model.builders.WorkItemBuilder;

import org.tasktide.core.TaskTideModelType;
import org.tasktide.core.model.collection.Workflow;
import org.tasktide.core.model.collection.Step;
import org.tasktide.core.model.workitem.WorkItem;
import org.tasktide.core.model.task.ItemTask;


/**
 * 
 *
 * @author bkenna
 */
@Dependent
public class TaskTideManager {
    
    // Attributes
    private final Map<TaskTideModelType, TaskTideService> map = new HashMap<>();
    
    /**
     * Construct with the {@link TaskTideService TaskTideServices}
     * 
     * @param workflowServ
     * @param stepServ
     * @param workItemServ 
     */
    @Inject
    public TaskTideManager(
        TaskTideService<Workflow> workflowServ,
        TaskTideService<Step> stepServ,
        TaskTideService<WorkItem> workItemServ
    ) {
        map.put(TaskTideModelType.WORKITEM, workflowServ);
        map.put(TaskTideModelType.STEP, stepServ);
        map.put(TaskTideModelType.WORKFLOW, workflowServ);
    }

    
    /**
     * Get {@link TaskTideService TaskTideService}
     * 
     * @param query
     * @return {@link TaskTideService TaskTideService}
     */
    public TaskTideService getService(TaskTideModelType query) {
        return map.get(query);
    }
    
    
    /**
     * Get the {@link TaskTideService TaskTideService} matching query string
     * 
     * @param query
     * @return {@link TaskTideService TaskTideService}
     */
    public TaskTideService getService(String query) {
        TaskTideModelType modelType = TaskTideModelType.getQuery(query);
        if ( modelType != null ) {
            return map.get(modelType);
        }
        else {
            return null;
        }
    } 
}
