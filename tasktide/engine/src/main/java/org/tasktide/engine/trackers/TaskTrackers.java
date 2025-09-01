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
