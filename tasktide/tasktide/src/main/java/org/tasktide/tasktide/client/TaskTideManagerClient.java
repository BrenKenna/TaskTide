/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.tasktide.tasktide.client;

import jakarta.json.JsonObject;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.tasktide.core.TaskTideModel;

import org.tasktide.core.manager.ManagerAction;
import org.tasktide.core.manager.ManagerTarget;
import org.tasktide.core.manager.ManagerTask;
import org.tasktide.core.manager.TaskTideManagerUtility;
import org.tasktide.core.manager.TaskTideServiceManager;

import org.tasktide.core.model.workitem.ItemState;
import org.tasktide.core.model.workitem.WorkItem;

import org.tasktide.core.supporting.JsonUtils;

import org.tasktide.tasktide.parser.model.ArgumentMap;


/**
 * Manager client for providing IO functionality for 
 *  Workloads, Steps, and WorkItems
 * 
 * @author bkenna
 */
public class TaskTideManagerClient extends TaskTideClient {
    
    // Attributes
    private final Logger LOGGER = LogManager.getLogger(TaskTideEngineClient.class);
    private final ArgumentMap managerArgs;
    private final ArgumentMap globalArgs;


    /**
     * Construct engine client
     * 
     * @param configMap
     */
    public TaskTideManagerClient(ClientConfigMap configMap) {
        super(configMap);
        managerArgs = this.getArgTree().getTree().getDataForAddress("manager");
        globalArgs = this.getArgTree().getTree().getDataForAddress("");
    }

    
    /**
     * Validate client arguments are configurable
     * 
     * @return boolean
     */
    @Override
    protected boolean configureClient() {
        if ( ((String) this.managerArgs.getArgument("Method").getValue()).isEmpty() ) {
            return false;
        }
        String inFile = ((String) this.managerArgs.getArgument("Target File").getValue());
        return !inFile.isEmpty();
    }
    

    /**
     * Executes the configured manager method to by mapping client action
     */
    @Override
    protected void performClientTask() {
        
        // Fetch task to perform
        String actVal = (String) this.managerArgs.getArgument("Method").getValue();
        ManagerAction action = ManagerAction.get(actVal);
        
        // Handle action to perform
        switch ( action ) {
            case IMPORT -> {
                LOGGER.info("Manager client configured for import");
                this.handleImport();
            }
        
            case EXPORT -> {
                LOGGER.info("Manager client configured for export");
                this.handleExport();
            }
            
            case ADD -> {
                LOGGER.info("Manager client configured for adding workitem");
                this.addWorkItem();
            }
            
            case APPEND -> {
                LOGGER.info("Manager client configured for workitem appending");
                this.appendToWorkItem();
            }
            
            case RESET_ITEM -> {
                LOGGER.info("Manager client configured for workitem reset");
                this.resetWorkItem();
            }
            
            case RESET_ITEMS -> {
                LOGGER.info("Manager client configured for collection of resets");
                int counter = this.resetWorkItems();
                LOGGER.info("'{}' WorkItems reset", counter);
            }
             
            case SUMMARIZE -> {
                LOGGER.info("Manager client configured to summarize the target collection");
                Map<ItemState, Integer> results = summarize();
                String jsonDoc = JsonUtils.toJson(true, results);
                LOGGER.info("Displaying summary:\n{}", jsonDoc);
            }
            
            case SUMMARIZE_EACH -> {
                LOGGER.info("Manager client configured to summarize each unit target collection");
                Map<String, Map<ItemState, Integer>> results = summarizeEach();
                String jsonDoc = JsonUtils.toJson(true, results);
                LOGGER.info("Displaying summary:\n{}", jsonDoc);
            }
            
            default -> {
                LOGGER.error("Error, unable to determine Manager Client action to take");
                String template = String.format(
                    "Manager method must be one of:\t'%s'",
                    ManagerAction.valuesString()
                );
                throw new IllegalStateException(template);
            }
        }
    }
    
    
    /**
     * Collapses counts of {@link ItemState} across all collection units
     * 
     * @return Map-{@link ItemState}, Integer
     */
    public Map<ItemState, Integer> summarize() {
        
        // Fetch coordinating arguments
        String step = (String) this.globalArgs.getArgument("Step Name").getValue();
        String stepId = TaskTideServiceManager.fetchStepService().viewByField("StepName", step).get(0).getId();
        
        // Collect into concurrent map
        Map<ItemState, Integer> results = TaskTideServiceManager
            .fetchWorkItemService()
            .viewByField("StepId", stepId)
            .parallelStream()
                .map( elm -> elm.summarizeByState() )
                .flatMap( map -> map.entrySet().stream() )
        .collect(Collectors.toConcurrentMap(
            Map.Entry::getKey,
            Map.Entry::getValue,
            Integer::sum
        ));
        
        // Provide as hash map
        return new HashMap<>(results);
    }

    
    /**
     * Fetches count of {@link ItemTask} {@link ItemState} per {@link WorkItem}
     * 
     * @return List-Map-{@link ItemState}, Integer
     */
    public Map<String, Map<ItemState, Integer>> summarizeEach() {
        
        // Fetch coordinating arguments
        String step = (String) this.globalArgs.getArgument("Step Name").getValue();
        String stepId = TaskTideServiceManager.fetchStepService().viewByField("StepName", step).get(0).getId();
        
        // Calculate results
        Map<String, Map<ItemState, Integer>> results =
            TaskTideServiceManager
            .fetchWorkItemService()
            .viewByField("StepId", stepId)
            .parallelStream()
        .collect(Collectors.toConcurrentMap(
            elm -> elm.getId(),
            elm -> elm.summarizeByState()
        ));
        
        // Provide as hash map
        return new HashMap<>(results);
    }
    
    
    /**
     * Reset {@link WorkItem}
     * 
     * @return {@link WorkItem}
     */
    public int resetWorkItems() {
        String targetFile = (String) this.managerArgs.getArgument("Target File").getValue();
        String delimiter = (String) this.managerArgs.getArgument("Delimiter").getValue();
        return TaskTideManagerUtility.resetItems(targetFile, delimiter);
    }
    
    
    /**
     * Reset {@link WorkItem}
     * 
     * @return {@link WorkItem}
     */
    public WorkItem resetWorkItem() {
        String itemId = (String) this.managerArgs.getArgument("ItemId").getValue();
        WorkItem workItem = TaskTideServiceManager.fetchWorkItemService().fetchById(itemId);
        workItem.resetModel();
        return TaskTideServiceManager.fetchWorkItemService().updateModel(workItem);
    }
    
    
    /**
     * Imports a task supplied as a json string, for SingleTsak {@link WorkItem}/initiating
     * 
     * @return {@link WorkItem}
     */
    public WorkItem addWorkItem() {
        String step = (String) this.globalArgs.getArgument("Step Name").getValue();
        String data = (String) this.managerArgs.getArgument("Import String").getValue();
        
        JsonObject json = JsonUtils.stringToJson(data);
        ManagerTask task = new ManagerTask(json.getString("Task Name"), json.getString("Task Script"));
        return TaskTideManagerUtility.importTask(task, json.getString("Task Name"), step);
    }
    
    
    /**
     * Add a task to a {@link WorkItem}
     * 
     * @return {@link WorkItem}
     */
    public WorkItem appendToWorkItem() {
        String data = (String) this.managerArgs.getArgument("Import String").getValue();
        
        JsonObject json = JsonUtils.stringToJson(data);
        ManagerTask task = new ManagerTask(json.getString("Task Name"), json.getString("Task Script"));
        // String workItemId = JsonUtils.fetchStringFieldFromJson("WorkItemId", json);
        String workItemId = json.getString("WorkItemId");
        
        return TaskTideManagerUtility.appendTask(task, workItemId);
    }
    
    
    
    
    /**
     * Export the {@link TaskTideModel} list to specified file
     * 
     * @param data
     * @param outFile
     * @return boolean
     */
    private boolean exportJson(List<TaskTideModel> data, String outFile) {
        try (Writer writer = new FileWriter(outFile) ) {
            Jsonb jsonb = JsonbBuilder.create();
            jsonb.toJson(data, writer);
            return true;
        }
        catch (IOException ex) {
            return false;
        }
    }
    
    
    /**
     * Deserialize {@link WorkItem} collection from 
     * 
     * @param file
     * @return List of {@link WorkItem}
     */
    private List<WorkItem> importJson(String file){
        LOGGER.info("Attempting to read JSON file:\t'{}'", file);
        try ( Reader inpStream = new FileReader(file) ) {
            Jsonb jsonb = JsonbBuilder.create();
            LOGGER.info("Streaming JSON data into WorkItem list");
            List<WorkItem> output = Arrays.asList(jsonb.fromJson(inpStream, WorkItem[].class));
            LOGGER.info("Streamed JSON data into WorkItem list");
            return output;
        }
        catch ( IOException ex ) {
            LOGGER.error("Error encountered during read '{}', displaying full stack trace", ex);
            ex.printStackTrace();
            return null;
        }
    }
    
    
    /**
     * Wrapper method to handle file import
     * 
     * @return List of {@link WorkItem}
     * @throws IOException 
     */
    private List<WorkItem> importFile() throws IOException {
    
        // Fetch arguments
        String file = (String) this.managerArgs.getArgument("Target File").getValue();
        String delimiter = (String) this.managerArgs.getArgument("Delimiter").getValue();
        String nestedDelimiter = (String) this.managerArgs.getArgument("Nested Delimiter").getValue();
        String stepName = (String) this.globalArgs.getArgument("Step Name").getValue();
        
        // Import workload from JSON: Format argument instead
        if (delimiter.equalsIgnoreCase("json")) {
            LOGGER.info("Importing JSON file");
            return this.importJson(file);
        }
        
        // Otherwise table
        else {
            
            // With no nested delimiter
            LOGGER.info("Evaluating nested delimiter of value '{}'", nestedDelimiter);
            if (nestedDelimiter == null) {
                return TaskTideManagerUtility.importTasks(stepName, file, delimiter);
            }
            
            if ( nestedDelimiter.isEmpty() ) {
                return TaskTideManagerUtility.importTasks(stepName, file, delimiter);
            }
            
            // Use nested delimiter
            else {
                return TaskTideManagerUtility.importTasks(stepName, file, delimiter, nestedDelimiter);
            }
        }
    }
    
    
    /**
     * Wrapper method to import workitem workload into
     *  workitem service
     * 
     */
    private void handleImport() {
    
        try {
            List<WorkItem> workload = this.importFile();
            LOGGER.info("Importing '{}' workitems", workload.size());

            boolean status = TaskTideServiceManager.fetchWorkItemService().extendModel(workload);
            LOGGER.info(
          "Import status '{}'",
                status
            );
        } catch (IOException ex) {
            LOGGER.error(
          "IOException encountered during import. Please review provided arguments, diaplying stack trace for reference",
         ex
            );
            ex.printStackTrace();
        }
    }
    
    
    /**
     * Handle exporting target data to output file
     */
    private void handleExport() {
        String targetFile = (String) this.managerArgs.getArgument("Target File").getValue();
        String collection = (String) this.globalArgs.getArgument("Step Name").getValue();
        if ( !targetFile.isEmpty() && !collection.isEmpty() ) {
            String target = (String) this.managerArgs.getArgument("Target").getValue();
            ManagerTarget tgt = ManagerTarget.get(target);
            if ( tgt != null ) {
                List<TaskTideModel> data = tgt.fetchModels();
                this.exportJson(data, targetFile);
            }
            else {
                throw new IllegalStateException(String.format("Target must be one of: %s", ManagerTarget.valuesString()));
            }
        }
        else {
            throw new IllegalArgumentException("OutFile & Target cannot be empty");
        }
    }
    
    
    @Override
    protected void cleanUp() {
    }
}