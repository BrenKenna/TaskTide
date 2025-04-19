/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package org.tasktide.core.supporting.generator;

import java.util.Map;


/**
 * Interface for generating mock tasks
 * 
 * @author bkenna
 */
public interface Task {
    
    /**
     * Generate command to run
     * 
     * @return Map<String, String>
     */
    public Map<String, String> generateCmd();
}
