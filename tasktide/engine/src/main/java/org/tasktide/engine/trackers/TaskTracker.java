/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.tasktide.engine.trackers;

import java.util.Map;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import java.util.stream.Collectors;

import org.tasktide.core.TaskTideModel;
import org.tasktide.core.model.task.ItemTask;
import org.tasktide.core.model.workitem.WorkItem;


/**
 * Generic class to hold logic for tracking execution states of {@link TaskTideModel}
 *  elements (ie {@link ItemTask}, {@link WorkItem}).
 * 
 * @param <T> of TaskTideModel 
 * @author bkenna
 */
public class TaskTracker<T extends TaskTideModel<T>> {
    
    // Map of task states
    private final ConcurrentMap<String, ExecutionState> taskStates = new ConcurrentHashMap<>();
    
    
    /**
     * Only constructable within package
     */
    TaskTracker() {}
    
    
    /**
     * Mark task with {@link ExecutionState}
     * 
     * @param taskId
     * @param state 
     */
    public void markTask(String taskId, ExecutionState state) {
        taskStates.putIfAbsent(taskId, state);
    }
    
    
    /**
     * Fetch state of task
     * 
     * @param taskId
     * @return {@link ExecutionState}
     */
    public ExecutionState get(String taskId) {
        return taskStates.get(taskId);
    }
    
    
    /**
     * Check whether task is completed
     * 
     * @param taskId
     * @return boolean
     */
    public boolean isDone(String taskId) {
        ExecutionState state = taskStates.get(taskId);
        return state == ExecutionState.COMPLETED ||
           state == ExecutionState.FAILED ||
           state == ExecutionState.ABORTED;
    }
    
    
    /**
     * Check if task is still running or in prepare
     * 
     * @param taskId
     * @return boolean
     */
    public boolean isActive(String taskId) {
        ExecutionState state = taskStates.get(taskId);
        return state == ExecutionState.PREPARE ||
           state == ExecutionState.RUNNING;
    }
    
    
    /**
     * Check if task is running
     * 
     * @param taskId
     * @return boolean
     */
    public boolean isRunning(String taskId) {
        ExecutionState state = taskStates.get(taskId);
        return state == ExecutionState.RUNNING;
    }
    
    
    /**
     * Check whether is held in a non completed state
     * 
     * @param taskId
     * @return boolean
     */
    public boolean isHeld(String taskId) {
        ExecutionState state = taskStates.get(taskId);
        return state == ExecutionState.QUEUED ||
           state == ExecutionState.PREPARE ||
           state == ExecutionState.RUNNING;
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
     * String representation of tracked tasks and their states.
     * 
     * @return String
     */
    @Override
    public String toString() {
        return taskStates.entrySet()
            .stream()
            .sorted(Map.Entry.comparingByKey())
            .map(entry -> String.format("TaskId: %s, State: %s", entry.getKey(), entry.getValue()))
            .collect(Collectors.joining("\n"));
    }
}
