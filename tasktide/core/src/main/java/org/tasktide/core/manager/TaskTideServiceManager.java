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
import org.tasktide.core.supporting.JsonUtils;


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
    
    /**
     * Package private construction with the {@link TaskTideService}
     * 
     * @param workItemServ
     * @param stepServ
     * @param workflowServ
     */
    private TaskTideServiceManager(
        TaskTideService<WorkItem> workItemServ,
        TaskTideService<Step> stepServ,
        TaskTideService<Workflow> workflowServ
    ) {
        this.serviceMap = new ConcurrentHashMap<>();
        this.serviceMap.put(ManagerTarget.WORKITEM, workItemServ);
        this.serviceMap.put(ManagerTarget.STEP, stepServ);
        this.serviceMap.put(ManagerTarget.WORKFLOW, workflowServ);
    }
    
    
    /**
     * Package private construction with the {@link TaskTideService}
     * 
     * @param workItemServ
     * @param stepServ
     * @param workflowServ
     * @param jobEnvServ
     * @param metricDataServ
     * @param metricProfileServ
     */
    private TaskTideServiceManager(
        TaskTideService<WorkItem> workItemServ,
        TaskTideService<Step> stepServ,
        TaskTideService<Workflow> workflowServ,
        TaskTideService<JobEnvironment> jobEnvServ,
        TaskTideService<MetricData> metricDataServ,
        TaskTideService<MetricProfile> metricProfileServ
    ) {
        this.serviceMap = new ConcurrentHashMap<>();
        this.serviceMap.put(ManagerTarget.WORKITEM, workItemServ);
        this.serviceMap.put(ManagerTarget.STEP, stepServ);
        this.serviceMap.put(ManagerTarget.WORKFLOW, workflowServ);
        this.serviceMap.put(ManagerTarget.METRIC_DATA, metricDataServ);
        this.serviceMap.put(ManagerTarget.METRIC_PROFILE, metricProfileServ);
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
     * @param jobEnvServ 
     * @param metricDataServ 
     * @param metricProfileServ 
     */
    public static synchronized void initialize(
        TaskTideService<WorkItem> workItemServ,
        TaskTideService<Step> stepServ,
        TaskTideService<Workflow> workflowServ,
        TaskTideService<JobEnvironment> jobEnvServ,
        TaskTideService<MetricData> metricDataServ,
        TaskTideService<MetricProfile> metricProfileServ
    ) {
        if ( INSTANCE != null ) {
            throw new IllegalStateException("TaskTideServiceManager already initialized");
        }
        INSTANCE = new TaskTideServiceManager(workItemServ, stepServ, workflowServ, jobEnvServ, metricDataServ, metricProfileServ);
    }
    

    /**
     * Fetches {@link WorkItemService} if initialized
     * 
     * @return {@link TaskTideService}-{@link WorkItem}
     */
    public static TaskTideService<WorkItem> fetchWorkItemService() {
        if ( INSTANCE != null ) {
            return INSTANCE.getServiceFor(ManagerTarget.WORKITEM);
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
            return INSTANCE.getServiceFor(ManagerTarget.STEP);
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
            return INSTANCE.getServiceFor(ManagerTarget.WORKFLOW);
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
            return INSTANCE.getServiceFor(ManagerTarget.METRIC_DATA);
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
            return INSTANCE.getServiceFor(ManagerTarget.METRIC_PROFILE);
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
            return INSTANCE.getServiceFor(ManagerTarget.JOB_ENVIRONMENT);
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
            if ( tgt.isManagerTarget(ManagerTarget.MANAGERTASK) ) {
                tgt = ManagerTarget.WORKITEM;
            }
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
        if ( tgt.isManagerTarget(ManagerTarget.MANAGERTASK) ) {
            tgt = ManagerTarget.WORKITEM;
        }
        return INSTANCE.serviceMap.get(tgt);
    }
    
    
    /**
     * Represent as String
     * 
     * @return String
     */
    @Override
    public String toString() {
        return "TaskTideServiceManager{" +
            "serviceMap=" + serviceMap +
        '}';
    }
    
    
    /**
     * Represent as JSON string
     * 
     * @return String
     */
    public static String toJson() {
        if ( INSTANCE != null ) {
             return JsonUtils.toJson(true, INSTANCE.serviceMap);
        }
        throw new IllegalStateException("TaskTideServiceManager must be initialized first");  
    }
}