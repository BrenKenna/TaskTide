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
package org.tasktide.core.manager.command.commands;

import jakarta.json.bind.annotation.JsonbProperty;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.tasktide.core.manager.TaskTideServiceManager;
import org.tasktide.core.manager.command.CommandSpec;
import org.tasktide.core.manager.command.CommandType;
import org.tasktide.core.manager.command.ManagerAction;
import org.tasktide.core.manager.command.ManagerTarget;

import org.tasktide.core.model.state_summary.StateSummaryOld;
import org.tasktide.core.model.state_summary.ItemState;
import org.tasktide.core.supporting.FileIO;

// For JavaDocs
import org.tasktide.core.TaskTideModel;
import org.tasktide.core.model.collection.Step;
import org.tasktide.core.model.task.ItemTask;
import org.tasktide.core.model.workitem.WorkItem;


/**
 * Carries logic for summarizing {@link TaskTideModel},
 *  {@link ItemState}
 *
 * @author Brendan Kenna
 */
public class SummarizeCommand extends AbstractCommand {
    
    // Attibutes
    private final Logger LOGGER = LogManager.getLogger(SummarizeCommand.class);
    private String STEP_ID;

    
    /**
     * Construct summarize command
     * 
     * @param action
     * @param target
     * @param cmdSpec 
     * @param cmdType 
     */
    public SummarizeCommand(
        @JsonbProperty("Manager Action") ManagerAction action,
        @JsonbProperty("Manager Target") ManagerTarget target,
        @JsonbProperty("Command Spec") CommandSpec cmdSpec,
        @JsonbProperty("Command Type") CommandType cmdType
    ) {
        super(action, target, cmdSpec, cmdType);
    }

    
    /**
     * Performs required summary action
     * 
     * @return boolean
     */
    @Override
    public Object runCommand() {
        
        switch ( this.action ) {
        
            case SUMMARIZE -> {
                LOGGER.info("Summarizing collection");
                StateSummaryOld<ItemState> summary = this.summarize();
                
                if ( !this.directOutput(summary) ) {
                    LOGGER.info("Directing results to stdout");
                    return summary;
                }
                else {
                    return true;
                }
            }
            
            case SUMMARIZE_BY_ITEM_TASK -> {
                LOGGER.info("Summarizing ItemTask state across collection");
                StateSummaryOld<ItemState> summary = this.summarizeAcrossItemTask();
                
                if ( !this.directOutput(summary) ) {
                    LOGGER.info("Directing results to stdout");
                    return summary;
                }
                else {
                    return true;
                }
            }
            
            case SUMMARIZE_EACH -> {
                LOGGER.info("Summarizing each element in collection");
                Map<String, StateSummaryOld<ItemState>> summaries = this.summarizeEach();
                if ( !this.directOutput(summaries) ) {
                    LOGGER.info("Directing results to stdout");
                    return summaries;
                }
                else {
                    return true;
                }
            }
            
            default -> {
                ManagerAction[] actions = {ManagerAction.SUMMARIZE, ManagerAction.SUMMARIZE_EACH};
                String msg = String.format("Summarize command must be one of:\t'%s'", (Object[]) actions);
                LOGGER.error("Unable to determine summary action");
                throw new IllegalArgumentException(msg);
            }
        }
    }

    
    /**
     * Direct results to stdout or the the configured file path
     *  from {@link CommandSpec}
     * 
     * @param results
     * @return boolean
     */
    private boolean directOutput(Object results) {
        if ( this.cmdSpec.getFilePath().isPresent() ) {
            String filePath = this.cmdSpec.getFilePath().get();
            if ( !filePath.isEmpty() ) {
                LOGGER.info("Directing output to '{}'", filePath);
                return FileIO.exportJson(true, results, filePath);
            }
        }
        return false;
    } 
    
    
    /**
     * Validates {@link CommandSpec} for summary
     * 
     * @return boolean
     */
    @Override
    public boolean validateCommand() {
        if ( !this.cmdSpec.hasOptionsKey("Step Name") ) {
            LOGGER.error("No Step name provided");
            return false;
        }
        
        String step = (String) this.cmdSpec.getOptionsKey("Step Name").get();
        List<Step> steps = TaskTideServiceManager.fetchStepService().viewByField("stepName", step);
        if ( steps.isEmpty() ) {
            LOGGER.error("No step found for queried '{}' Step", step);
            return false;
        }
        
        this.STEP_ID = steps.get(0).getId();
        return true;
    }
    
    
    /**
     * Summarize
     * 
     * @return 
     */
    public StateSummaryOld<ItemState> summarize() {
    
        // Initialize vars
        List<WorkItem> workItems;
        StateSummaryOld<ItemState> output;
        
        // Fetch work items
        workItems = TaskTideServiceManager
            .fetchWorkItemService()
        .viewByField("stepId", this.STEP_ID);
        
        // Collapse task count by state
        Map<ItemState, Integer> stateCount = ItemState.fetchEmptyStateMap();
        for ( WorkItem task : workItems ) {
            ItemState state = task.getItemState();
            if ( stateCount.isEmpty() ) {
                stateCount.put(state, 1);
            }
            
            else if ( !stateCount.containsKey( state ) ) {
                stateCount.put(state, 1);
            }
            
            else {
                int count = stateCount.get( state );
                stateCount.put(state, count+1);
            }
        }
        
        // Return results as StateSummaryOld
        output = new StateSummaryOld<>(stateCount);
        return output;
    }
    
    
    
    /**
     * Collapses counts of {@link ItemState} across all collection units
     * 
     * @return {@link StateSummaryOld} of {@link ItemState}
     */
    public StateSummaryOld<ItemState> summarizeAcrossItemTask() {
        
        // Fetch coordinating arguments
        StateSummaryOld<ItemState> output;
        
        // Collect into concurrent map
        Map<ItemState, Integer> results = TaskTideServiceManager
            .fetchWorkItemService()
            .viewByField("stepId", this.STEP_ID)
            .parallelStream()
                .map( elm -> elm.summarizeByState() )
                .flatMap( map -> map.entrySet().stream() )
        .collect(Collectors.toConcurrentMap(
            Map.Entry::getKey,
            Map.Entry::getValue,
            Integer::sum
        ));
        
        // Provide as hash map
        output = new StateSummaryOld<>(new HashMap<>(results));
        return output;
    }
    
    
    /**
     * Fetches count of {@link ItemTask} {@link ItemState} per {@link WorkItem}
     * 
     * @return Map-String, {@link StateSummaryOld} of {@link ItemState}
     */
    public Map<String, StateSummaryOld<ItemState>> summarizeEach() {

        // Calculate results
        Map<String, StateSummaryOld<ItemState>> results;
        results =
            TaskTideServiceManager
            .fetchWorkItemService()
            .viewByField("stepId", this.STEP_ID)
            .parallelStream()
        .collect(Collectors.toConcurrentMap(elm -> elm.getId(),
            elm -> new StateSummaryOld<ItemState>(elm.summarizeByState())
        ));
        
        // Provide as hash map
        return new HashMap<>(results);
    }
}