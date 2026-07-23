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

import java.util.HashMap;
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

// For JavaDocs
import org.tasktide.core.model.workitem.WorkItem;

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
    private static String STEP_ID = "Step-" + Utils.generateSalt();
    private static String WORKFLOW_ID = "Workflow-" + Utils.generateSalt();
    
    // Perhaps written to a file instead?
    private static final String JOB_ENVIRONMENT_ID = "JobEnvironment-" + Utils.generateSalt();

    
    public static void updateStepId() {
        STEP_ID = "Step-" + Utils.generateSalt();
    }
    
    
    public static void updateWorkflowId() {
        WORKFLOW_ID = "Workflow-" + Utils.generateSalt();
    }
    
    
    public static void updateStepWorkflowIds() {
        updateStepId();
        updateWorkflowId();
    }
    
    
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
     * Fetch workflowId for workflow name if present
     * 
     * @param workflowName
     * 
     * @return String
     */
    public static String fetchWorkflowIdForName(String workflowName) {
        List<Workflow> workflows = TaskTideServiceManager.fetchWorkflowService().viewByField("workflowName", workflowName);
        if ( workflows.isEmpty() ) {
            return "";
        }
        else {
            return workflows.get(0).getId();
        }
    }
    
    
    /**
     * Fetch workflowId for workflow name if present
     * 
     * @param workflowName
     * 
     * @return String
     */
    public static Workflow fetchWorkflowForName(String workflowName) {
        List<Workflow> workflows = TaskTideServiceManager.fetchWorkflowService().viewByField("workflowName", workflowName);
        if ( workflows.isEmpty() ) {
            return null;
        }
        else {
            return workflows.get(0);
        }
    }
    
    
    /**
     * Fetch stepId for step name if present
     * 
     * @param stepName
     * 
     * @return String
     */
    public static String fetchStepIdForName(String stepName) {
        List<Step> steps = TaskTideServiceManager.fetchStepService().viewByField("stepName", stepName);
        if ( steps.isEmpty() ) {
            return "";
        }
        else {
            return steps.get(0).getId();
        }
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
            LOGGER.info("No step detected, begining creation for:\n'{}'", stepName);
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
    
    
    public static void configureNewStepNewId(String stepName) {
        updateStepWorkflowIds();
        Step step = BuilderUtility.buildStep(stepName);
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
            LOGGER.info("No workflow assigned to step, proceeding with StepName:\n'{}'", step.toJsonDoc());
            List<Workflow> data = TaskTideServiceManager.fetchWorkflowService().viewByField("workflowName", step.getStepName());
            if ( data.isEmpty() ) {
                LOGGER.info("No workflow detected, configuring for:\t'{}'", step.getStepName());
                workflowId = fetchWorkflowId(step.getStepName(), step);
            }
            else {
                LOGGER.info("Previous workflow under step detected:\t'{}'", step.getStepName());
                workflowId = fetchWorkflowId(data.get(0).getId(), step);
            }
        }
        else {
            LOGGER.info("Checking backend for workflow assignment to below Step:\t'{}'\n\n'{}'", step.getCollection(), step.toJsonDoc());
            workflowId = fetchWorkflowId(step.getCollection(), step);
        }
        step.setWorkflowId(workflowId);
    }
    
    
    /**
     * Fetch workflowId for provided step
     * 
     * @param workflowName
     * @param step
     * @return String
     */
    public static String fetchWorkflowId(String workflowName, Step step) {
        LOGGER.info("Fetching workflow for query:\t'{}'", workflowName);
        List<Workflow> workflows = TaskTideServiceManager.fetchWorkflowService().viewByField("workflowName", workflowName);
        if ( workflows.isEmpty() ) {
            LOGGER.info("No workflow detected, configuring:\t'{}'", workflowName);
            configureNewWorkflow(workflowName, step);
            return WORKFLOW_ID;
        }
        else {
            LOGGER.info("Workflow detected:\t'{}'", workflows.get(0).getId());
            return workflows.get(0).getId();
        }
    }
    
    
    /**
     * Configures and imports {@link Workflow}
     * 
     * @param workflowName 
     * @param step 
     */
    public static void configureNewWorkflow(String workflowName, Step step) {
        
        // Configure workflow
        Workflow workflow = BuilderUtility.buildEmptyWorkflow();
        workflow.setWorkflowName(workflowName);
        workflow.setWorkflowId(WORKFLOW_ID);
        workflow.setWorkflowSteps(Map.of(step.getId(), step));
        
        // Upload
        try {
            TaskTideServiceManager.fetchWorkflowService().appendModel(workflow);
        }
        catch (Exception ex) {
            LOGGER.warn("Unable to append workflow. Displaying exception\n", ex);
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
        workflow.setWorkflowSteps(new HashMap<>());
        
        // Upload
        try {
            TaskTideServiceManager.fetchWorkflowService().appendModel(workflow);
        }
        catch (Exception ex) {
            LOGGER.warn("Unable to append workflow. Displaying exception\n", ex);
        }
    }
    
    
    /**
     * Fetch {@link JobEnvironment} Id
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
            .viewByField(
                "host", jobEnv.getHostname()
            )
        ;
        if ( !jobEnvs.isEmpty()) {
            return jobEnvs.get(0).getId();
        }
        
        // Otherwise push and return
        TaskTideServiceManager
            .fetchJobEnvironmentService()
        .appendModel(jobEnv);
        return jobEnv.getId();
    }
    
    
    
    /**
     * Fetch {@link JobEnvironment} Id
     * 
     * @param arrIndOrHost
     * 
     * @return String
     */
    public static String fetchJobEnvironmentId(boolean arrIndOrHost) {
        
        // Fetch job env
        Optional<JobEnvironment> result = JobType.fetchJobEnvironment();
        if ( !result.isPresent() ) {
            return null;
        }
        
        // Set standard id
        JobEnvironment jobEnv = result.get();
        jobEnv.setId(JOB_ENVIRONMENT_ID);
        
        // Push if not in db
        List<JobEnvironment> jobEnvs = fetchEnvs(jobEnv, arrIndOrHost);
        if ( !jobEnvs.isEmpty()) {
            return jobEnvs.get(0).getId();
        }
        
        // Otherwise push and return
        TaskTideServiceManager
            .fetchJobEnvironmentService()
        .appendModel(jobEnv);
        return jobEnv.getId();
    }
    
    
    /**
     * Fetch {@link JobEnvironment} matching jobId-arrayInd, or hostname
     * 
     * @param jobEnv
     * @param arrIndOrHost
     * 
     * @return List-{@link JobEnvironment}
     */
    public static List<JobEnvironment> fetchEnvs(JobEnvironment jobEnv, boolean arrIndOrHost) {
    
       if ( arrIndOrHost ) {
           return TaskTideServiceManager
                .fetchJobEnvironmentService()
                .viewByFieldForGroup(
                    "jobId", jobEnv.getJobId(),
                    "arrayInd", jobEnv.getArrayInd()
                )
            ;
       }
       
       else {
            return TaskTideServiceManager
                .fetchJobEnvironmentService()
                .viewByField(
                    "host", jobEnv.getHostname()
                )
            ;
       }
    }
    
    
    /**
     * Add {@link Workflow}
     * 
     * @param workflowName
     * 
     * @return {@link Workflow}
     */
    public static Workflow addWorkflow(String workflowName) {
        
        // Check if workflow already exists
        if ( !TaskTideServiceManager.fetchWorkflowService().viewByField("workflowName", workflowName).isEmpty() ) {
            LOGGER.warn("Workflow already exists for:\t'{}'", workflowName);
            return null;
        }
        
        // Configure workflow
        Workflow workflow = BuilderUtility
            .buildWorkflow(workflowName);
        
        // Import record
        return TaskTideServiceManager
            .fetchWorkflowService()
        .appendModel(workflow);
    }
}