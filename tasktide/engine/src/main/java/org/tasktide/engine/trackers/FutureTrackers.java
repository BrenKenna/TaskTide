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
