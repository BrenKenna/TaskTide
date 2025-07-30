/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.tasktide.core.repository.itemstore_repo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.tasktide.core.TaskTideService;
import org.tasktide.core.manager.ManagerTarget;
import org.tasktide.core.model.collection.Step;
import org.tasktide.core.model.collection.Workflow;
import org.tasktide.core.model.workitem.WorkItem;

import org.tasktide.core.TaskTideRepository;
import org.tasktide.core.manager.TaskTideServiceManager;
import org.tasktide.core.repository.RepositoryType;
import org.tasktide.core.services.ServiceFactory;

import org.tasktide.itemstore.ItemStore;
import org.tasktide.itemstore.ItemStoreType;


/**
 * Supports creation of {@link ItemStore} {@link TaskTideRepository},
 *  {@link TaskTideService}, and initialize the {@link TaskTideServiceManager} 
 * 
 * @author bkenna
 */
public class ItemStoreRepositoryUtility {
    
    private static final Logger LOGGER = LogManager.getLogger(ItemStoreRepositoryUtility.class);
    
    
    /**
     * Initialize {@link TaskTideServiceManager} with the provided {@link TaskTideRepository} map
     * 
     * @param repoType
     * @param repoMap 
     */
    public static void initServiceManager(RepositoryType repoType, Map<ManagerTarget, TaskTideRepository> repoMap) {
        
        // Initialize vars
        TaskTideService<WorkItem> workItemService;
        TaskTideService<Step> stepService;
        TaskTideService<Workflow> workflowService;
        
        // Make services
        workItemService = ServiceFactory.makeWorkItemService(repoType, repoMap.get(ManagerTarget.WORKITEM), "WorkItem-Service");
        stepService = ServiceFactory.makeStepService(repoType, repoMap.get(ManagerTarget.STEP), "Step-Service");
        workflowService = ServiceFactory.makeWorkflowService(repoType, repoMap.get(ManagerTarget.WORKFLOW), "Workflow-Service");
        
        // Return manager
        TaskTideServiceManager.initialize(workItemService, stepService, workflowService);
    }
    
    
    /**
     * Fetch {@link ItemStore} for store name
     * 
     * @param storeName
     * @param storeType
     * @return {@link ItemStore}
     */
    public static ItemStore fetchItemStore(String storeName, ItemStoreType storeType) {
        
        // Resolve store locatoin
        Path store = Paths.get(storeName);
        try {
            Files.createDirectories(store);
            LOGGER.info("ItemStore Directory created under:\t'{}'", storeName);
        }
        catch (IOException ex) {
            LOGGER.info("ItemStoreDirectory already exists under:\t'{}'", storeName);
        }
        
        // Set vars
        String dbDirectory = store.toString();
        String masterDB = "master";
        String protoDB = UUID.randomUUID().toString();
        return storeType.makeItemStore(storeName, dbDirectory, masterDB, protoDB);
    }
    
    
    /**
     * Fetch {@link ItemStore} map
     * 
     * @param storeType
     * @param storeName
     * @return 
     */
    public static Map<ManagerTarget, ItemStore> fetchItemStoreMap(ItemStoreType storeType, String storeName) {
        Map<ManagerTarget, ItemStore> output = new HashMap<>();
        for ( ManagerTarget elm : ManagerTarget.values() ) {
           ItemStore store = fetchItemStore(storeName + "/" + elm.toString(), storeType);
           output.put(elm, store);
        }
        return output;
    }
    
    
    /**
     * Wrapper method to fetch {@link ItemStore} {@link TaskTideRepository} map
     * 
     * @param storeType
     * @param storeName
     * @return Map-{@link ManagerTarget}, {@link TaskTideRepository}
     */
    public static Map<ManagerTarget, TaskTideRepository> fetchItemStoreRepoMap(ItemStoreType storeType, String storeName) {
        
        // Initialize output and fetch item store map
        Map<ManagerTarget, TaskTideRepository> output = new HashMap<>();
        Map<ManagerTarget, ItemStore> itemStoreMap = fetchItemStoreMap(storeType, storeName);
        
        // Add work item repo
        ItemStore store = itemStoreMap.get(ManagerTarget.WORKITEM);
        TaskTideRepository repo = RepositoryType.ITEMSTORE.createRepository(WorkItem.class, store, store.getDbDirectory());
        output.put(ManagerTarget.WORKITEM, repo);
        
        // Add step repo
        store = itemStoreMap.get(ManagerTarget.STEP);
        repo = RepositoryType.ITEMSTORE.createRepository(Step.class, store, store.getDbDirectory());
        output.put(ManagerTarget.STEP, repo);
        
        // Add workflow repo
        store = itemStoreMap.get(ManagerTarget.WORKFLOW);
        repo = RepositoryType.ITEMSTORE.createRepository(Workflow.class, store, store.getDbDirectory());
        output.put(ManagerTarget.WORKFLOW, repo);
        
        // Return results
        return output;
    }
}