/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.tasktide.core.repository.nosql_repo;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import jakarta.nosql.Template;

import org.tasktide.core.model.collection.Step;


/**
 * Step repository
 * 
 * @author bkenna
 */
@ApplicationScoped
public class StepRepository extends TemplateRepository<Step> {
    
    /**
     * Construct StepRepository with injectable template and configurable collection name
     * 
     * @param template
     * @param collectionName step.repo-name
     */
    @Inject
    public StepRepository(Template template,
       @ConfigProperty(name = "step.repo-name", defaultValue = "Step") String collectionName
    ) {
        super(template, Step.class, collectionName);
    }
}
