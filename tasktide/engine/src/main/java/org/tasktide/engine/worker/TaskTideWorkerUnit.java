/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package org.tasktide.engine.worker;

import org.tasktide.core.TaskTideModel;


/**
 * Marker interface for TaskTide processing
 * 
 * @author bkenna
 * @param <T> of {@link TaskTideModel}-{@link WorkItem},{@link ItemTask}
 */
public interface TaskTideWorkerUnit<T extends TaskTideModel<T>> {
    
}
