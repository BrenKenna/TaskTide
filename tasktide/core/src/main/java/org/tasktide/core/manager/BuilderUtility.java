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
    private static final TaskGenerator taskGenerator = new TaskGenerator();
    private static final Utils utils = new Utils("dd/MM/yy HH:mm:ss", 4);

    
    /**
     * Build an empty {@link ProcessLog ProcessLog}
     * 
     * @return Empty {@link ProcessLog ProcessLog}
     */
    public static ProcessLog makeEmptyProcessLog() {
        return new ProcessLogBuilder()
            .id("ProcessLog-" + utils.generateSalt())
        .build();
    }

    
    /**
     * Build a {@link ProcessLog ProcessLog} from stdout/err string[] 
     * 
     * @param stdout
     * @param stderr
     * @return {@link ProcessLog ProcessLog}
     */
    public static ProcessLog buildProcessLog(String[] stdout, String[] stderr) {
        return new ProcessLogBuilder()
            .id("ProcessLog-" + utils.generateSalt())
            .stdout(stdout)
            .stderr(stderr)
        .build();
    }
    
    
    /**
     * Build a {@link ProcessLog ProcessLog} from stdout/err string[] 
     * 
     * @param procLogId
     * @param stdout
     * @param stderr
     * @return {@link ProcessLog ProcessLog}
     */
    public static ProcessLog buildProcessLog(String procLogId, String[] stdout, String[] stderr) {
        return new ProcessLogBuilder()
            .id(procLogId)
            .stdout(stdout)
            .stderr(stderr)
        .build();
    }
    
    
    /**
     * Build an empty {@link TaskLogging TaskLogging}
     * 
     * @return Empty {@link TaskLogging TaskLogging}
     */
    public static TaskLogging buildEmptyTaskLogging() {
        return new TaskLoggingBuilder()
            .id("TaskLogging-" + utils.generateSalt())
        .build();
    }
    
    
    /**
     * Build a {@link TaskLogging TaskLogging} with a {@link ProcessLog ProcessLog}
     * 
     * @param procLog
     * @return 
     */
    public static TaskLogging buildTaskLogging(ProcessLog procLog) {
        return new TaskLoggingBuilder()
            .id("TaskLogging-" + utils.generateSalt())
            .processLog(procLog)
        .build();
    }
    
    
    /**
     * Build {@link TaskLogging TaskLogging} from {@link ProcessLog ProcessLog} and process
     * 
     * @param procLog
     * @param proc
     * @return {@link TaskLogging TaskLogging}
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
     * Build a {@link TaskLogging TaskLogging} with a {@link ProcessLog ProcessLog}
     * 
     * @param taskLogId
     * @param procLog
     * @return TaskLogging
     */
    public static TaskLogging buildTaskLogging(String taskLogId, ProcessLog procLog) {
        return new TaskLoggingBuilder()
            .id(taskLogId)
            .processLog(procLog)
        .build();
    }
    
    
    /**
     * Build an empty {@link Workload Workload}
     * 
     * @return Empty {@link Workload Workload}
     */
    public static Workload buildEmptyWorkload() {
        return new WorkloadBuilder()
            .id( "Workload-" + utils.generateSalt() )
            .workloadState(ItemState.TODO)
        .build();
    }
    
    
    /**
     * Build a {@link Workload Workload} from {@link ItemTask ItemTask}
     * 
     * @param itemTask
     * @return {@link Workload Workload}
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
     * Build a {@link Workload Workload} from {@link ItemTask ItemTask}
     * 
     * @param workloadId
     * @param itemTask
     * @return {@link Workload Workload}
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
     * Build a {@link ItemType ItemType.NESTED} {@link Workload Workload} from {@link ItemTask ItemTask} list
     * 
     * @param tasks
     * @return {@link Workload Workload}
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
     * Build a {@link ItemType ItemType.NESTED} {@link Workload Workload} from {@link ItemTask ItemTask} list
     * 
     * @param workloadId
     * @param tasks
     * @return {@link Workload Workload}
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
     * Build a {@link WorkItem} from {@link Workload Workload}
     * 
     * @param itemName
     * @param workload
     * @param stepName
     * @return {@link WorkItem WorkItem}
     */
    public static WorkItem buildWorkItem(String itemName, Workload workload, String stepName) {
        return new WorkItemBuilder()
            .id( "WorkItem-" + utils.generateSalt() )
            .itemName(itemName)
            .workload(workload)
            .itemState(ItemState.TODO)
            .taskCount(workload.getWorkloadSize())
            .itemType(workload.getWorkloadType())
            .stepName(stepName)
        .build();
    }
    
    
    /**
     * Build a {@link WorkItem} from {@link Workload Workload}
     * 
     * @param itemId
     * @param itemName
     * @param workload
     * @param stepName
     * @return {@link WorkItem WorkItem}
     */
    public static WorkItem buildWorkItem(String itemId, String itemName, Workload workload, String stepName) {
        return new WorkItemBuilder()
            .id(itemId)
            .itemName(itemName)
            .workload(workload)
            .itemState(ItemState.TODO)
            .taskCount(workload.getWorkloadSize())
            .itemType(workload.getWorkloadType())
            .stepName(stepName)
        .build();
    }
    
    
    /**
     * Build an empty {@link Step Step}
     * 
     * @return Empty {@link Step Step}
     */
    public static Step buildEmptyStep() {
        return new StepBuilder()
            .stepId( "Step-" + utils.generateSalt() )
            .stepState(TaskState.PENDING)
        .build();
    }
    
    
    /**
     * Make a {@link Step Step}
     * 
     * @param stepName
     * @return {@link Step Step}
     */
    public static Step buildStep(String stepName) {
        return new StepBuilder()
            .stepId( "Step-" + utils.generateSalt() )
            .stepName(stepName)
            .stepState(TaskState.PENDING)
        .build();
    }
    
    
    /**
     * Make a {@link Step Step}
     * 
     * @param stepId
     * @param stepName
     * @return {@link Step Step}
     */
    public static Step buildStep(String stepId, String stepName) {
        return new StepBuilder()
            .stepId(stepId)
            .stepName(stepName)
            .stepState(TaskState.PENDING)
        .build();
    }
    
    
    /**
     * Build an empty {@link Workflow Workflow}
     * 
     * @return Empty {@link Workflow Workflow}
     */
    public static Workflow buildEmptyWorkflow() {
        return new WorkflowBuilder()
            .id("Workflow-" + utils.generateSalt())
        .build();
    }
    
    
    /**
     * Build {@link Workflow Workflow}
     * 
     * @param steps
     * @param workflowName
     * @return {@link Workflow Workflow}
     */
    public static Workflow buildWorkflow(List<Step> steps, String workflowName) {
        return new WorkflowBuilder()
            .id("Workflow-" + utils.generateSalt())
            .workflowName(workflowName)
            .steps( steps )
        .build();
    }
    
    
    /**
     * Build {@link Workflow Workflow}
     * 
     * @param steps
     * @param workflowId
     * @param workflowName
     * @return {@link Workflow Workflow}
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
