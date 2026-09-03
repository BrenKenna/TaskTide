/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.tasktide.core.manager.generator;

import java.util.HashMap;
import java.util.Map;


/**
 * Generating NS Lookup tasks
 * 
 * @author bkenna
 */
public class NsLookupGenerator extends ExampleGenerator {

    /**
     * Constructor
     * 
     */
    public NsLookupGenerator() {}

    
    /**
     * Generate ping command
     * 
     * @return 
     */
    @Override
    public Map<String, String> generateCmd() {
        Map<String, String> output = new HashMap<>();
        String host = HostList.getRandomHost();
        String cmd = "nslookup " + host;
        output.put("Task Name", "NS Lookup " + host);
        output.put("Task Script", cmd);
        return output;
    }
}