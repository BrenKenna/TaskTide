/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.tasktide.core.manager;

import org.tasktide.core.supporting.Utils;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbConfig;
import jakarta.json.bind.annotation.JsonbProperty;

import java.util.HashMap;
import java.util.Map;

import org.tasktide.core.model.builders.ItemTaskBuilder;
import org.tasktide.core.model.task.ItemTask;
import org.tasktide.core.model.task.TaskState;


/**
 * Simple converter class for converting user input into {@link ItemTask ItemTask}.
 * <br><br>
 * Using to get the lay of the land of this in {@linl TaskTideManagerTests TaskTideManagerTests}.
 * 
 * @author bkenna
 */
public class ManagerTask {

    @JsonbProperty("Task Name")
    private String taskName;
    
    @JsonbProperty("Task Script")
    private String taskScript;
    
    private final ItemTaskBuilder itemTaskBuilder;
    private final Utils utils;
    
    /**
     * Null constructor
     */
    public ManagerTask() {
        this.itemTaskBuilder = new ItemTaskBuilder();
        this.utils = new Utils("dd/MM/yy HH:mm:ss", 4);
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
        this.utils = new Utils("dd/MM/yy HH:mm:ss", 4);
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
            .id( "ItemTask-" + utils.generateSalt() )
            .taskName(taskName)
            .task(taskScript)
            .taskState(TaskState.PENDING)
            .build();
    }
    
    
    /**
     * Get task name
     * 
     * @return String
     */
    public String getTaskName() {
        return taskName;
    }

    
    /**
     * Set task name
     * 
     * @param taskName 
     */
    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    
    /**
     * Get task script
     * 
     * @return String
     */
    public String getTaskScript() {
        return taskScript;
    }

    
    /**
     * Set task script
     * 
     * @param taskScript 
     */
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

    
    /**
     * Represent as String
     * 
     * @return String
     */
    @Override
    public String toString() {
        return "ManagerTask{" +
            "taskName=" + taskName +
            ", taskScript=" + taskScript +
            ", itemTaskBuilder=" + itemTaskBuilder + 
        '}';
    }
}
