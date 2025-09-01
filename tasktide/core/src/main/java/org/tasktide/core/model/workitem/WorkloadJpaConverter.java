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
package org.tasktide.core.model.workitem;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import org.tasktide.core.model.task.ItemTask;
import org.tasktide.core.supporting.JsonUtils;


/**
 * Converter class for JSON-Serde of Workloads 
 * 
 * @author bkenna
 */
@Converter(autoApply=false)
public class WorkloadJpaConverter implements AttributeConverter<Map<String, ItemTask>, String> {

    
    /**
     * Serialize {@link Workload} to JSON
     * 
     * @param attribute
     * @return String
     */
    @Override
    public String convertToDatabaseColumn(Map<String, ItemTask> attribute) {
        return JsonUtils.toJson(true, attribute);
    }

    
    /**
     * Deserialize from JSON string
     * 
     * @param dbData
     * @return Map-String, {@link ItemTask}
     */
    @Override
    public Map<String, ItemTask> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isBlank()) {
            return new HashMap<>();
        }
        Type mapType = new HashMap<String, ItemTask>() {}.getClass().getGenericSuperclass();
        return JsonUtils.fromJson(dbData, mapType);
    }
}