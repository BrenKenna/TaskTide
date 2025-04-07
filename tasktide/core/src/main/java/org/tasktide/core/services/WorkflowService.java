/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.tasktide.core.services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import jakarta.nosql.document.DocumentTemplate;

import org.tasktide.core.model.collection.Workflow;


/**
 *
 * Service to provide WorkflowService interactions to backend DB
 * 
 * @author bkenna
 */
@ApplicationScoped
public class WorkflowService {
    
    // Attributes
    @Inject
    private DocumentTemplate template;
    private final String COLLECTION = "Workflow";
    private final Class COLLECTION_CLASS = Workflow.class;
    private static final Logger logger = LogManager.getLogger(WorkflowService.class);
}
