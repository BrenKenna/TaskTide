/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.tasktide.engine.observer.chain;

import java.util.List;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import org.tasktide.core.model.workitem.WorkItem;

import org.tasktide.engine.observer.ObserverChain;
import org.tasktide.engine.observer.TaskTideEngineObserver;
import org.tasktide.engine.observer.WorkerObserver;

import org.tasktide.engine.observer.worker.executor.WorkItemExecutorObserver;
import org.tasktide.engine.observer.worker.timekeeper.WorkItemTimeKeeper;
import org.tasktide.engine.observer.worker.stateobserver.WorkItemStateObserver;



/**
 * Coordinates the work across the {@link WorkerObserver}s for the processing of {@link WorkItem}
 *  through the {@link TaskTideEngineObserver} chain for {@link WorkItem}s
 * 
 * @author bkenna
 */
public class WorkItemObserver extends ObserverChain<WorkItem> {

    
    /**
     * Default constructor for simple test purposes
     */
    public WorkItemObserver() {
        super(
     List.of(
                new WorkItemTimeKeeper(100000),
                new WorkItemStateObserver(),
                new WorkItemExecutorObserver()
            )
        );
    }
    
    
    /**
     * Construct with required arguments
     * 
     * @param maxTime 
     */
    public WorkItemObserver(
        @ConfigProperty(name="task-tide.engine.observer.worker.timekeeper.workitem", defaultValue="100000") int maxTime
    ) {
        super(
            List.of(
                new WorkItemTimeKeeper(maxTime),
                new WorkItemStateObserver(),
                new WorkItemExecutorObserver()
            )
        );
    }
    
    
    /**
     * Construct with optional workload
     * 
     * @param workload
     * @param maxTime
     */
    public WorkItemObserver(
        List<WorkItem> workload,
        @ConfigProperty(name="task-tide.engine.observer.worker.timekeeper.workitem", defaultValue="100000") int maxTime
    ) {
        super(
            List.of(
                new WorkItemTimeKeeper(maxTime),
                new WorkItemStateObserver(workload),
                new WorkItemExecutorObserver()
            )
        );
    }

    @Override
    public List<WorkerObserver<WorkItem>> getObservers() {
        return this.observers;
    }

    @Override
    public WorkerObserver<WorkItem> getObserver(String query) {
        for ( WorkerObserver<WorkItem> obs : this.observers ) {
            if ( obs.getName().equals(query) ) {
                return obs;
            }
        }
        return null;
    }
}
