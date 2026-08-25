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
package org.tasktide.engine.executor;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import static javax.management.Query.value;

import org.apache.logging.log4j.LogManager;
import org.tasktide.core.manager.BuilderUtility;

import org.tasktide.core.manager.TaskTideServiceManager;
import org.tasktide.core.model.CustomAnnotation;

import org.tasktide.core.model.task.ItemTask;
import org.tasktide.core.model.task.TaskLogging;
import org.tasktide.core.model.workitem.WorkItem;

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
    public boolean executeTask(ItemTask task) throws IOException, InterruptedException {
        
        // Acknowledge task execution
        TaskLogging taskLog;
        LOGGER.info(
            "Configuring task execution on thread '{}':\n'{}'",
            Thread.currentThread().getName(),
            task.getTask()
        );
        this.observer.onTaskProcessing(task);
        
        // Construct environment variable map
        LOGGER.info("Configuring Environmental Variables for ProcessExecutor");
        Map<String, String> envVars = new HashMap<>();
        envVars.put("WORK_ITEM_ITEM_ID", task.getWorkItemId());
        envVars.put("ITEM_TASK_ID", task.getId());
        envVars.put("JOB_ENVIRONMENT_ID", task.getJobEnvId());
        if ( task.getAnnotations() != null ) {
            if ( !task.getAnnotations().getAnno().isEmpty() ) {
                for ( Entry<String, Object> elm : task.getAnnotations().getAnno().entrySet() ) {
                    envVars.put(elm.getKey(), elm.getValue().toString());
                }
            }
        }
        
        // Process task
        LOGGER.info("Commencing ProcessExectuor for task:\t'{}'", task.getId());
        taskLog = processExecutor.execute(task.getTask(), task.getId(), envVars);
        task.setTaskLog(taskLog);

        // Handle logging execution state
        if (taskLog.getExitCode() == 0) {
            LOGGER.info(
                "Task '{}' successful on thread '{}' with exit code {}\n",
                task.getTaskName(), Thread.currentThread().getName(), taskLog.getExitCode()
            );
            try {
                LOGGER.debug("Determining whether to apply results annotation");
                this.annotateResults(task);
            }
            catch (Exception ex) {
                LOGGER.error(
                    "Error applying results annotation, displaying stack trace:\n\n'{}'",
                    ex
                );
            }
        }
        else {
            LOGGER.error(
                "Task '{}' failed on thread '{}' with exit code {}\n",
                task.getTaskName(), Thread.currentThread().getName(), taskLog.getExitCode()
            );
        }

        // Debugger message
        // Move to Observer LOGGER.debug("Task after execution:\n{}\n\n", task.toJsonDoc());
        return taskLog.getExitCode() == 0;
    }
    
    
    
    /**
     * Apply results annotation if configured on {@link ItemTask}
     * 
     * @param task
     * @throws Exception 
     */
    private void annotateResults(ItemTask task) throws Exception {
        
        // Apply annotation on ItemTask if none yet
        if ( task.getAnnotations() == null ) {
            CustomAnnotation anno = BuilderUtility.makeEmptyAnnotation();
            task.setAnnotations(anno);
        }
        
        if ( task.getAnnotations().hasKey("Results Path") ) {
            LOGGER.debug("'Results Path' annotation detected for task:\n'{}'", task.getId());
            String path = (String) task.getAnnotations().getKey("Results Path");
            Path resultsPath = Paths.get(path);
            String data = Files.readString(resultsPath, StandardCharsets.UTF_8);
            task.getAnnotations().add("Results", data);
            WorkItem item = TaskTideServiceManager.fetchWorkItemService().fetchById(task.getWorkItemId());
            item.dropTask(task);
            item.addTask(task);
            TaskTideServiceManager.fetchWorkItemService().appendModel(item);
        }
        else {
            LOGGER.debug("No 'Results Path' annotation detected for task:\n'{}'", task.getId());
        }
    }
}