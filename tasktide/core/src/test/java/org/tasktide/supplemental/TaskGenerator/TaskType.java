/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package org.tasktide.supplemental.TaskGenerator;


/**
 *
 * Enum of valid task types
 * 
 * @author bkenna
 */
public enum TaskType {
    
    PING {
        @Override
        public Task createTask() {
            return new MockPingTask();
        }
    },
    
    SEQ {
        @Override
        public Task createTask() {
            return new MockSeqTask();
        }
    };
    
    
    /**
     * Abstract method to generate a task
     * 
     * @return Task
     */
    public abstract Task createTask();
}
