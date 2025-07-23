/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.tasktide.core.repository.json_repo;

import java.util.List;

import org.tasktide.core.model.collection.Step;
import org.tasktide.core.repository.JsonRepository;


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
    public JsonStepRepository(
        List<Step> modelCollection,
        String collectionName
    ) {
        super(modelCollection, Step.class, collectionName);
    }
}
