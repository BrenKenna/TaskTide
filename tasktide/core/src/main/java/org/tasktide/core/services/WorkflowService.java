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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.tasktide.core.TaskTideRepository;
import org.tasktide.core.TaskTideService;
import org.tasktide.core.TaskTideMapper;

import org.tasktide.core.model.collection.Workflow;
import org.tasktide.core.model.collection.Step;
import org.tasktide.core.model.state_summary.StateSummary;
import org.tasktide.core.model.workitem.ItemState;


/**
 *
 * Service to provide WorkflowService interactions to backend DB
 * 
 * @author bkenna
 */
public class WorkflowService extends AbstractTaskTideService<Workflow>
    implements TaskTideMapper<Workflow, Step>
{
  
    /**
     * Construct with repo for testing
     *
     * @param repo
     */
    public WorkflowService(TaskTideRepository<Workflow> repo) {
        super(repo);
    }

    
    /**
     * Add {@link Step Step} from {@link Workflow}
     *
     * @param workflow
     * @param step
     * @return {@link Workflow}
     */
    public synchronized Workflow addStepToWorkflow(Workflow workflow, Step step) {
        workflow.getWorkflowSteps().put(step.getId(), step);
        return repo.updateModel(workflow);
    }

    
    /**
     * Drop {@link Step Step} from {@link Workflow}
     *
     * @param workflow
     * @param step
     * @return boolean
     */
    public synchronized boolean dropStepFromWorkflow(Workflow workflow, Step step) {
        workflow.getWorkflowSteps().remove(step.getId());
        return repo.deleteModel(workflow.getWorkflowId());
    }
    
    
    /**
     * Fetch steps for {@link Workflow}
     *
     * @param mappingServ
     * @param model
     * @return List-{@link Step}
     */
    @Override
    public synchronized List<Step> getThroughLink(TaskTideService<Step> mappingServ, Workflow model) {
        return new ArrayList<>(model.getWorkflowSteps().values());
    }

    
    /**
     * Represent service as string
     *
     * @return String
     */
    @Override
    public synchronized String toString() {
        return "WorkflowService{"
            + "WorkflowType=Workflow"
            + ",ServiceLink=Step"
        + '}';
    }
}