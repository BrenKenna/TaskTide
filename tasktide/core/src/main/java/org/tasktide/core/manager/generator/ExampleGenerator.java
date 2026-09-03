/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.tasktide.core.manager.generator;

import java.util.Map;

import org.tasktide.core.manager.ManagerTask;
import org.tasktide.core.manager.command.ManagerCommand;

import org.tasktide.core.supporting.Utils;


/**
 * {@link ManagerTask} generator
 *
 * @author Bren
 */
public abstract class ExampleGenerator {

    // Attributes
    protected final Utils utils;
    
    
    /**
     * Default constructor
     * 
     */
    public ExampleGenerator() {
        this.utils = new Utils("dd/MM/yy HH:mm:ss", 4);
    }
    
    
    /**
     * Generate {@link ManagerCommand} for creating a
     *  {@link ManagerTask}
     * 
     * @return {@link ManagerTask}
     */
    protected abstract Map<String, String> generateCmd();
    
    
    /**
     * Provide {@link ManagerTask}
     * 
     * @param cmd
     * 
     * @return {@link ManagerTask}
     */
    public ManagerTask createTask() {
        
        // Generate command map
        Map<String, String> cmd = this.generateCmd();
        
        // Generate manager task
        return new ManagerTask(cmd.get("Task Name"), cmd.get("Task Script"));
    }
}