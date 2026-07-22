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
package org.tasktide.core.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.tasktide.core.TaskTideModel;
import org.tasktide.core.TaskTideRepository;
import org.tasktide.core.TaskTideService;


/**
 * Implements the upper level CRUD logic for {@link TaskTideService}
 *  against {@link TaskTideRepository} for related {@link TaskTideModel}.
 *   Allowing concrete children to decorate these processes if needs be
 *
 * @author Brendan Kenna
 * @param <T>
 */
public class AbstractTaskTideService<T extends TaskTideModel<T>>
    implements TaskTideService<T>
{
    
    // Attributes
    protected final TaskTideRepository<T> repo;
    protected int resultSetSize;
    
    /**
     * Construct {@link TaskTideService} with {@link TaskTideRepository}
     * 
     * @param repo 
     */
    public AbstractTaskTideService(TaskTideRepository<T> repo) {
        this.repo = repo;
    }
    
    
    /**
     * Construct {@link TaskTideService} with {@link TaskTideRepository}
     * 
     * @param repo 
     * @param resultSetSize 
     */
    public AbstractTaskTideService(TaskTideRepository<T> repo, int resultSetSize) {
        this.repo = repo;
        this.resultSetSize = resultSetSize;
    }

    
    /**
     * Checks that the {@link TaskTideRepository} property
     *  is not null
     * 
     * @return boolean
     */
    @Override
    public boolean isConfigured() {
        return this.repo != null;
    }
    
    
    /**
     * Appends provided {@link TaskTideModel} to backend {@link TaskTideRepository}
     * 
     * @param model
     * @return inserted {@link TaskTideModel}
     */
    @Override
    public synchronized T appendModel(T model) {
        return repo.insertModel(model);
    }

    
    /**
     * Queries backend {@link TaskTideRepository} for {@link TaskTideModel}
     *   records matching provided
     * 
     * @param field
     * @param value
     * @return List-{@link TaskTideModel}
     */
    @Override
    public synchronized List<T> viewByField(String field, Object value) {
        return repo.findByField(field, value);
    }

    
    /**
     * Queries backend {@link TaskTideRepository} for {@link TaskTideModel}
     *   records matching provided value for provided group
     * 
     * @param field
     * @param value
     * @return List-{@link TaskTideModel}
     */
    @Override
    public synchronized List<T> viewByFieldForGroup(String field, Object value, String group, Object groupVal) {
        return repo.findByFieldForGroup(field, value, group, groupVal);
    }

    
    /**
     * Fetches all records from backend {@link TaskTideRepository}
     * 
     * @return List-{@link TaskTideModel}
     */
    @Override
    public List<T> viewAll() {
        return repo.findAll();
    }

    
    /**
     * View all records casted to interface class
     *  from backend {@link TaskTideRepository}
     * 
     * @return List-{@link TaskTideModel}
     */
    @Override
    public synchronized List<TaskTideModel> viewAllToTaskTideModel() {
        return this.viewAll()
            .stream()
            .parallel()
            .map(elm -> (TaskTideModel<T>) elm)
        .collect(Collectors.toList());
    }

    
    /**
     * Fetches record matching queried Id
     *  from backend {@link TaskTideRepository}
     * 
     * @param id
     * @return {@link TaskTideModel}
     */
    @Override
    public synchronized T fetchById(String id) {
        Optional<T> res = repo.findById(id);
        if ( res.isPresent() ) {
            return res.get();
        }
        else {
            return null;
        }
    }

    
    /**
     * Drops the record for provided Id
     *  from backend {@link TaskTideRepository}
     * 
     * @param id
     * @return boolean
     */
    @Override
    public synchronized boolean dropById(String id) {
        return repo.deleteModel(id);
    }

    
    /**
     * Replaces provided record in backend {@link TaskTideRepository}
     * 
     * @param model
     * @return updated {@link TaskTideModel}
     */
    @Override
    public synchronized T updateModel(T model) {
        return repo.updateModel(model);
    }

    
    /**
     * Inserts provided records to backend {@link TaskTideRepository}
     * 
     * @param toAdd
     * @return boolean
     */
    @Override
    public synchronized boolean extendModel(List<T> toAdd) {
        return repo.extendModel(toAdd);
    }

    
    /**
     * Saves backend {@link TaskTideRepository}
     * 
     * @return int
     */
    @Override
    public synchronized int save() {
        return repo.save();
    }

    
    /**
     * Provides backend {@link TaskTideRepository}
     * 
     * @return {@link TaskTideRepository}-{@link TaskTideModel}
     */
    @Override
    public TaskTideRepository<T> getRepo() {
        return this.repo;
    }

    
    /**
     * Gets result set size
     * 
     * @return 
     */
    @Override
    public int getResultSetSize() {
        return this.resultSetSize;
    }

    
    /**
     * Sets new result set size
     * 
     * @param nRecords 
     */
    @Override
    public void setResultSetSize(int nRecords) {
        this.resultSetSize = nRecords;
        this.repo.setResultSetSize(nRecords);
    }
    
    
    
}