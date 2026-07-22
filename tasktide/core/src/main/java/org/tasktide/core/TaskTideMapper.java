/*
 * Copyright 2026 Brendan Kenna.
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
package org.tasktide.core;

import java.util.List;

import org.tasktide.core.model.workitem.WorkItem;
import org.tasktide.core.model.collection.Step;
import org.tasktide.core.model.collection.Workflow;
import org.tasktide.core.model.task.ItemTask;

/**
 * 
 * Interface to facilitate model class mapping from
 * <br>
 * <ul>
 *  <li>{@link TaskTideService WorkflowService} -> {@link Step}</li>
 *  <li>{@link TaskTideService StepService} -> {@link WorkItem}</li>
 *  <li>{@link TaskTideService WorkItemService} -> {@link ItemTask}</li>
 * </ul>
 * 
 * @param <T> of {@link Workflow},{@link Step},{@link WorkItem}
 * @param <U> of {@link Step},{@link WorkItem},{@link ItemTask}
 * 
 * @author bkenna
 */
public interface TaskTideMapper<T extends TaskTideModel<T>, U extends TaskTideModel<U>> {
    
    
    /**
     * Map {@link TaskTideModel} to its lower class via the {@link TaskTideService}
     * 
     * @param mappingServ 
     * @param model
     * @return List-{@link Step} from {@link Workflow}, {@link WorkItem} from {@link Step}
     */
    List<U> getThroughLink(TaskTideService<U> mappingServ, T model);
}
