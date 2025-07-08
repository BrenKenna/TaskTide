/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.tasktide.core.manager.generator;

import java.util.ArrayList;
import java.util.List;

import org.tasktide.core.manager.BuilderUtility;

import org.tasktide.core.manager.ManagerTask;
import org.tasktide.core.model.task.ItemTask;

import org.tasktide.core.model.workitem.WorkItem;
import org.tasktide.core.model.workitem.Workload;


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
     * @return Map-String of {@link ManagerTask} Name, String of task
     */
    public static ManagerTask generateTask(ExampleGenerators taskType) {
        return taskType.createTask();
    }
    
    
    /**
     * Generate random ping task
     * 
     * @return Map-String of {@link ManagerTask} Name, String of task
     */
    public static ManagerTask generatePingTask() {
        return ExampleGenerators.PING.createTask();
    }
    
    
    /**
     * Generate random seq task
     * 
     * @return Map-String of {@link ManagerTask} Name, String of task
     */
    public static ManagerTask generateSeqTask() {
        return ExampleGenerators.SEQ.createTask();
    }
    
    
    /**
     * Fetch required number of random tasks for type
     * 
     * @param taskType
     * @param nTasks
     * @return List-Map-String of {@link ManagerTask} Name, String of task
     */
    public static List<ManagerTask> generateTasks(ExampleGenerators taskType, int nTasks) {
        List<ManagerTask> output = new ArrayList<>();
        for ( int i = 0; i < nTasks; i ++ ) {
            ManagerTask task = generateTask(taskType);
            output.add(task);
        }
        return output;
    }
    
    
    /**
     * Fetch required number of random tasks for type
     * 
     * @param taskType
     * @param nTasks
     * @return List-Map-String of {@link ItemTask} Name, String of task
     */
    public static List<ItemTask> generateItemTasks(ExampleGenerators taskType, int nTasks) {
        List<ItemTask> output = new ArrayList<>();
        for ( int i = 0; i < nTasks; i++ ) {
            ManagerTask elm = generateTask(taskType);
            ItemTask task = elm.asItemTask();
            task.setTaskName( task.getTaskName() + "-" + i );            
            output.add(task);
        }
        return output;
    }
    
    
    /**
     * Build {@link WorkItem} with required number of tasks
     * 
     * @param taskType
     * @param nTasks
     * 
     * @return {@link WorkItem} 
     */
    public static WorkItem generateExampleWorkItem(ExampleGenerators taskType, int nTasks) {
        Workload workload = BuilderUtility.buildWorkload(generateItemTasks(taskType, nTasks));
        WorkItem output = BuilderUtility.buildWorkItem(taskType.toString(), workload, taskType.name());
        output.setLockId("asdfg");
        return output;
    }
    
    
    /**
     * Generate required of {@link WorkItem} for example tasks
     * 
     * @param taskType
     * @param nItems
     * @param nTasks
     * 
     * @return List-{@link WorkItem} 
     */
    public static List<WorkItem> generateExampleWorkItem(ExampleGenerators taskType, int nItems, int nTasks) {
        List<WorkItem> output = new ArrayList<>();
        for (int i = 0; i < nItems; i++) {
            output.add( generateExampleWorkItem(taskType, nTasks) );
        }
        return output;
    }
}
