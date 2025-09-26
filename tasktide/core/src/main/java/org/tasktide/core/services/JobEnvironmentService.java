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

import org.tasktide.core.TaskTideMapper;
import org.tasktide.core.TaskTideModel;
import org.tasktide.core.TaskTideRepository;
import org.tasktide.core.TaskTideService;

import org.tasktide.core.model.job_env.JobEnvironment;
import org.tasktide.core.model.job_env.metrics.MetricProfile;


/**
 * Service to provide {@link JobEnvironment} interactions to backend DB ({@link TaskTideRepository}).
 * <br><br>
 * A step has a list of {@link MetricProfile} associated with it.
 * <br><br>
 * Service methods include providing JobEnvironment, list of them. Getting list of MetricProfiles be useful, means composing repo though.
 * <br><br>
 * Implementing the {@link TaskTideMapper} allows mapping of {@link JobEnvironment} to {@link MetricProfile}
 * 
 * @author Brendan Kenna
 */
public class JobEnvironmentService implements TaskTideMapper<JobEnvironment, MetricProfile>, TaskTideService<JobEnvironment> {
    
    // Attributes
    private final TaskTideRepository<JobEnvironment> repo;

    
    /**
     * Constucted with {@link TaskTideRepository}
     * 
     * @param repo 
     */
    public JobEnvironmentService(TaskTideRepository<JobEnvironment> repo) {
        this.repo = repo;
    }

    
    /**
     * Get {@link MetricProfile} collection through provided {@link TaskTideService} link
     * 
     * @param mappingServ
     * @param model
     * @return List-{@link MetricProfile}
     */
    @Override
    public List<MetricProfile> getThroughLink(TaskTideService<MetricProfile> mappingServ, JobEnvironment model) {
        return mappingServ.viewByField("jobEnvId", model.getId());
    }

    
    /**
     * Inserts provided record
     * 
     * @param model
     * @return {@link JobEnvironment}
     */
    @Override
    public JobEnvironment appendModel(JobEnvironment model) {
        return this.repo.insertModel(model);
    }

    
    /**
     * Fetch {@link JobEnvironment} with provided field, matching input
     * 
     * @param field
     * @param value
     * @return List-{@link JobEnvironment}
     */
    @Override
    public List<JobEnvironment> viewByField(String field, Object value) {
        return this.repo.findByField(field, value);
    }

    
    /**
     * Fetch {@link JobEnvironment} with provided field, matching input,
     *  and second condition
     * 
     * @param field
     * @param value
     * @param group
     * @param groupVal
     * @return List-{@link JobEnvironment}
     */
    @Override
    public List<JobEnvironment> viewByFieldForGroup(String field, Object value, String group, Object groupVal) {
        return this.repo.findByFieldForGroup(field, value, group, groupVal);
    }

    
    /**
     * Fetch all records
     * 
     * @return List-{@link JobEnvironment}
     */
    @Override
    public List<JobEnvironment> viewAll() {
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
                elm -> (TaskTideModel<JobEnvironment>) elm
            )
        .collect(Collectors.toList());
    }

    
    /**
     * Fetch record matching queried Id
     * 
     * @return {@link JobEnvironment}
     */
    @Override
    public JobEnvironment fetchById(String id) {
        return this.repo.findById(id).orElse(null);
    }

    
    /**
     * Drop {@link JobEnvironment} by Id
     * 
     * @param id
     * @return boolean
     */
    @Override
    public boolean dropById(String id) {
        return this.repo.deleteModel(id);
    }

    
    /**
     * Update {@link JobEnvironment}
     * 
     * @param model
     * @return {@link JobEnvironment}
     */
    @Override
    public synchronized JobEnvironment updateModel(JobEnvironment model) {
        return this.repo.updateModel(model);
    }

    
    /**
     * Extend provided {@link JobEnvironment} list to backend
     * 
     * @param toAdd
     * @return boolean
     */
    @Override
    public synchronized boolean extendModel(List<JobEnvironment> toAdd) {
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
     * @return {@link TaskTideRepository}-{@link JobEnvironment}
     */
    @Override
    public TaskTideRepository<JobEnvironment> getRepo() {
        return this.repo;
    }
}