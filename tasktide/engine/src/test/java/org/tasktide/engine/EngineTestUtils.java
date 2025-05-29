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

import org.tasktide.core.model.task.ItemTask;
import org.tasktide.core.model.task.TaskState;
import org.tasktide.core.model.workitem.ItemState;
import org.tasktide.core.model.workitem.WorkItem;

import org.tasktide.engine.concurrency.ParallelItemTaskExecutor;


/**
 * Test utility to support development of the TaskTideEngine
 * 
 * @author bkenna
 */
public class EngineTestUtils {
    
    
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
                    Collection<ItemTask> itemTasks = elm.getWorkload().getWorkload().values();
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
               task.getTaskName(), 
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
            fetchExecutionTimes(item.fetchByStates().get(ItemState.DONE), logger);
            fetchExecutionTimes(item.fetchByStates().get(ItemState.ERROR), logger);
        }
    }
}
