/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
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