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

import org.tasktide.core.TaskTideMapper;
import org.tasktide.core.TaskTideRepository;
import org.tasktide.core.TaskTideService;

import org.tasktide.core.model.job_env.JobEnvironment;
import org.tasktide.core.model.job_env.metrics.MetricProfile;


/**
 * Service to provide {@link MetricProfile} interactions to backend DB ({@link TaskTideRepository}).
 * <br><br>
 * A {@link MetricProfile} has a single {@link JobEnvironment} associated with it.
 * <br><br>
 * Service methods include providing MetricProfile, list of them. Getting list of JobEnvironments be useful, means composing repo though.
 * <br><br>
 * Implementing the {@link TaskTideMapper} allows mapping of {@link MetricProfile} to {@link JobEnvironment}
 * 
 * @author Brendan Kenna
 */
public class MetricProfileService extends AbstractTaskTideService<MetricProfile>
    implements TaskTideMapper<MetricProfile, JobEnvironment>
{
    
    /**
     * Constucted with {@link TaskTideRepository}
     * 
     * @param repo 
     */
    public MetricProfileService(TaskTideRepository<MetricProfile> repo) {
        super(repo);
    }

    
    /**
     * Get {@link JobEnvironment} collection through provided {@link TaskTideService} link
     * 
     * @param mappingServ
     * @param model
     * @return List-{@link JobEnvironment}
     */
    @Override
    public List<JobEnvironment> getThroughLink(TaskTideService<JobEnvironment> mappingServ, MetricProfile model) {
        return List.of(mappingServ.fetchById(model.getJobEnvId()));
    }
}