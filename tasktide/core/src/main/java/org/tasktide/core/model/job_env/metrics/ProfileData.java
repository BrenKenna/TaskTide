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
package org.tasktide.core.model.job_env.metrics;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbConfig;

import jakarta.json.bind.annotation.JsonbCreator;
import jakarta.json.bind.annotation.JsonbProperty;

import java.lang.reflect.Field;

import java.util.HashMap;
import java.util.Map;

import org.tasktide.core.TaskTideModel;


/**
 * Holds a collection of {@link MetricData} observed over a time-period,
 *  for embedding into {@link MetricProfile}
 *
 * @author Brendan Kenna
 */
@jakarta.nosql.Embeddable
@jakarta.persistence.Embeddable
public class ProfileData implements TaskTideModel<ProfileData> {
    
    @JsonbProperty("Id")
    @jakarta.nosql.Column("Id")
    @jakarta.persistence.Column(name = "Id")
    private String id;
    
    @JsonbProperty("Metric Profile")
    @jakarta.nosql.Column("MetricProfile")
    @jakarta.persistence.Column(name = "MetricProfile")
    private Map<String, MetricData> metricProfile;
    
    @JsonbProperty("Metric Type")
    @jakarta.nosql.Column("MetricType")
    @jakarta.persistence.Column(name = "MetricType")
    private MetricType type;
    
    
    /**
     * Null constructor
     */
    public ProfileData() {
        this.metricProfile = new HashMap<>();
    }
    
    
    /**
     * Construct with all args JSON-B SERDE
     * 
     * @param id
     * @param metricProfile
     * @param type 
     */
    @JsonbCreator
    public ProfileData(
        @JsonbProperty("Id") String id,
        @JsonbProperty("Metric Profile") Map<String, MetricData> metricProfile,
        @JsonbProperty("Metric Type") MetricType type
    ) {
        this.id = id;
        this.metricProfile = metricProfile;
        this.type = type;
    }

    
    /**
     * Represent as JSON-B string
     * 
     * @return String
     */
    @Override
    public String toJson() {
        Jsonb json = JsonbBuilder.create();
        return json.toJson(this);
    }
    
    
    /**
     * Represent as JSON-B Document
     * 
     * @return String
     */
    @Override
    public String toJsonDoc() {
        JsonbConfig conf = new JsonbConfig().withFormatting(Boolean.TRUE);
        Jsonb json = JsonbBuilder.create(conf);
        return json.toJson(this);
    }

    
    /**
     * Returns {@link MetricType}
     * 
     * @return String
     */
    @Override
    public String getState() {
        return this.type.name();
    }
    
    
    /**
     * Return {@link MetricType}
     * 
     * @return String
     */
    @Override
    public String getCollection() {
        return this.type.name();
    }

    
    /**
     * Reset model to empty
     * 
     */
    @Override
    public void resetModel() {
        this.metricProfile = new HashMap<>();
    }

    
    /**
     * Get profile id
     * 
     * @return String
     */
    @Override
    public String getId() {
        return id;
    }

    /**
     * Set profile Id
     * 
     * @param id 
     */
    public void setId(String id) {
        this.id = id;
    }

    
    /**
     * Get metric profile
     * 
     * @return Map-String, {@link MetricData}
     */
    public Map<String, MetricData> getMetricProfile() {
        return metricProfile;
    }

    
    /**
     * Set metric profile
     * 
     * @param metricProfile 
     */
    public void setMetricProfile(Map<String, MetricData> metricProfile) {
        this.metricProfile = metricProfile;
    }

    
    /**
     * Get {@link MetricType}
     * 
     * @return {@link MetricType}
     */
    public MetricType getType() {
        return type;
    }

    
    /**
     * Sets {@link MetricType}
     * 
     * @param type 
     */
    public void setType(MetricType type) {
        this.type = type;
    }

    
    /**
     * Represent as String
     * 
     * @return String
     */
    @Override
    public String toString() {
        return "MetricProfile{" +
            "id=" + id +
            ", metricProfile=" + metricProfile +
            ", type=" + type +
        '}';
    }

    
    /**
     * Get value of queried field
     * 
     * @param field
     * @return Object
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
}