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

import java.util.Map;

import jakarta.json.bind.annotation.JsonbCreator;
import jakarta.json.bind.annotation.JsonbProperty;


/**
 * Data model class for representing task summary counts
 *  across valid {@link TaskState} instance
 *
 * @author Bren
 */
public final class TaskStateSummary extends StateSummary<TaskState> {

    
    /**
     * Construct empty {@link TaskState} summary map
     * 
     */
    public TaskStateSummary() {
        super();
    }
    
    
    /**
     * Construct {@link TaskState} summary map
     * 
     * @param map 
     */
    @JsonbCreator
    public TaskStateSummary(
        @JsonbProperty("State Summary") Map<String, Integer> map
    ) {
        super(map);
    }
    
    
    /**
     * Maps the queried string to an {@link TaskState}
     * 
     * @param query
     * @return {@link TaskState}
     */
    @Override
    public TaskState mapQueryToState(String query) {
        return TaskState.get(query);
    }
    
    
    /**
     * Represent as JSON document
     * 
     * @return String
     */
    @Override
    public String toString() {
        return this.toJson(true);
    }
}