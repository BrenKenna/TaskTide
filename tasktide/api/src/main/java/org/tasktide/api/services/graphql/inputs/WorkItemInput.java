/*
 * Copyright 2026 Bren.
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
package org.tasktide.api.services.graphql.inputs;

import org.eclipse.microprofile.graphql.Input;
import org.eclipse.microprofile.graphql.Description;

import org.tasktide.core.model.CustomAnnotation;

import org.tasktide.core.model.workitem.WorkItem;
import org.tasktide.core.model.workitem.Workload;
import org.tasktide.core.model.workitem.ItemState;
import org.tasktide.core.model.workitem.ItemType;

import org.tasktide.core.model.builders.WorkItemBuilder;

import org.tasktide.core.supporting.JsonUtils;


/**
 * Data model for GraphQL for {@link WorkItem}
 *
 * @author Bren
 */
@Input
@Description("GraphQL Data Model for TaskTide-WorkItem")
public class WorkItemInput {

    private final WorkItemBuilder workItemBuilder = new WorkItemBuilder();
    
    public String workItemId, workItemName,
        lockId, stepName,
        stepId, jobEnvId;
    
    public long lockDate, doneDate;
    
    public int taskCount, taskDone;
    
    @Description("ItemType of WorkItem: Single/Nested")
    public ItemType itemType;
    
    @Description("Last defined ItemState of Step")
    public ItemState itemState;
    
    @Description("Workload as JSON string")
    public String workload;
    
    @Description("CustomAnnotation as JSON string")
    public String anno;
    
    
    /**
     * Parse {@link CustomAnnotation} from JSON string
     * 
     * @return {@link CustomAnnotation}
     */
    public CustomAnnotation parseAnnotation() {
        return JsonUtils.fromJson(this.anno, CustomAnnotation.class);
    }
    
    
    /**
     * Parse {@link Workload} from JSON string
     * 
     * @return {@link Workload}
     */
    public Workload parseWorkload() {
        return JsonUtils.fromJson(this.workload, Workload.class);
    }
    
    
    /**
     * Represent as {@link WorkItem}
     * 
     * @return {@link WorkItem}
     */
    public WorkItem asWorkItem() {
        return this.workItemBuilder
            .withId(workItemId)
            .withItemName(this.workItemName)
            .withItemType(itemType)
            .withItemState(itemState)
            .withLockId(lockId)
            .withLockDate(lockDate)
            .withDoneDate(doneDate)
            .withTaskCount(this.taskCount)
            .withTaskDone(this.taskDone)
            .withWorkload(this.parseWorkload())
            .withStepName(this.stepName)
            .withStepId(stepId)
            .withJobEnvId(jobEnvId)
            .withAnnotation(this.parseAnnotation())
        .build();
    }
}