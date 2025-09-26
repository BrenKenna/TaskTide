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

import jakarta.persistence.CascadeType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;

import java.util.ArrayList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Holds a collection of {@link MetricData} observed over a time-period,
 *  for embedding into {@link MetricProfile}
 *
 * @author Brendan Kenna
 */
@jakarta.nosql.Embeddable
@jakarta.persistence.Embeddable
public class ProfileData {
    
    @JsonbProperty("Id")
    @jakarta.nosql.Column("Id")
    @jakarta.persistence.Column(name = "ProfileId")
    private String id;
    
    @JsonbProperty("Metric Profile")
    @jakarta.nosql.Column("MetricProfile")
    @jakarta.persistence.Transient
    private Map<String, MetricData> metricProfile;
    
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "ProfileDataId")
    private final List<MetricData> metricList = new ArrayList<>();
    
    
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
     */
    @JsonbCreator
    public ProfileData(
        @JsonbProperty("Id") String id,
        @JsonbProperty("Metric Profile") Map<String, MetricData> metricProfile
    ) {
        this.id = id;
        this.metricProfile = metricProfile;
    }
    

    /**
     * JPA PostLoad method for populating {@link MetricData}
     * 
     */
    @jakarta.persistence.PostLoad
    public void populateStateMap() {
        metricProfile = new HashMap<>();
        for ( MetricData elm : metricList ) {
            metricProfile.put(elm.getId(), elm);
        }
    }

        
    /**
     * Represent as JSON-B string
     * 
     * @return String
     */
    public String toJson() {
        Jsonb json = JsonbBuilder.create();
        return json.toJson(this);
    }
    
    
    /**
     * Represent as JSON-B Document
     * 
     * @return String
     */
    public String toJsonDoc() {
        JsonbConfig conf = new JsonbConfig().withFormatting(Boolean.TRUE);
        Jsonb json = JsonbBuilder.create(conf);
        return json.toJson(this);
    }

    
    /**
     * Get profile id
     * 
     * @return String
     */
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
     * Represent as String
     * 
     * @return String
     */
    @Override
    public String toString() {
        return "MetricProfile{" +
            "id=" + id +
            ", metricProfile=" + metricProfile +
        '}';
    }
}