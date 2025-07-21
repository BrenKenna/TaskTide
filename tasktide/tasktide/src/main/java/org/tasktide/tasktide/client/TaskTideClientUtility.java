/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.tasktide.tasktide.client;

import jakarta.enterprise.inject.spi.CDI;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbConfig;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jakarta.nosql.Template;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.eclipse.jnosql.mapping.column.ColumnTemplate;
import org.eclipse.jnosql.mapping.document.DocumentTemplate;
import org.eclipse.jnosql.mapping.document.spi.DocumentExtension;
import org.eclipse.jnosql.mapping.graph.GraphTemplate;
import org.eclipse.jnosql.mapping.keyvalue.KeyValueTemplate;
import org.eclipse.jnosql.mapping.reflection.spi.ReflectionEntityMetadataExtension;
import org.tasktide.core.TaskTideRepository;

import org.tasktide.core.TaskTideService;
import org.tasktide.core.manager.ManagerTarget;
import org.tasktide.core.services.ServiceFactory;
import org.tasktide.core.repository.RepositoryType;
import org.tasktide.core.manager.TaskTideServiceManager;

import org.tasktide.core.model.collection.Step;
import org.tasktide.core.model.collection.Workflow;
import org.tasktide.core.model.task.ItemTask;
import org.tasktide.core.model.workitem.WorkItem;
import org.tasktide.itemstore.ItemStore;
import org.tasktide.itemstore.stores.RocksDBStore;
import org.tasktide.tasktide.TaskTide;
import org.tasktide.tasktide.configurer.EngineConfig;

import org.tasktide.tasktide.configurer.GlobalConfig;
import org.tasktide.tasktide.configurer.ManagerConfig;
import org.tasktide.tasktide.containerprovider.CdiContainerProvider;
import org.tasktide.tasktide.containerprovider.CdiProviders;
import org.tasktide.tasktide.parser.ArgumentTree;



/**
 * Utility class for {@link TaskTideClient} interface
 * 
 * @author bkenna
 */
public class TaskTideClientUtility {
    
    // Attributes
    private static final Logger LOGGER = LogManager.getLogger(TaskTideClientUtility.class);
    private static final Jsonb JSON_PRETTY = JsonbBuilder.create(new JsonbConfig().withFormatting(true));
    private static final Jsonb JSON = JsonbBuilder.create(new JsonbConfig());
    
    
    /**
     * Fetches splash string
     * 
     * @return String
     */
    public static String fetchSplashString() {
        String output = "";
        try (InputStream is = TaskTide.class.getClassLoader().getResourceAsStream("splash.txt");
             BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output += "\n" + line;
            }
            return output;
        } catch (IOException e) {
            return "";
        }
    }
    
    
    /**
     * Print splash
     * 
     */
    public static void printSplash() {
        try (InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("splash.txt");
             BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
            System.out.println();
        } catch (IOException e) {
            
        }
    }
    
    
    /**
     * Configures {@link CdiContainerProvider} for provided {@link CdiProviders} type
     * 
     * @param cdiProvider
     * @return {@link CdiContainerProvider}
     */
    @SuppressWarnings("unchecked")
    public static CdiContainerProvider configureCdiInstance(CdiProviders cdiProvider) {
        
        // Initialize
        CdiContainerProvider provider = cdiProvider.createProvider();
        provider.initialize();
        
        // Add packages, and extensions
        //  Required with Netbeans, not with in linux shell?
        if ( !cdiProvider.isProvider(CdiProviders.QUARKUS) ) {
            provider.addExtension(ReflectionEntityMetadataExtension.class, DocumentExtension.class);
            provider.addBeanClass(
           GlobalConfig.class, ManagerConfig.class, EngineConfig.class,
           WorkItem.class, ItemTask.class, Step.class, Workflow.class
            );
        }
        
        // Return
        LOGGER.info("Starting '{}' container", cdiProvider);
        provider.start();
        return provider;
    }
    
    
    /**
     * Represent map as json string
     * 
     * @param map
     * @return String Json
     */
    public static String mapToJsonString(Object map) {
        Jsonb jsonb = JsonbBuilder.create(new JsonbConfig().withFormatting(true));
        return jsonb.toJson(map);
    }
    
    
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
        String str = (String) configMap.getArgTree().getGlobalArguments().getArgMap().get("Repository Type").getValue();
        return RepositoryType.get(str);
    }
    
    
    /**
     * Fetches {@link TaskTideServiceManager} from {@link CdiContainerProvider} using
     *  the value of the {@link RepositoryType}
     * 
     * @param repoType
     * @param configMap
     * @return {@link TaskTideServiceManager}
     */

    public static TaskTideServiceManager fetchManager(RepositoryType repoType, ClientConfigMap configMap) {
        switch ( repoType ) {
            case NOSQL -> {
                LOGGER.info("Configuring NoSQL Template ServiceManager");
                Template backend = TaskTideClientUtility.fetchTemplate(configMap);
                return fetchManager(backend);
            }
            
            case ITEMSTORE -> {
                LOGGER.info("Configuring ItemStore ServiceManager");
                Map<ManagerTarget, TaskTideRepository> repoMap = fetchItemStoreRepoMap(repoType, configMap);
                return fetchManager(repoType, repoMap);
            }
            
            default -> {
                throw new IllegalStateException("No backend repository type detected");
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
     * Fetch {@link TaskTideServiceManager} from {@link TaskTideRepository} map
     * 
     * @param repoType
     * @param repoMap
     * @return {@link TaskTideServiceManager}
     */
    public static TaskTideServiceManager fetchManager(RepositoryType repoType, Map<ManagerTarget, TaskTideRepository> repoMap) {
        
        // Initialize vars
        TaskTideService<WorkItem> workItemService;
        TaskTideService<Step> stepService;
        TaskTideService<Workflow> workflowService;
        
        // Make services
        workItemService = ServiceFactory.makeWorkItemService(repoType, repoMap.get(ManagerTarget.WORKITEM), "WorkItem-Service");
        stepService = ServiceFactory.makeStepService(repoType, repoMap.get(ManagerTarget.STEP), "Step-Service");
        workflowService = ServiceFactory.makeWorkflowService(repoType, repoMap.get(ManagerTarget.WORKFLOW), "Workflow-Service");
        
        // Return manager
        return new TaskTideServiceManager(workItemService, stepService, workflowService);
    }
    
    /**
     * Fetch {@link TaskTideServiceManager} for the backend {@link RepositoryType}
     * 
     * @param repoType
     * @param backend
     * @return {@link TaskTideServiceManager}
     */
    public static TaskTideServiceManager fetchManager(RepositoryType repoType, Object backend) {
        
        // Initialize vars
        TaskTideService<WorkItem> workItemService;
        TaskTideService<Step> stepService;
        TaskTideService<Workflow> workflowService;
        
        // Make services
        workItemService = ServiceFactory.makeWorkItemService(repoType, backend, "WorkItem-Service");
        stepService = ServiceFactory.makeStepService(repoType, backend, "Step-Service");
        workflowService = ServiceFactory.makeWorkflowService(repoType, backend, "Workflow-Service");
        
        // Query as a sanity check
        // LOGGER.info("Services for TaskTideModels created. Sanity checking querying a record");
        // List<WorkItem> data = workItemService.viewAll();
        // LOGGER.info("\nDisplaying sanity check data:\n{}", data.get(0).toJsonDoc());
        
        // Return manager
        return new TaskTideServiceManager(workItemService, stepService, workflowService);
    }
    
    
    
    /**
     * Fetches {@link Template}
     * 
     * @param configMap
     * @return {@link Template}
     */
    @SuppressWarnings("unchecked")
    public static Template fetchTemplate(ClientConfigMap configMap) {
        Class clazz = provideTemplateClass(configMap);
        return (Template) CDI.current().select(clazz).get();
    }
    
    
    /**
     * Provides the required {@link Temaplte} class to inject
     * 
     * @param configMap
     * @return Class
     */
    public static Class provideTemplateClass(ClientConfigMap configMap) {
        String dbType = (String) configMap.getArgTree().getGlobalArguments().getArgMap().get("NoSQL Database Type").getValue();
        dbType = dbType.strip().replace(" ", "").replace("-", "");
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
     * Fetch {@link ItemStore} for store name
     * 
     * @param storeName
     * @return {@link ItemStore}
     */
    public static ItemStore fetchItemStore(String storeName) {
        
        // Resolve store locatoin
        Path store = Paths.get(storeName);
        try {
            Files.createDirectories(store);
            LOGGER.info("RocksDB ItemStore created under:\t'{}'", storeName);
        }
        catch (IOException ex) {
            LOGGER.info("RocksDB ItemStore already exists under:\t'{}'", storeName);
        }
        
        // Set vars
        String dbDirectory = store.toString();
        String masterDB = "master";
        String protoDB = UUID.randomUUID().toString();
        return new RocksDBStore(storeName, dbDirectory, masterDB, protoDB);
    }
    
    
    /**
     * Fetch {@link ItemStore} map
     * 
     * @param repoType
     * @param configMap
     * @return 
     */
    public static Map<ManagerTarget, ItemStore> fetchItemStoreMap(RepositoryType repoType, ClientConfigMap configMap) {
        Map<ManagerTarget, ItemStore> output = new HashMap<>();
        String storeName = (String) configMap.getArgTree().getGlobalArguments().getArgument("File Path").getValue();
        for ( ManagerTarget elm : ManagerTarget.values() ) {
           ItemStore store = fetchItemStore(storeName + "/" + elm.toString());
           output.put(elm, store);
        }
        return output;
    }
    
    
    
    /**
     * 
     * 
     * @param repoType
     * @param configMap
     * @return 
     */
    public static Map<ManagerTarget, TaskTideRepository> fetchItemStoreRepoMap(RepositoryType repoType, ClientConfigMap configMap) {
        
        // Initialize output and fetch item store map
        Map<ManagerTarget, TaskTideRepository> output = new HashMap<>();
        Map<ManagerTarget, ItemStore> itemStoreMap = fetchItemStoreMap(repoType, configMap);
        
        // Add work item repo
        ItemStore store = itemStoreMap.get(ManagerTarget.WORKITEM);
        TaskTideRepository repo = repoType.createRepository(WorkItem.class, store, store.getDbDirectory());
        output.put(ManagerTarget.WORKITEM, repo);
        
        // Add step repo
        store = itemStoreMap.get(ManagerTarget.STEP);
        repo = repoType.createRepository(Step.class, store, store.getDbDirectory());
        output.put(ManagerTarget.STEP, repo);
        
        // Add workflow repo
        store = itemStoreMap.get(ManagerTarget.WORKFLOW);
        repo = repoType.createRepository(Workflow.class, store, store.getDbDirectory());
        output.put(ManagerTarget.WORKFLOW, repo);
        
        // Return results
        return output;
    }
}
