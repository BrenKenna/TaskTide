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
package org.tasktide.engine.worker;

import org.tasktide.core.TaskTideModel;
import org.tasktide.core.model.workitem.WorkItem;
import org.tasktide.core.model.task.ItemTask;


/**
 * Marker interface for TaskTide processing
 * 
 * @author bkenna
 * @param <T> of {@link TaskTideModel}-{@link WorkItem},{@link ItemTask}
 */
public interface TaskTideWorkerUnit<T extends TaskTideModel<T>> {
    
}