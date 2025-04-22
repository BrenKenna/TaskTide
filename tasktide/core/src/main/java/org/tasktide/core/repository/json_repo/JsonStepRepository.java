/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.tasktide.core.repository.json_repo;

import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.util.List;

import org.tasktide.core.model.collection.Step;


/**
 *
 * JSON File I/O repository for Step
 * 
 * @author bkenna
 */
public class JsonStepRepository extends JsonRepository<Step> {


    /**
     * Construct StepRepository with injectable template and configurable collection name
     * 
     * @param modelCollection
     * @param collectionName task-tide.repository.json.collection.step.name
     */
    @Inject
    public JsonStepRepository(
        List<Step> modelCollection,
        @ConfigProperty(name = "task-tide.repository.json.collection.step.name", defaultValue = "Step-Data") String collectionName
    ) {
        super(modelCollection, Step.class, collectionName);
    }
}
