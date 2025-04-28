/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.tasktide.core.manager.generator;

import org.tasktide.core.manager.ManagerTask;
import java.util.ArrayList;
import java.util.List;


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
     * @return Map-String of ManagerTask Name, String of task
     */
    public ManagerTask generateTask(ExampleGenerators taskType) {
        return taskType.createTask();
    }
    
    
    /**
     * Generate random ping task
     * 
     * @return Map-String of ManagerTask Name, String of task
     */
    public ManagerTask generatePingTask() {
        return ExampleGenerators.PING.createTask();
    }
    
    
    /**
     * Generate random seq task
     * 
     * @return Map-String of ManagerTask Name, String of task
     */
    public ManagerTask generateSeqTask() {
        return ExampleGenerators.SEQ.createTask();
    }
    
    
    /**
     * Fetch required number of random tasks for type
     * 
     * @param taskType
     * @param nTasks
     * @return List-Map-String of ManagerTask Name, String of task
     */
    public List<ManagerTask> generateTasks(ExampleGenerators taskType, int nTasks) {
        List<ManagerTask> output = new ArrayList<>();
        for ( int i = 0; i < nTasks; i ++ ) {
            ManagerTask task = generateTask(taskType);
            output.add(task);
        }
        return output;
    }
}
