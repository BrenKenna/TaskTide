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

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.tasktide.core.manager.TaskTideServiceManager;
import org.tasktide.core.manager.command.CommandSpec;
import org.tasktide.core.manager.command.ManagerAction;
import org.tasktide.core.manager.command.ManagerTarget;

import org.tasktide.core.model.state_summary.StateSummary;
import org.tasktide.core.model.workitem.ItemState;


/**
 * Carries logic for summarizing {@link TaskTideModel},
 *  {@link ItemState}
 *
 * @author Brendan Kenna
 */
public class SummarizeCommand extends AbstractCommand {
    
    // Attibutes
    private final Logger LOGGER = LogManager.getLogger(SummarizeCommand.class);

    
    /**
     * Construct summarize command
     * 
     * @param action
     * @param target
     * @param cmdSpec 
     */
    public SummarizeCommand(ManagerAction action, ManagerTarget target, CommandSpec cmdSpec) {
        super(action, target, cmdSpec);
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
                StateSummary<ItemState> summary = this.summarize();
                return summary;
            }
            
            case SUMMARIZE_EACH -> {
                LOGGER.info("Summarizing each element in collection");
                Map<String, StateSummary<ItemState>> summaries = this.summarizeEach();
                return summaries;
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
     * Validates {@link CommandSpec} for summary
     * 
     * @return boolean
     */
    @Override
    public boolean validateCommand() {
        if ( !this.cmdSpec.hasOptionsKey("Step Name") ) {
            return false;
        }
        
        return true;
    }
    
    
    /**
     * Collapses counts of {@link ItemState} across all collection units
     * 
     * @return {@link StateSummary} of {@link ItemState}
     */
    public StateSummary<ItemState> summarize() {
        
        // Fetch coordinating arguments
        StateSummary<ItemState> output;
        String step = (String) this.cmdSpec.getOptionsKey("Step Name").get();
        String stepId = TaskTideServiceManager.fetchStepService().viewByField("stepName", step).get(0).getId();
        
        // Collect into concurrent map
        Map<ItemState, Integer> results = TaskTideServiceManager
            .fetchWorkItemService()
            .viewByField("stepId", stepId)
            .parallelStream()
                .map( elm -> elm.summarizeByState() )
                .flatMap( map -> map.entrySet().stream() )
        .collect(Collectors.toConcurrentMap(
            Map.Entry::getKey,
            Map.Entry::getValue,
            Integer::sum
        ));
        
        // Provide as hash map
        output = new StateSummary<>(new HashMap<>(results));
        return output;
    }
    
    
    /**
     * Fetches count of {@link ItemTask} {@link ItemState} per {@link WorkItem}
     * 
     * @return Map-String, {@link StateSummary} of {@link ItemState}
     */
    public Map<String, StateSummary<ItemState>> summarizeEach() {
        
        // Fetch coordinating arguments
        String step = (String) this.cmdSpec.getOptionsKey("Step Name").get();
        String stepId = TaskTideServiceManager.fetchStepService().viewByField("stepName", step).get(0).getId();
        
        // Calculate results
        Map<String, StateSummary<ItemState>> results =
            TaskTideServiceManager
            .fetchWorkItemService()
            .viewByField("stepId", stepId)
            .parallelStream()
        .collect(Collectors.toConcurrentMap(
            elm -> elm.getId(),
            elm -> new StateSummary<ItemState>(elm.summarizeByState())
        ));
        
        // Provide as hash map
        return new HashMap<>(results);
    }
}