/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.tasktide.engine.observer.builders;

import java.util.List;

import org.tasktide.core.model.workitem.WorkItem;

import org.tasktide.engine.observer.TaskTideEngineObserver;
import org.tasktide.engine.observer.chain.WorkItemObserver;

import org.tasktide.engine.worker.tasktracker.TaskTracker;


/**
 * Class to the logic for build {@link TaskTideEngineObserver} of {@link WorkItemObserver}
 * 
 * @author bkenna
 */
public class WorkItemObserverBuilder {
    
    // Attributes
    private List<WorkItem> workload;
    private TaskTracker taskTracker;
    private int maxTime;
    
    
    /**
     * Build with {@link WorkItem} workload
     * 
     * @param workload
     * @return {@link WorkItemObserverBuilder}
     */
    public WorkItemObserverBuilder withWorkload(List<WorkItem> workload) {
        this.workload = workload;
        return this;
    }
    
    
    /**
     * Build with {@link TaskTracker}
     * 
     * @param taskTracker
     * @return {@link WorkItemObserverBuilder}
     */
    public WorkItemObserverBuilder withTaskTracker(TaskTracker taskTracker) {
        this.taskTracker = taskTracker;
        return this;
    }
    
    
    /**
     * Build with {@link TimeKeeper} max time
     * 
     * @param maxTime
     * @return {@link WorkItemObserverBuilder}
     */
    public WorkItemObserverBuilder withMaxTime(int maxTime) {
        this.maxTime = maxTime;
        return this;
    }
    
    
    /**
     * Build {@link WorkItem} {@link TaskTideEngineObserver}
     * 
     * @return {@link TaskTideEngineObserver} of {@link WorkItem}
     */
    public TaskTideEngineObserver<WorkItem> build() {
        return new WorkItemObserver(taskTracker, workload, maxTime);
    }
}