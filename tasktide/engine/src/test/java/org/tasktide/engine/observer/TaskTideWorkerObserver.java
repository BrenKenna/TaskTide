/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package org.tasktide.engine.observer;

import java.util.List;
import org.tasktide.core.TaskTideModel;
import org.tasktide.engine.worker.TaskTideWorkerUnit;
import org.tasktide.engine.worker.processor.TaskTideProcessor;


/**
 * Observer interface for observing {@link Workitem}, and {@link ItemTask} execution
 * 
 * @param <T> of {@link TaskTideModel}-{@link Workitem}, {@link ItemTask}
 * 
 * @author bkenna
 */
public interface TaskTideWorkerObserver<T extends TaskTideModel<T>> extends TaskTideWorkerUnit<T> {
    
    
    /**
     * Defines action to take on starting task execution
     * 
     * @param task
     * @return boolean
     */
    boolean onTaskStart(T task);
    
    
    /**
     * Define action to take when task completes
     * 
     * @param task
     * @return boolean
     */
    boolean onTaskEnd(T task);
    
    
    /**
     * Defines actions to take on subtasking by the {@link TaskTideProcessor}
     * 
     * @param subList 
     * @return boolean
     */
    boolean onSubTasking(List<T> subList);
}
