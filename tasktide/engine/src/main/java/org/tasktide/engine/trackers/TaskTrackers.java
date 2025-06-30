/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.tasktide.engine.trackers;

import org.tasktide.core.model.task.ItemTask;
import org.tasktide.core.model.workitem.WorkItem;


/**
 * Holder class for generic {@link TaskTracker} for {@link ItemTask}, and {@link WorkItem}
 * 
 * @author bkenna
 */
public final class TaskTrackers {
    
    // Attributes
    public static final TaskTracker ITEM_TASK_TRACKER = new TaskTracker<ItemTask>();
    public static final TaskTracker WORK_ITEM_TRACKER = new TaskTracker<WorkItem>();
    
    private TaskTrackers(){}
}
