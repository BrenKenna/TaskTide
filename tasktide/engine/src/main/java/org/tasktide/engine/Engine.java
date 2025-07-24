/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.tasktide.engine;


/**
 * Entry-point class for the TaskTide Engine
 * 
 * @author bkenna
 */
public class Engine {
    
    // Attributes
    private final TaskTideExecutorServiceProvider execProv;
    private final TaskTideWorkerUnitProvider workerProv;
    
    
    /**
     * Construct with required arguments
     * 
     * @param workItemThreads
     * @param itemTaskThreads 
     */
    public Engine(int workItemThreads, int itemTaskThreads) {
        TaskTideExecutorServiceProvider.initialize(workItemThreads, itemTaskThreads);
        this.execProv = TaskTideExecutorServiceProvider.getInstance();
        this.workerProv = new TaskTideWorkerUnitProvider();
    }
}
