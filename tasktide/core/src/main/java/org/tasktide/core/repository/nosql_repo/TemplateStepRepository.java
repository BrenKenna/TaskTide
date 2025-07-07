/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.tasktide.core.repository.nosql_repo;

import org.tasktide.core.repository.TemplateRepository;
// import jakarta.enterprise.context.ApplicationScoped;
// import jakarta.inject.Inject;
// import org.eclipse.microprofile.config.inject.ConfigProperty;

import jakarta.nosql.Template;
import java.util.List;

import org.tasktide.core.model.collection.Step;


/**
 * Step repository
 * 
 * @author bkenna
 */
// @ApplicationScoped
public class TemplateStepRepository extends TemplateRepository<Step> {
    
    /**
     * Construct StepRepository with injectable template and configurable collection name
     * 
     * @param template
     * @param collectionName step.repo-name
     */
    // @Inject
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
