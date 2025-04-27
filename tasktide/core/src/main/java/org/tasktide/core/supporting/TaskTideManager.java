/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.tasktide.core.supporting;

import org.tasktide.core.supporting.generator.ManagerTask;
import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import java.util.HashMap;
import java.util.Map;

import org.tasktide.core.TaskTideModelType;
import org.tasktide.core.TaskTideService;

import org.tasktide.core.services.WorkItemService;

import org.tasktide.core.model.builders.ModelBuilderProvider;
import org.tasktide.core.model.builders.WorkItemBuilder;

import org.tasktide.core.model.collection.Workflow;
import org.tasktide.core.model.collection.Step;
import org.tasktide.core.model.task.ItemTask;
import org.tasktide.core.model.workitem.WorkItem;

import org.tasktide.manager.model.manager_task.ManagerTask;


/**
 *
 * @author bkenna
 */
@Dependent
public class TaskTideManager {
    
    // Attributes
    private final Map<TaskTideModelType, TaskTideService> map = new HashMap<>();
    private final ModelBuilderProvider modelBuilder = new ModelBuilderProvider();

    
    
    /**
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
    
    
    
    public void importTask(ManagerTask task) {
        WorkItemService workItemService = (WorkItemService) map.get(TaskTideModelType.WORKITEM);
        WorkItemBuilder builder = (WorkItemBuilder) modelBuilder.getBuilder(TaskTideModelType.WORKITEM);
        ItemTask work = task.asItemTask();
        WorkItem item = builder
                        .workload(work)
                        .itemName(work.getTaskName())
                        .build();
        
    }
}
