/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.tasktide.tasktide.configurer;

import jakarta.enterprise.context.ApplicationScoped;
import java.util.Random;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import org.tasktide.core.model.task.ItemTask;
import org.tasktide.core.model.workitem.WorkItem;

import org.tasktide.tasktide.parser.ArgumentTree;
import org.tasktide.tasktide.parser.model.Argument;
import org.tasktide.tasktide.parser.model.ArgumentType;

import org.tasktide.engine.observer.WorkerObserver;
import org.tasktide.engine.observer.worker.TimeKeeperObserver;
import org.tasktide.engine.Engine;
        

/**
 * Class to template the {@link Engine} module parameters
 * 
 * @author bkenna
 */
@ApplicationScoped
public class EngineConfig extends AbstractConfigurer {
    
    
    /**
     *
     * Worker Params
     * 
     */
    @ConfigProperty(name = "tasktide.engine.worker.lock-wait-time", defaultValue = "4")
    int lockTime;
    
    @ConfigProperty(name = "tasktide.engine.worker.processor.threads.workitem", defaultValue = "1")
    int workItemThreads;
    
    @ConfigProperty(name = "tasktide.engine.worker.processor.threshold.workItem", defaultValue = "-1")
    int workItemThreshold;
    
    @ConfigProperty(name = "tasktide.engine.worker.processor.threads.itemTask", defaultValue = "1")
    int itemTaskThreads;
    
    @ConfigProperty(name = "tasktide.engine.worker.processor.itemTask.threshold", defaultValue = "-1")
    int itemTaskThreshold;
    
    @ConfigProperty(name = "tasktide.engine.step", defaultValue = "NA")
    String step;
    
    
    /**
     *
     * TimeKeeper Params
     * 
     */
    @ConfigProperty(name = "tasktide.engine.observer.timekeeper.level", defaultValue = "Optional")
    int timeKeeperLevel;
    
    @ConfigProperty(name = "tasktide.engine.observer.timekeeper.onStart.canFail", defaultValue = "true")
    boolean timeKeeperOnStartCanFail;
    
    @ConfigProperty(name = "tasktide.engine.observer.timekeeper.onProcessing.canFail", defaultValue = "true")
    boolean timeKeeperOnProcessingCanFail;
    
    @ConfigProperty(name = "tasktide.engine.observer.timekeeper.onPEnd.canFail", defaultValue = "true")
    boolean timeKeeperOnEndCanFail;
    
    @ConfigProperty(name = "tasktide.engine.observer.timekeeper.waitTime", defaultValue = "1000000")
    int timeKeeperWaitTime;
    
    
    /**
     * Defaults config path to 'tasktide engine'
     * 
     */
    public EngineConfig() {
        super("engine");
    }
    
    
    /**
     * Uses supplied path for engine config
     * 
     * @param path 
     */
    public EngineConfig(String path) {
        super(path);
    }
    
    
    /**
     * Applies the {@link Engine} configurations to {@link ArgumentTree}
     * 
     * @param argTree 
     */
    @Override
    public void initConfig(ArgumentTree argTree) {
        this.workItemThreads();
        this.workItemSubTaskThreshold();
        this.itemTaskThreads();
        this.itemTaskSubTaskThreshold();
        this.timeKeeperMaxWallTime();
        this.step();
        
        if ( this.getPath().isEmpty() ) {
            argTree.getTree().getRoot().setData(this.getArgumentMap());
        }
        else {
            argTree.getTree().addChild(this.getPath(), this.getArgumentMap());
        }
    }
    
    
    /**
     * Configures the wait time in seconds for locking an item
     * 
     */
    public void lockTime() {
        Argument<Integer> arg;
        arg = this.getArgumentBuilder()
            .withName("Lock Wait Time")
            .withDescription("Configures the wait time in seconds for locking an item")
            .withShortFlag("-t")
            .withLongFlag("--max-wall-time")
            .withArgType(ArgumentType.ACTION)
        .build();
        arg.setRefClass(int.class);
        
        // Set lock time
        try {
            this.lockTime = this.getConfig().getValue("tasktide.engine.worker.lock-wait-time", int.class);
        }
        catch ( Exception ex ) {
            this.lockTime = (new Random()).nextInt(4)+1;
        }
        arg.setValue(lockTime);
        this.getArgumentMap().putArgument(arg);
    }
    
    
    /**
     * Configure the number of threads for {@link WorkItem} processing
     * 
     */
    public void workItemThreads() {
        Argument<Integer> arg;
        arg = this.getArgumentBuilder()
            .withName("WorkItem Threads")
            .withDescription("Defines the number of threads that work item processing can be distributed to")
            .withShortFlag("-w")
            .withLongFlag("--work-item-threads")
            .withArgType(ArgumentType.ACTION)
        .build();
        arg.setRefClass(int.class);
        
        // Set work item threads
        try {
            this.workItemThreads = this.getConfig().getValue("tasktide.engine.worker.processor.threads.workitem", int.class);
        }
        catch ( Exception ex ) {
            this.workItemThreads = 1;
        }
        arg.setValue(workItemThreads);
        this.getArgumentMap().putArgument(arg);
    }
    
    
    /**
     * Configure the sub-tasking threshold for work items
     * 
     */
    public void workItemSubTaskThreshold() {
        Argument<Integer> arg;
        arg = this.getArgumentBuilder()
            .withName("WorkItem SubTasking Threshold")
            .withDescription("Defines the numbers of WorkItems which are processed as a workload by its ThreadPool")
            .withShortFlag("-ws")
            .withLongFlag("--work-item-sub-task-threshold")
            .withArgType(ArgumentType.ACTION)
        .build();
        arg.setRefClass(int.class);
        
        // Set work item sub tasking threshold
        try {
            this.workItemThreshold = this.getConfig().getValue("tasktide.engine.worker.processor.threshold.workitem", int.class);
            int value = this.workItemThreshold <= 0 ? this.workItemThreads : this.workItemThreshold;
            this.workItemThreshold = value;
        }
        catch ( Exception ex ) {
            this.workItemThreshold = workItemThreads;
        }
        arg.setValue(workItemThreshold);
        this.getArgumentMap().putArgument(arg);
    }
    
    
    /**
     * Configure the sub-tasking threshold for item tasks
     * 
     */
    public void itemTaskSubTaskThreshold() {
        Argument<Integer> arg;
        arg = this.getArgumentBuilder()
            .withName("ItemTask SubTasking Threshold")
            .withDescription("Defines the numbers of ItemTasks which are processed as a workload by its ThreadPool")
            .withShortFlag("-is")
            .withLongFlag("--item-task-sub-task-threshold")
            .withArgType(ArgumentType.ACTION)
        .build();
        arg.setRefClass(int.class);
        
        // Set item task threshold
        try {
            this.itemTaskThreshold = this.getConfig().getValue("tasktide.engine.worker.processor.threshold.itemtask", int.class);
            int value = this.itemTaskThreshold <= 0 ? this.itemTaskThreads : this.itemTaskThreshold;
            this.itemTaskThreshold = value;
        }
        catch ( Exception ex ) {
            this.itemTaskThreshold = itemTaskThreads;
        }
        arg.setValue(itemTaskThreshold);
        this.getArgumentMap().putArgument(arg);
    }
    
    
    /**
     * Configure the number of threads for {@link ItemTask} processing
     * 
     */
    public void itemTaskThreads() {
        Argument<Integer> arg;
        arg = this.getArgumentBuilder()
            .withName("ItemTask Threads")
            .withDescription("Defines the number of threads that item task processing can be distributed to")
            .withShortFlag("-i")
            .withLongFlag("--item-task-threads")
            .withArgType(ArgumentType.ACTION)
        .build();
        arg.setRefClass(int.class);

        // Set item task threads
        try {
            this.itemTaskThreads = this.getConfig().getValue("tasktide.engine.worker.processor.threads.itemtask", int.class);
        }
        catch ( Exception ex ) {
            this.itemTaskThreads = 1;
        }
        arg.setValue(itemTaskThreads);
        this.getArgumentMap().putArgument(arg);
    }
    
    
    /**
     * Configures the max time wall time for task processing.
     * 
     */
    public void timeKeeperMaxWallTime() {
        Argument<Integer> arg;
        arg = this.getArgumentBuilder()
            .withName("TimeKeeper Wall Time")
            .withDescription("Configures the max wall-time for task processing in __")
            .withShortFlag("-t")
            .withLongFlag("--max-wall-time")
            .withArgType(ArgumentType.ACTION)
        .build();
        arg.setRefClass(int.class);
        
        // Set wait time
        try {
            this.timeKeeperWaitTime = this.getConfig().getValue("tasktide.engine.observer.timekeeper.waitTime", int.class);
        }
        catch (Exception ex) {
            this.timeKeeperWaitTime = 10000;
        }
        arg.setValue(this.timeKeeperWaitTime);
        this.getArgumentMap().putArgument(arg);
    }
    
    
    /**
     * Configure the {@link TimeKeeperObserver} {@link WorkerObserver} level
     * 
     */
    public void timeKeeperLevel() {
        Argument<Integer> arg;
        arg = this.getArgumentBuilder()
            .withName("TimeKeeper Observer Level")
            .withDescription("Configure whether TimeKeeper is optional")
            .withShortFlag("-to")
            .withLongFlag("--timeKeeper-observer")
            .withArgType(ArgumentType.ACTION)
        .build();
        arg.setRefClass(int.class);
        
        // Set observer level
        try {
            this.timeKeeperLevel = this.getConfig().getValue("tasktide.engine.observer.timekeeper.level", int.class);
        }
        catch (Exception ex) {
            this.timeKeeperLevel = 10000;
        }
        arg.setValue(this.timeKeeperLevel);
        this.getArgumentMap().putArgument(arg);
    }
    
    
    /**
     * Configure the {@link TimeKeeperObserver} {@link WorkerObserver} level
     * 
     */
    public void timeKeeperOnStartCanFail() {
        Argument<Boolean> arg;
        arg = this.getArgumentBuilder()
            .withName("TimeKeeper Observer onStart")
            .withDescription("Configure whether TimeKeeper onStart method can fail")
            .withShortFlag("-tos")
            .withLongFlag("--timeKeeper-onStart")
            .withArgType(ArgumentType.ACTION)
        .build();
        arg.setRefClass(boolean.class);
        
        // Set whether on start can fail
        try {
            this.timeKeeperOnStartCanFail = this.getConfig().getValue("tasktide.engine.observer.timekeeper.onStart.canFail", boolean.class);
        }
        catch (Exception ex) {
            this.timeKeeperOnStartCanFail = true;
        }
        arg.setValue(this.timeKeeperOnStartCanFail);
        this.getArgumentMap().putArgument(arg);
    }
    
    
    /**
     * Configure the {@link TimeKeeperObserver} {@link WorkerObserver} level
     * 
     */
    public void timeKeeperOnProcessingCanFail() {
        Argument<Boolean> arg;
        arg = this.getArgumentBuilder()
            .withName("TimeKeeper Observer onProcessing")
            .withDescription("Configure whether TimeKeeper onProcessing method can fail")
            .withShortFlag("-top")
            .withLongFlag("--timeKeeper-onProcessing")
            .withArgType(ArgumentType.ACTION)
        .build();
        arg.setRefClass(boolean.class);
        
        // Set whether on processing can fail
        try {
            this.timeKeeperOnProcessingCanFail = this.getConfig().getValue("tasktide.engine.observer.timekeeper.onProcessing.canFail", boolean.class);
        }
        catch (Exception ex) {
            this.timeKeeperOnProcessingCanFail = true;
        }
        arg.setValue(this.timeKeeperOnProcessingCanFail);
        this.getArgumentMap().putArgument(arg);
    }
    
    
    /**
     * Configure the {@link TimeKeeperObserver} {@link WorkerObserver} level
     * 
     */
    public void timeKeeperOnEndCanFail() {
        Argument<Boolean> arg;
        arg = this.getArgumentBuilder()
            .withName("TimeKeeper Observer onEnd")
            .withDescription("Configure whether TimeKeeper onEnd method can fail")
            .withShortFlag("-toe")
            .withLongFlag("--timeKeeper-onEnd")
            .withArgType(ArgumentType.ACTION)
        .build();
        arg.setRefClass(boolean.class);
        
        // Set whether 
        try {
            this.timeKeeperOnEndCanFail = this.getConfig().getValue("tasktide.engine.observer.timekeeper.onEnd.canFail", boolean.class);
        }
        catch (Exception ex) {
            this.timeKeeperOnEndCanFail = true;
        }
        arg.setValue(this.timeKeeperOnEndCanFail);
        this.getArgumentMap().putArgument(arg);
    }
    
    
    /**
     * Sets step, defaulting to Arbitrary
     */
    public void step() {
        Argument<String> arg;
        arg = this.getArgumentBuilder()
            .withName("Step")
            .withDescription("Step(s) to process")
            .withShortFlag("-st")
            .withLongFlag("--step")
            .withArgType(ArgumentType.ACTION)
        .build();
        arg.setRefClass(String.class);
        try {
            this.step = this.getConfig().getValue("tasktide.engine.step", String.class);
        }
        catch (Exception ex) {
            this.step = "Arbitrary";
        }
        arg.setValue(this.step);
        this.getArgumentMap().putArgument(arg);
    }
}