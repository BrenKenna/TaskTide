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
package org.tasktide.engine.policies.resources;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbConfig;
import jakarta.json.bind.annotation.JsonbProperty;
import org.tasktide.core.TaskTideModel;

import org.tasktide.core.model.CustomAnnotation;
import org.tasktide.core.model.workitem.ItemState;


/**
 * Data object for representing {@link AbstractAcquisitionPolicy}
 *
 * @param <T> of {@link TaskTideModel}
 * @author Bren
 */
public class AcquisitionPolicyJsonResource<T extends TaskTideModel<T>> {
    
    // JSON String handlers
    private final Jsonb JSON_BUILDER = JsonbBuilder.create();
    private final Jsonb JSON_BUILDER_PRETTY = JsonbBuilder.create(new JsonbConfig().withFormatting(Boolean.TRUE));
    
    // Attributes
    @JsonbProperty("Id")
    protected String id;

    @JsonbProperty("Policy Type")
    protected String type;

    @JsonbProperty("Policy Target")
    protected String target;

    @JsonbProperty("Item State")
    protected ItemState state;

    @JsonbProperty("Annotation Key")
    protected String annoKey;

    @JsonbProperty("Annotation Value")
    protected Object annoVal;

    @JsonbProperty("Custom Annotation")
    protected CustomAnnotation anno;
    
    @JsonbProperty("Target Set")
    protected boolean targetted;
    
    @JsonbProperty("Annotation String Set")
    protected boolean annoString;
   
    @JsonbProperty("Custom Annotation Set")
    protected boolean annotation;

    public AcquisitionPolicyJsonResource() {}

    public AcquisitionPolicyJsonResource(
        @JsonbProperty("Id") String id,
        @JsonbProperty("Policy Type") String type,
        @JsonbProperty("Policy Target") String target,
        @JsonbProperty("Item State") ItemState state,
        @JsonbProperty("Annotation Key") String annoKey,
        @JsonbProperty("Annotation Value") Object annoVal,
        @JsonbProperty("CustomAnnotation") CustomAnnotation anno,
        @JsonbProperty("Target Set") boolean targetted,
        @JsonbProperty("Annotation String") boolean annoString,
        @JsonbProperty("Custom Annotation Set") boolean annotation
    ) {
        this.id = id;
        this.type = type;
        this.target = target;
        this.state = state;
        this.annoKey = annoKey;
        this.annoVal = annoVal;
        this.anno = anno;
        this.targetted = targetted;
        this.annoString = annoString;
        this.annotation = annotation;
    }

    
    /**
     * Represent as {@link Jsonb} string
     * 
     * @return String
     */
    public String toJson() {
        return this.JSON_BUILDER.toJson(this);
    }
    
    
    /**
     * Represent as {@link Jsonb} document
     * 
     * @return String
     */
    public String toJsonDoc() {
        return this.JSON_BUILDER_PRETTY.toJson(this);
    }
    
    
    /**
     * Represent policy as string
     * 
     * @return String
     */
    @Override
    public String toString() {
        return "AcquisitionPolicyJsonResource{" +
            "id=" + id +
            ", type=" + type +
            ", target=" + target +
            ", state=" + state +
            ", annoKey=" + annoKey +
            ", annoVal=" + annoVal +
            ", anno=" + anno +
            ", targetted=" + targetted +
            ", annoString=" + annoString +
            ", annotation=" + annotation +
        '}';
    }


    /**
     * 
     * Getters & Setters
     * 
     */
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public ItemState getState() {
        return state;
    }

    public void setState(ItemState state) {
        this.state = state;
    }

    public String getAnnoKey() {
        return annoKey;
    }

    public void setAnnoKey(String annoKey) {
        this.annoKey = annoKey;
    }

    public Object getAnnoVal() {
        return annoVal;
    }

    public void setAnnoVal(Object annoVal) {
        this.annoVal = annoVal;
    }

    public CustomAnnotation getAnno() {
        return anno;
    }

    public void setAnno(CustomAnnotation anno) {
        this.anno = anno;
    }

    public boolean isTargetted() {
        return targetted;
    }

    public void setTargetted(boolean targetted) {
        this.targetted = targetted;
    }

    public boolean isAnnoString() {
        return annoString;
    }

    public void setAnnoString(boolean annoString) {
        this.annoString = annoString;
    }

    public boolean isAnnotation() {
        return annotation;
    }

    public void setAnnotation(boolean annotation) {
        this.annotation = annotation;
    }
}