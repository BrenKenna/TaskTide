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

import org.tasktide.core.TaskTideModel;
import org.tasktide.core.model.CustomAnnotation;

// For JavaDocs
import org.tasktide.core.model.job_env.JobEnvironment;

/**
 * Holds {@link ProfileData} and computes mean across its
 *  observed {@link MetricData} datapoints as its own Entity.
 * 
 * @author Brendan Kenna
 */
@jakarta.nosql.Entity("MetricProfile")
@jakarta.persistence.Entity
@jakarta.persistence.Table(name = "MetricProfile")
public class MetricProfile implements TaskTideModel<MetricProfile> {
    
    // Profile Identifier
    @jakarta.nosql.Id
    @jakarta.persistence.Id
    @JsonbProperty("Id")
    private String id;
    
    // Label for profile
    @JsonbProperty("Label")
    @jakarta.nosql.Column("Label")
    @jakarta.persistence.Column(name = "Label")
    private String label;
    
    // Timestamp for profile
    @jakarta.nosql.Column("Timestamp")
    @jakarta.persistence.Column(name = "Timestamp")
    @JsonbProperty("Timestamp")
    private long timestamp;
    
    // Mean total
    @JsonbProperty("Mean Total")
    @jakarta.nosql.Column("MeanTotal")
    @jakarta.persistence.Column(name = "MeanTotal")
    private float meanTotal;
    
    // Mean available
    @JsonbProperty("Mean Available")
    @jakarta.nosql.Column("MeanAvailable")
    @jakarta.persistence.Column(name = "MeanAvailable")
    private float meanAvailable;
    
    // Mean used
    @JsonbProperty("Mean Used")
    @jakarta.nosql.Column("MeanUsed")
    @jakarta.persistence.Column(name = "MeanUsed")
    private float meanUsed;
    
    // Dataset supporting mean metrics
    @JsonbProperty("Profile Data")
    @jakarta.nosql.Column("ProfileData")
    @jakarta.persistence.Embedded
    private ProfileData profile;
    
    // Units for metric
    @jakarta.nosql.Column("Units")
    @jakarta.persistence.Column(name = "Units")
    @JsonbProperty("Units")
    private String units;
    
    // Type of metric ie Graphics etc
    @JsonbProperty("Metric Type")
    @jakarta.nosql.Column("MetricType")
    @jakarta.persistence.Column(name = "MetricType")
    private MetricType type;
    
    // JobEnvironment from which metric originates
    @JsonbProperty("Job Environment Id")
    @jakarta.nosql.Column("JobEnvironmentId")
    @jakarta.persistence.Column(name = "JobEnvironmentId")
    private String jobEnvId;

    // Custom annotations
    @jakarta.nosql.Column("Annotations")
    @jakarta.persistence.Column(name = "Annotations")
    @JsonbProperty("Annotations")
    private CustomAnnotation anno;
    
    
    /**
     * Empty constructor
     */
    public MetricProfile() {
        this.profile = new ProfileData();
    }
    
    
    /**
     * Construct with data
     * 
     * @param id
     * @param label
     * @param timestamp
     * @param units
     * @param meanTotal
     * @param meanAvailable
     * @param meanUsed
     * @param profile
     * @param type
     * @param jobEnvId
     * @param anno
     */
    @JsonbCreator
    public MetricProfile(
        @JsonbProperty("Id") String id,
        @JsonbProperty("Label") String label,
        @JsonbProperty("Timestamp") long timestamp,
        @JsonbProperty("Units") String units,
        @JsonbProperty("Mean Total") float meanTotal,
        @JsonbProperty("Mean Available") float meanAvailable,
        @JsonbProperty("Mean Used") float meanUsed,
        @JsonbProperty("Metric Profile") ProfileData profile,
        @JsonbProperty("Metric Type") MetricType type,
        @JsonbProperty("Job Environment Id") String jobEnvId,
        @JsonbProperty("Annotations") CustomAnnotation anno
    ) {
        this.id = id;
        this.label = label;
        this.timestamp = timestamp;
        this.units = units;
        this.meanTotal = meanTotal;
        this.meanAvailable = meanAvailable;
        this.meanUsed = meanUsed;
        this.profile = profile;
        this.type = type;
        this.jobEnvId = jobEnvId;
        this.anno = anno;
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
     * Set annotation provided
     * 
     * @param anno 
     */
    @Override
    public void setAnnotations(CustomAnnotation anno) {
        this.anno = anno;
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
     * Get profile Id
     * 
     * @return String
     */
    @Override
    public String getId() {
        return this.id;
    }

    
    /**
     * Get units as state
     * 
     * @return 
     */
    @Override
    public String getState() {
        return this.units;
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
        this.meanAvailable = 0;
        this.meanTotal = 0;
        this.meanUsed = 0;
        this.profile = new ProfileData();
    }

    
    /**
     * Get {@link JobEnvironment} Id
     * 
     * @return String
     */
    public String getJobEnvId() {
        return this.jobEnvId;
    }

    
    /**
     * Set {@link JobEnvironment} Id
     * 
     * @param jobEnvId 
     */
    public void setJobEnvId(String jobEnvId) {
        this.jobEnvId = jobEnvId;
    }
    
    
    /**
     * Get profile label
     * 
     * @return String
     */
    public String getLabel() {
        return label;
    }

    
    /**
     * Set profile label
     * 
     * @param label 
     */
    public void setLabel(String label) {
        this.label = label;
    }

    
    /**
     * Get profile timestamp
     * 
     * @return long
     */
    public long getTimestamp() {
        return timestamp;
    }

    
    /**
     * Set time stamp
     * 
     * @param timestamp 
     */
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    
    /**
     * Get mean total
     * 
     * @return float
     */
    public float getMeanTotal() {
        return meanTotal;
    }

    
    /**
     * Set mean total
     * 
     * @param meanTotal 
     */
    public void setMeanTotal(float meanTotal) {
        this.meanTotal = meanTotal;
    }
    
    
    /**
     * Get mean available
     * 
     * @return float
     */
    public float getMeanAvailable() {
        return meanAvailable;
    }

    
    /**
     * Set mean available
     * 
     * @param meanAvailable 
     */
    public void setMeanAvailable(float meanAvailable) {
        this.meanAvailable = meanAvailable;
    }

    
    /**
     * Get mean used
     * 
     * @return float
     */
    public float getMeanUsed() {
        return meanUsed;
    }

    
    /**
     * Set mean used
     * 
     * @param meanUsed 
     */
    public void setMeanUsed(float meanUsed) {
        this.meanUsed = meanUsed;
    }
    

    /**
     * Get profiled data
     * 
     * @return {@link ProfileData}
     */
    public ProfileData getProfile() {
        return profile;
    }

    
    /**
     * Set profile data
     * 
     * @param profile 
     */
    public void setProfile(ProfileData profile) {
        this.profile = profile;
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
     * Get units for profile
     * 
     * @return String
     */
    public String getUnits() {
        return this.units;
    }

    
    /**
     * Set units
     * 
     * @param units 
     */
    public void setUnits(String units) {
        this.units = units;
    }

    
    /**
     * Represent as string
     * 
     * @return String
     */
    @Override
    public String toString() {
        return "MetricProfile{" +
            "id=" + id +
            ", label=" + label +
            ", timestamp=" + timestamp +
            ", meanTotal=" + meanTotal +
            ", meanAvailable=" + meanAvailable +
            ", meanUsed=" + meanUsed +
            ", profile=" + profile +
            ", units=" + units +
            ", type=" + type +
            ", jobEnvId=" + jobEnvId +
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