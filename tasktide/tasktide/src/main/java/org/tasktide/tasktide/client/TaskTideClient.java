/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package org.tasktide.tasktide.client;

import java.util.List;

import org.tasktide.core.manager.TaskTideServiceManager;
import org.tasktide.core.model.workitem.ItemState;
import org.tasktide.core.model.workitem.WorkItem;
import org.tasktide.core.services.WorkItemService;

import org.tasktide.tasktide.configurer.TaskTideConfigurer;
import org.tasktide.tasktide.parser.ArgumentTree;


/**
 * Abstract for standardizing client logic for
 *  configuration, running, and clean-up
 * 
 * @author bkenna
 */
public abstract class TaskTideClient {
    
    // Shared attributes
    private final TaskTideServiceManager taskTideManager;
    private final TaskTideConfigurer clientConf;
    private final ArgumentTree argTree;
    
    
    /**
     * Construct engine client
     * 
     * @param manager
     * @param clientConf
     * @param argTree 
     */
    public TaskTideClient(TaskTideServiceManager manager, TaskTideConfigurer clientConf, ArgumentTree argTree) {
        this.taskTideManager = manager;
        this.clientConf = clientConf;
        this.argTree = argTree;
    }
    
    
    /**
     * Parses the provided command-line arguments, configure
     *  client with these parameters, performs the required client
     *  action, and runs any required clean-up
     * 
     * @param argsIn 
     */
    public void runClient(String[] argsIn) {
    
        // Parse command-line arguments
        this.getClientConf().initConfig(this.getArgTree());
        this.getClientConf().parseCommandLineArguments(argsIn, this.getArgTree());
        
        // Initialize client
        this.configureClient();
        
        // Perform the work of the client
        this.performClientTask();
        
        // Run any required clean-up
        this.cleanUp();
    }
    
    
    /**
     * Configures concrete {@link TaskTideConfigurer}
     * 
     */
    protected abstract void configureClient();
    
    
    /**
     * Performs configured action of client
     * 
     */
    protected abstract void performClientTask();
    
    
    /**
     * Performs any clean-up action(s)
     */
    protected abstract void cleanUp();


    /**
     * Get {@link TaskTideServiceManager}
     * 
     * @return {@link TaskTideServiceManager}
     */
    public TaskTideServiceManager getTaskTideManager() {
        return taskTideManager;
    }

    
    /**
     * Fetch to work from {@link WorkItemService}
     * 
     * @return List of {@link WorkItem}
     */
    protected List<WorkItem> fetchToDoWork() {
        List<WorkItem> workload, check;
        check = ((WorkItemService) this.taskTideManager.getWorkItemService()).viewItemsByState(ItemState.TODO);
        workload = this.taskTideManager.getWorkItemService().viewByField("state", "todo");
        System.out.println(String.format("Comparing query sizes:\tConcrete service '%d', Manager '%d'", check.size(), workload.size()));
        return workload;
    }

    
    /**
     * Get concrete {@link TaskTideConfigurer}
     * 
     * @return {@link TaskTideConfigurer}
     */
    public TaskTideConfigurer getClientConf() {
        return clientConf;
    }

    
    /**
     * Get {@link ArgumentTree} shared across clients
     * 
     * @return {@link ArgumentTree}
     */
    public ArgumentTree getArgTree() {
        return argTree;
    }
}
