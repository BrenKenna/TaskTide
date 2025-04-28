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
 * Generating seq tasks
 * 
 * @author bkenna
 */
public class SeqGenerator {
    
    private final Utils utils;
    
    public SeqGenerator() {
        utils = new Utils("dd/MM/yy HH:mm:ss", 4);
    }

    public Map<String, String> generateCmd() {
        Map<String, String> output = new HashMap<>();
        int limit = utils.getRandInt(100);
        String cmd = "seq " + limit;
        output.put("Task Name", "Seq " + limit);
        output.put("Task Script", cmd);
        return output;
    }
}
