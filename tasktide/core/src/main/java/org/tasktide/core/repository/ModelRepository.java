/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.tasktide.core.repository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import jakarta.nosql.document.DocumentTemplate;

import org.tasktide.core.supporting.Utils;


/**
 *
 * @author bkenna
 */
@ApplicationScoped
public abstract class ModelRepository<T> implements RepositoryInterface<T> {
    
    @Inject
    //protected final DocumentTemplate template;
    protected final Class<T> modelClass;
    protected final String collectionName;
    
    protected final Utils utils = new Utils();
    protected final int LOCKING_WAIT_TIME = 4;
    
    public ModelRepository(Class<T> modelClass, String collectionName) {
        this.modelClass = modelClass;
        this.collectionName = collectionName;
    }
}
