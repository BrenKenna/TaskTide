/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.tasktide.engine.tasktracker;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;


/**
 * Class for tracking of task states
 * 
 * @author bkenna
 */
public class TaskTracker {
    
    // Map of task states
    private final ConcurrentMap<String, ExecutionState> taskStates = new ConcurrentHashMap<>();

    
    /**
     * Mark task with {@link ExecutionState}
     * 
     * @param taskId
     * @param state 
     */
    public void markTask(String taskId, ExecutionState state) {
        taskStates.put(taskId, state);
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
}
