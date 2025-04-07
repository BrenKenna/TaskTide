/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.tasktide.core.supporting;

import java.util.ArrayList;
import java.util.List;

import java.util.HashMap;
import java.util.Map;


/**
 *
 * Class to support generating random tasks
 * 
 * @author bkenna
 */
public class TaskGenerator {
    
    private final Utils utils;
    private final String [] pings = {
        "amazon.com", "google.com", "facebook.com", "rte.ie", "github.com", "twitter.com", "netflix.com",
        "youtube.com", "spotify.com", "amazon.ie", "bbc.com", "instagram.com"
    };
    
    public TaskGenerator() {
        this.utils = new Utils();
    }
    
    
    /**
     * Get random ping command
     * 
     * @return Map-String
     */
    public Map<String, String> getPingCmd() {
        Map<String, String> output = new HashMap<>();
        int randNum = utils.getRandInt(pings.length);
        String cmd = pings[randNum];
        
        output.put("Task", "ping " + cmd);
        output.put("Task Name", "Ping " + cmd);
        return output;
    }
    
    
    /**
     * Fetch random seq command
     * 
     * @param limit
     * @return Map-String, String
     */
    public Map<String, String> getSeqCmd(int limit) {
        Map<String, String> output = new HashMap<>();
        int randNum = utils.getRandInt(limit);
        
        String cmd = "seq " + limit;
        output.put("Task", cmd);
        output.put("Task Name", cmd);
        return output;
    }
    
    
    /**
     * Generate required number of random of ping tasks
     * 
     * @param num
     * @return List-Map-String, String
     */
    public List< Map<String, String> > getRandPings(int num) {
        List< Map<String, String> > output = new ArrayList<>();
        for (int i = 0; i <= num; i++ ) {
            Map<String, String> task = getPingCmd();
            Map<String, String> insert = new HashMap<>();
            insert.put(task.keySet().toArray()[0] + "-" + num, (String) task.values().toArray()[0]);
            output.add(insert);
        }
        return output;
    }
    
    
    /**
     * Generate required number of random of ping tasks
     * 
     * @param num
     * @return List-Map-String, String
     */
    public List< Map<String, String> > getRandSeqs(int num) {
        List< Map<String, String> > output = new ArrayList<>();
        for (int i = 0; i <= num; i++ ) {
            Map<String, String> task = getSeqCmd(130);
            Map<String, String> insert = new HashMap<>();
            insert.put(task.keySet().toArray()[0] + "-" + num, (String) task.values().toArray()[0]);
            output.add(insert);
        }
        return output;
    }
}
