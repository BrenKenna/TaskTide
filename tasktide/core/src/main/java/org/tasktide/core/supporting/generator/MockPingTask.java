/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.tasktide.core.supporting.generator;

import java.util.HashMap;
import java.util.Map;

import org.tasktide.core.supporting.Utils;


/**
 *
 * Generating ping tasks
 * 
 * @author bkenna
 */
public class MockPingTask implements TaskTideTask {

    private final Utils utils;

    public MockPingTask() {
        this.utils = new Utils();
    }

    @Override
    public Map<String, String> generateCmd() {
        Map<String, String> output = new HashMap<>();
        String cmd = "ping " + HostList.getRandomHost();
        output.put("Task", cmd);
        output.put("Task Name", "Ping " + cmd);
        return output;
    }
    
    
}
