/*
 * Copyright 2025 Brendan Kenna.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.tasktide.engine.trackers;

import java.util.Map;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.tasktide.core.TaskTideModel;
import org.tasktide.core.model.workitem.WorkItem;
import org.tasktide.core.model.task.ItemTask;


/**
 * Generic class to hold logic for tracking futures of {@link TaskTideModel}
 *  elements (ie {@link ItemTask}, {@link WorkItem}) as {@link ExecutorServiceItem}.
 * 
 * @param <T> of {@link TaskTideModel} of {@link WorkItem}, {@link ItemTask}
 * @author bkenna
 */
public class ExecutorServiceTracker<T extends TaskTideModel<T>> {
    
    // Attributes
    private final ConcurrentMap<String, ExecutorServiceItem<T>> taskStates;
    private final Logger LOGGER = LogManager.getLogger(ExecutorServiceTracker.class);
    private final Class<T> TYPE;
    
    
    /**
     * Only constructable from within package
     */
    ExecutorServiceTracker(Class<T> type) {
        this.taskStates = new ConcurrentHashMap<>();
        this.TYPE = type;
    }
    
    
    /**
     * Construct with state map
     * 
     * @param type
     * @param taskStates 
     */
    ExecutorServiceTracker(Class<T> type, ConcurrentMap<String, ExecutorServiceItem<T>> taskStates) {
        if ( taskStates != null ) {
            this.taskStates = taskStates;
        }
        else {
            this.taskStates = new ConcurrentHashMap<>();
        }
        this.TYPE = type;
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
     * Fetch entry set
     * 
     * @return 
     */
    public Set<Entry<String, ExecutorServiceItem<T>>> fetchEntrySet() {
        return this.taskStates.entrySet();
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
     * Count done and terminated elements
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
     * Wait for future list
     * 
     * @param futures 
     */
    public void waitForAll(List<Future<?>> futures) {
        for( Future<?> future : futures ) {
            try {
                future.get();
            }
            catch ( InterruptedException | ExecutionException ex) {
                LOGGER.error("Error encountered waiting on workload:\n{}", ex);
            }
        }
    }
    
    
    /**
     * Fetch a {@link TrackerWaiter} for provided workload
     * 
     * @param tasks
     * @return {@link TrackerWaiter}
     */
    public TrackerWaiter<T> fetchWaiterFor(List<T> tasks) {
        if ( tasks.isEmpty() ) {
            LOGGER.error("Cannot fetch waiter for empty list, passing on creation");
            return null;
        }
        LOGGER.info("Task size:\t'{}'", this.taskStates.size());
        ExecutorServiceTracker<T> tracker = new ExecutorServiceTracker<>(this.TYPE, this.taskStates);
        return new TrackerWaiter<>(tasks, tracker, tracker.getType());
    }
    
    
    /**
     * Get class type of tracker
     * 
     * @return Class of T
     */
    public Class<T> getType() {
        return this.TYPE;
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