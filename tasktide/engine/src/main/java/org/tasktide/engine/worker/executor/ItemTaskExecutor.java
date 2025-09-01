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
package org.tasktide.engine.worker.executor;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;

import org.tasktide.core.model.task.ItemTask;
import org.tasktide.core.model.task.TaskLogging;

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
     * Construct with {@link ItemTaskObserver}
     * 
     * @param observer 
     */
    public ItemTaskExecutor(ItemTaskObserver observer) {
        super(observer, LogManager.getLogger(ItemTaskExecutor.class));
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
        this.observer.onTaskProcessing(task);
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
        // Move to Observer logger.debug("Task after execution:\n{}\n\n", task.toJsonDoc());
        return taskLog.getExitCode() == 0;
    }
}