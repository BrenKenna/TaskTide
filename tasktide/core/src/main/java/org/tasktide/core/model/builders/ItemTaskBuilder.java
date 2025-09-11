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
package org.tasktide.core.model.builders;

import org.tasktide.core.model.CustomAnnotation;
import org.tasktide.core.model.task.ItemTask;
import org.tasktide.core.model.task.TaskState;
import org.tasktide.core.model.task.TaskLogging;


/**
 * Allow ProcessLog objects to be built where all fields are optional
 * 
 * @author bkenna
 */
public class ItemTaskBuilder extends ModelBuilder<ItemTask> {
    
    // Attributes
    private String id, taskName, task, workItemId;
    private TaskLogging taskLog;
    private TaskState taskState;
    private CustomAnnotation anno;
    
    
    public ItemTaskBuilder() {
        super();
    }
    
    
    /**
     * Add id field
     * 
     * @param id 
     * @return ItemTaskBuilder
     */
    public ItemTaskBuilder withId(String id) {
        this.id = id;
        return this;
    }
    
    
    /**
     * Add workItemId field
     * 
     * @param workItemId
     * @return {@link ItemTaskBuilder}
     */
    public ItemTaskBuilder withWorkItemId(String workItemId) {
        this.workItemId = workItemId;
        return this;
    }
    
    
    /**
     * Add task name field
     * 
     * @param taskName
     * @return {@link ItemTaskBuilder}
     */
    public ItemTaskBuilder withTaskName(String taskName) {
        this.taskName = taskName;
        return this;
    }
    
    
    /**
     * Add task field
     * 
     * @param task
     * @return {@link ItemTaskBuilder}
     */
    public ItemTaskBuilder withTask(String task) {
        this.task = task;
        return this;
    }
    
    
    /**
     * Add {@link TaskState} field
     * 
     * @param taskState
     * @return {@link ItemTaskBuilder}
     */
    public ItemTaskBuilder withTaskState(TaskState taskState) {
        this.taskState = taskState;
        return this;
    }
    
    
    /**
     * Add {@link TaskLogging} field
     * 
     * @param taskLog
     * @return {@link ItemTaskBuilder}
     */
    public ItemTaskBuilder withTaskLog(TaskLogging taskLog) {
        this.taskLog = taskLog;
        return this;
    }
    
    
    /**
     * Adds {@link CustomAnnotation}
     * 
     * @param anno
     * @return {@ilnk ItemTaskBuilder}
     */
    public ItemTaskBuilder withAnnotation(CustomAnnotation anno) {
        this.anno = anno;
        return this;
    }
    
    
    /**
     * Construct {@link ItemTask} from provided fields
     * 
     * @return {@link ItemTask} 
     */
    @Override
    public ItemTask build() {
        if ( workItemId != null ) {
            return new ItemTask(id, taskName, task, taskState, taskLog, workItemId, anno);
        }
        return new ItemTask(id, taskName, task, taskState, taskLog, anno);
    }
}