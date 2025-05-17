/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.tasktide.engine.worker.executor;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;

import org.tasktide.core.model.task.ItemTask;
import org.tasktide.core.model.task.TaskLogging;
import org.tasktide.core.model.task.TaskState;

import org.tasktide.engine.observer.chain.ItemTaskObserver;


/**
 * Class to handle the execution of {@link ItemTask}
 * 
 * @author bkenna
 */
public class ItemTaskExecutor extends TaskTideExecutor<ItemTask> {

    
    /**
     * Construct {@link TaskTideExecutor} for {@link ItemTask}
     */
    public ItemTaskExecutor() {
        super(new ItemTaskObserver(), LogManager.getLogger(ItemTaskExecutor.class));
    }

    
    /**
     * Execute the work of a {@link ItemTask}
     * 
     * @param task
     * @return boolean
     * 
     * @throws IOException
     * @throws InterruptedException 
     */
    @Override
    protected boolean executeTask(ItemTask task) throws IOException, InterruptedException {
        
        // Acknowledge task execution
        logger.info("Executing task on thread '{}':{}", Thread.currentThread().getName(), task.getTask());
        TaskLogging taskLog = processExecutor.execute(task.getTask());
        task.setTaskLog(taskLog);

        // Handle logging execution state
        if (taskLog.getExitCode() == 0) {
            logger.info(
          "Task '{}' successful on thread '{}' with exit code {}\n",
             task.getTaskName(), Thread.currentThread().getName(), taskLog.getExitCode()
            );
        }
        else {
            logger.error(
          "Task '{}' failed on thread '{}' with exit code {}\n",
             task.getTaskName(), Thread.currentThread().getName(), taskLog.getExitCode()
            );
        }

        // Debugger message
        logger.debug("Task after execution:\n{}\n\n", task.toJsonDoc());
        return taskLog.getExitCode() == 0;
    }
}