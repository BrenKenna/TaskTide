/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.tasktide.core.model.builders;

import org.tasktide.core.model.task.ItemTask;
import org.tasktide.core.model.task.TaskState;
import org.tasktide.core.model.task.TaskLogging;


/**
 * 
 * Allow ProcessLog objects to be built where all fields are optional
 * 
 * @author bkenna
 */
public class ItemTaskBuilder extends ModelBuilder {
    
    // Attributes
    private String id, taskName, task;
    private TaskLogging taskLog;
    private TaskState taskState;
    
    
    public ItemTaskBuilder() {
        super();
    }
    
    
    /**
     * Add id field
     * 
     * @param id 
     * @return ItemTaskBuilder
     */
    public ItemTaskBuilder id(String id) {
        this.id = id;
        return this;
    }
    
    
    /**
     * Add task name field
     * 
     * @param taskName
     * @return {@link ItemTaskBuilder}
     */
    public ItemTaskBuilder taskName(String taskName) {
        this.taskName = taskName;
        return this;
    }
    
    
    /**
     * Add task field
     * 
     * @param task
     * @return {@link ItemTaskBuilder}
     */
    public ItemTaskBuilder task(String task) {
        this.task = task;
        return this;
    }
    
    
    /**
     * Add task state field
     * 
     * @param taskState
     * @return {@link ItemTaskBuilder}
     */
    public ItemTaskBuilder taskState(TaskState taskState) {
        this.taskState = taskState;
        return this;
    }
    
    
    /**
     * Add task logging field
     * 
     * @param taskLog
     * @return {@link ItemTaskBuilder}
     */
    public ItemTaskBuilder taskLog(TaskLogging taskLog) {
        this.taskLog = taskLog;
        return this;
    }
    
    
    /**
     * Construct ItemTask from provided fields
     * 
     * @return {@link ItemTask} 
     */
    @Override
    public ItemTask build() {
        return new ItemTask(id, taskName, task, taskState, taskLog);
    }
}
