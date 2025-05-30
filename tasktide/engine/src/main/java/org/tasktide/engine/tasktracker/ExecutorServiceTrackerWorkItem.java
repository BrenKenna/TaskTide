/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.tasktide.engine.tasktracker;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import org.tasktide.core.model.task.ItemTask;
import org.tasktide.core.model.workitem.WorkItem;


/**
 * Stores {@link Future} from {@link WorkItem}/{@link ItemTask} processing
 *  and what can be used for
 * 
 * @author bkenna
 */
public class ExecutorServiceTrackerWorkItem {
    
    // Map of task futures
    private static volatile ExecutorServiceTrackerWorkItem instance;
    private final ConcurrentMap<String, ExecutorServiceItem<WorkItem>> taskStates;
    
    
    private ExecutorServiceTrackerWorkItem() {
        this.taskStates = new ConcurrentHashMap<>();
    }
    
    
    public static synchronized ExecutorServiceTrackerWorkItem getInstance() {
        if ( instance == null ) {
            instance = new ExecutorServiceTrackerWorkItem();
        }
        return instance;
    }
    
    
    /**
     * Mark task with {@link ExecutorServiceItem}
     * 
     * @param taskId
     * @param item 
     */
    public void markTask(String taskId, ExecutorServiceItem<WorkItem> item) {
        taskStates.putIfAbsent(taskId, item);
    }
    
    
    /**
     * Fetch future of task
     * 
     * @param taskId
     * @return {@link ExecutorServiceItem}
     */
    public ExecutorServiceItem<WorkItem> get(String taskId) {
        return taskStates.get(taskId);
    }
    
    
    /**
     * Check whether task is completed
     * 
     * @param taskId
     * @return boolean
     */
    public boolean isDone(String taskId) {
        ExecutorServiceItem<WorkItem> item = taskStates.get(taskId);
        return item.getFuture().isDone();
    }
    
    
    /**
     * Check if task is still running or in prepare
     * 
     * @param taskId
     * @return boolean
     */
    public boolean isActive(String taskId) {
        ExecutorServiceItem<WorkItem> item = taskStates.get(taskId);
        return !item.getFuture().isDone() || !item.getFuture().isCancelled();
    }

    
    /**
     * Check if task is still done or cancelled
     * 
     * @param taskId
     * @return boolean
     */
    public boolean isComplete(String taskId) {
        ExecutorServiceItem<WorkItem>item = taskStates.get(taskId);
        return item.getFuture().isDone();
    }
    
        
    /**
     * Check whether task is present
     * 
     * @param taskId
     * @return boolean
     */
    public boolean isPresent(String taskId) {
        return taskStates.containsKey(taskId);
    }
    
    
    /**
     * Fetch count of tasks being tracked
     * 
     * @return int
     */
    public int taskCount() {
        return taskStates.size();
    }
    
    
    /**
     * Count active elements
     * 
     * @return int
     */
    public int countActive() {
        return (int) taskStates.keySet().stream()
            .filter(
                elm -> this.isActive(elm)
            )
        .count();
    }
    
    
    /**
     * Count done elements
     * 
     * @return int
     */
    public int countDone() {
        return (int) taskStates.keySet().stream()
            .filter(
                elm -> this.isDone(elm)
            )
        .count();
    }
    
    
    /**
     * Count done & terminated elements
     * 
     * @return int
     */
    public int countComplete() {
        return (int) taskStates.keySet().stream()
            .filter(
                elm -> this.isComplete(elm)
            )
        .count();
    }
    
    
    public Set<String> getIds() {
        return taskStates.keySet();
    }
    
    
    /**
     * String representation of tracked tasks and their states.
     * 
     * @return String
     */
    @Override
    public String toString() {
        return taskStates.entrySet()
            .stream()
            .sorted(Map.Entry.comparingByKey())
            .map(entry -> String.format("TaskId: %s, Is Done: %s", entry.getKey(), entry.getValue().getFuture().isDone()))
            .collect(Collectors.joining("\n"));
    }
}
