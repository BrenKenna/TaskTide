/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.tasktide.tasktide.client;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.tasktide.core.TaskTideModel;

import org.tasktide.core.manager.ManagerAction;
import org.tasktide.core.manager.ManagerTarget;
import org.tasktide.core.manager.TaskTideManagerUtility;
import org.tasktide.core.manager.TaskTideServiceManager;
import org.tasktide.core.model.collection.Step;
import org.tasktide.core.model.workitem.WorkItem;

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
    private final ArgumentMap argMap;


    /**
     * Construct engine client
     * 
     * @param configMap
     */
    public TaskTideManagerClient(ClientConfigMap configMap) {
        super(configMap);
        argMap = this.getArgTree().getTree().getDataForAddress("manager");
    }

    
    /**
     * Validate client arguments are configurable
     * 
     * @return boolean
     */
    @Override
    protected boolean configureClient() {
        if ( ((String) this.argMap.getArgument("Method").getValue()).isEmpty() ) {
            return false;
        }
        String inFile = ((String) this.argMap.getArgument("Input File").getValue());
        String outFile = ((String) this.argMap.getArgument("Output File").getValue());
        return !(inFile.isEmpty() && outFile.isEmpty());
    }
    

    /**
     * Executes the configured manager method to by mapping client action
     */
    @Override
    protected void performClientTask() {
        
        // Fetch task to perform
        String actVal = (String) this.argMap.getArgument("Method").getValue();
        ManagerAction action = ManagerAction.get(actVal);
        
        // Handle action to perform
        switch ( action ) {
            case IMPORT -> {
                this.handleImport();
            }
        
            case EXPORT -> {
                this.handleExport();
            }
            
            default -> {
                throw new IllegalStateException("Manager method must be one of Import/Export");
            }
        }
    }
    
    
    /**
     * Parse file to use based on the manager action 
     * 
     * @param action
     * @return {@link ManagerAction}
     */
    private String parseFile(ManagerAction action) {
        switch ( action ) {
            case IMPORT -> {
                return (String) this.argMap.getArgument("Input File").getValue();
            }
            
            case EXPORT -> {
                return (String) this.argMap.getArgument("Output File").getValue();
            }
            
            default -> {
                return null;
            }
        }
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
        try ( Reader inpStream = new FileReader(file) ) {
            Jsonb jsonb = JsonbBuilder.create();
            return Arrays.asList(jsonb.fromJson(inpStream, WorkItem[].class));
        }
        catch ( IOException ex ) {
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
        String file = this.parseFile(ManagerAction.IMPORT);
        String delimiter = (String) this.argMap.getArgument("Delimiter").getValue();
        String nestedDelimiter = (String) this.argMap.getArgument("Nested Delimiter").getValue();
        String stepName = (String) this.argMap.getArgument("Target Step").getValue();
        
        // Import workload from JSON
        if (delimiter.equalsIgnoreCase("json") || nestedDelimiter.equalsIgnoreCase("json")) {
            return this.importJson(file);
        }
        
        // Otherwise table
        else {
            
            // With no nested delimiter
            if (nestedDelimiter.isEmpty()) {
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
            LOGGER.info("Import '{}' workitems", workload.size());

            TaskTideServiceManager.fetchWorkItemService().extendModel(workload);
            LOGGER.info(
          "Import complete displaying first item for referece",
             TaskTideServiceManager.fetchWorkItemService().fetchById(workload.get(0).getId())
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
        String outFile = parseFile(ManagerAction.EXPORT);
        String target = (String) this.argMap.getArgument("Target").getValue();
        if ( !outFile.isEmpty() && !target.isEmpty() ) {
            ManagerTarget tgt = ManagerTarget.get(target);
            if ( tgt != null ) {
                List<TaskTideModel> data = tgt.fetchModels();
                this.exportJson(data, outFile);
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