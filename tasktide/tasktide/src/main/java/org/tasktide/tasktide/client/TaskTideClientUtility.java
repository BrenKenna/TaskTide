/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.tasktide.tasktide.client;

import jakarta.enterprise.inject.se.SeContainer;
import jakarta.enterprise.inject.se.SeContainerInitializer;
import jakarta.enterprise.inject.spi.CDI;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jakarta.nosql.Template;
import org.eclipse.jnosql.mapping.column.ColumnTemplate;
import org.eclipse.jnosql.mapping.document.DocumentTemplate;
import org.eclipse.jnosql.mapping.graph.GraphTemplate;
import org.eclipse.jnosql.mapping.keyvalue.KeyValueTemplate;

import org.tasktide.core.TaskTideService;
import org.tasktide.core.services.ServiceFactory;
import org.tasktide.core.repository.RepositoryType;
import org.tasktide.core.manager.TaskTideServiceManager;

import org.tasktide.core.model.collection.Step;
import org.tasktide.core.model.collection.Workflow;
import org.tasktide.core.model.workitem.WorkItem;

import org.tasktide.tasktide.configurer.GlobalConfig;
import org.tasktide.tasktide.containerprovider.CdiContainerProvider;
import org.tasktide.tasktide.parser.ArgumentTree;


/**
 * Utility class for {@link TaskTideClient} interface
 * 
 * @author bkenna
 */
public class TaskTideClientUtility {
    
    // Attributes
    private static final Logger LOGGER = LogManager.getLogger(TaskTideClientUtility.class);
    
    
    /**
     * Fetch the specific client to configure and run
     * 
     * @param argTree
     * @return {@link TaskTideClientType}
     */
    public static TaskTideClientType configureClient(ArgumentTree argTree) {
        String cliString = (String) argTree.getGlobalArguments().getArgMap().get("Client").getValue();
        return TaskTideClientType.valueOf(cliString);
    }

    
    /**
     * Fetches the {@link RepositoryType} from the {@link GlobalConfig}.
     *   Returns NOSQL atm
     * 
     * @param configMap
     * @return {@link RepositoryType}
     */
    public static RepositoryType fetchRepoType(ClientConfigMap configMap) {
        return RepositoryType.NOSQL;
    }
    
    
    /**
     * Fetches {@link TaskTideServiceManager} from {@link CdiContainerProvider} using
     *  the value of the {@link RepositoryType}
     * 
     * @param cdiProvider
     * @param repoType
     * @return 
     */
    @SuppressWarnings("unchecked")
    public static TaskTideServiceManager fetchManager(CdiContainerProvider cdiProvider, RepositoryType repoType) {
        switch ( repoType ) {
            case NOSQL -> {
                //SeContainer weldContainer = (SeContainer) cdiProvider.getContainer();
                Template backend = CDI.current().select(DocumentTemplate.class).get();
                //Template backend = (DocumentTemplate) cdiProvider.getBean(DocumentTemplate.class);
                return fetchManager(backend);
            }
            
            default -> {
                return null;
            }
        }
    }
    
    
    /**
     * Fetch {@link TaskTideServiceManager} for {@link Template} backend
     * 
     * @param backend
     * @return {@link TaskTideServiceManager} 
     */
    public static TaskTideServiceManager fetchManager(Template backend) {
        
        // Initialize vars
        TaskTideService<WorkItem> workItemService;
        TaskTideService<Step> stepService;
        TaskTideService<Workflow> workflowService;
        
        // Make services
        workItemService = ServiceFactory.makeWorkItemService(RepositoryType.NOSQL, backend, "WorkItem-Service");
        stepService = ServiceFactory.makeStepService(RepositoryType.NOSQL, backend, "Step-Service");
        workflowService = ServiceFactory.makeWorkflowService(RepositoryType.NOSQL, backend, "Workflow-Service");
        
        // Query as a sanity check
        // LOGGER.info("Services for TaskTideModels created. Sanity checking querying a record");
        // List<WorkItem> data = workItemService.viewAll();
        // LOGGER.info("\nDisplaying sanity check data:\n{}", data.get(0).toJsonDoc());
        
        // Return manager
        return new TaskTideServiceManager(workItemService, stepService, workflowService);
    }
    
    
    /**
     * Fetch {@link DocumentTemplate} for {@link Template}
     * 
     * @param container
     * @return {@link Template}
     */
    public static Template fetchDocumentTemplate(SeContainer container) {
        container = SeContainerInitializer.newInstance().initialize();
        return container.select(DocumentTemplate.class).get();
    }
    
    
    /**
     * Fetch {@link KeyValueTemplate} for {@link Template}
     * 
     * @param container
     * @return {@link Template}
     */
    public static Template fetchKeyValueTemplate(SeContainer container) {
        container = SeContainerInitializer.newInstance().initialize();
        return container.select(KeyValueTemplate.class).get();
    }
    
    
    /**
     * Fetch {@link ColumnTemplate} for {@link Template}
     * 
     * @param container
     * @return {@link Template}
     */
    public static Template fetchColumnTemplate(SeContainer container) {
        container = SeContainerInitializer.newInstance().initialize();
        return container.select(ColumnTemplate.class).get();
    }
    
    
    /**
     * Fetch {@link GraphTemplate} for {@link Template}
     * 
     * @param container
     * @return {@link Template}
     */
    public static Template fetchGraphTemplate(SeContainer container) {
        container = SeContainerInitializer.newInstance().initialize();
        return container.select(GraphTemplate.class).get();
    }
}
