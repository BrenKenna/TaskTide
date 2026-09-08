/*
 * Copyright 2026 Bren.
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
package org.tasktide.api.security_context.data_models;

import java.lang.reflect.Field;

import jakarta.json.bind.annotation.JsonbCreator;
import jakarta.json.bind.annotation.JsonbProperty;

import org.tasktide.core.TaskTideModel;
import org.tasktide.core.supporting.JsonUtils;
import org.tasktide.core.model.CustomAnnotation;


/**
 * Data model to provide access-control to {@link TaskTideModel}
 *  instances
 *
 * @param <T> of {@link TaskTideModel}
 * @author Bren
 */
@jakarta.persistence.MappedSuperclass
public abstract class AbstractResourcePermission<T extends TaskTideModel<T>> implements TaskTideModel<T> {

    // Field attributes
    @jakarta.nosql.Column("ResourceId")
    @jakarta.persistence.Column(name = "ResourceId")
    @JsonbProperty("Resource Id")
    protected String resourceId;
    
    
    @jakarta.nosql.Column("ResourceType")
    @jakarta.persistence.Column(name = "ResourceType")
    @JsonbProperty("Resource Type")
    protected String resourceType;
    
    
    @jakarta.nosql.Column("SubjectId")
    @jakarta.persistence.Column(name = "SubjectId")
    @JsonbProperty("Subject Id")
    protected String subjectId;
    
    
    @jakarta.nosql.Column("Permission")
    @jakarta.persistence.Column(name = "Permission")
    @JsonbProperty("Permission")
    protected String permission;
    
    
    @jakarta.nosql.Column("Annotations")
    @jakarta.persistence.Column(name = "Annotations")
    @JsonbProperty("Annotations")
    protected CustomAnnotation anno;
    
    
    /**
     * Default constructor
     * 
     */
    public AbstractResourcePermission() {}
    
    
    /**
     * Construct with attributes
     * 
     * @param id
     * @param type
     * @param subjectId
     * @param permission 
     * @param anno
     */
    @JsonbCreator
    public AbstractResourcePermission(
        @JsonbProperty("Resource Id") String id,
        @JsonbProperty("Resource Type") String type,
        @JsonbProperty("Subject Id") String subjectId,
        @JsonbProperty("Permission") String permission,
        @JsonbProperty("Annotations") CustomAnnotation anno
    ) {
        this.resourceId = id;
        this.resourceType = type;
        this.subjectId = subjectId;
        this.permission = permission;
    }

    
    /**
     * Get Id for resource
     * 
     * @return String
     */
    @Override
    public String getId() {
        return this.resourceId;
    }
    
    
    /**
     * Set resource Id
     * 
     * @param resourceId 
     */
    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    
    /**
     * Get subject Id
     * 
     * @return String
     */
    public String getSubjectId() {
        return subjectId;
    }

    
    /**
     * Set subjectId
     * 
     * @param subjectId 
     */
    public void setSubjectId(String subjectId) {
        this.subjectId = subjectId;
    }

    
    /**
     * Get permission field
     * 
     * @return String
     */
    public String getPermission() {
        return permission;
    }

    
    /**
     * Set permission field
     * 
     * @param permission 
     */
    public void setPermission(String permission) {
        this.permission = permission;
    }
    
    
    /**
     * Get permission/state of resource
     * 
     * @return String
     */
    @Override
    public String getState() {
        return this.getPermission();
    }

    
    /**
     * Get resource type
     * 
     * @return String
     */
    public String getResourceType() {
        return resourceType;
    }

    
    /**
     * Set resource type
     * 
     * @param resourceType
     */
    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }
    
    
    /**
     * Get resource type
     * 
     * @return String
     */
    @Override
    public String getCollection() {
        return this.getResourceType();
    }

    
    /**
     * Get annotations
     * 
     * @return {@link CustomAnnotation}
     */
    @Override
    public CustomAnnotation getAnnotations() {
        return this.anno;
    }

    
    /**
     * Set new {@link CustomAnnotation} field
     * 
     * @param anno 
     */
    @Override
    public void setAnnotations(CustomAnnotation anno) {
        this.anno = anno;
    }
    
    
    /**
     * Get field from instance
     * 
     * @param field
     * 
     * @return field value
     */
    @Override
    public Object getValueFromField(String field) {
        try {
            // Use reflection to get the declared field from this class
            Field declaredField = this.getClass().getDeclaredField(field);
            declaredField.setAccessible(true); // In case the field is private
            Object fieldValue = declaredField.get(this);

            return fieldValue;

        }
        catch (Exception ex) {
            // Optional: Log or rethrow if needed
            return null;
        }
    }

    
    /**
     * Represent as String
     * 
     * @return String
     */
    @Override
    public String toString() {
        return "ResourcePermission{" + 
            "resourceId=" + resourceId +
            ", resourceType=" + resourceType +
            ", subjectId=" + subjectId +
            ", permission=" + permission +
            ", anno=" + anno +
        '}';
    }
    
    
    /**
     * Represent as JSON string
     * 
     * @return String
     */
    @Override
    public String toJson() {
        return JsonUtils.getJsonb(false).toJson(this);
    }

    
    /**
     * Represent as JSON document
     * 
     * @return String
     */
    @Override
    public String toJsonDoc() {
        return JsonUtils.getJsonb(true).toJson(this);
    }
    
    
    @Override
    public void resetModel() {
    }
}