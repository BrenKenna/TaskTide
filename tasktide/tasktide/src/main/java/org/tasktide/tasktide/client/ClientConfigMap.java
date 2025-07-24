/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.tasktide.tasktide.client;

import java.util.HashMap;
import java.util.Map;

import org.tasktide.tasktide.configurer.GlobalConfig;
import org.tasktide.tasktide.configurer.TaskTideConfigurer;

import org.tasktide.tasktide.containerprovider.CdiContainerProvider;
import org.tasktide.tasktide.parser.ArgumentTree;


/**
 * 
 * 
 * @author bkenna
 */
public class ClientConfigMap {
    
    // Attributes
    private final Map<TaskTideClientType, TaskTideConfigurer> configMap;
    private final ArgumentTree argTree;
    private String[] argsIn;
    
    
    /**
     * 
     */
    public ClientConfigMap() {
        this.configMap = new HashMap<>();
        this.argTree = new ArgumentTree(" ");
    }
    
    
    /**
     * 
     * 
     * @param provider
     * @param clientType
     * @return {@link TaskTideConfigurer}
     */
    @SuppressWarnings("unchecked")
    private TaskTideConfigurer parseConfigs(CdiContainerProvider provider, TaskTideClientType clientType) {
        TaskTideConfigurer config;
        config = (TaskTideConfigurer) provider.getBean( clientType.getConfigClass());
        config.initConfig(this.argTree);
        return config;
    }
    
    
    /**
     * 
     * @param provider 
     */
    public void addConfigs(CdiContainerProvider provider) {
        for ( TaskTideClientType elm : TaskTideClientType.values() ) {
            TaskTideConfigurer config = this.parseConfigs(provider, elm);
            this.configMap.put(elm, config);
        }
    }    

    
    /**
     * Fetch which client to run ie Engine, Manager
     * 
     * @return {@link TaskTideClientType}
     */
    public TaskTideClientType whichClient() {
        GlobalConfig key = (GlobalConfig) configMap.get(TaskTideClientType.GLOBAL);
        String client = (String) key.getArgumentMap().getArgument("Client").getValue();
        if ( client != null ) {
            return TaskTideClientType.get(client);
        }
        return null;
    }
    
    
    /**
     * Return provided arguments
     * 
     * @return String[]
     */
    public String[] getArgsIn() {
        return argsIn;
    }

    
    /**
     * 
     * @param argsIn 
     */
    public void setArgsIn(String[] argsIn) {
        this.argsIn = argsIn;
    }

    
    /**
     * 
     * @return Map-{@link TaskTideClientType}, {@link TaskTideConfigurer}
     */
    public Map<TaskTideClientType, TaskTideConfigurer> getConfigMap() {
        return configMap;
    }

    
    /**
     * 
     * 
     * @param clientType
     * @return {@link TaskTideConfigurer}
     */
    public TaskTideConfigurer getConfig(TaskTideClientType clientType) {
        return this.configMap.get(clientType);
    }
    
    
    /**
     * 
     * 
     * @return {@link ArgumentTree}
     */
    public ArgumentTree getArgTree() {
        return argTree;
    }
}