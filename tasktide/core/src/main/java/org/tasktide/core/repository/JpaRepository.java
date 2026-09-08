/*
 * Copyright 2025 Brendan Kenna.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.tasktide.core.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.tasktide.core.TaskTideModel;
import org.tasktide.core.TaskTideRepository;


/**
 * JPA Entity Manager repository allowing subclasses to be persisted
 *  to Relational backends (ex Postgres, MySQL etc) 
 * 
 * @author bkenna
 * @param <T> of {@link TaskTideRepository} for {@link TaskTideModel}
 */
public abstract class JpaRepository<T extends TaskTideModel<T>> extends AbstractRepository<T> {

    // Attributes
    private final Logger LOGGER = LogManager.getLogger(JpaRepository.class);
    protected final EntityManager entityManager;

    
    /**
     * Construct with target model class, and collection
     * 
     * @param entityManager
     * @param clazz
     * @param collectionName
     */
    public JpaRepository(EntityManager entityManager, Class<T> clazz, String collectionName) {
        super(clazz, collectionName, RepositoryType.SQL);
        this.entityManager = entityManager;
    }
    
    
    /**
     * Method to support encasing methods in transaction
     * 
     * @param <R>
     * @param operation
     * @return R
     */
    private <R> R transaction(Supplier<R> operation) {
        EntityTransaction tx = this.entityManager.getTransaction();
        
        // Try execute operation
        try {
            tx.begin();
            R result = operation.get();
            tx.commit();
            return result;
        }
        
        catch ( RuntimeException ex ) {
            LOGGER.error("Error during database operation", ex);
            if ( tx.isActive() ) {
                tx.rollback();
            }
            throw ex;
        }
    }
    
    
    /**
     * Find the {@link TaskTideModel} having id
     * 
     * @param id
     * @return Optional-T
     */
    @Override
    public Optional<T> findById(String id) {
        T result = entityManager.find(COLLECTION_CLASS, id);
        return Optional.ofNullable(result);
    }

    
    /**
     * Insert record
     * 
     * @param model
     * @return T
     */
    @Override
    public T insertModel(T model) {
        return this.transaction( () -> {
            this.entityManager.persist(model);
            this.entityManager.flush();
            return entityManager.find(COLLECTION_CLASS, model.getId());
        });
    }

    
    /**
     * Update provided model
     * 
     * @param model
     * @return T
     */
    @Override
    public T updateModel(T model) {
        return this.transaction( () -> {
            T result = this.entityManager.merge(model);
            return result;
        });
    }

    
    /**
     * Remove model with provided id
     * 
     * @param id
     * @return boolean
     */
    @Override
    public boolean deleteModel(String id) {
        return this.transaction( () -> {
            Optional<T> forDeletion = this.findById(id);
            forDeletion.ifPresent(
                elm -> this.entityManager.remove(elm)
            );
            entityManager.flush();
            
            Optional<T> result = this.findById(id);
            return result.isEmpty();
        });
    }

    
    /**
     * Find records where provided field '==' value
     * 
     * @param field
     * @param value
     * @return List-T
     */
    @Override
    public List<T> findByField(String field, Object value) {
        
        // Configure query string
        String query = String.format(
            "SELECT e FROM %s e WHERE e.%s = :value",
            COLLECTION_CLASS.getSimpleName(), field
        );
        
        // Parameterize and reduce to result set size
        if ( this.resultSetSize >= 1 ) {
            return this.entityManager
                .createQuery(query, COLLECTION_CLASS)
                .setParameter("value", value)
                .setMaxResults(this.resultSetSize)
            .getResultList();
        }
        
        // Otherwise all
        else {
            return this.entityManager
                .createQuery(query, COLLECTION_CLASS)
                .setParameter("value", value)
            .getResultList();
        }
    }

    
    /**
     * Find {@link TaskTideModel} from backend with field and group
     *  having specified value. Step = Name, State = ToDo
     * 
     * @param field
     * @param value
     * @param group
     * @param groupVal
     * @return List-{@link TaskTideModel} 
     */
    @Override
    public List<T> findByFieldForGroup(String field, Object value, String group, Object groupVal) {
        String query = String.format(
            "SELECT e FROM %s e WHERE e.%s = :value AND e.%s = :groupVal",
            COLLECTION_CLASS.getSimpleName(), field, group
        );
        
        // Reduce to result set size
        if ( this.resultSetSize >= 1 ) {
            return this.entityManager
                .createQuery(query, COLLECTION_CLASS)
                .setParameter("value", value)
                .setParameter("groupVal", groupVal)
                .setMaxResults(this.resultSetSize)
            .getResultList();
        }
        
        // Otherwise all
        else {
            return this.entityManager
                .createQuery(query, COLLECTION_CLASS)
                .setParameter("value", value)
                .setParameter("groupVal", groupVal)
            .getResultList();
        }
    }
    
    
    /**
     * Fetch all records
     * 
     * @return List-T 
     */
    @Override
    public List<T> findAll() {
        
        // Reduce to result set size
        if ( this.resultSetSize >= 1 ) {
            return this.entityManager
                .createQuery(
                    String.format("SELECT e FROM %s e", COLLECTION_CLASS.getSimpleName()),
                        COLLECTION_CLASS
                )
                .setMaxResults(this.resultSetSize)
            .getResultList();
        }
        
        // Otherwise all
        else {
            return this.entityManager
                .createQuery(
                    String.format("SELECT e FROM %s e", COLLECTION_CLASS.getSimpleName()),
                        COLLECTION_CLASS
                )
            .getResultList();
        }
    }

    
    /**
     * Persist all records
     * 
     * @return int
     */
    @Override
    public int save() {
        this.entityManager.flush();
        return (int) countRecords();
    }

    
    /**
     * Count records
     * 
     * @return long
     */
    public long countRecords() {
        String query = String.format(
            "SELECT COUNT(DISTINCT e.id) FROM %s e",
            COLLECTION_CLASS.getSimpleName()
        );
        return entityManager
            .createQuery(query, Long.class)
        .getSingleResult();
    }
    
    
    /**
     * Fetch all records
     * 
     * @return List-T
     */
    @Override
    public List<T> load() {
        return this.findAll();
    }

    
    /**
     * Insert provided records
     * 
     * @param toAdd
     * @return boolean
     */
    @Override
    public boolean extendModel(List<T> toAdd) {
        return this.transaction( () -> {
            
            // Add all records
            int count = 0, batchSize = 50; // Flush every 50
            for ( T elm : toAdd ) {
                entityManager.persist(elm);

                // Flush batch if limit is hit
                if ( count > 0 && count % batchSize == 0 ) {
                    entityManager.flush();
                    entityManager.clear();
                }
                count++;
            }

            // Ensure changes are committed
            entityManager.flush();
            entityManager.clear();

            // Return results
            return count == toAdd.size();
        });
    }
}