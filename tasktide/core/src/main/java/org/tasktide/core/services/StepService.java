/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.tasktide.core.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import jakarta.nosql.document.DocumentTemplate;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.tasktide.core.model.collection.Step;


/**
 *
 * Service to provide Step interactions to backend DB
 * 
 * @author bkenna
 */
@ApplicationScoped
public class StepService {
    
    // Attributes
    @Inject
    private DocumentTemplate template;
    private final String COLLECTION = "Step";
    private final Class COLLECTION_CLASS = Step.class;
    private static final Logger logger = LogManager.getLogger(StepService.class);
}
