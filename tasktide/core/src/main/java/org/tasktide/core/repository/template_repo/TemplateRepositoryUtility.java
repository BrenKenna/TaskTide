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
package org.tasktide.core.repository.template_repo;

import jakarta.nosql.Template;
import jakarta.enterprise.inject.spi.CDI;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import org.eclipse.jnosql.mapping.column.ColumnTemplate;
import org.eclipse.jnosql.mapping.document.DocumentTemplate;
import org.eclipse.jnosql.mapping.graph.GraphTemplate;
import org.eclipse.jnosql.mapping.keyvalue.KeyValueTemplate;

import org.tasktide.core.TaskTideService;
import org.tasktide.core.TaskTideRepository;

import org.tasktide.core.manager.TaskTideServiceManager;
import org.tasktide.core.repository.RepositoryType;
import org.tasktide.core.services.ServiceFactory;

import org.tasktide.core.model.collection.Step;
import org.tasktide.core.model.collection.Workflow;
import org.tasktide.core.model.job_env.JobEnvironment;
import org.tasktide.core.model.job_env.metrics.MetricData;
import org.tasktide.core.model.job_env.metrics.MetricProfile;
import org.tasktide.core.model.workitem.WorkItem;


/**
 * Supports creation of {@link Template} {@link TaskTideRepository},
 *  {@link TaskTideService}, and initialize the {@link TaskTideServiceManager} 
 * 
 * @author bkenna
 */
public class TemplateRepositoryUtility {
    
    // Logging
    private final Logger LOGGER = LogManager.getLogger(TemplateRepositoryUtility.class);
    
    // Attributes
    private static TemplateRepositoryUtility INSTANCE;
    private final String dbType;
    
    
    /**
     * Construct utility
     * 
     * @param dbType 
     */
    private TemplateRepositoryUtility(String dbType) {
        this.dbType = dbType;
    }
    
    
    /**
     * Initialize utility with the DB type, throwing an
     *  illegal state exception if already initialized
     * 
     * @param dbType 
     */
    public static void initialize(String dbType) {
        if ( INSTANCE != null ) {
            throw new IllegalStateException("NoSqlTemplateUtility already initialized");
        }
        INSTANCE = new TemplateRepositoryUtility(dbType);
    }
    
    
    /**
     * Get configured utility, throwing illegal state exception
     *  if not already initialized.
     * 
     * @return TemplateRepositoryUtility
     */
    public static TemplateRepositoryUtility get() {
        if ( INSTANCE != null ) {
            return INSTANCE;
        }
        throw new IllegalStateException("NoSqlTemplate not initialized");
    }
    
    
    /**
     * Fetch Template for template type (ie DocumentTemplate etc)
     * 
     * @return Template
     */
    @SuppressWarnings("unchecked")
    public Template fetchTemplate() {
        Class clazz = provideTemplateClass(this.dbType);
        return (Template) CDI.current().select(clazz).get();
    }
    
    
    
    /**
     * Maps provided NoSQL DB type to Template class
     * 
     * @param dbType
     * @return Class
     */
    public Class provideTemplateClass(String dbType) {
        switch( dbType.toLowerCase() ) {
            case "document" -> {
                return DocumentTemplate.class;
            }
            
            case "keyvalue" -> {
                return KeyValueTemplate.class;
            }
            
            case "column" -> {
                return ColumnTemplate.class;
            }
            
            case "graph" -> {
                return GraphTemplate.class;
            }
            
            default -> {
                throw new IllegalArgumentException("NoSQL DB Type must be one of: Document, KeyValue, Column or Grpah");
            }
        }
    }
    
    
    /**
     * Iniialize the {@link TaskTideServiceManager}
     * 
     */
    public void initServiceManager() {
        
        // Initialize vars
        Template backend;
        TaskTideService<WorkItem> workItemService;
        TaskTideService<Step> stepService;
        TaskTideService<Workflow> workflowService;
        
        // Fetch backend
        backend = this.fetchTemplate();
        
        // Make services
        workItemService = ServiceFactory.makeWorkItemService(RepositoryType.NOSQL, backend, "WorkItem-Service");
        stepService = ServiceFactory.makeStepService(RepositoryType.NOSQL, backend, "Step-Service");
        workflowService = ServiceFactory.makeWorkflowService(RepositoryType.NOSQL, backend, "Workflow-Service");
        
        // Fetch additional
        TaskTideService<MetricData> metricServ = ServiceFactory.makeMetricDataService(RepositoryType.NOSQL, backend, "MetricData");
        TaskTideService<MetricProfile> profileServ = ServiceFactory.makeMetricProfileService(RepositoryType.NOSQL, backend, "MetricProfile");
        TaskTideService<JobEnvironment> jobEnvServ = ServiceFactory.makeJobEnvironmentService(RepositoryType.NOSQL, backend, "JobEnvironment");
        
        // Initialize service manager with services
        TaskTideServiceManager.initialize(workItemService, stepService, workflowService, jobEnvServ, metricServ, profileServ);
        //LOGGER.debug("Displaying configured service manager:\n'{}'", TaskTideServiceManager.toJson());
    }
}