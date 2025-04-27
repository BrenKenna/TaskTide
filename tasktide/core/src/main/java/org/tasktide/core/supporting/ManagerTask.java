/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.tasktide.core.supporting;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbConfig;
import jakarta.json.bind.annotation.JsonbProperty;

import java.util.HashMap;
import java.util.Map;

import org.tasktide.core.model.builders.ItemTaskBuilder;
import org.tasktide.core.model.task.ItemTask;


/**
 * Supporting class to support creation of custom tasks
 * 
 * @author bkenna
 */
public class ManagerTask {

    @JsonbProperty("Task Name")
    private String taskName;
    
    @JsonbProperty("Task Script")
    private String taskScript;
    
    private final ItemTaskBuilder itemTaskBuilder;
    
    /**
     * Null constructor
     */
    public ManagerTask() {
        this.itemTaskBuilder = new ItemTaskBuilder();
    }
    
    
    /**
     * Construct with attributes
     * 
     * @param taskName
     * @param taskScript 
     */
    public ManagerTask(
        @JsonbProperty("Task Name") String taskName,
        @JsonbProperty("Task Script") String taskScript
    ) {
        this.taskName = taskName;
        this.taskScript = taskScript;
        this.itemTaskBuilder = new ItemTaskBuilder();
    }
    
    
    /**
     * Generate a command map from {@link ManagerTask ManagerTask}
     * 
     * @return Map-String, String
     */
    public Map<String, String> asMap() {
        Map<String, String> output = new HashMap<>();
        output.put("Task Script", taskScript);
        output.put("Task Name", taskName);
        return output;
    }
        
    
    /**
     * Represent as {@link ItemTask ItemTask}
     * 
     * @return {@link ItemTask ItemTask}
     */
    public ItemTask asItemTask() {
        return itemTaskBuilder
            .taskName(taskName)
            .task(taskScript)
            .build();
    }

    
    public String getTaskName() {
        return taskName;
    }

    
    
    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    
    
    public String getTaskScript() {
        return taskScript;
    }

    
    
    public void setTaskScript(String taskScript) {
        this.taskScript = taskScript;
    }

    
    /**
     * Serialize to JSON string
     * 
     * @return String
     */
    public String toJsonString() {
        Jsonb json = JsonbBuilder.create();
        return json.toJson(this);
    }
    
    
    /**
     * Serialize to a human readable formatted JSON string
     * 
     * @return String
     */
    public String toJsonDoc() {
        JsonbConfig conf = new JsonbConfig().withFormatting(Boolean.TRUE);
        Jsonb json = JsonbBuilder.create(conf);
        return json.toJson(this);
    }
}
