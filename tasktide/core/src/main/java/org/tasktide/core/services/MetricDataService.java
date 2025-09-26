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
package org.tasktide.core.services;

import java.util.List;
import java.util.stream.Collectors;

import org.tasktide.core.TaskTideModel;
import org.tasktide.core.TaskTideRepository;
import org.tasktide.core.TaskTideService;

import org.tasktide.core.model.job_env.metrics.MetricData;
import org.tasktide.core.model.job_env.metrics.MetricProfile;


/**
 * Service to provide {@link MetricData} interactions to backend DB ({@link TaskTideRepository}).
 * <br><br>
 * Service methods include providing MetricData, list of them. Getting list of JobEnvironments be useful, means composing repo though.
 * 
 * @author Brendan Kenna
 */
public class MetricDataService implements TaskTideService<MetricData> {
 
    // Attributes
    private final TaskTideRepository<MetricData> repo;

    /**
     * Constucted with {@link TaskTideRepository}
     * 
     * @param repo 
     */
    public MetricDataService(TaskTideRepository<MetricData> repo) {
        this.repo = repo;
    }

    
    /**
     * Inserts provided record
     * 
     * @param model
     * @return {@link MetricData}
     */
    @Override
    public MetricData appendModel(MetricData model) {
        return this.repo.insertModel(model);
    }

    
    /**
     * Fetch {@link MetricData} with provided field, matching input
     * 
     * @param field
     * @param value
     * @return List-{@link MetricData}
     */
    @Override
    public List<MetricData> viewByField(String field, Object value) {
        return this.repo.findByField(field, value);
    }

    
    /**
     * Fetch {@link MetricData} with provided field, matching input,
     *  and second condition
     * 
     * @param field
     * @param value
     * @param group
     * @param groupVal
     * @return List-{@link MetricData}
     */
    @Override
    public List<MetricData> viewByFieldForGroup(String field, Object value, String group, Object groupVal) {
        return this.repo.findByFieldForGroup(field, value, group, groupVal);
    }

    
    /**
     * Fetch all records
     * 
     * @return List-{@link MetricProfile}
     */
    @Override
    public List<MetricData> viewAll() {
        return this.repo.findAll();
    }

    
    /**
     * Collects records into list of {@link TaskTideModel}
     * 
     * @return List-{@link TaskTideModel}
     */
    @Override
    public List<TaskTideModel> viewAllToTaskTideModel() {
        return this.viewAll()
            .stream()
            .parallel()
            .map(
                elm -> (TaskTideModel<MetricData>) elm
            )
        .collect(Collectors.toList());
    }

    
    /**
     * Fetch record matching queried Id
     * 
     * @return {@link MetricData}
     */
    @Override
    public MetricData fetchById(String id) {
        return this.repo.findById(id).orElse(null);
    }

    
    /**
     * Drop {@link MetricProfile} by Id
     * 
     * @param id
     * @return boolean
     */
    @Override
    public boolean dropById(String id) {
        return this.repo.deleteModel(id);
    }

    
    /**
     * Update {@link MetricData}
     * 
     * @param model
     * @return {@link MetricData}
     */
    @Override
    public synchronized MetricData updateModel(MetricData model) {
        return this.repo.updateModel(model);
    }

    
    /**
     * Extend provided {@link MetricData} list to backend
     * 
     * @param toAdd
     * @return boolean
     */
    @Override
    public synchronized boolean extendModel(List<MetricData> toAdd) {
        return this.repo.extendModel(toAdd);
    }

    
    /**
     * Commit changes to backend
     * 
     * @return int
     */
    @Override
    public synchronized int save() {
        return this.repo.save();
    }

    
    /**
     * Provide repository
     * 
     * @return {@link TaskTideRepository}-{@link MetricData}
     */
    @Override
    public TaskTideRepository<MetricData> getRepo() {
        return this.repo;
    }
}