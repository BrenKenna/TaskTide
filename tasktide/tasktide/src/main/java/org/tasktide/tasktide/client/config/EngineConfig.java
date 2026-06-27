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
package org.tasktide.tasktide.client.config;

import java.util.Random;

import jakarta.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import org.tasktide.core.model.task.ItemTask;
import org.tasktide.core.model.workitem.WorkItem;

import org.tasktide.parser.ArgumentTree;
import org.tasktide.parser.model.Argument;
import org.tasktide.parser.model.ArgumentType;

import org.tasktide.engine.observer.WorkerObserver;
import org.tasktide.engine.observer.worker.TimeKeeperObserver;
import org.tasktide.engine.worker.TaskTideEngineWorker;

import org.tasktide.parser.configuration.AbstractConfig;


/**
 * Class to template the {@link TaskTideEngineWorker} module parameters
 * 
 * @author bkenna
 */
@ApplicationScoped
public class EngineConfig extends AbstractConfig {
    
    /**
     * Task binding
     * 
     */
    @ConfigProperty(name = "tasktide.engine.pilot-label.key", defaultValue = "")
    String pilotLabelKey;
    
    @ConfigProperty(name = "tasktide.engine.pilot-label.value", defaultValue = "")
    String pilotLabelValue;
    
    @ConfigProperty(name = "tasktide.engine.pilot-label.annotation", defaultValue = "")
    String pilotLabelAnnotation;
    
    
    /**
     * Engine execution policy
     * 
     */
    @ConfigProperty(name = "tasktide.engine.execution-policy", defaultValue = "batch")
    String executionPolicy;
    
    
    /**
     * Process Executor stream directory
     * 
     */
    @ConfigProperty(name = "tasktide.engine.process-executor.stream-directory", defaultValue = "batch")
    String processExecutorStreamDirectory;
    
    
    /**
     * Worker Params
     * 
     */
    @ConfigProperty(name = "tasktide.engine.worker.lock-wait-time", defaultValue = "4")
    int lockTime;
    
    @ConfigProperty(name = "tasktide.engine.worker.threads.worker-pool-size", defaultValue = "1")
    int workerPoolSize;
    
    @ConfigProperty(name = "tasktide.engine.worker.window-size", defaultValue = "10")
    int windowSize;
    
    @ConfigProperty(name = "tasktide.engine.worker.threads.itemTask", defaultValue = "1")
    int itemTaskThreads;
    
    
    /**
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
     * Applies the {@link TaskTideEngineWorker} configurations to {@link ArgumentTree}
     * 
     * @param argTree 
     */
    @Override
    public void initConfig(ArgumentTree argTree) {
        this.help();
        this.workerPoolSize();
        this.windowSize();
        this.itemTaskThreads();
        this.timeKeeperMaxWallTime();
        this.pilotLabelKey();
        this.pilotLabelValue();
        this.pilotLabelAnnotation();
        this.executionPolicy();
        
        this.processExecutorStreamDirectory();
        
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
    @Override
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
     * Configures Stream Directory for ProcessExecutor
     * 
     */
    public void processExecutorStreamDirectory() {
        Argument<String> arg;
        arg = this.getArgumentBuilder()
            .withName("Process Executor Stream Directory")
            .withDescription("Log stream directory for Process Executor")
            .withShortFlag("-sd")
            .withLongFlag("--stream-directory")
            .withArgType(ArgumentType.ACTION)
            .withRefClass(String.class)
        .build();
        
        this.processExecutorStreamDirectory = this.getConfigValue("tasktide.engine.process-executor.stream-directory", String.class, null);
        arg.setValue(this.processExecutorStreamDirectory);
        
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
            .withShortFlag("-l")
            .withLongFlag("--lock-wait-time")
            .withArgType(ArgumentType.ACTION)
            .withRefClass(Integer.class)
        .build();
        
        this.lockTime = this.getConfigValue("tasktide.engine.worker.lock-wait-time", Integer.class, (new Random()).nextInt(4)+1);
        arg.setValue(lockTime);
        this.getArgumentMap().putArgument(arg);
    }
    
    
    /**
     * Configure the number of threads for {@link WorkItem} processing
     * 
     */
    public void workerPoolSize() {
        Argument<Integer> arg;
        arg = this.getArgumentBuilder()
            .withName("Worker Pool Size")
            .withDescription("Defines the number of active engine workers")
            .withShortFlag("-w")
            .withLongFlag("--worker-pool-size")
            .withArgType(ArgumentType.ACTION)
            .withRefClass(Integer.class)
        .build();
        
        this.workerPoolSize = this.getConfigValue("tasktide.engine.worker.threads.worker-pool-size", Integer.class, 1);
        int value = this.workerPoolSize <= 0 ? 1 : this.workerPoolSize;
        this.workerPoolSize = value;
        arg.setValue(workerPoolSize);
        this.getArgumentMap().putArgument(arg);
    }
    
    
    /**
     * Configure the number of threads for {@link WorkItem} processing
     * 
     */
    public void windowSize() {
        Argument<Integer> arg;
        arg = this.getArgumentBuilder()
            .withName("Window Size")
            .withDescription("Defines the number of tasks polled")
            .withShortFlag("-wws")
            .withLongFlag("--worker-window-size")
            .withArgType(ArgumentType.ACTION)
            .withRefClass(Integer.class)
        .build();
        
        this.windowSize = this.getConfigValue("tasktide.engine.worker.window-size", Integer.class, 10);
        int value = this.windowSize <= 0 ? 10 : this.windowSize;
        this.windowSize = value;
        arg.setValue(windowSize);
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
            .withRefClass(Integer.class)
        .build();
        
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
            .withShortFlag("-m")
            .withLongFlag("--max-wall-time")
            .withArgType(ArgumentType.ACTION)
            .withRefClass(Integer.class)
        .build();
        
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
            .withRefClass(Integer.class)
        .build();

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
            .withRefClass(Boolean.class)
        .build();
        
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
            .withRefClass(Boolean.class)
        .build();
        
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
            .withRefClass(Boolean.class)
        .build();
        
        this.timeKeeperOnEndCanFail = this.getConfigValue("tasktide.engine.observer.timekeeper.onEnd.canFail", Boolean.class, true);
        arg.setValue(this.timeKeeperOnEndCanFail);
        this.getArgumentMap().putArgument(arg);
    }
    
    
    /**
     * Configure pilot label keu
     * 
     */
    public void pilotLabelKey() {
        Argument<String> arg;
        arg = this.getArgumentBuilder()
            .withName("Pilot Label Key")
            .withDescription("For early task binding to pilot job")
            .withShortFlag("-plk")
            .withLongFlag("--pilot-label-key")
            .withArgType(ArgumentType.ACTION)
            .withRefClass(String.class)
        .build();
        
        this.pilotLabelKey = this.getConfigValue("tasktide.engine.pilot.label.key", String.class, "");
        arg.setValue(this.pilotLabelKey);
        this.getArgumentMap().putArgument(arg);
    }
    
    
    /**
     * Configure pilot label value
     * 
     */
    public void pilotLabelValue() {
        Argument<String> arg;
        arg = this.getArgumentBuilder()
            .withName("Pilot Label Value")
            .withDescription("For early task binding to pilot job")
            .withShortFlag("-plv")
            .withLongFlag("--pilot-label-value")
            .withArgType(ArgumentType.ACTION)
            .withRefClass(String.class)
        .build();
        
        this.pilotLabelValue = this.getConfigValue("tasktide.engine.pilot.label.value", String.class, "");
        arg.setValue(this.pilotLabelValue);
        this.getArgumentMap().putArgument(arg);
    }
    
    
    /**
     * Configure pilot label annotation
     * 
     */
    public void pilotLabelAnnotation() {
        Argument<String> arg;
        arg = this.getArgumentBuilder()
            .withName("Pilot Label Annotation")
            .withDescription("For early task binding to pilot job")
            .withShortFlag("-pa")
            .withLongFlag("--pilot-label-annotation")
            .withArgType(ArgumentType.ACTION)
            .withRefClass(String.class)
        .build();
        
        this.pilotLabelAnnotation = this.getConfigValue("tasktide.engine.pilot.label.annotation", String.class, "");
        arg.setValue(this.pilotLabelAnnotation);
        this.getArgumentMap().putArgument(arg);
    }
    
    
    /**
     * Execution policy of engine
     * 
     */
    public void executionPolicy() {
        Argument<String> arg;
        arg = this.getArgumentBuilder()
            .withName("Execution Policy")
            .withDescription("Execution policy of engine:\tBatch (Default), Service")
            .withShortFlag("-ep")
            .withLongFlag("--execution-policy")
            .withArgType(ArgumentType.ACTION)
            .withRefClass(String.class)
        .build();
        
        this.executionPolicy = this.getConfigValue("tasktide.engine.execution-policy", String.class, "batch");
        arg.setValue(this.executionPolicy);
        this.getArgumentMap().putArgument(arg);
    }
}