/*
 * Copyright 2025 Brendan Kenna.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.tasktide.core.manager;

import java.util.List;
import java.util.Map;

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
import org.tasktide.core.model.CustomAnnotation;
import org.tasktide.core.model.builders.CustomAnnotationBuilder;


/**
 * Various static methods to make {@link TaskTideModel TaskTideModel} objects
 * 
 * @author bkenna
 */
public class BuilderUtility {

    
    /**
     * Make {@link CustomAnnotation} from data map
     * 
     * @param data
     * @return {@link CustomAnnotation}
     */
    public static CustomAnnotation makeAnnotation(Map<String, Object> data) {
        return new CustomAnnotationBuilder()
            .withId("CustomAnnotation-" + Utils.generateSalt())
            .withAnno(data)
        .build();
    }
    
    
    /**
     * Build an empty {@link CustomAnnotation}
     * 
     * @return {@link CustomAnnotation}
     */
    public static CustomAnnotation makeEmptyAnnotation() {
        return new CustomAnnotationBuilder()
            .withId("CustomAnnotation-" + Utils.generateSalt())
        .build();
    }
    
    
    /**
     * Build an empty {@link ProcessLog ProcessLog}
     * 
     * @return Empty {@link ProcessLog ProcessLog}
     */
    public static ProcessLog makeEmptyProcessLog() {
        return new ProcessLogBuilder()
            .withId("ProcessLog-" + Utils.generateSalt())
            .withStdout(new String[0])
            .withStderr(new String[0])
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
            .withId("ProcessLog-" + Utils.generateSalt())
            .withStdout(stdout)
            .withStderr(stderr)
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
            .withId(procLogId)
            .withStdout(stdout)
            .withStderr(stderr)
        .build();
    }
    
    
    /**
     * Build an empty {@link TaskLogging}
     * 
     * @return Empty {@link TaskLogging}
     */
    public static TaskLogging buildEmptyTaskLogging() {
        return new TaskLoggingBuilder()
            .withId("TaskLogging-" + Utils.generateSalt())
            .withProcessLog(makeEmptyProcessLog())
            .withCpuDuration(0L)
            .withEndTime(0L)
            .withExitCode(-1)
            .withProcId(0L)
            .withStartTime(0L)
            .withThreadName("")
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
            .withId("TaskLogging-" + Utils.generateSalt())
            .withProcessLog(procLog)
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
             .withId("TaskLogging-" + Utils.generateSalt())
             .withProcessLog(procLog)
             .withCpuDuration(0L)
             .withProcId(proc.pid())
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
            .withId(taskLogId)
            .withProcessLog(procLog)
        .build();
    }
    
    
    /**
     * Build an empty {@link Workload}
     * 
     * @return Empty {@link Workload}
     */
    public static Workload buildEmptyWorkload() {
        return new WorkloadBuilder()
            .withId( "Workload-" + Utils.generateSalt() )
            .withWorkloadState(ItemState.TODO)
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
            .withId( "Workload-" + Utils.generateSalt() )
            .withWorkload(itemTask)
            .withWorkloadState(ItemState.TODO)
            .withWorkloadType(ItemType.SINGLE)
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
            .withId(workloadId)
            .withWorkload(itemTask)
            .withWorkloadState(ItemState.TODO)
            .withWorkloadType(ItemType.SINGLE)
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
            .withId( "Workload-" + Utils.generateSalt() )
            .withWorkload(tasks)
            .withWorkloadState(ItemState.TODO)
            .withWorkloadType(ItemType.NESTED)
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
            .withId(workloadId)
            .withWorkload(tasks)
            .withWorkloadState(ItemState.TODO)
            .withWorkloadType(ItemType.NESTED)
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
            .withId( "WorkItem-" + Utils.generateSalt() )
            .withItemName(itemName)
            .withWorkload(workload)
            .withItemState(ItemState.TODO)
            .withTaskCount(workload.getWorkloadSize())
            .withItemType(workload.getWorkloadType())
            .withStepName(stepName)
            .withAnnotation( makeEmptyAnnotation() )
            .withJobEnvId("")
        .build();
        output.getWorkload()
            .getTaskMap().values().stream()
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
            .withId( "WorkItem-" + Utils.generateSalt() )
            .withItemName(itemName)
            .withWorkload(workload)
            .withItemState(ItemState.TODO)
            .withTaskCount(workload.getWorkloadSize())
            .withItemType(workload.getWorkloadType())
            .withStepName(stepName)
            .withStepId(stepId)
            .withAnnotation( makeEmptyAnnotation() )
            .withJobEnvId("")
        .build();
        output.getWorkload()
            .getTaskMap().values().stream()
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
            .withId(itemId)
            .withItemName(itemName)
            .withWorkload(workload)
            .withItemState(ItemState.TODO)
            .withTaskCount(workload.getWorkloadSize())
            .withItemType(workload.getWorkloadType())
            .withStepName(stepName)
            .withAnnotation( makeEmptyAnnotation() )
            .withJobEnvId("")
        .build();
        
        output.getWorkload()
            .getTaskMap().values().stream()
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
            .withId(itemId)
            .withItemName(itemName)
            .withWorkload(workload)
            .withItemState(ItemState.TODO)
            .withTaskCount(workload.getWorkloadSize())
            .withItemType(workload.getWorkloadType())
            .withStepName(stepName)
            .withStepId(stepId)
            .withAnnotation( makeEmptyAnnotation() )
            .withJobEnvId("")
        .build();
        
        output.getWorkload()
            .getTaskMap().values().stream()
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
            .withStepId( "Step-" + Utils.generateSalt() )
            .withStepState(TaskState.PENDING)
            .withAnnotation( makeEmptyAnnotation() )
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
            .withStepId( "Step-" + Utils.generateSalt() )
            .withStepName(stepName)
            .withStepState(TaskState.PENDING)
            .withAnnotation( makeEmptyAnnotation() )
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
            .withStepId(stepId)
            .withStepName(stepName)
            .withStepState(TaskState.PENDING)
            .withAnnotation( makeEmptyAnnotation() )
        .build();
    }
    
    
    /**
     * Build an empty {@link Workflow}
     * 
     * @return Empty {@link Workflow}
     */
    public static Workflow buildEmptyWorkflow() {
        return new WorkflowBuilder()
            .withId("Workflow-" + Utils.generateSalt())
            .withAnnotation( makeEmptyAnnotation() )
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
            .withId("Workflow-" + Utils.generateSalt())
            .withWorkflowName(workflowName)
            .withSteps( steps )
            .withAnnotation( makeEmptyAnnotation() )
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
            .withId("Workflow-" + Utils.generateSalt())
            .withWorkflowName(workflowName)
            .withSteps( steps )
            .withAnnotation( makeEmptyAnnotation() )
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