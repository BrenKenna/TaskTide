/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.tasktide.manager.model.manager_task.generator;

import java.util.HashMap;
import java.util.Map;

import org.tasktide.core.supporting.Utils;


/**
 *
 * Generating ping tasks
 * 
 * @author bkenna
 */
public class PingGenerator {

    public PingGenerator() {}

    public Map<String, String> generateCmd() {
        Map<String, String> output = new HashMap<>();
        String host = HostList.getRandomHost();
        String cmd = "ping " + host;
        output.put("Task Name", "Ping " + host);
        output.put("Task Script", cmd);
        return output;
    }
}
