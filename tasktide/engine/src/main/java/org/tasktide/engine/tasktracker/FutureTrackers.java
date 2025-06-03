/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.tasktide.engine.tasktracker;

import org.tasktide.core.model.task.ItemTask;
import org.tasktide.core.model.workitem.WorkItem;


/**
 * Holder class for {@link ExecutorServiceTracker} for {@link ItemTask}, and
 *  {@link WorkItem}
 *
 * @author bkenna
 */
public final class FutureTrackers {
    
    // Attributes
    public static final ExecutorServiceTracker<ItemTask> ITEM_TASK_TRACKER = new ExecutorServiceTracker<ItemTask>();
    public static final ExecutorServiceTracker<WorkItem> WORK_ITEM_TRACKER = new ExecutorServiceTracker<WorkItem>();
    
    private FutureTrackers(){}
}
