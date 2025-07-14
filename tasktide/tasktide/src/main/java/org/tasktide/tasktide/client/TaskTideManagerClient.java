/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.tasktide.tasktide.client;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.tasktide.core.manager.TaskTideServiceManager;


/**
 *
 * @author bkenna
 */
public class TaskTideManagerClient extends TaskTideClient {
    
    private final Logger logger = LogManager.getLogger(TaskTideEngineClient.class);


    /**
     * Construct engine client
     * 
     * @param manager
     * @param configMap
     */
    public TaskTideManagerClient(TaskTideServiceManager manager, ClientConfigMap configMap) {
        super(manager, configMap);
    }

    @Override
    protected void configureClient() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    protected void performClientTask() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    protected void cleanUp() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}
