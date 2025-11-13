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
package org.tasktide.core.manager;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.tasktide.core.TaskTideModel;

import org.tasktide.core.model.collection.Step;
import org.tasktide.core.model.collection.Workflow;
import org.tasktide.core.model.job_env.JobEnvironment;
import org.tasktide.core.model.job_env.JobType;
import org.tasktide.core.supporting.Utils;


/**
 * Collection of static methods to support {@link ManagerTask} to {@link TaskTideModel}.
 * <br><br>1). Handling {@link Step} for {@link WorkItem}
 * <br><br>2). Handling {@link Workflow} for {@link Step}. Where workflow name defaults
 *  to the same as that for the step. Meaning, random "Arbitrary" tasks
 *  can still be viewed as they are, and workflows developed as individual
 *  steps, without interfering with defined workflows (StepA, StepB, StepC)
 * 
 * Maybe some methods for ManagerCommand instead?
 * 
 * @author bkenna
 */
public class TaskTideManagerUtility {
    
    // Attributes
    private static final Logger LOGGER = LogManager.getLogger(TaskTideManagerUtility.class);
    private static final String STEP_ID = "Step-" + Utils.generateSalt();
    private static final String WORKFLOW_ID = "Workflow-" + Utils.generateSalt();
    
    // Perhaps written to a file instead?
    private static final String JOB_ENVIRONMENT_ID = "JobEnvironment-" + Utils.generateSalt();

    
    /**
     * Validate delimiter
     * 
     * @param delim
     * @return
     * @throws IllegalArgumentException 
     */
    public static String handleDelim(String delim) throws IllegalArgumentException {
    
        // Handle delimiter
        if ( delim == null || delim.isBlank() || delim.isEmpty() ) {
            throw new IllegalArgumentException("Delimiter cannot be null or empty");
        }
        
        if ( delim.equals("|") ) {
            delim = "\\|";
        }
        return delim;
    }
    
    
    /**
     * Fetch stepId for provided step, or register as new
     * 
     * @param stepName
     * @return String
     */
    public static String fetchStepId(String stepName) {
        List<Step> steps = TaskTideServiceManager.fetchStepService().viewByField("stepName", stepName);
        if ( steps.isEmpty() ) {
            TaskTideManagerUtility.configureNewStep(stepName);
            return STEP_ID;
        }
        else {
            return steps.get(0).getId();
        }
    }
    
    
    /**
     * Configures a new {@link Step}
     * 
     * @param stepName 
     */
    public static void configureNewStep(String stepName) {
        Step step = BuilderUtility.buildStep(STEP_ID, stepName);
        handleWorkflowForStep(step);
        try {
            TaskTideServiceManager.fetchStepService().appendModel(step);
        }
        catch (Exception ex) {
            LOGGER.warn("Unable to append step. Displaying exception\n", ex);
        }
    }
    
    
    /**
     * Handle {@link Workflow} for {@link Step}. Where workflow name defaults
     *  to the same as that for the step. Meaning, random "Arbitrary" tasks
     *  can still be viewed as they are, and workflows developed as individual
     *  steps, without interfering with defined workflows (StepA, StepB, StepC)
     * 
     * @param step 
     */
    public static void handleWorkflowForStep(Step step) {
        String workflowId;
        if (step.getCollection() == null) {
            List<Workflow> data = TaskTideServiceManager.fetchWorkflowService().viewByField("workflowName", step.getStepName());
            if ( data.isEmpty() ) {
                workflowId = fetchWorkflowId(step.getStepName());
            }
            else {
                workflowId = fetchWorkflowId(data.get(0).getId());
            }
        }
        else {
            workflowId = fetchWorkflowId(step.getCollection());
        }
        step.setWorkflowId(workflowId);
    }
    
    
    /**
     * Fetch workflowId for provided step
     * 
     * @param workflowName
     * @return String
     */
    public static String fetchWorkflowId(String workflowName) {
        List<Workflow> workflows = TaskTideServiceManager.fetchWorkflowService().viewByField("workflowName", workflowName);
        if ( workflows.isEmpty() ) {
            configureNewWorkflow(workflowName);
            return WORKFLOW_ID;
        }
        else {
            return workflows.get(0).getId();
        }
    }
    
    
    /**
     * Configures and imports {@link Workflow}
     * 
     * @param workflowName 
     */
    public static void configureNewWorkflow(String workflowName) {
        
        // Configure workflow
        Workflow workflow = BuilderUtility.buildEmptyWorkflow();
        workflow.setWorkflowName(workflowName);
        workflow.setWorkflowId(WORKFLOW_ID);
        workflow.setWorkflowSteps(Map.of("", BuilderUtility.buildEmptyStep()));
        
        // Upload
        try {
            TaskTideServiceManager.fetchWorkflowService().appendModel(workflow);
        }
        catch (Exception ex) {
            LOGGER.warn("Unable to append workflow. Displaying exception\n", ex);
        }
    }
    
    
    /**
     * Fetch {@link JobEnvironmentId}
     * 
     * @return String
     */
    public static String fetchJobEnvironmentId() {
        
        // Fetch job env
        Optional<JobEnvironment> result = JobType.fetchJobEnvironment();
        if ( !result.isPresent() ) {
            return null;
        }
        
        // Set standard id
        JobEnvironment jobEnv = result.get();
        jobEnv.setId(JOB_ENVIRONMENT_ID);
        
        // Push if not in db
        List<JobEnvironment> jobEnvs = TaskTideServiceManager
            .fetchJobEnvironmentService()
            .viewByFieldForGroup(
                "jobId", jobEnv.getJobId(),
                "arrayInd", jobEnv.getArrayInd()
            );
        if ( !jobEnvs.isEmpty()) {
            return jobEnvs.get(0).getId();
        }
        
        // Otherwise push and return
        TaskTideServiceManager
            .fetchJobEnvironmentService()
        .appendModel(jobEnv);
        return jobEnv.getId();
    }
}