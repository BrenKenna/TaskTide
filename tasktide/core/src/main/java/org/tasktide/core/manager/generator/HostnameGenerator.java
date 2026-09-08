/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.tasktide.core.manager.generator;

import java.util.HashMap;
import java.util.Map;


/**
 * Generating Hostname tasks
 * 
 * @author bkenna
 */
public class HostnameGenerator extends ExampleGenerator {

    /**
     * Constructor
     * 
     */
    public HostnameGenerator() {}

    
    /**
     * Generate hostname command
     * 
     * @return cmd map
     */
    @Override
    public Map<String, String> generateCmd() {
        Map<String, String> output = new HashMap<>();
        String host = HostList.getRandomHost();
        String cmd = "hostname " + host;
        output.put("Task Name", "Hostname " + host);
        output.put("Task Script", cmd);
        return output;
    }
}