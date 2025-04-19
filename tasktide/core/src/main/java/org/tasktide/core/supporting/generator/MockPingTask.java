/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.tasktide.core.supporting.generator;

import com.arangodb.shaded.vertx.core.impl.Utils;
import java.util.HashMap;
import java.util.Map;


/**
 *
 * Generating ping tasks
 * 
 * @author bkenna
 */
public class MockPingTask implements Task {

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
