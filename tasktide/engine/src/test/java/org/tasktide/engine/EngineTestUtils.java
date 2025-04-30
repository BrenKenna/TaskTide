/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.tasktide.engine;

import org.apache.logging.log4j.Logger;

import java.util.List;

import org.tasktide.core.model.task.ItemTask;
import org.tasktide.core.model.task.TaskState;

import org.tasktide.engine.concurrency.ParallelItemTaskExecutor;


/**
 * Test utility to support development of the TaskTideEngine
 * 
 * @author bkenna
 */
public class EngineTestUtils {
    
    
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
            try {Thread.sleep(waitTime);} catch(Exception ex) {logger.warn("Unable to sleep for iteration:\t" + counter);}
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
     * Log execution times on INFO level
     * 
     * @param workload 
     * @param logger  
     */
    public static void fetchExecutionTimes(List<ItemTask> workload, Logger logger) {
        for (ItemTask task : workload) {
            logger.info(
           "\n\nTask '{}' started '{}' finished '{}' duration '{}'\n\n",
            task.getTaskName(),
            task.getTaskLog().getStartTime(),
            task.getTaskLog().getEndTime(),
               task.getTaskLog().getEndTime() - task.getTaskLog().getStartTime()
            );
        }
    }
}
