/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.tasktide.engine;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Map;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import org.tasktide.core.model.task.ItemTask;
import org.tasktide.core.model.task.TaskState;
import org.tasktide.core.model.workitem.ItemState;
import org.tasktide.core.model.workitem.WorkItem;

import org.tasktide.engine.concurrency.ParallelItemTaskExecutor;
import org.tasktide.engine.trackers.FutureTrackers;

import org.tasktide.engine.workerunit.provider.TaskTideExecutorServiceProvider;



/**
 * Test utility to support development of the TaskTideEngine
 * 
 * @author bkenna
 */
public class EngineTestUtils {
    
    
    
    /**
     * Initialize and return an {@link ExecutorService} for {@link WorkItem}
     * 
     * @param nWorkItemThreads
     * @param nItemTaskThreads
     * @return {@link ExecutorService}
     */
    public static ExecutorService initThenFetchExecutorService(int nWorkItemThreads, int nItemTaskThreads) {
    
        // Configure executor service for work items, and item tasks
        TaskTideExecutorServiceProvider.initialize(nWorkItemThreads, nItemTaskThreads);
        
        // Return result
        return TaskTideExecutorServiceProvider.engineWorkerExecutorService();
    }
    
    
    /**
     * Waits via {@link ExecutorServiceTrackerWorkItem}
     * 
     * @param expected
     * @param logger 
     */
    public static void waitOnExecutorTrackerItemTask(int expected, Logger logger) {
        
        // Initialize vars
        boolean done = false;
        int baseDelaySeconds = 1, counter = 0;
        long sleepTime;
        int baseCount, currentDone, nRemaining;
        
        // Wait until done
        baseCount = FutureTrackers.ITEM_TASK_TRACKER.taskCount();
        logger.info("Begining state monitoring of ExecutorServiceTracker:\tN tasks = '{}'", baseCount);
        while ( !done ) {
        
            // Measure delay capping to 512
            int waitVal = baseDelaySeconds * (int)Math.pow(2, counter - 1);
            if ( waitVal <= 5) {
                sleepTime = 10 * 1000L;
            }
            else {
                sleepTime = Math.min(waitVal, 512) * 1000L;
            }
            
            // Wait
            logger.info("Letting '{}'ms elapse for state monitoring of ExecutorServiceTracker:\t'{}'", 
                sleepTime, FutureTrackers.ITEM_TASK_TRACKER.toString()
            );
            try {TimeUnit.MILLISECONDS.sleep(sleepTime);} catch(InterruptedException ex) {Thread.currentThread().interrupt();}
            
            // Fetch summary
            currentDone = FutureTrackers.ITEM_TASK_TRACKER.countDone();
            nRemaining = baseCount - currentDone;
            if (nRemaining < 0) {
                baseCount = FutureTrackers.ITEM_TASK_TRACKER.taskCount();
                currentDone = FutureTrackers.ITEM_TASK_TRACKER.countDone();
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
     * Waits via {@link ExecutorServiceTrackerWorkItem}
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
     * Wait required time
     * 
     * @param waitTime
     * @param logger
     */
    public static void wait(int waitTime, Logger logger) {
        try {
            Thread.sleep(waitTime);
            logger.info("Waited time '{}'", waitTime);
        } catch (InterruptedException ex) {
            logger.warn("Unable to wait time '{}'", waitTime);
        }
    }    
    
    
    /**
     * Wait for a number of iterations until limit is reached
     * 
     * @param limit
     * @param waitTime
     * @param itemTaskExecutor 
     * @param logger 
     */
    public static void waitByCountedTime(int limit, int waitTime, ParallelItemTaskExecutor itemTaskExecutor, Logger logger) {
        int counter = 0;
        while (counter <= limit || itemTaskExecutor.getTotalExecuted() >= 4) {
            logger.info(
               "Oberserving tasks iteration:\t'{}'. Task done count:\t'{}'",
            counter, itemTaskExecutor.getTotalExecuted()
            );
            counter++;
            try {Thread.sleep(waitTime);} catch(Exception ex) {logger.warn("Unable to sleep for iteration:\t" + counter);}
        }
    }
    
    
    /**
     * Wait until tasks are done
     * 
     * @param workload
     * @param waitTime 
     * @param logger 
     */
    public static void waitUntilDoneTarget(List<ItemTask> workload, int waitTime, Logger logger) {
        int counter = 0;
        int nInactive = countNotActive(workload);
        while ( nInactive < workload.size() ) {
            
            // Log done
            logger.info(
          "Oberserving tasks iteration:\t'{}'. Task done count:\t'{}'",
             counter, nInactive
            );
            
            // Wait and check
            try {Thread.sleep(waitTime * 1000L);} catch(Exception ex) {logger.warn("Unable to sleep for iteration:\t" + counter);}
            nInactive = countNotActive(workload);
            logger.debug("\n\n=========== Count of Tasks Done = " + nInactive + " ===============\n\n");
            counter++;
            
            // Display
            logger.debug("\n\n======== Displaying Workload From Main Thread ========");
            workload.forEach(task -> logger.debug((task.toJsonDoc()))) ;
        }
        logger.info("Workload processing completed");
    }
    
    
    /**
     * Wait until tasks are done
     * 
     * @param workload
     * @param waitTime 
     * @param logger 
     */
    public static void waitUntilDoneWorkItem(List<WorkItem> workload, int waitTime, Logger logger) {
        int counter = 0;
        int nInactive = countNonActive(workload);
        while ( nInactive < workload.size() ) {
            
            // Log done
            logger.info(
          "Oberserving tasks iteration:\t'{}'. Task done count:\t'{}'",
             counter, nInactive
            );
            
            // Wait and check
            try {Thread.sleep(waitTime * 1000L);} catch(Exception ex) {logger.warn("Unable to sleep for iteration:\t" + counter);}
            nInactive = countNonActive(workload);
            logger.debug("\n\n=========== Count of Tasks Done = " + nInactive + " ===============\n\n");
            counter++;
            
            // Display
            logger.debug("\n\n======== Displaying Workload From Main Thread ========");
            workload.forEach(task -> logger.debug((task.toJsonDoc()))) ;
        }
        logger.info("Workload processing completed");
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
                    Collection<ItemTask> itemTasks = elm.getWorkload().getTaskMap().values();
                    return EngineTestUtils.countNotActive(new ArrayList<>(itemTasks));
            })
            .sum();
    }
    
    
    /**
     * Count non-pending tasks
     * 
     * @param task
     * @return int
     */
    public static int countNonPending(WorkItem task) {
        return (int) task.summarizeByState().entrySet()
            .stream()
            .filter( elm -> 
                    elm.getKey() == ItemState.ERROR ||
                    elm.getKey() == ItemState.DONE
            )
            .mapToInt( Map.Entry::getValue  )
            .sum();
    }
    
    
    /**
     * Count non-pending tasks across {@link WorkItem} list
     * 
     * @param tasks
     * @return int
     */
    public static int countNonPending(List<WorkItem> tasks) {
        return (int) tasks
            .stream()
            .parallel()
            .mapToInt( elm -> EngineTestUtils.countNonPending(elm))
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
}
