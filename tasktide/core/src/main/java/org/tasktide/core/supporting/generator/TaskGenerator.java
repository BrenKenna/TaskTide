/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.tasktide.core.supporting.generator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 *
 * Generator to support creation of random tasks
 * 
 * @author bkenna
 */
public class TaskGenerator {
    
    
    /**
     * Generate random task of required type
     * 
     * @param taskType
     * @return Map-String, String
     */
    public static Map<String, String> generateTask(TaskType taskType) {
        return taskType.createTask().generateCmd();
    }
    
    
    /**
     * Generate random ping task
     * 
     * @return Map-String, String
     */
    public static Map<String, String> generatePingTask() {
        return TaskType.PING.createTask().generateCmd();
    }
    
    
    /**
     * Generate random seq task
     * 
     * @return Map-String, String
     */
    public static Map<String, String> generateSeqTask() {
        return TaskType.SEQ.createTask().generateCmd();
    }
    
    
    /**
     * Fetch required number of random tasks for type
     * 
     * @param taskType
     * @param nTasks
     * @return List-Map-String, String
     */
    public static List<Map<String, String>> generateTasks(TaskType taskType, int nTasks) {
        List<Map<String, String>> output = new ArrayList<>();
        for ( int i = 0; i < nTasks; i ++ ) {
            Map<String, String> task = generateTask(taskType);
            output.add(task);
        }
        return output;
    }
}
