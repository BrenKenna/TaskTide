/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.tasktide;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.tasktide.core.model.builders.ItemTaskBuilder;
import org.tasktide.core.model.builders.ModelBuilderProvider;
import org.tasktide.core.model.builders.ProcessLogBuilder;
import org.tasktide.core.model.builders.TaskLoggingBuilder;
import org.tasktide.core.model.builders.WorkItemBuilder;
import org.tasktide.core.model.builders.WorkloadBuilder;

import org.tasktide.core.model.task.ItemTask;
import org.tasktide.core.model.task.ProcessLog;
import org.tasktide.core.model.task.TaskLogging;
import org.tasktide.core.model.task.TaskState;
import org.tasktide.core.model.workitem.ItemState;
import org.tasktide.core.model.workitem.ItemType;
import org.tasktide.core.model.workitem.WorkItem;
import org.tasktide.core.model.workitem.Workload;

import org.tasktide.core.repository.TaskTideRepository;
import org.tasktide.core.repository.json_repo.JsonWorkItemRepository;

import org.tasktide.core.supporting.generator.TaskGenerator;
import org.tasktide.core.supporting.generator.TaskType;


/**
 *
 * Utility to help testing model classes
 * 
 * @author bkenna
 */
public class TestUtils {
    
    // Model builder
    public static ModelBuilderProvider modelBuidler = new ModelBuilderProvider();
    public static TaskGenerator taskGenerator = new TaskGenerator();
    
    
    /**
     * Make ProcessLog for testing
     * 
     * @return ProcessLog
     */
    public static ProcessLog makeTestProcessLog() {
        String[] stdout = {"apples", "oragnes"};
        String[] stderr = {"pears", "pineapples"};
        return new ProcessLogBuilder()
                    .id("alpha")
                    .stdout(stdout)
                    .stderr(stderr)
                    .build();
    }
    
    
    /**
     * Make TaskLogging for testing
     * 
     * @return TaskLog
     */
    public static TaskLogging makeTestTaskLog() {
        return new TaskLoggingBuilder()
                   .id("beta")
                   .processLog(makeTestProcessLog())
                   .threadName("myThread")
                   .cpuDuration(-1L)
                   .startTime(-2L)
                   .endTime(-3L)
                   .procId(-4L)
                   .build();
    }
    
    
    /**
     * Make ItemTask for testing
     * 
     * @return ItemTask
     */
    public static ItemTask makeTestItemTask() {
        return new ItemTaskBuilder()
                    .id("gamma")
                    .taskName("My Task Name")
                    .task("My Task")
                    .taskState(TaskState.COMPLETE)
                    .taskLog(makeTestTaskLog())
                    .build();
    }
    
    
    /**
     * Make Workload for testing
     * 
     * @return Workload
     */
    public static Workload makeTestWorkload() {
        List<ItemTask> itemTasks = new ArrayList<>();
        itemTasks.add(makeTestItemTask());
        itemTasks.add(makeTestItemTask());
        
        return new WorkloadBuilder()
                    .id("My Workload")
                    .workload(itemTasks)
                    .build();
    }
    
    
    /**
     * Make ItemTask for testing
     * 
     * @return WorkItem
     */
    public static WorkItem makeTestWorkItem() {
        return new WorkItemBuilder()
                .id("My WorkItem")
                .itemName("My WorkItem Name")
                .workload(makeTestWorkload())
                .lockId("Some random hexadecimal string")
                .lockDate(0L)
                .doneDate(0L)
                .taskCount(1)
                .taskDone(0)
                .itemState(ItemState.TODO)
                .itemType(ItemType.SINGLE)
                .build();
    }
   
    
    /**
     * Fetch seq task
     * 
     * @return Map-String, String
     */
    public static Map<String, String> getSeqTask() {
        return taskGenerator.generateSeqTask();
    }
    
    
    /**
     * Fetch ping task
     * 
     * @return Map-String, String
     */
    public static Map<String, String> getPingTask() {
        return taskGenerator.generatePingTask();
    }
    
    
    /**
     * Generate list of random ping tasks
     * 
     * @param nTasks
     * @return List-Map-String, String
     */
    public static List<Map<String, String>> getPingTasks(int nTasks) {
        return taskGenerator.generateTasks(TaskType.PING, nTasks);
    }
    
    
    /**
     * Generate list of random seq tasks
     * 
     * @param nTasks
     * @return List-Map-String, String
     */
    public static List<Map<String, String>> getSeqTasks(int nTasks) {
        return taskGenerator.generateTasks(TaskType.SEQ, nTasks);
    }
    
    
    /**
     * Create testing workitem json repository
     * 
     * @return 
     */
    public static TaskTideRepository createWorkItemJsonRepo() {
    
        // Generate data
        List<WorkItem> data = new ArrayList<>();
        data.add(TestUtils.makeTestWorkItem());
        data.add(TestUtils.makeTestWorkItem());
        data.add(TestUtils.makeTestWorkItem());
        
        // Return results
        return new JsonWorkItemRepository(data, "myData");
    }
}
