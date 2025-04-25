/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.tasktide.core.supporting.generator;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbConfig;
import jakarta.json.bind.annotation.JsonbProperty;

import java.util.HashMap;
import java.util.Map;


/**
 * Supporting class to support creation of custom tasks
 * 
 * @author bkenna
 */
public class Task implements TaskTideTask {

    @JsonbProperty("Task Name")
    private String taskName;
    
    @JsonbProperty("Task")
    private String task;
    
    @JsonbProperty("Task Args")
    private String args;
    
    
    /**
     * Null constructor
     */
    public Task() {}
    
    
    /**
     * Construct with attributes
     * 
     * @param taskName
     * @param task
     * @param args 
     */
    public Task(
        @JsonbProperty("Task Name") String taskName,
        @JsonbProperty("Task") String task,
        @JsonbProperty("Task Args") String args
    ) {
        this.taskName = taskName;
        this.task = task;
        this.args = args;
    }
    
    
    /**
     * Generate a command map from {@link TaskTideTask TaskTideTask}
     * 
     * @return Map-String, String
     */
    @Override
    public Map<String, String> generateCmd() {
        Map<String, String> output = new HashMap<>();
        output.put("Task", task + " " + args);
        output.put("Task Name", taskName);
        return output;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public String getArgs() {
        return args;
    }

    public void setArgs(String args) {
        this.args = args;
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
