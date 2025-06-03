/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.tasktide.engine.tasktracker;

import org.tasktide.core.model.task.ItemTask;
import org.tasktide.core.model.workitem.WorkItem;


/**
 * Holder class for generic {@link TaskTracker} for {@link ItemTask}, and {@link WorkItem}
 * 
 * @author bkenna
 */
public final class TaskTrackers {
    
    // Attributes
    public static final GenericTaskTracker ITEM_TASK_TRACKER = new GenericTaskTracker<ItemTask>();
    public static final GenericTaskTracker WORK_ITEM_TRACKER = new GenericTaskTracker<WorkItem>();
    
    private TaskTrackers(){}
}
