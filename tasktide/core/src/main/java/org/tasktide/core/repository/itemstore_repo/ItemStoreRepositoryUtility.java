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
    
    // Logging
    private final Logger LOGGER = LogManager.getLogger(ItemStoreRepositoryUtility.class);
    
    // Attributes
    private static ItemStoreRepositoryUtility INSTANCE;
    private final ItemStoreType storeType;
    private final String storeName;
    
    
    /**
     * Construct utility with {@link ItemStoreType} and store file location
     * 
     * @param storeType
     * @param storeName 
     */
    private ItemStoreRepositoryUtility(ItemStoreType storeType, String storeName) {
        this.storeType = storeType;
        this.storeName = storeName;
    }
    
    
    /**
     * Initialize the utility with the store type and file location
     * 
     * @param storeType
     * @param storeName 
     */
    public static void initialize(ItemStoreType storeType, String storeName) {
        if ( INSTANCE != null ) {
            throw new IllegalStateException("ItemStoreRepositoryUtility already initialized");
        }
        INSTANCE = new ItemStoreRepositoryUtility(storeType, storeName);
    }
    
    
    /**
     * Fetch utility, throwing illegal state exception if not initialized
     * 
     * @return ItemStoreRepositoryUtility
     */
    public static ItemStoreRepositoryUtility get() {
        if ( INSTANCE != null ) {
            return INSTANCE;
        }
        throw new IllegalStateException("ItemStoreRepositoryUtility not initialized");
    }
    
    
    /**
     * Initialize {@link TaskTideServiceManager}
     */
    public void initServiceManager() {
        
        // Initialize vars
        Map<ManagerTarget, ItemStore> itemStoreMap;
        TaskTideService<WorkItem> workItemService;
        TaskTideService<Step> stepService;
        TaskTideService<Workflow> workflowService;
        
        // Fetch item store map
        itemStoreMap = fetchItemStoreMap(this.storeType, this.storeName);
        
        // Construct services
        workItemService = ServiceFactory.makeWorkItemService(RepositoryType.ITEMSTORE, itemStoreMap.get(ManagerTarget.WORKITEM), "WorkItem-Service");
        stepService = ServiceFactory.makeStepService(RepositoryType.ITEMSTORE, itemStoreMap.get(ManagerTarget.STEP), "Step-Service");
        workflowService = ServiceFactory.makeWorkflowService(RepositoryType.ITEMSTORE, itemStoreMap.get(ManagerTarget.WORKFLOW), "Workflow-Service");
        
        // Initialize manager with these services
        TaskTideServiceManager.initialize(workItemService, stepService, workflowService);
    }
    
    
    /**
     * Fetch {@link ItemStore} for store name
     * 
     * @param storeName
     * @param storeType
     * @return {@link ItemStore}
     */
    public ItemStore fetchItemStore(String storeName, ItemStoreType storeType) {
        
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
    public Map<ManagerTarget, ItemStore> fetchItemStoreMap(ItemStoreType storeType, String storeName) {
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
    public Map<ManagerTarget, TaskTideRepository> fetchItemStoreRepoMap(ItemStoreType storeType, String storeName) {
        
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