/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package org.tasktide.manager.model.manager_task.generator;

import java.util.Map;
import org.tasktide.manager.model.manager_task.ManagerTask;



/**
 *
 * Enum of valid task types
 * 
 * @author bkenna
 */
public enum ExampleGenerators {
    
    PING {
        @Override
        public ManagerTask createTask() {
            PingGenerator pingGen = new PingGenerator();
            Map<String, String> map = pingGen.generateCmd();
            return new ManagerTask(map.get("Task Name"), map.get("Task Script"));
        }
    },
    
    SEQ {
        @Override
        public ManagerTask createTask() {
            SeqGenerator seqGen = new SeqGenerator();
            Map<String, String> map = seqGen.generateCmd();
            return new ManagerTask(map.get("Task Name"), map.get("Task Script"));
        }
    };
    
    
    /**
     * Abstract method to generate a task
     * 
     * @return ManagerTask
     */
    public abstract ManagerTask createTask();
}
