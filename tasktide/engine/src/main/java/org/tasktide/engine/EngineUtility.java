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
package org.tasktide.engine;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.Logger;

import org.tasktide.core.manager.TaskTideServiceManager;
import org.tasktide.core.model.CustomAnnotation;

import org.tasktide.core.model.task.ItemTask;
import org.tasktide.core.model.task.TaskState;
import org.tasktide.core.model.workitem.ItemState;
import org.tasktide.core.model.workitem.WorkItem;

import org.tasktide.engine.trackers.FutureTrackers;


/**
 * Collection of useful engine methods
 *
 * @author bkenna
 */
public class EngineUtility {
    
    
    /**
     * Waits via {@link FutureTrackers}
     * 
     * @param expected
     * @param logger 
     */
    public static void waitOnExecutorTrackerWorkItem(int expected, Logger logger) {
        
        // Initialize vars
        boolean done = false;
        int baseDelaySeconds = 1, counter = 0;
        long sleepTime;
        int baseCount, currentDone, nRemaining;
        
        // Wait until done
        baseCount = FutureTrackers.WORK_ITEM_TRACKER.taskCount();
        logger.info("Begining state monitoring of ExecutorServiceTracker:\tN tasks = '{}'", baseCount);
        while ( !done ) {
        
            // Measure delay capping to 512
            int waitVal = baseDelaySeconds * (int)Math.pow(2, counter - 1);
            if ( waitVal <= 5) {
                sleepTime = 10 * 1000L;
            }
            else {
                sleepTime = Math.min(waitVal, 85) * 1000L;
            }
            
            // Wait
            logger.info("Letting '{}'ms elapse for state monitoring of ExecutorServiceTracker:\t'{}'", 
                sleepTime
            );
            try {TimeUnit.MILLISECONDS.sleep(sleepTime);} catch(InterruptedException ex) {Thread.currentThread().interrupt();}
            
            // Fetch summary
            currentDone = FutureTrackers.WORK_ITEM_TRACKER.countDone();
            nRemaining = baseCount - currentDone;
            if (nRemaining < 0) {
                baseCount = FutureTrackers.WORK_ITEM_TRACKER.taskCount();
                currentDone = FutureTrackers.WORK_ITEM_TRACKER.countDone();
                nRemaining = baseCount - currentDone;
            }
            logger.info(
          "Displaying Iter-'{}' StateSummary of ExecutorServiceTracker:\n\nTotal='{}', Remaining='{}', Done='{}', Expected='{}'", 
             counter, baseCount, nRemaining, currentDone, expected
            );
            
            // Sum of touched ItemTasks, did any raise TK error, Executor have states?
            counter++;
            done = currentDone == expected;
        }
    }
    
    
    /**
     * Scan tasks for count of done
     * 
     * @param workload
     * @return int
     */
    public static int countNotActive(List<ItemTask> workload) {
        return (int) workload.stream()
            .parallel()
            .filter(
                task -> 
                    task.getTaskState() == TaskState.COMPLETE 
                        || task.getTaskState() == TaskState.ERROR
            )
        .count();
    }
    
    
    /**
     * Count non-pending tasks across {@link WorkItem} list
     * 
     * @param tasks
     * @return int
     */
    public static int countNonActive(List<WorkItem> tasks) {
        return (int) tasks
            .stream()
            .parallel()
            .mapToInt( elm -> {
                    Collection<ItemTask> itemTasks = elm.getWorkload().getWorkload().values();
                    return countNotActive(new ArrayList<>(itemTasks));
            })
            .sum();
    }
    
    
    /**
     * Log execution times on INFO level
     * 
     * @param workload 
     * @param logger  
     */
    public static void fetchExecutionTimes(List<ItemTask> workload, Logger logger) {
        String output = "\n\n";
        for (ItemTask task : workload) {
            output += String.format(
               "Task '%s' started on Thread '%s' '%d' finished '%d' duration '%d'\n",
               task.getId(), 
               task.getTaskLog().getThreadName(),
               task.getTaskLog().getStartTime(),
               task.getTaskLog().getEndTime(),
               task.getTaskLog().getEndTime() - task.getTaskLog().getStartTime()
            );
        }
        logger.info("Displaying Execution Times Across ItemTasks:{}", output);
    }
    
    
    /**
     * Fetch execution times across all
     * 
     * @param workload
     * @param logger 
     */
    public static void fetchExecutionTimesWorkItem(List<WorkItem> workload, Logger logger) {
        for ( WorkItem item : workload ) {
            System.out.println("\n\n========= Analysing WorkItem:\t'" + item.getId() + "'=============\n\n");
            fetchExecutionTimes(item.fetchByStates().get(ItemState.DONE), logger);
            fetchExecutionTimes(item.fetchByStates().get(ItemState.ERROR), logger);
            System.out.println("\n\n========= Done WorkItem:\t'" + item.getId() + "'=============\n\n");
        }
    }
    
    
    /**
     * Fetch todo
     * 
     * @return List-{@link WorkItem}
     */
    public static List<WorkItem> fetchToDoWork() {
        return TaskTideServiceManager.fetchWorkItemService().viewByField("itemState", ItemState.TODO);
    }
    
    
    /**
     * Fetch todo for target step
     * 
     * @param stepName
     * @return List-{@link WorkItem}
     */
    public static List<WorkItem> fetchToDoWorkTarget(String stepName) {
        return TaskTideServiceManager
            .fetchWorkItemService()
            .viewByFieldForGroup("itemState", ItemState.TODO, "stepName", stepName);
    }
    
    
    /**
     * Fetch todo for target step
     * 
     * @param stepName
     * @param key
     * @param value
     * @return List-{@link WorkItem}
     */
    public static List<WorkItem> fetchToDoWorkTargetPilotLabel(String stepName, String key, Object value) {
        return TaskTideServiceManager
            .fetchWorkItemService()
            .getRepo()
        .findByFieldForGroupWithAnno("itemState", ItemState.TODO, "stepName", stepName, key, value);
    }
    
    
    /**
     * Fetch todo for target step
     * 
     * @param stepName
     * @param anno
     * @return List-{@link WorkItem}
     */
    public static List<WorkItem> fetchToDoWorkTargetPilotLabel(String stepName, CustomAnnotation anno) {
        return TaskTideServiceManager
            .fetchWorkItemService()
            .getRepo()
        .findByFieldForGroupWithAnno("itemState", ItemState.TODO, "stepName", stepName, anno);
    }
}