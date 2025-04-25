/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package org.tasktide.core.supporting.generator;


/**
 *
 * Enum of valid task types
 * 
 * @author bkenna
 */
public enum TaskType {
    
    PING {
        @Override
        public TaskTideTask createTask() {
            return new MockPingTask();
        }
    },
    
    SEQ {
        @Override
        public TaskTideTask createTask() {
            return new MockSeqTask();
        }
    };
    
    
    /**
     * Abstract method to generate a task
     * 
     * @return TaskTideTask
     */
    public abstract TaskTideTask createTask();
}
