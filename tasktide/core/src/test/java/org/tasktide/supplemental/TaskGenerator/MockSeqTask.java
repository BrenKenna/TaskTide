/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.tasktide.supplemental.TaskGenerator;

import java.util.HashMap;
import java.util.Map;
import org.tasktide.core.supporting.Utils;


/**
 *
 * Generating seq tasks
 * 
 * @author bkenna
 */
public class MockSeqTask implements Task {
    
    private final Utils utils;
    
    public MockSeqTask() {
        utils = new Utils();
    }

    @Override
    public Map<String, String> generateCmd() {
        Map<String, String> output = new HashMap<>();
        int limit = utils.getRandInt(100);
        String cmd = "seq " + limit;
        output.put("Task", cmd);
        output.put("Task Name", cmd);
        return output;
    }
}
