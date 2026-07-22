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


/**
 * Marker interface for TaskTideService, want to restrict eventually.
 * <br><br>
 * Want to keep the {@link TaskTideRepository} away from anywhere except the {@link TaskTideService}
 * 
 * @param <T> of {@link TaskTideModel}-{@link WorkItem},{@link Step},{@link Workflow}
 * @author bkenna
 */
public interface TaskTideService<T extends TaskTideModel<T>> {
    
    /**
     * Get result set size
     * 
     * @return int
     */
    public int getResultSetSize();
    
    
    /**
     * Set result set size
     * 
     * @param nRecords 
     */
    public void setResultSetSize(int nRecords);
    
    
    /**
     * Verifies that the related service was configured
     * 
     * @return boolean
     */
    public boolean isConfigured();
    
    
    /**
     * Append model to backend
     * 
     * @param model
     * @return {@link TaskTideModel} of {@link WorkItem},{@link Step},{@link Workflow}
     */
    public T appendModel(T model);
    
    
    /**
     * Query backend for models having field of value
     * 
     * @param field
     * @param value
     * 
     * @return List-{@link TaskTideModel} of {@link WorkItem},{@link Step},{@link Workflow}
     */
    public List<T> viewByField(String field, Object value);
    
    
    /**
     * Find {@link TaskTideModel} from backend with field and group
     *  having specified value. Step = Name, State = ToDo
     * 
     * @param field
     * @param value
     * @param group
     * @param groupVal
     * @return List-{@link TaskTideModel}
     */
    public List<T> viewByFieldForGroup(String field, Object value, String group, Object groupVal);
    
    
    /**
     * Fetch model collection
     * 
     * @return List-{@link TaskTideModel} of {@link WorkItem},{@link Step},{@link Workflow}
     */
    public List<T> viewAll();
    
    
    /**
     * View all records to {@link TaskTideModel}
     * 
     * @return List-{@link TaskTideModel}
     */
    public List<TaskTideModel> viewAllToTaskTideModel();
    
    
    /**
     * Query backend for models with Id
     * 
     * @param id
     * @return List-{@link TaskTideModel} of {@link WorkItem},{@link Step},{@link Workflow}
     */
    public T fetchById(String id);
    
    
    /**
     * Drop model with Id from backend
     * 
     * @param id
     * @return boolean
     */
    public boolean dropById(String id);
    
    
    /**
     * Update backend model
     * 
     * @param model
     * @return List-{@link TaskTideModel} of {@link WorkItem},{@link Step},{@link Workflow}
     */
    public T updateModel(T model);
    
    
    /**
     * Extend backend with list of {@link TaskTideModel}, measuring imported count against expected
     * 
     * @param toAdd
     * @return boolean
     */
    public boolean extendModel(List<T> toAdd);
    
    
    /**
     * Save cached records
     * 
     * @return int
     */
    public int save();
    
    
    /**
     * Return repository
     * 
     * @return {@link TaskTideRepository} of {@link Workflow},{@link Step},{@link WorkItem}
     */
    public TaskTideRepository<T> getRepo();
}