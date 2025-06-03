/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.tasktide.engine.tasktracker;

import java.util.Map;
import java.util.List;
import java.util.Set;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

import org.tasktide.core.TaskTideModel;


/**
 * Generic class to hold logic for tracking futures of {@link TaskTideModel}
 *  elements (ie {@link ItemTask}, {@link WorkItem}) as {@link ExecutorServiceItem}.
 * 
 * @param <T> of {@link TaskTideModel}
 * @author bkenna
 */
public class ExecutorServiceTracker<T extends TaskTideModel<T>> {
    
    // Attributes
    private final ConcurrentMap<String, ExecutorServiceItem<T>> taskStates;
    
    
    ExecutorServiceTracker() {
        this.taskStates = new ConcurrentHashMap<>();
    }
    
    
    /**
     * Clear entries from collection
     * 
     */
    public void clearMap() {
        this.taskStates.clear();
    }
    
    
    /**
     * Fetch list of completed Ids
     * 
     * @return List-String
     */
    public List<String> fetchDone() {
        return taskStates.keySet()
            .stream()
            .parallel()
            .filter(
                elm -> this.isDone(elm)
            )
            .collect(Collectors.toList());
    }
    
    
    /**
     * Mark task with {@link ExecutorServiceItem}
     * 
     * @param taskId
     * @param item 
     */
    public void markTask(String taskId, ExecutorServiceItem<T> item) {
        taskStates.putIfAbsent(taskId, item);
    }
    
    
    /**
     * Fetch future of task
     * 
     * @param taskId
     * @return {@link ExecutorServiceItem}
     */
    public ExecutorServiceItem<T> get(String taskId) {
        return taskStates.get(taskId);
    }
    
    
    /**
     * Check whether task is completed
     * 
     * @param taskId
     * @return boolean
     */
    public boolean isDone(String taskId) {
        ExecutorServiceItem<T> item = taskStates.get(taskId);
        return item.getFuture().isDone();
    }
    
    
    /**
     * Check if task is still running or in prepare
     * 
     * @param taskId
     * @return boolean
     */
    public boolean isActive(String taskId) {
        ExecutorServiceItem<T> item = taskStates.get(taskId);
        return !item.getFuture().isDone() || !item.getFuture().isCancelled();
    }

    
    /**
     * Check if task is still done or cancelled
     * 
     * @param taskId
     * @return boolean
     */
    public boolean isComplete(String taskId) {
        ExecutorServiceItem<T>item = taskStates.get(taskId);
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
    
    
    /**
     * Get Ids from task states
     * 
     * @return Set of String
     */
    public Set<String> getIds() {
        return taskStates.keySet();
    }
    
    
    /**
     * Represent collection as String
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