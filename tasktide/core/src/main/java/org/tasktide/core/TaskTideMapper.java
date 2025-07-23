/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package org.tasktide.core;

import java.util.List;

import org.tasktide.core.model.workitem.WorkItem;
import org.tasktide.core.model.collection.Step;
import org.tasktide.core.model.collection.Workflow;
import org.tasktide.core.model.task.ItemTask;

/**
 * 
 * Interface to facilitate model class mapping from
 * <br>
 * <ul>
 *  <li>{@link TaskTideService WorkflowService} -> {@link Step}</li>
 *  <li>{@link TaskTideService StepService} -> {@link WorkItem}</li>
 *  <li>{@link TaskTideService WorkItemService} -> {@link ItemTask}</li>
 * </ul>
 * 
 * @param <T> of {@link Workflow},{@link Step},{@link WorkItem}
 * @param <U> of {@link Step},{@link WorkItem},{@link ItemTask}
 * 
 * @author bkenna
 */
public interface TaskTideMapper<T extends TaskTideModel<T>, U extends TaskTideModel<U>> {
    
    
    /**
     * Map {@link TaskTideModel} to its lower class via the {@link TaskTideService}
     * 
     * @param mappingServ 
     * @param model
     * @return List-{@link Step} from {@link Workflow}, {@link WorkItem} from {@link Step}
     */
    List<U> getThroughLink(TaskTideService<U> mappingServ, T model);
}
