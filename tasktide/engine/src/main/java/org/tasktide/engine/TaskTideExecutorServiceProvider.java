/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.tasktide.engine;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * @author bkenna
 */
public class TaskTideExecutorServiceProvider {
 
    // There can be only one
    private static volatile TaskTideExecutorServiceProvider instance;
    
    // Executor services
    private final int workItemThreads, itemTaskThreads;
    private final ExecutorService workItemExecutorService;
    private final ExecutorService itemTaskExecutorService;
    
    
    /**
     * Construct with required thread pool size
     * 
     * @param workItemThreads
     * @param itemTaskThreds 
     */
    private TaskTideExecutorServiceProvider(int workItemThreads, int itemTaskThreds) {
        this.workItemThreads = workItemThreads;
        this.itemTaskThreads = itemTaskThreds;
        this.workItemExecutorService = Executors.newFixedThreadPool(workItemThreads);
        this.itemTaskExecutorService = Executors.newFixedThreadPool(itemTaskThreds);
    }

    
    public static synchronized void initialize(int workItemThreads, int itemTaskThreads) {
        if ( instance != null ) {
            throw new IllegalStateException("TaskTideExecutorService already initialize");
        }
        instance = new TaskTideExecutorServiceProvider(workItemThreads, itemTaskThreads);
    
    }
    
    
    /**
     * 
     * @return 
     */
    public static TaskTideExecutorServiceProvider getInstance() {
        if ( instance == null ) {
            throw new IllegalStateException("TaskTideExecutorService not initialized");
        }
        else {
            return instance;
        }
    }
    
    
    /**
     * Get configured {@link ExecutorService} for {@link WorkItem} processing
     * 
     * @return {@link ExecutorService}
     */
    public ExecutorService getWorkItemExecutorService() {
        return workItemExecutorService;
    }

    
    /**
     * Get configured {@link ExecutorService} for {@link ItemTask} processing
     * 
     * @return {@link ExecutorService}
     */
    public ExecutorService getItemTaskExecutorService() {
        return itemTaskExecutorService;
    }
    
    
    // Static accessors for convenience
    public static ExecutorService workItemExecutorService() {
        return getInstance().getWorkItemExecutorService();
    }

    public static ExecutorService itemTaskExecutorService() {
        return getInstance().getItemTaskExecutorService();
    }

    public int getWorkItemThreads() {
        return workItemThreads;
    }

    public int getItemTaskThreads() {
        return itemTaskThreads;
    }
    
    
    
}