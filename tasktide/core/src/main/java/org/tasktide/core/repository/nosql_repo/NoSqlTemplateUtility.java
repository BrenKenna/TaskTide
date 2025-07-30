/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.tasktide.core.repository.nosql_repo;

import jakarta.enterprise.inject.spi.CDI;
import jakarta.nosql.Template;

import org.eclipse.jnosql.mapping.column.ColumnTemplate;
import org.eclipse.jnosql.mapping.document.DocumentTemplate;
import org.eclipse.jnosql.mapping.graph.GraphTemplate;
import org.eclipse.jnosql.mapping.keyvalue.KeyValueTemplate;

import org.tasktide.core.TaskTideService;
import org.tasktide.core.manager.TaskTideServiceManager;
import org.tasktide.core.repository.RepositoryType;
import org.tasktide.core.services.ServiceFactory;

import org.tasktide.core.model.collection.Step;
import org.tasktide.core.model.collection.Workflow;
import org.tasktide.core.model.workitem.WorkItem;


/**
 * Supports creation of {@link Template} {@link TaskTideRepository},
 *  {@link TaskTideService}, and initialize the {@link TaskTideServiceManager} 
 * 
 * @author bkenna
 */
public class NoSqlTemplateUtility {
    

    /**
     * Fetch Template for template type (ie DocumentTemplate etc)
     * 
     * @param dbType
     * @return Template
     */
    @SuppressWarnings("unchecked")
    public static Template fetchTemplate(String dbType) {
        Class clazz = provideTemplateClass(dbType);
        return (Template) CDI.current().select(clazz).get();
    }
    
    
    
    /**
     * Maps provided NoSQL DB type to Template class
     * 
     * @param dbType
     * @return Class
     */
    public static Class provideTemplateClass(String dbType) {
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
     * @param backend 
     */
    public static void initServiceManager(Template backend) {
        
        // Initialize vars
        TaskTideService<WorkItem> workItemService;
        TaskTideService<Step> stepService;
        TaskTideService<Workflow> workflowService;
        
        // Make services
        workItemService = ServiceFactory.makeWorkItemService(RepositoryType.NOSQL, backend, "WorkItem-Service");
        stepService = ServiceFactory.makeStepService(RepositoryType.NOSQL, backend, "Step-Service");
        workflowService = ServiceFactory.makeWorkflowService(RepositoryType.NOSQL, backend, "Workflow-Service");
        
        // Return manager
        TaskTideServiceManager.initialize(workItemService, stepService, workflowService);
    }
}
