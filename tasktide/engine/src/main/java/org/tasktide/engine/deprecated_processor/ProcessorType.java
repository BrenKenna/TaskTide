/*
 * Copyright 2026 Brendan Kenna.
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

package org.tasktide.engine.deprecated_processor;

import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

import org.tasktide.core.TaskTideModel;
import org.tasktide.core.model.task.ItemTask;
import org.tasktide.core.model.workitem.WorkItem;

import org.tasktide.engine.wokerunit.provider.TaskTideExecutorServiceProvider;
import org.tasktide.engine.wokerunit.provider.TaskTideWorkerUnitProvider;
import org.tasktide.engine.trackers.ExecutorServiceTracker;
import org.tasktide.engine.trackers.FutureTrackers;


/**
 * Enum to support operations over valid {@link TaskTideProcessor}
 *  implementations, and generic methods
 *
 * @author Brendan Kenna
 */
public enum ProcessorType {

    WORKITEM {
        @Override
        public String toString() {
            return name();
        }

        @Override
        public boolean isProcessorType(String query) {
            return name().equalsIgnoreCase(query);
        }

        @Override
        public boolean isProcessorType(ProcessorType query) {
            return this == query;
        }
        
        @Override
        public ExecutorService getExecutorService() {
            return TaskTideExecutorServiceProvider.getInstance().getWorkItemExecutorService();
        }
        
        @Override
        public int getExecutorServiceThreads() {
            return TaskTideExecutorServiceProvider.getInstance().getWorkItemThreads();
        }
        
        @Override
        public ExecutorServiceTracker<WorkItem> getFutureTracker() {
            return FutureTrackers.WORK_ITEM_TRACKER;
        }
        
        @Override
        public TaskTideProcessor<WorkItem> getNewProcessor() {
            
            // initialize vars
            ExecutorService execServ;
            TaskTideWorkerUnitProvider unitProvider;
            
            // Fetch executor service, and unit provider
            execServ = getExecutorService();
            unitProvider = new TaskTideWorkerUnitProvider();
        
            // Construct new processor
            return unitProvider.getWorkItemProcBuilder()
                .withExecutorService(execServ)
            .build();
        }
    },

    ITEM_TASK {
        @Override
        public String toString() {
            return name();
        }

        @Override
        public boolean isProcessorType(String query) {
            return name().equalsIgnoreCase(query);
        }

        @Override
        public boolean isProcessorType(ProcessorType query) {
            return this == query;
        }
        
        @Override
        public ExecutorService getExecutorService() {
            return TaskTideExecutorServiceProvider.getInstance().getItemTaskExecutorService();
        }
        
        @Override
        public int getExecutorServiceThreads() {
            return TaskTideExecutorServiceProvider.getInstance().getItemTaskThreads();
        }
        
        @Override
        public ExecutorServiceTracker<ItemTask> getFutureTracker() {
            return FutureTrackers.ITEM_TASK_TRACKER;
        }
        
        @Override
        public TaskTideProcessor<ItemTask> getNewProcessor() {
            
            // initialize vars
            ExecutorService execServ;
            TaskTideWorkerUnitProvider unitProvider;
            
            // Fetch executor service, and unit provider
            execServ = getExecutorService();
            unitProvider = new TaskTideWorkerUnitProvider();
        
            // Construct new processor
            return unitProvider.getItemTaskProcBuilder()
                .withExecutorService(execServ)
            .build();
        }
    };

    
    /**
     * Get a new {@link TaskTideProcessor} for
     *  {@link TaskTideModel}
     * 
     * @return {@link TaskTideProcessor}
     */
    public abstract TaskTideProcessor<?> getNewProcessor();
    
    
    /**
     * Get {@link ExecutorServiceTracker} for {@link TaskTideModel}
     * 
     * @return {@link ExecutorServiceTracker}
     */
    public abstract ExecutorServiceTracker<?> getFutureTracker();
    
    
    /**
     * Get thread count for {@link ExecutorService}
     * 
     * @return int
     */
    public abstract int getExecutorServiceThreads();
    
    
    /**
     * Get {@link ExecutorService} for 
     *  implemented value
     * 
     * @return {@link ExecutorService}
     */
    public abstract ExecutorService getExecutorService();
    

    /**
     * Abstract method check if query is enum value
     *
     * @param query
     * @return boolean
     */
    public abstract boolean isProcessorType(String query);


    /**
     * Abstract method check if query is enum value
     *
     * @param query
     * @return boolean
     */
    public abstract boolean isProcessorType(ProcessorType query);


    /**
     * Fetch the index for mapped query string
     *
     * @param query
     * @return >0/-1
     */
    public static int indexOf(String query) {
        for ( ProcessorType elm : values() ) {
            if ( elm.isProcessorType(query) ) {
                return elm.ordinal();
            }
        }
        return -1;
    }


    /**
     * Check if query maps to enum value
     *
     * @param query
     * @return boolean
     */
    public static boolean hasQuery(String query) {
        if ( query == null ) {
            return false;
        }

        for ( ProcessorType elm : values() ) {
            if ( elm.isProcessorType(query) ) {
                return true;
            }
        }
        return false;
    }
    

    /**
     * Map query to enum value
     *
     * @param query
     * @return ProcessorType
     */
    public static ProcessorType get(String query) {
        int ind = indexOf(query);
        if ( ind >= 0 ) {
            return values()[ind];
        }
        return null;
    }


    /**
     * Represent enum as string
     *
     * @return String
     */
    public static String valuesString() {
        return Arrays.stream(values())
            .map( elm -> elm.name() )
        .collect(Collectors.joining(","));
    }
}