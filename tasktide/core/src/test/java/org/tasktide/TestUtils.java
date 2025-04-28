/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.tasktide;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbConfig;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.tasktide.core.TaskTideModel;


/**
 *
 * Various static methods to support development & use of TaskTide
 * 
 * @author bkenna
 */
public class TestUtils {
    
    
    /**
     * Represent map as json string
     * 
     * @param map
     * @return String Json
     */
    public static String mapToJsonString(Map map) {
        Jsonb jsonb = JsonbBuilder.create(new JsonbConfig().withFormatting(true));
        return jsonb.toJson(map);
    }

    
    /**
     * Represent list as json string
     * 
     * @param list
     * @return String Json
     */
    public static String mapToJsonString(List list) {
        Jsonb jsonb = JsonbBuilder.create(new JsonbConfig().withFormatting(true));
        return jsonb.toJson(list);
    }
    
    
    /**
     * Represent {@link TaskTideModel TaskTideModel} list as json doc
     * 
     * @param models
     * @return String
     */
    public static String modelToJsonString(List<? extends TaskTideModel<?>> models) {
        return models.stream()
                .map(TaskTideModel::toJson)
                .collect(Collectors.joining(",\n", "{\n", "\n]"));
    }
}
