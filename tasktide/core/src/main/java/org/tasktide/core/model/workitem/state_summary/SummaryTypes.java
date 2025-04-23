/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package org.tasktide.core.model.workitem.state_summary;


/**
 * Enum to support Summaries for ItemTasks, and WorkItems
 * 
 * @author bkenna
 */
public enum SummaryTypes {
    
    ITEMTASK {
        @Override
        public String toString() {
            return "ItemTask";
        }
    },
    
    WORKITEM {
        @Override
        public String toString() {
            return "WorkItem";
        }
    };
    
    /**
     * Represent value as string
     * 
     * @return String
     */
    @Override
    public abstract String toString();
}
