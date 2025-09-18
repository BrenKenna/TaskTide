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

import java.util.concurrent.ConcurrentHashMap;
import org.tasktide.core.TaskTideModel;
import org.tasktide.core.manager.command.ManagerTarget;
import org.tasktide.core.TaskTideService;

import org.tasktide.core.model.collection.Step;
import org.tasktide.core.model.collection.Workflow;
import org.tasktide.core.model.job_env.JobEnvironment;
import org.tasktide.core.model.job_env.metrics.MetricData;
import org.tasktide.core.model.job_env.metrics.MetricProfile;
import org.tasktide.core.model.workitem.WorkItem;
import org.tasktide.core.services.JobEnvironmentService;
import org.tasktide.core.services.MetricDataService;
import org.tasktide.core.services.MetricProfileService;

import org.tasktide.core.services.WorkItemService;
import org.tasktide.core.services.StepService;
import org.tasktide.core.services.WorkflowService;


/**
 * Container class {@link TaskTideService} for {@link WorkItem},
 *   {@link Step}, {@link Workflow}, {@link JobEnvironment},
 *   {@link MetricProfile}, and {@link JobEnvironment}
 *
 * @author bkenna
 */
public final class TaskTideServiceManager {
    
    // There can be only one
    private static volatile TaskTideServiceManager INSTANCE;
    private final ConcurrentHashMap<ManagerTarget, TaskTideService> serviceMap;
    
    // Attributes
    @Deprecated(forRemoval = true)
    private final TaskTideService<WorkItem> workItemServ;
    @Deprecated(forRemoval = true)
    private final TaskTideService<Step> stepServ;
    @Deprecated(forRemoval = true)
    private final TaskTideService<Workflow> workflowServ;
    @Deprecated(forRemoval = true)
    private final TaskTideService<MetricData> metricDataServ;
    @Deprecated(forRemoval = true)
    private final TaskTideService<MetricProfile> metricProfileServ;
    @Deprecated(forRemoval = true)
    private final TaskTideService<JobEnvironment> jobEnvServ;
    
    
    /**
     * Package private construction with the {@link TaskTideService}
     * 
     * @param workItemServ
     * @param stepServ
     * @param workflowServ
     */
    @Deprecated(forRemoval = true)
    private TaskTideServiceManager(
        TaskTideService<WorkItem> workItemServ,
        TaskTideService<Step> stepServ,
        TaskTideService<Workflow> workflowServ
    ) {
        this.workItemServ = workItemServ;
        this.stepServ = stepServ;
        this.workflowServ = workflowServ;
        this.metricDataServ = null;
        this.metricProfileServ = null;
        this.jobEnvServ = null;
        this.serviceMap = null;
    }
    
    
    /**
     * Package private construction with the {@link TaskTideService}
     * 
     * @param workItemServ
     * @param stepServ
     * @param workflowServ
     * @param metricDataServ
     * @param metricProfileServ
     * @param jobEnvServ
     */
    private TaskTideServiceManager(
        TaskTideService<WorkItem> workItemServ,
        TaskTideService<Step> stepServ,
        TaskTideService<Workflow> workflowServ,
        TaskTideService<MetricData> metricDataServ,
        TaskTideService<MetricProfile> metricProfileServ,
        TaskTideService<JobEnvironment> jobEnvServ
    ) {
        this.serviceMap = new ConcurrentHashMap<>();
        this.workItemServ = workItemServ;
        this.serviceMap.put(ManagerTarget.WORKITEM, workItemServ);
        this.stepServ = stepServ;
        this.serviceMap.put(ManagerTarget.STEP, stepServ);
        this.workflowServ = workflowServ;
        this.serviceMap.put(ManagerTarget.WORKFLOW, workflowServ);
        this.metricDataServ = metricDataServ;
        this.serviceMap.put(ManagerTarget.METRIC_DATA, metricDataServ);
        this.metricProfileServ = metricProfileServ;
        this.serviceMap.put(ManagerTarget.METRIC_PROFILE, metricProfileServ);
        this.jobEnvServ = jobEnvServ;
        this.serviceMap.put(ManagerTarget.JOB_ENVIRONMENT, jobEnvServ);
    }

    
    /**
     * Returns whether the service manager is initialized
     * 
     * @return boolean
     */
    public static boolean isInitialized() {
        return INSTANCE != null;
    }
    
    
    /**
     * Initialize service manager, throws error if already initialized
     * 
     * @param workItemServ
     * @param stepServ
     * @param workflowServ 
     */
    @Deprecated(forRemoval = true)
    public static synchronized void initialize(
        TaskTideService<WorkItem> workItemServ,
        TaskTideService<Step> stepServ,
        TaskTideService<Workflow> workflowServ
    ) {
        if ( INSTANCE != null ) {
            throw new IllegalStateException("TaskTideServiceManager already initialized");
        }
        INSTANCE = new TaskTideServiceManager(workItemServ, stepServ, workflowServ);
    }
    
    
    /**
     * Initialize service manager, throws error if already initialized
     * 
     * @param workItemServ
     * @param stepServ
     * @param workflowServ 
     * @param metricDataServ 
     * @param metricProfileServ 
     * @param jobEnvServ 
     */
    public static synchronized void initialize(
        TaskTideService<WorkItem> workItemServ,
        TaskTideService<Step> stepServ,
        TaskTideService<Workflow> workflowServ,
        TaskTideService<MetricData> metricDataServ,
        TaskTideService<MetricProfile> metricProfileServ,
        TaskTideService<JobEnvironment> jobEnvServ
    ) {
        if ( INSTANCE != null ) {
            throw new IllegalStateException("TaskTideServiceManager already initialized");
        }
        INSTANCE = new TaskTideServiceManager(workItemServ, stepServ, workflowServ, metricDataServ, metricProfileServ, jobEnvServ);
    }
    

    /**
     * Fetches {@link WorkItemService} if initialized
     * 
     * @return {@link TaskTideService}-{@link WorkItem}
     */
    public static TaskTideService<WorkItem> fetchWorkItemService() {
        if ( INSTANCE != null ) {
            return INSTANCE.getWorkItemService();
        }
        throw new IllegalStateException("TaskTideServiceManager must be initialized first");
    }
    
    
    /**
     * Fetches {@link StepService} if initialized
     * 
     * @return {@link TaskTideService}-{@link Step}
     */
    public static TaskTideService<Step> fetchStepService() {
        if ( INSTANCE != null ) {
            return INSTANCE.getStepService();
        }
        throw new IllegalStateException("TaskTideServiceManager must be initialized first");
    }
    
    
    /**
     * Fetches {@link WorkflowService} if initialized
     * 
     * @return {@link TaskTideService}-{@link Workflow}
     */
    public static TaskTideService<Workflow> fetchWorkflowService() {
        if ( INSTANCE != null ) {
            return INSTANCE.getWorkflowService();
        }
        throw new IllegalStateException("TaskTideServiceManager must be initialized first");
    }
    
    
    /**
     * Fetches {@link MetricDataService} if initialized
     * 
     * @return {@link TaskTideService}-{@link MetricData}
     */
    public static TaskTideService<MetricData> fetchMetricDataService() {
        if ( INSTANCE != null ) {
            return INSTANCE.getMetricDataService();
        }
        throw new IllegalStateException("TaskTideServiceManager must be initialized first");
    }
    
    
    /**
     * Fetches {@link MetricProfileService} if initialized
     * 
     * @return {@link TaskTideService}-{@link MetricProfile}
     */
    public static TaskTideService<MetricProfile> fetchMetricProfileService() {
        if ( INSTANCE != null ) {
            return INSTANCE.getMetricProfileService();
        }
        throw new IllegalStateException("TaskTideServiceManager must be initialized first");
    }
    
    
    /**
     * Fetches {@link JobEnvironmentService} if initialized
     * 
     * @return {@link TaskTideService}-{@link JobEnvironment}
     */
    public static TaskTideService<JobEnvironment> fetchJobEnvironmentService() {
        if ( INSTANCE != null ) {
            return INSTANCE.getJobEnvironmentService();
        }
        throw new IllegalStateException("TaskTideServiceManager must be initialized first");
    }
    
    
    /**
     * Get {@link TaskTideService} for {@link ManagerTarget}
     * 
     * @param <T> of {@link TaskTideModel}
     * @param tgt
     * @return TaskTideService of {@link WorkItem}, {@link Step}, {@link Workflow},
     *      {@link JobEnvironment}, {@link MetricData}, {@link MetricProfile}
     */
    public static <T extends TaskTideModel<T>> TaskTideService<T> getService(ManagerTarget tgt) {
        if ( INSTANCE != null ) {
             return INSTANCE.getServiceFor(tgt);
        }
        throw new IllegalStateException("TaskTideServiceManager must be initialized first");      
    }
    
    
    /**
     * Get {@link TaskTideService} for {@link ManagerTarget}
     * 
     * @param <T>
     * @param tgt
     * @return {@link TaskTideService}
     */
    @SuppressWarnings("unchecked")
    public <T extends TaskTideModel<T>> TaskTideService<T> getServiceFor(ManagerTarget tgt) {
        return INSTANCE.serviceMap.get(tgt);   
    }
    
    
    /**
     * Get {@link WorkItemService}
     * 
     * @return {@link TaskTideService} of {@link WorkItem}
     */
    public TaskTideService<WorkItem> getWorkItemService() {
        return this.workItemServ;
    }
    
    
    /**
     * Get {@link StepService}
     * 
     * @return {@link TaskTideService} of {@link Step}
     */
    public TaskTideService<Step> getStepService() {
        return this.stepServ;
    }
    
    
    /**
     * Get {@link WorkflowService}
     * 
     * @return {@link TaskTideService} of {@link Workflow}
     */
    public TaskTideService<Workflow> getWorkflowService() {
        return this.workflowServ;
    }
    
    
    /**
     * Get {@link MetricDataService}
     * 
     * @return {@link TaskTideService} of {@link MetricData}
     */
    public TaskTideService<MetricData> getMetricDataService() {
        return this.metricDataServ;
    }
    
    
    /**
     * Get {@link MetricProfileService}
     * 
     * @return {@link TaskTideService} of {@link MetricProfile}
     */
    public TaskTideService<MetricProfile> getMetricProfileService() {
        return this.metricProfileServ;
    }
    
    
    /**
     * Get {@link JobEnvironmentService}
     * 
     * @return {@link TaskTideService} of {@link JobEnvironment}
     */
    public TaskTideService<JobEnvironment> getJobEnvironmentService() {
        return this.jobEnvServ;
    }
}