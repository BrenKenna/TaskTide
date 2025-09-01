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
package org.tasktide.core.repository.jpa_repo;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import org.tasktide.core.TaskTideRepository;
import org.tasktide.core.model.collection.Step;
import org.tasktide.core.repository.JpaRepository;


/**
 * Persistence of {@link Step}
 * 
 * @author bkenna
 */
public class JpaStepRepository extends JpaRepository<Step> {
    
    
    /**
     * Constructs {@link Step} {@link TaskTideRepository}
     * 
     * @param backend
     * @param collectionName 
     */
    public JpaStepRepository(EntityManager backend, String collectionName) {
        super(backend, Step.class, collectionName);
    }
    
    
    /**
     * Update state counts for step
     * 
     * @return boolean
     */
    public boolean updateStateCounts() {
        String query = """
            UPDATE Step 
               INNER JOIN (
                  SELECT 
                       StepId,
                       SUM(TaskCount) AS Total,
                       SUM(TaskDone) AS Done,
                       SUM(CASE WHEN ItemState == 'TODO' THEN 1 ELSE 0 END) AS ToDo,
                       SUM(CASE WHEN ItemState == 'LOCKED' THEN 1 ELSE 0 END) AS Locked,
                       SUM(CASE WHEN ItemState == 'ERROR' THEN 1 ELSE 0 END) AS Error
                  FROM WorkItem
                  GROUP By StepId
               ) AS Work 
               ON
                  Step.stepId = Work.StepId
               Set 
                  StepCount = Total,
                  StepsDone = Done,
                  StepsError = Error,
                  StepsLocked = Locked
            ;
        """;
        
        EntityTransaction tx = entityManager.getTransaction();
        try {
            tx.begin();
            entityManager
                .createNativeQuery(query)
            .executeUpdate();
            tx.commit();
            return true;
        }
        catch( Exception ex ) {
            tx.rollback();
            return false;
        }
    }
}
