/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.tasktide;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import org.tasktide.core.model.builders.ModelBuilderProvider;
import org.tasktide.core.model.builders.WorkloadBuilder;

import org.tasktide.core.model.builders.WorkItemBuilder;

import org.tasktide.core.model.workitem.WorkItem;
import org.tasktide.core.model.workitem.Workload;

import org.tasktide.core.model.task.ItemTask;
import org.tasktide.core.model.workitem.ItemState;
import org.tasktide.core.model.workitem.ItemType;

import org.tasktide.core.supporting.ManagerTask;
import org.tasktide.core.supporting.generator.ExampleGenerators;
import org.tasktide.core.supporting.generator.TaskGenerator;


/**
 * Various static methods to make {@link TaskTideModel TaskTideModel } objects
 * 
 * @author bkenna
 */
public class BuilderUtility {
    
    // Model builder
    public static ModelBuilderProvider modelBuidler = new ModelBuilderProvider();
    public static TaskGenerator taskGenerator = new TaskGenerator();
    
    
    /**
     * Make {@link Workload Workload} from {@link ItemTask ItemTask}
     * 
     * @param itemTask
     * @return {@link Workload Workload}
     */
    public static Workload makeWorkload(ItemTask itemTask) {
        return new WorkloadBuilder()
                    .workload(itemTask)
                    .workloadState(ItemState.TODO)
                    .workloadType(ItemType.SINGLE)
                    .build();
    }
    
    
    /**
     * Make {@link ItemType ItemType.NESTED} {@link Workload Workload} from {@link ItemTask ItemTask} list
     * 
     * @param tasks
     * @return {@link Workload Workload}
     */
    public static Workload makeWorkload(List<ItemTask> tasks) {
        return new WorkloadBuilder()
                    .workload(tasks)
                    .workloadState(ItemState.TODO)
                    .workloadType(ItemType.NESTED)
                    .build();
    }
    
    
    /**
     * Make {@link WorkItem} from {@link Workload Workload}
     * 
     * @param itemName
     * @param workload
     * @param stepName
     * @return {@link WorkItem WorkItem}
     */
    public static WorkItem makeWorkItem(String itemName, Workload workload, String stepName) {
        return new WorkItemBuilder()
                .itemName(itemName)
                .workload(workload)
                .itemState(ItemState.TODO)
                .taskCount(workload.getWorkloadSize())
                .itemType(workload.getWorkloadType())
                .stepName(stepName)
                .build();
    }
    
    
    
    /**
     * Fetch seq task
     * 
     * @return {@link ManagerTask ManagerTask}
     */
    public static ManagerTask getSeqTask() {
        return taskGenerator.generateSeqTask();
    }
    
    
    /**
     * Fetch ping task
     * 
     * @return {@link ManagerTask ManagerTask}
     */
    public static ManagerTask getPingTask() {
        return taskGenerator.generatePingTask();
    }
    
    
    /**
     * Generate list of random ping tasks
     * 
     * @param nTasks
     * @return List-{@link ManagerTask ManagerTask}
     */
    public static List<ManagerTask> getPingTasks(int nTasks) {
        return taskGenerator.generateTasks(ExampleGenerators.PING, nTasks);
    }
    
    
    /**
     * Generate list of random seq tasks
     * 
     * @param nTasks
     * @return List-{@link ManagerTask ManagerTask}
     */
    public static List<ManagerTask> getSeqTasks(int nTasks) {
        return taskGenerator.generateTasks(ExampleGenerators.SEQ, nTasks);
    }
}
