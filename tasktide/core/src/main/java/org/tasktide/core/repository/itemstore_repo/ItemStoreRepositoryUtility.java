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
import org.tasktide.core.manager.command.ManagerTarget;
import org.tasktide.core.model.collection.Step;
import org.tasktide.core.model.collection.Workflow;
import org.tasktide.core.model.workitem.WorkItem;

import org.tasktide.core.TaskTideRepository;
import org.tasktide.core.manager.TaskTideServiceManager;
import org.tasktide.core.model.job_env.JobEnvironment;
import org.tasktide.core.model.job_env.metrics.MetricData;
import org.tasktide.core.model.job_env.metrics.MetricProfile;
import org.tasktide.core.repository.RepositoryType;
import org.tasktide.core.services.ServiceFactory;
import org.tasktide.core.supporting.JsonUtils;
import org.tasktide.itemstore.DbTarget;

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
        TaskTideService<MetricData> metricServ;
        TaskTideService<MetricProfile> profileServ;
        TaskTideService<JobEnvironment> jobEnvServ;
        
        // Fetch item store map
        itemStoreMap = fetchItemStoreMap(this.storeType, this.storeName);
        
        // Construct services
        workItemService = ServiceFactory.makeWorkItemService(RepositoryType.ITEMSTORE, itemStoreMap.get(ManagerTarget.WORKITEM), "WorkItem-Service");
        stepService = ServiceFactory.makeStepService(RepositoryType.ITEMSTORE, itemStoreMap.get(ManagerTarget.STEP), "Step-Service");
        workflowService = ServiceFactory.makeWorkflowService(RepositoryType.ITEMSTORE, itemStoreMap.get(ManagerTarget.WORKFLOW), "Workflow-Service");
        
        // Construct additional services
        metricServ = ServiceFactory.makeMetricDataService(RepositoryType.ITEMSTORE, itemStoreMap.get(ManagerTarget.METRIC_DATA), "MetricData");
        profileServ = ServiceFactory.makeMetricProfileService(RepositoryType.ITEMSTORE, itemStoreMap.get(ManagerTarget.METRIC_PROFILE), "MetricProfile");
        jobEnvServ = ServiceFactory.makeJobEnvironmentService(RepositoryType.ITEMSTORE, itemStoreMap.get(ManagerTarget.JOB_ENVIRONMENT), "JobEnvironment");
        
        // Initialize service manager with services
        TaskTideServiceManager.initialize(workItemService, stepService, workflowService, jobEnvServ, metricServ, profileServ);
        LOGGER.debug("Displaying configured service manager:\n'{}'", TaskTideServiceManager.toJson());
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
            LOGGER.debug("ItemStore Directory created under:\t'{}'", storeName);
        }
        catch (IOException ex) {
            LOGGER.debug("ItemStoreDirectory already exists under:\t'{}'", storeName);
        }
        
        // Set vars
        String dbDirectory = store.toString();
        String masterDB = "master";
        String protoDB = UUID.randomUUID().toString();
        return storeType.makeItemStore(storeName, dbDirectory, masterDB, protoDB);
    }
    
    
    /**
     * Fetch {@link ItemStore} providing flag for leader election
     * 
     * @param storeName
     * @param storeType
     * @param isElected
     * 
     * @return {@link ItemStore}
     */
    public ItemStore fetchItemStore(String storeName, ItemStoreType storeType, boolean isElected) {
        
        // Resolve store locatoin
        Path store = Paths.get(storeName);
        try {
            Files.createDirectories(store);
            LOGGER.debug("ItemStore Directory created under:\t'{}'", storeName);
        }
        catch (IOException ex) {
            LOGGER.debug("ItemStoreDirectory already exists under:\t'{}'", storeName);
        }
        
        // Set vars
        String dbDirectory = store.toString();
        String masterDB = "master";
        String protoDB = UUID.randomUUID().toString();
        
        ItemStore result;
        if ( !isElected ) {
            result = storeType.makeItemStore(storeName, dbDirectory, masterDB, protoDB);
        }
        else {
            result = storeType.makeItemStoreNoElection(storeName, dbDirectory, masterDB, protoDB);
        }
        result.closeConn(DbTarget.BOTH, false);
        return result;
    }
    
    
    /**
     * Closes connections across
     * 
     * @param donor
     * @param recipients 
     */
    public void closeConnections(ItemStore donor, Map<String, ItemStore> recipients) {
        donor.execute(DbTarget.MASTER, recipients, (varA, varB) -> {
            LOGGER.info("Openning & closing connections across:\n'{}'", JsonUtils.toJson(true, recipients));
            return null;
        });
    }
    
    
    /**
     * Fetch {@link ItemStore} map
     * 
     * @param storeType
     * @param storeName
     * 
     * @return Map of {@link ManagerTarget}-{@link ItemStore}
     */
    public Map<ManagerTarget, ItemStore> fetchItemStoreMap(ItemStoreType storeType, String storeName) {
        LOGGER.info("Prcessing ItemStore from under:\t'{}'", storeName);
        Map<ManagerTarget, ItemStore> output = new HashMap<>();
        
        boolean isElected = false;
        ItemStore leader = null;
        for (ManagerTarget elm : ManagerTarget.withRepositories() ) {
            ItemStore store = fetchItemStore(storeName + "/" + elm.toString(), storeType, isElected);
            output.put(elm, store);
                
            if ( !isElected ) {
                    leader = store;
            }
            
            isElected = true;
        }
     
        if ( leader != null ) {
            leader.closeConn(DbTarget.BOTH, true);
        }
        return output;
    }
}