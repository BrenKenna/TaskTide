/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package org.tasktide.core;

import java.util.List;


/**
 * 
 * Interface to facilitate model class mapping from
 * <br>
 * <ul>
 *  <li>{@link WorkflowService WorkflowSerivce} -> {@link Step Step}</li>
 *  <li>{@link StepService StepService} -> {@link WorkItem WorkItem}</li>
 *  <li>{@link WorkItemService WorkItemService} -> Does not link</li>
 * </ul>
 * 
 * @param <T> of {@link Workflow Workflow},{@link Step Step},{@link WorkItem WorkItem}
 * @param <U> of {@link Step Step},{@link WorkItem WorkItem},{@link Null Null}
 * 
 * @author bkenna
 */
public interface TaskTideMapper<T extends TaskTideModel, U extends TaskTideModel> {
    
    
    /**
     * Map {@link TaskTideModel} to its lower class via the {@link TaskTideService TaskTideService}
     * 
     * @param mappingServ 
     * @param model
     * @return List-{@link Step Step} from {@link Workflow Workflow}, {@link WorkItem WorkItem} from {@link Step Step}
     */
    List<U> getThroughLink(TaskTideService<U> mappingServ, T model);
}
