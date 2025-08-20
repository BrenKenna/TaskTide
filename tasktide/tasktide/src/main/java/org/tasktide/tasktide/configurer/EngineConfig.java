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
    
    
    /**
     *
     * TimeKeeper Params
     * 
     */
    @ConfigProperty(name = "tasktide.engine.observer.timekeeper.level", defaultValue = "0")
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
        this.help();
        this.workItemThreads();
        this.workItemSubTaskThreshold();
        this.itemTaskThreads();
        this.itemTaskSubTaskThreshold();
        this.timeKeeperMaxWallTime();
        
        if ( this.getPath().isEmpty() ) {
            argTree.getTree().getRoot().setData(this.getArgumentMap());
        }
        else {
            argTree.getTree().addChild(this.getPath(), this.getArgumentMap());
        }
    }
    
    
    /**
     * Configure help
     */
    public void help() {
        Argument<Boolean> arg;
        arg = this.getArgumentBuilder()
            .withName("Help")
            .withDescription("Displays command-line documentation")
            .withShortFlag("-h")
            .withLongFlag("--help")
            .withArgType(ArgumentType.ACTION)
            .withValue(false, Boolean.class)
        .build();
        this.getArgumentMap().putArgument(arg);
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
        arg.setRefClass(Integer.class);
        
        this.lockTime = this.getConfigValue("tasktide.engine.worker.lock-wait-time", Integer.class, (new Random()).nextInt(4)+1);
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
        arg.setRefClass(Integer.class);
        
        this.workItemThreads = this.getConfigValue("tasktide.engine.worker.processor.threads.workitem", Integer.class, 1);
        int value = this.workItemThreads <= 0 ? 1 : this.workItemThreads;
        this.workItemThreads = value;
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
        arg.setRefClass(Integer.class);
        
        this.workItemThreshold = this.getConfigValue("tasktide.engine.worker.processor.treshold.workitem", Integer.class, 1);
        int value = this.workItemThreshold <= 0 ? this.workItemThreshold : this.workItemThreshold;
        this.workItemThreshold = value;
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
        arg.setRefClass(Integer.class);
        
        this.itemTaskThreshold = this.getConfigValue("tasktide.engine.worker.processor.threshold.itemtask", Integer.class, this.itemTaskThreads);
        int value = this.itemTaskThreshold <= 0 ? this.itemTaskThreads : this.itemTaskThreshold;
        this.itemTaskThreshold = value;
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
        arg.setRefClass(Integer.class);
        
        this.itemTaskThreads = this.getConfigValue("tasktide.engine.worker.processor.threads.itemtask", Integer.class, 1);
        int value = this.itemTaskThreads <= 0 ? 1 : this.itemTaskThreads;
        this.itemTaskThreads = value;
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
        arg.setRefClass(Integer.class);
        
        this.timeKeeperWaitTime = this.getConfigValue("tasktide.engine.observer.timekeeper.waitTime", Integer.class, 10000);
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
            .withShortFlag("-tk")
            .withLongFlag("--time-keeper")
            .withArgType(ArgumentType.ACTION)
        .build();
        arg.setRefClass(Integer.class);

        this.timeKeeperLevel = this.getConfigValue("tasktide.engine.observer.timekeeper.level", Integer.class, 0);
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
            .withShortFlag("-tks")
            .withLongFlag("--time-keeper-onStart")
            .withArgType(ArgumentType.ACTION)
        .build();
        arg.setRefClass(Boolean.class);
        
        this.timeKeeperOnStartCanFail = this.getConfigValue("tasktide.engine.observer.timekeeper.onStart.canFail", Boolean.class, true);
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
            .withShortFlag("-tkp")
            .withLongFlag("--time-keeper-onProcessing")
            .withArgType(ArgumentType.ACTION)
        .build();
        arg.setRefClass(Boolean.class);
        
        this.timeKeeperOnProcessingCanFail = this.getConfigValue("tasktide.engine.observer.timekeeper.onProcessing.canFail", Boolean.class, true);
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
            .withShortFlag("-tke")
            .withLongFlag("--time-keeper-onEnd")
            .withArgType(ArgumentType.ACTION)
        .build();
        arg.setRefClass(Boolean.class);
        
        this.timeKeeperOnEndCanFail = this.getConfigValue("tasktide.engine.observer.timekeeper.onEnd.canFail", Boolean.class, true);
        arg.setValue(this.timeKeeperOnEndCanFail);
        this.getArgumentMap().putArgument(arg);
    }
}