/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.tasktide.core.manager;

import java.util.List;

import org.tasktide.core.model.builders.ProcessLogBuilder;
import org.tasktide.core.model.builders.StepBuilder;
import org.tasktide.core.model.builders.TaskLoggingBuilder;
import org.tasktide.core.model.builders.WorkloadBuilder;
import org.tasktide.core.model.builders.WorkItemBuilder;

import org.tasktide.core.TaskTideModel;
import org.tasktide.core.model.builders.WorkflowBuilder;
import org.tasktide.core.model.task.ItemTask;
import org.tasktide.core.model.task.ProcessLog;
import org.tasktide.core.model.task.TaskLogging;
import org.tasktide.core.model.workitem.Workload;

import org.tasktide.core.model.workitem.WorkItem;
import org.tasktide.core.model.collection.Step;
import org.tasktide.core.model.collection.Workflow;

import org.tasktide.core.model.workitem.ItemState;
import org.tasktide.core.model.workitem.ItemType;
import org.tasktide.core.model.task.TaskState;

import org.tasktide.core.supporting.Utils;
import org.tasktide.core.manager.generator.TaskGenerator;
import org.tasktide.core.manager.generator.ExampleGenerators;


/**
 * Various static methods to make {@link TaskTideModel TaskTideModel} objects
 * 
 * @author bkenna
 */
public class BuilderUtility {
    
    // Task Generator
    private static final Utils utils = new Utils("dd/MM/yy HH:mm:ss", 4);

    
    /**
     * Fetch a random Id
     * 
     * @return String
     */
    public static String fetchRandomId() {
        return utils.generateSalt();
    }
    
    
    public static String fetchRandomToken() {
        return utils.generateBase64Token();
    }
    
    
    /**
     * Build an empty {@link ProcessLog ProcessLog}
     * 
     * @return Empty {@link ProcessLog ProcessLog}
     */
    public static ProcessLog makeEmptyProcessLog() {
        return new ProcessLogBuilder()
            .id("ProcessLog-" + utils.generateSalt())
            .stdout(new String[0])
            .stderr(new String[0])
        .build();
    }

    
    /**
     * Build a {@link ProcessLog} from stdout/err string[] 
     * 
     * @param stdout
     * @param stderr
     * @return {@link ProcessLog}
     */
    public static ProcessLog buildProcessLog(String[] stdout, String[] stderr) {
        return new ProcessLogBuilder()
            .id("ProcessLog-" + utils.generateSalt())
            .stdout(stdout)
            .stderr(stderr)
        .build();
    }
    
    
    /**
     * Build a {@link ProcessLog} from stdout/err string[] 
     * 
     * @param procLogId
     * @param stdout
     * @param stderr
     * @return {@link ProcessLog}
     */
    public static ProcessLog buildProcessLog(String procLogId, String[] stdout, String[] stderr) {
        return new ProcessLogBuilder()
            .id(procLogId)
            .stdout(stdout)
            .stderr(stderr)
        .build();
    }
    
    
    /**
     * Build an empty {@link TaskLogging}
     * 
     * @return Empty {@link TaskLogging}
     */
    public static TaskLogging buildEmptyTaskLogging() {
        return new TaskLoggingBuilder()
            .id("TaskLogging-" + utils.generateSalt())
            .processLog(makeEmptyProcessLog())
            .cpuDuration(0L)
            .endTime(0L)
            .exitCode(-1)
            .procId(0L)
            .startTime(0L)
            .threadName("")
        .build();
    }
    
    
    /**
     * Build a {@link TaskLogging} with a {@link ProcessLog}
     * 
     * @param procLog
     * @return {@link TaskLogging}
     */
    public static TaskLogging buildTaskLogging(ProcessLog procLog) {
        return new TaskLoggingBuilder()
            .id("TaskLogging-" + utils.generateSalt())
            .processLog(procLog)
        .build();
    }
    
    
    /**
     * Build {@link TaskLogging} from {@link ProcessLog} and process
     * 
     * @param procLog
     * @param proc
     * @return {@link TaskLogging}
     */
    public static TaskLogging buildTaskLogging(ProcessLog procLog, Process proc) {
        return new TaskLoggingBuilder()
             .id("TaskLogging-" + utils.generateSalt())
             .processLog(procLog)
             .cpuDuration(0L)
             .procId(proc.pid())
        .build();
             
    }
    
    
    /**
     * Build a {@link TaskLogging} with a {@link ProcessLog}
     * 
     * @param taskLogId
     * @param procLog
     * @return {@link TaskLogging}
     */
    public static TaskLogging buildTaskLogging(String taskLogId, ProcessLog procLog) {
        return new TaskLoggingBuilder()
            .id(taskLogId)
            .processLog(procLog)
        .build();
    }
    
    
    /**
     * Build an empty {@link Workload}
     * 
     * @return Empty {@link Workload}
     */
    public static Workload buildEmptyWorkload() {
        return new WorkloadBuilder()
            .id( "Workload-" + utils.generateSalt() )
            .workloadState(ItemState.TODO)
        .build();
    }
    
    
    /**
     * Build a {@link Workload} from {@link ItemTask}
     * 
     * @param itemTask
     * @return {@link Workload}
     */
    public static Workload buildWorkload(ItemTask itemTask) {
        return new WorkloadBuilder()
            .id( "Workload-" + utils.generateSalt() )
            .workload(itemTask)
            .workloadState(ItemState.TODO)
            .workloadType(ItemType.SINGLE)
        .build();
    }
    
    
    /**
     * Build a {@link Workload} from {@link ItemTask}
     * 
     * @param workloadId
     * @param itemTask
     * @return {@link Workload}
     */
    public static Workload buildWorkload(String workloadId, ItemTask itemTask) {
        return new WorkloadBuilder()
            .id(workloadId)
            .workload(itemTask)
            .workloadState(ItemState.TODO)
            .workloadType(ItemType.SINGLE)
        .build();
    }
    
    
    /**
     * Build a {@link ItemType ItemType.NESTED} {@link Workload} from {@link ItemTask} list
     * 
     * @param tasks
     * @return {@link Workload}
     */
    public static Workload buildWorkload(List<ItemTask> tasks) {
        return new WorkloadBuilder()
            .id( "Workload-" + utils.generateSalt() )
            .workload(tasks)
            .workloadState(ItemState.TODO)
            .workloadType(ItemType.NESTED)
        .build();
    }
    
    /**
     * Build a {@link ItemType ItemType.NESTED} {@link Workload} from {@link ItemTask} list
     * 
     * @param workloadId
     * @param tasks
     * @return {@link Workload}
     */
    public static Workload buildWorkload(String workloadId, List<ItemTask> tasks) {
        return new WorkloadBuilder()
            .id(workloadId)
            .workload(tasks)
            .workloadState(ItemState.TODO)
            .workloadType(ItemType.NESTED)
        .build();
    }
    
    
    /**
     * Build a {@link WorkItem} from {@link Workload}
     * 
     * @param itemName
     * @param workload
     * @param stepName
     * @return {@link WorkItem}
     */
    public static WorkItem buildWorkItem(String itemName, Workload workload, String stepName) {
        WorkItem output = new WorkItemBuilder()
            .id( "WorkItem-" + utils.generateSalt() )
            .itemName(itemName)
            .workload(workload)
            .itemState(ItemState.TODO)
            .taskCount(workload.getWorkloadSize())
            .itemType(workload.getWorkloadType())
            .stepName(stepName)
        .build();
        output.getWorkload()
            .getWorkload().values().stream()
            .forEach(
               elm -> elm.setWorkItemId(output.getId())
        );
        return output;
    }
    
    
    /**
     * Build with stepId
     * 
     * @param itemName
     * @param workload
     * @param stepName
     * @param stepId
     * @return {@link WorkItem}
     */
    public static WorkItem buildWorkItem(String itemName, Workload workload, String stepName, String stepId) {
        WorkItem output = new WorkItemBuilder()
            .id( "WorkItem-" + utils.generateSalt() )
            .itemName(itemName)
            .workload(workload)
            .itemState(ItemState.TODO)
            .taskCount(workload.getWorkloadSize())
            .itemType(workload.getWorkloadType())
            .stepName(stepName)
            .stepId(stepId)
        .build();
        output.getWorkload()
            .getWorkload().values().stream()
            .forEach(
               elm -> elm.setWorkItemId(output.getId())
        );
        return output;
    }
    
    
    /**
     * Build a {@link WorkItem} from {@link Workload}
     * 
     * @param itemId
     * @param itemName
     * @param workload
     * @param stepName
     * @return {@link WorkItem}
     */
    public static WorkItem buildWorkItem(String itemId, String itemName, Workload workload, String stepName) {
        WorkItem output = new WorkItemBuilder()
            .id(itemId)
            .itemName(itemName)
            .workload(workload)
            .itemState(ItemState.TODO)
            .taskCount(workload.getWorkloadSize())
            .itemType(workload.getWorkloadType())
            .stepName(stepName)
        .build();
        
        output.getWorkload()
            .getWorkload().values().stream()
            .forEach(
               elm -> elm.setWorkItemId(output.getId())
        );
        return output;
    }
    
    
    /**
     * Build with StepId
     * 
     * @param itemId
     * @param itemName
     * @param workload
     * @param stepName
     * @param stepId
     * 
     * @return {@link WorkItem}
     */
    public static WorkItem buildWorkItem(String itemId, String itemName, Workload workload, String stepName, String stepId) {
        WorkItem output = new WorkItemBuilder()
            .id(itemId)
            .itemName(itemName)
            .workload(workload)
            .itemState(ItemState.TODO)
            .taskCount(workload.getWorkloadSize())
            .itemType(workload.getWorkloadType())
            .stepName(stepName)
            .stepId(stepId)
        .build();
        
        output.getWorkload()
            .getWorkload().values().stream()
            .forEach(
               elm -> elm.setWorkItemId(output.getId())
        );
        return output;
    }
    
    
    /**
     * Build an empty {@link Step}
     * 
     * @return Empty {@link Step}
     */
    public static Step buildEmptyStep() {
        return new StepBuilder()
            .stepId( "Step-" + utils.generateSalt() )
            .stepState(TaskState.PENDING)
        .build();
    }
    
    
    /**
     * Make a {@link Step}
     * 
     * @param stepName
     * @return {@link Step}
     */
    public static Step buildStep(String stepName) {
        return new StepBuilder()
            .stepId( "Step-" + utils.generateSalt() )
            .stepName(stepName)
            .stepState(TaskState.PENDING)
        .build();
    }
    
    
    /**
     * Make a {@link Step}
     * 
     * @param stepId
     * @param stepName
     * @return {@link Step}
     */
    public static Step buildStep(String stepId, String stepName) {
        return new StepBuilder()
            .stepId(stepId)
            .stepName(stepName)
            .stepState(TaskState.PENDING)
        .build();
    }
    
    
    /**
     * Build an empty {@link Workflow}
     * 
     * @return Empty {@link Workflow}
     */
    public static Workflow buildEmptyWorkflow() {
        return new WorkflowBuilder()
            .id("Workflow-" + utils.generateSalt())
        .build();
    }
    
    
    /**
     * Build {@link Workflow}
     * 
     * @param steps
     * @param workflowName
     * @return {@link Workflow}
     */
    public static Workflow buildWorkflow(List<Step> steps, String workflowName) {
        return new WorkflowBuilder()
            .id("Workflow-" + utils.generateSalt())
            .workflowName(workflowName)
            .steps( steps )
        .build();
    }
    
    
    /**
     * Build {@link Workflow}
     * 
     * @param steps
     * @param workflowId
     * @param workflowName
     * @return {@link Workflow}
     */
    public static Workflow buildWorkflow(List<Step> steps, String workflowId, String workflowName) {
        return new WorkflowBuilder()
            .id("Workflow-" + utils.generateSalt())
            .workflowName(workflowName)
            .steps( steps )
        .build();
    }
    
    
    /**
     * Fetch seq task
     * 
     * @return {@link ManagerTask}
     */
    public static ManagerTask getSeqTask() {
        return TaskGenerator.generateSeqTask();
    }
    
    
    /**
     * Fetch ping task
     * 
     * @return {@link ManagerTask}
     */
    public static ManagerTask getPingTask() {
        return TaskGenerator.generatePingTask();
    }
    
    
    /**
     * Generate list of random ping tasks
     * 
     * @param nTasks
     * @return List-{@link ManagerTask}
     */
    public static List<ManagerTask> getPingTasks(int nTasks) {
        return TaskGenerator.generateTasks(ExampleGenerators.PING, nTasks);
    }
    
    
    /**
     * Generate list of random seq tasks
     * 
     * @param nTasks
     * @return List-{@link ManagerTask}
     */
    public static List<ManagerTask> getSeqTasks(int nTasks) {
        return TaskGenerator.generateTasks(ExampleGenerators.SEQ, nTasks);
    }
}
