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
package org.tasktide.core.model;

import jakarta.json.bind.Jsonb;

import jakarta.json.bind.annotation.JsonbCreator;
import jakarta.json.bind.annotation.JsonbProperty;

import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbConfig;

import java.util.HashMap;
import java.util.Map;


/**
 * Class to hold collection of custom annotations
 *  as a {@link LinkedHashMap}
 * 
 * @author Brendan Kenna
 */
@jakarta.nosql.Embeddable
@jakarta.persistence.Embeddable
public class CustomAnnotation {
    
    // Id for annotation collection
    @jakarta.nosql.Column("Id")
    @jakarta.persistence.Column(name = "AnnotationId")
    @JsonbProperty("Id")
    private String annoId;
    
    // Annotations field
    @jakarta.nosql.Column("Annotations")
    @jakarta.persistence.Column(name = "Annotations")
    @JsonbProperty("Annotations")
    private Map<String, Object> anno;

    
    /**
     * Simplifies Jakarta (de)serailization
     */
    public CustomAnnotation() {
        this.anno = new HashMap<>();
    }
    
    
    /**
     * Construct with attributes
     * 
     * @param annoId
     * @param anno 
     */
    @JsonbCreator
    public CustomAnnotation(
       @JsonbProperty("Id") String annoId,
       @JsonbProperty("Annotations") HashMap<String, Object> anno
    ) {
        this.annoId = annoId;
        this.anno = anno;
    }

    
    /**
     * Add entry if not present, returns false if present
     * 
     * @param key
     * @param value
     * @return boolean
     */
    public boolean add(String key, Object value) {
        return this.anno.putIfAbsent(key, value) == null;
    }
    
    
    /**
     * Get value matching key, otherwise null
     * 
     * @param key
     * @return Object
     */
    public Object getKey(String key) {
        return this.anno.get(key);
    }
    
    
    /**
     * Checks whether map contains key
     * 
     * @param key
     * @return boolean
     */
    public boolean hasKey(String key) {
        return this.getKey(key) != null;
    }
    
    
    /**
     * Get annotation Id
     * 
     * @return String
     */
    public String getAnnoId() {
        return annoId;
    }

    
    /**
     * Set annotation Id
     * 
     * @param annoId 
     */
    public void setAnnoId(String annoId) {
        this.annoId = annoId;
    }

    
    /**
     * Get annotation proeprty
     * 
     * @return 
     */
    public Map<String, Object> getAnno() {
        return anno;
    }

    
    /**
     * Sets annotation property
     * 
     * @param anno 
     */
    public void setAnno(HashMap<String, Object> anno) {
        this.anno = anno;
    }

    
    /**
     * Serialize to JSON string
     * 
     * @return String
     */
    public String toJsonString() {
        Jsonb json = JsonbBuilder.create();
        return json.toJson(this);
    }
    
    
    /**
     * Serialize to a human readable formatted JSON string
     * 
     * @return String
     */
    public String toJsonDoc() {
        JsonbConfig conf = new JsonbConfig().withFormatting(Boolean.TRUE);
        Jsonb json = JsonbBuilder.create(conf);
        return json.toJson(this);
    }
    
    
    /**
     * Represent as String
     * 
     * @return String
     */
    @Override
    public String toString() {
        return "CustomAnnotation{" +
            "annoId=" + annoId +
            ", anno=" + anno +
        '}';
    }
}