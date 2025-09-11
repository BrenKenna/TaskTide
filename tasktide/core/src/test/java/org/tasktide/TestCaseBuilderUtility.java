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
package org.tasktide;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.tasktide.core.model.builders.ProcessLogBuilder;
import org.tasktide.core.model.builders.TaskLoggingBuilder;
import org.tasktide.core.model.builders.ItemTaskBuilder;
import org.tasktide.core.model.builders.WorkloadBuilder;
import org.tasktide.core.model.builders.WorkItemBuilder;
import org.tasktide.core.model.builders.StepBuilder;
import org.tasktide.core.model.builders.WorkflowBuilder;

import org.tasktide.core.manager.ManagerTask;
import org.tasktide.core.model.task.ProcessLog;
import org.tasktide.core.model.task.TaskLogging;
import org.tasktide.core.model.task.ItemTask;
import org.tasktide.core.model.workitem.Workload;

import org.tasktide.core.model.workitem.WorkItem;
import org.tasktide.core.model.collection.Step;
import org.tasktide.core.model.collection.Workflow;

import org.tasktide.core.model.task.TaskState;
import org.tasktide.core.model.workitem.ItemState;
import org.tasktide.core.model.workitem.ItemType;

import org.tasktide.core.TaskTideRepository;
import org.tasktide.core.repository.json_repo.JsonStepRepository;
import org.tasktide.core.repository.json_repo.JsonWorkItemRepository;
import org.tasktide.core.repository.json_repo.JsonWorkflowRepository;

import org.tasktide.core.manager.generator.TaskGenerator;
import org.tasktide.core.manager.generator.ExampleGenerators;
import static org.tasktide.core.manager.generator.TaskGenerator.generateSeqTask;
import org.tasktide.core.model.CustomAnnotation;


/**
 * Various static methods to support test cases
 * 
 * @author bkenna
 */
public class TestCaseBuilderUtility {
    
    // Task Generator
    public static TaskGenerator taskGenerator = new TaskGenerator();
    
    /**
     * Make {@link ProcessLog ProcessLog} for testing
     * 
     * @return {@link ProcessLog ProcessLog}
     */
    public static ProcessLog makeTestProcessLog() {
        String[] stdout = {"apples", "oragnes"};
        String[] stderr = {"pears", "pineapples"};
        return new ProcessLogBuilder()
            .withId(UUID.randomUUID().toString())
            .withStdout(stdout)
            .withStderr(stderr)
        .build();
    }
    
    
    /**
     * Make {@link TaskLogging TaskLogging} for testing
     * 
     * @return {@link TaskLogging TaskLogging}
     */
    public static TaskLogging makeTestTaskLog() {
        return new TaskLoggingBuilder()
            .withId(UUID.randomUUID().toString())
            .withProcessLog(makeTestProcessLog())
            .withThreadName("myThread")
            .withCpuDuration(-1L)
            .withStartTime(-2L)
            .withEndTime(-3L)
            .withProcId(-4L)
        .build();
    }
    
    
    /**
     * Make {@link ItemTask ItemTask} for testing
     * 
     * @return {@link ItemTask ItemTask}
     */
    public static ItemTask makeTestItemTask() {
        CustomAnnotation anno = TestUtils.makeAnnotation("SomePilotJobLabel", "TemplateRepository-UnitTests");
        return new ItemTaskBuilder()
            .withId(UUID.randomUUID().toString())
            .withTaskName("My Task Name")
            .withTask("My Task")
            .withTaskState(TaskState.COMPLETE)
            .withTaskLog(makeTestTaskLog())
            .withAnnotation(anno)
            .withWorkItemId("")
        .build();
    }
    
    
    /**
     * Make {@link Workload Workload} for testing
     * 
     * @return {@link Workload Workload}
     */
    public static Workload makeTestWorkload() {
        List<ItemTask> itemTasks = new ArrayList<>();
        itemTasks.add(makeTestItemTask());
        itemTasks.add(makeTestItemTask());
        
        return new WorkloadBuilder()
            .withId("My Workload")
            .withWorkload(itemTasks)
            .withWorkloadState(ItemState.LOCKED)
            .withWorkloadType(ItemType.SINGLE)
        .build();
    }
    
    
    /**
     * Make {@link ItemTask ItemTask} for testing
     * 
     * @return {@link WorkItem WorkItem}
     */
    public static WorkItem makeTestWorkItem() {
        CustomAnnotation anno = TestUtils.makeAnnotation("SomePilotJobLabel", "TemplateRepository-UnitTests");
        return new WorkItemBuilder()
            .withId(UUID.randomUUID().toString())
            .withItemName("My WorkItem Name")
            .withWorkload(makeTestWorkload())
            .withLockId(UUID.randomUUID().toString())
            .withLockDate(0L)
            .withDoneDate(0L)
            .withTaskCount(1)
            .withTaskDone(0)
            .withItemState(ItemState.TODO)
            .withItemType(ItemType.SINGLE)
            .withStepName("Arbitrary")
            .withAnnotation(anno)
        .build();
    }
    
    
    /**
     * Make {@link ItemTask ItemTask} for testing
     * 
     * @param stepName
     * @return {@link WorkItem WorkItem}
     */
    public static WorkItem makeTestWorkItem(String stepName) {
        return new WorkItemBuilder()
            .withId(UUID.randomUUID().toString())
            .withItemName("My WorkItem Name")
            .withWorkload(makeTestWorkload())
            .withLockId(UUID.randomUUID().toString())
            .withLockDate(0L)
            .withDoneDate(0L)
            .withTaskCount(1)
            .withTaskDone(0)
            .withItemState(ItemState.TODO)
            .withItemType(ItemType.SINGLE)
            .withStepName(stepName)
            .withAnnotation(new CustomAnnotation())
        .build();
    }
    
    
    /**
     * Make a test {@link Step Step}
     * 
     * @return {@link Step Step}
     */
    public static Step makeTestStep() {
        return new StepBuilder()
            .withStepId(UUID.randomUUID().toString())
            .withStepName("My step name")
            .withStepState(TaskState.PENDING)
            .withStepCount(10)
            .withStepsToDo(5)
            .withStepsLocked(2)
            .withStepsDone(2)
            .withStepsError(1)
            .withWorkflowId(UUID.randomUUID().toString())
            .withCustomAnnotation(new CustomAnnotation())
        .build();
    }
    
    
    /**
     * Make a test {@link Step Step}
     * 
     * @param stepId
     * @param stepName
     * @return {@link Step Step}
     */
    public static Step makeTestStep(String stepId, String stepName) {
        return new StepBuilder()
            .withStepId(stepId)
            .withStepName(stepName)
            .withStepState(TaskState.PENDING)
            .withStepCount(10)
            .withStepsToDo(5)
            .withStepsLocked(2)
            .withStepsDone(2)
            .withStepsError(1)
            .withWorkflowId(UUID.randomUUID().toString())
            .withCustomAnnotation(new CustomAnnotation())
        .build();
    }
    
    
    /**
     * Make test List-{@link Step Step}
     * 
     * @return List-{@link Step Step}
     */
    public static List<Step> makeTestStepList() {
        List<Step> output = new ArrayList<>();
        output.add( makeTestStep("step1", "myFirstStep") );
        output.add( makeTestStep("step2", "mySecondStep") );
        return output;
    }
    
    
    /**
     * Make a test {@link Workflow Workflow}
     * 
     * @param steps
     * @param workflowName
     * @return {@link Workflow Workflow}
     */
    public static Workflow makeTestWorkflow(List<Step> steps, String workflowName) {
        return new WorkflowBuilder()
            .withId(UUID.randomUUID().toString())
            .withWorkflowName(workflowName)
            .withSteps( steps )
            .withCustomAnnotation(new CustomAnnotation())
        .build();
    }
    
    
    /**
     * Make {@link Workflow Workflow} collection
     * 
     * @return List-{@link Workflow Workflow}
     */
    public static List<Workflow> makeTestWorkflows() {
        
        // Initialize vars
        List<Workflow> output = new ArrayList<>();
        List<Step> stepA;
        List<Step> stepB = new ArrayList<>();
        List<Step> stepC = new ArrayList<>();
        
        // Create step lists
        stepA = makeTestStepList();
        stepB.add(makeTestStep("step3", "myThirdStep"));
        stepC.add(makeTestStep("step4", "myFourthStep"));
        
        // Append workflows
        output.add(makeTestWorkflow(stepA, "myFirstWorkflow"));
        output.add(makeTestWorkflow(stepB, "mySecondWorkflow"));
        output.add(makeTestWorkflow(stepC, "myThirdWorkflow") );
        
        // Return results
        return output;
    }
    
    
    /**
     * Fetch seq task
     * 
     * @return {@link ManagerTask ManagerTask}
     */
    public static ManagerTask getSeqTask() {
        return generateSeqTask();
    }
    
    
    /**
     * Fetch ping task
     * 
     * @return {@link ManagerTask ManagerTask}
     */
    public static ManagerTask getPingTask() {
        return TaskGenerator.generatePingTask();
    }
    
    
    /**
     * Generate list of random ping tasks
     * 
     * @param nTasks
     * @return List-{@link ManagerTask ManagerTask}
     */
    public static List<ManagerTask> getPingTasks(int nTasks) {
        return TaskGenerator.generateTasks(ExampleGenerators.PING, nTasks);
    }
    
    
    /**
     * Generate list of random seq tasks
     * 
     * @param nTasks
     * @return List-{@link ManagerTask ManagerTask}
     */
    public static List<ManagerTask> getSeqTasks(int nTasks) {
        return TaskGenerator.generateTasks(ExampleGenerators.SEQ, nTasks);
    }
    
    
    /**
     * Create testing {@link JsonWorkItemRepository JsonWorkflowRepository}
     * 
     * @return {@link TaskTideRepository TaskTideRepository-{@link WorkItem WorkItem}}
     */
    public static TaskTideRepository<WorkItem> createWorkItemJsonRepo() {
    
        // Generate data
        List<WorkItem> data = new ArrayList<>();
        data.add(makeTestWorkItem());
        data.add(makeTestWorkItem());
        data.add(makeTestWorkItem());
        
        // Return results
        return new JsonWorkItemRepository(data, "myData");
    }
    
    
    /**
     * Create testing {@link JsonStepRepository JsonStepRepository}
     * 
     * @return {@link TaskTideRepository TaskTideRepository-{@link Step Step}}
     */
    public static TaskTideRepository<Step> createStepJsonRepo() {
        return new JsonStepRepository(makeTestStepList(), "myData");
    }
    
    
    /**
     * Create testing {@link JsonWorkflowRepository JsonWorkflowRepository}
     * 
     * @return {@link TaskTideRepository TaskTideRepository-{@link Workflow Workflow}}
     */
    public static TaskTideRepository<Workflow> createWorkflowJsonRepo() {
        return new JsonWorkflowRepository(makeTestWorkflows(), "myData");
    }
}
