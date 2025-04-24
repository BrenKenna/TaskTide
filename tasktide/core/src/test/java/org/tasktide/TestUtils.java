/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.tasktide;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbConfig;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.tasktide.core.model.builders.ItemTaskBuilder;
import org.tasktide.core.model.builders.ModelBuilderProvider;
import org.tasktide.core.model.builders.ProcessLogBuilder;
import org.tasktide.core.model.builders.TaskLoggingBuilder;
import org.tasktide.core.model.builders.WorkItemBuilder;
import org.tasktide.core.model.builders.WorkloadBuilder;
import org.tasktide.core.model.builders.StepBuilder;

import org.tasktide.core.model.task.ItemTask;
import org.tasktide.core.model.task.ProcessLog;
import org.tasktide.core.model.task.TaskLogging;
import org.tasktide.core.model.task.TaskState;
import org.tasktide.core.model.workitem.ItemState;
import org.tasktide.core.model.workitem.ItemType;
import org.tasktide.core.model.workitem.WorkItem;
import org.tasktide.core.model.workitem.Workload;
import org.tasktide.core.model.collection.Step;

import org.tasktide.core.TaskTideRepository;
import org.tasktide.core.model.builders.WorkflowBuilder;
import org.tasktide.core.model.collection.Workflow;
import org.tasktide.core.model.state_summary.StateSummary;
import org.tasktide.core.repository.json_repo.JsonStepRepository;
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
     * @return {@link Workflow Workflow}
     */
    public static Workflow makeTestWorkflow() {
        return new WorkflowBuilder()
                .workflowId("My Workflow Id")
                .workflowName("My Workflow Name")
                .steps( makeTestStepList() )
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
     * @return {@link TaskTideRepository TaskTideRepository}
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
    
    
    /**
     * Create testing workitem json repository
     * 
     * @return {@link TaskTideRepository TaskTideRepository}
     */
    public static TaskTideRepository createStepJsonRepo() {
        return new JsonStepRepository(TestUtils.makeTestStepList(), "myData");
    }
    
    
    /**
     * Represent {@link StateSummary StateSummary} as json string
     * 
     * @param map
     * @return String Json
     */
    public static String mapToJsonString(Map<String, StateSummary<ItemState>> map) {
        Jsonb jsonb = JsonbBuilder.create(new JsonbConfig().withFormatting(true));
        return jsonb.toJson(map);
    }
    
    
    /**
     * Represent step list as json doc
     * 
     * @param steps
     * @return String
     */
    public static String stepsToJsonString(List<Step> steps) {
        return steps.stream()
                .map(Step::toJsonDoc)
                .collect(Collectors.joining(",\n", "{\n", "\n]"));
    }
    
    
    /**
     * Represent step list as json doc
     * 
     * @param workItems
     * @return String
     */
    public static String workItemsToJsonString(List<WorkItem> workItems) {
        return workItems.stream()
                .map(WorkItem::toJsonDoc)
                .collect(Collectors.joining(",\n", "{\n", "\n]"));
    }
}
