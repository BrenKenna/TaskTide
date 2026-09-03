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
package org.tasktide.core.manager.generator;

import java.util.ArrayList;
import java.util.List;

import org.tasktide.core.manager.BuilderUtility;

import org.tasktide.core.manager.ManagerTask;
import org.tasktide.core.model.task.ItemTask;

import org.tasktide.core.model.workitem.WorkItem;
import org.tasktide.core.model.workitem.Workload;


/**
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
     * Generate random {@link PingGenerator} task
     * 
     * @return {@link ManagerTask}
     */
    public static ManagerTask generatePingTask() {
        return ExampleGenerators.PING.createTask();
    }
    
    
    /**
     * Generate random {@link SeqGenerator} task
     * 
     * @return {@link ManagerTask}
     */
    public static ManagerTask generateSeqTask() {
        return ExampleGenerators.SEQ.createTask();
    }
    
    
    /**
     * Generate random {@link NsLookupGenerator} task
     * 
     * @return {@link ManagerTask}
     */
    public static ManagerTask generateNsLookupTask() {
        return ExampleGenerators.NSLOOKUPS.createTask();
    }
    
    
    /**
     * Generate random {@link HostnameGenerator} task
     * 
     * @return {@link ManagerTask}
     */
    public static ManagerTask generateHostnameTask() {
        return ExampleGenerators.HOSTNAME.createTask();
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
            task.setJobEnvId("");
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
        output.setLockId("");
        output.setJobEnvId("");
        output.setStepId("");
        output.setStepName("");
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
