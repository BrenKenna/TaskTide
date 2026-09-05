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
package org.tasktide.api.security_context.service_hooks;

import java.util.concurrent.ConcurrentHashMap;

import org.tasktide.core.TaskTideModel;
import org.tasktide.core.TaskTideService;
import org.tasktide.core.supporting.JsonUtils;
import org.tasktide.core.manager.command.ManagerTarget;

import org.tasktide.api.security_context.data_models.StepResourcePermission;
import org.tasktide.api.security_context.data_models.WorkItemResourcePermission;
import org.tasktide.api.security_context.data_models.WorkflowResourcePermission;
import org.tasktide.api.security_context.data_models.MetricDataResourcePermission;
import org.tasktide.api.security_context.data_models.JobEnvironmentResourcePermission;
import org.tasktide.api.security_context.data_models.MetricProfileResourcePermission;


/**
 * Container class for {@link WorkItemResourcePermission},
 *   {@link StepResourcePermission}, {@link WorkflowResourcePermission}, {@link JobEnvironmentResourcePermission},
 *   {@link MetricProfileResourcePermission}, and {@link JobEnvironmentResourcePermission}
 *
 * @author bkenna
 */
public final class TaskTideResourcePermissionServiceManager {
    
    // There can be only one
    private static volatile TaskTideResourcePermissionServiceManager INSTANCE;
    private final ConcurrentHashMap<ManagerTarget, TaskTideService> serviceMap;
    
    /**
     * Package private construction with the {@link TaskTideService}
     * 
     * @param workItemServ
     * @param stepServ
     * @param workflowServ
     */
    private TaskTideResourcePermissionServiceManager(
        TaskTideService<WorkItemResourcePermission> workItemServ,
        TaskTideService<StepResourcePermission> stepServ,
        TaskTideService<WorkflowResourcePermission> workflowServ
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
    private TaskTideResourcePermissionServiceManager(
        TaskTideService<WorkItemResourcePermission> workItemServ,
        TaskTideService<StepResourcePermission> stepServ,
        TaskTideService<WorkflowResourcePermission> workflowServ,
        TaskTideService<JobEnvironmentResourcePermission> jobEnvServ,
        TaskTideService<MetricDataResourcePermission> metricDataServ,
        TaskTideService<MetricProfileResourcePermission> metricProfileServ
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
        TaskTideService<WorkItemResourcePermission> workItemServ,
        TaskTideService<StepResourcePermission> stepServ,
        TaskTideService<WorkflowResourcePermission> workflowServ
    ) {
        if ( INSTANCE != null ) {
            throw new IllegalStateException("TaskTideServiceManager already initialized");
        }
        INSTANCE = new TaskTideResourcePermissionServiceManager(workItemServ, stepServ, workflowServ);
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
        TaskTideService<WorkItemResourcePermission> workItemServ,
        TaskTideService<StepResourcePermission> stepServ,
        TaskTideService<WorkflowResourcePermission> workflowServ,
        TaskTideService<JobEnvironmentResourcePermission> jobEnvServ,
        TaskTideService<MetricDataResourcePermission> metricDataServ,
        TaskTideService<MetricProfileResourcePermission> metricProfileServ
    ) {
        if ( INSTANCE != null ) {
            throw new IllegalStateException("TaskTideServiceManager already initialized");
        }
        INSTANCE = new TaskTideResourcePermissionServiceManager(workItemServ, stepServ, workflowServ, jobEnvServ, metricDataServ, metricProfileServ);
    }
    

    /**
     * Fetches {@link WorkItemResourcePermissionService} if initialized
     * 
     * @return {@link TaskTideService}-{@link WorkItemResourcePermission}
     */
    public static TaskTideService<WorkItemResourcePermission> fetchWorkItemService() {
        if ( INSTANCE != null ) {
            return INSTANCE.getServiceFor(ManagerTarget.WORKITEM);
        }
        throw new IllegalStateException("TaskTideServiceManager must be initialized first");
    }
    
    
    /**
     * Fetches {@link StepResourcePermissionService} if initialized
     * 
     * @return {@link TaskTideService}-{@link StepResourcePermission}
     */
    public static TaskTideService<StepResourcePermission> fetchStepService() {
        if ( INSTANCE != null ) {
            return INSTANCE.getServiceFor(ManagerTarget.STEP);
        }
        throw new IllegalStateException("TaskTideServiceManager must be initialized first");
    }
    
    
    /**
     * Fetches {@link WorkflowResourcePermissionService} if initialized
     * 
     * @return {@link TaskTideService}-{@link WorkflowResourcePermission}
     */
    public static TaskTideService<WorkflowResourcePermission> fetchWorkflowService() {
        if ( INSTANCE != null ) {
            return INSTANCE.getServiceFor(ManagerTarget.WORKFLOW);
        }
        throw new IllegalStateException("TaskTideServiceManager must be initialized first");
    }
    
    
    /**
     * Fetches {@link MetricDataResourcePermissionService} if initialized
     * 
     * @return {@link TaskTideService}-{@link MetricDataResourcePermission}
     */
    public static TaskTideService<MetricDataResourcePermission> fetchMetricDataService() {
        if ( INSTANCE != null ) {
            return INSTANCE.getServiceFor(ManagerTarget.METRIC_DATA);
        }
        throw new IllegalStateException("TaskTideServiceManager must be initialized first");
    }
    
    
    /**
     * Fetches {@link MetricProfileResourcePermission} if initialized
     * 
     * @return {@link TaskTideService}-{@link MetricProfileResourcePermission}
     */
    public static TaskTideService<MetricProfileResourcePermission> fetchMetricProfileService() {
        if ( INSTANCE != null ) {
            return INSTANCE.getServiceFor(ManagerTarget.METRIC_PROFILE);
        }
        throw new IllegalStateException("TaskTideServiceManager must be initialized first");
    }
    
    
    /**
     * Fetches {@link JobEnvironmentResourcePermissionService} if initialized
     * 
     * @return {@link TaskTideService}-{@link JobEnvironmentResourcePermission}
     */
    public static TaskTideService<JobEnvironmentResourcePermission> fetchJobEnvironmentService() {
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
     * @return TaskTideService of {@link WorkItemResourcePermission}, {@link StepResourcePermission}, {@link WorkflowResourcePermission},
     *      {@link JobEnvironmentResourcePermission}, {@link MetricDataResourcePermission}, {@link MetricProfileResourcePermission}
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