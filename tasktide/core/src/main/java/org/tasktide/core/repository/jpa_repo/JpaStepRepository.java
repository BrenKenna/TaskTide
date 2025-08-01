/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
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
