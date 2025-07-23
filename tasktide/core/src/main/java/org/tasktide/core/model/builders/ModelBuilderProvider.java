/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.tasktide.core.model.builders;

import java.util.HashMap;
import java.util.Map;

import org.tasktide.core.TaskTideModelType;
import org.tasktide.core.TaskTideModel;


/**
 *
 * Class for providing builders
 * 
 * @author bkenna
 */
public class ModelBuilderProvider {
    
    // Set attributes
    private final Map<BuilderType, ModelBuilder> builderMap;
    

    /**
     * Construct provider with builders
     */
    public ModelBuilderProvider () {
        this.builderMap = new HashMap<>();
        for ( BuilderType i : BuilderType.values() ) {
            ModelBuilder j = i.makeBuilder();
            this.builderMap.put(i, j);
        }
    }
    
    
    /**
     * Get required builder
     * 
     * @param builderType
     * @return {@link ModelBuilder}
     */
    public ModelBuilder getBuilder(BuilderType builderType) {
        return builderMap.get(builderType);
    }
    
    
    /**
     * Get builder for {@link TaskTideModel}
     * 
     * @param modelType
     * @return {@link ModelBuilder}
     */
    public ModelBuilder getBuilder(TaskTideModelType modelType) {
        return getBuilder(mapToModel(modelType));
    }
    
    
    /**
     * Map {@link TaskTideModelType} to {@link BuilderType}
     * 
     * @param modelType
     * @return {@link BuilderType}
     */
    public BuilderType mapToModel(TaskTideModelType modelType) {
        return switch (modelType) {
            case WORKITEM -> BuilderType.WORK_ITEM;
            case STEP -> BuilderType.STEP;
            case WORKFLOW -> BuilderType.WORKFLOW;
            
        };      
    }
}
