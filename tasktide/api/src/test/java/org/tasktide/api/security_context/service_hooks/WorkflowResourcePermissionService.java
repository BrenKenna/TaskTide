/*
 * Copyright 2026 Bren.
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
package org.tasktide.api.security_context.service_hooks;

import java.util.List;

import org.tasktide.core.TaskTideMapper;
import org.tasktide.core.TaskTideService;
import org.tasktide.core.TaskTideRepository;
import org.tasktide.core.model.collection.Workflow;
import org.tasktide.core.services.AbstractTaskTideService;

import org.tasktide.api.security_context.data_models.WorkflowResourcePermission;


/**
 * Service to provide {@link WorkflowResourcePermission} interactions to backend DB ({@link TaskTideRepository}).
 * <br><br>
 * A {@link WorkflowResourcePermission} is linked to a {@link Workflow}
 * 
 * @author Brendan Kenna
 */
public class WorkflowResourcePermissionService
    extends AbstractTaskTideService<WorkflowResourcePermission>
    implements TaskTideMapper<WorkflowResourcePermission, Workflow>
{
    
    /**
     * Constucted with {@link TaskTideRepository}
     * 
     * @param repo 
     */
    public WorkflowResourcePermissionService(TaskTideRepository<WorkflowResourcePermission> repo) {
        super(repo);
    }

    
    /**
     * Get {@link Workflow} collection through provided {@link TaskTideService} link
     * 
     * @param mappingServ
     * @param model
     * @return List-{@link Workflow}
     */
    @Override
    public List<Workflow> getThroughLink(TaskTideService<Workflow> mappingServ, WorkflowResourcePermission model) {
        return mappingServ.viewByField("resourceId", model.getId());
    }
}