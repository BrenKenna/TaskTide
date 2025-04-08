/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package org.tasktide.core.repository.qualifiers;

import jakarta.nosql.document.DocumentTemplate;


/**
 *
 * Enum to hold valid StorageTypes
 * 
 * @author bkenna
 */
public enum StorageType {
    
    MONGODB {
        @Override
        public DocumentTemplate createTemplate() {
            return null;
        }
    },
    
    DYNAMODB {
        @Override
        public DocumentTemplate createTemplate() {
            return null;
        }
    },
    
    COUCHDB {
        @Override
        public DocumentTemplate createTemplate() {
            return null;
        }
    };
    
    
    /**
     * Create DocumentTemplate based on enum value
     * 
     * @return DocumentTemplate
     */
    public abstract DocumentTemplate createTemplate();
}
