/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.tasktide.core.repository.nosql_repo;

import java.util.List;

import jakarta.nosql.Template;

import org.tasktide.core.model.collection.Step;
import org.tasktide.core.repository.TemplateRepository;


/**
 * Step repository
 * 
 * @author bkenna
 */
public class TemplateStepRepository extends TemplateRepository<Step> {
    
    /**
     * Construct StepRepository with injectable template and configurable collection name
     * 
     * @param template
     * @param collectionName step.repo-name
     */
    public TemplateStepRepository(Template template,
       String collectionName
    ) {
        super(template, Step.class, collectionName);
    }

    @Override
    public boolean extendModel(List<Step> toAdd) {
        long importCount = toAdd.stream()
            .filter( elm -> insertModel(elm) != null)
        .count();
        return importCount == toAdd.size();
    }
}
