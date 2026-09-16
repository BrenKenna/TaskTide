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
package org.tasktide.core.model.state_summary;

import java.util.HashMap;
import java.util.Map;


/**
 * For static initialization of valid {@link StateSummary}
 *
 * @author Bren
 */
public class StateSummaryFactory {

    
    /**
     * Make a blank {@link StateSummary} for across {@link ItemState}
     *  instances
     * 
     * @return {@link StateSummary} of {@link ItemState}
     */
    public static StateSummary<ItemState> makeItemStateSummary() {
        
        // Initialize output and data map
        ItemStateSummary output;
        Map<String, Integer> dataMap;
        dataMap = new HashMap<>();
        
        // Pull references from state type
        for ( ItemState elm : ItemState.values() ) {
            dataMap.put(elm.name(), 0);
        }
        
        // Return results
        output = new ItemStateSummary(dataMap);
        return output;
    }
    
    
    /**
     * Make a blank {@link StateSummary} for across {@link TaskState}
     *  instances
     * 
     * @return {@link StateSummary} of {@link TaskState}
     */
    public static StateSummary<TaskState> makeTaskStateSummary() {
        
        // Initialize output and data map
        TaskStateSummary output;
        Map<String, Integer> dataMap;
        dataMap = new HashMap<>();
        
        // Pull references from state type
        for ( TaskState elm : TaskState.values() ) {
            dataMap.put(elm.name(), 0);
        }
        
        // Return results
        output = new TaskStateSummary(dataMap);
        return output;
    }
}