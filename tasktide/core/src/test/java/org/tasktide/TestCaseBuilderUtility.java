/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.tasktide;

import java.util.ArrayList;
import java.util.List;

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
            .id("alpha")
            .stdout(stdout)
            .stderr(stderr)
        .build();
    }
    
    
    /**
     * Make {@link TaskLogging TaskLogging} for testing
     * 
     * @return {@link TaskLogging TaskLogging}
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
     * Make {@link ItemTask ItemTask} for testing
     * 
     * @return {@link ItemTask ItemTask}
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
     * Make {@link Workload Workload} for testing
     * 
     * @return {@link Workload Workload}
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
     * Make {@link ItemTask ItemTask} for testing
     * 
     * @return {@link WorkItem WorkItem}
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
     * Make {@link ItemTask ItemTask} for testing
     * 
     * @param stepName
     * @return {@link WorkItem WorkItem}
     */
    public static WorkItem makeTestWorkItem(String stepName) {
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
            .stepName(stepName)
        .build();
    }
    
    
    /**
     * Make a test {@link Step Step}
     * 
     * @return {@link Step Step}
     */
    public static Step makeTestStep() {
        return new StepBuilder()
            .stepId("My Step Id")
            .stepName("My step name")
            .stepState(TaskState.PENDING)
            .stepCount(10)
            .stepsToDo(5)
            .stepsLocked(2)
            .stepsDone(2)
            .stepsError(1)
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
            .stepId(stepId)
            .stepName(stepName)
            .stepState(TaskState.PENDING)
            .stepCount(10)
            .stepsToDo(5)
            .stepsLocked(2)
            .stepsDone(2)
            .stepsError(1)
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
     * @param workflowId
     * @param workflowName
     * @return {@link Workflow Workflow}
     */
    public static Workflow makeTestWorkflow(List<Step> steps, String workflowId, String workflowName) {
        return new WorkflowBuilder()
            .id(workflowId)
            .workflowName(workflowName)
            .steps( steps )
        .build();
    }
    
    
    /**
     * Make {@link Workflow Workflow} collection
     * 
     * @return List-{@link Workflow Workflow}
     */
    public static List<Workflow> makeTestWorkflows() {
        
        // Initialize vars
        List<Workflow> output = new ArrayList();
        List<Step> stepA;
        List<Step> stepB = new ArrayList();
        List<Step> stepC = new ArrayList();
        
        // Create step lists
        stepA = makeTestStepList();
        stepB.add(makeTestStep("step3", "myThirdStep"));
        stepC.add(makeTestStep("step4", "myFourthStep"));
        
        // Append workflows
        output.add(makeTestWorkflow(stepA, "workflow1", "myFirstWorkflow"));
        output.add(makeTestWorkflow(stepB, "workflow2", "mySecondWorkflow"));
        output.add(makeTestWorkflow(stepC, "workflow3", "myThirdWorkflow") );
        
        // Return results
        return output;
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
    
    
    /**
     * Create testing {@link JsonWorkItemRepository JsonWorkflowRepository}
     * 
     * @return {@link TaskTideRepository TaskTideRepository-{@link WorkItem WorkItem}}
     */
    public static TaskTideRepository createWorkItemJsonRepo() {
    
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
    public static TaskTideRepository createStepJsonRepo() {
        return new JsonStepRepository(makeTestStepList(), "myData");
    }
    
    
    /**
     * Create testing {@link JsonWorkflowRepository JsonWorkflowRepository}
     * 
     * @return {@link TaskTideRepository TaskTideRepository-{@link Workflow Workflow}}
     */
    public static TaskTideRepository createWorkflowJsonRepo() {
        return new JsonWorkflowRepository(makeTestWorkflows(), "myData");
    }
}
